package com.treasurehunt.data

import com.treasurehunt.data.local.model.PlaceEntity
import com.treasurehunt.data.remote.model.PlaceDTO
import kotlinx.coroutines.flow.Flow

interface PlaceRepository {

    suspend fun insert(place: PlaceEntity): Long

    suspend fun insert(place: PlaceDTO): String

    fun getLocalPlaceById(id: String): Flow<PlaceEntity>

    fun getAllLocalVisits(): Flow<List<PlaceEntity>>

    fun getAllLocalPlans(): Flow<List<PlaceEntity>>

    suspend fun getRemotePlaceById(id: String): PlaceDTO

    suspend fun update(place: PlaceEntity): Int

    suspend fun update(id: String, place: PlaceDTO)

    suspend fun delete(vararg places: PlaceEntity): Int

    suspend fun deleteAllLocalPlaces()

    suspend fun delete(id: String)
}
