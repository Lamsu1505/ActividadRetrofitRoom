package com.example.actividadretrofitroom.Core

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.actividadretrofitroom.Features.CountryDetail.CountryDetailScreen
import com.example.actividadretrofitroom.Features.Home.HomeScreen

// ─────────────────────────────────────────────────────────────────────────────
// Constantes de animación
// ─────────────────────────────────────────────────────────────────────────────

private const val ANIM_DURATION = 350

// ─────────────────────────────────────────────────────────────────────────────
// Nodo de navegación principal
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController(),
) {
    NavHost(
        navController = navController,
        startDestination = AppRoutes.CountryList,
        contentAlignment = Alignment.Center,
        // Transición de entrada global (puede ser sobreescrita por destino)
        enterTransition = {
            slideInHorizontally(
                initialOffsetX = { fullWidth -> fullWidth },
                animationSpec = tween(ANIM_DURATION),
            ) + fadeIn(animationSpec = tween(ANIM_DURATION))
        },
        exitTransition = {
            slideOutHorizontally(
                targetOffsetX = { fullWidth -> -fullWidth / 3 },
                animationSpec = tween(ANIM_DURATION),
            ) + fadeOut(animationSpec = tween(ANIM_DURATION))
        },
        popEnterTransition = {
            slideInHorizontally(
                initialOffsetX = { fullWidth -> -fullWidth / 3 },
                animationSpec = tween(ANIM_DURATION),
            ) + fadeIn(animationSpec = tween(ANIM_DURATION))
        },
        popExitTransition = {
            slideOutHorizontally(
                targetOffsetX = { fullWidth -> fullWidth },
                animationSpec = tween(ANIM_DURATION),
            ) + fadeOut(animationSpec = tween(ANIM_DURATION))
        },
    ) {

        // ── Destino 1: Lista de países ────────────────────────────────────────
        composable<AppRoutes.CountryList> {
            HomeScreen(
                onCountryClick = { cca3 ->
                    navController.navigate(AppRoutes.CountryDetail(cca3 = cca3))
                },
            )
        }

        // ── Destino 2: Detalle de país ────────────────────────────────────────
        composable<AppRoutes.CountryDetail> {
            CountryDetailScreen(
                onBack = { navController.popBackStack() },
            )
        }
    }
}