package com.struggleassist.View.Activities;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.IntentCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.struggleassist.Controller.DatabaseController;
import com.struggleassist.Model.ViewContext;
import com.struggleassist.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.struggleassist.Model.ViewContext.getContext;

public class EditProfileActivity extends AppCompatActivity {

    //User information fields
    private EditText etEditFirstName;
    private EditText etEditLastName;
    private EditText etEditDateOfBirth;
    private EditText etEditEmergencyContact;

    private Button bEditConfirm;

    //Global variables for selecting an emergency contact
    static final int PICK_CONTACT_REQUEST = 1;
    String userID = null;
    String ecID = null;
    String ecName = null;
    String ecNumber = null;
    String userFName;
    String userLName;
    String userBirthdate;
    String userType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.editProfileToolbar);
        toolbar.setTitle("Edit Profile");

        //EditText Fields
        etEditFirstName = (EditText) findViewById(R.id.etEditFirstName);
        etEditLastName = (EditText) findViewById(R.id.etEditLastName);
        etEditDateOfBirth = (EditText) findViewById(R.id.etEditDateOfBirth);
        etEditEmergencyContact = (EditText) findViewById(R.id.etEditEmergencyContact);


        DatabaseController db = new DatabaseController(getContext());
        db.open();
        Cursor dbCursor = db.getAllUsers();
        dbCursor.moveToFirst();
        userID = dbCursor.getString(dbCursor.getColumnIndex("uid"));
        userFName = dbCursor.getString(dbCursor.getColumnIndex("firstName"));
        userLName = dbCursor.getString(dbCursor.getColumnIndex("lastName"));
        userBirthdate = dbCursor.getString(dbCursor.getColumnIndex("dateOfBirth"));
        ecNumber = dbCursor.getString(dbCursor.getColumnIndex("emergencyContactNumber"));
        ecID = dbCursor.getString(dbCursor.getColumnIndex("emergencyContactID"));
        userType = dbCursor.getString(dbCursor.getColumnIndex("userType"));
        dbCursor.close();
        db.close();

        Cursor cursor = getContext().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                ContactsContract.CommonDataKinds.Phone._ID + " ='" + ecID + "'", null, null);

        cursor.moveToFirst();

        ecName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

        etEditFirstName.setText(userFName);
        etEditLastName.setText(userLName);
        etEditDateOfBirth.setText(userBirthdate);
        etEditEmergencyContact.setText(ecName+", "+ecNumber);

        //Confirm button
        bEditConfirm = (Button) findViewById(R.id.bEditConfirm);
    }

    public void cancel_onClick(View v){
        Intent i = new Intent();
        setResult(Activity.RESULT_CANCELED, i);
        finish();
    }

    //Confirm Button Method
    public void create_onClick(View v) {

        //Boolean for empty fields (false = no empty fields, true = at least 1 empty field)
        boolean emptyField = false;

        //Show error for each empty field
        if(TextUtils.isEmpty(etEditFirstName.getText().toString())){
            etEditFirstName.setError("Field Required");
            emptyField = true;
        }
        if(TextUtils.isEmpty(etEditLastName.getText().toString())){
            etEditLastName.setError("Field Required");
            emptyField = true;
        }
        if(TextUtils.isEmpty(etEditDateOfBirth.getText().toString())){
            etEditDateOfBirth.setError("Field Required");
            emptyField = true;
        }
        if(TextUtils.isEmpty(etEditEmergencyContact.getText().toString())){
            etEditEmergencyContact.setError("Field Required");
            emptyField = true;
        }

        //If there are no empty fields, insert user into database
        if(emptyField==false){
            DatabaseController db = new DatabaseController(this);
            db.open();
            db.updateUser(userID, etEditFirstName.getText().toString(), etEditLastName.getText().toString(), userBirthdate, ecID, ecNumber, userType);
            db.close();
            Intent i = new Intent();
            setResult(Activity.RESULT_OK, i);
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

        DatePickerDialog mDatePicker = new DatePickerDialog(EditProfileActivity.this,
                AlertDialog.THEME_HOLO_LIGHT, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                //Set etDateOfBirth text field to selected date
                userBirthdate = (++selectedmonth) + "/" + selectedday + "/" + selectedyear;
                etEditDateOfBirth.setText(userBirthdate);
                //Remove any errors in the edittext
                etEditDateOfBirth.setError(null);
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
                etEditEmergencyContact.setText(ecName+", "+ecNumber);
                etEditEmergencyContact.setError(null);
            }
            //If the contact does not have a number, send error and clear selection
            else {
                ecID = null;
                ecName = null;
                Toast.makeText(getApplicationContext(), "Emergency contact must have a phone number", Toast.LENGTH_LONG);
            }
        }
    }
}
