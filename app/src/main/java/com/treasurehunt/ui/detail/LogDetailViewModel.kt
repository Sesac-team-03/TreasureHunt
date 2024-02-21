package com.treasurehunt.ui.detail

import androidx.lifecycle.ViewModel
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
    private val placeRepository: PlaceRepository,
    private val logRepository: LogRepository,
    private val userRepository: UserRepository,
    private val imageRepo: ImageRepository

) : ViewModel() {

    suspend fun getLog(placeId: String): LogModel? {
        val placeDTO: PlaceDTO = placeRepository.getRemotePlace(placeId)
        val logId = placeDTO.log ?: return null
        val logDTO: LogDTO = logRepository.getRemoteLog(logId)

        return logDTO.toLogModel(imageRepo)
    }

    suspend fun getRemotePlace(placeId: String): PlaceDTO {
        return placeRepository.getRemotePlace(placeId)
    }

    suspend fun deletePost(logId: String, placeId: String, userId: String) {
        val log = logRepository.getRemoteLog(logId).toLogEntity(logId)
        logRepository.delete(log)
        logRepository.delete(logId)

        val place = placeRepository.getRemotePlace(placeId).toPlaceEntity(placeId)
        placeRepository.delete(place)
        placeRepository.delete(placeId)

        // TODO: image db, storage delete

        val user = userRepository.getRemoteUser(userId)
        userRepository.update(
            userId,
            user.copy(
                places = user.places + (placeId to false),
                logs = user.logs + (logId to false)
            )
        )
    }
}