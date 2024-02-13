package com.treasurehunt.ui.feed

import android.adservices.adid.AdId
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.treasurehunt.data.ImageRepository
import com.treasurehunt.data.LogRepository
import com.treasurehunt.data.local.model.toLogModel
import com.treasurehunt.ui.model.FeedUiState
import com.treasurehunt.ui.model.LogModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val logRepo: LogRepository,
    private val imageRepo: ImageRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(FeedUiState())
    val uiState = _uiState.asStateFlow()

    init {
        getAllLogs()
    }

    private fun getAllLogs() {
        viewModelScope.launch {
            logRepo.getAllLogs().collect { allLogs ->
                _uiState.update {
                    FeedUiState(
                        allLogs.map { logEntity ->
                            logEntity.toLogModel(
                                getImageUrls(
                                    logEntity.imageIds
                                )
                            )
                        }, true
                    )
                }
            }
        }
    }

    private suspend fun getImageUrls(imageIds: List<String>): List<String> {
        return imageIds.map { id ->
            imageRepo.getRemoteImage(id).url
        }
    }

}