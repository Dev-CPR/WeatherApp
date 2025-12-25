package com.perennial.weather.domain.usecase

import com.perennial.weather.R
import com.perennial.weather.domain.model.AuthError
import com.perennial.weather.domain.model.Result
import com.perennial.weather.domain.repository.AuthRepository
import com.perennial.weather.utils.AuthValidator

class RegisterUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(
        name: String,
        email: String,
        password: String,
        confirmPassword: String
    ): Result<Boolean> {
        return try {
            AuthValidator.validateName(name)?.let {
                return Result.Error(it)
            }
            
            AuthValidator.validateEmail(email)?.let {
                return Result.Error(it)
            }
            
            AuthValidator.validatePassword(password)?.let {
                return Result.Error(it)
            }
            
            AuthValidator.validateConfirmPassword(confirmPassword, password)?.let {
                return Result.Error(it)
            }
            
            val success = authRepository.register(
                name = name.trim(),
                email = email.trim(),
                password = password.trim()
            )
            
            if (success) {
                Result.Success(true)
            } else {
                Result.Error(AuthError(R.string.user_already_exists))
            }
        } catch (e: Exception) {
            Result.Error(AuthError(R.string.error_occurred, e.message))
        }
    }
}

