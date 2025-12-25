package com.perennial.weather.di

import com.perennial.weather.domain.repository.AuthRepository
import com.perennial.weather.domain.repository.WeatherRepository
import com.perennial.weather.domain.usecase.GetCurrentWeatherUseCase
import com.perennial.weather.domain.usecase.GetWeatherHistoryUseCase
import com.perennial.weather.domain.usecase.LoginUseCase
import com.perennial.weather.domain.usecase.RegisterUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {
    
    @Provides
    @Singleton
    fun provideLoginUseCase(authRepository: AuthRepository): LoginUseCase {
        return LoginUseCase(authRepository = authRepository)
    }
    
    @Provides
    @Singleton
    fun provideRegisterUseCase(authRepository: AuthRepository): RegisterUseCase {
        return RegisterUseCase(authRepository = authRepository)
    }

    @Provides
    @Singleton
    fun provideGetCurrentWeatherUseCase(
        repository: WeatherRepository
    ): GetCurrentWeatherUseCase {
        return GetCurrentWeatherUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideGetWeatherHistoryUseCase(
        repository: WeatherRepository
    ): GetWeatherHistoryUseCase {
        return GetWeatherHistoryUseCase(repository)
    }
}

