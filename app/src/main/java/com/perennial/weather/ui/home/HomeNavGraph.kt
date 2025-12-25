package com.perennial.weather.ui.home

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.perennial.weather.ui.home.currentweather.CurrentWeatherScreen
import com.perennial.weather.ui.home.weatherhistory.WeatherHistoryScreen

@Composable
fun HomeNavGraph(
    navHostController: NavHostController,
    homeViewModel: HomeViewModel,
    onRequestLocation: () -> Unit,
    onRefresh: () -> Unit
){
    NavHost(
        navController = navHostController,
        startDestination = BottomNavItem.CurrentWeather.route
    ){
        composable(BottomNavItem.CurrentWeather.route){
            CurrentWeatherScreen(
                viewModel = homeViewModel,
                onRequestLocation = onRequestLocation,
                onRefresh = onRefresh
            )
        }
        composable(BottomNavItem.WeatherHistory.route){
            WeatherHistoryScreen()
        }

    }
}