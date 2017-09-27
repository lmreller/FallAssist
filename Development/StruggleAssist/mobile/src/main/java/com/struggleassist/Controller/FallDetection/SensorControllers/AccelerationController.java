package com.struggleassist.Controller.FallDetection.SensorControllers;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import com.struggleassist.Controller.FallDetection.FallDetection;
import com.struggleassist.Controller.FallDetection.SensorData;

/**
 * Created by lucas on 9/14/2017.
 * Purpose: Will control the low level interactions with the devices linear accelerometer (no gravity)
 */

public class AccelerationController extends SensorController {

    //**********************************************************************************************
    //Description: Constuctor for the AccelerationController
    //Input: Context context, should be 'this' to reference the device
    //Result: a new sensor setup is configured and registered with a listener for the linear
    //        acceleration sensor in the device
    //**********************************************************************************************
    public AccelerationController(Context context){
        super(context);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
    }

    //**********************************************************************************************
    //Description: Event Listener for the linear acceleration sensor
    //Input: SensorEvent event, sensor info from the phones hardware
    //Result: !!!!!!!!!!!!!!
    //**********************************************************************************************
    @Override
    public void onSensorChanged(SensorEvent event){
        xValue = event.values[0]; //x-axis (wide)
        yValue = event.values[1]; //y-axis (tall)
        zValue = event.values[2]; //z-axis (screen)
        SensorData.setAccelX(xValue);
        SensorData.setAccelY(yValue);
        SensorData.setAccelZ(zValue);

        Log.d("ACCEL SENSOR", "xAccel " + xValue);
        Log.d("ACCEL SENSOR", "yAccel " + yValue);
        Log.d("ACCEL SENSOR", "zAccel " + zValue);

        //trigger fall detection class somewhere in here based on conditionals
        if(Math.abs(xValue) > 10 ||Math.abs(yValue) > 10 || Math.abs(zValue) > 10){
            Log.d("FALL", "Potential Fall");

            FallDetection.run();
        }
    }
}
