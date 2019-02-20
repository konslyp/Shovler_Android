package com.app.shovelerapp.activity;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.shovelerapp.R;
import com.app.shovelerapp.adapter.AvailableJobAdapter;
import com.app.shovelerapp.callback.AvailableJobCallback;
import com.app.shovelerapp.callback.UpdateLatLngCallback;
import com.app.shovelerapp.doc.Globals;
import com.app.shovelerapp.model.RequestorJobModel;
import com.app.shovelerapp.netutils.NetUtils;
import com.app.shovelerapp.service.GPSTracker;
import com.app.shovelerapp.service.ILoadService;
import com.app.shovelerapp.service.ServiceManager;
import com.app.shovelerapp.utils.Constants;
import com.app.shovelerapp.utils.SharedPrefClass;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.ui.IconGenerator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by supriya.n on 10-06-2016.
 */
public class AvailableJobActivity extends AppCompatActivity implements AvailableJobCallback,OnMapReadyCallback,ILoadService,
        NavigationView.OnNavigationItemSelectedListener, UpdateLatLngCallback, SwipeRefreshLayout.OnRefreshListener,View.OnClickListener {
    private ListView mAvalJobsListView;
    private TextView mPrice, mDistance, mJobType, AlertTextView;
    private AvailableJobAdapter adapter;
    private ImageView mSubLogo;
    private TextView mTitle;
    private ProgressDialog mProgressDialog;

    public TextView txtTabList;
    public TextView txtTabMap;
    public TextView txtJobStatus;

    private SharedPrefClass prefClass;

    private DrawerLayout drawer;
    NavigationView navigationView;
    private ImageView mDrawer;
    public static ArrayList<RequestorJobModel> models = new ArrayList<RequestorJobModel>();

    String mUserId, mFilter = "0", mAd = "0", mDistansAd = "0", mPriceAd = "0";
    private int jobFlag = 0;
    private CharSequence[] jobTypeList = {"ALL", "CAR", "HOME", "BUSINESS"};
    Double lat = 0.0, lan = 0.0;
    GPSTracker tracker;
    public int currenTab = 0;
    public LinearLayout layoutList;
    public LinearLayout layoutMap;
    public SupportMapFragment mapFragment;
    public GoogleMap mMap;
    List<Marker> lstMarkers = new ArrayList<>();
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    //  private GoogleApiClient client;

    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_ava_jobs);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        init();

        tracker = new GPSTracker(AvailableJobActivity.this);

        //Code for navigation drawer

        mDrawer = (ImageView) toolbar.findViewById(R.id.drawer);

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


        prefClass = new SharedPrefClass(AvailableJobActivity.this);

        // navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (!prefClass.isUserLogin()) {
            navigationView.setVisibility(View.VISIBLE);
            mDrawer.setVisibility(View.GONE);
        } else {
            mDrawer.setVisibility(View.VISIBLE);
            if (prefClass.getSavedStringPreference(SharedPrefClass.UTYPE).equals("Shovler")) {
                navigationView.getMenu().clear();
                navigationView.inflateMenu(R.menu.activity_welcome_shoveler_drawer);
            } else {
                navigationView.getMenu().clear();
                navigationView.inflateMenu(R.menu.activity_welcome_requestor_drawer);
            }

        }


        drawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.openDrawer(GravityCompat.END);
            }
        });
        navigationView.setNavigationItemSelectedListener(this);

        mSubLogo = (ImageView) toolbar.findViewById(R.id.logo_image);
        mTitle = (TextView) toolbar.findViewById(R.id.title);

        mSubLogo.setImageResource(R.drawable.shoveler);
        mTitle.setText("AVAILABLE JOBS");

        txtTabMap = (TextView) this.findViewById(R.id.txtShovlerJobMap);
        txtTabList = (TextView) this.findViewById(R.id.txtShovlerJobList);
        txtJobStatus = (TextView) this.findViewById(R.id.txtShovlerJobStatus);
        layoutList = (LinearLayout) this.findViewById(R.id.layout_joblisttab);
        layoutMap = (LinearLayout) this.findViewById(R.id.layout_jobmaptab);

        mapFragment = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(
                R.id.mapAvailableJob));
        txtTabMap.setOnClickListener(this);
        txtTabList.setOnClickListener(this);

        if (prefClass.isUserLogin()) {
            txtJobStatus.setVisibility(View.VISIBLE);
            txtJobStatus.setBackgroundColor(0xff8CC63E);

            txtJobStatus.setOnClickListener(this);


            mUserId = prefClass.getSavedStringPreference(SharedPrefClass.USER_ID);

            ServiceManager.onReadDutyStatus(mUserId,this);
        }
        else
            txtJobStatus.setVisibility(View.GONE);




        callUpdateLatLng();

        //ServiceManager.onUpdateDutyStatus(mUserId,0,this);

        mPrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jobFlag = 0;
                mFilter = "2";
                if (mPriceAd.equals("0")) {
                    mPriceAd = "1";
                    mAd = "1";
                } else if (mPriceAd.equals("1")) {
                    mPriceAd = "2";
                    mAd = "2";
                } else if (mPriceAd.equals("2")) {
                    mPriceAd = "1";
                    mAd = "1";
                }

                callAvailableJobsApi();
            }
        });


        mJobType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //jobTypeList[] = new String[]{"1", "2", "3", "4"};
                InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                // onCreateDialogSingleChoice(modelList, "Select Model", mModelEdit).show();
                final AlertDialog.Builder chooseDrinkdialog = new AlertDialog.Builder(
                        AvailableJobActivity.this);
                View titleview = getLayoutInflater().inflate(R.layout.alert_title, null);
                TextView title1 = (TextView) titleview.findViewById(R.id.dialogtitle);
                title1.setText("Select Job Type");
                chooseDrinkdialog.setCustomTitle(titleview);


                chooseDrinkdialog.setSingleChoiceItems(jobTypeList, jobFlag,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int item) {
                                jobFlag = item;
                                switch (jobTypeList[item].toString()) {
                                    case "CAR":
                                        mFilter = "3";
                                        mAd = "0";
                                        callAvailableJobsApi();
                                        break;
                                    case "HOME":
                                        mFilter = "5";
                                        mAd = "0";
                                        callAvailableJobsApi();
                                        break;
                                    case "BUSINESS":
                                        mFilter = "4";
                                        mAd = "0";
                                        callAvailableJobsApi();
                                        break;
                                    case "ALL":
                                        mFilter = "0";
                                        mAd = "0";
                                        callAvailableJobsApi();
                                    default:
                                        mFilter = "0";
                                        mAd = "0";
                                }
                                dialog.dismiss();
                            }
                        });

                chooseDrinkdialog.show();
            }
        });

        mDistance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFilter = "1";
                jobFlag = 0;
                if (mDistansAd.equals("0")) {
                    mDistansAd = "1";
                    mAd = "1";
                } else if (mDistansAd.equals("1")) {
                    mDistansAd = "2";
                    mAd = "2";
                } else if (mDistansAd.equals("2")) {
                    mDistansAd = "1";
                    mAd = "1";
                }
                callAvailableJobsApi();

            }
        });


        adapter = new AvailableJobAdapter(AvailableJobActivity.this, models);
        mAvalJobsListView.setAdapter(adapter);
        setTab(0);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
