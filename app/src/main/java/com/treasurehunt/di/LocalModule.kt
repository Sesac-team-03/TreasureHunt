package com.treasurehunt.di

import android.content.Context
import androidx.room.Room
import com.treasurehunt.data.local.TreasureHuntDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LocalModule {

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context): TreasureHuntDatabase {
        return Room.databaseBuilder(
            context,
            TreasureHuntDatabase::class.java,
            "db"
        ).build()
    }

    @Provides
    fun provideUserDao(database: TreasureHuntDatabase) = database.userDao()

    @Provides
    fun provideLogDao(database: TreasureHuntDatabase) = database.logDao()

    @Provides
    fun providePlaceDao(database: TreasureHuntDatabase) = database.placeDao()

    @Provides
    fun provideImageDao(database: TreasureHuntDatabase) = database.imageDao()
}


