package com.treasurehunt.ui.profile

import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.treasurehunt.data.UserRepository
import com.treasurehunt.data.remote.model.UserDTO
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {
    private val _userData: MutableLiveData<UserDTO> = MutableLiveData<UserDTO>()
    val userData: LiveData<UserDTO> = _userData
    private val _imageUri: MutableLiveData<String> = MutableLiveData<String>()
    val imageUri: LiveData<String> = _imageUri
    private val _profileUri: MutableLiveData<String> = MutableLiveData<String>()
    val profileUri: LiveData<String> = _profileUri

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
            _userData.value = userRepository.getRemoteUser(uid)
        }
    }

    fun updateUserData(userDTO: UserDTO) {
        viewModelScope.launch {
            userRepository.update(
                Firebase.auth.currentUser!!.uid,
                userDTO
            )
        }
        _userData.value = userDTO
    }
}

