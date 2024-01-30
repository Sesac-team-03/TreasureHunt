package com.treasurehunt.ui.savelog

import android.content.Intent
import android.provider.MediaStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.treasurehunt.TreasureHuntApplication
import com.treasurehunt.data.LogRepository
import com.treasurehunt.data.PlaceRepository
import com.treasurehunt.data.local.model.LogEntity
import com.treasurehunt.data.local.model.PlaceEntity
import com.treasurehunt.data.remote.model.LogDTO
import com.treasurehunt.data.remote.model.PlaceDTO
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SaveLogViewModel(
    private val logRepo: LogRepository,
    private val placeRepo: PlaceRepository
) : ViewModel() {

    private val _images: MutableStateFlow<List<ImageModel>> = MutableStateFlow(emptyList())
    val images = _images.asStateFlow()
    private val _imageUrl: MutableStateFlow<List<String>> = MutableStateFlow(emptyList())
    val imageUrl = _imageUrl.asStateFlow()
    private val _text: MutableStateFlow<String> = MutableStateFlow("")
    val text = _text.asStateFlow()
    private val _isEnabled: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isEnabled = _isEnabled.asStateFlow()

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

    suspend fun insertLog(logEntity: LogEntity) {
        logRepo.insert(logEntity)
    }

    suspend fun insertLog(logDTO: LogDTO) {
        logRepo.insert(logDTO)
    }

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

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(
                modelClass: Class<T>,
                extras: CreationExtras
            ): T {
                return SaveLogViewModel(
                    TreasureHuntApplication.logRepo,
                    TreasureHuntApplication.placeRepo
                ) as T
            }
        }
    }
}