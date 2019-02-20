package com.app.shovelerapp.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.shovelerapp.R;
import com.app.shovelerapp.callback.GetAddressFromLatLongCallback;
import com.app.shovelerapp.callback.GetProfileCallback;
import com.app.shovelerapp.callback.SaveProfileCallback;
import com.app.shovelerapp.netutils.NetUtils;
import com.app.shovelerapp.service.GPSTracker;
import com.app.shovelerapp.utils.Constants;
import com.app.shovelerapp.utils.SharedPrefClass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by supriya.n on 10-06-2016.
 */
public class Confi1MyInfoActivity extends AppCompatActivity implements GetProfileCallback, SaveProfileCallback,
        GetAddressFromLatLongCallback{
    private static final String TAG = Confi1MyInfoActivity.class.getSimpleName();
    private TextView mFNameTextView, mLNameTextView, mHomeAddressTitleTextView, mPhoneTitle, mPhoneValue, mPaypal, mPaypalVal, mEmailTitle, mEmailValue;
    private EditText mfNameVal, mlNameVal, mHomeAddressValueTextView, mApiVal, mSearchVal;
    private ProgressDialog mProgressDilog;
    private String uid_str = null, zipcode_str = null, pass_str = null, u_type_str = null, fb_status = null, address, api_key = "";
    SharedPrefClass prefClass;
    private Button mSaveButton;
    private ImageView mSubLogo, mAddressPointer;
    private TextView mTitle;
    private GPSTracker tracker;
    private Double lat, lng;
    private RelativeLayout apiRelativeLayout, searchDistanceLayout;
    private Button helpButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_my_info);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mSubLogo = (ImageView) toolbar.findViewById(R.id.logo_image);
        mTitle = (TextView) toolbar.findViewById(R.id.title);

        mSubLogo.setImageResource(R.drawable.settings1);
        mTitle.setText("MY INFO & PAYMENT");

        tracker = new GPSTracker(Confi1MyInfoActivity.this);

        lat = tracker.getLatitude();
        lng = tracker.getLongitude();

        mFNameTextView = (TextView) findViewById(R.id.fname_title);
        mLNameTextView = (TextView) findViewById(R.id.lname_title);
        mfNameVal = (EditText) findViewById(R.id.fname_val);
        mlNameVal = (EditText) findViewById(R.id.lname_val);
        mApiVal = (EditText) findViewById(R.id.api_key_val);
        mSearchVal = (EditText) findViewById(R.id.search_dist_val);
        mHomeAddressTitleTextView = (TextView) findViewById(R.id.home_add_title);
        mHomeAddressValueTextView = (EditText) findViewById(R.id.home_add_value);
        mPhoneTitle = (TextView) findViewById(R.id.phone_title);
        mPhoneValue = (TextView) findViewById(R.id.phone_val);
        mPaypal = (TextView) findViewById(R.id.paypal_title);
        mPaypalVal = (TextView) findViewById(R.id.paypal_activate_val);
        mEmailTitle = (TextView) findViewById(R.id.email_title);
        mEmailValue = (TextView) findViewById(R.id.email_val);
        mSaveButton = (Button) findViewById(R.id.save);
        mAddressPointer = (ImageView) findViewById(R.id.address_indicator);
        apiRelativeLayout = (RelativeLayout) findViewById(R.id.api_key_info);
        searchDistanceLayout = (RelativeLayout) findViewById(R.id.search_dist);
        helpButton = (Button) findViewById(R.id.btn_help);

        setFont();

        prefClass = new SharedPrefClass(Confi1MyInfoActivity.this);

        uid_str = prefClass.getSavedStringPreference(SharedPrefClass.USER_ID);
        zipcode_str = prefClass.getSavedStringPreference(SharedPrefClass.ZIPCODE);
        pass_str = prefClass.getSavedStringPreference(SharedPrefClass.PASSWORD);
        u_type_str = prefClass.getSavedStringPreference(SharedPrefClass.UTYPE);
        fb_status = prefClass.getSavedStringPreference(SharedPrefClass.FBSTATUS);

        if (u_type_str.equals("Shovler")) {
            apiRelativeLayout.setVisibility(View.VISIBLE);
            searchDistanceLayout.setVisibility(View.VISIBLE);
        } else {
            apiRelativeLayout.setVisibility(View.GONE);
            searchDistanceLayout.setVisibility(View.GONE);
        }

        getProfile();


        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (u_type_str.equals("Shovler")) {
                    api_key = mApiVal.getText().toString();
                }
                if (mfNameVal.getText().equals("")) {
                    setErrorFun(mfNameVal, getResources().getString(R.string.enter_fname));
                } else if (mlNameVal.getText().toString().equals("")) {
                    setErrorFun(mfNameVal, getResources().getString(R.string.enter_lname));
                } else if (mHomeAddressValueTextView.getText().toString().equals("")) {
                    setErrorFun(mfNameVal, getResources().getString(R.string.enter_address));
                } else {
                    if (u_type_str.equals("Shovler")) {
                        if (mSearchVal.getText().toString().equals("")) {
                            setErrorFun(mSearchVal, getResources().getString(R.string.enter_search_dist));
                        } else {
                            saveProfile();
                        }
                    } else
                        saveProfile();
                }
            }
        });

        mAddressPointer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                address = Constants.getCompleteAddressString(Confi1MyInfoActivity.this, lat, lng);
                NetUtils.getAddressSuggestions("",String.valueOf(lat), String.valueOf(lng), Confi1MyInfoActivity.this,
                        Confi1MyInfoActivity.this);
