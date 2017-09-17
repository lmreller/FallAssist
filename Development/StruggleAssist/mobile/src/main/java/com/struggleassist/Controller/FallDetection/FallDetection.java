package com.struggleassist.Controller.FallDetection;

import android.os.CountDownTimer;

import com.struggleassist.Controller.FallDetection.SensorControllers.GravityController;
import com.struggleassist.Controller.ViewContext;

/**
 * Created by lucas on 9/14/2017.
 */

public class FallDetection {
    private static final int timerLength = 1000;
    private static final int tickLength = 10;

    public static void run(){
        GravityController grav = new GravityController(ViewContext.getContext());

        //timer for fall duration and to collect data on ticks
        new CountDownTimer(timerLength, tickLength){
            public void onTick(long millisUntilFinished){

            }

            public void onFinish(){

            }
        };
    }
}
