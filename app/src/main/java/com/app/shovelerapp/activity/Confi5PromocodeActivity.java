package com.app.shovelerapp.activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.shovelerapp.R;
import com.app.shovelerapp.utils.Constants;
import com.app.shovelerapp.utils.SharedPrefClass;

/**
 * Created by supriya.n on 22-06-2016.
 */
public class Confi5PromocodeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private Button mAddPromo;
    private TextView mInviteTextView, mSendPromoTextView;
    private ImageView mSubLogo, mDrawer;
    private TextView mTitle;
    private DrawerLayout drawer;
    NavigationView navigationView;
    SharedPrefClass prefClass;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_promo_code);

        mAddPromo = (Button) findViewById(R.id.add_promocode);
        mInviteTextView = (TextView) findViewById(R.id.invite_text);
        mSendPromoTextView = (TextView) findViewById(R.id.send_promo);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mSubLogo = (ImageView) toolbar.findViewById(R.id.logo_image);
        mTitle = (TextView) toolbar.findViewById(R.id.title);

        mSubLogo.setImageResource(R.drawable.settings1);
        mTitle.setText("PROMOCODE");

        //Code for navigation drawer

        mDrawer = (ImageView) toolbar.findViewById(R.id.drawer);
        mDrawer.setVisibility(View.VISIBLE);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.setDrawerIndicatorEnabled(false);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        mDrawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.openDrawer(GravityCompat.END);
            }
        });

        prefClass = new SharedPrefClass(Confi5PromocodeActivity.this);

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

        mAddPromo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Confi5PromocodeActivity.this, AddPromoActivity.class));
            }
        });

        mSendPromoTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Confi5PromocodeActivity.this, SendPromoCodeActivity.class));
            }
        });

        setFont();

    }

    private void setFont() {
        Typeface tfRegular = Constants.setRegularLatoFont(Confi5PromocodeActivity.this);
        Typeface tfLight = Constants.setLightLatoFont(Confi5PromocodeActivity.this);
        Typeface tfThin = Constants.setThinLatoFont(Confi5PromocodeActivity.this);
        Typeface tfMedium = Constants.setMediumLatoFont(Confi5PromocodeActivity.this);

        mAddPromo.setTypeface(tfRegular);
        mInviteTextView.setTypeface(tfRegular);
        mSendPromoTextView.setTypeface(tfRegular);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //startActivity(new Intent(Confi5PromocodeActivity.this, WelcomeShovelerActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();


        if (id == R.id.request) {
            Log.v("click", "request");
            startActivity(new Intent(Confi5PromocodeActivity.this, FirstStepActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
            //startActivity(new Intent(Confi5PromocodeActivity.this, FirstStepActivity.class));
        } else if (id == R.id.job_status) {
            Log.v("click", "job_status");
            startActivity(new Intent(Confi5PromocodeActivity.this, JobStatusActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
        } else if (id == R.id.aval_jobs) {
            Log.v("click", "aval_jobs");
            startActivity(new Intent(Confi5PromocodeActivity.this, AvailableJobActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
        } else if (id == R.id.configuration) {
            Log.v("click", "configuration");
            startActivity(new Intent(Confi5PromocodeActivity.this, SettingActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
        } else if (id == R.id.promocode) {
            Log.v("click", "promocode");
           // startActivity(new Intent(Confi5PromocodeActivity.this, Confi5PromocodeActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.END);

        return true;
    }
}
