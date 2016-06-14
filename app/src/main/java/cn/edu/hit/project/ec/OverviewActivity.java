package cn.edu.hit.project.ec;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import cn.edu.hit.project.ec.loaders.data.BaseDataLoader;
import cn.edu.hit.project.ec.models.user.User;
import cn.edu.hit.project.ec.network.NetworkStateMonitor;
import cn.edu.hit.project.ec.views.adapters.SectionsPagerAdapter;

public class OverviewActivity extends AppCompatActivity
        implements BaseDataLoader.OnLoadFailedListener, NetworkStateMonitor.OnNetworkStateChangeListener {
    private View mRootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview);

        mRootView = ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        if (intent != null) {
            if (intent.getBooleanExtra("EXIT", false)) {
                logout();
            }
        }

        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(),
                getResources().getStringArray(R.array.overview_tab_title));
        ViewPager mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_overview, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_camera) {
            startActivity(new Intent(this, VideoActivity.class));
        } else if (id == R.id.action_match) {
            startActivity(new Intent(this, MatchActivity.class));
        } else if (id == R.id.action_logout) {
            logout();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onLoadFailed() {
        logout();
    }

    public void logout() {
        User.logout(PreferenceManager.getDefaultSharedPreferences(this));
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        NetworkStateMonitor.registerListener(this);
        NetworkStateMonitor.checkNetworkState(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        NetworkStateMonitor.unregisterListener(this);
    }

    @Override
    public void onConnect() {}

    @Override
    public void onDisconnect() {
        Snackbar.make(mRootView, getString(R.string.error_no_network), Snackbar.LENGTH_LONG).show();
    }
}
