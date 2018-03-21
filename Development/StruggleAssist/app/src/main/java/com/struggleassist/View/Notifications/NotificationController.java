package com.struggleassist.View.Notifications;

import android.app.NotificationManager;
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
        }
        public void onFinish(){
            Intent timeoutIntent = new Intent(ViewContext.getContext(),NotificationController.class);
            timeoutIntent.setAction(TIMEOUT_ACTION).putExtra("uniqueID",uniqueID);;
            ViewContext.getContext().startService(timeoutIntent);

            PendingIntent pTimeoutIntent = PendingIntent.getService(ViewContext.getContext(),0,timeoutIntent,0);
        }
    };

    private static RemoteViews idleView;
    private static RemoteViews alertView;
    private static NotificationManager notificationManger;

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

        Log.d("NotificationController: ", "onStartCommand()");

        userResponse = intent.getAction();


        //Cases where the system updates the notification (Start, Idle, or Alert
        Intent mIntent = new Intent(ViewContext.getContext(), LaunchActivity.class);
        mIntent.setAction(MAIN_ACTION);
        mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pIntent = PendingIntent.getActivity(ViewContext.getContext(), 0, mIntent, 0);


        if (userResponse.equals(IDLE_ACTION)) {
            idleView = new RemoteViews("com.struggleassist", R.layout.notification_idle_layout);

            Intent stopIntent = new Intent(ViewContext.getContext(), NotificationController.class);
            stopIntent.setAction(STOP_ACTION);
            PendingIntent pStopIntent = PendingIntent.getService(ViewContext.getContext(), 0, stopIntent, 0);

            idleView.setOnClickPendingIntent(R.id.idleNotificationButtonClose, pStopIntent);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(ViewContext.getContext());
            builder.setContent(idleView)
                    .setSmallIcon(R.mipmap.struggleassist_icon)
                    .setAutoCancel(true)
                    .setOngoing(true)
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .setCustomBigContentView(idleView)
                    .setContentIntent(pIntent);

            NotificationManager notificationManager = (NotificationManager) ViewContext.getContext().getSystemService(NOTIFICATION_SERVICE);
            notificationManager.notify(uniqueID, builder.build());

        } else if (userResponse.equals(ALERT_ACTION)) {
            alertView = new RemoteViews("com.struggleassist", R.layout.notification_alert_layout);

            Intent confirmIntent = new Intent(ViewContext.getContext(), NotificationController.class);
            confirmIntent.setAction(CONFIRM_ACTION).putExtra("uniqueID", uniqueID);
            PendingIntent pConfirmIntent = PendingIntent.getService(ViewContext.getContext(), 0, confirmIntent, 0);

            Intent cancelIntent = new Intent(ViewContext.getContext(), NotificationController.class);
            cancelIntent.setAction(CANCEL_ACTION).putExtra("uniqueID", uniqueID);
            PendingIntent pCancelIntent = PendingIntent.getService(ViewContext.getContext(), 0, cancelIntent, 0);

            alertView.setOnClickPendingIntent(R.id.alertNotificationButtonConfirm, pConfirmIntent);
            alertView.setOnClickPendingIntent(R.id.alertNotificationButtonCancel, pCancelIntent);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(ViewContext.getContext());
            builder.setContent(alertView)
                    .setSmallIcon(R.mipmap.struggleassist_icon)
                    .setAutoCancel(true)
                    .setOngoing(true)
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .setCustomBigContentView(alertView)
                    .setContentIntent(pIntent);

            NotificationManager notificationManager = (NotificationManager) ViewContext.getContext().getSystemService(NOTIFICATION_SERVICE);
            notificationManager.notify(uniqueID, builder.build());

            notificationTimer.start();
        } else
            //If the user responds to an alert (Confirms, Cancels, or Timeout occurs), send that response to FallDetection
            if (userResponse.equals(CONFIRM_ACTION) || userResponse.equals(CANCEL_ACTION) ) {
                notificationTimer.cancel();
                sendMessage();
            } else if(userResponse.equals(TIMEOUT_ACTION)) {
                sendMessage();
            } else if (userResponse.equals(STOP_ACTION)) {
                NotificationManager notificationManager = (NotificationManager) ViewContext.getContext().getSystemService(NOTIFICATION_SERVICE);
                notificationManager.cancel(uniqueID);
                sendMessage();
                stopForeground(true);
                stopSelf();
            }

        return Service.START_STICKY;
    }

    //Send the user response to FallDetection, FallDetection will decide what to do with the response
    private void sendMessage(){
        notificationTimer.cancel();
        Intent intent = new Intent("NotificationControllerBroadcast");
        intent.putExtra("userResponse",userResponse);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

}