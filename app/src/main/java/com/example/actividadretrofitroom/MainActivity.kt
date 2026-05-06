package com.example.actividadretrofitroom

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Surface
import com.example.actividadretrofitroom.Core.AppNavigation
import com.example.actividadretrofitroom.ui.theme.ActividadRetrofitRoomTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ActividadRetrofitRoomTheme {
                Surface() {
                    AppNavigation()
                }
            }
        }
    }
}