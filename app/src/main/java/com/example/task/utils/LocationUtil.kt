package com.example.task.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.location.LocationManager
import android.content.pm.PackageManager
import android.os.Bundle
import android.location.LocationListener
import android.content.Context.LOCATION_SERVICE
import android.content.IntentSender
import android.location.Location
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest

class LocationUtil {

    private val LOCATION_REFRESH_TIME = 1000 // 1 seconds. The Minimum Time to get location update
    private val LOCATION_REFRESH_DISTANCE = 10 // 10 meters. The Minimum Distance to be changed to get location update
    private lateinit var mLocationManager: LocationManager
    private lateinit var locationRequest: LocationRequest
    private var myLocationListener: MyLocationListener? = null

    companion object {
        public const val LOCATION_PERMISSION_REQUEST_CODE = 1
        public const val REQUEST_CHECK_SETTINGS = 2
    }

    interface MyLocationListener {
        fun onLocationChanged(location: Location)
    }

    fun createLocationRequest(context: Context, myListener: MyLocationListener) {
        locationRequest = LocationRequest.create().apply {
            interval = 100
            fastestInterval = 50
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            maxWaitTime= 100
        }

        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)

        val client = LocationServices.getSettingsClient(context)
        val task = client.checkLocationSettings(builder.build())
        task.addOnSuccessListener {
            startListeningUserLocation(context, myListener)
        }
        task.addOnFailureListener { e ->
            if (e is ResolvableApiException) {
                try {
                    e.startResolutionForResult(
                        context as Activity,
                        REQUEST_CHECK_SETTINGS
                    )
                } catch (sendEx: IntentSender.SendIntentException) {
                }
            }
        }
    }

    fun startListeningUserLocation(context: Context, myListener: MyLocationListener) {
        myLocationListener = myListener

        mLocationManager = context.getSystemService(LOCATION_SERVICE) as LocationManager

        val mLocationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                myLocationListener!!.onLocationChanged(location)
            }
            override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
            override fun onProviderEnabled(provider: String) {}
            override fun onProviderDisabled(provider: String) {}
        }
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(context,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_REFRESH_TIME.toLong(),LOCATION_REFRESH_DISTANCE.toFloat(), mLocationListener)
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(context as Activity, Manifest.permission.ACCESS_FINE_LOCATION) || ActivityCompat.shouldShowRequestPermissionRationale(context as Activity,Manifest.permission.ACCESS_COARSE_LOCATION)) {
                createLocationRequest(context, myLocationListener!!)
            } else {
                ActivityCompat.requestPermissions(context,arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),LOCATION_PERMISSION_REQUEST_CODE)
            }
        }
    }
}