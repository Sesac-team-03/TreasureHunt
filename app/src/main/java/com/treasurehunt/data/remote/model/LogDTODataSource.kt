package com.treasurehunt.data.remote.model

import com.treasurehunt.data.remote.RemoteDatabaseService

class LogDTODataSource(private val remoteDatabaseService: RemoteDatabaseService) {

    suspend fun getLog(id: String) = remoteDatabaseService.getLog(id)

}