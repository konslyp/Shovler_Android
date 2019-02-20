package com.app.shovelerapp.activity;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.shovelerapp.R;
import com.app.shovelerapp.callback.GetPromoCodeCallback;
import com.app.shovelerapp.model.Promocode;
import com.app.shovelerapp.netutils.NetUtils;
import com.app.shovelerapp.utils.Constants;
import com.app.shovelerapp.utils.SharedPrefClass;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by supriya.n on 22-06-2016.
 */
public class SendPromoCodeActivity extends AppCompatActivity implements GetPromoCodeCallback {
    private TextView mSetCodeTextView, mDiscountTextView, mSendTextView, mPromocodeTextView;
    private Button mSendEmailButton;
    private RelativeLayout mSendText, mShareFacebook, mShareTwitter;
    CallbackManager callbackManager;
    ShareDialog shareDialog;
    private ImageView mSubLogo, mDrawer;
    private TextView mTitle;
    private SharedPrefClass prefClass;
    private ProgressDialog mProgressDialog;

    private String discount = "10";
    private String promoCodeString = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(SendPromoCodeActivity.this);
        setContentView(R.layout.fragment_send_promo);

        prefClass = new SharedPrefClass(SendPromoCodeActivity.this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mSubLogo = (ImageView) toolbar.findViewById(R.id.logo_image);
        mTitle = (TextView) toolbar.findViewById(R.id.title);
        mDrawer = (ImageView) toolbar.findViewById(R.id.drawer);
        mDrawer.setVisibility(View.GONE);
        mSubLogo.setImageResource(R.drawable.settings1);
        mTitle.setText("PROMOCODE");

        mSetCodeTextView = (TextView) findViewById(R.id.set_code_text);
        mDiscountTextView = (TextView) findViewById(R.id.discount_text);
        mSendTextView = (TextView) findViewById(R.id.promocode_title);
        mPromocodeTextView = (TextView) findViewById(R.id.promocode_val);

        mSendEmailButton = (Button) findViewById(R.id.send_email);

        mSendText = (RelativeLayout) findViewById(R.id.message_click);
        mShareFacebook = (RelativeLayout) findViewById(R.id.facebook_click);
        mShareTwitter = (RelativeLayout) findViewById(R.id.twit_click);

        //mPromocodeTextView.setText("Test");


        setFont();

        getPromocode();

        mSendEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                String shareBody = "I’ve been using Shovler and think you’d really like it too! It is the easiest way to find someone to shovel snow for you. If you sign up with promo code,"+mPromocodeTextView.getText().toString()+" , you will get 10% off your first job. You can download the app here: LINK";
//                String shareBody = "I’ve been using Shovler and think you’d really like it too! It is the easiest way to find someone to shovel snow for you. If you sign up with promo code, " + promoCodeString + ", and you will get a " + discount + "% discount on your first job. You can download the app here: LINK";
                String shareBody = "I've been using Shovler and think you'd really like it too! It is the absolute easiest way to hire a snow shoveler! If you sign up now with promo code, " + promoCodeString + ", you will get a " + discount + "% discount on your first job. You can download the app here: www.shovler.com";

                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Shovler Promo code");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, "Email"));
            }
        });

        mSendText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent message = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + ""));
//                String shareBody = "I’ve been using Shovler and think you’d really like it too! It is the easiest way to find someone to shovel snow for you. If you sign up with promo code, " + promoCodeString + ", and you will get a " + discount + "% discount on your first job. You can download the app here: LINK";
//                message.putExtra("sms_body", "I’ve been using Shovler and think you’d really like it too! It is the easiest way to find someone to shovel snow for you. If you sign up with promo code," + mPromocodeTextView.getText().toString() + " , you will get  a " + discount + " off your first job. You can download the app here: LINK");
                String shareBody = "I've been using Shovler and think you'd really like it too! It is the absolute easiest way to hire a snow shoveler! If you sign up now with promo code, " + promoCodeString + ", you will get a " + discount + "% discount on your first job. You can download the app here: www.shovler.com";
                message.putExtra("sms_body", shareBody);
                startActivity(message);
            }
        });

        mShareFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                String shareBody = "I’ve been using Shovler and think you’d really like it too! It is the easiest way to find someone to shovel snow for you. If you sign up with promo code, " + promoCodeString + ", and you will get a " + discount + "% discount on your first job. You can download the app here: LINK";
                String shareBody = "I've been using Shovler and think you'd really like it too! It is the absolute easiest way to hire a snow shoveler! If you sign up now with promo code, " + promoCodeString + ", you will get a " + discount + "% discount on your first job. You can download the app here: www.shovler.com";
                ShareLinkContent linkContent = new ShareLinkContent.Builder()
                        .setContentTitle("Shovler Application")
