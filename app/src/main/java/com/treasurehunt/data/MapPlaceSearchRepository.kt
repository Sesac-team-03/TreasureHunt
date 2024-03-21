package com.treasurehunt.data

import com.treasurehunt.data.remote.model.MapPlaceSearchResultDTO

interface MapPlaceSearchRepository {

    suspend fun getMapPlaceByKeyword(keyword: String): MapPlaceSearchResultDTO
}