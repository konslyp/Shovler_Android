package com.app.shovelerapp.activity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.app.shovelerapp.R;
import com.app.shovelerapp.adapter.ImagePagerAdapter;
import com.app.shovelerapp.callback.AcceptJobCallback;
import com.app.shovelerapp.doc.Globals;
import com.app.shovelerapp.netutils.NetUtils;
import com.app.shovelerapp.service.ILoadService;
import com.app.shovelerapp.service.ServiceManager;
import com.app.shovelerapp.utils.CirclePageIndicator;
import com.app.shovelerapp.utils.Constants;
import com.app.shovelerapp.utils.SharedPrefClass;
import com.app.shovelerapp.utils.Utils;
import com.bigkoo.pickerview.TimePickerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.Target;
import com.github.jjobes.slidedatetimepicker.SlideDateTimeListener;
import com.github.jjobes.slidedatetimepicker.SlideDateTimePicker;
import com.google.android.gms.maps.OnStreetViewPanoramaReadyCallback;
import com.google.android.gms.maps.StreetViewPanorama;
import com.google.android.gms.maps.SupportStreetViewPanoramaFragment;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by supriya.n on 14-06-2016.
 */
public class AvailableJobDetailsActivity extends FragmentActivity implements AcceptJobCallback,ILoadService {

    private Button mAcceptJob, mIgnoreJob;
    private TextView mLocation, mLocationValue, mRequest, mRequestVal, mPrice, mPriceValue, mSpecialinstructionvalue, mSpecialinstructiontitle;

    private LinearLayout mCarLayout, mHomeLayout, mBusinessLayout;
    private RelativeLayout mCarLayoutLayout, mHomeLayoutLayout, mBusinessLayoutLayout;

    private StreetViewPanorama  mHomeImageViewPanorama, mBusinessImageViewPanorama;
    private TextView  mHomeMask, mBusinessMask;

