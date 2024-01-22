package com.treasurehunt.ui.savelog

import android.content.Intent
import android.provider.MediaStore
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class SaveLogViewModel : ViewModel() {

    private val _images: MutableStateFlow<List<ImageModel>> = MutableStateFlow(emptyList())
    val images = _images.asStateFlow()
    private val _isImageMax = MutableStateFlow(false)
    val isImageMax = _isImageMax.asStateFlow()
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

    fun addImage(image: ImageModel) {
        _images.value = images.value + image
        _isImageMax.value = images.value.size >= 5
        setButtonState()
    }

    fun removeImage(image: ImageModel) {
        _images.value = images.value - image
        _isImageMax.value = images.value.size >= 5
        setButtonState()
    }

    fun setTextInput(input: CharSequence) {
        _text.value = input.toString()
        setButtonState()
    }

    private fun setButtonState(): Boolean {
        _isEnabled.value = _images.value.isNotEmpty() && _text.value?.isNotEmpty() == true
        return _isEnabled.value
    }
}