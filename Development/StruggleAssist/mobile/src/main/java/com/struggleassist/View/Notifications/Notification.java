package com.struggleassist.View.Notifications;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;

import com.struggleassist.Model.ViewContext;
import com.struggleassist.R;
import com.struggleassist.View.Activities.LaunchActivity;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by Ryan on 10/20/2017.
 */

public class Notification {

    //Give notification context
    Context nContext;
    public Notification(Context nContext){
        this.nContext = nContext;
    }

    NotificationCompat.Builder notification;
    private static final int uniqueID = 2112;

    public void Notify(String notificationTitle, String notificationMessage){

        notification = new NotificationCompat.Builder(nContext);
        notification.setAutoCancel(true);

        //Builds notification

        notification.setSmallIcon(R.mipmap.ic_launcher); //App icon
        //notification.setTicker(); //Ticker text
        notification.setWhen(System.currentTimeMillis()); //Set current time
        notification.setVibrate(new long[]{0,1000,500});
        notification.setLights(Color.WHITE,1,0);
        notification.setContentTitle(notificationTitle); //Set notification title
        notification.setContentText(notificationMessage); //Set notification message

        //Brings user to LaunchActivity upon selecting the notification
        Intent intent = new Intent(nContext, LaunchActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(nContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        notification.setContentIntent(pendingIntent);

        //Issues notification
        NotificationManager notificationManager = (NotificationManager) nContext.getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(uniqueID,notification.build());
    }
}
