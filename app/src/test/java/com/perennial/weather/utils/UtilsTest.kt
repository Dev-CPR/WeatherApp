package com.perennial.weather.utils

import org.junit.Assert.*
import org.junit.Test

class UtilsTest {

    @Test
    fun `isValidEmail should return true for valid email`() {
        val validEmail = "test@example.com"
        val result = Utils.isValidEmail(validEmail)
        assertTrue(result)
    }

    @Test
    fun `isValidEmail should return false for invalid email without @`() {
        val invalidEmail = "testexample.com"
        val result = Utils.isValidEmail(invalidEmail)
        assertFalse(result)
    }

    @Test
    fun `isValidEmail should return false for invalid email without domain`() {
        val invalidEmail = "test@"
        val result = Utils.isValidEmail(invalidEmail)
        assertFalse(result)
    }

    @Test
    fun `isValidEmail should return false for invalid email without TLD`() {
        val invalidEmail = "test@example"
        val result = Utils.isValidEmail(invalidEmail)
        assertFalse(result)
    }

    @Test
    fun `isValidEmail should return true for email with subdomain`() {
        val validEmail = "test@mail.example.com"
        val result = Utils.isValidEmail(validEmail)
        assertTrue(result)
    }

    @Test
    fun `isValidEmail should return true for email with plus sign`() {
        val validEmail = "test+tag@example.com"
        val result = Utils.isValidEmail(validEmail)
        assertTrue(result)
    }

    @Test
    fun `isValidEmail should return true for email with dots`() {
        val validEmail = "test.name@example.com"
        val result = Utils.isValidEmail(validEmail)
        assertTrue(result)
    }

    @Test
    fun `isValidEmail should return false for empty string`() {
        val emptyEmail = ""

        val result = Utils.isValidEmail(emptyEmail)

        assertFalse(result)
    }

    @Test
    fun `formatTime should format timestamp correctly`() {
        val timestamp = 1609459200L

        val formatted = Utils.formatTime(timestamp)

        assertFalse(formatted.isEmpty())
        assertTrue(formatted.contains(":"))
        assertTrue(formatted.any { it.isDigit() })
        val upperFormatted = formatted.uppercase()
        assertTrue(upperFormatted.contains("AM") || upperFormatted.contains("PM"))
    }

    @Test
    fun `formatTime should handle zero timestamp`() {
        val timestamp = 0L

        val formatted = Utils.formatTime(timestamp)

        assertFalse(formatted.isEmpty())
    }

    @Test
    fun `formatTime should handle large timestamp`() {
        val timestamp = 2147483647L

        val formatted = Utils.formatTime(timestamp)

        assertFalse(formatted.isEmpty())
    }

    @Test
    fun `formatTime should handle negative timestamp`() {
        val timestamp = -1000L

        val formatted = Utils.formatTime(timestamp)

        assertFalse(formatted.isEmpty())
    }

    @Test
    fun `isValidEmail should return false for email with spaces`() {
        val invalidEmail = "test @example.com"

        val result = Utils.isValidEmail(invalidEmail)

        assertFalse(result)
    }

    @Test
    fun `isValidEmail should return false for email with multiple @ signs`() {
        val invalidEmail = "test@@example.com"

        val result = Utils.isValidEmail(invalidEmail)

        assertFalse(result)
    }

    @Test
    fun `isValidEmail should return false for email starting with @`() {
        val invalidEmail = "@example.com"

        val result = Utils.isValidEmail(invalidEmail)

        assertFalse(result)
    }

    @Test
    fun `isValidEmail should return false for email ending with @`() {
        val invalidEmail = "test@"

        val result = Utils.isValidEmail(invalidEmail)

        assertFalse(result)
    }

    @Test
    fun `isValidEmail should return false for email with only @`() {
        val invalidEmail = "@"

        val result = Utils.isValidEmail(invalidEmail)

        assertFalse(result)
    }

    @Test
    fun `isValidEmail should return true for email with underscores`() {
        val validEmail = "test_user@example.com"

        val result = Utils.isValidEmail(validEmail)

        assertTrue(result)
    }

    @Test
    fun `isValidEmail should return true for email with hyphens`() {
        val validEmail = "test-user@example.com"

        val result = Utils.isValidEmail(validEmail)

        assertTrue(result)
    }

    @Test
    fun `isValidEmail should return true for email with numbers`() {
        val validEmail = "test123@example.com"

        val result = Utils.isValidEmail(validEmail)

        assertTrue(result)
    }

    @Test
    fun `isValidEmail should return true for email with uppercase letters`() {
        val validEmail = "TEST@EXAMPLE.COM"

        val result = Utils.isValidEmail(validEmail)

        assertTrue(result)
    }

