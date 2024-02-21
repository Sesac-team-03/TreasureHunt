package com.treasurehunt.ui.detail

import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.storage.storage
import com.treasurehunt.data.ImageRepository
import com.treasurehunt.data.LogRepository
import com.treasurehunt.data.PlaceRepository
import com.treasurehunt.data.UserRepository
import com.treasurehunt.data.remote.model.LogDTO
import com.treasurehunt.data.remote.model.PlaceDTO
import com.treasurehunt.data.remote.model.toLogEntity
import com.treasurehunt.data.remote.model.toLogModel
import com.treasurehunt.data.remote.model.toPlaceEntity
import com.treasurehunt.ui.model.LogModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LogDetailViewModel @Inject constructor(
    private val placeRepo: PlaceRepository,
    private val logRepo: LogRepository,
    private val userRepo: UserRepository,
    private val imageRepo: ImageRepository

) : ViewModel() {

    suspend fun getLogByRemotePlaceId(placeId: String): LogModel? {
        val placeDTO: PlaceDTO = placeRepo.getRemotePlace(placeId)
        val logId = placeDTO.log ?: return null
        val logDTO: LogDTO = logRepo.getRemoteLog(logId)

        return logDTO.toLogModel(imageRepo)
    }

    suspend fun getRemotePlace(placeId: String) = placeRepo.getRemotePlace(placeId)

    suspend fun deleteLogAndAssociatedData(logId: String, placeId: String, userId: String) {
        deleteImages(logId, userId)
        deleteLog(logId)
        deletePlace(placeId)
        updateUser(logId, placeId, userId)
    }

    private suspend fun deleteImages(logId: String, userId: String) {
        val remoteLog = logRepo.getRemoteLog(logId)
        val storageRef = Firebase.storage.reference.child("${userId}/log_images")
        remoteLog.images.filterValues { true }.keys.forEach { imageId ->
            val imageFileName = imageRepo.getRemoteImage(imageId).url.substringAfter("log_images/")
            storageRef.child("/$imageFileName").delete()
            imageRepo.delete(imageId)
        }
    }

    private suspend fun deleteLog(logId: String) {
        val remoteLog = logRepo.getRemoteLog(logId)
        val localLog = remoteLog.toLogEntity(logId)
        logRepo.delete(localLog)
        logRepo.delete(logId)
    }

    private suspend fun deletePlace(placeId: String) {
        val place = placeRepo.getRemotePlace(placeId).toPlaceEntity(placeId)
        placeRepo.delete(place)
        placeRepo.delete(placeId)
    }

    private suspend fun updateUser(logId: String, placeId: String, userId: String) {
        val user = userRepo.getRemoteUser(userId)
        userRepo.update(
            userId,
            user.copy(
                places = user.places + (placeId to false),
                logs = user.logs + (logId to false)
            )
        )
    }
}