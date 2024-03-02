package com.treasurehunt.data

import com.treasurehunt.data.remote.MapPlaceSearchService
import javax.inject.Inject

class MapPlaceSearchRepositoryImpl @Inject constructor(
    private val mapPlaceSearchService: MapPlaceSearchService
) : MapPlaceSearchRepository {

    override suspend fun getMapPlaceByKeyword(keyword: String) =
        mapPlaceSearchService.getMapPlaceByKeyword(keyword)
}