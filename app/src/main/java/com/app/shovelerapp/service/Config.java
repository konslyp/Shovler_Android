package com.app.shovelerapp.service;

import com.app.shovelerapp.netutils.Webservices;

/**
 * Created by Administrator on 4/6/2017.
 */
public class Config {
    public static String updateDutyStatusUrl = Webservices.base_url + "userreg.php/updateDutyStatus/";
    public static String readDutyStatusUrl = Webservices.base_url + "userreg.php/readDutyStatus/";
    public static String checkNearbyUrl = Webservices.base_url + "jobmgt.php/checkNearShovler/";
    public static String updateLocationUrl = Webservices.base_url + "userreg.php/updateLocation/";
    public static String getShovlerLocationUrl = Webservices.base_url + "userreg.php/getShovlerLocation/";
    public static String resetPasswordUrl = Webservices.base_url + "login.php/resetPassword/";
    public static String readEmailStatusUrl = Webservices.base_url + "login.php/readEmailStatus/";
    public static String updateEmailStatusUrl = Webservices.base_url + "login.php/updateEmailStatus/";

    //Chat

    public static String loadChatHistoryUrl = Webservices.base_url + "userreg.php/loadChatHistory/";
    public static String sendMessageUrl = Webservices.base_url + "userreg.php/sendMessage/";

    //Sms
    public static String sendSmsUrl = Webservices.base_url + "userreg.php/sendVerifySms/";
    public static String verifyPhoneUrl = Webservices.base_url + "userreg.php/verifyPhone/";

    //Payment
    public static String requestTokenUrl = Webservices.base_url + "userreg.php/getBraintreeToken/";

}
