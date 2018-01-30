package com.struggleassist.View.Notifications;

import android.Manifest;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.telephony.SmsManager;
import android.widget.Toast;

import com.struggleassist.Controller.DatabaseController;
import com.struggleassist.Model.ViewContext;

import static com.struggleassist.Model.ViewContext.getContext;

/**
 * Created by Ryan on 11/7/2017.
 * Purpose:This class is designed to handle the button presses given by the user in the
 * issued notifications
 */

/**
 * TODO: Check permissions for call
 */

public class NotificationReceiver extends BroadcastReceiver {

    public static final String CANCEL_ACTION = "com.struggleassist.View.Notifications.Notification.cancelAction";
    public static final String CONFIRM_ACTION = "com.struggleassist.View.Notifications.Notification.confirmAction";
    public static final String TIMEOUT_ACTION = "com.struggleassist.View.Notifications.Notification.timeoutAction";

    public static final String MESSAGE = " may have experienced a fall.";
    public String ecNumber;
    public String userName;

    private SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(ViewContext.getContext());
    private boolean texts;
    private boolean calls;


    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        String action = intent.getAction();

        Notification notification = Notification.getInstance();

        //Developer call and text settings
        texts = settings.getBoolean("texts", false);
        calls = settings.getBoolean("calls", false);

        //Do nothing if cancel is pressed
        if (CANCEL_ACTION.equals(action)) {
            Toast.makeText(context, "Cancel Pressed", Toast.LENGTH_LONG).show();
            notification.cancelTimer();
        } else {
            DatabaseController db = new DatabaseController(context);
            db.open();
            Cursor cursor = db.getAllUsers();
            cursor.moveToFirst();

            //Retrieve emergency contact number and user's first and last name
            ecNumber = cursor.getString(cursor.getColumnIndex("emergencyContactNumber"));
            userName = cursor.getString(cursor.getColumnIndex("firstName")) + " "
                    + cursor.getString(cursor.getColumnIndex("lastName"));

            cursor.close();
            db.close();

            //Confirm action
            if (CONFIRM_ACTION.equals(action)) {
                Toast.makeText(getContext(), "Confirm pressed", Toast.LENGTH_LONG).show();
                if (texts)
                    sendSMS(ecNumber);
                if (calls)
                    makeCall(ecNumber);
                notification.cancelTimer();
            }
            //Timeout Action
            else if (TIMEOUT_ACTION.equals(action)) {
                Toast.makeText(getContext(), "Timed out", Toast.LENGTH_LONG).show();
                if (texts)
                    sendSMS(ecNumber);
                if (calls)
                    makeCall(ecNumber);
            }
        }

        //Remove notification from notification list
        notificationManager.cancel(intent.getIntExtra("uniqueID", 0));
    }

    public void sendSMS(String ecNumber) {
        //Toast.makeText(getContext(),userName + ecNumber,Toast.LENGTH_LONG).show();
        if (ContextCompat.checkSelfPermission(getContext(),Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {

            SmsManager sms = SmsManager.getDefault();
            //Send text message
            PendingIntent sentIntent = PendingIntent.getBroadcast(getContext(), 0, new Intent("SMS_SENT"), 0);
            PendingIntent deliveredIntent = PendingIntent.getBroadcast(getContext(), 0, new Intent("SMS_DELIVERED"), 0);
            sms.sendTextMessage(ecNumber, null, userName + MESSAGE, sentIntent, deliveredIntent);
        } else {
            Toast.makeText(getContext(), "UNABLE TO SEND SMS DUE TO RESTRICTED PERMISSIONS SETTINGS", Toast.LENGTH_SHORT).show();
        }
    }

    public void makeCall(String ecNumber) {
        if (ContextCompat.checkSelfPermission(getContext(),Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            //Make call
            Uri call = Uri.parse("tel:" + ecNumber);
            Intent makeCallIntent = new Intent(Intent.ACTION_CALL, call);
            getContext().startActivity(makeCallIntent);
        } else {
            Toast.makeText(getContext(),"UNABLE TO MAKE CALL DUE TO RESTRICTED PERMISSIONS SETTINGS", Toast.LENGTH_SHORT).show();
        }
    }
}