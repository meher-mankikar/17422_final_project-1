package com.example.a17422_final_project

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.a17422_final_project.databinding.ActivityStepBinding


class StepActivity : AppCompatActivity() {

    private lateinit var mAccel : Accelerometer
    private lateinit var binding: ActivityStepBinding
    private var steps : Int = 0

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStepBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACTIVITY_RECOGNITION) != PackageManager.PERMISSION_GRANTED)
        {
            Log.d("perms", "activity_recognition")
            requestPermissions(arrayOf<String>(Manifest.permission.ACTIVITY_RECOGNITION), 0)
        }

        mAccel = Accelerometer(this)

        // create a listener for accelerometer
        mAccel.setListener(object : Accelerometer.Listener {
            //on translation method of accelerometer
//            override fun onTranslation(tx: Float, ty: Float, ts: Float) {
//                // set the color red if the device moves in positive x axis
//                Log.d("accelerometer", "[%6.3f, %6.3f, %6.3f]".format(tx, ty, ts))
//                if (tx > 1.0f) {
//                    window.decorView.setBackgroundColor(Color.RED)
//                } else if (tx < -1.0f) {
//                    window.decorView.setBackgroundColor(Color.BLUE)
//                }
//            }
            override fun onTranslation() {
                steps++
                val tv1: TextView = findViewById(R.id.stepCount)
                tv1.text = steps.toString()
                Log.d("step", steps.toString())
            }
        })
    }
    override fun onResume() {
        super.onResume()
        mAccel.register()
    }

    override fun onPause() {
        super.onPause()
        mAccel.unregister()
    }

//    override fun onBackInvoke() {
//        super.onBackPressed()
//    }
}