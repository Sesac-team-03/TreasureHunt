package com.treasurehunt.ui.model

import com.naver.maps.map.overlay.Marker

data class HomeUiState(
    override val uid: String? = null,
    override val isOnline: Boolean = false,
    val visitMarkers: List<Marker> = listOf(),
    val planMarkers: List<Marker> = listOf(),
    val allMarkers: Map<Marker, Boolean> = mapOf()
) : BaseUiState(uid, isOnline)