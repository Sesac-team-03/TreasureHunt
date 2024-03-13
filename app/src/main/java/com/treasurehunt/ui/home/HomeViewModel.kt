package com.treasurehunt.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
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
import com.treasurehunt.ui.model.HomeMapUiState
import com.treasurehunt.util.ConnectivityRepository
import com.treasurehunt.util.STORAGE_LOCATION_PROFILE_IMAGE
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.IOException
import javax.inject.Inject

private const val VISIT_MARKER_WIDTH = 116
private const val VISIT_MARKER_HEIGHT = 80
private const val PLAN_MARKER_WIDTH = 96
private const val PLAN_MARKER_HEIGHT = 80

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val logRepo: LogRepository,
    private val placeRepo: PlaceRepository,
    private val userRepo: UserRepository,
    private val connectivityRepo: ConnectivityRepository
) :
    ViewModel() {

    private val _uiState: MutableStateFlow<HomeMapUiState> = MutableStateFlow(HomeMapUiState())
    val uiState: StateFlow<HomeMapUiState> = _uiState.asStateFlow()
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

    suspend fun getUserProfileImageStorageRef(uid: String): StorageReference? =
        Firebase.storage.reference.child(uid).child(STORAGE_LOCATION_PROFILE_IMAGE)
            .list(1)
            .await()
            .items
            .singleOrNull()

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
                combine(
                    placeRepo.getAllLocalVisits(),
                    placeRepo.getAllLocalPlans()
                ) { visits, plans ->
                    visits + plans
                }
                    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())
                    .collect { visitsAndPlans ->
                        val (visits, plans) = visitsAndPlans.partition { !it.isPlan }
                        _uiState.update { uiState ->
                            uiState.copy(
                                visitMarkers = visits.mapToMarkers(),
                                planMarkers = plans.mapToMarkers()
                            )
                        }
                        _uiState.update { uiState ->
                            uiState.copy(
                                allMarkers = uiState.allMarkers
                                        + uiState.visitMarkers.associateWith { true }
                                        + uiState.planMarkers.associateWith { true }
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
                VISIT_MARKER_WIDTH,
                VISIT_MARKER_HEIGHT
            )
        } else {
            Marker(LatLng(place.lat, place.lng)).from(
                place.remoteId,
                place.caption,
                OverlayImage.fromResource(R.drawable.ic_chest_closed),
                PLAN_MARKER_WIDTH,
                PLAN_MARKER_HEIGHT
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