package com.rbj.bawang.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import com.rbj.bawang.pages.Bookmark;
import com.rbj.bawang.pages.History;
import com.rbj.bawang.util.AboutDialog;
import com.rbj.bawang.util.BackUrl;
import com.rbj.bawang.util.ImageAdapter;
import com.rbj.bawang.util.SyncSet;
import com.rbj.browser.R;
import com.rbj.widget.CircleFlowIndicator;
import com.rbj.widget.ViewFlow;
import com.slidingmenu.lib.SlidingMenu;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class Home extends Activity implements OnClickListener ,OnKeyListener,OnItemClickListener{
	private static Boolean isExit = false;
	private static Boolean isExit1 = false;
	private AutoCompleteTextView autoCompleteTextView;
	private GridView gridView;
	private SimpleAdapter adapter;
	private ViewFlow viewFlow;
	private RelativeLayout homebglLayout;
	public static Home home = null;
	private Context context;
	private Intent intentServer = new Intent("InitResorceServer.servers");
	private String[] a = new String[] { "google", "百度", "腾讯", "淘宝", "雅虎",
			"豌豆荚", "人人", "搜狐", "RSS", "书哈哈", "导航" };
	private String[] c = new String[] { "http://www.google.com.hk",
			"http://www.baidu.com", "http://info.3g.qq.com",
			"http://www.taobao.com", "http://www.yahoo.com",
			"http://www.wandoujia.com", "http://www.renren.com",
			"http://www.sohu.com", "http://www.163.com/rss",
			"http://www.shuhaha.com/Book/ShowBookList.aspx",
			"file:///android_asset/testpage.htm" };
	private int[] b = new int[] { R.drawable.google, R.drawable.baidu,
			R.drawable.qq, R.drawable.taobao, R.drawable.yahoo,
			R.drawable.wandoujia, R.drawable.renren, R.drawable.sohu,
			R.drawable.rss, R.drawable.shuhaha, R.drawable.daohang };
	private ArrayList<HashMap<String, Object>> ItemList;
	private SlidingMenu slidingMenu;//slidingmenu
	private TextView searchText,goSearchText;
	private Button exitBtn;
	@SuppressWarnings("deprecation")
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home_index);
		init();
		initSlidingmenu();
	}

	private void initSlidingmenu() {
		// TODO Auto-generated method stub
		slidingMenu=new SlidingMenu(context);
		slidingMenu.setMode(SlidingMenu.LEFT);
		slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		slidingMenu.setShadowWidthRes(R.dimen.shadow_width);
		slidingMenu.setShadowDrawable(R.drawable.shadow);
		slidingMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		slidingMenu.setBehindWidth(300);
		slidingMenu.setFadeDegree(0.5f);
		slidingMenu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
		slidingMenu.setMenu(R.layout.slidingmenu);
		
		Button button;
	    button=(Button) slidingMenu.findViewById(R.id.button5);
		button.setOnClickListener(this);
		
		searchText=(TextView) findViewById(R.id.searchText);//搜索编辑框
		searchText.setOnKeyListener(this);
		goSearchText=(TextView) findViewById(R.id.goSearchText);//开始搜索
		goSearchText.setOnClickListener(this);
		exitBtn=(Button) findViewById(R.id.exit);//退出
		exitBtn.setOnClickListener(this);
		
		
		ListView listView=(ListView) slidingMenu.findViewById(R.id.menulist);

		List<Map<String,Object >>listData =new ArrayList<Map<String,Object >>();
		String []d=new String[] {"文件","历史","书签","设置","关于"};
		int []e=new int[] {android.R.drawable.sym_contact_card,android.R.drawable.ic_menu_recent_history,
						   R.drawable.bookmark,R.drawable.tab_settings,android.R.drawable.sym_action_chat};
		for (int i = 0; i < 5; i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("image", e[i]);
			map.put("text", d[i]);
			listData.add(map);
		}
		SimpleAdapter simpleAdapter = new SimpleAdapter(context, listData,
				R.layout.slidingmenu_listitem, new String[] { "image",
						"text" }, new int[] { R.id.image,
						R.id.text });
		listView.setAdapter(simpleAdapter);
		listView.setOnItemClickListener(this);
	}

	private void init() {
		// TODO Auto-generated method stub
		home = this;
		context = Home.this;
		
		viewFlow = (ViewFlow) findViewById(R.id.viewflow);
		viewFlow.setAdapter(new ImageAdapter(this));
		viewFlow.setmSideBuffer(3); // 实际图片张数， 我的ImageAdapter实际图片张数为3
		CircleFlowIndicator indic = (CircleFlowIndicator) findViewById(R.id.viewflowindic);
		viewFlow.setFlowIndicator(indic);
		viewFlow.setTimeSpan(2000);
		viewFlow.setSelection(3 * 1000); // 设置初始位置
		viewFlow.startAutoFlowTimer(); // 启动自动播放

//======================================================================
		homebglLayout = (RelativeLayout) findViewById(R.id.home_bg);
		new SyncSet().setBgForUI(context, homebglLayout);
//======================================================================
		SharedPreferences hotPreferences = getSharedPreferences("addHistory",MODE_WORLD_WRITEABLE);
		String hotWord = hotPreferences.getString("hotWord", "");
		String[] hotWords = hotWord.split(",");
		autoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView);
		ArrayAdapter<String> AutoAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_dropdown_item_1line, hotWords); // 创建一个ArrayAdapter适配器
		autoCompleteTextView.setAdapter(AutoAdapter);
		autoCompleteTextView.setOnKeyListener(this);
