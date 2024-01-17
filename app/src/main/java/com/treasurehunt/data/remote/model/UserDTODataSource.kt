package com.treasurehunt.data.remote.model

import com.treasurehunt.data.remote.RemoteDatabaseService

class UserDTODataSource(private val firebaseService: RemoteDatabaseService) {

    suspend fun resisterUser(uid: String, data: UserDTO) {
        firebaseService.resisterUser(uid, data)
    }

    suspend fun getUserData(uid: String) = firebaseService.getUser(uid)

}