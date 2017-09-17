package com.struggleassist.Controller.FallDetection;

/**
 * Created by lucas on 9/15/2017.
 */

public class SensorData {
    private static float accelData[] = new float[3];
    private static float gravData[] = new float[3];


    public static void setAccelX(float x){accelData[0] = x;}
    public static void setAccelY(float y){accelData[1] = y;}
    public static void setAccelZ(float z){accelData[2] = z;}
    public static float getAccelX(){return accelData[0];}
    public static float getAccelY(){return accelData[1];}
    public static float getAccelZ(){return accelData[2];}

    public static void setGravX(float x){gravData[0] = x;}
    public static void setGravY(float y){gravData[1] = y;}
    public static void setGravZ(float z){gravData[2] = z;}
    public static float getGravX(){return gravData[0];}
    public static float getGravY(){return gravData[1];}
    public static float getGravZ(){return gravData[2];}

}
