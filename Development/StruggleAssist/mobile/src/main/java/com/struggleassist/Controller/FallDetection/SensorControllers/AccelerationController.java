package com.struggleassist.Controller.FallDetection.SensorControllers;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.util.Log;

import com.struggleassist.Controller.FallDetection.FallDetection;

/**
 * Created by lucas on 9/14/2017.
 * Purpose: Will control the low level interactions with the devices linear accelerometer (no gravity)
 */

public class AccelerationController extends SensorController {

    private final static int startThreshold = 5;
    private boolean type;

    //**********************************************************************************************
    //Description: Constuctor for the AccelerationController
    //Input: Context context, should be 'this' to reference the device
    //       boolean type, true is to start fall detection; false is for data collection
    //Result: a new sensor setup is configured and registered with a listener for the linear
    //        acceleration sensor in the device
    //**********************************************************************************************
    public AccelerationController(Context context, boolean type) {
        super(context);
        this.type = type;
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
    }

    //**********************************************************************************************
    //Description: Event Listener for the linear acceleration sensor
    //Input: SensorEvent event, sensor info from the phones hardware
    //Result: !!!!!!!!!!!!!!
    //**********************************************************************************************
    @Override
    public void onSensorChanged(SensorEvent event) {
        xValue = event.values[0]; //x-axis (wide)
        yValue = event.values[1]; //y-axis (tall)
        zValue = event.values[2]; //z-axis (screen)

        if (type) {
            //trigger fall detection class somewhere in here based on conditionals
            if (Math.abs(xValue) > startThreshold || Math.abs(yValue) > startThreshold || Math.abs(zValue) > startThreshold) {
                Log.d("FALL", "Potential Fall");
                FallDetection.run();
            }
        }
    }

}
