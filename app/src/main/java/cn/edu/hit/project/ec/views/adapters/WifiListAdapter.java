package cn.edu.hit.project.ec.views.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cn.edu.hit.project.ec.R;

public class WifiListAdapter extends BaseAdapter {
    private LayoutInflater inflater = null;
    private Context context = null;
    private List<String> names;
    private List<Integer> signals;
    private List<Boolean> locks;

    public static class ViewHolder {
        public TextView nameView;
        public ImageView iconView;

        public ViewHolder(View rootView) {
            nameView = (TextView) rootView.findViewById(R.id.name);
            iconView = (ImageView) rootView.findViewById(R.id.icon);
        }
    }

    public WifiListAdapter(Context context, List<String> names, List<Integer> signals, List<Boolean> locks) {
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.names = names;
        this.signals = signals;
        this.locks = locks;
    }

    @Override
    public int getCount() {
        return signals.size();
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
            convertView = inflater.inflate(R.layout.list_item_wifi, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }
        holder.nameView.setText(names.get(position));
        int drawable;
        switch (signals.get(position)) {
            case 1: drawable = !locks.get(position) ? R.drawable.ic_signal_wifi_1_bar_black_24dp : R.drawable.ic_signal_wifi_1_bar_lock_black_24dp; break;
            case 2: drawable = !locks.get(position) ? R.drawable.ic_signal_wifi_2_bar_black_24dp : R.drawable.ic_signal_wifi_2_bar_lock_black_24dp; break;
            case 3: drawable = !locks.get(position) ? R.drawable.ic_signal_wifi_3_bar_black_24dp : R.drawable.ic_signal_wifi_3_bar_lock_black_24dp; break;
            case 4: drawable = !locks.get(position) ? R.drawable.ic_signal_wifi_4_bar_black_24dp : R.drawable.ic_signal_wifi_4_bar_lock_black_24dp; break;
            default: drawable = R.drawable.ic_signal_wifi_0_bar_black_24dp; break;
        }
        holder.iconView.setImageDrawable(context.getResources().getDrawable(drawable));
        return convertView;
    }
}