package com.treasurehunt.data.remote

import com.treasurehunt.data.remote.model.ImageDTO
import com.treasurehunt.data.remote.model.RemoteIdWrapper
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

internal const val REMOTE_DATABASE_IMAGES = "images"

interface ImageService {

    @POST("$REMOTE_DATABASE_IMAGES.json")
    suspend fun insert(@Body image: ImageDTO): RemoteIdWrapper

    @GET("$REMOTE_DATABASE_IMAGES/{id}.json")
    suspend fun getRemoteImageById(@Path("id") id: String): ImageDTO

    @DELETE("$REMOTE_DATABASE_IMAGES/{id}.json")
    suspend fun delete(@Path("id") id: String)
}
