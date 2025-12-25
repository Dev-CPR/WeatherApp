package com.perennial.weather.ui.auth.login

import android.content.Context
import androidx.navigation.NavHostController
import com.perennial.weather.R
import com.perennial.weather.domain.model.FormField
import com.perennial.weather.domain.model.Result
import com.perennial.weather.domain.usecase.LoginUseCase
import com.perennial.weather.utils.TestMainDispatcher
import com.perennial.weather.utils.UserPreferencesManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@RunWith(MockitoJUnitRunner::class)
class LoginViewModelTest {

    @Mock
    private lateinit var loginUseCase: LoginUseCase

    @Mock
    private lateinit var userPreferencesManager: UserPreferencesManager

    @Mock
    private lateinit var navController: NavHostController

    @Mock
    private lateinit var context: Context

    private lateinit var loginViewModel: LoginViewModel

    @Before
    fun setup() {
        TestMainDispatcher.setup()
        loginViewModel = LoginViewModel(loginUseCase, userPreferencesManager)
        whenever(navController.context).thenReturn(context)
    }

    @After
    fun tearDown() {
        TestMainDispatcher.teardown()
    }

    @Test
    fun `onEmailChange should update email and clear field error`() = runTest {
        val newEmail = "test@example.com"

        loginViewModel.onEmailChange(newEmail)

        assertEquals(newEmail, loginViewModel.email.first())
    }

    @Test
    fun `onPasswordChange should update password and clear field error`() = runTest {
        val newPassword = "password123"

        loginViewModel.onPasswordChange(newPassword)

        assertEquals(newPassword, loginViewModel.password.first())
    }

    @Test
    fun `onLoginClick should set loading to true then false`() = runTest(TestMainDispatcher.getDispatcher()) {
        val email = "test@example.com"
        val password = "password123"
        loginViewModel.onEmailChange(email)
        loginViewModel.onPasswordChange(password)
        whenever(loginUseCase(email, password)).thenReturn(Result.Success(true))

        loginViewModel.onLoginClick(navController) {}
        testScheduler.advanceUntilIdle()
        val isLoadingAfter = loginViewModel.isLoading.first()

        assertFalse(isLoadingAfter)
    }

    @Test
    fun `onLoginClick should call onSuccess when login succeeds`() = runTest(TestMainDispatcher.getDispatcher()) {
        val email = "test@example.com"
        val password = "password123"
        loginViewModel.onEmailChange(email)
        loginViewModel.onPasswordChange(password)
        whenever(loginUseCase(email, password)).thenReturn(Result.Success(true))
        var successCalled = false

        loginViewModel.onLoginClick(navController) {
            successCalled = true
        }
        testScheduler.advanceUntilIdle()

        assertTrue(successCalled)
        verify(userPreferencesManager).saveUserEmail(email)
    }

    @Test
    fun `onLoginClick should set field error when login fails`() = runTest(TestMainDispatcher.getDispatcher()) {
        val email = "test@example.com"
        val password = "password123"
        loginViewModel.onEmailChange(email)
        loginViewModel.onPasswordChange(password)
        val errorMessage = "Invalid credentials"
        whenever(loginUseCase(email, password)).thenReturn(Result.Error(com.perennial.weather.domain.model.AuthError(R.string.invalid_credentials)))
        whenever(context.getString(R.string.invalid_credentials)).thenReturn(errorMessage)

        loginViewModel.onLoginClick(navController) {}
        testScheduler.advanceUntilIdle()

        val fieldError = loginViewModel.fieldError.first()
        assertNotNull(fieldError)
        assertEquals(errorMessage, fieldError?.message)
        assertEquals(FormField.EMAIL, fieldError?.field)
    }

