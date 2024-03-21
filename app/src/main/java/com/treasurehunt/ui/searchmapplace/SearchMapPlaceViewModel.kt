package com.treasurehunt.ui.searchmapplace

import androidx.lifecycle.ViewModel
import com.treasurehunt.data.LogRepository
import com.treasurehunt.data.MapPlaceSearchRepository
import com.treasurehunt.data.PlaceRepository
import com.treasurehunt.data.UserRepository
import com.treasurehunt.data.remote.model.toMapPlaceModel
import com.treasurehunt.ui.model.MapPlaceModel
import com.treasurehunt.ui.model.MapPlaceSearchUiState
import com.treasurehunt.util.ConnectivityRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class SearchMapPlaceViewModel @Inject constructor(
    private val logRepo: LogRepository,
    private val placeRepo: PlaceRepository,
    private val userRepo: UserRepository,
    private val mapPlaceSearchRepo: MapPlaceSearchRepository,
    private val connectivityRepo: ConnectivityRepository
) :
    ViewModel() {

    private val _uiState = MutableStateFlow(MapPlaceSearchUiState(listOf()))
    val uiState: StateFlow<MapPlaceSearchUiState> = _uiState

    suspend fun search(keyword: String): List<MapPlaceModel> {
        return mapPlaceSearchRepo.getMapPlaceByKeyword(keyword)
            .items
            .map { it.toMapPlaceModel() }
    }
}