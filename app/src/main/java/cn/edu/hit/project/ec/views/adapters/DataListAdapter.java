package cn.edu.hit.project.ec.views.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import cn.edu.hit.project.ec.R;

public class DataListAdapter extends BaseAdapter {
    private LayoutInflater mInflater = null;
    private List<String> labels;
    private List<Float> values;
    private String unit;

    public static class ViewHolder {
        public TextView dateTimeView;
        public TextView valueView;
        public TextView unitView;

        public ViewHolder(View rootView) {
            dateTimeView = (TextView) rootView.findViewById(R.id.datetime);
            valueView = (TextView) rootView.findViewById(R.id.value);
            unitView = (TextView) rootView.findViewById(R.id.unit);
        }
    }

    public DataListAdapter(Context context, List<String> labels, List<Float> values, String unit) {
        mInflater = LayoutInflater.from(context);
        this.labels = labels;
        this.values = values;
        this.unit = unit;
    }

    @Override
    public int getCount() {
        return values.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView == null) {
            convertView = mInflater.inflate(R.layout.list_item_data, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }
        holder.dateTimeView.setText(labels.get(position));
        holder.valueView.setText(String.valueOf(values.get(position)));
        holder.unitView.setText(unit);
        return convertView;
    }
}