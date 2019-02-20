package com.app.shovelerapp.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.shovelerapp.R;
import com.app.shovelerapp.ShovlerApplication;
import com.app.shovelerapp.adapter.MyFilterableAdapter;
import com.app.shovelerapp.callback.GetAddressFromLatLongCallback;
import com.app.shovelerapp.callback.GetLatLongFromAddressCallback;
import com.app.shovelerapp.callback.GetPlacesCallback;
import com.app.shovelerapp.doc.Globals;
import com.app.shovelerapp.model.JobDetails;
import com.app.shovelerapp.netutils.NetUtils;
import com.app.shovelerapp.service.FetchAddressIntentService;
import com.app.shovelerapp.service.GPSTracker;
import com.app.shovelerapp.service.ILoadService;
import com.app.shovelerapp.service.ServiceManager;
import com.app.shovelerapp.utils.Constants;
import com.app.shovelerapp.utils.SharedPrefClass;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by supriya.n on 25-07-2016.
 */
public class FirstStepActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener, GetPlacesCallback, NavigationView.OnNavigationItemSelectedListener,
        GetLatLongFromAddressCallback,ILoadService,
        GetAddressFromLatLongCallback {

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private Button mCarButton, mHomeButton, mBusinessButton;
    private Button mContinue;
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static String TAG = "MAP LOCATION";
    private ImageView mCurrentLocImage, mCloseImageView;
    private LatLng latLng;
    private Boolean drivewayABoolean = false, carABoolean = false, sidewalkBoolean = false;
    private ProgressDialog mProgressDilog;
    // private ArrayAdapter<String> adapter;
    private MyFilterableAdapter adapter;
    // private AddressResultReceiver resultReceiver;
    private File file;
    //Pojo class
    public static JobDetails details = new JobDetails();
    private ArrayList<String> placesList = new ArrayList<String>();

    private JobDetails.JobDetailsEntity jobdetailsEntity = new JobDetails.JobDetailsEntity();
    private JobDetails.JobDetailsEntity.CarEntity carEntity = new JobDetails.JobDetailsEntity.CarEntity();
    private JobDetails.JobDetailsEntity.HomeEntity homeEntity = new JobDetails.JobDetailsEntity.HomeEntity();
    private JobDetails.JobDetailsEntity.BusinessEntity businessEntity = new JobDetails.JobDetailsEntity.BusinessEntity();

    TextView mLocationMarkerText;
    private LatLng mCenterLatLong;
    private AutoCompleteTextView mAddressEditText;
    private String mAreaOutput;

    LocationManager manager;

    public int isFromAddress = 0;

    public boolean isCheckShovlerStatus = true;


    private TextView mSnowTitle, mLocatedAtTv;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
    private SupportMapFragment mapFragment;

    private ImageView mSubLogo, mDrawer;
    private TextView mTitle;
    private GPSTracker tracker;
    private Double lat, lng;

    private String currentAddress = null;
    private DrawerLayout drawer;
    NavigationView navigationView;
    SharedPrefClass prefClass;

    private boolean addressFromMap = false;
    private boolean manuallyEnteredAddress = true;

    public LinearLayout layoutShovlerStatus;
    public TextView txtShovlerStatus;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_request);
      /*  SupportMapFragment mapFragment = (SupportMapFragment) getFragmentManager()
                .findFragmentById(R.id.map);*/
        mapFragment = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(
                R.id.map));


        tracker = new GPSTracker(FirstStepActivity.this);
        lat = tracker.getLatitude();
        lng = tracker.getLongitude();
        // TODO: 1/13/2017 Commented Line
