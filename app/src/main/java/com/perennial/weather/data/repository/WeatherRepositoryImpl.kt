package com.perennial.weather.data.repository

import com.perennial.weather.BuildConfig
import com.perennial.weather.data.local.dao.WeatherDao
import com.perennial.weather.data.local.entity.WeatherEntity
import com.perennial.weather.data.remote.WeatherApi
import com.perennial.weather.domain.repository.WeatherRepository
import com.perennial.weather.utils.Constant
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import kotlin.collections.getOrNull
import kotlin.text.ifEmpty

class WeatherRepositoryImpl @Inject constructor(
    private val api: WeatherApi,
    private val weatherDao: WeatherDao,
) : WeatherRepository{

    override suspend fun fetchAndSaveWeather(lat: Double, lon: Double, userEmail: String) {
        val response = api.getWeather(lat, lon, apiKey = BuildConfig.OPEN_WEATHER_API_KEY)

        val main = response.main
        val condition = response.weather.getOrNull(0)?.main ?: Constant.UNKNOWN
        val icon = response.weather.getOrNull(0)?.icon ?: ""

        val data = WeatherEntity(
            city = response.name,
            country = response.sys.country.ifEmpty { Constant.UNKNOWN },
            temperatureCelsius = main.temp.toInt(),
            sunrise = response.sys.sunrise.toLong(),
            sunset = response.sys.sunset.toLong(),
            condition = condition,
            icon = icon,
            createdAt = System.currentTimeMillis(),
            userEmail = userEmail
        )

        weatherDao.insertWeather(data)
    }

    override suspend fun getLatestWeather(): WeatherEntity? = weatherDao.getLatestWeather()
    override fun getWeatherHistory(): Flow<List<WeatherEntity>> = weatherDao.getWeatherHistory()
    override fun getWeatherHistoryByEmail(email: String): Flow<List<WeatherEntity>> = weatherDao.getWeatherHistoryByEmail(email)

}