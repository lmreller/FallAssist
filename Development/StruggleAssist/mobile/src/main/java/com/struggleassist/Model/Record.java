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

    private boolean incident;
    private Date dateOfIncident;
    private Location incidentLocation;
    private MediaStore.Video incidentVideo;
    private String incidentNotes;
    float incidentScore;

    SimpleDateFormat ft =
            new SimpleDateFormat("E yyyy/MM/dd hh:mm");

    public Record(boolean incident, float incidentScore){
        this.incident = incident;
        this.incidentScore = incidentScore;
        dateOfIncident = new Date();

        Toast.makeText(ViewContext.getContext(),Boolean.toString(incident) + ":"
                + ft.format(dateOfIncident) + ":" + Float.toString(incidentScore),Toast.LENGTH_LONG).show();
    }

    public boolean getIncident(){
        return incident;
    }
    public void setIncident(boolean incident){
        this.incident = incident;
    }

    public Date getDateOfIncident(){
        return dateOfIncident;
    }
    public void setDateOfIncident(Date dateOfIncident){
        this.dateOfIncident = dateOfIncident;
    }

    public Location getIncidentLocation(){
        return incidentLocation;
    }
    public void setIncidentLocation(Location incidentLocation){
        this.incidentLocation = incidentLocation;
    }

    public MediaStore.Video getIncidentVideo() {
        return incidentVideo;
    }
    public void setIncidentVideo(MediaStore.Video incidentVideo){
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
}
