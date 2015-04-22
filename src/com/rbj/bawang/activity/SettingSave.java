package com.rbj.bawang.activity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.rbj.bawang.util.AboutDialog;
import com.rbj.bawang.util.SyncSet;
import com.rbj.browser.R;
import com.rbj.server.MyconnService;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SettingSave extends Activity implements OnClickListener {

	public static String PREFERENCES_NAME = "PRE_NAME";
	public static String KEY_support_JS = "VALUE_JS";
	public static String KEY_support_PIC = "VALUE_PIC";
	public static String KEY_support_ZOOM = "VALUE_ZOOM";
	public static String KEY_SUPPORT_CACHE = "VALUE_CACHE";
	public static String KEY_SUPPORT_HISTORY = "VALUE_HISTORY";
	public static String KEY_SUPPORT_PERCENTAGE = "VALUE_PERCENTAGE";
	private SharedPreferences preferences;
	private SharedPreferences.Editor prefer_editor;
	private CheckBox toggleBtn_setJS = null;
	private CheckBox toggleBtn_setZoom = null;
	private CheckBox toggleBtn_setPIC = null;
	private Button change_save_directory_Btn = null;
	private Button clear_data_Btn = null;
	private View dialogView = null;
	public TextView dialogText = null;
	public EditText dialogEdittext = null;
	private TextView settings_randomPIC;
	private ImageButton openPicture,backHome;
	private ImageView imageViewbg;
	private RelativeLayout settingBg;
	private Button setPicButton;
	private TextView setLight;
	private File file;
	private boolean change = false;
	private String getIntentName;
	public static SettingSave settingSave = null;
	public static final String search_engine_baidu = "http://www.baidu.com/s?wd=";
	public static final String search_engine_soso = "http://www.soso.com/q?w=";
	public static final String search_engine_360 = "http://www.so.com/s?q=";
	public final String[] engines = { "低调", "奢华", "内涵", "本色", "调节", "纯黑",
			"鲜艳绿", "一辈子", "蓝白" };
	public boolean clear_flags[] = { false, false };
	public ArrayAdapter<String> adapter;
	private ByteArrayOutputStream oStreamTest;
	private Context context;

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		System.gc();
		System.out.println("setting页面结束，已提醒系统回收垃圾");
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.settings);
		initView();

	}

	private void initView() {
		// TODO Auto-generated method stub
		settingSave = this;
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		getIntentName = bundle.getString("intent").toString();
		// Toast.makeText(getApplicationContext(), getIntentName, 0).show();
		settingBg = (RelativeLayout) findViewById(R.id.setting_bg);
		SyncSet syncLight = new SyncSet();
		// syncLight.syncLight(getApplicationContext(),settingBg);
		syncLight.setBgForUI(getApplicationContext(), settingBg);
		preferences = getSharedPreferences(PREFERENCES_NAME,
				MODE_WORLD_WRITEABLE);
		prefer_editor = preferences.edit();
		backHome=(ImageButton) findViewById(R.id.backtohome);
		backHome.setOnClickListener(this);
		
		toggleBtn_setJS = (CheckBox) findViewById(R.id.settings_set_javascript_toggle_btn);
		toggleBtn_setJS.setChecked(preferences
				.getBoolean(KEY_support_JS, false));// default case:doesn't
		toggleBtn_setJS
				.setOnCheckedChangeListener(new onCheckedChangedListener());
		toggleBtn_setZoom = (CheckBox) findViewById(R.id.settings_set_zoom_toggle_btn);
		toggleBtn_setZoom.setChecked(preferences.getBoolean(KEY_support_ZOOM,
				false));
		toggleBtn_setZoom
				.setOnCheckedChangeListener(new onCheckedChangedListener());
		toggleBtn_setPIC = (CheckBox) findViewById(R.id.settings_set_picture_toggle_btn);
		toggleBtn_setPIC.setChecked(preferences.getBoolean(KEY_support_PIC,
				false));
		toggleBtn_setPIC
				.setOnCheckedChangeListener(new onCheckedChangedListener());
		setLight = (TextView) findViewById(R.id.lightset);
		setLight.setOnClickListener(new myButtonClickListener());
		setPicButton = (Button) findViewById(R.id.set_pic);
		setPicButton.setOnClickListener(new myButtonClickListener());
		change_save_directory_Btn = (Button) findViewById(R.id.change_directory);
		change_save_directory_Btn
				.setOnClickListener(new myButtonClickListener());
		clear_data_Btn = (Button) findViewById(R.id.clear_data);
		clear_data_Btn.setOnClickListener(new myButtonClickListener());
		openPicture = (ImageButton) findViewById(R.id.openPic);
		openPicture.setOnClickListener(new myButtonClickListener());
		settings_randomPIC = (TextView) findViewById(R.id.settings_randomPIC);
		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, engines);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		settings_randomPIC.setOnClickListener(new myButtonClickListener());// 切换开发者推荐皮肤
		dialogView = LayoutInflater.from(getApplicationContext()).inflate(
				R.layout.clean_history, null);
	}
	@SuppressLint("NewApi")
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (resultCode == RESULT_OK) {
			Uri uri = data.getData();
			Log.e("uri", uri.toString() + "///___" + uri.getPath().length());
			ContentResolver cr = this.getContentResolver();
			try {
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inJustDecodeBounds = false;
				options.inSampleSize = 2; // width，hight设为原来的十分一
				Log.e("图片状态", "图片已修改，地址和大小为：" + uri.getEncodedPath() + "`````"
						+ uri.getPath().length() + "压缩为" + options.inSampleSize
						+ "分之一");
				Bitmap bitmap = BitmapFactory.decodeStream(
						cr.openInputStream(uri), null, options);
				// uri.parse(uriString)

				ByteArrayOutputStream oStream = new ByteArrayOutputStream();// 声明并创建一个输出字节流对象
				bitmap.compress(Bitmap.CompressFormat.PNG, 80, oStream);// 将BITMAP压缩为png格式，图像质量为50，第三个参数为输出流
				// ContentValues values=new ContentValues();
				// values.put("image",
				// oStream.toByteArray());//将BITMAP转换为字节数组，并直接进行存储
				oStreamTest = oStream;
				change = true;

				imageViewbg = (ImageView) findViewById(R.id.imagebg_look);
				// imageViewbg.setBackgroundDrawable(new
				// BitmapDrawable(BitmapFactory.decodeFile(file.getPath()+"/bg.png")));//直接读取SD卡中的图片，路径读取。
				/* 将Bitmap设定到ImageView */
				Drawable drawable1 = new BitmapDrawable(bitmap);
				imageViewbg.setBackgroundDrawable(drawable1);
				// settingBg.setBackground(drawable1);
				// imageViewbg.setImageBitmap(bitmap);

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			// String a=getClass().getName().toString();
			// Toast.makeText(getApplicationContext(), a+"", 0).show();
			Intent intenta;
			if (getIntentName.equals("home")) {
				Home.home.finish();
				intenta = new Intent(getApplicationContext(), Home.class);
				startActivity(intenta);
			}
			Log.i("activity", getIntentName);
			SettingSave.this.finish();
			overridePendingTransition(R.anim.feature_scale_in, R.anim.translate);
			/**
			 * 
			 * 控制ACTIVITY的两种方法
			 */
			// ActivityManager
			// manager=(ActivityManager)getSystemService(ACTIVITY_SERVICE);
			// manager.restartPackage(getIntentName.trim());

		}
		return false;
	}

	public class onCheckedChangedListener implements OnCheckedChangeListener {

		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			// for these toggle_btns,use switch to do
			switch (buttonView.getId()) {
			case R.id.settings_set_javascript_toggle_btn:
				if (isChecked) {
					prefer_editor.putBoolean(KEY_support_JS, true);
				} else {
					prefer_editor.putBoolean(KEY_support_JS, false);

				}
				prefer_editor.commit();
				new AboutDialog().getToast(getApplicationContext(), "ok",
						Gravity.CENTER, 0);
				break;
			case R.id.settings_set_zoom_toggle_btn:
				if (isChecked) {
					prefer_editor.putBoolean(KEY_support_ZOOM, true);

				} else {
					prefer_editor.putBoolean(KEY_support_ZOOM, false);
				}
				prefer_editor.commit();
				new AboutDialog().getToast(getApplicationContext(), "ok",
						Gravity.CENTER, 0);
				break;

			}

		}

	}

	@SuppressLint("SdCardPath")
	public class myButtonClickListener implements OnClickListener {

		@SuppressLint({ "ResourceAsColor", "SdCardPath" })
		@Override
		public void onClick(View v) {

			switch (v.getId()) {
			case R.id.change_directory:
				setDialogView();
				break;
			case R.id.clear_data:
				cleanHistory();
				break;
			case R.id.openPic:

				Intent intent = new Intent();
				/* 开启Pictures画面Type设定为image */
				intent.setType("image/*");
				/* 使用Intent.ACTION_GET_CONTENT这个Action */
				intent.setAction(Intent.ACTION_GET_CONTENT);
				/* 取得相片后返回本画面 */
				startActivityForResult(intent, 1);
				// Intent intent = new Intent(Settings.this, ShowPicture.class);
				// startActivity(intent);
				// Settings.this.finish();
				overridePendingTransition(R.anim.feature_scale_in,
						R.anim.translate);
				break;
			case R.id.set_pic:
				if (change) {
					try {
						if (!Environment.getExternalStorageDirectory().exists()) {
							file = new File(
									Environment.getExternalStorageDirectory()
											+ "/BWfile");
						} else {
							file = new File("/sdcard/BWfile");
						}
						if (!file.exists()) {
							file.mkdir();
							Log.i("设定路径为空", "已自动创建该地址的文件夹");
							FileOutputStream fileOutputStream = new FileOutputStream(
									file + "/bg.png");
							fileOutputStream.write(oStreamTest.toByteArray());
							fileOutputStream.close();
							Log.e("存储的背景图片写入状态", "已完成写入");
						} else {
							Log.i("设定路径已存在", "！！");
							FileOutputStream fileOutputStream = new FileOutputStream(
									file + "/bg.png");
							fileOutputStream.write(oStreamTest.toByteArray());
							fileOutputStream.close();
							Log.e("存储的背景图片写入状态", "已完成写入");

						}
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					new AboutDialog().getToast(getApplicationContext(),
							"自定义背景进度：成功！", Gravity.BOTTOM, 0);
					if (getIntentName.equals("home")) {
						Home.home.finish();
					} else {
						MainView.mainView.finish();
					}
					Intent re = new Intent(SettingSave.this, Start.class);
					startActivity(re);
					SettingSave.this.finish();
					overridePendingTransition(R.anim.feature_scale_in,
							R.anim.translate);
				} else {
					new AboutDialog().getToast(getApplicationContext(),
							"自定义背景进度：失败！\n你还未选择背景当作更换对象。", Gravity.BOTTOM, 0);
				}

				break;
			case R.id.lightset:
				// lightMenu();
				new AboutDialog().getToast(getApplicationContext(),
						"此功能有异常，后续版本奉上~", Gravity.CENTER, 0);
				break;
			case R.id.settings_randomPIC:
				File file,
				file1;

				if (!Environment.getExternalStorageDirectory().exists()) {
					file = new File(Environment.getExternalStorageDirectory()
							+ "/BWfile/bg.png");
					file1 = new File(Environment.getExternalStorageDirectory()
							+ "/BWfile/bg1.png");
				} else {
					file = new File("/sdcard/BWfile/bg.png");
					file1 = new File("/sdcard/BWfile/bg1.png");
				}
				if (file1.exists()) {
					file.delete();
					file1.renameTo(file);
					Log.e(!file.exists() + "", !file1.exists() + "");
					new AboutDialog().getToast(getApplicationContext(),
							"切换成功,效果还行吧~", Gravity.CENTER, 0);
					if (getIntentName.equals("home")) {
						Home.home.finish();
					} else {
						MainView.mainView.finish();
					}
					Intent re = new Intent(SettingSave.this, Home.class);
					startActivity(re);
					SettingSave.this.finish();
					overridePendingTransition(R.anim.feature_scale_in,
							R.anim.translate);

				} else {
					new AboutDialog().getToast(getApplicationContext(),
							"切换切换失败\n此功能需要接入WIFI网络才能实现\n确定接入WIFI可以重启浏览器试试",
							Gravity.CENTER, 0);
				}
				Log.e(!file.exists() + "", !file1.exists() + "");
				break;

			}

		}

	}

	public void setDialogView() {

		final Dialog dialog = new Dialog(SettingSave.this);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.dialogsetfilepath);
		final Button tureSet;
		final Button falseSet;
		tureSet = (Button) dialog.findViewById(R.id.tureSet);
		falseSet = (Button) dialog.findViewById(R.id.falseSet);
		tureSet.setOnClickListener(new View.OnClickListener() {

			@SuppressLint("SdCardPath")
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				EditText inputText = (EditText) dialog
						.findViewById(R.id.inputText);
				// File file;
				// if (Environment.getExternalStorageDirectory().exists()) {
				// file = new
				// File(Environment.getExternalStorageDirectory()+"/");
				// }else {
				// file = new File("/sdcard/BWfile/");
				// }
				// String directory =file.getPath();
				if (inputText.getText().toString().trim().equals("")) {
					new AboutDialog().getToast(getApplicationContext(), "输入为空",
							Gravity.TOP, 0);
				} else {
					System.out.println(inputText.getText().toString());
					String savePath = inputText.getText().toString().trim();
					SharedPreferences sp = getSharedPreferences("down_path",
							getApplicationContext().MODE_WORLD_READABLE);
					SharedPreferences.Editor et = sp.edit();
					et.putString("down_path", savePath);
					et.putBoolean("isChangePath", true);
					et.commit();
					new AboutDialog().getToast(getApplicationContext(),
							"下载地址更改成功！", Gravity.TOP, 0);
					dialog.dismiss();
					// savePath = sp.getBoolean("isChangePath", false);
				}

			}
		});
		falseSet.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});

		dialog.show();
	}

	public void cleanHistory() {

		final Dialog dialog = new Dialog(SettingSave.this);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.clean_history);
		final Button tureSet, falseSet;
		final RadioButton radioButton1, radioButton2;
		tureSet = (Button) dialog.findViewById(R.id.button1);
		falseSet = (Button) dialog.findViewById(R.id.button2);
		radioButton1 = (RadioButton) dialog.findViewById(R.id.radioButton1);
		// radioButton2=(RadioButton)dialog.findViewById(R.id.radioButton2);
		tureSet.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				if (radioButton1.isChecked()) {
					// clear settings

					// 1
					try {
						File file, file1;
						if (!Environment.getExternalStorageDirectory().exists()) {
							file = new File(Environment
									.getExternalStorageDirectory()
									+ "/BWfile/bg.png");
							file1 = new File(Environment
									.getExternalStorageDirectory()
									+ "/BWfile/bg1.png");
						} else {
							file = new File("/sdcard/BWfile/bg.png");
							file1 = new File("/sdcard/BWfile/bg1.png");
						}
						file.delete();// 清空背景图片的设定
						file1.delete();

						// 2
						prefer_editor.clear();
						prefer_editor.commit();
						// 3
						SharedPreferences sp = getSharedPreferences(
								"down_path",
								getApplicationContext().MODE_WORLD_READABLE);
						SharedPreferences.Editor et = sp.edit();
						et.clear();
						et.commit();

						if (getIntentName.equals("home")) {
							Home.home.finish();
						} else {
							MainView.mainView.finish();
						}
						Intent intent = new Intent();
						intent.setClass(SettingSave.this, Start.class);
						startActivity(intent);
						SettingSave.this.finish();
						Log.e("clear_settings", "cleared");
					} catch (Exception e) {
						Log.e("clear_settings", "failed");
					}
					new AboutDialog().getToast(getApplicationContext(),
							"已还原设置！", Gravity.CENTER, 0);
				}

				dialog.dismiss();
				// savePath = sp.getBoolean("isChangePath", false);

			}
		});
		falseSet.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});

		dialog.show();
	}

	// 设置夜间模式,需要用线程实现才能实时调节屏幕的亮度
	public void lightMenu() {

		final String[] items = new String[] { "夜间", "日间", "正常" };
		Builder builder = new AlertDialog.Builder(SettingSave.this);
		// builder.setIcon(R.drawable.about);
		// builder.setTitle("Please choose list:");
		builder.setItems(items, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				new AboutDialog().getToast(getApplicationContext(), "已切换"
						+ items[which] + "模式", Gravity.CENTER, 0);
				SharedPreferences sp1 = getApplicationContext()
						.getSharedPreferences("LIGHTSET", MODE_WORLD_READABLE);
				switch (which) {
				case 0:
					// 设置完毕后，保证每个页面的亮度都一致
					SharedPreferences.Editor et1 = sp1.edit();
					et1.putString("light", "0");
					et1.commit();

					break;
				case 1:
					SharedPreferences.Editor et2 = sp1.edit();
					et2.putString("light", "1");
					et2.commit();
					break;
				case 2:
					WindowManager.LayoutParams p = getWindow().getAttributes();
					Log.e("亮度为", p.screenBrightness + "");
					SharedPreferences.Editor et3 = sp1.edit();
					et3.putString("light", p.screenBrightness + "");
					et3.commit();
					break;
				default:
					break;

				}
				new AboutDialog().getToast(getApplicationContext(),
						"文件浏览和设置页面将不会同步亮度的设置", Gravity.BOTTOM, 0);
			}
		});
		builder.create().show();

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.backtohome:
			Intent intenta;
			if (getIntentName.equals("home")) {
				Home.home.finish();
				intenta = new Intent(getApplicationContext(), Home.class);
				startActivity(intenta);
			}
			Log.i("activity", getIntentName);
			SettingSave.this.finish();
			overridePendingTransition(R.anim.feature_scale_in, R.anim.translate);
			break;

		}
	}
}
