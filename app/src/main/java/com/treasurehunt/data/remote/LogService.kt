package com.treasurehunt.data.remote

import com.treasurehunt.data.remote.model.LogDTO
import com.treasurehunt.data.remote.model.RemoteIdWrapper
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface LogService {

    @GET("/logs/{id}.json")
    suspend fun getLog(
        @Path("id") id: String
    ): LogDTO

    @GET("logs.json")
    suspend fun getAllLogs(): List<LogDTO>

    @POST("logs.json")
    suspend fun insert(@Body logDTO: LogDTO): RemoteIdWrapper

    @DELETE("/logs/{id}.json")
    suspend fun deleteLog(@Path("id") id: String) : Response<Unit>
}
