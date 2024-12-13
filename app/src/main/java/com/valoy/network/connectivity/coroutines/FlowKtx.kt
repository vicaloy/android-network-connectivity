package com.valoy.network.connectivity.coroutines

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.transform

fun <T> Flow<T>.throttleLatest(delayMillis: Long) = this
    .conflate()
    .transform {
        emit(it)
        delay(delayMillis)
    }
