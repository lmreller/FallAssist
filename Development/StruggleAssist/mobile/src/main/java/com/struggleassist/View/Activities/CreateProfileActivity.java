package com.struggleassist.View.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.struggleassist.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CreateProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);


        final EditText etName = (EditText) findViewById(R.id.etName);
        final EditText etDateOfBirth = (EditText) findViewById(R.id.etDateOfBirth);
        final EditText etEmergencyContact = (EditText) findViewById(R.id.etEmergencyContact);

        Button bSave = (Button) findViewById(R.id.bSave);
        Button bCancel = (Button) findViewById(R.id.bCancel);

        bCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //Send information back to LaunchActivity
        bSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent();
                intent.putExtra("name", etName.getText().toString());
                intent.putExtra("dateOfBirth", etDateOfBirth.getText().toString());
                intent.putExtra("emergencyContact", etEmergencyContact.getText().toString());

                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }
}
