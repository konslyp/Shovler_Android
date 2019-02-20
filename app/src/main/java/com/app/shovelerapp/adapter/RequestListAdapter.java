package com.app.shovelerapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.app.shovelerapp.R;
import com.app.shovelerapp.activity.AcceptedJobDetailsActivity;
import com.app.shovelerapp.activity.ApproveJobActivity;
import com.app.shovelerapp.activity.JobStatusActivity;
import com.app.shovelerapp.activity.ThirdStepActivity;
import com.app.shovelerapp.model.JobDetails;
import com.app.shovelerapp.model.RequestorJobModel;
import com.app.shovelerapp.utils.Constants;
import com.app.shovelerapp.utils.SharedPrefClass;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by supriya.n on 13-06-2016.
 */
public class RequestListAdapter extends BaseAdapter {
    private Context context;
    private View view;
    private ImageView mSettings;
    private LayoutInflater layoutInflater;
    private SharedPrefClass prefClass;

    private ArrayList<RequestorJobModel> jobModels = new ArrayList<RequestorJobModel>();

    public RequestListAdapter(Context context, ArrayList<RequestorJobModel> jobModels) {
        this.context = context;
        this.jobModels = jobModels;
        prefClass = new SharedPrefClass(context);
    }

    @Override
    public int getCount() {
        return jobModels.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, final View convertView, ViewGroup parent) {
        TextView mStatusButton, mJobName, mDateTitle, mDateValue, mPaymentTitle, mPaymentValue, mTimeTitle, mTimeValue, mPaymentStatusTitle;
        ImageView mJobImageView, mPendingJobIv;
        if (layoutInflater == null) {
            layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        view = layoutInflater.inflate(R.layout.adapter_request_list, null);
        mSettings = (ImageView) view.findViewById(R.id.settings);
        mStatusButton = (TextView) view.findViewById(R.id.status_button);
        mJobName = (TextView) view.findViewById(R.id.job_name);
        mDateTitle = (TextView) view.findViewById(R.id.date_title);
        mDateValue = (TextView) view.findViewById(R.id.date_value);
        mPaymentTitle = (TextView) view.findViewById(R.id.payment_title);
        mPaymentValue = (TextView) view.findViewById(R.id.payment_value);
        mTimeTitle = (TextView) view.findViewById(R.id.time_title);
        mTimeValue = (TextView) view.findViewById(R.id.time_value);
        mPaymentStatusTitle = (TextView) view.findViewById(R.id.payment_status_title);
        mJobImageView = (ImageView) view.findViewById(R.id.job_image);
        mPendingJobIv = (ImageView) view.findViewById(R.id.pending_job_iv);

        mJobName.setText(jobModels.get(position).getJobtype());
        mPaymentValue.setText("$" + jobModels.get(position).getPrice());
        mStatusButton.setText(jobModels.get(position).getStatus());

        if (jobModels.get(position).getStatus().equals("FINISHED")) {
            mStatusButton.setBackgroundResource(R.drawable.green_button);
            mPendingJobIv.setVisibility(View.VISIBLE);
            mPendingJobIv.setBackgroundResource(R.drawable.cancelred);
            mPaymentStatusTitle.setText("REVIEWED");
        } else if (mStatusButton.getText().equals("ACTIVE")) {
            mStatusButton.setBackgroundResource(R.drawable.orange_button);
        } else if (mStatusButton.getText().equals("CANCELLED")) {
            mStatusButton.setBackgroundResource(R.drawable.red_button);
        } else if (jobModels.get(position).getStatus().equals("DONE")) {
            mStatusButton.setBackgroundResource(R.drawable.green_button);
            mStatusButton.setText("FINISHED");
            mPendingJobIv.setVisibility(View.VISIBLE);
            mPendingJobIv.setBackgroundResource(R.drawable.checkmark);
            mPaymentStatusTitle.setText("PAYED");
            //mStatusButton.setText("APPROVED");
        } else if (mStatusButton.getText().equals("HOLD")) {
            mStatusButton.setBackgroundResource(R.drawable.red_button);
        } else if (mStatusButton.getText().equals("OPEN")) {
            mStatusButton.setBackgroundResource(R.drawable.green_button);
        }


        String date = jobModels.get(position).getJobdt();
        String[] parts = date.split(" ");

        String[] timeparts = parts[1].split(":");

        String reqDate = parts[0];


//        SimpleDateFormat simpleDate = new SimpleDateFormat("MM-dd-yyyy");
        try {
            Date originalDate;
            if (JobStatusActivity.isRequestor) {
                originalDate = new SimpleDateFormat("dd-MM-yyyy").parse(reqDate);
            } else {
                originalDate = new SimpleDateFormat("yyyy-MM-dd").parse(reqDate);
            }
            String dateString2 = new SimpleDateFormat("MM-dd-yyyy").format(originalDate);
            mDateValue.setText("" + dateString2);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        mTimeValue.setText(timeparts[0] + ":" + timeparts[1]);

        Typeface tfRegular = Constants.setRegularLatoFont(context);
        Typeface tfLight = Constants.setLightLatoFont(context);
        Typeface tfThin = Constants.setThinLatoFont(context);
        Typeface tfMedium = Constants.setMediumLatoFont(context);

        mJobName.setTypeface(tfRegular);
        mDateTitle.setTypeface(tfRegular);
        mDateValue.setTypeface(tfRegular);
        mPaymentTitle.setTypeface(tfRegular);
        mPaymentValue.setTypeface(tfRegular);
        mTimeTitle.setTypeface(tfRegular);
        mTimeValue.setTypeface(tfRegular);
        mPaymentStatusTitle.setTypeface(tfRegular);
        mStatusButton.setTypeface(tfRegular);

        Glide.with(context).load(jobModels.get(position).getJobpic()).error(R.mipmap.app_logo).into(mJobImageView);


        mSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.settings:
                        PopupMenu popup = new PopupMenu(context, v);
                        popup.getMenuInflater().inflate(R.menu.popup_menu,
                                popup.getMenu());
                        popup.show();
                        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {

                                switch (item.getItemId()) {
                                    case R.id.view:

                                        //Or Some other code you want to put here.. This is just an example.
                                        Toast.makeText(context, " View Clicked at position " + " : " + position, Toast.LENGTH_LONG).show();

                                        break;
                                    case R.id.cancel:

                                        Toast.makeText(context, "Cancel Clicked at position " + " : " + position, Toast.LENGTH_LONG).show();

                                        break;
                                    case R.id.mark_as_finish:

                                        Toast.makeText(context, "Mark as finish Clicked at position " + " : " + position, Toast.LENGTH_LONG).show();

                                        break;
                                    default:
                                        break;
                                }

                                return true;
                            }
                        });

                        break;

                    default:
                        break;
                }
            }
        });

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (prefClass.getSavedStringPreference(SharedPrefClass.UTYPE).equals("Shovler")) {
                    if (jobModels.get(position).getStatus().equals("ACTIVE")) {
                        context.startActivity(new Intent(context, AcceptedJobDetailsActivity.class).putExtra("position", position).putExtra("ActivityFlag", "Job"));
                    }
                } else {
                    JobStatusActivity.details.setJobDetails(JobStatusActivity.jobdetailsEntity);
                    JobStatusActivity.jobdetailsEntity.setAddress(jobModels.get(position).getAddress());
                    JobStatusActivity.jobdetailsEntity.setLat(jobModels.get(position).getLoclat());
                    JobStatusActivity.jobdetailsEntity.setLng(jobModels.get(position).getLoclng());
                    JobStatusActivity.jobdetailsEntity.setDesc(jobModels.get(position).getDescp());
                    JobStatusActivity.jobdetailsEntity.exptime = jobModels.get(position).esptime;
                    if (jobModels.get(position).getJobtype().equals("Car")) {
                        JobStatusActivity.details.getJobDetails().setBusiness(null);
                        JobStatusActivity.details.getJobDetails().setHome(null);
                        JobStatusActivity.carEntity.setUrl(jobModels.get(position).getJobpic());
                        JobStatusActivity.carEntity.setModel(jobModels.get(position).getModel());
                        JobStatusActivity.carEntity.setColor(jobModels.get(position).getColor());
                        JobStatusActivity.carEntity.setLicense(jobModels.get(position).getLicplateno());
                        JobStatusActivity.carEntity.setPrice(jobModels.get(position).getPrice());
                        JobStatusActivity.carEntity.setState(jobModels.get(position).getLicplatestate());
                        JobStatusActivity.details.getJobDetails().setCar(JobStatusActivity.carEntity);
                    } else if (jobModels.get(position).getJobtype().equals("Home")) {
                        JobStatusActivity.details.getJobDetails().setBusiness(null);
                        JobStatusActivity.details.getJobDetails().setCar(null);
                        JobStatusActivity.homeEntity.setUrl(jobModels.get(position).getJobpic());
                        JobStatusActivity.homeEntity.setPrice(jobModels.get(position).getPrice());
                        JobStatusActivity.details.getJobDetails().setHome(JobStatusActivity.homeEntity);
                    } else if (jobModels.get(position).getJobtype().equals("Business")) {
                        JobStatusActivity.details.getJobDetails().setHome(null);
                        JobStatusActivity.details.getJobDetails().setCar(null);
                        JobStatusActivity.businessEntity.setUrl(jobModels.get(position).getJobpic());
                        JobStatusActivity.businessEntity.setPrice(jobModels.get(position).getPrice());
                        JobStatusActivity.businessEntity.setSize(jobModels.get(position).getSizeofwork());
                        JobStatusActivity.details.getJobDetails().setBusiness(JobStatusActivity.businessEntity);
                    }
                    if (jobModels.get(position).getStatus().equals("FINISHED")) {
                        RequestorJobModel jobModel = jobModels.get(position);
                        context.startActivity(new Intent(context, ApproveJobActivity.class).putExtra("data", jobModel));
                    } else {
                        Constants.JOB_STATUS_DETAILS_FLAG = true;
                        context.startActivity(new Intent(context, ThirdStepActivity.class).putExtra("status", jobModels.get(position).getStatus()).putExtra("jid", jobModels.get(position).getJid()));
                    }
                }
            }
        });


        return view;
    }
}
