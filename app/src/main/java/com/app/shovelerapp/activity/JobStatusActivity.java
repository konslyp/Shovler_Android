package com.app.shovelerapp.activity;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.shovelerapp.R;
import com.app.shovelerapp.adapter.RequestListAdapter;
import com.app.shovelerapp.callback.GetRequestorJobCallback;
import com.app.shovelerapp.callback.GetShovelerJobCallback;
import com.app.shovelerapp.model.JobDetails;
import com.app.shovelerapp.model.RequestorJobModel;
import com.app.shovelerapp.netutils.NetUtils;
import com.app.shovelerapp.utils.Constants;
import com.app.shovelerapp.utils.SharedPrefClass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by supriya.n on 13-06-2016.
 */
public class JobStatusActivity extends AppCompatActivity implements GetRequestorJobCallback, NavigationView.OnNavigationItemSelectedListener, GetShovelerJobCallback {

    private ListView mRequestListView;
    private RequestListAdapter adapter;
    public static RelativeLayout mAcceptJobLayout;
    private TextView mOkTv;
    private ImageView mSubLogo, mDrawer, mRefresh;
    private TextView mTitle, mRequestTitle;
    private ProgressDialog mProgressDilog;
    private SharedPrefClass prefClass;
    public static ArrayList<RequestorJobModel> jobModels = new ArrayList<RequestorJobModel>();
    NavigationView navigationView;
    private DrawerLayout drawer;

