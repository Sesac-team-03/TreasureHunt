package com.treasurehunt.ui.login

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.google.firebase.auth.auth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.ktx.Firebase
import com.navercorp.nid.profile.data.NidProfile
import com.treasurehunt.TreasureHuntApplication
import com.treasurehunt.data.model.UserDTO
import com.treasurehunt.data.remote.FirebaseRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

const val USER_UPDATE_DELAY = 1000L

class LoginViewModel(
    private val firebaseRepository: FirebaseRepository
) : ViewModel() {

    fun resisterUser(naverProfile: NidProfile) {
        updateProfile(naverProfile)
        viewModelScope.launch {
            updateProfile(naverProfile)
            delay(USER_UPDATE_DELAY)
            val user = Firebase.auth.currentUser!!
            val userDTO = UserDTO(
                email = user.email!!,
                nickname = user.displayName,
                profileImage = user.photoUrl.toString()
            )
            firebaseRepository.resisterUser(user.uid, userDTO)
        }
    }

    private fun updateProfile(naverProfile: NidProfile) {
        val profileUpdate = userProfileChangeRequest {
            displayName = naverProfile.nickname
            photoUri = Uri.parse(naverProfile.profileImage)
        }
        Firebase.auth.currentUser!!.updateProfile(profileUpdate)
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(
                modelClass: Class<T>,
                extras: CreationExtras
            ): T {
                return LoginViewModel(
                    TreasureHuntApplication.firebaseRepository
                ) as T
            }
        }
    }
}