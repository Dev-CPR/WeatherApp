package com.perennial.weather.utils

import android.content.Context
import com.perennial.weather.domain.model.AuthError

fun AuthError.getMessage(context: Context): String {
    val baseMessage = context.getString(messageResId)
    return if (message != null) {
        "$baseMessage: $message"
    } else {
        baseMessage
    }
}

