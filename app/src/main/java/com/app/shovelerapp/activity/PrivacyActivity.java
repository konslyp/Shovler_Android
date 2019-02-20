package com.app.shovelerapp.activity;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class PrivacyActivity extends AppCompatActivity implements callStaticAPI {
    private TextView mPrivacyTextView,mTitle;
    private ProgressDialog mProgressDialog;
    private ImageView mSubLogo;
    private WebView privacyPolicyWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mSubLogo= (ImageView) toolbar.findViewById(R.id.logo_image);
        mTitle= (TextView) toolbar.findViewById(R.id.title);

        mSubLogo.setImageResource(R.drawable.settings1);
        mTitle.setText("Privacy Policy");


        mPrivacyTextView = (TextView) findViewById(R.id.privacy);

        privacyPolicyWebView = (WebView)findViewById(R.id.wv_privacy);
//        privacyPolicyWebView.loadUrl("http://carshovel.com/api/jobmgt.php/cms/?CMSId=1");
        privacyPolicyWebView.loadUrl("http://carshovel.com/admin/cms.php/?CMSId=1");

//        callPrivacy();
    }

    private void callPrivacy() {
        mProgressDialog = ProgressDialog.show(PrivacyActivity.this, "",
                getResources().getString(R.string.loading), true, false);
        NetUtils.CallStatic("2", this, PrivacyActivity.this);
    }

    @Override
    public void callStaticAPISuccess(String success) {
        mProgressDialog.dismiss();
        try {
            JSONObject jsonObject = new JSONObject(success);
            if (jsonObject.getString("status").equals("true")) {
                JSONArray jsonArray = jsonObject.getJSONArray("items");
                JSONObject object = jsonArray.getJSONObject(0);
                mPrivacyTextView.setText(Html.fromHtml(object.getString("CMS")));
            } else {
                Constants.showAlert(PrivacyActivity.this, "No data found");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void callStaticAPIError(String error) {
        mProgressDialog.dismiss();
        Constants.showAlert(PrivacyActivity.this, error);
    }
}
