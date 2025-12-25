package com.perennial.weather.utils

import org.junit.Assert.*
import org.junit.Test

class PasswordUtilsTest {

    @Test
    fun `hashPassword should return hashed string`() {
        val password = "testPassword123"

        val hashed = PasswordUtils.hashPassword(password)

        assertFalse(hashed.isEmpty())
        assertNotEquals(password, hashed)
        assertEquals(64, hashed.length)
    }

    @Test
    fun `hashPassword should produce same hash for same input`() {
        val password = "testPassword123"

        val hashed1 = PasswordUtils.hashPassword(password)
        val hashed2 = PasswordUtils.hashPassword(password)

        assertEquals(hashed1, hashed2)
    }

    @Test
    fun `hashPassword should produce different hash for different input`() {
        val password1 = "testPassword123"
        val password2 = "testPassword456"

        val hashed1 = PasswordUtils.hashPassword(password1)
        val hashed2 = PasswordUtils.hashPassword(password2)

        assertNotEquals(hashed1, hashed2)
    }

    @Test
    fun `hashPassword should handle empty string`() {
        val password = ""

        val hashed = PasswordUtils.hashPassword(password)

        assertFalse(hashed.isEmpty())
        assertEquals(64, hashed.length)
    }

    @Test
    fun `hashPassword should handle special characters`() {
        val password = "!@#$%^&*()"

        val hashed = PasswordUtils.hashPassword(password)

        assertFalse(hashed.isEmpty())
        assertEquals(64, hashed.length)
    }
}
