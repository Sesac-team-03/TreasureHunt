package com.treasurehunt.data.remote

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.treasurehunt.BuildConfig
import com.treasurehunt.data.remote.model.UserDTO
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.PUT
import retrofit2.http.Path

private const val BASE_URL = BuildConfig.BASE_URL
private val jsonRule = Json {
    isLenient = true
    ignoreUnknownKeys = true
    coerceInputValues = true
}

interface UserService {

    @PUT("/users/{id}.json")
    suspend fun insert(
        @Path("id") id: String,
        @Body data: UserDTO
    )

    @PATCH("/users/{id}.json")
    suspend fun update(
        @Path("id") id: String,
        @Body data: UserDTO
    )

    @GET("/users/{id}.json")
    suspend fun getUser(
        @Path("id") id: String
    ): UserDTO

    companion object {

        fun create(): UserService {
            val contentType = "application/json".toMediaType()
            return Retrofit.Builder()
                .client(Client.getClient())
                .baseUrl(BASE_URL)
                .addConverterFactory(jsonRule.asConverterFactory(contentType))
                .build()
                .create(UserService::class.java)
        }
    }
}