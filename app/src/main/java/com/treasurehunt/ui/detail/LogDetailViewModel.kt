package com.treasurehunt.ui.detail

import android.util.Log
import androidx.lifecycle.ViewModel
import com.treasurehunt.data.ImageRepository
import com.treasurehunt.data.LogRepository
import com.treasurehunt.data.PlaceRepository
import com.treasurehunt.data.UserRepository
import com.treasurehunt.data.remote.LogService
import com.treasurehunt.data.remote.PlaceService
import com.treasurehunt.data.remote.UserService
import com.treasurehunt.data.remote.model.LogDTO
import com.treasurehunt.data.remote.model.PlaceDTO
import com.treasurehunt.data.remote.model.toLogEntity
import com.treasurehunt.data.remote.model.toLogModel
import com.treasurehunt.ui.model.LogModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LogDetailViewModel @Inject constructor(
    private val placeRepository: PlaceRepository,
    private val logRepository: LogRepository,
    private val userRepository: UserRepository,
    private val imageRepo: ImageRepository,
    private val placeService: PlaceService,
    private val logService: LogService,
    private val userService: UserService

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

    suspend fun deletePost(placeId: String, userId: String, logId: String) {
        val remoteLog = logRepository.getRemoteLog(logId).toLogEntity(logId)
        logRepository.delete(remoteLog)

        val deleteLogResponse = logService.deleteLog(logId)
        if (!deleteLogResponse.isSuccessful) {
            Log.e(
                "DeletePost",
                "Failed to delete log: ${deleteLogResponse.errorBody()?.string()}"
            )
        }

        // update user
//        val deleteUserResponse = userService.deleteUser(userId)
//        if (!deleteUserResponse.isSuccessful) {
//            Log.e(
//                "DeletePost",
//                "Failed to delete user: ${deleteUserResponse.errorBody()?.string()}"
//            )
//        }

        val deletePlaceResponse = placeService.deletePlace(placeId)
        if (!deletePlaceResponse.isSuccessful) {
            Log.e(
                "DeletePost",
                "Failed to delete place: ${deletePlaceResponse.errorBody()?.string()}"
            )
        }
//        placeRepository.deleteAll()
    }
}