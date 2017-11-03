/**
 * Copyright © 2015GreatVision. All rights reserved.
 *
 * @Title: FragmentSettings.java
 * @Prject: Mul2Launcher
 * @Package: com.sz.ead.framework.mul2launcher.settings
 * @Description: TODO
 * @author: lijungang 
 * @date: 2015-4-22 上午10:50:48
 */
package com.sz.ead.framework.mul2launcher.settings;

import java.lang.reflect.Field;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.setting.settingservice.SettingManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

import com.sz.ead.framework.mul2launcher.FixedSpeedScroller;
import com.sz.ead.framework.mul2launcher.FragmentBase;
import com.sz.ead.framework.mul2launcher.R;
import com.sz.ead.framework.mul2launcher.settings.upgrade.ScreenControlView;

public class FragmentSettings extends FragmentBase{
	
	private Activity mActivity;
	private int m_focus_fragment ; //0：设置第一张fragment, 1:设置第二章fragment
	private ViewPager settings_view_pager;
	private ScreenControlView indicator;
	private FixedSpeedScroller scroller = null;
	private FragmentPagerAdapter settings_Adapter;
	private FragmentSettingsFirst mFragmentSettingsFirst;
	private FragmentSettingsSecond mFragmentSettingsSecond;
	
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		this.mActivity = activity;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View v = null;
		v = inflater.inflate(R.layout.settings_fragment, container, false);
		settings_view_pager = (ViewPager)v.findViewById(R.id.settings_view_pager);
		indicator = (ScreenControlView) v.findViewById(R.id.idScreenControl);
		indicator.setTotolScreens(2);
		indicator.setVisibility(View.VISIBLE);
		indicator.setCurrentScreen(0);
		setScrollerTime(300);
		

		
		settings_Adapter = new SettingsSectionsPagerAdapter(getChildFragmentManager());
		settings_view_pager.setAdapter(settings_Adapter);
		m_focus_fragment = 0;
		return v;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
	}
	
	/**
	 * 
	 * @Title: onSettingsSaveInstanceState
	 * @Description: 保存子fragment对象
	 * @return: void
	 */
	public void onSettingsSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		getChildFragmentManager().putFragment(outState,"num00",(Fragment)mFragmentSettingsFirst);
		getChildFragmentManager().putFragment(outState,"num11",(Fragment)mFragmentSettingsSecond);
		outState.putInt("FOCUS", mFragmentSettingsFirst.getFocus());
	}
	/**
	 * 
	 * @Title: onSettingsRestoreInstanceState
	 * @Description: 恢复子fragment对象
	 * @return: void
	 */
	public void onSettingsRestoreInstanceState(Bundle arg0) {
		// TODO Auto-generated method stub
		mFragmentSettingsFirst = (FragmentSettingsFirst)getChildFragmentManager().getFragment(arg0,"num00");
		mFragmentSettingsSecond = (FragmentSettingsSecond)getChildFragmentManager().getFragment(arg0,"num11");
		mFragmentSettingsFirst.setFocus(arg0.getInt("FOCUS"));
	}
	
	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onResume()
	 */
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}
	/**
	 * 
	 * @Title: setScrollerTime
	 * @Description: 自定义ViewPager的Scroller
	 * @param scrollerTime
	 */
	public void setScrollerTime(int scrollerTime) {
		try {
			if (scroller != null) {
				scroller.setTime(scrollerTime);
			} else {
				Field mScroller;
				mScroller = ViewPager.class.getDeclaredField("mScroller");
				mScroller.setAccessible(true);
				scroller = new FixedSpeedScroller(settings_view_pager.getContext(),
						new DecelerateInterpolator());
				scroller.setTime(scrollerTime);
				mScroller.set(settings_view_pager, scroller);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 
	 * @Title: doKeyEvent
	 * @Description: 导航栏按键函数
	 * @return: int (-1:不响应, 0：子菜单响应按键, 2：设置up按键到导航栏)
	 */
	@Override
	public int dispatchKeyEvent(KeyEvent event) {
		// TODO Auto-generated method stub
		int ret = -1;
		if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT && event.getAction() == KeyEvent.ACTION_DOWN){
			if(m_focus_fragment == 0){
				ret = mFragmentSettingsFirst.dispatchKeyEvent(event);
			}else if(m_focus_fragment == 1){
				ret = mFragmentSettingsSecond.dispatchKeyEvent(event);
			}
			if(ret == 1){
				settings_view_pager.setCurrentItem(1 ,true);
				m_focus_fragment = 1;
				if(m_focus_fragment == 0){
					mFragmentSettingsFirst.initFragmentFocus();
				}else if(m_focus_fragment == 1){
					mFragmentSettingsSecond.initFragmentFocus();
				}
				setCurrentPoint();
				return 0;
			}
		}else if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT && event.getAction() == KeyEvent.ACTION_DOWN){
			if(m_focus_fragment == 0){
				ret = mFragmentSettingsFirst.dispatchKeyEvent(event);
			}else if(m_focus_fragment == 1){
				ret = mFragmentSettingsSecond.dispatchKeyEvent(event);
			}
			if(ret == 2){
				settings_view_pager.setCurrentItem(0 ,true);
				m_focus_fragment = 0;
				mFragmentSettingsFirst.fragmentLeftSwitch();
				setCurrentPoint();
				return 0;
			}
		}else if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_UP && event.getAction() == KeyEvent.ACTION_DOWN){
			if(m_focus_fragment == 0){
				mFragmentSettingsFirst.resetFragment();
			}else if(m_focus_fragment == 1){
				mFragmentSettingsSecond.resetFragment();
			}
			return 2;
		}
		return ret;
	}
	/**
	 * 
	 * @Title: setCurrentPoint
	 * @Description: 设置当前point
	 * @return: void
	 */
	private void setCurrentPoint(){
		indicator.setCurrentScreen(m_focus_fragment);
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		// TODO Auto-generated method stub
		if(!hidden){
		}
		super.onHiddenChanged(hidden);
	}
	
	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
