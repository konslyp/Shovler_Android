package com.app.shovelerapp.activity;


import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.shovelerapp.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;

/**
 * Created by supriya.n on 16-06-2016.
 */
public class JobCancelActivity extends AppCompatActivity {
    private Button mExploreJobButton,mGoToJobStatusButton;
    private ImageView mJobCancelPic;
    private TextView mJobPrice,mJobTitle;
    private String price,url,title;
    private ImageView mSubLogo;
    private TextView mTitle;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cancel_layout);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mSubLogo= (ImageView) toolbar.findViewById(R.id.logo_image);
        mTitle= (TextView) toolbar.findViewById(R.id.title);

        mSubLogo.setImageResource(R.drawable.shoveler);
        mTitle.setText("JOB CANCEL");

        mExploreJobButton= (Button) findViewById(R.id.explore_job_done_pic);
        mGoToJobStatusButton= (Button) findViewById(R.id.go_to_job_status);
        mJobCancelPic= (ImageView) findViewById(R.id.imageView2);
        mJobPrice= (TextView) findViewById(R.id.job_price);
        mJobTitle= (TextView) findViewById(R.id.job_title);

        url=getIntent().getStringExtra("pic");
        price=getIntent().getStringExtra("price");
        title=getIntent().getStringExtra("title");

        Glide.with(JobCancelActivity.this).load(url).asBitmap().centerCrop().into(new BitmapImageViewTarget(mJobCancelPic) {
            @Override
            protected void setResource(Bitmap resource) {
                RoundedBitmapDrawable circularBitmapDrawable =
                        RoundedBitmapDrawableFactory.create(getResources(), resource);
                circularBitmapDrawable.setCircular(true);
                mJobCancelPic.setImageDrawable(circularBitmapDrawable);
            }
        });

        mJobPrice.setText("$ "+price);
        mJobTitle.setText(title);

        mExploreJobButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(JobCancelActivity.this, AvailableJobActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });

        mGoToJobStatusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(JobCancelActivity.this, JobStatusActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });

    }
}
