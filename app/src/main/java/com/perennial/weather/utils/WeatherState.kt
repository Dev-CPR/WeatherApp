package com.perennial.weather.utils

import com.perennial.weather.data.local.entity.WeatherEntity

data class WeatherState(
    val isLoading: Boolean = false, val data: WeatherEntity? = null, val error: String? = null
)

