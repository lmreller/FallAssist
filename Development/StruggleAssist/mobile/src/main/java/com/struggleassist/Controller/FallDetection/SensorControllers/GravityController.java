package com.struggleassist.Controller.FallDetection.SensorControllers;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

/**
 * Created by lucas on 9/14/2017.
 * Purpose: Will control the low level interactions with the devices gravity sensor
 */

public class GravityController extends SensorController{
    private float xGravity;
    private float yGravity;
    private float zGravity;

    //**********************************************************************************************
    //Description: Constuctor for the GravityController
    //Input: Context context, should be 'this' to reference the device
    //Result: a new sensor setup is configured and registered with a listener for the gravity
    //        sensor in the device
    //**********************************************************************************************
    public GravityController(Context context){
        super(context);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
    }

    //**********************************************************************************************
    //Description: Event Listener for the gravity sensor
    //Input: SensorEvent event, sensor info from the phones hardware
    //Result: !!!!!!!!!!!!!!
    //**********************************************************************************************
    @Override
    public void onSensorChanged(SensorEvent event){
        xGravity = event.values[0]; //x-axis (wide)
        yGravity = event.values[1]; //y-axis (tall)
        zGravity = event.values[2]; //z-axis (screen)
    }

    //**********************************************************************************************
    //Description: GETTERS
    //Input: N/A
    //Result: Each respective variable is returned to the function call location
    //**********************************************************************************************
    public float getxGravity(){
        return xGravity;
    }

    public float getyGravity(){
        return yGravity;
    }

    public float getzGravity(){
        return zGravity;
    }
    //END GETTERS
}
