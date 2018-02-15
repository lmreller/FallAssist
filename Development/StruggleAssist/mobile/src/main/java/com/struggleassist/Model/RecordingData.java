package com.struggleassist.Model;

import android.net.Uri;

/**
 * Created by Lucas on 2/14/2018.
 */

public class RecordingData {

    private static String Address;
    private static Uri VideoUri;

    public static String getAddress() {
        return Address;
    }

    public static void setAddress(String address) {
        Address = address;
    }

    public static Uri getVideoUri()
    {
        return VideoUri;
    }

    public static void setVideoUri(Uri videoUri)
    {
        VideoUri = videoUri;
    }

}
