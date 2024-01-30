package com.treasurehunt.util

import android.os.Build
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Date

fun getCurrentTime() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
    LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
} else {
    Date().time
}