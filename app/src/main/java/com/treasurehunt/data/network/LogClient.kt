package com.treasurehunt.data.network


import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit

object LogClient {

    private const val BASE_URL =
        "https://treasurehunt-32565-default-rtdb.asia-southeast1.firebasedatabase.app/"
    private val jsonRule = Json {
        isLenient = true
        ignoreUnknownKeys = true
        coerceInputValues = true
    }
    private val jsonType: MediaType = "apapplication/json".toMediaType()

    fun create(): LogService {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(jsonRule.asConverterFactory(jsonType))
            .build()
            .create(LogService::class.java)
    }
}