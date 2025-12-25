package com.perennial.weather.domain.model

data class AuthError(
    val messageResId: Int,
    val message: String? = null
)

