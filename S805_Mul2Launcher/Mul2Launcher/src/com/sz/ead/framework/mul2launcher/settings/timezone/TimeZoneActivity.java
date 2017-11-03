/**
 * Copyright © 2014GreatVision. All rights reserved.
 *
 * @Title: TimeZoneActivity.java
 * @Prject: BananaTvSetting
 * @Description: TODO
 * @author: lijungang  
 * @date: 2014-7-22 下午2:12:40
 */
package com.sz.ead.framework.mul2launcher.settings.timezone;

import java.util.HashMap;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.setting.settingservice.SettingManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.sz.ead.framework.mul2launcher.BaseActivity;
import com.sz.ead.framework.mul2launcher.MainActivity;
import com.sz.ead.framework.mul2launcher.R;
import com.sz.ead.framework.mul2launcher.status.Statusbar;
import com.szgvtv.ead.framework.bi.Bi;

/**
 * Copyright © 2014GreatVision. All rights reserved.
 *
 * @Title: TimeZoneActivity.java
 * @Prject: BananaTvSetting
 * @Description: TODO
 * @author: lijungang 
 * @date: 2014-7-22 下午2:12:40
 */
public class TimeZoneActivity extends BaseActivity implements OnClickListener{

	private Statusbar mStatus_Bar;
	private Button setting_timezone_selected;
	private TextView setting_timezone_info;
	private String mTimeZone = "";
	private int mTimeZoneIndex;
	private SettingManager settingManager;
	private int total_timezone = 30;
	private boolean isBiSend;
	
