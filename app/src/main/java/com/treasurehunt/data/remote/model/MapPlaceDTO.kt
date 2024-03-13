package com.treasurehunt.data.remote.model

import com.treasurehunt.ui.model.MapPlaceModel
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
data class MapPlaceSearchResultDTO(
    val lastBuildDate: String = "",
    val total: Long = 0,
    val start: Long = 0,
    val display: Long = 0,
    val items: List<MapPlaceDTO> = emptyList()
)

fun MapPlaceSearchResultDTO.toJsonString() = Json.encodeToString(this)

fun MapPlaceSearchResultDTO.fromJsonString(string: String) =
    Json.decodeFromString<MapPlaceSearchResultDTO>(string)

@Serializable
data class MapPlaceDTO(
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

fun MapPlaceDTO.toMapPlaceModel() = MapPlaceModel(
    title, link, category, description, telephone, address, roadAddress, mapx, mapy
)