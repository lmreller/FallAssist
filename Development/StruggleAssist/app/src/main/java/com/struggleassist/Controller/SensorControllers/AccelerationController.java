package com.struggleassist.Controller.SensorControllers;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.util.Log;

import com.struggleassist.Controller.FallDetection;

import java.util.ArrayList;

/**
 * Created by lucas on 9/14/2017.
 * Purpose: Will control the low level interactions with the devices linear accelerometer (no gravity)
 */

public class AccelerationController extends SensorController {

    private static AccelerationController INSTANCE = null;

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

    private final static float startThreshold = 4;
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
    private AccelerationController(Context context) {
        super(context);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        this.type = type;
        triggered = false;
    }

    public static AccelerationController getInstance(Context context){
        if(INSTANCE == null){
            INSTANCE = new AccelerationController(context);
        }
        return INSTANCE;
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

        linearAcceleration[0] = input[0] - gravity[0]; //x-axis (wide)
        linearAcceleration[1] = input[1] - gravity[1]; //y-axis (tall)
        linearAcceleration[2] = input[2] - gravity[2]; //z-axis (screen)

        xValue = linearAcceleration[0];
        yValue = linearAcceleration[1];
        zValue = linearAcceleration[2];

        double trueAccel = Math.sqrt((xValue*xValue)+(yValue*yValue)+(zValue*zValue));

//        Log.d("Accel: ",type+ " trueAccel: " + trueAccel + " X:"+ xValue +"| Y:"+yValue +"| Z:"+zValue);

        if (type) {
            //trigger fall detection class somewhere in here based on conditionals
            if ((trueAccel > startThreshold)) {
                Log.d("FALL", "Potential Fall");
                FallDetection fallDetection = FallDetection.getInstance();
                fallDetection.runAlgorithm();
                linearAcceleration[0] = 0;
                linearAcceleration[1] = 0;
                linearAcceleration[2] = 0;
            }
        }
    }

    public float getDirectionalAccelData(int direction){
        accelData[0] = Math.abs(getxValue());
        accelData[1] = Math.abs(getyValue());
        accelData[2] = Math.abs(getzValue());

        return(accelData[direction]);
    }

    public void setType(boolean t){
        type = t;
    }

}
