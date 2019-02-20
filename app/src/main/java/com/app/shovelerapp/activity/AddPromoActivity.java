package com.app.shovelerapp.activity;


import android.app.ProgressDialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.shovelerapp.R;
import com.app.shovelerapp.callback.PromocodeCallback;
import com.app.shovelerapp.model.QuestionModel;
import com.app.shovelerapp.netutils.NetUtils;
import com.app.shovelerapp.utils.Constants;
import com.app.shovelerapp.utils.SharedPrefClass;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by supriya.n on 22-06-2016.
 */
public class AddPromoActivity extends AppCompatActivity implements PromocodeCallback{
    private EditText mPromoCodeEditText;
    private Button mAddPromoCodeText;
    private ImageView mSubLogo;
    private TextView mTitle;
    private ProgressDialog progressDialog;
    private String str_cust_id;
    private SharedPrefClass prefClass;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_add_promo);

        prefClass=new SharedPrefClass(AddPromoActivity.this);

        mPromoCodeEditText = (EditText) findViewById(R.id.promo_code_et);
        mAddPromoCodeText = (Button) findViewById(R.id.add_promo_code);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mSubLogo = (ImageView) toolbar.findViewById(R.id.logo_image);
        mTitle = (TextView) toolbar.findViewById(R.id.title);

        mSubLogo.setImageResource(R.drawable.settings1);
        mTitle.setText("SETTING");


        setFont();

        mAddPromoCodeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPromoCodeEditText.getText().toString().equals("")){
                    Constants.showAlert(AddPromoActivity.this,"Please Enter PromoCode");
                }else {
                    callAddPromo();
                }
            }
        });
    }

    private void callAddPromo() {
        progressDialog = ProgressDialog.show(AddPromoActivity.this, "",
                getResources().getString(R.string.loading), true, false);
        NetUtils.CallAddPromocode(mPromoCodeEditText.getText().toString(),
                prefClass.getSavedStringPreference(SharedPrefClass.USER_ID),this,AddPromoActivity.this);
    }

    private void setFont() {
        Typeface tfRegular = Constants.setRegularLatoFont(AddPromoActivity.this);
        Typeface tfLight = Constants.setLightLatoFont(AddPromoActivity.this);
        Typeface tfThin = Constants.setThinLatoFont(AddPromoActivity.this);
        Typeface tfMedium = Constants.setMediumLatoFont(AddPromoActivity.this);

        mPromoCodeEditText.setTypeface(tfRegular);
        mAddPromoCodeText.setTypeface(tfRegular);
    }

    @Override
    public void PromocodeCallbackSuccess(String success) {
        progressDialog.dismiss();
        try {
            JSONObject object=new JSONObject(success);
            if (object.getString("status").equals("true")){
                String msg=object.getString("message");

                Constants.showAlert(AddPromoActivity.this,msg);
                //mThnksRelativeLayout.setVisibility(View.VISIBLE);
            }else {
                String msg=object.getString("items");
                Constants.showAlert(AddPromoActivity.this,msg);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void PromocodeCallbackError(String error) {
        progressDialog.dismiss();
        Constants.showAlert(AddPromoActivity.this,error);
    }
}
