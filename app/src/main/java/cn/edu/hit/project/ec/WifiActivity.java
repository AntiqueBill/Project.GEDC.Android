package cn.edu.hit.project.ec;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.edu.hit.project.ec.models.wifi.WifiSecurityType;
import cn.edu.hit.project.ec.utils.WifiUtils;
import cn.edu.hit.project.ec.views.adapters.WifiListAdapter;

public class WifiActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private final static int REQUEST_NETWORK_PERMISSIONS = 0;

    @BindView(R.id.list) public ListView mList;

    private WifiManager mWifiManager;
    private List<ScanResult> mWifiList;
    private List<String> mWifiNames;
    private List<Integer> mWifiSignalLevels;
    private List<Boolean> mWifiLocks;
    private WifiListAdapter mListAdapter;
    private BroadcastReceiver mWifiReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi);

        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mWifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);

        mWifiNames = new ArrayList<>();
        mWifiSignalLevels = new ArrayList<>();
        mWifiLocks = new ArrayList<>();

        mWifiReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context c, Intent intent) {
                mWifiList = mWifiManager.getScanResults();
                mWifiNames.clear();
                mWifiSignalLevels.clear();
                mWifiLocks.clear();
                for (ScanResult wifi : mWifiList) {
                    mWifiNames.add(wifi.SSID);
                    mWifiSignalLevels.add(WifiManager.calculateSignalLevel(wifi.level, 5));
                    mWifiLocks.add(WifiUtils.getWifiSecurity(wifi) != WifiSecurityType.NONE);
                }
                mListAdapter.notifyDataSetChanged();
            }
        };

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_NETWORK_PERMISSIONS);
        } else {
            if (!mWifiManager.isWifiEnabled()) {
                mWifiManager.setWifiEnabled(true);
            }
            registerReceiver(mWifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
            mWifiManager.startScan();
        }

        mListAdapter = new WifiListAdapter(this, mWifiNames, mWifiSignalLevels, mWifiLocks);
        mList.setAdapter(mListAdapter);
        mList.setOnItemClickListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            unregisterReceiver(mWifiReceiver);
        } catch (IllegalArgumentException ignored) {}
    }

    public void returnResult(ScanResult wifi, String password) {
        Intent intent = new Intent();
        intent.putExtra("wifi", wifi);
        intent.putExtra("password", password);
        setResult(0, intent);
        finish();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (position >= mWifiList.size()) {
            return;
        }
        final ScanResult wifi = mWifiList.get(position);
        if (WifiUtils.getWifiSecurity(wifi) == WifiSecurityType.EAP) {
            ViewGroup rootView = (ViewGroup) ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0);
            Snackbar.make(rootView, getString(R.string.error_wifi_eap_not_supported), Snackbar.LENGTH_LONG).show();
            return;
        }
        if (WifiUtils.getWifiSecurity(wifi) == WifiSecurityType.NONE) {
            returnResult(wifi, "");
            return;
        }
        final TextInputLayout input = (TextInputLayout) getLayoutInflater().inflate(R.layout.dialog_password_input, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.common_connect_to) + wifi.SSID);
        builder.setView(input);
        builder.setPositiveButton(getString(R.string.common_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                returnResult(wifi, input.getEditText().getText().toString());
            }
        });
        builder.setNegativeButton(getString(R.string.common_cancel), null);
        builder.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        ViewGroup rootView = (ViewGroup) ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0);
        switch (requestCode) {
            case REQUEST_NETWORK_PERMISSIONS:
                for (int i: grantResults) {
                    if (i != PackageManager.PERMISSION_GRANTED) {
                        Snackbar.make(rootView, getString(R.string.error_wifi_permission_granted_2), Snackbar.LENGTH_LONG).show();
                        return;
                    }
                }
                registerReceiver(mWifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
                if (!mWifiManager.isWifiEnabled()) {
                    mWifiManager.setWifiEnabled(true);
                }
                mWifiManager.startScan();
        }
    }
}
