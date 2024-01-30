package com.treasurehunt.data.remote.model

import com.treasurehunt.data.remote.PlaceService

class PlaceRemoteDataSource(private val placeService: PlaceService) {

    suspend fun getPlace(id: String) = placeService.getPlace(id)
}