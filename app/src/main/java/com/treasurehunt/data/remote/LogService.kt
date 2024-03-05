package com.treasurehunt.data.remote

import com.treasurehunt.data.remote.model.LogDTO
import com.treasurehunt.data.remote.model.RemoteIdWrapper
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

internal const val REMOTE_DATABASE_LOGS = "logs"

interface LogService {

    @POST("$REMOTE_DATABASE_LOGS.json")
    suspend fun insert(@Body log: LogDTO): RemoteIdWrapper

    @GET("$REMOTE_DATABASE_LOGS/{id}.json")
    suspend fun getRemoteLogById(@Path("id") id: String): LogDTO

    @GET("$REMOTE_DATABASE_LOGS.json")
    suspend fun getAllRemoteLogs(): List<LogDTO>

    @PATCH("$REMOTE_DATABASE_LOGS/{id}.json")
    suspend fun update(
        @Path("id") id: String,
        @Body log: LogDTO
    )

    @DELETE("$REMOTE_DATABASE_LOGS/{id}.json")
    suspend fun delete(@Path("id") id: String)
}
