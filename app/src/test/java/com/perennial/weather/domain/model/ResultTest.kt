package com.perennial.weather.domain.model

import org.junit.Assert.*
import org.junit.Test

class ResultTest {

    @Test
    fun `Success should contain data`() {
        val data = "test data"

        val result = Result.Success(data)

        assertEquals(data, result.data)
    }

    @Test
    fun `Error should contain AuthError`() {
        val authError = AuthError(1, "Error message")

        val result = Result.Error(authError)

        assertEquals(authError, result.error)
    }

    @Test
    fun `Success should work with different data types`() {
        val stringData = "string"
        val intData = 42
        val booleanData = true

        val stringResult = Result.Success(stringData)
        val intResult = Result.Success(intData)
        val booleanResult = Result.Success(booleanData)

        assertEquals(stringData, stringResult.data)
        assertEquals(intData, intResult.data)
        assertEquals(booleanData, booleanResult.data)
    }

    @Test
    fun `Error should work with different AuthError configurations`() {
        val errorWithMessage = AuthError(1, "Error message")
        val errorWithoutMessage = AuthError(1)

        val resultWithMessage = Result.Error(errorWithMessage)
        
        val resultWithoutMessage = Result.Error(errorWithoutMessage)

        assertEquals("Error message", resultWithMessage.error.message)

        assertNull(resultWithoutMessage.error.message)
    }
}