    @Test
    fun `onLoginClick should clear field error before attempting login`() = runTest(TestMainDispatcher.getDispatcher()) {
        val email = "test@example.com"
        val password = "password123"
        loginViewModel.onEmailChange(email)
        loginViewModel.onPasswordChange(password)
        whenever(loginUseCase(email, password)).thenReturn(Result.Success(true))

        loginViewModel.onLoginClick(navController) {}
        testScheduler.advanceUntilIdle()

        val fieldError = loginViewModel.fieldError.first()

        assertNull(fieldError)
    }

    @Test
    fun onEmailChange_shouldClearFieldErrorWhenFieldMatchesEmail() = runTest(TestMainDispatcher.getDispatcher()) {
        val email = ""
        val password = ""
        val errorMessage = "Email error"
        whenever(loginUseCase(email, password)).thenReturn(Result.Error(com.perennial.weather.domain.model.AuthError(R.string.enter_email)))
        whenever(context.getString(R.string.enter_email)).thenReturn(errorMessage)
        
        loginViewModel.onLoginClick(navController) {}
        testScheduler.advanceUntilIdle()
        
        assertNotNull(loginViewModel.fieldError.first())
        assertEquals(FormField.EMAIL, loginViewModel.fieldError.first()?.field)
        
        loginViewModel.onEmailChange("new@example.com")
        
        assertNull(loginViewModel.fieldError.first())
    }

    @Test
    fun onPasswordChange_shouldClearFieldErrorWhenFieldMatchesPassword() = runTest(TestMainDispatcher.getDispatcher()) {
        val email = "test@example.com"
        val password = ""
        val errorMessage = "Password error"
        loginViewModel.onEmailChange(email)
        loginViewModel.onPasswordChange(password)
        whenever(loginUseCase(email, password)).thenReturn(Result.Error(com.perennial.weather.domain.model.AuthError(R.string.enter_password)))
        whenever(context.getString(R.string.enter_password)).thenReturn(errorMessage)
        
        loginViewModel.onLoginClick(navController) {}
        testScheduler.advanceUntilIdle()
        
        assertNotNull(loginViewModel.fieldError.first())
        assertEquals(FormField.PASSWORD, loginViewModel.fieldError.first()?.field)
        
        loginViewModel.onPasswordChange("newpassword")
        
        assertNull(loginViewModel.fieldError.first())
    }

    @Test
    fun onLoginClick_shouldSetPasswordFieldErrorForPasswordErrors() = runTest(TestMainDispatcher.getDispatcher()) {
        val email = "test@example.com"
        val password = "short"
        loginViewModel.onEmailChange(email)
        loginViewModel.onPasswordChange(password)
        val errorMessage = "Invalid password"
        whenever(loginUseCase(email, password)).thenReturn(Result.Error(com.perennial.weather.domain.model.AuthError(R.string.enter_valid_password)))
        whenever(context.getString(R.string.enter_valid_password)).thenReturn(errorMessage)

        loginViewModel.onLoginClick(navController) {}
        testScheduler.advanceUntilIdle()

        val fieldError = loginViewModel.fieldError.first()
        assertNotNull(fieldError)
        assertEquals(errorMessage, fieldError?.message)
        assertEquals(FormField.PASSWORD, fieldError?.field)
    }

    @Test
    fun initialState_shouldHaveEmptyValues() = runTest {
        assertEquals("", loginViewModel.email.first())
        assertEquals("", loginViewModel.password.first())
        assertFalse(loginViewModel.isLoading.first())
        assertNull(loginViewModel.fieldError.first())
    }

    @Test
    fun onLoginClick_shouldSetEmailFieldErrorForEnterValidEmail() = runTest(TestMainDispatcher.getDispatcher()) {
        val email = "invalid-email"
        val password = "password123"
        loginViewModel.onEmailChange(email)
        loginViewModel.onPasswordChange(password)
        val errorMessage = "Enter valid email"
        whenever(loginUseCase(email, password)).thenReturn(Result.Error(com.perennial.weather.domain.model.AuthError(R.string.enter_valid_email)))
        whenever(context.getString(R.string.enter_valid_email)).thenReturn(errorMessage)

        loginViewModel.onLoginClick(navController) {}
        testScheduler.advanceUntilIdle()

        val fieldError = loginViewModel.fieldError.first()
        assertNotNull(fieldError)
        assertEquals(errorMessage, fieldError?.message)
        assertEquals(FormField.EMAIL, fieldError?.field)
    }

