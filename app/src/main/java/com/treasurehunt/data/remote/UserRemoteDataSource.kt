package com.treasurehunt.data.remote

import com.treasurehunt.data.remote.model.UserDTO
import javax.inject.Inject

class UserRemoteDataSource @Inject constructor(private val userService: UserService) {

    suspend fun insert(uid: String, data: UserDTO) {
        userService.insert(uid, data)
    }

    suspend fun update(uid: String, data: UserDTO) {
        userService.update(uid, data)
    }

    suspend fun getUserData(uid: String) = userService.getUser(uid)

}