package com.struggleassist.Controller.FallDetection;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.struggleassist.Controller.FallDetection.SensorControllers.AccelerationController;
import com.struggleassist.Controller.FallDetection.SensorControllers.GravityController;
import com.struggleassist.Controller.IncidentRecording.RecordingController;
import com.struggleassist.Model.Record;
import com.struggleassist.Model.ViewContext;
import com.struggleassist.R;
import com.struggleassist.View.Activities.LaunchActivity;
import com.struggleassist.View.Notifications.Notification;
import com.struggleassist.View.Notifications.ToastController;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by lucas on 9/14/2017.
 */

public class FallDetection extends Service {
    private static final int timerLength = 1000;
    private static final int tickLength = 50;

    private static AccelerationController accel;
    private static GravityController grav;

    private static float accelData[] = new float[3];
    private static float gravData[] = new float[3];
    private static ArrayList<Float> fallData = new ArrayList<Float>();

    private static ToastController fallDetected;
    private static ToastController potentialFall;
    private static ToastController falseAlarm;
    private static RecordingController recording;

    private static Notification notification;

    private static float Q1;
    private static float med;
    private static float Q3;
    private static float max;
    private static float avg;
    private static float incidentScore;
    private static boolean incident;

    //Start fall detection
    @Override
    public void onCreate(){
        startDetection();
    }

    //Create service as a background service (sticky)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        super.onStartCommand(intent,flags,startId);
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Intent bIntent = new Intent(FallDetection.this, LaunchActivity.class);
        PendingIntent pbIntent = PendingIntent.getActivity(FallDetection.this,0,bIntent,0);
        NotificationCompat.Builder bBuilder =
                new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Fall Detection")
                .setAutoCancel(true)
                .setOngoing(true)
                .setContentIntent(pbIntent);
        this.startForeground(1,bBuilder.build());

        return Service.START_STICKY;
    }

    //Needs to be overridden for Service
    @Override
    public IBinder onBind(Intent intent){
        return null;
    }


    public static void startDetection() {
        Log.d("START", "Start start()");
        accel = new AccelerationController(ViewContext.getContext(), true);
        accel.start();
    }

    public static void runAlgorithm() {
        Log.d("RUN", "Start run()");

        accel = new AccelerationController(ViewContext.getContext(), false);
        grav = new GravityController(ViewContext.getContext());
        accel.start();
        grav.start();

        recording = new RecordingController();
        recording.startRecording();

        potentialFall = new ToastController("Potential Fall!");
        fallDetected = new ToastController("Fall Detected!");
        falseAlarm = new ToastController("False Alarm!");

        notification = new Notification(ViewContext.getContext());


        potentialFall.showToastShort();

        //timer for fall duration and to collect data on ticks
        new CountDownTimer(timerLength, tickLength) {

            int index;

            public void onTick(long millisUntilFinished) {
                accelData[0] = Math.abs(accel.getxValue());
                accelData[1] = Math.abs(accel.getyValue());
                accelData[2] = Math.abs(accel.getzValue());
                gravData[0] = grav.getxValue();
                gravData[1] = grav.getyValue();
                gravData[2] = grav.getzValue();
                index = findFallDirection();
                fallData.add(accelData[index]);
            }

            public void onFinish() {
                Collections.sort(fallData);

                Q1 = findQ1();
                med = findMed();
                Q3 = findQ3();
                max = findMax();
                avg = calculateAverage(fallData);

                incidentScore = calcWeightedScore();

                Log.d("Q1", Float.toString(Q1));
                Log.d("MED", Float.toString(med));
                Log.d("Q3", Float.toString(Q3));
                Log.d("MAX", Float.toString(max));
                Log.d("AVG", Float.toString(avg));
                Log.d("SCORE", Float.toString(incidentScore));

                if (incidentScore > 3) {
                    notification.Notify("Fall detected!", ""); //Notify user of fall (Title, description)
                    incident = true;
                    //fallDetected.showToastLong(); //debugging purposes only
                } else {
                    falseAlarm.showToastLong();//debugging purposes only
                    incident = false;
                }
                Record record = new Record(incident,incidentScore);     //Create record object

                accel.stopSensor();
                grav.stopSensor();
                startDetection();
            }
        }.start();
    }

    private static int findFallDirection() {
        int direction = 4;

        if (gravData[0] >= gravData[1] && gravData[0] >= gravData[2])
            direction = 0;
        else if (gravData[1] >= gravData[0] && gravData[1] >= gravData[2])
            direction = 1;
        else if (gravData[2] >= gravData[0] && gravData[2] >= gravData[1])
            direction = 2;
        return direction;
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

    private static float calcWeightedScore() {
        float Q1Weight = 0.1f;
        //float medWeight = 0.1f;
        float Q3Weight = 0.1f;
        float maxWeight = 0.2f;
        float avgWeight = 0.6f;

        float score = (Q1Weight * Q1) + (Q3Weight * Q3) + (maxWeight * max) + (avgWeight * avg);

        return score;
    }

    private static float calculateAverage(ArrayList<Float> list) {
        float total = 0;

        for (int i = 0; i < list.size(); i++) {
            total += list.get(i);
        }
        return total / list.size();
    }
}
