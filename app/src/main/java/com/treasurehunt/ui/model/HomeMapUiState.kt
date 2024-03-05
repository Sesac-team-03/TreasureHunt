package com.treasurehunt.ui.model

import com.naver.maps.map.overlay.Marker

data class HomeMapUiState(
    val uid: String? = null,
    val visitMarkers: List<Marker> = listOf(),
    val planMarkers: List<Marker> = listOf(),
    val allMarkers: Map<Marker, Boolean> = mapOf(),
    val isOnline: Boolean = false
)