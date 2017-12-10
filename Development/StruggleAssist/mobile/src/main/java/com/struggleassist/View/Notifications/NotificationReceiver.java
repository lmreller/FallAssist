package com.struggleassist.View.Notifications;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.AudioManager;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.telephony.PhoneStateListener;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import com.struggleassist.Controller.DatabaseController;
import com.struggleassist.Model.ViewContext;

import java.lang.reflect.Method;

import static com.struggleassist.Model.ViewContext.context;
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

    TelephonyManager telephonyManager;
    StatePhoneReceiver phoneStateListener;
    boolean callFromApp = false;
    boolean callFromOffHook = false;

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
            notification.cancelTimer();     //Cancel the countdown timer
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
                notification.cancelTimer();     //Cancel the countdown timer
                if(texts)
                    sendSMS(ecNumber);
                if(calls)
                    makeCall(ecNumber);
            }
            //Timeout Action
            else if (TIMEOUT_ACTION.equals(action)) {
                Toast.makeText(getContext(), "Timed out", Toast.LENGTH_LONG).show();
                if(texts)
                    sendSMS(ecNumber);
                if(calls)
                    makeCall(ecNumber);
            }
        }

        //Remove notification from notification list
        notificationManager.cancel(intent.getIntExtra("uniqueID", 0));
    }

    public void sendSMS(String ecNumber) {
        //Toast.makeText(getContext(),userName + ecNumber,Toast.LENGTH_LONG).show();

        SmsManager sms = SmsManager.getDefault();
        //Send text message
        PendingIntent sentIntent = PendingIntent.getBroadcast(getContext(), 0, new Intent("SMS_SENT"),0);
        PendingIntent deliveredIntent = PendingIntent.getBroadcast(getContext(),0,new Intent("SMS_DELIVERED"),0);
        sms.sendTextMessage(ecNumber, null,userName + MESSAGE,sentIntent,deliveredIntent);
    }

    public void makeCall(String ecNumber) {

        //Listen for a change in phone calling state
        phoneStateListener = new StatePhoneReceiver(context);
        telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        telephonyManager.listen(phoneStateListener,PhoneStateListener.LISTEN_CALL_STATE);
        callFromApp = true;

        //Make call
        Uri call = Uri.parse("tel:" + ecNumber);
        Intent makeCallIntent = new Intent(Intent.ACTION_CALL, call);
        getContext().startActivity(makeCallIntent);
    }

    // Monitor for changes to the state of the phone
    public class StatePhoneReceiver extends PhoneStateListener {
        Context context;
        public StatePhoneReceiver(Context context) {
            this.context = context;
        }

        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);

            switch (state) {

                case TelephonyManager.CALL_STATE_OFFHOOK: //Call is established
                    if (callFromApp) {
                        callFromApp=false;
                        callFromOffHook=true;

                        try {
                            Thread.sleep(500); // Delay 0,5 seconds to handle better turning on loudspeaker
                        } catch (InterruptedException e) {
                        }

                        //Activate loudspeaker
                        AudioManager audioManager = (AudioManager)
                                context.getSystemService(Context.AUDIO_SERVICE);
                        audioManager.setMode(AudioManager.MODE_IN_CALL);
                        audioManager.setSpeakerphoneOn(true);
                    }
                    break;

                case TelephonyManager.CALL_STATE_IDLE: //Call is finished
                    if (callFromOffHook) {
                        callFromOffHook=false;
                        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                        audioManager.setMode(AudioManager.MODE_NORMAL); //Deactivate loudspeaker
                        telephonyManager.listen(phoneStateListener, // Remove listener
                                PhoneStateListener.LISTEN_NONE);
                    }
                    break;
            }
        }
    }
}