package com.treasurehunt.util

import android.view.View
import com.google.android.material.snackbar.Snackbar
import com.naver.maps.geometry.LatLng
import kotlinx.serialization.json.Json
import java.math.RoundingMode
import java.text.DecimalFormat
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


//******************** String ********************

internal inline fun <reified R> String.convertToDataClass() = json.decodeFromString<R>(this)

private val json = Json {
    isLenient = true
    ignoreUnknownKeys = true
    coerceInputValues = true
}

internal fun String.extractDigits() = replace("[^0-9]".toRegex(), "")

private val coordMultiplier = 10.0.pow(7)

// currently, Naver Local Search API mapx, mapy implement WGS84 (LatLng) coord system as integer
// beware that y, x correspond to lat, lng respectively
fun convertMapXYToLatLng(xy: Pair<String?, String?>): LatLng? {
    val x = xy.first?.toLongOrNull() ?: return null
    val y = xy.second?.toLongOrNull() ?: return null
    return LatLng(y / coordMultiplier, x / coordMultiplier)
}

private const val KILOMETER_BREAKPOINT = 1000.0
private const val UNIT_METER = "m"
private const val UNIT_KILOMETER = "km"

fun formatDistance(meter: Long): String {
    require(meter >= 0)

    if (meter < KILOMETER_BREAKPOINT) return "${meter}$UNIT_METER"

    return "${(meter / KILOMETER_BREAKPOINT).roundOff()}$UNIT_KILOMETER"
}

fun Double.roundOff(): Double = DecimalFormat("#.##").apply {
    roundingMode = RoundingMode.CEILING
}.format(this).toDouble()
