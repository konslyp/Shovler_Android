package com.app.shovelerapp.activity;


import android.app.ProgressDialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.shovelerapp.R;
import com.app.shovelerapp.callback.callStaticAPI;
import com.app.shovelerapp.netutils.NetUtils;
import com.app.shovelerapp.utils.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by supriya.n on 10-06-2016.
 */
public class Confi3AboutActivity extends AppCompatActivity implements callStaticAPI {
    private TextView mAboutDesc;
    private ImageView mSubLogo;
    private TextView mTitle;
    private WebView aboutWebView;
    private ProgressDialog mProgressDilog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_about);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mSubLogo= (ImageView) toolbar.findViewById(R.id.logo_image);
        mTitle= (TextView) toolbar.findViewById(R.id.title);

        mSubLogo.setImageResource(R.drawable.settings1);
        mTitle.setText("ABOUT");

        aboutWebView = (WebView)findViewById(R.id.wv_about_us);
//        aboutWebView.loadUrl("http://carshovel.com/api/jobmgt.php/cms/?CMSId=3");
        aboutWebView.loadUrl("http://carshovel.com/admin/cms.php/?CMSId=3");

//        callAboutAPI();
        mAboutDesc= (TextView) findViewById(R.id.about_desc);
        Typeface tfRegular= Constants.setRegularLatoFont(Confi3AboutActivity.this);
        Typeface tfLight=Constants.setLightLatoFont(Confi3AboutActivity.this);
        Typeface tfThin=Constants.setThinLatoFont(Confi3AboutActivity.this);
        Typeface tfMedium=Constants.setMediumLatoFont(Confi3AboutActivity.this);

        mAboutDesc.setTypeface(tfRegular);
    }

    private void callAboutAPI() {
        mProgressDilog = ProgressDialog.show(Confi3AboutActivity.this, "",
                getResources().getString(R.string.loading), true, false);
        NetUtils.CallStatic("3",this,Confi3AboutActivity.this);
    }

    @Override
    public void callStaticAPISuccess(String success) {
        mProgressDilog.dismiss();
        try {
            JSONObject jsonObject=new JSONObject(success);
            if (jsonObject.getString("status").equals("true")){
                JSONArray jsonArray = jsonObject.getJSONArray("items");
                JSONObject object = jsonArray.getJSONObject(0);
                String text=object.getString("CMS");
                mAboutDesc.setText(Html.fromHtml(text));
            }else {
                Constants.showAlert(Confi3AboutActivity.this, "No data found");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void callStaticAPIError(String error) {
        mProgressDilog.dismiss();
        Constants.showAlert(Confi3AboutActivity.this,error);
    }
}
