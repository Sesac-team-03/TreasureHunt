package com.treasurehunt

import android.content.Intent
import android.provider.MediaStore
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class LogViewModel : ViewModel() {

    private val _images: MutableStateFlow<List<ImageModel>> = MutableStateFlow(emptyList())
    val images = _images.asStateFlow()
    private val _isImageMax = MutableStateFlow(false)
    val isImageMax = _isImageMax.asStateFlow()

    fun getImage(): Intent {
        return Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).apply {
            type = "image/*"
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        }
    }

    fun addImage(image: ImageModel) {
        _images.value = images.value + image
        _isImageMax.value = images.value.size >= 5
    }

    fun removeImage(image: ImageModel) {
        _images.value = images.value - image
        _isImageMax.value = images.value.size >= 5
    }
}