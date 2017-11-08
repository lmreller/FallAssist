package com.struggleassist.View.Notifications;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;

import com.struggleassist.R;
import com.struggleassist.View.Activities.LaunchActivity;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by Ryan on 10/20/2017.
 * Purpose: This class is designed only to push notifications to the user
 * This class formats the notification and sets up basic user interaction
 * NotificationReceiver.java is responsible for the outcomes of the actions (buttons) pressed
 */

public class Notification {

    //Give notification context
    Context nContext;
    public Notification(Context nContext){
        this.nContext = nContext;
    }

    NotificationCompat.Builder notification;
    private static final int uniqueID = 2112;

    public static final String CANCEL_ACTION = "com.struggleassist.View.Notifications.Notification.cancelAction";
    public static final String CONFIRM_ACTION = "com.struggleassist.View.Notifications.Notification.confirmAction";
    public static final String TIMEOUT_ACTION = "com.struggleassist.View.Notifications.Notification.timeoutAction";

    public void Notify(String notificationTitle, String notificationMessage){

        //Cancel action intent
        Intent cancelIntent = new Intent(nContext,NotificationReceiver.class)
            .setAction(CANCEL_ACTION)
            .putExtra("uniqueID",uniqueID);
        PendingIntent cancelPendingIntent = PendingIntent.getBroadcast(nContext,0,cancelIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        //Cancel action's formatting
        NotificationCompat.Action cancelAction
                = new NotificationCompat.Action.Builder(R.mipmap.ic_launcher_round,"Cancel",cancelPendingIntent).build();

        //Confirm action intent
        Intent confirmIntent = new Intent(nContext,NotificationReceiver.class)
            .setAction(CONFIRM_ACTION)
            .putExtra("uniqueID",uniqueID);
        PendingIntent confirmPendingIntent = PendingIntent.getBroadcast(nContext, 0, confirmIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        //Confirm action's formatting
        NotificationCompat.Action confirmAction
                = new NotificationCompat.Action.Builder(R.mipmap.ic_launcher_round,"Confirm",confirmPendingIntent).build();

        //Notification Timeout
        AlarmManager alarmManager = (AlarmManager) nContext.getSystemService(Context.ALARM_SERVICE);
        Intent timeoutIntent = new Intent(nContext,NotificationReceiver.class)
            .setAction(TIMEOUT_ACTION)
            .putExtra("uniqueID",uniqueID);
        PendingIntent timeoutPendingIntent = PendingIntent.getBroadcast(nContext, 0, timeoutIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        //Creates notification properties
        notification = new NotificationCompat.Builder(nContext)
            .setAutoCancel(true)                                  //Dismisses notification when tapped (does not dismiss when actions are pressed, see NotificationReceiver.java)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)  //Allows for lock screen interaction
            .setSmallIcon(R.mipmap.ic_launcher)         //App icon
            .setWhen(System.currentTimeMillis())        //Set current time
            .setVibrate(new long[]{0,1000,500})         //Vibration pattern
            .setLights(Color.WHITE,1,0)    //Notification light color/pattern
            .setContentTitle(notificationTitle)         //Set notification title
            .setContentText(notificationMessage)        //Set notification message
            .addAction(cancelAction)                    //Cancel button
            .addAction(confirmAction);                  //Confirm button

        //Brings user to LaunchActivity upon selecting the notification
        Intent launchIntent = new Intent(nContext, LaunchActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(nContext, 0, launchIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        notification.setContentIntent(pendingIntent);

        //Issues notification
        NotificationManager notificationManager = (NotificationManager) nContext.getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(uniqueID,notification.build());
        //alarmManager.setExact(AlarmManager.RTC,15000,timeoutPendingIntent);
    }
}