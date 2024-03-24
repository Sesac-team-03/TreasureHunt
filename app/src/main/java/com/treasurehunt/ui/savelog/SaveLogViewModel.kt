package com.treasurehunt.ui.savelog

import android.content.Intent
import android.provider.MediaStore
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.treasurehunt.data.ImageRepository
import com.treasurehunt.ui.model.ImageModel
import com.treasurehunt.ui.model.LogModel
import com.treasurehunt.ui.model.SaveLogUiState
import com.treasurehunt.util.MIME_TYPE_IMAGE
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class SaveLogViewModel @Inject constructor(
    private val imageRepo: ImageRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState: MutableStateFlow<SaveLogUiState> = MutableStateFlow(SaveLogUiState())
    val uiState: StateFlow<SaveLogUiState> = _uiState.asStateFlow()

    init {
        initUiState(SaveLogFragmentArgs.fromSavedStateHandle(savedStateHandle).log)
    }

    private fun initUiState(log: LogModel?) {
        log?.let {
            _uiState.update { uiState ->
                uiState.copy(
                    isTextFieldNotEmpty = it.text.isNotEmpty(),
                    isTextThemeEnabled = it.imageUrls.isEmpty() && it.text.isNotEmpty(),
                )
            }
        }
    }

    fun getImagePick() =
        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).apply {
            type = MIME_TYPE_IMAGE
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        }

    fun addImage(image: ImageModel) {
        _uiState.update { uiState ->
            uiState.copy(images = uiState.images + image)
        }
        setSaveButtonState()
        setTextThemeState()
    }

    fun removeImage(image: ImageModel) {
        _uiState.update { uiState ->
            uiState.copy(images = uiState.images - image)
        }
        setSaveButtonState()
        setTextThemeState()
    }

    fun setTextFieldState(input: CharSequence) {
        _uiState.update { uiState ->
            uiState.copy(isTextFieldNotEmpty = input.isNotEmpty())
        }
        setSaveButtonState()
        setTextThemeState()
    }

    private fun setSaveButtonState() {
        _uiState.update { uiState ->
            uiState.copy(isSaveButtonEnabled = uiState.images.isNotEmpty() || uiState.isTextFieldNotEmpty)
        }
    }

    fun setSaveButtonState(value: Boolean) {
        _uiState.update { uiState ->
            uiState.copy(isSaveButtonEnabled = value)
        }
    }

    private fun setTextThemeState() {
        _uiState.update { uiState ->
            uiState.copy(isTextThemeEnabled = uiState.images.isEmpty() && uiState.isTextFieldNotEmpty)
        }
    }

    suspend fun getImageStorageUrls(ids: List<String>): List<String> = ids.map { id ->
        imageRepo.getRemoteImageById(id).url
    }
}