//        currentAddress = Constants.getCompleteAddressString(FirstStepActivity.this, lat, lng);
        NetUtils.getAddressSuggestions("", String.valueOf(lat), String.valueOf(lng), FirstStepActivity.this,
                FirstStepActivity.this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mSubLogo = (ImageView) toolbar.findViewById(R.id.logo_image);
        mTitle = (TextView) toolbar.findViewById(R.id.title);

        mSubLogo.setImageResource(R.drawable.ic_action_request);
        mTitle.setText("REQUEST");

       /* IntentFilter filter = new IntentFilter();
        filter.addAction(FirstStepActivity.AddressResultReceiver.PROCESS_RESPONSE);
        resultReceiver=new AddressResultReceiver();
        registerReceiver(resultReceiver,filter);*/

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("pranoti"));
        //Code for navigation drawer
        mDrawer = (ImageView) toolbar.findViewById(R.id.drawer);
        try {
            MapsInitializer.initialize(getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        mapFragment.getMapAsync(this);
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


        prefClass = new SharedPrefClass(FirstStepActivity.this);

        // navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (!prefClass.isUserLogin()) {
            navigationView.setVisibility(View.VISIBLE);
            mDrawer.setVisibility(View.GONE);
            navigationView.setVisibility(View.GONE);
        } else {
            mDrawer.setVisibility(View.VISIBLE);
            navigationView.setVisibility(View.VISIBLE);
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

        mapFragment = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map));

        layoutShovlerStatus = (LinearLayout) this.findViewById(R.id.layoutRequestShovlerStatus);
        txtShovlerStatus = (TextView) this.findViewById(R.id.txtRequestShovlerStatus);

        layoutShovlerStatus.setVisibility(View.GONE);
        //  mapFragment.getMapAsync(this);
        mAddressEditText = (AutoCompleteTextView) findViewById(R.id.address_e);
        mCarButton = (Button) findViewById(R.id.car);
        mHomeButton = (Button) findViewById(R.id.home1);
        mBusinessButton = (Button) findViewById(R.id.business);
        mContinue = (Button) findViewById(R.id.continue_btn);
        mSnowTitle = (TextView) findViewById(R.id.text);
        mLocatedAtTv = (TextView) findViewById(R.id.text2);
        mCloseImageView = (ImageView) findViewById(R.id.close_btn);
        mCurrentLocImage = (ImageView) findViewById(R.id.current_address_indicator);

       /* tracker = new GPSTracker(FirstStepActivity.this);
        lat = tracker.getLatitude();
        lng = tracker.getLongitude();*/


        setFont();


        // mResultReceiver = new AddressResultReceiver(new Handler());

        if (checkPlayServices()) {
            // If this check succeeds, proceed with normal processing.
            // Otherwise, prompt user to get valid Play Services APK.
            if (!Constants.isLocationEnabled(FirstStepActivity.this)) {
                // notify user
                AlertDialog.Builder dialog = new AlertDialog.Builder(FirstStepActivity.this);
                dialog.setMessage("Location not enabled!");
                dialog.setPositiveButton("Open location settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(myIntent, 21);
                    }
                });
                dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        // TODO Auto-generated method stub

                    }
                });
                dialog.show();
            }
            buildGoogleApiClient();
        } else {
            Toast.makeText(FirstStepActivity.this, "Location not supported in this device", Toast.LENGTH_SHORT).show();
        }

        //Default car button is selected
        mCarButton.setBackground(getResources().getDrawable(R.drawable.brown_button));
        mCarButton.setTextColor(getResources().getColor(R.color.white));

        mHomeButton.setBackground(getResources().getDrawable(R.drawable.dash_rect));
        mHomeButton.setTextColor(getResources().getColor(R.color.brown));

        mBusinessButton.setBackground(getResources().getDrawable(R.drawable.dash_rect));
        mBusinessButton.setTextColor(getResources().getColor(R.color.brown));

        jobdetailsEntity.setCar(carEntity);
        jobdetailsEntity.setHome(null);
        jobdetailsEntity.setBusiness(null);

        details.setJobDetails(jobdetailsEntity);

        // Button click logic
        mCarButton.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {
                Log.v("click", "sidewalk");
                if (mCarButton.getBackground().getConstantState().equals(getResources().getDrawable(R.drawable.dash_rect).getConstantState())) {
                    mCarButton.setBackground(getResources().getDrawable(R.drawable.brown_button));
                    mCarButton.setTextColor(getResources().getColor(R.color.white));

                    mHomeButton.setBackground(getResources().getDrawable(R.drawable.dash_rect));
                    mHomeButton.setTextColor(getResources().getColor(R.color.brown));

                    mBusinessButton.setBackground(getResources().getDrawable(R.drawable.dash_rect));
                    mBusinessButton.setTextColor(getResources().getColor(R.color.brown));

                    jobdetailsEntity.setCar(carEntity);
                    jobdetailsEntity.setHome(null);
                    jobdetailsEntity.setBusiness(null);

                    details.setJobDetails(jobdetailsEntity);

                    Log.v("car details", "" + details);

                }
            }
        });

        mHomeButton.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {
                if (mHomeButton.getBackground().getConstantState().equals(getResources().getDrawable(R.drawable.dash_rect).getConstantState())) {
                    mHomeButton.setBackground(getResources().getDrawable(R.drawable.brown_button));
                    mHomeButton.setTextColor(getResources().getColor(R.color.white));

                    mCarButton.setBackground(getResources().getDrawable(R.drawable.dash_rect));
                    mCarButton.setTextColor(getResources().getColor(R.color.brown));

                    mBusinessButton.setBackground(getResources().getDrawable(R.drawable.dash_rect));
                    mBusinessButton.setTextColor(getResources().getColor(R.color.brown));

                    jobdetailsEntity.setCar(null);
                    jobdetailsEntity.setHome(homeEntity);
                    jobdetailsEntity.setBusiness(null);


                    details.setJobDetails(jobdetailsEntity);

                    Log.v("home details", "" + details);
                }
            }
        });

        mBusinessButton.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)

            @Override
            public void onClick(View v) {
                if (mBusinessButton.getBackground().getConstantState().equals(getResources().getDrawable(R.drawable.dash_rect).getConstantState())) {
                    mBusinessButton.setBackground(getResources().getDrawable(R.drawable.brown_button));
                    mBusinessButton.setTextColor(getResources().getColor(R.color.white));

                    mCarButton.setBackground(getResources().getDrawable(R.drawable.dash_rect));
                    mCarButton.setTextColor(getResources().getColor(R.color.brown));

                    mHomeButton.setBackground(getResources().getDrawable(R.drawable.dash_rect));
                    mHomeButton.setTextColor(getResources().getColor(R.color.brown));

                    jobdetailsEntity.setCar(null);
                    jobdetailsEntity.setHome(null);
                    jobdetailsEntity.setBusiness(businessEntity);

                    details.setJobDetails(jobdetailsEntity);

                    Log.v("business details", "" + details);
                }
            }
        });


        mAddressEditText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                currentAddress = placesList.get(position);
                isFromAddress = 1;
                NetUtils.CallGetLatLongFromAddressApi(placesList.get(position), FirstStepActivity.this, FirstStepActivity.this);


