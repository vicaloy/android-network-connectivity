package com.valoy.network.connectivity.di

import android.content.Context
import com.valoy.network.connectivity.tracker.ConnectivityTracker
import com.valoy.network.connectivity.tracker.DeviceConnectivityTracker
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ConnectivityModule {

    @Provides
    fun provideContext(@ApplicationContext application: Context): Context {
        return application.applicationContext
    }

    @Provides
    fun provideCoroutineScope(): CoroutineScope {
        return CoroutineScope(SupervisorJob() + Dispatchers.IO)
    }

    @Provides
    @Singleton
    fun provideConnectivityTracker(
        context: Context,
        scope: CoroutineScope
    ): ConnectivityTracker {
        return DeviceConnectivityTracker(context, scope)
    }
}