//		try {
//		    Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
//		    childFragmentManager.setAccessible(true);
//		    childFragmentManager.set(this, null);
//		} catch (NoSuchFieldException e) {
//		    throw new RuntimeException(e);
//		} catch (IllegalAccessException e) {
//		    throw new RuntimeException(e);
//		}
		super.onDestroyView();
	}

	@Override
	public void onDetach() {
		// TODO Auto-generated method stub
		super.onDetach();
	}
	/**
	 * 
	 * @Title: initFragmentFocus
	 * @Description: 初始化子fragment焦点
	 * @return: void
	 */
	@Override
	public void initFragmentFocus() {
		// TODO Auto-generated method stub
		if(m_focus_fragment == 0){
			mFragmentSettingsFirst.initFragmentFocus();
		}else if(m_focus_fragment == 1){
			mFragmentSettingsSecond.initFragmentFocus();
		}
	}

	@Override
	public void resetFragment() {
		// TODO Auto-generated method stub
		if(m_focus_fragment == 0){
			mFragmentSettingsFirst.resetFragment();
		}else if(m_focus_fragment == 1){
			mFragmentSettingsSecond.resetFragment();
		}
	}
	/**
	 * 
	 * @Title: changeFirstFragment
	 * @Description: 从系统设置切换到应用商城时，将子fragment切换到第一页
	 * @return: void
	 */
	public void changeFirstFragment(){
		indicator.setCurrentScreen(0);
		m_focus_fragment = 0;
		settings_view_pager.setCurrentItem(0 ,true);
	}
	/**
	 * 
	 * @Title: onActivityResult
	 * @Description: 子fragment启动activity时使用的是父fragment对象，故在父fragment中监听界面退出回调
	 * @return: void
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		mFragmentSettingsFirst.onResult(requestCode, resultCode, data);
		super.onActivityResult(requestCode, resultCode, data);
	}
	/**
	 * 
	 * @Title: SettingsSectionsPagerAdapter
	 * @Description: 子fragment所在viewpager所用的adapter
	 * @return: void
	 */
	public class SettingsSectionsPagerAdapter extends FragmentPagerAdapter{
		 
	    public SettingsSectionsPagerAdapter(FragmentManager fm) {  
	        super(fm);  
	    }  

		@Override
		public Fragment getItem(int position) {
			// TODO Auto-generated method stub
			switch (position)
			{
			case 0:
				mFragmentSettingsFirst = new FragmentSettingsFirst();
				return mFragmentSettingsFirst;
			case 1:
				mFragmentSettingsSecond = new FragmentSettingsSecond();
				return mFragmentSettingsSecond;
			default:
				return null;
			}
		}  
	    @Override  
	    public int getCount() {
	    	return 2; 
	    }
	}
}
