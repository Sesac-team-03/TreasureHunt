package com.treasurehunt

import android.app.Application
import com.treasurehunt.data.LogRepository
import com.treasurehunt.data.LogRepositoryImpl
import com.treasurehunt.data.PlaceRepository
import com.treasurehunt.data.PlaceRepositoryImpl
import com.treasurehunt.data.UserRepository
import com.treasurehunt.data.UserRepositoryImpl
import com.treasurehunt.data.local.TreasureHuntDatabase
import com.treasurehunt.data.ImageRepository
import com.treasurehunt.data.ImageRepositoryImpl
import com.treasurehunt.data.remote.ImageService
import com.treasurehunt.data.remote.LogService
import com.treasurehunt.data.remote.PlaceService
import com.treasurehunt.data.remote.UserService
import com.treasurehunt.data.remote.ImageRemoteDataSource
import com.treasurehunt.data.remote.LogRemoteDataSource
import com.treasurehunt.data.remote.PlaceRemoteDataSource
import com.treasurehunt.data.remote.UserRemoteDataSource

class TreasureHuntApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        userRepo = UserRepositoryImpl(
                TreasureHuntDatabase.from(this).userDao(),
                UserRemoteDataSource(UserService.create())
            )
        logRepo = LogRepositoryImpl(
            TreasureHuntDatabase.from(this).logDao(),
            LogRemoteDataSource(LogService.create())
        )
        placeRepo = PlaceRepositoryImpl(
            TreasureHuntDatabase.from(this).placeDao(),
            PlaceRemoteDataSource(PlaceService.create())
        )
        imageRepo = ImageRepositoryImpl(
            TreasureHuntDatabase.from(this).imageDao(),
            ImageRemoteDataSource(ImageService.create())
        )
    }

    companion object {
        lateinit var userRepo: UserRepository
        lateinit var logRepo: LogRepository
        lateinit var placeRepo: PlaceRepository
        lateinit var imageRepo: ImageRepository
    }
}