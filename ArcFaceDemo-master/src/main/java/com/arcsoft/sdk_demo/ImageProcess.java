package com.arcsoft.sdk_demo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

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
import com.arcsoft.genderestimation.ASGE_FSDKEngine;
import com.arcsoft.genderestimation.ASGE_FSDKError;
import com.arcsoft.genderestimation.ASGE_FSDKFace;
import com.arcsoft.genderestimation.ASGE_FSDKGender;
import com.arcsoft.genderestimation.ASGE_FSDKVersion;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ImageProcess {
    private Bitmap mBitmap;
    private byte[] data;
    private final String TAG = this.getClass().getSimpleName();
    private AFR_FSDKFace mAFR_FSDKFace;
    private AFD_FSDKFace mAFD_FSDKFace;
    private int left,right,top,bottom;
    private Canvas face_canvas;
    private String gender,age;
    List<FaceDB.FaceRegist> mResgist ;
    private List<ProcessOut> OutList=new LinkedList<ProcessOut>();
    private int height0,width0;
    public ImageProcess(Bitmap bmp,byte[] dat,List<FaceDB.FaceRegist> mresgist,int hh,int ww)
    {
        mBitmap=bmp.copy(Bitmap.Config.ARGB_8888,true);

        if(mBitmap==null){
            Log.d("test null","yes is null");
        }
        else{
            Log.d("test null","no isnt null");
        }
        data=dat;
        mResgist=mresgist;
        height0=hh;
        width0=ww;
    }

    public Bitmap getmBitmap() {
        return mBitmap;
    }
    public void remove(){
        if(mBitmap!= null && !mBitmap.isRecycled()){
            mBitmap.recycle();
            mBitmap = null;
        }
        System.gc();
    }

    public List<ProcessOut> getOutList(){
        return OutList;
    }
    public byte[] getData() {
        return data;
    }
    public static void saveImage(Bitmap bmp) {
        File appDir = new File(Environment.getExternalStorageDirectory(), "Boohee");
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = System.currentTimeMillis() + ".jpeg";
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 80, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void start(){


        AFD_FSDKEngine engine = new AFD_FSDKEngine();
        AFD_FSDKVersion version = new AFD_FSDKVersion();
        List<AFD_FSDKFace> result = new ArrayList<AFD_FSDKFace>();
        AFD_FSDKError err = engine.AFD_FSDK_InitialFaceEngine(FaceDB.appid, FaceDB.fd_key, AFD_FSDKEngine.AFD_OPF_0_HIGHER_EXT, 16, 5);
        Log.d(TAG, "AFD_FSDK_InitialFaceEngine = " + err.getCode());
        err = engine.AFD_FSDK_GetVersion(version);
        Log.d(TAG, "AFD_FSDK_GetVersion =" + version.toString() + ", " + err.getCode());
        err  = engine.AFD_FSDK_StillImageFaceDetection(data, mBitmap.getWidth(), mBitmap.getHeight(), AFD_FSDKEngine.CP_PAF_NV21, result);
        Log.d(TAG, "AFD_FSDK_StillImageFaceDetection =" + err.getCode() + "<" + result.size());

        if (!result.isEmpty()) {
            for (int i = 0; i < result.size(); i++) {
                ProcessOut one=new ProcessOut();
                mAFD_FSDKFace = result.get(i).clone();
                AFR_FSDKVersion version1 = new AFR_FSDKVersion();
                AFR_FSDKEngine engine1 = new AFR_FSDKEngine();
                AFR_FSDKFace result1 = new AFR_FSDKFace();
                AFR_FSDKError error1 = engine1.AFR_FSDK_InitialEngine(FaceDB.appid, FaceDB.fr_key);
                Log.d("com.arcsoft", "AFR_FSDK_InitialEngine = " + error1.getCode());
                if (error1.getCode() != AFD_FSDKError.MOK) {
                    Log.d("com.arcsoft", "FR initial error");
                }
                error1 = engine1.AFR_FSDK_GetVersion(version1);
                Log.d("com.arcsoft", "FR=" + version.toString() + "," + error1.getCode()); //(210, 178 - 478, 446), degree = 1　780, 2208 - 1942, 3370
                error1 = engine1.AFR_FSDK_ExtractFRFeature(data, mBitmap.getWidth(), mBitmap.getHeight(), AFR_FSDKEngine.CP_PAF_NV21, new Rect(result.get(i).getRect()), result.get(i).getDegree(), result1);
                Log.d("com.arcsoft", "Face=" + result1.getFeatureData()[0] + "," + result1.getFeatureData()[1] + "," + result1.getFeatureData()[2] + "," + error1.getCode());
                if (error1.getCode() == error1.MOK) {
                    mAFR_FSDKFace = result1.clone();
                    left = mAFD_FSDKFace.getRect().left;
                    top = mAFD_FSDKFace.getRect().top;
                    bottom = mAFD_FSDKFace.getRect().bottom;
                    right = mAFD_FSDKFace.getRect().right;
                    Log.d("com.arcsoft", "left, " + Integer.toString(left));
                    Log.d("com.arcsoft", "right," + Integer.toString(right));
                    Log.d("com.arcsoft", "top, " + Integer.toString(top));
                    Log.d("com.arcsoft", "bottom, " + Integer.toString(bottom));
                    face_canvas = new Canvas(mBitmap);
                    Paint paint = new Paint();
                    paint.setColor(Color.BLUE);
                    paint.setStyle(Paint.Style.STROKE);//不填充
                    paint.setStrokeWidth(2);
                    face_canvas.drawRect(left, top, right, bottom, paint);
                    one.setRect(mAFD_FSDKFace.getRect());
                    List<ASAE_FSDKFace> face1 = new ArrayList<>();
                    List<ASGE_FSDKFace> face2 = new ArrayList<>();
                    ASAE_FSDKVersion mAgeVersion = new ASAE_FSDKVersion();
                    ASAE_FSDKEngine mAgeEngine = new ASAE_FSDKEngine();
                    ASGE_FSDKVersion mGenderVersion = new ASGE_FSDKVersion();
                    ASGE_FSDKEngine mGenderEngine = new ASGE_FSDKEngine();

                    ASAE_FSDKError err3 = mAgeEngine.ASAE_FSDK_InitAgeEngine(FaceDB.appid, FaceDB.age_key);
                    Log.d(TAG, "ASAE_FSDK_InitAgeEngine =" + err3.getCode());
                    err3 = mAgeEngine.ASAE_FSDK_GetVersion(mAgeVersion);
                    Log.d(TAG, "ASAE_FSDK_GetVersion:" + mAgeVersion.toString() + "," + err3.getCode());

                    ASGE_FSDKError err4 = mGenderEngine.ASGE_FSDK_InitgGenderEngine(FaceDB.appid, FaceDB.gender_key);
                    Log.d(TAG, "ASGE_FSDK_InitgGenderEngine =" + err4.getCode());
                    err4 = mGenderEngine.ASGE_FSDK_GetVersion(mGenderVersion);
                    Log.d(TAG, "ASGE_FSDK_GetVersion:" + mGenderVersion.toString() + "," + err4.getCode());
                    List<ASAE_FSDKAge> ages = new ArrayList<>();
                    List<ASGE_FSDKGender> genders = new ArrayList<>();
                    face1.clear();
                    face2.clear();
                    face1.add(new ASAE_FSDKFace(mAFD_FSDKFace.getRect(), mAFD_FSDKFace.getDegree()));
                    face2.add(new ASGE_FSDKFace(mAFD_FSDKFace.getRect(), mAFD_FSDKFace.getDegree()));
                    ASAE_FSDKError err1 = mAgeEngine.ASAE_FSDK_AgeEstimation_Image(data, mBitmap.getWidth(), mBitmap.getHeight(), AFR_FSDKEngine.CP_PAF_NV21, face1, ages);
                    ASGE_FSDKError err2 = mGenderEngine.ASGE_FSDK_GenderEstimation_Image(data, mBitmap.getWidth(), mBitmap.getHeight(), AFR_FSDKEngine.CP_PAF_NV21, face2, genders);
                    Log.d(TAG, "ASAE_FSDK_AgeEstimation_Image:" + err1.getCode() + ",ASGE_FSDK_GenderEstimation_Image:" + err2.getCode());
                    Log.d(TAG, "age:" + ages.get(0).getAge() + ",gender:" + genders.get(0).getGender());
                    age = ages.get(0).getAge() == 0 ? "年龄未知" : ages.get(0).getAge() + "岁";
                    gender = genders.get(0).getGender() == -1 ? "性别未知" : (genders.get(0).getGender() == 0 ? "男" : "女");
                    err1 = mAgeEngine.ASAE_FSDK_UninitAgeEngine();
                    Log.d("com.arcsoft", "ASAE_FSDK_UninitAgeEngine =" + err.getCode());
                    err2=mGenderEngine.ASGE_FSDK_UninitGenderEngine();



                    Log.d("com.arcsoft", "FR extract success");



                    AFR_FSDKMatching score = new AFR_FSDKMatching();
                    float max = 0.0f;
                    String name = null;
                    for (FaceDB.FaceRegist fr : mResgist) {
                        for (AFR_FSDKFace face : fr.mFaceList) {
                            error1 = engine1.AFR_FSDK_FacePairMatching(result1, face, score);
                            Log.d(TAG, "Score:" + score.getScore() + ", AFR_FSDK_FacePairMatching=" + error1.getCode() + ",with " + fr.mName);
                            if (max < score.getScore()) {
                                max = score.getScore();
                                name = fr.mName;
                            }
                        }
                    }
                    if (max > 0.5f) {
                        String testString = "测试：gafaeh:1234";
                        Paint mPaint = new Paint();
                        mPaint.setStrokeWidth(2);
                        mPaint.setTextSize(50);
                        mPaint.setColor(Color.BLUE);
                        Rect bounds = new Rect();
                        mPaint.getTextBounds(testString, 0, testString.length(), bounds);
                        float picbottom = (height0 - mBitmap.getHeight()) / 2 + mBitmap.getHeight();

                        final float max_score = max;
                        Log.d(TAG, "fit Score:" + max + ", NAME:" + name);
                        final String mNameShow = name;
                        if (bottom + 20 > picbottom) {
                            face_canvas.drawText(mNameShow + ", " + age + ", " + gender, left, top - 20, mPaint);
                            one.setLeft(left);
                            one.setTop(top-20);
                            one.setDesc(mNameShow + ", " + age + ", " + gender);
                        } else
                        {
                            face_canvas.drawText(mNameShow + ", " + age + ", " + gender, left, bottom + 20, mPaint);
                            one.setLeft(left);
                            one.setTop(bottom+20);
                            one.setDesc(mNameShow + ", " + age + ", " + gender);
                        }

                    } else {
                        String testString = "测试：gafaeh:1234哈哈哈";
                        Paint mPaint = new Paint();
                        mPaint.setStrokeWidth(2);
                        mPaint.setTextSize(50);
                        mPaint.setColor(Color.BLUE);
                        Rect bounds = new Rect();
                        mPaint.getTextBounds(testString, 0, testString.length(), bounds);

                        float picbottom = (height0 - mBitmap.getHeight()) / 2 + mBitmap.getHeight();

                        final float max_score = max;
                        Log.d(TAG, "fit Score:" + max + ", NAME:" + name);
                        final String mNameShow = "识别失败，打扰了 ";

                        if (bottom + 20 > picbottom) {
                            face_canvas.drawText(mNameShow + ", " + age + ", " + gender, left, top - 20, mPaint);
                            one.setLeft(left);
                            one.setTop(top-20);
                            one.setDesc(mNameShow + ", " + age + ", " + gender);
                        } else {
                            face_canvas.drawText(mNameShow + ", " + age + ", " + gender, left, bottom + 20, mPaint);

                            one.setLeft(left);
                            one.setTop(bottom+20);
                            one.setDesc(mNameShow + ", " + age + ", " + gender);
                        }
                    }
                    error1 = engine1.AFR_FSDK_UninitialEngine();
                    Log.d("com.arcsoft", "AFR_FSDK_UninitialEngine : " + error1.getCode());
                } else {
                    Log.d("com.arcsoft", "是空的");
                }
                OutList.add(one);
                Log.d("daraole","wo zai ce shi "+one.getDesc());
            }
            } else{
                Log.d("com.arcsoft", "FD extract failed");
            }
            err = engine.AFD_FSDK_UninitialFaceEngine();
            Log.d(TAG, "AFD_FSDK_UninitialFaceEngine =" + err.getCode());

    }

    }