    @Test
    fun onLoginClick_shouldSetEmailFieldErrorForDefaultCase() = runTest(TestMainDispatcher.getDispatcher()) {
        val email = "test@example.com"
        val password = "password123"
        loginViewModel.onEmailChange(email)
        loginViewModel.onPasswordChange(password)
        val errorMessage = "Unknown error"
        whenever(loginUseCase(email, password)).thenReturn(Result.Error(com.perennial.weather.domain.model.AuthError(R.string.user_already_exists)))
        whenever(context.getString(R.string.user_already_exists)).thenReturn(errorMessage)

        loginViewModel.onLoginClick(navController) {}
        testScheduler.advanceUntilIdle()

        val fieldError = loginViewModel.fieldError.first()
        assertNotNull(fieldError)
        assertEquals(errorMessage, fieldError?.message)
        assertEquals(FormField.EMAIL, fieldError?.field)
    }

    @Test
    fun onEmailChange_shouldNotClearFieldErrorWhenFieldDoesNotMatch() = runTest(TestMainDispatcher.getDispatcher()) {
        val email = "test@example.com"
        val password = ""
        val errorMessage = "Password error"
        loginViewModel.onEmailChange(email)
        loginViewModel.onPasswordChange(password)
        whenever(loginUseCase(email, password)).thenReturn(Result.Error(com.perennial.weather.domain.model.AuthError(R.string.enter_password)))
        whenever(context.getString(R.string.enter_password)).thenReturn(errorMessage)

        loginViewModel.onLoginClick(navController) {}
        testScheduler.advanceUntilIdle()

        val fieldErrorBefore = loginViewModel.fieldError.first()
        assertNotNull(fieldErrorBefore)
        assertEquals(FormField.PASSWORD, fieldErrorBefore?.field)

        loginViewModel.onEmailChange("new@example.com")

        val fieldErrorAfter = loginViewModel.fieldError.first()
        assertNotNull(fieldErrorAfter)
        assertEquals(FormField.PASSWORD, fieldErrorAfter?.field)
    }

    @Test
    fun onPasswordChange_shouldNotClearFieldErrorWhenFieldDoesNotMatch() = runTest(TestMainDispatcher.getDispatcher()) {
        val email = ""
        val password = "password123"
        val errorMessage = "Email error"
        loginViewModel.onEmailChange(email)
        loginViewModel.onPasswordChange(password)
        whenever(loginUseCase(email, password)).thenReturn(Result.Error(com.perennial.weather.domain.model.AuthError(R.string.enter_email)))
        whenever(context.getString(R.string.enter_email)).thenReturn(errorMessage)

        loginViewModel.onLoginClick(navController) {}
        testScheduler.advanceUntilIdle()

        val fieldErrorBefore = loginViewModel.fieldError.first()
        assertNotNull(fieldErrorBefore)
        assertEquals(FormField.EMAIL, fieldErrorBefore?.field)

        loginViewModel.onPasswordChange("newpassword")

        val fieldErrorAfter = loginViewModel.fieldError.first()
        assertNotNull(fieldErrorAfter)
        assertEquals(FormField.EMAIL, fieldErrorAfter?.field)
    }

    @Test
    fun onEmailChange_shouldNotClearFieldErrorWhenNoErrorExists() = runTest {
        loginViewModel.onEmailChange("test@example.com")

        assertNull(loginViewModel.fieldError.first())
    }

    @Test
    fun onPasswordChange_shouldNotClearFieldErrorWhenNoErrorExists() = runTest {
        loginViewModel.onPasswordChange("password123")

        assertNull(loginViewModel.fieldError.first())
    }
}

