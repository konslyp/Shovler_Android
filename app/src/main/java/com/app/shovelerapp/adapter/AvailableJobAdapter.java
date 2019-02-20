package com.app.shovelerapp.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.shovelerapp.R;
import com.app.shovelerapp.activity.AvailableJobActivity;
import com.app.shovelerapp.activity.AvailableJobDetailsActivity;
import com.app.shovelerapp.doc.Globals;
import com.app.shovelerapp.model.RequestorJobModel;
import com.app.shovelerapp.service.ILoadService;
import com.app.shovelerapp.service.ServiceManager;
import com.app.shovelerapp.utils.Constants;
import com.app.shovelerapp.utils.ScaledImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.Target;

import java.util.ArrayList;

/**
 * Created by supriya.n on 11-06-2016.
 */
public class AvailableJobAdapter extends BaseAdapter implements View.OnClickListener,ILoadService{
    private Context context;
    private View view;
    private ArrayList<RequestorJobModel> models = new ArrayList<RequestorJobModel>();
    private LayoutInflater layoutInflater;

    public AvailableJobAdapter(Context context, ArrayList<RequestorJobModel> models) {
        this.context = context;
        this.models = models;
    }

    @Override
    public int getCount() {
        return models.size();
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        TextView mPriceTextView;
        TextView mDistanceTextView;
        TextView mJobLevel;
        TextView mJobName;
        TextView zipCodeTextView;
        ScaledImageView mImage;
        if (layoutInflater == null) {
            layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        view = layoutInflater.inflate(R.layout.adapter_aval_job, null);
        mPriceTextView = (TextView) view.findViewById(R.id.price_tv);
        mDistanceTextView = (TextView) view.findViewById(R.id.job_distance);
        zipCodeTextView = (TextView) view.findViewById(R.id.job_zipcode_val);
        mJobLevel = (TextView) view.findViewById(R.id.job_level);
        mJobName = (TextView) view.findViewById(R.id.job_name);
        mImage = (ScaledImageView) view.findViewById(R.id.image);

        Typeface tfRegular = Constants.setRegularLatoFont(context);
        Typeface tfLight = Constants.setLightLatoFont(context);
        Typeface tfThin = Constants.setThinLatoFont(context);
        Typeface tfMedium = Constants.setMediumLatoFont(context);

        mPriceTextView.setTypeface(tfRegular);
        mDistanceTextView.setTypeface(tfRegular);
        mJobLevel.setTypeface(tfRegular);
        mJobName.setTypeface(tfRegular);


        mPriceTextView.setText("$" + String.format("%.2f", Double.valueOf(models.get(position).getPrice())));
        mDistanceTextView.setText("" + Math.round(Float.valueOf(models.get(position).getDistance())) + " miles away");
        mJobName.setText(models.get(position).getJobtype());
        zipCodeTextView.setText(models.get(position).getZipcode());
        //  mDistanceTextView.setText(models.get(position).getDistance());


        if (models.get(position).getJobtype().equals("Car")) {
            Glide.with(context).load(models.get(position).getJobpic()).override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL) // don't use target size, load full image
                    .diskCacheStrategy(DiskCacheStrategy.RESULT).placeholder(null).into(mImage);
        }
        else
        {
            if (models.get(position).getJobpic().endsWith("openjobpics/"))
            {
                String location = models.get(position).getLoclat() + "," + models.get(position).getLoclng();
                ServiceManager.onGetParnoramaID(location,this,position);
            }
            else
            {
                Glide.with(context).load(models.get(position).getJobpic()).override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL) // don't use target size, load full image
                        .diskCacheStrategy(DiskCacheStrategy.RESULT).placeholder(null).into(mImage);
            }
        }

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v("click", "" + position);
                if (Globals.g_dutyStatus.equals("1")) {
                    ((AvailableJobActivity) context).startActivityForResult(new Intent(context, AvailableJobDetailsActivity.class).putExtra("position", position), 111);
                }
                else
                {
                    Toast.makeText(context,"Please Go On Duty to accept a job.",Toast.LENGTH_SHORT).show();
                }
              /*  Fragment fragment = new AvailableJobDetailsActivity();
                FragmentManager fragmentManager = ((AppCompatActivity)context).getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.add(R.id.container, fragment,"AvailableJobDetailsFragment");
                fragmentTransaction.addToBackStack(AvailableJobDetailsActivity.class.getName());
                fragmentTransaction.commit();*/
            }
        });
        return view;
    }

    @Override
    public void onResponse(int code) {

    }

    @Override
    public void onResponse(int code, int index) {
        switch (code)
        {
            case 400:
                models.get(index).setJobpic(Globals.g_googlePhoto);
                notifyDataSetChanged();
                break;
        }
    }
    @Override
    public void onClick(View v) {

    }
}
