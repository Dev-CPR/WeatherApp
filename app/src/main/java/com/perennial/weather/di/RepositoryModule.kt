package com.perennial.weather.di

import com.perennial.weather.data.local.dao.UserDao
import com.perennial.weather.data.local.dao.WeatherDao
import com.perennial.weather.data.remote.WeatherApi
import com.perennial.weather.data.repository.AuthRepositoryImpl
import com.perennial.weather.data.repository.WeatherRepositoryImpl
import com.perennial.weather.domain.repository.AuthRepository
import com.perennial.weather.domain.repository.WeatherRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Provides
    @Singleton
    fun provideAuthRepository(userDao: UserDao): AuthRepository {
        return AuthRepositoryImpl(userDao = userDao)
    }

    @Provides
    @Singleton
    fun provideWeatherRepository(
        api: WeatherApi,
        weatherDao: WeatherDao
    ): WeatherRepository {
        return WeatherRepositoryImpl(api, weatherDao)
    }
}