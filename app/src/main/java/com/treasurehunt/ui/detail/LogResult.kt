package com.treasurehunt.ui.detail

import com.treasurehunt.ui.model.LogModel

sealed class LogResult {

    data class LogLoaded(val value: LogModel) : LogResult()

    data object LogLoading : LogResult()

    data object LogNotLoaded : LogResult()
}
