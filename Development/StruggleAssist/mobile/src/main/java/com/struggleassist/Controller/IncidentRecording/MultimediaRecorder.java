package com.struggleassist.Controller.IncidentRecording;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

import com.struggleassist.R;

import java.io.File;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class MultimediaRecorder extends Service implements SurfaceHolder.Callback {

    private WindowManager windowManager;
    private SurfaceView surfaceView;
    private Camera camera = null;
    private MediaRecorder mediaRecorder = null;

    private File outputFile;
    private Uri videoUri = null;

    @Override
    public void onCreate() {

        //Start foreground service to avoid unexpected kill
        Notification.Builder notification = new Notification.Builder(this)
                .setContentTitle("Background Video Recorder")
                .setContentText("")
                .setSmallIcon(R.mipmap.ic_launcher);
        startForeground(1234, notification.build());

        //Create new SurfaceView, set its size to 1x1, move it to the top left corner and set this service as a callback
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        surfaceView = new SurfaceView(this);
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                1,1,
                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                PixelFormat.TRANSLUCENT);
        params.x = 0;
        params.y = 0;
        params.gravity = Gravity.LEFT | Gravity.TOP;
        windowManager.addView(surfaceView,params);
        surfaceView.getHolder().addCallback(this);
    }

    // Method called right after Surface created (initializing and starting MediaRecorder)
    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {

        camera = Camera.open();
        mediaRecorder = new MediaRecorder();
        camera.unlock();

        mediaRecorder.setPreviewDisplay(surfaceHolder.getSurface());
        mediaRecorder.setCamera(camera);
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        mediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH));

        //File directory (will be made automatically if it doesn't exist)
        File sdcard = Environment.getExternalStorageDirectory();
        File dir = new File(sdcard.getAbsolutePath()+"/fall_detection/");
        dir.mkdir();
        outputFile = new File(dir,DateFormat.format("yyyy-MM-dd_kk-mm-ss", new Date().getTime())+
                ".mp4");
        mediaRecorder.setOutputFile(outputFile.getAbsolutePath());

        //Send file to RecordingController
        videoUri = Uri.fromFile(outputFile);
        sendMessage();

        try { mediaRecorder.prepare(); } catch (Exception e) {}
        mediaRecorder.start();

        //For testing purposes, record 5 second clip
        Timer t = new Timer();
        t.schedule(new TimerTask(){
            @Override
            public void run(){
                stopSelf();
            }
        },5000);
    }

    // Stop recording and remove SurfaceView
    @Override
    public void onDestroy() {

        mediaRecorder.stop();
        mediaRecorder.reset();
        mediaRecorder.release();

        camera.lock();
        camera.release();

        windowManager.removeView(surfaceView);

        super.onDestroy();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int width, int height) {}

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {}

    @Override
    public IBinder onBind(Intent intent) { return null; }

    //Send the filepath back to the RecordingController
    private void sendMessage(){
        Intent intent = new Intent("MultimediaRecorderBroadcast");
        intent.putExtra("videoUri",videoUri);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

}