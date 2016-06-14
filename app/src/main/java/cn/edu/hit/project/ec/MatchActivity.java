package cn.edu.hit.project.ec;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import cn.edu.hit.project.ec.views.fragments.EnableWifiFragment;
import cn.edu.hit.project.ec.views.fragments.MatchCompleteFragment;
import cn.edu.hit.project.ec.views.fragments.MatchDeviceFragment;

public class MatchActivity extends AppCompatActivity
        implements EnableWifiFragment.OnWifiEnabledListener, MatchDeviceFragment.OnMatchFinishedListener {
    private int step;
    private View rootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match);
        rootView = ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        step = wifiManager.isWifiEnabled() ? 1 : 0;
        next();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    public void next() {
        Fragment fragment = null;
        EnableWifiFragment ewf;
        MatchDeviceFragment swf;
        Bundle args = new Bundle();
        switch (step) {
            case 0:
                ewf = new EnableWifiFragment();
                ewf.setOnWifiEnabledListener(this);
                fragment = ewf;
                break;
            case 1:
                swf = new MatchDeviceFragment();
                args.putInt("type", MatchDeviceFragment.DEVICE_BAND);
                swf.setArguments(args);
                swf.setOnMatchFinishedListener(this);
                fragment = swf;
                break;
            case 2:
                swf = new MatchDeviceFragment();
                args.putInt("type", MatchDeviceFragment.DEVICE_CAMERA);
                swf.setArguments(args);
                swf.setOnMatchFinishedListener(this);
                fragment = swf;
                break;
            case 3:
                fragment = new MatchCompleteFragment();
                break;
        }
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.enter, R.anim.exit)
                    .replace(R.id.container, fragment)
                    .commit();
        }
        step++;
    }

    @Override
    public void onWifiEnabled() {
        next();
    }

    private String getDeviceType() {
        String deviceType = null;
        switch (step - 1) {
            case 1: deviceType = getString(R.string.common_band); break;
            case 2: deviceType = getString(R.string.common_camera); break;
        }
        return deviceType;
    }

    @Override
    public void onSuccess() {
        if (getDeviceType() != null) {
            Snackbar.make(rootView, String.format(getString(R.string.tip_match_success), getDeviceType()), Snackbar.LENGTH_LONG).show();
            next();
        }
    }

    @Override
    public void onFailed(String message) {
        if (getDeviceType() != null) {
            Snackbar.make(rootView, String.format(message, getDeviceType()), Snackbar.LENGTH_LONG).show();
        }
    }
}
