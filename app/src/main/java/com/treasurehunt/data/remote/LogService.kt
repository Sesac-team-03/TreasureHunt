package com.treasurehunt.data.remote

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.treasurehunt.BuildConfig
import com.treasurehunt.data.remote.model.LogDTO
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

private const val BASE_URL = BuildConfig.BASE_URL
private val jsonRule = Json {
    isLenient = true
    ignoreUnknownKeys = true
    coerceInputValues = true
}

interface LogService {

    @GET("/logs/{id}.json")
    suspend fun getLog(
        @Path("id") id: String
    ): LogDTO

    @GET("logs.json")
    suspend fun getAllLogs(): List<LogDTO>

    @POST("logs.json")
    suspend fun insert(@Body logDTO: LogDTO)

    companion object {

        fun create(): LogService {
            val contentType = "application/json".toMediaType()
            return Retrofit.Builder()
                .client(Client.getClient())
                .baseUrl(BASE_URL)
                .addConverterFactory(jsonRule.asConverterFactory(contentType))
                .build()
                .create(LogService::class.java)
        }
    }

}