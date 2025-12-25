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
class LoginUseCaseTest {

    @Mock
    private lateinit var authRepository: AuthRepository

    private lateinit var loginUseCase: LoginUseCase

    @Before
    fun setup() {
        loginUseCase = LoginUseCase(authRepository)
    }

    @Test
    fun `invoke should return Success when login is successful`() = runTest {
        val email = "test@example.com"
        val password = "password123"
        whenever(authRepository.login(email.trim(), password.trim())).thenReturn(true)

        val result = loginUseCase(email, password)

        assertTrue((result as Result.Success).data)
    }

    @Test
    fun `invoke should return Error when login fails`() = runTest {
        val email = "test@example.com"
        val password = "password123"
        whenever(authRepository.login(email.trim(), password.trim())).thenReturn(false)

        val result = loginUseCase(email, password)

        assertTrue(result is Result.Error)
        assertEquals(R.string.invalid_credentials, (result as Result.Error).error.messageResId)
    }

    @Test
    fun `invoke should return Error for invalid email`() = runTest {
        val email = "invalid-email"
        val password = "password123"

        val result = loginUseCase(email, password)

        assertTrue(result is Result.Error)
        assertEquals(R.string.enter_valid_email, (result as Result.Error).error.messageResId)
    }

    @Test
    fun `invoke should return Error for blank email`() = runTest {
        val email = ""
        val password = "password123"

        val result = loginUseCase(email, password)

        assertTrue(result is Result.Error)
        assertEquals(R.string.enter_email, (result as Result.Error).error.messageResId)
    }

    @Test
    fun `invoke should return Error for blank password`() = runTest {
        val email = "test@example.com"
        val password = ""

        val result = loginUseCase(email, password)

        assertTrue(result is Result.Error)
        assertEquals(R.string.enter_password, (result as Result.Error).error.messageResId)
    }

    @Test
    fun `invoke should return Error for short password`() = runTest {
        val email = "test@example.com"
        val password = "pass12"

        val result = loginUseCase(email, password)

        assertTrue(result is Result.Error)
        assertEquals(R.string.enter_valid_password, (result as Result.Error).error.messageResId)
    }

    @Test
    fun `invoke should return Error for long password`() = runTest {
        val email = "test@example.com"
        val password = "password123456789"

        val result = loginUseCase(email, password)

        assertTrue(result is Result.Error)
        assertEquals(R.string.enter_valid_password, (result as Result.Error).error.messageResId)
    }

    @Test
    fun `invoke should trim email and password when calling repository`() = runTest {
        val email = "test@example.com"
        val password = "password123"
        whenever(authRepository.login(email.trim(), password.trim())).thenReturn(true)

        val result = loginUseCase(email, password)

        assertTrue(result is Result.Success)

        verify(authRepository).login(email.trim(), password.trim())
    }

    @Test
    fun `invoke should return Error for email with leading and trailing spaces`() = runTest {
        val email = "  test@example.com  "
        val password = "password123"

        val result = loginUseCase(email, password)

        assertTrue(result is Result.Error)
    }

    @Test
    fun `invoke should return Error when repository throws exception`() = runTest {
        val email = "test@example.com"
        val password = "password123"
        val exception = RuntimeException("Network error")
        whenever(authRepository.login(email.trim(), password.trim())).thenThrow(exception)

        val result = loginUseCase(email, password)

        assertTrue(result is Result.Error)
        assertEquals(R.string.error_occurred, (result as Result.Error).error.messageResId)
        assertEquals(exception.message, result.error.message)
    }
}

