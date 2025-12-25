package com.perennial.weather.ui.auth.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.perennial.weather.R
import com.perennial.weather.domain.model.Result
import com.perennial.weather.domain.usecase.RegisterUseCase
import com.perennial.weather.domain.model.FieldError
import com.perennial.weather.domain.model.FormField
import com.perennial.weather.utils.getMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(private val registerUseCase: RegisterUseCase) : ViewModel() {

    private val _name = MutableStateFlow("")
    val name: StateFlow<String> = _name.asStateFlow()

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    private val _confirmPassword = MutableStateFlow("")
    val confirmPassword: StateFlow<String> = _confirmPassword.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _fieldError = MutableStateFlow<FieldError?>(null)
    val fieldError: StateFlow<FieldError?> = _fieldError.asStateFlow()

    fun onNameChange(name: String) {
        _name.value = name
        if (_fieldError.value?.field == FormField.NAME) {
            _fieldError.value = null
        }
    }

    fun onEmailChange(email: String){
        _email.value = email
        if (_fieldError.value?.field == FormField.EMAIL) {
            _fieldError.value = null
        }
    }

    fun onPasswordChange(password: String){
        _password.value = password
        if (_fieldError.value?.field == FormField.PASSWORD) {
            _fieldError.value = null
        }

        if (password == _confirmPassword.value && _fieldError.value?.field == FormField.CONFIRM_PASSWORD) {
            _fieldError.value = null
        }
    }

    fun onConfirmPasswordChange(confirmPassword : String){
        _confirmPassword.value = confirmPassword
        if (_fieldError.value?.field == FormField.CONFIRM_PASSWORD) {
            _fieldError.value = null
        }
    }

    fun onRegisterClick(navHostController: NavHostController, onSuccess : () -> Unit){
        viewModelScope.launch {
            _isLoading.value = true
            _fieldError.value = null
            
            when (val result = registerUseCase(
                name = name.value,
                email = email.value,
                password = password.value,
                confirmPassword = confirmPassword.value
            )) {
                is Result.Success -> {
                    onSuccess()
                }
                is Result.Error -> {
                    val errorMessage = result.error.getMessage(navHostController.context)
                    val field = when (result.error.messageResId) {
                        R.string.enter_name -> FormField.NAME
                        R.string.enter_email, R.string.enter_valid_email, R.string.user_already_exists -> FormField.EMAIL
                        R.string.enter_password, R.string.enter_valid_password -> FormField.PASSWORD
                        R.string.enter_confirm_password, R.string.enter_valid_confirm_password -> FormField.CONFIRM_PASSWORD
                        else -> FormField.EMAIL
                    }
                    _fieldError.value = FieldError(errorMessage, field)
                }
            }
            
            _isLoading.value = false
        }
    }
}
