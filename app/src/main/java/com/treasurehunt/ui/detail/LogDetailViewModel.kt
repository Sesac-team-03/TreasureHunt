package com.treasurehunt.ui.detail

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
import com.treasurehunt.data.remote.model.toMapSymbol
import com.treasurehunt.data.remote.model.toPlaceEntity
import com.treasurehunt.ui.model.LogModel
import com.treasurehunt.ui.model.MapSymbol
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LogDetailViewModel @Inject constructor(
    private val placeRepo: PlaceRepository,
    private val logRepo: LogRepository,
    private val userRepo: UserRepository,
    private val imageRepo: ImageRepository,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    val args = LogDetailFragmentArgs.fromSavedStateHandle(savedStateHandle)
    private val _log = MutableStateFlow<LogModel?>(null)
    val log: StateFlow<LogModel?> = _log

    init {
        initLog()
    }

    private fun initLog() {
        viewModelScope.launch {
            val placeId = LogDetailFragmentArgs.fromSavedStateHandle(savedStateHandle).remotePlaceId
            val log = if (placeId.isNotEmpty()) {
                getLogByRemotePlaceId(placeId)
            } else {
                args.log
            }
            _log.update { log }
        }
    }

    suspend fun getMapSymbol(log: LogModel? = null, remotePlaceId: String = ""): MapSymbol {
        val placeId = remotePlaceId.ifEmpty { log!!.remotePlaceId }
        val placeDTO = placeRepo.getRemotePlaceById(placeId)
        return placeDTO.toMapSymbol()
    }

    private suspend fun getLogByRemotePlaceId(placeId: String): LogModel? {
        val placeDTO: PlaceDTO = placeRepo.getRemotePlaceById(placeId)
        val logId = placeDTO.remoteLogId ?: return null
        val logDTO: LogDTO = logRepo.getRemoteLogById(logId)
        val imageUrls = logDTO.remoteImageIds.filterValues { it }.keys.map { id ->
            imageRepo.getRemoteImageById(id).url
        }

        return logDTO.toLogModel(imageUrls, logDTO.localId, logId)
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