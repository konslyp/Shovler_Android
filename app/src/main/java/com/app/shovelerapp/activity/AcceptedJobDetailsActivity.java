package com.app.shovelerapp.activity;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.shovelerapp.R;
import com.app.shovelerapp.callback.CancelJobCallback;
import com.app.shovelerapp.doc.Globals;
import com.app.shovelerapp.model.RequestorJobModel;
import com.app.shovelerapp.netutils.NetUtils;
import com.app.shovelerapp.service.GPSTracker;
import com.app.shovelerapp.service.ILoadService;
import com.app.shovelerapp.service.ServiceManager;
import com.app.shovelerapp.utils.Constants;
import com.app.shovelerapp.utils.SharedPrefClass;
import com.app.shovelerapp.utils.Utils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
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


/**
 * Created by supriya.n on 15-06-2016.
 */
public class AcceptedJobDetailsActivity extends AppCompatActivity implements OnMapReadyCallback, CancelJobCallback,View.OnClickListener,ILoadService {
    private Button mCancelJob, mFinishWork;
    private TextView mLocation, mLocationValue, mRequest, mRequestVal, mPrice, mPriceValue, mSpecialinstructionvalue, mSpecialinstructiontitle;
    private LinearLayout mCarLayout, mHomeLayout, mBusinessLayout;
    private TextView mCarModel, mCarPlateNo, mCarState;
    private ImageView mSubLogo,mCarColor;

    private ImageView mCarImageView, mHomeImageView, mBusinessImageView;
    private StreetViewPanorama mCarImageViewPanorama, mHomeImageViewPanorama, mBusinessImageViewPanorama;
    private TextView mCarMask, mHomeMask, mBusinessMask;

    private RelativeLayout mCarLayoutLayout, mHomeLayoutLayout, mBusinessLayoutLayout;
    private SharedPrefClass prefClass;
    private TextView mBusinessSize,mShowMap;
    public TextView txtExpectTime;
    private TextView mTitle;
    private int position = 0;
    private String ActivityFlag;
    private SupportMapFragment mapFragment;
    private GoogleMap mMap;
    private GPSTracker getTracker;
    private RequestorJobModel jobModel;

    private GPSTracker tracker;
    private ProgressDialog mProgressDialog;
    private String reason;

    private boolean isTrack = true;
    private Marker mMaker = null;
    //public ImageView imgChatIcon;
    public LinearLayout layoutChat;

    @Override
    protected void onDestroy() {
        isTrack = false;
        super.onDestroy();

    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_accepted_job);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getTracker=new GPSTracker(AcceptedJobDetailsActivity.this);

        tracker = new GPSTracker(AcceptedJobDetailsActivity.this);