//        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

        try {
            MapsInitializer.initialize(getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        mapFragment.getMapAsync(this);
    }
    public void initTab()
    {
        txtTabList.setBackgroundResource(R.drawable.drawable_left_stroke);
        txtTabMap.setBackgroundResource(R.drawable.drawable_right_stroke);
        txtTabList.setTextColor(0xffA28468);
        txtTabMap.setTextColor(0xffA28468);
    }
    private void callUpdateLatLng() {

        double tempLat = tracker.getLatitude();
        double tempLng = tracker.getLongitude();

//        String currentAddress = Constants.getCompleteAddressString(AvailableJobActivity.this, tempLat,
//                tempLng);

//        NetUtils.CallNewUpdateLatLng(mUserId, String.valueOf(tempLat), String.valueOf(tempLng),
//                currentAddress,this, this);
        NetUtils.CallUpdateLatLng(mUserId, String.valueOf(tempLat),
                String.valueOf(tempLng), this, AvailableJobActivity.this);
    }

    private void init() {
        mPrice = (TextView) findViewById(R.id.price);
        mDistance = (TextView) findViewById(R.id.distance);
        mJobType = (TextView) findViewById(R.id.job_type);
        AlertTextView = (TextView) findViewById(R.id.alert_msg);
        mAvalJobsListView = (ListView) findViewById(R.id.avail_jobs_list);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
    }

    private void callAvailableJobsApi() {
        mProgressDialog = ProgressDialog.show(AvailableJobActivity.this, "",
                getResources().getString(R.string.loading), true, false);
//        swipeRefreshLayout.setVisibility(View.VISIBLE);
        NetUtils.CallGetAvailableJob(mUserId, mFilter, mAd, String.valueOf(tracker.getLatitude()), String.valueOf(tracker.getLongitude()), this, AvailableJobActivity.this);
    }

    @Override
    public void AvailableJobCallbackSuccess(String success) {
        mProgressDialog.dismiss();
//        swipeRefreshLayout.setVisibility(View.GONE);
        // stopping swipe refresh
        swipeRefreshLayout.setRefreshing(false);
        try {

            JSONObject jsonObject = new JSONObject(success);
            models.clear();
            if (jsonObject.getString("status").equals("true")) {

                JSONArray array = jsonObject.getJSONArray("items");
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
                    jobModel.setAjuid(jobObject.getString("ajuid"));
                    jobModel.setStatus(jobObject.getString("status"));
                    jobModel.setDistance(jobObject.getString("distance"));
                    jobModel.setSizeofwork(jobObject.getString("sizeofwork"));
                    jobModel.setZipcode(jobObject.getString("zipcode"));
                    models.add(jobModel);
                }

                if (models.size() > 0) {
                    AlertTextView.setVisibility(View.GONE);
                    mAvalJobsListView.setVisibility(View.VISIBLE);
                    swipeRefreshLayout.setVisibility(View.VISIBLE);
                } else {
                    AlertTextView.setVisibility(View.VISIBLE);
                    mAvalJobsListView.setVisibility(View.GONE);
                    swipeRefreshLayout.setVisibility(View.GONE);
                }

                adapter.notifyDataSetChanged();
            } else {
                AlertTextView.setVisibility(View.VISIBLE);
                mAvalJobsListView.setVisibility(View.GONE);
                swipeRefreshLayout.setVisibility(View.GONE);

            }
            adapter.notifyDataSetChanged();
            if (mMap != null)
            {
                addMarkers();
            }

           /* if (mFilter.equals("2")){
                if (mPriceAd.equals("1")) {
                    mPriceAd = "2";
                    mAd = "2";
                } else if (mPriceAd.equals("2")) {
                    mPriceAd = "1";
                    mAd = "1";
                }
            }else if (mFilter.equals("1")){
                if (mDistansAd.equals("1")) {
                    mDistansAd = "2";
                    mAd = "2";
                } else if (mDistansAd.equals("2")) {
                    mDistansAd = "1";
                    mAd = "1";
                }
            }*/
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void addMarkers()
    {
        lstMarkers.clear();
        for (int i = 0; i <  models.size();i++)
        {
            final RequestorJobModel mModel = models.get(i);

            View localView = LayoutInflater.from(this).inflate(R.layout.marker_car, null);

            String title = "$" + String.valueOf((int)Double.parseDouble(mModel.getPrice()));

            if (mModel.getJobtype().toLowerCase().equals("car"))
            {
                if (mMap != null) {
                    ((TextView)localView.findViewById(R.id.txtMarkerName)).setText(title);
                    ((ImageView)localView.findViewById(R.id.imgMarkerIcon)).setImageResource(R.drawable.markcar);
                    IconGenerator generator = new IconGenerator(this);
                    generator.setBackground(null);
                    generator.setContentView(localView);
                    Bitmap icon = generator.makeIcon();

                    BitmapDescriptor icon1 = BitmapDescriptorFactory.fromBitmap(icon);

                    Marker mk = mMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(mModel.getLoclat()), Double.parseDouble(mModel.getLoclng()))).icon(icon1));
                    lstMarkers.add(mk);
                }
            }
            else if (mModel.getJobtype().toLowerCase().equals("home"))
            {

                ((TextView)localView.findViewById(R.id.txtMarkerName)).setText(title);
                ((ImageView)localView.findViewById(R.id.imgMarkerIcon)).setImageResource(R.drawable.markhome);
                IconGenerator generator = new IconGenerator(this);
                generator.setBackground(null);
                generator.setContentView(localView);
                Bitmap icon = generator.makeIcon();

                BitmapDescriptor icon1 = BitmapDescriptorFactory.fromBitmap(icon);


                Marker mk = mMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(mModel.getLoclat()), Double.parseDouble(mModel.getLoclng()))).icon(icon1));
                lstMarkers.add(mk);
            }
            else if (mModel.getJobtype().toLowerCase().equals("business"))
            {

                ((TextView)localView.findViewById(R.id.txtMarkerName)).setText(title);
                ((ImageView)localView.findViewById(R.id.imgMarkerIcon)).setImageResource(R.drawable.markstore);
                IconGenerator generator = new IconGenerator(this);
                generator.setBackground(null);
                generator.setContentView(localView);
                Bitmap icon = generator.makeIcon();

                BitmapDescriptor icon1 = BitmapDescriptorFactory.fromBitmap(icon);

                Marker mk = mMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(mModel.getLoclat()), Double.parseDouble(mModel.getLoclng()))).icon(icon1));
                lstMarkers.add(mk);
            }

        }
    }
    @Override
    public void AvailableJobCallbackError(String error) {
        mProgressDialog.dismiss();
//        swipeRefreshLayout.setVisibility(View.GONE);
        // stopping swipe refresh
        swipeRefreshLayout.setRefreshing(false);
        Constants.showAlert(AvailableJobActivity.this, error);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();


        if (id == R.id.request) {
            Log.v("click", "request");
            //startActivity(new Intent(WelcomeShovelerActivity.this, FirstStepActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            //startActivity(new Intent(FirstStepActivity.this, FirstStepActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
        } else if (id == R.id.job_status) {
            Log.v("click", "job_status");
            startActivity(new Intent(AvailableJobActivity.this, JobStatusActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
        } else if (id == R.id.aval_jobs) {
            Log.v("click", "aval_jobs");
            startActivity(new Intent(AvailableJobActivity.this, AvailableJobActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
        } else if (id == R.id.configuration) {
            Log.v("click", "configuration");
            startActivity(new Intent(AvailableJobActivity.this, SettingActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
        } else if (id == R.id.promocode) {
            Log.v("click", "promocode");
            startActivity(new Intent(AvailableJobActivity.this, SendPromoCodeActivity.class));
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
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == 111) {
            startActivityForResult(new Intent(AvailableJobActivity.this, AvailableJobActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP),111);
        }
    }
    private long lastPressedTime;
    private static final int PERIOD = 2000;

    @Override
    public void UpdateLatLngCallbackSuccess(String success) {

    }

    @Override
    public void UpdateLatLngCallbackError(String error) {

    }

    @Override
    public void onRefresh() {
        callAvailableJobsApi();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.txtShovlerJobList:
                setTab(0);
                break;
            case R.id.txtShovlerJobMap:
                setTab(1);
                break;
            case R.id.txtShovlerJobStatus:
                if (Globals.g_dutyStatus.equals("0"))
                {
                      if (!prefClass.isPhoneVerify())
                      {
                          Intent i = new Intent(AvailableJobActivity.this, PhoneVerifyActivity.class);
                          startActivity(i);
                      }
                      else
                      {
                          ServiceManager.onUpdateDutyStatus(mUserId,1,this);
                      }
                }
                else
                {
                    ServiceManager.onUpdateDutyStatus(mUserId,0,this);

                }
                break;
        }
    }

    public void setTab(int mode)
    {
        initTab();
        currenTab = mode;
        switch(mode)
        {
            case 0:
                txtTabList.setBackgroundResource(R.drawable.drawable_left_stroke_fill);
                txtTabList.setTextColor(0xffffffff);

                layoutList.setVisibility(View.GONE);
                layoutMap.setVisibility(View.VISIBLE);
                break;
            case 1:
                txtTabMap.setBackgroundResource(R.drawable.drawable_right_stroke_fill);
                txtTabMap.setTextColor(0xffffffff);

                layoutList.setVisibility(View.VISIBLE);
                layoutMap.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //LatLng latLng = new LatLng(lat, lng);
        //callLoadMap(latLng);

        if (mMap != null) {
            mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
                @Override
                public void onCameraChange(CameraPosition cameraPosition) {
                    Location location = new Location("GPS");
                    location.setLatitude(cameraPosition.target.latitude);
                    location.setLongitude(cameraPosition.target.longitude);
                    //changeMap(location);
                }
            });
            LatLng curPos = new LatLng(tracker.getLatitude(), tracker.getLongitude());
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(curPos,13));

            mMap.getUiSettings().setZoomGesturesEnabled(false);
            mMap.getUiSettings().setZoomControlsEnabled(false);


            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    for (int i = 0;i < lstMarkers.size();i++)
                    {
                        Marker mm = lstMarkers.get(i);
                        if (mm.getId().equals(marker.getId()))
                        {
                            final int k = i;
                            //RequestorJobModel jModel = models.get(i);
                            if (Globals.g_dutyStatus.equals("1")) {
                                AvailableJobActivity.this.startActivityForResult(new Intent(AvailableJobActivity.this, AvailableJobDetailsActivity.class).putExtra("position", k), 111);
                            }
                            else
                            {
                                Toast.makeText(AvailableJobActivity.this,"Please Go On Duty to accept a job.",Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                    return false;
                }
            });
        }
        //if (!Globals.g_dutyStatus.equals("0"))
        //    callAvailableJobsApi();
        callAvailableJobsApi();
    }

    @Override
    public void onResponse(int code) {
        switch (code)
        {
            case 1://Update Duty Status
                setDutyButton();
                break;
            case 2://Read Duty Status
                setDutyButton();
                break;
        }
    }

    @Override
    public void onResponse(int code, int index) {

    }

    public void setDutyButton()
    {
        if (Globals.g_dutyStatus.equals("0"))
        {
            txtJobStatus.setText("Go On Duty");
            txtJobStatus.setBackgroundColor(0xff8CC63E);

//            models.clear();
//            adapter.notifyDataSetChanged();
//            if (mMap != null)
//            {
//                mMap.clear();
//                addMarkers();
//            }
        }
        else
        {
            txtJobStatus.setText("Go Off Duty");
            txtJobStatus.setBackgroundColor(0xffE4655A);


        }
    }
}
