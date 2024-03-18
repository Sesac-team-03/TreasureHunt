package com.treasurehunt.ui.feed

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.treasurehunt.data.ImageRepository
import com.treasurehunt.data.LogRepository
import com.treasurehunt.data.local.model.LogEntity
import com.treasurehunt.data.local.model.toLogModel
import com.treasurehunt.ui.model.FeedUiState
import com.treasurehunt.ui.model.LogModel
import com.treasurehunt.ui.model.asLogEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val INITIAL_LOAD_SIZE = 3
private const val PAGE_SIZE = 3

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val logRepo: LogRepository,
    private val imageRepo: ImageRepository
) : ViewModel() {
    private val _isLogsDataUpdated: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isLogsDataUpdated = _isLogsDataUpdated.asStateFlow()
    private val _isRefreshed = MutableStateFlow(true)
    val isRefreshed = _isRefreshed.asStateFlow()

    private var _pagingLogs: Flow<PagingData<LogModel>> = getLogs()
    val pagingLogs get() = _pagingLogs

    private val _uiState = MutableStateFlow(FeedUiState()) //TODO feedUiState연결
    val uiState: StateFlow<FeedUiState> = _uiState.asStateFlow()

    fun initLogs() {
        _pagingLogs = getLogs()
        _isRefreshed.update { false }
    }

//    fun refreshLogs() {
//        viewModelScope.launch {
//            getLogs().collectLatest { pagingData ->
//                val updatedLogs = pagingData.map { it }.toList()
//                _uiState.value = _uiState.value.copy(logs = updatedLogs.map { it.toLog() })
//            }
//        }
//    }

    //TODO: 기록 수정 시 기존 기록 삭제안되는 버그 수정
//    fun updateLogs(updatedLog: LogModel) {
//        viewModelScope.launch {
//            val oldLog = _uiState.value.logs.find { it.id == updatedLog.id }
//            oldLog?.let { _uiState.value.logs = _uiState.value.logs - it }
//
//            _uiState.value.logs = _uiState.value.logs + updatedLog
//
//            _uiState.value = _uiState.value.copy()
//        }
//    }

    private fun getLogs(): Flow<PagingData<LogModel>> {
        return logRepo.getPagingLogs(PAGE_SIZE, INITIAL_LOAD_SIZE)
            .map { pagingData -> initPagingLogs(pagingData) }
            .cachedIn(viewModelScope)
    }

    private suspend fun initPagingLogs(pagingData: PagingData<LogEntity>)
            : PagingData<LogModel> {
        _isLogsDataUpdated.update { true }
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