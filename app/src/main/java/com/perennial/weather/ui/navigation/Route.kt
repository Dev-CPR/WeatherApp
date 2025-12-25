package com.perennial.weather.ui.navigation

import com.perennial.weather.utils.Constant

sealed class Route(val route: String){
    data object Splash : Route(Constant.SPLASH)
    data object Login : Route(Constant.LOGIN)
    data object Register : Route(Constant.REGISTER)
    data object Home : Route(Constant.HOME)
}