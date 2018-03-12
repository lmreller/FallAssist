package com.struggleassist.Model;

import android.location.Location;
import android.provider.MediaStore;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by lucas on 10/8/2017.
 * Data type to hold incident records
 */

public class Record {

    private Date date;

    private boolean incident;
    private String dateOfIncident;
    private String incidentLocation;
    private String incidentVideo;
    private String incidentNotes;
    private String userResponse;
    float incidentScore;
    private String id;

    SimpleDateFormat outputFormat =
            new SimpleDateFormat("E yyyy/MM/dd hh:mm");

    SimpleDateFormat idFormat =
            new SimpleDateFormat("yyyyMMddhhmmss");

    public Record(boolean incident, float incidentScore){
        date = new Date();

        this.incident = incident;
        dateOfIncident = outputFormat.format(date);
        incidentLocation = "";
        incidentVideo = "";
        incidentNotes = "";
        this.incidentScore = incidentScore;

        id = generateId();
    }

    public boolean getIncident(){
        return incident;
    }
    public void setIncident(boolean incident){
        this.incident = incident;
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

    public String generateId(){
        id = idFormat.format(date) + Float.toString(incidentScore);
        return id;
    }
}
