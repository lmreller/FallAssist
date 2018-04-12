package com.struggleassist.Controller;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.NotificationCompat;
import android.support.v7.preference.PreferenceManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import com.struggleassist.Model.ViewContext;
import com.struggleassist.View.Activities.LaunchActivity;
import com.struggleassist.View.Notifications.NotificationController;

import static com.struggleassist.Model.ViewContext.getContext;

public class MessageListener extends BroadcastReceiver {


    private static SharedPreferences settings;
    private static boolean notificationSound;
    private static boolean notificationVibration;

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub

        settings = PreferenceManager.getDefaultSharedPreferences(ViewContext.getContext());
        notificationSound = settings.getBoolean("pref_enable_sound", false);
        notificationVibration = settings.getBoolean("pref_enable_vibration",false);


        DatabaseController db = new DatabaseController(ViewContext.getContext());
        db.open();
        Cursor dbCursor = db.getAllUsers();
        dbCursor.moveToFirst();
        String ecID = dbCursor.getString(dbCursor.getColumnIndex("emergencyContactID"));
        dbCursor.close();
        db.close();

        Cursor cursor = ViewContext.getContext().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                ContactsContract.CommonDataKinds.Phone._ID + " ='" + ecID + "'", null, null);

        String ecNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
        String ecName = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));

        if(intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")){
            Bundle bundle = intent.getExtras();           //---get the SMS message passed in---
            SmsMessage[] msgs = null;
            String msg_from;
            if (bundle != null){
                //---retrieve the SMS message received---
                try{
                    Object[] pdus = (Object[]) bundle.get("pdus");
                    msgs = new SmsMessage[pdus.length];
                    for(int i=0; i<msgs.length; i++){
                        msgs[i] = SmsMessage.createFromPdu((byte[])pdus[i]);
                        msg_from = msgs[i].getOriginatingAddress();
                        String msgBody = msgs[i].getMessageBody();
                        if(msgBody.contains(PhoneController.MESSAGE) && msg_from.equals(ecNumber)){
                            Toast.makeText(ViewContext.getContext(),msgBody,Toast.LENGTH_LONG).show();

                            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(ViewContext.getContext());

                            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
                            if(alarmSound == null){
                                alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
                                if(alarmSound == null){
                                    alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                                }
                            }
                            if(notificationSound) {
                                AudioManager audioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);

                                alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
                                if (alarmSound == null) {
                                    alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
                                    if (alarmSound == null) {
                                        alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                                    }
                                }
                                if(alarmSound!=null){
                                    mBuilder.setSound(alarmSound);
                                }
                                audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                                audioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION,audioManager.getStreamMaxVolume(AudioManager.STREAM_RING),0);

                                NotificationManager mNotificationMangager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
                                if(Build.VERSION.SDK_INT>=23)
                                    mNotificationMangager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALL);
                            }
                            if(notificationVibration){
                                mBuilder.setVibrate(new long[]{0,3000,1000,3000,1000,3000,1000,3000,1000});
                            }

                            mBuilder.setSmallIcon(com.struggleassist.R.mipmap.struggleassist_icon);
                            mBuilder.setContentTitle("Struggle Assist");
                            mBuilder.setContentText(ecName + PhoneController.MESSAGE);
                            mBuilder.setAutoCancel(true);
                            mBuilder.setSound(alarmSound);

                            Intent mIntent = new Intent(ViewContext.getContext(), LaunchActivity.class);
                            mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            PendingIntent pIntent = PendingIntent.getActivity(ViewContext.getContext(), 0, mIntent, 0);

                            mBuilder.setContentIntent(pIntent);

                            NotificationManager mNotificationMangager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
                            if(Build.VERSION.SDK_INT>=23)
                                mNotificationMangager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_NONE);
                            mNotificationMangager.notify(0,mBuilder.build());
                        }
                    }
                }catch(Exception e){
                    Log.d("Exception caught", e.getMessage());
                }
            }
        }
    }
}
