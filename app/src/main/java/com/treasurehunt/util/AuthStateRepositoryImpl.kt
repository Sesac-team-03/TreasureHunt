package com.treasurehunt.util

import com.google.firebase.Firebase
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class AuthStateRepositoryImpl : AuthStateRepository {

    override fun getUid(): Flow<String?> = callbackFlow {
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
}