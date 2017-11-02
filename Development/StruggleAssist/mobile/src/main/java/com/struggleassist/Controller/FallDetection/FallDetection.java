package com.struggleassist.Controller.FallDetection;

import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;

import com.struggleassist.Controller.FallDetection.SensorControllers.AccelerationController;
import com.struggleassist.Controller.FallDetection.SensorControllers.GravityController;
import com.struggleassist.Model.ViewContext;
import com.struggleassist.View.Notifications.Notification;
import com.struggleassist.View.Notifications.ToastController;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by lucas on 9/14/2017.
 */

public class FallDetection {
    private static final int timerLength = 2000;
    private static final int tickLength = 50;

    private static AccelerationController accel;
    private static GravityController grav;

    private static float accelData[] = new float[3];
    private static float gravData[] = new float[3];
    private static ArrayList<Float> fallData = new ArrayList<Float>();

    private static ToastController fallDetected;
    private static ToastController potentialFall;
    private static ToastController falseAlarm;

    private static Notification notification;

    private static float min;
    private static float Q1;
    private static float med;
    private static float Q3;
    private static float max;
    private static float incidentScore;
    private static float avg;

    public static void start(){
        accel = new AccelerationController(ViewContext.getContext(), true);
    }

    public static void run(){
        accel.stopSensor();
        accel = new AccelerationController(ViewContext.getContext(), false);
        grav = new GravityController(ViewContext.getContext());

        potentialFall = new ToastController("Potential Fall!");
        fallDetected = new ToastController("Fall Detected!");
        falseAlarm = new ToastController("False Alarm!");

        notification = new Notification(ViewContext.getContext());


        potentialFall.showToastShort();

        //timer for fall duration and to collect data on ticks
        new CountDownTimer(timerLength, tickLength){

            int index;

            public void onTick(long millisUntilFinished){
                accelData[0] = Math.abs(accel.getxValue());
                accelData[1] = Math.abs(accel.getyValue());
                accelData[2] = Math.abs(accel.getzValue());
                gravData[0] = grav.getxValue();
                gravData[1] = grav.getyValue();
                gravData[2] = grav.getzValue();
                index = findFallDirection();
                fallData.add(accelData[index]);
            }

            public void onFinish(){

                Collections.sort(fallData);

                min = findMin();
                Q1 = findQ1();
                med = findMed();
                Q3 = findQ3();
                max = findMax();

                incidentScore = calcWeightedScore();

                Log.d("MIN", Float.toString(min));
                Log.d("Q1", Float.toString(Q1));
                Log.d("MED", Float.toString(med));
                Log.d("Q3", Float.toString(Q3));
                Log.d("MAX", Float.toString(max));
                Log.d("SCORE", Float.toString(incidentScore));

                if(incidentScore > 4)
                    notification.Notify("Fall detected!",""); //Notify user of fall (Title, description)
                    //fallDetected.showToastLong();
                else
                    falseAlarm.showToastLong();//debugging purposes only

                accel.stopSensor();
                grav.stopSensor();
                start();
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
        return fallData.get(0);
    }

    private static float findQ1(){
        ArrayList<Float> lowerHalf = new ArrayList<Float>();
        for(int i = 0; i < fallData.size()/2; i++)
            lowerHalf.add(fallData.get(i));

        float upperMed = lowerHalf.get(lowerHalf.size()/2);
        float lowerMed = lowerHalf.get((lowerHalf.size()/2)-1);

        return(upperMed+lowerMed)/2;
    }

    private static float findMed(){
        float upperMed = fallData.get(fallData.size()/2);
        float lowerMed = fallData.get((fallData.size()/2)-1);
        return(upperMed+lowerMed)/2;
    }

    private static float findQ3(){
        ArrayList<Float> upperHalf = new ArrayList<Float>();
        for(int i = fallData.size()/2; i < fallData.size(); i++)
            upperHalf.add(fallData.get(i));

        float upperMed = upperHalf.get(upperHalf.size()/2);
        float lowerMed = upperHalf.get((upperHalf.size()/2)-1);

        return(upperMed+lowerMed)/2;
    }

    private static float findMax(){
        return fallData.get(fallData.size()-1);
    }

    private static float calcWeightedScore(){
        float minWeight = 0.1f;
        float Q1Weight = 0.125f;
        float avgWeight = 0.25f;
        float Q3Weight = 0.125f;
        float maxWeight = 0.4f;

        float score = (minWeight*min) + (Q1Weight*Q1) + (avgWeight*avg) + (Q3Weight*Q3) + (maxWeight*max);

        return score;
    }

    private static float calculateAverage(ArrayList<Float> list){
        float total = 0;

        for(int i = 0; i < list.size(); i++){
            total += list.get(i);
        }
        return total/list.size();
    }
}
