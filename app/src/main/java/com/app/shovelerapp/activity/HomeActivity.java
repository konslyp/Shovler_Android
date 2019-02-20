package com.app.shovelerapp.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.shovelerapp.R;
import com.app.shovelerapp.callback.GetAddressFromLatLongCallback;
import com.app.shovelerapp.callback.LoginCallback;
import com.app.shovelerapp.callback.UpdateLatLngCallback;
import com.app.shovelerapp.netutils.NetUtils;
import com.app.shovelerapp.service.GPSTracker;
import com.app.shovelerapp.utils.Constants;
import com.app.shovelerapp.utils.SharedPrefClass;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Collection;


public class HomeActivity extends AppCompatActivity implements LoginCallback, UpdateLatLngCallback,
        GetAddressFromLatLongCallback{
    private static final String TAG = HomeActivity.class.getSimpleName();
    CallbackManager callbackManager;
    private LoginButton mFacebook;
    public LoginManager loginManager;
    private ImageView mFacebookImageView;
    private TextView mLogin;
    private Button mSignUp;
    public static HomeActivity activity;
    private ProgressDialog mProgressDilog;
    private String email;
    private SharedPrefClass prefClass;
    private int jobFlag = 0;
    private CharSequence[] jobTypeList = {"SHOVLER", "REQUESTER"};

    private String firstName = "";
    private String lastName = "";

    private String currentAddress = "";

    private GPSTracker tracker;
    private Double lat, lng;
    private String userId = "";

    public Collection<String> permissions = Arrays.asList("public_profile","email");


    private FacebookCallback<LoginResult> mCallBack = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {
            GraphRequest request = GraphRequest.newMeRequest(
                    loginResult.getAccessToken(),
                    new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(
                                JSONObject object,
                                GraphResponse response) {
                            // Application code
                            Log.v("FacebookActivity", object.toString());

                            //Toast.makeText(HomeActivity.this, object.toString(), Toast.LENGTH_SHORT).show();
                            try {
                                if (response.getJSONObject().has("first_name")) {
                                    firstName = response.getJSONObject().getString("first_name");
                                }
                                if (response.getJSONObject().has("last_name")) {
                                    lastName = response.getJSONObject().getString("last_name");
                                }

                                Log.v("email", object.getString("email"));
                                email = object.getString("email");

                                if (Constants.LOGIN_FROM.equals("SETTINGS")) {
                                    showSelectTypeAlert();
                                } else {
                                    callLoginApi();
                                }
                            } catch (JSONException e) {
                                    /*Constants.showAlert(HomeActivity.this,"Your email id is private");*/
                                    //email = "fb-" + object.getString("id") + "@shovler.com";

                                    AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
                                    builder.setTitle("No email address is associated with this Facebook account. Please enter your email address now");

// Set up the input
                                    final EditText input = new EditText(HomeActivity.this);
                                    builder.setCancelable(false);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                                    input.setInputType(InputType.TYPE_CLASS_TEXT);
                                    builder.setView(input);

// Set up the buttons
                                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (input.getText().toString().equals(""))
                                            {
                                                Toast.makeText(HomeActivity.this,"Empty Email Address",Toast.LENGTH_SHORT).show();
                                                return;
                                            }
                                            if (input.getText().toString().contains("@"))
                                            {
                                                Toast.makeText(HomeActivity.this,"Please input validate Email address",Toast.LENGTH_SHORT).show();
                                                return;
                                            }
                                            email = input.getText().toString();
                                            if (Constants.LOGIN_FROM.equals("SETTINGS")) {
                                                showSelectTypeAlert();
                                            } else {
                                                callLoginApi();
                                            }
                                        }
                                    });
                                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                        }
                                    });

                                    builder.show();
                                LoginManager.getInstance().logOut();
                                e.printStackTrace();
                            }

                        }
                    });
            Bundle parameters = new Bundle();
            parameters.putString("fields", "id,name,first_name,last_name,email,gender, birthday");
            request.setParameters(parameters);
            request.executeAsync();
            System.out.println("Facebook Login Successful!");
            System.out.println("Logged in user Details : ");
            System.out.println("--------------------------");
            System.out.println("User ID  : " + loginResult.getAccessToken().getUserId());
            System.out.println("Authentication Token : " + loginResult.getAccessToken().getToken());
        }

        @Override
        public void onCancel() {
            String ss = "";
            int i = 1;
        }

        @Override
        public void onError(FacebookException error) {
            String ss = "";
            int i = 1;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(HomeActivity.this);
        callbackManager = CallbackManager.Factory.create();

        loginManager    =   LoginManager.getInstance();
        loginManager.registerCallback(callbackManager,mCallBack);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_home);

        tracker = new GPSTracker(HomeActivity.this);

        lat = tracker.getLatitude();
        lng = tracker.getLongitude();

        activity = this;
        init();

        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.app.shovelerapp", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.e("KeyHash:",
                        Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        mFacebookImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //mFacebook.performClick();
                loginManager.logInWithReadPermissions(HomeActivity.this, permissions);
            }
        });

        // Callback registration
        // LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile"));
        //  mFacebook.setPublishPermissions(Arrays.asList("public_profile"));
        mFacebook.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(
                                    JSONObject object,
                                    GraphResponse response) {
                                // Application code
                                Log.v("FacebookActivity", object.toString());

                                //Toast.makeText(HomeActivity.this, object.toString(), Toast.LENGTH_SHORT).show();
                                try {
                                    if (response.getJSONObject().has("first_name")) {
                                        firstName = response.getJSONObject().getString("first_name");
                                    }
                                    if (response.getJSONObject().has("last_name")) {
                                        lastName = response.getJSONObject().getString("last_name");
                                    }

                                    Log.v("email", object.getString("email"));
                                    email = object.getString("email");

                                    if (Constants.LOGIN_FROM.equals("SETTINGS")) {
                                        showSelectTypeAlert();
                                    } else {
                                        callLoginApi();
                                    }
                                } catch (JSONException e) {
                                    /*Constants.showAlert(HomeActivity.this,"Your email id is private");*/
                                    try {
                                        email = "fb-" + object.getString("id") + "@shovler.com";
                                        if (Constants.LOGIN_FROM.equals("SETTINGS")) {
                                            showSelectTypeAlert();
                                        } else {
                                            callLoginApi();
                                        }
                                    } catch (JSONException e1) {
                                        e1.printStackTrace();
                                    }

                                    LoginManager.getInstance().logOut();
                                    e.printStackTrace();
                                }

                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,first_name,last_name,email,gender, birthday");
                request.setParameters(parameters);
                request.executeAsync();
                System.out.println("Facebook Login Successful!");
                System.out.println("Logged in user Details : ");
                System.out.println("--------------------------");
                System.out.println("User ID  : " + loginResult.getAccessToken().getUserId());
                System.out.println("Authentication Token : " + loginResult.getAccessToken().getToken());
                // Toast.makeText(SignUpActivity.this, "Login Successful!", Toast.LENGTH_LONG).show();
            }


            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
            }
        });


        mSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(HomeActivity.this, SignUpActivity.class),10);
            }
        });

        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, LoginActivity.class));
            }
        });


    }

    private void showSelectTypeAlert() {
        final AlertDialog.Builder chooseDrinkdialog = new AlertDialog.Builder(
                HomeActivity.this);
        View titleview = getLayoutInflater().inflate(R.layout.alert_title, null);
        TextView title1 = (TextView) titleview.findViewById(R.id.dialogtitle);
        title1.setText("Select Job Type");
        chooseDrinkdialog.setCustomTitle(titleview);
        chooseDrinkdialog.setCancelable(false);

        chooseDrinkdialog.setSingleChoiceItems(jobTypeList, jobFlag,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        jobFlag = item;
                        switch (jobTypeList[item].toString()) {
                            case "SHOVLER":
                                Constants.USER_TYPE = "Shovler";
                                callLoginApi();
                                break;
                            case "REQUESTER":
                                Constants.USER_TYPE = "Requester";
                                callLoginApi();
                                break;

                            default:

                        }
                        dialog.dismiss();
                    }
                });

        chooseDrinkdialog.show();
    }

    private void init() {
        mFacebook = (LoginButton) findViewById(R.id.login_button);
        mFacebookImageView = (ImageView) findViewById(R.id.facebook_click2);
        mLogin = (TextView) findViewById(R.id.start_shoveling);
        mSignUp = (Button) findViewById(R.id.signup);
    }

    private void callLoginApi() {
        mProgressDilog = ProgressDialog.show(HomeActivity.this, "",
                getResources().getString(R.string.loading), true, false);
        NetUtils.CallLogin(email, "", "1", firstName, lastName, this, HomeActivity.this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 10 && resultCode == 100) {
            startActivity(new Intent(HomeActivity.this, LoginActivity.class));
        }

    }

    @Override
    public void LoginCallbackSuccess(String success) {
        mProgressDilog.dismiss();
        try {
            JSONObject jsonObject = new JSONObject(success);
            if (jsonObject.getString("status").equals("true")) {
                try {
                    LoginManager.getInstance().logOut();
                } catch (Exception e) {
                }
                final JSONArray jsonArray = jsonObject.getJSONArray("items");
                putValuesInPreferences(jsonArray);
                AlertDialog.Builder builder1 = new AlertDialog.Builder(HomeActivity.this);
                builder1.setMessage(jsonObject.getString("message"));
                builder1.setCancelable(true);
                builder1.setPositiveButton("Ok",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                try {
                                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                                    // TODO: 11/4/2016 Send current location to server
                                    sendCurrentLocation();

                                    if (jsonObject.getString("utype").equals("Shovler")) {
                                        startActivity(new Intent(HomeActivity.this, AvailableJobActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                    } else {
                                        if (Constants.LOGIN_FROM.equals("SETTINGS")) {
                                            startActivity(new Intent(HomeActivity.this, FirstStepActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                        } else {
                                            finish();
                                        }
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        });


                AlertDialog alert11 = builder1.create();
                alert11.show();

            } else {
                try {
                    LoginManager.getInstance().logOut();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Constants.showAlert(HomeActivity.this, jsonObject.getString("message"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void sendCurrentLocation() {

//        currentAddress = Constants.getCompleteAddressString(HomeActivity.this, lat, lng);
//        userId = prefClass.getSavedStringPreference(SharedPrefClass.USER_ID);
//        NetUtils.CallNewUpdateLatLng(userId, String.valueOf(lat), String.valueOf(lng),
//                currentAddress, this, this);

        NetUtils.getAddressSuggestions("", String.valueOf(lat), String.valueOf(lng), HomeActivity.this,
                HomeActivity.this);
    }

    @Override
    public void LoginCallbackError(String error) {
        mProgressDilog.dismiss();
    }

    private void putValuesInPreferences(JSONArray jsonArray) {
        try {
            JSONObject jsonObject = jsonArray.getJSONObject(0);
            prefClass = new SharedPrefClass(HomeActivity.this);
            prefClass.savePreference(SharedPrefClass.USER_ID, jsonObject.getString("uid"));
            prefClass.savePreference(SharedPrefClass.FNAME, jsonObject.getString("fname"));
            prefClass.savePreference(SharedPrefClass.LNAME, jsonObject.getString("lname"));
            prefClass.savePreference(SharedPrefClass.ADDRESS, jsonObject.getString("address"));
            //prefClass.savePreference(SharedPrefClass.ZIPCODE,jsonObject.getString("zipcode"));
            //prefClass.savePreference(SharedPrefClass.MOBILE_NO,jsonObject.getString("mobno"));
            prefClass.savePreference(SharedPrefClass.EMAIL, jsonObject.getString("emailid"));
            prefClass.savePreference(SharedPrefClass.PASSWORD, jsonObject.getString("password"));
            prefClass.savePreference(SharedPrefClass.UTYPE, jsonObject.getString("utype"));
            prefClass.savePreference(SharedPrefClass.FBSTATUS, jsonObject.getString("fbstatus"));
            prefClass.savePreference(SharedPrefClass.JOB_CNT, jsonObject.getString("jobcnt"));
            prefClass.savePreferenceBoolean(SharedPrefClass.LOGIN_STATUS, true);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void UpdateLatLngCallbackSuccess(String success) {

    }

    @Override
    public void UpdateLatLngCallbackError(String error) {

    }

    @Override
    public void getAddressFromLatLongSuccess(String success) {
        Log.e(TAG, success);
        try {
            JSONObject successJsonObject = new JSONObject(success);
            if (successJsonObject.has("status")) {
                String status = successJsonObject.getString("status");
                if (status.equalsIgnoreCase("OK")) {
                    if (successJsonObject.has("results")) {
                        JSONArray resultsJsonArray = successJsonObject.getJSONArray("results");
                        int size = resultsJsonArray.length();
                        if (size > 0) {
                            JSONObject resultJsonObject = resultsJsonArray.getJSONObject(0);
                            if (resultJsonObject.has("formatted_address")) {
                                currentAddress = resultJsonObject.getString("formatted_address");
                                userId = prefClass.getSavedStringPreference(SharedPrefClass.USER_ID);
                                NetUtils.CallNewUpdateLatLng(userId, String.valueOf(lat), String.valueOf(lng),
                                        currentAddress, this, this);
                            }
                        }
                    }
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void getAddressFromLatLongError(String error) {

    }
}
