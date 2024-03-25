package com.treasurehunt.ui.model

sealed class LogResult {

    data class Loaded(val value: LogModel) : LogResult()

    data object Loading : LogResult()

    data object NotLoaded : LogResult()
}
