package com.treasurehunt.ui.savelog

import android.content.Intent
import android.provider.MediaStore
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class SaveLogViewModel @Inject constructor() : ViewModel() {

    private val _images: MutableStateFlow<List<ImageModel>> = MutableStateFlow(emptyList())
    val images = _images.asStateFlow()
    private val _text: MutableStateFlow<String> = MutableStateFlow("")
    val text = _text.asStateFlow()
    private val _isEnabled: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isEnabled = _isEnabled.asStateFlow()

    fun getImagePick() =
        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).apply {
            type = "image/*"
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        }

    fun addImage(image: ImageModel) {
        _images.value += image
        setButtonState()
    }

    fun removeImage(image: ImageModel) {
        _images.value -= image
        setButtonState()
    }

    fun setTextInput(input: CharSequence) {
        _text.value = input.toString()
        setButtonState()
    }

    private fun setButtonState() {
        _isEnabled.value = _images.value.isNotEmpty() && _text.value.isNotEmpty()
    }
}