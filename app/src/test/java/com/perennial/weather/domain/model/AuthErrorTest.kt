package com.perennial.weather.domain.model

import org.junit.Assert.*
import org.junit.Test

class AuthErrorTest {

    @Test
    fun `AuthError should store messageResId`() {
        val messageResId = 123

        val authError = AuthError(messageResId)

        assertEquals(messageResId, authError.messageResId)
    }

    @Test
    fun `AuthError should store optional message`() {
        val messageResId = 123
        val message = "Error message"

        val authError = AuthError(messageResId, message)

        assertEquals(messageResId, authError.messageResId)

        assertEquals(message, authError.message)
    }

    @Test
    fun `AuthError should have null message when not provided`() {
        val messageResId = 123

        val authError = AuthError(messageResId)

        assertNull(authError.message)
    }

    @Test
    fun `AuthError should be equal when messageResId and message are same`() {
        val messageResId = 123
        val message = "Error message"

        val authError1 = AuthError(messageResId, message)

        val authError2 = AuthError(messageResId, message)

        assertEquals(authError2, authError1)
    }

    @Test
    fun `AuthError should not be equal when messageResId differs`() {
        val message = "Error message"

        val authError1 = AuthError(123, message)

        val authError2 = AuthError(456, message)

        assertNotEquals(authError2, authError1)
    }

    @Test
    fun `AuthError should not be equal when message differs`() {
        val messageResId = 123

        val authError1 = AuthError(messageResId, "Error message 1")

        val authError2 = AuthError(messageResId, "Error message 2")

        assertNotEquals(authError2, authError1)
    }
}

