package com.app.shovelerapp.utils;

import com.app.shovelerapp.doc.Globals;

/**
 * Created by Administrator on 4/7/2017.
 */
public class Utils {
    public static void getImageFromGoogle(String lat,String lon)
    {
        String mapKey = "AIzaSyDMSVwjY-Ml_OeJpzDKQEM-8fNZWJnHXY8";
        Globals.g_googlePhoto = "https://maps.googleapis.com/maps/api/streetview?size=700x400&location=" +lat+","+lon
                + "&key=" + mapKey;

    }
}
