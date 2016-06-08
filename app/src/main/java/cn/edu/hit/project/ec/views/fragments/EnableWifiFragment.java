package cn.edu.hit.project.ec.views.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.edu.hit.project.ec.R;

public class EnableWifiFragment extends Fragment {
    @BindView(R.id.enableWifi) public Button enableWifi;

    private OnWifiEnabledListener mListener;
    private BroadcastReceiver mReceiver;

    public interface OnWifiEnabledListener {
        void onWifiEnabled();
    }

    public EnableWifiFragment() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                ConnectivityManager conMan = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo netInfo = conMan.getActiveNetworkInfo();
                if (netInfo != null && netInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                    enableWifi.setEnabled(false);
                    enableWifi.setText(getString(R.string.common_wifi_enabled));
                    if (mListener != null) {
                        mListener.onWifiEnabled();
                    }
                }
            }
        };

        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        getActivity().registerReceiver(mReceiver, filter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(mReceiver);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_match_enable_wifi, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    @OnClick(R.id.enableWifi)
    public void onEnableWifiClick(View view) {
        WifiManager wifiManager = (WifiManager) getActivity().getSystemService(Context.WIFI_SERVICE);
        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }
        enableWifi.setText(getString(R.string.common_wifi_enabling));
    }

    public void setOnWifiEnabledListener(OnWifiEnabledListener listener) {
        this.mListener = listener;
    }
}
