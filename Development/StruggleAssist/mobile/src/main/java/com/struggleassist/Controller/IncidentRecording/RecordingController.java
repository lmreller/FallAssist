package com.struggleassist.Controller.IncidentRecording;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.util.Log;

import com.struggleassist.Model.Record;
import com.struggleassist.Model.ViewContext;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by Lucas on 2/14/2018.
 */

public class RecordingController {

    private LocationRecorder location;
    private MultimediaRecorder multimedia;

    private String address;

    public RecordingController(){
        location = new LocationRecorder();
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
                RecordingController.this.address = geo.getAddress();
            }
        };
        location = new LocationRecorder();
        location.getLocation(ViewContext.getContext(), locationResult);
    }

    public void stopRecording(){

    }

    public String stopAndGetLocation(){
        Log.d("GPSL", "Out " + address);
        return address;
    }

    public Record createRecord(){
        return null;
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
}
