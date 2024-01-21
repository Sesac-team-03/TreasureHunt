package com.treasurehunt.data.remote

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.treasurehunt.BuildConfig
import com.treasurehunt.data.model.UserDTO
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.http.Body
import retrofit2.http.PUT
import retrofit2.http.Path

private const val BASE_URL = BuildConfig.BASE_URL
private const val HEADER_USER_AGENT = "User-Agent"
private const val APP_NAME = "TreasureHunt"
private val jsonRule = Json {
    isLenient = true
    ignoreUnknownKeys = true
    coerceInputValues = true
}

interface FirebaseService {

    @PUT("/users/{uid}.json")
    suspend fun resisterUser(
        @Path("uid") uid: String,
        @Body data: UserDTO
    )

    companion object {

        fun create(): FirebaseService {
            val contentType = "application/json".toMediaType()
            return Retrofit.Builder()
                .client(getClient())
                .baseUrl(BASE_URL)
                .addConverterFactory(jsonRule.asConverterFactory(contentType))
                .build()
                .create(FirebaseService::class.java)
        }

        private fun getClient(): OkHttpClient {
            return OkHttpClient
                .Builder()
                .addNetworkInterceptor { chain ->
                    val builder = chain.request().newBuilder()
                    builder.header(HEADER_USER_AGENT, APP_NAME)
                    return@addNetworkInterceptor chain.proceed(builder.build())
                }.build()
        }
    }

}