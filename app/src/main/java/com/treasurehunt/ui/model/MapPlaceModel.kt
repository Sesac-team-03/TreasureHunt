package com.treasurehunt.ui.model

data class MapPlaceModel(
    val title: String,
    val link: String? = null,
    val category: String? = null,
    val description: String? = null,
    val telephone: String? = null,
    val address: String? = null,
    val roadAddress: String? = null,
    val mapx: String? = null,
    val mapy: String? = null
)