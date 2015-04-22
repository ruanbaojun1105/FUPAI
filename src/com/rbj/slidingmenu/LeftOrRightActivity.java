package com.rbj.slidingmenu;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.rbj.browser.R;
import com.slidingmenu.lib.SlidingMenu;

public class LeftOrRightActivity extends Activity implements OnClickListener{
	
	/**
	 * SlidingMenu 
	 * 
    viewAbove - a reference to the layout that you want to use as the above view of the SlidingMenu
    viewBehind - a reference to the layout that you want to use as the behind view of the SlidingMenu
    touchModeAbove - an enum that designates what part of the screen is touchable when the above view is showing. Margin means only the left margin. Fullscreen means the entire screen. Default is margin.
    behindOffset - a dimension representing the number of pixels that you want the above view to show when the behind view is showing. Default is 0.
    behindWidth - a dimension representing the width of the behind view. Default is the width of the screen (equivalent to behindOffset = 0).
    behindScrollScale - a float representing the relationship between the above view scrolling and the behind behind view scrolling. If set to 0.5f, the behind view will scroll 1px for every 2px that the above view scrolls. If set to 1.0f, the behind view will scroll 1px for every 1px that the above view scrolls. And if set to 0.0f, the behind view will never scroll; it will be static. This one is fun to play around with. Default is 0.25f.
    shadowDrawable - a reference to a drawable to be used as a drop shadow from the above view onto the below view. Default is no shadow for now.
    shadowWidth - a dimension representing the width of the shadow drawable. Default is 0.
    fadeEnabled - a boolean representing whether or not the behind view should fade when the SlidingMenu is closing and "un-fade" when opening
    fadeDegree - a float representing the "amount" of fade. 1.0f would mean fade all the way to black when the SlidingMenu is closed. 0.0f would mean do not fade at all.
    selectorEnabled - a boolean representing whether or not a selector should be drawn on the left side of the above view showing a selected view on the behind view.
    selectorDrawable - a reference to a drawable to be used as the selector NOTE : in order to have the selector drawn, you must call SlidingMenu.setSelectedView(View v) with the selected view. Note that this will most likely not work with items in a ListView because of the way that Android recycles item views.

	 */

	
	private SlidingMenu menu ;
	Button btn1,btn5;
	Context context;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		    menu = new SlidingMenu(this);
		    context=this;
		    setContentView(R.layout.testmain);
		    btn1=(Button) findViewById(R.id.button1);
		    btn1.setOnClickListener(this);
	        /*Button button = new Button(this);
	        button.setId(0);
	        button.setText("toggle");
	        button.setOnClickListener(this);
	        */
	       
	        
	        //RelativeLayout view = new RelativeLayout(this);
	        /*RelativeLayout view = findViewById(R.id.re);
	        LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
	        view.addView(button,lp);*/
	        
	        
	        menu.setMode(SlidingMenu.LEFT);
	        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
	        menu.setShadowWidthRes(R.dimen.shadow_width);
	        menu.setShadowDrawable(R.drawable.shadow);
	        menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
	        menu.setBehindWidth(400);
	        menu.setFadeDegree(0.5f);
	        menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
	        menu.setMenu(R.layout.slidingmenu);
	        RelativeLayout view1 = (RelativeLayout) menu.findViewById(R.id.rela);
	        btn5=(Button) menu.findViewById(R.id.button5);
		    btn5.setOnClickListener(this);
	     
			
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub  
		switch (v.getId()) {
		case R.id.button1:
			 menu.toggle();
			break;
			case R.id.button5:
				Toast.makeText(context, "hiiiiii", 0).show();
				break;
		}
	}


}
