package com.treasurehunt.data.remote

import com.treasurehunt.data.remote.model.UserDTO
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface UserService {

    @PUT("/users/{id}.json")
    suspend fun insert(
        @Path("id") id: String,
        @Body userDTO: UserDTO
    )

    @GET("/users/{id}.json")
    suspend fun getUser(
        @Path("id") id: String
    ): UserDTO

    @PATCH("/users/{id}.json")
    suspend fun update(
        @Path("id") id: String,
        @Body userDTO: UserDTO
    )

    @DELETE("/users/{id}.json")
    suspend fun deleteUser(@Path("id") id: String) : Response<Unit>

    @Serializable
    data class UserUpdates(
        val places: String? = null,
        val logs: String? = null
    )

    @PATCH("/users/{id}.json")
    suspend fun deleteUser2(@Path("id") id: String, @Body updates: UserUpdates): Response<Unit>

    @GET("/users.json")
    suspend fun search(
        @Query("orderBy") orderBy: String,
        @Query("startAt") startAt: String,
        @Query("limitToFirst") limit: Int
    ): JsonObject
}