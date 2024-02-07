package com.treasurehunt.data.remote

import com.treasurehunt.data.remote.model.LogDTO
import javax.inject.Inject

class LogRemoteDataSource @Inject constructor(private val logService: LogService) {

    suspend fun getLog(id: String) = logService.getLog(id)

    suspend fun getAllLogs(): List<LogDTO> = logService.getAllLogs()

    suspend fun insert(logDTO: LogDTO) = logService.insert(logDTO)
}