        mapFragment = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(
                R.id.map));
        mapFragment.getMapAsync(this);

        prefClass = new SharedPrefClass(AcceptedJobDetailsActivity.this);

        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mSubLogo = (ImageView) toolbar.findViewById(R.id.logo_image);
        mTitle = (TextView) toolbar.findViewById(R.id.title);

        mShowMap= (TextView) findViewById(R.id.show_map);
        mSubLogo.setImageResource(R.drawable.shoveler);
        mTitle.setText("CURRENT JOB");

        position = getIntent().getExtras().getInt("position");
        ActivityFlag = getIntent().getExtras().getString("ActivityFlag");
        if (ActivityFlag.equals("Job")) {
            jobModel = JobStatusActivity.jobModels.get(position);
        } else {
            jobModel = AvailableJobActivity.models.get(position);
        }


        mFinishWork = (Button) findViewById(R.id.finish_work);
        mCancelJob = (Button) findViewById(R.id.cancel_job);

        mLocation = (TextView) findViewById(R.id.location_title);
        mLocationValue = (TextView) findViewById(R.id.location_val);
        mRequest = (TextView) findViewById(R.id.request);
        mRequestVal = (TextView) findViewById(R.id.request_value);
        mPrice = (TextView) findViewById(R.id.price_title);
        mPriceValue = (TextView) findViewById(R.id.price_value);

        //imgChatIcon = (ImageView) findViewById(R.id.imgJobChat);
        layoutChat = (LinearLayout) findViewById(R.id.layoutChatShovler);

        mCarImageView = (ImageView) findViewById(R.id.car_iv);
        mHomeImageView = (ImageView) findViewById(R.id.home_iv);
        mBusinessImageView = (ImageView) findViewById(R.id.business_iv);

        mCarMask = (TextView) findViewById(R.id.mask_0);
        mHomeMask = (TextView) findViewById(R.id.mask_1);
        mBusinessMask = (TextView) findViewById(R.id.mask_2);




        //imgChatIcon.setOnClickListener(this);
        layoutChat.setOnClickListener(this);

        mCarImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent m = new Intent(AcceptedJobDetailsActivity.this,ImageShowActivity.class);
                if (!jobModel.getJobpic().endsWith("openjobpics/"))
                    m.putExtra("path",jobModel.getJobpic());
                else
                    m.putExtra("path",Globals.g_googlePhoto);
                AcceptedJobDetailsActivity.this.startActivity(m);
            }
        });

        mBusinessImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent m = new Intent(AcceptedJobDetailsActivity.this,ImageShowActivity.class);
                if (!jobModel.getJobpic().endsWith("openjobpics/"))
                    m.putExtra("path",jobModel.getJobpic());
                else
                    m.putExtra("path",Globals.g_googlePhoto);
                AcceptedJobDetailsActivity.this.startActivity(m);
            }
        });

        mHomeImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent m = new Intent(AcceptedJobDetailsActivity.this,ImageShowActivity.class);
                if (!jobModel.getJobpic().endsWith("openjobpics/"))
                    m.putExtra("path",jobModel.getJobpic());
                else
                    m.putExtra("path",Globals.g_googlePhoto);
                AcceptedJobDetailsActivity.this.startActivity(m);
            }
        });

        mCarModel = (TextView) findViewById(R.id.model);
        mCarColor = (ImageView) findViewById(R.id.color_val);
        mCarPlateNo = (TextView) findViewById(R.id.plate_val);
        mCarState = (TextView) findViewById(R.id.state_val);

        mBusinessSize = (TextView) findViewById(R.id.size_value);

        mSpecialinstructiontitle = (TextView) findViewById(R.id.spe_title);
        mSpecialinstructionvalue = (TextView) findViewById(R.id.special_instruction_val);
        //mSpecialInstructionedit = (EditText) findViewById(R.id.extra_instruction_edit);

        mCarLayout = (LinearLayout) findViewById(R.id.car_layout);
        mHomeLayout = (LinearLayout) findViewById(R.id.home_layout);
        mBusinessLayout = (LinearLayout) findViewById(R.id.business_layout);

        mCarLayoutLayout= (RelativeLayout) findViewById(R.id.car_upload_image_layout);
        mHomeLayoutLayout= (RelativeLayout) findViewById(R.id.home_upload_image_layout);
        mBusinessLayoutLayout= (RelativeLayout) findViewById(R.id.business_upload_image_layout);
        txtExpectTime = (TextView) findViewById(R.id.txtExpectTimeArrive);

        setFont();

        mRequestVal.setText(jobModel.getJobtype());
        mLocationValue.setText(jobModel.getAddress());

        if (!jobModel.getDescp().equals(""))
            mSpecialinstructionvalue.setText(jobModel.getDescp());
        else mSpecialinstructionvalue.setText("No instruction needed");

        txtExpectTime.setText(jobModel.esptime);

        mShowMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                        Uri.parse("http://maps.google.com/maps?saddr="+getTracker.getLatitude()+","+getTracker.getLongitude()+"&daddr="+jobModel.getLoclat()+","+jobModel.getLoclng()));
                startActivity(intent);
            }
        });

        if (jobModel.getJobtype().equals("Car")) {
            mCarLayout.setVisibility(View.VISIBLE);
            mHomeLayout.setVisibility(View.GONE);
            mBusinessLayout.setVisibility(View.GONE);

            mHomeMask.setVisibility(View.GONE);
            mBusinessMask.setVisibility(View.GONE);
            mCarMask.setVisibility(View.GONE);

            mCarModel.setText(jobModel.getModel());
            mCarColor.setBackgroundColor(Color.parseColor(jobModel.getColor()));
            mCarPlateNo.setText(jobModel.getLicplateno());
            mCarState.setText(jobModel.getLicplatestate());
            mPriceValue.setText("$ " + jobModel.getPrice());

            if (!jobModel.getJobpic().endsWith("openjobpics/")) {
                Glide.with(AcceptedJobDetailsActivity.this).load(jobModel.getJobpic()).override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL) // don't use target size, load full image
                        .diskCacheStrategy(DiskCacheStrategy.RESULT).error(R.drawable.action_bar_bg).placeholder(null).into(mCarImageView);
                // /mCarImageView.setImageURI(Uri.parse(jobModel.getJobpic()));
            } else {
                //Utils.getImageFromGoogle(jobModel.getLoclat(),jobModel.getLoclng());
                //Globals.g_googlePhoto = jobModel.getJobpic();

                mCarImageView.setImageResource(R.drawable.action_bar_bg);

//                Glide.with(AcceptedJobDetailsActivity.this).load(Globals.g_googlePhoto).override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL) // don't use target size, load full image
//                        .diskCacheStrategy(DiskCacheStrategy.RESULT).error(R.drawable.action_bar_bg).placeholder(null).into(mCarImageView);
                //mCarImageView.setVisibility(View.GONE);
                //mCarLayoutLayout.setVisibility(View.GONE);
            }


        } else if (jobModel.getJobtype().equals("Home")) {
            mHomeLayout.setVisibility(View.VISIBLE);
            mCarLayout.setVisibility(View.GONE);
            mBusinessLayout.setVisibility(View.GONE);

            mPriceValue.setText("$ " + jobModel.getPrice());

            if (!jobModel.getJobpic().endsWith("openjobpics/")) {
                //mHomeImageView.setImageURI(Uri.parse(jobModel.getJobpic()));
                mHomeImageView.setVisibility(View.VISIBLE);
                mHomeMask.setVisibility(View.VISIBLE);
                Glide.with(AcceptedJobDetailsActivity.this).load(jobModel.getJobpic()).override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL) // don't use target size, load full image
                        .diskCacheStrategy(DiskCacheStrategy.RESULT).error(R.drawable.action_bar_bg).placeholder(null).into(mHomeImageView);

            } else {
                //mHomeImageView.setVisibility(View.GONE);
                //mHomeLayoutLayout.setVisibility(View.GONE);
                mHomeImageView.setVisibility(View.GONE);
                mHomeMask.setVisibility(View.GONE);
                Utils.getImageFromGoogle(jobModel.getLoclat(),jobModel.getLoclng());
                String location = jobModel.getLoclat() + "," + jobModel.getLoclng();
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
                                mHomeImageViewPanorama.setPosition(new LatLng(Double.parseDouble(jobModel.getLoclat()),Double.parseDouble(jobModel.getLoclng())));

                                // Only set the panorama to SAN_FRAN on startup (when no panoramas have been
                                // loaded which is when the savedInstanceState is null).
//                        if (savedInstanceState == null) {
//                            //mStreetViewPanorama.setPosition("1320 Trafalgar st. Teaneck NJ 07666");
//                            mCarImageViewPanorama.setPosition(SAN_FRAN);
//                        }
                            }
                        });
            }

        } else if (jobModel.getJobtype().equals("Business")) {
            mBusinessLayout.setVisibility(View.VISIBLE);
            mHomeLayout.setVisibility(View.GONE);
            mCarLayout.setVisibility(View.GONE);

            mPriceValue.setText("$ " + jobModel.getPrice());

            if (jobModel.getSizeofwork() != null) {
                mBusinessSize.setText(jobModel.getSizeofwork() + " ft²");
            }else {
                mBusinessSize.setText("12" + " ft²");
            }

            if (!jobModel.getJobpic().endsWith("openjobpics/")) {
                mBusinessImageView.setVisibility(View.VISIBLE);
                mBusinessMask.setVisibility(View.VISIBLE);
                Glide.with(AcceptedJobDetailsActivity.this).load(jobModel.getJobpic()).override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL) // don't use target size, load full image
                        .diskCacheStrategy(DiskCacheStrategy.RESULT).error(R.drawable.action_bar_bg).placeholder(null).into(mBusinessImageView);

                getSupportFragmentManager().findFragmentById(R.id.business_iv_panorama).setUserVisibleHint(false);

            } else {
                //mBusinessImageView.setVisibility(View.GONE);
                //mBusinessImageView.setVisibility(View.GONE);
                Utils.getImageFromGoogle(jobModel.getLoclat(),jobModel.getLoclng());
                mBusinessImageView.setVisibility(View.GONE);
                mBusinessMask.setVisibility(View.GONE);
                String location = jobModel.getLoclat() + "," + jobModel.getLoclng();
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
                                mBusinessImageViewPanorama.setPosition(new LatLng(Double.parseDouble(jobModel.getLoclat()),Double.parseDouble(jobModel.getLoclng())));
                            }
                        });


            }
        }

        mFinishWork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showDialogAreYouSure();
            }

        });

        mCancelJob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogWhyCancelJob();
            }
        });

