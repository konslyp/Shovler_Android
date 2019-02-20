package com.app.shovelerapp.activity;

import android.graphics.Typeface;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.shovelerapp.R;
import com.app.shovelerapp.utils.Constants;

/**
 * Created by supriya.n on 01-07-2016.
 */
public class WithdrawActivity extends AppCompatActivity {
    private TextView withdrawTextView,destiTextView;
    private Button mSendRequest;
    private ImageView mSubLogo;
    private TextView mTitle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_withdraw_money);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mSubLogo= (ImageView) toolbar.findViewById(R.id.logo_image);
        mTitle= (TextView) toolbar.findViewById(R.id.title);

        mSubLogo.setImageResource(R.drawable.ic_action_request);
        mTitle.setText("SETTING");

        withdrawTextView= (TextView) findViewById(R.id.withdraw_id);
        destiTextView= (TextView) findViewById(R.id.destination_id);
        mSendRequest= (Button) findViewById(R.id.send_request);

        setFont();
    }

    private void setFont() {
        Typeface tfRegular= Constants.setRegularLatoFont(WithdrawActivity.this);
        Typeface tfLight=Constants.setLightLatoFont(WithdrawActivity.this);
        Typeface tfThin=Constants.setThinLatoFont(WithdrawActivity.this);
        Typeface tfMedium=Constants.setMediumLatoFont(WithdrawActivity.this);
        withdrawTextView.setTypeface(tfRegular);
        destiTextView.setTypeface(tfRegular);
        mSendRequest.setTypeface(tfRegular);
    }
}
