package com.treasurehunt.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class ConnectivityRepository @Inject constructor(@ApplicationContext context: Context) {

    private val _isConnected = MutableStateFlow(false)
    val isConnected: StateFlow<Boolean> = _isConnected.asStateFlow()
    private val connectivityManager = context.getSystemService(ConnectivityManager::class.java)
    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            super.onAvailable(network)

            _isConnected.value = true
        }

        override fun onLost(network: Network) {
            super.onLost(network)

            _isConnected.value = false
        }
    }

    init {
        connectivityManager.registerDefaultNetworkCallback(networkCallback)
    }

    fun release() {
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }
}