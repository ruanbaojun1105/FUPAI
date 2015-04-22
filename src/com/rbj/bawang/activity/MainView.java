package com.rbj.bawang.activity;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import net.youmi.android.AdManager;
import net.youmi.android.banner.AdSize;
import net.youmi.android.banner.AdView;
import net.youmi.android.spot.SpotManager;

import com.rbj.bawang.activity.Downloader.DownLoaderTask;
import com.rbj.bawang.bean.BW_bookmark;
import com.rbj.bawang.bean.BW_history;
import com.rbj.bawang.database.BookmarkDAO;
import com.rbj.bawang.database.HistoryDAO;
import com.rbj.bawang.pages.Bookmark;
import com.rbj.bawang.pages.History;
import com.rbj.bawang.util.AboutDialog;
import com.rbj.bawang.util.BackUrl;
import com.rbj.browser.R;
import com.slidingmenu.lib.SlidingMenu;

import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;

@SuppressLint({ "WorldWriteableFiles", "SetJavaScriptEnabled" })
public class MainView extends Activity implements OnClickListener,
		OnItemClickListener, OnCheckedChangeListener, OnKeyListener {
	public static WebView webView;
	private TextView downloadProgressPercentage = null;
	// Handler handler = null;
	public static String cur_url = "";
	private SharedPreferences preferences;
	private ProgressBar downProgressBar = null;
	private ProgressBar progressWeb = null;
	private ImageButton menu_ImageBtn_forward = null;
	private ImageButton menu_ImageBtn_back = null;
	private ImageButton menu_ImageBtn_refresh = null;
	private ImageButton menu_ImageBtn_menu = null;
	private boolean flag_loading = false;
	private LinearLayout changeBigLayout;
	public Handler handler = null;
	private int count = 1;// 记录当前页面为本次开启浏览器的第几个页面
	private PopupWindow popWindow = null;
	private TextView downFileName = null;
	private ProgressBar downloadProgressbar = null;
	private TextView downFileProgress = null;
	private View contentView = null;
	private Button close_popwindow_Btn = null;
	public static MainView mainView = null;
	private String urls;
	private Context context;
	private SlidingMenu slidingMenu;
	private List<Map<String, Object>> listData = new ArrayList<Map<String, Object>>();
	private SimpleAdapter simpleAdapter;
	private CheckBox checkBox;
	private ProgressBar copyBar;
	private TextView searchText, goSearchText;
	private Button exitBtn;
	private int backHomeNum = 2;
	private boolean isExit1 = false;// 防止重复响应回车事件标签
	private LinearLayout adclose;

	// private Intent intentServer = new Intent("SearcPictureServer.servers");
	// private String getIntentName;
	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_PROGRESS);
		setContentView(R.layout.main_view);
		mainView = this;
		context = MainView.this;

		init();// id初始化
		initSlidingmenu();// slidingmenu初始化
		initWebview();// 浏览器属性初始化
		readWebketPreferen();// 读取保存浏览器的属性
		System.gc();
	}

	/**
	 * 初始化slidingmenu
	 */
	private void initSlidingmenu() {
		// TODO Auto-generated method stub
		slidingMenu = new SlidingMenu(context);
		slidingMenu.setMode(SlidingMenu.LEFT);
		slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		slidingMenu.setShadowWidthRes(R.dimen.shadow_width);
		slidingMenu.setShadowDrawable(R.drawable.shadow);
		slidingMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		slidingMenu.setBehindWidth(300);
		slidingMenu.setFadeDegree(0.5f);
		slidingMenu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
		slidingMenu.setMenu(R.layout.slidingmenu);

		searchText = (TextView) findViewById(R.id.searchText);// 搜索编辑框
		searchText.setOnKeyListener(this);
		goSearchText = (TextView) findViewById(R.id.goSearchText);// 开始搜索
		goSearchText.setOnClickListener(this);
		exitBtn = (Button) findViewById(R.id.exit);// 退出
		exitBtn.setText("回到桌面");
		exitBtn.setOnClickListener(this);

		ListView listView = (ListView) slidingMenu.findViewById(R.id.menulist);

		listData = new ArrayList<Map<String, Object>>();
		String[] d = new String[] { "文件", "历史", "书签", "设置", "存书签" };
		int[] e = new int[] { android.R.drawable.sym_contact_card,
				android.R.drawable.ic_menu_recent_history, R.drawable.bookmark,
				R.drawable.tab_settings, android.R.drawable.ic_menu_add };
		for (int i = 0; i < 5; i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("image", e[i]);
			map.put("text", d[i]);
			listData.add(map);
		}
		simpleAdapter = new SimpleAdapter(context, listData,
				R.layout.slidingmenu_listitem,
				new String[] { "image", "text" }, new int[] { R.id.image,
						R.id.text });
		listView.setAdapter(simpleAdapter);
		listView.setOnItemClickListener(this);
	}

	// 数据初始化
	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	public void init() {
		adclose = (LinearLayout) findViewById(R.id.adlinear);// 广告栏
		adclose.setVisibility(View.GONE);

		urls = getIntent().getStringExtra("end");

		checkBox = (CheckBox) findViewById(R.id.checkBox1);// 全屏
		checkBox.setOnCheckedChangeListener(this);
		changeBigLayout = (LinearLayout) findViewById(R.id.change_big);// 全屏时使用

		copyBar = (ProgressBar) findViewById(R.id.copy);
		copyBar.setOnClickListener(this);

		preferences = getSharedPreferences(SettingSave.PREFERENCES_NAME,
				MODE_WORLD_WRITEABLE);
		progressWeb = (ProgressBar) findViewById(R.id.progressWeb);
		downProgressBar = (ProgressBar) findViewById(R.id.download_progressbar);
		downProgressBar.setVisibility(View.INVISIBLE);
		downProgressBar.setOnClickListener(new PopWin());
		menu_ImageBtn_forward = (ImageButton) findViewById(R.id.menu_imagebtn_forward);
		menu_ImageBtn_forward.setOnClickListener(this);
		menu_ImageBtn_back = (ImageButton) findViewById(R.id.menu_imagebtn_back);
		menu_ImageBtn_back.setOnClickListener(this);
		menu_ImageBtn_refresh = (ImageButton) findViewById(R.id.menu_imagebtn_refresh);
		menu_ImageBtn_refresh.setOnClickListener(this);
		menu_ImageBtn_menu = (ImageButton) findViewById(R.id.menu_imagebtn_home);
		menu_ImageBtn_menu.setOnClickListener(this);
		downloadProgressPercentage = (TextView) findViewById(R.id.downloadProgress_percentage);
		downloadProgressPercentage.setVisibility(View.INVISIBLE);
		contentView = LayoutInflater.from(getApplicationContext()).inflate(
				R.layout.download_popupwin, null);
		handler = new PopWin();
		popWindow = new PopupWindow(contentView,
				ViewGroup.LayoutParams.FILL_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);

		downFileName = (TextView) contentView
				.findViewById(R.id.download_fileName);
		downloadProgressbar = (ProgressBar) contentView
				.findViewById(R.id.download_window_progressbar);
		downFileProgress = (TextView) contentView
				.findViewById(R.id.download_window_progress);

		close_popwindow_Btn = (Button) contentView
				.findViewById(R.id.close_popWindow);
		close_popwindow_Btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				popWindow.dismiss();

			}
		});
	}

	/**
	 * 初始化浏览器属性
	 */
	private void initWebview() {
		// TODO Auto-generated method stub
		webView = (WebView) findViewById(R.id.webview);
		webView.requestFocus();
		webView.getSettings().setDefaultTextEncodingName("utf-8");
		webView.getSettings().setJavaScriptEnabled(true);// webView开启JavaScript
		String dir = this.getApplicationContext()
				.getDir("bawang", Context.MODE_WORLD_READABLE).getPath();
		webView.getSettings().setAppCachePath(dir);
		webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
		webView.getSettings().setAppCacheMaxSize(1024 * 1024 * 8);
		webView.getSettings().setSaveFormData(true);
		webView.getSettings().setSavePassword(true);
		webView.setDownloadListener(new myDownloaderListener());
		webView.setInitialScale(80);// 浏览器的缩放比例
		webView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
		webView.getSettings().setUseWideViewPort(true);
		webView.loadUrl(urls);
		webView.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				count = count + 1;
				// view.loadUrl(url);
				// cur_url = url;
				SharedPreferences sp = MainView.this.getSharedPreferences(
						"addHistory", MODE_PRIVATE);
				if (sp.getBoolean("addHistory", true)) {
					try {
						if (view.getTitle().equals("")) {
							Log.i("标签为空，不添加记录", view.getUrl());
						} else {
							Date date = new Date(System.currentTimeMillis());
							HistoryDAO flagDAO = new HistoryDAO(
									getApplicationContext());// 创建FlagDAO对象
							BW_history tb_flag = new BW_history();
							String flag = view.getTitle() + "--"
									+ date.toLocaleString().toString();
							tb_flag.setFlag(flag);
							tb_flag.setWeburl(view.getUrl());
							Log.i("网址添加成功::", flag + url);
							Log.i("时间:", date.toLocaleString());
							flagDAO.add(tb_flag);
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				System.gc();
				return super.shouldOverrideUrlLoading(view, url);
			}

		});

		webView.setWebChromeClient(new WebChromeClient() {
			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				handler = new progressWeb();
				progressWeb.setProgress(0);
				menu_ImageBtn_refresh.setImageDrawable(getResources()
						.getDrawable(R.drawable.cancel));
				flag_loading = true;
				if (newProgress > 20) {
					/*
					 * SharedPreferences sp =
					 * MainView.this.getSharedPreferences( "sava_weburl",
					 * MODE_WORLD_READABLE); SharedPreferences.Editor et =
					 * sp.edit(); et.putString("sava_weburl",
					 * webView.getUrl().toString()); et.commit();
					 */
					menu_ImageBtn_refresh.setImageDrawable(getResources()
							.getDrawable(R.drawable.refresh));
				}
				if (newProgress >= 100) {
					flag_loading = false;
					progressWeb.setVisibility(View.GONE);
				} else {
					progressWeb.setVisibility(View.VISIBLE);
				}
				if (handler != null) {
					Message msg = handler.obtainMessage();
					msg.arg2 = newProgress;
					handler.sendMessage(msg);
				}

				super.onProgressChanged(webView, newProgress);
			}
		});
	}

	/**
	 * 读取浏览器开启属性的记录
	 */
	private void readWebketPreferen() {
		preferences = getSharedPreferences(SettingSave.PREFERENCES_NAME,
				MODE_WORLD_WRITEABLE);
		boolean isopen = false;
		isopen = preferences.getBoolean(SettingSave.KEY_support_JS, isopen);
		if (isopen) {
			webView.getSettings().setJavaScriptEnabled(true); // 开启JavaScript
		} else {
			webView.getSettings().setJavaScriptEnabled(false); // 关闭JavaScript
		}
		boolean isopen2 = false;
		isopen2 = preferences.getBoolean(SettingSave.KEY_support_ZOOM, isopen2);
		if (isopen2) {
			webView.getSettings().setSupportZoom(true);// 开启缩放
			webView.getSettings().setBuiltInZoomControls(true);// 显示缩放控件图标
		} else {
			webView.getSettings().setSupportZoom(true);
			webView.getSettings().setBuiltInZoomControls(false);
		}
		boolean isopen3 = false;
		isopen3 = preferences.getBoolean(SettingSave.KEY_support_PIC, isopen3);
		if (isopen3) {
			webView.getSettings().setBlockNetworkImage(true); // 显示网络图片
		} else {
			webView.getSettings().setBlockNetworkImage(false); // 不显示网络图片
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		handler = null;
		System.gc();
		System.out.println("mainview页面结束，已提醒系统回收垃圾");
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			boolean canExit = false;
			if (webView.canGoBack()) {
				count = count - 1;
				webView.goBack();
				backHomeNum = 2;
			} else {
				slidingMenu.toggle();
				backHomeNum--;
				if (backHomeNum < 1) {
					Intent back = new Intent();
					back.setClass(getApplicationContext(), Home.class);
					startActivity(back);
					MainView.this.finish();
					overridePendingTransition(R.anim.feature_scale_in,
							R.anim.translate);
				}

			}
			popWindow.dismiss();

		}
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			slidingMenu.toggle();
		}
		return false;
	}

	// settings
	public void setJavascript(boolean flag) {
		Log.e("my_set_js", ":" + flag);
		webView.getSettings().setJavaScriptEnabled(flag);
		// Toast.makeText(getApplicationContext(), "javascript: " + flag,
		// Toast.LENGTH_SHORT).show();
	}

	public void setZoom(boolean flag) {
		Log.e("my_set_zoom", ":" + flag);
		webView.getSettings().setSupportZoom(flag);
		webView.getSettings().setBuiltInZoomControls(flag);
	}

	public void setBlockPicture(boolean flag) {
		Log.e("my_set_pic", ":" + flag);
		webView.getSettings().setBlockNetworkImage(flag);
	}

	public void setCache(boolean flag) {
		Log.e("my_set_cache", ":" + flag);
		if (flag) {
			webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
		} else {
			webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
		}
	}

	// inner class for WebViewCLient
	/**
	 * 下载功能
	 * 
	 * @author baojun
	 *
	 */
	// inner class for download
	private class myDownloaderListener implements DownloadListener {

		@Override
		public void onDownloadStart(final String url, String userAgent,
				String arg2, String arg3, long arg4) {
			// if the SD card can't be written or read,then toast
			if (!Environment.getExternalStorageState().equals(
					Environment.MEDIA_MOUNTED)) {
				Toast.makeText(getApplicationContext(), "no SDcard!", 0).show();
				return;
			} else {

				Dialog alertDialog = new AlertDialog.Builder(MainView.this)
						// .setTitle("文件下载提示：")
						.setMessage("文件下载提示：\n此操作会消耗流量\n" + "确定下载吗？")
						.setIcon(R.drawable.about)
						.setPositiveButton("确定",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										// TODO Auto-generated method stub
										// handler = new PopWin();
										DownLoaderTask download_task = new DownLoaderTask(
												context, new PopWin(),
												downProgressBar, downFileName);
										download_task.execute(url);
										count -= 1;
									}
								})
						.setNegativeButton("取消",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										// TODO Auto-generated method stub
									}
								}).create();
				alertDialog.show();

			}
		}

	}

	@SuppressLint("HandlerLeak")
	private class PopWin extends Handler implements OnClickListener {

		@Override
		public void onClick(View v) {
			popWindow.setFocusable(true);
			if (!popWindow.isShowing()) {
				popWindow.showAtLocation(webView, Gravity.CENTER, 0, 300);
				popWindow.update(400, 180);
				// popWindow.update(400,100);
				Log.e("pop", "yes");
			} else {
				popWindow.dismiss();
			}
		}

		@Override
		public void handleMessage(Message msg) {
			Log.e("handler", "message:" + msg.arg1);
			downloadProgressbar.setProgress(msg.arg1);
			downFileProgress.setText("进度：" + msg.arg1 + "%");
			if (msg.arg1 == 100)
				popWindow.dismiss();
			super.handleMessage(msg);
		}

	}

	@SuppressLint("HandlerLeak")
	private class progressWeb extends Handler {

		@Override
		public void handleMessage(Message msg) {
			Log.e("handler2", "记录:" + msg.arg2);
			progressWeb.setProgress(msg.arg2);
			if (msg.arg2 == 100)
				progressWeb.setProgress(100);
			super.handleMessage(msg);
		}

	}

	@SuppressLint("HandlerLeak")
	private class ChanggeBig extends Handler {

		@Override
		public void handleMessage(Message msg) {
			// 0:全屏
			// 1：退出全屏
			Animation leftAni = AnimationUtils.loadAnimation(context,
					R.anim.ttt);
			if (msg.what == 0)
				{
				changeBigLayout.setVisibility(View.GONE);
				}
			if (msg.what == 1){
				changeBigLayout.setVisibility(View.VISIBLE);
				changeBigLayout.startAnimation(leftAni);
			}
			Log.e("状态", "0为全屏1为退出全屏，当前状态为:" + msg.what);
			super.handleMessage(msg);
		}
			

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		switch (parent.getId()) {
		case R.id.menulist:
			switch (position + 1) {

			case 1:
				Intent intent_filetest = new Intent();
				intent_filetest.putExtra("intent", "mainview");
				intent_filetest.setClass(getApplicationContext(),
						FileTest.class);
				startActivity(intent_filetest);
				overridePendingTransition(R.anim.feature_scale_in,
						R.anim.translate);
				break;
			case 2:
				Intent intent_history = new Intent();
				intent_history.putExtra("intent", "mainview");
				intent_history.setClass(getApplicationContext(), History.class);
				startActivity(intent_history);
				overridePendingTransition(R.anim.feature_scale_in,
						R.anim.translate);
				// MainView.this.finish();
				break;
			case 3:
				Intent intent_bookmark = new Intent();
				intent_bookmark.putExtra("intent", "mainview");
				intent_bookmark.setClass(getApplicationContext(),
						Bookmark.class);
				startActivity(intent_bookmark);
				overridePendingTransition(R.anim.feature_scale_in,
						R.anim.translate);
				// MainView.this.finish();
				break;
			case 4:
				Intent intent_settings = new Intent();
				intent_settings.putExtra("intent", "mainview");
				intent_settings.setClass(getApplicationContext(),
						SettingSave.class);
				startActivity(intent_settings);
				overridePendingTransition(R.anim.feature_scale_in,
						R.anim.translate);
				// MainView.this.finish();
				break;
			case 5:
				Date date = new Date(System.currentTimeMillis());
				String name = webView.getTitle();
				String url = webView.getUrl();
				BookmarkDAO bookmarkDAO = new BookmarkDAO(context);
				BW_bookmark bw_bookmark = new BW_bookmark();
				bw_bookmark.setFlag(name);
				bw_bookmark.setWeburl(url);
				bw_bookmark.setTime(date.toLocaleString().toString());
				bookmarkDAO.add(bw_bookmark);
				new AboutDialog().getToast(getApplicationContext(), "书签添加成功！",
						Gravity.CENTER, 0);
				// MainView.this.finish();
				break;

			}
			slidingMenu.toggle();
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.menu_imagebtn_forward:
			webView.goForward();
			break;
		case R.id.menu_imagebtn_back:
			webView.goBack();
			count -= 1;

			break;
		case R.id.menu_imagebtn_refresh:
			if (flag_loading == false)
				webView.reload();
			else
				webView.stopLoading();
			break;
		case R.id.menu_imagebtn_home:
			Intent intent = new Intent();
			intent.setClass(getApplicationContext(), Home.class);
			startActivity(intent);
			MainView.this.finish();
			overridePendingTransition(R.anim.feature_scale_in, R.anim.translate);
			break;
		case R.id.copy:
			// 粘贴板
			ClipboardManager cmb = (ClipboardManager) context
					.getSystemService(context.CLIPBOARD_SERVICE);
			cmb.setText(webView.getUrl());
			new AboutDialog().getToast(context, "网址已复制到剪贴板", Gravity.CENTER, 0);
			break;
		case R.id.goSearchText:
			slidingMenu.toggle();
			webView.loadUrl(new BackUrl().openBowers(searchText.getText()
					.toString()));
			// 强制隐藏输入法
			((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
					.hideSoftInputFromWindow(MainView.this.getCurrentFocus()
							.getWindowToken(),
							InputMethodManager.HIDE_NOT_ALWAYS);
			break;
		case R.id.exit:
			/*
			 * stopService(intentServer);// 停止服务 MainView.this.finish();
			 */
			/**
			 * 回退HOME桌面
			 */
			slidingMenu.toggle();
			Intent home = new Intent(Intent.ACTION_MAIN);
			home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			home.addCategory(Intent.CATEGORY_HOME);
			startActivity(home);
			overridePendingTransition(R.anim.feature_scale_in, R.anim.translate);
			break;
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		// TODO Auto-generated method stub
		switch (buttonView.getId()) {
		case R.id.checkBox1:
			Message msg = new ChanggeBig().obtainMessage();
			if (isChecked) {
				msg.what = 0;
				new AboutDialog().getToast(getApplicationContext(), "已开启全屏浏览",
						Gravity.TOP, 0);
				buttonView.setText("退出全屏");
				// 全屏
				//((Activity) context).requestWindowFeature(Window.FEATURE_NO_TITLE);
				((Activity) context).getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN); 
				// /< 有米广告展示
				
//				  adclose.setVisibility(View.VISIBLE); LinearLayout
//				  adLayout=(LinearLayout) findViewById(R.id.adLayout);
//				  AdManager.getInstance(context).init("f6cd83fb7a8b7cd8",
//				  "98af27ec7f4af709", false); AdView adView = new
//				  AdView(context, AdSize.SIZE_300x250);
//				  adLayout.addView(adView);
//				  SpotManager.getInstance(context).loadSpotAds();
//				  SpotManager.getInstance(context).showSpotAds(this);
//				 
			} else {

				msg.what = 1;
				new AboutDialog().getToast(getApplicationContext(), "已退出全屏浏览",
						Gravity.TOP, 0);
				buttonView.setText("全屏");
				// /< 有米广告展示
				adclose.setVisibility(View.GONE);
				
				//退出全屏
				final WindowManager.LayoutParams attrs = getWindow()
						.getAttributes();
				attrs.flags = (WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
				getWindow().setAttributes(attrs);
				getWindow().clearFlags(
						WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
			}
			new ChanggeBig().sendMessage(msg);
			overridePendingTransition(R.anim.feature_scale_in, R.anim.translate);
			break;
		}
	}

	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.searchText:
			Timer tExit1 = null;
			if (isExit1 == false) {
				isExit1 = true; // 准备退出第二次的相应
				if (keyCode == KeyEvent.KEYCODE_ENTER) {
					slidingMenu.toggle();
					webView.loadUrl(new BackUrl().openBowers(searchText
							.getText().toString()));
					// 强制隐藏输入法
					((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
							.hideSoftInputFromWindow(MainView.this
									.getCurrentFocus().getWindowToken(),
									InputMethodManager.HIDE_NOT_ALWAYS);
				}
				tExit1 = new Timer();
				tExit1.schedule(new TimerTask() {
					@Override
					public void run() {
						isExit1 = false; // 取消退出
					}
				}, 1000); // 如果2秒钟内没有按下返回键，则启动定时器取消掉刚才执行的任务

			}

			break;
		}
		return false;
	}
}
