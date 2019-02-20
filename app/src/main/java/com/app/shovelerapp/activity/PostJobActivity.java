package com.app.shovelerapp.activity;


import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;

import com.app.shovelerapp.R;
import com.app.shovelerapp.utils.Constants;

/**
 * Created by supriya.n on 14-06-2016.
 */
public class PostJobActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private Button mGoJobStatus;
    private Toolbar toolbar;
    private TextView textToolHeader, mPostJobText, mPostJobDesc, mPostOtherJob;
    private ImageView mSubLogo;
    private TextView mTitle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_post_job);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mSubLogo = (ImageView) toolbar.findViewById(R.id.logo_image);
        mTitle = (TextView) toolbar.findViewById(R.id.title);


        mSubLogo.setImageResource(R.drawable.ic_action_request);
        mTitle.setText("REQUEST");


/*        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);*/


        mGoJobStatus = (Button) findViewById(R.id.go_job_status);
        mPostJobText = (TextView) findViewById(R.id.posted_job);
        mPostJobDesc = (TextView) findViewById(R.id.post_job_desc);
        mPostOtherJob = (TextView) findViewById(R.id.undo_tv);

        setFont();


        mGoJobStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PostJobActivity.this, JobStatusActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });
        mPostOtherJob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PostJobActivity.this, FirstStepActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });

    }

    private void setFont() {
        Typeface tfRegular = Constants.setRegularLatoFont(PostJobActivity.this);
        Typeface tfLight = Constants.setLightLatoFont(PostJobActivity.this);
        Typeface tfThin = Constants.setThinLatoFont(PostJobActivity.this);
        Typeface tfMedium = Constants.setMediumLatoFont(PostJobActivity.this);

        mPostJobText.setTypeface(tfRegular);
        mGoJobStatus.setTypeface(tfRegular);
        mPostJobDesc.setTypeface(tfRegular);


    }

    @Override
    public void onBackPressed() {

    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        return false;
    }
}
