package com.perennial.weather.domain.model

import org.junit.Assert.*
import org.junit.Test

class FieldErrorTest {

    @Test
    fun `FieldError should store message and field`() {
        val message = "Error message"
        val field = FormField.EMAIL

        val fieldError = FieldError(message, field)

        assertEquals(message, fieldError.message)

        assertEquals(field, fieldError.field)
    }

    @Test
    fun `FieldError should work with all FormField types`() {
        val message = "Error message"

        val nameError = FieldError(message, FormField.NAME)
        val emailError = FieldError(message, FormField.EMAIL)
        val passwordError = FieldError(message, FormField.PASSWORD)
        val confirmPasswordError = FieldError(message, FormField.CONFIRM_PASSWORD)

        assertEquals(FormField.NAME, nameError.field)
        assertEquals(FormField.EMAIL, emailError.field)
        assertEquals(FormField.PASSWORD, passwordError.field)
        assertEquals(FormField.CONFIRM_PASSWORD, confirmPasswordError.field)
    }

    @Test
    fun `FieldError should be equal when message and field are same`() {
        val message = "Error message"
        val field = FormField.EMAIL

        val fieldError1 = FieldError(message, field)

        val fieldError2 = FieldError(message, field)

        assertEquals(fieldError2, fieldError1)
    }

    @Test
    fun `FieldError should not be equal when message differs`() {
        val field = FormField.EMAIL

        val fieldError1 = FieldError("Error message 1", field)

        val fieldError2 = FieldError("Error message 2", field)

        assertNotEquals(fieldError2, fieldError1)
    }

    @Test
    fun `FieldError should not be equal when field differs`() {
        val message = "Error message"

        val fieldError1 = FieldError(message, FormField.EMAIL)

        val fieldError2 = FieldError(message, FormField.PASSWORD)

        assertNotEquals(fieldError2, fieldError1)
    }
}

