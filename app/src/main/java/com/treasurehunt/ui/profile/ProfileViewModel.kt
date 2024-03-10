package com.treasurehunt.ui.profile

import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.FirebaseException
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.treasurehunt.BuildConfig
import com.treasurehunt.data.UserRepository
import com.treasurehunt.data.remote.REMOTE_DATABASE_USERS
import com.treasurehunt.data.remote.model.UserDTO
import com.treasurehunt.ui.model.ProfileUiState
import com.treasurehunt.util.COMPILATION_WARNING_UNCHECKED_CAST
import com.treasurehunt.util.FILENAME_EXTENSION_PNG
import com.treasurehunt.util.MIME_TYPE_IMAGE
import com.treasurehunt.util.STORAGE_LOCATION_PROFILE_IMAGE
import com.treasurehunt.util.extractDigits
import com.treasurehunt.util.getUidCallbackFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

private const val BASE_URL = BuildConfig.BASE_URL
private val pathEmail = UserDTO::email.name
private val pathNickname = UserDTO::nickname.name
private val pathProfileImage = UserDTO::profileImage.name

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepo: UserRepository
) : ViewModel() {

    private val _uiState: MutableStateFlow<ProfileUiState> = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()
    private val uid: Flow<String?> = getUidCallbackFlow()
    private val user: Flow<UserDTO?> = uid.convertToUserFlow()

    init {
        initUid()
        initUser()
    }

    private fun Flow<String?>.convertToUserFlow() = transform { uid ->
        if (uid == null) {
            emit(null)
        } else {
            emitAll(getRemoteUserFlow(uid))
        }
    }

    private fun getRemoteUserFlow(uid: String): Flow<UserDTO?> = callbackFlow {
        val db = FirebaseDatabase.getInstance(BASE_URL)
        val userRef = db.reference.child(REMOTE_DATABASE_USERS).child(uid)
        val callback = object : ValueEventListener {
            @Suppress(COMPILATION_WARNING_UNCHECKED_CAST)
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.run {
                    val email = child(pathEmail).value as? String?
                    val nickname = child(pathNickname).value as? String?
                    val profileImage = child(pathProfileImage).value as? String?
                    trySendBlocking(
                        UserDTO(
                            email = email,
                            nickname = nickname,
                            profileImage = profileImage,
                        )
                    )
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        }

        try {
            userRef.addValueEventListener(callback)
        } catch (e: FirebaseException) {
            trySendBlocking(null)
            channel.close()
        }

        awaitClose { userRef.removeEventListener(callback) }
    }

    private fun initUid() {
        viewModelScope.launch {
            uid.collect { uid ->
                _uiState.update {
                    it.copy(uid = uid)
                }
            }
        }
    }

    private fun initUser() {
        viewModelScope.launch {
            user.collect { user ->
                _uiState.update {
                    it.copy(user = user)
                }
            }
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