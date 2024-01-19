package com.treasurehunt.data.local

import com.treasurehunt.data.model.LogEntity
import kotlinx.coroutines.flow.Flow

interface LogRepository {

    suspend fun insert(log: LogEntity)

    fun getLogById(id: String): Flow<LogEntity>

    fun getAllLogs(): Flow<List<LogEntity>>

    suspend fun delete(log: LogEntity)
}