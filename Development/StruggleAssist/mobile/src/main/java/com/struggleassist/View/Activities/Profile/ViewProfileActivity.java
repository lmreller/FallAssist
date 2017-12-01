package com.struggleassist.View.Activities.Profile;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.struggleassist.R;

public class ViewProfileActivity extends AppCompatActivity {

    private String fName;
    private String lName;
    private String dateOfBirth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);
    }

    public void loadUserInfo(){

    }

}
