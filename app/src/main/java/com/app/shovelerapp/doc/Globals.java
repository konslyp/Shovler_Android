package com.app.shovelerapp.doc;

import com.app.shovelerapp.model.ChatModel;
import com.app.shovelerapp.model.RequestorJobModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 4/6/2017.
 */
public class Globals {
    public static String g_dutyStatus = "0";
    public static int g_availableShovler = 0;
    public static String g_googlePhoto = "";
    public static String g_shovlerLat = "";
    public static String g_shovlerLng = "";
    public static String g_code = "";
    public static String g_email = "";
    public static String g_emailStatus = "0";
    public static String g_currentJobID;
    public static String g_phoneCode = "";
    public static String g_phoneNumber = "";

    public static String g_testPhone = "";

    //payment

    public static String g_clientToken = "";


    public static List<ChatModel> g_lstMessages = new ArrayList<ChatModel>();
}
