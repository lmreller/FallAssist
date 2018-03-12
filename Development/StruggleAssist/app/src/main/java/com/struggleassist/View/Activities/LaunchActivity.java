package com.struggleassist.View.Activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.struggleassist.Controller.DatabaseController;
import com.struggleassist.Model.ViewContext;
import com.struggleassist.R;

public class LaunchActivity extends AppCompatActivity {


    private TextView tvLaunch;
    private ImageView launchActivityIcon;

    final static int PERMISSION_ALL = 1;    //Permissions request code, used for onRequestPermissionsResult()
    final static int OVERLAY_CODE = 2;

    //List of permissions required
    final static String[] PERMISSIONS = {Manifest.permission.READ_SMS,
                                         Manifest.permission.READ_PHONE_STATE,
                                         Manifest.permission.READ_CONTACTS,
                                         Manifest.permission.ACCESS_FINE_LOCATION,
                                         Manifest.permission.CAMERA,
                                         Manifest.permission.RECORD_AUDIO,
                                         Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                         Manifest.permission.SYSTEM_ALERT_WINDOW};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
        ViewContext.setContext(this);

        tvLaunch = (TextView) findViewById(R.id.tvLaunch);
        launchActivityIcon = (ImageView) findViewById(R.id.launchActivityIcon);

        if(!Settings.canDrawOverlays(this)){
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:"+getPackageName()));
            startActivityForResult(intent,OVERLAY_CODE);
        }

        //Request permissions (will launch main activity after permissions are granted or if they are already granted on launch)
        if(!hasPermissions(this,PERMISSIONS))
            requestPermissions();
        else {
            launchNextActivity();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == OVERLAY_CODE){
            if(Settings.canDrawOverlays(this)){

            }
            else{
                //We need to request permission for overlay capabilities to allow us to record video in the background of the app
                //This will allow the user to use the device and application as needed during an incident while the phone still records
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:"+getPackageName()));
                startActivityForResult(intent,OVERLAY_CODE);
            }

        }
    }

    private void requestPermissions(){
        if(!hasPermissions(this,PERMISSIONS)){
            ActivityCompat.requestPermissions(this, PERMISSIONS,PERMISSION_ALL);
        }
    }

    //Helper for requestPermissions(), checks if permissions are set
    public static boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    //Handle Permissions results
    @Override
    public void onRequestPermissionsResult(int requestCode,String[] permissions,int[] grantResults){
        super.onRequestPermissionsResult(requestCode,permissions,grantResults);
        switch (requestCode){
            case PERMISSION_ALL:{
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){                    //Launch activities if permissions are granted
                    launchNextActivity();
                } else if(Build.VERSION.SDK_INT >= 23 && !shouldShowRequestPermissionRationale(permissions[0])){        //If user selects "Never Ask Again" and denies request
                    tvLaunch.setText("Some critical permissions have not been granted. Please go to Application Settings to grant these permissions");
                    launchActivityIcon.setAlpha(1);
                } else {                                                                                                //Request permissions be set if they aren't already
                    requestPermissions();
                }
            }
        }
    }

    //Will trigger launchProfileActivity() or launchMainActivity()
    private void launchNextActivity(){
        DatabaseController db = new DatabaseController(this);
        boolean exists;

        db.open();
        exists = db.userExists();
        db.close();
        if (exists)
            launchMainActivity();
        else
            launchProfileActivity();
        finish();
    }

    private void launchProfileActivity(){
        Intent i;
        i = new Intent(this, CreateProfileActivity.class);
        startActivity(i);
    }

    private void launchMainActivity(){
        Intent i;
        i = new Intent(this, MainActivity.class);
        startActivity(i);
    }

}
