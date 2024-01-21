package com.treasurehunt

import android.app.Application
import com.treasurehunt.data.remote.FirebaseRepository
import com.treasurehunt.data.remote.FirebaseService

class TreasureHuntApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        firebaseRepository = FirebaseRepository(FirebaseService.create())
    }

    companion object {
        lateinit var firebaseRepository: FirebaseRepository
    }
}