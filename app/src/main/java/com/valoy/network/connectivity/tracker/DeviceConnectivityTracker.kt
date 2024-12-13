package com.valoy.network.connectivity.tracker

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.os.Build
import android.os.PowerManager
import androidx.core.content.getSystemService
import com.valoy.network.connectivity.coroutines.throttleLatest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.shareIn
import com.valoy.network.connectivity.tracker.ConnectivityTracker.ConnectivityStatus
import javax.inject.Inject

class DeviceConnectivityTracker @Inject constructor(
    private val context: Context,
    scope: CoroutineScope,
) : ConnectivityTracker {

    @OptIn(ExperimentalCoroutinesApi::class)
    override val networkStatus: Flow<ConnectivityStatus> = idleState()
        .throttleLatest(THROTTLE_DELAY)
        .flatMapLatest { idle ->
            if (idle) {
                flowOf(ConnectivityStatus.ConnectivityNotAvailable)
            } else {
                connectivityStatus()
            }
        }
        .shareIn(
            scope = scope,
            started = SharingStarted.WhileSubscribed(
                stopTimeoutMillis = 0,
                replayExpirationMillis = 0,
            ),
            replay = 1,
        )

    private fun isIdle(): Boolean = requireNotNull(context.getSystemService<PowerManager>()).run {
        isDeviceIdleMode && !isIgnoringBatteryOptimizations(context.packageName)
    }

    private fun idleState(): Flow<Boolean> = callbackFlow {
        val listener = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                trySend(isIdle())
            }
        }

        trySend(isIdle())

        val idleFilter = IntentFilter(PowerManager.ACTION_DEVICE_IDLE_MODE_CHANGED)
        context.registerReceiver(listener, idleFilter)

        awaitClose {
            context.unregisterReceiver(listener)
        }
    }

    @Suppress("MissingPermission")
    private fun connectivityStatus(): Flow<ConnectivityStatus> = callbackFlow {

        val activeNetwork = context.connectivityManager.activeNetwork
        if (activeNetwork == null) {
            trySend(ConnectivityStatus.ConnectivityNotAvailable)
        }
        val listener = object : ConnectivityManager.NetworkCallback() {

            override fun onLost(network: Network) {
                trySend(ConnectivityStatus.ConnectivityNotAvailable)
            }

            override fun onCapabilitiesChanged(
                network: Network,
                networkCapabilities: NetworkCapabilities
            ) {

                if (networkCapabilities.hasValidatedInternet) {
                    val uploadKbps = networkCapabilities.linkUpstreamBandwidthKbps
                    val downloadKbps = networkCapabilities.linkDownstreamBandwidthKbps
                    trySend(
                        ConnectivityStatus.Connected(
                            uploadKbps = uploadKbps,
                            downloadKbps = downloadKbps
                        )
                    )
                } else if (networkCapabilities.maybeHasInternet) {
                    trySend(ConnectivityStatus.MaybeConnected)
                } else {
                    trySend(ConnectivityStatus.ConnectivityNotAvailable)
                }

            }
        }
        context.connectivityManager.registerDefaultNetworkCallback(listener)

        awaitClose {
            context.connectivityManager.unregisterNetworkCallback(listener)
        }
    }

    private companion object {
        private const val THROTTLE_DELAY = 1000L
    }
}

private val Context.connectivityManager: ConnectivityManager
    get() = requireNotNull(getSystemService())

private val NetworkCapabilities.hasValidatedInternet: Boolean
    get() = hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)

private val NetworkCapabilities.maybeHasInternet: Boolean
    get() = hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
            hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_RESTRICTED) &&
            (
                    Build.VERSION.SDK_INT < Build.VERSION_CODES.P ||
                            hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_SUSPENDED)
                    )