package com.struggleassist.Controller.FallDetection.SensorControllers;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;

/**
 * Created by lucas on 9/14/2017.
 * Purpose: Will control the low level interactions with the devices gravity sensor
 */

public class GravityController extends SensorController {

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
        xValue = event.values[0]; //x-axis (wide)
        yValue = event.values[1]; //y-axis (tall)
        zValue = event.values[2]; //z-axis (screen)
    }
}
