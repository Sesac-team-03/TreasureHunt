package com.treasurehunt.data

import com.treasurehunt.data.local.UserDao
import com.treasurehunt.data.local.model.UserEntity
import com.treasurehunt.data.remote.model.UserDTO
import com.treasurehunt.data.remote.model.UserDTODataSource
import kotlinx.coroutines.flow.Flow

class UserRepositoryImpl(
    private val userDao: UserDao,
    private val userDTODataSource: UserDTODataSource
) : UserRepository {

    override suspend fun insert(user: UserEntity) = userDao.insert(user)

    override suspend fun insertRemoteUser(id: String, data: UserDTO) {
        userDTODataSource.resisterUser(id, data)
    }

    override suspend fun getRemoteUser(id: String): UserDTO = userDTODataSource.getUserData(id)

    override fun getUserById(id: String): Flow<UserEntity> = userDao.getUserById(id)

    override fun getAllUsers(): Flow<List<UserEntity>> = userDao.getAllUsers()

    override fun update(user: UserEntity) = userDao.update(user)

    override suspend fun delete(vararg users: UserEntity) = userDao.delete(*users)
}