package com.perennial.weather.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weather")
data class WeatherEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val city: String,
    val country: String,
    val temperatureCelsius: Int,
    val sunrise: Long,
    val sunset: Long,
    val condition: String,
    val icon: String,
    val createdAt: Long,
    val userEmail: String
)
