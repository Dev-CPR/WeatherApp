package com.perennial.weather.utils

import java.security.MessageDigest

object PasswordUtils {
    fun hashPassword(password : String): String{
        val byte = MessageDigest.getInstance("SHA-256").digest(password.toByteArray())
        return byte.joinToString("") { "%02x".format(it) }
    }
}