/**
 * Copyright © 2014GreatVision. All rights reserved.
 *
 * @Title: WiFiPasswordAcitivity.java
 * @Prject: BananaTvSetting
 * @Description: TODO
 * @author: luojw  
 * @date: 2014-8-1 上午10:55:13
 */
package com.sz.ead.framework.mul2launcher.settings.network;

import android.content.Context;
import android.notice.noticemanager.Notice;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sz.ead.framework.mul2launcher.R;
import com.sz.ead.framework.mul2launcher.settings.util.CommonUtils;

public class WiFiPasswordAcitivity extends RelativeLayout {
	
	public static final String TAG = "network";
	private Context mContext;
	EditText mEditPwd;
	Button mConnect;
	TextView mSsid;
	ImageView mSignal;
	FOCUS mFocus;
	boolean mCurshow;
	Handler mCurhandler;
	Handler mhandler;
	
	String mIpAddr;
	String mGateWay;
	String mDns;
	String mNetworkPrfix;
	
	enum FOCUS
	{
		NOT_FOCUS,
		FOCUS_PWD,
		FOCUS_CONNECT
	};
	
	public WiFiPasswordAcitivity(Context context) 
	{
		super(context);
		inflate(context, R.layout.settings_network_wifipwd_view, this);
		// TODO Auto-generated constructor stub
		
		mContext = context;
		findView();
	}
	
	/**
	 * 控件初始化
	 * @Title: findView
	 * @Description: TODO
	 * @return: void
	 */
	public void findView()
	{
		mEditPwd = (EditText)this.findViewById(R.id.id_setting_network_wifi_edit);
		mConnect = (Button)this.findViewById(R.id.id_setting_network_wifi_pwd_connect);
		mSsid = (TextView)this.findViewById(R.id.id_setting_network_wifi_pwd_name);
		mSignal = (ImageView)this.findViewById(R.id.id_setting_network_wifi_pwd_signal);
	}
	
	/**
	 * 自动获取页面
	 * @Title: ShowDhcp
	 * @Description: TODO
	 * @param ssid
	 * @param level
	 * @param handler
	 * @param curHandler
	 * @return: void
	 */
	public void ShowDhcp(String ssid, int level, Handler handler,Handler curHandler)
	{
		this.setVisibility(View.VISIBLE);
		mSsid.setText(ssid);
		mEditPwd.setText("");
		mEditPwd.requestFocus();
		mCurhandler = curHandler;
		mhandler = handler;
		mFocus = FOCUS.FOCUS_PWD;
		mCurshow = true;
		
		if (0 == level)
        {
			mSignal.setImageResource(R.drawable.settings_network_wifi_wifi0_uf);
        }
        else if (1 == level)
        {
        	mSignal.setImageResource(R.drawable.settings_network_wifi_wifi1_uf);
        }
        else if (2 == level)
        {
        	mSignal.setImageResource(R.drawable.settings_network_wifi_wifi2_uf);
        }
		
		mEditPwd.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
		InputMethodManager inputMethodManager = (InputMethodManager) mContext
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		inputMethodManager.toggleSoftInput(0,
				InputMethodManager.HIDE_NOT_ALWAYS);
	}
	
	/**
	 * 手动配置页面
	 * @Title: ShowManual
	 * @Description: TODO
	 * @param ssid
	 * @param level
	 * @param handler
	 * @param curHandler
	 * @param ipAddr
	 * @param gateWay
	 * @param dns
	 * @param networkPrfix
	 * @return: void
	 */
	public void ShowManual(String ssid, int level, Handler handler,Handler curHandler,
			String ipAddr,String gateWay,String dns,String networkPrfix)
	{
		this.setVisibility(View.VISIBLE);
		mSsid.setText(ssid);
		mEditPwd.setText("");
		mEditPwd.requestFocus();
		mCurhandler = curHandler;
		mhandler = handler;
		mFocus = FOCUS.FOCUS_PWD;
		
		mIpAddr = ipAddr;
		mGateWay = gateWay;
		mDns = dns;
		mNetworkPrfix = networkPrfix;
		
		mCurshow = false;
		
		if (0 == level)
        {
			mSignal.setImageResource(R.drawable.settings_network_wifi_wifi0_uf);
        }
        else if (1 == level)
        {
        	mSignal.setImageResource(R.drawable.settings_network_wifi_wifi1_uf);
        }
        else if (2 == level)
        {
        	mSignal.setImageResource(R.drawable.settings_network_wifi_wifi2_uf);
        }
		
		mEditPwd.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
		InputMethodManager inputMethodManager = (InputMethodManager) mContext
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		inputMethodManager.toggleSoftInput(0,
				InputMethodManager.HIDE_NOT_ALWAYS);
	}
	
