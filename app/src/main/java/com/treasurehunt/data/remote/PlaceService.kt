package com.treasurehunt.data.remote

import com.treasurehunt.data.remote.model.PlaceDTO
import com.treasurehunt.data.remote.model.RemoteIdWrapper
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface PlaceService {

    @POST("places.json")
    suspend fun insert(@Body placeDTO: PlaceDTO): RemoteIdWrapper

    @GET("/places/{id}.json")
    suspend fun getPlace(
        @Path("id") id: String
    ): PlaceDTO

    @PATCH("/places/{id}.json")
    suspend fun update(
        @Path("id") id: String,
        @Body data: PlaceDTO
    )
}