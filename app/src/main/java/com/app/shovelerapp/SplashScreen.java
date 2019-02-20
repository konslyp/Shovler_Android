package com.app.shovelerapp;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.app.shovelerapp.activity.AvailableJobActivity;
import com.app.shovelerapp.activity.FirstStepActivity;
import com.app.shovelerapp.activity.LoginActivity;
import com.app.shovelerapp.activity.PhoneVerifyActivity;
import com.app.shovelerapp.activity.WelcomeShovelerActivity;
import com.app.shovelerapp.utils.Constants;
import com.app.shovelerapp.utils.SharedPrefClass;
import com.google.firebase.iid.FirebaseInstanceId;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class SplashScreen extends AppCompatActivity {
    private static final String TAG = SplashScreen.class.getSimpleName();
    private static int SPLASH_TIME_OUT = 5000;
    private SharedPrefClass prefClass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_screen);

        prefClass=new SharedPrefClass(SplashScreen.this);
        Constants.DEVICE_ID= FirebaseInstanceId.getInstance().getToken();
//        try {
//            PackageInfo info = getPackageManager().getPackageInfo(
//                    "com.app.shovelerapp",
//                    PackageManager.GET_SIGNATURES);
//            for (Signature signature : info.signatures) {
//                MessageDigest md = MessageDigest.getInstance("SHA");
//                md.update(signature.toByteArray());
//                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
//            }
//        } catch (PackageManager.NameNotFoundException e) {
//
//        } catch (NoSuchAlgorithmException e) {
//
//        }


        new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity
                if (prefClass.isUserLogin()){
                    if (prefClass.getSavedStringPreference(SharedPrefClass.UTYPE).equals("Shovler")) {
//                        if (prefClass.isPhoneVerify())
//                        {
//                            Intent i = new Intent(SplashScreen.this, AvailableJobActivity.class);
//                            startActivity(i);
//                        }
//                        else
//                        {
//                            Intent i = new Intent(SplashScreen.this, PhoneVerifyActivity.class);
//                            startActivity(i);
//                        }

                        Intent i = new Intent(SplashScreen.this, AvailableJobActivity.class);
                        startActivity(i);
                        // close this activity
                        finish();
                    } else {
                        Intent i = new Intent(SplashScreen.this, FirstStepActivity.class);
                        startActivity(i);
                        // close this activity
                        finish();

                    }
                }else {
                    Intent i = new Intent(SplashScreen.this, WelcomeShovelerActivity.class);
                    startActivity(i);
                    // close this activity
                    finish();
                }
            }
        }, SPLASH_TIME_OUT);
    }
}
