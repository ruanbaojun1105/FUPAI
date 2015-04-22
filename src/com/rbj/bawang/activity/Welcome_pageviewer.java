package com.rbj.bawang.activity;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.rbj.browser.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

@SuppressLint("ResourceAsColor")
public class Welcome_pageviewer extends Activity implements
		OnPageChangeListener, OnTouchListener {
	private ViewPager mViewPager;
	private View view1, view2, view3;
	private List<View> list;
	public boolean flag = false;
	private LinearLayout pointLLayout;
	private ImageView[] imgs;
	private int count;
	private int currentItem;
	private int lastX = 0;// 获得当前X坐标
	private boolean isChangePath = false;
	private String welText;
	private TextView textWelcome;
	// final ShowDialog Dialog = new ShowDialog();
	private boolean isFirstUse;
	private SharedPreferences preferences;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.viewpager);
		overridePendingTransition(R.anim.base_slide_right_in, R.anim.anim_alpha);
		pointLLayout = (LinearLayout) findViewById(R.id.llayout);
		// textWelcome=(TextView)findViewById(R.id.textwelcome);
		count = pointLLayout.getChildCount();
		// Log.d("aaaa", "" + count);
		imgs = new ImageView[count];
		for (int i = 0; i < count; i++) {
			imgs[i] = (ImageView) pointLLayout.getChildAt(i);
			imgs[i].setEnabled(true);
			imgs[i].setTag(i);
		}
		currentItem = 0;
		imgs[currentItem].setEnabled(false);
		mViewPager = (ViewPager) findViewById(R.id.viewPager);
		mViewPager.setOnPageChangeListener(this);
		mViewPager.setOnTouchListener(this);
		final LayoutInflater inflater = LayoutInflater
				.from(Welcome_pageviewer.this);
		list = new ArrayList<View>();

		SharedPreferences sharedPreferences = this.getSharedPreferences(// 得到
				"share", MODE_PRIVATE);

		SharedPreferences sharedPreferences2 = this.getSharedPreferences(// 得到
				"PRE_NAME", MODE_PRIVATE);
		// int a= Integer.parseInt(getVersion(Welcome.this));
		boolean isFirstRun = sharedPreferences.getBoolean("isFirstRun", true);
		if (isFirstRun) {
			initpage(inflater);
			Editor editor = sharedPreferences.edit();
			editor.putBoolean("isFirstRun", false);
			editor.commit();

			String directory = Environment.getExternalStorageDirectory() + "/";
			String savePath = directory + "BWBowersFile";
			SharedPreferences spb = getSharedPreferences("down_path", 0);
			isChangePath = spb.getBoolean("isChangePath", false);
			if (isChangePath == false) {
				// String down_path = sp.getString("down_path", "");
				SharedPreferences.Editor et = spb.edit();
				et.putString("down_path", savePath);
				et.commit();
			}
			/*
			 * SharedPreferences preferencesb =
			 * getSharedPreferences(Settings.PREFERENCES_NAME,
			 * MODE_WORLD_WRITEABLE); SharedPreferences.Editor etb =
			 * preferencesb.edit(); etb.putInt("VALUE_PIFU", 2); etb.commit();
			 */
		}

		// SharedPreferences sp = getSharedPreferences("loding", 0);
		// flag = sp.getBoolean("loding_flag", false);
		// if (!flag) {
		// }
		else {
			new Handler().postDelayed(new Runnable() {

				@Override
				public void run() {

					Intent intent = new Intent(Welcome_pageviewer.this,
							Start.class);
					startActivity(intent);
					Welcome_pageviewer.this.finish();
				}
			}, 0);
		}
	}

	@SuppressLint("ResourceAsColor")
	public void initpage(LayoutInflater flater) {
		view1 = flater.inflate(R.layout.loading, null);
		view2 = flater.inflate(R.layout.loading, null);
		view3 = flater.inflate(R.layout.loading, null);
		ImageView iv1 = new ImageView(this);
		ImageView iv2 = new ImageView(this);
		ImageView iv3 = new ImageView(this);
		InputStream is1 = this.getResources()
				.openRawResource(R.drawable.lod1);
		InputStream is2 = this.getResources()
				.openRawResource(R.drawable.lod2);
		InputStream is3 = this.getResources()
				.openRawResource(R.drawable.lod3);
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = false;
		options.inSampleSize = 2; // width，hight设为原来的十分一
		Bitmap btp1 = BitmapFactory.decodeStream(is1, null, options);
		Bitmap btp2 = BitmapFactory.decodeStream(is2, null, options);
		Bitmap btp3 = BitmapFactory.decodeStream(is3, null, options);
		Drawable drawable1 = new BitmapDrawable(btp1);
		Drawable drawable2 = new BitmapDrawable(btp2);
		Drawable drawable3 = new BitmapDrawable(btp3);
		// iv1.setImageBitmap(btp);
		iv1.setBackgroundDrawable(drawable1);
		iv2.setBackgroundDrawable(drawable2);
		iv3.setBackgroundDrawable(drawable3);
		list.add(iv1);
		list.add(iv2);
		list.add(iv3);
		mViewPager.setAdapter(pager);
		/*
		 * if(!btp.isRecycled() ){ btp.recycle(); //回收图片所占的内存
		 * System.gc();//提醒系统及时回收 }
		 */
	}

	private void setcurrentPoint(int position) {
		if (position < 0 || position > count - 1 || currentItem == position) {
			return;
		}
		imgs[currentItem].setEnabled(true);
		imgs[position].setEnabled(false);
		currentItem = position;
	}

	PagerAdapter pager = new PagerAdapter() {

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			// TODO Auto-generated method stub
			return arg0 == arg1;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return list.size();
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			// TODO Auto-generated method stub
			container.removeView(list.get(position));
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			// TODO Auto-generated method stub
			container.addView(list.get(position));

			return list.get(position);
		}
	};

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.view.ViewPager.OnPageChangeListener#
	 * onPageScrollStateChanged(int)
	 */
	@Override
	public void onPageScrollStateChanged(int arg0) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.support.v4.view.ViewPager.OnPageChangeListener#onPageScrolled
	 * (int, float, int)
	 */
	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.support.v4.view.ViewPager.OnPageChangeListener#onPageSelected
	 * (int)
	 */
	@Override
	public void onPageSelected(int arg0) {
		// TODO Auto-generated method stub
		setcurrentPoint(arg0);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View.OnTouchListener#onTouch(android.view.View,
	 * android.view.MotionEvent)
	 */
	@Override
	public boolean onTouch(View arg0, MotionEvent arg1) {
		// TODO Auto-generated method stub
		switch (arg1.getAction()) {
		case MotionEvent.ACTION_DOWN:
			lastX = (int) arg1.getX();
			break;

		case MotionEvent.ACTION_UP:
			if (!flag) {
				if ((lastX - arg1.getX() > 100)
						&& (mViewPager.getCurrentItem() == mViewPager
								.getAdapter().getCount() - 1)) {// 从最后一页向右滑动
					new Handler().postDelayed(new Runnable() {
						public void run() {
							SharedPreferences sp = getSharedPreferences(
									"loding", 0);
							boolean isFirstRun = sp.getBoolean("isFirstRun",
									true);
							SharedPreferences.Editor et = sp.edit();
							et.putBoolean("isFirstRun", true);
							et.commit();
							Intent intent = new Intent(Welcome_pageviewer.this,
									Start.class);
							startActivity(intent);
							overridePendingTransition(R.anim.anim_alpha,
									R.anim.anim_alpha);
							finish();
						};
					}, 0);
				}
			}
		}
		return false;
	}

}
