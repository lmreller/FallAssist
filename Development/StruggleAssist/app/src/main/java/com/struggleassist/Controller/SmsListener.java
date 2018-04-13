package com.struggleassist.Controller;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v7.preference.PreferenceManager;
import android.telephony.SmsMessage;
import android.widget.Toast;

import com.struggleassist.Model.ViewContext;
import com.struggleassist.R;

import static com.struggleassist.Model.ViewContext.getContext;
import static com.struggleassist.View.Notifications.NotificationController.getAlarmSound;

public class SmsListener extends BroadcastReceiver {

    private SharedPreferences settings;
    private String ecNumber;
    private boolean notificationSound;
    private boolean notificationVibration;

    public void setEcNumber(String ecNumber){
        this.ecNumber=ecNumber;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub

        settings = PreferenceManager.getDefaultSharedPreferences(ViewContext.getContext());
        notificationSound = settings.getBoolean("pref_enable_sound", false);
        notificationVibration = settings.getBoolean("pref_enable_vibration", false);


        if(intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")){
            Bundle bundle = intent.getExtras();           //---get the SMS message passed in---
            SmsMessage[] msgs = null;
            String msg_from;
            String msgBody = "";
            if (bundle != null){
                //---retrieve the SMS message received---
                try{
                    Object[] pdus = (Object[]) bundle.get("pdus");
                    msgs = new SmsMessage[pdus.length];
                    for(int i=0; i<msgs.length; i++){
                        msgs[i] = SmsMessage.createFromPdu((byte[])pdus[i]);
                        msg_from = msgs[i].getOriginatingAddress();
                        msgBody += msgs[i].getMessageBody();
                        if(msgBody.contains(PhoneController.MESSAGE)&&msg_from.equals(ecNumber)) {
                            NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext());
                            builder.setSmallIcon(R.mipmap.struggleassist_icon)
                                    .setContentTitle("Struggle Assist")
                                    .setContentText(msgBody)
                                    .setLights(Color.RED,1000,1000)
                                    .setPriority(Notification.PRIORITY_MAX);

                            NotificationManager manager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
                            if(Build.VERSION.SDK_INT>=23) {
                                manager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALL);
                            }
                            if(notificationSound) {
                                AudioManager audioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);

                                if(getAlarmSound()!=null){
                                    builder.setSound(getAlarmSound());
                                }
                                audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                                audioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION,audioManager.getStreamMaxVolume(AudioManager.STREAM_RING),0);
                            }
                            if(notificationVibration){
                                builder.setVibrate(new long[]{0,3000,1000,3000,1000,3000});
                            }
                            manager.notify(2113,builder.build());
                        }
                    }
                }catch(Exception e){
                    //Log.d("Exception caught",e.getMessage());
                }
            }
        }
    }
}
