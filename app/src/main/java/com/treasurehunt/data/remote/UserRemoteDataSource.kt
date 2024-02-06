package com.treasurehunt.data.remote

import com.treasurehunt.data.remote.model.UserDTO
import com.treasurehunt.util.convertToDataClass
import javax.inject.Inject

class UserRemoteDataSource @Inject constructor(private val userService: UserService) {

    suspend fun insert(uid: String, userDTO: UserDTO) {
        userService.insert(uid, userDTO)
    }

    suspend fun update(uid: String, userDTO: UserDTO) {
        userService.update(uid, userDTO)
    }

    suspend fun getUserData(id: String) = userService.getUser(id)

    suspend fun search(startAt: String, limit: Int = 10): Map<String, UserDTO> {
        val result = userService.search(
            orderBy = "\"email\"",
            startAt = startAt,
            limit = limit
        )
        return result.entries.associate {
            it.key to it.value.toString().convertToDataClass()
        }
    }
}