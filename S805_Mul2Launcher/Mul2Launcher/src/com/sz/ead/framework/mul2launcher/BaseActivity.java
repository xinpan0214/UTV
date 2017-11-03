/**
 * Copyright © 2014GreatVision. All rights reserved.
 *
 * @Title: BaseActivity.java
 * @Prject: BananaTvSetting
 * @Description: TODO
 * @date: 2014-2-28 上午11:13:47
 */
package com.sz.ead.framework.mul2launcher;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;

import com.sz.ead.framework.mul2launcher.application.UILApplication;

/**
 * Copyright © 2014GreatVision. All rights reserved.
 *
 * @Title: BaseActivity.java
 * @Prject: BananaTvSetting
 * @Description: TODO
 * @author: lijungang 
 * @date: 2014-2-28 上午11:13:47
 */
public class BaseActivity extends Activity {
	
	protected HomeKeyEventBroadCastReceiver mHomeKeyEventBroadCastReceiver;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return false;
	}
	
	@Override
	protected void onResume() {
		if(UILApplication.isSwitchToHome){
			switchToHome();
			UILApplication.isSwitchToHome = false;
		}
		mHomeKeyEventBroadCastReceiver = new HomeKeyEventBroadCastReceiver();  
		registerReceiver(mHomeKeyEventBroadCastReceiver, new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)); 
		super.onResume();
	}
	
	@Override
	protected void onPause() {
		UILApplication.isSwitchToHome = false;
		unregisterReceiver(mHomeKeyEventBroadCastReceiver);
		super.onPause();
	}
	
	protected void switchToHome(){
		Intent intent = new Intent();   
		intent.setClass(this, MainActivity.class);  
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);  //注意本行的FLAG设置  
		startActivity(intent);
		finish();//关掉自己  
	}
	
	protected class HomeKeyEventBroadCastReceiver extends BroadcastReceiver {

		static final String SYSTEM_REASON = "reason";
		static final String SYSTEM_HOME_KEY = "homekey";//home key
		static final String SYSTEM_RECENT_APPS = "recentapps";//long home key
		
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
				String reason = intent.getStringExtra(SYSTEM_REASON);
				if (reason != null) {
					if (reason.equals(SYSTEM_HOME_KEY)) {
						switchToHome();
					} else if (reason.equals(SYSTEM_RECENT_APPS)) {
						// long home key处理点
					}
				}
			}
		}
	}
	
	@Override
	protected void onDestroy(){
		super.onDestroy();
	}
}
