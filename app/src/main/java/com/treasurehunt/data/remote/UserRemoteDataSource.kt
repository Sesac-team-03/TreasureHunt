package com.treasurehunt.data.remote

import com.treasurehunt.data.remote.model.UserDTO
import javax.inject.Inject

class UserRemoteDataSource @Inject constructor(private val userService: UserService) {

    suspend fun insert(uid: String, userDTO: UserDTO) {
        userService.insert(uid, userDTO)
    }

    suspend fun update(uid: String, userDTO: UserDTO) {
        userService.update(uid, userDTO)
    }

    suspend fun getUserData(uid: String) = userService.getUser(uid)

}