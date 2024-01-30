package com.treasurehunt.ui.home

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import com.treasurehunt.R
import com.treasurehunt.TreasureHuntApplication.Companion.logRepo
import com.treasurehunt.TreasureHuntApplication.Companion.placeRepo
import com.treasurehunt.TreasureHuntApplication.Companion.userRepo
import com.treasurehunt.data.LogRepository
import com.treasurehunt.data.PlaceRepository
import com.treasurehunt.data.UserRepository
import com.treasurehunt.data.local.model.PlaceEntity
import com.treasurehunt.data.remote.model.MapUiState
import com.treasurehunt.data.remote.model.PlaceDTO
import com.treasurehunt.data.remote.model.UserDTO
import com.treasurehunt.util.ConnectivityRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.IOException

class HomeViewModel(
    private val logRepo: LogRepository,
    private val placeRepo: PlaceRepository,
    private val userRepo: UserRepository,
    private val connectivityRepo: ConnectivityRepository,
    private val savedStateHandle: SavedStateHandle
) :
    ViewModel() {

    private val _uiState = MutableStateFlow(MapUiState())
    val uiState: StateFlow<MapUiState> = _uiState
    private var fetchJob: Job? = null

    init {
        initUser()
        updateNetworkConnectivity()
        getAllMarkers()
    }

    override fun onCleared() {
        connectivityRepo.release()
        super.onCleared()
    }

    private fun initUser() {
        val uid = HomeFragmentArgs.fromSavedStateHandle(savedStateHandle).uid

        viewModelScope.launch {
            _uiState.update {
                it.copy(uid = uid)
            }
        }
    }

    private fun updateNetworkConnectivity() {
        viewModelScope.launch {
            connectivityRepo.isConnected.collect { value ->
                _uiState.update {
                    it.copy(isOnline = value)
                }
            }
        }
    }

    suspend fun getRemoteUser(uid: String): UserDTO = userRepo.getRemoteUser(uid)

    suspend fun updateUser(uid: String, user: UserDTO) {
        userRepo.update(uid, user)
    }

    suspend fun addPlace(place: PlaceEntity): Long = viewModelScope.async {
        return@async placeRepo.insert(place)
    }.await()


    suspend fun addPlace(place: PlaceDTO): String = viewModelScope.async {
        return@async placeRepo.insert(place)
    }.await()

    suspend fun getRemotePlaceById(id: String): PlaceDTO = viewModelScope.async {
        return@async placeRepo.getRemotePlace(id)
    }.await()

    suspend fun updatePlace(place: PlaceEntity) {
        placeRepo.update(place)
    }

    suspend fun updatePlace(id: String, place: PlaceDTO) {
        placeRepo.update(id, place)
    }

    private fun getAllMarkers() {
        fetchJob?.cancel()

        fetchJob = viewModelScope.launch {
            try {
                merge(placeRepo.getAllVisits(), placeRepo.getAllPlans()).collect { visitsAndPlans ->
                    _uiState.update { uiState ->
                        val (places, plans) = visitsAndPlans.partition { !it.plan }
                        uiState.copy(
                            visitMarkers = places.mapToMarkers(),
                            planMarkers = plans.mapToMarkers()
                        )
                    }
                }
            } catch (e: IOException) {
            }
        }
    }

    private fun List<PlaceEntity>.mapToMarkers() = map { place ->
        if (!place.plan) {
            Marker(LatLng(place.lat, place.lng)).from(
                place.remoteId,
                place.caption,
                OverlayImage.fromResource(R.drawable.ic_chest_open),
                116,
                80
            )
        } else {
            Marker(LatLng(place.lat, place.lng)).from(
                place.remoteId,
                place.caption,
                OverlayImage.fromResource(R.drawable.ic_chest_closed),
                96,
                80
            )
        }
    }

    private fun Marker.from(
        tag: String?,
        captionText: String,
        icon: OverlayImage,
        width: Int,
        height: Int
    ): Marker = apply {
        this.tag = tag
        this.captionText = captionText
        this.icon = icon
        this.width = width
        this.height = height
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>,
                extras: CreationExtras
            ): T {
                val application = checkNotNull(extras[APPLICATION_KEY])
                return HomeViewModel(
                    logRepo,
                    placeRepo,
                    userRepo,
                    ConnectivityRepository(application.applicationContext),
                    extras.createSavedStateHandle()
                ) as T
            }
        }
    }
}
