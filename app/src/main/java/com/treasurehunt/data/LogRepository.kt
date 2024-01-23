package com.treasurehunt.data

import com.treasurehunt.data.local.model.LogEntity
import com.treasurehunt.data.remote.model.LogDTO
import kotlinx.coroutines.flow.Flow

interface LogRepository {

    suspend fun insert(log: LogEntity): Long

    suspend fun getRemoteLog(id: String): LogDTO

    suspend fun getRemoteLogs(): List<LogDTO>

    suspend fun addRemoteLog(logModel: LogDTO)

    fun getLogById(id: String): Flow<LogEntity>

    fun getAllLogs(): Flow<List<LogEntity>>

    fun update(log: LogEntity): Int

    suspend fun delete(vararg logs: LogEntity): Int

    suspend fun deleteAll()
}