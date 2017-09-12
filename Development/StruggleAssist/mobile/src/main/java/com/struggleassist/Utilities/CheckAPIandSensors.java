package com.struggleassist.Utilities;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

/**
 * Created by Lucas on 9/11/2017.
 * Purpose: This class will be used to determine the api level and hi-fi sensor support of the given phone
 */

public class CheckAPIandSensors {
    //variable to hold the api level that support high accuracy sensors
    private static final int HighAccuracySensorsAPI = 23;
    private static final int DeviceAPI = Build.VERSION.SDK_INT;

    //**********************************************************************************************
    //Description: determines if a device has hi-fi sensors or not
    //
    //Input: Context context, should be 'this' to reference the device
    //
    //Result: returns true if hi-fi sensors are supported
    //        returns false if hi-fi sensors are not supported
    //**********************************************************************************************
    public static boolean highAccuracySensorSupport(Context context){
        final PackageManager packageManager = context.getPackageManager();

        //the sensor check only works on API levels 23 and up
        if(DeviceAPI >= HighAccuracySensorsAPI) {
            //returns false if the device doesn't support hi-fi sensors
            return packageManager.hasSystemFeature("android.hardware.sensor.hifi_sensors");
        }
        else{
            return false;
        }
    }

    //**********************************************************************************************
    //Description: gets the devices current API level
    //
    //Input: N/A
    //
    //Result: returns an integer value that is equivalent to the devices API level
    //**********************************************************************************************
    public static int getAPINumber(){
        return DeviceAPI;
    }
}
