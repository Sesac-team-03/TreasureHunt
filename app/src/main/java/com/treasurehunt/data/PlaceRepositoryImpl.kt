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

    override suspend fun insert(place: PlaceDTO) = placeRemoteDataSource.insert(place)

    override fun getLocalPlaceById(id: String) = placeDao.getLocalPlaceById(id)

    override fun getAllLocalVisits() = placeDao.getAllLocalVisits()

    override fun getAllLocalPlans() = placeDao.getAllLocalPlans()

    override suspend fun getRemotePlaceById(id: String) = placeRemoteDataSource.getRemotePlaceById(id)

    override suspend fun update(place: PlaceEntity) = placeDao.update(place)

    override suspend fun update(id: String, place: PlaceDTO) {
        placeRemoteDataSource.update(id, place)
    }

    override suspend fun delete(vararg places: PlaceEntity) = placeDao.delete(*places)

    override suspend fun deleteAllLocalPlaces() {
        placeDao.deleteAllLocalPlaces()
    }

    override suspend fun delete(id: String) {
        placeRemoteDataSource.delete(id)
    }
}