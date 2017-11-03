/**
 * Copyright © 2015GreatVision. All rights reserved.
 *
 * @Title: FragmentSettingsSecond.java
 * @Prject: Mul2Launcher
 * @Package: com.sz.ead.framework.mul2launcher.settings
 * @Description: TODO
 * @author: lijungang  
 * @date: 2015-4-25 上午10:49:24
 */
package com.sz.ead.framework.mul2launcher.settings;

import java.lang.reflect.Field;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.setting.settingservice.SettingManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.ViewGroup;

import com.sz.ead.framework.mul2launcher.FragmentBase;
import com.sz.ead.framework.mul2launcher.R;
import com.sz.ead.framework.mul2launcher.settings.about.AboutActivity;
import com.sz.ead.framework.mul2launcher.settings.restore.RestoreActivity;

/**
 * Copyright © 2015GreatVision. All rights reserved.
 *
 * @Title: FragmentSettingsSecond.java
 * @Prject: Mul2Launcher
 * @Package: com.sz.ead.framework.mul2launcher.settings
 * @Description: TODO
 * @author: lijungang 
 * @date: 2015-4-25 上午10:49:24
 */
public class FragmentSettingsSecond extends FragmentBase implements OnClickListener, OnFocusChangeListener{
	
	private SettingManager settingManager;
	private Activity mActivity;
	private FragmentSettingsSection mFragmentSettingsSectionRestore;
	private FragmentSettingsSection mFragmentSettingsSectionAbout;
	
	private static final int FOCUS_NONE = -1;
	private static final int FOCUS_RESTORE = 0;
	private static final int FOCUS_ABOUT = 1;
	private int m_focus;
	
	private Animation mBigAnimation;
	private Animation mSmallAnimation;
	
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		this.mActivity = activity;
		settingManager = (SettingManager)mActivity.getSystemService(Context.SETTING_SERVICE);
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
		v = inflater.inflate(R.layout.settings_fragment_second, container, false);
		mFragmentSettingsSectionRestore = (FragmentSettingsSection) v.findViewById(R.id.settings_restore);
		mFragmentSettingsSectionRestore.setBgImg(R.drawable.settings_section_uf_network);
		mFragmentSettingsSectionRestore.setSectionImg(R.drawable.settings_restore_uf_icon);
		mFragmentSettingsSectionRestore.setSectionText1(getResources().getString(R.string.settings_about_reset), R.color.settings_menu_f_color);
		mFragmentSettingsSectionRestore.setSectionText2("", R.color.settings_menu_f_color);
		
		mFragmentSettingsSectionAbout = (FragmentSettingsSection) v.findViewById(R.id.settings_about);
		mFragmentSettingsSectionAbout.setBgImg(R.drawable.settings_section_uf_lang);
		mFragmentSettingsSectionAbout.setSectionImg(R.drawable.settings_about_uf_icon);
		mFragmentSettingsSectionAbout.setSectionText1(getResources().getString(R.string.settings_menu_about_title), R.color.settings_menu_f_color);
		mFragmentSettingsSectionAbout.setSectionText2(settingManager.getDevName(), R.color.settings_menu_f_color);
		
		mFragmentSettingsSectionRestore.setOnClickListener(this);
		mFragmentSettingsSectionRestore.setOnFocusChangeListener(this);
		mFragmentSettingsSectionAbout.setOnClickListener(this);
		mFragmentSettingsSectionAbout.setOnFocusChangeListener(this);
		
		mBigAnimation = AnimationUtils.loadAnimation(mActivity, R.anim.settings_scale_big_action);
		mSmallAnimation = AnimationUtils.loadAnimation(mActivity, R.anim.settings_scale_small_action);
		
