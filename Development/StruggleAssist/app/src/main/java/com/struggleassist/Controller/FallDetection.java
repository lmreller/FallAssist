package com.struggleassist.Controller;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.support.v4.app.NotificationCompat;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.struggleassist.Controller.IncidentRecording.LocationRecorder;
import com.struggleassist.Controller.IncidentRecording.RecordingController;
import com.struggleassist.Controller.SensorControllers.AccelerationController;
import com.struggleassist.Controller.SensorControllers.GravityController;
import com.struggleassist.Controller.SensorControllers.SensorController;
import com.struggleassist.Model.ViewContext;
import com.struggleassist.R;
import com.struggleassist.View.Activities.LaunchActivity;
import com.struggleassist.View.Activities.MainActivity;
import com.struggleassist.View.Notifications.NotificationController;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Ryan on 2/28/2018.
 */

public class FallDetection extends Service {

    private static final int timerLength = 1000;
    private static final int tickLength = 50;

    private static AccelerationController accel = null;
    private static GravityController grav = null;

    private static ArrayList<Float> fallData = new ArrayList<>();

    private static float incidentScore;

    private static String address;
    private static int uniqueID = 2112;

    private static SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(ViewContext.getContext());

    private static float threshold;

    private static FallDetection mFallDetection = null;

    public FallDetection(){ }

    @Override
    public void onCreate(){
        super.onCreate();

        Log.d("Fall Detection: ","onCreate()");

//        if(Build.VERSION.SDK_INT >= 26){
//            NotificationController.createChannel();
//        }

        Notification notification = NotificationController.getNotificationBuilder(NotificationController.IDLE_ACTION).build();
        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        this.startForeground(uniqueID,notification);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);



        mFallDetection = this;

        /*----------------BEGIN BUILDING THE SERVICE NOTIFICATION----------------*/
        Notification notification = NotificationController.getNotificationBuilder(intent.getAction()).build();
        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        this.startForeground(uniqueID,notification);
        /*-----------------END BUILDING THE SERVICE NOTIFICATION-----------------*/

        /*----------------BEGIN HANDLING THE SERVICE NOTIFICATION----------------*/

        if(intent != null) {

            if (NotificationController.STOP_ACTION.equalsIgnoreCase(intent.getAction())) {
                stopDetection();

                Intent serviceIntent = new Intent(this, FallDetection.class);
                stopService(serviceIntent);
            }

            if(MainActivity.getUserType().equals("primaryUser")) {

                if (!NotificationController.STOP_ACTION.equalsIgnoreCase(intent.getAction())) {
                    startDetection();

                    PhoneController phoneController = new PhoneController();
                    String userName = getUserName();
                    String ecNumber = getEmergencyContactNumber();

                    switch (intent.getAction()) {

                        case NotificationController.CONFIRM_ACTION:                 //Confirm: Make call, get address, sendSMS, stop recording
                            phoneController.makeCall(ecNumber);                         //makeCall
                            address = RecordingController.getAddress();                 //Get address


                        case NotificationController.TIMEOUT_ACTION:                 //Timeout: Get address, send SMS, stop recording
                            phoneController.sendSMS(userName, ecNumber, address);       //sendSMS

                        case NotificationController.CANCEL_ACTION:                  //Cancel: Stop recording
                            RecordingController.stopRecording(intent.getAction(), incidentScore);    //Stop recording
                            NotificationController.notificationTimer.cancel();                             //cancel timer (already finished on timeout)
                            TrendAnalysis.updateValue();
                            break;
                        default:
                            break;
                    }
                }
            }

            if(MainActivity.getUserType().equals("emergencyContact")){

                DatabaseController db = new DatabaseController(ViewContext.getContext());
                db.open();
                Cursor dbCursor = db.getAllUsers();
                dbCursor.moveToFirst();
                String ecID = dbCursor.getString(dbCursor.getColumnIndex("emergencyContactID"));
                dbCursor.close();
                db.close();

                Cursor cursor = ViewContext.getContext().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                        ContactsContract.CommonDataKinds.Phone._ID + " ='" + ecID + "'", null, null);
                cursor.moveToFirst();
                String ecNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER));

                SmsListener listener = new SmsListener();
                listener.setEcNumber(ecNumber);
                IntentFilter filter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
                registerReceiver(listener,filter);
            }
        /*-----------------END HANDLING THE SERVICE NOTIFICATION-----------------*/


    }
        return Service.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent){ return null; }

    @Override
    public void onDestroy(){
        stopDetection();
        super.onDestroy();
    }

    //-----Starting and Stopping Actions-----//

    public static void startDetection() {
        accel = AccelerationController.getInstance(ViewContext.getContext());
        Log.d("START", "Start start()");
        accel.setType(true);
        accel.start();
    }

    public static void stopDetection(){
        if(accel!=null)
            accel.stopSensor();
    }

    public void runAlgorithm(){
        Log.d("RUN", "Start runAlgorithm()");

        grav = new GravityController(ViewContext.getContext());
        accel.setType(false);
        grav.start();


        new CountDownTimer(timerLength,tickLength){

            public void onTick(long millisUntilFinished){
                int direction = grav.findFallDirection();
                fallData.add(accel.getDirectionalAccelData(direction));
            }

            public void onFinish(){
                grav.stopSensor();
                accel.stopSensor();

                incidentScore = findIncidentScore();

                threshold = settings.getFloat("trend_analysis_value",1.5F);
                //Toast.makeText(ViewContext.getContext(),"Threshold: "+incidentScore,Toast.LENGTH_LONG).show();

                if(incidentScore > threshold){
                //if(incidentScore>1.5){    //Static threshold
                    //Fall has been detected
                    Intent startIntent = new Intent(ViewContext.getContext(), FallDetection.class);
                    startIntent.setAction(NotificationController.ALERT_ACTION);
                    RecordingController.startRecording();
                    ViewContext.getContext().startService(startIntent);
                } else {
                    //Fall has not been detected
                    startDetection();
                }
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

    //-----Notification-----//

    //-----Getters-----//

    private static String getUserName(){
        DatabaseController db = new DatabaseController(ViewContext.getContext());
        db.open();
        Cursor cursor = db.getAllUsers();
        cursor.moveToFirst();
        String name = cursor.getString(cursor.getColumnIndex("firstName")) + " "
                + cursor.getString(cursor.getColumnIndex("lastName"));
        db.close();
        return name;
    }

    private static String getEmergencyContactNumber(){
        DatabaseController db = new DatabaseController(ViewContext.getContext());
        db.open();
        Cursor cursor = db.getAllUsers();
        cursor.moveToFirst();
        String number = cursor.getString(cursor.getColumnIndex("emergencyContactNumber"));
        db.close();
        return number;
    }

    public static FallDetection getInstance(){
        return mFallDetection;
    }

}