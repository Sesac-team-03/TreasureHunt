package com.treasurehunt.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.FirebaseException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.treasurehunt.BuildConfig
import com.treasurehunt.data.LogRepository
import com.treasurehunt.data.PlaceRepository
import com.treasurehunt.data.UserRepository
import com.treasurehunt.data.remote.model.UserDTO
import com.treasurehunt.data.remote.model.toUserModel
import com.treasurehunt.ui.model.FriendUiState
import com.treasurehunt.util.ConnectivityRepository
import dagger.hilt.android.lifecycle.HiltViewModel
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
import javax.inject.Inject

private const val BASE_URL = BuildConfig.BASE_URL

@HiltViewModel
class FriendViewModel @Inject constructor(
    private val logRepo: LogRepository,
    private val placeRepo: PlaceRepository,
    private val userRepo: UserRepository,
    private val connectivityRepo: ConnectivityRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(FriendUiState(false))
    val uiState: StateFlow<FriendUiState> = _uiState
    private val db = FirebaseDatabase.getInstance(BASE_URL)
    private val friendIds: Flow<List<String>> = getFriendIdsFlow()

    init {
        viewModelScope.launch {
            initCurrentUser()
            initFriends()
        }
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
        val signedInAsMember = currentFirebaseUser?.isAnonymous == false
        val uid = currentFirebaseUser?.uid

        _uiState.update {
            it.copy(
                isSignedInAsMember = signedInAsMember,
                uid = uid
            )
        }
    }

    private suspend fun initFriends() {
        friendIds.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())
            .collect { friendIds ->
                val friends = friendIds.map { friendId ->
                    userRepo.getRemoteUser(friendId).toUserModel(remoteId = friendId)
                }
                _uiState.update {
                    it.copy(friends = friends.associateWith { true })
                }
            }
    }

    suspend fun searchUser(startAt: String): List<UserDTO> {
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
        val user = userRepo.getRemoteUser(uid)
        if (user.friends[friendId] == true) return

        userRepo.update(
            uid, user.copy(friends = user.friends + (friendId to true))
        )

        _uiState.update { uiState ->
            val friend = userRepo.getRemoteUser(friendId).toUserModel(friendId)
            uiState.copy(friends = uiState.friends + (friend to true))
        }
    }

    suspend fun removeFriend(uid: String, friendId: String) {
        val user = userRepo.getRemoteUser(uid)
        if (user.friends[friendId] != true) return

        userRepo.update(
            uid, user.copy(friends = user.friends + (friendId to false))
        )

        _uiState.update {
            val friend = userRepo.getRemoteUser(friendId).toUserModel(friendId)
            it.copy(friends = it.friends - friend)
        }
    }
}