		return v;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
	}
	
	/**
	 * 
	 * @Title: dispatchKeyEvent
	 * @Description: 按键响应
	 * @return: int (-1:不响应, 0：左右响应按键，但不切换fragment, 1:向右翻页, 2:向左翻页)
	 */
	@Override
	public int dispatchKeyEvent(KeyEvent event) {
		// TODO Auto-generated method stub
		int index = -1;
		if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT && event.getAction() == KeyEvent.ACTION_DOWN)
		{
			index = doKeyLeft();
		}else if(event.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT&& event.getAction() == KeyEvent.ACTION_DOWN){
			index = doKeyRight();
		}
		return index;
	}
	/**
	 * 
	 * @Title: doKeyLeft
	 * @Description: 左按键
	 * @return: void
	 */
	@SuppressLint("NewApi")
	private int doKeyLeft(){
		if (m_focus == FOCUS_RESTORE)
		{
			mFragmentSettingsSectionRestore.setBgImg(R.drawable.settings_section_uf_network);
			mFragmentSettingsSectionRestore.setSectionImg(R.drawable.settings_restore_uf_icon);
			mFragmentSettingsSectionRestore.setSectionText1(getResources().getString(R.string.settings_about_reset), R.color.settings_menu_f_color);
			mFragmentSettingsSectionRestore.setSectionText2("", R.color.settings_menu_f_color);
			m_focus = FOCUS_NONE;
			return 2;
		}
		else if (m_focus == FOCUS_ABOUT)
		{
			mFragmentSettingsSectionAbout.setBgImg(R.drawable.settings_section_uf_lang);
			mFragmentSettingsSectionAbout.setSectionImg(R.drawable.settings_about_uf_icon);
			mFragmentSettingsSectionAbout.setSectionText1(getResources().getString(R.string.settings_menu_about_title), R.color.settings_menu_f_color);
			mFragmentSettingsSectionAbout.setSectionText2(settingManager.getDevName(), R.color.settings_menu_f_color);
			
			mFragmentSettingsSectionRestore.requestFocus();
			mFragmentSettingsSectionRestore.setBgImg(R.drawable.settings_section_f_icon);
			mFragmentSettingsSectionRestore.setSectionImg(R.drawable.settings_restore_f_icon);
			mFragmentSettingsSectionRestore.setSectionText1(getResources().getString(R.string.settings_about_reset), R.color.settings_menu_f_color);
			mFragmentSettingsSectionRestore.setSectionText2("", R.color.settings_menu_f_color);
			m_focus = FOCUS_RESTORE;
		}
		
		return 0;
	}
	
	/**
	 * 
	 * @Title: doKeyLeft
	 * @Description: 左按键
	 * @return: void
	 */
	@SuppressLint("NewApi")
	private int doKeyRight(){
		if (m_focus == FOCUS_RESTORE)
		{
			mFragmentSettingsSectionRestore.setBgImg(R.drawable.settings_section_uf_network);
			mFragmentSettingsSectionRestore.setSectionImg(R.drawable.settings_restore_uf_icon);
			mFragmentSettingsSectionRestore.setSectionText1(getResources().getString(R.string.settings_about_reset), R.color.settings_menu_f_color);
			mFragmentSettingsSectionRestore.setSectionText2("", R.color.settings_menu_f_color);
			
			mFragmentSettingsSectionAbout.requestFocus();
			mFragmentSettingsSectionAbout.setBgImg(R.drawable.settings_section_f_icon);
			mFragmentSettingsSectionAbout.setSectionImg(R.drawable.settings_about_f_icon);
			mFragmentSettingsSectionAbout.setSectionText1(getResources().getString(R.string.settings_menu_about_title), R.color.settings_menu_f_color);
			mFragmentSettingsSectionAbout.setSectionText2(settingManager.getDevName(), R.color.settings_menu_f_color);
			m_focus = FOCUS_ABOUT;
		}
		
		return 0;
	}
	
	/**
	 * 
	 * @Title: initFragmentFocus
	 * @Description: 初始化焦点
	 * @return: void
	 */
	@SuppressLint("NewApi")
	@Override
	public void initFragmentFocus() {
		// TODO Auto-generated method stub
		mFragmentSettingsSectionRestore.requestFocus();
		mFragmentSettingsSectionRestore.setBgImg(R.drawable.settings_section_f_icon);
		mFragmentSettingsSectionRestore.setSectionImg(R.drawable.settings_restore_f_icon);
		mFragmentSettingsSectionRestore.setSectionText1(getResources().getString(R.string.settings_about_reset), R.color.settings_menu_f_color);
		mFragmentSettingsSectionRestore.setSectionText2("", R.color.settings_menu_f_color);
		mFragmentSettingsSectionRestore.startAnimation(mBigAnimation);
		m_focus = FOCUS_RESTORE;
	}

	/* (non-Javadoc)
	 * @see android.view.View.OnFocusChangeListener#onFocusChange(android.view.View, boolean)
	 */
	@Override
	public void onFocusChange(View arg0, boolean arg1) {
		// TODO Auto-generated method stub
		if (arg1)
		{
			if (arg0 == mFragmentSettingsSectionRestore)
			{
				mFragmentSettingsSectionRestore.startAnimation(mBigAnimation);
			}
			else if(arg0 == mFragmentSettingsSectionAbout)
			{
				mFragmentSettingsSectionAbout.startAnimation(mBigAnimation);
			}
		}
		else
		{
			if (arg0 == mFragmentSettingsSectionRestore)
			{
				mFragmentSettingsSectionRestore.startAnimation(mSmallAnimation);
			}
			else if(arg0 == mFragmentSettingsSectionAbout)
			{
				mFragmentSettingsSectionAbout.startAnimation(mSmallAnimation);
			}
		}
	}

	/* (non-Javadoc)
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@SuppressLint("NewApi")
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch(arg0.getId())
		{
		case R.id.settings_restore:
			Intent intent_network = new Intent();
			intent_network.setClass(mActivity, RestoreActivity.class);
			startActivity(intent_network);
			break;
		case R.id.settings_about:
			Intent intent_about = new Intent();
			intent_about.setClass(mActivity, AboutActivity.class);
			startActivity(intent_about);
		default:
			break;
		}
	}

	/**
	 * 
	 * @Title: resetFragment
	 * @Description: 重置状态
	 * @return: void
	 */
	@SuppressLint("NewApi")
	@Override
	public void resetFragment() {
		// TODO Auto-generated method stub
		mFragmentSettingsSectionRestore.setBgImg(R.drawable.settings_section_uf_network);
		mFragmentSettingsSectionRestore.setSectionImg(R.drawable.settings_restore_uf_icon);
		mFragmentSettingsSectionRestore.setSectionText1(getResources().getString(R.string.settings_about_reset), R.color.settings_menu_f_color);
		mFragmentSettingsSectionRestore.setSectionText2("", R.color.settings_menu_f_color);
		
		mFragmentSettingsSectionAbout.setBgImg(R.drawable.settings_section_uf_lang);
		mFragmentSettingsSectionAbout.setSectionImg(R.drawable.settings_about_uf_icon);
		mFragmentSettingsSectionAbout.setSectionText1(getResources().getString(R.string.settings_menu_about_title), R.color.settings_menu_f_color);
		mFragmentSettingsSectionAbout.setSectionText2(settingManager.getDevName(), R.color.settings_menu_f_color);
		
		m_focus = FOCUS_NONE;
	}
	
	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();
		//onDetach();
	}

	@Override
	public void onDetach() {
		// TODO Auto-generated method stub
		super.onDetach();
	}
}
