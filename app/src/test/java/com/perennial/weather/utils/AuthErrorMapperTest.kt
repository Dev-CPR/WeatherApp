package com.perennial.weather.utils

import android.content.Context
import org.junit.Assert.*
import com.perennial.weather.R
import com.perennial.weather.domain.model.AuthError
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.whenever

@RunWith(MockitoJUnitRunner::class)
class AuthErrorMapperTest {

    @Mock
    private lateinit var context: Context

    @Test
    fun `getMessage should return base message when message is null`() {
        val baseMessage = "Error occurred"
        val authError = AuthError(R.string.error_occurred)
        whenever(context.getString(R.string.error_occurred)).thenReturn(baseMessage)

        val result = authError.getMessage(context)

        assertEquals(baseMessage, result)
    }

    @Test
    fun `getMessage should return base message with additional message when message is not null`() {
        val baseMessage = "Error occurred"
        val additionalMessage = "Network error"
        val authError = AuthError(R.string.error_occurred, additionalMessage)
        whenever(context.getString(R.string.error_occurred)).thenReturn(baseMessage)

        val result = authError.getMessage(context)

        assertEquals("$baseMessage: $additionalMessage", result)
    }

    @Test
    fun `getMessage should handle empty additional message`() {
        val baseMessage = "Error occurred"
        val authError = AuthError(R.string.error_occurred, "")
        whenever(context.getString(R.string.error_occurred)).thenReturn(baseMessage)

        val result = authError.getMessage(context)

        assertEquals("$baseMessage: ", result)
    }
}

