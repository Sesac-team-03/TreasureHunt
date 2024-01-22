package com.treasurehunt.data.remote.model

import com.naver.maps.map.overlay.Marker
import com.treasurehunt.data.remote.model.PlaceEntity

data class MapUiState(
    val isSignedInAsMember: Boolean = false,
    val places: List<PlaceEntity> = listOf(),
    val plans: List<PlaceEntity> = listOf(),
    val markers: List<Marker> = listOf()
)