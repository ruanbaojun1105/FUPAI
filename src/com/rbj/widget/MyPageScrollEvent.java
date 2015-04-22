package com.rbj.widget;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.support.v4.view.ViewPager.OnPageChangeListener;

public class MyPageScrollEvent implements OnPageChangeListener {
	private ActionBar mActionBar;

	public MyPageScrollEvent(ActionBar actionBar) {
		mActionBar = actionBar;
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		// TODO Auto-generated method stub

	}

	@SuppressLint("NewApi")
	@Override
	public void onPageSelected(int arg0) {
		// TODO Auto-generated method stub
		mActionBar.selectTab(mActionBar.getTabAt(arg0));

	}

}
