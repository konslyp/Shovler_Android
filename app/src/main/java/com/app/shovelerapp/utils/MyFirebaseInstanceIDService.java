package com.app.shovelerapp.utils;


import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created on 5/27/2016.
 */


public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "MyFirebaseIIDService";
    public static final String REGISTRATION_SUCCESS = "RegistrationSuccess";


    private SharedPrefClass prefClass;

    @Override
    public void onTokenRefresh() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();

        prefClass=new SharedPrefClass(this);
        prefClass.savePreference(SharedPrefClass.DEVICE_ID,refreshedToken);

        Constants.DEVICE_ID = refreshedToken;
        Log.e(TAG, "Refreshed token: " + refreshedToken);
    }

    private void sendRegistrationToServer(String token) {

    }
}