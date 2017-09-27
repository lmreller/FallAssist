package com.struggleassist.View.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.struggleassist.R;

public class CreateProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);

        //EditText Fields
        final EditText etName = (EditText) findViewById(R.id.etName);
        final EditText etDateOfBirth = (EditText) findViewById(R.id.etDateOfBirth);
        final EditText etEmergencyContact = (EditText) findViewById(R.id.etEmergencyContact);

        //Cancel and Confirm buttons
        Button bCancel = (Button) findViewById(R.id.bCancel);
        Button bConfirm = (Button) findViewById(R.id.bConfirm);

        //Cancel, return to previous activity
        bCancel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                finish();
            }
        });

        //Confirm
        bConfirm.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent();

                //Send items in text fields back to previous activity
                intent.putExtra("cpName",etName.getText().toString());
                intent.putExtra("cpDateOfBirth",etDateOfBirth.getText().toString());
                intent.putExtra("cpEmergencyContact",etEmergencyContact.getText().toString());

                setResult(RESULT_OK,intent);
                finish();
            }
        });
    }
}
