/**
 * Copyright © 2015GreatVision. All rights reserved.
 *
 * @Title: FragmentSettingsFirst.java
 * @Prject: Mul2Launcher
 * @Package: com.sz.ead.framework.mul2launcher.settings
 * @Description: TODO
 * @author: lijungang  
 * @date: 2015-4-25 上午10:49:13
 */
package com.sz.ead.framework.mul2launcher.settings;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.setting.settingservice.SettingManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.sz.ead.framework.mul2launcher.FragmentBase;
import com.sz.ead.framework.mul2launcher.R;
import com.sz.ead.framework.mul2launcher.settings.lang.LangActivity;
import com.sz.ead.framework.mul2launcher.settings.network.NetworkActivity;
import com.sz.ead.framework.mul2launcher.settings.timezone.TimeZoneActivity;
import com.sz.ead.framework.mul2launcher.settings.upgrade.UpgradeActivity;
import com.sz.ead.framework.mul2launcher.settings.util.NetUtils;
import com.sz.ead.framework.mul2launcher.settings.zoom.ZoomActivity;

/**
 * Copyright © 2015GreatVision. All rights reserved.
 *
 * @Title: FragmentSettingsFirst.java
 * @Prject: Mul2Launcher
 * @Package: com.sz.ead.framework.mul2launcher.settings
 * @Description: TODO
 * @author: lijungang 
 * @date: 2015-4-25 上午10:49:13
 */
public class FragmentSettingsFirst extends FragmentBase implements OnClickListener, OnFocusChangeListener{

	private Activity mActivity;
	private FragmentSettingsSection[] mFragmentSettingsSection = new FragmentSettingsSection[5];
	private String setting_net;
	private String setting_lang;
	private String setting_timezone;
	private String setting_zoom;
	private String setting_upgrade;
	
	private SettingManager settingManager;
	
	private static final int FOCUS_NONE = -1;
	private static final int FOCUS_NETWORK = 0;
	private static final int FOCUS_LANG = 1;
	private static final int FOCUS_TIMEZONE = 2;
	private static final int FOCUS_ZOOM = 3;
	private static final int FOCUS_UPGRADE = 4;
	private int m_focus;
	
