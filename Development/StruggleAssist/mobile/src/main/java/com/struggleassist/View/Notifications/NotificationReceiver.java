package com.struggleassist.View.Notifications;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Ryan on 11/7/2017.
 * Purpose:This class is designed to handle the button presses given by the user in the
 * issued notifications
 */

public class NotificationReceiver extends BroadcastReceiver {

    public static final String CANCEL_ACTION = "com.struggleassist.View.Notifications.Notification.cancelAction";
    public static final String CONFIRM_ACTION = "com.struggleassist.View.Notifications.Notification.confirmAction";
    public static final String TIMEOUT_ACTION = "com.struggleassist.View.Notifications.Notification.timeoutAction";


    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        String action = intent.getAction();

        if (CANCEL_ACTION.equals(action)) {
            Toast.makeText(context, "Cancel Pressed", Toast.LENGTH_LONG).show();
        } else if (CONFIRM_ACTION.equals(action)) {
            Toast.makeText(context, "Confirm Pressed", Toast.LENGTH_LONG).show();
        }
        else if(TIMEOUT_ACTION.equals(action)){
            Toast.makeText(context, "Notification Timed Out", Toast.LENGTH_LONG).show();
        }
        notificationManager.cancel(intent.getIntExtra("uniqueID",0));
    }
}
