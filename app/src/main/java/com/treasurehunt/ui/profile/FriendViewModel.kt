package com.treasurehunt.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.treasurehunt.TreasureHuntApplication
import com.treasurehunt.data.LogRepository
import com.treasurehunt.data.PlaceRepository
import com.treasurehunt.data.UserRepository
import com.treasurehunt.data.remote.model.UserDTO
import com.treasurehunt.util.ConnectivityRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class FriendViewModel(
    private val logRepo: LogRepository,
    private val placeRepo: PlaceRepository,
    private val userRepo: UserRepository,
    private val connectivityRepo: ConnectivityRepository
) : ViewModel() {

    suspend fun getRemoteUser(uid: String) = viewModelScope.async {
        return@async userRepo.getRemoteUser(uid)
    }.await()

    // limit = 5
    suspend fun search(startAt: String): Map<String, UserDTO> {
        return viewModelScope.async {
            return@async userRepo.search("\"$startAt\"", 5)
        }.await()
    }

    suspend fun addFriend(uid: String, friendId: String) {
        viewModelScope.launch {
            val user = userRepo.getRemoteUser(uid)
            userRepo.update(
                uid,
                user.copy(friends = user.friends + (friendId to true))
            )
        }
    }

    suspend fun removeFriend(uid: String, friendId: String) {
        viewModelScope.launch {
            val user = userRepo.getRemoteUser(uid)
            userRepo.update(
                uid,
                user.copy(friends = user.friends.minus(friendId))
            )
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