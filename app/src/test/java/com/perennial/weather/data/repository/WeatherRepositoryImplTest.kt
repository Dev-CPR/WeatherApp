package com.perennial.weather.data.repository

import org.junit.Assert.*
import com.perennial.weather.BuildConfig
import com.perennial.weather.data.local.dao.WeatherDao
import com.perennial.weather.data.local.entity.WeatherEntity
import com.perennial.weather.data.remote.WeatherApi
import com.perennial.weather.data.remote.model.Clouds
import com.perennial.weather.data.remote.model.Coord
import com.perennial.weather.data.remote.model.Main
import com.perennial.weather.data.remote.model.Sys
import com.perennial.weather.data.remote.model.Weather
import com.perennial.weather.data.remote.model.WeatherResponse
import com.perennial.weather.data.remote.model.Wind
import com.perennial.weather.utils.Constant
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@RunWith(MockitoJUnitRunner::class)
class WeatherRepositoryImplTest {

    @Mock
    private lateinit var weatherApi: WeatherApi

    @Mock
    private lateinit var weatherDao: WeatherDao

    private lateinit var weatherRepository: WeatherRepositoryImpl

    @Before
    fun setup() {
        weatherRepository = WeatherRepositoryImpl(weatherApi, weatherDao)
    }

    @Test
    fun `fetchAndSaveWeather should fetch weather and save to database`() = runTest {
        val lat = 40.7128
        val lon = -74.0060
        val userEmail = "test@example.com"
        val weatherResponse = createMockWeatherResponse()
        whenever(weatherApi.getWeather(lat, lon, BuildConfig.OPEN_WEATHER_API_KEY)).thenReturn(weatherResponse)

        weatherRepository.fetchAndSaveWeather(lat, lon, userEmail)

        verify(weatherApi).getWeather(lat, lon, BuildConfig.OPEN_WEATHER_API_KEY)
        verify(weatherDao).insertWeather(any())
    }

    @Test
    fun `fetchAndSaveWeather should map weather response correctly`() = runTest {
        val lat = 40.7128
        val lon = -74.0060
        val userEmail = "test@example.com"
        val weatherResponse = createMockWeatherResponse()
        whenever(weatherApi.getWeather(lat, lon, BuildConfig.OPEN_WEATHER_API_KEY)).thenReturn(weatherResponse)

        weatherRepository.fetchAndSaveWeather(lat, lon, userEmail)

        verify(weatherDao).insertWeather(
            org.mockito.kotlin.argThat { entity ->
                entity.city == weatherResponse.name &&
                        entity.country == weatherResponse.sys.country &&
                        entity.temperatureCelsius == weatherResponse.main.temp.toInt() &&
                        entity.condition == weatherResponse.weather[0].main &&
                        entity.icon == weatherResponse.weather[0].icon &&
                        entity.userEmail == userEmail
            }
        )
    }

    @Test
    fun `fetchAndSaveWeather should use UNKNOWN when weather list is empty`() = runTest {
        val lat = 40.7128
        val lon = -74.0060
        val userEmail = "test@example.com"
        val weatherResponse = createMockWeatherResponse(emptyList())
        whenever(weatherApi.getWeather(lat, lon, BuildConfig.OPEN_WEATHER_API_KEY)).thenReturn(weatherResponse)

        weatherRepository.fetchAndSaveWeather(lat, lon, userEmail)

        verify(weatherDao).insertWeather(
            org.mockito.kotlin.argThat { entity ->
                entity.condition == Constant.UNKNOWN && entity.icon == ""
            }
        )
    }

    @Test
    fun `fetchAndSaveWeather should use UNKNOWN when country is empty`() = runTest {
        val lat = 40.7128
        val lon = -74.0060
        val userEmail = "test@example.com"
        val weatherResponse = createMockWeatherResponse(country = "")
        whenever(weatherApi.getWeather(lat, lon, BuildConfig.OPEN_WEATHER_API_KEY)).thenReturn(weatherResponse)

        weatherRepository.fetchAndSaveWeather(lat, lon, userEmail)

        verify(weatherDao).insertWeather(
            org.mockito.kotlin.argThat { entity ->
                entity.country == Constant.UNKNOWN
            }
        )
    }

    @Test
    fun `getLatestWeather should return latest weather from dao`() = runTest {
        val expectedWeather = createMockWeatherEntity()
        whenever(weatherDao.getLatestWeather()).thenReturn(expectedWeather)

        val result = weatherRepository.getLatestWeather()

        assertEquals(expectedWeather, result)
        verify(weatherDao).getLatestWeather()
    }

    @Test
    fun `getLatestWeather should return null when no weather exists`() = runTest {
        whenever(weatherDao.getLatestWeather()).thenReturn(null)

        val result = weatherRepository.getLatestWeather()

        assertNull(result)
        verify(weatherDao).getLatestWeather()
    }

    @Test
    fun `getWeatherHistory should return flow from dao`() = runTest {
        val expectedHistory = listOf(createMockWeatherEntity())
        whenever(weatherDao.getWeatherHistory()).thenReturn(flowOf(expectedHistory))

        val result = weatherRepository.getWeatherHistory()

        assertEquals(expectedHistory, result.first())
    }

    @Test
    fun `getWeatherHistoryByEmail should return flow from dao`() = runTest {
        val email = "test@example.com"
        val expectedHistory = listOf(createMockWeatherEntity())
        whenever(weatherDao.getWeatherHistoryByEmail(email)).thenReturn(flowOf(expectedHistory))

        val result = weatherRepository.getWeatherHistoryByEmail(email)

        assertEquals(expectedHistory, result.first())
    }

    private fun createMockWeatherResponse(
        weather: List<Weather> = listOf(Weather("clear sky", "01d", 800, "Clear")),
        country: String = "US"
    ): WeatherResponse {
        return WeatherResponse(
            base = "stations",
            clouds = Clouds(0),
            cod = 200,
            coord = Coord(40.7128, -74.0060),
            dt = 1609459200,
            id = 5128581,
            main = Main(15.5, 1013, 60, 1013, 1013, 15.5, 16.0, 15.0),
            name = "New York",
            sys = Sys(country, 1, 1609459200, 1609502400, 1),
            timezone = -18000,
            visibility = 10000,
            weather = weather,
            wind = Wind(270, 3.5)
        )
    }

    private fun createMockWeatherEntity(): WeatherEntity {
        return WeatherEntity(
            id = 1,
            city = "New York",
            country = "US",
            temperatureCelsius = 15,
            sunrise = 1609459200L,
            sunset = 1609502400L,
            condition = "Clear",
            icon = "01d",
            createdAt = System.currentTimeMillis(),
            userEmail = "test@example.com"
        )
    }
}

