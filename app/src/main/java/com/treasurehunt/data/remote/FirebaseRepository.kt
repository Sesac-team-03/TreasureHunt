package com.treasurehunt.data.remote

import com.treasurehunt.data.model.UserDTO

class FirebaseRepository(private val firebaseService: FirebaseService) {

    suspend fun resisterUser(uid: String, data: UserDTO) {
        firebaseService.resisterUser(uid, data)
    }
}