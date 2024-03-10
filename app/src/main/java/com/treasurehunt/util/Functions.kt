package com.treasurehunt.util

import android.os.Build
import com.google.firebase.Firebase
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Date

fun getCurrentTime(): Long = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
    LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
} else {
    Date().time
}

internal fun getUidCallbackFlow(): Flow<String?> = callbackFlow {
    val callback = FirebaseAuth.AuthStateListener { auth ->
        trySendBlocking(auth.currentUser?.uid)
    }

    try {
        Firebase.auth.addAuthStateListener(callback)
    } catch (e: FirebaseException) {
        trySendBlocking(null)
        channel.close()
    }

    awaitClose {
        Firebase.auth.removeAuthStateListener(callback)
    }
}