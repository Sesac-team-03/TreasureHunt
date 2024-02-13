package com.treasurehunt.ui.savelog

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.treasurehunt.data.ImageRepository
import com.treasurehunt.data.LogRepository
import com.treasurehunt.data.PlaceRepository
import com.treasurehunt.data.UserRepository
import com.treasurehunt.data.local.model.LogEntity
import com.treasurehunt.data.local.model.PlaceEntity
import com.treasurehunt.data.remote.model.ImageDTO
import com.treasurehunt.data.remote.model.LogDTO
import com.treasurehunt.data.remote.model.PlaceDTO
import com.treasurehunt.data.remote.model.UserDTO
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

    override suspend fun doWork(): Result {
        return try {
            uid = inputData.getString(WORK_DATA_UID) ?: return Result.failure()
            val urls = inputData.getStringArray(WORK_DATA_URLS)?.asList() ?: return Result.failure()
            val text = inputData.getString(WORK_DATA_LOG_TEXT) ?: return Result.failure()
            val lat = inputData.getDouble(WORK_DATA_LAT, 0.0)
            val lng = inputData.getDouble(WORK_DATA_LNG, 0.0)
            val caption = inputData.getString(WORK_DATA_CAPTION) ?: ""
            val isPlan = inputData.getBoolean(WORK_DATA_IS_PLAN, false)
            val planId = inputData.getString(WORK_DATA_PLAN_ID)

            mapSymbol = MapSymbol(lat, lng, caption, isPlan, planId)

            init(urls, text)

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

    // -- 기존 Viewmodel --
    private suspend fun insertLog(logEntity: LogEntity) = logRepo.insert(logEntity)

    private suspend fun insertLog(logDTO: LogDTO) = logRepo.insert(logDTO)

    private suspend fun insertPlace(placeEntity: PlaceEntity) = placeRepo.insert(placeEntity)

    private suspend fun insertPlace(placeDTO: PlaceDTO) = placeRepo.insert(placeDTO)

    private suspend fun getRemotePlaceById(id: String) = placeRepo.getRemotePlace(id)

    private suspend fun updatePlace(place: PlaceEntity) = placeRepo.update(place)

    private suspend fun updatePlace(id: String, place: PlaceDTO) {
        placeRepo.update(id, place)
    }

    private suspend fun getUserById(uid: String) = userRepo.getRemoteUser(uid)

    private suspend fun updateUser(uid: String, user: UserDTO) {
        userRepo.update(uid, user)
    }

    private suspend fun insertImage(image: ImageDTO) = imageRepo.insertImage(image)

    // -- 기존 Fragment --
    private suspend fun getRemotePlaceId(): String {
        val planId = mapSymbol.remoteId
        return if (planId.isNullOrEmpty()) {
            insertPlace(mapSymbol)
        } else {
            updatePlaceFromPlanToVisit(planId)
            planId
        }
    }

    private suspend fun insertPlace(mapSymbol: MapSymbol): String {
        val place = mapSymbol.toPlace()
        val remotePlaceId = insertPlace(place.asPlaceDTO())
        val localPlaceId = insertPlace(place.asPlaceEntity())

        updatePlace(
            place.asPlaceEntity(remotePlaceId, localPlaceId)
        )

        updatePlace(
            remotePlaceId,
            place.asPlaceDTO(localPlaceId)
        )

        return remotePlaceId
    }

    private suspend fun updatePlaceFromPlanToVisit(remotePlaceId: String) {
        val placeDTO = getRemotePlaceById(remotePlaceId)

        updatePlace(
            placeDTO.toPlaceEntity(remotePlaceId)
                .copy(plan = false)
        )
        updatePlace(
            remotePlaceId, placeDTO.copy(plan = false)
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
            insertImage(
                ImageDTO(url = imageUrl)
            )
        }

        return LogModel(
            remotePlaceId,
            imageIds,
            text,
            theme,
            createdDate
        )
    }

    private suspend fun insertLog(log: LogModel): String {
        insertLog(log.asLogEntity())
        return insertLog(log.asLogDTO())
    }

    private suspend fun updatePlaceWithLog(remotePlaceId: String, remoteLogId: String) {
        val updatedPlace = getRemotePlaceById(remotePlaceId).copy(log = remoteLogId)
        updatePlace(remotePlaceId, updatedPlace)
    }

    private suspend fun updateUser(remotePlaceId: String, remoteLogId: String) {

        val userDTO = getUserById(uid)

        if (!mapSymbol.remoteId.isNullOrEmpty()) {
            updateUser(
                uid, userDTO.copy(
                    plans = userDTO.plans.minus(remotePlaceId)
                )
            )
        }

        updateUser(
            uid, userDTO.copy(
                places = userDTO.places.plus(remotePlaceId to true),
                logs = userDTO.logs.plus(remoteLogId to true)
            )
        )
    }
}