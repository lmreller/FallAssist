package com.struggleassist.View.Contents;

import android.content.Intent;
import android.database.Cursor;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.struggleassist.Controller.DatabaseController;
import com.struggleassist.Model.Record;
import com.struggleassist.Model.RecordAdapter;
import com.struggleassist.Model.ViewContext;
import com.struggleassist.R;
import com.struggleassist.View.Activities.PostIncident;
import com.struggleassist.View.Activities.ViewRecordActivity;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Ryan on 3/9/2018.
 */

public class IncidentReportContent extends Fragment {

    ListView listView;
    SwipeRefreshLayout swipeRefreshLayout;
    RecordAdapter adapter;
    ArrayList<Record> recordList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        getActivity().setTitle(R.string.action_incident_reports);

        View rootView = inflater.inflate(R.layout.content_incident_reports,container,false);

        listView = (ListView) rootView.findViewById(R.id.recordList);
        recordList = populateRecordList();

        adapter = new RecordAdapter(getActivity(), 0, recordList);

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Record record = adapter.getItem(position);

                Intent i = new Intent(getContext(),ViewRecordActivity.class);
                i.putExtra("Record",record);
                startActivityForResult(i,0);
                }
        });

        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.content_incident_reports);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                recordList = populateRecordList();
                adapter = new RecordAdapter(getActivity(), 0, recordList);
                listView.setAdapter(adapter);
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        return rootView;
    }

    @Override
    public void onResume(){
        super.onResume();
        getActivity().setTitle(R.string.action_incident_reports);
        recordList = populateRecordList();
        adapter = new RecordAdapter(getActivity(), 0, recordList);
        listView.setAdapter(adapter);
        swipeRefreshLayout.setRefreshing(false);
    }

    private ArrayList<Record> populateRecordList(){
        DatabaseController db = new DatabaseController(getContext());
        db.open();

        ArrayList<Record> list = new ArrayList<Record>();

        Cursor c = db.getAllRecords();
        if(c.moveToFirst()){
            while(c.moveToNext()){
                String rid = c.getString(c.getColumnIndex("rid"));
                String date = c.getString(c.getColumnIndex("dateOfIncident"));
                String location = c.getString(c.getColumnIndex("incidentLocation"));
                String video = c.getString(c.getColumnIndex("incidentVideo"));
                String notes = c.getString(c.getColumnIndex("incidentNotes"));
                String score = c.getString(c.getColumnIndex("incidentScore"));
                String response = c.getString(c.getColumnIndex("userResponse"));
                try{
                    Record record = new Record();

                    record.setId(rid);
                    record.setDateOfIncident(date);
                    record.setIncidentLocation(location);
                    record.setIncidentVideo(video);
                    record.setIncidentNotes(notes);
                    record.setIncidentScore(Float.parseFloat(score));
                    record.setUserResponse(response);

                    list.add(record);
                }catch(Exception e){
                    Log.d("IncidentReportContent:",e.toString());
                }
            }
        }
        c.close();
        db.close();

        Collections.reverse(list);

        return list;
    }
}