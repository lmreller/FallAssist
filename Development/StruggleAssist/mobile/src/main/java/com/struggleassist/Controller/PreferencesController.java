package com.struggleassist.Controller;

import android.content.Context;
import android.content.SharedPreferences;

import com.struggleassist.Model.User;
import com.struggleassist.Model.ViewContext;

/**
 * Created by Lucas on 10/15/2017.
 * Has access to the applicaiton stored preferences
 */

public class PreferencesController {

    private static Context context = ViewContext.getContext();
    private static String userFileName = "com.StruggleAssist.USER";
    private static String settingsFileName = "com.StruggleAssist.SETTINGS";
    private static SharedPreferences userPref = context.getSharedPreferences(userFileName, Context.MODE_PRIVATE);
    private static SharedPreferences settingsPref = context.getSharedPreferences(settingsFileName, Context.MODE_PRIVATE);

    public static void saveUser(User user){
    }

    public static User readUser(){
        return null;
    }

    public static int[] readSettings(){
        return null;
    }
}
