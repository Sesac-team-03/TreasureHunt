package com.treasurehunt.ui.savelog

import android.app.NotificationManager
import android.content.Context
import android.net.Uri
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
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

    private lateinit var mapSymbol: MapSymbol

    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    private lateinit var builder: NotificationCompat.Builder

    // -- worker --
    override suspend fun doWork(): Result {
        val uid = inputData.getString("uid") ?: return Result.failure()
        val urls = inputData.getStringArray("urls")?.asList() ?: return Result.failure()
        val text = inputData.getString("logText") ?: return Result.failure()
        val uris = urls.map { Uri.parse(it) }

        val lat = inputData.getDouble("lat", 0.0)
        val lng = inputData.getDouble("lng", 0.0)
        val caption = inputData.getString("caption") ?: ""
        val isPlan = inputData.getBoolean("isPlan", false)
        val planId = inputData.getString("planID")
        mapSymbol = MapSymbol(lat, lng, caption, isPlan, planId)

        init(urls, text)

        val result = true
        return if (result) {
            Result.success()
        } else {
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

    // -- viewmodel --
    suspend fun insertLog(logEntity: LogEntity) = logRepo.insert(logEntity)

    suspend fun insertLog(logDTO: LogDTO) = logRepo.insert(logDTO)

    suspend fun insertPlace(placeEntity: PlaceEntity): Long {
        return placeRepo.insert(placeEntity)
    }

    suspend fun insertPlace(placeDTO: PlaceDTO): String {
        return placeRepo.insert(placeDTO)
    }

    suspend fun getRemotePlaceById(id: String): PlaceDTO {
        return placeRepo.getRemotePlace(id)
    }

    suspend fun updatePlace(place: PlaceEntity) {
        placeRepo.update(place)
    }

    suspend fun updatePlace(id: String, place: PlaceDTO) {
        placeRepo.update(id, place)
    }

    suspend fun getUserById(uid: String) = userRepo.getRemoteUser(uid)

    suspend fun updateUser(uid: String, user: UserDTO) {
        userRepo.update(uid, user)
    }

    suspend fun insertImage(image: ImageDTO) = imageRepo.insertImage(image)

    // -- fragment --
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
        val uid = Firebase.auth.currentUser!!.uid
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