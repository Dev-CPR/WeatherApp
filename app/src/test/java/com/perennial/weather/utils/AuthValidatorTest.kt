package com.perennial.weather.utils

import org.junit.Assert.*
import com.perennial.weather.R
import org.junit.Test

class AuthValidatorTest {

    @Test
    fun `validateEmail should return null for valid email`() {
        val validEmail = "test@example.com"

        val result = AuthValidator.validateEmail(validEmail)

        assertNull(result)
    }

    @Test
    fun `validateEmail should return error for blank email`() {
        val blankEmail = ""

        val result = AuthValidator.validateEmail(blankEmail)

        assertNotNull(result)
        assertEquals(R.string.enter_email, result?.messageResId)
    }

    @Test
    fun `validateEmail should return error for whitespace only email`() {
        val whitespaceEmail = "   "

        val result = AuthValidator.validateEmail(whitespaceEmail)

        assertNotNull(result)
        assertEquals(R.string.enter_email, result?.messageResId)
    }

    @Test
    fun `validateEmail should return error for invalid email format`() {
        val invalidEmail = "invalid-email"

        val result = AuthValidator.validateEmail(invalidEmail)

        assertNotNull(result)
        assertEquals(R.string.enter_valid_email, result?.messageResId)
    }

    @Test
    fun `validateEmail should return error for email without domain`() {
        val invalidEmail = "test@"

        val result = AuthValidator.validateEmail(invalidEmail)

        assertNotNull(result)
        assertEquals(R.string.enter_valid_email, result?.messageResId)
    }

    @Test
    fun `validateEmail should accept valid email with subdomain`() {
        val validEmail = "test@mail.example.com"

        val result = AuthValidator.validateEmail(validEmail)

        assertNull(result)
    }

    @Test
    fun `validatePassword should return null for valid password`() {
        val validPassword = "password123"

        val result = AuthValidator.validatePassword(validPassword)

        assertNull(result)
    }

    @Test
    fun `validatePassword should return error for blank password`() {
        val blankPassword = ""

        val result = AuthValidator.validatePassword(blankPassword)

        assertNotNull(result)
        assertEquals(R.string.enter_password, result?.messageResId)
    }

    @Test
    fun `validatePassword should return error for password shorter than 8 characters`() {
        val shortPassword = "pass12"

        val result = AuthValidator.validatePassword(shortPassword)

        assertNotNull(result)
        assertEquals(R.string.enter_valid_password, result?.messageResId)
    }

    @Test
    fun `validatePassword should return error for password longer than 16 characters`() {
        val longPassword = "password123456789"

        val result = AuthValidator.validatePassword(longPassword)

        assertNotNull(result)
        assertEquals(R.string.enter_valid_password, result?.messageResId)
    }

    @Test
    fun `validatePassword should accept password with exactly 8 characters`() {
        val password = "password"

        val result = AuthValidator.validatePassword(password)

        assertNull(result)
    }

    @Test
    fun `validatePassword should accept password with exactly 16 characters`() {
        val password = "password1234567"

        val result = AuthValidator.validatePassword(password)

        assertNull(result)
    }

    @Test
    fun `validateName should return null for valid name`() {
        val validName = "Test"

        val result = AuthValidator.validateName(validName)

        assertNull(result)
    }

    @Test
    fun `validateName should return error for blank name`() {
        val blankName = ""

        val result = AuthValidator.validateName(blankName)

        assertNotNull(result)
        assertEquals(R.string.enter_name, result?.messageResId)
    }

    @Test
    fun `validateName should return error for whitespace only name`() {
        val whitespaceName = "   "

        val result = AuthValidator.validateName(whitespaceName)

        assertNotNull(result)
        assertEquals(R.string.enter_name, result?.messageResId)
    }

    @Test
    fun `validateConfirmPassword should return null when passwords match`() {
        val password = "password123"
        val confirmPassword = "password123"

        val result = AuthValidator.validateConfirmPassword(confirmPassword, password)

        assertNull(result)
    }

    @Test
    fun `validateConfirmPassword should return error for blank confirm password`() {
        val password = "password123"
        val confirmPassword = ""

        val result = AuthValidator.validateConfirmPassword(confirmPassword, password)

        assertNotNull(result)
        assertEquals(R.string.enter_confirm_password, result?.messageResId)
    }

    @Test
    fun `validateConfirmPassword should return error when passwords do not match`() {
        val password = "password123"
        val confirmPassword = "password456"

        val result = AuthValidator.validateConfirmPassword(confirmPassword, password)

        assertNotNull(result)
        assertEquals(R.string.enter_valid_confirm_password, result?.messageResId)
    }
}

