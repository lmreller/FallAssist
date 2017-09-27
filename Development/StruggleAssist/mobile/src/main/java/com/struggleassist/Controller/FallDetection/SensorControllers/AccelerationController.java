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

public class AccelerationController implements SensorEventListener {
    private SensorManager mSensorManager;
    private Sensor mSensor;
    private float xAccel;
    private float yAccel;
    private float zAccel;

    //**********************************************************************************************
    //Description: Constuctor for the AccelerationController
    //
    //Input: Context context, should be 'this' to reference the device
    //
    //Result: a new sensor setup is configured and registered with a listener for the linear
    //        acceleration sensor in the device
    //**********************************************************************************************
    public AccelerationController(Context context){
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        mSensorManager.registerListener(this, mSensor , SensorManager.SENSOR_DELAY_NORMAL);
    }

    //**********************************************************************************************
    //Description: Event Listener for the linear acceleration sensor
    //
    //Input: SensorEvent event, sensor info from the phones hardware
    //
    //Result: !!!!!!!!!!!!!!
    //**********************************************************************************************
    @Override
    public void onSensorChanged(SensorEvent event){
        xAccel = event.values[0]; //x-axis (wide)
        yAccel = event.values[1]; //y-axis (tall)
        zAccel = event.values[2]; //z-axis (screen)
        SensorData.setAccelX(xAccel);
        SensorData.setAccelY(yAccel);
        SensorData.setAccelZ(zAccel);

        Log.d("ACCEL SENSOR", "xAccel " + xAccel);
        Log.d("ACCEL SENSOR", "yAccel " + yAccel);
        Log.d("ACCEL SENSOR", "zAccel " + zAccel);

        //trigger fall detection class somewhere in here based on conditionals
        if(Math.abs(xAccel) > 10 ||Math.abs(yAccel) > 10 || Math.abs(zAccel) > 10){
            Log.d("FALL", "Potential Fall");

            FallDetection.run();
        }
    }

    //**********************************************************************************************
    //Description: unused, required to implement SensorEvenListener
    //
    //Input: N/A
    //
    //Result: N/A
    //**********************************************************************************************
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    //**********************************************************************************************
    //Description: unregisters the event listener to conserve battery life
    //
    //Input: N/A
    //
    //Result: event listener no longer exists so sensor is no longer in use
    //**********************************************************************************************
    public void end(){
        mSensorManager.unregisterListener(this);
    }

    //**********************************************************************************************
    //Description: GETTERS
    //
    //Input: N/A
    //
    //Result: Each respective variable is returned to the function call location
    //**********************************************************************************************
    public float getxAccel(){
        return xAccel;
    }

    public float getyAccel(){
        return yAccel;
    }

    public float getzAccel(){
        return zAccel;
    }
    //END GETTERS
}
