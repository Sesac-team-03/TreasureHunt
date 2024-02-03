package com.treasurehunt

import android.app.Application
import com.treasurehunt.data.ImageRepository
import com.treasurehunt.data.LogRepository
import com.treasurehunt.data.PlaceRepository
import com.treasurehunt.data.UserRepository
import com.treasurehunt.di.AppContainer

class TreasureHuntApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        userRepo = AppContainer.provideUserRepository(this)
        logRepo = AppContainer.provideLogRepository(this)
        placeRepo = AppContainer.providePlaceRepository(this)
    }

    companion object {
        lateinit var userRepo: UserRepository
        lateinit var logRepo: LogRepository
        lateinit var placeRepo: PlaceRepository
        lateinit var imageRepo: ImageRepository
    }

}