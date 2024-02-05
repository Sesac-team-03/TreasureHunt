package com.treasurehunt.data.remote

import com.treasurehunt.data.remote.model.ImageDTO
import com.treasurehunt.data.remote.model.RemoteIdWrapper
import retrofit2.http.Body
import retrofit2.http.POST

interface ImageService {

    @POST("images.json")
    suspend fun insert(@Body imageDTO: ImageDTO): RemoteIdWrapper

}
