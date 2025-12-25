package com.perennial.weather.ui.auth.register

import android.content.Context
import androidx.navigation.NavHostController
import org.junit.Assert.*
import com.perennial.weather.R
import com.perennial.weather.domain.model.AuthError
import com.perennial.weather.domain.model.FormField
import com.perennial.weather.domain.model.Result
import com.perennial.weather.domain.usecase.RegisterUseCase
import com.perennial.weather.utils.TestMainDispatcher
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever

@RunWith(MockitoJUnitRunner::class)
class RegisterViewModelTest {

    @Mock
    private lateinit var registerUseCase: RegisterUseCase

    @Mock
    private lateinit var navController: NavHostController

    @Mock
    private lateinit var context: Context

    private lateinit var registerViewModel: RegisterViewModel

    @Before
    fun setup() {
        TestMainDispatcher.setup()
        registerViewModel = RegisterViewModel(registerUseCase)
        whenever(navController.context).thenReturn(context)
    }

    @After
    fun tearDown() {
        TestMainDispatcher.teardown()
    }

    @Test
    fun `onNameChange should update name and clear field error`() = runTest {
        val newName = "Test"

        registerViewModel.onNameChange(newName)

        assertEquals(newName, registerViewModel.name.first())
    }

    @Test
    fun `onEmailChange should update email and clear field error`() = runTest {
        val newEmail = "test@example.com"

        registerViewModel.onEmailChange(newEmail)

        assertEquals(newEmail, registerViewModel.email.first())
    }

    @Test
    fun `onPasswordChange should update password and clear field error`() = runTest {
        val newPassword = "password123"

        registerViewModel.onPasswordChange(newPassword)

        assertEquals(newPassword, registerViewModel.password.first())
    }

    @Test
    fun `onConfirmPasswordChange should update confirm password and clear field error`() = runTest {
        val newConfirmPassword = "password123"

        registerViewModel.onConfirmPasswordChange(newConfirmPassword)

        assertEquals(newConfirmPassword, registerViewModel.confirmPassword.first())
    }

    @Test
    fun `onRegisterClick should call onSuccess when registration succeeds`() =
        runTest(TestMainDispatcher.getDispatcher()) {
            val name = "Test"
            val email = "test@example.com"
            val password = "password123"
            val confirmPassword = "password123"
            registerViewModel.onNameChange(name)
            registerViewModel.onEmailChange(email)
            registerViewModel.onPasswordChange(password)
            registerViewModel.onConfirmPasswordChange(confirmPassword)
            whenever(
                registerUseCase(
                    name,
                    email,
                    password,
                    confirmPassword
                )
            ).thenReturn(Result.Success(true))
            var successCalled = false

            registerViewModel.onRegisterClick(navController) {
                successCalled = true
            }
            testScheduler.advanceUntilIdle()

            assertTrue(successCalled)
        }

    @Test
    fun `onRegisterClick should set field error when registration fails`() =
        runTest(TestMainDispatcher.getDispatcher()) {
            val name = "Test"
            val email = "test@example.com"
            val password = "password123"
            val confirmPassword = "password123"
            registerViewModel.onNameChange(name)
            registerViewModel.onEmailChange(email)
            registerViewModel.onPasswordChange(password)
            registerViewModel.onConfirmPasswordChange(confirmPassword)
            val errorMessage = "User already exists"
            whenever(registerUseCase(name, email, password, confirmPassword))
                .thenReturn(Result.Error(AuthError(R.string.user_already_exists)))
            whenever(context.getString(R.string.user_already_exists)).thenReturn(errorMessage)

            registerViewModel.onRegisterClick(navController) {}
            testScheduler.advanceUntilIdle()

            val fieldError = registerViewModel.fieldError.first()
            assertNotNull(fieldError)
            assertEquals(errorMessage, fieldError?.message)
            assertEquals(FormField.EMAIL, fieldError?.field)
        }

    @Test
    fun `onRegisterClick should set loading state correctly`() =
        runTest(TestMainDispatcher.getDispatcher()) {
            registerViewModel.onNameChange("Test")
            registerViewModel.onEmailChange("test@example.com")
            registerViewModel.onPasswordChange("password123")
            registerViewModel.onConfirmPasswordChange("password123")
            whenever(registerUseCase(any(), any(), any(), any())).thenReturn(Result.Success(true))

            registerViewModel.onRegisterClick(navController) {}
            testScheduler.advanceUntilIdle()

            assertFalse(registerViewModel.isLoading.first())
        }

