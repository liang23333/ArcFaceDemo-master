package com.arcsoft.sdk_demo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener {
	private final String TAG = this.getClass().toString();

	private static final int REQUEST_CODE_IMAGE_CAMERA = 1;
	private static final int REQUEST_CODE_IMAGE_OP = 2;
	private static final int REQUEST_CODE_OP = 3;
	private static final int REQUEST_CODE_IMAGE_OP_1 = 4;
	private static final int REQUEST_CODE_IMAGE_CAMERA_1 = 5;
	private static final int REQUEST_CODE_VIDEO_OP=6;
	private static final int REQUEST_CODE_VIDEO_PROCESS=7;
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.main_test);
		View v = this.findViewById(R.id.button1);
		v.setOnClickListener(this);
		v = this.findViewById(R.id.button2);
		v.setOnClickListener(this);
		v = this.findViewById(R.id.button3);
		v.setOnClickListener(this);

		v = this.findViewById(R.id.button4);
		v.setOnClickListener(this);
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_CODE_IMAGE_OP && resultCode == RESULT_OK) {
			Uri mPath = data.getData();
			String file = getPath(mPath);
			Bitmap bmp = Application.decodeImage(file);
			if (bmp == null || bmp.getWidth() <= 0 || bmp.getHeight() <= 0 ) {
				Log.e(TAG, "error");
			} else {
				Log.i(TAG, "bmp [" + bmp.getWidth() + "," + bmp.getHeight());
			}
			startRegister(bmp, file);
		} else if (requestCode == REQUEST_CODE_OP) {
			Log.i(TAG, "RESULT =" + resultCode);
			if (data == null) {
				return;
			}
			Bundle bundle = data.getExtras();
			String path = bundle.getString("imagePath");
			Log.i(TAG, "path="+path);
		} else if (requestCode == REQUEST_CODE_IMAGE_CAMERA && resultCode == RESULT_OK) {
			Uri mPath = ((Application)(MainActivity.this.getApplicationContext())).getCaptureImage();
			String file = getPath(mPath);
			Bitmap bmp = Application.decodeImage(file);
			startRegister(bmp, file);
		}else if(requestCode==REQUEST_CODE_IMAGE_CAMERA_1&&resultCode==RESULT_OK){
			Uri mPath = ((Application)(MainActivity.this.getApplicationContext())).getCaptureImage();
			String file = getPath(mPath);
			Bitmap bmp = Application.decodeImage(file);
			startImageDetector(bmp, file);
		}
		else if(requestCode==REQUEST_CODE_IMAGE_OP_1&&resultCode==RESULT_OK){
			Uri mPath = data.getData();
			String file = getPath(mPath);
			Bitmap bmp = Application.decodeImage(file);
			if (bmp == null || bmp.getWidth() <= 0 || bmp.getHeight() <= 0 ) {
				Log.e(TAG, "error");
			} else {
				Log.i(TAG, "bmp [" + bmp.getWidth() + "," + bmp.getHeight());
			}
			startImageDetector(bmp, file);
		}
		else if(requestCode==REQUEST_CODE_VIDEO_OP&&resultCode==RESULT_OK)
		{
			Uri uri = data.getData();
			Cursor cursor = getContentResolver().query(uri, null, null,
					null, null);
			cursor.moveToFirst();
			String imgNo = cursor.getString(0); // 图片编号
			imgNo=imgNo.replace("primary:","/sdcard/");
			Log.d("choose video","imgNo="+imgNo);
			String v_path = cursor.getString(1); // 图片文件路径
			Log.d("choose video","v_path="+v_path);
			String v_size = cursor.getString(2); // 图片大小
			Log.d("choose video","v_size="+v_size);
			String v_name = cursor.getString(3); // 图片文件名
			Log.d("choose video","v_name="+v_name);
			String v_4 = cursor.getString(4); // 图片文件名
			Log.d("choose video","v_4="+v_4);
			String v_5 = cursor.getString(5); // 图片文件名
			Log.d("choose video","v_5="+v_5);
			String v_6=cursor.getString(6);
			Log.d("choose video","v_6="+v_6);

			startVideoDetection(v_6,v_size);
//			startVideoDetection(imgNo,v_size);
		}
	}

	@Override
	public void onClick(View paramView) {
		// TODO Auto-generated method stub
		switch (paramView.getId()) {
			case R.id.button3:
//				if( ((Application)getApplicationContext()).mFaceDB.mRegister.isEmpty() ) {
//					Toast.makeText(this, "没有注册人脸，请先注册！", Toast.LENGTH_SHORT).show();
//				} else {
					new AlertDialog.Builder(this)
							.setTitle("请选择相机")
							.setIcon(android.R.drawable.ic_dialog_info)
							.setItems(new String[]{"后置相机", "前置相机"}, new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog, int which) {
											startDetector(which);
										}
									})
							.show();
