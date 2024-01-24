package com.treasurehunt.data

import com.treasurehunt.data.local.model.UserEntity
import com.treasurehunt.data.remote.model.UserDTO
import kotlinx.coroutines.flow.Flow

interface UserRepository {

    suspend fun insert(user: UserEntity): Long
    suspend fun insert(id: String, data: UserDTO)
    suspend fun getRemoteUser(id: String): UserDTO

    fun getUserById(id: String): Flow<UserEntity>

    fun getAllUsers(): Flow<List<UserEntity>>

    fun update(user: UserEntity): Int

    suspend fun delete(vararg users: UserEntity): Int
}