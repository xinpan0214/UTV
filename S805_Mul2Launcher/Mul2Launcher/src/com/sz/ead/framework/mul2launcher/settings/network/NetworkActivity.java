/**
 * 
 * Copyright © 2014GreatVision. All rights reserved.
 *
 * @Title: NetworkActivity.java
 * @Prject: BananaTvSetting
 * @Description: TODO
 * @author: luojw 
 * @date: 2014-1-21 上午11:56:16
 */
package com.sz.ead.framework.mul2launcher.settings.network;

import android.content.Context;
import android.content.Intent;
import android.login.loginservice.LoginManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.sz.ead.framework.mul2launcher.BaseActivity;
import com.sz.ead.framework.mul2launcher.MainActivity;
import com.sz.ead.framework.mul2launcher.R;
import com.sz.ead.framework.mul2launcher.settings.util.CommonUtils;
import com.sz.ead.framework.mul2launcher.status.Statusbar;

public class NetworkActivity extends BaseActivity implements OnClickListener,
OnFocusChangeListener{
	
	public static final String TAG = "network";
	
	private WiredActivity m_wiredActivity;			// 有线设置页面
	private DetectionActivity m_detectionActivity; 	// 网络检测页面
	private WifiActivity m_wifiActivity;			// WIFI设置页面

	private RadioButton m_tv_wired;		// 有线		
	private RadioButton m_tv_wifi;			//	 WI-FI
	private RadioButton m_tv_detection;	// 网络检测
	private RadioGroup m_tv_radiogroup;
	private Statusbar mStatus_Bar;
	LoginManager mLogin;
	
	enum FOCUS
	{
		WIRED,				// 有线
		WIRED_ACTIVITY,		// 有线页面
		WIFI,				// 无线
		WIFI_ACTIVITY,		// 无线页面
		DETECTION,			// 网络检测
		DETECTION_ACTIVITY,	// 网络检测页面
		AUTO,				// 自动获取
		MANUAL,				// 手动配置
	};
	
	private FOCUS m_focus;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings_network_activity);
		 
		findViews();
		setListeners();
		
		mLogin = (LoginManager)this.getSystemService(Context.LOGIN_SERVICE);
		
		CommonUtils.setContext(this);
		
		m_wiredActivity = new WiredActivity(this);
		m_detectionActivity = new DetectionActivity(this);
		m_wifiActivity = new WifiActivity(this);
		
		LayoutParams params =  
	            new LinearLayout.LayoutParams( 
	                LayoutParams.FILL_PARENT,
	                LayoutParams.FILL_PARENT);
		
		this.addContentView(m_wiredActivity, params);
		this.addContentView(m_detectionActivity, params);
		this.addContentView(m_wifiActivity, params);
	
		
		boolean isHwCon = CommonUtils.getSetting().getEthMagHWConnected();
		if (isHwCon)
		{
			this.m_tv_wired.requestFocus();
			m_focus = FOCUS.WIRED;
			this.wiredActivityShow();
			setMenuTextColor(1,true);
			mLogin.setPageType(2);
		}
		else
		{
			this.m_tv_wifi.requestFocus();
			m_focus = FOCUS.WIFI;
			this.m_wifiActivity.startScanWifi();
			this.wifiActivityShow();
			setMenuTextColor(2,true);
			mLogin.setPageType(3);
		}
		
	}

	/**
	 * 初始化控件
	 * @Title: findViews
	 * @Description: TODO
	 * @return: void
	 */
	private void findViews() 
	{
		this.m_tv_wired = (RadioButton)findViewById(R.id.id_setting_network_wired);
		this.m_tv_wifi = (RadioButton)findViewById(R.id.id_setting_network_wifi);
		this.m_tv_detection = (RadioButton)findViewById(R.id.id_setting_network_detection);
		this.m_tv_radiogroup = (RadioGroup)findViewById(R.id.nav_radiogroup);
		
		mStatus_Bar = (Statusbar) findViewById(R.id.llyt_main_header);
		mStatus_Bar.setTaskBarTitle(this.getResources().getString(R.string.settings_menu_network_title));
	}
	
	 
	/**
	 * 设置监听
	 * @Title: setListeners
	 * @Description: TODO
	 * @return: void
	 */
	 public void setListeners() {
		this.m_tv_wired.setOnClickListener(this);
		this.m_tv_wifi.setOnClickListener(this);
		this.m_tv_detection.setOnClickListener(this);
		
		this.m_tv_wired.setOnFocusChangeListener(this);
		this.m_tv_wifi.setOnFocusChangeListener(this);
		this.m_tv_detection.setOnFocusChangeListener(this); 
	}
	
	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		// TODO Auto-generated method stub
	}
	
	 @Override
	 public void onWindowFocusChanged(boolean hasFocus) {
	  // TODO Auto-generated method stub
		if(m_focus == FOCUS.WIRED){
			setMenuTextColor(1, true);
		}else if(m_focus == FOCUS.WIFI){
			setMenuTextColor(2, true);
		}else if(m_focus == FOCUS.DETECTION){
			setMenuTextColor(3, true);
		}
		super.onWindowFocusChanged(hasFocus);
	 }
	
	/**
	 * 有线页面显示
	 * @Title: wiredActivityShow
	 * @Description: TODO
	 * @return: void
	 */
	public void wiredActivityShow()
	{
		this.m_wiredActivity.setVisibility(View.VISIBLE);
		this.m_wifiActivity.setVisibility(View.GONE);
		this.m_detectionActivity.setVisibility(View.GONE);
	}
	 
	/**
	 * 网络检测页面显示
	 * @Title: detectionActivityShow
	 * @Description: TODO
	 * @return: void
	 */
	public void detectionActivityShow()
	{
		this.m_wiredActivity.setVisibility(View.GONE);
		this.m_wifiActivity.setVisibility(View.GONE);
		this.m_detectionActivity.setVisibility(View.VISIBLE);
	}
	
	/**
	 * WIFI页面显示 
	 * @Title: wifiActivityShow
	 * @Description: TODO
	 * @return: void
	 */
	public void wifiActivityShow()
	{
		this.m_wifiActivity.setVisibility(View.VISIBLE);
		this.m_wiredActivity.setVisibility(View.GONE);
		this.m_detectionActivity.setVisibility(View.GONE);
	}
	
	/**
	 * 按键处理
	 */
	@Override
	public boolean dispatchKeyEvent(KeyEvent event)
	{
		
		if (this.m_wifiActivity.getIsFocusListView())
		{
			if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT && event.getAction() == KeyEvent.ACTION_DOWN)
			{
				this.m_wifiActivity.doKeyRight();
			}
			else
			{
				super.dispatchKeyEvent(event);
			}
			
			return true;
		}
		
		if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT && event.getAction() == KeyEvent.ACTION_DOWN)
		{
			doKeyRight();
		}
		else if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT && event.getAction() == KeyEvent.ACTION_DOWN)
		{
			doKeyLeft();
		}
		else if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_UP && event.getAction() == KeyEvent.ACTION_DOWN)
		{
			doKeyUp();
		}
		else if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_DOWN && event.getAction() == KeyEvent.ACTION_DOWN)
		{
			doKeyDown(event);
		}
		else if (((event.getKeyCode() == KeyEvent.KEYCODE_0) ||
				(event.getKeyCode() == KeyEvent.KEYCODE_1) ||
				(event.getKeyCode() == KeyEvent.KEYCODE_2) ||
				(event.getKeyCode() == KeyEvent.KEYCODE_3) ||
				(event.getKeyCode() == KeyEvent.KEYCODE_4) ||
				(event.getKeyCode() == KeyEvent.KEYCODE_5) ||
				(event.getKeyCode() == KeyEvent.KEYCODE_6) ||
				(event.getKeyCode() == KeyEvent.KEYCODE_7) ||
				(event.getKeyCode() == KeyEvent.KEYCODE_8) ||
				(event.getKeyCode() == KeyEvent.KEYCODE_9) )
				&& (event.getAction() == KeyEvent.ACTION_DOWN))			// 数字键处理
		{
			doKeyNum(event);
		}
		else if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_CENTER && event.getAction() == KeyEvent.ACTION_UP)
		{
			doKeyOk();
		}
		else if (event.getKeyCode() == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_DOWN)
		{
			doKeyDel();
		}
		else if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN)
		{
			if (!m_wiredActivity.isSettingOn())
			{
				if (m_focus == FOCUS.WIFI_ACTIVITY)
				{
					if (m_wifiActivity.onKeyBack())
					{
						setTitleShow(true);
						return true;
					}
				}
				
				this.m_wifiActivity.back();
				setResult(0);
				finish();
			}
		}
		
		return true;
	}
	
	/**
	 * 方向右键处理
	 * @Title: doKeyRight
	 * @Description: TODO
	 * @return: void
	 */
	public void doKeyRight()
	{
		if (m_focus == FOCUS.WIRED)
		{
			this.m_tv_wifi.requestFocus();
			m_focus = FOCUS.WIFI;
			this.wifiActivityShow();
			this.setMenuTextColor(2,true);
			this.m_wifiActivity.checkHWConnect();
			mLogin.setPageType(3);
		}
		else if (m_focus == FOCUS.WIFI)
		{
			this.m_tv_detection.requestFocus();
			m_focus = FOCUS.DETECTION;
			this.detectionActivityShow();
			this.setMenuTextColor(3,true);
			mLogin.setPageType(4);
		}
		else if (m_focus == FOCUS.WIFI_ACTIVITY)
		{
			this.m_wifiActivity.doKeyRight();
		}
		else if (m_focus == FOCUS.DETECTION)
		{
			this.m_tv_wired.requestFocus();
			m_focus = FOCUS.WIRED;
			this.wiredActivityShow();
			this.setMenuTextColor(1,true);
			mLogin.setPageType(2);
		}
		else if (m_focus == FOCUS.WIRED_ACTIVITY)
		{
			this.m_wiredActivity.doKeyRight();
		}
	}
	
	/**
	 * 方向左键处理
	 * @Title: doKeyLeft
	 * @Description: TODO
	 * @return: void
	 */
	public void doKeyLeft()
	{
		if (m_focus == FOCUS.WIRED)
		{
			this.m_tv_detection.requestFocus();
			m_focus = FOCUS.DETECTION; 
			
			this.detectionActivityShow();
			this.setMenuTextColor(3,true);
			mLogin.setPageType(4);
		}
		else if (m_focus == FOCUS.WIFI)
		{
			this.m_tv_wired.requestFocus();
			m_focus = FOCUS.WIRED;
				
			this.wiredActivityShow();
			this.setMenuTextColor(1,true);
			mLogin.setPageType(2);
		}
		else if (m_focus == FOCUS.DETECTION)
		{
			this.m_tv_wifi.requestFocus();
			m_focus = FOCUS.WIFI;
			
			this.wifiActivityShow();
			this.setMenuTextColor(2,true);
			this.m_wifiActivity.checkHWConnect();
			mLogin.setPageType(3);
		}
		else if (m_focus == FOCUS.WIRED_ACTIVITY)
		{
			this.m_wiredActivity.doKeyLeft();
		}
		else if (m_focus == FOCUS.WIFI_ACTIVITY)
		{
			this.m_wifiActivity.doKeyLeft();
		}
	}
	
	/**
	 * 方向上键处理
	 * @Title: doKeyUp
	 * @Description: TODO
	 * @return: void
	 */
	public void doKeyUp()
	{
		if (m_focus == FOCUS.WIRED_ACTIVITY)
		{
			if (this.m_wiredActivity.doKeyUp())
			{
				this.m_tv_wired.requestFocus();
				this.m_focus = FOCUS.WIRED;
				setMenuTextColor(1,true);
			}
		}
		else if (m_focus == FOCUS.DETECTION_ACTIVITY)
		{
			
			this.m_tv_detection.requestFocus();
			this.m_focus = FOCUS.DETECTION;
			setMenuTextColor(3,true);
			
		}
		else if (m_focus == FOCUS.WIFI_ACTIVITY)
		{
			if (this.m_wifiActivity.doKeyUp())
			{
				this.m_tv_wifi.requestFocus();
				this.m_focus = FOCUS.WIFI;
				setMenuTextColor(2,true);
			}
		}
	}
	
	/**
	 * 方向下键处理
	 * @Title: doKeyDown
	 * @Description: TODO
	 * @param event
	 * @return: void
	 */
	public void doKeyDown(KeyEvent event)
	{
		if (m_focus == FOCUS.WIRED)
		{
			this.m_wiredActivity.autoRequestFocus();
			this.m_focus = FOCUS.WIRED_ACTIVITY;
			this.setMenuTextColor(1,false);
		}
		else if (m_focus == FOCUS.WIRED_ACTIVITY)
		{
			this.m_wiredActivity.doKeyDown();
		}
		else if (m_focus == FOCUS.DETECTION)
		{
			this.m_detectionActivity.doKeyDown();
			this.m_focus = FOCUS.DETECTION_ACTIVITY;
			this.setMenuTextColor(3,false);
		}
		else if (m_focus == FOCUS.WIFI)
		{
			this.m_wifiActivity.doKeyDown();
			this.m_focus = FOCUS.WIFI_ACTIVITY;
			this.setMenuTextColor(2,false);
		}
		else if (m_focus == FOCUS.WIFI_ACTIVITY)
		{
			this.m_wifiActivity.doKeyDown();
		}

	}
	
	/**
	 * 设置菜单栏的字体颜色，1代表有线，2代表wifi，3代表网络检测
	 * @Title: setMenuTextColor
	 * @Description: TODO
	 * @param index
	 * @return: void
	 */
	public void setMenuTextColor(int index, boolean isFocus)
	{
		if (1 == index)
		{
			this.m_tv_wifi.setTextSize(getResources().getDimension(R.dimen.size_28));
			this.m_tv_detection.setTextSize(getResources().getDimension(R.dimen.size_28));
			this.m_tv_wifi.setSelected(false);
			this.m_tv_detection.setSelected(false);
			if(isFocus){
				this.m_tv_wired.setSelected(true);
				this.m_tv_wired.setTextSize(getResources().getDimension(R.dimen.size_32));
			}else{
				this.m_tv_wired.setTextSize(getResources().getDimension(R.dimen.size_32));
			}
			
		}
		else if (2 == index)
		{
			this.m_tv_wired.setTextSize(getResources().getDimension(R.dimen.size_28));
			this.m_tv_detection.setTextSize(getResources().getDimension(R.dimen.size_28));
			this.m_tv_wired.setSelected(false);
			this.m_tv_detection.setSelected(false);
			if(isFocus){
				this.m_tv_wifi.setSelected(true);
				this.m_tv_wifi.setTextSize(getResources().getDimension(R.dimen.size_32));
			}else{
				this.m_tv_wifi.setTextSize(getResources().getDimension(R.dimen.size_32));
			}
		}
		else if (3 == index)
		{
			this.m_tv_wired.setTextSize(getResources().getDimension(R.dimen.size_28));
			this.m_tv_wifi.setTextSize(getResources().getDimension(R.dimen.size_28));
			this.m_tv_wired.setSelected(false);
			this.m_tv_wifi.setSelected(false);
			if(isFocus){
				this.m_tv_detection.setSelected(true);
				this.m_tv_detection.setTextSize(getResources().getDimension(R.dimen.size_32));
			}else{
				this.m_tv_detection.setTextSize(getResources().getDimension(R.dimen.size_32));
			}
		}
	}
	
	/**
	 * 数字键
	 * @Title: doKeyNum
	 * @Description: TODO
	 * @param event
	 * @return: void
	 */
	public void doKeyNum(KeyEvent event)
	{
		if (m_focus == FOCUS.WIRED_ACTIVITY)
		{
			this.m_wiredActivity.doKeyNum(event);
		}
		else if (m_focus == FOCUS.WIFI_ACTIVITY)
		{
			this.m_wifiActivity.doKeyNum(event);
		}
	}
	
	/**
	 * 删除键处理
	 * @Title: doKeyDel
	 * @Description: TODO
	 * @return: void
	 */
	public void doKeyDel()
	{
		if (m_focus == FOCUS.WIRED_ACTIVITY)
		{
			this.m_wiredActivity.doKeyDel();
		}
		else if (m_focus == FOCUS.WIFI_ACTIVITY)
		{
			this.m_wifiActivity.doKeyDel();
		}
	}
	
	/**
	 * OK键处理
	 * @Title: doKeyOk
	 * @Description: TODO
	 * @return: void
	 */
	public void doKeyOk()
	{
		if (m_focus == FOCUS.WIRED_ACTIVITY)
		{
			this.m_wiredActivity.doKeyOk();
		}
		else if (m_focus == FOCUS.WIFI_ACTIVITY)
		{
			if (m_wifiActivity.doKeyOk())
			{
				setTitleShow(false);
			}
			else
			{
				setTitleShow(true);
			}
		}
		else if (m_focus == FOCUS.DETECTION_ACTIVITY)
		{
			this.m_detectionActivity.doKeyOk();
		}
	}
	
	/**
	 * 设置菜单栏
	 * @Title: setTitleShow
	 * @Description: TODO
	 * @param isShow
	 * @return: void
	 */
	public void setTitleShow(boolean isShow)
	{
		if (isShow)
		{
			m_tv_radiogroup.setVisibility(View.VISIBLE);
		}else{
			m_tv_radiogroup.setVisibility(View.GONE);
		}
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void onResume() {
        //mContext.registerReceiver(mReceiver, mIntentFilter);
		super.onResume();
		m_wifiActivity.resume();
    }

	@Override
    public void onPause() {
        //mContext.unregisterReceiver(mReceiver);
		super.onPause();
		m_wifiActivity.pause();
    }
}
