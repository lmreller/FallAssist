package com.struggleassist.View.Contents;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.InflateException;
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
import com.struggleassist.View.Activities.RecordListFragment;
import com.struggleassist.View.Activities.ViewRecordActivity;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Ryan on 3/9/2018.
 */

public class IncidentReportContent extends Fragment {

    private static View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        super.onCreateView(inflater,container,savedInstanceState);
        getActivity().setTitle(R.string.action_incident_reports);

        //Since there is a fragment within this fragment, this block is necessary to make sure we don't duplicate the child
        if(view != null){
            ViewGroup parent = (ViewGroup) view.getParent();
            if(parent!=null)
                parent.removeView(view);
        }
        try{
            view = inflater.inflate(R.layout.content_incident_reports,container,false);
        }catch(InflateException e){
            //Return view as is
        }

        return view;
    }

    @Override
    public void onResume(){
        super.onResume();
        getActivity().setTitle(R.string.action_incident_reports);
    }
}