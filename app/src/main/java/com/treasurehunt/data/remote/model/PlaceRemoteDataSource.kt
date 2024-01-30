package com.treasurehunt.data.remote.model

import com.treasurehunt.data.remote.PlaceService

class PlaceRemoteDataSource(private val placeService: PlaceService) {

    suspend fun insert(placeDTO: PlaceDTO) = placeService.insert(placeDTO).name

    suspend fun getPlace(id: String) = placeService.getPlace(id)

    suspend fun update(id: String, placeDTO: PlaceDTO) {
        placeService.update(id, placeDTO)
    }
}