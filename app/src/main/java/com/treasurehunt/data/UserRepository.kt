package com.treasurehunt.data

import com.treasurehunt.data.local.model.UserEntity
import com.treasurehunt.data.remote.model.UserDTO
import kotlinx.coroutines.flow.Flow

interface UserRepository {

    suspend fun insert(user: UserEntity): Long

    suspend fun insert(id: String, data: UserDTO)

    fun getLocalUserById(id: String): Flow<UserEntity>

    fun getAllLocalUsers(): Flow<List<UserEntity>>

    suspend fun getRemoteUserById(id: String): UserDTO

    fun update(user: UserEntity): Int

    suspend fun update(id: String, user: UserDTO)

    suspend fun delete(vararg users: UserEntity): Int

    suspend fun search(startAt: String, limit: Int = 10): Map<String, UserDTO>
}