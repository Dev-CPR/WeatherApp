package com.perennial.weather.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.perennial.weather.data.local.entity.UserEntity

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun registerUser(user: UserEntity)

    @Query("SELECT * FROM users where email = :email and password = :password")
    suspend fun login(email: String, password: String) : UserEntity?

    @Query("SELECT * FROM users where email = :email")
    suspend fun getUserByEmail(email: String) : UserEntity?

}