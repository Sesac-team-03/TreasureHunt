package com.treasurehunt.ui.savelog

import android.content.Intent
import android.provider.MediaStore
import androidx.lifecycle.ViewModel
import com.treasurehunt.data.ImageRepository
import com.treasurehunt.data.remote.model.LogDTO
import com.treasurehunt.ui.model.ImageModel
import com.treasurehunt.ui.model.SaveLogUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

private const val MIME_TYPE_IMAGE = "image/*"

@HiltViewModel
class SaveLogViewModel @Inject constructor(private val imageRepo: ImageRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(SaveLogUiState())
    val uiState: StateFlow<SaveLogUiState> = _uiState

    fun getImagePick() =
        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).apply {
            type = MIME_TYPE_IMAGE
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        }

    fun addImage(image: ImageModel) {
        _uiState.update {  uiState ->
            uiState.copy(images = uiState.images + image)
        }
        setSaveButtonState()
    }

    fun removeImage(image: ImageModel) {
        _uiState.update {  uiState ->
            uiState.copy(images = uiState.images - image)
        }
        setSaveButtonState()
    }

    fun setTextFieldState(input: CharSequence) {
        _uiState.update { uiState ->
            uiState.copy(isTextFieldNotEmpty = input.isNotEmpty())
        }
        setSaveButtonState()
    }

    private fun setSaveButtonState() {
        _uiState.update {  uiState ->
            uiState.copy(isSaveButtonEnabled = uiState.images.isNotEmpty() && uiState.isTextFieldNotEmpty)
        }
    }

    suspend fun getImageStorageUrls(ids: List<String>): List<String> = ids.map { id ->
        imageRepo.getRemoteImageById(id).url
    }
}