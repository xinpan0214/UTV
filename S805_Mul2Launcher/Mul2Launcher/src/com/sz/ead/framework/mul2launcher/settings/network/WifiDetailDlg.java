/**
 * 
 * Copyright © 2014GreatVision. All rights reserved.
 *
 * @Title: WifiDetailDlg.java
 * @Prject: BananaTvSetting
 * @Description: TODO
 * @author: luojw 
 * @date: 2014-1-21 上午11:58:13
 */
package com.sz.ead.framework.mul2launcher.settings.network;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.sz.ead.framework.mul2launcher.R;

public class WifiDetailDlg extends Dialog implements OnClickListener {
	
	Context m_context;
	
	TextView m_wifiName;
	TextView m_signal;
	TextView m_security;
	TextView m_macAddr;
	
	Button m_foget;
	Button m_cancel;
	
	Button m_connect;
	Button m_connect_cancel;
	Button m_connect_foget;
	public static final String TAG = "network";
	
	int m_type;
	Handler m_handler;
	boolean m_isConnect;
	
	//焦点选中
	enum FOCUS
	{
		FOGET,
		CANCEL,
		CONNECT,
		CONNECT_CANCEL,
		CONNECT_FOGET
	};
	
	FOCUS m_focus;
	
	public WifiDetailDlg(Context context, Handler handler, boolean isConnect) {
		super(context,R.style.settings_network_dialog_transparency_bg);
		m_context = context;	
		m_handler = handler;
		m_isConnect = isConnect;
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setContentView(R.layout.settings_network_wifi_detail_dlg);
		this.init();
	}
	
	public void init()
	{
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
		m_wifiName = (TextView)findViewById(R.id.id_setting_network_wifi_detail_wifiname);
		m_signal = (TextView)findViewById(R.id.id_setting_network_wifi_detail_signal_context);
		m_security = (TextView)findViewById(R.id.id_setting_network_wifi_detail_security_context);
		m_macAddr = (TextView)findViewById(R.id.id_setting_network_wifi_detail_macaddr_context);
		
		m_foget = (Button)findViewById(R.id.id_setting_network_wifi_forget);
		m_cancel = (Button)findViewById(R.id.id_setting_network_wifi_detail_cancel);
		
		m_connect = (Button)findViewById(R.id.id_setting_network_wifi_connect_connect);
		m_connect_foget = (Button)findViewById(R.id.id_setting_network_wifi_detail_connect_forget);
		m_connect_cancel = (Button)findViewById(R.id.id_setting_network_connect_cancel);
		
		m_foget.setOnClickListener(this);
		m_foget.setTag(1);
		
		m_cancel.setOnClickListener(this);
		m_cancel.setTag(2);
		
		m_connect.setOnClickListener(this);
		m_connect_foget.setOnClickListener(this);
		m_connect_cancel.setOnClickListener(this);
		m_connect.setTag(3);
		m_connect_foget.setTag(4);
		m_connect_cancel.setTag(5);
		
		if (m_isConnect)
		{
			m_connect.setVisibility(View.GONE);
			m_connect_foget.setVisibility(View.GONE);
			m_connect_cancel.setVisibility(View.GONE);
			
			m_foget.setVisibility(View.VISIBLE);
			m_cancel.setVisibility(View.VISIBLE);

			m_cancel.requestFocus();
		}
		else
		{
			
			m_connect.setVisibility(View.VISIBLE);
			m_connect_foget.setVisibility(View.VISIBLE);
			m_connect_cancel.setVisibility(View.VISIBLE);
			
			m_foget.setVisibility(View.GONE); 
			m_cancel.setVisibility(View.GONE);

			m_connect_cancel.requestFocus(); 
		}
	}
	
	// 1代表只有忘记和取消按钮，2代表有忘记、不保存和取消按钮
	/**
	 * 设置wifi名称
	 * @Title: setWifiName
	 * @Description: TODO
	 * @param name
	 * @return: void
	 */
	public void setWifiName(String name)
	{
		m_wifiName.setText(name);
	}
	
	/**
	 * 设置wifi信号等级
	 * @Title: setSignal
	 * @Description: TODO
	 * @param level
	 * @return: void
	 */
	public void setSignal(int level)
	{
		if (0 == level)
        {
			m_signal.setText(R.string.settings_network_weak);
        }
        else if (1 == level)
        {
        	m_signal.setText(R.string.settings_network_ordinary);
        }
        else if (2 == level)
        {
        	m_signal.setText(R.string.settings_network_strong_less);
        }
        else if (3 == level)
        {
        	m_signal.setText(R.string.settings_network_strong);
        }
	}
	
	/**
	 * 设置wifi安全性类型
	 * @Title: setSecurity
	 * @Description: TODO
	 * @param security
	 * @return: void
	 */
	public void setSecurity(int security)
	{
		if (0 == security)
        {
			m_security.setText(R.string.settings_network_none);
        }
        else if (1 == security)
        {
        	m_security.setText(R.string.settings_network_wep);
        }
        else if (2 == security)
        {
        	m_security.setText(R.string.settings_network_psk);
        }
	}
	
	/**
	 * 设置mac地址
	 * @Title: setMacAddr
	 * @Description: TODO
	 * @param macAddr
	 * @return: void
	 */
	public void setMacAddr(String macAddr)
	{
		Log.d(TAG, "mac:"+macAddr);
		m_macAddr.setVisibility(View.VISIBLE);
		m_macAddr.setText(macAddr);
	}

	
	@Override
	public void onClick(View view)
	{
		this.doKeyOk(view);
	}
	
	/**
	 * 按OK键处理
	 * @Title: doKeyOk
	 * @Description: TODO
	 * @param view
	 * @return: void
	 */
	public void doKeyOk(View view)
	{
		int tag = (Integer) view.getTag();
		Message msg = new Message();  
		switch (tag) 
		{
		case 1:
            msg.what = 1;  
	        m_handler.sendMessage(msg);
	        this.dismiss();
			break;
		case 2:
			this.dismiss();
			msg.what = 3;  
	        m_handler.sendMessage(msg);
			
			break;
		case 3:
            msg.what = 2;  
	        m_handler.sendMessage(msg);
	        this.dismiss();
			break;
		case 4:
            msg.what = 1;  
	        m_handler.sendMessage(msg);
	        this.dismiss();
			break;
		case 5:
			msg.what = 3;  
	        m_handler.sendMessage(msg);
			this.dismiss();
			break;
		default:
			break;
			}
	}
	
	@Override
	public void onBackPressed() {
		Message msg = new Message();  
		msg.what = 3;  
        m_handler.sendMessage(msg);
		this.dismiss();
	}
}
