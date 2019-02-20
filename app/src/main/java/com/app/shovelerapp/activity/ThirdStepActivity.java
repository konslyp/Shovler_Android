package com.app.shovelerapp.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.shovelerapp.R;
import com.app.shovelerapp.callback.CancelJobsCallback;
import com.app.shovelerapp.callback.GetBusinessPriceCallback;
import com.app.shovelerapp.callback.HoldCancelJobsCallback;
import com.app.shovelerapp.callback.PostJobCallback;
import com.app.shovelerapp.callback.RelunchCallback;
import com.app.shovelerapp.doc.Globals;
import com.app.shovelerapp.model.JobDetails;
import com.app.shovelerapp.netutils.NetUtils;
import com.app.shovelerapp.service.ILoadService;
import com.app.shovelerapp.service.ServiceManager;
import com.app.shovelerapp.utils.Constants;
import com.app.shovelerapp.utils.SharedPrefClass;
import com.app.shovelerapp.utils.Utils;
import com.braintreepayments.api.dropin.DropInActivity;
import com.braintreepayments.api.dropin.DropInRequest;
import com.braintreepayments.api.dropin.DropInResult;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.OnStreetViewPanoramaReadyCallback;
import com.google.android.gms.maps.StreetViewPanorama;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.SupportStreetViewPanoramaFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.ui.IconGenerator;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.File;
import java.util.HashMap;

/**
 * Created by supriya.n on 06-08-2016.
 */
public class ThirdStepActivity extends AppCompatActivity implements /*GetBusinessPriceCallback, */PostJobCallback, View.OnClickListener,OnMapReadyCallback,ILoadService,RelunchCallback, CancelJobsCallback, HoldCancelJobsCallback {

    private LinearLayout mCarLayout, mHomeLayout, mBusinessLayout;
    private RelativeLayout mCarLayoutLayout, mHomeLayoutLayout, mBusinessLayoutLayout;
    private TextView mRequestTextView, mLocationTextView, mPriceTextView, mDescTv;
    private TextView mCarModel, mCarPlateNo, mCarState;
    private TextView mBusinessSize;
    private ImageView mCarImageView, mHomeImageView, mBusinessImageView, mNumberImage,mCarColor;

    private StreetViewPanorama mCarImageViewPanorama, mHomeImageViewPanorama, mBusinessImageViewPanorama;
    private TextView mCarMask, mHomeMask, mBusinessMask;

    private Button mPostButton;
    private ImageView mSubLogo;
    String str_status = "", jid = "";
    private TextView mTitle, mSpecialInstructionTitle;
    private JobDetails.JobDetailsEntity jobDetailsEntity = new JobDetails.JobDetailsEntity();
    private SharedPrefClass prefClass;
    private ProgressDialog mProgressDilog;
    private File file = new File("");
    private Marker mMaker = null;
    //public  ImageView imgChat;
    public LinearLayout layoutChat;
    public LinearLayout layoutTrack;
    public SupportMapFragment mapFragment;
    public GoogleMap mMap;
    private boolean isTrack = true;
    public RelativeLayout layoutExp;
    public TextView txtTime;

    @Override
    protected void onDestroy() {
        isTrack = false;
        super.onDestroy();

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third_step);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        prefClass = new SharedPrefClass(ThirdStepActivity.this);

        mSubLogo = (ImageView) toolbar.findViewById(R.id.logo_image);
        mTitle = (TextView) toolbar.findViewById(R.id.title);
        mSpecialInstructionTitle = (TextView) toolbar.findViewById(R.id.spe_title);
        layoutTrack = (LinearLayout) this.findViewById(R.id.layout_jobtrack);
        layoutExp = (RelativeLayout) this.findViewById(R.id.exptimeLayout);
        txtTime = (TextView) this.findViewById(R.id.txtExpTimeArrive);


        mSubLogo.setImageResource(R.drawable.ic_action_request);


        mCarLayout = (LinearLayout) findViewById(R.id.car_layout);
        mHomeLayout = (LinearLayout) findViewById(R.id.home_layout);
        mBusinessLayout = (LinearLayout) findViewById(R.id.business_layout);

