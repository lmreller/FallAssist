package com.struggleassist.Controller.FallDetection.SensorControllers.Unused;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

/**
 * Created by Lucas on 9/25/2017.
 */

public class SensorController implements SensorEventListener{

    protected SensorManager mSensorManager;
    protected Sensor mSensor;

    protected float xValue;
    protected float yValue;
    protected float zValue;

    public SensorController(Context context){
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
    }

    //**********************************************************************************************
    //Description: registers the event listener to the sensor
    //Result: event listener is now registered and the sensor is ready to use
    //**********************************************************************************************
    public void start(){
        mSensorManager.registerListener(this, mSensor , SensorManager.SENSOR_DELAY_NORMAL);
    }

    //**********************************************************************************************
    //Description: unregisters the event listener to conserve battery life
    //Result: event listener no longer exists so sensor is no longer in use
    //**********************************************************************************************
    public void end(){
        mSensorManager.unregisterListener(this);
    }

    //Implemented in subclasses
    @Override
    public void onSensorChanged(SensorEvent event){}

    //GETTERS
    public float getxValue() {
        return xValue;
    }

    public float getyValue() {
        return yValue;
    }

    public float getzValue() {
        return zValue;
    }
    //END GETTERS

    //Un-used method required by SensorEventListener
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}
}
