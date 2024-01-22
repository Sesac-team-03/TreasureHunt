package com.treasurehunt

import android.app.Application
<<<<<<< HEAD
<<<<<<< HEAD
import android.util.Log
=======
>>>>>>> 56cce4e (feat: 사용자 정보를 조회해서 가지고 있는 기록과 장소를 로컬 데이터 베이스에 추가)
import com.treasurehunt.data.local.LogRepository
import com.treasurehunt.data.local.LogRepositoryImpl
import com.treasurehunt.data.local.PlaceRepository
import com.treasurehunt.data.local.PlaceRepositoryImpl
import com.treasurehunt.data.local.TreasureHuntDatabase
import com.treasurehunt.data.remote.FirebaseRepository
import com.treasurehunt.data.remote.FirebaseService
class TreasureHuntApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        firebaseRepository = FirebaseRepository(FirebaseService.create())
        logRepo = LogRepositoryImpl(TreasureHuntDatabase.from(this).logDao())
        placeRepo = PlaceRepositoryImpl(TreasureHuntDatabase.from(this).placeDao())
    }

    companion object {

        lateinit var firebaseRepository: FirebaseRepository
        lateinit var logRepo: LogRepository
        lateinit var placeRepo: PlaceRepository
    }
}