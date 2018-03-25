package com.struggleassist.View.Notifications;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
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
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.struggleassist.Controller.FallDetection;
import com.struggleassist.Controller.IncidentRecording.RecordingController;
import com.struggleassist.Model.ViewContext;
import com.struggleassist.R;
import com.struggleassist.View.Activities.LaunchActivity;

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

    @RequiresApi(Build.VERSION_CODES.O)
    public static void createChannel(){
        NotificationManager notificationManager = (NotificationManager) ViewContext.getContext().getSystemService(Context.NOTIFICATION_SERVICE);

        String id = CHANNEL_ID;
        CharSequence name = "Struggle Assist";
        String description = "Fall Detection";
        int importance = NotificationManager.IMPORTANCE_HIGH;

        NotificationChannel notificationChannel = new NotificationChannel(id, name, importance);
        notificationChannel.setDescription(description);
        notificationChannel.setShowBadge(true);
        notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

        notificationManager.createNotificationChannel(notificationChannel);
    }

    public static NotificationCompat.Builder getNotificationBuilder(String action){

        Log.d("NotificationController","getNotificationBuilder");   

        NotificationCompat.Builder builder = new NotificationCompat.Builder(ViewContext.getContext(),CHANNEL_ID);

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
                    .setOngoing(true)
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .setCustomBigContentView(alertView)
                    .setContentIntent(pIntent);

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
                    .setOngoing(true)
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .setCustomBigContentView(idleView)
                    .setContentIntent(pIntent);
        }
        return builder;
    }


}