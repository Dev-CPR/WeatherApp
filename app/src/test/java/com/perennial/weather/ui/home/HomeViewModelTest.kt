package com.perennial.weather.ui.home

import org.junit.Assert.*
import app.cash.turbine.test
import com.perennial.weather.data.local.entity.WeatherEntity
import com.perennial.weather.domain.usecase.GetCurrentWeatherUseCase
import com.perennial.weather.utils.ErrorConstant
import com.perennial.weather.utils.TestMainDispatcher
import com.perennial.weather.utils.UserPreferencesManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.whenever

@RunWith(MockitoJUnitRunner::class)
class HomeViewModelTest {

    @Mock
    private lateinit var getCurrentWeatherUseCase: GetCurrentWeatherUseCase

    @Mock
    private lateinit var userPreferencesManager: UserPreferencesManager

    private lateinit var homeViewModel: HomeViewModel

    @Before
    fun setup() {
        TestMainDispatcher.setup()
        homeViewModel = HomeViewModel(getCurrentWeatherUseCase, userPreferencesManager)
    }

    @After
    fun tearDown() {
        TestMainDispatcher.teardown()
    }

    @Test
    fun `updateLocation should update latitude and longitude`() = runTest {
        val lat = 40.7128
        val lon = -74.0060

        homeViewModel.updateLocation(lat, lon)

        assertEquals(lat, homeViewModel.latitude.first(), 0.0001)
        assertEquals(lon, homeViewModel.longitude.first(), 0.0001)
    }

    @Test
    fun `updateError should update error message`() = runTest {
        val errorMessage = "Test error"

        homeViewModel.updateError(errorMessage)

        assertEquals(errorMessage, homeViewModel.error)
    }

    @Test
    fun `updateError should set empty string when message is null`() = runTest {
        homeViewModel.updateError(null)

        assertEquals("", homeViewModel.error)
    }

    @Test
    fun `loadWeather should set loading state and then load weather successfully`() = runTest(TestMainDispatcher.getDispatcher()) {
        val lat = 40.7128
        val lon = -74.0060
        val userEmail = "test@example.com"
        val weatherEntity = createMockWeatherEntity()
        whenever(userPreferencesManager.getUserEmail()).thenReturn(userEmail)
        whenever(getCurrentWeatherUseCase(lat, lon, userEmail))
            .thenReturn(Result.success(weatherEntity))

        homeViewModel.loadWeather(lat, lon)
        testScheduler.advanceUntilIdle()

        val weatherState = homeViewModel.weatherState.first()
        assertFalse(weatherState.isLoading)
        assertEquals(weatherEntity, weatherState.data)
        assertNull(weatherState.error)
    }

    @Test
    fun `loadWeather should set error state when use case fails`() = runTest(TestMainDispatcher.getDispatcher()) {
        val lat = 40.7128
        val lon = -74.0060
        val userEmail = "test@example.com"
        val errorMessage = "Network error"
        whenever(userPreferencesManager.getUserEmail()).thenReturn(userEmail)
        whenever(getCurrentWeatherUseCase(lat, lon, userEmail))
            .thenReturn(Result.failure(RuntimeException(errorMessage)))

        homeViewModel.loadWeather(lat, lon)
        testScheduler.advanceUntilIdle()

        val weatherState = homeViewModel.weatherState.first()
        assertFalse(weatherState.isLoading)
        assertNull(weatherState.data)
        assertEquals(errorMessage, weatherState.error)
    }

    @Test
    fun `loadWeather should use default error message when exception has no message`() = runTest(TestMainDispatcher.getDispatcher()) {
        val lat = 40.7128
        val lon = -74.0060
        val userEmail = "test@example.com"
        whenever(userPreferencesManager.getUserEmail()).thenReturn(userEmail)
        whenever(getCurrentWeatherUseCase(lat, lon, userEmail))
            .thenReturn(Result.failure(RuntimeException()))

        homeViewModel.loadWeather(lat, lon)
        testScheduler.advanceUntilIdle()

        val weatherState = homeViewModel.weatherState.first()
        assertEquals(ErrorConstant.SOMETHING_WENT_WRONG, weatherState.error)
    }

    @Test
    fun `refresh should call loadWeather`() = runTest(TestMainDispatcher.getDispatcher()) {
        val lat = 40.7128
        val lon = -74.0060
        val userEmail = "test@example.com"
        val weatherEntity = createMockWeatherEntity()
        whenever(userPreferencesManager.getUserEmail()).thenReturn(userEmail)
        whenever(getCurrentWeatherUseCase(lat, lon, userEmail))
            .thenReturn(Result.success(weatherEntity))

        homeViewModel.refresh(lat, lon)
        testScheduler.advanceUntilIdle()

        val weatherState = homeViewModel.weatherState.first()
        assertEquals(weatherEntity, weatherState.data)
    }

    @Test
    fun `clearWeatherError should clear error from weather state`() = runTest(TestMainDispatcher.getDispatcher()) {
        val lat = 40.7128
        val lon = -74.0060
        val userEmail = "test@example.com"
        whenever(userPreferencesManager.getUserEmail()).thenReturn(userEmail)
        whenever(getCurrentWeatherUseCase(lat, lon, userEmail))
            .thenReturn(Result.failure(RuntimeException("Error")))
        homeViewModel.loadWeather(lat, lon)
        testScheduler.advanceUntilIdle()

        homeViewModel.clearWeatherError()
        testScheduler.advanceUntilIdle()

        val weatherState = homeViewModel.weatherState.first()
        assertNull(weatherState.error)
    }

