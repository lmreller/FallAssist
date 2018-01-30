package com.struggleassist.View.Activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import com.struggleassist.Controller.DatabaseController;
import com.struggleassist.Controller.Utilities.CheckAPIandSensors;
import com.struggleassist.Model.ViewContext;
import com.struggleassist.R;
import com.struggleassist.View.Activities.Profile.CreateProfileActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LaunchActivity extends AppCompatActivity {


    TextView tvLaunch;

    final static int PERMISSION_ALL = 1;    //Permissions request code, used for onRequestPermissionsResult()

    //List of permissions required
    final static String[] PERMISSIONS = {Manifest.permission.READ_SMS,
                                         Manifest.permission.READ_PHONE_STATE,
                                         Manifest.permission.READ_CONTACTS};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
        ViewContext.setContext(this);

        tvLaunch = (TextView) findViewById(R.id.tvLaunch);

        //Request permissions (will launch main activity after permissions are granted or if they are already granted on launch)
        if(!hasPermissions(this,PERMISSIONS))
            requestPermissions();
        else {
            launchNextActivity();
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
        i = new Intent(getApplicationContext(), CreateProfileActivity.class);
        startActivity(i);
    }

    private void launchMainActivity(){
        Intent i;
        i = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(i);
    }

    public void checkSensorsandAPI() {
        int apiVal = CheckAPIandSensors.getAPINumber();
        boolean hifiSensors = CheckAPIandSensors.highAccuracySensorSupport(this);
    }

}
