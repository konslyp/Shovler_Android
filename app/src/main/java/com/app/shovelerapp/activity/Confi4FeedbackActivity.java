package com.app.shovelerapp.activity;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.shovelerapp.R;
import com.app.shovelerapp.callback.FeedbackCallback;
import com.app.shovelerapp.netutils.NetUtils;
import com.app.shovelerapp.utils.Constants;
import com.app.shovelerapp.utils.SharedPrefClass;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by supriya.n on 10-06-2016.
 */
public class Confi4FeedbackActivity extends AppCompatActivity implements FeedbackCallback{
    private Button mFeedbackButton;
    private RelativeLayout mThnksRelativeLayout;
    private EditText mFeedbaackEditText;
    private TextView mFeedbackDetails;
    private ImageView mSubLogo;
    private TextView mTitle;
    private SharedPrefClass prefClass;
    private ProgressDialog mProgressDilog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_feedback);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mSubLogo= (ImageView) toolbar.findViewById(R.id.logo_image);
        mTitle= (TextView) toolbar.findViewById(R.id.title);

        mSubLogo.setImageResource(R.drawable.settings1);
        mTitle.setText("FEEDBACK");


        prefClass=new SharedPrefClass(Confi4FeedbackActivity.this);

        mFeedbackButton= (Button) findViewById(R.id.send_feedback);
        mThnksRelativeLayout= (RelativeLayout) findViewById(R.id.thanks_layout);
        mFeedbaackEditText= (EditText) findViewById(R.id.feedback_et);
        mFeedbackDetails= (TextView) findViewById(R.id.feedback_desc);

        Typeface tfRegular= Constants.setRegularLatoFont(Confi4FeedbackActivity.this);
        Typeface tfLight=Constants.setLightLatoFont(Confi4FeedbackActivity.this);
        Typeface tfThin=Constants.setThinLatoFont(Confi4FeedbackActivity.this);
        Typeface tfMedium=Constants.setMediumLatoFont(Confi4FeedbackActivity.this);

        mFeedbaackEditText.setTypeface(tfRegular);
        mFeedbackButton.setTypeface(tfRegular);
        mFeedbackDetails.setTypeface(tfRegular);


        mFeedbackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mFeedbaackEditText.getText().toString().equals("")){
               Constants.showAlert(Confi4FeedbackActivity.this,"Please Enter Feedback");
                }else {
                    callFeedBack();
                }
            }
        });
    }

    private void callFeedBack() {
        mProgressDilog = ProgressDialog.show(Confi4FeedbackActivity.this, "",
                getResources().getString(R.string.loading), true, false);
       NetUtils.CallFeedback(prefClass.getSavedStringPreference(SharedPrefClass.USER_ID),mFeedbaackEditText.getText().toString(),this,Confi4FeedbackActivity.this);
    }

    @Override
    public void FeedbackCallbackSuccess(String success) {
        mProgressDilog.dismiss();
        try {
            JSONObject object=new JSONObject(success);
            if (object.getString("status").equals("true")){
                String msg=object.getString("items");
                AlertDialog.Builder builder1 = new AlertDialog.Builder(Confi4FeedbackActivity.this);
                builder1.setMessage(msg);
                builder1.setCancelable(false);
                builder1.setPositiveButton("Ok",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                finish();
                            }
                        });


                AlertDialog alert11 = builder1.create();
                alert11.show();;
                //mThnksRelativeLayout.setVisibility(View.VISIBLE);
            }else {
                String msg=object.getString("items");
                Constants.showAlert(Confi4FeedbackActivity.this,msg);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void FeedbackCallbackError(String error) {
        mProgressDilog.dismiss();
        Constants.showAlert(Confi4FeedbackActivity.this,error);
    }
}
