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
        val placeDTO: PlaceDTO = placeRepo.getRemotePlaceById(placeId)
        val logId = placeDTO.remoteLogId ?: return null
        val logDTO: LogDTO = logRepo.getRemoteLogById(logId)
        val imageUrls = logDTO.remoteImageIds.filterValues { it }.keys.map { id ->
            imageRepo.getRemoteImageById(id).url
        }

        return logDTO.toLogModel(imageUrls)
    }

    suspend fun getRemotePlace(placeId: String) = placeRepo.getRemotePlaceById(placeId)

    suspend fun deleteLogAndAssociatedData(logId: String, placeId: String, userId: String) {
        deleteImages(logId, userId)
        deleteLog(logId)
        deletePlace(placeId)
        updateUser(logId, placeId, userId)
    }

    private suspend fun deleteImages(logId: String, userId: String) {
        val remoteLog = logRepo.getRemoteLogById(logId)
        val storageRef = Firebase.storage.reference.child("${userId}/log_images")
        remoteLog.remoteImageIds.filterValues { it }.keys.forEach { id ->
            val imageFileName = imageRepo.getRemoteImageById(id).url.substringAfter("log_images/")
            storageRef.child("/$imageFileName").delete()
            imageRepo.delete(id)
        }
    }

    private suspend fun deleteLog(logId: String) {
        val remoteLog = logRepo.getRemoteLogById(logId)
        val localLog = remoteLog.toLogEntity(logId)
        logRepo.delete(localLog)
        logRepo.delete(logId)
    }

    private suspend fun deletePlace(placeId: String) {
        val place = placeRepo.getRemotePlaceById(placeId).toPlaceEntity(placeId)
        placeRepo.delete(place)
        placeRepo.delete(placeId)
    }

    private suspend fun updateUser(logId: String, placeId: String, userId: String) {
        val user = userRepo.getRemoteUserById(userId)
        userRepo.update(
            userId,
            user.copy(
                remoteVisitIds = user.remoteVisitIds + (placeId to false),
                remoteLogIds = user.remoteLogIds + (logId to false)
            )
        )
    }
}