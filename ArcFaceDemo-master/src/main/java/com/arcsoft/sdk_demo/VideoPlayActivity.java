package com.arcsoft.sdk_demo;

import android.app.Activity;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.MediaController;
import android.widget.VideoView;

import java.io.File;

public class VideoPlayActivity extends Activity {
    private final String TAG = this.getClass().toString();
    String mFilePath;
    String mFileName;
    private VideoView videoView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_play);
        if (!getIntentData(getIntent().getExtras())) {
            Log.e(TAG, "getIntentData fail!");
            this.finish() ;
        }
        Log.d(TAG,"i enter play");
        videoView=(VideoView)findViewById(R.id.videoView);
        videoView.setVideoPath(mFilePath);
        MediaController mediaController=new MediaController(this);
        mediaController.setMediaPlayer(videoView);
        videoView.setMediaController(mediaController);
    }
    private boolean getIntentData(Bundle bundle) {
        try {

            mFileName=bundle.getString("VideoName");
            mFilePath=new File(Environment.getExternalStorageDirectory(),
                    "out"+mFileName).toString();
            if (mFilePath == null || mFilePath.isEmpty()) {
                return false;
            }
            Log.i(TAG, "getIntentData:" + mFilePath);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
