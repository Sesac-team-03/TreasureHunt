package com.treasurehunt.ui.model

import com.naver.maps.map.overlay.Marker
import com.treasurehunt.data.local.model.PlaceEntity

data class MapUiState(
    val uid: String? = null,
    val visitMarkers: Map<Marker, Boolean> = mapOf(),
    val planMarkers: Map<Marker, Boolean> = mapOf(),
    val isOnline: Boolean = false
)