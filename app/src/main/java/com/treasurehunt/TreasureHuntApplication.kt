package com.treasurehunt

import android.app.Application
import android.util.Log
import com.treasurehunt.data.local.LogRepository
import com.treasurehunt.data.local.LogRepositoryImpl
import com.treasurehunt.data.local.PlaceRepository
import com.treasurehunt.data.local.PlaceRepositoryImpl
import com.treasurehunt.data.local.TreasureHuntDatabase

class TreasureHuntApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        logRepo = LogRepositoryImpl(TreasureHuntDatabase.from(this).logDao())
        placeRepo = PlaceRepositoryImpl(TreasureHuntDatabase.from(this).placeDao())
    }

    companion object {
        lateinit var logRepo: LogRepository
        lateinit var placeRepo: PlaceRepository
    }
}