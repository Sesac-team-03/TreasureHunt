package com.treasurehunt.data.remote.model

import com.treasurehunt.data.remote.LogService
import com.treasurehunt.data.remote.UserService

class UserDTODataSource(private val userService: UserService) {

    suspend fun resisterUser(uid: String, data: UserDTO) {
        userService.resisterUser(uid, data)
    }

    suspend fun getUserData(uid: String) = userService.getUser(uid)

}