package com.treasurehunt.data.remote

import com.treasurehunt.data.remote.model.MapPlaceSearchResultDTO
import retrofit2.http.GET
import retrofit2.http.Query

private const val LIMIT_MAX = 5

interface MapPlaceSearchService {

    @GET("local.json")
    suspend fun getMapPlaceByKeyword(
        @Query("query") keyword: String,
        @Query("display") limit: Int = LIMIT_MAX
    ): MapPlaceSearchResultDTO
}