package com.treasurehunt.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.google.firebase.FirebaseException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.treasurehunt.TreasureHuntApplication
import com.treasurehunt.data.LogRepository
import com.treasurehunt.data.PlaceRepository
import com.treasurehunt.data.UserRepository
import com.treasurehunt.data.remote.model.UserDTO
import com.treasurehunt.data.remote.model.toUserModel
import com.treasurehunt.ui.model.FriendUiState
import com.treasurehunt.util.ConnectivityRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val firebaseUrl =
    "https://treasurehunt-32565-default-rtdb.asia-southeast1.firebasedatabase.app"

class FriendViewModel(
    private val logRepo: LogRepository,
    private val placeRepo: PlaceRepository,
    private val userRepo: UserRepository,
    private val connectivityRepo: ConnectivityRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(FriendUiState(false))
    val uiState: StateFlow<FriendUiState> = _uiState
    private val db = FirebaseDatabase.getInstance(firebaseUrl)
    private val friendIds: Flow<List<String>> = getFriendIdsFlow()

    init {
        initCurrentUser()
        initFriends()
    }

    private fun getFriendIdsFlow(): Flow<List<String>> = callbackFlow {
        val uid = Firebase.auth.currentUser!!.uid
        val friendsRef = db.reference.child("users").child(uid).child("friends")
        val callback = object : ValueEventListener {
            @Suppress("UNCHECKED_CAST")
            override fun onDataChange(snapshot: DataSnapshot) {
                val friendIds = (snapshot.value as? Map<String, Boolean>
                    ?: emptyMap()).filter { it.value }.keys.toList()

                trySendBlocking(friendIds)
            }

            override fun onCancelled(error: DatabaseError) {
            }
        }

        try {
            friendsRef.addValueEventListener(callback)
        } catch (e: FirebaseException) {
            trySendBlocking(emptyList())
            channel.close()
        }
        awaitClose {
            friendsRef.removeEventListener(callback)
        }
    }

    private fun initCurrentUser() {
        val currentFirebaseUser = Firebase.auth.currentUser
        val signedInAsRegisteredUser = currentFirebaseUser?.isAnonymous == false
        val uid = currentFirebaseUser?.uid

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    signedInAsRegisteredUser = signedInAsRegisteredUser, uid = uid
                )
            }
        }
    }

    private fun initFriends() {
        viewModelScope.launch {
            friendIds.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())
                .collect { friendIds ->
                    val friends = friendIds.map { friendId ->
                        userRepo.getRemoteUser(friendId).toUserModel(remoteId = friendId)
                    }
                    _uiState.update {
                        it.copy(friends = friends)
                    }
                }
        }
    }

    suspend fun search(startAt: String): List<UserDTO> {
        return viewModelScope.async {
            val searchResult = userRepo.search("\"$startAt\"")
            return@async searchResult.map { remoteUserEntry ->
                remoteUserEntry.value.copy(
                    remoteId = remoteUserEntry.key
                )
            }
        }.await()
    }

    suspend fun addFriend(uid: String, friendId: String) {
        viewModelScope.launch {
            val user = userRepo.getRemoteUser(uid)
            userRepo.update(
                uid, user.copy(friends = user.friends + (friendId to true))
            )

            _uiState.update { uiState ->
                val friend = userRepo.getRemoteUser(friendId).toUserModel(friendId)
                uiState.copy(friends = uiState.friends + friend)
            }
        }
    }

    suspend fun removeFriend(uid: String, friendId: String) {
        val user = userRepo.getRemoteUser(uid)

        userRepo.update(
            uid, user.copy(friends = user.friends + (friendId to false))
        )

        _uiState.update {
            val friend = userRepo.getRemoteUser(friendId).toUserModel(friendId)
            it.copy(friends = it.friends - friend)
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>, extras: CreationExtras
            ): T {
                val application =
                    checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY])
                return FriendViewModel(
                    TreasureHuntApplication.logRepo,
                    TreasureHuntApplication.placeRepo,
                    TreasureHuntApplication.userRepo,
                    ConnectivityRepository(application.applicationContext)
                ) as T
            }
        }
    }
}