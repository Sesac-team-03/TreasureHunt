package com.treasurehunt.data.remote

import com.treasurehunt.data.remote.model.PlaceDTO
import javax.inject.Inject

class PlaceRemoteDataSource @Inject constructor(private val placeService: PlaceService) {

    suspend fun insert(place: PlaceDTO) = placeService.insert(place).name

    suspend fun getRemotePlaceById(id: String) = placeService.getRemotePlaceById(id)

    suspend fun update(id: String, place: PlaceDTO) {
        placeService.update(id, place)
    }

    suspend fun delete(id: String) {
        placeService.delete(id)
    }
}