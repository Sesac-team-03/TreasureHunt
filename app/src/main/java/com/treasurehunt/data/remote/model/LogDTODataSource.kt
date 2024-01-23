package com.treasurehunt.data.remote.model

import com.treasurehunt.data.remote.LogService

class LogDTODataSource(private val logService: LogService) {

    suspend fun getLog(id: String) = logService.getLog(id)

    suspend fun getLogs(): List<LogDTO> = logService.getLogs()

    suspend fun addLog(logModel: LogDTO) = logService.addLog(logModel)
}