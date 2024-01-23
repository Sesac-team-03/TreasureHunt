package com.treasurehunt.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.treasurehunt.data.local.model.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(user: UserEntity): Long

    @Query("SELECT * from users WHERE id = :id")
    fun getUserById(id: String): Flow<UserEntity>

    @Query("SELECT * from users")
    fun getAllUsers(): Flow<List<UserEntity>>

    @Update
    fun update(user: UserEntity): Int

    @Delete
    suspend fun delete(vararg users: UserEntity): Int
}