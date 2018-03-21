package com.struggleassist.Controller.SensorControllers;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.util.Log;

import com.struggleassist.Controller.FallDetection;
/**
 * Created by lucas on 9/14/2017.
 * Purpose: Will control the low level interactions with the devices linear accelerometer (no gravity)
 */

public class AccelerationController extends SensorController {

    //Constants for the low-pass filters
    private float timeConstant = 0.18f;
    private float alpha = 0.9f;
    private float dt = 0;

    // Timestamps for the low-pass filters
    private float timestamp = System.nanoTime();
    private float timestampOld = System.nanoTime();

    //Gravity and linear acceleration components
    private float[] gravity = new float[]{ 0, 0, 0 };
    private float[] linearAcceleration = new float[]{ 0, 0, 0 };
    //Raw sensor data
    private float[] input = new float[]{0,0,0};

    private int count = 0;

    private final static int startThreshold = 5;
    private boolean type;
    private boolean triggered;

    private static float accelData[] = new float[3];

    //**********************************************************************************************
    //Description: Constuctor for the AccelerationController
    //Input: Context context, should be 'this' to reference the device
    //       boolean type, true is to start fall detection; false is for data collection
    //Result: a new sensor setup is configured and registered with a listener for the linear
    //        acceleration sensor in the device
    //**********************************************************************************************
    public AccelerationController(Context context, boolean type) {
        super(context);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        this.type = type;
        triggered = false;
    }

    //**********************************************************************************************
    //Description: Event Listener for the linear acceleration sensor
    //Input: SensorEvent event, sensor info from the phones hardware
    //Result: !!!!!!!!!!!!!!
    //**********************************************************************************************
    @Override
    public void onSensorChanged(SensorEvent event) {
        System.arraycopy(event.values,0,input,0,event.values.length);

        timestamp = System.nanoTime();
        // Find the sample period (between updates).
        // Convert from nanoseconds to seconds
        dt = 1 / (count / ((timestamp - timestampOld) / 1000000000.0f));
        count++;

        alpha = timeConstant/(timeConstant+dt);

        gravity[0] = alpha * gravity[0] + (1 - alpha) * input[0];
        gravity[1] = alpha * gravity[1] + (1 - alpha) * input[1];
        gravity[2] = alpha * gravity[2] + (1 - alpha) * input[2];

        xValue = input[0] - gravity[0]; //x-axis (wide)
        yValue = input[1] - gravity[1]; //y-axis (tall)
        zValue = input[2] - gravity[2]; //z-axis (screen)

        if (type) {
            //trigger fall detection class somewhere in here based on conditionals
            //Log.d("Accel: ","X:"+xValue +"| Y:"+yValue +"| Z:"+zValue);
            if ((Math.abs(xValue) > startThreshold || Math.abs(yValue) > startThreshold || Math.abs(zValue) > startThreshold) && !triggered) {
                triggered = true;
                Log.d("FALL", "Potential Fall");
                stopSensor();
                FallDetection.runAlgorithm();
            }
        }
    }

    public float getDirectionalAccelData(int direction){
        accelData[0] = Math.abs(getxValue());
        accelData[1] = Math.abs(getyValue());
        accelData[2] = Math.abs(getzValue());

        return(accelData[direction]);
    }

}
