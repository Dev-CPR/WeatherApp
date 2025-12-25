package com.perennial.weather.ui.auth.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.perennial.weather.R
import com.perennial.weather.domain.model.Result
import com.perennial.weather.domain.usecase.LoginUseCase
import com.perennial.weather.domain.model.FieldError
import com.perennial.weather.domain.model.FormField
import com.perennial.weather.utils.UserPreferencesManager
import com.perennial.weather.utils.getMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val userPreferencesManager: UserPreferencesManager
): ViewModel() {
    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _fieldError = MutableStateFlow<FieldError?>(null)
    val fieldError: StateFlow<FieldError?> = _fieldError.asStateFlow()

    fun onEmailChange(email: String) {
        _email.value = email
        if (_fieldError.value?.field == FormField.EMAIL) {
            _fieldError.value = null
        }
    }

    fun onPasswordChange(password: String) {
        _password.value = password
        if (_fieldError.value?.field == FormField.PASSWORD) {
            _fieldError.value = null
        }
    }

    fun onLoginClick(navController : NavHostController, onSuccess : () -> Unit){
        viewModelScope.launch {
            _isLoading.value = true
            _fieldError.value = null
            
            when (val result = loginUseCase(email = email.value, password = password.value)) {
                is Result.Success -> {
                    userPreferencesManager.saveUserEmail(email.value)
                    onSuccess()
                }
                is Result.Error -> {
                    val errorMessage = result.error.getMessage(navController.context)
                    val field = when (result.error.messageResId) {
                        R.string.enter_email, R.string.enter_valid_email, R.string.invalid_credentials -> FormField.EMAIL
                        R.string.enter_password, R.string.enter_valid_password -> FormField.PASSWORD
                        else -> FormField.EMAIL
                    }
                    _fieldError.value = FieldError(errorMessage, field)
                }
            }
            
            _isLoading.value = false
        }
    }
}