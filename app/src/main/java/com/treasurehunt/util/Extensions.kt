package com.treasurehunt.util

import android.view.Gravity
import android.view.View
import android.view.View.TEXT_ALIGNMENT_CENTER
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Firebase
import com.google.firebase.storage.storage
import com.treasurehunt.R
import com.treasurehunt.ui.model.BaseUiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
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

/* ----------- ViewModel ----------- */

fun <T : BaseUiState> ViewModel.updateUid(
    authStateRepo: AuthStateRepository,
    uiState: MutableStateFlow<T>,
    block: T.(uid: String?) -> T
) {
    viewModelScope.launch {
        authStateRepo.getUid()
            .stateIn(viewModelScope)
            .collect { uid ->
                uiState.update { uiState ->
                    uiState.block(uid)
                }
            }
    }
}

fun <T : BaseUiState> ViewModel.updateNetworkConnectivity(
    connectivityRepo: ConnectivityRepository,
    uiState: MutableStateFlow<T>,
    block: T.(value: Boolean) -> T
) {
    viewModelScope.launch {
        connectivityRepo.isConnected
            .collect { value ->
                uiState.update { uiState ->
                    uiState.block(value)
                }
            }
    }
}

/* ----------- StateFlow ----------- */

/**
 * this function allows UiState collector to avoid collecting default UiState,
 * with default values such as uid = null and isOnline = false
 * */
@OptIn(kotlinx.coroutines.FlowPreview::class)
fun <T : BaseUiState> StateFlow<T>.filterSuccessiveStateChange(): Flow<T> =
    debounce(1500)

fun <T : BaseUiState> Flow<T>.filterUidUnrelatedStateChange(): Flow<T> =
    distinctUntilChangedBy {
        it.uid
    }

fun <T : BaseUiState> Flow<T>.filterConnectivityUnrelatedStateChange(): Flow<T> =
    distinctUntilChangedBy {
        it.isOnline
    }

/* ----------- Fragment ----------- */

fun Fragment.preloadProfileImage(storageUrl: String?) {
    if (storageUrl == null) return

    Glide.with(requireContext())
        .load(Firebase.storage.getReferenceFromUrl(storageUrl))
        .preload()
}

fun <T : BaseUiState> Fragment.directToLoginScreenOnNullUid(uiState: StateFlow<T>) {
    viewLifecycleOwner.lifecycleScope.launch {
        uiState.filterSuccessiveStateChange()
            .filterUidUnrelatedStateChange()
            .collect { uiState ->
                if (uiState.uid == null) {
                    findNavController().navigate(R.id.action_global_to_loginFragment)
                }
            }
    }
}

fun <T : BaseUiState> Fragment.showDisconnectedWarningMessage(
    uiState: StateFlow<T>,
    rootView: View
) {
    viewLifecycleOwner.lifecycleScope.launch {
        uiState.filterSuccessiveStateChange()
            .filterConnectivityUnrelatedStateChange()
            .collect { uiState ->
                if (!uiState.isOnline) {
                    rootView.showSnackbar(R.string.disconnected_warning_message)
                }
            }
    }
}

fun <T : BaseUiState> Fragment.restrictOnLostConnectivity(
    uiState: StateFlow<T>,
    block: () -> Unit
) {
    viewLifecycleOwner.lifecycleScope.launch {
        uiState.filterSuccessiveStateChange()
            .filterConnectivityUnrelatedStateChange()
            .collect { uiState ->
                if (!uiState.isOnline) {
                    block()
                }
            }
    }
}