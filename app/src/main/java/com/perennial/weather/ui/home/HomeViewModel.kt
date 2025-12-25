package com.perennial.weather.ui.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.perennial.weather.domain.usecase.GetCurrentWeatherUseCase
import com.perennial.weather.utils.ErrorConstant
import com.perennial.weather.utils.UserPreferencesManager
import com.perennial.weather.utils.WeatherState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getCurrentWeatherUseCase: GetCurrentWeatherUseCase,
    private val userPreferencesManager: UserPreferencesManager
) : ViewModel() {

    private val _weatherState = MutableStateFlow(WeatherState())
    val weatherState: StateFlow<WeatherState> = _weatherState

    private val _latitude = MutableStateFlow(0.00)
    val latitude: StateFlow<Double> = _latitude

    private val _longitude = MutableStateFlow(0.00)
    val longitude: StateFlow<Double> = _longitude


    var error by mutableStateOf<String?>(null)
        private set

    fun updateLocation(lat: Double, lon: Double) {
        _latitude.value = lat
        _longitude.value = lon
    }

    fun updateError(msg: String ?= null) {
        error = msg ?: ""
    }

    fun loadWeather(lat: Double, lon: Double) {
        viewModelScope.launch {
            _weatherState.value = WeatherState(isLoading = true)
            val userEmail = userPreferencesManager.getUserEmail() ?: ""
            getCurrentWeatherUseCase(lat, lon, userEmail)
                .onSuccess { weather ->
                    _weatherState.value = WeatherState(data = weather)
                }
                .onFailure { exception ->
                    _weatherState.value = WeatherState(
                        error = exception.message ?: ErrorConstant.SOMETHING_WENT_WRONG
                    )
                }
        }
    }

    fun refresh(lat: Double, lon: Double) = loadWeather(lat = lat, lon = lon)

    fun clearWeatherError() {
        _weatherState.value = _weatherState.value.copy(error = null)
    }

}