package cn.edu.hit.project.ec.views.fragments;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.edu.hit.project.ec.R;
import cn.edu.hit.project.ec.WifiActivity;
import cn.edu.hit.project.ec.utils.WifiUtils;

public class MatchWifiFragment extends Fragment {
    private final static int REQUEST_HOST_WIFI = 0;
    private final static int REQUEST_CLIENT_WIFI = 1;
    @BindView(R.id.hostWifiText) public TextView hostWifiText;
    @BindView(R.id.clientWifiText) public TextView clientWifiText;
    @BindView(R.id.hostWifiTip) public TextView hostWifiTip;
    @BindView(R.id.clientWifiTip) public TextView clientWifiTip;
    @BindView(R.id.match) public Button match;
    private View rootView;

    private boolean enableCheck = false;

    private ScanResult mHostWifi;
    private String mHostWifiPassword;
    private ScanResult mClientWifi;
    private String mClientWifiPassword;
    private WifiManager wifiManager;
    private BroadcastReceiver mReceiver;
    private OnWifiMatchedListener mListener;

    public interface OnWifiMatchedListener {
        void onWifiMatched(ScanResult wifi, String password);
    }

    public MatchWifiFragment() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        wifiManager = (WifiManager) getActivity().getSystemService(Context.WIFI_SERVICE);

        mReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                if (!enableCheck || mHostWifi == null || mListener == null) {
                    return;
                }
                ConnectivityManager conMan = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo netInfo = conMan.getActiveNetworkInfo();
                String destSSID = String.format("\"%s\"", mHostWifi.SSID);
                if (netInfo != null && netInfo.getType() == ConnectivityManager.TYPE_WIFI && destSSID.equals(netInfo.getExtraInfo())) {
                    mListener.onWifiMatched(mClientWifi, mClientWifiPassword);
                    enableCheck = false;
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
        if (mReceiver != null) {
            getActivity().unregisterReceiver(mReceiver);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_match_wifi, container, false);
        ButterKnife.bind(this, rootView);

        Bundle args = getArguments();
        String deviceType = args.getString("type");
        hostWifiTip.setText(String.format(getString(R.string.tip_connect_to), deviceType));
        clientWifiTip.setText(String.format(getString(R.string.tip_client_wifi), deviceType));
        return rootView;
    }

    @OnClick(R.id.hostWifi)
    public void onHostWifiSelect(View v) {
        startActivityForResult(new Intent(getActivity(), WifiActivity.class), REQUEST_HOST_WIFI);
    }

    @OnClick(R.id.clientWifi)
    public void onClientWifiSelect(View v) {
        startActivityForResult(new Intent(getActivity(), WifiActivity.class), REQUEST_CLIENT_WIFI);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {
            return;
        }
        switch (requestCode) {
            case REQUEST_HOST_WIFI:
                mHostWifi = data.getParcelableExtra("wifi");
                mHostWifiPassword = data.getStringExtra("password");
                hostWifiText.setText(mHostWifi.SSID);
                break;
            case REQUEST_CLIENT_WIFI:
                mClientWifi = data.getParcelableExtra("wifi");
                mClientWifiPassword = data.getStringExtra("password");
                clientWifiText.setText(mClientWifi.SSID);
                break;
        }
        checkReadyState();
    }

    private void checkReadyState() {
        if (enableCheck || mHostWifi == null || mHostWifiPassword == null || mClientWifi == null || mClientWifiPassword == null) {
            match.setEnabled(false);
        } else {
            match.setEnabled(true);
            match.setText(getString(R.string.common_match));
        }
    }

    public void setOnWifiSelectedListener(OnWifiMatchedListener listener) {
        this.mListener = listener;
    }

    @OnClick(R.id.match)
    public void OnMatchClient() {
        if (mListener != null) {
            match.setEnabled(false);
            match.setText(getString(R.string.common_matching));
            WifiConfiguration configuration = WifiUtils.getWifiConfiguration(mHostWifi, mHostWifiPassword);
            if (configuration == null) {
                return;
            }
            wifiManager.addNetwork(configuration);
            List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
            for( WifiConfiguration i : list ) {
                if(i.SSID != null && i.SSID.equals("\"" + mHostWifi.SSID + "\"")) {
                    wifiManager.disconnect();
                    enableCheck = true;
                    wifiManager.enableNetwork(i.networkId, true);
                    wifiManager.reconnect();
                    return;
                }
            }
            enableCheck = false;
            Snackbar.make(rootView, String.format(getString(R.string.error_data_not_found), mHostWifi.SSID), Snackbar.LENGTH_LONG).show();
        }
    }
}
