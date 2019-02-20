package com.app.shovelerapp.activity;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;

import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.shovelerapp.R;

import com.app.shovelerapp.callback.LogoutCallback;
import com.app.shovelerapp.doc.Globals;
import com.app.shovelerapp.netutils.NetUtils;
import com.app.shovelerapp.service.ILoadService;
import com.app.shovelerapp.service.ServiceManager;
import com.app.shovelerapp.utils.Constants;
import com.app.shovelerapp.utils.SharedPrefClass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Set;

/**
 * Created by supriya.n on 10-06-2016.
 */
public class SettingActivity extends AppCompatActivity implements View.OnClickListener,ILoadService,NavigationView.OnNavigationItemSelectedListener,LogoutCallback {
    private RelativeLayout mMyInfoRelativeLayout, mBalanceRelativeLayout, mAboutRelativeLayout, mFeedbackRelativeLayout, mNotificationRelativeLayout, mPromoCodeRelativeLayout, mFAQRelativeLayout,mLogoutRelativeLayout;
    private TextView mMyInfoTextView, mMyInfoDescTextView, mBalanceTitle, mBalance, mTotalBalance, mAboutTitle, mAboutDesc, mFeedback, mFeedbackDesc, mNotification, mNotificatinDesc, mPromoCodeTv, mAppName1;
    private ImageView mSubLogo, mDrawer;
    private TextView mTitle, mLogout, mLogout_line;
    private SharedPrefClass prefClass;
    private DrawerLayout drawer;
    private ProgressDialog mProgressDilog;
    NavigationView navigationView;

