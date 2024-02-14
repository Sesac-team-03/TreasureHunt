package com.treasurehunt.data

import com.google.firebase.database.FirebaseDatabase
import com.treasurehunt.data.local.UserDao
import com.treasurehunt.data.local.model.UserEntity
import com.treasurehunt.data.remote.UserRemoteDataSource
import com.treasurehunt.data.remote.model.UserDTO
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userDao: UserDao,
    private val userRemoteDataSource: UserRemoteDataSource
) : UserRepository {

    override suspend fun insert(user: UserEntity) = userDao.insert(user)

    override suspend fun insert(id: String, user: UserDTO) {
        userRemoteDataSource.insert(id, user)
    }

    override fun getLocalUserById(id: String) = userDao.getLocalUserById(id)

    override fun getAllLocalUsers() = userDao.getAllLocalUsers()

    override suspend fun getRemoteUserById(id: String) = userRemoteDataSource.getRemoteUserById(id)

    override fun update(user: UserEntity) = userDao.update(user)

    override suspend fun update(id: String, user: UserDTO) {
        userRemoteDataSource.update(id, user)
    }

    override suspend fun delete(vararg users: UserEntity) = userDao.delete(*users)

    override suspend fun search(startAt: String, limit: Int) = userRemoteDataSource.search(startAt, limit)

//    override suspend fun deleteUser(userId: String) {
//        userRemoteDataSource.deleteUser(userId)
//    }
//
//    override suspend fun deleteUser(userId: String) {
//        val databaseReference = FirebaseDatabase.getInstance().getReference("users").child(userId)
//        databaseReference.removeValue()
//    }
}