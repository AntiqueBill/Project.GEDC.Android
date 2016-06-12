package cn.edu.hit.project.ec.views.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
<<<<<<< HEAD
=======
import android.net.NetworkInfo;
>>>>>>> e504fc4cb061d82be55349a32b3791fb9376ba73
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
<<<<<<< HEAD

    @BindView(R.id.enableWifi) public Button enableWifi;
    private View rootView;

    private WifiManager mWifiManager;
    private BroadcastReceiver mReceiver;
    private OnWifiEnabledListener mListener;
=======
    @BindView(R.id.enableWifi) public Button enableWifi;

    private OnWifiEnabledListener mListener;
    private BroadcastReceiver mReceiver;
>>>>>>> e504fc4cb061d82be55349a32b3791fb9376ba73

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
<<<<<<< HEAD
                if (mWifiManager.isWifiEnabled()) {
=======
                ConnectivityManager conMan = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo netInfo = conMan.getActiveNetworkInfo();
                if (netInfo != null && netInfo.getType() == ConnectivityManager.TYPE_WIFI) {
>>>>>>> e504fc4cb061d82be55349a32b3791fb9376ba73
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
<<<<<<< HEAD

        mWifiManager = (WifiManager) getActivity().getSystemService(Context.WIFI_SERVICE);
=======
>>>>>>> e504fc4cb061d82be55349a32b3791fb9376ba73
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
<<<<<<< HEAD
        try {
            getActivity().unregisterReceiver(mReceiver);
        } catch (IllegalArgumentException ignored) {}
=======
        getActivity().unregisterReceiver(mReceiver);
>>>>>>> e504fc4cb061d82be55349a32b3791fb9376ba73
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
<<<<<<< HEAD
        rootView = inflater.inflate(R.layout.fragment_match_enable_wifi, container, false);
        ButterKnife.bind(this, rootView);

        return rootView;
=======
        View view = inflater.inflate(R.layout.fragment_match_enable_wifi, container, false);
        ButterKnife.bind(this, view);

        return view;
>>>>>>> e504fc4cb061d82be55349a32b3791fb9376ba73
    }

    @OnClick(R.id.enableWifi)
    public void onEnableWifiClick(View view) {
<<<<<<< HEAD
        if (!mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(true);
=======
        WifiManager wifiManager = (WifiManager) getActivity().getSystemService(Context.WIFI_SERVICE);
        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
>>>>>>> e504fc4cb061d82be55349a32b3791fb9376ba73
        }
        enableWifi.setText(getString(R.string.common_wifi_enabling));
    }

    public void setOnWifiEnabledListener(OnWifiEnabledListener listener) {
        this.mListener = listener;
    }
}
