package com.treasurehunt.data.network

import kotlinx.serialization.Serializable
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface LogService {
    @GET("logs.json")
    suspend fun getLogs(): List<LogModel>

    @POST("logs.json")
    suspend fun addLog(@Body logModel: LogModel)
}

@Serializable
data class LogModel (
    val createdDate: Long,
    val images: List<String>,
    val place: Int,
    val text: String,
    val theme: Int,
    val user: Int
)