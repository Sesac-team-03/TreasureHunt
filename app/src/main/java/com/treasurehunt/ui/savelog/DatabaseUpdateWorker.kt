package com.treasurehunt.ui.savelog

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.treasurehunt.data.ImageRepository
import com.treasurehunt.data.LogRepository
import com.treasurehunt.data.PlaceRepository
import com.treasurehunt.data.UserRepository
import com.treasurehunt.data.remote.model.ImageDTO
import com.treasurehunt.data.remote.model.toPlaceEntity
import com.treasurehunt.ui.model.LogModel
import com.treasurehunt.ui.model.MapSymbol
import com.treasurehunt.ui.model.asLogDTO
import com.treasurehunt.ui.model.asLogEntity
import com.treasurehunt.ui.model.asPlaceDTO
import com.treasurehunt.ui.model.asPlaceEntity
import com.treasurehunt.ui.model.toPlace
import com.treasurehunt.util.getCurrentTime
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class DatabaseUpdateWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val logRepo: LogRepository,
    private val placeRepo: PlaceRepository,
    private val userRepo: UserRepository,
    private val imageRepo: ImageRepository
) : CoroutineWorker(context, params) {

    private lateinit var uid: String
    private lateinit var mapSymbol: MapSymbol
    var test = 0

    override suspend fun doWork(): Result {
        return try {
            uid = inputData.getString(WORK_DATA_UID)
                ?: return Result.failure()
            val urlStrings = inputData.getStringArray(WORK_DATA_URL_STRINGS)?.asList()
                ?: return Result.failure()
            val text = inputData.getString(WORK_DATA_LOG_TEXT)
                ?: return Result.failure()
            val lat = inputData.getDouble(WORK_DATA_LAT, 0.0)
            val lng = inputData.getDouble(WORK_DATA_LNG, 0.0)
            val caption = inputData.getString(WORK_DATA_CAPTION) ?: ""
            val isPlan = inputData.getBoolean(WORK_DATA_IS_PLAN, false)
            val planId = inputData.getString(WORK_DATA_PLAN_ID)

            mapSymbol = MapSymbol(lat, lng, isPlan, caption, planId)

            init(urlStrings, text)

            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }

    private suspend fun init(imageUrls: List<String>, text: String) {
        val remotePlaceId = getRemotePlaceId()
        val log = getLogFor(imageUrls, remotePlaceId, text)
        val remoteLogId = insertLog(log)

        updatePlaceWithLog(remotePlaceId, remoteLogId)
        updateUser(remotePlaceId, remoteLogId)
    }

    private suspend fun getRemotePlaceId(): String {
        val planId = mapSymbol.remotePlanId
        return if (planId.isNullOrEmpty()) {
            insertPlace(mapSymbol)
        } else {
            updatePlaceFromPlanToVisit(planId)
            planId
        }
    }

    private suspend fun insertPlace(mapSymbol: MapSymbol): String {
        val place = mapSymbol.toPlace()
        val remotePlaceId = placeRepo.insert(place.asPlaceDTO())
        val localPlaceId = placeRepo.insert(place.asPlaceEntity())

        placeRepo.update(
            place.asPlaceEntity(remotePlaceId, localPlaceId)
        )

        placeRepo.update(
            remotePlaceId,
            place.asPlaceDTO(localPlaceId)
        )

        return remotePlaceId
    }

    private suspend fun updatePlaceFromPlanToVisit(remotePlaceId: String) {
        val placeDTO = placeRepo.getRemotePlaceById(remotePlaceId)

        placeRepo.update(
            placeDTO.toPlaceEntity(remotePlaceId)
                .copy(isPlan = false)
        )
        placeRepo.update(
            remotePlaceId, placeDTO.copy(isPlan = false)
        )
    }

    private suspend fun getLogFor(
        imageUrls: List<String>,
        remotePlaceId: String,
        text: String
    ): LogModel {
        val theme = "123"
        val createdDate = getCurrentTime()
        val imageIds = imageUrls.map { imageUrl ->
            imageRepo.insert(
                ImageDTO(url = imageUrl)
            )
        }

        return LogModel(
            remotePlaceId,
            text,
            theme,
            createdDate,
            imageIds,
        )
    }

    private suspend fun insertLog(log: LogModel): String {
        logRepo.insert(log.asLogEntity())
        return logRepo.insert(log.asLogDTO())
    }

    private suspend fun updatePlaceWithLog(remotePlaceId: String, remoteLogId: String) {
        val updatedPlace =
            placeRepo.getRemotePlaceById(remotePlaceId).copy(remoteLogId = remoteLogId)
        placeRepo.update(remotePlaceId, updatedPlace)
    }

    private suspend fun updateUser(remotePlaceId: String, remoteLogId: String) {
        val userDTO = userRepo.getRemoteUserById(uid)

        if (!mapSymbol.remotePlanId.isNullOrEmpty()) {
            userRepo.update(
                uid, userDTO.copy(
                    remotePlanIds = userDTO.remotePlanIds.minus(remotePlaceId)
                )
            )
        }

        userRepo.update(
            uid, userDTO.copy(
                remoteVisitIds = userDTO.remoteVisitIds.plus(remotePlaceId to true),
                remoteLogIds = userDTO.remoteLogIds.plus(remoteLogId to true)
            )
        )
    }
}