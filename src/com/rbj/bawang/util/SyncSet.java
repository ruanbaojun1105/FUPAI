package com.rbj.bawang.util;

import java.io.File;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Environment;
import android.os.RemoteException;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.provider.Settings.System;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

public class SyncSet {
	public void syncLight(final Context context, final View view) {
		// 设置完毕后，保证每个页面的亮度都一致

		new Thread(new Runnable() {

			@Override
			public void run() {
				// Message m=new Message();
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// m.what=0x111;
				// mHandler.sendMessage(m);
				SharedPreferences spset = context.getSharedPreferences(
						"LIGHTSET", -1);
				String lightSet = spset.getString("light", "");
				if (lightSet != null) {
					if (lightSet.equals("0")) {
						WindowManager.LayoutParams p = ((Activity) context)
								.getWindow().getAttributes();
						Log.e("亮度为", p.screenBrightness + "");
						p.screenBrightness = 0.01f;
						((Activity) context).getWindow().setAttributes(p);
						Log.e("亮度为", p.screenBrightness + "");
					} else if (lightSet.equals("1")) {
						WindowManager.LayoutParams p1 = ((Activity) context)
								.getWindow().getAttributes();
						p1.screenBrightness = 1.0f;
						((Activity) context).getWindow().setAttributes(p1);
						Log.e("亮度为", p1.screenBrightness + "");
					} else {
						WindowManager.LayoutParams p2 = ((Activity) context)
								.getWindow().getAttributes();
						p2.screenBrightness = -1.0f;
						((Activity) context).getWindow().setAttributes(p2);
						Log.e("亮度为", p2.screenBrightness + "");
						// int value = 0;
						// ContentResolver cr = context.getContentResolver();
						// try {
						// value = Settings.System.getInt(cr,
						// Settings.System.SCREEN_BRIGHTNESS);
						// } catch (SettingNotFoundException e) {
						// value=255;
						// }
						// WindowManager.LayoutParams p1 = ((Activity)
						// context).getWindow()
						// .getAttributes();
						// float f=value / 255.0f;
						// p1.screenBrightness = f;
						// ((Activity) context).getWindow().setAttributes(p1);
						// Log.e("亮度为", p1.screenBrightness+"");

					}

				}

			}

		}).start();

	}

	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	public void setBgForUI(Context context, View view) {
		// add save pic_path
		File file;
		if (!Environment.getExternalStorageDirectory().exists()) {
			file = new File(Environment.getExternalStorageDirectory()
					+ "/BWfile/bg.png");
		} else {
			file = new File("/sdcard/BWfile/bg.png");
		}
		if (!file.exists()) {
			Log.e("是否已更改过背景图片", "否，不处理此方法");
		} else {
			Log.e("是否已更改过背景图片", "是，正在修改背景");
			view.setBackgroundDrawable(new BitmapDrawable(BitmapFactory
					.decodeFile(file.getPath())));
		}
		/*
		 * InputStream is1 =
		 * context.getResources().openRawResource(R.drawable.baidu);
		 * BitmapFactory.Options options=new BitmapFactory.Options();
		 * options.inJustDecodeBounds = false; options.inSampleSize = 3;
		 * //width，hight设为原来的十分一 Bitmap btp1
		 * =BitmapFactory.decodeStream(is1,null,options); Drawable drawable1 =
		 * new BitmapDrawable(btp1);
		 */

	}

}
