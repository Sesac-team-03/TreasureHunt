package com.treasurehunt.ui.model

data class FeedUiState(
    val logs: List<LogModel> = emptyList(),
    val isLogUpdate: Boolean = false,
)