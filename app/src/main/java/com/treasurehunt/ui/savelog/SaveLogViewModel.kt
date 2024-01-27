package com.treasurehunt.ui.savelog

import android.content.Intent
import android.provider.MediaStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.treasurehunt.TreasureHuntApplication
import com.treasurehunt.data.LogRepository
import com.treasurehunt.data.remote.model.LogDTO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class SaveLogViewModel(
    private val logRepo: LogRepository,
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

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(
                modelClass: Class<T>,
                extras: CreationExtras
            ): T {
                return SaveLogViewModel(
                    TreasureHuntApplication.logRepo
                ) as T
            }
        }
    }
}