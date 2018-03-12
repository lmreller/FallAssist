package com.struggleassist.View.Notifications;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.struggleassist.Model.ViewContext;
import com.struggleassist.R;
import com.struggleassist.View.Activities.LaunchActivity;

/**
 * Created by Ryan on 3/1/2018.
 */

public class NotificationController extends Service {

    private static final int uniqueID = 2112;

    public static final String MAIN_ACTION = "com.struggleassist.View.Notifications.NotificationController.mainAction";         //Tap on notification, not a button

    public static final String START_ACTION = "com.struggleassist.View.Notifications.NotificationController.startAction";       //Initialize
    public static final String STOP_ACTION = "com.struggleassist.View.Notifications.NotificationController.stopAction";         //Stop

    public static final String CONFIRM_ACTION = "com.struggleassist.View.Notifications.NotificationController.confirmAction";   //Tap confirm (alert)
    public static final String CANCEL_ACTION = "com.struggleassist.View.Notifications.NotificationController.cancelAction";     //Tap cancel (alert)
    public static final String TIMEOUT_ACTION = "com.struggleassist.View.Notifications.NotificationController.timeoutAction";   //Notification timeout (alert)

    public static final String IDLE_ACTION = "com.struggleassist.View.Notifications.NotificationController.idleAction";         //Update to idle
    public static final String ALERT_ACTION = "com.struggleassist.View.Notifications.NotificationController.alertAction";       //Update to alert


    private String userResponse;

    private static int timerLength = 30000;
    private static int timerTicks = 1000;

    private static CountDownTimer notificationTimer = new CountDownTimer(timerLength,timerTicks){
        public void onTick(long millisUntilFinished){
            //what to do every tick
            Toast.makeText(ViewContext.getContext(),millisUntilFinished/1000 + " seconds left",Toast.LENGTH_SHORT).show();
        }
        public void onFinish(){
            Intent timeoutIntent = new Intent(ViewContext.getContext(),NotificationController.class);
            timeoutIntent.setAction(TIMEOUT_ACTION);
            ViewContext.getContext().startService(timeoutIntent);

            PendingIntent pTimeoutIntent = PendingIntent.getService(ViewContext.getContext(),0,timeoutIntent,0);
        }
    };

    private static RemoteViews idleView;
    private static RemoteViews alertView;
    private static Notification builder;

    @Override
    public void onCreate(){
        super.onCreate();
    }


    @Override
    public IBinder onBind(Intent intent) { return null; }

    @Override
    public void onDestroy(){
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        userResponse = intent.getAction();

        //Cases where the system updates the notification (Start, Idle, or Alert
        if(userResponse.equals(START_ACTION) || userResponse.equals(IDLE_ACTION) || userResponse.equals(ALERT_ACTION)){
            showNotification(userResponse);
        } else
        //If the user responds to an alert (Confirms, Cancels, or Timeout occurs), send that response to FallDetection
            if(userResponse.equals(CONFIRM_ACTION) || userResponse.equals(CANCEL_ACTION) || userResponse.equals(TIMEOUT_ACTION)){
            sendMessage();
        } else
        //If the user responds by pressing close (in the idle notification), send response to FallDetection and close notification
            if(userResponse.equals(STOP_ACTION)){
            sendMessage();
            stopForeground(true);
            stopSelf();
        }

        return Service.START_STICKY;
    }

    //Initialize and build the application
    private void showNotification(String notificationStatus){
        builder = new Notification.Builder(ViewContext.getContext()).build();

        if(notificationStatus.equals(ALERT_ACTION)){
            alertView = new RemoteViews("com.struggleassist",R.layout.notification_alert_layout);

            Intent confirmIntent = new Intent(ViewContext.getContext(),NotificationController.class);
            confirmIntent.setAction(CONFIRM_ACTION);
            PendingIntent pConfirmIntent = PendingIntent.getService(ViewContext.getContext(),0,confirmIntent,0);

            Intent cancelIntent = new Intent(ViewContext.getContext(),NotificationController.class);
            cancelIntent.setAction(CANCEL_ACTION);
            PendingIntent pCancelIntent = PendingIntent.getService(ViewContext.getContext(),0,cancelIntent,0);

            alertView.setOnClickPendingIntent(R.id.alertNotificationButtonConfirm,pConfirmIntent);
            alertView.setOnClickPendingIntent(R.id.alertNotificationButtonCancel, pCancelIntent);

            builder.contentView = alertView;    //Small alert notification view
            //builder.contentView = ...;        //Expanded alert notification view

            startNotificationTimer();
        }
        else {
            idleView = new RemoteViews("com.struggleassist",R.layout.notification_idle_layout);

            Intent stopIntent = new Intent(ViewContext.getContext(),NotificationController.class);
            stopIntent.setAction(STOP_ACTION);
            PendingIntent pStopIntent = PendingIntent.getService(ViewContext.getContext(),0,stopIntent,0);

            idleView.setOnClickPendingIntent(R.id.idleNotificationButtonClose,pStopIntent);

            builder.contentView = idleView; //Small idle notification view
            //builder.bigContentView = ...; //Expanded idle notification view
        }

        Intent intent = new Intent(ViewContext.getContext(),LaunchActivity.class);
        intent.setAction(MAIN_ACTION);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pIntent = PendingIntent.getActivity(ViewContext.getContext(),0,intent,0);

        builder.contentIntent = pIntent;
        builder.flags = Notification.FLAG_ONGOING_EVENT;
        builder.icon = R.mipmap.struggleassist_icon;


        startForeground(uniqueID,builder);
    }

    //Send the user response to FallDetection, FallDetection will decide what to do with the response
    private void sendMessage(){
        notificationTimer.cancel();
        Intent intent = new Intent("NotificationControllerBroadcast");
        intent.putExtra("userResponse",userResponse);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void startNotificationTimer(){
        notificationTimer.start();
    }
}