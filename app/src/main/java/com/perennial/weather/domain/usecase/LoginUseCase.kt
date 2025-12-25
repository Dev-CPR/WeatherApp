package com.perennial.weather.domain.usecase

import com.perennial.weather.R
import com.perennial.weather.domain.model.AuthError
import com.perennial.weather.domain.model.Result
import com.perennial.weather.domain.repository.AuthRepository
import com.perennial.weather.utils.AuthValidator

class LoginUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): Result<Boolean> {
        return try {
            AuthValidator.validateEmail(email)?.let {
                return Result.Error(it)
            }
            
            AuthValidator.validatePassword(password)?.let {
                return Result.Error(it)
            }
            
            val success = authRepository.login(email = email.trim(), password = password.trim())
            
            if (success) {
                Result.Success(true)
            } else {
                Result.Error(AuthError(R.string.invalid_credentials))
            }
        } catch (e: Exception) {
            Result.Error(AuthError(R.string.error_occurred, e.message))
        }
    }
}

