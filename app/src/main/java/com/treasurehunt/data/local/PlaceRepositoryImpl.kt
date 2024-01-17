package com.treasurehunt.data.local

import com.treasurehunt.data.model.PlaceEntity
import kotlinx.coroutines.flow.Flow

class PlaceRepositoryImpl(private val placeDao: PlaceDao) : PlaceRepository {
    override suspend fun insert(place: PlaceEntity) {
        placeDao.insert(place)
    }

    override fun getPlaceById(id: String): Flow<PlaceEntity> = placeDao.getPlaceById(id)

    override fun getAllPlaces(): Flow<List<PlaceEntity>> = placeDao.getAllPlaces()

    override fun getAllPlans(): Flow<List<PlaceEntity>> = placeDao.getAllPlans()

    override suspend fun delete(id: String) {
        placeDao.delete(id)
    }
}