    @Test
    fun `loadWeather should use empty string when user email is null`() = runTest(TestMainDispatcher.getDispatcher()) {
        val lat = 40.7128
        val lon = -74.0060
        whenever(userPreferencesManager.getUserEmail()).thenReturn(null)
        val weatherEntity = createMockWeatherEntity()
        whenever(getCurrentWeatherUseCase(lat, lon, ""))
            .thenReturn(Result.success(weatherEntity))

        homeViewModel.loadWeather(lat, lon)
        testScheduler.advanceUntilIdle()

        val weatherState = homeViewModel.weatherState.first()
        assertEquals(weatherEntity, weatherState.data)
    }

    @Test
    fun initialState_shouldHaveDefaultValues() = runTest {
        assertEquals(0.0, homeViewModel.latitude.first(), 0.0001)
        assertEquals(0.0, homeViewModel.longitude.first(), 0.0001)
        assertNull(homeViewModel.error)
        val weatherState = homeViewModel.weatherState.first()
        assertFalse(weatherState.isLoading)
        assertNull(weatherState.data)
        assertNull(weatherState.error)
    }

    @Test
    fun loadWeather_shouldSetLoadingStateInitially() = runTest(TestMainDispatcher.getDispatcher()) {
        val lat = 40.7128
        val lon = -74.0060
        val userEmail = "test@example.com"
        val weatherEntity = createMockWeatherEntity()
        whenever(userPreferencesManager.getUserEmail()).thenReturn(userEmail)
        whenever(getCurrentWeatherUseCase(lat, lon, userEmail))
            .thenReturn(Result.success(weatherEntity))

        homeViewModel.weatherState.test {
            val initialState = awaitItem()
            assertFalse("Initial state should not be loading", initialState.isLoading)

            homeViewModel.loadWeather(lat, lon)

            testScheduler.runCurrent()

            val loadingState = awaitItem()
            assertTrue("Expected loading state to be true", loadingState.isLoading)

            testScheduler.advanceUntilIdle()

            val finalState = awaitItem()
            assertFalse("Expected loading state to be false after completion", finalState.isLoading)
            assertEquals(weatherEntity, finalState.data)
            
            cancel()
        }
    }

    @Test
    fun clearWeatherError_shouldPreserveOtherStateFields() = runTest(TestMainDispatcher.getDispatcher()) {
        val lat = 40.7128
        val lon = -74.0060
        val userEmail = "test@example.com"
        val weatherEntity = createMockWeatherEntity()
        whenever(userPreferencesManager.getUserEmail()).thenReturn(userEmail)
        whenever(getCurrentWeatherUseCase(lat, lon, userEmail))
            .thenReturn(Result.success(weatherEntity))

        homeViewModel.loadWeather(lat, lon)
        testScheduler.advanceUntilIdle()

        val stateBeforeClear = homeViewModel.weatherState.value
        assertNotNull(stateBeforeClear.data)
        assertNull(stateBeforeClear.error)

        whenever(getCurrentWeatherUseCase(lat, lon, userEmail))
            .thenReturn(Result.failure(RuntimeException("Error")))
        homeViewModel.loadWeather(lat, lon)
        testScheduler.advanceUntilIdle()

        val stateWithError = homeViewModel.weatherState.value
        assertNotNull(stateWithError.error)

        homeViewModel.clearWeatherError()

        val stateAfterClear = homeViewModel.weatherState.value
        assertNull(stateAfterClear.error)
        assertFalse(stateAfterClear.isLoading)
    }

    @Test
    fun loadWeather_shouldClearPreviousErrorBeforeLoading() = runTest(TestMainDispatcher.getDispatcher()) {
        val lat = 40.7128
        val lon = -74.0060
        val userEmail = "test@example.com"
        whenever(userPreferencesManager.getUserEmail()).thenReturn(userEmail)

        whenever(getCurrentWeatherUseCase(lat, lon, userEmail))
            .thenReturn(Result.failure(RuntimeException("First error")))
        homeViewModel.loadWeather(lat, lon)
        testScheduler.advanceUntilIdle()

        val stateWithError = homeViewModel.weatherState.value
        assertNotNull(stateWithError.error)
        assertEquals("First error", stateWithError.error)

        val weatherEntity = createMockWeatherEntity()
        whenever(getCurrentWeatherUseCase(lat, lon, userEmail))
            .thenReturn(Result.success(weatherEntity))
        
        homeViewModel.weatherState.test {
            skipItems(1)
            
            homeViewModel.loadWeather(lat, lon)
            
            testScheduler.runCurrent()
            
            val loadingState = awaitItem()
            assertTrue(loadingState.isLoading)
            assertNull("Error should be cleared when loading starts", loadingState.error)
            
            testScheduler.advanceUntilIdle()
            
            val successState = awaitItem()
            assertNull(successState.error)
            assertEquals(weatherEntity, successState.data)
            
            cancel()
        }
    }

    @Test
    fun updateError_shouldUpdateErrorWithEmptyStringWhenCalledWithNoArgs() = runTest {
        homeViewModel.updateError()

        assertEquals("", homeViewModel.error)
    }

    @Test
    fun clearWeatherError_shouldWorkWhenNoErrorExists() = runTest {
        val initialState = homeViewModel.weatherState.value
        assertNull(initialState.error)

        homeViewModel.clearWeatherError()

        val stateAfterClear = homeViewModel.weatherState.value
        assertNull(stateAfterClear.error)
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

