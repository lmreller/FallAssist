package com.struggleassist.View.Activities;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.struggleassist.Controller.DatabaseController;
import com.struggleassist.Model.ViewContext;
import com.struggleassist.R;
import com.struggleassist.View.Notifications.SimpleEula;

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
    private CheckBox yesCheck;
    private CheckBox noCheck;
    private String userType = "";

    //Global variables for date of birth selection and storage
    DateFormat inputDateFormat = new SimpleDateFormat("MM/dd/yyyy");
    DateFormat storedDateFormat = new SimpleDateFormat("yyyy/MM/dd");
    Date dateOfBirth;
    String dateOfBirthString;

    //Global variables for selecting an emergency contact
    static final int PICK_CONTACT_REQUEST = 1;
    String ecID = null;
    String ecName = null;
    String ecNumber = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);

        //EULA
        DatabaseController db = new DatabaseController(this);
        boolean exists;
        db.open();
        exists = db.userExists();
        db.close();
        if (!exists) {
            new SimpleEula(this).show();
        }

        //EditText Fields
        etFirstName = (EditText) findViewById(R.id.etFirstName);
        etLastName = (EditText) findViewById(R.id.etLastName);
        etDateOfBirth = (EditText) findViewById(R.id.etDateOfBirth);
        etEmergencyContact = (EditText) findViewById(R.id.etEmergencyContact);
        yesCheck = (CheckBox) findViewById(R.id.checkBoxYes);
        noCheck = (CheckBox) findViewById(R.id.checkBoxNo);

        //Confirm button
        bConfirm = (Button) findViewById(R.id.bConfirm);
    }

    //Confirm Button Method
    public void create_onClick(View v) {

        //Boolean for empty fields (false = no empty fields, true = at least 1 empty field)
        boolean emptyField = false;

        //Show error for each empty field
        if(TextUtils.isEmpty(etFirstName.getText().toString())){
            etFirstName.setError("Field Required");
            emptyField = true;
        }
        if(TextUtils.isEmpty(etLastName.getText().toString())){
            etLastName.setError("Field Required");
            emptyField = true;
        }
        if(TextUtils.isEmpty(etDateOfBirth.getText().toString())){
            etDateOfBirth.setError("Field Required");
            emptyField = true;
        }
        if(TextUtils.isEmpty(etEmergencyContact.getText().toString())){
            etEmergencyContact.setError("Field Required");
            emptyField = true;
        }
        if(!yesCheck.isChecked()&&!noCheck.isChecked()){
            yesCheck.setError("Field Required");
            emptyField = true;
        }

        //If there are no empty fields, insert user into database
        if(emptyField==false){

            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(ViewContext.getContext());
            SharedPreferences.Editor editor = pref.edit();
            float startingThreshold = (float) 1.5;
            editor.putFloat("trend_analysis_value",startingThreshold);
            editor.commit();

            DatabaseController db = new DatabaseController(this);
            db.open();
            db.insertUser(etFirstName.getText().toString(), etLastName.getText().toString(), dateOfBirthString, ecID, ecNumber, userType);
            db.close();
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
            finish();
        }
    }


    //Launch DatePickerDialog when selecting Date of Birth text field
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void launchDatePicker(View v) {
        //To show current date in the datepicker
        Calendar mCurrentDate = Calendar.getInstance();
        int mYear = mCurrentDate.get(Calendar.YEAR);
        int mMonth = mCurrentDate.get(Calendar.MONTH);
        int mDay = mCurrentDate.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog mDatePicker = new DatePickerDialog(CreateProfileActivity.this,
                AlertDialog.THEME_HOLO_LIGHT, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                //Set etDateOfBirth text field to selected date
                dateOfBirthString = (++selectedmonth) + "/" + selectedday + "/" + selectedyear;
                etDateOfBirth.setText(dateOfBirthString);
                //Remove any errors in the edittext
                etDateOfBirth.setError(null);
            }
        }, mYear, mMonth, mDay);
        mDatePicker.setTitle("Select Date of Birth");
        mDatePicker.show();
    }


    //Launch contacts (intent) when selecting emergency contact
    public void pickContact(View v) {
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            Intent pickContactIntent = new Intent(Intent.ACTION_PICK, Uri.parse("content://contacts"));
            pickContactIntent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
            startActivityForResult(pickContactIntent, PICK_CONTACT_REQUEST);
        } else {
            Toast.makeText(this,"Please grant Contact Permission in Application Settings to continue",Toast.LENGTH_LONG).show();
        }
    }

    //Grab emergency contact information from intent
    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        if (reqCode == PICK_CONTACT_REQUEST && resultCode == RESULT_OK) {
            Uri uriContact = data.getData();

            ContentResolver cr = getContentResolver();
            Cursor cursor = cr.query(uriContact,null,null,null,null);

            //Retrieve id and name
            if(cursor.moveToFirst()){
                ecID = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                ecName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                //If the contact has a phone number, grab that number
                if(Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)))>0)
                    ecNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            }
            cursor.close();

            //If the contact does have a number, set the edittext and clear any errors
            if(ecNumber!=null){
                etEmergencyContact.setText(ecName+", "+ecNumber);
                etEmergencyContact.setError(null);
            }
            //If the contact does not have a number, send error and clear selection
            else {
                ecID = null;
                ecName = null;
                Toast.makeText(getApplicationContext(), "Emergency contact must have a phone number", Toast.LENGTH_LONG);
            }
        }
    }

    public void yesCheck(View v){
        noCheck.setChecked(false);
        userType = "primaryUser";
    }

    public void noClick(View v){
        yesCheck.setChecked(false);
        userType = "emergencyContact";
    }
}
