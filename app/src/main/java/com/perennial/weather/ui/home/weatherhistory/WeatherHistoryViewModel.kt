package com.perennial.weather.ui.home.weatherhistory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.perennial.weather.data.local.entity.WeatherEntity
import com.perennial.weather.domain.usecase.GetWeatherHistoryUseCase
import com.perennial.weather.utils.UserPreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class WeatherHistoryViewModel @Inject constructor(
    private val getWeatherHistoryUseCase: GetWeatherHistoryUseCase,
    private val userPreferencesManager: UserPreferencesManager
) : ViewModel() {

    val weatherHistory: StateFlow<List<WeatherEntity>> =
        getWeatherHistoryUseCase(userPreferencesManager.getUserEmail() ?: "")
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )
}