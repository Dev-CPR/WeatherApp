package com.perennial.weather.ui.auth.login

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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

@Composable
fun LoginScreen(
    navController: NavHostController,
    loginViewModel: LoginViewModel = hiltViewModel(),
    navigateToRegister: () -> Unit,
    navigateToHome: () -> Unit) {

    val email by loginViewModel.email.collectAsState()
    val password by loginViewModel.password.collectAsState()
    val isLoading by loginViewModel.isLoading.collectAsState()
    val fieldError by loginViewModel.fieldError.collectAsState()

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center)
    {
        Text(
            text = navController.context.resources.getString(R.string.welcome_text),
            fontSize = 20.sp,
            color = Color.Black,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.headlineMedium
        )

        AppSpacer(32.dp)

        EditTextFields(
            email = email,
            password = password,
            fieldError = fieldError,
            navController = navController,
            loginViewModel = loginViewModel
        )

        AppSpacer(16.dp)

        LoginButton(
            navController = navController,
            loginViewModel = loginViewModel,
            isLoading = isLoading,
            navigateToHome = navigateToHome
        )

        AppSpacer(20.dp)

        DontHaveAccount(
            navController = navController,
            navigateToRegister = navigateToRegister
        )
    }

}
@Composable
fun EditTextFields(
    email : String,
    password : String,
    fieldError: FieldError?,
    navController: NavHostController,
    loginViewModel: LoginViewModel
    ){
    OutlinedTextField(
        value = email,
        onValueChange = {loginViewModel.onEmailChange(it)},
        label = {Text(navController.context.resources.getString(R.string.email))},
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
        onValueChange = {loginViewModel.onPasswordChange(it)},
        label = {Text(navController.context.resources.getString(R.string.password))},
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        isError = fieldError?.field == FormField.PASSWORD,
        supportingText = if (fieldError?.field == FormField.PASSWORD) { { Text(fieldError.message) } } else null,
        visualTransformation = PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Done)
    )
}

@Composable
fun LoginButton(
    navController: NavHostController,
    loginViewModel: LoginViewModel,
    isLoading : Boolean,
    navigateToHome: () -> Unit){
    Button(
        onClick = {
            loginViewModel.onLoginClick(navController = navController,
                onSuccess = {
                    navigateToHome()
                }
            )},
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp),
        enabled = !isLoading

    ) {
        if (isLoading){
            CircularProgressIndicator(
                modifier = Modifier.width(22.dp),
                color = Color.Black,
                strokeWidth = 2.dp
            )
        }else{
            Text(
                text = navController.context.resources.getString(R.string.login),
                fontSize = 16.sp,
            )
        }
    }
}

@Composable
fun DontHaveAccount(navController: NavHostController, navigateToRegister: () -> Unit){
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        Text(text = "${navController.context.resources.getString(R.string.dont_have_account)} ")

        Text(
            text = navController.context.resources.getString(R.string.register),
            color = Color.Blue,
            modifier = Modifier.clickable{
                navigateToRegister()
            }
        )
    }
}
