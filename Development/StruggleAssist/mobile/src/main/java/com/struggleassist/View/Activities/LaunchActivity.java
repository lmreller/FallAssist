package com.struggleassist.View.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.struggleassist.Controller.DatabaseController;
import com.struggleassist.Controller.Utilities.CheckAPIandSensors;
import com.struggleassist.Model.ViewContext;
import com.struggleassist.R;
import com.struggleassist.View.Activities.Profile.CreateProfileActivity;

public class LaunchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
        ViewContext.setContext(this);
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
