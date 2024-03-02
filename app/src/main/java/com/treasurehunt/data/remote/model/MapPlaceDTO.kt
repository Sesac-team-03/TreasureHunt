package com.treasurehunt.data.remote.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
data class MapPlaceSearchResultDTO (
    val lastBuildDate: String,
    val total: Long,
    val start: Long,
    val display: Long,
    val items: List<MapPlace>
)

fun MapPlaceSearchResultDTO.toJsonString() = Json.encodeToString(this)

fun MapPlaceSearchResultDTO.fromJsonString(string: String) = Json.decodeFromString<MapPlaceSearchResultDTO>(string)

@Serializable
data class MapPlace (
    val title: String,
    val link: String,
    val category: String,
    val description: String,
    val telephone: String,
    val address: String,
    val roadAddress: String,
    val mapx: String,
    val mapy: String
)