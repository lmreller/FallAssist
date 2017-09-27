package com.struggleassist.View.Activities;

import android.hardware.Sensor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

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
    }
}
