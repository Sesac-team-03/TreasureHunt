package com.treasurehunt.data.remote.model

import com.treasurehunt.data.remote.UserService

class UserRemoteDataSource(private val userService: UserService) {

    suspend fun insert(id: String, userDTO: UserDTO) {
        userService.insert(id, userDTO)
    }

    suspend fun update(id: String, userDTO: UserDTO) {
        userService.update(id, userDTO)
    }

    suspend fun getUserData(id: String) = userService.getUser(id)

}