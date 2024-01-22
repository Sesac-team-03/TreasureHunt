package com.treasurehunt.data.remote

import com.treasurehunt.data.remote.model.UserDTO

class FirebaseRepository(private val firebaseService: FirebaseService) {

    suspend fun resisterUser(uid: String, data: UserDTO) {
        firebaseService.resisterUser(uid, data)
    }

    suspend fun getUserData(uid: String) = firebaseService.getUser(uid)

    suspend fun getLog(key: String) = firebaseService.getLog(key)

    suspend fun getPlace(key: String) = firebaseService.getPlace(key)
}