package com.valoy.network.connectivity.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.valoy.network.connectivity.R
import com.valoy.network.connectivity.tracker.ConnectivityTracker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class NetworkViewModel @Inject constructor(
    connectivityTracker: ConnectivityTracker
) : ViewModel() {

    val uiState = connectivityTracker.networkStatus.map { result ->

        val networkInfo = when (result) {
            is ConnectivityTracker.ConnectivityStatus.Connected -> {
                UiState.Success(
                    status = ConnectionStatus(
                        text = R.string.connected, color = android.graphics.Color.GREEN
                    ), speed = ConnectionSpeed(
                        "${result.uploadKbps / 1000} Mbps", "${result.downloadKbps / 1000} Mbps"
                    )
                )
            }

            is ConnectivityTracker.ConnectivityStatus.MaybeConnected -> {
                UiState.Success(
                    status = ConnectionStatus(
                        text = R.string.maybe_connected, color = android.graphics.Color.YELLOW
                    ), speed = null
                )
            }

            is ConnectivityTracker.ConnectivityStatus.ConnectivityNotAvailable -> {
                UiState.Success(
                    status = ConnectionStatus(
                        text = R.string.connectivity_not_available, color = android.graphics.Color.RED
                    ), speed = null
                )
            }
        }
        networkInfo
    }.catch { exception ->
        Log.d("MainViewModel", exception.toString())
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = UiState.Loading
    )
}
