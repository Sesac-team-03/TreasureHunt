package com.treasurehunt.ui.profile

import android.content.Intent
import android.service.autofill.UserData
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.treasurehunt.TreasureHuntApplication
import com.treasurehunt.data.User
import com.treasurehunt.data.UserRepository
import com.treasurehunt.data.remote.model.UserDTO
import com.treasurehunt.ui.savelog.ImageModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _nicknameData: MutableStateFlow<String> = MutableStateFlow("")
    val nicknameData = _nicknameData.asStateFlow()
    private val _emailData: MutableStateFlow<String> = MutableStateFlow("")
    val emailData = _emailData.asStateFlow()
    private val _imageUri: MutableStateFlow<String> = MutableStateFlow("")
    val imageUri = _imageUri.asStateFlow()
    private val _profileUri: MutableStateFlow<String> = MutableStateFlow("")
    val profileUri = _profileUri.asStateFlow()

    private fun setNickname(nickname: String) {
        _nicknameData.value = nickname
    }

    private fun setEmail(email: String) {
        _emailData.value = email
    }

    fun setProfileUri(profileUri: String) {
        _profileUri.value = profileUri
    }

    fun getImage(): Intent {
        return Intent(Intent.ACTION_PICK).apply {
            type = "image/"
        }
    }

    fun addImage(image: String) {
        _imageUri.value = image
    }

    fun getUserData() {
        val uid = Firebase.auth.currentUser!!.uid
        viewModelScope.launch {
            val user = userRepository.getRemoteUser(uid)
            if (user.email.isEmpty()) {
                setNickname(uid)
            } else {
                setNickname(user.nickname.toString())
                setEmail(user.email)
                setProfileUri(user.profileImage.toString())
            }
        }
    }

    suspend fun insertUserData(userDTO: UserDTO) {
        userRepository.insert(Firebase.auth.currentUser!!.uid, userDTO)
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

