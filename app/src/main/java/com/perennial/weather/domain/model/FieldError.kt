package com.perennial.weather.domain.model

data class FieldError(
    val message: String,
    val field: FormField
)

enum class FormField {
    NAME,
    EMAIL,
    PASSWORD,
    CONFIRM_PASSWORD
}

