package com.struggleassist.View.Notifications;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.struggleassist.Controller.FallDetection;
import com.struggleassist.Controller.IncidentRecording.RecordingController;
import com.struggleassist.Model.ViewContext;
import com.struggleassist.R;
import com.struggleassist.View.Activities.LaunchActivity;

import static com.struggleassist.Model.ViewContext.getContext;

/**
 * Created by Ryan on 3/1/2018.
 */

public class NotificationController {

    public static final String MAIN_ACTION = "com.struggleassist.View.Notifications.NotificationController.mainAction";         //Tap on notification, not a button
    public static final String STOP_ACTION = "com.struggleassist.View.Notifications.NotificationController.stopAction";         //Stop
    public static final String CONFIRM_ACTION = "com.struggleassist.View.Notifications.NotificationController.confirmAction";   //Tap confirm (alert)
    public static final String CANCEL_ACTION = "com.struggleassist.View.Notifications.NotificationController.cancelAction";     //Tap cancel (alert)
    public static final String TIMEOUT_ACTION = "com.struggleassist.View.Notifications.NotificationController.timeoutAction";   //Notification timeout (alert)
    public static final String IDLE_ACTION = "com.struggleassist.View.Notifications.NotificationController.idleAction";         //Update to idle
    public static final String ALERT_ACTION = "com.struggleassist.View.Notifications.NotificationController.alertAction";       //Update to alert

    private static final int notificationTimerLength = 30000;
    private static final int notificationTickLength = 1000;

    private static SharedPreferences settings;
    private static boolean notificationSound;
    private static boolean notificationVibration;

    private static final String CHANNEL_ID = "fall_detection_channel";
    public static CountDownTimer notificationTimer = new CountDownTimer(notificationTimerLength,notificationTickLength){
        public void onTick(long millisUntilFinished){
            //what to do every tick (progress bar, maybe?)
        }
        public void onFinish(){
            Intent timeoutIntent = new Intent(ViewContext.getContext(),FallDetection.class);
            timeoutIntent.setAction(NotificationController.TIMEOUT_ACTION);
            ViewContext.getContext().startService(timeoutIntent);
        }
    };


///NotificationChannels required for Oreo (API26)
//    @RequiresApi(Build.VERSION_CODES.O)
//    public static void createChannel(){
//        NotificationManager notificationManager = (NotificationManager) ViewContext.getContext().getSystemService(Context.NOTIFICATION_SERVICE);
//
//        String id = CHANNEL_ID;
//        CharSequence name = "Struggle Assist";
//        String description = "Fall Detection";
//        int importance = NotificationManager.IMPORTANCE_HIGH;
//
//        NotificationChannel notificationChannel = new NotificationChannel(id, name, importance);
//        notificationChannel.setDescription(description);
//        notificationChannel.setShowBadge(true);
//        notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
//
//        notificationManager.createNotificationChannel(notificationChannel);
//    }

    public static NotificationCompat.Builder getNotificationBuilder(String action){


        settings = PreferenceManager.getDefaultSharedPreferences(ViewContext.getContext());
        notificationSound = settings.getBoolean("pref_enable_sound", false);
        notificationVibration = settings.getBoolean("pref_enable_vibration",false);

        Log.d("NotificationController","getNotificationBuilder");

        NotificationCompat.Builder builder = new NotificationCompat.Builder(ViewContext.getContext());

        Intent mIntent = new Intent(ViewContext.getContext(), LaunchActivity.class);
        mIntent.setAction(NotificationController.MAIN_ACTION);
        mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pIntent = PendingIntent.getActivity(ViewContext.getContext(), 0, mIntent, 0);

        if (NotificationController.ALERT_ACTION.equalsIgnoreCase(action)){

            RemoteViews alertView = new RemoteViews("com.struggleassist", R.layout.notification_alert_layout);

            Intent confirmIntent = new Intent(ViewContext.getContext(), FallDetection.class);
            confirmIntent.setAction(NotificationController.CONFIRM_ACTION);
            PendingIntent pConfirmIntent = PendingIntent.getService(ViewContext.getContext(), 0, confirmIntent, 0);

            Intent cancelIntent = new Intent(ViewContext.getContext(), FallDetection.class);
            cancelIntent.setAction(NotificationController.CANCEL_ACTION);
            PendingIntent pCancelIntent = PendingIntent.getService(ViewContext.getContext(), 0, cancelIntent, 0);

            alertView.setOnClickPendingIntent(R.id.alertNotificationButtonConfirm, pConfirmIntent);
            alertView.setOnClickPendingIntent(R.id.alertNotificationButtonCancel, pCancelIntent);

            builder.setContent(alertView)
                    .setSmallIcon(R.mipmap.struggleassist_icon)
                    .setAutoCancel(true)
                    .setLights(Color.RED,1000,1000)
                    .setOngoing(false)
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .setCustomBigContentView(alertView)
                    .setContentIntent(pIntent);

            if(notificationSound) {
                AudioManager audioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);

                Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
                if (alarmSound == null) {
                    alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
                    if (alarmSound == null) {
                        alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                    }
                }
                if(alarmSound!=null){
                    builder.setSound(alarmSound);
                }
                audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                audioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION,audioManager.getStreamMaxVolume(AudioManager.STREAM_RING),0);

                NotificationManager mNotificationMangager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
                if(Build.VERSION.SDK_INT>=23)
                    mNotificationMangager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALL);
            }
            if(notificationVibration){
                builder.setVibrate(new long[]{0,3000,1000,3000,1000,3000,1000,3000,1000});
            }
            notificationTimer.start();

        } else {                //NotificationController.IDLE_ACTION -- default to this

            RemoteViews idleView = new RemoteViews("com.struggleassist", R.layout.notification_idle_layout);

            Intent stopIntent = new Intent(ViewContext.getContext(), FallDetection.class);
            stopIntent.setAction(NotificationController.STOP_ACTION);
            PendingIntent pStopIntent = PendingIntent.getService(ViewContext.getContext(), 0, stopIntent, 0);

            idleView.setOnClickPendingIntent(R.id.idleNotificationButtonClose, pStopIntent);
            builder.setContent(idleView)
                    .setSmallIcon(R.mipmap.struggleassist_icon)
                    .setAutoCancel(true)
                    .setOngoing(false)
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .setCustomBigContentView(idleView)
                    .setContentIntent(pIntent)
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .setVibrate(new long[]{0})
                    .setLights(Color.TRANSPARENT,0,0);
        }
        return builder;
    }


}