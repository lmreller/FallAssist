package com.struggleassist.Controller.IncidentRecording;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.hardware.Camera;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import com.struggleassist.Controller.DatabaseController;
import com.struggleassist.Model.Record;
import com.struggleassist.Model.RecordingData;
import com.struggleassist.Model.ViewContext;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by Lucas on 2/14/2018.
 */

/**
 * NOTES: -The filepath to the video [videoUri] is not being picked up by Record or RecordData.
 *        -The filepath is being properly stored in videoUri at the beginning of the fall detection
 *        -We should be able to delete this file programmatically if we need to
 * TODO: Get reference of filepath into record
 * TODO: Stop MultimediaRecorder after false alarm, cancel notification (maybe after confirm/timeout?)
 * TODO:            -Done via stopRecording(){ ... stopIntent(intent) ... }
 */

public class RecordingController{

    private Record record;

    private LocationRecorder location;
    private MultimediaRecorder multimedia;

    private String address;
    private Uri videoUri;

    private boolean incident;
    private float incidentScore;

    public RecordingController(boolean incident, float incidentScore){
        record = new Record(incident,incidentScore);

        location = new LocationRecorder();
        multimedia = new MultimediaRecorder();

        this.incident=incident;
        this.incidentScore=incidentScore;

        LocalBroadcastManager.getInstance(ViewContext.getContext()).registerReceiver(bReceiver,
                new IntentFilter("MultimediaRecorderBroadcast"));
    }

    public void startRecording(){
        LocationRecorder.LocationResult locationResult = new LocationRecorder.LocationResult(){
            @Override
            public void gotLocation(Location location){
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                //Got the location!
                Log.d("GPSL", Double.toString(latitude));
                Log.d("GPSL", Double.toString(longitude));

                ReverseGeocoder geo = new ReverseGeocoder(latitude, longitude);
                setAddress(geo.getAddress());
                record.setIncidentLocation(geo.getAddress());
            }
        };
        location = new LocationRecorder();
        location.getLocation(ViewContext.getContext(), locationResult);

        Intent i = new Intent(ViewContext.getContext(),MultimediaRecorder.class);
        ViewContext.getContext().startService(i);
    }

    private BroadcastReceiver bReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            videoUri = intent.getParcelableExtra("videoUri");

            createRecord();
        }
    };

    public void stopRecording(){

    }

    public String stopAndGetLocation(){
        Log.d("GPSL", "Out " + address);
        return address;
    }

    public void createRecord(){
        if(videoUri!=null){
            record.setIncidentVideo(videoUri.toString());
        }

        DatabaseController db = new DatabaseController(ViewContext.getContext());
        db.open();
        db.insertRecord(record);

        Cursor dbCursor = db.getAllRecords();
        dbCursor.moveToFirst();
        String rID = dbCursor.getString(dbCursor.getColumnIndex("rid"));
        String doi = dbCursor.getString(dbCursor.getColumnIndex("dateOfIncident"));
        String iL = dbCursor.getString(dbCursor.getColumnIndex("incidentLocation"));
        String iV = dbCursor.getString(dbCursor.getColumnIndex("incidentVideo"));
        String iN = dbCursor.getString(dbCursor.getColumnIndex("incidentNotes"));
        String iS = dbCursor.getString(dbCursor.getColumnIndex("incidentScore"));
        dbCursor.close();

        Toast.makeText(ViewContext.getContext(),rID,Toast.LENGTH_SHORT).show();
        Toast.makeText(ViewContext.getContext(),doi,Toast.LENGTH_SHORT).show();
        Toast.makeText(ViewContext.getContext(),iL ,Toast.LENGTH_SHORT).show();
        Toast.makeText(ViewContext.getContext(),iV ,Toast.LENGTH_SHORT).show();
        Toast.makeText(ViewContext.getContext(),iN ,Toast.LENGTH_SHORT).show();
        Toast.makeText(ViewContext.getContext(),iS ,Toast.LENGTH_SHORT).show();

        db.close();
    }

    private static class ReverseGeocoder{
        Geocoder geocoder;
        List<Address> addresses;

        String address;
        String city;
        String state;
        String country;
        String postalCode;
        String knownName;

        public ReverseGeocoder(double latitude, double longitude) {
            geocoder = new Geocoder(ViewContext.getContext(), Locale.getDefault());
            try {
                addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

                address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                city = addresses.get(0).getLocality();
                state = addresses.get(0).getAdminArea();
                country = addresses.get(0).getCountryName();
                postalCode = addresses.get(0).getPostalCode();
                knownName = addresses.get(0).getFeatureName(); // Only if available else return NULL
            }
            catch(IOException e){
                e.printStackTrace();
            }
        }

        public String getAddress() {
            return address;
        }

        public String getCity() {
            return city;
        }

        public String getState() {
            return state;
        }

        public String getCountry() {
            return country;
        }

        public String getPostalCode() {
            return postalCode;
        }

        public String getKnownName() {
            return knownName;
        }

    }


    public void setAddress(String adr){
        this.address = adr;
    }

    public Uri getVideoUri()
    {
        return videoUri;
    }

}
