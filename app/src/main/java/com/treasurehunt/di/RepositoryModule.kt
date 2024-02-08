package com.treasurehunt.di

import com.treasurehunt.data.ImageRepository
import com.treasurehunt.data.ImageRepositoryImpl
import com.treasurehunt.data.LogRepository
import com.treasurehunt.data.LogRepositoryImpl
import com.treasurehunt.data.PlaceRepository
import com.treasurehunt.data.PlaceRepositoryImpl
import com.treasurehunt.data.UserRepository
import com.treasurehunt.data.UserRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindUserRepository(userRepositoryImpl: UserRepositoryImpl): UserRepository

    @Binds
    abstract fun bindLogRepository(logRepositoryImpl: LogRepositoryImpl): LogRepository

    @Binds
    abstract fun bindPlaceRepository(placeRepositoryImpl: PlaceRepositoryImpl): PlaceRepository

    @Binds
    abstract fun bindImageRepository(imageRepositoryImpl: ImageRepositoryImpl): ImageRepository
}