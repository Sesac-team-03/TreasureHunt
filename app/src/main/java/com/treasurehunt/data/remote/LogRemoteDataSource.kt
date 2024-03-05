package com.treasurehunt.data.remote

import com.treasurehunt.data.remote.model.LogDTO
import javax.inject.Inject

class LogRemoteDataSource @Inject constructor(private val logService: LogService) {

    suspend fun insert(log: LogDTO) = logService.insert(log)

    suspend fun getRemoteLogById(id: String) = logService.getRemoteLogById(id)

    suspend fun getAllRemoteLogs() = logService.getAllRemoteLogs()

    suspend fun update(id: String, log: LogDTO) = logService.update(id, log)

    suspend fun delete(id: String) {
        logService.delete(id)
    }
}