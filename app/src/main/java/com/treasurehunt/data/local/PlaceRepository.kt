package com.treasurehunt.data.local

import com.treasurehunt.data.model.PlaceEntity
import kotlinx.coroutines.flow.Flow

interface PlaceRepository {

    suspend fun insert(place: PlaceEntity)

    fun getPlaceById(id: String): Flow<PlaceEntity>

    fun getAllPlaces(): Flow<List<PlaceEntity>>

    fun getAllPlans(): Flow<List<PlaceEntity>>

    suspend fun delete(id: String)
}

