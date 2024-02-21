package com.treasurehunt.data.remote

import com.treasurehunt.data.remote.model.LogDTO
import com.treasurehunt.data.remote.model.RemoteIdWrapper
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface LogService {

    @POST("logs.json")
    suspend fun insert(@Body logDTO: LogDTO): RemoteIdWrapper

    @GET("/logs/{id}.json")
    suspend fun getRemoteLog(@Path("id") id: String): LogDTO

    @GET("logs.json")
    suspend fun getAllRemoteLogs(): List<LogDTO>

    @DELETE("/logs/{id}.json")
    suspend fun delete(@Path("id") id: String)
}
