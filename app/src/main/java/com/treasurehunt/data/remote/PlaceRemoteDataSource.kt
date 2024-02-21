package com.treasurehunt.data.remote

import com.treasurehunt.data.remote.model.PlaceDTO
import javax.inject.Inject

class PlaceRemoteDataSource @Inject constructor(private val placeService: PlaceService) {

    suspend fun insert(placeDTO: PlaceDTO) = placeService.insert(placeDTO).name

    suspend fun getRemotePlace(id: String) = placeService.getRemotePlace(id)

    suspend fun update(id: String, placeDTO: PlaceDTO) {
        placeService.update(id, placeDTO)
    }

    suspend fun delete(id: String) {
        placeService.delete(id)
    }
}