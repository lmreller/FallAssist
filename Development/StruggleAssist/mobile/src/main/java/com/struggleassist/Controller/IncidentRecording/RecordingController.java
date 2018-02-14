package com.struggleassist.Controller.IncidentRecording;

import android.location.Location;
import android.util.Log;

import com.struggleassist.Model.Record;
import com.struggleassist.Model.ViewContext;

/**
 * Created by Lucas on 2/14/2018.
 */

public class RecordingController {

    private LocationRecorder location;
    private MultimediaRecorder multimedia;

    boolean success;

    public RecordingController(){
        location = new LocationRecorder();
    }

    public void startRecording(){
        LocationRecorder.LocationResult locationResult = new LocationRecorder.LocationResult(){
            @Override
            public void gotLocation(Location location){
                //Got the location!
                Log.d("GPSL", Double.toString(location.getLatitude()));
                Log.d("GPSL", Double.toString(location.getLongitude()));
            }
        };
        location = new LocationRecorder();
        location.getLocation(ViewContext.getContext(), locationResult);
    }

    public void stopRecording(){
    }

    public Record createRecord(){
        return null;
    }
}
