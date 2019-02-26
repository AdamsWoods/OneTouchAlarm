package com.Demo.onekey2alarm.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.Switch;

public class SlidingMenu extends HorizontalScrollView {
	private LinearLayout mWapper;
	private ViewGroup mMenu;
	private ViewGroup mContent;
	private int mScreenWidth;
	
	private int mMwnuWidth;
	
	
	//DP
	private int mMenuRingtPadding = 50;
	private boolean once;
	
	
	
	public SlidingMenu(Context context, AttributeSet attrs) 
	{
		super(context, attrs);
		// TODO Auto-generated constructor stub
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics outMetrics = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(outMetrics);
		mScreenWidth = outMetrics.widthPixels;
		
		//把sp转化为px
		mMenuRingtPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, context.getResources().getDisplayMetrics());
		
		
	}
	/*
	 * 设置宽高
	 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		if (!once) {
			mWapper = (LinearLayout) getChildAt(0);
			mMenu = (ViewGroup) mWapper.getChildAt(0);
			mContent = (ViewGroup) mWapper.getChildAt(1);
			mMwnuWidth = mMenu.getLayoutParams().width = mScreenWidth - mMenuRingtPadding;
			mContent.getLayoutParams().width = mScreenWidth;
			once = true;
		}
		
		// TODO Auto-generated method stub
		
		
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}
	//设置偏移量
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		// TODO Auto-generated method stub
		
		super.onLayout(changed, l, t, r, b);
		if (changed) {
			this.scrollTo(mMwnuWidth, 0);
		}
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent ev) 
	{
		int action = ev.getAction();
		switch (action) {
		case MotionEvent.ACTION_UP:
			//隐藏在左边的宽度
			int scrollX = getScrollX();
			
			if (scrollX >= mMwnuWidth / 2) 
			{
				this.smoothScrollTo(mMwnuWidth, 0);
			}else {
				this.smoothScrollTo(0,0);
			}
			return true;
		}
		return super.onTouchEvent(ev);
	}
	
	
}




















