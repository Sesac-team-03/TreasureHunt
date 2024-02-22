package com.treasurehunt.data.remote

import com.treasurehunt.data.remote.model.PlaceDTO
import com.treasurehunt.data.remote.model.RemoteIdWrapper
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface PlaceService {

    @POST("places.json")
    suspend fun insert(@Body place: PlaceDTO): RemoteIdWrapper

    @GET("places/{id}.json")
    suspend fun getRemotePlaceById(@Path("id") id: String): PlaceDTO

    @PATCH("places/{id}.json")
    suspend fun update(
        @Path("id") id: String,
        @Body place: PlaceDTO
    )

    @DELETE("places/{id}.json")
    suspend fun delete(@Path("id") id: String)
}