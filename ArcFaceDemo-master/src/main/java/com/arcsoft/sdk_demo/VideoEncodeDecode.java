package com.arcsoft.sdk_demo;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.os.Environment;
import android.util.Log;

import junit.framework.Assert;

import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.media.Image;
import android.media.MediaExtractor;
import android.util.Log;

import com.guo.android_extend.image.ImageConverter;
import com.guo.android_extend.java.ExtByteArrayOutputStream;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;


/**
 * Created by guoheng-iri on 2016/8/1.
 */
public class VideoEncodeDecode {

    public int mWidth=-1;
    public int mHeight=-1;

    private static final int BUFFER_SIZE = 30;

    private static final String MIME_TYPE = "video/avc";    // H.264 Advanced Video Coding

    private static final int DEFAULT_TIMEOUT_US=20000;
    private int height0,width0;
    private List<ProcessOut> outList;

    public void setHeight0(int height0) {
        this.height0 = height0;
    }

    public void setWidth0(int width0) {
        this.width0 = width0;
    }
    private List<FaceDB.FaceRegist> mResgist;

    public void setmResgist(List<FaceDB.FaceRegist> mResgist) {
        this.mResgist = mResgist;
    }

    public static int NUMFRAMES=590;

    private static final String TAG = "EncodeDecodeTest";
    private static final boolean VERBOSE = true;           // lots of logging


    Queue<byte[]> ImageQueue;
    Queue<byte[]> MyQueue;

    VideoEncode myencoder;
    VideoDecode mydecoder;

    private String mFilename;

    public void setmFilename(String mFilename) {
        this.mFilename = mFilename;
    }

    void VideoCodecPrepare(String videoInputFilePath)
    {
        mydecoder=new VideoDecode();
        mydecoder.VideoDecodePrepare(videoInputFilePath);


        mWidth = mydecoder.mediaFormat.getInteger(MediaFormat.KEY_WIDTH);
        mHeight = mydecoder.mediaFormat.getInteger(MediaFormat.KEY_HEIGHT);


        Log.d("testing width","width = "+Integer.toString(mWidth));
        Log.d("testing height","height = "+Integer.toString(mHeight));


        myencoder=new VideoEncode();
        myencoder.setmWidth(mWidth);
        myencoder.setmHeight(mHeight);
        Log.d("test width height","width , height = "+Integer.toString(mWidth)+"  "+Integer.toString(mHeight));
        myencoder.setOutName(mFilename);
        myencoder.VideoEncodePrepare();

        ImageQueue= new LinkedList<byte[]>();
        MyQueue=new LinkedList<byte[]>();
    }

    void MyProcessing()
    {
        //do process
    }


