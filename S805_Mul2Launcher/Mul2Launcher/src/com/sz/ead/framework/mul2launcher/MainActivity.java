/**
 * Copyright © 2015GreatVision. All rights reserved.
 *
 * @Title: MainActivity.java
 * @Prject: Mul2Launcher
 * @Package: com.sz.ead.framework.mul2launcher
 * @Description: 主页
 * @author: lijungang 
 * @date: 2015-4-22 上午10:50:48
 */
package com.sz.ead.framework.mul2launcher;

import java.lang.reflect.Field;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.login.loginservice.LoginManager;
import android.notice.noticemanager.Notice;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.animation.DecelerateInterpolator;

import com.sz.ead.framework.mul2launcher.application.UILApplication;
import com.sz.ead.framework.mul2launcher.appstore.FragmentAppStore;
import com.sz.ead.framework.mul2launcher.appstore.UpdateAppListService;
import com.sz.ead.framework.mul2launcher.appstore.downloader.DownConstants;
import com.sz.ead.framework.mul2launcher.dataprovider.dataitem.AuthResInfo;
import com.sz.ead.framework.mul2launcher.mainapp.FragmentMainApp;
import com.sz.ead.framework.mul2launcher.nav.NavLayout;
import com.sz.ead.framework.mul2launcher.settings.FragmentSettings;
import com.sz.ead.framework.mul2launcher.status.LauncherStatusbar;
import com.sz.ead.framework.mul2launcher.util.AuthFailUtil;
import com.sz.ead.framework.mul2launcher.util.ConstantUtil;
import com.sz.ead.framework.mul2launcher.util.LogUtil;

public class MainActivity extends FragmentActivity 
{
	//切换到应用商城的广播action
	public static final String ACTION_SWITCH_TO_APPSTORE = "com.sz.ead.framework.mul2launcher.action_switch_to_appstore"; 
	//更新应用列表
	public static final String ACTION_UPDATE_APPLIST = "com.sz.ead.framework.mul2launcher.action_update_applist"; 
	//鉴权
	public static final String ACTION_AUTH = "com.sz.ead.framework.info.InfoService.auth";
	
	public static final String TAG = "mul2launcher";
	
	private boolean isFocusedNav;
	private Context mContext;
	private ViewPager mPager;
	private FixedSpeedScroller scroller = null;
	private NavLayout mNavLayout;        //导航栏
	private int cur_focus_fragment = -1; // -1:当前选中的fragment初始值, 0：mainapp, 1:appstore, 2:settings
	private int pre_focus_fragment = -1; // 上次一次选中的fragment
	private LauncherStatusbar mStatus_Bar;
	private Mul2launcherReceiver mReceiver = new Mul2launcherReceiver();
	private boolean mUpdateable = false;
	private StringBuffer upgradeKey = new StringBuffer();
	private static final String UPGRADE_PASS = "2009818111";
	private StringBuffer upgradeKeyDirection = new StringBuffer();
	private static final String UPGRADE_PASS_DIRECTION = "102231032"; // 方向键调出线下升级，上为0，下为1，左为2，右为3。(下上左左右下上右左)
	private AuthResInfo authInfo = null;
	private LoginManager mLogin = null;
	private Notice messageNotic = null;
	protected HomeKeyEventBroadCastReceiver mHomeKeyEventBroadCastReceiver;
	private FragmentMainApp mFragmentMainApp; //我的应用fragment
	private FragmentAppStore mFragmentAppStore; //应用商城fragment
	private FragmentSettings mFragmentSettings; //系统设置fragment
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		Log.v(TAG, "MainActivity onCreate!");
		super.onCreate(savedInstanceState);
		mContext = this;
		setContentView(R.layout.main_activity);
		findViews();
		initFragment();
		
