package com.treasurehunt.ui.savelog

import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.google.firebase.Firebase
import com.google.firebase.storage.storage
import com.treasurehunt.TreasureHuntApplication
import com.treasurehunt.data.ImageRepository
import com.treasurehunt.data.LogRepository
import com.treasurehunt.data.PlaceRepository
import com.treasurehunt.data.UserRepository
import com.treasurehunt.data.local.model.LogEntity
import com.treasurehunt.data.local.model.PlaceEntity
import com.treasurehunt.data.remote.model.ImageDTO
import com.treasurehunt.data.remote.model.LogDTO
import com.treasurehunt.data.remote.model.PlaceDTO
import com.treasurehunt.data.remote.model.UserDTO
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await

class SaveLogViewModel(
    private val logRepo: LogRepository,
    private val placeRepo: PlaceRepository,
    private val userRepo: UserRepository,
    private val imageRepo: ImageRepository
) : ViewModel() {

    private val _images: MutableStateFlow<List<ImageModel>> = MutableStateFlow(emptyList())
    val images = _images.asStateFlow()
    private val _imageUrl: MutableStateFlow<List<String>> = MutableStateFlow(emptyList())
    val imageUrl = _imageUrl.asStateFlow()
    private val _text: MutableStateFlow<String> = MutableStateFlow("")
    val text = _text.asStateFlow()
    private val _isEnabled: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isEnabled = _isEnabled.asStateFlow()

    suspend fun uploadImage(currentCount: Int, maxCount: Int, uid: String, uri: Uri): Boolean {
        val storage = Firebase.storage
        val storageRef = storage.getReference("${uid}/log_images")
        val fileName = uri.toString().replace("[^0-9]".toRegex(), "")
        val mountainsRef = storageRef.child("${fileName}.png")
        val uploadTask = mountainsRef.putFile(uri)
        var result = false

        uploadTask.addOnSuccessListener { taskSnapshot ->
            addImageUrl(taskSnapshot.storage.toString())
            result = true
        }.addOnFailureListener {
            result = false
        }
        uploadTask.await()

        return result
    }

    fun getImage(): Intent {
        return Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).apply {
            type = "image/*"
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        }
    }

    fun addImageUrl(url: String) {
        _imageUrl.value += url
    }

    fun addImage(image: ImageModel) {
        _images.value += image
        setButtonState()
    }

    suspend fun insertLog(logEntity: LogEntity) = logRepo.insert(logEntity)

    suspend fun insertLog(logDTO: LogDTO) = logRepo.insert(logDTO)

    fun removeImage(image: ImageModel) {
        _images.value -= image
        setButtonState()
    }

    fun setTextInput(input: CharSequence) {
        _text.value = input.toString()
        setButtonState()
    }

    private fun setButtonState(): Boolean {
        _isEnabled.value = _images.value.isNotEmpty() && _text.value.isNotEmpty()
        return _isEnabled.value
    }

    suspend fun insertPlace(placeEntity: PlaceEntity): Long {
        return viewModelScope.async {
            placeRepo.insert(placeEntity)
        }.await()
    }

    suspend fun insertPlace(placeDTO: PlaceDTO): String {
        return viewModelScope.async {
            placeRepo.insert(placeDTO)
        }.await()
    }

    suspend fun getRemotePlaceById(id: String): PlaceDTO {
        return viewModelScope.async {
            return@async placeRepo.getRemotePlace(id)
        }.await()
    }

    suspend fun updatePlace(place: PlaceEntity) {
        placeRepo.update(place)
    }

    suspend fun updatePlace(id: String, place: PlaceDTO) {
        placeRepo.update(id, place)
    }

    suspend fun getUserById(uid: String) = userRepo.getRemoteUser(uid)

    suspend fun updateUser(uid: String, user: UserDTO) {
        userRepo.update(uid, user)
    }

    suspend fun insertImage(image: ImageDTO) = imageRepo.insertImage(image)

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(
                modelClass: Class<T>,
                extras: CreationExtras
            ): T {
                return SaveLogViewModel(
                    TreasureHuntApplication.logRepo,
                    TreasureHuntApplication.placeRepo,
                    TreasureHuntApplication.userRepo,
                    TreasureHuntApplication.imageRepo
                ) as T
            }
        }
    }
}