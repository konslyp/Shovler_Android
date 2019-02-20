package com.app.shovelerapp.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.app.shovelerapp.R;
import com.app.shovelerapp.callback.ApproveJobCallback;
import com.app.shovelerapp.doc.Globals;
import com.app.shovelerapp.model.RequestorJobModel;
import com.app.shovelerapp.netutils.NetUtils;
import com.app.shovelerapp.service.ILoadService;
import com.app.shovelerapp.service.ServiceManager;
import com.app.shovelerapp.utils.Constants;
import com.app.shovelerapp.utils.SharedPrefClass;
import com.app.shovelerapp.utils.Utils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;

import org.json.JSONException;
import org.json.JSONObject;

public class ApproveJobActivity extends AppCompatActivity implements ApproveJobCallback,ILoadService {
    private ImageView mSubLogo, mCircleImage, mImageView1, mImageView2, mImageView3;
    private TextView mTitle, mCommentsTv, mPriceTv;
    private RatingBar ratingBar;
    private EditText mTipAmtEditText;
    private Button mApprove;
    private LinearLayout mPicLinearLayout;
    private RequestorJobModel jobModel;
    private SharedPrefClass prefClass;
    private ProgressDialog mProgressDilog;
    public AlertDialog.Builder chooseTimeDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_approve_job);

        init();

        prefClass = new SharedPrefClass(ApproveJobActivity.this);
        jobModel = (RequestorJobModel) getIntent().getSerializableExtra("data");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mSubLogo = (ImageView) toolbar.findViewById(R.id.logo_image);
        mTitle = (TextView) toolbar.findViewById(R.id.title);

        mSubLogo.setImageResource(R.drawable.ic_action_job_status);
        mTitle.setText("JOB STATUS");

        mPriceTv.setText("$ " + jobModel.getPrice());

        if (jobModel.getScomments().equals("")) {
            mCommentsTv.setText("No comments added here..");
        } else {
            mCommentsTv.setText(jobModel.getScomments());
        }
        if (jobModel.getPic1() == null && jobModel.getPic2() == null && jobModel.getPic3() == null) {
            mPicLinearLayout.setVisibility(View.GONE);
        } else {
            mPicLinearLayout.setVisibility(View.VISIBLE);
        }

        if (jobModel.getPic1() != null) {
            Glide.with(ApproveJobActivity.this).load(jobModel.getPic1()).override(600, 600).error(R.mipmap.app_logo).into(mImageView1);
        }

        if (jobModel.getPic2() != null) {
            Glide.with(ApproveJobActivity.this).load(jobModel.getPic2()).override(600, 600).error(R.mipmap.app_logo).into(mImageView2);
        }

        if (jobModel.getPic3() != null) {
            Glide.with(ApproveJobActivity.this).load(jobModel.getPic3()).override(600, 600).error(R.mipmap.app_logo).into(mImageView3);
        }

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                if (rating < 1.0) {
                    mTipAmtEditText.setEnabled(false);
                } else if (rating == 1.0) {
                    showAlertForOneStar();
                    mTipAmtEditText.setText("0");
                    mTipAmtEditText.setEnabled(false);
                } else {
                    mTipAmtEditText.setText("");
                    mTipAmtEditText.setEnabled(true);
                }
            }
        });

        TextWatcher m_MobileWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().contains("$")) {
                    mTipAmtEditText.setText("$ " + s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };

        // mTipAmtEditText.addTextChangedListener(m_MobileWatcher);

        //Glide.with(ApproveJobActivity.this).load(jobModel.getJobpic()).error(R.mipmap.app_logo).into(mCircleImage);

        if (jobModel.getJobpic() == null || jobModel.getJobpic().endsWith("openjobpics/"))
        {
            if (jobModel.getJobtype().equals("Home") || jobModel.getJobpic().equals("Business")) {
                Utils.getImageFromGoogle(jobModel.getLoclat(), jobModel.getLoclng());
                String location = jobModel.getLoclat() + "," + jobModel.getLoclng();
                ServiceManager.onGetParnoramaID(location,this);

            }
        }
        else {
            Glide.with(ApproveJobActivity.this).load(jobModel.getJobpic()).asBitmap().centerCrop().error(R.drawable.ic_placeholder_approve_job).into(new BitmapImageViewTarget(mCircleImage) {
                @Override
                protected void setResource(Bitmap resource) {
                    RoundedBitmapDrawable circularBitmapDrawable =
                            RoundedBitmapDrawableFactory.create(getResources(), resource);
                    circularBitmapDrawable.setCircular(true);
                    mCircleImage.setImageDrawable(circularBitmapDrawable);
                }
            });
        }

        mApprove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Constants.hideKeyboard(ApproveJobActivity.this, v);

//                if () {
//                    showAlertForOneStar();
//                } else {
                callApproveJob();
//                }
            }
        });

        mImageView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageView mZoomImageView;
                final Dialog dialog = new Dialog(ApproveJobActivity.this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setCancelable(true);
                dialog.setContentView(R.layout.zoom_image);
                mZoomImageView = (ImageView) dialog.findViewById(R.id.image);
                Glide.with(ApproveJobActivity.this).load(jobModel.getPic1()).placeholder(null).error(R.drawable.no_image_1).into(mZoomImageView);
                dialog.show();
            }
        });

        mImageView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageView mZoomImageView;
                final Dialog dialog = new Dialog(ApproveJobActivity.this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setCancelable(true);
                dialog.setContentView(R.layout.zoom_image);
                mZoomImageView = (ImageView) dialog.findViewById(R.id.image);
                Glide.with(ApproveJobActivity.this).load(jobModel.getPic2()).placeholder(null).error(R.drawable.no_image_1).into(mZoomImageView);
                dialog.show();
            }
        });

        mImageView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageView mZoomImageView;
                final Dialog dialog = new Dialog(ApproveJobActivity.this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setCancelable(true);
                dialog.setContentView(R.layout.zoom_image);
                mZoomImageView = (ImageView) dialog.findViewById(R.id.image);
                Glide.with(ApproveJobActivity.this).load(jobModel.getPic3()).placeholder(null).error(R.drawable.no_image_1).into(mZoomImageView);
                dialog.show();
            }
        });


    }

    private void showAlertForOneStar() {
        String oneStarMessage = "You are about to give this snow shoveler a 1 star rating which will ban their account. Please confirm that the job they did is really only 1 star or change the review to 2 stars.";

        AlertDialog.Builder builder1 = new AlertDialog.Builder(ApproveJobActivity.this);
        builder1.setMessage(oneStarMessage);
        builder1.setCancelable(false);
        builder1.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });

        AlertDialog oneStarAlertDialog = builder1.create();
        oneStarAlertDialog.show();
    }

    private void callApproveJob() {

        showRateDialog();
//        mProgressDilog = ProgressDialog.show(ApproveJobActivity.this, "",
//                getResources().getString(R.string.loading), true, false);
//        NetUtils.CallApproveJob(jobModel.getJid(), prefClass.getSavedStringPreference(SharedPrefClass.USER_ID), mTipAmtEditText.getText().toString().replace("$", ""), String.valueOf(ratingBar.getRating()), this, ApproveJobActivity.this);
    }
    public void showRateDialog()
    {
        chooseTimeDialog = new AlertDialog.Builder(
                ApproveJobActivity.this);

        View titleview = getLayoutInflater().inflate(R.layout.alert_time, null);
        TextView title1 = (TextView) titleview.findViewById(R.id.dialogTimeTitle);
        title1.setText("Happy Shovler");

        chooseTimeDialog.setCustomTitle(titleview);
        TextView message = new TextView(ApproveJobActivity.this);
        message.setPadding(12, 10, 12, 4);

        String messageString = "We are happy that you had a good experience with Shovler. Can you please leave us a good rating on the app store?";
        message.setText(messageString);
        message.setMovementMethod(LinkMovementMethod.getInstance());
        chooseTimeDialog.setView(message);
        chooseTimeDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        chooseTimeDialog.setPositiveButton("Rate it", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.app.shovelerapp&hl=en"));
                startActivity(browserIntent);
            }
        });
        chooseTimeDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                startActivity(new Intent(ApproveJobActivity.this, JobStatusActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });
        chooseTimeDialog.show();
    }
    private void init() {
        mCircleImage = (ImageView) findViewById(R.id.imageView2);
        mImageView1 = (ImageView) findViewById(R.id.iv1);
        mImageView2 = (ImageView) findViewById(R.id.iv2);
        mImageView3 = (ImageView) findViewById(R.id.iv3);
        mCommentsTv = (TextView) findViewById(R.id.comments_tv);
        mPriceTv = (TextView) findViewById(R.id.price_job);
        ratingBar = (RatingBar) findViewById(R.id.ratting);
        mTipAmtEditText = (EditText) findViewById(R.id.tip_et);
        mApprove = (Button) findViewById(R.id.approved_job);
        mPicLinearLayout = (LinearLayout) findViewById(R.id.pic_layout);
    }

    @Override
    public void ApproveJobCallbackSuccess(String success) {
        mProgressDilog.dismiss();
        try {
            JSONObject jsonObject = new JSONObject(success);
            if (jsonObject.getString("status").equals("true")) {
                showRateDialog();
            } else {
                Constants.showAlert(ApproveJobActivity.this, jsonObject.getString("items"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void ApproveJobCallbackError(String error) {
        mProgressDilog.dismiss();
        Constants.showAlert(ApproveJobActivity.this, error);
    }

    @Override
    public void onResponse(int code) {
        switch (code)
        {
            case 400:
                Glide.with(ApproveJobActivity.this).load(Globals.g_googlePhoto).asBitmap().centerCrop().error(R.drawable.ic_placeholder_approve_job).into(new BitmapImageViewTarget(mCircleImage) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable circularBitmapDrawable =
                                RoundedBitmapDrawableFactory.create(getResources(), resource);
                        circularBitmapDrawable.setCircular(true);
                        mCircleImage.setImageDrawable(circularBitmapDrawable);
                    }
                });
                break;
        }
    }

    @Override
    public void onResponse(int code, int index) {

    }
}
