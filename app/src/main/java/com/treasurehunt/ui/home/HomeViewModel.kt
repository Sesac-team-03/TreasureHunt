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
import com.treasurehunt.data.model.PlaceEntity
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.IOException

data class MapUiState(
    val isSignedIn: Boolean = false,
    val places: List<PlaceEntity> = listOf(),
    val plans: List<PlaceEntity> = listOf(),
    val markers: List<Marker> = listOf()
)

class HomeViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(MapUiState())
    val uiState: StateFlow<MapUiState> = _uiState.asStateFlow()
    private var fetchJob: Job? = null

    init {
        fetchMarkers()
    }

    private fun fetchMarkers() {
        fetchJob?.cancel()
        fetchJob = viewModelScope.launch {
            try {
                val places = getPlaces()
                val plans = getPlans()
                val placeMarkers = places.map {
                    Marker().apply {
                        position = LatLng(it.lat, it.lng)
                        icon = OverlayImage.fromResource(R.drawable.ic_chest_open)
                        // captionText = "톰톰"
                        width = 116
                        height = 80
                    }
                }
                val planMarkers = plans.map {
                    Marker().apply {
                        position = LatLng(it.lat, it.lng)
                        icon = OverlayImage.fromResource(R.drawable.ic_chest_closed)
                        // captionText = "스벅"
                        width = 96
                        height = 80
                    }
                }
                val markers = placeMarkers + planMarkers

                _uiState.update {
                    it.copy(
                        places = getPlaces(),
                        plans = getPlans(),
                        markers = markers

                    )
                }
            } catch (ioe: IOException) {
                // Handle the error and notify the UI when appropriate.
            }
        }
    }

    private fun getPlaces() = listOf(
        PlaceEntity(
            37.495401046241, 127.03978300094604, false, "memory"
        )
    )

    private fun getPlans() = listOf(
        PlaceEntity(
            37.49220887221752, 127.0393967628479, true
        )
    )

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                HomeViewModel()
            }
        }
    }
}
