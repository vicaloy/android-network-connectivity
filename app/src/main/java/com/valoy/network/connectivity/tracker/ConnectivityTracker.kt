package com.valoy.network.connectivity.tracker

import kotlinx.coroutines.flow.Flow

interface ConnectivityTracker {

    val networkStatus: Flow<ConnectivityStatus>

    sealed interface ConnectivityStatus {
        data object MaybeConnected : ConnectivityStatus
        data class Connected(val uploadKbps: Int, val downloadKbps: Int) : ConnectivityStatus
        data object ConnectivityNotAvailable : ConnectivityStatus
    }
}