    @Test
    fun onPasswordChange_shouldClearConfirmPasswordErrorWhenPasswordsMatch() =
        runTest(TestMainDispatcher.getDispatcher()) {
            registerViewModel.onConfirmPasswordChange("password123")
            registerViewModel.onPasswordChange("different")
            whenever(registerUseCase("", "", "different", "password123"))
                .thenReturn(Result.Error(AuthError(R.string.enter_confirm_password)))
            whenever(context.getString(R.string.enter_confirm_password)).thenReturn("Passwords don't match")
            registerViewModel.onRegisterClick(navController) {}
            testScheduler.advanceUntilIdle()

            assertNotNull(registerViewModel.fieldError.first())
            assertEquals(FormField.CONFIRM_PASSWORD, registerViewModel.fieldError.first()?.field)

            registerViewModel.onPasswordChange("password123")

            assertNull(registerViewModel.fieldError.first())
        }

    @Test
    fun onNameChange_shouldClearFieldErrorWhenFieldMatchesName() =
        runTest(TestMainDispatcher.getDispatcher()) {
            whenever(registerUseCase("", "", "", ""))
                .thenReturn(Result.Error(AuthError(R.string.enter_name)))
            whenever(context.getString(R.string.enter_name)).thenReturn("Name required")
            registerViewModel.onRegisterClick(navController) {}
            testScheduler.advanceUntilIdle()

            assertNotNull(registerViewModel.fieldError.first())
            assertEquals(FormField.NAME, registerViewModel.fieldError.first()?.field)

            registerViewModel.onNameChange("New Name")

            assertNull(registerViewModel.fieldError.first())
        }

    @Test
    fun onEmailChange_shouldClearFieldErrorWhenFieldMatchesEmail() =
        runTest(TestMainDispatcher.getDispatcher()) {
            registerViewModel.onNameChange("Test")
            registerViewModel.onEmailChange("")
            whenever(registerUseCase("Test", "", "", ""))
                .thenReturn(Result.Error(AuthError(R.string.enter_email)))
            whenever(context.getString(R.string.enter_email)).thenReturn("Email required")
            registerViewModel.onRegisterClick(navController) {}
            testScheduler.advanceUntilIdle()

            assertNotNull(registerViewModel.fieldError.first())
            assertEquals(FormField.EMAIL, registerViewModel.fieldError.first()?.field)

            registerViewModel.onEmailChange("new@example.com")

            assertNull(registerViewModel.fieldError.first())
        }

    @Test
    fun onPasswordChange_shouldClearFieldErrorWhenFieldMatchesPassword() =
        runTest(TestMainDispatcher.getDispatcher()) {
            registerViewModel.onNameChange("Test")
            registerViewModel.onEmailChange("test@example.com")
            registerViewModel.onPasswordChange("")
            whenever(registerUseCase("Test", "test@example.com", "", ""))
                .thenReturn(Result.Error(AuthError(R.string.enter_password)))
            whenever(context.getString(R.string.enter_password)).thenReturn("Password required")
            registerViewModel.onRegisterClick(navController) {}
            testScheduler.advanceUntilIdle()

            assertNotNull(registerViewModel.fieldError.first())
            assertEquals(FormField.PASSWORD, registerViewModel.fieldError.first()?.field)

            registerViewModel.onPasswordChange("newpassword")

            assertNull(registerViewModel.fieldError.first())
        }

    @Test
    fun onConfirmPasswordChange_shouldClearFieldErrorWhenFieldMatchesConfirmPassword() =
        runTest(TestMainDispatcher.getDispatcher()) {
            registerViewModel.onNameChange("Test")
            registerViewModel.onEmailChange("test@example.com")
            registerViewModel.onPasswordChange("password123")
            registerViewModel.onConfirmPasswordChange("")
            whenever(registerUseCase("Test", "test@example.com", "password123", ""))
                .thenReturn(Result.Error(AuthError(R.string.enter_confirm_password)))
            whenever(context.getString(R.string.enter_confirm_password)).thenReturn("Confirm password required")
            registerViewModel.onRegisterClick(navController) {}
            testScheduler.advanceUntilIdle()

            assertNotNull(registerViewModel.fieldError.first())
            assertEquals(FormField.CONFIRM_PASSWORD, registerViewModel.fieldError.first()?.field)

            registerViewModel.onConfirmPasswordChange("password123")

            assertNull(registerViewModel.fieldError.first())
        }

