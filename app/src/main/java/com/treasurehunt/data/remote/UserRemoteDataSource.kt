package com.treasurehunt.data.remote

import com.treasurehunt.data.remote.model.UserDTO
import com.treasurehunt.util.convertToDataClass
import javax.inject.Inject

class UserRemoteDataSource @Inject constructor(private val userService: UserService) {

    suspend fun insert(id: String, user: UserDTO) {
        userService.insert(id, user)
    }

    suspend fun getRemoteUserById(id: String) = userService.getRemoteUserById(id)

    suspend fun update(id: String, user: UserDTO) {
        userService.update(id, user)
    }

    suspend fun delete(id: String) {
        userService.delete(id)
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

    suspend fun searchEmail(email: String): String {
        return try {
            userService.searchNaverUserEmail(
                email = email
            )
        } catch (e: Exception) {
            ""
        }
    }
}