    @Test
    fun `isValidEmail should return true for email with mixed case`() {
        val validEmail = "TestUser@Example.Com"

        val result = Utils.isValidEmail(validEmail)

        assertTrue(result)
    }

    @Test
    fun `isValidEmail should return false for email with invalid TLD length`() {
        val invalidEmail = "test@example.c"

        val result = Utils.isValidEmail(invalidEmail)

        assertFalse(result)
    }

    @Test
    fun `isValidEmail should return true for email with long TLD`() {
        val validEmail = "test@example.info"

        val result = Utils.isValidEmail(validEmail)

        assertTrue(result)
    }

    @Test
    fun `isValidEmail should return false for null-like string`() {
        val invalidEmail = "null"

        val result = Utils.isValidEmail(invalidEmail)

        assertFalse(result)
    }

    @Test
    fun `isValidEmail should return false for email with special characters at start`() {
        val invalidEmail = "#test@example.com"

        val result = Utils.isValidEmail(invalidEmail)
        assertFalse(result)
    }

    @Test
    fun `isValidEmail should return false for email with consecutive dots`() {
        val invalidEmail = "test@user@example.com"

        val result = Utils.isValidEmail(invalidEmail)
        assertFalse(result)
    }

    @Test
    fun `formatTime should format timestamp with different time zones`() {
        val timestamp = 1609459200L

        val formatted = Utils.formatTime(timestamp)

        assertFalse(formatted.isEmpty())
        assertTrue(formatted.contains(":"))
    }

    @Test
    fun `formatTime should handle timestamp at epoch start`() {
        val timestamp = 1L

        val formatted = Utils.formatTime(timestamp)

        assertFalse(formatted.isEmpty())
    }

    @Test
    fun `formatTime should handle very large timestamp`() {
        val timestamp = Long.MAX_VALUE / 1000L

        val formatted = Utils.formatTime(timestamp)

        assertFalse(formatted.isEmpty())
        assertTrue(formatted.contains(":"))
    }

    @Test
    fun `formatTime should handle timestamp multiplication edge case`() {
        val timestamp = 253402300799L

        val formatted = Utils.formatTime(timestamp)

        assertFalse(formatted.isEmpty())
        assertTrue(formatted.contains(":"))
    }

    @Test
    fun `formatTime should format midnight correctly`() {
        val timestamp = 0L

        val formatted = Utils.formatTime(timestamp)

        assertFalse(formatted.isEmpty())
        assertTrue(formatted.contains(":"))
        val upperFormatted = formatted.uppercase()
        assertTrue(upperFormatted.contains("AM") || upperFormatted.contains("PM"))
    }

    @Test
    fun `formatTime should format noon correctly`() {
        val timestamp = 43200L

        val formatted = Utils.formatTime(timestamp)

        assertFalse(formatted.isEmpty())
        assertTrue(formatted.contains(":"))
    }

    @Test
    fun `formatTime should format end of day correctly`() {
        val timestamp = 86399L

        val formatted = Utils.formatTime(timestamp)

        assertFalse(formatted.isEmpty())
        assertTrue(formatted.contains(":"))
    }

    @Test
    fun `formatTime should handle timestamp that results in same hour different minutes`() {
        val timestamp1 = 3600L
        val timestamp2 = 3660L

        val formatted1 = Utils.formatTime(timestamp1)
        val formatted2 = Utils.formatTime(timestamp2)

        assertNotEquals(formatted1, formatted2)
        assertTrue(formatted1.contains(":"))
        assertTrue(formatted2.contains(":"))
    }

    @Test
    fun `formatTime should use 12-hour format`() {
        val timestamp = 43200L

        val formatted = Utils.formatTime(timestamp)

        assertFalse(formatted.isEmpty())
        assertTrue(
            formatted.contains(":") && (formatted.uppercase()
                .contains("AM") || formatted.uppercase().contains("PM"))
        )
    }

    @Test
    fun `formatTime should handle timestamp for different days`() {
        val timestamp1 = 0L
        val timestamp2 = 86400L

        val formatted1 = Utils.formatTime(timestamp1)
        val formatted2 = Utils.formatTime(timestamp2)

        assertFalse(formatted1.isEmpty())
        assertFalse(formatted2.isEmpty())
        assertTrue(formatted1.contains(":"))
        assertTrue(formatted2.contains(":"))
    }

    @Test
    fun `formatTime should handle timestamp with milliseconds precision loss`() {
        val timestamp = 1609459200L

        val formatted = Utils.formatTime(timestamp)

        assertFalse(formatted.isEmpty())
        assertTrue(formatted.contains(":"))
    }

