package com.example.actividadretrofitroom.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.actividadretrofitroom.ui.screens.detail.CountryDetailScreen
import com.example.actividadretrofitroom.ui.screens.detail.CountryDetailViewModel
import com.example.actividadretrofitroom.ui.screens.list.CountriesListScreen
import com.example.actividadretrofitroom.ui.screens.list.CountriesListViewModel

sealed class Route(val pattern: String) {
    data object List : Route("list")
    data object Detail : Route("detail/{cca3}") {
        fun create(cca3: String) = "detail/$cca3"
        const val ARG = "cca3"
    }
}

@Composable
fun AppNav(navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = Route.List.pattern) {
        composable(Route.List.pattern) {
            val vm: CountriesListViewModel = hiltViewModel()
            val state by vm.state.collectAsState()
            val online by vm.isOnline.collectAsState()
            CountriesListScreen(
                state = state,
                pagingFlow = vm.pagingFlow,
                isOnline = online,
                onRefresh = vm::refreshAll,
                onApplySearch = vm::applySearch,
                onApplyRegion = vm::applyRegion,
                onCountryClick = { navController.navigate(Route.Detail.create(it)) },
            )
        }
        composable(Route.Detail.pattern) {
            val vm: CountryDetailViewModel = hiltViewModel()
            val state by vm.state.collectAsState()
            val online by vm.isOnline.collectAsState()
            CountryDetailScreen(
                state = androidx.compose.runtime.rememberUpdatedState(state),
                isOnline = androidx.compose.runtime.rememberUpdatedState(online),
                onRetry = vm::refresh,
                onBack = { navController.popBackStack() },
            )
        }
    }
}

