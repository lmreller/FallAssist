package com.struggleassist.Controller;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.CountDownTimer;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.struggleassist.Controller.IncidentRecording.LocationRecorder;
import com.struggleassist.Controller.IncidentRecording.RecordingController;
import com.struggleassist.Controller.SensorControllers.AccelerationController;
import com.struggleassist.Controller.SensorControllers.GravityController;
import com.struggleassist.Model.ViewContext;
import com.struggleassist.View.Notifications.NotificationController;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Ryan on 2/28/2018.
 */

public class FallDetection{
    private static boolean detectionInitialized = false;
    private static boolean notificationInitialized = false;

    private static RecordingController recordingController;

    private static final int timerLength = 1000;
    private static final int tickLength = 50;

    private static AccelerationController accel;
    private static GravityController grav;

    private static ArrayList<Float> fallData = new ArrayList<>();

    private static float incidentScore;

    private static boolean isIncident;

    private static IntentFilter filter;
    private static BroadcastReceiver notificationReceiver;
    private static String userResponse;

    private static String address;

    private static SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(ViewContext.getContext());
    private static boolean fallDetectionPref = settings.getBoolean("pref_enable_fall_detection",false);


    public FallDetection(){
        filter = new IntentFilter("NotificationControllerBroadcast");
        startDetection();
        startNotification(NotificationController.IDLE_ACTION);
    }

    //-----Starting and Stopping Actions-----//

    public static void startDetection() {
        if (fallDetectionPref) {
            Log.d("START", "Start start()");
            accel = new AccelerationController(ViewContext.getContext(), true);
            accel.start();
            detectionInitialized = true;
        }
    }

    public static void stopDetection(){
        accel.stopSensor();
        detectionInitialized=false;
    }

    public boolean isDetectionInitialized(){
        return detectionInitialized;
    }

    public static void runAlgorithm(){
        Log.d("RUN", "Start runAlgorithm()");

        accel = new AccelerationController(ViewContext.getContext(), false);
        grav = new GravityController(ViewContext.getContext());
        accel.start();
        grav.start();


        new CountDownTimer(timerLength,tickLength){

            public void onTick(long millisUntilFinished){
                int direction = grav.findFallDirection();
                fallData.add(accel.getDirectionalAccelData(direction));
            }

            public void onFinish(){
                incidentScore = findIncidentScore();
                fallData.clear();
                if(incidentScore > 2.3){
                    //Fall has been detected
                    isIncident = true;
                    recordingController = new RecordingController();
                    recordingController.startRecording();
                    address = recordingController.getAddress();
                    startNotification(NotificationController.ALERT_ACTION);
                } else {
                    //Fall has not been detected
                    isIncident = false;
                }
                accel.stopSensor();
                grav.stopSensor();
                startDetection();
            }
        }.start();
    }

    //-----Fall Calculations-----//

    private static float findIncidentScore(){

        Collections.sort(fallData);

        float Q1Weight = 0.1f;
        float medWeight = 0.1f;
        float Q3Weight = 0.1f;
        float maxWeight = 0.1f;
        float avgWeight = 0.6f;

        float Q1  = findQ1();
        float med = findMed();
        float Q3  = findQ3();
        float max = findMax();
        float avg = findAvg();

        float score = (Q1Weight*Q1) + (Q3Weight * Q3) + (maxWeight * max) + (avgWeight * avg);

        Log.d("Q1", Float.toString(Q1));
        Log.d("MED", Float.toString(med));
        Log.d("Q3", Float.toString(Q3));
        Log.d("MAX", Float.toString(max));
        Log.d("AVG", Float.toString(avg));
        Log.d("SCORE", Float.toString(score));

        return score;
    }

    private static float findQ1() {
        ArrayList<Float> lowerHalf = new ArrayList<Float>();
        for (int i = 0; i < fallData.size() / 2; i++)
            lowerHalf.add(fallData.get(i));

        float upperMed = lowerHalf.get(lowerHalf.size() / 2);
        float lowerMed = lowerHalf.get((lowerHalf.size() / 2) - 1);

        return (upperMed + lowerMed) / 2;
    }

    private static float findMed() {
        float upperMed = fallData.get(fallData.size() / 2);
        float lowerMed = fallData.get((fallData.size() / 2) - 1);
        return (upperMed + lowerMed) / 2;
    }

    private static float findQ3() {
        ArrayList<Float> upperHalf = new ArrayList<Float>();
        for (int i = fallData.size() / 2; i < fallData.size(); i++)
            upperHalf.add(fallData.get(i));

        float upperMed = upperHalf.get(upperHalf.size() / 2);
        float lowerMed = upperHalf.get((upperHalf.size() / 2) - 1);

        return (upperMed + lowerMed) / 2;
    }

    private static float findMax() {
        return fallData.get(fallData.size() - 1);
    }

    private static float findAvg() {
        float total = 0;

        for (int i = 0; i < fallData.size(); i++) {
            total += fallData.get(i);
        }
        return total / fallData.size();
    }

    //-----Notification Actions-----//

    private static void startNotification(String action) {
        notificationReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //This is where we handle the user response
                userResponse = intent.getStringExtra("userResponse");

                PhoneController phone = new PhoneController();
                String userName = getUserName();
                String ecNumber = getEmergencyContactNumber();
                recordingController = new RecordingController();

                switch (userResponse) {
                    case NotificationController.STOP_ACTION:                    //Stop = stop detection
                        stopDetection();
                        stopNotification();
                        break;
                    case NotificationController.CONFIRM_ACTION:                 //Confirm = call, text, and return to idle
                        phone.makeCall(ecNumber);
                    case NotificationController.TIMEOUT_ACTION:                 //Timeout = text, and return to idle
                        address = recordingController.getAddress();
                        phone.sendSMS(userName, ecNumber, address);
                    case NotificationController.CANCEL_ACTION:                  //Cancel = return to idle
                        recordingController.stopRecording(userResponse, incidentScore);
                        startNotification(NotificationController.IDLE_ACTION);
                        break;
                    default:
                        break;
                }
            }
        };

        if(!notificationInitialized) {
            LocalBroadcastManager.getInstance(ViewContext.getContext()).registerReceiver(notificationReceiver, filter);
            notificationInitialized = true;
        }
        Intent notificationIntent = new Intent(ViewContext.getContext(), NotificationController.class);
        notificationIntent.setAction(action);
        ViewContext.getContext().startService(notificationIntent);
    }

    private static void stopNotification(){
        LocalBroadcastManager.getInstance(ViewContext.getContext()).unregisterReceiver(notificationReceiver);

        Intent notificationIntent = new Intent(ViewContext.getContext(),NotificationController.class);
        ViewContext.getContext().stopService(notificationIntent);

        notificationInitialized = false;
    }

    private static String getUserName(){
        DatabaseController db = new DatabaseController(ViewContext.getContext());
        db.open();
        Cursor cursor = db.getAllUsers();
        cursor.moveToFirst();
        return (cursor.getString(cursor.getColumnIndex("firstName")) + " "
                + cursor.getString(cursor.getColumnIndex("lastName")));
    }

    private static String getEmergencyContactNumber(){
        DatabaseController db = new DatabaseController(ViewContext.getContext());
        db.open();
        Cursor cursor = db.getAllUsers();
        cursor.moveToFirst();
        return cursor.getString(cursor.getColumnIndex("emergencyContactNumber"));
    }

}
