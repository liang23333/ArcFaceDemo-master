package com.arcsoft.sdk_demo;

import android.util.Log;

import java.util.List;

class VideoThread extends Thread{
    private boolean flag=false; //标记Video转换是否完成
    private String mFileName;
    private String mFilePath;
    private List<FaceDB.FaceRegist> mResgist;
    private int height0;
    private int width0;
    public void setmFileName(String mFileName) {
        this.mFileName = mFileName;
    }

    public void setmResgist(List<FaceDB.FaceRegist> mResgist) {
        this.mResgist = mResgist;
    }

    public void setmFilePath(String mFilePath) {
        this.mFilePath = mFilePath;
    }

    public void setWidth0(int width0) {
        this.width0 = width0;
    }

    public void setHeight0(int height0) {
        this.height0 = height0;
    }

    @Override
    public void run()
    {
        VideoEncodeDecode videoEncodeDecode=new VideoEncodeDecode();
        videoEncodeDecode.setHeight0(height0);
        videoEncodeDecode.setWidth0(width0);
        videoEncodeDecode.setmFilename(mFileName);
//                videoEncodeDecode.setmFileName(mFileName);
//
        videoEncodeDecode.setmResgist(mResgist);
        videoEncodeDecode.VideoCodecPrepare(mFilePath);
        videoEncodeDecode.VideoEncodeDecodeLoop();
        videoEncodeDecode.close();
        flag=true;
        Log.d("VideoThread","i have finish");
    }
    public boolean getFlag(){
        return flag;
    }
}