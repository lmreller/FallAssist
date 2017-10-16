package com.struggleassist.Controller;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.struggleassist.Model.User;
import com.struggleassist.Model.ViewContext;

/**
 * Created by Lucas on 10/15/2017.
 * Has access to the applicaiton stored preferences
 */

public class PreferencesController {

    private static Context context = ViewContext.getContext();
    private static String userFileName = "com.StruggleAssist.USER";
    private static SharedPreferences userPref = context.getSharedPreferences(userFileName, Context.MODE_PRIVATE);
    //Settings uses the default file
    private static SharedPreferences settingsPref = PreferenceManager.getDefaultSharedPreferences(context);

    public static void saveUser(User user){
    }

    public static User readUser(){
        return null;
    }

    public static int[] readSettings(){
        return null;
    }
}
