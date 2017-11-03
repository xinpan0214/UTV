/**
 * Copyright © 2015GreatVision. All rights reserved.
 *
 * @Title: NavLayout.java
 * @Prject: Mul2Launcher
 * @Package: com.sz.ead.framework.mul2launcher.nav
 * @Description: 导航栏
 * @author: lijungang 
 * @date: 2015-4-22 上午10:50:48
 */
package com.sz.ead.framework.mul2launcher.nav;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import com.sz.ead.framework.mul2launcher.MainActivity;
import com.sz.ead.framework.mul2launcher.R;

public class NavLayout extends RelativeLayout implements OnFocusChangeListener
{
	private Context mContext;
	private RelativeLayout mRllt;
	private RadioButton[] mRadioButton = new RadioButton[3];
	private int mFocusNavIndex; //焦点不在nav上:-1, mainapp:0, appstore:1, settings:2;
	
	public NavLayout(Context context) 
	{
		super(context);
		mContext = context;
		init();
	}
	
	public NavLayout(Context context, AttributeSet attrs) 
	{
		super(context, attrs);
		mContext = context;
		init();
	}

	public NavLayout(Context context, AttributeSet attrs, int defStyle) 
	{
		super(context, attrs, defStyle);
		mContext = context;
		init();
	}
	
	/**
	 * 
	 * @Title: init
	 * @Description: 初始化导航界面
	 * @return: void
	 */
	private void init()
	{
		mRllt = (RelativeLayout) View.inflate(mContext, R.layout.nav_layout, null);
		mRadioButton[0] = (RadioButton)mRllt.findViewById(R.id.nav_mainapp);
		mRadioButton[1] = (RadioButton)mRllt.findViewById(R.id.nav_appstore);
		mRadioButton[2] = (RadioButton)mRllt.findViewById(R.id.nav_settings);
		
		RelativeLayout.LayoutParams rl = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		addView(mRllt, rl);
		
		for(RadioButton button: mRadioButton){
			button.setOnFocusChangeListener(this);
		}
		
		mRadioButton[0].requestFocus();
		mRadioButton[0].requestFocusFromTouch();
		mFocusNavIndex = 0;
	}
	
	/**
	 * 
	 * @Title: isFocusNav
	 * @Description: 导航栏按键函数
	 * @return: boolean
	 */
	public boolean isFocusNav()
	{
		return mFocusNavIndex < 0 ? false : true;
	}
	
	/**
	 * 
	 * @Title: getNavFocusIndex
	 * @Description: 获取导航栏索引
	 * @return
	 * @return: int
	 */
	public int getNavFocusIndex()
	{
		return mFocusNavIndex;
	}
	/**
	 * 
	 * @Title: restoreInstanceState
	 * @Description: 切换语言恢复状态
	 * @return
	 */
	public void restoreInstanceState(int nav_focus, int fragment_focus, boolean isFocusNav){
		mFocusNavIndex = nav_focus;
		if(isFocusNav){
			setFocusNav(mFocusNavIndex);
		}else{
			if(fragment_focus == 2){
				mRadioButton[0].setSelected(false);
				mRadioButton[0].setTextSize(getResources().getDimension(R.dimen.size_28));
				mRadioButton[1].setSelected(false);
				mRadioButton[1].setTextSize(getResources().getDimension(R.dimen.size_28));
				mRadioButton[2].setSelected(true);
				mRadioButton[2].setTextSize(getResources().getDimension(R.dimen.size_32));
			}else if(fragment_focus == 1){
				mRadioButton[0].setSelected(false);
				mRadioButton[0].setTextSize(getResources().getDimension(R.dimen.size_28));
				mRadioButton[1].setSelected(true);
				mRadioButton[1].setTextSize(getResources().getDimension(R.dimen.size_32));
				mRadioButton[2].setSelected(false);
				mRadioButton[2].setTextSize(getResources().getDimension(R.dimen.size_28));
			}else{
				mRadioButton[0].setSelected(true);
				mRadioButton[0].setTextSize(getResources().getDimension(R.dimen.size_32));
				mRadioButton[1].setSelected(false);
				mRadioButton[1].setTextSize(getResources().getDimension(R.dimen.size_28));
				mRadioButton[2].setSelected(false);
				mRadioButton[2].setTextSize(getResources().getDimension(R.dimen.size_28));
			}
		}
	}
	/**
	 * 
	 * @Title: setFocusNav
	 * @Description: 设置导航栏焦点
	 * @return: int 
	 */
	public void setFocusNav(int index)
	{
		mRadioButton[0].setSelected(false);
		mRadioButton[0].setTextSize(getResources().getDimension(R.dimen.size_28));
		mRadioButton[1].setSelected(false);
		mRadioButton[1].setTextSize(getResources().getDimension(R.dimen.size_28));
		mRadioButton[2].setSelected(false);
		mRadioButton[2].setTextSize(getResources().getDimension(R.dimen.size_28));
		
		mFocusNavIndex = index;
		mRadioButton[mFocusNavIndex].setSelected(false);
		mRadioButton[mFocusNavIndex].setTextSize(getResources().getDimension(R.dimen.size_32));
		mRadioButton[mFocusNavIndex].requestFocus();
	}
	
