package com.perennial.weather.domain.usecase

import org.junit.Assert.*
import com.perennial.weather.data.local.entity.WeatherEntity
import com.perennial.weather.domain.repository.WeatherRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.whenever

@RunWith(MockitoJUnitRunner::class)
class GetWeatherHistoryUseCaseTest {

    @Mock
    private lateinit var weatherRepository: WeatherRepository

    private lateinit var getWeatherHistoryUseCase: GetWeatherHistoryUseCase

    @Before
    fun setup() {
        getWeatherHistoryUseCase = GetWeatherHistoryUseCase(weatherRepository)
    }

    @Test
    fun `invoke should return flow from repository`() = runTest {
        val email = "test@example.com"
        val expectedHistory = listOf(
            createMockWeatherEntity(1),
            createMockWeatherEntity(2)
        )
        whenever(weatherRepository.getWeatherHistoryByEmail(email)).thenReturn(flowOf(expectedHistory))

        val result = getWeatherHistoryUseCase(email)

        assertEquals(expectedHistory, result.first())
    }

    @Test
    fun `invoke should return empty list when no history exists`() = runTest {
        val email = "test@example.com"
        whenever(weatherRepository.getWeatherHistoryByEmail(email)).thenReturn(flowOf(emptyList()))

        val result = getWeatherHistoryUseCase(email)

        assertTrue(result.first().isEmpty())
    }

    private fun createMockWeatherEntity(id: Int = 1): WeatherEntity {
        return WeatherEntity(
            id = id,
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

