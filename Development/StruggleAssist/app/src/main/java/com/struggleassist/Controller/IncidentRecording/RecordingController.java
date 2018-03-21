package com.struggleassist.Controller.IncidentRecording;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.struggleassist.Controller.DatabaseController;
import com.struggleassist.Model.Record;
import com.struggleassist.Model.ViewContext;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by Ryan on 3/11/2018.
 */

public class RecordingController {

    private LocationRecorder location;
    private MultimediaRecorder multimedia;

    private String address = null;
    private String videoPath = null;

    public RecordingController() {
        location = new LocationRecorder();
        multimedia = new MultimediaRecorder();

    }

    public void startRecording() {

        //Get Address
        LocationRecorder.LocationResult locationResult = new LocationRecorder.LocationResult() {
            @Override
            public void gotLocation(Location location) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                //Got the location!
                Log.d("GPSL", Double.toString(latitude));
                Log.d("GPSL", Double.toString(longitude));

                ReverseGeocoder geo = new ReverseGeocoder(latitude, longitude);
                setAddress(geo.getLocalAddress());
            }
        };
        location = new LocationRecorder();
        location.getLocation(ViewContext.getContext(), locationResult);

    }

    private BroadcastReceiver bReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            videoPath = intent.getParcelableExtra("videoUri");
            Toast.makeText(ViewContext.getContext(),videoPath,Toast.LENGTH_SHORT).show();
        }
    };

    public void stopRecording(String userResponse, float incidentScore) {
        DatabaseController db = new DatabaseController(ViewContext.getContext());
        Record record = new Record(userResponse,incidentScore);

        if(address != null)
            record.setIncidentLocation(address);
        if(videoPath != null)
            record.setIncidentVideo(videoPath);
        db.open();
        db.insertRecord(record);
        db.close();
    }


    private static class ReverseGeocoder {
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
                Log.d("ADDRESS",address);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        public String getLocalAddress() {
            return address;
        }

    }

    public void setAddress(String adr) {
            this.address = adr;
        }
    public String getAddress(){return address;}
}