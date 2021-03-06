package com.struggleassist.View.Contents;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.struggleassist.Controller.DatabaseController;
import com.struggleassist.R;
import com.struggleassist.View.Activities.EditProfileActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by Ryan on 3/9/2018.
 */

public class ViewProfileContent extends Fragment {

    private static View view;

    private TextView tvUserName;
    private TextView tvUserBirthdate;
    private Button bEditProfile;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        getActivity().setTitle(R.string.action_profile);

        if(view != null){
            ViewGroup parent = (ViewGroup) view.getParent();
            if(parent!=null)
                parent.removeView(view);
        }
        try{
            view = inflater.inflate(R.layout.content_view_profile,container,false);
        }catch(InflateException e){
            //Return view as is
        }

        tvUserName = (TextView) view.findViewById(R.id.tvViewProfileUserName);
        tvUserBirthdate = (TextView) view.findViewById(R.id.tvViewProfileBirthDate);

        bEditProfile = (Button) view.findViewById(R.id.bEditProfile);
        bEditProfile.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                launchEditProfile();
            }
        });



        setFields();

        return view;
    }

    @Override
    public void onResume(){
        super.onResume();
        getActivity().setTitle(R.string.action_profile);

        setFields();
    }

    private void setFields(){

        DatabaseController db = new DatabaseController(view.getContext());
        db.open();
        Cursor dbCursor = db.getAllUsers();
        dbCursor.moveToFirst();
        String userFName = dbCursor.getString(dbCursor.getColumnIndex("firstName"));
        String userLName = dbCursor.getString(dbCursor.getColumnIndex("lastName"));
        String userBirthdate = dbCursor.getString(dbCursor.getColumnIndex("dateOfBirth"));
        dbCursor.close();
        db.close();

        tvUserName.setText(userFName+" "+userLName);
        tvUserBirthdate.setText("Age: " + getAge(userBirthdate));
    }

    public void launchEditProfile(){
        Intent i = new Intent(getContext(), EditProfileActivity.class);       //Currently launching create profile activity, will need to change to edit profile activity
        startActivityForResult(i,123);
    }

    private int getAge(String userBirthdate){
        int age = 0;

        try {
            Calendar dobCal = new GregorianCalendar();
            Calendar currentCal = new GregorianCalendar();

            int factor = 0;

            Date dob = new SimpleDateFormat("MM/dd/yyyy").parse(userBirthdate);
            Date currentDate = new Date();

            dobCal.setTime(dob);
            currentCal.setTime(currentDate);
            if (currentCal.get(Calendar.DAY_OF_YEAR) < dobCal.get(Calendar.DAY_OF_YEAR)) {
                factor = -1;
            }
            age = currentCal.get(Calendar.YEAR) - dobCal.get(Calendar.YEAR) + factor;
        } catch (ParseException e){
        }
        return age;
    }
}