    @Test
    fun onRegisterClick_shouldSetNameFieldErrorForNameErrors() =
        runTest(TestMainDispatcher.getDispatcher()) {
            registerViewModel.onNameChange("")
            registerViewModel.onEmailChange("test@example.com")
            registerViewModel.onPasswordChange("password123")
            registerViewModel.onConfirmPasswordChange("password123")
            val errorMessage = "Name is required"
            whenever(registerUseCase("", "test@example.com", "password123", "password123"))
                .thenReturn(Result.Error(AuthError(R.string.enter_name)))
            whenever(context.getString(R.string.enter_name)).thenReturn(errorMessage)

            registerViewModel.onRegisterClick(navController) {}
            testScheduler.advanceUntilIdle()

            val fieldError = registerViewModel.fieldError.first()
            assertNotNull(fieldError)
            assertEquals(errorMessage, fieldError?.message)
            assertEquals(FormField.NAME, fieldError?.field)
        }

    @Test
    fun onRegisterClick_shouldSetPasswordFieldErrorForPasswordErrors() =
        runTest(TestMainDispatcher.getDispatcher()) {
            registerViewModel.onNameChange("Test")
            registerViewModel.onEmailChange("test@example.com")
            registerViewModel.onPasswordChange("short")
            registerViewModel.onConfirmPasswordChange("short")
            val errorMessage = "Invalid password"
            whenever(registerUseCase("Test", "test@example.com", "short", "short"))
                .thenReturn(Result.Error(AuthError(R.string.enter_valid_password)))
            whenever(context.getString(R.string.enter_valid_password)).thenReturn(errorMessage)

            registerViewModel.onRegisterClick(navController) {}
            testScheduler.advanceUntilIdle()

            val fieldError = registerViewModel.fieldError.first()
            assertNotNull(fieldError)
            assertEquals(errorMessage, fieldError?.message)
            assertEquals(FormField.PASSWORD, fieldError?.field)
        }

    @Test
    fun onRegisterClick_shouldSetConfirmPasswordFieldErrorForConfirmPasswordErrors() =
        runTest(TestMainDispatcher.getDispatcher()) {
            registerViewModel.onNameChange("Test")
            registerViewModel.onEmailChange("test@example.com")
            registerViewModel.onPasswordChange("password123")
            registerViewModel.onConfirmPasswordChange("different")
            val errorMessage = "Passwords don't match"
            whenever(registerUseCase("Test", "test@example.com", "password123", "different"))
                .thenReturn(Result.Error(AuthError(R.string.enter_valid_confirm_password)))
            whenever(context.getString(R.string.enter_valid_confirm_password)).thenReturn(
                errorMessage
            )

            registerViewModel.onRegisterClick(navController) {}
            testScheduler.advanceUntilIdle()

            val fieldError = registerViewModel.fieldError.first()
            assertNotNull(fieldError)
            assertEquals(errorMessage, fieldError?.message)
            assertEquals(FormField.CONFIRM_PASSWORD, fieldError?.field)
        }

    @Test
    fun initialState_shouldHaveEmptyValues() = runTest {
        assertEquals("", registerViewModel.name.first())
        assertEquals("", registerViewModel.email.first())
        assertEquals("", registerViewModel.password.first())
        assertEquals("", registerViewModel.confirmPassword.first())
        assertFalse(registerViewModel.isLoading.first())
        assertNull(registerViewModel.fieldError.first())
    }

    @Test
    fun onRegisterClick_shouldSetEmailFieldErrorForEnterValidEmail() =
        runTest(TestMainDispatcher.getDispatcher()) {
            registerViewModel.onNameChange("Test")
            registerViewModel.onEmailChange("invalid-email")
            registerViewModel.onPasswordChange("password123")
            registerViewModel.onConfirmPasswordChange("password123")
            val errorMessage = "Enter valid email"
            whenever(registerUseCase("Test", "invalid-email", "password123", "password123"))
                .thenReturn(Result.Error(AuthError(R.string.enter_valid_email)))
            whenever(context.getString(R.string.enter_valid_email)).thenReturn(errorMessage)

            registerViewModel.onRegisterClick(navController) {}
            testScheduler.advanceUntilIdle()

            val fieldError = registerViewModel.fieldError.first()
            assertNotNull(fieldError)
            assertEquals(errorMessage, fieldError?.message)
            assertEquals(FormField.EMAIL, fieldError?.field)
        }

