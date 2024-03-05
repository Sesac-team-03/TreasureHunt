package com.treasurehunt.data

import com.treasurehunt.data.local.model.LogEntity
import com.treasurehunt.data.remote.model.LogDTO
import kotlinx.coroutines.flow.Flow

interface LogRepository {

    suspend fun insert(log: LogEntity): Long

    suspend fun insert(log: LogDTO): String

    fun getLocalLogById(id: String): Flow<LogEntity>

    fun getAllLocalLogs(): Flow<List<LogEntity>>

    suspend fun getRemoteLogById(id: String): LogDTO

    suspend fun getAllRemoteLogs(): List<LogDTO>

    fun update(log: LogEntity): Int

    suspend fun update(id: String, log: LogDTO)

    suspend fun delete(vararg logs: LogEntity): Int

    suspend fun deleteAllLocalLogs()

    suspend fun delete(id: String)
}