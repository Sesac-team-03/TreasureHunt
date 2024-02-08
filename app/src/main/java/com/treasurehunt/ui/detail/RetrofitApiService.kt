package com.treasurehunt.ui.detail

import com.treasurehunt.ui.model.LogModel
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

interface RetrofitApiService {
    @GET("logs/{logId}.json")
    suspend fun getLogData(@Path("logId") logId: String): LogModel
}

object NetworkModule {
    private const val BASE_URL = ""

    val service: RetrofitApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RetrofitApiService::class.java)
    }
}