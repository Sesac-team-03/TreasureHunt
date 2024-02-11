package com.treasurehunt.ui.model

data class FeedUiState(
    val logs: List<LogModel> = emptyList(),
    val hasLog: Boolean = false,
)