		UILApplication.initService(getApplicationContext()); 
		registerIntentReceivers();
		getASUrl(); // 获取服务器URL
	}

	/**
	 * 
	 * @Title: onSaveInstanceState
	 * @Description: 保存状态
	 * @return
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		Log.v(TAG, "MainActivity onSaveInstanceState!");
		getSupportFragmentManager().putFragment(outState,"num0",(Fragment)mFragmentMainApp);
		getSupportFragmentManager().putFragment(outState,"num1",(Fragment)mFragmentAppStore);
		getSupportFragmentManager().putFragment(outState,"num2",(Fragment)mFragmentSettings);
		mFragmentSettings.onSettingsSaveInstanceState(outState);
		outState.putInt("NAV_FOCUS", mNavLayout.getNavFocusIndex());
		outState.putBoolean("IS_FOCUSED_NAV", isFocusedNav);
		outState.putInt("CUR_FOCUS", cur_focus_fragment);
		super.onSaveInstanceState(outState);
	}

	/**
	 * 
	 * @Title: onRestoreInstanceState
	 * @Description: 恢复状态
	 * @return
	 */
	@Override
	protected void onRestoreInstanceState(Bundle arg0) {
		Log.v(TAG, "MainActivity onRestoreInstanceState!");
		mFragmentMainApp = (FragmentMainApp)getSupportFragmentManager().getFragment(arg0,"num0");
		mFragmentAppStore = (FragmentAppStore)getSupportFragmentManager().getFragment(arg0,"num1");
		mFragmentSettings = (FragmentSettings)getSupportFragmentManager().getFragment(arg0,"num2");
		mFragmentSettings.onSettingsRestoreInstanceState(arg0);
		cur_focus_fragment = arg0.getInt("CUR_FOCUS");
		isFocusedNav = arg0.getBoolean("IS_FOCUSED_NAV");
		mNavLayout.restoreInstanceState(arg0.getInt("NAV_FOCUS"), cur_focus_fragment, isFocusedNav);
		super.onRestoreInstanceState(arg0);
	}
	
	/**
	 * 
	 * @Title: findViews
	 * @Description: 初始化界面
	 * @return: void
	 */
	private void findViews()
	{
		mNavLayout = (NavLayout)findViewById(R.id.nav_title);
		mPager = (ViewPager)findViewById(R.id.main_rllt);
		mStatus_Bar = (LauncherStatusbar) findViewById(R.id.lsb_main_header);
		isFocusedNav = true;
		setScrollerTime(300);
		mLogin = (LoginManager) getSystemService(Context.LOGIN_SERVICE);
	}

	/**
	 * 
	 * @Title: setScrollerTime
	 * @Description: 自定义ViewPager的Scroller
	 * @param scrollerTime
	 */
	public void setScrollerTime(int scrollerTime) {
		try {
			if (scroller != null) {
				scroller.setTime(scrollerTime);
			} else {
				Field mScroller;
				mScroller = ViewPager.class.getDeclaredField("mScroller");
				mScroller.setAccessible(true);
				scroller = new FixedSpeedScroller(mPager.getContext(),
						new DecelerateInterpolator());
				scroller.setTime(scrollerTime);
				mScroller.set(mPager, scroller);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @Title: initFragment
	 * @Description: 初始化fragment
	 * @return: void
	 */
	@SuppressLint("NewApi")
	private void initFragment()
	{
		mPager.setAdapter(new MainSectionsPagerAdapter(getSupportFragmentManager()));
		mPager.setOffscreenPageLimit(2);
		cur_focus_fragment = 0;
		pre_focus_fragment = 0;
		mPager.setCurrentItem(cur_focus_fragment ,false);
		mPager.setOnPageChangeListener(new OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int arg0) {
				switch(arg0){
				case 0:
					mFragmentAppStore.doForHomeKey();//焦点在我的应用时清空应用商城焦点
					break;
				case 1:
					mFragmentSettings.changeFirstFragment();
					break;
				case 2:
					break;
				}
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				
			}
			
			@Override
			public void onPageScrollStateChanged(int arg0) {
				
			}
		});
	}
	
	/**
	 * 
	 * @Title: registerIntentReceivers
	 * @Description: 注册广播
	 * @return: void
	 */
	private void registerIntentReceivers() 
	{
		//切换到应用商城
		IntentFilter filter1 = new IntentFilter();
		filter1.addAction(ACTION_SWITCH_TO_APPSTORE);
		mContext.registerReceiver(mReceiver, filter1);
		//更新应用商城的应用列表
		IntentFilter filter2 = new IntentFilter();
		filter2.addAction(ACTION_UPDATE_APPLIST);
		mContext.registerReceiver(mReceiver, filter2);
		//鉴权
		IntentFilter filter3 = new IntentFilter();
		filter3.addAction(ACTION_AUTH);  
		mContext.registerReceiver(mReceiver, filter3);
		
		//服务器URL广播
		IntentFilter filter4 = new IntentFilter();
		filter4.addAction(DownConstants.URL_ACTION);  
		mContext.registerReceiver(mReceiver, filter4);
	}
	
	/**
	 * 
	 * @Title: unregisterIntentReceivers
	 * @Description: 注销广播
	 * @return: void
	 */
	private void unregisterIntentReceivers() 
	{
		if (mReceiver != null) 
		{
			mContext.unregisterReceiver(mReceiver);
			mReceiver = null;
		}
	}

	@Override
	protected void onStart() {
		Log.v(TAG, "MainActivity onStart!");
		super.onStart();
	}

	/**
	 * 
	 * @Title: onResume
	 * @Description: Resume状态注册home广播，处理home按键
	 * @param 
	 */
	@Override
	protected void onResume() {
		Log.v(TAG, "MainActivity onResume!");
		if(UILApplication.isSwitchToHome){
			mNavLayout.setFocusNav(0);
			cur_focus_fragment = 0;
			mPager.setCurrentItem(cur_focus_fragment ,false);
			mFragmentMainApp.doForHomeKey();
		}
		mHomeKeyEventBroadCastReceiver = new HomeKeyEventBroadCastReceiver();  
		registerReceiver(mHomeKeyEventBroadCastReceiver, new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));
		super.onResume();
	}

	/**
	 * 
	 * @Title: HomeKeyEventBroadCastReceiver
	 * @Description: home按键广播
	 * @param 
	 */
	private class HomeKeyEventBroadCastReceiver extends BroadcastReceiver {

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
						switchToFirst();
					} else if (reason.equals(SYSTEM_RECENT_APPS)) {
						// long home key处理点
					}
				}
			}
		}
	}
	
	/**
	 * 
	 * @Title: setFocusNav
	 * @Description: 设置焦点是否选中nav栏
	 * @return: void
	 */
	public void setFocusNav(boolean flag){
		isFocusedNav = flag;
	}
	
	/**
	 * 
	 * @Title: isFocusNav
	 * @Description: 焦点是否选中nav栏
	 * @return: boolean
	 */
	public boolean isFocusNav(){
		return isFocusedNav;
	}
	
	/**
	 * 
	 * @Title: dispatchKeyEvent
	 * @Description: 按键入口
	 * @return: boolean
	 */
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) 
	{
		if(event.getAction() == KeyEvent.ACTION_DOWN)
		{
			checkOffLineUpgrade(event);
			if (event.getKeyCode() >= KeyEvent.KEYCODE_0 && event.getKeyCode() <= KeyEvent.KEYCODE_9) 
			{
				upgradeKey.append(event.getKeyCode() - KeyEvent.KEYCODE_0);
				if (UPGRADE_PASS.contains(upgradeKey.toString())) 
				{
					if (UPGRADE_PASS.equals(upgradeKey.toString())) 
					{
						Intent intent = new Intent();
						intent.setClassName("com.sz.ead.framework.systemupgrade", "com.sz.ead.framework.systemupgrade.MainActivity");
						intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						intent.putExtra("upgradeType", "sdcard");
						this.startActivity(intent);
					}
				} 
				else 
				{
					upgradeKey.setLength(0);
					upgradeKey.append(event.getKeyCode() - KeyEvent.KEYCODE_0);
				}
			} 
			else 
			{
				upgradeKey.setLength(0);
			}
		}
		
		if(mNavLayout.isFocusNav())
		{
			int nav_focus = -2;
			nav_focus = mNavLayout.doKeyEvent(event);
			Log.v(TAG, "MainActivity isFocusNav! nav_focus:" + nav_focus);
			if(nav_focus == -2)  
			{
				//左右边界
				return true;
			}
			else if(nav_focus == -1)  //down 按键
			{
				//当"应用商城"的应用个数为0时, 不能向下
				if (cur_focus_fragment == 1)
				{
					int appCount = mFragmentAppStore.getAppCount();
					if (appCount <= 0)
					{
						mNavLayout.reSetFocusNav();
						return true;
					}
				}
				if(cur_focus_fragment == 0){
					mFragmentMainApp.initFragmentFocus();
				}else if(cur_focus_fragment ==1){
					mFragmentAppStore.initFragmentFocus();
				}else if(cur_focus_fragment ==2){
					mFragmentSettings.initFragmentFocus();
				}
				isFocusedNav = false;
				return true;
			}
			else if(nav_focus >= 0)  //左右按键，nav_focus返回值为导航栏焦点
			{
				pre_focus_fragment = cur_focus_fragment;
				cur_focus_fragment = nav_focus;
				
				if (cur_focus_fragment == 1 && mUpdateable)
				{
					mUpdateable = false;
					mFragmentAppStore.reGetAppList();
				}
				mPager.setCurrentItem(cur_focus_fragment, true);
				if(cur_focus_fragment == 0){
					mFragmentMainApp.resetFragment();
				}else if(cur_focus_fragment ==1){
					mFragmentAppStore.resetFragment();
				}else if(cur_focus_fragment ==2){
					mFragmentSettings.resetFragment();
				}
				return true;
			}
		}
		else
		{
			int index = -1;
			Log.v(TAG, "MainActivity isnot Focus Nav! cur_focus_fragment:" + cur_focus_fragment);
			if(cur_focus_fragment == 0){
				index = mFragmentMainApp.dispatchKeyEvent(event);
			}else if(cur_focus_fragment ==1){
				index = mFragmentAppStore.dispatchKeyEvent(event);
			}else if(cur_focus_fragment ==2){
				index = mFragmentSettings.dispatchKeyEvent(event);
			}
			if(index == -2)  //向左切换
			{ 
				pre_focus_fragment = cur_focus_fragment;
				cur_focus_fragment = cur_focus_fragment + 1;
				mPager.setCurrentItem(cur_focus_fragment, true);
				return true;
			}
			else if(index == -3)  //向右切换
			{ 
				pre_focus_fragment = cur_focus_fragment;
				cur_focus_fragment = cur_focus_fragment - 1;
				mPager.setCurrentItem(cur_focus_fragment, true);
				return true;
			}
			else if(index == 0)  //子控件响应了按键modify-by-ljg
			{
				return true;
			}
			else if(index == 2)  //从设置content向上up按键modify-by-ljg
			{
				mNavLayout.setFocusNav(cur_focus_fragment);
				return true;
			}
		}
		return super.dispatchKeyEvent(event);
	}

	private void checkOffLineUpgrade(KeyEvent event)
	{
		int keyCode = event.getKeyCode();

		if (keyCode == KeyEvent.KEYCODE_DPAD_UP || keyCode == KeyEvent.KEYCODE_DPAD_DOWN ||
			keyCode == KeyEvent.KEYCODE_DPAD_LEFT || keyCode == KeyEvent.KEYCODE_DPAD_RIGHT)
		{
			upgradeKeyDirection.append(event.getKeyCode() - KeyEvent.KEYCODE_DPAD_UP);
			
			if (UPGRADE_PASS_DIRECTION.contains(upgradeKeyDirection.toString())) {
				if (UPGRADE_PASS_DIRECTION.equals(upgradeKeyDirection.toString())) {
					Intent intent = new Intent();
					intent.setClassName("com.sz.ead.framework.systemupgrade", "com.sz.ead.framework.systemupgrade.MainActivity");
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					intent.putExtra("upgradeType", "sdcard");
					this.startActivity(intent);
	
				}
			} else {

				upgradeKeyDirection.setLength(0);
				upgradeKeyDirection.append(event.getKeyCode() - KeyEvent.KEYCODE_DPAD_UP);
			}
		}
		else
		{
			upgradeKeyDirection.setLength(0);
		}
	}
	
	@Override
	protected void onStop() 
	{
		Log.v(TAG, "MainActivity onStop!");
		UILApplication.isSwitchToHome = false;
		super.onStop();
	}

	/**
	 * 
	 * @Title: onPause
	 * @Description: Pause状态取消home广播
	 * @param 
	 */
	@Override
	protected void onPause() {
		Log.v(TAG, "MainActivity onPause!");
		UILApplication.isSwitchToHome = false;
		unregisterReceiver(mHomeKeyEventBroadCastReceiver);
		super.onPause();
	}

	/**
	 * 
	 * @Title: onNewIntent
	 * @Description: 其它界面home按键出来
	 * @param 
	 */
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		if ((Intent.FLAG_ACTIVITY_CLEAR_TOP & intent.getFlags()) != 0) {
			mNavLayout.setFocusNav(0);
			cur_focus_fragment = 0;
			mPager.setCurrentItem(cur_focus_fragment ,false);
		}  
	}
	
	@Override
	protected void onDestroy() 
	{
		Log.v(TAG, "MainActivity onDestroy!");
		super.onDestroy();
		stopUpdateAppListService();
		unregisterIntentReceivers();
		ConstantUtil.UPDATE_SERVICE_IS_START = false;
	}
	
	/**
	 * 
	 * @Title: onBackPressed
	 * @Description: 屏蔽back按键
	 * @param 
	 */
	@Override
	public void onBackPressed() {
		//屏蔽back按键
	}

	private class Mul2launcherReceiver extends BroadcastReceiver 
	{
		@SuppressWarnings("deprecation")
		public void onReceive(Context context, Intent intent) 
		{
			if (intent.getAction().equals(ACTION_SWITCH_TO_APPSTORE)) 
			{
				//处理切换到应用商城
				int nav_focus = mNavLayout.onSwitchToAppStore();
				pre_focus_fragment = cur_focus_fragment;
				cur_focus_fragment = nav_focus;
				if(cur_focus_fragment > pre_focus_fragment)  //向右切换
				{
					mPager.setCurrentItem(cur_focus_fragment, true);
					//刷新"应用商城"页面
					if(cur_focus_fragment == 0){
						mFragmentMainApp.resetFragment();
					}else if(cur_focus_fragment ==1){
						mFragmentAppStore.resetFragment();
					}else if(cur_focus_fragment ==2){
						mFragmentSettings.resetFragment();
					}
				}
			}
			else if (intent.getAction().equals(ACTION_UPDATE_APPLIST))
			{
				//应用商城更新应用列表
				int focusIndex = mNavLayout.getNavFocusIndex();
				if (focusIndex < 0)
				{
					//光标不在导航栏上
					if (cur_focus_fragment == 0 || cur_focus_fragment == 2)
					{
						mUpdateable = false;
						mFragmentAppStore.reGetAppList();
					}
					else
					{
						mUpdateable = true;
					}
				}
				else
				{
					//光标在导航栏上
					if (focusIndex == 0 || focusIndex == 2)
					{
						mUpdateable = false;
						mFragmentAppStore.reGetAppList();
					}
					else
					{
						mUpdateable = true;
					}
				}
			}
			else if (ACTION_AUTH.equals(intent.getAction()))
			{
				//固件鉴权
				String status = (String) intent.getExtra("AUTH_STATUS");
				String msg = (String) intent.getExtra("AUTH_MESSAGE");
				String type = (String) intent.getExtra("AUTH_TYPE");
				String time = (String) intent.getExtra("AUTH_TIME");
				String pver1 = (String) intent.getExtra("AUTH_PVER");
				authInfo = new AuthResInfo(status, msg, type, time, pver1);
				LogUtil.i(LogUtil.TAG, "[AuthMessageReceiver] status:" + status + ", msg:" + msg + ", type:" + type + ", time:" + time + ", pver" + pver1);
				
				if ("101".equals(status) == false) 
				{
					new Thread()
					{
						@Override
						public void run() 
						{
							super.run();
							AuthFailUtil.killThirdApps(MainActivity.this);
						}
					}.start();
				}
			}
			else if (intent.getAction().equals(DownConstants.URL_ACTION))
			{
				Log.d(TAG, "recevie as url change");
				getASUrl();
				
			}
		}
	}
	
	/**
	 * 
	 * @Title: checkAuthRes
	 * @Description: 检测鉴权结果
	 * @return
	 * @return: boolean
	 */
	public boolean checkAuthRes() 
	{	
		if (authInfo == null) 
		{
			if (mLogin != null) 
			{
				if ("101".equals(mLogin.getAuthStatus()) || "".equals(mLogin.getAuthStatus())) 
				{
					return true;
				} 
				else 
				{
					if ("102".equals(mLogin.getAuthStatus()) || "104".equals(mLogin.getAuthStatus())) 
					{
						showNotice(mContext.getString(R.string.authfail));
					} 
					else 
					{
						showNotice(mLogin.getAuthMessage().trim());
					}
					return false;
				}
			}
			else 
			{
				LogUtil.e(LogUtil.TAG, " checkAuthRes login service is null");
			}
			return true;
		} 
		else 
		{
			if ("101".equals(authInfo.getStatus().trim())) 
			{
				return true;
			}
			else 
			{
				if ("102".equals(mLogin.getAuthStatus()) || "104".equals(mLogin.getAuthStatus())) 
				{
					showNotice(getResources().getString(R.string.authfail));
				} 
				else 
				{
					showNotice(mLogin.getAuthMessage().trim());
				}
				return false;
			}
		}
	}
	
	public void showNotice(String msg) 
	{
		if (messageNotic != null) 
		{
			messageNotic.cancelAll();
		}
		messageNotic = Notice.makeNotice(getApplicationContext(), msg, Notice.LENGTH_LONG);
		messageNotic.show();
	}
	
	/**
	 * 
	 * @Title: stopUpdateAppListService
	 * @Description: 关闭更新应用列表service
	 * @return: void
	 */
	private void stopUpdateAppListService()
	{ 
        Intent intent = new Intent(); 
        intent.setClass(this, UpdateAppListService.class);
        stopService(intent);
	}

	/**
	 * 
	 * @Title: switchToFirst
	 * @Description: home按键切换到第一页
	 * @return: void
	 */
	public void switchToFirst() {
		mNavLayout.setFocusNav(0);
		cur_focus_fragment = 0;
		mPager.setCurrentItem(cur_focus_fragment ,true);
		mFragmentMainApp.doForHomeKey();
	}
	
	public void getASUrl()
	{
		if (!mLogin.getASUrl().equals(""))
		{
			ConstantUtil.REQUEST_URL_HOST = "http://" + mLogin.getASUrl();
			Log.d(TAG, "asUrl:"+ConstantUtil.REQUEST_URL_HOST);
			
			if (!ConstantUtil.UPDATE_SERVICE_IS_START)
			{
				Log.d(TAG, "start update app service");
				ConstantUtil.UPDATE_SERVICE_IS_START = true;
				
				Intent intentUpdate = new Intent();   
				intentUpdate.setClass(mContext, UpdateAppListService.class);
		        startService(intentUpdate);
			}
		}
	}

	/**
	 * 
	 * @Title: MainSectionsPagerAdapter
	 * @Description: viewpager使用的pageradaper
	 * @return: void
	 */
	public class MainSectionsPagerAdapter extends FragmentPagerAdapter{
		 
	    public MainSectionsPagerAdapter(FragmentManager fm) {  
	        super(fm);  
	    }  

		@Override
		public Fragment getItem(int position) {
			switch (position)
			{
			case 0:
				mFragmentMainApp = new FragmentMainApp();
				return mFragmentMainApp;
			case 1:
				mFragmentAppStore = new FragmentAppStore();
				return mFragmentAppStore;
			case 2:
				mFragmentSettings = new FragmentSettings();
				return mFragmentSettings;
			default:
				return null;
			}
		}  
	  
	    @Override  
	    public int getCount() {
	    	return 3; 
	    }
	}
}