    @Test
    fun onRegisterClick_shouldSetEmailFieldErrorForDefaultCase() =
        runTest(TestMainDispatcher.getDispatcher()) {
            registerViewModel.onNameChange("Test")
            registerViewModel.onEmailChange("test@example.com")
            registerViewModel.onPasswordChange("password123")
            registerViewModel.onConfirmPasswordChange("password123")
            val errorMessage = "Unknown error"
            whenever(registerUseCase("Test", "test@example.com", "password123", "password123"))
                .thenReturn(Result.Error(AuthError(R.string.error_occurred)))
            whenever(context.getString(R.string.error_occurred)).thenReturn(errorMessage)

            registerViewModel.onRegisterClick(navController) {}
            testScheduler.advanceUntilIdle()

            val fieldError = registerViewModel.fieldError.first()
            assertNotNull(fieldError)
            assertEquals(errorMessage, fieldError?.message)
            assertEquals(FormField.EMAIL, fieldError?.field)
        }

    @Test
    fun onNameChange_shouldNotClearFieldErrorWhenFieldDoesNotMatch() =
        runTest(TestMainDispatcher.getDispatcher()) {
            registerViewModel.onNameChange("Test")
            registerViewModel.onEmailChange("")
            whenever(registerUseCase("Test", "", "", ""))
                .thenReturn(Result.Error(AuthError(R.string.enter_email)))
            whenever(context.getString(R.string.enter_email)).thenReturn("Email required")
            registerViewModel.onRegisterClick(navController) {}
            testScheduler.advanceUntilIdle()

            val fieldErrorBefore = registerViewModel.fieldError.first()
            assertNotNull(fieldErrorBefore)
            assertEquals(FormField.EMAIL, fieldErrorBefore?.field)

            registerViewModel.onNameChange("New Name")

            val fieldErrorAfter = registerViewModel.fieldError.first()
            assertNotNull(fieldErrorAfter)
            assertEquals(FormField.EMAIL, fieldErrorAfter?.field)
        }

    @Test
    fun onEmailChange_shouldNotClearFieldErrorWhenFieldDoesNotMatch() =
        runTest(TestMainDispatcher.getDispatcher()) {
            registerViewModel.onNameChange("")
            registerViewModel.onEmailChange("test@example.com")
            whenever(registerUseCase("", "test@example.com", "", ""))
                .thenReturn(Result.Error(AuthError(R.string.enter_name)))
            whenever(context.getString(R.string.enter_name)).thenReturn("Name required")
            registerViewModel.onRegisterClick(navController) {}
            testScheduler.advanceUntilIdle()

            val fieldErrorBefore = registerViewModel.fieldError.first()
            assertNotNull(fieldErrorBefore)
            assertEquals(FormField.NAME, fieldErrorBefore?.field)

            registerViewModel.onEmailChange("new@example.com")

            val fieldErrorAfter = registerViewModel.fieldError.first()
            assertNotNull(fieldErrorAfter)
            assertEquals(FormField.NAME, fieldErrorAfter?.field)
        }

    @Test
    fun onPasswordChange_shouldNotClearFieldErrorWhenFieldDoesNotMatch() =
        runTest(TestMainDispatcher.getDispatcher()) {
            registerViewModel.onNameChange("")
            registerViewModel.onPasswordChange("password123")
            whenever(registerUseCase("", "", "password123", ""))
                .thenReturn(Result.Error(AuthError(R.string.enter_name)))
            whenever(context.getString(R.string.enter_name)).thenReturn("Name required")
            registerViewModel.onRegisterClick(navController) {}
            testScheduler.advanceUntilIdle()

            val fieldErrorBefore = registerViewModel.fieldError.first()
            assertNotNull(fieldErrorBefore)
            assertEquals(FormField.NAME, fieldErrorBefore?.field)

            registerViewModel.onPasswordChange("newpassword")

            val fieldErrorAfter = registerViewModel.fieldError.first()
            assertNotNull(fieldErrorAfter)
            assertEquals(FormField.NAME, fieldErrorAfter?.field)
        }

