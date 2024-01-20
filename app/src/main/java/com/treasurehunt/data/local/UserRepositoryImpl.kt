package com.treasurehunt.data.local

import com.treasurehunt.data.model.UserEntity
import kotlinx.coroutines.flow.Flow

class UserRepositoryImpl(private val userDao: UserDao) {

    suspend fun insert(user: UserEntity) = userDao.insert(user)

    fun getUserById(id: String): Flow<UserEntity> = userDao.getUserById(id)

    fun getAllUsers(): Flow<List<UserEntity>> = userDao.getAllUsers()

    fun update(user: UserEntity) = userDao.update(user)

    suspend fun delete(vararg users: UserEntity) = userDao.delete(*users)
}