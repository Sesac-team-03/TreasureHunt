package com.treasurehunt.ui.searchmapplace

import androidx.lifecycle.ViewModel
import com.treasurehunt.data.MapPlaceSearchRepository
import com.treasurehunt.data.remote.model.toMapPlaceModel
import com.treasurehunt.ui.model.MapPlaceModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SearchMapPlaceViewModel @Inject constructor(
    private val mapPlaceSearchRepo: MapPlaceSearchRepository
) : ViewModel() {

    suspend fun search(keyword: String): List<MapPlaceModel> {
        return mapPlaceSearchRepo.getMapPlaceByKeyword(keyword)
            .items
            .map { it.toMapPlaceModel() }
    }
}