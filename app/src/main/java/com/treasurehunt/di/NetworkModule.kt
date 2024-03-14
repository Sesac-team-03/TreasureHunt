package com.treasurehunt.di

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.treasurehunt.BuildConfig
import com.treasurehunt.data.remote.ImageService
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
import javax.inject.Named
import javax.inject.Singleton

private const val HEADER_USER_AGENT = "User-Agent"
private const val APP_NAME = "TreasureHunt"
private const val BASE_URL = BuildConfig.BASE_URL
private val contentType = "application/json".toMediaType()
private const val CLIENT_QUALIFIER_REMOTE_DATABASE = "RemoteDatabase"

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Singleton
    @Provides
    @Named(CLIENT_QUALIFIER_REMOTE_DATABASE)
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
    @Named(CLIENT_QUALIFIER_REMOTE_DATABASE)
    fun provideRemoteBuilder(
        @Named(CLIENT_QUALIFIER_REMOTE_DATABASE) okHttpClient: OkHttpClient
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(Json.asConverterFactory(contentType))
            .build()
    }

    @Provides
    fun provideUserService(
        @Named(CLIENT_QUALIFIER_REMOTE_DATABASE) remoteBuilder: Retrofit
    ): UserService =
        remoteBuilder.create(UserService::class.java)

    @Provides
    fun provideLogService(
        @Named(CLIENT_QUALIFIER_REMOTE_DATABASE) remoteBuilder: Retrofit
    ): LogService =
        remoteBuilder.create(LogService::class.java)

    @Provides
    fun providePlaceService(
        @Named(CLIENT_QUALIFIER_REMOTE_DATABASE) remoteBuilder: Retrofit
    ): PlaceService =
        remoteBuilder.create(PlaceService::class.java)

    @Provides
    fun provideImageService(
        @Named(CLIENT_QUALIFIER_REMOTE_DATABASE) remoteBuilder: Retrofit
    ): ImageService =
        remoteBuilder.create(ImageService::class.java)
}