    public RelativeLayout relEmailNotify;
    public TextView txtEmailStatus;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_configuration);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mSubLogo = (ImageView) toolbar.findViewById(R.id.logo_image);
        mTitle = (TextView) toolbar.findViewById(R.id.title);

        mSubLogo.setImageResource(R.drawable.settings1);
        mTitle.setText("SETTINGS");

        //Code for navigation drawer

        mDrawer = (ImageView) toolbar.findViewById(R.id.drawer);
        mDrawer.setVisibility(View.VISIBLE);

        relEmailNotify = (RelativeLayout) this.findViewById(R.id.relSettingEmailNotify);
        txtEmailStatus = (TextView) this.findViewById(R.id.txtEmailNotifyStatus);

        relEmailNotify.setOnClickListener(this);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.setDrawerIndicatorEnabled(false);
        toggle.syncState();

        mDrawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.openDrawer(GravityCompat.END);
            }
        });

        prefClass = new SharedPrefClass(SettingActivity.this);

        // navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (!prefClass.isUserLogin()) {
            navigationView.setVisibility(View.VISIBLE);
        } else {
            if (prefClass.getSavedStringPreference(SharedPrefClass.UTYPE).equals("Shovler")) {
                navigationView.getMenu().clear();
                navigationView.inflateMenu(R.menu.activity_welcome_shoveler_drawer);
            } else {
                navigationView.getMenu().clear();
                navigationView.inflateMenu(R.menu.activity_welcome_requestor_drawer);
            }
        }

        navigationView.setNavigationItemSelectedListener(this);


        mMyInfoRelativeLayout = (RelativeLayout) findViewById(R.id.my_info);
        mBalanceRelativeLayout = (RelativeLayout) findViewById(R.id.balance);
        mAboutRelativeLayout = (RelativeLayout) findViewById(R.id.about);
        mFeedbackRelativeLayout = (RelativeLayout) findViewById(R.id.feedback);
        mNotificationRelativeLayout = (RelativeLayout) findViewById(R.id.notification);
        mPromoCodeRelativeLayout = (RelativeLayout) findViewById(R.id.promocode);
        mFAQRelativeLayout = (RelativeLayout) findViewById(R.id.faq);
        mLogoutRelativeLayout = (RelativeLayout) findViewById(R.id.logout_layout);

        mMyInfoTextView = (TextView) findViewById(R.id.my_info_title);
        mMyInfoDescTextView = (TextView) findViewById(R.id.my_info_desc);
        mBalanceTitle = (TextView) findViewById(R.id.balance_tv);
        mBalance = (TextView) findViewById(R.id.price_tv);
        mTotalBalance = (TextView) findViewById(R.id.total_price_tv);
        mAboutTitle = (TextView) findViewById(R.id.about_tv);
        mAboutDesc = (TextView) findViewById(R.id.app_name_tv);
        mFeedback = (TextView) findViewById(R.id.feedback_tv);
        mFeedbackDesc = (TextView) findViewById(R.id.feedback_desc);
        mNotification = (TextView) findViewById(R.id.notification_tv);
        mNotificatinDesc = (TextView) findViewById(R.id.notification_desc);
        mPromoCodeTv = (TextView) findViewById(R.id.promo_code_tv);
        mAppName1 = (TextView) findViewById(R.id.app_name_tv1);
        mLogout = (TextView) findViewById(R.id.logout);
        mLogout_line = (TextView) findViewById(R.id.logout_tv);


        setEmailStatus();
        if (!prefClass.isUserLogin()) {
            //  mLogout_line.setVisibility(View.GONE);
            mLogout.setText("LOGIN");
        } else {
            //mLogout_line.setVisibility(View.VISIBLE);
            mLogout.setText("LOGOUT");
        }

        ServiceManager.readEmailStatus(prefClass.getSavedStringPreference(SharedPrefClass.USER_ID),this);
        setFont();

        mMyInfoRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (prefClass.isUserLogin()) {
                    startActivity(new Intent(SettingActivity.this, Confi1MyInfoActivity.class));
                } else {
                    Constants.showAlert(SettingActivity.this, getResources().getString(R.string.please_login));
                }
            }
        });
        mBalanceRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (prefClass.isUserLogin()) {
                    startActivity(new Intent(SettingActivity.this, Confi2BalanceActivity.class));
                } else {
                    Constants.showAlert(SettingActivity.this, getResources().getString(R.string.please_login));
                }
            }
        });
        mAboutRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingActivity.this, Confi3AboutActivity.class));
            }
        });
        mFeedbackRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (prefClass.isUserLogin()) {
                    startActivity(new Intent(SettingActivity.this, Confi4FeedbackActivity.class));
                } else {
                    Constants.showAlert(SettingActivity.this, getResources().getString(R.string.please_login));
                }
            }
        });
        mNotificationRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        mLogoutRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mLogout.getText().equals("LOGIN")) {
                    Constants.LOGIN_FROM = "SETTINGS";
                    startActivity(new Intent(SettingActivity.this, HomeActivity.class));
                } else {
                    callLogoutApi();
                }

            }
        });

        mFAQRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startActivity(new Intent(SettingActivity.this, FAQActivity.class));
                startActivity(new Intent(SettingActivity.this, NewFAQActivity.class));
            }
        });

        mPromoCodeRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (prefClass.isUserLogin())
                    startActivity(new Intent(SettingActivity.this, SendPromoCodeActivity.class));
                else
                    Constants.showAlert(SettingActivity.this, getResources().getString(R.string.please_login));
            }
        });
    }

    private void callLogoutApi() {
        mProgressDilog = ProgressDialog.show(SettingActivity.this, "",
                getResources().getString(R.string.loading), true, false);

        NetUtils.CallLogOut(prefClass.getSavedStringPreference(SharedPrefClass.USER_ID),this,SettingActivity.this);
    }


    private void setFont() {
        Typeface tfRegular = Constants.setRegularLatoFont(SettingActivity.this);
        Typeface tfLight = Constants.setLightLatoFont(SettingActivity.this);
        Typeface tfThin = Constants.setThinLatoFont(SettingActivity.this);
        Typeface tfMedium = Constants.setMediumLatoFont(SettingActivity.this);

        mMyInfoTextView.setTypeface(tfRegular);
        mMyInfoDescTextView.setTypeface(tfRegular);
        mBalanceTitle.setTypeface(tfRegular);
        mBalance.setTypeface(tfRegular);
        mTotalBalance.setTypeface(tfRegular);
        mAboutTitle.setTypeface(tfRegular);
        mAboutDesc.setTypeface(tfRegular);
        mFeedback.setTypeface(tfRegular);
        mFeedbackDesc.setTypeface(tfRegular);
        mNotification.setTypeface(tfRegular);
        mNotificatinDesc.setTypeface(tfRegular);
        mPromoCodeTv.setTypeface(tfRegular);
        mAppName1.setTypeface(tfRegular);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //startActivity(new Intent(SettingActivity.this,WelcomeShovelerActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.request) {
            Log.v("click", "request");
            //startActivity(new Intent(WelcomeShovelerActivity.this, FirstStepActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            startActivity(new Intent(SettingActivity.this, FirstStepActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
        } else if (id == R.id.job_status) {
            Log.v("click", "job_status");
            startActivity(new Intent(SettingActivity.this, JobStatusActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
        } else if (id == R.id.aval_jobs) {
            Log.v("click", "aval_jobs");
            Constants.USER_TYPE = "Shovler";
            if (prefClass.isUserLogin()) {
                if (prefClass.getSavedStringPreference(SharedPrefClass.UTYPE).equals("Shovler"))
                    startActivity(new Intent(SettingActivity.this, AvailableJobActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            } else {
                startActivity(new Intent(SettingActivity.this, HomeActivity.class));
            }
        } else if (id == R.id.configuration) {
            Log.v("click", "configuration");
            // startActivity(new Intent(SettingActivity.this, SettingActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
        } else if (id == R.id.promocode) {
            Log.v("click", "promocode");
            startActivity(new Intent(SettingActivity.this, SendPromoCodeActivity.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.END);

        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (prefClass.isUserLogin()) {
            if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
                switch (event.getAction()) {
                    case KeyEvent.ACTION_DOWN:
                        if (event.getDownTime() - lastPressedTime < PERIOD) {
                            Intent intent = new Intent(Intent.ACTION_MAIN);
                            intent.addCategory(Intent.CATEGORY_HOME);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);

                        } else {

                            Toast.makeText(this, "Press the back button once again to close the application.", Toast.LENGTH_SHORT).show();

                            lastPressedTime = event.getEventTime();
                        }
                        return true;
                }
            }
        }
        return false;
    }

    private long lastPressedTime;
    private static final int PERIOD = 2000;

    @Override
    public void LogoutCallbackSuccess(String success) {
        mProgressDilog.dismiss();
        try {
            JSONObject jsonObject=new JSONObject(success);
            String msg=jsonObject.getString("items");
            AlertDialog.Builder builder1 = new AlertDialog.Builder(SettingActivity.this);
            builder1.setMessage(msg);
            builder1.setCancelable(false);
            builder1.setPositiveButton("Ok",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            prefClass.clearKeyVal();
                            prefClass.setUserLogin(false);
                            startActivity(new Intent(SettingActivity.this, WelcomeShovelerActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                        }
                    });


            AlertDialog alert11 = builder1.create();
            alert11.show();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void LogoutCallbackError(String error) {
        mProgressDilog.dismiss();
        Constants.showAlert(SettingActivity.this,error);
    }
    public void setEmailStatus()
    {
        if (Globals.g_emailStatus.equals("0"))
        {
            txtEmailStatus.setText("On");
        }
        else
        {
            txtEmailStatus.setText("Off");
        }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.relSettingEmailNotify:
                if (txtEmailStatus.getText().equals("On"))
                {
                    ServiceManager.updateEmailStatus(prefClass.getSavedStringPreference(SharedPrefClass.USER_ID),"1",this);
                }
                else
                {
                    ServiceManager.updateEmailStatus(prefClass.getSavedStringPreference(SharedPrefClass.USER_ID),"0",this);
                }
                break;
        }
    }

    @Override
    public void onResponse(int code) {
        switch (code)
        {
            case 0:
                setEmailStatus();
                break;
            case 2:
                setEmailStatus();
                break;
        }
    }

    @Override
    public void onResponse(int code, int index) {

    }
}
