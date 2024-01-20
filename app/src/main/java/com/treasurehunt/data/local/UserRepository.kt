package com.treasurehunt.data.local

import com.treasurehunt.data.model.LogEntity
import com.treasurehunt.data.model.UserEntity
import kotlinx.coroutines.flow.Flow

interface UserRepository {

    suspend fun insert(user: UserEntity): Long

    fun getUserById(id: String): Flow<UserEntity>

    fun getAllUsers(): Flow<List<UserEntity>>

    fun update(user: UserEntity): Int

    suspend fun delete(vararg users: UserEntity): Int
}