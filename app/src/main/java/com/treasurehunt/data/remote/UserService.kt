package com.treasurehunt.data.remote

import com.treasurehunt.data.remote.model.UserDTO
import kotlinx.serialization.json.JsonObject
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

internal const val REMOTE_DATABASE_USERS = "users"

interface UserService {

    @PUT("$REMOTE_DATABASE_USERS/{id}.json")
    suspend fun insert(
        @Path("id") id: String,
        @Body user: UserDTO
    )

    @GET("$REMOTE_DATABASE_USERS/{id}.json")
    suspend fun getRemoteUserById(@Path("id") id: String): UserDTO

    @PATCH("$REMOTE_DATABASE_USERS/{id}.json")
    suspend fun update(
        @Path("id") id: String,
        @Body user: UserDTO
    )

    @DELETE("$REMOTE_DATABASE_USERS/{id}.json")
    suspend fun delete(@Path("id") id: String)

    suspend fun search(
        @Query("orderBy") orderBy: String,
        @Query("startAt") startAt: String,
        @Query("limitToFirst") limit: Int
    ): JsonObject
}