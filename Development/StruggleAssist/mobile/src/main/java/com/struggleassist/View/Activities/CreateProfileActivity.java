package com.struggleassist.View.Activities;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.icu.util.Calendar;
import android.icu.util.TimeZone;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.struggleassist.R;

public class CreateProfileActivity extends AppCompatActivity {

    //User information fields
    private EditText etName;
    private EditText etDateOfBirth;
    private EditText etEmergencyContact;

    private Button bConfirm;
    private Button bCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);

        //EditText Fields
        etName = (EditText) findViewById(R.id.etName);
        etDateOfBirth = (EditText) findViewById(R.id.etDateOfBirth);
        etEmergencyContact = (EditText) findViewById(R.id.etEmergencyContact);

        //Cancel and Confirm buttons
        bConfirm = (Button) findViewById(R.id.bConfirm);
        bCancel = (Button) findViewById(R.id.bCancel);
    }

    //Cancel Button Method
    public void cancelCreateProfile(View v){
        finish();
    }

    //Confirm Button Method
    public void confirmCreateProfile(View v){
        Intent intent = new Intent();

        //Send items in text fields back to previous activity
        intent.putExtra("cpName",etName.getText().toString());
        intent.putExtra("cpDateOfBirth",etDateOfBirth.getText().toString());
        intent.putExtra("cpEmergencyContact",etEmergencyContact.getText().toString());

        setResult(RESULT_OK,intent);
        finish();
    }

    public void launchDatePicker(View v){
        //To show current date in the datepicker
        Calendar mcurrentDate=Calendar.getInstance();
        int mYear=mcurrentDate.get(Calendar.YEAR);
        int mMonth=mcurrentDate.get(Calendar.MONTH);
        int mDay=mcurrentDate.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog mDatePicker= new DatePickerDialog(CreateProfileActivity.this, AlertDialog.THEME_HOLO_LIGHT,
                new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                etDateOfBirth.setText((++selectedmonth)+"/"+selectedday+"/"+selectedyear);
            }
        },mYear, mMonth, mDay);
        mDatePicker.setTitle("Select date");
        mDatePicker.show();}
}
