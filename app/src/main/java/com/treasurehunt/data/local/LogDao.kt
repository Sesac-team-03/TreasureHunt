package com.treasurehunt.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.treasurehunt.data.local.model.LogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LogDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(log: LogEntity): Long

    @Query("SELECT * from logs WHERE uid = :id")
    fun getLogById(id: String): Flow<LogEntity>

    @Query("SELECT * from logs ORDER BY created_date DESC")
    fun getAllLogs(): Flow<List<LogEntity>>

    @Update
    fun update(log: LogEntity): Int

    @Delete
    suspend fun delete(vararg logs: LogEntity): Int

    @Query("DELETE FROM logs")
    suspend fun deleteAllLogs()
}