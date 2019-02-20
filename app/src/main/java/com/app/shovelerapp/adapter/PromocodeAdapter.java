package com.app.shovelerapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.shovelerapp.R;
import com.app.shovelerapp.model.Promocode;

import java.util.ArrayList;

/**
 * Created by supriya.n on 27-09-2016.
 */
public class PromocodeAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Promocode> promocodeArrayList=new ArrayList<Promocode>();
    private View view;
    private LayoutInflater layoutInflater;

    public PromocodeAdapter(Context context,ArrayList<Promocode> promocodeArrayList){
        this.context=context;
        this.promocodeArrayList=promocodeArrayList;
    }
    @Override
    public int getCount() {
        return promocodeArrayList.size();
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
        TextView  mPromoTextView,mDiscountTextView,mTillValid;
        if (layoutInflater == null) {
            layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        view = layoutInflater.inflate(R.layout.promo_adapter, null);

        mPromoTextView= (TextView) view.findViewById(R.id.promo_code);
        mDiscountTextView= (TextView) view.findViewById(R.id.discount);
        mTillValid= (TextView) view.findViewById(R.id.valid_till);

        mPromoTextView.setText(promocodeArrayList.get(position).getPcode());
        mDiscountTextView.setText(promocodeArrayList.get(position).getDiscount()+"%");
        mTillValid.setText(promocodeArrayList.get(position).getTodate());
        return view;
    }
}
