package com.treasurehunt.data

import com.treasurehunt.data.local.model.PlaceEntity
import com.treasurehunt.data.remote.model.PlaceDTO
import kotlinx.coroutines.flow.Flow

interface PlaceRepository {

    suspend fun insert(place: PlaceEntity): Long

    suspend fun insert(place: PlaceDTO): String

    suspend fun getRemotePlace(id: String): PlaceDTO

    fun getPlaceById(id: String): Flow<PlaceEntity>

    fun getAllVisits(): Flow<List<PlaceEntity>>

    fun getAllPlans(): Flow<List<PlaceEntity>>

    suspend fun update(place: PlaceEntity): Int

    suspend fun update(id: String, placeDTO: PlaceDTO)

    suspend fun delete(vararg places: PlaceEntity): Int

    suspend fun deleteAll()
}
