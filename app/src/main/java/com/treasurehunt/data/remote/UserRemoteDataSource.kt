package com.treasurehunt.data.remote

import com.treasurehunt.data.remote.model.UserDTO
import com.treasurehunt.util.convertToDataClass
import javax.inject.Inject

class UserRemoteDataSource @Inject constructor(private val userService: UserService) {

    suspend fun insert(uid: String, userDTO: UserDTO) {
        userService.insert(uid, userDTO)
    }

    suspend fun getRemoteUser(id: String) = userService.getRemoteUser(id)

    suspend fun update(uid: String, userDTO: UserDTO) {
        userService.update(uid, userDTO)
    }

    suspend fun search(startAt: String, limit: Int = 10): Map<String, UserDTO> {
        return try {
            userService.search(
                orderBy = "\"email\"",
                startAt = startAt,
                limit = limit
            ).entries.associate {
                it.key to it.value.toString().convertToDataClass()
            }
        } catch (e: Exception) {
            emptyMap()
        }
    }
}