//                LatLng latLng = Constants.getLocationFromAddress(FirstStepActivity.this, placesList.get(position));
//                if (latLng != null) {
//                    lat = latLng.latitude;
//                    lng = latLng.longitude;
//                    callLoadMap(latLng);
//                } else {
//                    mAddressEditText.setText("");
//                    Toast.makeText(FirstStepActivity.this, "Unable to get Latitude and Longitude for this address location.", Toast.LENGTH_SHORT).show();
//                }


            }
        });

//        if (currentAddress == null || currentAddress.equals(null) || currentAddress.equals("null")) {
//            mAddressEditText.setHint("Can't get address");
//            manuallyEnteredAddress = true;
//        } else {
//            manuallyEnteredAddress = false;
//            mAddressEditText.setText(currentAddress);
//        }

        mAddressEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, final int start, final int before,
                                      final int count) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.e(TAG, "" + start + ", " + before + ", " + count);

                        if (count == 1) {
                            addressFromMap = false;
                            manuallyEnteredAddress = true;
                        }
                    }
                });
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO: 12/10/2016 Here code comes from both Map and edittext
                ShovlerApplication.getInstance().getRequestQueue().cancelAll("GetAddress");
                if (s.length() == 1) {
                    addressFromMap = false;
                    manuallyEnteredAddress = true;
                }
                if (!addressFromMap) {
                    callPlacesApi(s.toString().replace(" ", "+"));
                }
                // getSuggestonAsyncTask.execute(s.toString());
            }
        });


        mContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!manuallyEnteredAddress) {
                    jobdetailsEntity.setAddress(mAddressEditText.getText().toString());
                    jobdetailsEntity.setLat(String.valueOf(lat));
                    jobdetailsEntity.setLng(String.valueOf(lng));
                    details.setJobDetails(jobdetailsEntity);

                    if (mAddressEditText.getText().toString().equals("")) {
                        Constants.showAlert(FirstStepActivity.this, "Please enter address");
                    } else {
                        Intent intent = new Intent(FirstStepActivity.this, SecondStepActivity.class);
                        startActivity(intent);
                    }
                } else {
                    Constants.showAlert(FirstStepActivity.this, "Select address from dropdown address list or using map to ensure the address is valid.");
                }

            }
        });

        mCurrentLocImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //mAddressEditText.setText("");
                tracker = new GPSTracker(FirstStepActivity.this);
                LatLng latLng = new LatLng(tracker.getLatitude(), tracker.getLongitude());
                lat = latLng.latitude;
                lng = latLng.longitude;
                callLoadMap(latLng);
            }
        });

        mCloseImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAddressEditText.setText("");
            }
        });


        if (ActivityCompat.checkSelfPermission(FirstStepActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(FirstStepActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

        }

        checkShovlerStart();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
//        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.

    }
    public void checkShovlerStart()
    {
        if (!prefClass.isUserLogin())
        {
            layoutShovlerStatus.setVisibility(View.GONE);
        }
        else {
            new Thread() {
                public void run() {
                    while (isCheckShovlerStatus) {
                        try {
                            ServiceManager.onCheckNearbyShovler(lat, lng, FirstStepActivity.this);
                            sleep(5000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }.start();
        }
    }
    private void callLoadMap(LatLng latLng) {
        if (mMap != null) {
            mMap.getUiSettings().setZoomControlsEnabled(false);


            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(latLng).zoom(14).tilt(70).build();

            //mMap.setMyLocationEnabled(true);
            //mMap.getUiSettings().setMyLocationButtonEnabled(true);
            mMap.animateCamera(CameraUpdateFactory
                    .newCameraPosition(cameraPosition));

            //mLocationMarkerText.setText("Lat : " + location.getLatitude() + "," + "Long : " + location.getLongitude());
            //startIntentService(location);
        } else {
            Toast.makeText(FirstStepActivity.this,
                    "Sorry! unable to create maps", Toast.LENGTH_SHORT)
                    .show();
        }
    }

    private void callPlacesApi(String s) {
        NetUtils.CallGetPlacesApi(s, lat + "," + lng, this, FirstStepActivity.this);
    }

    private void setFont() {
        Typeface tfRegular = Constants.setRegularLatoFont(FirstStepActivity.this);
        Typeface tfLight = Constants.setLightLatoFont(FirstStepActivity.this);
        Typeface tfThin = Constants.setThinLatoFont(FirstStepActivity.this);

        mHomeButton.setTypeface(tfRegular);
        mBusinessButton.setTypeface(tfRegular);
        mCarButton.setTypeface(tfRegular);

        mSnowTitle.setTypeface(tfRegular);
        mLocatedAtTv.setTypeface(tfRegular);

        mAddressEditText.setTypeface(tfRegular);

        mContinue.setTypeface(tfRegular);
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(FirstStepActivity.this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, FirstStepActivity.this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.v("call", "fragment");
            }
            return false;
        }
        return true;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng latLng = new LatLng(lat, lng);
        callLoadMap(latLng);

        if (mMap != null) {
            mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
                @Override
                public void onCameraChange(CameraPosition cameraPosition) {
                    Location location = new Location("GPS");
                    location.setLatitude(cameraPosition.target.latitude);
                    location.setLongitude(cameraPosition.target.longitude);
                    changeMap(location);


                    if (isFromAddress == 0) {
                        currentAddress = Constants.getCompleteAddressString(FirstStepActivity.this, cameraPosition.target.latitude, cameraPosition.target.longitude);
                        mAddressEditText.setText(currentAddress);
                    }
                    isFromAddress = 0;
                }
            });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    protected synchronized void buildGoogleApiClient() {
       /* mGoogleApiClient = new GoogleApiClient.Builder(FirstStepActivity.this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();*/
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {


    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }

    private void changeMap(Location location) {

        Log.d(TAG, "Reaching map" + mMap);

        //mAddressEditText.addTextChangedListener(null);
        if (ActivityCompat.checkSelfPermission(FirstStepActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(FirstStepActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }

        // check if map is created successfully or not
        if (mMap != null)
            startIntentService(location);

    }


    @Override
    public void onLocationChanged(Location location) {
        try {
            if (location != null)
                changeMap(location);

            LocationServices.FusedLocationApi.removeLocationUpdates(
                    mGoogleApiClient, this);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override

    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    public void removeDriveway() {
        Log.v("fragment", "removeDriveway");
        drivewayABoolean = true;

    }


    public void removeCar() {
        Log.v("fragment", "removeCar");
        carABoolean = true;

    }


    public void removeSidewalk() {
        sidewalkBoolean = true;
        Log.v("fragment", "removeSidewalk");

    }


    @Override
    public void onResume() {
        Log.v("onResume", "onResume");
        try {
            if (mAddressEditText.getText().toString().equals("")) {
                String provider = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
                if (provider != null) {
                    Log.v(TAG, " Location providers: " + provider);
                    tracker = new GPSTracker(FirstStepActivity.this);
                    LatLng latLng = new LatLng(tracker.getLatitude(), tracker.getLongitude());
                    lat = latLng.latitude;
                    lng = latLng.longitude;
                    if (mMap != null)
                        callLoadMap(latLng);
//                    currentAddress = Constants.getCompleteAddressString(FirstStepActivity.this, lat, lng);
                    NetUtils.getAddressSuggestions("", String.valueOf(lat), String.valueOf(lng), FirstStepActivity.this,
                            FirstStepActivity.this);
                    //Start searching for location and update the location text when update available.
// Do whatever you want

                } else {
                    //Users did not switch on the GPS
                    tracker = new GPSTracker(FirstStepActivity.this);
                    LatLng latLng = new LatLng(tracker.getLatitude(), tracker.getLongitude());
                    lat = latLng.latitude;
                    lng = latLng.longitude;
                    callLoadMap(latLng);
//                    currentAddress = Constants.getCompleteAddressString(FirstStepActivity.this, lat, lng);
                    NetUtils.getAddressSuggestions("", String.valueOf(lat), String.valueOf(lng), FirstStepActivity.this,
                            FirstStepActivity.this);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onResume();
    }


    @Override
    public void onPause() {
        super.onPause();
        Log.v("onPause", "onPause");
    }

    @Override
    public void GetPlacesCallbackSuccess(String success) {
        Log.v("success", "" + success);
        placesList.clear();
        try {
            JSONObject jsonObject = new JSONObject(success);
            if (jsonObject.getString("status").equals("OK")) {
                JSONArray jsonArray = jsonObject.getJSONArray("predictions");
                for (int i = 0; i < jsonArray.length(); i++) {
                    // flag gotFromMap = true
                    placesList.add(jsonArray.getJSONObject(i).getString("description"));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.v("placesList", "" + placesList);
//        if (!addressFromMap) {
        runOnUiThread(new Runnable() {
            public void run() {
                //set adapter
                // adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.items, placesList);
                adapter = new MyFilterableAdapter(FirstStepActivity.this, placesList);
                mAddressEditText.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
        });

//        }
    }

    @Override
    public void GetPlacesCallbackError(String error) {
        Log.v("error", "" + error);
    }

    protected void startIntentService(Location mLocation) {
        // Create an intent for passing to the intent service responsible for fetching the address.
        Log.v("call", "" + "startIntentService");

        lat = mLocation.getLatitude();
        lng = mLocation.getLongitude();

        NetUtils.getAddressSuggestions("", String.valueOf(lat), String.valueOf(lng),
                FirstStepActivity.this, FirstStepActivity.this);

//        Intent intent = new Intent(FirstStepActivity.this, FetchAddressIntentService.class);
//        intent.putExtra(Constants.LOCATION_DATA_EXTRA, mLocation);
//        startService(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isCheckShovlerStatus = false;
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (!prefClass.isUserLogin())
            startActivity(new Intent(FirstStepActivity.this, WelcomeShovelerActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
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
            startActivity(new Intent(FirstStepActivity.this,
                    JobStatusActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
        } else if (id == R.id.aval_jobs) {
            Log.v("click", "aval_jobs");
            startActivity(new Intent(FirstStepActivity.this,
                    AvailableJobActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
        } else if (id == R.id.configuration) {
            Log.v("click", "configuration");
            startActivity(new Intent(FirstStepActivity.this,
                    SettingActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
        } else if (id == R.id.promocode) {
            Log.v("click", "promocode");
            startActivity(new Intent(FirstStepActivity.this, SendPromoCodeActivity.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.END);

        return true;
    }


    /*   public class AddressResultReceiver extends BroadcastReceiver
       {

           public static final String PROCESS_RESPONSE = "RECEIVE_ADDRESS";

           @Override
           public void onReceive(Context context, Intent intent) {
               String address=intent.getStringExtra("address");

               mAddressEditText.setText(address);
           }
       }*/
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            String address = intent.getStringExtra("address");
            Log.e("receiver", "Got message: " + address);
            if (address == null || address.equals(null) || address.equals("null")) {
                mAddressEditText.setHint("Can't get address");
            } else {// flag gotFromMap = true
                addressFromMap = true;
                manuallyEnteredAddress = false;
                mAddressEditText.setText(address);
            }
        }
    };

   /* @Override
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
        return true;
    }

    private long lastPressedTime;
    private static final int PERIOD = 2000;*/


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 21) {
            try {
                if (mAddressEditText.getText().toString().equals("")) {
                    String provider = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
                    if (provider != null) {
                        Log.v(TAG, " Location providers: " + provider);
                        tracker = new GPSTracker(FirstStepActivity.this);
                        LatLng latLng = new LatLng(tracker.getLatitude(), tracker.getLongitude());
                        lat = latLng.latitude;
                        lng = latLng.longitude;
                        callLoadMap(latLng);
//                        currentAddress = Constants.getCompleteAddressString(FirstStepActivity.this, lat, lng);
                        NetUtils.getAddressSuggestions("", String.valueOf(lat), String.valueOf(lng), FirstStepActivity.this,
                                FirstStepActivity.this);
                        //Start searching for location and update the location text when update available.
// Do whatever you want

                    } else {
                        //Users did not switch on the GPS
                        tracker = new GPSTracker(FirstStepActivity.this);
                        LatLng latLng = new LatLng(tracker.getLatitude(), tracker.getLongitude());
                        lat = latLng.latitude;
                        lng = latLng.longitude;
                        callLoadMap(latLng);
//                        currentAddress = Constants.getCompleteAddressString(FirstStepActivity.this, lat, lng);
                        NetUtils.getAddressSuggestions("", String.valueOf(lat), String.valueOf(lng), FirstStepActivity.this,
                                FirstStepActivity.this);

                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void GetLatLongFromAddressCallbackSuccess(String success) {
        Log.e("success", "" + success);

        latLng = null;
        double tempLat = 0;
        double tempLng = 0;

        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(success);
            if (jsonObject != null && jsonObject.has("results") && jsonObject.has("status")) {
                String status = jsonObject.getString("status");
                if (status.equals("OK")) {
                    JSONArray jarray = jsonObject.getJSONArray("results");
                    if (jarray != null && jarray.length() > 0) {
                        JSONObject json = jarray.getJSONObject(0);
                        if (json != null && json.has("geometry")) {
                            JSONObject loca = json.getJSONObject("geometry");
                            if (loca != null && loca.has("location")) {
                                JSONObject loc = loca.getJSONObject("location");
                                if (loc.has("lat")) {
                                    tempLat += loc.getDouble("lat");
                                }
                                if (loc.has("lng")) {
                                    tempLng += loc.getDouble("lng");
                                }

                                latLng = new LatLng(tempLat, tempLng);
                            }
                        }
                    }
                } else {
                    latLng = null;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (latLng != null) {
            lat = latLng.latitude;
            lng = latLng.longitude;
            callLoadMap(latLng);
        } else {
            mAddressEditText.setText("");
            Toast.makeText(FirstStepActivity.this, "Unable to get Latitude and Longitude for this address location.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void GetLatLongFromAddressCallbackError(String error) {
        mAddressEditText.setText("");
        Toast.makeText(FirstStepActivity.this,
                "Unable to get Latitude and Longitude for this address location.",
                Toast.LENGTH_SHORT).show();
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

//                                if (currentAddress == null || currentAddress.equals(null) || currentAddress.equals("null")) {
//                                    mAddressEditText.setHint("Can't get address");
//                                    manuallyEnteredAddress = true;
//                                } else {
//                                    manuallyEnteredAddress = false;
//                                    mAddressEditText.setText(currentAddress);
//                                }

                                if (currentAddress == null || currentAddress.equals(null) || currentAddress.equals("null")) {
                                    mAddressEditText.setHint("Can't get address");
                                    manuallyEnteredAddress = true;
                                } else {// flag gotFromMap = true
                                    addressFromMap = true;
                                    manuallyEnteredAddress = false;
                                    //mAddressEditText.setText(currentAddress);
                                }
                            }

                            if (resultJsonObject.has("address_components")) {
                                JSONArray address_componentsJsonArray = resultJsonObject.getJSONArray("address_components");
                                int forZipcodeSize = address_componentsJsonArray.length();
                                if (forZipcodeSize > 0) {
                                    JSONObject zipCodeJsonObject =
                                            address_componentsJsonArray.getJSONObject(forZipcodeSize - 1);

                                    if (zipCodeJsonObject.has("types")) {
                                        JSONArray typesJsonArray = zipCodeJsonObject.getJSONArray("types");
                                        int postalCodeSize = typesJsonArray.length();
                                        if (postalCodeSize > 0) {
                                            String postalType = typesJsonArray.getString(0);
                                            if (postalType.equalsIgnoreCase("postal_code")) {
                                                if (zipCodeJsonObject.has("long_name")) {
                                                    Constants.Postal_code = zipCodeJsonObject.getString("long_name");
                                                }
                                            }
                                        }
                                    }
                                }
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

    @Override
    public void onResponse(int code) {
        switch (code)
        {
            case 1:
                if (Globals.g_availableShovler > 0)
                {
                    FirstStepActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            layoutShovlerStatus.setVisibility(View.VISIBLE);
                            layoutShovlerStatus.setBackgroundColor(0xff8CC63E);
                            txtShovlerStatus.setText("Shovelers are currently available in your area");
                            txtShovlerStatus.setTextColor(0xffffffff);
                        }
                    });

                }
                else {
                    FirstStepActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            layoutShovlerStatus.setVisibility(View.GONE);
                        }
                    });

                }
                break;
        }
    }

    @Override
    public void onResponse(int code, int index) {

    }
}


