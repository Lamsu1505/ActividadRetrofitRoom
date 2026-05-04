package com.example.actividadretrofitroom.core.connectivity

import kotlinx.coroutines.flow.StateFlow

interface ConnectivityObserver {
    val isOnline: StateFlow<Boolean>
}

