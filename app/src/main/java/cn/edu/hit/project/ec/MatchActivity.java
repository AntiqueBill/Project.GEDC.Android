package cn.edu.hit.project.ec;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.ViewGroup;

import cn.edu.hit.project.ec.views.fragments.EnableWifiFragment;
import cn.edu.hit.project.ec.views.fragments.MatchWifiFragment;

public class MatchActivity extends AppCompatActivity
        implements EnableWifiFragment.OnWifiEnabledListener, MatchWifiFragment.OnWifiMatchedListener {
    private int step;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match);

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
        MatchWifiFragment swf;
        Bundle args = new Bundle();
        switch (step) {
            case 0:
                ewf = new EnableWifiFragment();
                ewf.setOnWifiEnabledListener(this);
                fragment = ewf;
                break;
            case 1:
                swf = new MatchWifiFragment();
                args.putString("type", getString(R.string.common_band));
                swf.setArguments(args);
                swf.setOnWifiSelectedListener(this);
                fragment = swf;
                break;
            case 2:
                swf = new MatchWifiFragment();
                args.putString("type", getString(R.string.common_camera));
                swf.setArguments(args);
                swf.setOnWifiSelectedListener(this);
                fragment = swf;
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

    @Override
    public void onWifiMatched(ScanResult wifi, String password) {
        String deviceType = null;
        switch (step - 1) {
            case 1: deviceType = getString(R.string.common_band); break;
            case 2: deviceType = getString(R.string.common_camera); break;
            default: return;
        }
        ViewGroup rootView = (ViewGroup) ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0);
        Snackbar.make(rootView, String.format(getString(R.string.tip_match_success), deviceType), Snackbar.LENGTH_LONG).show();
        next();
    }
}
