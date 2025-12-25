package com.perennial.weather.ui.home.weatherhistory

import org.junit.Assert.*
import app.cash.turbine.test
import com.perennial.weather.data.local.entity.WeatherEntity
import com.perennial.weather.domain.usecase.GetWeatherHistoryUseCase
import com.perennial.weather.utils.TestMainDispatcher
import com.perennial.weather.utils.UserPreferencesManager
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.whenever

@RunWith(MockitoJUnitRunner::class)
class WeatherHistoryViewModelTest {

    @Mock
    private lateinit var getWeatherHistoryUseCase: GetWeatherHistoryUseCase

    @Mock
    private lateinit var userPreferencesManager: UserPreferencesManager

    @Before
    fun setup() {
        TestMainDispatcher.setup()
    }

    @After
    fun tearDown() {
        TestMainDispatcher.teardown()
    }

    @Test
    fun weatherHistory_shouldEmitWeatherHistoryFromUseCase() = runTest(TestMainDispatcher.getDispatcher()) {
        val email = "test@example.com"
        val weatherHistory = listOf(
            createMockWeatherEntity(1),
            createMockWeatherEntity(2),
            createMockWeatherEntity(3)
        )
        whenever(userPreferencesManager.getUserEmail()).thenReturn(email)
        val mockFlow = MutableSharedFlow<List<WeatherEntity>>(replay = 1)
        whenever(getWeatherHistoryUseCase(email)).thenReturn(mockFlow)

        val weatherHistoryViewModel = WeatherHistoryViewModel(getWeatherHistoryUseCase, userPreferencesManager)
        
        weatherHistoryViewModel.weatherHistory.test {
            val initialValue = awaitItem()
            assertTrue(initialValue.isEmpty())

            testScheduler.advanceUntilIdle()
            
            mockFlow.emit(weatherHistory)
            testScheduler.advanceUntilIdle()
            
            val actualValue = awaitItem()
            assertEquals(weatherHistory, actualValue)
            
            cancel()
        }
    }

    @Test
    fun weatherHistory_shouldEmitEmptyListWhenNoHistoryExists() = runTest(TestMainDispatcher.getDispatcher()) {
        val email = "test@example.com"
        whenever(userPreferencesManager.getUserEmail()).thenReturn(email)
        val mockFlow = MutableSharedFlow<List<WeatherEntity>>(replay = 1)
        whenever(getWeatherHistoryUseCase(email)).thenReturn(mockFlow)

        val weatherHistoryViewModel = WeatherHistoryViewModel(getWeatherHistoryUseCase, userPreferencesManager)
        
        testScheduler.advanceUntilIdle()
        
        weatherHistoryViewModel.weatherHistory.test {
            val initialValue = awaitItem()
            assertTrue(initialValue.isEmpty())

            cancel()
        }
    }

    @Test
    fun weatherHistory_shouldUseEmptyStringWhenUserEmailIsNull() = runTest(TestMainDispatcher.getDispatcher()) {
        val weatherHistory = listOf(createMockWeatherEntity(1))
        whenever(userPreferencesManager.getUserEmail()).thenReturn(null)
        val mockFlow = MutableSharedFlow<List<WeatherEntity>>(replay = 1)
        whenever(getWeatherHistoryUseCase("")).thenReturn(mockFlow)

        val weatherHistoryViewModel = WeatherHistoryViewModel(getWeatherHistoryUseCase, userPreferencesManager)
        
        weatherHistoryViewModel.weatherHistory.test {
            val initialValue = awaitItem()
            assertTrue(initialValue.isEmpty())

            testScheduler.advanceUntilIdle()

            mockFlow.emit(weatherHistory)
            testScheduler.advanceUntilIdle()
            
            val actualValue = awaitItem()
            assertEquals(weatherHistory, actualValue)
            
            cancel()
        }
    }

    @Test
    fun initialState_shouldHaveEmptyList() = runTest(TestMainDispatcher.getDispatcher()) {
        val email = "test@example.com"
        whenever(userPreferencesManager.getUserEmail()).thenReturn(email)
        whenever(getWeatherHistoryUseCase(email)).thenReturn(flow { emit(emptyList()) })

        val weatherHistoryViewModel = WeatherHistoryViewModel(getWeatherHistoryUseCase, userPreferencesManager)
        
        weatherHistoryViewModel.weatherHistory.test {
            val initialValue = awaitItem()
            assertTrue(initialValue.isEmpty())
            
            cancel()
        }
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

