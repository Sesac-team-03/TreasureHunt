package com.treasurehunt.data.remote

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.treasurehunt.BuildConfig
import com.treasurehunt.data.remote.model.PlaceDTO
import com.treasurehunt.data.remote.model.RemoteIdWrapper
import com.treasurehunt.data.remote.model.UserDTO
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path


private const val BASE_URL = BuildConfig.BASE_URL
private val jsonRule = Json {
    isLenient = true
    ignoreUnknownKeys = true
    coerceInputValues = true
}

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