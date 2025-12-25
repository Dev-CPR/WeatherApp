package com.perennial.weather.utils

import android.Manifest
import android.content.Context
import android.location.LocationManager
import android.os.Handler
import android.os.Looper
import androidx.activity.result.ActivityResultLauncher
import androidx.annotation.RequiresPermission
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority

class AppLocationManager(
    private val context: Context,
    private val permissionLauncher: ActivityResultLauncher<Array<String>>
) {

    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    fun requestLocationPermission() {
        permissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    fun isGpsEnabled(): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }


    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    fun getCurrentLocation(
        onSuccess: (latitude: Double, longitude: Double) -> Unit,
        onFailure: (String) -> Unit
    ) {
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                onSuccess(location.latitude, location.longitude)
            } else {
                requestFreshLocation(onSuccess, onFailure)
            }
        }.addOnFailureListener {
            requestFreshLocation(onSuccess, onFailure)
        }
    }

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    private fun requestFreshLocation(
        onSuccess: (latitude: Double, longitude: Double) -> Unit,
        onFailure: (String) -> Unit
    ) {
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000)
            .setWaitForAccurateLocation(false)
            .setMinUpdateIntervalMillis(5000)
            .setMaxUpdateDelayMillis(10000)
            .build()

        var isCallbackHandled = false
        val handler = Handler(Looper.getMainLooper())
        
        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                if (isCallbackHandled) return
                isCallbackHandled = true
                
                handler.removeCallbacksAndMessages(null)
                val location = locationResult.lastLocation
                fusedLocationClient.removeLocationUpdates(this)
                
                if (location != null) {
                    onSuccess(location.latitude, location.longitude)
                } else {
                    onFailure(ErrorConstant.LOCATION_NOT_AVAILABLE)
                }
            }
        }

        handler.postDelayed({
            if (!isCallbackHandled) {
                isCallbackHandled = true
                fusedLocationClient.removeLocationUpdates(locationCallback)
                onFailure(ErrorConstant.LOCATION_REQUEST_TIME_OUT)
            }
        }, 30000)

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        ).addOnFailureListener { exception ->
            if (!isCallbackHandled) {
                isCallbackHandled = true
                handler.removeCallbacksAndMessages(null)
                fusedLocationClient.removeLocationUpdates(locationCallback)
                onFailure("${ErrorConstant.UNABLE_TO_GET_LOCATION} ${exception.message}")
            }
        }
    }
}
