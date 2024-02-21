package com.treasurehunt.data

import com.treasurehunt.data.local.model.LogEntity
import com.treasurehunt.data.remote.model.LogDTO
import kotlinx.coroutines.flow.Flow

interface LogRepository {

    suspend fun insert(log: LogEntity): Long

    suspend fun insert(log: LogDTO): String

    fun getLogById(id: String): Flow<LogEntity>

    fun getAllLogs(): Flow<List<LogEntity>>

    suspend fun getRemoteLog(id: String): LogDTO

    suspend fun getAllRemoteLogs(): List<LogDTO>

    fun update(log: LogEntity): Int

    suspend fun delete(vararg logs: LogEntity): Int

    suspend fun deleteAll()

    suspend fun delete(id: String)
}