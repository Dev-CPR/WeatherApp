package com.perennial.weather.ui.splash

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.perennial.weather.R
import com.perennial.weather.ui.home.HomeViewModel
import com.perennial.weather.ui.navigation.AppNavGraph
import com.perennial.weather.utils.AppLocationManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlin.getValue

@AndroidEntryPoint
class SplashActivity : ComponentActivity() {

    private lateinit var locationManager: AppLocationManager
    private val homeViewModel: HomeViewModel by viewModels()

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
            val granted = result.values.any { it }
            if (granted) getLocation() else homeViewModel.updateError("Permission Denied")
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        locationManager = AppLocationManager(this, permissionLauncher)

        setContent {
            val navHostController = rememberNavController()
            AppNavGraph(navHostController = navHostController)
        }
    }

    private fun getLocation() {
        locationManager.getCurrentLocation(
            onSuccess = { lat, lon ->
                homeViewModel.updateLocation(lat, lon)
            },
            onFailure = {
                homeViewModel.updateError(it)
            }
        )
    }

}

@Composable
fun SplashScreen(navigateToLogin : () -> Unit, navigateToHome : () -> Unit, onTimeOut: () -> Unit) {
    var alpha by remember {
        mutableStateOf(0f)
    }

    LaunchedEffect(true) {
        animate(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = tween(1200)
        ){
            value, _ ->
            alpha = value
        }

        delay(1000)
        onTimeOut()
    }

    Box(
        modifier = Modifier.fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ){
        Image(painter = painterResource(R.drawable.app_logo),
            contentDescription = "Splash Screen",
            modifier = Modifier.width(160.dp)
                .graphicsLayer{
                    this.alpha = alpha
                })
    }
}