//        mCarImageView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ImageView mZoomImageView;
//                final Dialog dialog = new Dialog(AcceptedJobDetailsActivity.this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
//                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//                dialog.setCancelable(true);
//                dialog.setContentView(R.layout.zoom_image);
//                mZoomImageView = (ImageView) dialog.findViewById(R.id.image);
//                Glide.with(AcceptedJobDetailsActivity.this).load(jobModel.getJobpic()).placeholder(null).error(R.drawable.no_image_1).into(mZoomImageView);
//                dialog.show();
//            }
//        });
//
//        mHomeImageView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ImageView mZoomImageView;
//                final Dialog dialog = new Dialog(AcceptedJobDetailsActivity.this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
//                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//                dialog.setCancelable(true);
//                dialog.setContentView(R.layout.zoom_image);
//                mZoomImageView = (ImageView) dialog.findViewById(R.id.image);
//                Glide.with(AcceptedJobDetailsActivity.this).load(jobModel.getJobpic()).placeholder(null).error(R.drawable.no_image_1).into(mZoomImageView);
//                dialog.show();
//            }
//        });
//
//        mBusinessImageView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ImageView mZoomImageView;
//                final Dialog dialog = new Dialog(AcceptedJobDetailsActivity.this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
//                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//                dialog.setCancelable(true);
//                dialog.setContentView(R.layout.zoom_image);
//                mZoomImageView = (ImageView) dialog.findViewById(R.id.image);
//                Glide.with(AcceptedJobDetailsActivity.this).load(jobModel.getJobpic()).placeholder(null).error(R.drawable.no_image_1).into(mZoomImageView);
//                dialog.show();
//            }
//        });
    }


    private void setFont() {
        Typeface tfRegular = Constants.setRegularLatoFont(AcceptedJobDetailsActivity.this);
        Typeface tfLight = Constants.setLightLatoFont(AcceptedJobDetailsActivity.this);
        Typeface tfThin = Constants.setThinLatoFont(AcceptedJobDetailsActivity.this);

        mFinishWork.setTypeface(tfRegular);
        mCancelJob.setTypeface(tfRegular);

        mLocation.setTypeface(tfRegular);
        mLocationValue.setTypeface(tfRegular);
        mRequest.setTypeface(tfRegular);
        mRequestVal.setTypeface(tfRegular);
        mPrice.setTypeface(tfRegular);
        mPriceValue.setTypeface(tfRegular);
        mSpecialinstructiontitle.setTypeface(tfRegular);
        mSpecialinstructionvalue.setTypeface(tfRegular);
        //mSpecialInstructionedit.setTypeface(tfRegular);


    }

    private void showDialogWhyCancelJob() {
        final TextView mReasonTextView1, mReasonTextView2, mReasonTextView3, mReasonTextView4, mReasonTextView5;
        final Dialog dialog = new Dialog(AcceptedJobDetailsActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.why_cancel_dialog);
        mReasonTextView1 = (TextView) dialog.findViewById(R.id.reason1);
        mReasonTextView2 = (TextView) dialog.findViewById(R.id.reason2);
        mReasonTextView3 = (TextView) dialog.findViewById(R.id.reason3);
        mReasonTextView4 = (TextView) dialog.findViewById(R.id.reason4);
        mReasonTextView5 = (TextView) dialog.findViewById(R.id.reason5);

        mReasonTextView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reason = mReasonTextView1.getText().toString();
                // callCancelAPI();
                dialog.dismiss();
                showCancelDialog();

            }
        });

        mReasonTextView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                reason = mReasonTextView2.getText().toString();
                showCancelDialog();
                //    callCancelAPI(mReasonTextView2.getText().toString());
            }
        });

        mReasonTextView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                reason = mReasonTextView3.getText().toString();
                showCancelDialog();
                // callCancelAPI(mReasonTextView3.getText().toString());
            }
        });

        mReasonTextView4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                reason = mReasonTextView4.getText().toString();
                showCancelDialog();
                //callCancelAPI(mReasonTextView4.getText().toString());
            }
        });

        mReasonTextView5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                reason = mReasonTextView5.getText().toString();
                showCancelDialog();
                //callCancelAPI(mReasonTextView5.getText().toString());
            }
        });

        dialog.show();

    }

    private void callCancelAPI() {
        mProgressDialog = ProgressDialog.show(AcceptedJobDetailsActivity.this, "",
                getResources().getString(R.string.loading), true, false);

        NetUtils.CallCancelJob(jobModel.getJid(), prefClass.getSavedStringPreference(SharedPrefClass.USER_ID), reason, this, AcceptedJobDetailsActivity.this);
    }

    private void showCancelDialog() {
        TextView mOk, mUndo;
        final Dialog dialog = new Dialog(AcceptedJobDetailsActivity.this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.cancel_undo_button);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.parseColor("#d4c2272d")));
        mOk = (TextView) dialog.findViewById(R.id.ok_tv);
        mUndo = (TextView) dialog.findViewById(R.id.undo_tv);

        mUndo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        mOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                callCancelAPI();
                /*Fragment fragment = new JobCancelFragment();
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.add(R.id.container, fragment, "JobCancelFragment");
                fragmentTransaction.addToBackStack(JobCancelFragment.class.getName());
                fragmentTransaction.commit();
                dialog.dismiss();*/
            }
        });
        dialog.show();
    }

    public void showDialogAreYouSure() {
        TextView mFinishWorkTv, mCancelTv;
        final Dialog dialog = new Dialog(AcceptedJobDetailsActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.r_u_sure_dialog);

        mFinishWorkTv = (TextView) dialog.findViewById(R.id.finish_work);
        mCancelTv = (TextView) dialog.findViewById(R.id.cancel);

        mFinishWorkTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                showActionDialog();
            }

        });

        mCancelTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();

    }

    private void showActionDialog() {
        TextView mOk;
        final Dialog dialog = new Dialog(AcceptedJobDetailsActivity.this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.action_screen);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.parseColor("#df8cc63e")));
        mOk = (TextView) dialog.findViewById(R.id.ok_tv);
        mOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AcceptedJobDetailsActivity.this, UploadPhotoNotification.class).putExtra("jid", jobModel.getJid()).putExtra("ajuid", jobModel.getAjuid()));
            }
        });
        dialog.show();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(false);
        mMap.getUiSettings().setZoomGesturesEnabled(false);

        LatLng latLng = new LatLng(Double.valueOf(jobModel.getLoclat()), Double.valueOf(jobModel.getLoclng()));

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(latLng).zoom(13f).tilt(70).build();





        //mMap.setMyLocationEnabled(true);
        //mMap.getUiSettings().setMyLocationButtonEnabled(true);

        View localView = LayoutInflater.from(this).inflate(R.layout.marker_car, null);

        String title = "$" + String.valueOf((int)Double.parseDouble(jobModel.getPrice()));

        if (jobModel.getJobtype().toLowerCase().equals("car"))
        {
            ((TextView)localView.findViewById(R.id.txtMarkerName)).setText(title);
            ((ImageView)localView.findViewById(R.id.imgMarkerIcon)).setImageResource(R.drawable.markcar);
            IconGenerator generator = new IconGenerator(this);
            generator.setBackground(null);
            generator.setContentView(localView);
            Bitmap icon = generator.makeIcon();
            BitmapDescriptor icon1 = BitmapDescriptorFactory.fromBitmap(icon);
            mMap.addMarker(new MarkerOptions().position(latLng).icon(icon1));

//            mMap.addMarker(new MarkerOptions()
//                    .position(latLng)
//                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.markcar))
//                    .title(jobModel.getAddress()));
        }
        else if (jobModel.getJobtype().toLowerCase().equals("home"))
        {

            ((TextView)localView.findViewById(R.id.txtMarkerName)).setText(title);
            ((ImageView)localView.findViewById(R.id.imgMarkerIcon)).setImageResource(R.drawable.markhome);
            IconGenerator generator = new IconGenerator(this);
            generator.setBackground(null);
            generator.setContentView(localView);
            Bitmap icon = generator.makeIcon();
            BitmapDescriptor icon1 = BitmapDescriptorFactory.fromBitmap(icon);
            mMap.addMarker(new MarkerOptions().position(latLng).icon(icon1));


//            mMap.addMarker(new MarkerOptions()
//                    .position(latLng)
//                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.markhome))
//                    .title(jobModel.getAddress()));
        }
        else if (jobModel.getJobtype().toLowerCase().equals("business"))
        {

            ((TextView)localView.findViewById(R.id.txtMarkerName)).setText(title);
            ((ImageView)localView.findViewById(R.id.imgMarkerIcon)).setImageResource(R.drawable.markstore);
            IconGenerator generator = new IconGenerator(this);
            generator.setBackground(null);
            generator.setContentView(localView);
            Bitmap icon = generator.makeIcon();
            BitmapDescriptor icon1 = BitmapDescriptorFactory.fromBitmap(icon);
            mMap.addMarker(new MarkerOptions().position(latLng).icon(icon1));


//            mMap.addMarker(new MarkerOptions()
//                    .position(latLng)
//                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.markstore))
//                    .title(jobModel.getAddress()));
        }

        //mMap.animateCamera(CameraUpdateFactory
        //        .newCameraPosition(cameraPosition));
        startTracking();
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
                        final LatLng latLng = new LatLng(Double.valueOf(tracker.getLatitude()), Double.valueOf(tracker.getLongitude()));
                        final CameraPosition cameraPosition = new CameraPosition.Builder()
                                .target(latLng).zoom(13f).tilt(70).build();
                        AcceptedJobDetailsActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (mMaker == null)
                                {
                                    mMap.addMarker(new MarkerOptions()
                                            .position(latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_shovler)));
                                }
                                else
                                {
                                    mMaker.setPosition(latLng);
                                }
                                mMap.animateCamera(CameraUpdateFactory
                                        .newCameraPosition(cameraPosition));
                            }
                        });

                        sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }
    @Override
    public void CancelJobCallbackSuccess(String success) {
        mProgressDialog.dismiss();

        try {
            JSONObject jsonObject = new JSONObject(success);
            if (jsonObject.getString("status").equals("true")) {
                startActivity(new Intent(AcceptedJobDetailsActivity.this, JobCancelActivity.class).putExtra("pic",jobModel.getJobpic()).putExtra("price",jobModel.getPrice()).putExtra("title",jobModel.getJobtype()).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
            } else {
                String msg = jsonObject.getString("items");
                Constants.showAlert(AcceptedJobDetailsActivity.this, msg);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void CancelJobCallbackError(String error) {
        mProgressDialog.dismiss();
        Constants.showAlert(AcceptedJobDetailsActivity.this, error);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.layoutChatShovler:
                Globals.g_currentJobID = jobModel.getJid();
                Intent m = new Intent(this,ChatActivity.class);
                this.startActivity(m);
                break;
        }
    }

    @Override
    public void onResponse(int code) {
        switch (code)
        {
            case 400:
                if (jobModel.getJobtype().equals("Home"))
                {
                    Glide.with(AcceptedJobDetailsActivity.this).load(Globals.g_googlePhoto).override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL) // don't use target size, load full image
                            .diskCacheStrategy(DiskCacheStrategy.RESULT).error(R.drawable.action_bar_bg).placeholder(null).into(mHomeImageView);
                }
                else if (jobModel.getJobtype().equals("Business"))
                {
                    Glide.with(AcceptedJobDetailsActivity.this).load(Globals.g_googlePhoto).override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL) // don't use target size, load full image
                            .diskCacheStrategy(DiskCacheStrategy.RESULT).error(R.drawable.action_bar_bg).placeholder(null).into(mBusinessImageView);
                }
                break;
        }
    }

    @Override
    public void onResponse(int code, int index) {

    }
}
