package com.perennial.weather.domain.repository

interface AuthRepository {
    suspend fun register(name: String, email: String, password : String) : Boolean
    suspend fun login(email : String, password : String) : Boolean
}