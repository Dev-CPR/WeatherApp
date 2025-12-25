package com.perennial.weather.utils

import com.perennial.weather.R
import com.perennial.weather.domain.model.AuthError

object AuthValidator {

    fun validateEmail(email: String): AuthError? {
        return when {
            email.isBlank() -> AuthError(R.string.enter_email)
            !Utils.isValidEmail(email) -> AuthError(R.string.enter_valid_email)
            else -> null
        }
    }

    fun validatePassword(password: String): AuthError? {
        return when {
            password.isBlank() -> AuthError(R.string.enter_password)
            password.length !in 8..16 -> AuthError(R.string.enter_valid_password)
            else -> null
        }
    }

    fun validateName(name: String): AuthError? {
        return if (name.isBlank()) {
            AuthError(R.string.enter_name)
        } else {
            null
        }
    }

    fun validateConfirmPassword(confirmPassword: String, password: String): AuthError? {
        return when {
            confirmPassword.isBlank() -> AuthError(R.string.enter_confirm_password)
            password != confirmPassword -> AuthError(R.string.enter_valid_confirm_password)
            else -> null
        }
    }
}