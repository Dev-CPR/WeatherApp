package com.perennial.weather.ui.home

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.compose.rememberNavController
import com.perennial.weather.ui.home.component.BottomNavigationBar
import com.perennial.weather.utils.AppLocationManager
import com.perennial.weather.utils.ErrorConstant

private const val DEFAULT_COORDINATE = 0.00

@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val bottomNavController = rememberNavController()

    val locationManagerRef = remember { mutableStateOf<AppLocationManager?>(null) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.values.any { it }) {
            locationManagerRef.value?.let { manager ->
                if (manager.isGpsEnabled()) {
                    fetchLocation(manager, homeViewModel)
                } else {
                    homeViewModel.updateError(ErrorConstant.GPS_NOT_ENABLED)
                }
            } ?: homeViewModel.updateError(ErrorConstant.LOCATION_NOT_INITIALIZED)
        } else {
            homeViewModel.updateError(ErrorConstant.PERMISSION_DENIED)
        }
    }

    val locationManager = remember {
        AppLocationManager(context, permissionLauncher).also {
            locationManagerRef.value = it
        }
    }

    val checkAndFetchLocation = {
        if (hasLocationPermission(context) && locationManager.isGpsEnabled()) {
            fetchLocation(locationManager, homeViewModel)
        } else if (!hasLocationPermission(context)) {
            locationManager.requestLocationPermission()
        }
    }

    LaunchedEffect(Unit) {
        checkAndFetchLocation()
    }

    SetupLifecycleObserver(homeViewModel, checkAndFetchLocation)

    val handleLocationRequest = {
        when {
            hasLocationPermission(context) && locationManager.isGpsEnabled() ->
                fetchLocation(locationManager, homeViewModel)
            hasLocationPermission(context) ->
                context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            locationManager.isGpsEnabled() ->
                locationManager.requestLocationPermission()
            else ->
                context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
        }
    }

    val handleRefresh = {
        when {
            hasLocationPermission(context) && locationManager.isGpsEnabled() -> {
                locationManager.getCurrentLocation(
                    onSuccess = { lat, lon ->
                        homeViewModel.updateLocation(lat, lon)
                        homeViewModel.updateError()
                        homeViewModel.refresh(lat = lat, lon = lon)
                    },
                    onFailure = { homeViewModel.updateError(it) }
                )
            }
            hasLocationPermission(context) ->
                homeViewModel.updateError(ErrorConstant.GPS_NOT_ENABLED)
            else ->
                locationManager.requestLocationPermission()
        }
    }

    Scaffold(
        bottomBar = { BottomNavigationBar(bottomNavController) }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            HomeNavGraph(
                navHostController = bottomNavController,
                homeViewModel = homeViewModel,
                onRequestLocation = handleLocationRequest,
                onRefresh = handleRefresh
            )
        }
    }
}

@Composable
private fun SetupLifecycleObserver(
    homeViewModel: HomeViewModel,
    onResume: () -> Unit
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                val lat = homeViewModel.latitude.value
                val lon = homeViewModel.longitude.value
                if (lat == DEFAULT_COORDINATE || lon == DEFAULT_COORDINATE) {
                    onResume()
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }
}

private fun hasLocationPermission(context: android.content.Context): Boolean =
    ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

private fun fetchLocation(
    locationManager: AppLocationManager,
    homeViewModel: HomeViewModel
) {
    locationManager.getCurrentLocation(
        onSuccess = { lat, lon ->
            homeViewModel.updateLocation(lat, lon)
            homeViewModel.updateError()
        },
        onFailure = { error -> homeViewModel.updateError(error) }
    )
}