    public byte[] process(byte[] mImage)
    {
        YuvImage yuv = new YuvImage(mImage, ImageFormat.NV21, mWidth, mHeight, null);
        ExtByteArrayOutputStream ops = new ExtByteArrayOutputStream();
        yuv.compressToJpeg(new Rect(0,0,mWidth,mHeight), 80, ops);
        Bitmap bmp = BitmapFactory.decodeByteArray(ops.getByteArray(), 0, ops.getByteArray().length).copy(Bitmap.Config.RGB_565,true);
        try {
            ops.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Canvas face_canvas;
        for(ProcessOut one:outList){
            if(one==null){
                Log.d("wozaiceshi","wo zai ce shi ne ,is null");
                continue;

            }
            String desc=one.getDesc();
            int left=one.getLeft();
            int top=one.getTop();
            Rect rect=one.getRect();
            if(rect==null){
                Log.d("wozaiceshi","wo zai ce shi ne ,is null");
                continue;

            }
            int l =rect.left;
            int t = rect.top;
            int b = rect.bottom;
            int r = rect.right;
            face_canvas = new Canvas(bmp);
            Paint paint = new Paint();
            paint.setColor(Color.RED);
            paint.setStyle(Paint.Style.STROKE);//不填充
            paint.setStrokeWidth(2);
            face_canvas.drawRect(l,t,r,b, paint);
            Paint mPaint = new Paint();
            mPaint.setStrokeWidth(2);
            mPaint.setTextSize(20);
            mPaint.setColor(Color.RED);
            Rect bounds = new Rect();
            mPaint.getTextBounds(desc, 0, desc.length(), bounds);
            face_canvas.drawText(desc, left, top, mPaint);

        }
        try {
            ImageConverter convert = new ImageConverter();
            convert.initial(bmp.getWidth(), bmp.getHeight(), ImageConverter.CP_PAF_NV21);
            if (convert.convert(bmp, mImage)) {
                Log.d(TAG, "convert ok!");
                System.out.println("convert ok");
            }
            convert.destroy();
        } catch (Exception e) {
            System.out.println("convert fail");
            Log.d(TAG,"convert fail");
            e.printStackTrace();
        }
        return mImage;
    }
    public void processNV21(byte[] mImage){
        if(mImage == null)
        {
            Log.d("fuck","wanle");
        }
        else
        {
            Log.d("fuck","haixing");
        }
        YuvImage yuv = new YuvImage(mImage, ImageFormat.NV21, mWidth, mHeight, null);
        ExtByteArrayOutputStream ops = new ExtByteArrayOutputStream();
        yuv.compressToJpeg(new Rect(0,0,mWidth,mHeight), 80, ops);
        Bitmap bmp = BitmapFactory.decodeByteArray(ops.getByteArray(), 0, ops.getByteArray().length);


        try {
            ops.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(bmp == null)
        {
            Log.d("fuck","wuyule");
        }
        else
        {
            Log.d("fuck","chenglehaha");
        }

        ImageProcess ip=new ImageProcess(bmp,mImage,mResgist,height0,width0);
        ip.start();
        bmp=ip.getmBitmap().copy(Bitmap.Config.RGB_565,true);
        outList=ip.getOutList();
        ip.remove();

        try {
            ImageConverter convert = new ImageConverter();
            convert.initial(bmp.getWidth(), bmp.getHeight(), ImageConverter.CP_PAF_NV21);
            if (convert.convert(bmp, mImage)) {
                Log.d(TAG, "convert ok!");
                System.out.println("convert ok");
            }
            convert.destroy();
        } catch (Exception e) {
            System.out.println("convert fail");
            Log.d(TAG,"convert fail");
            e.printStackTrace();
        }


        if(bmp!= null && !bmp.isRecycled()){
            bmp.recycle();
            bmp = null;
        }
        System.gc();
    }
    void VideoEncodeDecodeLoop()
    {
        //here is decode flag
        boolean sawInputEOS = false;
        boolean sawOutputEOS = false;

        boolean IsImageBufferFull=false;


        MediaCodec.BufferInfo encodeinfo = new MediaCodec.BufferInfo();
        MediaCodec.BufferInfo decodeinfo = new MediaCodec.BufferInfo();
        mWidth = mydecoder.mediaFormat.getInteger(MediaFormat.KEY_WIDTH);
        mHeight = mydecoder.mediaFormat.getInteger(MediaFormat.KEY_HEIGHT);

        byte[] frameData = new byte[mWidth * mHeight * 3 / 2];
        byte[] frameData1 = new byte[mWidth * mHeight * 3 / 2];
        ByteBuffer[] encoderInputBuffers = myencoder.mediaCodec.getInputBuffers();
        ByteBuffer[] encoderOutputBuffers = myencoder.mediaCodec.getOutputBuffers();


        int generateIndex = 0;

        boolean encodeinputDone = false;
        boolean encoderDone = false;


        int index0=0;
        while((!encoderDone) && (!sawOutputEOS))
        {

            while (!sawOutputEOS && (!IsImageBufferFull)) {
                if (!sawInputEOS) {
                    int inputBufferId = mydecoder.decoder.dequeueInputBuffer(DEFAULT_TIMEOUT_US);
                    if (inputBufferId >= 0) {
                        ByteBuffer inputBuffer = mydecoder.decoder.getInputBuffer(inputBufferId);
                        int sampleSize = mydecoder.extractor.readSampleData(inputBuffer, 0); //将一部分视频数据读取到inputbuffer中，大小为sampleSize
                        if (sampleSize < 0) {
                            mydecoder.decoder.queueInputBuffer(inputBufferId, 0, 0, 0L, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
                            sawInputEOS = true;
                        } else {
                            long presentationTimeUs = mydecoder.extractor.getSampleTime();
                            mydecoder.decoder.queueInputBuffer(inputBufferId, 0, sampleSize, presentationTimeUs, 0);
                            mydecoder.extractor.advance();  //移动到视频文件的下一个地址
                        }
                    }
                }
                int outputBufferId = mydecoder.decoder.dequeueOutputBuffer(decodeinfo, DEFAULT_TIMEOUT_US);
                if (outputBufferId >= 0) {
                    if ((decodeinfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                        sawOutputEOS = true;
                    }
                    boolean doRender = (decodeinfo.size != 0);
                    if (doRender) {
                        //NUMFRAMES++;
                        Image image = mydecoder.decoder.getOutputImage(outputBufferId);

                        byte[] imagedata=mydecoder.getDataFromImage(image, mydecoder.FILE_TypeNV21);
                        byte[] imagedata1=mydecoder.getDataFromImage(image,-1);
                        ImageQueue.offer(imagedata);
                        MyQueue.offer(imagedata1);
                        if (ImageQueue.size()==BUFFER_SIZE)
                        {
                            IsImageBufferFull = true;
                        }
                        image.close();
                        mydecoder.decoder.releaseOutputBuffer(outputBufferId, true);
                    }
                }
            }


            //MyProcessing();


            while ((!encoderDone) && IsImageBufferFull) {

                if (!encodeinputDone) {
                    int inputBufIndex = myencoder.mediaCodec.dequeueInputBuffer(DEFAULT_TIMEOUT_US);

                    if (inputBufIndex >= 0) {
                        long ptsUsec = myencoder.computePresentationTime(generateIndex);
                        if (generateIndex == NUMFRAMES) {
                            myencoder.mediaCodec.queueInputBuffer(inputBufIndex, 0, 0, ptsUsec,
                                    MediaCodec.BUFFER_FLAG_END_OF_STREAM);
                            encodeinputDone = true;
                            if (VERBOSE) Log.d(TAG, "sent input EOS (with zero-length frame)");
                        } else {
                            
                            frameData=ImageQueue.poll();
                            frameData1=MyQueue.poll();
                            if(index0%25==0) {
                                processNV21(frameData1);
                                frameData=process(frameData);
                            }
                            else
                                frameData=process(frameData);
                            index0+=1;
                            ByteBuffer inputBuf = encoderInputBuffers[inputBufIndex];
                            // the buffer should be sized to hold one full frame
                            inputBuf.clear();
                            inputBuf.put(frameData);
                            myencoder.mediaCodec.queueInputBuffer(inputBufIndex, 0, frameData.length, ptsUsec, 0);

                            if (ImageQueue.size()==0)
                            {
                                IsImageBufferFull=false;
                            }

                            if (VERBOSE) Log.d(TAG, "submitted frame " + generateIndex + " to enc");
                        }
                        generateIndex++;
                    } else {
                        // either all in use, or we timed out during initial setup
                        if (VERBOSE) Log.d(TAG, "input buffer not available");
                    }
                }
                // Check for output from the encoder.  If there's no output yet, we either need to
                // provide more input, or we need to wait for the encoder to work its magic.  We
                // can't actually tell which is the case, so if we can't get an output buffer right
                // away we loop around and see if it wants more input.
                //
                // Once we get EOS from the encoder, we don't need to do this anymore.
                if (!encoderDone) {
                    int encoderStatus = myencoder.mediaCodec.dequeueOutputBuffer(encodeinfo, DEFAULT_TIMEOUT_US);
                    if (encoderStatus == MediaCodec.INFO_TRY_AGAIN_LATER) {
                        // no output available yet
                        if (VERBOSE) Log.d(TAG, "no output from encoder available");
                    } else if (encoderStatus == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
                        // not expected for an encoder
                        encoderOutputBuffers = myencoder.mediaCodec.getOutputBuffers();
                        if (VERBOSE) Log.d(TAG, "encoder output buffers changed");
                    } else if (encoderStatus == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                        // not expected for an encoder
                        MediaFormat newFormat = myencoder.mediaCodec.getOutputFormat();
                        if (VERBOSE) Log.d(TAG, "encoder output format changed: " + newFormat);

                        if (myencoder.mMuxerStarted) {
                            throw new RuntimeException("format changed twice");
                        }

                        Log.d(TAG, "encoder output format changed: " + newFormat);

                        // now that we have the Magic Goodies, start the muxer
                        myencoder.mTrackIndex = myencoder.mMuxer.addTrack(newFormat);
                        myencoder.mMuxer.start();
                        myencoder.mMuxerStarted = true;

                    } else if (encoderStatus < 0) {
                        Log.d(TAG,"unexpected result from encoder.dequeueOutputBuffer: " + encoderStatus);
                    } else { // encoderStatus >= 0
                        ByteBuffer encodedData = encoderOutputBuffers[encoderStatus];
                        if (encodedData == null) {
                            Log.d(TAG,"encoderOutputBuffer " + encoderStatus + " was null");
                        }

                        if ((encodeinfo.flags & MediaCodec.BUFFER_FLAG_CODEC_CONFIG) != 0) {

                            MediaFormat format =
                                    MediaFormat.createVideoFormat(MIME_TYPE, mWidth, mHeight);
                            format.setByteBuffer("csd-0", encodedData);

                            encodeinfo.size = 0;

                        } else {
                            // Get a decoder input buffer, blocking until it's available

                            if ((encodeinfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0)
                                encoderDone = true;
                            if (VERBOSE) Log.d(TAG, "passed " + encodeinfo.size + " bytes to decoder"
                                    + (encoderDone ? " (EOS)" : ""));
                        }


                        // It's usually necessary to adjust the ByteBuffer values to match BufferInfo.
                        if (encodeinfo.size != 0) {
                            encodedData.position(encodeinfo.offset);
                            encodedData.limit(encodeinfo.offset + encodeinfo.size);
                            myencoder.mMuxer.writeSampleData(myencoder.mTrackIndex, encodedData, encodeinfo);

                        }
                        myencoder.mediaCodec.releaseOutputBuffer(encoderStatus, false);
                    }
                }


            }


        }
    }


    public void close()
    {
        myencoder.close();
        mydecoder.close();
    }

}