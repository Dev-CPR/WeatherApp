package com.perennial.weather.domain.usecase

import com.perennial.weather.data.local.entity.WeatherEntity
import com.perennial.weather.domain.repository.WeatherRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetWeatherHistoryUseCase @Inject constructor(
    private val repository: WeatherRepository
) {
    operator fun invoke(email: String): Flow<List<WeatherEntity>> {
        return repository.getWeatherHistoryByEmail(email)
    }
}