    private TextView mCarModel, mCarPlateNo, mCarState;
    private ImageView mSubLogo, mCarColor;
    private ImageView mCarImageView, mHomeImageView, mBusinessImageView;
    private SharedPrefClass prefClass;
    private TextView mBusinessSize;
    private TextView mTitle;
    private int position = 0;
    ProgressDialog mProgressDialog;
    private int startHour;
    private int startMinue;
    public AlertDialog.Builder chooseTimeDialog;
    TextView message;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_available_job_details);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        //getSupportActionBar().setDisplayShowHomeEnabled(false);
        //getSupportActionBar().setDisplayShowTitleEnabled(false);

        mSubLogo = (ImageView) toolbar.findViewById(R.id.logo_image);
        mTitle = (TextView) toolbar.findViewById(R.id.title);

        mSubLogo.setImageResource(R.drawable.shoveler);
        mTitle.setText("AVAILABLE JOBS");

        position = getIntent().getExtras().getInt("position");

        prefClass = new SharedPrefClass(AvailableJobDetailsActivity.this);

        mAcceptJob = (Button) findViewById(R.id.accept_job);
        mIgnoreJob = (Button) findViewById(R.id.ignore_job);

        mLocation = (TextView) findViewById(R.id.location_title);
        mLocationValue = (TextView) findViewById(R.id.location_val);
        mRequest = (TextView) findViewById(R.id.request);
        mRequestVal = (TextView) findViewById(R.id.request_value);
        mPrice = (TextView) findViewById(R.id.price_title);
        mPriceValue = (TextView) findViewById(R.id.price_value);

        mCarImageView = (ImageView) findViewById(R.id.car_iv);
        mHomeImageView = (ImageView) findViewById(R.id.home_iv);
        mBusinessImageView = (ImageView) findViewById(R.id.business_iv);

        mCarImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent m = new Intent(AvailableJobDetailsActivity.this,ImageShowActivity.class);
                AvailableJobDetailsActivity.this.startActivity(m);
            }
        });
        mHomeImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent m = new Intent(AvailableJobDetailsActivity.this,ImageShowActivity.class);
                if (!AvailableJobActivity.models.get(position).getJobpic().endsWith("openjobpics/"))
                    m.putExtra("path",AvailableJobActivity.models.get(position).getJobpic());
                else
                    m.putExtra("path",Globals.g_googlePhoto);
                AvailableJobDetailsActivity.this.startActivity(m);
            }
        });

        mBusinessImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent m = new Intent(AvailableJobDetailsActivity.this,ImageShowActivity.class);
                if (!AvailableJobActivity.models.get(position).getJobpic().endsWith("openjobpics/"))
                    m.putExtra("path",AvailableJobActivity.models.get(position).getJobpic());
                else
                    m.putExtra("path",Globals.g_googlePhoto);
                AvailableJobDetailsActivity.this.startActivity(m);
            }
        });

        final Calendar c = Calendar.getInstance();
        startHour = c.get(Calendar.HOUR_OF_DAY);
        startMinue = c.get(Calendar.MINUTE);

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

        mCarLayoutLayout = (RelativeLayout) findViewById(R.id.car_upload_image_layout);
        mHomeLayoutLayout = (RelativeLayout) findViewById(R.id.home_upload_image_layout);
        mBusinessLayoutLayout = (RelativeLayout) findViewById(R.id.business_upload_image_layout);


        mHomeMask = (TextView) findViewById(R.id.mask_1);
        mBusinessMask = (TextView) findViewById(R.id.mask_2);

        setFont();

        mRequestVal.setText(AvailableJobActivity.models.get(position).getJobtype());
        mLocationValue.setText("" + String.format("%.2f", Double.valueOf(AvailableJobActivity.models.get(position).getDistance())) + " miles away");
        mSpecialinstructionvalue.setText(AvailableJobActivity.models.get(position).getDescp());
        if (AvailableJobActivity.models.get(position).getDescp() == null || AvailableJobActivity.models.get(position).getDescp().equals("")) {
            mSpecialinstructionvalue.setText("No instructions entered");
        }
        if (AvailableJobActivity.models.get(position).getJobtype().equals("Car")) {
            mCarLayout.setVisibility(View.VISIBLE);
            mHomeLayout.setVisibility(View.GONE);
            mBusinessLayout.setVisibility(View.GONE);

            mCarModel.setText(AvailableJobActivity.models.get(position).getModel());
            mCarColor.setBackgroundColor(Color.parseColor(AvailableJobActivity.models.get(position).getColor()));
            mCarPlateNo.setText(AvailableJobActivity.models.get(position).getLicplateno());
            mCarState.setText(AvailableJobActivity.models.get(position).getLicplatestate());
            mPriceValue.setText("$ " + AvailableJobActivity.models.get(position).getPrice());


            //if (AvailableJobActivity.models.get(position).getJobpic() != null) {
            if (!AvailableJobActivity.models.get(position).getJobpic().endsWith("openjobpics/")) {
                Glide.with(AvailableJobDetailsActivity.this).load(AvailableJobActivity.models.get(position).getJobpic()).override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL) // don't use target size, load full image
                        .diskCacheStrategy(DiskCacheStrategy.RESULT).error(R.drawable.action_bar_bg).placeholder(null).into(mCarImageView);
                // /mCarImageView.setImageURI(Uri.parse(AvailableJobActivity.models.get(position).getJobpic()));
            } else {
                //getGooglePlacePicture(AvailableJobActivity.models.get(position).getLoclat(),AvailableJobActivity.models.get(position).getLoclng());
                //mCarImageView.setVisibility(View.GONE);
                //mCarLayoutLayout.setVisibility(View.GONE);
                mCarImageView.setImageResource(R.drawable.action_bar_bg);
            }


        } else if (AvailableJobActivity.models.get(position).getJobtype().equals("Home")) {
            mHomeLayout.setVisibility(View.VISIBLE);
            mCarLayout.setVisibility(View.GONE);
            mBusinessLayout.setVisibility(View.GONE);

            mPriceValue.setText("$ " + AvailableJobActivity.models.get(position).getPrice());

            if (!AvailableJobActivity.models.get(position).getJobpic().endsWith("openjobpics/") && !AvailableJobActivity.models.get(position).getJobpic().startsWith("http://cbk0.google.com/")){
                //mHomeImageView.setImageURI(Uri.parse(AvailableJobActivity.models.get(position).getJobpic()));
                mHomeImageView.setVisibility(View.VISIBLE);
                mHomeMask.setVisibility(View.VISIBLE);

                Glide.with(AvailableJobDetailsActivity.this).load(AvailableJobActivity.models.get(position).getJobpic()).override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL) // don't use target size, load full image
                        .diskCacheStrategy(DiskCacheStrategy.RESULT).error(R.drawable.action_bar_bg).placeholder(null).into(mHomeImageView);
            } else {
                mHomeImageView.setVisibility(View.GONE);
                mHomeMask.setVisibility(View.GONE);
                Utils.getImageFromGoogle(AvailableJobActivity.models.get(position).getLoclat(),AvailableJobActivity.models.get(position).getLoclng());
                String location = AvailableJobActivity.models.get(position).getLoclat() + "," + AvailableJobActivity.models.get(position).getLoclng();
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
                                mHomeImageViewPanorama.setPosition(new LatLng(Double.parseDouble(AvailableJobActivity.models.get(position).getLoclat()),Double.parseDouble(AvailableJobActivity.models.get(position).getLoclng())));

                                // Only set the panorama to SAN_FRAN on startup (when no panoramas have been
                                // loaded which is when the savedInstanceState is null).
//                        if (savedInstanceState == null) {
//                            //mStreetViewPanorama.setPosition("1320 Trafalgar st. Teaneck NJ 07666");
//                            mCarImageViewPanorama.setPosition(SAN_FRAN);
//                        }
                            }
                        });

            }

        } else if (AvailableJobActivity.models.get(position).getJobtype().equals("Business")) {
            mBusinessLayout.setVisibility(View.VISIBLE);
            mHomeLayout.setVisibility(View.GONE);
            mCarLayout.setVisibility(View.GONE);

            mPriceValue.setText("$ " + AvailableJobActivity.models.get(position).getPrice());

            mBusinessSize.setText(AvailableJobActivity.models.get(position).getSizeofwork() + " ft²");

            if (!AvailableJobActivity.models.get(position).getJobpic().endsWith("openjobpics/")  && !AvailableJobActivity.models.get(position).getJobpic().startsWith("http://cbk0.google.com/")){

                mBusinessImageView.setVisibility(View.VISIBLE);
                mBusinessMask.setVisibility(View.VISIBLE);

                Glide.with(AvailableJobDetailsActivity.this).load(AvailableJobActivity.models.get(position).getJobpic()).override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL) // don't use target size, load full image
                        .diskCacheStrategy(DiskCacheStrategy.RESULT).error(R.drawable.action_bar_bg).placeholder(null).into(mBusinessImageView);

            } else {

                //Utils.getImageFromGoogle(AvailableJobActivity.models.get(position).getLoclat(),AvailableJobActivity.models.get(position).getLoclng());
                mBusinessImageView.setVisibility(View.GONE);
                mBusinessMask.setVisibility(View.GONE);
                String location = AvailableJobActivity.models.get(position).getLoclat() + "," + AvailableJobActivity.models.get(position).getLoclng();
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
                                mBusinessImageViewPanorama.setPosition(new LatLng(Double.parseDouble(AvailableJobActivity.models.get(position).getLoclat()),Double.parseDouble(AvailableJobActivity.models.get(position).getLoclng())));
                            }
                        });


                //mBusinessImageView.setVisibility(View.GONE);
                //mBusinessLayoutLayout.setVisibility(View.GONE);
            }
        }

        mAcceptJob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Globals.g_dutyStatus.equals("1"))
                {
                    Toast.makeText(AvailableJobDetailsActivity.this,"Please go on duty so you can accept job",Toast.LENGTH_SHORT).show();
                    return;
                }


                ///
                /*String strArrive = editArrivalTime.getText().toString();
                if (strArrive.equals(""))
                {
                    setErrorFun(editArrivalTime, getResources().getString(R.string.enter_arrival));
                    return;
                }*/

                //if (prefClass.getSavedStringPreference(SharedPrefClass.API_KEY).equals("")) {
                if (!prefClass.getSavedStringPreference(SharedPrefClass.API_KEY).equals("")) {
//                    CharSequence[] buttonItems = {"Insert Key", "Register for Stripe", "Cancel"};

                    AlertDialog.Builder builder = new AlertDialog.Builder(AvailableJobDetailsActivity.this);
//                    builder.setMessage("Please enter your Stripe API key in My Info section in settings to finish this job and receive payments.")
                    builder.setMessage("Please enter your Stripe API Key in My Info section in settings to accept this job and receive payments. If you do not yet have a Stripe account, sign up for one below.");
                    builder.setCancelable(false);
//                    builder.setTitle("Please enter your Stripe API Key in My Info section in settings to accept this job and receive payments. If you do not yet have a Stripe account, sign up for one below.")
//                    builder.setItems(buttonItems, new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            switch (which) {
//                                case 0:
//                                    startActivity(new Intent(AvailableJobDetailsActivity.this, Confi1MyInfoActivity.class));
//                                    break;
//                                case 1:
//
//                                    break;
//                                case 2:
//
//                                    break;
//                            }
//                        }
//                    });

                    builder.setPositiveButton("Insert Key", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            startActivity(new Intent(AvailableJobDetailsActivity.this, Confi1MyInfoActivity.class));
                        }
                    });

                    builder.setNegativeButton("Register", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://dashboard.stripe.com/login"));
                            startActivity(browserIntent);
                        }
                    });

                    builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    AlertDialog alert = builder.create();
                    alert.show();
                } else {

                    //Show Time Dialog
                    showTimeDialog();



                }
            }


        });


        mIgnoreJob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
    public void showTimeDialog()
    {
        chooseTimeDialog = new AlertDialog.Builder(
                AvailableJobDetailsActivity.this);





        View titleview = getLayoutInflater().inflate(R.layout.alert_time, null);
        TextView title1 = (TextView) titleview.findViewById(R.id.dialogTimeTitle);
        title1.setText("What time will you reach the job?");

        chooseTimeDialog.setCustomTitle(titleview);
        message = new TextView(AvailableJobDetailsActivity.this);
        message.setPadding(12, 10, 12, 4);

        String messageString = "Click here to Select Time";

        message.setHint(messageString);
        message.setMovementMethod(LinkMovementMethod.getInstance());
        message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog(1000);
            }
        });
        chooseTimeDialog.setView(message);
        chooseTimeDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (!message.getText().toString().equals(""))
                {
                    showAcceptDialog();
                }
                else
                {
                    Toast.makeText(AvailableJobDetailsActivity.this,"Please select arrive time.",Toast.LENGTH_SHORT).show();
                }
            }
        });

        chooseTimeDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        chooseTimeDialog.show();
    }
    private static String getZoon(int c)
    {
        if (c >= 12)
            return "PM";
        else
            return "AM";
    }
    private static String pad(int c) {
        if (c >= 12) {
            int k = c - 12;
            //return String.valueOf(c);
            return pad(k);
        }
        else if (c >= 10)
        {
            return String.valueOf(c);
        }
        else
            return "0" + String.valueOf(c);
    }

    private SlideDateTimeListener listener = new SlideDateTimeListener() {

        @Override
        public void onDateTimeSet(Date date)
        {
            // Do something with the date. This Date object contains
            // the date and time that the user has selected.
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(date.getTime());
            startHour = calendar.get(Calendar.HOUR_OF_DAY);
            startMinue = calendar.get(Calendar.MINUTE);

            // set current time into textview
            message.setText(new StringBuilder().append(pad(startHour))
                    .append(":").append(startMinue).append(" ").append(getZoon(startHour)));
        }

        @Override
        public void onDateTimeCancel()
        {
            // Overriding onDateTimeCancel() is optional.
        }
    };


    protected void openDialog(int id) {
        switch (id) {
            case 1000:
                // set time picker as current time
//                TimePickerDialog tt = new TimePickerDialog(this,R.style.DialogTheme,
//                        startTimePickerListener, startHour, startMinue, true);

//                TimePickerView pvTime = new TimePickerView.Builder(AvailableJobDetailsActivity.this, new TimePickerView.OnTimeSelectListener() {
//                    @Override
//                    public void onTimeSelect(Date date, View v) {//选中事件回调
//                        //tvTime.setText(getTime(date));
//
//                        Calendar calendar = Calendar.getInstance();
//                        calendar.setTimeInMillis(date.getTime());
//                        startHour = calendar.get(Calendar.HOUR_OF_DAY);
//                        startMinue = calendar.get(Calendar.MINUTE);
//
//                        // set current time into textview
//                        message.setText(new StringBuilder().append(pad(startHour))
//                                .append(":").append(pad(startMinue)).append(" ").append(getZoon(startHour)));
//
//                    }
//                })
//                        .build();
//                pvTime.setDate(Calendar.getInstance());//注：根据需求来决定是否使用该方法（一般是精确到秒的情况），此项可以在弹出选择器的时候重新设置当前时间，避免在初始化之后由于时间已经设定，导致选中时间与当前时间不匹配的问题。
//                pvTime.show();

                new SlideDateTimePicker.Builder(this.getSupportFragmentManager())
                        .setListener(listener)
                        .setInitialDate(new Date())
                        .build()
                        .show();

        }
    }
    public void showAcceptDialog()
    {
        final AlertDialog.Builder chooseDrinkdialog = new AlertDialog.Builder(
                AvailableJobDetailsActivity.this);
        View titleview = getLayoutInflater().inflate(R.layout.alert_title, null);
        TextView title1 = (TextView) titleview.findViewById(R.id.dialogtitle);
        title1.setText("Alert");

        chooseDrinkdialog.setCustomTitle(titleview);
        final TextView message = new TextView(AvailableJobDetailsActivity.this);
        message.setPadding(12, 4, 12, 4);

        String messageString = "Before accepting this job, make sure that your Stripe account is switched to live. If it is on test, you will not be paid for this job. After you accept this job, please complete it as soon as possible.\n" +
                "\n" +
                "The rating you receive from the requester will effect how much are you ultimately paid for the job:\n" +
                "\n" +
                "- 4 & 5 stars receive full payment price listed.\n" +
                "- 3 stars will receive $5 less.\n" +
                "- 2 stars will receive $10 less.\n" +
                "- 1 star will not be paid and could lead to being banned from Shovler.";

        SpannableString ss = new SpannableString(messageString);
        ClickableSpan span1 = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                // do some thing
                Toast.makeText(AvailableJobDetailsActivity.this, "Reset password",
                        Toast.LENGTH_SHORT).show();

                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://shovler.com/img/stripe_switch.jpg"));
                startActivity(browserIntent);
            }
        };
        ss.setSpan(span1, 65, 81, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        message.setText(ss);
        message.setMovementMethod(LinkMovementMethod.getInstance());
        chooseDrinkdialog.setView(message);
//                    chooseDrinkdialog.setMessage(ss);


//                    chooseDrinkdialog.setMessage("You are about to accept a shoveling job.Please be aware that you are expected to arrive at the job within 10 min. of accepting and finish the job as soon as possible. The rating you receive from the customer will effect how much you are ultimately paid for the job.\n\n4 & 5 stars receive full payment price listed.\n\n3 stars will receive $5 less.\n\n2 stars will receive $10 less.\n\n1 star will not be payed and will potentially lead to being banned from Shovler.");
//                    chooseDrinkdialog.setMessage("Please be aware that you are expected to arrive at the job within 10 minutes of accepting a job and finish the job as soon as possible. \nThe rating you receive from the customer will effect how much are you ultimately paid for the job: \n\n4 & 5 stars receive full payment price listed.\n\n3 stars will receive $5 less.\n\n2 stars will receive $10 less.\n\n1 star will not be payed and will potentially lead to being banned from Shovler.");
//                    chooseDrinkdialog.setMessage("Please be aware that you are expected to arrive at the job within 15 minutes of accepting a job and then finish the job as soon as possible.\n" +
//                            "\n" +
//                            "The rating you receive from the requester will effect how much are you ultimately paid for the job:\n" +
//                            "\n" +
//                            "- 4 & 5 stars receive full payment price listed.\n" +
//                            "- 3 stars will receive $5 less.\n" +
//                            "- 2 stars will receive $10 less.\n" +
//                            "- 1 star will not be payed and will potentially lead to being banned from Shovler.");


        chooseDrinkdialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                callAcceptJob();
            }
        });

        chooseDrinkdialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        chooseDrinkdialog.show();
    }
    private void setErrorFun(EditText edit, String msg) {
        /*edit.setError(msg);
        edit.requestFocus();*/
        Snackbar.make(edit, msg, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
        edit.requestFocus();
    }
    private void callAcceptJob() {
        mProgressDialog = ProgressDialog.show(AvailableJobDetailsActivity.this, "",
                getResources().getString(R.string.loading), true, false);
        //String estTime = editArrivalTime.getText().toString();
        String estTime = message.getText().toString();
        int i = 0;
        NetUtils.CallAcceptJob(AvailableJobActivity.models.get(position).getJid(), prefClass.getSavedStringPreference(SharedPrefClass.USER_ID),estTime, this, AvailableJobDetailsActivity.this);
    }
    public void getGooglePlacePicture(String lat,String lon)
    {
        String mapKey = "AIzaSyDMSVwjY-Ml_OeJpzDKQEM-8fNZWJnHXY8";
        //ServiceManager.onGetPlacePicture(sb.toString(),this);
        Globals.g_googlePhoto = "https://maps.googleapis.com/maps/api/streetview?size=600x240&location=" +lat+","+lon
        + "&key=" + mapKey;
        onResponse(1);
    }


    private void setFont() {
        Typeface tfRegular = Constants.setRegularLatoFont(AvailableJobDetailsActivity.this);
        Typeface tfLight = Constants.setLightLatoFont(AvailableJobDetailsActivity.this);
        Typeface tfThin = Constants.setThinLatoFont(AvailableJobDetailsActivity.this);

        mAcceptJob.setTypeface(tfRegular);
        mIgnoreJob.setTypeface(tfRegular);

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


    @Override
    public void AcceptJobCallbackSuccess(String success) {
        mProgressDialog.dismiss();

        Log.v("Success", "" + success);

        try {
            JSONObject jsonObject = new JSONObject(success);

            if (jsonObject.getString("status").equals("true")) {
                JSONObject jobObject = jsonObject.getJSONObject("items");
                startActivityForResult(new Intent(AvailableJobDetailsActivity.this, AcceptedJobDetailsActivity.class).putExtra("position", position).putExtra("ActivityFlag", "Available"),111);
            } else {
                String msg = jsonObject.getString("items");
                Constants.showAlert(AvailableJobDetailsActivity.this, msg);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == 111) {
           finish();
        }
    }
    @Override
    public void AcceptJobCallbackError(String error) {
        mProgressDialog.dismiss();

        Constants.showAlert(AvailableJobDetailsActivity.this, error);
    }

    @Override
    public void onResponse(int code) {
        switch (code)
        {
            case 1:
                if (AvailableJobActivity.models.get(position).getJobtype().equals("Car")) {
                    Glide.with(AvailableJobDetailsActivity.this).load(Globals.g_googlePhoto).override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL) // don't use target size, load full image
                             .diskCacheStrategy(DiskCacheStrategy.RESULT).error(R.drawable.action_bar_bg).placeholder(null).into(mCarImageView);
                }
                else if (AvailableJobActivity.models.get(position).getJobtype().equals("Home")) {
                    Glide.with(AvailableJobDetailsActivity.this).load(Globals.g_googlePhoto).override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL) // don't use target size, load full image
                        .diskCacheStrategy(DiskCacheStrategy.RESULT).error(R.drawable.action_bar_bg).placeholder(null).into(mHomeImageView);
                }
                else if (AvailableJobActivity.models.get(position).getJobtype().equals("Business")) {
                    Glide.with(AvailableJobDetailsActivity.this).load(Globals.g_googlePhoto).override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL) // don't use target size, load full image
                            .diskCacheStrategy(DiskCacheStrategy.RESULT).error(R.drawable.action_bar_bg).placeholder(null).into(mBusinessImageView);
                }

                break;
            case 400:
                if (AvailableJobActivity.models.get(position).getJobtype().equals("Home")) {
                    Glide.with(AvailableJobDetailsActivity.this).load(Globals.g_googlePhoto).override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL) // don't use target size, load full image
                            .diskCacheStrategy(DiskCacheStrategy.RESULT).error(R.drawable.action_bar_bg).placeholder(null).into(mHomeImageView);
                }
                else if (AvailableJobActivity.models.get(position).getJobtype().equals("Business")) {
                    Glide.with(AvailableJobDetailsActivity.this).load(Globals.g_googlePhoto).override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL) // don't use target size, load full image
                            .diskCacheStrategy(DiskCacheStrategy.RESULT).error(R.drawable.action_bar_bg).placeholder(null).into(mBusinessImageView);
                }


                break;
        }
    }

    @Override
    public void onResponse(int code, int index) {

    }
}
