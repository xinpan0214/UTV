/**
 * Copyright © 2015GreatVision. All rights reserved.
 *
 * @Title: FragmentBase.java
 * @Prject: Mul2Launcher
 * @Package: com.sz.ead.framework.mul2launcher
 * @Description: TODO
 * @author: lijungang 
 * @date: 2015-4-22 上午10:50:48
 */
package com.sz.ead.framework.mul2launcher;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;

import com.sz.ead.framework.mul2launcher.application.UILApplication;

@SuppressLint("NewApi")
public abstract class FragmentBase extends Fragment{
	
//	protected HomeKeyEventBroadCastReceiver mHomeKeyEventBroadCastReceiver;
	//按键操作基类,fragment 按键基类可自定义
	public abstract int dispatchKeyEvent(KeyEvent event);
	//从导航向下按键切换到内容时初始化内容焦点
	public abstract void initFragmentFocus();
	//重置fragment数据接口
	public abstract void resetFragment();
	
//	private FragmentHomeKeySwitch mFragmentHomeKeySwitch ;
//
//	public void setFragmentHomeKeySwitch(FragmentHomeKeySwitch fragmentHomeKeySwitch) {
//		this.mFragmentHomeKeySwitch = fragmentHomeKeySwitch;
//	}
//	
//	@Override
//	public void onResume() {
//		// TODO Auto-generated method stub
//		if(UILApplication.isSwitchToHome){
//			fragmentSwitchToFirst();
//			UILApplication.isSwitchToHome = false;
//		}
//		mHomeKeyEventBroadCastReceiver = new HomeKeyEventBroadCastReceiver();  
//		getActivity().registerReceiver(mHomeKeyEventBroadCastReceiver, new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)); 
//		super.onResume();
//	}
//	
//	private void fragmentSwitchToFirst(){
//		mFragmentHomeKeySwitch.switchToFirst();
//	}
//	
//	@Override
//	public void onPause() {
//		// TODO Auto-generated method stub
//		UILApplication.isSwitchToHome = false;
//		getActivity().unregisterReceiver(mHomeKeyEventBroadCastReceiver);
//		super.onPause();
//	}
//	
//	protected class HomeKeyEventBroadCastReceiver extends BroadcastReceiver {
//
//		static final String SYSTEM_REASON = "reason";
//		static final String SYSTEM_HOME_KEY = "homekey";//home key
//		static final String SYSTEM_RECENT_APPS = "recentapps";//long home key
//		
//		@Override
//		public void onReceive(Context context, Intent intent) {
//			String action = intent.getAction();
//			if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
//				String reason = intent.getStringExtra(SYSTEM_REASON);
//				if (reason != null) {
//					if (reason.equals(SYSTEM_HOME_KEY)) {
//						fragmentSwitchToFirst();
//					} else if (reason.equals(SYSTEM_RECENT_APPS)) {
//						// long home key处理点
//					}
//				}
//			}
//		}
//	}
}
