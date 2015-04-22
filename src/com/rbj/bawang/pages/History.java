package com.rbj.bawang.pages;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.rbj.bawang.activity.Home;
import com.rbj.bawang.activity.MainView;
import com.rbj.bawang.bean.BW_history;
import com.rbj.bawang.database.HistoryDAO;
import com.rbj.bawang.pages.History;
import com.rbj.bawang.util.AboutDialog;
import com.rbj.bawang.util.SyncSet;
import com.rbj.browser.R;

import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class History extends Activity implements OnItemClickListener,OnItemLongClickListener,OnClickListener{
	// 获取所有收入信息，并存储到List泛型集合中
	private List<BW_history> listinfos;
	private HistoryDAO flagDAO = new HistoryDAO(History.this);// 创建FlagDAO对象
	// private BW_history tb_flag = new BW_history();
	private ListView listView_history;
	private SimpleAdapter simpleAdapter;
	private String[] str_flag = null;// 定义字符串数组，用来存储收入信息
	private String[] str_url = null;// 定义字符串数组，用来存储收入信息
	private List<Map<String, String>> listData = new ArrayList<Map<String, String>>();
	private LinearLayout historyBg;
	public static History history = null;
	private String getIntentName;
	private Context context;
	private ImageButton backHome;
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		System.gc();
		System.out.println("history页面结束，已提醒系统回收垃圾");
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.page_history);
		History.history = this;
		context=History.this;
		
		init();
	}

	private void init() {
		// TODO Auto-generated method stub
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		getIntentName = bundle.getString("intent").toString();
		
		backHome=(ImageButton) findViewById(R.id.backtohome);//返回主页
		backHome.setOnClickListener(this);;
		
		listView_history = (ListView) findViewById(R.id.history_list);
		listView_history.setOnItemClickListener(this);
		listView_history.setOnItemLongClickListener(this);
		
		historyBg = (LinearLayout) findViewById(R.id.history_bg);
		SyncSet syncLight = new SyncSet();
		syncLight.setBgForUI(getApplicationContext(), historyBg);
		syncLight.syncLight(History.this, historyBg);

		/**
		 * 从数据库得到数据
		 */

		if (flagDAO.getCount() > 60) {
			listinfos = flagDAO.getScrollData(flagDAO.getCount() - 50,
					flagDAO.getCount());

		} else {
			listinfos = flagDAO.getScrollData(0, flagDAO.getCount());
		}

		String[] strInfos = null;// 定义字符串数组，用来存储收入信息
		strInfos = new String[listinfos.size()];// 设置字符串数组的长度
		str_flag = new String[listinfos.size()];// 设置字符串数组的长度
		str_url = new String[listinfos.size()];// 设置字符串数组的长度
		int m = 0;// 定义一个开始标识
		if (flagDAO.getMaxId() == flagDAO.getCount() + 50) {
			new AboutDialog().getToast(getApplicationContext(),
					"为方便浏览，超过60条记录自动只显示50条", Gravity.CENTER, 0);
		}
		if (flagDAO.getCount() == 0) {
			new AboutDialog().getToast(getApplicationContext(), "没有浏览历史",
					Gravity.CENTER, 0);
			str_flag = null;
			str_url = null;
		} else {
			listData = new ArrayList<Map<String, String>>();
			for (BW_history tb_flag : listinfos) {// 遍历List泛型集合
				// 将收入相关信息组合成一个字符串，存储到字符串数组的相应位置
				strInfos[m] = tb_flag.getid() + "|" + tb_flag.getFlag() + "|"
						+ tb_flag.getWeburl();
				str_flag[m] = tb_flag.getFlag();
				str_url[m] = tb_flag.getWeburl();
				
				String urls = tb_flag.getFlag();
				String[] str;
				str = urls.split("--");
				String s2 = "";
				s2 = str[0];
				Log.e("s2+str.length", s2 + str.length + " ");
				Map<String, String> map = new HashMap<String, String>();
				map.put("name", s2);
				map.put("url", tb_flag.getWeburl());
				listData.add(map);
				m++;
			}
			}
			simpleAdapter = new SimpleAdapter(context, listData,
					R.layout.history_display_style, new String[] { "name",
							"url" }, new int[] { R.id.website_names,
							R.id.website_url });
			listView_history.setAdapter(simpleAdapter);
		}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent intent = new Intent();
			if (getIntentName.equals("home")) {
				Home.home.finish();
				intent.setClass(getApplicationContext(), Home.class);
				startActivity(intent);
			}
			History.this.finish();
			overridePendingTransition(R.anim.feature_scale_in, R.anim.translate);
		}
		return false;
	}

	@SuppressWarnings("deprecation")
	public String forDate() {
		String dateTest = "";
		Date date = new Date(System.currentTimeMillis());
		String time = date.toLocaleString().toString();
		for (int i = 0; i < 50; i++) {
			dateTest += "时间是:" + time;
		}
		return dateTest;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.historymenu, menu);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case R.id.history_clean:
			for (int i = 1; i <= flagDAO.getMaxId(); i++) {
				flagDAO.detele(i);
				Log.e("删除了：", flagDAO.find(i) + "");
			}
			new AboutDialog().getToast(getApplicationContext(),
					"已清空浏览历史，继续看新闻吧！", Gravity.TOP, 0);
			String[] a = null;
			str_flag = a;
			str_url = a;
			simpleAdapter.notifyDataSetChanged();
			simpleAdapter.notifyDataSetInvalidated();
			listView_history.destroyDrawingCache();
			break;
		case R.id.history_shutdown:
			SharedPreferences sp1 = History.this.getSharedPreferences(
					"addHistory", MODE_PRIVATE);
			if (sp1.getBoolean("addHistory", true)) {
				item.setTitle("开启记录历史");
				SharedPreferences sp = History.this.getSharedPreferences(
						"addHistory", 0);
				SharedPreferences.Editor et = sp.edit();
				et.putBoolean("addHistory", false);
				et.commit();
				new AboutDialog().getToast(getApplicationContext(),
						"已开启无痕浏览\n浏览器将不会记录下你的浏览的网页.请放心浏览", Gravity.CENTER, 0);
			} else {
				item.setTitle("开启无痕浏览");
				SharedPreferences sp = History.this.getSharedPreferences(
						"addHistory", 0);
				SharedPreferences.Editor et = sp.edit();
				et.putBoolean("addHistory", true);
				et.commit();
				new AboutDialog().getToast(getApplicationContext(),
						"已开启记录历史，浏览器将不会记录下你的浏览的网页！", Gravity.CENTER, 0);
			}
			break;
		}
		return super.onMenuItemSelected(featureId, item);
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {
		// TODO Auto-generated method stub
		String urls = str_flag[position].toString();
		String[] str;
		str = urls.split("--");
		String s2 = "";
		s2 = str[1];
		ClipboardManager cmb = (ClipboardManager) context
				.getSystemService(context.CLIPBOARD_SERVICE);
		cmb.setText(listData.get(position).get("url"));
		new AboutDialog().getToast(getApplicationContext(),
				"记录时间：" + s2+"\n此地址已复制到粘贴板", Gravity.CENTER, 0);
		return false;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		String urls = str_url[position].toString();
		Log.i("得到的链接::", urls);
		Intent intent = new Intent();
		if (getIntentName.equals("home"))
			Home.home.finish();
		else
			MainView.mainView.finish();
		intent.setClass(getApplicationContext(), MainView.class);
		intent.putExtra("end", urls);
		startActivity(intent);
		History.this.finish();
		overridePendingTransition(R.anim.feature_scale_in,
				R.anim.translate);
		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.backtohome:
			Intent intent = new Intent();
			if (getIntentName.equals("home")) {
				Home.home.finish();
				intent.setClass(getApplicationContext(), Home.class);
				startActivity(intent);
			}
			History.this.finish();
			overridePendingTransition(R.anim.feature_scale_in, R.anim.translate);
			break;

		default:
			break;
		}
	}

}