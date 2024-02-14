package com.treasurehunt.ui.feed

import com.treasurehunt.ui.model.LogModel

fun interface LogClickListener {
    fun onClick(logModel: LogModel)
}