        mCarLayoutLayout = (RelativeLayout) findViewById(R.id.car_upload_image_layout);
        mHomeLayoutLayout = (RelativeLayout) findViewById(R.id.home_upload_image_layout);
        mBusinessLayoutLayout = (RelativeLayout) findViewById(R.id.business_upload_image_layout);

        mPostButton = (Button) findViewById(R.id.post_button);

        mCarImageView = (ImageView) findViewById(R.id.car_iv);
        mHomeImageView = (ImageView) findViewById(R.id.home_iv);
        mBusinessImageView = (ImageView) findViewById(R.id.business_iv);

        mCarMask = (TextView) findViewById(R.id.mask_0);
        mHomeMask = (TextView) findViewById(R.id.mask_1);
        mBusinessMask = (TextView) findViewById(R.id.mask_2);


        mNumberImage = (ImageView) findViewById(R.id.number_image);
        //imgChat = (ImageView) findViewById(R.id.imgRequestJobChat);
        layoutChat = (LinearLayout) findViewById(R.id.layoutChatShovlerRequester);
        layoutChat.setOnClickListener(this);
        //imgChat.setOnClickListener(this);

        mCarImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent m = new Intent(ThirdStepActivity.this,ImageShowActivity.class);

                if (jobDetailsEntity.getCar().getUrl()!= null && !jobDetailsEntity.getCar().getUrl().endsWith("openjobpics/")) {
                    m.putExtra("path",jobDetailsEntity.getCar().getUrl());
                    ThirdStepActivity.this.startActivity(m);
                }
                //else m.putExtra("path",Globals.g_googlePhoto);

            }
        });

        mHomeImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent m = new Intent(ThirdStepActivity.this,ImageShowActivity.class);

                if (jobDetailsEntity.getHome().getUrl()!= null && !jobDetailsEntity.getHome().getUrl().endsWith("openjobpics/")) {
                    m.putExtra("path",jobDetailsEntity.getHome().getUrl());
                }
                else m.putExtra("path",Globals.g_googlePhoto);
                ThirdStepActivity.this.startActivity(m);
            }
        });

        mBusinessImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent m = new Intent(ThirdStepActivity.this,ImageShowActivity.class);

                if (jobDetailsEntity.getBusiness().getUrl()!= null && !jobDetailsEntity.getBusiness().getUrl().endsWith("openjobpics/")) {
                    m.putExtra("path",jobDetailsEntity.getBusiness().getUrl());
                }
                else m.putExtra("path",Globals.g_googlePhoto);
                ThirdStepActivity.this.startActivity(m);
            }
        });

        mCarModel = (TextView) findViewById(R.id.model);
        mCarColor = (ImageView) findViewById(R.id.color_val);
        mCarPlateNo = (TextView) findViewById(R.id.plate_val);
        mCarState = (TextView) findViewById(R.id.state_val);

        mRequestTextView = (TextView) findViewById(R.id.request_value);
        mLocationTextView = (TextView) findViewById(R.id.location_val);
        mPriceTextView = (TextView) findViewById(R.id.price_value);
        mDescTv = (TextView) findViewById(R.id.special_instruction_val);

        mBusinessSize = (TextView) findViewById(R.id.size_value);

        if (Constants.JOB_STATUS_DETAILS_FLAG) {
            mTitle.setText("JOB DETAILS");
            layoutTrack.setVisibility(View.VISIBLE);
            jobDetailsEntity = JobStatusActivity.details.getJobDetails();
            mNumberImage.setVisibility(View.GONE);
            str_status = getIntent().getExtras().getString("status");
            jid = getIntent().getExtras().getString("jid");
            //imgChat.setVisibility(View.VISIBLE);
            layoutChat.setVisibility(View.VISIBLE);
            layoutExp.setVisibility(View.VISIBLE);
            txtTime.setText(jobDetailsEntity.exptime);


            mPostButton.setText("TAKE ACTION");
        } else {
            mTitle.setText("REQUEST");
            jobDetailsEntity = FirstStepActivity.details.getJobDetails();
            mNumberImage.setVisibility(View.VISIBLE);
            mPostButton.setText("POST JOB");
            layoutTrack.setVisibility(View.GONE);
            //imgChat.setVisibility(View.GONE);
            layoutChat.setVisibility(View.GONE);
            layoutExp.setVisibility(View.GONE);
        }


        //, mCarColor, mCarPlateNo, mCarState;


        mLocationTextView.setText(jobDetailsEntity.getAddress());

        if (jobDetailsEntity.getDesc() != null && !jobDetailsEntity.getDesc().equals("")) {
            mDescTv.setText(jobDetailsEntity.getDesc());
        }

        if (jobDetailsEntity.getBusiness() != null) {
//            callBusinessPriceAPI();
            mPriceTextView.setText("$ " + Constants.BUSINESS_PRICE);
        }


        /*if (jobDetailsEntity.getDesc()!=null || jobDetailsEntity.getDesc().equals("")){

        }else {
            mSpecialInstructionTitle.setVisibility(View.GONE);
            mDescTv.setVisibility(View.GONE);
        }*/


        if (jobDetailsEntity.getCar() != null) {
            mCarLayout.setVisibility(View.VISIBLE);
            mHomeLayout.setVisibility(View.GONE);
            mBusinessLayout.setVisibility(View.GONE);
            mRequestTextView.setText("Car");
            mCarModel.setText(jobDetailsEntity.getCar().getModel());
            mCarColor.setBackgroundColor(Color.parseColor(jobDetailsEntity.getCar().getColor()));
            mCarPlateNo.setText(jobDetailsEntity.getCar().getLicense());
            mCarState.setText(jobDetailsEntity.getCar().getState());
            mPriceTextView.setText("$ "+jobDetailsEntity.getCar().getPrice());

            if (jobDetailsEntity.getCar().getUrl()!= null && !jobDetailsEntity.getCar().getUrl().endsWith("openjobpics/")) {
                //mCarImageView.setImageURI(Uri.parse(jobDetailsEntity.getCar().getUrl()));
                //Glide.with(ThirdStepActivity.this).load(jobDetailsEntity.getBusiness().getUrl()).placeholder(null).into(mCarImageView);
                Glide.with(ThirdStepActivity.this).load(jobDetailsEntity.getCar().getUrl()).override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL) // don't use target size, load full image
                        .diskCacheStrategy(DiskCacheStrategy.RESULT).error(R.drawable.action_bar_bg).placeholder(null).into(mCarImageView);
            } else {
                //mCarImageView.setVisibility(View.GONE);
                //mCarLayoutLayout.setVisibility(View.GONE);
                Utils.getImageFromGoogle(jobDetailsEntity.getLat(),jobDetailsEntity.getLng());
                mCarImageView.setImageResource(R.drawable.action_bar_bg);
//                Glide.with(ThirdStepActivity.this).load(null).override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL) // don't use target size, load full image
//                        .diskCacheStrategy(DiskCacheStrategy.RESULT).error(R.drawable.action_bar_bg).placeholder(null).into(mCarImageView);
            }

        } else if (jobDetailsEntity.getHome() != null) {

            mRequestTextView.setText("Home");

            mHomeLayout.setVisibility(View.VISIBLE);
            mCarLayout.setVisibility(View.GONE);
            mBusinessLayout.setVisibility(View.GONE);
            mPriceTextView.setText("$ "+jobDetailsEntity.getHome().getPrice());

            if (jobDetailsEntity.getHome().getUrl()!= null && !jobDetailsEntity.getHome().getUrl().endsWith("openjobpics/")) {
                //mHomeImageView.setImageURI(Uri.parse(jobDetailsEntity.getHome().getUrl()));
                Glide.with(ThirdStepActivity.this).load(jobDetailsEntity.getHome().getUrl()).override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL) // don't use target size, load full image
                        .diskCacheStrategy(DiskCacheStrategy.RESULT).error(R.drawable.action_bar_bg).placeholder(null).into(mHomeImageView);
            } else {
                //mHomeImageView.setVisibility(View.GONE);
                //mHomeLayoutLayout.setVisibility(View.GONE);
                mHomeImageView.setVisibility(View.GONE);
                mHomeMask.setVisibility(View.GONE);
                Utils.getImageFromGoogle(jobDetailsEntity.getLat(),jobDetailsEntity.getLng());
                //String location = jobModel.getLoclat() + "," + jobModel.getLoclng();
                //ServiceManager.onGetParnoramaID(location,this);
                SupportStreetViewPanoramaFragment homePanorama = (SupportStreetViewPanoramaFragment)
                        getSupportFragmentManager().findFragmentById(R.id.home_iv_panorama);

                homePanorama.getStreetViewPanoramaAsync(
                        new OnStreetViewPanoramaReadyCallback() {
                            @Override
                            public void onStreetViewPanoramaReady(StreetViewPanorama panorama) {
                                mHomeImageViewPanorama = panorama;
                                mHomeImageViewPanorama.setStreetNamesEnabled(false);
                                mHomeImageViewPanorama
                                        .setUserNavigationEnabled(false);
                                mHomeImageViewPanorama.setZoomGesturesEnabled(true);
                                mHomeImageViewPanorama.setPanningGesturesEnabled(true);
                                mHomeImageViewPanorama.setPosition(new LatLng(Double.parseDouble(jobDetailsEntity.getLat()),Double.parseDouble(jobDetailsEntity.getLng())));

                                // Only set the panorama to SAN_FRAN on startup (when no panoramas have been
                                // loaded which is when the savedInstanceState is null).
//                        if (savedInstanceState == null) {
//                            //mStreetViewPanorama.setPosition("1320 Trafalgar st. Teaneck NJ 07666");
//                            mCarImageViewPanorama.setPosition(SAN_FRAN);
//                        }
                            }
                        });

//                Glide.with(ThirdStepActivity.this).load(Globals.g_googlePhoto).override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL) // don't use target size, load full image
//                        .diskCacheStrategy(DiskCacheStrategy.RESULT).error(R.drawable.action_bar_bg).placeholder(null).into(mHomeImageView);
            }

        } else if (jobDetailsEntity.getBusiness() != null) {

            mRequestTextView.setText("Business");

            mBusinessLayout.setVisibility(View.VISIBLE);
            mCarLayout.setVisibility(View.GONE);
            mHomeLayout.setVisibility(View.GONE);

            //mPriceTextView.setText(jobDetailsEntity.getBusiness().getPrice());

            mBusinessSize.setText(jobDetailsEntity.getBusiness().getSize() + " ft²");

            if (jobDetailsEntity.getBusiness().getUrl()!= null && !jobDetailsEntity.getBusiness().getUrl().endsWith("openjobpics/")) {
                //mBusinessImageView.setImageURI(Uri.parse(jobDetailsEntity.getBusiness().getUrl()));
                Glide.with(ThirdStepActivity.this).load(jobDetailsEntity.getBusiness().getUrl()).override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL) // don't use target size, load full image
                        .diskCacheStrategy(DiskCacheStrategy.RESULT).error(R.drawable.action_bar_bg).placeholder(null).into(mBusinessImageView);
            } else {
                //mBusinessImageView.setVisibility(View.GONE);
                //mBusinessLayoutLayout.setVisibility(View.GONE);
                Utils.getImageFromGoogle(jobDetailsEntity.getLat(),jobDetailsEntity.getLng());

                String location = String.valueOf(jobDetailsEntity.getLat()) + "," + String.valueOf(jobDetailsEntity.getLng());
                //Utils.getImageFromGoogle(jobModel.getLoclat(),jobModel.getLoclng());
                mBusinessImageView.setVisibility(View.GONE);
                mBusinessMask.setVisibility(View.GONE);
                //String location = jobModel.getLoclat() + "," + jobModel.getLoclng();
                //ServiceManager.onGetParnoramaID(location,this);
                getSupportFragmentManager().findFragmentById(R.id.business_iv_panorama).setUserVisibleHint(false);
                SupportStreetViewPanoramaFragment businessPanorama = (SupportStreetViewPanoramaFragment)
                        getSupportFragmentManager().findFragmentById(R.id.business_iv_panorama);

                businessPanorama.getStreetViewPanoramaAsync(
                        new OnStreetViewPanoramaReadyCallback() {
                            @Override
                            public void onStreetViewPanoramaReady(StreetViewPanorama panorama) {
                                mBusinessImageViewPanorama = panorama;
                                mBusinessImageViewPanorama.setStreetNamesEnabled(false);
                                mBusinessImageViewPanorama
                                        .setUserNavigationEnabled(false);
                                mBusinessImageViewPanorama.setZoomGesturesEnabled(true);
                                mBusinessImageViewPanorama.setPanningGesturesEnabled(true);
                                mBusinessImageViewPanorama.setPosition(new LatLng(Double.parseDouble(jobDetailsEntity.getLat()),Double.parseDouble(jobDetailsEntity.getLng())));
                            }
                        });
            }
        }
        layoutTrack.setVisibility(View.GONE);
        layoutChat.setVisibility(View.GONE);
        if (str_status.equals("ACTIVE") || str_status.equals("DONE")) {
            mPostButton.setVisibility(View.GONE);
            if (str_status.equals("ACTIVE"))
            {
                layoutTrack.setVisibility(View.VISIBLE);
                layoutChat.setVisibility(View.VISIBLE);
                mapFragment = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(
                        R.id.mapTrack));

                try {
                    MapsInitializer.initialize(getApplicationContext());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mapFragment.getMapAsync(this);

            }
        }
        mPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mPostButton.getText().equals("POST JOB")) {
                    if (!prefClass.isUserLogin()) {
                        Constants.LOGIN_FROM = "THIRD STEP";
                        startActivity(new Intent(ThirdStepActivity.this, HomeActivity.class));
                    } else {
                        //callPostJobAPI();
                        //startActivity(new Intent(ThirdStepActivity.this, RequestorStripeActivity.class));

                        ServiceManager.onRequestToken(ThirdStepActivity.this);
                    }

                } else {
                    showAlert(str_status);

                }


            }

        });
    }
    public void requestToken()
    {

    }
    private void showAlert(String str_status) {

        if (str_status.equals("HOLD")) {
            CharSequence[] items = {"RELAUNCH JOB", "CANCEL JOB", "NO ACTION"};
            alert(items);
        } else if (str_status.equals("ACTIVE")) {
            mPostButton.setVisibility(View.GONE);
        } else if (str_status.equals("OPEN")) {
            CharSequence[] items = {"CANCEL JOB", "NO ACTION"};
            alert(items);
        } else if (str_status.equals("CANCELLED")) {
            CharSequence[] items = {"RELAUNCH JOB", "NO ACTION"};
            alert(items);
        }


    }

    private void alert(final CharSequence[] items) {
        final AlertDialog.Builder chooseDrinkdialog = new AlertDialog.Builder(
                ThirdStepActivity.this);
        View titleview = getLayoutInflater().inflate(R.layout.alert_title, null);
        TextView title1 = (TextView) titleview.findViewById(R.id.dialogtitle);
        title1.setText("Select the action you would like to take");
        chooseDrinkdialog.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                switch (items[which].toString()) {
                    case "RELAUNCH JOB":
                        dialog.dismiss();
                        if (str_status.equals("HOLD")) {
                            cancelHoldJob("OPEN");
                        } else
                            callRelunch();
                        break;
                    case "CANCEL JOB":
                        dialog.dismiss();
                        if (str_status.equals("HOLD")) {
                            cancelHoldJob("CANCELLED");
                        } else
                            cancelJob();
                        break;
                    case "NO ACTION":
                        dialog.dismiss();
                        break;
                    default:
                        break;

                }
            }
        });
        chooseDrinkdialog.setCustomTitle(titleview);
        chooseDrinkdialog.show();
    }

    private void cancelHoldJob(String status) {
        mProgressDilog = ProgressDialog.show(ThirdStepActivity.this, "",
                getResources().getString(R.string.loading), true, false);
        NetUtils.CallHoldCancelJob(jid, prefClass.getSavedStringPreference(SharedPrefClass.USER_ID), status, this, ThirdStepActivity.this);
    }

    private void cancelJob() {
        mProgressDilog = ProgressDialog.show(ThirdStepActivity.this, "",
                getResources().getString(R.string.loading), true, false);
        NetUtils.CallCancelJob(jid, prefClass.getSavedStringPreference(SharedPrefClass.USER_ID), this, ThirdStepActivity.this);
    }

    private void callRelunch() {
        mProgressDilog = ProgressDialog.show(ThirdStepActivity.this, "",
                getResources().getString(R.string.loading), true, false);
        NetUtils.CallRelaunch(jid, prefClass.getSavedStringPreference(SharedPrefClass.USER_ID), this, ThirdStepActivity.this);
    }


    private void callPostJobAPI() {
        mProgressDilog = ProgressDialog.show(ThirdStepActivity.this, "",
                getResources().getString(R.string.loading), true, false);
        if (jobDetailsEntity.getCar() != null) {
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("uid", prefClass.getSavedStringPreference(SharedPrefClass.USER_ID));
            map.put("jobtype", "Car");
            map.put("loclat", jobDetailsEntity.getLat());
            map.put("loclng", jobDetailsEntity.getLng());
            map.put("address", jobDetailsEntity.getAddress());
            map.put("descp", jobDetailsEntity.getDesc());
            map.put("model", jobDetailsEntity.getCar().getModel());
            map.put("color", jobDetailsEntity.getCar().getColor());
            map.put("licplateno", jobDetailsEntity.getCar().getLicense());
            map.put("licplatestate", jobDetailsEntity.getCar().getState());
            map.put("zipcode", Constants.Postal_code);
            if (jobDetailsEntity.getCar().getFile() == null)
                NetUtils.CallAddJob(map, file, this, ThirdStepActivity.this);
            else
                NetUtils.CallAddJob(map, jobDetailsEntity.getCar().getFile(), this, ThirdStepActivity.this);
        } else if (jobDetailsEntity.getHome() != null) {
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("uid", prefClass.getSavedStringPreference(SharedPrefClass.USER_ID));
            map.put("jobtype", "Home");
            map.put("loclat", jobDetailsEntity.getLat());
            map.put("loclng", jobDetailsEntity.getLng());
            map.put("address", jobDetailsEntity.getAddress());
            map.put("descp", jobDetailsEntity.getDesc());
            map.put("zipcode", Constants.Postal_code);
            if (jobDetailsEntity.getHome().getFile() == null)
                NetUtils.CallAddJob(map, file, this, ThirdStepActivity.this);
            else
                NetUtils.CallAddJob(map, jobDetailsEntity.getHome().getFile(), this, ThirdStepActivity.this);
        } else if (jobDetailsEntity.getBusiness() != null) {
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("uid", prefClass.getSavedStringPreference(SharedPrefClass.USER_ID));
            map.put("jobtype", "Business");
            map.put("loclat", jobDetailsEntity.getLat());
            map.put("loclng", jobDetailsEntity.getLng());
            map.put("address", jobDetailsEntity.getAddress());
            map.put("descp", jobDetailsEntity.getDesc());
            map.put("sizeofwork", jobDetailsEntity.getBusiness().getSize());
            map.put("zipcode", Constants.Postal_code);
            if (jobDetailsEntity.getBusiness().getFile() == null)
                NetUtils.CallAddJob(map, file, this, ThirdStepActivity.this);
            else
                NetUtils.CallAddJob(map, jobDetailsEntity.getBusiness().getFile(), this, ThirdStepActivity.this);
        }
    }

