package com.treasurehunt.di

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.treasurehunt.BuildConfig
import com.treasurehunt.data.remote.LogService
import com.treasurehunt.data.remote.PlaceService
import com.treasurehunt.data.remote.UserService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import javax.inject.Singleton


private const val HEADER_USER_AGENT = "User-Agent"
private const val APP_NAME = "TreasureHunt"
private const val BASE_URL = BuildConfig.BASE_URL
private val contentType = "application/json".toMediaType()

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Singleton
    @Provides
    fun provideClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addNetworkInterceptor { chain ->
                val builder = chain.request().newBuilder()
                builder.header(HEADER_USER_AGENT, APP_NAME)
                return@addNetworkInterceptor chain.proceed(builder.build())
            }.build()
    }

    @Singleton
    @Provides
    fun provideRemoteBuilder(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(Json.asConverterFactory(contentType))
            .build()
    }


    @Provides
    fun provideUserService(remoteBuilder:Retrofit): UserService =
        remoteBuilder.create(UserService::class.java)


    @Provides
    fun provideLogService(remoteBuilder:Retrofit): LogService =
        remoteBuilder.create(LogService::class.java)

    @Provides
    fun providePlaceService(remoteBuilder:Retrofit): PlaceService =
        remoteBuilder.create(PlaceService::class.java)

}