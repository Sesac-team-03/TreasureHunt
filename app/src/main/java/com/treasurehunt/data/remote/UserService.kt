package com.treasurehunt.data.remote

import com.treasurehunt.data.remote.model.UserDTO
import kotlinx.serialization.json.JsonObject
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface UserService {

    @PUT("users/{id}.json")
    suspend fun insert(
        @Path("id") id: String,
        @Body userDTO: UserDTO
    )

    @GET("users/{id}.json")
    suspend fun getUser(
        @Path("id") id: String
    ): UserDTO

    @PATCH("users/{id}.json")
    suspend fun update(
        @Path("id") id: String,
        @Body userDTO: UserDTO
    )

    @GET("users.json")
    suspend fun search(
        @Query("orderBy") orderBy: String,
        @Query("startAt") startAt: String,
        @Query("limitToFirst") limit: Int
    ): JsonObject
}