//                        .setContentDescription("I’ve been using Shovler and think you’d really like it too! It is the easiest way to find someone to shovel snow for you. If you sign up with promo code," + mPromocodeTextView.getText().toString() + " , you will get 10% off your first job. You can download the app here: LINK")
                        .setContentDescription(shareBody)
                        .setContentUrl(Uri.parse("http://shovler.com"))
                        .build();

                shareDialog.show(linkContent);
            }
        });

        mShareTwitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                String shareBody = "I’ve been using Shovler and think you’d really like it too! " +
//                        "It is the easiest way to find someone to shovel snow for you. " +
//                        "If you sign up with promo code, " + promoCodeString + ", and you will get a " + discount +
//                        "% discount on your first job. You can download the app here: LINK";

//                String shareBody = "I use Shovler.com app and I think you’d really like it too! easiest " +
//                        "way to hire a snow shoveler! Use " + promoCodeString + " for " + discount +
//                        "% off #snow";

                String shareBody = "I use the Shovler.com app and think you’d really like it! Easiest way to hire a snow shoveler! Use " + promoCodeString + " for " + discount + "% off #snow";


                Intent share = new Intent(Intent.ACTION_SEND);
                // Set the MIME type
                share.setType("image/*");
//                share.putExtra(Intent.EXTRA_TEXT, "I’ve been using Shovler and think you’d really like it too! It is the easiest way to find someone to shovel snow for you. If you sign up with promo code," + mPromocodeTextView.getText().toString() + " , you will get 10% off your first job. You can download the app here: LINK");
                share.putExtra(Intent.EXTRA_TEXT, shareBody);
                if (isPackageInstalled("com.twitter.android", SendPromoCodeActivity.this)) {
                    share.setPackage("com.twitter.android");
                    startActivity(share);
                } else {
                    Toast.makeText(SendPromoCodeActivity.this, "Please Install Twitter!",
                            Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    private void getPromocode() {
        mProgressDialog = ProgressDialog.show(SendPromoCodeActivity.this, "",
                getResources().getString(R.string.loading), true, false);
        NetUtils.CallGetPromocode(this, SendPromoCodeActivity.this);
    }

    private boolean isPackageInstalled(String packagename, Context context) {
        PackageManager pm = context.getPackageManager();
        try {
            pm.getPackageInfo(packagename, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    private void setFont() {
        Typeface tfRegular = Constants.setRegularLatoFont(SendPromoCodeActivity.this);
        Typeface tfLight = Constants.setLightLatoFont(SendPromoCodeActivity.this);
        Typeface tfThin = Constants.setThinLatoFont(SendPromoCodeActivity.this);
        Typeface tfMedium = Constants.setMediumLatoFont(SendPromoCodeActivity.this);

        mSetCodeTextView.setTypeface(tfRegular);
        mDiscountTextView.setTypeface(tfThin);
        mSendTextView.setTypeface(tfRegular);
        mPromocodeTextView.setTypeface(tfRegular);
        mSendEmailButton.setTypeface(tfRegular);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void GetPromoCodeCallbackSuccess(String success) {
        mProgressDialog.dismiss();
        try {
            JSONObject jsonObject = new JSONObject(success);
            if (jsonObject.getString("status").equals("true")) {
                JSONArray jsonArray = jsonObject.getJSONArray("items");
                Promocode promocode;
//                for (int i = 0; i < jsonArray.length(); i++) {
                if (jsonArray.length() > 0) {
                    JSONObject object = jsonArray.getJSONObject(0);
                    if (object.has("pcode"))
                        promoCodeString = object.getString("pcode");
                    mPromocodeTextView.setText(promoCodeString);

                    if (object.has("discount"))
                        discount = object.getString("discount");
                    String setCodeString = "SHARE PROMO CODE AND NEW USERS WILL RECEIVE " + discount + "% OFF ON THEIR FIRST JOB!";
                    mSetCodeTextView.setText(setCodeString);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void GetPromoCodeCallbackError(String error) {
        mProgressDialog.dismiss();
    }
}
