/**
 * Copyright © 2015GreatVision. All rights reserved.
 *
 * @Title: BootCompletedReceive.java
 * @Prject: Mul2Launcher
 * @Package: com.sz.ead.framework.mul2launcher.util
 * @Description: TODO
 * @date: 2015-5-7 下午7:01:26
 */
package com.sz.ead.framework.mul2launcher.util;

import com.sz.ead.framework.mul2launcher.appstore.UpdateAppListService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.login.loginservice.LoginManager;
import android.util.Log;

/**
 * Copyright © 2015GreatVision. All rights reserved.
 *
 * @Title: BootCompletedReceive.java
 * @Prject: Mul2Launcher
 * @Package: com.sz.ead.framework.mul2launcher.util
 * @Description: TODO
 * @author: lijungang 
 * @date: 2015-5-7 下午7:01:26
 */
public class BootCompletedReceive extends BroadcastReceiver {

	public static final String TAG = "mul2launcher";
	/* (non-Javadoc)
	 * @see android.content.BroadcastReceiver#onReceive(android.content.Context, android.content.Intent)
	 */
	@Override
	public void onReceive(Context arg0, Intent arg1) {
		// TODO Auto-generated method stub
		// 获取商城URL
		Log.d(TAG, "receive boot msg");
		LoginManager mLogin = (LoginManager) arg0.getSystemService(Context.LOGIN_SERVICE);
		
		if (!mLogin.getASUrl().equals(""))
		{
			ConstantUtil.REQUEST_URL_HOST = "http://" + mLogin.getASUrl();
			Log.d(TAG, "asUrl:"+ConstantUtil.REQUEST_URL_HOST);
			
			if (!ConstantUtil.UPDATE_SERVICE_IS_START)
			{
				ConstantUtil.UPDATE_SERVICE_IS_START = true;
				
				Log.d(TAG, "start update app service");
				
				Intent intent = new Intent();   
		        intent.setClass(arg0, UpdateAppListService.class);
		        arg0.startService(intent);
			}
		}
	}
}
