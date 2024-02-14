package com.treasurehunt.data

import com.treasurehunt.data.local.PlaceDao
import com.treasurehunt.data.local.model.PlaceEntity
import com.treasurehunt.data.remote.PlaceRemoteDataSource
import com.treasurehunt.data.remote.model.PlaceDTO
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PlaceRepositoryImpl @Inject constructor(
    private val placeDao: PlaceDao,
    private val placeRemoteDataSource: PlaceRemoteDataSource
) : PlaceRepository {

    override suspend fun insert(place: PlaceEntity) = placeDao.insert(place)

    override suspend fun insert(place: PlaceDTO): String = placeRemoteDataSource.insert(place)

    override suspend fun getRemotePlace(id: String): PlaceDTO = placeRemoteDataSource.getPlace(id)

    override fun getPlaceById(id: String): Flow<PlaceEntity> = placeDao.getPlaceById(id)

    override fun getAllVisits(): Flow<List<PlaceEntity>> = placeDao.getAllVisits()

    override fun getAllPlans(): Flow<List<PlaceEntity>> = placeDao.getAllPlans()

    override suspend fun update(place: PlaceEntity) = placeDao.update(place)

    override suspend fun update(id: String, placeDTO: PlaceDTO) {
        placeRemoteDataSource.update(id, placeDTO)
    }

    override suspend fun delete(vararg places: PlaceEntity) = placeDao.delete(*places)

    override suspend fun deleteAll() {
        placeDao.deleteAllPlaces()
    }

    override suspend fun deletePlace(placeId: String) {
        placeDao.deletePlace(placeId)
    }
}