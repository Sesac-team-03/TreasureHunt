package com.treasurehunt.data.remote

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.treasurehunt.BuildConfig
import com.treasurehunt.data.remote.model.ImageDTO
import com.treasurehunt.data.remote.model.RemoteIdWrapper
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.http.Body
import retrofit2.http.POST

private const val BASE_URL = BuildConfig.BASE_URL
private val jsonRule = Json {
    isLenient = true
    ignoreUnknownKeys = true
    coerceInputValues = true
}

interface ImageService {

    @POST("images.json")
    suspend fun insert(@Body imageDTO: ImageDTO): RemoteIdWrapper

    companion object {

        fun create(): ImageService {
            val contentType = "application/json".toMediaType()
            return Retrofit.Builder()
                .client(Client.getClient())
                .baseUrl(BASE_URL)
                .addConverterFactory(jsonRule.asConverterFactory(contentType))
                .build()
                .create(ImageService::class.java)
        }
    }
}