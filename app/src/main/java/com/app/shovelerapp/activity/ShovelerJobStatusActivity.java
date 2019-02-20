package com.app.shovelerapp.activity;


import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.app.shovelerapp.R;
import com.app.shovelerapp.adapter.JobListAdapter;

/**
 * Created by supriya.n on 13-06-2016.
 */
public class  ShovelerJobStatusActivity extends AppCompatActivity {
    private ListView mJobListView;
    private JobListAdapter adapter;
    private ImageView mSubLogo;
    private TextView mTitle;

    public static TextView mOkTv,mUndoTv;

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        setContentView(R.layout.fragment_job_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mSubLogo= (ImageView) toolbar.findViewById(R.id.logo_image);
        mTitle= (TextView) toolbar.findViewById(R.id.title);

        mSubLogo.setImageResource(R.drawable.ic_action_job_status);
        mTitle.setText("JOB STATUS");
        mJobListView = (ListView) findViewById(R.id.job_list);



        adapter = new JobListAdapter(ShovelerJobStatusActivity.this);
        mJobListView.setAdapter(adapter);
    }
}
