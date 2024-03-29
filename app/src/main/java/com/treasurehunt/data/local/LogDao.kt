package com.treasurehunt.data.local

import androidx.paging.PagingSource
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

    @Query("SELECT * from logs WHERE localId = :id")
    fun getLocalLogById(id: String): Flow<LogEntity>

    @Query("SELECT * from logs ORDER BY created_date DESC")
    fun getAllLocalLogs(): Flow<List<LogEntity>>

    @Query("SELECT * FROM logs ORDER BY created_date DESC")
    fun getPagingLogs(): PagingSource<Int, LogEntity>

    @Update
    fun update(log: LogEntity): Int

    @Delete
    suspend fun delete(vararg logs: LogEntity): Int

    @Query("DELETE FROM logs")
    suspend fun deleteAllLocalLogs()
}