package com.app.shovelerapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.app.shovelerapp.R;

/**
 * Created by supriya.n on 16-06-2016.
 */
public class GridViewItemAdapter extends BaseAdapter {
    private Context context;
    private View view;
    private LayoutInflater layoutInflater;

    public GridViewItemAdapter(Context context){
        this.context=context;
    }
    @Override
    public int getCount() {
        return 5;
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
        if (layoutInflater == null) {
            layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        view = layoutInflater.inflate(R.layout.gridview_item, null);
        return view;
    }
}
