package com.treasurehunt.data.remote

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.treasurehunt.BuildConfig
import com.treasurehunt.data.remote.model.PlaceDTO
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Path


private const val BASE_URL = BuildConfig.BASE_URL
private val jsonRule = Json {
    isLenient = true
    ignoreUnknownKeys = true
    coerceInputValues = true
}

interface PlaceService {

    @GET("/places/{id}.json")
    suspend fun getPlace(
        @Path("id") id: String
    ): PlaceDTO

    companion object {

        fun create(): PlaceService {
            val contentType = "application/json".toMediaType()
            return Retrofit.Builder()
                .client(Client.getClient())
                .baseUrl(BASE_URL)
                .addConverterFactory(jsonRule.asConverterFactory(contentType))
                .build()
                .create(PlaceService::class.java)
        }
    }
}