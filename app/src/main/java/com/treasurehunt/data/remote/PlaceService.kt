package com.treasurehunt.data.remote

import com.treasurehunt.data.remote.model.PlaceDTO
import com.treasurehunt.data.remote.model.RemoteIdWrapper
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

internal const val REMOTE_DATABASE_PLACES = "places"

interface PlaceService {

    @POST("$REMOTE_DATABASE_PLACES.json")
    suspend fun insert(@Body place: PlaceDTO): RemoteIdWrapper

    @GET("$REMOTE_DATABASE_PLACES/{id}.json")
    suspend fun getRemotePlaceById(@Path("id") id: String): PlaceDTO

    @PATCH("$REMOTE_DATABASE_PLACES/{id}.json")
    suspend fun update(
        @Path("id") id: String,
        @Body place: PlaceDTO
    )

    @DELETE("$REMOTE_DATABASE_PLACES/{id}.json")
    suspend fun delete(@Path("id") id: String)
}