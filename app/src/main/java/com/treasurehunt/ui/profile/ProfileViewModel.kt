package com.treasurehunt.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.treasurehunt.TreasureHuntApplication
import com.treasurehunt.data.UserRepository
import com.treasurehunt.data.remote.model.UserDTO

class ProfileViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    suspend fun getUserData(): UserDTO {
        val uid = Firebase.auth.currentUser!!.uid
        return userRepository.getRemoteUser(uid)
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(
                modelClass: Class<T>,
                extras: CreationExtras
            ): T {
                return ProfileViewModel(
                    TreasureHuntApplication.userRepo,
                ) as T
            }
        }
    }
}

