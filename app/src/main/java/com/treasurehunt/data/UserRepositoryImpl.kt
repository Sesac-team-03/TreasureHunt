package com.treasurehunt.data

import com.treasurehunt.data.local.UserDao
import com.treasurehunt.data.local.model.UserEntity
import com.treasurehunt.data.remote.UserRemoteDataSource
import com.treasurehunt.data.remote.model.UserDTO
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userDao: UserDao,
    private val userRemoteDataSource: UserRemoteDataSource
) : UserRepository {

    override suspend fun insert(user: UserEntity) = userDao.insert(user)

    override suspend fun insert(id: String, data: UserDTO) {
        userRemoteDataSource.insert(id, data)
    }

    override suspend fun getRemoteUser(id: String): UserDTO = userRemoteDataSource.getUserData(id)

    override fun getUserById(id: String): Flow<UserEntity> = userDao.getUserById(id)

    override fun getAllUsers(): Flow<List<UserEntity>> = userDao.getAllUsers()

    override fun update(user: UserEntity) = userDao.update(user)

    override suspend fun update(id: String, data: UserDTO) {
        userRemoteDataSource.update(id, data)
    }

    override suspend fun delete(vararg users: UserEntity) = userDao.delete(*users)

    override suspend fun search(startAt: String, limit: Int) = userRemoteDataSource.search(startAt, limit)
}