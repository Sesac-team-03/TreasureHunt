package com.treasurehunt

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.treasurehunt.data.ImageRepository
import com.treasurehunt.data.LogRepository
import com.treasurehunt.data.PlaceRepository
import com.treasurehunt.data.UserRepository
import com.treasurehunt.ui.savelog.DatabaseUpdateWorker
import com.treasurehunt.ui.savelog.ImageUploadWorker
import javax.inject.Inject

class CustomWorkerFactory @Inject constructor(
    private val logRepo: LogRepository,
    private val placeRepo: PlaceRepository,
    private val userRepo: UserRepository,
    private val imageRepo: ImageRepository
) : WorkerFactory() {

    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {

        return when (workerClassName) {
            DatabaseUpdateWorker::class.java.name ->
                DatabaseUpdateWorker(
                    appContext,
                    workerParameters,
                    logRepo,
                    placeRepo,
                    userRepo,
                    imageRepo
                )

            ImageUploadWorker::class.java.name ->
                ImageUploadWorker(appContext,
                    workerParameters,
                    logRepo,
                    imageRepo)

            else -> null
        }
    }
}