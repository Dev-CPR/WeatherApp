package com.perennial.weather.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.perennial.weather.data.local.entity.WeatherEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WeatherDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeather(weather: WeatherEntity)

    @Query("SELECT * FROM weather ORDER BY createdAt DESC LIMIT 1")
    suspend fun getLatestWeather(): WeatherEntity?

    @Query("SELECT * FROM weather ORDER BY createdAt DESC")
    fun getWeatherHistory(): Flow<List<WeatherEntity>>

    @Query("SELECT * FROM weather WHERE userEmail = :email ORDER BY createdAt DESC")
    fun getWeatherHistoryByEmail(email: String): Flow<List<WeatherEntity>>
}