//======================================================================
		gridView = (GridView) findViewById(R.id.index_girdview);
		ItemList = new ArrayList<HashMap<String, Object>>();
		for (int i = 0; i < a.length; i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("name", a[i]);
			map.put("image", b[i]);
			map.put("url", c[i]);
			ItemList.add(map);
		}
		adapter = new SimpleAdapter(getApplicationContext(), ItemList,
				R.layout.index_item_style, new String[] { "image", "name" },
				new int[] { R.id.ItemImage, R.id.ItemText });
		gridView.setAdapter(adapter);
		gridView.setOnItemClickListener(this);
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.menuhome:
			openOptionsMenu();
			break;
			case R.id.button5:
				new AboutDialog().getToast(context, "welcome slidingmenu world"
						+ "\nwish you happy every day!", Gravity.CENTER, 0);
				break;
				case R.id.goSearchText:
					Intent intent = new Intent(context, MainView.class);
					intent.putExtra("end",new BackUrl().openBowers(searchText.getText().toString()));
					startActivity(intent);
					Home.this.finish();
					overridePendingTransition(R.anim.base_slide_right_in, R.anim.anim_alpha);
					break;
					case R.id.exit:
						stopService(intentServer);// 停止服务
						home.finish();
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
				if (keyCode==KeyEvent.KEYCODE_ENTER) {
					Intent intent = new Intent(context, MainView.class);
					intent.putExtra("end",new BackUrl().openBowers(searchText.getText().toString()));
					startActivity(intent);
					Home.this.finish();
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
		case R.id.autoCompleteTextView:
			Timer tExit = null;
			if (isExit1 == false) {
				isExit1 = true; // 准备退出第二次的相应
				if (keyCode == KeyEvent.KEYCODE_ENTER) { // 如果为回车键
					String url = autoCompleteTextView.getText().toString()
							.trim();
					if (!url.isEmpty()) {
						System.out.println(url);
						SharedPreferences sp = Home.this
								.getSharedPreferences("addHistory",
										MODE_WORLD_WRITEABLE);
						String aString = sp.getString("hotWord", "");
						if (aString.contains(url)) {
							Log.e("热词已有无需添加！", "bad:_" + url);
						} else {
							SharedPreferences.Editor et = sp.edit();
							et.putString("hotWord", aString + "," + url);
							et.commit();
							Log.e("热词已经添加！", "ok:_" + url);
						}

						Intent intent = new Intent();
						intent.setClass(getApplicationContext(), MainView.class);
						intent.putExtra("end",new BackUrl().openBowers(url));
						startActivity(intent);
						Home.this.finish();
						overridePendingTransition(R.anim.base_slide_right_in, R.anim.anim_alpha);
					} else
						new AboutDialog().getToast(getApplicationContext(),
								"你还未输入任何字符~", Gravity.TOP, 0);
				}
				tExit = new Timer();
				tExit.schedule(new TimerTask() {
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
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		switch (parent.getId()) {
			case R.id.index_girdview:
				Log.e("toast:", "touched");
				startActivity(new Intent(getApplicationContext(),
					MainView.class).putExtra("end",
					ItemList.get(position).get("url").toString()));
				Home.this.finish();
				overridePendingTransition(R.anim.base_slide_right_in,R.anim.anim_alpha);
				break;
			case R.id.menulist:
				switch (position+1) {
				case 1:
					startActivity(new Intent(getApplicationContext(), FileTest.class)
							.putExtra("intent", "home"));
					overridePendingTransition(R.anim.feature_scale_in, R.anim.translate);
					break;
				case 2:
					startActivity(new Intent(getApplicationContext(), History.class)
							.putExtra("intent", "home"));
					overridePendingTransition(R.anim.feature_scale_in, R.anim.translate);
					Home.this.finish();
					break;
				case 3:
					startActivity(new Intent(getApplicationContext(), Bookmark.class)
							.putExtra("intent", "home"));
					overridePendingTransition(R.anim.feature_scale_in, R.anim.translate);
					Home.this.finish();
					break;
				case 4:
					startActivity(new Intent(getApplicationContext(), SettingSave.class)
							.putExtra("intent", "home"));
					overridePendingTransition(R.anim.feature_scale_in, R.anim.translate);
					break;
				case 5:
					// Toast.makeText(Home.this, "暂无消息~", 0).show();
					// 服务操作
					// conn=new MyconnService();
					new AboutDialog().setDialogView(Home.this);
					break;
					
				}
				slidingMenu.toggle();
		}
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		System.gc();
		System.out.println("home页面结束，已提醒系统回收垃圾");
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK)
			exitBy2Click();
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			slidingMenu.toggle();
		}
		return false;
	}

	
	
	
	// 双击退出
	private void exitBy2Click() {
		Timer tExit = null;
		if (isExit == false) {
			isExit = true; // 准备退出
			slidingMenu.toggle();
			new AboutDialog().getToast(getApplicationContext(), "再按一次回到桌面",
					Gravity.BOTTOM, 0);
			tExit = new Timer();
			tExit.schedule(new TimerTask() {
				@Override
				public void run() {
					isExit = false; // 取消退出
				}
			}, 1000); // 如果2秒钟内没有按下返回键，则启动定时器取消掉刚才执行的任务

		} else {
			Intent home = new Intent(Intent.ACTION_MAIN);
			home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			home.addCategory(Intent.CATEGORY_HOME);
			startActivity(home);
			overridePendingTransition(R.anim.feature_scale_in, R.anim.translate);
			// finish();
			// System.exit(0);
		}

	}

}

class multiwindows implements DialogInterface.OnClickListener {

	@Override
	public void onClick(DialogInterface dialog, int which) {
		ArrayList<HashMap<String, String>> windowsList = new ArrayList<HashMap<String, String>>();
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("WebName", MainView.webView.getTitle());
		map.put("WebUrl", MainView.webView.getUrl());
		windowsList.add(map);

	}

}