//				}
				break;
			case R.id.button4:
				new AlertDialog.Builder(this)
						.setTitle("请选择注册方式")
						.setIcon(android.R.drawable.ic_dialog_info)
						.setItems(new String[]{"打开本地视频"}, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								switch (which){
									case 0:
										Intent intent = new Intent();
										/* 开启Pictures画面Type设定为image */
										//intent.setType("image/*");
										// intent.setType("audio/*"); //选择音频
										intent.setType("video/*"); //选择视频 （mp4 3gp 是android支持的视频格式）

										// intent.setType("video/*;image/*");//同时选择视频和图片

										/* 使用Intent.ACTION_GET_CONTENT这个Action */
										intent.setAction(Intent.ACTION_GET_CONTENT);
										/* 取得相片后返回本画面 */
										startActivityForResult(intent, REQUEST_CODE_VIDEO_OP);
										break;
									default:;
								}
							}
						})
						.show();
				break;
			case R.id.button2:
//				if( ((Application)getApplicationContext()).mFaceDB.mRegister.isEmpty() ) {
//					Toast.makeText(this, "没有注册人脸，请先注册！", Toast.LENGTH_SHORT).show();
//				} else {
//					new AlertDialog.Builder(this)
//							.setTitle("请选择相机")
//							.setIcon(android.R.drawable.ic_dialog_info)
//							.setItems(new String[]{"后置相机", "前置相机"}, new DialogInterface.OnClickListener() {
//										@Override
//										public void onClick(DialogInterface dialog, int which) {
//											startDetector(which);
//										}
//									})
//							.show();
					new AlertDialog.Builder(this)
							.setTitle("请选择注册方式")
							.setIcon(android.R.drawable.ic_dialog_info)
							.setItems(new String[]{"打开图片", "拍摄照片"}, new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									switch (which){
										case 1:
											Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
											ContentValues values = new ContentValues(1);
											values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
											Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
											((Application)(MainActivity.this.getApplicationContext())).setCaptureImage(uri);
											intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
											startActivityForResult(intent, REQUEST_CODE_IMAGE_CAMERA_1);
											break;
										case 0:
											Intent getImageByalbum = new Intent(Intent.ACTION_GET_CONTENT);
											getImageByalbum.addCategory(Intent.CATEGORY_OPENABLE);
											getImageByalbum.setType("image/jpeg");
											startActivityForResult(getImageByalbum, REQUEST_CODE_IMAGE_OP_1);
											break;
										default:;
									}
								}
							})
							.show();

