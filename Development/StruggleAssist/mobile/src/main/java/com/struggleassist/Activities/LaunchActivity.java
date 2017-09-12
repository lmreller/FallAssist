package com.struggleassist.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.struggleassist.R;
import com.struggleassist.Utilities.CheckAPIandSensors;

public class LaunchActivity extends AppCompatActivity {

    TextView api;
    TextView hifi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

        api = (TextView) findViewById(R.id.api);
        hifi = (TextView) findViewById(R.id.hifi);

        int apiVal = CheckAPIandSensors.getAPINumber();
        boolean hifiSensors = CheckAPIandSensors.highAccuracySensorSupport(this);

        api.setText(Integer.toString(apiVal));
        hifi.setText(String.valueOf(hifiSensors));
    }
}