//    private void callBusinessPriceAPI() {
//        mProgressDilog = ProgressDialog.show(ThirdStepActivity.this, "",
//                getResources().getString(R.string.loading), true, false);
//        NetUtils.CallGetBusinessPrice(jobDetailsEntity.getBusiness().getSize(), this, ThirdStepActivity.this);
//    }

    @Override
    protected void onResume() {
        super.onResume();
    }

//    @Override
//    public void GetBusinessPriceCallbackSuccess(String success) {
//        mProgressDilog.dismiss();
//
//        try {
//            JSONObject jsonObject = new JSONObject(success);
//            if (jsonObject.getString("status").equals("true")) {
//                JSONObject object = jsonObject.getJSONObject("items");
//                mPriceTextView.setText("$ " + object.getString("businessprice"));
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//    }

//    @Override
//    public void GetBusinessPriceCallbackError(String error) {
//        mProgressDilog.dismiss();
//        Constants.showAlert(ThirdStepActivity.this, error);
//    }

    @Override
    public void PostJobCallbackSuccess(String success) {
        mProgressDilog.dismiss();

        try {
            JSONObject jsonObject = new JSONObject(success);
            if (jsonObject.getString("status").equals("true")) {
                startActivity(new Intent(ThirdStepActivity.this, PostJobActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void PostJobCallbackError(String error) {
        mProgressDilog.dismiss();
        Constants.showAlert(ThirdStepActivity.this, error);
    }

    @Override
    public void RelunchCallbackSuccess(String success) {
        mProgressDilog.dismiss();
        try {
            JSONObject jsonObject = new JSONObject(success);

            if (jsonObject.getString("status").equals("true")) {
                startActivity(new Intent(ThirdStepActivity.this, JobStatusActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void RelunchCallbackError(String error) {
        mProgressDilog.dismiss();
        Constants.showAlert(ThirdStepActivity.this, error);
    }

    @Override
    public void CancelJobsCallbackSuccess(String success) {
        mProgressDilog.dismiss();
        try {
            JSONObject jsonObject = new JSONObject(success);

            if (jsonObject.getString("status").equals("true")) {
                startActivity(new Intent(ThirdStepActivity.this, JobStatusActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void CancelJobsCallbackError(String error) {
        mProgressDilog.dismiss();
        Constants.showAlert(ThirdStepActivity.this, error);
    }

    @Override
    public void HoldCancelJobsCallbackSuccess(String success) {
        mProgressDilog.dismiss();
        try {
            JSONObject jsonObject = new JSONObject(success);

            if (jsonObject.getString("status").equals("true")) {
                startActivity(new Intent(ThirdStepActivity.this, JobStatusActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void HoldCancelJobsCallbackError(String error) {
        mProgressDilog.dismiss();
        Constants.showAlert(ThirdStepActivity.this, error);
    }

    @Override
    public void onResponse(int code) {
        switch (code)
        {
            case 1:
                if (!Globals.g_shovlerLat.equals("") && !Globals.g_shovlerLng.equals("")) {
                    final LatLng latLng = new LatLng(Double.valueOf(Globals.g_shovlerLat), Double.valueOf(Globals.g_shovlerLng));
                    final CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(latLng).zoom(13f).tilt(70).build();
                    ThirdStepActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (mMaker == null) {
                                mMap.addMarker(new MarkerOptions()
                                        .position(latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_shovler)));
                            } else {
                                mMaker.setPosition(latLng);
                            }
                            mMap.animateCamera(CameraUpdateFactory
                                    .newCameraPosition(cameraPosition));
                        }
                    });
                }
                break;
            case 2:
                  startActivity(new Intent(ThirdStepActivity.this, RequestorStripeActivity.class));
//                DropInRequest dropInRequest = new DropInRequest()
//                        .clientToken(Globals.g_clientToken)
//                        .collectDeviceData(true);
//                startActivityForResult(dropInRequest.getIntent(this), 10);
            case 400:
                if (jobDetailsEntity.getHome() != null) {
                    Glide.with(ThirdStepActivity.this).load(Globals.g_googlePhoto).override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                        .diskCacheStrategy(DiskCacheStrategy.RESULT).error(R.drawable.action_bar_bg).placeholder(null).into(mHomeImageView);
                }
                else if (jobDetailsEntity.getBusiness() != null)
                {
                    Glide.with(ThirdStepActivity.this).load(Globals.g_googlePhoto).override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL) // don't use target size, load full image
                            .diskCacheStrategy(DiskCacheStrategy.RESULT).error(R.drawable.action_bar_bg).placeholder(null).into(mBusinessImageView);
                }
                break;
        }
    }

    @Override
    public void onResponse(int code, int index) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (mMap != null) {
            mMap.getUiSettings().setZoomGesturesEnabled(false);
            mMap.getUiSettings().setZoomControlsEnabled(false);
            LatLng latLng = new LatLng(Double.valueOf(jobDetailsEntity.getLat()), Double.valueOf(jobDetailsEntity.getLng()));

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(latLng).zoom(13f).tilt(70).build();

            View localView = LayoutInflater.from(this).inflate(R.layout.marker_car, null);

            String title = "";

            if (jobDetailsEntity.getCar() != null)
            {

                title = "$" + JobStatusActivity.carEntity.getPrice();
                ((TextView)localView.findViewById(R.id.txtMarkerName)).setText(title);
                ((ImageView)localView.findViewById(R.id.imgMarkerIcon)).setImageResource(R.drawable.markcar);
                IconGenerator generator = new IconGenerator(this);
                generator.setBackground(null);
                generator.setContentView(localView);
                Bitmap icon = generator.makeIcon();
                BitmapDescriptor icon1 = BitmapDescriptorFactory.fromBitmap(icon);
                mMap.addMarker(new MarkerOptions().position(latLng).icon(icon1));

            }
            else if (jobDetailsEntity.getHome() != null)
            {
                title = "$" + JobStatusActivity.homeEntity.getPrice();
                ((TextView)localView.findViewById(R.id.txtMarkerName)).setText(title);
                ((ImageView)localView.findViewById(R.id.imgMarkerIcon)).setImageResource(R.drawable.markhome);
                IconGenerator generator = new IconGenerator(this);
                generator.setBackground(null);
                generator.setContentView(localView);
                Bitmap icon = generator.makeIcon();
                BitmapDescriptor icon1 = BitmapDescriptorFactory.fromBitmap(icon);
                mMap.addMarker(new MarkerOptions().position(latLng).icon(icon1));


            }
            else if (jobDetailsEntity.getBusiness() != null)
            {
                title = "$" + JobStatusActivity.businessEntity.getPrice();
                ((TextView)localView.findViewById(R.id.txtMarkerName)).setText(title);
                ((ImageView)localView.findViewById(R.id.imgMarkerIcon)).setImageResource(R.drawable.markstore);
                IconGenerator generator = new IconGenerator(this);
                generator.setBackground(null);
                generator.setContentView(localView);
                Bitmap icon = generator.makeIcon();
                BitmapDescriptor icon1 = BitmapDescriptorFactory.fromBitmap(icon);
                mMap.addMarker(new MarkerOptions().position(latLng).icon(icon1));


            }
            startTracking();
        }
    }

    public void startTracking()
    {
        new Thread()
        {
            public void run()
            {
                while(isTrack)
                {
                    try {
                        ServiceManager.getShovlerLocation(jid,ThirdStepActivity.this);
                        sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode ==10) {
            if (resultCode == Activity.RESULT_OK) {
                DropInResult result = data.getParcelableExtra(DropInResult.EXTRA_DROP_IN_RESULT);
                int i = 0;
                // use the result to update your UI and send the payment method nonce to your server
            } else if (resultCode == Activity.RESULT_CANCELED) {
                // the user canceled
            } else {
                // handle errors here, an exception may be available in
                Exception error = (Exception) data.getSerializableExtra(DropInActivity.EXTRA_ERROR);
            }
        }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.layoutChatShovlerRequester:
            //case R.id.imgRequestJobChat:
                Globals.g_currentJobID = jid;
                Intent m = new Intent(this,ChatActivity.class);
                this.startActivity(m);
                break;
        }
    }
}
