package com.sz.ead.framework.mul2launcher.settings.network;

/**
 * 
 * Copyright © 2014GreatVision. All rights reserved.
 *
 * @Title: AutoFetchIpView.java
 * @Prject: BananaTvSetting
 * @Description: TODO
 * @author: luojw 
 * @date: 2014-1-21 上午11:48:12
 */

import android.content.Context;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.sz.ead.framework.mul2launcher.R;

public class AutoFetchIpView extends RelativeLayout {
	
	public Button m_bt_fetch;		// 自动获取按钮
	
	public AutoFetchIpView(Context context) {
		super(context);
		inflate(context, R.layout.settings_network_view_auto_fetch_ip, this);
		// TODO Auto-generated constructor stub
		
		this.m_bt_fetch = (Button)findViewById(R.id.id_setting_network_auto_fetch);
		
	}

	/**
	 * 自动获取按钮
	 * @Title: doKeyDown
	 * @Description: TODO
	 * @return: void
	 */
	public void doKeyDown()
	{
		this.m_bt_fetch.requestFocus();
		
	}
}
