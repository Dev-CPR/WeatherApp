package com.perennial.weather.domain.repository

import com.perennial.weather.data.local.entity.WeatherEntity
import kotlinx.coroutines.flow.Flow

interface WeatherRepository {
    suspend fun fetchAndSaveWeather(lat: Double, lon: Double, userEmail: String)
    suspend fun getLatestWeather(): WeatherEntity?
    fun getWeatherHistory(): Flow<List<WeatherEntity>>
    fun getWeatherHistoryByEmail(email: String): Flow<List<WeatherEntity>>
}

