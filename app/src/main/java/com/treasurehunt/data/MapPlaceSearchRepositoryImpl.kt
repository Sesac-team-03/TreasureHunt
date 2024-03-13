package com.treasurehunt.data

import com.treasurehunt.data.remote.MapPlaceSearchService
import com.treasurehunt.data.remote.model.MapPlaceSearchResultDTO
import javax.inject.Inject

class MapPlaceSearchRepositoryImpl @Inject constructor(
    private val mapPlaceSearchService: MapPlaceSearchService
) : MapPlaceSearchRepository {

    override suspend fun getMapPlaceByKeyword(keyword: String): MapPlaceSearchResultDTO {
        return try {
            mapPlaceSearchService.getMapPlaceByKeyword(keyword)
        } catch(e: Exception) {
            MapPlaceSearchResultDTO()
        }
    }
}