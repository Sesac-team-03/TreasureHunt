package com.treasurehunt.data.local

import com.treasurehunt.data.remote.model.LogEntity
import kotlinx.coroutines.flow.Flow

interface LogRepository {

    suspend fun insert(log: LogEntity): Long

    fun getLogById(id: String): Flow<LogEntity>

    fun getAllLogs(): Flow<List<LogEntity>>

    fun update(log: LogEntity): Int

    suspend fun delete(vararg logs: LogEntity): Int
}