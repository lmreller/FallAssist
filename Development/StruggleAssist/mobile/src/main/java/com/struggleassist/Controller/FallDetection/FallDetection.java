package com.struggleassist.Controller.FallDetection;

import android.os.CountDownTimer;
import android.util.Log;

import com.struggleassist.Controller.FallDetection.SensorControllers.GravityController;
import com.struggleassist.Model.ViewContext;
import com.struggleassist.View.Notifications.ToastController;

import java.util.ArrayList;

/**
 * Created by lucas on 9/14/2017.
 */

public class FallDetection {
    private static final int timerLength = 2000;
    private static final int tickLength = 50;

    private static float accelData[] = new float[3];
    private static float gravData[] = new float[3];
    private static ArrayList<Float> fallData = new ArrayList<Float>();

    private static ToastController fallDetected;
    private static ToastController potentialFall;

    private static float min;
    private static float Q1;
    private static float avg;
    private static float Q2;
    private static float max;

    public static void run(){
        final GravityController grav = new GravityController(ViewContext.getContext());
        potentialFall = new ToastController("Potential Fall!");
        fallDetected = new ToastController("Fall Detected!");

        potentialFall.showToastShort();

        //timer for fall duration and to collect data on ticks
        new CountDownTimer(timerLength, tickLength){

            int index;

            public void onTick(long millisUntilFinished){
                accelData[0] = SensorData.getAccelX();
                accelData[1] = SensorData.getAccelY();
                accelData[2] = SensorData.getAccelZ();
                gravData[0] = grav.getxValue();
                gravData[1] = grav.getyValue();
                gravData[2] = grav.getzValue();
                index = findFallDirection();
                fallData.add(accelData[index]);
            }

            public void onFinish(){
                fallDetected.showToastLong();
                min = findMin();
                avg = calculateAverage();
                max = findMax();

                Log.d("MIN", Float.toString(min));
                Log.d("AVG", Float.toString(avg));
                Log.d("MAX", Float.toString(max));
            }
        }.start();
    }

    private static int findFallDirection(){
        int direction = 4;

        if(gravData[0] >= gravData[1] && gravData[0] >= gravData[2])
            direction = 0;
        else if(gravData[1] >= gravData[0] && gravData[1] >= gravData[2])
            direction = 1;
        else if(gravData[2] >= gravData[0] && gravData[2] >= gravData[1])
            direction = 2;
        return direction;
    }

    private static float findMin(){
        float min = 99;
        float value;
        for(int i = 0; i < fallData.size(); i++){
            value = fallData.get(i);
            if(value < min)
                min = value;
        }
        return min;
    }

    private static float calculateAverage(){
        float total = 0;

        for(int i = 0; i < fallData.size(); i++){
            total += fallData.get(i);
        }
        return total/fallData.size();
    }

    private static float findMax(){
        float max = 99;
        float value;
        for(int i = 0; i < fallData.size(); i++){
            value = fallData.get(i);
            if(value > min)
                max = value;
        }
        return max;
    }
}
