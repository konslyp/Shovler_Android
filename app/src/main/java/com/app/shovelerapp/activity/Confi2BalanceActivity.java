package com.app.shovelerapp.activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.shovelerapp.R;
import com.app.shovelerapp.utils.Constants;

/**
 * Created by supriya.n on 10-06-2016.
 */
public class Confi2BalanceActivity extends AppCompatActivity {
    private TextView mApprovedTextView,mPriceTextView,mPendingTextView,mPendingPriceTextView,mDescTextView;
    private Button mWithdrawMoney;
    private ImageView mSubLogo;
    private TextView mTitle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_balance);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mSubLogo= (ImageView) toolbar.findViewById(R.id.logo_image);
        mTitle= (TextView) toolbar.findViewById(R.id.title);

        mSubLogo.setImageResource(R.drawable.settings1);
        mTitle.setText("BALANCE");


        mApprovedTextView= (TextView) findViewById(R.id.approved);
        mPriceTextView= (TextView) findViewById(R.id.price);
        mPendingPriceTextView= (TextView) findViewById(R.id.pending_price);
        mPendingTextView= (TextView) findViewById(R.id.pending_title);
        mDescTextView= (TextView) findViewById(R.id.note);

        mWithdrawMoney= (Button) findViewById(R.id.button_withdraw);

        mWithdrawMoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               startActivity(new Intent(Confi2BalanceActivity.this,WithdrawActivity.class));
            }
        });

        setFont();
    }

    private void setFont() {
        Typeface tfRegular= Constants.setRegularLatoFont(Confi2BalanceActivity.this);
        Typeface tfLight=Constants.setLightLatoFont(Confi2BalanceActivity.this);
        Typeface tfThin=Constants.setThinLatoFont(Confi2BalanceActivity.this);
        Typeface tfMedium=Constants.setMediumLatoFont(Confi2BalanceActivity.this);

        mApprovedTextView.setTypeface(tfRegular);
        mPriceTextView.setTypeface(tfRegular);
        mPendingPriceTextView.setTypeface(tfRegular);
        mPendingTextView.setTypeface(tfRegular);
        mDescTextView.setTypeface(tfRegular);

        mWithdrawMoney.setTypeface(tfRegular);
    }
}
