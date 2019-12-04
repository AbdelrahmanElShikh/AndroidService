package com.abdelrahman.java.boundservice;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import java.util.Objects;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class OdometerService extends Service {
    private IBinder binder = new OdometerBinder();

    private static double distanceInMeter;
    private static Location lastLocation = null;


    public OdometerService() {
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
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public class OdometerBinder extends Binder {
        OdometerService getOdometer() {
            /**
             *  The Class.this syntax is useful when you have a non-static nested class that needs to refer to its outer class's instance.
             */
            return OdometerService.this;
        }
    }

    @Override
    public void onCreate() {
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if (lastLocation == null) lastLocation = location;
                distanceInMeter += location.distanceTo(lastLocation);
                lastLocation = location;
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Objects.requireNonNull(locationManager).requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, locationListener);
    }
    public double getMeters(){
        return distanceInMeter;
    }
}