	private Animation mBigAnimation;
	private Animation mSmallAnimation;
	
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		this.mActivity = activity;
		settingManager = (SettingManager)mActivity.getSystemService(Context.SETTING_SERVICE);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View v = null;
		v = inflater.inflate(R.layout.settings_fragment_first, container, false);
		mFragmentSettingsSection[0] = (FragmentSettingsSection)v.findViewById(R.id.settings_network);
		mFragmentSettingsSection[1] = (FragmentSettingsSection)v.findViewById(R.id.settings_lang);
		mFragmentSettingsSection[2] = (FragmentSettingsSection)v.findViewById(R.id.settings_timezone);
		mFragmentSettingsSection[3] = (FragmentSettingsSection)v.findViewById(R.id.settings_zoom);
		mFragmentSettingsSection[4] = (FragmentSettingsSection)v.findViewById(R.id.settings_upgrade);
		mBigAnimation = AnimationUtils.loadAnimation(mActivity, R.anim.settings_scale_big_action);
		mSmallAnimation = AnimationUtils.loadAnimation(mActivity, R.anim.settings_scale_small_action);
		getSettingInfo();
		setSettingInfo();
		setListeners();
		m_focus = FOCUS_NONE;
		return v;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
	}
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	/**
	 * 
	 * @Title: getSettingInfo
	 * @Description: 读取当前系统配置项
	 * @return: void
	 */
	@SuppressLint("NewApi")
	private void getSettingInfo(){
		if(NetUtils.isConnected(mActivity))
		{
			setting_net = NetUtils.getNetworkTypeName(mActivity);
		}
		else{
			setting_net = getActivity().getResources().getString(R.string.settings_menu_network_title_info_false);
		}
		setting_lang = settingManager.getCurLang();
		setting_timezone = "UTC" + settingManager.getTimeZone();
		setting_zoom = Integer.toString(settingManager.getCurZoom());
		setting_upgrade = settingManager.getSfVersion();
	}
	/**
	 * 
	 * @Title: setSettingInfo
	 * @Description: 设置当前系统配置项显示
	 * @return: void
	 */
	@SuppressLint("NewApi")
	private void setSettingInfo(){
		mFragmentSettingsSection[0].setBgImg(R.drawable.settings_section_uf_network);
		mFragmentSettingsSection[0].setSectionImg(R.drawable.settings_network_uf_icon);
		mFragmentSettingsSection[0].setSectionText1(getResources().getString(R.string.settings_menu_network_title), R.color.settings_menu_f_color);
		mFragmentSettingsSection[0].setSectionText2(setting_net, R.color.settings_menu_f_color);
		
		mFragmentSettingsSection[1].setBgImg(R.drawable.settings_section_uf_lang);
		mFragmentSettingsSection[1].setSectionImg(R.drawable.settings_lang_uf_icon);
		mFragmentSettingsSection[1].setSectionText1(getResources().getString(R.string.settings_menu_lang_title), R.color.settings_menu_f_color);
		if(setting_lang.equals("zh_CN")){
			mFragmentSettingsSection[1].setSectionText2(getResources().getString(R.string.settings_lang_ch), R.color.settings_menu_f_color);
		}else if(setting_lang.equals("zh_TW")){
			mFragmentSettingsSection[1].setSectionText2(getResources().getString(R.string.settings_lang_tw), R.color.settings_menu_f_color);
		}else if(setting_lang.equals("en")){
			mFragmentSettingsSection[1].setSectionText2(getResources().getString(R.string.settings_lang_eng), R.color.settings_menu_f_color);
		}else if(setting_lang.equals("ja")){
			mFragmentSettingsSection[1].setSectionText2(getResources().getString(R.string.settings_lang_jp), R.color.settings_menu_f_color);
		}else if(setting_lang.equals("ko")){
			mFragmentSettingsSection[1].setSectionText2(getResources().getString(R.string.settings_lang_kr), R.color.settings_menu_f_color);
		}else if(setting_lang.equals("th")){
			mFragmentSettingsSection[1].setSectionText2(getResources().getString(R.string.settings_lang_th), R.color.settings_menu_f_color);
		}
		else if(setting_lang.equals("th_TH"))
		{
			mFragmentSettingsSection[1].setSectionText2(getResources().getString(R.string.settings_lang_th), R.color.settings_menu_f_color);
		}
		else if(setting_lang.equals("vi_VN"))
		{
			mFragmentSettingsSection[1].setSectionText2(getResources().getString(R.string.settings_lang_vi_VN), R.color.settings_menu_f_color);
		}
		else if(setting_lang.equals("ms_MY"))
		{
			mFragmentSettingsSection[1].setSectionText2(getResources().getString(R.string.settings_lang_ms_MY), R.color.settings_menu_f_color);
		}
		else if(setting_lang.equals("tl_PH"))
		{
			mFragmentSettingsSection[1].setSectionText2(getResources().getString(R.string.settings_lang_tl_PH), R.color.settings_menu_f_color);
		}
		else if(setting_lang.equals("hi_IN"))
		{
			mFragmentSettingsSection[1].setSectionText2(getResources().getString(R.string.settings_lang_hi_IN), R.color.settings_menu_f_color);
		}
		else if(setting_lang.equals("es_ES"))
		{
			mFragmentSettingsSection[1].setSectionText2(getResources().getString(R.string.settings_lang_es_ES), R.color.settings_menu_f_color);
		}
		else if(setting_lang.equals("pt_PT"))
		{
			mFragmentSettingsSection[1].setSectionText2(getResources().getString(R.string.settings_lang_pt_PT), R.color.settings_menu_f_color);
		}
		else if(setting_lang.equals("fr_FR"))
		{
			mFragmentSettingsSection[1].setSectionText2(getResources().getString(R.string.settings_lang_fr_FR), R.color.settings_menu_f_color);
		}
		else if(setting_lang.equals("de_DE"))
		{
			mFragmentSettingsSection[1].setSectionText2(getResources().getString(R.string.settings_lang_de_DE), R.color.settings_menu_f_color);
		}
		else if(setting_lang.equals("ru_RU"))
		{
			mFragmentSettingsSection[1].setSectionText2(getResources().getString(R.string.settings_lang_ru_RU), R.color.settings_menu_f_color);
		}
		else{
		mFragmentSettingsSection[1].setSectionText2(getResources().getString(R.string.settings_lang_eng), R.color.settings_menu_f_color);
		}
		
		mFragmentSettingsSection[2].setBgImg(R.drawable.settings_section_uf_timezone);
		mFragmentSettingsSection[2].setSectionImg(R.drawable.settings_timezone_uf_icon);
		mFragmentSettingsSection[2].setSectionText1(getResources().getString(R.string.settings_menu_timezone_title), R.color.settings_menu_f_color);
		mFragmentSettingsSection[2].setSectionText2(setting_timezone, R.color.settings_menu_f_color);
		
		mFragmentSettingsSection[3].setBgImg(R.drawable.settings_section_uf_zoom);
		mFragmentSettingsSection[3].setSectionImg(R.drawable.settings_zoom_uf_icon);
		mFragmentSettingsSection[3].setSectionText1(getResources().getString(R.string.settings_menu_zoom_title), R.color.settings_menu_f_color);
		mFragmentSettingsSection[3].setSectionText2(setting_zoom+"%", R.color.settings_menu_f_color);
		
		mFragmentSettingsSection[4].setBgImg(R.drawable.settings_section_uf_upgrade);
		mFragmentSettingsSection[4].setSectionImg(R.drawable.settings_upgrade_uf_icon);
		mFragmentSettingsSection[4].setSectionText1(getResources().getString(R.string.settings_menu_upgrade_title), R.color.settings_menu_f_color);
		mFragmentSettingsSection[4].setSectionText2("V"+setting_upgrade, R.color.settings_menu_f_color);
	}
	/**
	 * 
	 * @Title: setListeners
	 * @Description: 监听每个选择块
	 * @return: void
	 */
	private void setListeners() {
		for (FragmentSettingsSection fss : mFragmentSettingsSection) {
			fss.setOnClickListener(this);
			fss.setOnFocusChangeListener(this);
		}
	}
	/**
	 * 
	 * @Title: dispatchKeyEvent
	 * @Description: 按键响应
	 * @return: int (-1:不响应, 0：左右响应按键，但不切换fragment, 1:向右翻页, 2:向左翻页)
	 */
	/* (non-Javadoc)
	 * @see com.sz.ead.framework.mul2launcher.FragmentBase#dispatchKeyEvent(android.view.KeyEvent)
	 */
	@Override
	public int dispatchKeyEvent(KeyEvent event) {
		// TODO Auto-generated method stub
		int index = -1;
		if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT && event.getAction() == KeyEvent.ACTION_DOWN)
		{
			index = doKeyRight();
		} 
		else if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT && event.getAction() == KeyEvent.ACTION_DOWN)
		{
			index = doKeyLeft();
		}
		return index;
	}
	/**
	 * 
	 * @Title: initFragmentFocus
	 * @Description: 初始化焦点
	 * @return: void
	 */
	@Override
	public void initFragmentFocus() {
		// TODO Auto-generated method stub
		mFragmentSettingsSection[0].requestFocus();
		mFragmentSettingsSection[0].setBgImg(R.drawable.settings_section_f_icon);
		mFragmentSettingsSection[0].setSectionImg(R.drawable.settings_network_f_icon);
		mFragmentSettingsSection[0].setSectionText1(getResources().getString(R.string.settings_menu_network_title), R.color.settings_menu_f_color);
		mFragmentSettingsSection[0].setSectionText2(setting_net, R.color.settings_menu_f_color);
		mFragmentSettingsSection[0].startAnimation(mBigAnimation);
		m_focus = FOCUS_NETWORK;
	}
	/**
	 * 
	 * @Title: setSectionFocus
	 * @Description: 设置section焦点
	 * @return: int
	 */
	@SuppressLint("NewApi")
	private void setSectionFocus(int index){
		setSettingInfo();
		if(index == 0){
			mFragmentSettingsSection[0].requestFocus();
			mFragmentSettingsSection[0].setBgImg(R.drawable.settings_section_f_icon);
			mFragmentSettingsSection[0].setSectionImg(R.drawable.settings_network_f_icon);
			mFragmentSettingsSection[0].setSectionText1(getResources().getString(R.string.settings_menu_network_title), R.color.settings_menu_f_color);
			mFragmentSettingsSection[0].setSectionText2(setting_net, R.color.settings_menu_f_color);
		}else if(index == 1){
			mFragmentSettingsSection[1].requestFocus();
			mFragmentSettingsSection[1].setBgImg(R.drawable.settings_section_f_icon);
			mFragmentSettingsSection[1].setSectionImg(R.drawable.settings_lang_f_icon);
			mFragmentSettingsSection[1].setSectionText1(getResources().getString(R.string.settings_menu_lang_title), R.color.settings_menu_f_color);
			if(setting_lang.equals("zh_CN")){
				mFragmentSettingsSection[1].setSectionText2(getResources().getString(R.string.settings_lang_ch), R.color.settings_menu_f_color);
			}else if(setting_lang.equals("zh_TW")){
				mFragmentSettingsSection[1].setSectionText2(getResources().getString(R.string.settings_lang_tw), R.color.settings_menu_f_color);
			}else if(setting_lang.equals("en")){
				mFragmentSettingsSection[1].setSectionText2(getResources().getString(R.string.settings_lang_eng), R.color.settings_menu_f_color);
			}else if(setting_lang.equals("ja")){
				mFragmentSettingsSection[1].setSectionText2(getResources().getString(R.string.settings_lang_jp), R.color.settings_menu_f_color);
			}else if(setting_lang.equals("ko")){
				mFragmentSettingsSection[1].setSectionText2(getResources().getString(R.string.settings_lang_kr), R.color.settings_menu_f_color);
			}
			else if(setting_lang.equals("th_TH"))
			{
				mFragmentSettingsSection[1].setSectionText2(getResources().getString(R.string.settings_lang_th), R.color.settings_menu_f_color);
			}
			else if(setting_lang.equals("vi_VN"))
			{
				mFragmentSettingsSection[1].setSectionText2(getResources().getString(R.string.settings_lang_vi_VN), R.color.settings_menu_f_color);
			}
			else if(setting_lang.equals("ms_MY"))
			{
				mFragmentSettingsSection[1].setSectionText2(getResources().getString(R.string.settings_lang_ms_MY), R.color.settings_menu_f_color);
			}
			else if(setting_lang.equals("tl_PH"))
			{
				mFragmentSettingsSection[1].setSectionText2(getResources().getString(R.string.settings_lang_tl_PH), R.color.settings_menu_f_color);
			}
			else if(setting_lang.equals("hi_IN"))
			{
				mFragmentSettingsSection[1].setSectionText2(getResources().getString(R.string.settings_lang_hi_IN), R.color.settings_menu_f_color);
			}
			else if(setting_lang.equals("es_ES"))
			{
				mFragmentSettingsSection[1].setSectionText2(getResources().getString(R.string.settings_lang_es_ES), R.color.settings_menu_f_color);
			}
			else if(setting_lang.equals("pt_PT"))
			{
				mFragmentSettingsSection[1].setSectionText2(getResources().getString(R.string.settings_lang_pt_PT), R.color.settings_menu_f_color);
			}
			else if(setting_lang.equals("fr_FR"))
			{
				mFragmentSettingsSection[1].setSectionText2(getResources().getString(R.string.settings_lang_fr_FR), R.color.settings_menu_f_color);
			}
			else if(setting_lang.equals("de_DE"))
			{
				mFragmentSettingsSection[1].setSectionText2(getResources().getString(R.string.settings_lang_de_DE), R.color.settings_menu_f_color);
			}
			else if(setting_lang.equals("ru_RU"))
			{
				mFragmentSettingsSection[1].setSectionText2(getResources().getString(R.string.settings_lang_ru_RU), R.color.settings_menu_f_color);
			}
		}else if(index == 2){
			mFragmentSettingsSection[2].requestFocus();
			mFragmentSettingsSection[2].setBgImg(R.drawable.settings_section_f_icon);
			mFragmentSettingsSection[2].setSectionImg(R.drawable.settings_timezone_f_icon);
			mFragmentSettingsSection[2].setSectionText1(getResources().getString(R.string.settings_menu_timezone_title), R.color.settings_menu_f_color);
			mFragmentSettingsSection[2].setSectionText2(setting_timezone, R.color.settings_menu_f_color);
		}else if(index == 3){
			mFragmentSettingsSection[3].requestFocus();
			mFragmentSettingsSection[3].setBgImg(R.drawable.settings_section_f_icon);
			mFragmentSettingsSection[3].setSectionImg(R.drawable.settings_zoom_f_icon);
			mFragmentSettingsSection[3].setSectionText1(getResources().getString(R.string.settings_menu_zoom_title), R.color.settings_menu_f_color);
			mFragmentSettingsSection[3].setSectionText2(setting_zoom+"%", R.color.settings_menu_f_color);
		}
		else if(index == 4){
			mFragmentSettingsSection[4].requestFocus();
			mFragmentSettingsSection[4].setBgImg(R.drawable.settings_section_f_icon);
			mFragmentSettingsSection[4].setSectionImg(R.drawable.settings_upgrade_f_icon);
			mFragmentSettingsSection[4].setSectionText1(getResources().getString(R.string.settings_menu_upgrade_title), R.color.settings_menu_f_color);
			mFragmentSettingsSection[4].setSectionText2("V"+setting_upgrade, R.color.settings_menu_f_color);
		}
	}
	/**
	 * 
	 * @Title: doKeyRight
	 * @Description: 右按键
	 * @return: int
	 */
	private int doKeyRight(){
		if (m_focus == FOCUS_NETWORK)
		{
			m_focus = FOCUS_LANG;
			setSectionFocus(1);
			return 0;
		}else if (m_focus == FOCUS_LANG)
		{
			m_focus = FOCUS_TIMEZONE;
			setSectionFocus(2);
			return 0;
		}else if (m_focus == FOCUS_TIMEZONE)
		{
			m_focus = FOCUS_ZOOM;
			setSectionFocus(3);
			return 0;
		}else if (m_focus == FOCUS_ZOOM)
		{
			m_focus = FOCUS_UPGRADE;
			setSectionFocus(4);
			return 0;
		}else if (m_focus == FOCUS_UPGRADE){
			m_focus = FOCUS_NONE;
			setSectionFocus(-1);
			return 1;
		}
		return -1;
	}
	/**
	 * 
	 * @Title: doKeyLeft
	 * @Description: 左按键
	 * @return: int
	 */
	private int doKeyLeft(){
		if (m_focus == FOCUS_LANG)
		{
			m_focus = FOCUS_NETWORK;
			setSectionFocus(0);
			return 0;
		}else if (m_focus == FOCUS_TIMEZONE)
		{
			m_focus = FOCUS_LANG;
			setSectionFocus(1);
			return 0;
		}else if (m_focus == FOCUS_ZOOM)
		{
			m_focus = FOCUS_TIMEZONE;
			setSectionFocus(2);
			return 0;
		}else if(m_focus == FOCUS_UPGRADE){
			m_focus = FOCUS_ZOOM;
			setSectionFocus(3);
			return 0;
		}else if(m_focus == FOCUS_NETWORK){
			return 0;
		}
		return -1;
	}

	@Override
	public void onFocusChange(View arg0, boolean arg1) {
		// TODO Auto-generated method stub
		
		if (arg1)
		{
			if (arg0 == mFragmentSettingsSection[0])
			{
				mFragmentSettingsSection[0].startAnimation(mBigAnimation);
			}
			else if(arg0 == mFragmentSettingsSection[1])
			{
				mFragmentSettingsSection[1].startAnimation(mBigAnimation);
			}
			else if(arg0 == mFragmentSettingsSection[2])
			{
				mFragmentSettingsSection[2].startAnimation(mBigAnimation);
			}
			else if(arg0 == mFragmentSettingsSection[3])
			{
				mFragmentSettingsSection[3].startAnimation(mBigAnimation);
			}
			else if(arg0 == mFragmentSettingsSection[4])
			{
				mFragmentSettingsSection[4].startAnimation(mBigAnimation);
			}
		}
		else
		{
			if (arg0 == mFragmentSettingsSection[0])
			{
				mFragmentSettingsSection[0].startAnimation(mSmallAnimation);
			}
			else if(arg0 == mFragmentSettingsSection[1])
			{
				mFragmentSettingsSection[1].startAnimation(mSmallAnimation);
			}
			else if(arg0 == mFragmentSettingsSection[2])
			{
				mFragmentSettingsSection[2].startAnimation(mSmallAnimation);
			}
			else if(arg0 == mFragmentSettingsSection[3])
			{
				mFragmentSettingsSection[3].startAnimation(mSmallAnimation);
			}
			else if(arg0 == mFragmentSettingsSection[4])
			{
				mFragmentSettingsSection[4].startAnimation(mSmallAnimation);
			}
		}
	}

	@SuppressLint("NewApi")
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId())
		{
		case R.id.settings_network:
			Intent intent_network = new Intent();
			intent_network.setClass(mActivity, NetworkActivity.class);
			getParentFragment().startActivityForResult(intent_network, 0);
			break;
		case R.id.settings_lang:
			Intent intent_lang = new Intent();
			intent_lang.setClass(mActivity, LangActivity.class);
			getParentFragment().startActivityForResult(intent_lang, 0);
			break;
		case R.id.settings_timezone:
			Intent intent_timezone = new Intent();
			intent_timezone.setClass(mActivity, TimeZoneActivity.class);
			getParentFragment().startActivityForResult(intent_timezone, 0);
			break;
		case R.id.settings_zoom:
			Intent intent_zoom = new Intent();
			intent_zoom.setClass(mActivity, ZoomActivity.class);
			getParentFragment().startActivityForResult(intent_zoom, 0);
			break;
		case R.id.settings_upgrade:
			Intent intent_upgrade = new Intent();
			intent_upgrade.setClass(mActivity, UpgradeActivity.class);
			getParentFragment().startActivityForResult(intent_upgrade, 0);
			break;
		default:
			break;
		}
	}
	/**
	 * 
	 * @Title: getFocus
	 * @Description: 获取当前焦点位置
	 * @return: int
	 */
	public int getFocus(){
		return m_focus;
	}
	/**
	 * 
	 * @Title: setFocus
	 * @Description: 切换语言重置恢复焦点位置
	 * @return: int
	 */
	public void setFocus(int focus){
		m_focus = focus;
	}
	/**
	 * 
	 * @Title: onResult
	 * @Description: 启动activity后返回更新主页状态
	 * @return: void
	 */
	public void onResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		getSettingInfo();
		if(m_focus == FOCUS_NETWORK){
			setSectionFocus(0);
		}else if(m_focus == FOCUS_LANG){
			setSectionFocus(1);
		}else if(m_focus == FOCUS_TIMEZONE){
			setSectionFocus(2);
		}else if(m_focus == FOCUS_ZOOM){
			setSectionFocus(3);
		}else if(m_focus == FOCUS_UPGRADE){
			setSectionFocus(4);
		}
	}
	/**
	 * 
	 * @Title: fragmentLeftSwitch
	 * @Description: 从子fragment第二页切换到子fragment第一页
	 * @return: void
	 */
	public void fragmentLeftSwitch() {
		// TODO Auto-generated method stub
		m_focus = FOCUS_UPGRADE;
		setSectionFocus(4);
	}
	/**
	 * 
	 * @Title: resetFragment
	 * @Description: 重置状态
	 * @return: void
	 */
	/* (non-Javadoc)
	 * @see com.sz.ead.framework.mul2launcher.FragmentBase#resetFragment()
	 */
	@Override
	public void resetFragment() {
		// TODO Auto-generated method stub
		m_focus = FOCUS_NONE;
		setSectionFocus(-1);
	}
	
	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();
	}

	@Override
	public void onDetach() {
		// TODO Auto-generated method stub
		super.onDetach();
	}
}
