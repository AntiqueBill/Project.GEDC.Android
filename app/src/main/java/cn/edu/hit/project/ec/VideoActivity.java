package cn.edu.hit.project.ec;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.widget.VideoView;

public class VideoActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        String fileName = "android.resource://" +  getPackageName() + "/raw/small";
        VideoView videoView = (VideoView) this.findViewById(R.id.videoView);
        videoView.setVideoURI(Uri.parse(fileName));
        videoView.start();
    }
}
