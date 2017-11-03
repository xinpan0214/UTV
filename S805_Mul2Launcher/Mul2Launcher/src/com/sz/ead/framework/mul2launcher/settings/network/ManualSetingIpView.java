/**
 * 
 * Copyright © 2014GreatVision. All rights reserved.
 *
 * @Title: ManualSetingIpView.java
 * @Prject: BananaTvSetting
 * @Description: TODO
 * @author: luojw 
 * @date: 2014-1-21 上午11:52:57
 */
package com.sz.ead.framework.mul2launcher.settings.network;

import android.content.Context;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.sz.ead.framework.mul2launcher.R;

public class ManualSetingIpView extends RelativeLayout {
	
	private Button m_bt_ok;	// 手动配置，OK按钮
	
	public ManualSetingIpView(Context context) {
		super(context);
		inflate(context, R.layout.settings_network_view_manual_setting_ip, this);
		// TODO Auto-generated constructor stub
		
		this.m_bt_ok = (Button)findViewById(R.id.id_setting_network_manual_ok);
		
	}
	
	/**
	 * 按键处理
	 */
	@Override
	public boolean dispatchKeyEvent(KeyEvent event)
	{
		if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_DOWN && event.getAction() == KeyEvent.ACTION_DOWN)
		{
			doKeyDown();
		}
		else if (event.getKeyCode() == KeyEvent.KEYCODE_BACK)
		{
			super.dispatchKeyEvent(event);
		}
		
		//super.dispatchKeyEvent(event);
		
		return true;
	}
	
	/**
	 * 手动设置按钮
	 * @Title: doKeyDown
	 * @Description: TODO
	 * @return: void
	 */
	public void doKeyDown()
	{
		m_bt_ok.requestFocus();
	}
}