    @Test
    fun onPasswordChange_shouldNotClearConfirmPasswordErrorWhenPasswordsDontMatch() =
        runTest(TestMainDispatcher.getDispatcher()) {
            registerViewModel.onPasswordChange("password123")
            registerViewModel.onConfirmPasswordChange("different")
            whenever(registerUseCase("", "", "password123", "different"))
                .thenReturn(Result.Error(AuthError(R.string.enter_valid_confirm_password)))
            whenever(context.getString(R.string.enter_valid_confirm_password)).thenReturn("Passwords don't match")
            registerViewModel.onRegisterClick(navController) {}
            testScheduler.advanceUntilIdle()

            val fieldErrorBefore = registerViewModel.fieldError.first()
            assertNotNull(fieldErrorBefore)
            assertEquals(FormField.CONFIRM_PASSWORD, fieldErrorBefore?.field)

            registerViewModel.onPasswordChange("stilldifferent")

            val fieldErrorAfter = registerViewModel.fieldError.first()
            assertNotNull(fieldErrorAfter)
            assertEquals(FormField.CONFIRM_PASSWORD, fieldErrorAfter?.field)
        }

    @Test
    fun onConfirmPasswordChange_shouldNotClearFieldErrorWhenFieldDoesNotMatch() =
        runTest(TestMainDispatcher.getDispatcher()) {
            registerViewModel.onNameChange("")
            registerViewModel.onConfirmPasswordChange("password123")
            whenever(registerUseCase("", "", "", "password123"))
                .thenReturn(Result.Error(AuthError(R.string.enter_name)))
            whenever(context.getString(R.string.enter_name)).thenReturn("Name required")
            registerViewModel.onRegisterClick(navController) {}
            testScheduler.advanceUntilIdle()

            val fieldErrorBefore = registerViewModel.fieldError.first()
            assertNotNull(fieldErrorBefore)
            assertEquals(FormField.NAME, fieldErrorBefore?.field)

            registerViewModel.onConfirmPasswordChange("newpassword")

            val fieldErrorAfter = registerViewModel.fieldError.first()
            assertNotNull(fieldErrorAfter)
            assertEquals(FormField.NAME, fieldErrorAfter?.field)
        }

    @Test
    fun onNameChange_shouldNotClearFieldErrorWhenNoErrorExists() = runTest {
        registerViewModel.onNameChange("Test")

        assertNull(registerViewModel.fieldError.first())
    }

    @Test
    fun onEmailChange_shouldNotClearFieldErrorWhenNoErrorExists() = runTest {
        registerViewModel.onEmailChange("test@example.com")

        assertNull(registerViewModel.fieldError.first())
    }

    @Test
    fun onPasswordChange_shouldNotClearFieldErrorWhenNoErrorExists() = runTest {
        registerViewModel.onPasswordChange("password123")

        assertNull(registerViewModel.fieldError.first())
    }

    @Test
    fun onConfirmPasswordChange_shouldNotClearFieldErrorWhenNoErrorExists() = runTest {
        registerViewModel.onConfirmPasswordChange("password123")

        assertNull(registerViewModel.fieldError.first())
    }

    @Test
    fun onRegisterClick_shouldClearFieldErrorBeforeAttemptingRegistration() =
        runTest(TestMainDispatcher.getDispatcher()) {
            registerViewModel.onNameChange("")
            whenever(registerUseCase("", "", "", ""))
                .thenReturn(Result.Error(AuthError(R.string.enter_name)))
            whenever(context.getString(R.string.enter_name)).thenReturn("Name required")
            registerViewModel.onRegisterClick(navController) {}
            testScheduler.advanceUntilIdle()

            val fieldErrorBefore = registerViewModel.fieldError.first()
            assertNotNull(fieldErrorBefore)

            registerViewModel.onNameChange("Test")
            registerViewModel.onEmailChange("test@example.com")
            registerViewModel.onPasswordChange("password123")
            registerViewModel.onConfirmPasswordChange("password123")
            whenever(registerUseCase("Test", "test@example.com", "password123", "password123"))
                .thenReturn(Result.Success(true))

            registerViewModel.onRegisterClick(navController) {}
            testScheduler.advanceUntilIdle()

            val fieldErrorAfter = registerViewModel.fieldError.first()
            assertNull(fieldErrorAfter)
        }
}

