package com.treasurehunt.util

import android.view.View
import com.google.android.material.snackbar.Snackbar
import kotlinx.serialization.json.Json
import java.math.RoundingMode
import java.text.DecimalFormat

/* ----------- View ----------- */

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

/* ----------- String ----------- */

internal fun String.extractDigits() = replace("[^0-9]".toRegex(), "")

private val json = Json {
    isLenient = true
    ignoreUnknownKeys = true
    coerceInputValues = true
}

internal inline fun <reified R> String.convertToDataClass() = json.decodeFromString<R>(this)

/* ----------- Double ----------- */

private const val DECIMAL_PLACEHOLDER = "#"

fun Double.roundOff(decimalPlaceCount: Int = 2): Double {
    require(decimalPlaceCount > 0)

    // Default Format: #.##
    return DecimalFormat("$DECIMAL_PLACEHOLDER.${DECIMAL_PLACEHOLDER.repeat(decimalPlaceCount)}").apply {
        roundingMode = RoundingMode.CEILING
    }.format(this).toDouble()
}
