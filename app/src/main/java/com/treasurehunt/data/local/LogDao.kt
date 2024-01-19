package com.treasurehunt.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.treasurehunt.data.model.LogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LogDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(log: LogEntity)

    @Query("SELECT * from logs WHERE uid = :id")
    fun getLogById(id: String): Flow<LogEntity>

    @Query("SELECT * from logs ORDER BY created_date DESC")
    fun getAllLogs(): Flow<List<LogEntity>>

    @Delete
    suspend fun delete(log: LogEntity)
}