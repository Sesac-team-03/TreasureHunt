package com.treasurehunt.ui.feed

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.treasurehunt.data.ImageRepository
import com.treasurehunt.data.LogRepository
import com.treasurehunt.data.PlaceRepository
import com.treasurehunt.data.UserRepository
import com.treasurehunt.data.local.model.LogEntity
import com.treasurehunt.data.local.model.toLogModel
import com.treasurehunt.data.remote.model.UserDTO
import com.treasurehunt.data.remote.model.toLogEntity
import com.treasurehunt.data.remote.model.toPlaceEntity
import com.treasurehunt.ui.model.LogModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.SerializationException
import javax.inject.Inject

private const val INITIAL_LOAD_SIZE = 3
private const val PAGE_SIZE = 3

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val logRepo: LogRepository,
    private val imageRepo: ImageRepository
) : ViewModel() {

    val pagingLogs = getLogs()

    private fun getLogs(): Flow<PagingData<LogModel>> {
        return logRepo.getPagingLogs(PAGE_SIZE, INITIAL_LOAD_SIZE)
            .map { pagingData -> initPagingLogs(pagingData) }
            .cachedIn(viewModelScope)
    }

    private fun initPagingLogs(pagingData: PagingData<LogEntity>)
            : PagingData<LogModel> {
        return pagingData.map { logEntity ->
            logEntity.toLogModel(
                getImageUrls(
                    logEntity.remoteImageIds
                )
            )
        }
    }

    private suspend fun getImageUrls(imageIds: List<String>): List<String> {
        return try {
            imageIds.map { id ->
                imageRepo.getRemoteImageById(id).url
            }
        } catch (e: SerializationException) {
            listOf("")
        }
    }
}