    private static final HashMap<String, String> ID_TIMEZONE = new HashMap<String, String>() {{
        put("1", "-11:00"); put("2", "-10:00"); put("3", "-8:00");put("4", "-7:00"); 
        put("5", "-6:00"); put("6", "-5:00"); put("7", "-4:30"); put("8", "-4:00");
        put("9", "-3:00"); put("10", "-2:00"); put("11", "-1:00"); put("12", "+0:00");
        put("13", "+1:00"); put("14", "+2:00"); put("15", "+3:00"); put("16", "+4:00");
        put("17", "+4:30"); put("18", "+5:00"); put("19", "+5:30"); put("20", "+5:45"); put("21", "+6:00");
        put("22", "+6:30"); put("23", "+7:00"); put("24", "+8:00"); put("25", "+9:00"); put("26", "+9:30");
        put("27", "+10:00"); put("28", "+11:00"); put("29", "+12:00"); put("30", "+13:00"); 
   }};
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings_timezone_activity);
		settingManager = (SettingManager)getSystemService(Context.SETTING_SERVICE);
		setting_timezone_selected = (Button)findViewById(R.id.setting_timezone_selected);
		setting_timezone_info = (TextView)findViewById(R.id.setting_timezone_info);
		mStatus_Bar = (Statusbar) findViewById(R.id.llyt_main_header);
		mStatus_Bar.setTaskBarTitle(this.getResources().getString(R.string.settings_menu_timezone_title));
		
		mTimeZone = settingManager.getTimeZone();
		setting_timezone_selected.setText("UTC" + mTimeZone);
		mTimeZoneIndex = getHashMapKey(mTimeZone);
		setTimeZoneInfo(mTimeZone);
		setting_timezone_selected.setOnClickListener(this);
		isBiSend = false;
	}
	
	/**
	 * 
	 * @Title: getHashMapKey
	 * @Description: 通过时区获取key
	 * @return: void
	 */
	private int getHashMapKey(String timezone){
		for(String key : ID_TIMEZONE.keySet()){
		   if(ID_TIMEZONE.get(key).equals(timezone)){
			   return Integer.parseInt(key);
		   }
		}
		return 0;
	}

	/**
	 * 
	 * @Title: setTimeZoneInfo
	 * @Description: 通过时区设置显示信息
	 * @return: void
	 */
	private void setTimeZoneInfo(String timezone){
		if(timezone.equalsIgnoreCase("-11:00")){
			setting_timezone_info.setText(getResources().getString(R.string.settings_timezone_info_1));
		}else if(timezone.equalsIgnoreCase("-10:00")){
			setting_timezone_info.setText(getResources().getString(R.string.settings_timezone_info_2));
		}else if(timezone.equalsIgnoreCase("-8:00")){
			setting_timezone_info.setText(getResources().getString(R.string.settings_timezone_info_3));
		}else if(timezone.equalsIgnoreCase("-7:00")){
			setting_timezone_info.setText(getResources().getString(R.string.settings_timezone_info_4));
		}else if(timezone.equalsIgnoreCase("-6:00")){
			setting_timezone_info.setText(getResources().getString(R.string.settings_timezone_info_5));
		}else if(timezone.equalsIgnoreCase("-5:00")){
			setting_timezone_info.setText(getResources().getString(R.string.settings_timezone_info_6));
		}else if(timezone.equalsIgnoreCase("-4:30")){
			setting_timezone_info.setText(getResources().getString(R.string.settings_timezone_info_7));
		}else if(timezone.equalsIgnoreCase("-4:00")){
			setting_timezone_info.setText(getResources().getString(R.string.settings_timezone_info_8));
		}else if(timezone.equalsIgnoreCase("-3:00")){
			setting_timezone_info.setText(getResources().getString(R.string.settings_timezone_info_9));
		}else if(timezone.equalsIgnoreCase("-2:00")){
			setting_timezone_info.setText(getResources().getString(R.string.settings_timezone_info_10));
		}else if(timezone.equalsIgnoreCase("-1:00")){
			setting_timezone_info.setText(getResources().getString(R.string.settings_timezone_info_11));
		}else if(timezone.equalsIgnoreCase("+0:00")){
			setting_timezone_info.setText(getResources().getString(R.string.settings_timezone_info_12));
		}else if(timezone.equalsIgnoreCase("+1:00")){
			setting_timezone_info.setText(getResources().getString(R.string.settings_timezone_info_13));
		}else if(timezone.equalsIgnoreCase("+2:00")){
			setting_timezone_info.setText(getResources().getString(R.string.settings_timezone_info_14));
		}else if(timezone.equalsIgnoreCase("+3:00")){
			setting_timezone_info.setText(getResources().getString(R.string.settings_timezone_info_15));
		}else if(timezone.equalsIgnoreCase("+4:00")){
			setting_timezone_info.setText(getResources().getString(R.string.settings_timezone_info_16));
		}else if(timezone.equalsIgnoreCase("+4:30")){
			setting_timezone_info.setText(getResources().getString(R.string.settings_timezone_info_17));
		}else if(timezone.equalsIgnoreCase("+5:00")){
			setting_timezone_info.setText(getResources().getString(R.string.settings_timezone_info_18));
		}else if(timezone.equalsIgnoreCase("+5:30")){
			setting_timezone_info.setText(getResources().getString(R.string.settings_timezone_info_19));
		}else if(timezone.equalsIgnoreCase("+5:45")){
			setting_timezone_info.setText(getResources().getString(R.string.settings_timezone_info_20));
		}else if(timezone.equalsIgnoreCase("+6:00")){
			setting_timezone_info.setText(getResources().getString(R.string.settings_timezone_info_21));
		}else if(timezone.equalsIgnoreCase("+6:30")){
			setting_timezone_info.setText(getResources().getString(R.string.settings_timezone_info_22));
		}else if(timezone.equalsIgnoreCase("+7:00")){
			setting_timezone_info.setText(getResources().getString(R.string.settings_timezone_info_23));
		}else if(timezone.equalsIgnoreCase("+8:00")){
			setting_timezone_info.setText(getResources().getString(R.string.settings_timezone_info_24));
		}else if(timezone.equalsIgnoreCase("+9:00")){
			setting_timezone_info.setText(getResources().getString(R.string.settings_timezone_info_25));
		}else if(timezone.equalsIgnoreCase("+9:30")){
			setting_timezone_info.setText(getResources().getString(R.string.settings_timezone_info_26));
		}else if(timezone.equalsIgnoreCase("+10:00")){
			setting_timezone_info.setText(getResources().getString(R.string.settings_timezone_info_27));
		}else if(timezone.equalsIgnoreCase("+11:00")){
			setting_timezone_info.setText(getResources().getString(R.string.settings_timezone_info_28));
		}else if(timezone.equalsIgnoreCase("+12:00")){
			setting_timezone_info.setText(getResources().getString(R.string.settings_timezone_info_29));
		}else if(timezone.equalsIgnoreCase("+13:00")){
			setting_timezone_info.setText(getResources().getString(R.string.settings_timezone_info_30));
		}
	}
	
	/**
	 * 
	 * @Title: doKeyRight
	 * @Description: 右键处理函数
	 * @return: void
	 */
	private void doKeyRight(){
		mTimeZoneIndex = mTimeZoneIndex + 1;
		if(mTimeZoneIndex > total_timezone){
			mTimeZoneIndex = 1;
		}
		mTimeZone = ID_TIMEZONE.get(Integer.toString(mTimeZoneIndex));
		settingManager.setTimeZone(mTimeZone);
		setting_timezone_selected.setText("UTC" + mTimeZone);
		setTimeZoneInfo(mTimeZone);
	}
	
	/**
	 * 
	 * @Title: doKeyLeft
	 * @Description: 左键处理函数
	 * @return: void
	 */
	private void doKeyLeft(){
		mTimeZoneIndex = mTimeZoneIndex - 1;
		if(mTimeZoneIndex < 1){
			mTimeZoneIndex = total_timezone;
		}
		mTimeZone = ID_TIMEZONE.get(Integer.toString(mTimeZoneIndex));
		settingManager.setTimeZone(mTimeZone);
		setting_timezone_selected.setText("UTC" + mTimeZone);
		setTimeZoneInfo(mTimeZone);
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		switch(keyCode)
		{
			case KeyEvent.KEYCODE_DPAD_RIGHT:
			{
				isBiSend = true;
				doKeyRight();
				break;
			}
			case KeyEvent.KEYCODE_DPAD_LEFT:
			{
				isBiSend = true;
				doKeyLeft();
				break;
			}
		}
		return super.onKeyDown(keyCode, event);
	}
	/* (non-Javadoc)
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * 
	 * @Title: onBackPressed
	 * @Description: 返回键处理函数
	 * @return: void
	 */
	public void onBackPressed(){
		if(isBiSend)
		{
			Bi.sendBiMsg(Bi.BICT_FIRMWARE, Bi.BICC_TIMEZONE_SET, mTimeZone);
		}
		setResult(0);
		finish();
	}
}
