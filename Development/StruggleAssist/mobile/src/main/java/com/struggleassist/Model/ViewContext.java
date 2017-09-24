package com.struggleassist.Model;

import android.content.Context;

/**
 * Created by lucas on 9/15/2017.
 */

public class ViewContext {
    public static Context context;

    public static void setContext(Context newContext){
        context = newContext;
    }

    public static Context getContext(){
        return context;
    }
}
