package com.perennial.weather.utils

import android.content.SharedPreferences
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPreferencesManager @Inject constructor(
    private val sharedPreferences: SharedPreferences
) {
    private val keyUserEmail = "user_email"
    fun saveUserEmail(email: String) {
        val editor = sharedPreferences.edit()
        editor.putString(keyUserEmail, email)
        editor.apply()
    }
    fun getUserEmail(): String? {
        val key = keyUserEmail
        val defaultValue: String? = null
        return sharedPreferences.getString(key, defaultValue)
    }
    fun clearUserEmail() {
        val editor = sharedPreferences.edit()
        editor.remove(keyUserEmail)
        editor.apply()
    }
}

