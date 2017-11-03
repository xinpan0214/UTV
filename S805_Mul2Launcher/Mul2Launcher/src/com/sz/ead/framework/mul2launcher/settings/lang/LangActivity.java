/**
 * 
 * Copyright © 2014GreatVision. All rights reserved.
 *
 * @Title: LangActivity.java
 * @Prject: BananaTvSetting
 * @Description: 系统语言设置界面
 * @author: lijungang 
 * @date: 2014-1-24 下午1:45:11
 */
package com.sz.ead.framework.mul2launcher.settings.lang;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.notice.noticemanager.Notice;
import android.os.Bundle;
import android.os.IBinder;
import android.setting.settingservice.SettingManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.sz.ead.framework.mul2launcher.BaseActivity;
import com.sz.ead.framework.mul2launcher.MainActivity;
import com.sz.ead.framework.mul2launcher.R;
import com.sz.ead.framework.mul2launcher.settings.util.ConstUtil;
import com.sz.ead.framework.mul2launcher.status.Statusbar;
import com.szgvtv.ead.framework.bi.Bi;

public class LangActivity extends BaseActivity implements OnClickListener, OnFocusChangeListener{

	private CircleTextView mCircleTextView;
	private RelativeLayout setting_lang_select;
	private ImageView setting_lang_select_icon;
	private String cur_lang = "";
	private String pre_lang = "";
	private SettingManager settingManager;
	private Statusbar mStatus_Bar;
	//按键次数
	private int keyCount = 0; 
	//标志第一次进入还是刷新语言后进入
	private boolean setting_lang_first_in;
    //语言字体数组
	private List<String> langList = new ArrayList<String>();
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings_lang_activity);
		mCircleTextView = (CircleTextView) findViewById(R.id.setting_lang_circleview);
		setting_lang_select = (RelativeLayout)findViewById(R.id.setting_lang_select);
		setting_lang_select_icon = (ImageView)findViewById(R.id.setting_lang_select_icon);
		mStatus_Bar = (Statusbar) findViewById(R.id.llyt_main_header);
		mStatus_Bar.setTaskBarTitle(this.getResources().getString(R.string.settings_menu_lang_title));
		settingManager = (SettingManager)getSystemService(Context.SETTING_SERVICE);
		
		getLangParam(); 
		setVisible(); 
		mCircleTextView.init(langList);
		mCircleTextView.bringToFront();
		setting_lang_select.setOnClickListener(this);
		setting_lang_select.setOnFocusChangeListener(this);
		keyCount = 0;
	}
	
	/**
	 * 
	 * @Title: getLangParam
	 * @Description: 获取当前语言和是否第一次进入标志
	 * @return: void
	 */
	private void getLangParam(){
		SharedPreferences settingInfo = getSharedPreferences(ConstUtil.SETTING_FILE_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = settingInfo.edit();
		if(settingInfo.getBoolean(ConstUtil.SETTING_LANG_FIRST_IN, true))
		{
			setting_lang_first_in = true;
			editor.putBoolean(ConstUtil.SETTING_LANG_FIRST_IN, setting_lang_first_in);
		}
		else{
			setting_lang_first_in = settingInfo.getBoolean(ConstUtil.SETTING_LANG_FIRST_IN, false);
		}
		cur_lang = settingManager.getCurLang();
		pre_lang = cur_lang;
		editor.commit();
	}
	
	/**
	 * 
	 * @Title: setVisible
	 * @Description: 更改界面显示
	 * @return: void
	 */
	private void setVisible(){
		langList.clear();
		if(setting_lang_first_in == true)
		{
		}else if(setting_lang_first_in == false)  //语言设置成功后会再次重新启动该activity
		{
			initImm(this);
		}
		Log.d("test", "lang:"+cur_lang);
		if(cur_lang.equals("zh_CN"))
		{
			langList.add(getResources().getString(R.string.settings_lang_jp));
			langList.add(getResources().getString(R.string.settings_lang_kr));
			langList.add(getResources().getString(R.string.settings_lang_ch));
			langList.add(getResources().getString(R.string.settings_lang_tw));
			langList.add(getResources().getString(R.string.settings_lang_eng));
			langList.add(getResources().getString(R.string.settings_lang_th));
			langList.add(getResources().getString(R.string.settings_lang_vi_VN));
			langList.add(getResources().getString(R.string.settings_lang_ms_MY));
			langList.add(getResources().getString(R.string.settings_lang_tl_PH));
			langList.add(getResources().getString(R.string.settings_lang_hi_IN));
			langList.add(getResources().getString(R.string.settings_lang_es_ES));
			langList.add(getResources().getString(R.string.settings_lang_pt_PT));
			langList.add(getResources().getString(R.string.settings_lang_fr_FR));
			langList.add(getResources().getString(R.string.settings_lang_de_DE));
			langList.add(getResources().getString(R.string.settings_lang_ru_RU));
		}
		else if(cur_lang.equals("zh_TW"))
		{
			langList.add(getResources().getString(R.string.settings_lang_kr));
			langList.add(getResources().getString(R.string.settings_lang_ch));
			langList.add(getResources().getString(R.string.settings_lang_tw));
			langList.add(getResources().getString(R.string.settings_lang_eng));
			langList.add(getResources().getString(R.string.settings_lang_th));
			langList.add(getResources().getString(R.string.settings_lang_vi_VN));
			langList.add(getResources().getString(R.string.settings_lang_ms_MY));
			langList.add(getResources().getString(R.string.settings_lang_tl_PH));
			langList.add(getResources().getString(R.string.settings_lang_hi_IN));
			langList.add(getResources().getString(R.string.settings_lang_es_ES));
			langList.add(getResources().getString(R.string.settings_lang_pt_PT));
			langList.add(getResources().getString(R.string.settings_lang_fr_FR));
			langList.add(getResources().getString(R.string.settings_lang_de_DE));
			langList.add(getResources().getString(R.string.settings_lang_ru_RU));
			langList.add(getResources().getString(R.string.settings_lang_jp));
		}
		else if(cur_lang.equals("en"))
		{
			langList.add(getResources().getString(R.string.settings_lang_ch));
			langList.add(getResources().getString(R.string.settings_lang_tw));
			langList.add(getResources().getString(R.string.settings_lang_eng));
			langList.add(getResources().getString(R.string.settings_lang_th));
			langList.add(getResources().getString(R.string.settings_lang_vi_VN));
			langList.add(getResources().getString(R.string.settings_lang_ms_MY));
			langList.add(getResources().getString(R.string.settings_lang_tl_PH));
			langList.add(getResources().getString(R.string.settings_lang_hi_IN));
			langList.add(getResources().getString(R.string.settings_lang_es_ES));
			langList.add(getResources().getString(R.string.settings_lang_pt_PT));
			langList.add(getResources().getString(R.string.settings_lang_fr_FR));
			langList.add(getResources().getString(R.string.settings_lang_de_DE));
			langList.add(getResources().getString(R.string.settings_lang_ru_RU));
			langList.add(getResources().getString(R.string.settings_lang_jp));
			langList.add(getResources().getString(R.string.settings_lang_kr));
		}
		else if(cur_lang.equals("th_TH"))
		{
			langList.add(getResources().getString(R.string.settings_lang_tw));
			langList.add(getResources().getString(R.string.settings_lang_eng));
			langList.add(getResources().getString(R.string.settings_lang_th));
			langList.add(getResources().getString(R.string.settings_lang_vi_VN));
			langList.add(getResources().getString(R.string.settings_lang_ms_MY));
			langList.add(getResources().getString(R.string.settings_lang_tl_PH));
			langList.add(getResources().getString(R.string.settings_lang_hi_IN));
			langList.add(getResources().getString(R.string.settings_lang_es_ES));
			langList.add(getResources().getString(R.string.settings_lang_pt_PT));
			langList.add(getResources().getString(R.string.settings_lang_fr_FR));
			langList.add(getResources().getString(R.string.settings_lang_de_DE));
			langList.add(getResources().getString(R.string.settings_lang_ru_RU));
			langList.add(getResources().getString(R.string.settings_lang_jp));
			langList.add(getResources().getString(R.string.settings_lang_kr));
			langList.add(getResources().getString(R.string.settings_lang_ch));
		}
		else if(cur_lang.equals("vi_VN"))
		{
			langList.add(getResources().getString(R.string.settings_lang_eng));
			langList.add(getResources().getString(R.string.settings_lang_th));
			langList.add(getResources().getString(R.string.settings_lang_vi_VN));
			langList.add(getResources().getString(R.string.settings_lang_ms_MY));
			langList.add(getResources().getString(R.string.settings_lang_tl_PH));
			langList.add(getResources().getString(R.string.settings_lang_hi_IN));
			langList.add(getResources().getString(R.string.settings_lang_es_ES));
			langList.add(getResources().getString(R.string.settings_lang_pt_PT));
			langList.add(getResources().getString(R.string.settings_lang_fr_FR));
			langList.add(getResources().getString(R.string.settings_lang_de_DE));
			langList.add(getResources().getString(R.string.settings_lang_ru_RU));
			langList.add(getResources().getString(R.string.settings_lang_jp));
			langList.add(getResources().getString(R.string.settings_lang_kr));
			langList.add(getResources().getString(R.string.settings_lang_ch));
			langList.add(getResources().getString(R.string.settings_lang_tw));
		}
		else if(cur_lang.equals("ms_MY"))
		{
			langList.add(getResources().getString(R.string.settings_lang_th));
			langList.add(getResources().getString(R.string.settings_lang_vi_VN));
			langList.add(getResources().getString(R.string.settings_lang_ms_MY));
			langList.add(getResources().getString(R.string.settings_lang_tl_PH));
			langList.add(getResources().getString(R.string.settings_lang_hi_IN));
			langList.add(getResources().getString(R.string.settings_lang_es_ES));
			langList.add(getResources().getString(R.string.settings_lang_pt_PT));
			langList.add(getResources().getString(R.string.settings_lang_fr_FR));
			langList.add(getResources().getString(R.string.settings_lang_de_DE));
			langList.add(getResources().getString(R.string.settings_lang_ru_RU));
			langList.add(getResources().getString(R.string.settings_lang_jp));
			langList.add(getResources().getString(R.string.settings_lang_kr));
			langList.add(getResources().getString(R.string.settings_lang_ch));
			langList.add(getResources().getString(R.string.settings_lang_tw));
			langList.add(getResources().getString(R.string.settings_lang_eng));
		}
		else if(cur_lang.equals("tl_PH"))
		{
			langList.add(getResources().getString(R.string.settings_lang_vi_VN));
			langList.add(getResources().getString(R.string.settings_lang_ms_MY));
			langList.add(getResources().getString(R.string.settings_lang_tl_PH));
			langList.add(getResources().getString(R.string.settings_lang_hi_IN));
			langList.add(getResources().getString(R.string.settings_lang_es_ES));
			langList.add(getResources().getString(R.string.settings_lang_pt_PT));
			langList.add(getResources().getString(R.string.settings_lang_fr_FR));
			langList.add(getResources().getString(R.string.settings_lang_de_DE));
			langList.add(getResources().getString(R.string.settings_lang_ru_RU));
			langList.add(getResources().getString(R.string.settings_lang_jp));
			langList.add(getResources().getString(R.string.settings_lang_kr));
			langList.add(getResources().getString(R.string.settings_lang_ch));
			langList.add(getResources().getString(R.string.settings_lang_tw));
			langList.add(getResources().getString(R.string.settings_lang_eng));
			langList.add(getResources().getString(R.string.settings_lang_th));
		}
		else if(cur_lang.equals("hi_IN"))
		{
			langList.add(getResources().getString(R.string.settings_lang_ms_MY));
			langList.add(getResources().getString(R.string.settings_lang_tl_PH));
			langList.add(getResources().getString(R.string.settings_lang_hi_IN));
			langList.add(getResources().getString(R.string.settings_lang_es_ES));
			langList.add(getResources().getString(R.string.settings_lang_pt_PT));
			langList.add(getResources().getString(R.string.settings_lang_fr_FR));
			langList.add(getResources().getString(R.string.settings_lang_de_DE));
			langList.add(getResources().getString(R.string.settings_lang_ru_RU));
			langList.add(getResources().getString(R.string.settings_lang_jp));
			langList.add(getResources().getString(R.string.settings_lang_kr));
			langList.add(getResources().getString(R.string.settings_lang_ch));
			langList.add(getResources().getString(R.string.settings_lang_tw));
			langList.add(getResources().getString(R.string.settings_lang_eng));
			langList.add(getResources().getString(R.string.settings_lang_th));
			langList.add(getResources().getString(R.string.settings_lang_vi_VN));
		}
		else if(cur_lang.equals("es_ES"))
		{
			langList.add(getResources().getString(R.string.settings_lang_tl_PH));
			langList.add(getResources().getString(R.string.settings_lang_hi_IN));
			langList.add(getResources().getString(R.string.settings_lang_es_ES));
			langList.add(getResources().getString(R.string.settings_lang_pt_PT));
			langList.add(getResources().getString(R.string.settings_lang_fr_FR));
			langList.add(getResources().getString(R.string.settings_lang_de_DE));
			langList.add(getResources().getString(R.string.settings_lang_ru_RU));
			langList.add(getResources().getString(R.string.settings_lang_jp));
			langList.add(getResources().getString(R.string.settings_lang_kr));
			langList.add(getResources().getString(R.string.settings_lang_ch));
			langList.add(getResources().getString(R.string.settings_lang_tw));
			langList.add(getResources().getString(R.string.settings_lang_eng));
			langList.add(getResources().getString(R.string.settings_lang_th));
			langList.add(getResources().getString(R.string.settings_lang_vi_VN));
			langList.add(getResources().getString(R.string.settings_lang_ms_MY));
		}
		else if(cur_lang.equals("pt_PT"))
		{
			langList.add(getResources().getString(R.string.settings_lang_hi_IN));
			langList.add(getResources().getString(R.string.settings_lang_es_ES));
			langList.add(getResources().getString(R.string.settings_lang_pt_PT));
			langList.add(getResources().getString(R.string.settings_lang_fr_FR));
			langList.add(getResources().getString(R.string.settings_lang_de_DE));
			langList.add(getResources().getString(R.string.settings_lang_ru_RU));
			langList.add(getResources().getString(R.string.settings_lang_jp));
			langList.add(getResources().getString(R.string.settings_lang_kr));
			langList.add(getResources().getString(R.string.settings_lang_ch));
			langList.add(getResources().getString(R.string.settings_lang_tw));
			langList.add(getResources().getString(R.string.settings_lang_eng));
			langList.add(getResources().getString(R.string.settings_lang_th));
			langList.add(getResources().getString(R.string.settings_lang_vi_VN));
			langList.add(getResources().getString(R.string.settings_lang_ms_MY));
			langList.add(getResources().getString(R.string.settings_lang_tl_PH));
		}
		else if(cur_lang.equals("fr_FR"))
		{
			langList.add(getResources().getString(R.string.settings_lang_es_ES));
			langList.add(getResources().getString(R.string.settings_lang_pt_PT));
			langList.add(getResources().getString(R.string.settings_lang_fr_FR));
			langList.add(getResources().getString(R.string.settings_lang_de_DE));
			langList.add(getResources().getString(R.string.settings_lang_ru_RU));
			langList.add(getResources().getString(R.string.settings_lang_jp));
			langList.add(getResources().getString(R.string.settings_lang_kr));
			langList.add(getResources().getString(R.string.settings_lang_ch));
			langList.add(getResources().getString(R.string.settings_lang_tw));
			langList.add(getResources().getString(R.string.settings_lang_eng));
			langList.add(getResources().getString(R.string.settings_lang_th));
			langList.add(getResources().getString(R.string.settings_lang_vi_VN));
			langList.add(getResources().getString(R.string.settings_lang_ms_MY));
			langList.add(getResources().getString(R.string.settings_lang_tl_PH));
			langList.add(getResources().getString(R.string.settings_lang_hi_IN));
			
		}
		else if(cur_lang.equals("de_DE"))
		{
			langList.add(getResources().getString(R.string.settings_lang_pt_PT));
			langList.add(getResources().getString(R.string.settings_lang_fr_FR));
			langList.add(getResources().getString(R.string.settings_lang_de_DE));
			langList.add(getResources().getString(R.string.settings_lang_ru_RU));
			langList.add(getResources().getString(R.string.settings_lang_jp));
			langList.add(getResources().getString(R.string.settings_lang_kr));
			langList.add(getResources().getString(R.string.settings_lang_ch));
			langList.add(getResources().getString(R.string.settings_lang_tw));
			langList.add(getResources().getString(R.string.settings_lang_eng));
			langList.add(getResources().getString(R.string.settings_lang_th));
			langList.add(getResources().getString(R.string.settings_lang_vi_VN));
			langList.add(getResources().getString(R.string.settings_lang_ms_MY));
			langList.add(getResources().getString(R.string.settings_lang_tl_PH));
			langList.add(getResources().getString(R.string.settings_lang_hi_IN));
			langList.add(getResources().getString(R.string.settings_lang_es_ES));
		}
		else if(cur_lang.equals("ru_RU"))
		{
			langList.add(getResources().getString(R.string.settings_lang_fr_FR));
			langList.add(getResources().getString(R.string.settings_lang_de_DE));
			langList.add(getResources().getString(R.string.settings_lang_ru_RU));
			langList.add(getResources().getString(R.string.settings_lang_jp));
			langList.add(getResources().getString(R.string.settings_lang_kr));
			langList.add(getResources().getString(R.string.settings_lang_ch));
			langList.add(getResources().getString(R.string.settings_lang_tw));
			langList.add(getResources().getString(R.string.settings_lang_eng));
			langList.add(getResources().getString(R.string.settings_lang_th));
			langList.add(getResources().getString(R.string.settings_lang_vi_VN));
			langList.add(getResources().getString(R.string.settings_lang_ms_MY));
			langList.add(getResources().getString(R.string.settings_lang_tl_PH));
			langList.add(getResources().getString(R.string.settings_lang_hi_IN));
			langList.add(getResources().getString(R.string.settings_lang_es_ES));
			langList.add(getResources().getString(R.string.settings_lang_pt_PT));
		}
		else if(cur_lang.equals("ja"))
		{
			langList.add(getResources().getString(R.string.settings_lang_de_DE));
			langList.add(getResources().getString(R.string.settings_lang_ru_RU));
			langList.add(getResources().getString(R.string.settings_lang_jp));
			langList.add(getResources().getString(R.string.settings_lang_kr));
			langList.add(getResources().getString(R.string.settings_lang_ch));
			langList.add(getResources().getString(R.string.settings_lang_tw));
			langList.add(getResources().getString(R.string.settings_lang_eng));
			langList.add(getResources().getString(R.string.settings_lang_th));
			langList.add(getResources().getString(R.string.settings_lang_vi_VN));
			langList.add(getResources().getString(R.string.settings_lang_ms_MY));
			langList.add(getResources().getString(R.string.settings_lang_tl_PH));
			langList.add(getResources().getString(R.string.settings_lang_hi_IN));
			langList.add(getResources().getString(R.string.settings_lang_es_ES));
			langList.add(getResources().getString(R.string.settings_lang_pt_PT));
			langList.add(getResources().getString(R.string.settings_lang_fr_FR));
		}
		else if(cur_lang.equals("ko"))
		{
			langList.add(getResources().getString(R.string.settings_lang_ru_RU));
			langList.add(getResources().getString(R.string.settings_lang_jp));
			langList.add(getResources().getString(R.string.settings_lang_kr));
			langList.add(getResources().getString(R.string.settings_lang_ch));
			langList.add(getResources().getString(R.string.settings_lang_tw));
			langList.add(getResources().getString(R.string.settings_lang_eng));
			langList.add(getResources().getString(R.string.settings_lang_th));
			langList.add(getResources().getString(R.string.settings_lang_vi_VN));
			langList.add(getResources().getString(R.string.settings_lang_ms_MY));
			langList.add(getResources().getString(R.string.settings_lang_tl_PH));
			langList.add(getResources().getString(R.string.settings_lang_hi_IN));
			langList.add(getResources().getString(R.string.settings_lang_es_ES));
			langList.add(getResources().getString(R.string.settings_lang_pt_PT));
			langList.add(getResources().getString(R.string.settings_lang_fr_FR));
			langList.add(getResources().getString(R.string.settings_lang_de_DE));
		}else{
			langList.add(getResources().getString(R.string.settings_lang_jp));
			langList.add(getResources().getString(R.string.settings_lang_kr));
			langList.add(getResources().getString(R.string.settings_lang_ch));
			langList.add(getResources().getString(R.string.settings_lang_tw));
			langList.add(getResources().getString(R.string.settings_lang_eng));
			langList.add(getResources().getString(R.string.settings_lang_th));
			langList.add(getResources().getString(R.string.settings_lang_vi_VN));
			langList.add(getResources().getString(R.string.settings_lang_ms_MY));
			langList.add(getResources().getString(R.string.settings_lang_tl_PH));
			langList.add(getResources().getString(R.string.settings_lang_hi_IN));
			langList.add(getResources().getString(R.string.settings_lang_es_ES));
			langList.add(getResources().getString(R.string.settings_lang_pt_PT));
			langList.add(getResources().getString(R.string.settings_lang_fr_FR));
			langList.add(getResources().getString(R.string.settings_lang_de_DE));
			langList.add(getResources().getString(R.string.settings_lang_ru_RU));
		}
	}
	
	/**
	 * 
	 * @Title: initImm
	 * @Description: 切换语言后重新初始化输入法
	 * @return: void
	 */
	private void initImm(Activity context) {
		try {
			View v = (View) getRootView(context);
			IBinder token = v.getWindowToken();
			InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.setInputMethod(token, "com.szgvtv.ead.framework.ime/.PinyinIME");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 
	 * @Title: getRootView
	 * @Description: 获取根view
	 * @return: void
	 */
	private View getRootView(Activity context) {
		return ((ViewGroup) context.findViewById(android.R.id.content)).getChildAt(0);
	}
	
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) 
	{
		if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT && event.getAction() == KeyEvent.ACTION_DOWN)
		{
			doKeyRight();
			return true;
		}
		else if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT && event.getAction() == KeyEvent.ACTION_DOWN)
		{
			doKeyLeft();
			return true;
		}
		else if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_CENTER && event.getAction() == KeyEvent.ACTION_DOWN)
		{
			doKeyOK();
			return true;
		}
		return super.dispatchKeyEvent(event);
	}
	/**
	 * 
	 * @Title: doKeyRight
	 * @Description: 右按键操作
	 * @return: void
	 */
	private void doKeyRight(){
		keyCount ++;
		keyCount = keyCount%15;
		mCircleTextView.doKeyRight(keyCount);
		if(keyCount == 0){
			setting_lang_select_icon.setBackground(getResources().getDrawable(R.drawable.settings_lang_select_icon_f));
		}else{
			setting_lang_select_icon.setBackground(getResources().getDrawable(R.drawable.settings_lang_select_icon_uf));
		}
	}
	/**
	 * 
	 * @Title: doKeyLeft
	 * @Description: 左按键操作
	 * @return: void
	 */
	private void doKeyLeft(){
		keyCount --;
		if(keyCount == -1)
		{
			keyCount = 14;
		}
		mCircleTextView.doKeyLeft(keyCount);
		if(keyCount == 0){
			setting_lang_select_icon.setBackground(getResources().getDrawable(R.drawable.settings_lang_select_icon_f));
		}else{
			setting_lang_select_icon.setBackground(getResources().getDrawable(R.drawable.settings_lang_select_icon_uf));
		}
	}
	/**
	 * 
	 * @Title: doKeyOK
	 * @Description: 设置语言
	 * @return: void
	 */
	private void doKeyOK(){
		String lang_name = langList.get((keyCount+2)%15);
		if(lang_name.equalsIgnoreCase(getResources().getString(R.string.settings_lang_ch))){
			cur_lang = "zh_CN";
		}else if(lang_name.equalsIgnoreCase(getResources().getString(R.string.settings_lang_tw))){
			cur_lang = "zh_TW";
		}else if(lang_name.equalsIgnoreCase(getResources().getString(R.string.settings_lang_eng))){
			cur_lang = "en";
		}else if(lang_name.equalsIgnoreCase(getResources().getString(R.string.settings_lang_jp))){
			cur_lang = "ja";
		}else if(lang_name.equalsIgnoreCase(getResources().getString(R.string.settings_lang_kr))){
			cur_lang = "ko";
		}
		else if(lang_name.equalsIgnoreCase(getResources().getString(R.string.settings_lang_th))){
			cur_lang = "th_TH";
		}
		else if(lang_name.equalsIgnoreCase(getResources().getString(R.string.settings_lang_vi_VN))){
			cur_lang = "vi_VN";
		}
		else if(lang_name.equalsIgnoreCase(getResources().getString(R.string.settings_lang_ms_MY))){
			cur_lang = "ms_MY";
		}
		else if(lang_name.equalsIgnoreCase(getResources().getString(R.string.settings_lang_tl_PH))){
			cur_lang = "tl_PH";
		}
		else if(lang_name.equalsIgnoreCase(getResources().getString(R.string.settings_lang_hi_IN))){
			cur_lang = "hi_IN";
		}
		else if(lang_name.equalsIgnoreCase(getResources().getString(R.string.settings_lang_es_ES))){
			cur_lang = "es_ES";
		}
		else if(lang_name.equalsIgnoreCase(getResources().getString(R.string.settings_lang_pt_PT))){
			cur_lang = "pt_PT";
		}
		else if(lang_name.equalsIgnoreCase(getResources().getString(R.string.settings_lang_fr_FR))){
			cur_lang = "fr_FR";
		}
		else if(lang_name.equalsIgnoreCase(getResources().getString(R.string.settings_lang_de_DE))){
			cur_lang = "de_DE";
		}
		else if(lang_name.equalsIgnoreCase(getResources().getString(R.string.settings_lang_ru_RU))){
			cur_lang = "ru_RU";
		}
		
		Bi.sendBiMsg(Bi.BICT_FIRMWARE, Bi.BICC_SET_LANGUAGE, cur_lang);
		Notice mNotice = Notice.makeNotice(this, this.getResources().getString(R.string.settings_prompt_dialog_loading_info), Notice.LENGTH_SHORT
				, R.layout.settings_dialog_waiting, R.id.wait_message);
		mNotice.cancelAll();
		mNotice.show();
		setLangParam();
		settingManager.setCurLang(cur_lang);
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

	}
	
	/**
	 * 
	 * @Title: setLangParam
	 * @Description: 设置语言参数
	 * @return: void
	 */
	private void setLangParam(){
		SharedPreferences settingInfo = getSharedPreferences(ConstUtil.SETTING_FILE_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = settingInfo.edit();
		editor.putBoolean(ConstUtil.SETTING_LANG_FIRST_IN, false);
		editor.commit();
		pre_lang = cur_lang;
	}
	
	/**
	 * 
	 * @Title: onBackPressed
	 * @Description: 返回键处理函数
	 * @return: void
	 */
	public void onBackPressed(){
		SharedPreferences settingInfo = getSharedPreferences(ConstUtil.SETTING_FILE_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = settingInfo.edit();
		editor.putBoolean(ConstUtil.SETTING_LANG_FIRST_IN, true);
		editor.commit();
		setResult(0);
		finish();
	}

	/* (non-Javadoc)
	 * @see android.view.View.OnFocusChangeListener#onFocusChange(android.view.View, boolean)
	 */
	@Override
	public void onFocusChange(View arg0, boolean arg1) {
		// TODO Auto-generated method stub
	}
}
