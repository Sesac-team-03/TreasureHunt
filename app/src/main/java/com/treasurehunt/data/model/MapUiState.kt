package com.treasurehunt.data.model

import com.naver.maps.map.overlay.Marker

data class MapUiState(
    val isSignedInAsMember: Boolean = false,
    val places: List<PlaceEntity> = listOf(),
    val plans: List<PlaceEntity> = listOf(),
    val markers: List<Marker> = listOf()
)