package com.rbj.bawang.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.security.auth.Destroyable;

import com.rbj.bawang.util.AboutDialog;
import com.rbj.bawang.util.DelectFiles;
import com.rbj.bawang.util.SyncSet;
import com.rbj.browser.R;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.RelativeLayout;
import android.widget.Toast;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
@SuppressLint({ "WorldReadableFiles", "NewApi" })
public class FileTest extends Activity implements OnClickListener {

	private String getIntentName;
	private ArrayList<Map<String, String>> listData;
	private SimpleAdapter adapter;
	private ListView listView;
	private String tag;
	boolean flag = false;
	boolean isAudioList = true;
	// boolean isDirectory=false;
	@SuppressLint("SdCardPath")
	private String rootPath = "/";
	private ImageButton popUpListButton;
	private ImageButton tohomeButton;
	private RelativeLayout relativeLayout;
	private TextView backList;
	private RelativeLayout fileBg;
	public static FileTest fileTest = null;
	Context context;

	// Button inSD, outSD, TopFile;

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		System.gc();
		System.out.println("filetest页面结束，已提醒系统回收垃圾");
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);// 设置界面没有titlebar
		setContentView(R.layout.filetest);
		fileTest = this;
		context = FileTest.this;
		init();

		try {
			AudioList(listView, rootPath);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	@SuppressLint("CutPasteId")
	public void init() {
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		getIntentName = bundle.getString("intent").toString();
		fileBg = (RelativeLayout) findViewById(R.id.file_bg);
		new SyncSet().setBgForUI(getApplicationContext(), fileBg);

		relativeLayout = (RelativeLayout) findViewById(R.id.file_bg);
		backList = (TextView) findViewById(R.id.backlistfile);
		backList.setOnClickListener(this);
		tohomeButton = (ImageButton) findViewById(R.id.backtohome);
		tohomeButton.setOnClickListener(this);
		popUpListButton = (ImageButton) findViewById(R.id.popuplist);// 弹出POPup菜单
		popUpListButton.setOnClickListener(this);
		listView = (ListView) findViewById(R.id.listView);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.popuplist:
			showPopup(v);
			break;

		case R.id.backtohome:
			Intent intenta;
			if (getIntentName.equals("home")) {
				Home.home.finish();
				intenta = new Intent(getApplicationContext(), Home.class);
				startActivity(intenta);
			}
			Log.i("已关闭activity:", getIntentName);
			FileTest.this.finish();
			overridePendingTransition(R.anim.feature_scale_in, R.anim.translate);
			break;
		case R.id.backlistfile:
			if (backList.getText().length() != 0) {
				try {
					getFiles(backList.getText().toString());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				adapter.notifyDataSetInvalidated();

			} else {
				new AboutDialog().getToast(getApplicationContext(), "空白",
						Gravity.CENTER, 0);
			}
			String a = backList.getText().toString();
			String[] str;
			str = a.split("/");
			String s2 = "";
			for (int i = 0; i < str.length - 1; i++) {
				s2 += (str[i] + "/");
			}
			System.out.println(s2 + str.length);
			backList.setText(s2);
			break;
		}
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {

			if (backList.getText().toString().equals("")) {
				if (getIntentName.equals("home")) {
					Home.home.finish();
					Intent intent = new Intent();
					intent.setClass(getApplicationContext(), Home.class);
					startActivity(intent);
				}
				FileTest.this.finish();
				overridePendingTransition(R.anim.feature_scale_in,
						R.anim.translate);

			} else {
				try {
					getFiles(backList.getText().toString());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				String a = backList.getText().toString();
				String[] str;
				str = a.split("/");
				String s2 = "";
				for (int i = 0; i < str.length - 1; i++) {
					s2 += (str[i] + "/");
				}
				System.out.println(s2 + str.length);
				backList.setText(s2);
			}
			// Intent intent = new Intent(FileTest.this, Home.class);
			// startActivity(intent);
		}

		if (keyCode == KeyEvent.KEYCODE_MENU) {

			showPopup(popUpListButton);
		}
		return true;
	}

	private Handler handler=new Handler(){
		@SuppressWarnings("unchecked")
		public void handleMessage(Message msg) {
			if (msg.what==1) {
				String[] str;
				str = backList.getText().toString().split("/");
				String s2 = "";
				for (int i = 0; i < str.length ; i++) {
					s2 += (str[i] + "/");
				}
				try {
					getFiles(s2);
					adapter.notifyDataSetChanged();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else {
				String[] str;
				str = backList.getText().toString().split("/");
				String s3 = "";
				for (int i = 0; i < str.length-1 ; i++) {
					s3 += (str[i] + "/");
				}
				try {
					getFiles(s3);
					adapter.notifyDataSetChanged();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		};
	};
	public void AudioList(final ListView listView, String filePathEX)
			throws Exception {
		if (getFiles(filePathEX)!=null) {
			listData = (ArrayList<Map<String, String>>) getFiles(filePathEX);
		}else {
			String[] str = null;
			str = backList.getText().toString().split("/");
			String s2 = "";
			for (int i = 0; i < str.length-1 ; i++) {
				s2 += (str[i] + "/");
			}
			try {
				getFiles(s2);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
			
		listView.setOnItemClickListener(new OnItemClickListener() {

			@SuppressWarnings("deprecation")
			@Override
			public void onItemClick(AdapterView<?> lisView, View view,
					int position, long id) {
				String a = listData.get(position).get("url");
				String b=backList.getText().toString();
				Log.e("点击位置：", position + a + "");
				File file = new File(a);
				if (!file.isDirectory()) {
					Log.e("report", "点击的为文件");
					new DelectFiles().delefileDialog(context, a,handler);

				} else {
					
					try {
						getFiles(listData.get(position).get("url"));
						backList.setText(a);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						new AboutDialog().getToast(context,
								"no permission read this file!",
								Gravity.CENTER, 0);
						
					}
				}

			}

		});
		listView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@SuppressWarnings("deprecation")
			@Override
			public boolean onItemLongClick(AdapterView<?> lisView, View view,
					int position, long id) {
				new DelectFiles().delefileDialog(context, listData.get(position).get("url"),handler);
				return true;
			}
		});

	}

	public static String[] ImageFormatSet = new String[] { "apk" };

	/**
	 * @param判断文件格式函数、可以自定义丰富的功能
	 */
	public static boolean isAudioFile(String path) {
		for (String format : ImageFormatSet) {
			if (path.contains(format)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 获取该地址的全部列表
	 * @param url
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, String>> getFiles(String url) throws Exception {
		File files = new File(url);
		// str_url = new String[listinfos.size()];// 设置字符串数组的长度
		File[] file = files.listFiles();
		if (files.listFiles().length != 0 && url.endsWith("/")) {
			listData = new ArrayList<Map<String, String>>();

			for (int j = 0; j < file.length; j++) {
				Map<String, String> map = new HashMap<String, String>();
				map.put("name", file[j].getName());
				map.put("url", file[j].getAbsolutePath() + "/");
				listData.add(map);
			}
			adapter = new SimpleAdapter(context, listData,
					R.layout.history_display_style, new String[] { "name",
							"url" }, new int[] { R.id.website_names,
							R.id.website_url });
			listView.setAdapter(adapter);
			adapter.notifyDataSetChanged();
			System.gc();// 提醒系统及时回收

		} else {
			new AboutDialog().getToast(context,"空文件夹，如果不需要可长按删除！", Gravity.CENTER, 0);
			//return listData=null;
		}
		return listData;

		
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@SuppressLint({ "NewApi", "ResourceAsColor" })
	public void showPopup(final View v) {
		PopupMenu popupMenu = new PopupMenu(this, v);
		MenuInflater inflater = popupMenu.getMenuInflater();
		inflater.inflate(R.menu.popupmenu, popupMenu.getMenu());
		v.setBackgroundColor(R.color.light_grey);
		popupMenu.show();
		popupMenu
				.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

					@SuppressLint("ResourceAsColor")
					@Override
					public boolean onMenuItemClick(MenuItem item) {
						// TODO Auto-generated method stub
						switch (item.getItemId()) {

						case R.id.greyss:
							relativeLayout.setBackgroundColor(R.color.yellow);
							return true;
						default:
							relativeLayout.setBackgroundColor(R.color.white);
							v.setBackgroundColor(R.drawable.touch_bg);
							return true;
						}

					}

				});
	}

}