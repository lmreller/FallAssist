package com.struggleassist.View.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.struggleassist.Controller.DatabaseController;
import com.struggleassist.Controller.FallDetection.FallDetection;
import com.struggleassist.Controller.Utilities.CheckAPIandSensors;
import com.struggleassist.Model.ViewContext;
import com.struggleassist.R;

public class LaunchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
        ViewContext.setContext(this);
        DatabaseController db = new DatabaseController(this);
        Intent i;
        db.open();
        if (db.userExists())
            i = new Intent(getApplicationContext(), MainActivity.class);
        else
            i = new Intent(getApplicationContext(), CreateProfileActivity.class);
        db.close();

        startActivityForResult(i, 1);
        finish();
    }


    public void checkSensorsandAPI() {
        int apiVal = CheckAPIandSensors.getAPINumber();
        boolean hifiSensors = CheckAPIandSensors.highAccuracySensorSupport(this);
    }

//    //Launch CreateProfileActivity (button click)
//    public void createProfileLaunch(View v){
//        Intent i = new Intent(getApplicationContext(),CreateProfileActivity.class);
//        startActivityForResult(i, 1);
//    }

//    //Retrieve information from CreateProfile activity
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data){
//        if(requestCode == 1){
//            if(resultCode == RESULT_OK){
//                firstName = data.getStringExtra("cpFirstName");
//                lastName = data.getStringExtra("cpLastName");
//                dateOfBirth = data.getStringExtra("cpDateOfBirth");
//                emergencyContact = data.getStringExtra("cpEmergencyContact");
//
//                Toast.makeText(getApplicationContext(), firstName + " " + lastName +
//                                ", " + dateOfBirth + ", " + emergencyContact,
//                                Toast.LENGTH_SHORT).show();
//            }
//        }
//    }

//    public void settingsLaunch(View v){
//        Intent i = new Intent(getApplicationContext(),SettingsActivity.class);
//        startActivity(i);
//    }
}
