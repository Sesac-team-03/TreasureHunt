package com.treasurehunt.di

import android.content.Context
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.treasurehunt.BuildConfig
import com.treasurehunt.data.LogRepositoryImpl
import com.treasurehunt.data.PlaceRepositoryImpl
import com.treasurehunt.data.UserRepositoryImpl
import com.treasurehunt.data.local.TreasureHuntDatabase
import com.treasurehunt.data.remote.Client
import com.treasurehunt.data.remote.LogRemoteDataSource
import com.treasurehunt.data.remote.LogService
import com.treasurehunt.data.remote.PlaceService
import com.treasurehunt.data.remote.UserRemoteDataSource
import com.treasurehunt.data.remote.UserService
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit

private const val BASE_URL = BuildConfig.BASE_URL
private val contentType = "application/json".toMediaType()


private val jsonRule = Json {
    isLenient = true
    ignoreUnknownKeys = true
    coerceInputValues = true
}

object AppContainer {

    private val retrofitBuilder = Retrofit.Builder()
        .client(Client.getClient())
        .baseUrl(BASE_URL)
        .addConverterFactory(jsonRule.asConverterFactory(contentType))
        .build()

    private val userService = retrofitBuilder.create(UserService::class.java)
    private val logService = retrofitBuilder.create(LogService::class.java)
    private val placeService = retrofitBuilder.create(PlaceService::class.java)

    private val userRemoteDataSource = UserRemoteDataSource(userService)
    private val logRemoteDataSource = LogRemoteDataSource(logService)
    private val placeRemoteDataSource = PlaceRemoteDataSource(placeService)

    private fun createDatabase(context:Context) = TreasureHuntDatabase.from(context)

    fun provideUserRepository(context: Context) =
        UserRepositoryImpl(createDatabase(context).userDao(), userRemoteDataSource)

    fun provideLogRepository(context: Context) =
        LogRepositoryImpl(createDatabase(context).logDao(), logRemoteDataSource)

    fun providePlaceRepository(context: Context) =
        PlaceRepositoryImpl(createDatabase(context).placeDao(), placeRemoteDataSource)

}

