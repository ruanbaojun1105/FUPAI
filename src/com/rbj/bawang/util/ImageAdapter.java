package com.rbj.bawang.util;

import com.rbj.bawang.activity.Home;
import com.rbj.bawang.activity.MainView;
import com.rbj.browser.R;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class ImageAdapter extends BaseAdapter {

	private Context mContext;
	private LayoutInflater mInflater;
	private static final int[] ids = { R.drawable.test2, R.drawable.test1a,R.drawable.test3 };

	public ImageAdapter(Context context) {
		mContext = context;
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return Integer.MAX_VALUE; // 返回很大的值使得getView中的position不断增大来实现循环
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.image_item, null);
		}
		((ImageView) convertView.findViewById(R.id.imgView))
				.setImageResource(ids[position % ids.length]);
		convertView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				// bundle.putInt("end", ids[position%ids.length]);
				Intent intent = new Intent(mContext, MainView.class);
				switch (ids[position % ids.length]) {
				case R.drawable.test1a:
					intent.putExtra("end", "http://401763159.qzone.qq.com/");
					mContext.startActivity(intent);
					Home.home.finish();
					break;
				case R.drawable.test2:
					intent.putExtra("end", "http://wufazhuce.com/");
					mContext.startActivity(intent);
					Home.home.finish();
					break;
				case R.drawable.test3:
					intent.putExtra("end", "http://www.douban.com/");
					mContext.startActivity(intent);
					Home.home.finish();
					break;

				default:
					break;
				}

				// overridePendingTransition(R.anim.feature_scale_in,
				// R.anim.translate);
			}
		});
		return convertView;
	}

}
