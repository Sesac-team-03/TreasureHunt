package com.treasurehunt.ui.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.treasurehunt.data.ImageRepository
import com.treasurehunt.data.LogRepository
import com.treasurehunt.data.local.model.LogEntity
import com.treasurehunt.data.local.model.toLogModel
import com.treasurehunt.ui.model.LogModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import javax.inject.Inject

private const val PAGE_SIZE = 9
private const val INITIAL_PAGE_SIZE = 16

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val logRepo: LogRepository,
    private val imageRepo: ImageRepository
) : ViewModel() {


    private val _isLogsUpdated: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isLogsUpdated = _isLogsUpdated.asStateFlow()

    val pagingLogs: Flow<PagingData<LogModel>>

    init {
        pagingLogs = getLogs()
    }

    private fun getLogs(): Flow<PagingData<LogModel>> {
        return Pager(
            config = PagingConfig(
                PAGE_SIZE,
                initialLoadSize = INITIAL_PAGE_SIZE,
                enablePlaceholders = false,
            )
        ) { logRepo.getPagingLogs() }
            .flow
            .map { pagingData -> initPagingData(pagingData) }
            .cachedIn(viewModelScope)
    }

    private suspend fun initPagingData(pagingData: PagingData<LogEntity>)
            : PagingData<LogModel> {
        _isLogsUpdated.update { true }
        return pagingData.map { logEntity ->
            logEntity.toLogModel(
                getImageUrls(
                    logEntity.remoteImageIds
                )
            )
        }
    }

    private suspend fun getImageUrls(imageIds: List<String>): List<String> {
        return imageIds.map { id ->
            imageRepo.getRemoteImageById(id).url
        }
    }
}