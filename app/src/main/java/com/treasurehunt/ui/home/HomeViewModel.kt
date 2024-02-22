package com.treasurehunt.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import com.treasurehunt.R
import com.treasurehunt.data.LogRepository
import com.treasurehunt.data.PlaceRepository
import com.treasurehunt.data.UserRepository
import com.treasurehunt.data.local.model.PlaceEntity
import com.treasurehunt.data.remote.model.PlaceDTO
import com.treasurehunt.data.remote.model.UserDTO
import com.treasurehunt.ui.model.MapUiState
import com.treasurehunt.util.ConnectivityRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val logRepo: LogRepository,
    private val placeRepo: PlaceRepository,
    private val userRepo: UserRepository,
    private val connectivityRepo: ConnectivityRepository
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
        val uid = Firebase.auth.currentUser?.uid

        _uiState.update {
            it.copy(uid = uid)
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

    suspend fun getRemoteUser(uid: String): UserDTO = userRepo.getRemoteUserById(uid)

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
        return@async placeRepo.getRemotePlaceById(id)
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
                combine(placeRepo.getAllLocalVisits(), placeRepo.getAllLocalPlans()) { visits, plans ->
                    visits + plans
                }
                    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())
                    .collect { visitsAndPlans ->
                        _uiState.update { uiState ->
                            val (visits, plans) = visitsAndPlans.partition { !it.isPlan }
                            uiState.copy(
                                visitMarkers = visits.mapToMarkers(),
                                planMarkers = plans.mapToMarkers()
                            )
                        }
                    }
            } catch (e: IOException) {
            }
        }
    }

    private fun List<PlaceEntity>.mapToMarkers() = map { place ->
        if (!place.isPlan) {
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
}