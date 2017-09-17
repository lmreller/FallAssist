package com.struggleassist.Controller.FallDetection.SensorControllers;


import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.hardware.TriggerEvent;
import android.hardware.TriggerEventListener;

/**
 * Created by lucas on 9/14/2017.
 * Purpose: Will control the low level interactions with the Significant Motion sensor
 */

public class SigMotionController {

    //PROBABLY NOT A GOOD SENSOR TO USE


    public SensorManager mSensorManager;
    public Sensor mSensor;
    public TriggerEventListener mTriggerEventListener;

    public SigMotionController(Context context){
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_SIGNIFICANT_MOTION);
        mTriggerEventListener = new TriggerEventListener() {
            @Override
            public void onTrigger(TriggerEvent event) {
                // Do work
                output();
            }
        };
        mSensorManager.requestTriggerSensor(mTriggerEventListener, mSensor);
    }

    public String output(){
        return "Motion Detected";
    }
}
