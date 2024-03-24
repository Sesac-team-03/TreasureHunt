package com.treasurehunt.ui.model

data class SaveLogUiState(
    val uid: String? = null,
    val images: List<ImageModel> = listOf(),
    val isTextFieldNotEmpty: Boolean = false,
    val isSaveButtonEnabled: Boolean = false,
    val isTextThemeEnabled: Boolean = false,
    val isLogLoading: Boolean = false,
    val isOnline: Boolean = false
)
