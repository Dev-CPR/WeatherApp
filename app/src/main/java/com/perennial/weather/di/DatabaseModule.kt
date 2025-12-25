package com.perennial.weather.di

import android.content.Context
import androidx.room.Room
import com.perennial.weather.BuildConfig
import com.perennial.weather.data.local.database.WeatherDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import net.sqlcipher.database.SupportFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideDatabase(
        @ApplicationContext appContext: Context
    ): WeatherDatabase {

        val passphrase = BuildConfig.MASTER_KEY_SQL_CIPHER.toByteArray()
        val supportFactory = SupportFactory(passphrase, null, true)

        return Room.databaseBuilder(
            appContext,
            WeatherDatabase::class.java,
            "weather_db"
        )
            .openHelperFactory(supportFactory)
            .build()
    }

    @Singleton
    @Provides
    fun provideUserDao(database: WeatherDatabase) = database.userDao()

    @Singleton
    @Provides
    fun provideWeatherDao(database: WeatherDatabase) = database.weatherDao()

}