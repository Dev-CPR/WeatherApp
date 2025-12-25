package com.perennial.weather.ui.home.currentweather

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.perennial.weather.R
import com.perennial.weather.data.local.entity.WeatherEntity
import com.perennial.weather.ui.components.AppSpacer
import com.perennial.weather.ui.components.ErrorDialog
import com.perennial.weather.ui.components.WeatherIcon
import com.perennial.weather.ui.home.HomeViewModel
import com.perennial.weather.utils.Constant
import com.perennial.weather.utils.Utils.formatTime

private const val DEFAULT_COORDINATE = 0.00

@Composable
fun CurrentWeatherScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onRequestLocation: () -> Unit,
    onRefresh: () -> Unit
) {
    val state by viewModel.weatherState.collectAsState()
    val latitude by viewModel.latitude.collectAsState()
    val longitude by viewModel.longitude.collectAsState()
    val hasValidLocation = latitude != DEFAULT_COORDINATE && longitude != DEFAULT_COORDINATE

    LaunchedEffect(Unit) {
        if (!hasValidLocation) {
            onRequestLocation()
        }
    }

    LaunchedEffect(latitude, longitude) {
        if (hasValidLocation) {
            viewModel.loadWeather(lat = latitude, lon = longitude)
        }
    }

    ShowErrorDialogs(
        weatherError = state.error,
        locationError = viewModel.error,
        onClearWeatherError = { viewModel.clearWeatherError() },
        onClearLocationError = { viewModel.updateError() },
        onRetryWeather = {
            viewModel.clearWeatherError()
            onRefresh()
        },
        onRetryLocation = {
            viewModel.updateError()
            onRequestLocation()
        }
    )

    when {
        state.isLoading || !hasValidLocation -> LoadingState()
        state.data != null -> WeatherContent(
            weather = state.data!!,
            onRefresh = onRefresh
        )
    }
}

@Composable
private fun ShowErrorDialogs(
    weatherError: String?,
    locationError: String?,
    onClearWeatherError: () -> Unit,
    onClearLocationError: () -> Unit,
    onRetryWeather: () -> Unit,
    onRetryLocation: () -> Unit
) {
    weatherError?.let { error ->
        ErrorDialog(
            message = error,
            onDismiss = onClearWeatherError,
            onConfirm = {
                onClearWeatherError()
                onRetryWeather()
            }
        )
    }

    if (!locationError.isNullOrEmpty()) {
        ErrorDialog(
            message = locationError,
            onDismiss = onClearLocationError,
            onConfirm = {
                onClearLocationError()
                onRetryLocation()
            }
        )
    }
}

@Composable
private fun LoadingState() {
    Box(
        modifier = Modifier.fillMaxSize()
            .background(Color.LightGray),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun WeatherContent(
    weather: WeatherEntity,
    onRefresh: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "${weather.city}, ${weather.country}",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        AppSpacer(16.dp)

        WeatherIcon(
            weather.icon,
            modifier = Modifier.size(120.dp)
        )

        AppSpacer(16.dp)

        Text(
            text = "${weather.temperatureCelsius} ${Constant.CELSIUS}",
            style = MaterialTheme.typography.displayMedium,
            fontWeight = FontWeight.ExtraBold
        )

        AppSpacer(20.dp)

        SunRise(weather = weather)

        SunSet(weather = weather)

        AppSpacer(24.dp)

        Button(onClick = onRefresh) {
            Icon(Icons.Default.Refresh, contentDescription = null)
            Spacer(Modifier.width(6.dp))
            Text(Constant.REFRESH)
        }
    }
}

@Composable
fun SunRise(weather: WeatherEntity){
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Icon(
            painter = painterResource(id = R.drawable.sunrise),
            contentDescription = Constant.SUNRISE,
            tint = Color.Unspecified,
            modifier = Modifier.size(24.dp)
        )
        AppSpacer(8.dp)
        Text(
            text = "${Constant.SUNRISE}: ${formatTime(weather.sunrise)}",
            fontSize = 17.sp
        )
    }
}
@Composable
fun SunSet(weather: WeatherEntity){
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Icon(
            painter = painterResource(id = R.drawable.sunset),
            contentDescription = Constant.SUNSET,
            tint = Color.Unspecified,
            modifier = Modifier.size(24.dp)
        )
        AppSpacer(8.dp)
        Text(
            text = "${Constant.SUNSET}: ${formatTime(weather.sunset)}",
            fontSize = 17.sp
        )
    }
}


