package com.treasurehunt.data.remote.model

import com.treasurehunt.data.remote.LogService

class LogRemoteDataSource(private val logService: LogService) {

    suspend fun getLog(id: String) = logService.getLog(id)

    suspend fun getAllLogs(): List<LogDTO> = logService.getAllLogs()

    suspend fun insert(logDTO: LogDTO) = logService.insert(logDTO)
}