package com.struggleassist.View.Notifications;

import android.content.Context;
import android.widget.Toast;

import com.struggleassist.Model.ViewContext;

/**
 * Created by Lucas on 9/18/2017.
 */

public class ToastController {
    private Context context;
    private CharSequence text;
    private Toast toast;

    public ToastController(CharSequence text){
        context = ViewContext.getContext();
        this.text = text;

    }

    public void showToastShort(){
        toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
        toast.show();
    }

    public void showToastLong(){
        toast = Toast.makeText(context, text, Toast.LENGTH_LONG);
        toast.show();
    }
}
