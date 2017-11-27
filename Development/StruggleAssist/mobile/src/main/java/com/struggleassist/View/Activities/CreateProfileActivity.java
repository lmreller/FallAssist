package com.struggleassist.View.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.icu.util.Calendar;
import android.icu.util.TimeZone;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.struggleassist.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CreateProfileActivity extends AppCompatActivity {

    //User information fields
    private EditText etFirstName;
    private EditText etLastName;
    private EditText etDateOfBirth;
    private EditText etEmergencyContact;

    private Button bConfirm;
    private Button bCancel;


    DateFormat inputDateFormat = new SimpleDateFormat("MM/dd/yyyy");
    DateFormat storedDateFormat = new SimpleDateFormat("yyyy/MM/dd");
    Date dateOfBirth;

    static final int PICK_CONTACT_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);

        //EditText Fields
        etFirstName = (EditText) findViewById(R.id.etFirstName);
        etLastName = (EditText) findViewById(R.id.etLastName);
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

        //Convert date formats
        try {
            dateOfBirth = inputDateFormat.parse(etDateOfBirth.getText().toString());
        }
        catch(Exception e){
            Log.d("dateOfBirthParse","Failed to parse dateOfBirth inputDateFormat");
        }
        //Send items in text fields back to previous activity
        intent.putExtra("cpFirstName",etFirstName.getText().toString());
        intent.putExtra("cpLastName",etLastName.getText().toString());
        intent.putExtra("cpDateOfBirth",storedDateFormat.format(dateOfBirth));
        intent.putExtra("cpEmergencyContact",etEmergencyContact.getText().toString());

        setResult(RESULT_OK,intent);
        finish();
    }

    //Launch DatePickerDialog when selecting Date of Birth text field
    public void launchDatePicker(View v){
        //To show current date in the datepicker
        Calendar mCurrentDate=Calendar.getInstance();
        int mYear=mCurrentDate.get(Calendar.YEAR);
        int mMonth=mCurrentDate.get(Calendar.MONTH);
        int mDay=mCurrentDate.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog mDatePicker= new DatePickerDialog(CreateProfileActivity.this,
                AlertDialog.THEME_HOLO_LIGHT, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                etDateOfBirth.setText((++selectedmonth)+"/"+selectedday+"/"+selectedyear);      //Set etDateOfBirth text field to selected date
            }
        },mYear, mMonth, mDay);
        mDatePicker.setTitle("Select date");
        mDatePicker.show();}

    public void pickContact(View v) {
        Intent pickContactIntent = new Intent(Intent.ACTION_PICK, Uri.parse("content://contacts"));
        pickContactIntent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
        startActivityForResult(pickContactIntent, PICK_CONTACT_REQUEST);
    }

    @Override
    public void onActivityResult(int reqCode,int resultCode, Intent data){
        if(reqCode == PICK_CONTACT_REQUEST){
            if(resultCode == RESULT_OK){
                    Uri contactUri = data.getData();

                    String[] projection = {ContactsContract.CommonDataKinds.Phone.NUMBER};

                    Cursor cursor = getContentResolver()
                            .query(contactUri, projection, null, null, null);
                    cursor.moveToFirst();

                    int column = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                    String number = cursor.getString(column);
                    etEmergencyContact.setText(number.toString());
            }
        }
    }

}
