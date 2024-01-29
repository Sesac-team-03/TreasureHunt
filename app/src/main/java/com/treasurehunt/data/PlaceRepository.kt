package com.treasurehunt.data

import com.treasurehunt.data.local.model.PlaceEntity
import com.treasurehunt.data.remote.model.PlaceDTO
import com.treasurehunt.data.remote.model.UserDTO
import kotlinx.coroutines.flow.Flow

interface PlaceRepository {

    suspend fun insert(place: PlaceEntity): Long

    suspend fun getRemotePlace(id: String): PlaceDTO

    fun getPlaceById(id: String): Flow<PlaceEntity>

    fun getAllPlaces(): Flow<List<PlaceEntity>>

    fun getAllPlans(): Flow<List<PlaceEntity>>

    fun update(place: PlaceEntity): Int

    suspend fun delete(vararg places: PlaceEntity): Int
    suspend fun deleteAll()

}