//				}
				break;
			case R.id.button1:
				new AlertDialog.Builder(this)
						.setTitle("请选择注册方式")
						.setIcon(android.R.drawable.ic_dialog_info)
						.setItems(new String[]{"打开图片", "拍摄照片"}, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								switch (which){
									case 1:
										Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
										ContentValues values = new ContentValues(1);
										values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
										Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
										((Application)(MainActivity.this.getApplicationContext())).setCaptureImage(uri);
										intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
										startActivityForResult(intent, REQUEST_CODE_IMAGE_CAMERA);
										break;
									case 0:
										Intent getImageByalbum = new Intent(Intent.ACTION_GET_CONTENT);
										getImageByalbum.addCategory(Intent.CATEGORY_OPENABLE);
										getImageByalbum.setType("image/jpeg");
										startActivityForResult(getImageByalbum, REQUEST_CODE_IMAGE_OP);
										break;
									default:;
								}
							}
						})
						.show();
				break;
			default:;
		}
	}

	/**
	 * @param uri
	 * @return
	 */
	private String getPath(Uri uri) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			if (DocumentsContract.isDocumentUri(this, uri)) {
				// ExternalStorageProvider
				if (isExternalStorageDocument(uri)) {
					final String docId = DocumentsContract.getDocumentId(uri);
					final String[] split = docId.split(":");
					final String type = split[0];

					if ("primary".equalsIgnoreCase(type)) {
						return Environment.getExternalStorageDirectory() + "/" + split[1];
					}

					// TODO handle non-primary volumes
				} else if (isDownloadsDocument(uri)) {

					final String id = DocumentsContract.getDocumentId(uri);
					final Uri contentUri = ContentUris.withAppendedId(
							Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

					return getDataColumn(this, contentUri, null, null);
				} else if (isMediaDocument(uri)) {
					final String docId = DocumentsContract.getDocumentId(uri);
					final String[] split = docId.split(":");
					final String type = split[0];

					Uri contentUri = null;
					if ("image".equals(type)) {
						contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
					} else if ("video".equals(type)) {
						contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
					} else if ("audio".equals(type)) {
						contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
					}

					final String selection = "_id=?";
					final String[] selectionArgs = new String[] {
							split[1]
					};

					return getDataColumn(this, contentUri, selection, selectionArgs);
				}
			}
		}
		String[] proj = { MediaStore.Images.Media.DATA };
		Cursor actualimagecursor = this.getContentResolver().query(uri, proj, null, null, null);
		int actual_image_column_index = actualimagecursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		actualimagecursor.moveToFirst();
		String img_path = actualimagecursor.getString(actual_image_column_index);
		String end = img_path.substring(img_path.length() - 4);
		if (0 != end.compareToIgnoreCase(".jpg") && 0 != end.compareToIgnoreCase(".png")) {
			return null;
		}
		return img_path;
	}

	/**
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is ExternalStorageProvider.
	 */
	public static boolean isExternalStorageDocument(Uri uri) {
		return "com.android.externalstorage.documents".equals(uri.getAuthority());
	}

	/**
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is DownloadsProvider.
	 */
	public static boolean isDownloadsDocument(Uri uri) {
		return "com.android.providers.downloads.documents".equals(uri.getAuthority());
	}

	/**
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is MediaProvider.
	 */
	public static boolean isMediaDocument(Uri uri) {
		return "com.android.providers.media.documents".equals(uri.getAuthority());
	}

	/**
	 * Get the value of the data column for this Uri. This is useful for
	 * MediaStore Uris, and other file-based ContentProviders.
	 *
	 * @param context The context.
	 * @param uri The Uri to query.
	 * @param selection (Optional) Filter used in the query.
	 * @param selectionArgs (Optional) Selection arguments used in the query.
	 * @return The value of the _data column, which is typically a file path.
	 */
	public static String getDataColumn(Context context, Uri uri, String selection,
									   String[] selectionArgs) {

		Cursor cursor = null;
		final String column = "_data";
		final String[] projection = {
				column
		};

		try {
			cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
					null);
			if (cursor != null && cursor.moveToFirst()) {
				final int index = cursor.getColumnIndexOrThrow(column);
				return cursor.getString(index);
			}
		} finally {
			if (cursor != null)
				cursor.close();
		}
		return null;
	}

	/**
	 * @param mBitmap
	 */
	private void startRegister(Bitmap mBitmap, String file) {
		Intent it = new Intent(MainActivity.this, RegisterActivity.class);
		Bundle bundle = new Bundle();
		bundle.putString("imagePath", file);
		it.putExtras(bundle);
		startActivityForResult(it, REQUEST_CODE_OP);
	}

	private void startDetector(int camera) {
		Intent it = new Intent(MainActivity.this, DetecterActivity.class);
		it.putExtra("Camera", camera);
		startActivityForResult(it, REQUEST_CODE_OP);
	}

	private void startImageDetector(Bitmap mBitmap, String file) {
		Intent it = new Intent(MainActivity.this, ImageActivity.class);
		Bundle bundle = new Bundle();
		bundle.putString("imagePath", file);
		it.putExtras(bundle);
		startActivityForResult(it, REQUEST_CODE_OP);
	}
	private void startVideoDetection(String filepath,String filename)
	{
		Intent it = new Intent(MainActivity.this, VIdeoActivity.class);
		Bundle bundle = new Bundle();
		bundle.putString("VideoName", filename);
		bundle.putString("VideoPath",filepath);
		it.putExtras(bundle);
		startActivityForResult(it, REQUEST_CODE_VIDEO_PROCESS);
	}
}

