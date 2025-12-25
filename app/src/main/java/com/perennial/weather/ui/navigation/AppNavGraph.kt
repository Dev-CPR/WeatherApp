package com.perennial.weather.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.perennial.weather.ui.auth.login.LoginScreen
import com.perennial.weather.ui.auth.register.RegisterScreen
import com.perennial.weather.ui.home.HomeScreen
import com.perennial.weather.ui.splash.SplashScreen

@Composable
fun AppNavGraph(navHostController: NavHostController) {
    NavHost(
        navController = navHostController,
        startDestination = Route.Splash.route
    ) {
        composable(Route.Splash.route) {
            SplashScreen(
                navigateToLogin = {
                    navHostController.navigate(Route.Login.route) {
                        popUpTo(Route.Splash.route) { inclusive = true }
                    }
                },
                navigateToHome = {
                    navHostController.navigate(Route.Home.route){
                        popUpTo(Route.Splash.route){ inclusive = true }
                    }
                },
                onTimeOut = {
                    navHostController.navigate(Route.Login.route) {
                        popUpTo(Route.Splash.route) { inclusive = true }
                    }
                })
        }

        composable(Route.Login.route){
            LoginScreen(navController = navHostController,
                navigateToRegister = {
                    navHostController.navigate(Route.Register.route)
                },
                navigateToHome = {
                    navHostController.navigate(Route.Home.route){
                        popUpTo(Route.Login.route){ inclusive = true }
                    }
                })
        }

        composable(Route.Register.route){
            RegisterScreen(navHostController = navHostController)
        }

        composable(Route.Home.route){
            HomeScreen()
        }
    }
}