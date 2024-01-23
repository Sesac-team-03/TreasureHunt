package com.treasurehunt

import android.app.Application
import com.treasurehunt.data.LogRepository
import com.treasurehunt.data.LogRepositoryImpl
import com.treasurehunt.data.PlaceRepository
import com.treasurehunt.data.PlaceRepositoryImpl
import com.treasurehunt.data.UserRepository
import com.treasurehunt.data.UserRepositoryImpl
import com.treasurehunt.data.local.TreasureHuntDatabase
import com.treasurehunt.data.remote.LogService
import com.treasurehunt.data.remote.PlaceService
import com.treasurehunt.data.remote.UserService
import com.treasurehunt.data.remote.model.LogDTODataSource
import com.treasurehunt.data.remote.model.PlaceDTODataSource
import com.treasurehunt.data.remote.model.UserDTODataSource

class TreasureHuntApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        userRepo =
            UserRepositoryImpl(
                TreasureHuntDatabase.from(this).userDao(),
                UserDTODataSource(UserService.create())
            )
        logRepo = LogRepositoryImpl(
            TreasureHuntDatabase.from(this).logDao(),
            LogDTODataSource(LogService.create())
        )
        placeRepo = PlaceRepositoryImpl(
            TreasureHuntDatabase.from(this).placeDao(),
            PlaceDTODataSource(PlaceService.create())
        )
    }

    companion object {
        lateinit var userRepo: UserRepository
        lateinit var logRepo: LogRepository
        lateinit var placeRepo: PlaceRepository
    }
}