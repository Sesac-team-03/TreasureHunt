package com.treasurehunt.ui.feed

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.treasurehunt.data.ImageRepository
import com.treasurehunt.data.LogRepository
import com.treasurehunt.data.local.model.toLogModel
import com.treasurehunt.data.remote.model.toImageModel
import com.treasurehunt.ui.model.LogImageModel
import com.treasurehunt.ui.model.LogModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


data class FeedUiState(
    val logs: List<LogModel> = emptyList(),
)

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val logRepo: LogRepository,
    private val imageRepo: ImageRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(FeedUiState())
    val uiState get() = _uiState.asStateFlow()

    init {
        getAllLogs()
    }

    private fun getAllLogs() {
        viewModelScope.launch {
            logRepo.getAllLogs().catch {
            }.collect { allLogs ->
                _uiState.update {
                    FeedUiState(allLogs.map { it.toLogModel(imageRepo) })
                }
            }
        }
    }

}