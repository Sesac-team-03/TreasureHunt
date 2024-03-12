package com.treasurehunt.util

import android.util.Log
import android.view.View
import com.google.android.material.snackbar.Snackbar
import com.naver.maps.geometry.LatLng
import kotlinx.serialization.json.Json
import kotlin.math.pow

//******************** View ********************

fun View.showSnackbar(resId: Int) {
    Snackbar.make(
        this,
        resId,
        Snackbar.LENGTH_SHORT
    ).show()
}

fun View.showSnackbar(string: String) {
    Snackbar.make(
        this,
        string,
        Snackbar.LENGTH_SHORT
    ).show()
}

fun View.show() {
    visibility = View.VISIBLE
}

fun View.hide() {
    visibility = View.GONE
}

<<<<<<< HEAD

//******************** String ********************

internal inline fun <reified R> String.convertToDataClass() = json.decodeFromString<R>(this)

private val json = Json {
    isLenient = true
    ignoreUnknownKeys = true
    coerceInputValues = true
}

internal fun String.extractDigits() = replace("[^0-9]".toRegex(), "")

private val coordMultiplier = 10.0.pow(7)

// currently, mapx, mapy implements WGS84 (LatLng) coord system as integer
fun convertNaverLocalSearchMapXYToLatLng(mapx: String?, mapy: String?): LatLng? {
    Log.d("$$", "$mapy || ${mapy?.toLongOrNull()}")
    val x = mapx?.toLongOrNull() ?: return null
    val y = mapy?.toLongOrNull() ?: return null
    Log.d("$$ latlng", "${LatLng(y / coordMultiplier, x / coordMultiplier)}")
    return LatLng(y / coordMultiplier, x / coordMultiplier)
}
