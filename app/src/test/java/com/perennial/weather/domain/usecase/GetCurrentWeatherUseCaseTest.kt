package com.perennial.weather.domain.usecase

import org.junit.Assert.*
import com.perennial.weather.data.local.entity.WeatherEntity
import com.perennial.weather.domain.repository.WeatherRepository
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@RunWith(MockitoJUnitRunner::class)
class GetCurrentWeatherUseCaseTest {

    @Mock
    private lateinit var weatherRepository: WeatherRepository

    private lateinit var getCurrentWeatherUseCase: GetCurrentWeatherUseCase

    @Before
    fun setup() {
        getCurrentWeatherUseCase = GetCurrentWeatherUseCase(weatherRepository)
    }

    @Test
    fun `invoke should fetch and save weather then return latest weather`() = runTest {
        val lat = 40.7128
        val lon = -74.0060
        val userEmail = "test@example.com"
        val expectedWeather = createMockWeatherEntity()
        whenever(weatherRepository.getLatestWeather()).thenReturn(expectedWeather)

        val result = getCurrentWeatherUseCase(lat, lon, userEmail)

        verify(weatherRepository).fetchAndSaveWeather(lat, lon, userEmail)
        verify(weatherRepository).getLatestWeather()
        assertTrue(result.isSuccess)
        assertEquals(expectedWeather, result.getOrNull())
    }

    @Test
    fun `invoke should return null when no weather is available`() = runTest {
        val lat = 40.7128
        val lon = -74.0060
        val userEmail = "test@example.com"
        whenever(weatherRepository.getLatestWeather()).thenReturn(null)

        val result = getCurrentWeatherUseCase(lat, lon, userEmail)

        assertTrue(result.isSuccess)
        assertNull(result.getOrNull())
    }

    @Test
    fun `invoke should return failure when repository throws exception`() = runTest {
        val lat = 40.7128
        val lon = -74.0060
        val userEmail = "test@example.com"
        val exception = RuntimeException("Network error")
        whenever(weatherRepository.fetchAndSaveWeather(lat, lon, userEmail)).thenThrow(exception)

        val result = getCurrentWeatherUseCase(lat, lon, userEmail)

        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }

    @Test
    fun `invoke should return failure when getLatestWeather throws exception`() = runTest {
        val lat = 40.7128
        val lon = -74.0060
        val userEmail = "test@example.com"
        val exception = RuntimeException("Database error")
        whenever(weatherRepository.getLatestWeather()).thenThrow(exception)

        val result = getCurrentWeatherUseCase(lat, lon, userEmail)

        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
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

