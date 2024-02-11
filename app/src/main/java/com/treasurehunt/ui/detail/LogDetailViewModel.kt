package com.treasurehunt.ui.detail

import androidx.lifecycle.ViewModel
import com.treasurehunt.data.ImageRepository
import com.treasurehunt.data.LogRepository
import com.treasurehunt.data.PlaceRepository
import com.treasurehunt.data.remote.model.LogDTO
import com.treasurehunt.data.remote.model.PlaceDTO
import com.treasurehunt.data.remote.model.toLogModel
import com.treasurehunt.ui.model.LogModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LogDetailViewModel @Inject constructor(

    private val placeRepository: PlaceRepository,
    private val logRepository: LogRepository,
    private val imageRepo: ImageRepository
) : ViewModel() {
    suspend fun GetPlace(placeId: String): LogModel? {
        val placeDTO: PlaceDTO = placeRepository.getRemotePlace(placeId)
        val logId = placeDTO.log

        return if (logId != null) {
            val logDTO: LogDTO = logRepository.getRemoteLog(logId)
            logDTO.toLogModel(imageRepo)
        } else {
            null
        }
    }
}