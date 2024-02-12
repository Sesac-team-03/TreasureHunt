package com.treasurehunt.ui.model

data class FeedUiState(
    val logs: List<LogModel>,
    val isLogUpdated: Boolean = false
)