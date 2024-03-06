package com.treasurehunt.util

import android.view.View
import com.google.android.material.snackbar.Snackbar
import kotlinx.serialization.json.Json

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