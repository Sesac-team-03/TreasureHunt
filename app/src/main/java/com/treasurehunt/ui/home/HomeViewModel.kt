package com.treasurehunt.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import com.treasurehunt.R
import com.treasurehunt.TreasureHuntApplication.Companion.logRepo
import com.treasurehunt.TreasureHuntApplication.Companion.placeRepo
import com.treasurehunt.data.LogRepository
import com.treasurehunt.data.PlaceRepository
import com.treasurehunt.data.remote.model.MapUiState
import com.treasurehunt.data.local.model.PlaceEntity
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.IOException

class HomeViewModel(private val logRepo: LogRepository, private val placeRepo: PlaceRepository) :
    ViewModel() {

    private val _uiState = MutableStateFlow(MapUiState())
    val uiState: StateFlow<MapUiState> = _uiState
    private var fetchJob: Job? = null

    init {
        storeSampleData()
        getAllMarkers()
    }

    private fun getAllMarkers() {
        fetchJob?.cancel()

        fetchJob = viewModelScope.launch {
            try {
                merge(placeRepo.getAllVisits(), placeRepo.getAllPlans()).collect { placesAndPlans ->
                    _uiState.update {
                        it.copy(markers = placesAndPlans.mapToMarkers())
                    }
                }
            } catch (e: IOException) {
            }
        }
    }

    private fun List<PlaceEntity>.mapToMarkers() = map {
        Marker(LatLng(it.lat, it.lng)).from(it)
    }

    private fun Marker.from(place: PlaceEntity): Marker {
        return if (!place.plan) {
            apply {
                captionText = place.caption
                icon = OverlayImage.fromResource(R.drawable.ic_chest_open)
                width = 116
                height = 80
            }
        } else {
            apply {
                captionText = place.caption
                icon = OverlayImage.fromResource(R.drawable.ic_chest_closed)
                width = 96
                height = 80
            }
        }
    }

    // TEST
    private fun storeSampleData() {
        viewModelScope.launch {
            placeRepo.insert(
                PlaceEntity(
                    37.495401046241, 127.03978300094604, false, "탐앤탐스", "memory"
                )
            )
            placeRepo.insert(
                PlaceEntity(
                    37.49220887221752, 127.0393967628479, true, "스타벅스"
                )
            )
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                HomeViewModel(logRepo, placeRepo)
            }
        }
    }
}
