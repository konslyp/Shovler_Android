package com.app.shovelerapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.app.shovelerapp.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by supriya.n on 18-08-2016.
 */
public class MyFilterableAdapter extends BaseAdapter implements Filterable {
    private Context context;
    private List<String> items;
    private List<String> filteredItems;
    private ItemFilter mFilter = new ItemFilter();

    public MyFilterableAdapter(Context context, List<String> items) {
        //super(context, R.layout.your_row, items);
        this.context = context;
        this.items = items;
        this.filteredItems = items;
    }

    @Override
    public int getCount() {
        try {
            return filteredItems.size();
        }catch (Exception e){
            return 0;
        }
    }

    @Override
    public Object getItem(int position) {
        return filteredItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = inflater.inflate(R.layout.row_search, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.tvTitle = (TextView) convertView.findViewById(R.id.tv_title);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        String location = filteredItems.get(position);
        if (!location.isEmpty() || viewHolder != null) {
            viewHolder.tvTitle.setText(location);
        }
        return convertView;
    }

    public static class ViewHolder {
        TextView tvTitle;
    }

    private class ItemFilter extends Filter {
        @Override
        protected Filter.FilterResults performFiltering(CharSequence constraint) {
            String filterString = constraint.toString().toLowerCase();
            filterString=filterString.replace(" ", "+");
            filterString=filterString.trim();
            FilterResults results = new FilterResults();

            int count = items.size();
            final List<String> tempItems = new ArrayList<>(count);

            for (int i = 0; i < count; i++) {
                if (items.get(i).toLowerCase().startsWith(filterString)) {
                    tempItems.add(items.get(i));
                }
            }

            results.values = tempItems;
            results.count = tempItems.size();

            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredItems = (ArrayList<String>) results.values;
            notifyDataSetChanged();
        }
    }

    public Filter getFilter() {
        return mFilter;
    }
}

