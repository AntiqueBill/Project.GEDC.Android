package cn.edu.hit.project.ec;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.widget.VideoView;

import java.util.Locale;

public class VideoActivity extends Activity {
    private VideoView mVideoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        @SuppressLint("DefaultLocale")
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
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mVideoView != null) {
            mVideoView.resume();
        }
    }
}