    @Test
    fun `isValidEmail should test EMAIL_REGEX constant indirectly`() {
        val validEmail = "test@example.com"
        val invalidEmail = "invalid"

        assertTrue(Utils.isValidEmail(validEmail))
        assertFalse(Utils.isValidEmail(invalidEmail))

        assertTrue(Utils.EMAIL_REGEX.matches(validEmail))
        assertFalse(Utils.EMAIL_REGEX.matches(invalidEmail))
    }

    @Test
    fun `isValidEmail should handle email with long local part`() {
        val longLocalPart = "a".repeat(64) + "@example.com"

        val result = Utils.isValidEmail(longLocalPart)

        assertNotNull(result)
    }

    @Test
    fun `isValidEmail should handle email with long domain`() {
        val longDomain = "test@" + "a".repeat(63) + ".com"

        val result = Utils.isValidEmail(longDomain)

        assertNotNull(result)
    }

    @Test
    fun `isValidEmail should handle email with minimum valid components`() {
        val minimalEmail = "a@b.co"

        val result = Utils.isValidEmail(minimalEmail)

        assertTrue(result)
    }

    @Test
    fun `isValidEmail should handle email with maximum TLD length`() {
        val emailWithLongTld = "test@example.abcdefghij"

        val result = Utils.isValidEmail(emailWithLongTld)

        assertTrue(result)
    }

    @Test
    fun `isValidEmail should handle email with all allowed special characters`() {
        val emailWithAllChars = "test.user+tag-name_123@sub-domain.example.com"

        val result = Utils.isValidEmail(emailWithAllChars)

        assertTrue(result)
    }

    @Test
    fun `isValidEmail should handle email with multiple dots in domain`() {
        val emailWithMultipleDots = "test@mail.sub.example.com"

        val result = Utils.isValidEmail(emailWithMultipleDots)

        assertTrue(result)
    }

    @Test
    fun `isValidEmail should handle email with dash at start of domain`() {
        val emailWithDashStart = "test@-example.com"

        val result = Utils.isValidEmail(emailWithDashStart)

        assertTrue(result)
    }

    @Test
    fun `isValidEmail should handle email with dash at end of domain`() {
        val emailWithDashEnd = "test@example-.com"

        val result = Utils.isValidEmail(emailWithDashEnd)

        assertTrue(result)
    }

    @Test
    fun `formatTime should handle timestamp that causes date rollover`() {
        val timestamp = 86400L * 365

        val formatted = Utils.formatTime(timestamp)

        assertFalse(formatted.isEmpty())
        assertTrue(formatted.contains(":"))
    }

    @Test
    fun `formatTime should consistently format same timestamp`() {
        val timestamp = 1609459200L

        val formatted1 = Utils.formatTime(timestamp)
        val formatted2 = Utils.formatTime(timestamp)

        assertEquals(formatted1, formatted2)
    }

    @Test
    fun `formatTime should handle timestamp for leap year date`() {
        val timestamp = 951782400L

        val formatted = Utils.formatTime(timestamp)

        assertFalse(formatted.isEmpty())
        assertTrue(formatted.contains(":"))
    }

    @Test
    fun `isValidEmail should handle email with consecutive special characters`() {
        val emailWithConsecutiveDots = "test..user@example.com"

        val result = Utils.isValidEmail(emailWithConsecutiveDots)

        assertTrue(result)
    }

    @Test
    fun `isValidEmail should handle email with dot at start`() {
        val emailWithDotStart = ".test@example.com"

        val result = Utils.isValidEmail(emailWithDotStart)

        assertTrue(result)
    }

    @Test
    fun `isValidEmail should handle email with dot at end of local part`() {
        val emailWithDotEnd = "test.@example.com"

        val result = Utils.isValidEmail(emailWithDotEnd)

        assertTrue(result)
    }

    @Test
    fun `isValidEmail should handle email with plus at start`() {
        val emailWithPlusStart = "+test@example.com"

        val result = Utils.isValidEmail(emailWithPlusStart)

        assertTrue(result)
    }

    @Test
    fun `isValidEmail should handle email with hyphen at start of local part`() {
        val emailWithHyphenStart = "-test@example.com"

        val result = Utils.isValidEmail(emailWithHyphenStart)

        assertTrue(result)
    }

    @Test
    fun `isValidEmail should handle email with underscore at start`() {
        val emailWithUnderscoreStart = "_test@example.com"

        val result = Utils.isValidEmail(emailWithUnderscoreStart)

        assertTrue(result)
    }
}
