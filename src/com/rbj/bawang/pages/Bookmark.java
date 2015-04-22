package com.rbj.bawang.pages;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.rbj.bawang.activity.Home;
import com.rbj.bawang.activity.MainView;
import com.rbj.bawang.bean.BW_bookmark;
import com.rbj.bawang.database.BookmarkDAO;
import com.rbj.bawang.util.AboutDialog;
import com.rbj.bawang.util.SyncSet;
import com.rbj.browser.R;

public class Bookmark extends Activity implements OnItemClickListener ,OnClickListener,
		OnItemLongClickListener {
	private ListView bookmarklist;
	private SimpleAdapter adapter;
	private String time = "";
	private List<String> list1 = new ArrayList<String>();
	private List<String> list2 = new ArrayList<String>();
	private List<Map<String, String>> listData = new ArrayList<Map<String, String>>();
	private BookmarkDAO bookmarkDAO;
	private LinearLayout bookmarkBg;
	private String getIntentName;
	private Context context;
	public Bookmark bookmark;
	private ImageButton backHome;

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		System.gc();
		System.out.println("bookmark页面结束，已提醒系统回收垃圾");
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.page_bookmark);
		bookmark = Bookmark.this;
		context = Bookmark.this;

		init();
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent intenta;
			if (getIntentName.equals("home")) {
				Home.home.finish();
				intenta = new Intent(getApplicationContext(), Home.class);
				startActivity(intenta);
			}
			Log.i("activity", getIntentName);
			Bookmark.this.finish();
			overridePendingTransition(R.anim.feature_scale_in, R.anim.translate);

		}

		return false;
	}

	@SuppressLint("InlinedApi")
	public void init() {
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		getIntentName = bundle.getString("intent").toString();

		bookmarkBg = (LinearLayout) findViewById(R.id.bookmark_bg);
		SyncSet syncLight = new SyncSet();
		syncLight.syncLight(Bookmark.this, bookmarkBg);
		syncLight.setBgForUI(getApplicationContext(), bookmarkBg);
		bookmarklist = (ListView) findViewById(R.id.bookmark_list);

		bookmarkDAO = new BookmarkDAO(getApplicationContext());// 创建InaccountDAO对象
		List<BW_bookmark> listinfos = bookmarkDAO.getScrollData(0,
				bookmarkDAO.getCount());
		// String[] strInfos = null;// 定义字符串数组，用来存储收入信息
		// strInfos = new String[listinfos.size()];// 设置字符串数组的长度
		int m = 0;// 定义一个开始标识
		listData = new ArrayList<Map<String, String>>();
		for (BW_bookmark bw_bookmark : listinfos) {// 遍历List泛型集合
			// 将收入相关信息组合成一个字符串，存储到字符串数组的相应位置
			list1.add(bw_bookmark.getFlag());
			list2.add(bw_bookmark.getWeburl());
			Log.e(bw_bookmark.getFlag(), bw_bookmark.getWeburl());
			Map<String, String> map = new HashMap<String, String>();
			map.put("name", bw_bookmark.getFlag());
			map.put("url", bw_bookmark.getWeburl());
			listData.add(map);
			m++;
		}
		adapter = new SimpleAdapter(context, listData,
				R.layout.history_display_style, new String[] { "name", "url" },
				new int[] { R.id.website_names, R.id.website_url });
		;
		bookmarklist.setAdapter(adapter);

		// 设置ListView的项目按下事件监听
		bookmarklist.setOnItemClickListener(this);
		// 设置ListView的项目长按下事件监听
		bookmarklist.setOnItemLongClickListener(this);
		
		backHome=(ImageButton) findViewById(R.id.backtohome);//返回主页
		backHome.setOnClickListener(this);;
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {
		// TODO Auto-generated method stub
		bookmarkDAO = new BookmarkDAO(context);
		try {
			time = bookmarkDAO.find(position + 1).getTime().toString();
			ClipboardManager cmb = (ClipboardManager) context
					.getSystemService(context.CLIPBOARD_SERVICE);
			cmb.setText(time);
			new AboutDialog().getToast(getApplicationContext(),
					"记录时间：" + time+"\n此地址已复制到粘贴板", Gravity.CENTER, 0);
		} catch (Exception e) {
			// TODO: handle exception
			time = "";
		}
		return false;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		Intent intent = new Intent();
		if (getIntentName.equals("home"))
			Home.home.finish();
		else
			MainView.mainView.finish();
		intent.setClass(context, MainView.class);
		intent.putExtra("end", listData.get(position).get("url"));
		startActivity(intent);
		Bookmark.this.finish();
		overridePendingTransition(R.anim.feature_scale_in,
				R.anim.translate);
		
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
			Bookmark.this.finish();
			overridePendingTransition(R.anim.feature_scale_in, R.anim.translate);
			break;

		}
	}

}