package com.treasurehunt.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
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
import com.treasurehunt.ui.model.FriendUiState
import com.treasurehunt.util.ConnectivityRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FriendViewModel(
    private val logRepo: LogRepository,
    private val placeRepo: PlaceRepository,
    private val userRepo: UserRepository,
    private val connectivityRepo: ConnectivityRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(FriendUiState(false))
    val uiState: StateFlow<FriendUiState> = _uiState
    val db =
        FirebaseDatabase.getInstance("https://treasurehunt-32565-default-rtdb.asia-southeast1.firebasedatabase.app")
    private val _allUsers = MutableStateFlow(emptyList<UserDTO>())

    init {
        initCurrentUser()
        initAllUsers()
        initFriends()

    }

    private fun initCurrentUser() {
        val currentFirebaseUser = Firebase.auth.currentUser
        val signedInAsRegisteredUser = currentFirebaseUser?.isAnonymous == false
        val uid = currentFirebaseUser?.uid

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    signedInAsRegisteredUser = signedInAsRegisteredUser,
                    uid = uid
                )
            }
        }
    }

    private fun initAllUsers() {
        val usersRef = db.reference.child("users")
        usersRef.addValueEventListener(object : ValueEventListener {
            @Suppress("UNCHECKED_CAST")
            override fun onDataChange(snapshot: DataSnapshot) {
                _allUsers.update {
                    snapshot.children.map {
                        UserDTO(
                            email = it.child("email").value as? String?,
                            nickname = it.child("nickname").value as? String?,
                            profileImage = it.child("profileImage").value as? String?,
                            public = it.child("public").value as? Boolean ?: false,
                            friends = it.child("friends").value as? Map<String, Boolean>
                                ?: emptyMap(),
                            logs = it.child("logs").value as? Map<String, Boolean>
                                ?: emptyMap(),
                            places = it.child("places").value as? Map<String, Boolean>
                                ?: emptyMap(),
                            plans = it.child("plans").value as? Map<String, Boolean>
                                ?: emptyMap(),
                            localId = it.child("localId").value as? Long ?: 0,
                            remoteId = it.key
                        )
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun initFriends() {
        viewModelScope.launch {
            _allUsers.collect { users ->
                val uid = uiState.value.uid ?: return@collect
                val currentUser = getRemoteUser(uid)
                val friendIds = currentUser.friends.filter { friendEntry ->
                    friendEntry.value
                }.keys
                val friends = users.filter { user ->
                    friendIds.contains(user.remoteId)
                }
                _uiState.update {
                    it.copy(friends = friends)
                }
            }
        }
    }

    suspend fun getRemoteUser(uid: String) = viewModelScope.async {
        return@async userRepo.getRemoteUser(uid)
    }.await()

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
                uid,
                user.copy(friends = user.friends + (friendId to true))
            )

            _uiState.update {
                val friend = getRemoteUser(friendId)
                it.copy(friends = it.friends + friend)
            }
        }
    }

    suspend fun removeFriend(uid: String, friendId: String) {
        viewModelScope.launch {
            val user = userRepo.getRemoteUser(uid)
            userRepo.update(
                uid,
                user.copy(friends = user.friends + (friendId to false))
            )

            _uiState.update {
                val friend = getRemoteUser(friendId)
                it.copy(friends = it.friends - friend)
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>,
                extras: CreationExtras
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