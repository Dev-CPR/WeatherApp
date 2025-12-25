package com.perennial.weather.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.perennial.weather.data.local.dao.UserDao
import com.perennial.weather.data.local.dao.WeatherDao
import com.perennial.weather.data.local.entity.UserEntity
import com.perennial.weather.data.local.entity.WeatherEntity

@Database(entities = [UserEntity::class, WeatherEntity::class], version = 1, exportSchema = false)
abstract class WeatherDatabase : RoomDatabase() {
    abstract fun userDao() : UserDao
    abstract fun weatherDao(): WeatherDao
}