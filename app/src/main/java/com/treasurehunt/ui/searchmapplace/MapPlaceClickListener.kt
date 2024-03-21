package com.treasurehunt.ui.searchmapplace

import com.treasurehunt.ui.model.MapPlaceModel

fun interface MapPlaceClickListener {

    fun onClick(mapPlaceModel: MapPlaceModel)
}