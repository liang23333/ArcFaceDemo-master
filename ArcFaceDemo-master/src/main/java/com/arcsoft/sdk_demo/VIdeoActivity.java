


package com.arcsoft.sdk_demo;

        import android.content.Intent;
        import android.graphics.BitmapFactory;
        import android.graphics.ImageFormat;
        import android.graphics.YuvImage;
        import android.nfc.Tag;
        import android.os.Handler;
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
        import android.widget.ProgressBar;
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


public class VIdeoActivity extends Activity implements View.OnClickListener{
    private final String TAG = this.getClass().toString();

    private String 	mFilePath;
    private String mFileName;
    private Bitmap mBitmap;
    private Thread view;
    private VideoThread videoThread;
    private ImageView imageView ;
    private ImageView imageViewk ;
    private AFR_FSDKFace mAFR_FSDKFace;
    private AFD_FSDKFace mAFD_FSDKFace;
    private Bitmap bmp;
    private TextView mTextView;
    private int left,right,top,bottom;
    private Canvas face_canvas;
    private String gender,age;
    private DisplayMetrics outMetrics;
    private static final int REQUEST_VIDEO = 1;
    private int status=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.activity_video);
        if (!getIntentData(getIntent().getExtras())) {
            Log.e(TAG, "getIntentData fail!");
            this.finish() ;
        }
        WindowManager wm = (WindowManager) this
                .getSystemService(Context.WINDOW_SERVICE);
        WindowManager manager = this.getWindowManager();
        outMetrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(outMetrics);
        final int width0 = outMetrics.widthPixels;
        final int height0 = outMetrics.heightPixels;
        List<FaceDB.FaceRegist> mResgist = ((Application)VIdeoActivity.this.getApplicationContext()).mFaceDB.mRegister;

        final ProgressBar bar=(ProgressBar)findViewById(R.id.bar);
        final Handler mHandler=new Handler(){
            @Override
            public void handleMessage(Message message){
                if(message.what==0x111){
                    Log.d(TAG,"status "+ Integer.toString(status));
                    bar.setProgress(status);
                }
            }
        };
        final TextView mTextView=(TextView)findViewById(R.id.textView3);
        final Handler handler=new Handler(){
            @Override
            public void handleMessage(Message message){
                if(message.what==0x111){
                    Log.d(TAG,"textView ok");
                    mTextView.setText("转换成功，请查看");
                }
            }
        };
        View v = this.findViewById(R.id.button10);
        v.setOnClickListener(this);
        videoThread=new VideoThread();
        videoThread.setHeight0(height0);
        videoThread.setmFileName(mFileName);
        videoThread.setmFilePath(mFilePath);
        videoThread.setmResgist(mResgist);
        videoThread.setWidth0(width0);
        videoThread.start();
        new Thread(){
            public void run()
            {
                while(status<100){
                    if(videoThread.getFlag()==false){
                        if(status<92)
                        {
                            status+=1;
                            try{
                                Thread.sleep(500);
                            }catch (InterruptedException e)
                            {
                                e.printStackTrace();
                            }
                        }
                    }
                    else
                    {
                        status=100;
                        Message m=new Message();
                        m.what=0x111;
                        handler.sendMessage(m);
                    }
                    Message m=new Message();
                    m.what=0x111;
                    mHandler.sendMessage(m);

                }

            }
        }.start();





    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_VIDEO && resultCode == RESULT_OK) {
            Log.d(TAG,"i enter ActivityResult");
            if(!this.isFinishing()){
                this.finish();
                view.stop();
            }
        }
    }
    private boolean getIntentData(Bundle bundle) {
        try {
            mFilePath = bundle.getString("VideoPath");
            mFileName=bundle.getString("VideoName");
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

    public void onClick(View paramView) {
        // TODO Auto-generated method stub
        switch (paramView.getId()) {
            case R.id.button10:
                if(status==100){
                Intent it = new Intent(VIdeoActivity.this, VideoPlayActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("VideoName", mFileName);
                it.putExtras(bundle);
                startActivity(it);
                finish();}
                else
                    {
                        Toast toast=Toast.makeText(getApplicationContext(),"请稍等，视频还未处理完成",Toast.LENGTH_SHORT);
                        toast.show();
                    }
                break;
            default:;
        }
    }
}
