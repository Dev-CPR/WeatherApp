package com.perennial.weather.ui.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.size
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.perennial.weather.R
import com.perennial.weather.utils.Constant

@Composable
fun AppSpacer(height: Dp) {
    Spacer(modifier = Modifier.height(height))
}

@Composable
fun WeatherIcon(
    iconCode: String,
    modifier: Modifier = Modifier.size(80.dp)
){

    val iconUrl = "https://openweathermap.org/img/wn/${iconCode}@2x.png"
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(iconUrl)
            .crossfade(true)
            .build(),
        contentDescription = Constant.WEATHER_ICON,
        placeholder = painterResource(R.drawable.load),
        error = painterResource(R.drawable.load),
        modifier = modifier
    )
}
