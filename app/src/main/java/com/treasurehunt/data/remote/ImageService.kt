package com.treasurehunt.data.remote

import com.treasurehunt.data.remote.model.ImageDTO
import com.treasurehunt.data.remote.model.RemoteIdWrapper
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ImageService {

    @POST("images.json")
    suspend fun insert(@Body imageDTO: ImageDTO): RemoteIdWrapper

    @GET("images/{id}.json")
    suspend fun getImage(
        @Path("id") id: String
    ): ImageDTO

}