	/**
	 * 方向上键处理
	 * @Title: doKeyUp
	 * @Description: TODO
	 * @return
	 * @return: boolean
	 */
	public boolean doKeyUp()
	{
		if (FOCUS.FOCUS_CONNECT == mFocus)
		{
			mEditPwd.requestFocus();
			mFocus = FOCUS.FOCUS_PWD;
		}
		else if (FOCUS.FOCUS_PWD == mFocus)
		{
			mFocus = FOCUS.NOT_FOCUS;
			return true;
		}
		return false;
	}
	
	/**
	 * 方向下键处理
	 * @Title: doKeyDown
	 * @Description: TODO
	 * @return: void
	 */
	public void doKeyDown()
	{
		if (FOCUS.FOCUS_PWD == mFocus)
		{
			mConnect.requestFocus();
			mFocus = FOCUS.FOCUS_CONNECT;
		}
		else if (FOCUS.NOT_FOCUS == mFocus)
		{
			mEditPwd.requestFocus();
			mFocus = FOCUS.FOCUS_PWD;
		}
	}
	
	/**
	 * OK键处理
	 * @Title: doKeyOk
	 * @Description: TODO
	 * @return
	 * @return: boolean
	 */
	public boolean doKeyOk()
	{
		if (FOCUS.FOCUS_PWD == mFocus)
		{
			mEditPwd.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
			InputMethodManager inputMethodManager = (InputMethodManager) mContext
					.getSystemService(Context.INPUT_METHOD_SERVICE);
			inputMethodManager.toggleSoftInput(0,
					InputMethodManager.HIDE_NOT_ALWAYS);
		}
		else if (FOCUS.FOCUS_CONNECT == mFocus)
		{
			if (CommonUtils.getSetting().getEthMagHWConnected())
			{
				Notice notice = Notice.makeNotice(mContext, 
						mContext.getResources().getString(R.string.settings_network_connect_wired), 
						Notice.LENGTH_SHORT);
    			notice.cancelAll();
    			notice.showImmediately();
    			return false;
			}
			
			String password = this.mEditPwd.getText().toString();
			
			Log.d(TAG, "password:"+password);
			
			if (password.equals(""))
			{
				Notice notice = Notice.makeNotice(mContext, 
						mContext.getResources().getString(R.string.settings_network_input_password), 
						Notice.LENGTH_SHORT);
    			notice.cancelAll();
    			notice.showImmediately();
				return false;
			}
			
			Message msg = new Message();
			Bundle bundle = new Bundle(); 
			
			if (this.mCurshow)
			{
				msg.what = 4;  
				msg.obj = mhandler;
		        bundle.putString("password",password);  //往Bundle中存放数据
			}
			else
			{
				msg.what = 5;  
				msg.obj = mhandler;
				bundle.putString("password",password);  //往Bundle中存放数据   
		        bundle.putString("ipAddr",mIpAddr);
		        bundle.putString("gateWay",mGateWay);
		        bundle.putString("dns",mDns);
		        bundle.putString("networkPrfix",mNetworkPrfix);
			}
			
	        msg.setData(bundle);//mes利用Bundle传递数据   
	        mCurhandler.sendMessage(msg);
	        
	        return true;
		}
		
		return false;
	}
	
	/**
	 * 数字键处理
	 * @Title: doKeyNum
	 * @Description: TODO
	 * @param event
	 * @return: void
	 */
	public void doKeyNum(KeyEvent event)
	{
		if (mFocus == FOCUS.FOCUS_PWD)
		{
			int key = -1;
			
			switch (event.getKeyCode())
			{
			case KeyEvent.KEYCODE_0:
				key = 0;
				break;
			case KeyEvent.KEYCODE_1:
				key = 1;
				break;
			case KeyEvent.KEYCODE_2:
				key = 2;
				break;
			case KeyEvent.KEYCODE_3:
				key = 3;
				break;
			case KeyEvent.KEYCODE_4:
				key = 4;
				break;
			case KeyEvent.KEYCODE_5:
				key = 5;
				break;
			case KeyEvent.KEYCODE_6:
				key = 6;
				break;
			case KeyEvent.KEYCODE_7:
				key = 7;
				break;
			case KeyEvent.KEYCODE_8:
				key = 8;
				break;
			case KeyEvent.KEYCODE_9:
				key = 9;
				break;
			default:
				break;
			}
			
			String newPassword = this.mEditPwd.getText().toString() + String.valueOf(key);
			this.mEditPwd.setText(newPassword);
			mEditPwd.setSelection(newPassword.length());
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
		if (mFocus == FOCUS.FOCUS_PWD)
		{
			String passwordText = mEditPwd.getText().toString();
		
			int len = passwordText.length();
			
			if (len == 1)
			{
				mEditPwd.setText("");
			}
			else if (len == 0)
			{
				return;
			}
			else
			{
				passwordText = passwordText.substring(0,passwordText.length()-1); 
				mEditPwd.setText(passwordText);
				mEditPwd.setSelection(passwordText.length());
			}
		}
	}
}
