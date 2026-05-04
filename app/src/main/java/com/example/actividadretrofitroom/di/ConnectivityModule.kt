package com.example.actividadretrofitroom.di

import com.example.actividadretrofitroom.core.connectivity.ConnectivityObserver
import com.example.actividadretrofitroom.core.connectivity.ConnectivityObserverImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class ConnectivityModule {
    @Binds
    abstract fun bindConnectivityObserver(impl: ConnectivityObserverImpl): ConnectivityObserver
}

