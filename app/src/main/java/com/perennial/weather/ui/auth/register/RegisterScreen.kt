package com.perennial.weather.ui.auth.register

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.perennial.weather.R
import com.perennial.weather.domain.model.FieldError
import com.perennial.weather.domain.model.FormField
import com.perennial.weather.ui.components.AppSpacer
import com.perennial.weather.ui.navigation.Route

@Composable
fun RegisterScreen(
    navHostController: NavHostController,
    registerViewModel: RegisterViewModel = hiltViewModel()
)
{
    val name by registerViewModel.name.collectAsState()
    val email by registerViewModel.email.collectAsState()
    val password by registerViewModel.password.collectAsState()
    val confirmPassword by registerViewModel.confirmPassword.collectAsState()
    val isLoading by registerViewModel.isLoading.collectAsState()
    val fieldError by registerViewModel.fieldError.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center) {

        Text(
            text = navHostController.context.resources.getString(R.string.register),
            fontSize = 20.sp,
            color = Color.Black,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.headlineMedium
        )

        EditTextFields(
            name = name,
            email = email,
            password = password,
            confirmPassword = confirmPassword,
            fieldError = fieldError,
            navHostController = navHostController,
            registerViewModel = registerViewModel
        )

        AppSpacer(20.dp)

        RegisterButton(
            navHostController = navHostController,
            registerViewModel = registerViewModel,
            isLoading = isLoading
        )

        AppSpacer(20.dp)

        AlreadyHaveAccount(navHostController = navHostController)
    }
}

@Composable
fun EditTextFields(
    name: String,
    email: String,
    password: String,
    confirmPassword: String,
    fieldError: FieldError?,
    navHostController: NavHostController,
    registerViewModel: RegisterViewModel
){
    AppSpacer(32.dp)

    OutlinedTextField(
        value = name,
        onValueChange = {registerViewModel.onNameChange(it)},
        label = {Text(navHostController.context.resources.getString(R.string.name))},
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        isError = fieldError?.field == FormField.NAME,
        supportingText = if (fieldError?.field == FormField.NAME) { { Text(fieldError.message) } } else null,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Next)
    )

    AppSpacer(16.dp)

    OutlinedTextField(
        value = email,
        onValueChange = {registerViewModel.onEmailChange(it)},
        label = {Text(navHostController.context.resources.getString(R.string.email))},
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        isError = fieldError?.field == FormField.EMAIL,
        supportingText = if (fieldError?.field == FormField.EMAIL) { { Text(fieldError.message) } } else null,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Email,
            imeAction = ImeAction.Next)
    )

    AppSpacer(16.dp)

    OutlinedTextField(
        value = password,
        onValueChange = {registerViewModel.onPasswordChange(it)},
        label = {Text(navHostController.context.resources.getString(R.string.password))},
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        isError = fieldError?.field == FormField.PASSWORD,
        supportingText = if (fieldError?.field == FormField.PASSWORD) { { Text(fieldError.message) } } else null,
        visualTransformation = PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Next)
    )

    AppSpacer(16.dp)

    OutlinedTextField(
        value = confirmPassword,
        onValueChange = {registerViewModel.onConfirmPasswordChange(it)},
        label = {Text(navHostController.context.resources.getString(R.string.confirm_password))},
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        isError = fieldError?.field == FormField.CONFIRM_PASSWORD,
        supportingText = if (fieldError?.field == FormField.CONFIRM_PASSWORD) { { Text(fieldError.message) } } else null,
        visualTransformation = PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Done)
    )
}

@Composable
fun RegisterButton(navHostController: NavHostController, registerViewModel: RegisterViewModel, isLoading: Boolean){
    Button(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp),
        onClick = {registerViewModel.onRegisterClick(
            navHostController = navHostController,
            onSuccess = {
                navHostController.navigate(Route.Login.route){
                    popUpTo(Route.Register.route){
                        inclusive = true
                    }
                }
            }
        ) },

        ) {
        if (isLoading){
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                strokeWidth = 2.dp)
        }
        else{
            Text(navHostController.context.resources.getString(R.string.register))
        }
    }
}
@Composable
fun AlreadyHaveAccount(navHostController: NavHostController){
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        Text(text = "${navHostController.context.resources.getString(R.string.already_have_account)} ")

        Text(
            text = navHostController.context.resources.getString(R.string.login),
            color = Color.Blue,
            modifier = Modifier.clickable{
                navHostController.navigate(Route.Login.route){
                    popUpTo(Route.Login.route) { inclusive = true }
                }
            }
        )
    }
}