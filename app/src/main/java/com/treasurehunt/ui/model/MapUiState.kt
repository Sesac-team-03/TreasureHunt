package com.treasurehunt.ui.model

import com.naver.maps.map.overlay.Marker
import com.treasurehunt.data.local.model.PlaceEntity

data class MapUiState(
    val uid: String? = null,
    val visits: List<PlaceEntity> = listOf(),
    val plans: List<PlaceEntity> = listOf(),
    val visitMarkers: List<Marker> = listOf(),
    val planMarkers: List<Marker> = listOf(),
    val isOnline: Boolean = false
)