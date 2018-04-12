package com.struggleassist.View.Contents;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.struggleassist.Controller.DatabaseController;
import com.struggleassist.Model.ViewContext;
import com.struggleassist.R;
import com.struggleassist.View.Activities.EmergencyContactCardFragment;

/**
 * Created by Ryan on 3/9/2018.
 */

public class HomeContent extends Fragment {

    private static View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        super.onCreateView(inflater,container,savedInstanceState);
        getActivity().setTitle(R.string.action_home);

        //Since there is a fragment within this fragment, this block is necessary to make sure we don't duplicate the child
        if(view != null){
            ViewGroup parent = (ViewGroup) view.getParent();
            if(parent!=null)
                parent.removeView(view);
        }
        try{
            view = inflater.inflate(R.layout.content_home,container,false);
        }catch(InflateException e){
            //Return view as is
        }



        Button bResetDB = (Button) view.findViewById(R.id.bResetButton);
        bResetDB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                resetDB();
            }
        });

        return view;
    }

    @Override
    public void onResume(){
        super.onResume();
        getActivity().setTitle(R.string.action_home);
    }

    @Override
    public void onPause(){
        super.onPause();
//        Fragment fragment = getChildFragmentManager().findFragmentById(R.id.);
//        getActivity().getSupportFragmentManager().beginTransaction().remove(fragment);
    }

    public void resetDB(){
        DatabaseController db = new DatabaseController(ViewContext.getContext());
        db.open();
        db.reset();
        db.close();
    }
}
