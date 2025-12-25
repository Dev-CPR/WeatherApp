package com.perennial.weather.domain.usecase

import org.junit.Assert.*
import com.perennial.weather.R
import com.perennial.weather.domain.model.Result
import com.perennial.weather.domain.repository.AuthRepository
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@RunWith(MockitoJUnitRunner::class)
class RegisterUseCaseTest {

    @Mock
    private lateinit var authRepository: AuthRepository

    private lateinit var registerUseCase: RegisterUseCase

    @Before
    fun setup() {
        registerUseCase = RegisterUseCase(authRepository)
    }

    @Test
    fun `invoke should return Success when registration is successful`() = runTest {
        val name = "Test"
        val email = "test@example.com"
        val password = "password123"
        val confirmPassword = "password123"
        whenever(authRepository.register(name.trim(), email.trim(), password.trim())).thenReturn(true)

        val result = registerUseCase(name, email, password, confirmPassword)

        assertTrue(result is Result.Success)
        assertTrue((result as Result.Success).data)
    }

    @Test
    fun `invoke should return Error when user already exists`() = runTest {
        val name = "Test"
        val email = "test@example.com"
        val password = "password123"
        val confirmPassword = "password123"
        whenever(authRepository.register(name.trim(), email.trim(), password.trim())).thenReturn(false)

        val result = registerUseCase(name, email, password, confirmPassword)

        assertTrue(result is Result.Error)
        assertEquals(R.string.user_already_exists, (result as Result.Error).error.messageResId)
    }

    @Test
    fun `invoke should return Error for blank name`() = runTest {
        val name = ""
        val email = "test@example.com"
        val password = "password123"
        val confirmPassword = "password123"

        val result = registerUseCase(name, email, password, confirmPassword)

        assertTrue(result is Result.Error)
        assertEquals(R.string.enter_name, (result as Result.Error).error.messageResId)
    }

    @Test
    fun `invoke should return Error for invalid email`() = runTest {
        val name = "Test"
        val email = "invalid-email"
        val password = "password123"
        val confirmPassword = "password123"

        val result = registerUseCase(name, email, password, confirmPassword)

        assertTrue(result is Result.Error)
        assertEquals(R.string.enter_valid_email, (result as Result.Error).error.messageResId)
    }

    @Test
    fun `invoke should return Error for blank password`() = runTest {
        val name = "Test"
        val email = "test@example.com"
        val password = ""
        val confirmPassword = ""

        val result = registerUseCase(name, email, password, confirmPassword)

        assertTrue(result is Result.Error)
        assertEquals(R.string.enter_password, (result as Result.Error).error.messageResId)
    }

    @Test
    fun `invoke should return Error when passwords do not match`() = runTest {
        val name = "Test"
        val email = "test@example.com"
        val password = "password123"
        val confirmPassword = "password456"

        val result = registerUseCase(name, email, password, confirmPassword)

        assertTrue(result is Result.Error)
        assertEquals(R.string.enter_valid_confirm_password, (result as Result.Error).error.messageResId)
    }

    @Test
    fun `invoke should return Error for blank confirm password`() = runTest {
        val name = "Test"
        val email = "test@example.com"
        val password = "password123"
        val confirmPassword = ""

        val result = registerUseCase(name, email, password, confirmPassword)

        assertTrue(result is Result.Error)
        assertEquals(R.string.enter_confirm_password, (result as Result.Error).error.messageResId)
    }

    @Test
    fun `invoke should trim all inputs when calling repository`() = runTest {
        val name = "Test"
        val email = "test@example.com"
        val password = "password123"
        val confirmPassword = "password123"
        whenever(authRepository.register(name.trim(), email.trim(), password.trim())).thenReturn(true)

        val result = registerUseCase(name, email, password, confirmPassword)

        assertTrue(result is Result.Success)

        verify(authRepository).register(name.trim(), email.trim(), password.trim())
    }

    @Test
    fun `invoke should return Error for email with leading and trailing spaces`() = runTest {
        val name = "Test"
        val email = "  test@example.com  "
        val password = "password123"
        val confirmPassword = "password123"

        val result = registerUseCase(name, email, password, confirmPassword)

        assertTrue(result is Result.Error)
        assertEquals(R.string.enter_valid_email, (result as Result.Error).error.messageResId)
    }

    @Test
    fun `invoke should return Error when repository throws exception`() = runTest {
        val name = "Test"
        val email = "test@example.com"
        val password = "password123"
        val confirmPassword = "password123"
        val exception = RuntimeException("Database error")
        whenever(authRepository.register(name.trim(), email.trim(), password.trim())).thenThrow(exception)

        val result = registerUseCase(name, email, password, confirmPassword)

        assertTrue(result is Result.Error)
        assertEquals(R.string.error_occurred, (result as Result.Error).error.messageResId)
        assertEquals(exception.message, result.error.message)
    }
}

