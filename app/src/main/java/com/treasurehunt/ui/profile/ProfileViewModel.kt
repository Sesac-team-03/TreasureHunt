package com.treasurehunt.ui.profile

import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.storage.storage
import com.treasurehunt.data.UserRepository
import com.treasurehunt.data.remote.model.UserDTO
import com.treasurehunt.ui.model.ProfileUiState
import com.treasurehunt.util.FILENAME_EXTENSION_PNG
import com.treasurehunt.util.MIME_TYPE_IMAGE
import com.treasurehunt.util.STORAGE_LOCATION_PROFILE_IMAGE
import com.treasurehunt.util.extractDigits
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepo: UserRepository
) : ViewModel() {

    private val _uiState: MutableStateFlow<ProfileUiState> = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            initUid()
            syncUid()
            syncUser()
        }
    }

    private fun initUid() {
        _uiState.update {
            it.copy(uid = Firebase.auth.currentUser?.uid)
        }
    }

    private fun syncUid() {
        Firebase.auth.addAuthStateListener { auth ->
            _uiState.update {
                it.copy(uid = auth.currentUser?.uid)
            }
        }
    }

    private suspend fun syncUser() {
        val uid = _uiState.value.uid ?: return
        _uiState.update {
            it.copy(user = userRepo.getRemoteUserById(uid))
        }
    }

    fun getImagePick() =
        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).apply {
            type = MIME_TYPE_IMAGE
        }

    fun addImageContentUri(contentUri: String) {
        _uiState.update {
            it.copy(imageContentUri = contentUri)
        }
    }

    suspend fun saveProfile(nickname: String) {
        val user = _uiState.value.user ?: return
        val imageContentUri = _uiState.value.imageContentUri

        imageContentUri?.let {
            uploadProfileImage(it.toUri())
        }

        updateUser(
            user.copy(
                nickname = nickname,
                profileImage = _uiState.value.imageStorageUrl
            )
        )

        syncUser() // not needed if syncUser operates on callbackflow
    }

    private suspend fun uploadProfileImage(uri: Uri) {
        val uid = _uiState.value.uid ?: return
        val filename = uri.toString().extractDigits()
        val profileImageStorageRef =
            Firebase.storage.reference.child(uid).child(STORAGE_LOCATION_PROFILE_IMAGE)
                .child("$filename$FILENAME_EXTENSION_PNG")
        val uploadTask = profileImageStorageRef.putFile(uri)
        uploadTask.await()
        if (uploadTask.isSuccessful) {
            addImageStorageUrl(uploadTask.snapshot.storage.toString())
        } else {
            // TODO: handle fail
        }
    }

    private fun addImageStorageUrl(imageStorageUrl: String) {
        _uiState.update {
            it.copy(
                imageContentUri = null,
                imageStorageUrl = imageStorageUrl
            )
        }
    }

    private suspend fun updateUser(user: UserDTO) {
        val uid = _uiState.value.uid ?: return

        userRepo.update(
            uid,
            user
        )

        _uiState.update {
            it.copy(user = user)
        }
    }
}

