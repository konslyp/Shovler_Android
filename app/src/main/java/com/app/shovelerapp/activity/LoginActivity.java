package com.app.shovelerapp.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.app.shovelerapp.R;

import com.app.shovelerapp.callback.GetAddressFromLatLongCallback;
import com.app.shovelerapp.callback.LoginCallback;
import com.app.shovelerapp.callback.UpdateLatLngCallback;
import com.app.shovelerapp.netutils.NetUtils;
import com.app.shovelerapp.service.GPSTracker;
import com.app.shovelerapp.utils.Constants;
import com.app.shovelerapp.utils.SharedPrefClass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity implements LoginCallback, UpdateLatLngCallback,
        GetAddressFromLatLongCallback {

    private static final String TAG = LoginActivity.class.getSimpleName();
    private Button mSignUpButton, mForgotButton, mStartShoveling;

    private TextView EmailTitle;
    private EditText mEmailEditText, mPassEditText;
    private ProgressDialog mProgressDilog;
    private RadioGroup radioGroup;
    private RadioButton mRequestorRadioButton, mShovelerRadioButton;
    private SharedPrefClass prefClass;
    private String strSelection;

    private String currentAddress = "";

    private GPSTracker tracker;
    private Double lat, lng;
    private String userId = "";

    private static final String LOGIN_PREF = "LoginPref";
    private SharedPreferences mSharedPreferences = null;
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        tracker = new GPSTracker(LoginActivity.this);

        lat = tracker.getLatitude();
        lng = tracker.getLongitude();

//        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
//                new IntentFilter("pranoti"));

        init();
        mSharedPreferences = LoginActivity.this.getSharedPreferences(LOGIN_PREF, 0);
        if (getUsername() != null)
            mEmailEditText.setText(getUsername());
        if (getPassword() != null)
            mPassEditText.setText(getPassword());

//        mEmailEditText, mPassEditText

        if (Constants.USER_TYPE.equals("Shovler")) {
            mShovelerRadioButton.setChecked(true);
            mRequestorRadioButton.setChecked(false);
            mRequestorRadioButton.setClickable(false);
            mShovelerRadioButton.setClickable(false);
        } else {
            mRequestorRadioButton.setChecked(true);
            mShovelerRadioButton.setChecked(false);
            mRequestorRadioButton.setClickable(false);
            mShovelerRadioButton.setClickable(false);
        }

        if (Constants.LOGIN_FROM.equals("SETTINGS")) {
            mRequestorRadioButton.setClickable(true);
            mShovelerRadioButton.setClickable(true);

            mRequestorRadioButton.setChecked(true);
            mShovelerRadioButton.setChecked(false);
        }


        mSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(LoginActivity.this, SignUpActivity.class),100);
            }
        });

        mForgotButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, ForgetActivity.class));
            }
        });

        mStartShoveling.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isValidate()) {
                    callLoginApi();
                }

            }
        });


    }

    private void callLoginApi() {
        mProgressDilog = ProgressDialog.show(LoginActivity.this, "",
                getResources().getString(R.string.loading), true, false);
        NetUtils.CallLogin(mEmailEditText.getText().toString(), mPassEditText.getText().toString(), "0", this, LoginActivity.this);
    }

    private boolean isValidate() {
        if (radioGroup.getCheckedRadioButtonId() != -1) {
            int id = radioGroup.getCheckedRadioButtonId();
            View radioButton = radioGroup.findViewById(id);
            int radioId = radioGroup.indexOfChild(radioButton);
            RadioButton btn = (RadioButton) radioGroup.getChildAt(radioId);
            strSelection = (String) btn.getText();
            Log.v("selected rb", strSelection);

            if (strSelection.equals("Requester")) {
                Constants.USER_TYPE = "Requester";
            } else {
                Constants.USER_TYPE = "Shovler";
            }

        }

        if (mEmailEditText.getText().toString().equals("") && mPassEditText.getText().toString().equals("")) {
            setErrorFun(mEmailEditText, getResources().getString(R.string.all_fields));
            return false;
        } else if (mEmailEditText.getText().toString().equals("")) {
            setErrorFun(mEmailEditText, getResources().getString(R.string.enter_email));
            return false;
        } else if (!Constants.isValidEmail(mEmailEditText.getText().toString())) {
            setErrorFun(mEmailEditText, getResources().getString(R.string.enter_valid_email));
            return false;
        } else if (mPassEditText.getText().toString().equals("")) {
            setErrorFun(mPassEditText, getResources().getString(R.string.enter_pass));
            return false;
        } else {
            return true;
        }
    }

    private void init() {
        mSignUpButton = (Button) findViewById(R.id.sigup);

        mForgotButton = (Button) findViewById(R.id.forget_pass);
        mStartShoveling = (Button) findViewById(R.id.start_shoveling);

        radioGroup = (RadioGroup) findViewById(R.id.radio_button);
        mRequestorRadioButton = (RadioButton) findViewById(R.id.requester_id);
        mShovelerRadioButton = (RadioButton) findViewById(R.id.shoveler_id);

//        mSocialtitle = (TextView) findViewById(R.id.sign_in_social_net_title);
        EmailTitle = (TextView) findViewById(R.id.sign_in_email_title);

        mEmailEditText = (EditText) findViewById(R.id.email_ed);
        mPassEditText = (EditText) findViewById(R.id.pass_ed);

        setFont();

    }

    private void setErrorFun(EditText edit, String msg) {
        /*edit.setError(msg);
        edit.requestFocus();*/
        Snackbar.make(edit, msg, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
        edit.requestFocus();
    }

    private void setFont() {
        Typeface tfRegular = Constants.setRegularLatoFont(LoginActivity.this);
        Typeface tfLight = Constants.setLightLatoFont(LoginActivity.this);
        Typeface tfThin = Constants.setThinLatoFont(LoginActivity.this);
        Typeface tfMedium = Constants.setMediumLatoFont(LoginActivity.this);

        EmailTitle.setTypeface(tfRegular);

        mEmailEditText.setTypeface(tfRegular);
        mPassEditText.setTypeface(tfRegular);

        mForgotButton.setTypeface(tfRegular);
        mStartShoveling.setTypeface(tfRegular);
        mSignUpButton.setTypeface(tfRegular);
    }


    @Override
    public void LoginCallbackSuccess(String success) {
        mProgressDilog.dismiss();

        try {
            JSONObject jsonObject = new JSONObject(success);
            if (jsonObject.getString("status").equals("true")) {
                final JSONArray jsonArray = jsonObject.getJSONArray("items");
                putValuesInPreferences(jsonArray);
                AlertDialog.Builder builder1 = new AlertDialog.Builder(LoginActivity.this);
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
                                        /*if (jsonObject.getString("isphone").equals("0"))
                                        {
                                            startActivity(new Intent(LoginActivity.this,
                                                    PhoneVerifyActivity.class));
                                        }
                                        else {
                                            startActivity(new Intent(LoginActivity.this,
                                                    AvailableJobActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                        }*/
                                        boolean isPhone = false;
                                        if (jsonObject.getString("isphone").equals("0"))
                                            isPhone = false;
                                        else isPhone = true;
                                        prefClass.savePreferenceBoolean(SharedPrefClass.PHONE_STATUS, isPhone);

                                        startActivity(new Intent(LoginActivity.this,
                                                AvailableJobActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                    } else {
                                        if (Constants.LOGIN_FROM.equals("SETTINGS")) {
                                            startActivity(new Intent(LoginActivity.this,
                                                    FirstStepActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                        } else {
                                            finish();
                                            HomeActivity.activity.finish();
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
                Constants.showAlert(LoginActivity.this, jsonObject.getString("message"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void showAlertEmail()
    {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(LoginActivity.this);
                builder1.setMessage("Welcome to Shovler! Please check your email (including spam folder) to activate your account.");
                builder1.setCancelable(true);
                builder1.setPositiveButton("Ok",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });


                AlertDialog alert11 = builder1.create();
                alert11.show();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == 100 && resultCode == 100) {
            showAlertEmail();
        }
    }
    private void sendCurrentLocation() {
//        currentAddress = Constants.getCompleteAddressString(LoginActivity.this, lat, lng);
//        userId = prefClass.getSavedStringPreference(SharedPrefClass.USER_ID);
//        NetUtils.CallNewUpdateLatLng(userId, String.valueOf(lat), String.valueOf(lng),
//                currentAddress, this, this);

        NetUtils.getAddressSuggestions("", String.valueOf(lat), String.valueOf(lng), LoginActivity.this,
                LoginActivity.this);
    }

    private void putValuesInPreferences(JSONArray jsonArray) {
        try {
            JSONObject jsonObject = jsonArray.getJSONObject(0);
            prefClass = new SharedPrefClass(LoginActivity.this);
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
            prefClass.savePreference(SharedPrefClass.API_KEY, jsonObject.getString("apikey"));

            setUsername(mEmailEditText.getText().toString());
            setPassword(mPassEditText.getText().toString());

            try {
                prefClass.savePreference(SharedPrefClass.JOB_CNT, jsonObject.getString("jobcnt"));
            } catch (Exception e) {
                e.printStackTrace();
            }
            prefClass.savePreferenceBoolean(SharedPrefClass.LOGIN_STATUS, true);
            prefClass.savePreferenceBoolean(SharedPrefClass.PHONE_STATUS, false);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void LoginCallbackError(String error) {
        mProgressDilog.dismiss();
        Constants.showAlert(LoginActivity.this, error);
    }

    @Override
    public void UpdateLatLngCallbackSuccess(String success) {

    }

    @Override
    public void UpdateLatLngCallbackError(String error) {

    }

    public String getUsername() {
        return mSharedPreferences.getString(USERNAME, null);
    }

    public void setUsername(String username) {
        SharedPreferences.Editor mEditor = mSharedPreferences.edit();
        mEditor.putString(USERNAME, username);
        mEditor.apply();
    }

    public String getPassword() {
        return mSharedPreferences.getString(PASSWORD, null);
    }

    public void setPassword(String password) {
        SharedPreferences.Editor mEditor = mSharedPreferences.edit();
        mEditor.putString(PASSWORD, password);
        mEditor.apply();
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

//    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            // Get extra data included in the Intent
//            currentAddress = intent.getStringExtra("address");
//            Log.e("receiver", "Got message: " + currentAddress);
//            if (currentAddress == null || currentAddress.equals(null) || currentAddress.equals("null")) {
////                mAddressEditText.setHint("Can't get address");
//            } else {
////                mAddressEditText.setText(address);
//                sendCurrentLocation(currentAddress);
//            }
//        }
//    };
}
