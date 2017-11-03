package com.sz.ead.framework.mul2launcher.settings.network;

/**
 * Copyright © 2015GreatVision. All rights reserved.
 *
 * @Title: IPInputDialog.java
 * @Prject: BananaTvSetting
 * @Description: TODO
 * @date: 2015-7-15 下午2:55:49
 */


import com.sz.ead.framework.mul2launcher.R;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;

/**
 * Copyright © 2015GreatVision. All rights reserved.
 *
 * @Title: IPInputDialog.java
 * @Prject: BananaTvSetting
 * @Description: TODO
 * @author: lijungang 
 * @date: 2015-7-15 下午2:55:49
 */
public class IPInputDialog extends Dialog implements OnClickListener {
	
	Button mBt0;		// 数字键
	Button mBt1;
	Button mBt2;
	Button mBt3;
	Button mBt4;
	Button mBt5;
	Button mBt6;
	Button mBt7;
	Button mBt8;
	Button mBt9;
	Button mBtBack;		// 返回键
	Button mBtDel;		// 删除键
	
	String TAG = "network";
	
	Handler mHandler;
	Context mContext;
	
	public IPInputDialog(Context context, Handler handler) {
		super(context,R.style.settings_network_dialog_transparency_bg);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		//getWindow().setBackgroundDrawableResource(R.drawable.setting_dialog_bg);
		
		mHandler = handler;
		mContext = context;
		
		this.getWindow().setContentView(R.layout.setting_network_ip_input_dialog);
		initView();
	}
	
	private void initView()
	{
		mBt0 = (Button)this.findViewById(R.id.setting_network_ip_0);		
		mBt1 = (Button)this.findViewById(R.id.setting_network_ip_1);
		mBt1.requestFocus();
		mBt2 = (Button)this.findViewById(R.id.setting_network_ip_2);
		mBt3 = (Button)this.findViewById(R.id.setting_network_ip_3);
		mBt4 = (Button)this.findViewById(R.id.setting_network_ip_4);
		mBt5 = (Button)this.findViewById(R.id.setting_network_ip_5);
		mBt6 = (Button)this.findViewById(R.id.setting_network_ip_6);
		mBt7 = (Button)this.findViewById(R.id.setting_network_ip_7);
		mBt8 = (Button)this.findViewById(R.id.setting_network_ip_8);
		mBt9 = (Button)this.findViewById(R.id.setting_network_ip_9);
		mBtBack = (Button)this.findViewById(R.id.setting_network_ip_back);
		mBtDel = (Button)this.findViewById(R.id.setting_network_ip_del);
		
		setOnClickListener();
	}
	
	private void setOnClickListener()
	{
		mBt0.setOnClickListener(this);
		mBt1.setOnClickListener(this);
		mBt2.setOnClickListener(this);
		mBt3.setOnClickListener(this);
		mBt4.setOnClickListener(this);
		mBt5.setOnClickListener(this);
		mBt6.setOnClickListener(this);
		mBt7.setOnClickListener(this);
		mBt8.setOnClickListener(this);
		mBt9.setOnClickListener(this);
		mBtBack.setOnClickListener(this);
		mBtDel.setOnClickListener(this);
	}

	/* (non-Javadoc)
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		
		int num = -1;
		
		if (arg0 == mBt0)
		{
			num = 0;
		}
		else if (arg0 == mBt1)
		{
			num = 1;
		}
		else if (arg0 == mBt2)
		{
			num = 2;
		}
		else if (arg0 == mBt3)
		{
			num = 3;
		}
		else if (arg0 == mBt4)
		{
			num = 4;
		}
		else if (arg0 == mBt5)
		{
			num = 5;
		}
		else if (arg0 == mBt6)
		{
			num = 6;
		}
		else if (arg0 == mBt7)
		{
			num = 7;
		}
		else if (arg0 == mBt8)
		{
			num = 8;
		}
		else if (arg0 == mBt9)
		{
			num = 9;
		}
		else if (arg0 == mBtBack)
		{
			this.dismiss();
			return;
		}
		else if (arg0 == mBtDel)	// 删除
		{
			num = 11;
		}
		
		Message msg = new Message();
		msg.what = 1;
		msg.arg1 = num;
		
		mHandler.sendMessage(msg);
	}
	
	@Override
	public boolean dispatchKeyEvent(KeyEvent event)
	{
		if (event.getKeyCode() == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_DOWN)
		{
			// 发送删除键消息
			Message msg = new Message();
			msg.what = 1;
			msg.arg1 = 11;
			
			mHandler.sendMessage(msg);
			
			return true;
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
			return true;
		}
		return super.dispatchKeyEvent(event);
	}
	
	public void doKeyNum(KeyEvent event)
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
		
		Message msg = new Message();
		msg.what = 1;
		msg.arg1 = key;
		
		mHandler.sendMessage(msg);
	}
}
