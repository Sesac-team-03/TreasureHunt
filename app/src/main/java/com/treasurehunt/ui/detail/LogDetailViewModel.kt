package com.treasurehunt.ui.detail

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
import com.treasurehunt.util.STORAGE_LOCATION_LOG_IMAGES
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.SerializationException
import javax.inject.Inject

private const val STORAGE_LOCATION_USER_IMAGES = "%s/$STORAGE_LOCATION_LOG_IMAGES"
private const val IMAGE_FILE_NAME_PREFIX = "$STORAGE_LOCATION_LOG_IMAGES/"
private const val REMOTE_DATABASE_READ_REQUEST_ITERATION_COUNT = 20

@HiltViewModel
class LogDetailViewModel @Inject constructor(
    private val placeRepo: PlaceRepository,
    private val logRepo: LogRepository,
    private val userRepo: UserRepository,
    private val imageRepo: ImageRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    val args = LogDetailFragmentArgs.fromSavedStateHandle(savedStateHandle)
    private val _logResult: MutableStateFlow<LogResult> = MutableStateFlow(LogResult.LogLoading)
    val logResult: StateFlow<LogResult> = _logResult.asStateFlow()

    init {
        initLog()
    }

    private fun initLog() {
        viewModelScope.launch {
            val placeId = args.remotePlaceId
            val log = args.log
            val logResult = if (placeId != null) {
                getSafeLogByRemotePlaceId(placeId)
            } else if (log != null) {
                getSafeLogByRemoteLogId(log.remoteId)
            } else {
                LogResult.LogNotLoaded
            }
            _logResult.update { logResult }
        }
    }

    private suspend fun getSafeLogByRemotePlaceId(placeId: String): LogResult {
        var field: LogModel? = null

        for (i in 0..<REMOTE_DATABASE_READ_REQUEST_ITERATION_COUNT) {
            try {
                field = getLogByRemotePlaceId(placeId)
                break
            } catch (e: SerializationException) {
                continue
            }
        }
        return if (field == null) LogResult.LogNotLoaded else LogResult.LogLoaded(field)
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


    private suspend fun getSafeLogByRemoteLogId(logId: String?): LogResult {
        if (logId == null) return LogResult.LogNotLoaded

        var field: LogModel? = null

        for (i in 0..<REMOTE_DATABASE_READ_REQUEST_ITERATION_COUNT) {
            try {
                field = getLogByRemoteLogId(logId)
                break
            } catch (e: SerializationException) {
                continue
            }
        }
        return if (field == null) LogResult.LogNotLoaded else LogResult.LogLoaded(field)
    }

    private suspend fun getLogByRemoteLogId(logId: String): LogModel {
        val logDTO: LogDTO = logRepo.getRemoteLogById(logId)
        val imageUrls = logDTO.remoteImageIds.filterValues { it }.keys.map { id ->
            imageRepo.getRemoteImageById(id).url
        }

        return logDTO.toLogModel(imageUrls, logDTO.localId, logId)
    }

    suspend fun getMapSymbol(remotePlaceId: String? = null, log: LogModel? = null): MapSymbol {
        val placeId = remotePlaceId ?: log!!.remotePlaceId
        val placeDTO = placeRepo.getRemotePlaceById(placeId)
        return placeDTO.toMapSymbol()
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
        val storageRef = Firebase.storage.getReference(
            String.format(STORAGE_LOCATION_USER_IMAGES, userId)
        )
        remoteLog.remoteImageIds.filterValues { it }.keys.forEach { id ->
            val imageFileName = imageRepo.getRemoteImageById(id).url
                .substringAfter(IMAGE_FILE_NAME_PREFIX)
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