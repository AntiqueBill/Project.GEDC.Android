package cn.edu.hit.project.ec;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
<<<<<<< HEAD
import android.os.Handler;
import android.os.Message;
=======
>>>>>>> e504fc4cb061d82be55349a32b3791fb9376ba73
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
<<<<<<< HEAD
import android.view.View;
import android.view.ViewGroup;

import java.util.regex.Matcher;

import cn.edu.hit.project.ec.network.CameraSshService;
import cn.edu.hit.project.ec.utils.WifiUtils;
=======
import android.view.ViewGroup;

>>>>>>> e504fc4cb061d82be55349a32b3791fb9376ba73
import cn.edu.hit.project.ec.views.fragments.EnableWifiFragment;
import cn.edu.hit.project.ec.views.fragments.MatchWifiFragment;

public class MatchActivity extends AppCompatActivity
        implements EnableWifiFragment.OnWifiEnabledListener, MatchWifiFragment.OnWifiMatchedListener {
    private int step;
<<<<<<< HEAD
    private View rootView;
=======
>>>>>>> e504fc4cb061d82be55349a32b3791fb9376ba73

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match);
<<<<<<< HEAD
        rootView = (ViewGroup) ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0);
=======
>>>>>>> e504fc4cb061d82be55349a32b3791fb9376ba73

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
<<<<<<< HEAD
    public void onWifiMatched(String ip, ScanResult wifi, String password) {
        switch (step - 1) {
            case 1: setCamera(wifi, password); break;
            case 2: break;
        }
    }

    public static String escapeString(String s) {
        return s.replaceAll(Matcher.quoteReplacement("\\"), Matcher.quoteReplacement("\\\\\\\\"))
                .replaceAll("/", Matcher.quoteReplacement("\\\\/"));
    }

    /**
     * Write config to camera through SSH
     */
    public void setCamera(ScanResult wifi, String password) {
        final String command = "sed -i \"s:^STREAM_SERVER=.*:STREAM_SERVER=" + escapeString(App.STREAM_SERVER) + ":g\" /etc/init.d/streaming.sh && " +
                "sed -i \"s:^STREAM_ID=.*:STREAM_ID=" + ((App) getApplicationContext()).getSensorId() + ":g\" /etc/init.d/streaming.sh && " +
                "sed -ire \"s:(^ssid\\s*=\\s*)(.*):\\1" + escapeString(wifi.SSID) + ":g\" /etc/jffs2/camera.ini && " +
                "sed -ire \"s:(^security\\s*=\\s*)(.*):\\1" + escapeString(WifiUtils.getWifiSecurityString(wifi)) + ":g\" /etc/jffs2/camera.ini && " +
                "sed -ire \"s:(^password\\s*=\\s*)(.*):\\1" + escapeString(password) +  ":g\" /etc/jffs2/camera.ini && " +
                "sed -ire \"s:(^running\\s*=\\s*)(.*):\\1station:g\" /etc/jffs2/camera.ini && " +
                "/sbin/reboot";

        new Thread(new CameraSshService(command, new Handler() {
            @Override
            public void handleMessage(Message msg) {
                String message;
                switch (msg.what) {
                    case -1: message = getString(R.string.error_match_enter_device_failed); break;
                    case 0:  message = getString(R.string.tip_match_success); next(); break;
                    default: message = getString(R.string.error_match_exec_commands_failed); break;
                }
                Snackbar.make(rootView, String.format(message, getString(R.string.common_camera)), Snackbar.LENGTH_LONG).show();
            }
        })).start();
=======
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
>>>>>>> e504fc4cb061d82be55349a32b3791fb9376ba73
    }
}
