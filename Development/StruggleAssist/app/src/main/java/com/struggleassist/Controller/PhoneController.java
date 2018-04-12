package com.struggleassist.Controller;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceManager;
import android.telephony.SmsManager;
import android.widget.Toast;

import com.struggleassist.Model.ViewContext;

import static com.struggleassist.Model.ViewContext.getContext;

/**
 * Created by Ryan on 3/10/2018.
 */

public class PhoneController {

    public static final String MESSAGE = " may have experienced a fall";

    private SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(ViewContext.getContext());
    private boolean texts;
    private boolean calls;

    public PhoneController(){    }

    public void sendSMS(String userName, String ecNumber,String address){
        //Dev settings, if texts are off, don't send sms
        texts = settings.getBoolean("pref_enable_sms",false);
        //Check for permissions
        if(texts) {
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {

                SmsManager sms = SmsManager.getDefault();
                //Send text message
                PendingIntent sentIntent = PendingIntent.getBroadcast(getContext(), 0, new Intent("SMS_SENT"), 0);
                PendingIntent deliveredIntent = PendingIntent.getBroadcast(getContext(), 0, new Intent("SMS_DELIVERED"), 0);
                if (address != null){
                    sms.sendTextMessage(ecNumber, null, userName + MESSAGE + " @ " + address, sentIntent, deliveredIntent);
                }
                else{
                    sms.sendTextMessage(ecNumber, null, userName + MESSAGE, sentIntent, deliveredIntent);
                }
            } else {
                Toast.makeText(getContext(), "UNABLE TO SEND SMS DUE TO RESTRICTED PERMISSIONS SETTINGS", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void makeCall(String ecNumber) {
        //Dev settings, if calls are off, don't call
        calls = settings.getBoolean("pref_enable_phone",false);

        if(calls) {
            //Check for permissions
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                //Make call
                Uri call = Uri.parse("tel:" + ecNumber);
                Intent makeCallIntent = new Intent(Intent.ACTION_CALL, call);
                getContext().startActivity(makeCallIntent);
            } else {
                Toast.makeText(getContext(), "UNABLE TO MAKE CALL DUE TO RESTRICTED PERMISSIONS SETTINGS", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
