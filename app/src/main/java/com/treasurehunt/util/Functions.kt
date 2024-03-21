package com.treasurehunt.util

import android.os.Build
import com.google.firebase.Firebase
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.naver.maps.geometry.LatLng
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Date
import kotlin.math.pow

/* ----------- 시간 ----------- */

fun getCurrentTime(): Long = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
    LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
} else {
    Date().time
}

internal fun getUidCallbackFlow(): Flow<String?> = callbackFlow {
    val callback = FirebaseAuth.AuthStateListener { auth ->
        trySendBlocking(auth.currentUser?.uid)
    }

    try {
        Firebase.auth.addAuthStateListener(callback)
    } catch (e: FirebaseException) {
        trySendBlocking(null)
        channel.close()
    }

    awaitClose {
        Firebase.auth.removeAuthStateListener(callback)
    }
}

/* ----------- 좌표 ----------- */

private val coordMultiplier = 10.0.pow(7)

/**
 * currently, Naver Local Search API mapx, mapy implement WGS84 (LatLng) coord system as integer
 * beware that y, x correspond to lat, lng respectively
 * */
fun convertMapXYToLatLng(xy: Pair<String?, String?>): LatLng? {
    val x = xy.first?.toLongOrNull() ?: return null
    val y = xy.second?.toLongOrNull() ?: return null
    return LatLng(y / coordMultiplier, x / coordMultiplier)
}

/* ----------- 거리 ----------- */

fun getDistance(xy: Pair<String?, String?>, other: LatLng) =
    convertMapXYToLatLng(xy)
        ?.distanceTo(other)
        ?.toLong()
        ?.let(::formatDistance)

private const val KILOMETER_BREAKPOINT = 1000.0
private const val UNIT_METER = "m"
private const val UNIT_KILOMETER = "km"

fun formatDistance(meter: Long): String {
    require(meter >= 0)

    if (meter < KILOMETER_BREAKPOINT) return "${meter}$UNIT_METER"

    return "${(meter / KILOMETER_BREAKPOINT).roundOff()}$UNIT_KILOMETER"
}