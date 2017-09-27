package com.struggleassist.View.Activities;

import android.content.Intent;
import android.hardware.Sensor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.struggleassist.Controller.FallDetection.SensorControllers.AccelerationController;
import com.struggleassist.Controller.FallDetection.SensorControllers.GravityController;
import com.struggleassist.Controller.FallDetection.SensorControllers.SensorController;
import com.struggleassist.Model.ViewContext;
import com.struggleassist.R;
import com.struggleassist.Controller.Utilities.CheckAPIandSensors;

public class LaunchActivity extends AppCompatActivity {

    TextView api;
    TextView hifi;
    SensorController accel;
    SensorController grav;

    String name;
    String dateOfBirth;
    String emergencyContact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
        ViewContext.setContext(this);

        api = (TextView) findViewById(R.id.api);
        hifi = (TextView) findViewById(R.id.hifi);

        int apiVal = CheckAPIandSensors.getAPINumber();
        boolean hifiSensors = CheckAPIandSensors.highAccuracySensorSupport(this);

        api.setText(Integer.toString(apiVal));
        hifi.setText(String.valueOf(hifiSensors));


        //register sensors
        accel = new AccelerationController(this);
        accel.start();

        //Create Profile Button
        Button bCreateProfile = (Button) findViewById(R.id.bCreateProfile);
        bCreateProfile.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent i = new Intent(getApplicationContext(),CreateProfileActivity.class);
                startActivityForResult(i, 1);
            }
        });
    }

    //Retrieve information from CreateProfile activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == 1){
            if(resultCode == RESULT_OK){
                name = data.getStringExtra("cpName");
                dateOfBirth = data.getStringExtra("cpDateOfBirth");
                emergencyContact = data.getStringExtra("cpEmergencyContact");

                Toast.makeText(getApplicationContext(), name + ", " + dateOfBirth + ", " +
                                emergencyContact, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
