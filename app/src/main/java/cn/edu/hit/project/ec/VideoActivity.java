package cn.edu.hit.project.ec;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.VideoView;

import cn.edu.hit.project.ec.network.NetworkStateMonitor;

public class VideoActivity extends Activity implements NetworkStateMonitor.OnNetworkStateChangeListener {
    private View mRootView;
    private VideoView mVideoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        mRootView = ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0);

        String fileName = String.format("rtsp://115.28.134.90/camera-%d.sdp",
                ((App) getApplicationContext()).getSensorId());
        mVideoView = (VideoView) this.findViewById(R.id.videoView);
        mVideoView.setVideoURI(Uri.parse(fileName));
        mVideoView.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mVideoView != null && mVideoView.canPause()) {
            mVideoView.pause();
        }
        NetworkStateMonitor.registerListener(this);
        NetworkStateMonitor.checkNetworkState(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mVideoView != null) {
            mVideoView.resume();
        }
        NetworkStateMonitor.unregisterListener(this);
    }

    @Override
    public void onConnect() {}

    @Override
    public void onDisconnect() {
        Snackbar.make(mRootView, getString(R.string.error_no_network), Snackbar.LENGTH_LONG).show();
    }
}
