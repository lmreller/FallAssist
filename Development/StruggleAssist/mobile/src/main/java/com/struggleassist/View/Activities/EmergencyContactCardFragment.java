package com.struggleassist.View.Activities;

import android.app.Fragment;
import android.content.ContentUris;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.struggleassist.Controller.DatabaseController;
import com.struggleassist.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Ryan on 12/6/2017.
 */


public class EmergencyContactCardFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        //Inflate the emergency contact card fragment
        View view = inflater.inflate(R.layout.fragment_emergency_contact_card, container, false);

        //Initialize emergency contact fields
        ImageView ivEmergencyContact = (ImageView) view.findViewById(R.id.ivEmergencyContact);
        TextView tvEmergencyContactName = (TextView) view.findViewById(R.id.tvEmergencyContactName);
        TextView tvEmergencyContactNumber = (TextView) view.findViewById(R.id.tvEmergencyContactNumber);

        //Retrieve emergency contact ID
        DatabaseController db = new DatabaseController(view.getContext());
        db.open();
        Cursor dbCursor = db.getAllUsers();
        dbCursor.moveToFirst();
        String ecID = dbCursor.getString(dbCursor.getColumnIndex("emergencyContactID"));
        dbCursor.close();
        db.close();

        Bitmap bp = BitmapFactory.decodeResource(getContext().getResources(),R.mipmap.ic_launcher);
        Cursor cursor = getContext().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                ContactsContract.CommonDataKinds.Phone._ID + " ='" + ecID +"'", null, null);

        if(cursor.moveToFirst()){
            String image_uri = cursor.getString(cursor.getColumnIndex(
                    ContactsContract.CommonDataKinds.Phone.PHOTO_URI));
            if(image_uri != null){
                try{
                    bp = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(),
                            Uri.parse(image_uri));
                } catch (FileNotFoundException e){
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            ivEmergencyContact.setImageBitmap(bp);

            //Retrieve Name
            tvEmergencyContactName.setText(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)));
            //Retrieve Number
            tvEmergencyContactNumber.setText(cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
        }

        return view;
    }

}
