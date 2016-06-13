package cn.edu.hit.project.ec.views.fragments;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.edu.hit.project.ec.App;
import cn.edu.hit.project.ec.R;
import cn.edu.hit.project.ec.WifiActivity;
import cn.edu.hit.project.ec.network.BandTcpMatcher;
import cn.edu.hit.project.ec.network.CameraSshMatcher;
import cn.edu.hit.project.ec.utils.WifiUtils;

public class MatchDeviceFragment extends Fragment {
    private final static int REQUEST_HOST_WIFI = 0;
    private final static int REQUEST_CLIENT_WIFI = 1;

    public final static int DEVICE_BAND = 10;
    public final static int DEVICE_CAMERA = 11;

    @BindView(R.id.hotspot) public TextView tvHotspot;
    @BindView(R.id.dest_wifi) public TextView tvDestWifi;
    @BindView(R.id.hotspot_tip) public TextView tvHotspotTip;
    @BindView(R.id.dest_wifi_tip) public TextView tvDestWifiTip;
    @BindView(R.id.match) public Button btnMatch;
    private View rootView;

    private int mDeviceType;
    private boolean shouldCheckWifiState = false;

    private ScanResult mHotspot;
    private ScanResult mDestWifi;
    private String mHotspotPassword;
    private String mDestWifiPassword;
    private WifiManager mWifiManager;
    private BroadcastReceiver mReceiver;
    private OnMatchFinishedListener mOnMatchFinishedListener;

    public interface OnMatchFinishedListener {
        void onSuccess();
        void onFailed(String message);
    }

    public class MatchHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            boolean isSucceed;
            String message = null;
            switch (msg.what) {
                case 0:  isSucceed = true; break;
                case 1:
                    isSucceed = false;
                    switch (mDeviceType) {
                        case DEVICE_BAND:   message = getString(R.string.error_match_match_action_refused); break;
                        case DEVICE_CAMERA: message = getString(R.string.error_match_exec_commands_failed); break;
                    }
                    break;
                case -1: isSucceed = false; message = getString(R.string.error_match_failed_to_connect_to_device); break;
                default: isSucceed = false; message = getString(R.string.error_match_unknown_match_error); break;
            }
            if (mOnMatchFinishedListener != null) {
                if (isSucceed) {
                    mOnMatchFinishedListener.onSuccess();
                } else {
                    mOnMatchFinishedListener.onFailed(message);
                }
            }
            btnMatch.setEnabled(true);
            btnMatch.setText(getString(R.string.common_match));
        }
    }

    public MatchDeviceFragment() {}

    public void setOnMatchFinishedListener(OnMatchFinishedListener listener) {
        this.mOnMatchFinishedListener = listener;
    }

    private void afterConnection(String hotspotIp) {
        Runnable matcher;
        Handler handler = new MatchHandler();
        int sensorId = ((App) getActivity().getApplicationContext()).getSensorId();
        switch (mDeviceType) {
            case DEVICE_BAND: matcher = new BandTcpMatcher(hotspotIp, sensorId, mDestWifi, mDestWifiPassword, handler); break;
            case DEVICE_CAMERA: matcher = new CameraSshMatcher(sensorId, mDestWifi, mDestWifiPassword, handler); break;
            default: return;
        }
        new Thread(matcher).start();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        mDeviceType = args.getInt("type");
        mWifiManager = (WifiManager) getActivity().getSystemService(Context.WIFI_SERVICE);

        mReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                if (!shouldCheckWifiState || mHotspot == null || mDestWifi == null) {
                    return;
                }
                NetworkInfo networkInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                if (networkInfo != null && networkInfo.isConnected()) {
                    String SSID = mHotspot.SSID;
                    String altSSID = String.format("\"%s\"", SSID);
                    String curSSID = mWifiManager.getConnectionInfo().getSSID();
                    if (SSID.equals(curSSID) || altSSID.equals(curSSID)) {
                        DhcpInfo dhcp = mWifiManager.getDhcpInfo();
                        afterConnection(Formatter.formatIpAddress(dhcp.gateway));
                        shouldCheckWifiState = false;
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
        if (mReceiver != null) {
            getActivity().unregisterReceiver(mReceiver);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_match_device, container, false);
        ButterKnife.bind(this, rootView);

        String deviceType = "";
        switch (mDeviceType) {
            case DEVICE_BAND: deviceType = getString(R.string.common_band); break;
            case DEVICE_CAMERA: deviceType = getString(R.string.common_camera); break;
        }
        tvHotspotTip.setText(String.format(getString(R.string.tip_connect_to), deviceType));
        tvDestWifiTip.setText(String.format(getString(R.string.tip_client_wifi), deviceType));
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
                mHotspot = data.getParcelableExtra("wifi");
                mHotspotPassword = data.getStringExtra("password");
                tvHotspot.setText(mHotspot.SSID);
                break;
            case REQUEST_CLIENT_WIFI:
                mDestWifi = data.getParcelableExtra("wifi");
                mDestWifiPassword = data.getStringExtra("password");
                tvDestWifi.setText(mDestWifi.SSID);
                break;
        }
        checkReadyState();
    }

    private void checkReadyState() {
        if (shouldCheckWifiState || mHotspot == null || mHotspotPassword == null || mDestWifi == null || mDestWifiPassword == null) {
            btnMatch.setEnabled(false);
        } else {
            btnMatch.setEnabled(true);
            btnMatch.setText(getString(R.string.common_match));
        }
    }

    @OnClick(R.id.match)
    public void OnMatchClient() {
        boolean connectResult = false;
        btnMatch.setEnabled(false);
        btnMatch.setText(getString(R.string.common_matching));
        WifiConfiguration configuration = WifiUtils.getWifiConfiguration(mHotspot, mHotspotPassword);
        if (configuration == null) {
            return;
        }
        // Clear existed network with same SSID
        for(WifiConfiguration i : mWifiManager.getConfiguredNetworks() ) {
            if(i.SSID != null && i.SSID.equals("\"" + mHotspot.SSID + "\"")) {
                mWifiManager.removeNetwork(i.networkId);
            }
        }
        mWifiManager.addNetwork(configuration);
        for(WifiConfiguration i : mWifiManager.getConfiguredNetworks() ) {
            if(i.SSID != null && i.SSID.equals("\"" + mHotspot.SSID + "\"")) {
                mWifiManager.disconnect();
                mWifiManager.enableNetwork(i.networkId, true);
                connectResult = mWifiManager.reconnect();
                shouldCheckWifiState = true;
                break;
            }
        }
        if (!connectResult) {
            shouldCheckWifiState = false;
            Snackbar.make(rootView, String.format(getString(R.string.error_wifi_cannot_connect_to), mHotspot.SSID), Snackbar.LENGTH_LONG).show();
        }
    }
}
