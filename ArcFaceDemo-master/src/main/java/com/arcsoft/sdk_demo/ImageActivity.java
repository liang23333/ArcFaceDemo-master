package com.arcsoft.sdk_demo;

import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.YuvImage;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Message;
import android.text.InputFilter;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.arcsoft.ageestimation.ASAE_FSDKAge;
import com.arcsoft.ageestimation.ASAE_FSDKEngine;
import com.arcsoft.ageestimation.ASAE_FSDKError;
import com.arcsoft.ageestimation.ASAE_FSDKFace;
import com.arcsoft.ageestimation.ASAE_FSDKVersion;
import com.arcsoft.facedetection.AFD_FSDKEngine;
import com.arcsoft.facedetection.AFD_FSDKError;
import com.arcsoft.facedetection.AFD_FSDKFace;
import com.arcsoft.facedetection.AFD_FSDKVersion;
import com.arcsoft.facerecognition.AFR_FSDKEngine;
import com.arcsoft.facerecognition.AFR_FSDKError;
import com.arcsoft.facerecognition.AFR_FSDKFace;
import com.arcsoft.facerecognition.AFR_FSDKMatching;
import com.arcsoft.facerecognition.AFR_FSDKVersion;
import com.arcsoft.facetracking.AFT_FSDKEngine;
import com.arcsoft.genderestimation.ASGE_FSDKEngine;
import com.arcsoft.genderestimation.ASGE_FSDKError;
import com.arcsoft.genderestimation.ASGE_FSDKFace;
import com.arcsoft.genderestimation.ASGE_FSDKGender;
import com.arcsoft.genderestimation.ASGE_FSDKVersion;
import com.guo.android_extend.image.ImageConverter;
import com.guo.android_extend.java.ExtByteArrayOutputStream;
import com.guo.android_extend.widget.ExtImageView;
import com.guo.android_extend.widget.HListView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class ImageActivity extends Activity {
    private final String TAG = this.getClass().toString();
    private String 	mFilePath;
    private Bitmap mBitmap;
    private Thread view;
    private ImageView imageView ;
    private ImageView imageViewk ;
    private AFR_FSDKFace mAFR_FSDKFace;
    private AFD_FSDKFace mAFD_FSDKFace;
    private Bitmap bmp;
    private TextView mTextView;
    private int left,right,top,bottom;
    private Canvas face_canvas;
    private String gender,age;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.imagelayout);
        long t1=System.currentTimeMillis();//initial data.
        imageView = (ImageView) findViewById(R.id.imageviewk);
        if (!getIntentData(getIntent().getExtras())) {
            Log.e(TAG, "getIntentData fail!");
            this.finish() ;
        }
        mBitmap = Application.decodeImage(mFilePath);
        /* 将Bitmap设定到ImageView */
        List<FaceDB.FaceRegist> mResgist = ((Application)ImageActivity.this.getApplicationContext()).mFaceDB.mRegister;

        WindowManager wm = (WindowManager) this
                .getSystemService(Context.WINDOW_SERVICE);
        WindowManager manager = this.getWindowManager();
        DisplayMetrics outMetrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(outMetrics);
        int width0 = outMetrics.widthPixels;
        int height0 = outMetrics.heightPixels;
        System.out.println("hhahah");
                byte[] data = new byte[mBitmap.getWidth() * mBitmap.getHeight() * 3 / 2];
                try {
                    ImageConverter convert = new ImageConverter();
                    convert.initial(mBitmap.getWidth(), mBitmap.getHeight(), ImageConverter.CP_PAF_NV21);
                    if (convert.convert(mBitmap, data)) {
                        Log.d(TAG, "convert ok!");
                        System.out.println("convert ok");
                    }
                    convert.destroy();
                } catch (Exception e) {
                    System.out.println("convert fail");
                    Log.d(TAG,"convert fail");
                    e.printStackTrace();
                }

                Log.d("width height",Integer.toString(mBitmap.getWidth())+" picture "+Integer.toString(mBitmap.getHeight()));
                ImageProcess ip=new ImageProcess(mBitmap,data,mResgist,height0,width0);
                ip.start();
                bmp=ip.getmBitmap();

        imageView.setImageBitmap(bmp);
        long t2=System.currentTimeMillis();
        Log.d("com.arcsoft",String.valueOf(t2-t1));
    }
    private boolean getIntentData(Bundle bundle) {
        try {
            mFilePath = bundle.getString("imagePath");
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
