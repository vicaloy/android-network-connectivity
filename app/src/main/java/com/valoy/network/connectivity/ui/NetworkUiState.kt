package com.valoy.network.connectivity.ui

import androidx.compose.runtime.Stable


sealed interface UiState {
    data object Loading : UiState
    data class Success(val status: ConnectionStatus, val speed: ConnectionSpeed?) : UiState
}

@Stable
data class ConnectionSpeed(val upload: String, val download: String)

@Stable
data class ConnectionStatus(val text: Int, val color: Int)