package com.example.actividadretrofitroom

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.actividadretrofitroom.ui.theme.ActividadRetrofitRoomTheme
import com.example.actividadretrofitroom.ui.navigation.AppNav
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ActividadRetrofitRoomTheme {
                AppNav()
            }
        }
    }
}