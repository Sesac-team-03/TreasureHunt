package com.treasurehunt.data.remote

import com.treasurehunt.data.remote.model.LogDTO
import javax.inject.Inject

class LogRemoteDataSource @Inject constructor(private val logService: LogService) {

    suspend fun insert(logDTO: LogDTO) = logService.insert(logDTO)

    suspend fun getRemoteLog(id: String) = logService.getRemoteLog(id)

    suspend fun getAllRemoteLogs(): List<LogDTO> = logService.getAllRemoteLogs()

    suspend fun delete(id: String) {
        logService.delete(id)
    }
}