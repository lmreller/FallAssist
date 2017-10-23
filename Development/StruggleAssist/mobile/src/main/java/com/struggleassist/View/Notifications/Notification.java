package com.struggleassist.View.Notifications;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

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

    public static final String CANCEL_ACTION = "cancelAction";
    public static final String CONFIRM_ACTION = "confirmAction";

    public void Notify(String notificationTitle, String notificationMessage){


        //Cancel intent
        Intent cancelIntent = new Intent(nContext, NotificationAction.class).setAction(CANCEL_ACTION);
        PendingIntent cancelPendingIntent = PendingIntent.getService(nContext, 0, cancelIntent, PendingIntent.FLAG_ONE_SHOT);
        //Confirm intent
        Intent confirmIntent = new Intent(nContext, NotificationAction.class).setAction(CONFIRM_ACTION);
        PendingIntent confirmPendingIntent = PendingIntent.getService(nContext, 0, confirmIntent, PendingIntent.FLAG_ONE_SHOT);

        notification = new NotificationCompat.Builder(nContext);
        notification.setAutoCancel(true);
        notification.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
        notification.addAction(R.mipmap.ic_launcher_round,"Cancel",cancelPendingIntent);
        notification.addAction(R.mipmap.ic_launcher_round,"Confirm",confirmPendingIntent);

        //Builds notification

        notification.setSmallIcon(R.mipmap.ic_launcher); //App icon
        //notification.setTicker(); //Ticker text
        notification.setWhen(System.currentTimeMillis()); //Set current time
        notification.setVibrate(new long[]{0,1000,500});
        notification.setLights(Color.WHITE,1,0);
        notification.setContentTitle(notificationTitle); //Set notification title
        notification.setContentText(notificationMessage); //Set notification message

        //Brings user to LaunchActivity upon selecting the notification
        Intent launchIntent = new Intent(nContext, LaunchActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(nContext, 0, launchIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        notification.setContentIntent(pendingIntent);

        //Issues notification
        NotificationManager notificationManager = (NotificationManager) nContext.getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(uniqueID,notification.build());
    }

    public static class NotificationAction extends IntentService {
        public NotificationAction(){
           super(NotificationAction.class.getSimpleName());
        }
        @Override
        protected void onHandleIntent(Intent intent) {
            String action = intent.getAction();
            if(CANCEL_ACTION.equals(action)){
                Toast.makeText(getApplicationContext(),"Canceled",Toast.LENGTH_SHORT);
            }
            if(CONFIRM_ACTION.equals(action)){
                Toast.makeText(getApplicationContext(),"Confirmed",Toast.LENGTH_SHORT);
            }
        }
    }
}
