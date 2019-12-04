package com.abdelrahman.koltin.boundservice

import android.Manifest
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Binder
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.core.content.ContextCompat
import java.util.*
import kotlin.math.log10

class OdometerService : Service() {
    private val binder = OdometerBinder()

    companion object {
        private var lastLocation: Location? = null
        private var distanceInMeter = 0.0
    }


    class OdometerBinder : Binder() {
        fun getOdometer(): OdometerService {
            return OdometerService()
        }
    }

    /**
     *
     * @param intent : The intent contains any additional information the activity needs to pass to the service.
     * this method is used for binding component(activity) to the service.
     * how this method work?
     * 1 - The activity creates a ServiceConnection object.
     * A ServiceConnection is used to form a connection with the service.
     * 2 -The activity passes an Intent down the connection to the service.
     * 3 - The bound service creates a Binder object.
     * The Binder contains a reference to the bound service. The service sends the Binder back along the connection.
     * 4 - When the activity receives the Binder, it takes out the Service object and starts to use the service directly.
     */
    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    override fun onCreate() {
        Log.d("Service","Created")
        val locationListener = object : LocationListener {
            override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {
            }

            override fun onProviderEnabled(p0: String?) {
            }

            override fun onProviderDisabled(p0: String?) {
            }

            override fun onLocationChanged(location: Location?) {
                if (lastLocation != null) lastLocation = location
                distanceInMeter += location!!.distanceTo(lastLocation)
                lastLocation = location
            }

        }
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        Objects.requireNonNull(locationManager).requestLocationUpdates(
            LocationManager.GPS_PROVIDER, 1000, 1f, locationListener
        )
    }

    fun getMeters(): Double {
        return distanceInMeter
    }
}
