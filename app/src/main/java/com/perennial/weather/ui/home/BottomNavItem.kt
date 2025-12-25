package com.perennial.weather.ui.home


import androidx.annotation.DrawableRes
import com.perennial.weather.R
import com.perennial.weather.utils.Constant

sealed class BottomNavItem(
    val route: String,
    val label: String,
    @DrawableRes val icon: Int){
    object CurrentWeather : BottomNavItem(Constant.CURRENT_WEATHER,Constant.WEATHER, R.drawable.sunrise)
    object WeatherHistory : BottomNavItem(Constant.WEATHER_HISTORY,Constant.HISTORY, R.drawable.sunset)

}