//                mHomeAddressValueTextView.setText(address);
            }
        });

        helpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFAQAlert("Stripe key is required by shovelers to transfer payment in their stripe account. Refer shovelers section in FAQ for more details.",
                        "Stripe Info");
            }
        });
    }

    public void showFAQAlert(String message, String title) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(Confi1MyInfoActivity.this);
        builder1.setTitle(title);
        builder1.setMessage(message);
        builder1.setCancelable(false);
        builder1.setPositiveButton("Go To FAQ",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        startActivity(new Intent(Confi1MyInfoActivity.this, NewFAQActivity.class));
                    }
                });

        builder1.setNegativeButton("OK! Got it",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });


        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    private void setErrorFun(EditText edit, String msg) {
        /*edit.setError(msg);
        edit.requestFocus();*/
        Snackbar.make(edit, msg, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
        edit.requestFocus();
    }

    private void saveProfile() {
        mProgressDilog = ProgressDialog.show(Confi1MyInfoActivity.this, "",
                getResources().getString(R.string.loading), true, false);
        NetUtils.CallSaveProfile(uid_str, mfNameVal.getText().toString().trim(),
                mlNameVal.getText().toString().trim(), mHomeAddressValueTextView.getText().toString().trim(),
                zipcode_str, mPhoneValue.getText().toString(), mEmailValue.getText().toString(), pass_str,
                u_type_str, mSearchVal.getText().toString(), fb_status, api_key, this, Confi1MyInfoActivity.this);
    }

    private void getProfile() {
        mProgressDilog = ProgressDialog.show(Confi1MyInfoActivity.this, "",
                getResources().getString(R.string.loading), true, false);
        NetUtils.CallGetProfile(uid_str, this, Confi1MyInfoActivity.this);
    }

    private void setFont() {
        Typeface tfRegular = Constants.setRegularLatoFont(Confi1MyInfoActivity.this);
        Typeface tfLight = Constants.setLightLatoFont(Confi1MyInfoActivity.this);
        Typeface tfThin = Constants.setThinLatoFont(Confi1MyInfoActivity.this);
        Typeface tfMedium = Constants.setMediumLatoFont(Confi1MyInfoActivity.this);

        mFNameTextView.setTypeface(tfRegular);
        mLNameTextView.setTypeface(tfRegular);
        mfNameVal.setTypeface(tfRegular);
        mlNameVal.setTypeface(tfRegular);
        mHomeAddressTitleTextView.setTypeface(tfRegular);
        mHomeAddressValueTextView.setTypeface(tfRegular);
        mPhoneTitle.setTypeface(tfRegular);
        mPhoneValue.setTypeface(tfRegular);
        mPaypal.setTypeface(tfRegular);
        mPaypalVal.setTypeface(tfRegular);
        mEmailTitle.setTypeface(tfRegular);
        mEmailValue.setTypeface(tfRegular);

    }

    @Override
    public void GetProfileCallbackSuccess(String success) {
        mProgressDilog.dismiss();
        try {
            JSONObject jsonObject = new JSONObject(success);

            if (jsonObject.getString("status").equals("true")) {
                JSONArray array = jsonObject.getJSONArray("items");
                JSONObject object = array.getJSONObject(0);

                mfNameVal.setText(object.getString("fname"));
                mlNameVal.setText(object.getString("lname"));
                mEmailValue.setText(object.getString("emailid"));
                mHomeAddressValueTextView.setText(object.getString("address"));
                api_key = object.getString("apikey");
                mApiVal.setText(api_key);
                mSearchVal.setText(object.getString("searchdist"));
                // mPhoneValue.setText(object.getString("mobno"));

            } else {

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void GetProfileCallbackError(String error) {
        mProgressDilog.dismiss();
        Constants.showAlert(Confi1MyInfoActivity.this, error);
    }

    @Override
    public void SaveProfileCallbackSuccess(String success) {
        mProgressDilog.dismiss();
        try {
            JSONObject jsonObject = new JSONObject(success);
            if (jsonObject.getString("status").equals("true")) {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(Confi1MyInfoActivity.this);
                builder1.setMessage(getResources().getString(R.string.update_successfully));
                builder1.setCancelable(true);
                builder1.setPositiveButton("Ok",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                if (u_type_str.equals("Shovler")) {
                                    api_key = mApiVal.getText().toString();
                                    prefClass.savePreference(SharedPrefClass.API_KEY, api_key);
                                }
                                finish();
                            }
                        });


                AlertDialog alert11 = builder1.create();
                alert11.show();
                // Constants.showAlert(Confi1MyInfoActivity.this,);

            } else {
                Constants.showAlert(Confi1MyInfoActivity.this, jsonObject.getString("items"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void SaveProfileCallbackError(String error) {
        mProgressDilog.dismiss();
        Constants.showAlert(Confi1MyInfoActivity.this, error);
        mApiVal.setText("");
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
                                address = resultJsonObject.getString("formatted_address");

                                mHomeAddressValueTextView.setText(address);
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
