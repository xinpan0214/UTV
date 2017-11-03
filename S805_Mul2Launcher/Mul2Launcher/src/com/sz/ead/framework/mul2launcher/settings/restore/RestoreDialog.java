package com.sz.ead.framework.mul2launcher.settings.restore;

/**
 * 
 * Copyright © 2014GreatVision. All rights reserved.
 *
 * @Title: RestoreDialog.java
 * @Prject: BananaTvSetting
 * @Description: 恢复出厂设置对话框
 * @author: lijungang 
 * @date: 2014-1-24 下午1:42:45
 */

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.sz.ead.framework.mul2launcher.R;

public class RestoreDialog extends Dialog implements OnClickListener{
	private Button btn_ok;
	private Button btn_cancel;
	private Handler mHandler;
	
	public RestoreDialog(Context context) {
		// TODO Auto-generated constructor stub
		super(context);
	}

	public RestoreDialog(Context context, Handler handler, int theme) {
		// TODO Auto-generated constructor stub
		super(context, theme);
		this.mHandler = handler;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.settings_about_restore_dialog);
		
		btn_ok = (Button) findViewById(R.id.id_setting_about_restoredlg_ok);
		btn_cancel = (Button) findViewById(R.id.id_setting_about_restoredlg_cancel);
		btn_cancel.requestFocus();
		btn_ok.setOnClickListener(this);
		btn_cancel.setOnClickListener(this);
	}
	/**
	 * 
	 * @Title: onClick
	 * @Description: button处理确定取消按键
	 * @return: void
	 */
	public void onClick(View v) {
		if (v == btn_ok) {
			Message msg = new Message();
			msg.what = 1;
			mHandler.sendMessage(msg);
			this.dismiss();
		} else if (v == btn_cancel) {
			Message msg = new Message();
			msg.what = 0;
			mHandler.sendMessage(msg);
			this.dismiss();
		}
	}
}
