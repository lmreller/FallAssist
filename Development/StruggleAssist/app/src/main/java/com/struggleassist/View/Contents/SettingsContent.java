package com.struggleassist.View.Contents;

import android.os.Bundle;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.struggleassist.R;

/**
 * Created by Ryan on 3/9/2018.
 */

public class SettingsContent extends PreferenceFragmentCompat {
//    @Override
//    public void onCreate(Bundle savedInstanceState){
//        super.onCreate(savedInstanceState);
//        addPreferencesFromResource(R.xml.preferences);
//    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState,String rootkey){
        setPreferencesFromResource(R.xml.preferences,rootkey);
    }

//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
//        getActivity().setTitle(R.string.action_settings);
//        return inflater.inflate(R.layout.content_settings,container,false);
//    }

    @Override
    public void onResume(){
        super.onResume();
        getActivity().setTitle(R.string.action_settings);
    }
}
