package com.struggleassist.Model;

import android.location.Location;
import android.provider.MediaStore;
import android.widget.Toast;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by lucas on 10/8/2017.
 * Data type to hold incident records
 */

public class Record implements Serializable {

    private Date date;

    private String dateOfIncident;
    private String incidentLocation;
    private String incidentVideo;
    private String incidentNotes;
    private String userResponse;
    private float incidentScore;
    private String id;
    protected boolean isInitialized;

    SimpleDateFormat outputFormat =
            new SimpleDateFormat("E yyyy/MM/dd hh:mm");

    SimpleDateFormat idFormat =
            new SimpleDateFormat("yyyyMMddhhmmss");

    public Record(){};

    public Record(String userResponse, float incidentScore){
        date = new Date();

        this.userResponse = userResponse;
        dateOfIncident = outputFormat.format(date);
        incidentLocation = "";
        incidentVideo = "";
        incidentNotes = "";
        this.incidentScore = incidentScore;

        id = generateId();

        isInitialized = true;
    }

    public String getUserResponse(){
        return userResponse;
    }
    public void setUserResponse(String userResponse){
        this.userResponse = userResponse;
    }

    public String getDateOfIncident(){
        return dateOfIncident;
    }
    public void setDateOfIncident(String dateOfIncident){
        this.dateOfIncident = dateOfIncident;
    }

    public String getIncidentLocation(){
        return incidentLocation;
    }
    public void setIncidentLocation(String incidentLocation){
        this.incidentLocation = incidentLocation;
    }

    public String getIncidentVideo() {
        return incidentVideo;
    }
    public void setIncidentVideo(String incidentVideo){
        this.incidentVideo = incidentVideo;
    }

    public String getIncidentNotes(){
        return incidentNotes;
    }
    public void setIncidentNotes(String incidentNotes){
        this.incidentNotes = incidentNotes;
    }

    public float getIncidentScore(){
        return incidentScore;
    }
    public void setIncidentScore(float incidentScore){
        this.incidentScore = incidentScore;
    }

    public String getId(){
        return id;
    }
    public void setId(String id){this.id = id;}

    public String generateId(){
        float time = System.nanoTime();
        id = idFormat.format(date) + Float.toString(incidentScore) + time;
        return id;
    }
}
