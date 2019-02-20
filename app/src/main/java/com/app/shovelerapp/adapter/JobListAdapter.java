package com.app.shovelerapp.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.app.shovelerapp.R;
import com.app.shovelerapp.utils.Constants;

/**
 * Created by supriya.n on 13-06-2016.
 */
public class JobListAdapter  extends BaseAdapter {
    private Context context;
    private View view;
    private LayoutInflater layoutInflater;

    public JobListAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return 10;
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
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView mCancel,mJobName,mDateTitle,mDateValue,mPaymentTitle,mPaymentValue,mTimeTitle,mTimeValue,mPaymentStatusTitle;
        if (layoutInflater == null) {
            layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        view = layoutInflater.inflate(R.layout.adapter_job_list, null);
        mJobName= (TextView) view.findViewById(R.id.job_name);
        mDateTitle= (TextView) view.findViewById(R.id.date_title);
        mDateValue= (TextView) view.findViewById(R.id.date_value);
        mPaymentTitle= (TextView) view.findViewById(R.id.payment_title);
        mPaymentValue= (TextView) view.findViewById(R.id.payment_value);
        mTimeTitle= (TextView) view.findViewById(R.id.time_title);
        mTimeValue= (TextView) view.findViewById(R.id.time_value);
        mPaymentStatusTitle= (TextView) view.findViewById(R.id.payment_status_title);
        mCancel= (TextView) view.findViewById(R.id.status_button);

        Typeface tfRegular= Constants.setRegularLatoFont(context);
        Typeface tfLight=Constants.setLightLatoFont(context);
        Typeface tfThin=Constants.setThinLatoFont(context);
        Typeface tfMedium=Constants.setMediumLatoFont(context);

        mJobName.setTypeface(tfRegular);
        mDateTitle.setTypeface(tfRegular);
        mDateValue.setTypeface(tfRegular);
        mPaymentTitle.setTypeface(tfRegular);
        mPaymentValue.setTypeface(tfRegular);
        mTimeTitle.setTypeface(tfRegular);
        mTimeValue.setTypeface(tfRegular);
        mPaymentStatusTitle.setTypeface(tfRegular);
        mCancel.setTypeface(tfRegular);



        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        return view;
    }
}
