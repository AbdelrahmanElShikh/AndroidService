package com.abdelrahman.koltin.boundservice

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.util.Log
import androidx.core.os.postDelayed
import androidx.databinding.DataBindingUtil
import com.abdelrahman.koltin.boundservice.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.math.roundToInt

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var odometerService: OdometerService
    private var isBound = false

    private val connection = object : ServiceConnection {
        override fun onServiceDisconnected(p0: ComponentName?) {
            isBound = false
        }

        override fun onServiceConnected(p0: ComponentName?, binder: IBinder?) {
            val odometerBinder = binder as OdometerService.OdometerBinder
            odometerService = odometerBinder.getOdometer()
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        watchDistanceChange()
    }

    override fun onStart() {
        super.onStart()
        Log.d("MainActivity", "Start")
        bindService(Intent(this, OdometerService::class.java), connection, Context.BIND_AUTO_CREATE)
        /**
         * The code Context.BIND_AUTO_CREATE tells Android to
         * create the service if it doesn’t already exist.
         */
    }

    private fun watchDistanceChange() {
        val handler = Handler()
        handler.postDelayed(object : Runnable {
            override fun run() {
                Log.d("MainActivity", "run")
                var distance = 0.0
                if (odometerService != null)
                    distance = (odometerService.getMeters() * 100.0).roundToInt() / 100.00
                val stringDistance: String = "%.2f".format(distance)
                binding.distance.text = stringDistance
                binding.distance.append("Meter")
                handler.postDelayed(this, 1000)
            }

        }, 1000)
    }

    override fun onStop() {
        Log.d("MainActivity", "Stopped")
        super.onStop()
        if (isBound) {
            unbindService(connection)
            isBound = false
        }
    }
}
