package com.abdelrahman.java.boundservice;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.abdelrahman.java.boundservice.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private OdometerService odometerService;
    private boolean isBounded = false;

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder binder) {
            OdometerService.OdometerBinder odometerBinder = (OdometerService.OdometerBinder) binder;
            odometerService = odometerBinder.getOdometer();
            isBounded = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            isBounded = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        watchDistanceChange();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this,OdometerService.class);
        bindService(intent,connection, Context.BIND_AUTO_CREATE);
        /**
         * The code Context.BIND_AUTO_CREATE tells Android to
         * create the service if it doesn’t already exist.
         */
    }

    private void watchDistanceChange() {
        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                double distance = 0.0;
                if(odometerService != null){
                    distance = Math.round(odometerService.getMeters() * 100.0) / 100.0;
                }
                binding.distance.setText(distance+" Meter");
                handler.postDelayed(this,1000);
            }
        });
    }



    @Override
    protected void onStop() {
        super.onStop();
        if(isBounded){
            unbindService(connection);
            isBounded=false;
        }
    }
}
