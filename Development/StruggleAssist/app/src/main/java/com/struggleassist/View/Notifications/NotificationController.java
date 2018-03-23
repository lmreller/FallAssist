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

public class NotificationController {

    public static final String MAIN_ACTION = "com.struggleassist.View.Notifications.NotificationController.mainAction";         //Tap on notification, not a button
    public static final String STOP_ACTION = "com.struggleassist.View.Notifications.NotificationController.stopAction";         //Stop
    public static final String CONFIRM_ACTION = "com.struggleassist.View.Notifications.NotificationController.confirmAction";   //Tap confirm (alert)
    public static final String CANCEL_ACTION = "com.struggleassist.View.Notifications.NotificationController.cancelAction";     //Tap cancel (alert)
    public static final String TIMEOUT_ACTION = "com.struggleassist.View.Notifications.NotificationController.timeoutAction";   //Notification timeout (alert)
    public static final String IDLE_ACTION = "com.struggleassist.View.Notifications.NotificationController.idleAction";         //Update to idle
    public static final String ALERT_ACTION = "com.struggleassist.View.Notifications.NotificationController.alertAction";       //Update to alert

}