	/**
	 * 
	 * @Title: reSetFocusNav
	 * @Description: 重新设置导航栏焦点
	 * @return: void
	 */
	public void reSetFocusNav()
	{
		mFocusNavIndex = 1;
		mRadioButton[mFocusNavIndex].setSelected(false);
		mRadioButton[mFocusNavIndex].requestFocus();
	}
	
	/**
	 * 
	 * @Title: doKeyEvent
	 * @Description: 导航栏按键函数
	 * @return: int (-2:不响应, -1：down按键, 0 1 2:导航栏焦点)
	 */
	public int doKeyEvent(KeyEvent event)
	{
		int index = -2;
		if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_DOWN && event.getAction() == KeyEvent.ACTION_DOWN)  
		{
			mRadioButton[mFocusNavIndex].setSelected(true);
			mFocusNavIndex = -1;
			index = -1;
		} 
		else if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT && event.getAction() == KeyEvent.ACTION_DOWN)
		{
			if (mFocusNavIndex < 2)
			{
				mRadioButton[mFocusNavIndex].setSelected(false);
				mRadioButton[mFocusNavIndex].setTextSize(getResources().getDimension(R.dimen.size_28));
				
				mFocusNavIndex = mFocusNavIndex + 1;
				index = mFocusNavIndex;
				mRadioButton[mFocusNavIndex].setTextSize(getResources().getDimension(R.dimen.size_32));
				mRadioButton[index].requestFocus();
			}
		} 
		else if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT && event.getAction() == KeyEvent.ACTION_DOWN)
		{
			if (mFocusNavIndex > 0)
			{
				mRadioButton[mFocusNavIndex].setSelected(false);
				mRadioButton[mFocusNavIndex].setTextSize(getResources().getDimension(R.dimen.size_28));
				
				mFocusNavIndex = mFocusNavIndex - 1;
				index = mFocusNavIndex;
				mRadioButton[mFocusNavIndex].setTextSize(getResources().getDimension(R.dimen.size_32));
				mRadioButton[index].requestFocus();
			}
		}
		return index;
	}
	
	/**
	 * 
	 * @Title: onSwitchToAppStore
	 * @Description: 点击"添加应用"切换到应用商城时, 设置导航栏的状态
	 * @return
	 * @return: int
	 */
	public int onSwitchToAppStore()
	{
		int index = 0;
		mFocusNavIndex = 0;
		mRadioButton[mFocusNavIndex].setSelected(false);
		mRadioButton[mFocusNavIndex].setTextSize(getResources().getDimension(R.dimen.size_28));
		
		mFocusNavIndex = mFocusNavIndex + 1;
		index = mFocusNavIndex;
		mRadioButton[mFocusNavIndex].setTextSize(getResources().getDimension(R.dimen.size_32));
		mRadioButton[index].requestFocus();
		return index;
	}
	
	@Override
	public void onFocusChange(View v, boolean hasFocus) 
	{
		if(!hasFocus){
			return ;
		}
		switch(v.getId())
		{
		case R.id.nav_mainapp:
			mFocusNavIndex = 0;
			((MainActivity) mContext).setFocusNav(true);
			break;
		case R.id.nav_appstore:
			mFocusNavIndex = 1;
			((MainActivity) mContext).setFocusNav(true);
			break;
		case R.id.nav_settings:
			mFocusNavIndex = 2;
			((MainActivity) mContext).setFocusNav(true);
			break;
		default:
			break;
		}
	}
	
	@Override
	protected void onDetachedFromWindow() {
		// TODO Auto-generated method stub
		super.onDetachedFromWindow();
	}
}