    public static JobDetails details = new JobDetails();
    public static JobDetails.JobDetailsEntity jobdetailsEntity = new JobDetails.JobDetailsEntity();
    public static JobDetails.JobDetailsEntity.CarEntity carEntity = new JobDetails.JobDetailsEntity.CarEntity();
    public static JobDetails.JobDetailsEntity.HomeEntity homeEntity = new JobDetails.JobDetailsEntity.HomeEntity();
    public static JobDetails.JobDetailsEntity.BusinessEntity businessEntity = new JobDetails.JobDetailsEntity.BusinessEntity();
    public static boolean isRequestor = true;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.request_list_fragment);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        prefClass = new SharedPrefClass(JobStatusActivity.this);


        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mSubLogo = (ImageView) toolbar.findViewById(R.id.logo_image);
        mTitle = (TextView) toolbar.findViewById(R.id.title);

        mRequestTitle = (TextView) findViewById(R.id.request_title);

        mSubLogo.setImageResource(R.drawable.ic_action_job_status);
        mTitle.setText("JOB STATUS");

        //Code for navigation drawer

        mDrawer = (ImageView) toolbar.findViewById(R.id.drawer);
        mDrawer.setVisibility(View.VISIBLE);


        navigationView = (NavigationView) findViewById(R.id.nav_view);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        mRefresh = (ImageView) findViewById(R.id.refresh_button);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.setDrawerIndicatorEnabled(false);
        toggle.syncState();

        mRefresh = (ImageView) findViewById(R.id.refresh_button);



        // navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (!prefClass.isUserLogin()) {
            navigationView.setVisibility(View.VISIBLE);
            mRequestTitle.setText("Please Login");
            mRefresh.setVisibility(View.GONE);
        } else {
            mRefresh.setVisibility(View.VISIBLE);
            if (prefClass.getSavedStringPreference(SharedPrefClass.UTYPE).equals("Shovler")) {
                navigationView.getMenu().clear();
                navigationView.inflateMenu(R.menu.activity_welcome_shoveler_drawer);
                mRequestTitle.setText("SHOVELER");
                callShovelerJobApi();

            } else {
                navigationView.getMenu().clear();
                navigationView.inflateMenu(R.menu.activity_welcome_requestor_drawer);
                mRequestTitle.setText("REQUESTER");
                callRequestorJobApi();
            }
        }

        mRequestTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Constants.LOGIN_FROM="SETTINGS";
                startActivity(new Intent(JobStatusActivity.this,HomeActivity.class));
            }
        });

        navigationView.setNavigationItemSelectedListener(this);

        mRequestListView = (ListView) findViewById(R.id.request_list);
     /*   mAcceptJobLayout= (RelativeLayout) v.findViewById(R.id.accept_job_layout);
        mOkTv= (TextView) v.findViewById(R.id.ok_tv);*/

        mDrawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.openDrawer(GravityCompat.END);
            }
        });

        mRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (prefClass.getSavedStringPreference(SharedPrefClass.UTYPE).equals("Shovler")) {
                    callShovelerJobApi();
                } else {
                    callRequestorJobApi();
                }
            }
        });

    }

    private void callShovelerJobApi() {
        mProgressDilog = ProgressDialog.show(JobStatusActivity.this, "",
                getResources().getString(R.string.loading), true, false);
        NetUtils.CallGetShovelerJob(prefClass.getSavedStringPreference(SharedPrefClass.USER_ID), this, JobStatusActivity.this);
    }

    private void callRequestorJobApi() {
        mProgressDilog = ProgressDialog.show(JobStatusActivity.this, "",
                getResources().getString(R.string.loading), true, false);
        NetUtils.CallGetRequestorJob(prefClass.getSavedStringPreference(SharedPrefClass.USER_ID), this, JobStatusActivity.this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //startActivity(new Intent(JobStatusActivity.this,WelcomeShovelerActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
    }

    @Override
    public void GetRequestorJobCallbackSuccess(String success) {
        mProgressDilog.dismiss();
        try {
            JSONObject jsonObject = new JSONObject(success);
            if (jsonObject.getString("status").equals("true")) {
                JSONArray array = jsonObject.getJSONArray("items");
                jobModels.clear();
                RequestorJobModel jobModel = null;
                for (int i = 0; i < array.length(); i++) {
                    JSONObject jobObject = array.getJSONObject(i);
                    jobModel = new RequestorJobModel();
                    jobModel.setJid(jobObject.getString("jid"));
                    jobModel.setLoclat(jobObject.getString("loclat"));
                    jobModel.setLoclng(jobObject.getString("loclng"));
                    jobModel.setAddress(jobObject.getString("address"));
                    jobModel.setJobtype(jobObject.getString("jobtype"));
                    jobModel.setJid(jobObject.getString("jid"));
                    jobModel.setJobpic(jobObject.getString("jobpic"));
                    jobModel.setDescp(jobObject.getString("descp"));
                    jobModel.setModel(jobObject.getString("model"));
                    jobModel.setColor(jobObject.getString("color"));
                    jobModel.setLicplateno(jobObject.getString("licplateno"));
                    jobModel.setLicplatestate(jobObject.getString("licplatestate"));
                    jobModel.setPrice(jobObject.getString("price"));
                    jobModel.setJobdt(jobObject.getString("jobdt"));
                    jobModel.setAjuid(jobObject.getString("ajuid"));
                    jobModel.setStatus(jobObject.getString("status"));
                    jobModel.setPic1(jobObject.getString("pic1"));
                    jobModel.setPic2(jobObject.getString("pic2"));
                    jobModel.setPic3(jobObject.getString("pic3"));
                    jobModel.setSizeofwork(jobObject.getString("sizeofwork"));
                    jobModel.setScomments(jobObject.getString("scomments"));
                    jobModel.esptime = jobObject.getString("esttime");
                    //jobModel.setDistance(jobObject.getString("distance"));

                    jobModels.add(jobModel);
                }
                isRequestor = true;
                adapter = new RequestListAdapter(JobStatusActivity.this, jobModels);
                mRequestListView.setAdapter(adapter);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void GetRequestorJobCallbackError(String error) {
        mProgressDilog.dismiss();
        Constants.showAlert(JobStatusActivity.this, error);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.request) {
            Log.v("click", "request");
            startActivity(new Intent(JobStatusActivity.this, FirstStepActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
        } else if (id == R.id.job_status) {
            Log.v("click", "job_status");
            startActivity(new Intent(JobStatusActivity.this, JobStatusActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
        } else if (id == R.id.aval_jobs) {
            Log.v("click", "aval_jobs");
            Constants.USER_TYPE = "Shovler";
            if (prefClass.isUserLogin()) {
                if (prefClass.getSavedStringPreference(SharedPrefClass.UTYPE).equals("Shovler"))
                startActivity(new Intent(JobStatusActivity.this, AvailableJobActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
        } else {
            startActivity(new Intent(JobStatusActivity.this, HomeActivity.class));
        }
        } else if (id == R.id.configuration) {
            Log.v("click", "configuration");
            startActivity(new Intent(JobStatusActivity.this, SettingActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
        } else if (id == R.id.promocode) {
            Log.v("click", "promocode");
            if (prefClass.isUserLogin()){
                    startActivity(new Intent(JobStatusActivity.this, SendPromoCodeActivity.class));
            }else {
                Constants.showAlert(JobStatusActivity.this, getResources().getString(R.string.please_login));
            }

    }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.END);

        return true;
    }

    @Override
    public void GetShovelerJobCallbackSuccess(String success) {
        mProgressDilog.dismiss();
        try {
            JSONObject jsonObject = new JSONObject(success);
            if (jsonObject.getString("status").equals("true")) {
                JSONArray array = jsonObject.getJSONArray("items");
                jobModels.clear();
                RequestorJobModel jobModel = null;
                for (int i = 0; i < array.length(); i++) {
                    JSONObject jobObject = array.getJSONObject(i);
                    jobModel = new RequestorJobModel();
                    jobModel.setJid(jobObject.getString("jid"));
                    jobModel.setLoclat(jobObject.getString("loclat"));
                    jobModel.setLoclng(jobObject.getString("loclng"));
                    jobModel.setAddress(jobObject.getString("address"));
                    jobModel.setJobtype(jobObject.getString("jobtype"));
                    jobModel.setJid(jobObject.getString("jid"));
                    jobModel.setJobpic(jobObject.getString("jobpic"));
                    jobModel.setDescp(jobObject.getString("descp"));
                    jobModel.setModel(jobObject.getString("model"));
                    jobModel.setColor(jobObject.getString("color"));
                    jobModel.setLicplateno(jobObject.getString("licplateno"));
                    jobModel.setLicplatestate(jobObject.getString("licplatestate"));
                    jobModel.setPrice(jobObject.getString("finalprice"));
                    jobModel.setJobdt(jobObject.getString("jobdt"));
                    jobModel.setStatus(jobObject.getString("status"));
                    jobModel.setSizeofwork(jobObject.getString("sizeofwork"));
                    jobModel.esptime = jobObject.getString("esttime");
                    jobModels.add(jobModel);
                }

                isRequestor = false;
                adapter = new RequestListAdapter(JobStatusActivity.this, jobModels);
                mRequestListView.setAdapter(adapter);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void GetShovelerJobCallbackError(String error) {
        mProgressDilog.dismiss();
        Constants.showAlert(JobStatusActivity.this, error);
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
        }else {
            return true;
        }
        return false;
    }

    private long lastPressedTime;
    private static final int PERIOD = 2000;
}
