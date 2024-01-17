package com.treasurehunt.data.remote.model

import com.treasurehunt.data.remote.RemoteDatabaseService

class PlaceDTODataSource(private val remoteDatabaseService: RemoteDatabaseService) {

    suspend fun getPlace(id: String) = remoteDatabaseService.getPlace(id)

}