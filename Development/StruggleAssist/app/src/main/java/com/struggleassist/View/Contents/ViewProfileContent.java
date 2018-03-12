package com.struggleassist.View.Contents;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.struggleassist.R;

/**
 * Created by Ryan on 3/9/2018.
 */

public class ViewProfileContent extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        getActivity().setTitle(R.string.action_profile);
        return inflater.inflate(R.layout.content_view_profile,container,false);
    }

    @Override
    public void onResume(){
        super.onResume();
        getActivity().setTitle(R.string.action_profile);
    }
}

