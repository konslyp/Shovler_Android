package com.app.shovelerapp.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.shovelerapp.R;
import com.app.shovelerapp.adapter.MyFilterableAdapter;
import com.app.shovelerapp.utils.Constants;
import com.app.shovelerapp.utils.SharedPrefClass;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.iid.FirebaseInstanceId;


import java.util.ArrayList;
import java.util.List;


public class WelcomeShovelerActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private ImageView mDrawerImageView;
    private ImageView mBecomeShovler, mRequestShovler;
    private ImageView mMainLogo, mSubLogo;
    private TextView mBecomeTextView, mRequestTextView;
    private TextView mTitle;
    NavigationView navigationView;//ShovelerNavigationView, RequestorNavigationView;
    String title;
    String tag;
    int drawable;
    private static final int PERMISSIONS_REQUEST = 100;
    private FirebaseAnalytics mFirebaseAnalytics;

    private SharedPrefClass prefClass;
    private DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_welcome_shoveler);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

//       Log.e("Refreshed", "Refreshed token: " +  FirebaseInstanceId.getInstance().getToken());

        mFirebaseAnalytics.setUserProperty("CurrentActivity", "Welcome Shovler Screen");


        init();

        prefClass = new SharedPrefClass(WelcomeShovelerActivity.this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int hasCorseLocation = checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION);
            int hasFineLocation = checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);


            List<String> permissions = new ArrayList<String>();
            if (hasCorseLocation != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
            }

            if (hasFineLocation != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
            }

            if (!permissions.isEmpty()) {
                requestPermissions(permissions.toArray(new String[permissions.size()]), PERMISSIONS_REQUEST);
            }
        }

        title = getString(R.string.app_name);
        tag = "";
        drawable = R.drawable.ic_action_request;

        mDrawerImageView = (ImageView) toolbar.findViewById(R.id.drawer);
        mMainLogo = (ImageView) toolbar.findViewById(R.id.main_logo);
        mSubLogo = (ImageView) toolbar.findViewById(R.id.logo_image);
        mTitle = (TextView) toolbar.findViewById(R.id.title);


        navigationView = (NavigationView) findViewById(R.id.nav_view);


        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);


        mBecomeShovler = (ImageView) findViewById(R.id.become_shoveler);
        mRequestShovler = (ImageView) findViewById(R.id.request_shovler);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.setDrawerIndicatorEnabled(false);
        toggle.syncState();

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
        mDrawerImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.openDrawer(GravityCompat.END);
            }
        });

        mBecomeShovler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Constants.USER_TYPE = "Shovler";
                Constants.LOGIN_FROM = "";
                if (prefClass.isUserLogin()) {
                    if (prefClass.getSavedStringPreference(SharedPrefClass.UTYPE).equals("Shovler"))
                        startActivity(new Intent(WelcomeShovelerActivity.this, AvailableJobActivity.class));
                } else {
                    startActivity(new Intent(WelcomeShovelerActivity.this, HomeActivity.class));
                }


            }
        });

        mRequestShovler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Constants.LOGIN_FROM = "";
                Constants.USER_TYPE = "Requester";
                startActivity(new Intent(WelcomeShovelerActivity.this, FirstStepActivity.class));

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode != PERMISSIONS_REQUEST) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                int hasCorseLocation = checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION);
                int hasFineLocation = checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);


                List<String> permissions1 = new ArrayList<String>();
                if (hasCorseLocation != PackageManager.PERMISSION_GRANTED) {
                    permissions1.add(Manifest.permission.CAMERA);
                }

                if (hasFineLocation != PackageManager.PERMISSION_GRANTED) {
                    permissions1.add(Manifest.permission.READ_EXTERNAL_STORAGE);
                }

                if (!permissions1.isEmpty()) {
                    requestPermissions(permissions1.toArray(new String[permissions1.size()]), PERMISSIONS_REQUEST);
                }
            }
        }
    }

    private void init() {
        mBecomeTextView = (TextView) findViewById(R.id.become_a_shovler);
        mRequestTextView = (TextView) findViewById(R.id.request_a_shovler);


        mBecomeTextView.setTypeface(Constants.setRegularLatoFont(WelcomeShovelerActivity.this));
        mRequestTextView.setTypeface(Constants.setRegularLatoFont(WelcomeShovelerActivity.this));
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.v("here", "Act onAct");
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.container);
        fragment.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.welcome_shoveler, menu);
        return false;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();


        if (id == R.id.request) {
            Log.v("click", "request");
            //startActivity(new Intent(WelcomeShovelerActivity.this, FirstStepActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            startActivity(new Intent(WelcomeShovelerActivity.this, FirstStepActivity.class));
        } else if (id == R.id.job_status) {
            Log.v("click", "job_status");
            startActivity(new Intent(WelcomeShovelerActivity.this, JobStatusActivity.class));
        } else if (id == R.id.aval_jobs) {
            Log.v("click", "aval_jobs");
            Constants.USER_TYPE = "Shovler";
            if (prefClass.isUserLogin()) {
                if (prefClass.getSavedStringPreference(SharedPrefClass.UTYPE).equals("Shovler"))
                    startActivity(new Intent(WelcomeShovelerActivity.this, AvailableJobActivity.class));
            } else {
                startActivity(new Intent(WelcomeShovelerActivity.this, LoginActivity.class));
            }
        } else if (id == R.id.configuration) {
            Log.v("click", "configuration");
            startActivity(new Intent(WelcomeShovelerActivity.this, SettingActivity.class));
        } else if (id == R.id.promocode) {
            Log.v("click", "promocode");
            startActivity(new Intent(WelcomeShovelerActivity.this, Confi5PromocodeActivity.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.END);
        return true;
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
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
        return false;
    }
    private long lastPressedTime;
    private static final int PERIOD = 2000;
}
