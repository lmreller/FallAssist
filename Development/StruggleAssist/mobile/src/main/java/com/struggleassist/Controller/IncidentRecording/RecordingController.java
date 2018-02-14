package com.struggleassist.Controller.IncidentRecording;

import com.struggleassist.Model.Record;

/**
 * Created by Lucas on 2/14/2018.
 */

public class RecordingController {

    private LocationRecorder location;
    private MultimediaRecorder multimedia;

    public RecordingController(){
        location = new LocationRecorder();
    }

    public void startRecording(){

    }

    public void stopRecording(){

    }

    public Record createRecord(){
        return null;
    }
}
