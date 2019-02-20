package com.app.shovelerapp.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by supriya.n on 06-07-2016.
 */
public class SharedPrefClass {
    private Context context;

    public static final String USER_ID="uid";
    public static final String FNAME="fname";
    public static final String LNAME="lname";
    public static final String ADDRESS="address";
    public static final String ZIPCODE="zipcode";
    public static final String MOBILE_NO="mobno";
    public static final String EMAIL="emailid";
    public static final String PASSWORD="password";
    public static final String UTYPE="utype";
    public static final String FBSTATUS="fbstatus";
    public static final String LOGIN_STATUS = "LoginStatus";
    public static final String PHONE_STATUS = "PhoneStatus";
    public static final String JOB_CNT = "jobcnt";
    public static final String DEVICE_ID = "device_id";
    public static final String API_KEY = "apikey";

    private static final String DRIVER_USERNAME = "username";
    private static final String DRIVER_PASSWORD = "password";

    private SharedPreferences preferences = null;
    public static SharedPrefClass sharedPreferenceDataManager = null;

    public SharedPrefClass(Context context) {
        this.context = context;
        if (preferences == null) {
            preferences = context.getSharedPreferences(Constants.PREF_NAME,0);
        }
    }

    public static SharedPrefClass getInstance(Context context) {
        if (sharedPreferenceDataManager == null) {
            sharedPreferenceDataManager = new SharedPrefClass(context);
        }
        return sharedPreferenceDataManager;
    }

    public String getSavedStringPreference(String key) {
        String value = preferences.getString(key, "");
        return value;
    }

    public boolean getSavedBooleanPreference(String key) {
        boolean value = preferences.getBoolean(key, false);
        return value;
    }

    /* check login or not */
    public boolean isUserLogin() {
        return preferences.getBoolean(LOGIN_STATUS, false);
    }

    public boolean isPhoneVerify() {
        return preferences.getBoolean(PHONE_STATUS, false);
    }

    public void setUserLogin(boolean loginStatus) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(LOGIN_STATUS, loginStatus);
        editor.apply();
    }

    public void savePreference(String key, String value) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.apply();
    }
    public void savePreferenceBoolean(String key, boolean value) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public void clearKeyVal(){
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();
    }
    public boolean getSavedBooleanPreferenceWithDefaultValue(String key, boolean retuenDefaultValue) {
        boolean value = preferences.getBoolean(key, retuenDefaultValue);
        return value;
    }
//
//    public String getUsername() {
//        return preferences.getString(DRIVER_USERNAME, null);
//    }
//
//    public void setUsername(String username) {
//        SharedPreferences.Editor mEditor = preferences.edit();
//        mEditor.putString(DRIVER_USERNAME, username);
//        mEditor.apply();
//    }
//
//    public String getPassword() {
//        return preferences.getString(DRIVER_PASSWORD, null);
//    }
//
//    public void setPassword(String password) {
//        SharedPreferences.Editor mEditor = preferences.edit();
//        mEditor.putString(DRIVER_PASSWORD, password);
//        mEditor.apply();
//    }

}
