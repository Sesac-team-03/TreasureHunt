package com.treasurehunt.di

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.treasurehunt.BuildConfig
import com.treasurehunt.data.remote.MapPlaceSearchService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import javax.inject.Named
import javax.inject.Singleton

private const val HEADER_USER_AGENT = "User-Agent"
private const val APP_NAME = "TreasureHunt"
private const val HEADER_CLIENT_ID = "X-Naver-Client-Id"
private const val CLIENT_ID = BuildConfig.X_NAVER_CLIENT_ID
private const val HEADER_CLIENT_SECRET = "X-Naver-Client-Secret"
private const val CLIENT_SECRET = BuildConfig.X_NAVER_CLIENT_SECRET
private const val BASE_URL = BuildConfig.NAVER_SEARCH_API_BASE_URL
private val contentType = "application/json".toMediaType()
private const val CLIENT_QUALIFIER_MAP_PLACE_SEARCH = "MapPlaceSearch"

@OptIn(ExperimentalSerializationApi::class)
private val json = Json {
    ignoreUnknownKeys = true
    coerceInputValues = true
    explicitNulls = false
}

@Module
@InstallIn(SingletonComponent::class)
object MapPlaceSearchModule {

    @Singleton
    @Provides
    @Named(CLIENT_QUALIFIER_MAP_PLACE_SEARCH)
    fun provideClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addNetworkInterceptor { chain ->
                val builder = chain.request().newBuilder()
                builder.header(HEADER_USER_AGENT, APP_NAME)
                    .header(HEADER_CLIENT_ID, CLIENT_ID)
                    .header(HEADER_CLIENT_SECRET, CLIENT_SECRET)
                return@addNetworkInterceptor chain.proceed(builder.build())
            }.build()
    }

    @Singleton
    @Provides
    @Named(CLIENT_QUALIFIER_MAP_PLACE_SEARCH)
    fun provideRemoteBuilder(
        @Named(CLIENT_QUALIFIER_MAP_PLACE_SEARCH) okHttpClient: OkHttpClient
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
    }

    @Provides
    fun provideMapSearchService(
        @Named(CLIENT_QUALIFIER_MAP_PLACE_SEARCH) remoteBuilder: Retrofit
    ): MapPlaceSearchService =
        remoteBuilder.create(MapPlaceSearchService::class.java)
}