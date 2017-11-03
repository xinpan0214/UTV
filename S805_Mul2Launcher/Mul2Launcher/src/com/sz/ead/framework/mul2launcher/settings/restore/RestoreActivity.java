package com.sz.ead.framework.mul2launcher.settings.restore;

/**
 * Copyright © 2014GreatVision. All rights reserved.
 *
 * @Title: RestoreActivity.java
 * @Prject: BananaTvSetting
 * @Description: TODO
 * @date: 2014-9-28 下午5:47:21
 */

import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.setting.settingservice.SettingManager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sz.ead.framework.mul2launcher.BaseActivity;
import com.sz.ead.framework.mul2launcher.MainActivity;
import com.sz.ead.framework.mul2launcher.R;
import com.sz.ead.framework.mul2launcher.status.Statusbar;
import com.szgvtv.ead.framework.bi.Bi;

/**
 * Copyright © 2014GreatVision. All rights reserved.
 *
 * @Title: RestoreActivity.java
 * @Prject: BananaTvSetting
 * @Description: TODO
 * @author: lijungang 
 * @date: 2014-9-28 下午5:47:21
 */
public class RestoreActivity extends BaseActivity {
	
	private Context mContext;
	private Button setting_about_reset_button;
	private ProgressBar setting_about_progress;
	private TextView setting_about_progress_num;
	private TextView setting_about_reset_info;
	private int progress_num = 0;
	private Timer mProgressTimer = null;
	private Statusbar mStatus_Bar;
	SettingManager settingManager;
	
	enum FOCUS {
		RESETING	
		};
	public static FOCUS mFocus;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings_about_layout_reset);
		
		mContext = this;
		
		setting_about_reset_info = (TextView) findViewById(R.id.setting_about_reset_info);
		setting_about_reset_button = (Button) findViewById(R.id.setting_about_reset_button);
		setting_about_progress = (ProgressBar)findViewById(R.id.setting_about_progress);
		setting_about_progress_num = (TextView)findViewById(R.id.setting_about_progress_num);
		mStatus_Bar = (Statusbar) findViewById(R.id.llyt_main_header);
		mStatus_Bar.setTaskBarTitle(this.getResources().getString(R.string.settings_about_reset));
		showTextInfo();
		settingManager = (SettingManager)mContext.getSystemService(Context.SETTING_SERVICE);
	}
	/**
	 * 
	 * @Title: getFocus
	 * @Description: button获取焦点
	 * @return: void
	 */
	public void getFocus() {
		setting_about_reset_button.requestFocus();
	}
	/**
	 * 
	 * @Title: mHandler
	 * @Description: 主线程处理button相应
	 * @return: void
	 */
	public Handler mHandler = new Handler() {
		@Override
		public void handleMessage (Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
				case 0:
					break;
				case 1:
					Bi.sendBiMsg(Bi.BICT_FIRMWARE, Bi.BICC_RESTORE_FAC_SETTINGS);
					mFocus = FOCUS.RESETING;
					showProgress();
					if(mProgressTimer == null){
						mProgressTimer = new Timer(); 
						mProgressTimer.schedule(new ProgressTask(), 30, 30); 
					}
					break;
				case 2:
					setting_about_progress.setProgress(progress_num);
					setting_about_progress_num.setText(Integer.toString(progress_num) + "%");
					if(progress_num == 99){
						settingManager.startRestoreFacSetting();
					}
					break;
				default:
				 break;
			 }
		}
	};
	/**
	 * 
	 * @Title: dispatchKeyEvent
	 * @Description: 按键操作
	 * @return: boolean
	 */
	public boolean dispatchKeyEvent(KeyEvent event) {
		
		if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_CENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
			doKeyOk();
		} else if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			if(mFocus == FOCUS.RESETING){
				return true;
			}
			super.dispatchKeyEvent(event);
		}
		
		return true;
	}
	/**
	 * 
	 * @Title: doKeyOk
	 * @Description: 提示框
	 * @return: void
	 */
	public void doKeyOk(){
		RestoreDialog rd = new RestoreDialog(mContext, mHandler, R.style.settings_about_dialog);
		rd.show();
	}
	/**
	 * 
	 * @Title: showTextInfo
	 * @Description: 显示提示信息
	 * @return: void
	 */
	private void showTextInfo(){
		setting_about_progress.setVisibility(View.GONE);
		setting_about_progress_num.setVisibility(View.GONE);
		setting_about_reset_button.setVisibility(View.VISIBLE);
		setting_about_reset_info.setVisibility(View.VISIBLE);
	}
	/**
	 * 
	 * @Title: showProgress
	 * @Description: 显示进度条
	 * @return: void
	 */
	private void showProgress(){
		progress_num = 0;
		setting_about_reset_button.setVisibility(View.GONE);
		setting_about_reset_info.setVisibility(View.GONE);
		setting_about_progress.setVisibility(View.VISIBLE);
		setting_about_progress_num.setVisibility(View.VISIBLE);
		setting_about_progress.setProgress(progress_num);
		setting_about_progress_num.setText(Integer.toString(progress_num) + "%");
	}
	
	/**
	 * 
	 * @Title: onBackPressed
	 * @Description: 返回键处理函数
	 * @return: void
	 */
	public void onBackPressed(){
		finish();
	}
	
    class ProgressTask extends TimerTask{
		/* (non-Javadoc)
		 * @see java.util.TimerTask#run()
		 */
		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {
				progress_num = progress_num + 1;
				mHandler.sendEmptyMessage(2);
				if(progress_num == 99){
			    	if(mProgressTimer != null){
			    		mProgressTimer.cancel(); 
			    		mProgressTimer = null;
			    	}
				}
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
    }
}
