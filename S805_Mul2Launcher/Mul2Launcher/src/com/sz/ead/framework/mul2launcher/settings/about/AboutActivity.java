package com.sz.ead.framework.mul2launcher.settings.about;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import com.sz.ead.framework.mul2launcher.BaseActivity;
import com.sz.ead.framework.mul2launcher.R;
import com.sz.ead.framework.mul2launcher.status.Statusbar;

import android.content.Context;
import android.login.loginservice.LoginManager;
import android.os.Build;
import android.os.Bundle;
import android.setting.settingservice.SettingManager;
import android.text.method.ScrollingMovementMethod;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class AboutActivity extends BaseActivity implements OnClickListener, OnFocusChangeListener {
	
	private RadioButton mSysInfo;		// 有线		
	private RadioButton mLegal;			//	 WI-FI
	
	private Statusbar mStatus_Bar;
	private RelativeLayout mRelSysInfo;
	private TextView mTvLegal;
	
	private TextView mDeviceName;
	private TextView mAndroidVersion;
	private TextView mSystemVersion;
	private TextView mHardwareVersion;
	private TextView mMacAddress;
	private TextView mFetchVersion;
	
	private SettingManager mSetting;
	private LoginManager mLogin;
	
	enum FOCUS
	{
		SYSTEMINFO,
		LEGAL,
		LEGAL_PAGE
	};
	
	private FOCUS m_focus;
	
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.settings_about_activity);
		
		mSetting = (SettingManager)getSystemService(Context.SETTING_SERVICE);
		mLogin = (LoginManager)getSystemService(Context.LOGIN_SERVICE);
		
		findViews();
	}

	private void findViews(){
		
		mStatus_Bar = (Statusbar) findViewById(R.id.llyt_main_header);
		mStatus_Bar.setTaskBarTitle(this.getResources().getString(R.string.settings_menu_about_title));
		
		mSysInfo = (RadioButton)findViewById(R.id.id_setting_about_sysinfo);
		mLegal = (RadioButton)findViewById(R.id.id_setting_about_legal);
		
		mRelSysInfo = (RelativeLayout)findViewById(R.id.settings_sysinfo_rllt);
		mTvLegal = (TextView)findViewById(R.id.settings_legal_text);
		mTvLegal.setMovementMethod(ScrollingMovementMethod.getInstance());
		mTvLegal.setText(getFileText(R.raw.license));
		mTvLegal.setOnKeyListener(new KeyListenerImpl());
		
		mDeviceName = (TextView)findViewById(R.id.settings_device_name);
		mAndroidVersion = (TextView)findViewById(R.id.settings_android_version);
		mSystemVersion = (TextView)findViewById(R.id.settings_system_version);
		mHardwareVersion = (TextView)findViewById(R.id.settings_hardware_version);
		mMacAddress = (TextView)findViewById(R.id.settings_mac_address);
		mFetchVersion = (TextView)findViewById(R.id.settings_fetch_version);
	
		setListeners();
		setMenuTextColor(1,true);
		systemInfoShow();
		m_focus = FOCUS.SYSTEMINFO;
		setText();
	}
	
	private class KeyListenerImpl implements OnKeyListener{
		
		@Override
        public boolean onKey(View view, int pos, KeyEvent keyEvent) {
            switch (keyEvent.getKeyCode()) {
            case KeyEvent.KEYCODE_DPAD_UP:
            	// 滑动到顶部
            	if (0 == view.getScrollY())
            	{
            		setMenuTextColor(2,true);
            		m_focus = FOCUS.LEGAL;
            		mLegal.requestFocus();
            	}
                break;
            default:
                break;
            }
            return false;
        }
    };
	
	private void setText()
	{
		mDeviceName.setText(this.getResources().getText(R.string.settings_about_device)+" "+mSetting.getDevName());
		mAndroidVersion.setText(this.getResources().getText(R.string.settings_about_andver)+" "+Build.VERSION.RELEASE);
		mSystemVersion.setText(this.getResources().getText(R.string.settings_about_softver)+" "+
				mSetting.getDevName()+"-"+mSetting.getSfVersion());
		
		
		String hwVersion = mSetting.getHwVersion();
		String[] showVersion = hwVersion.split("_");
		mHardwareVersion.setText(this.getResources().getText(R.string.settings_about_hdver)+" "+showVersion[0]);
		
		//mHardwareVersion.setText(this.getResources().getText(R.string.settings_about_hdver)+" "+mSetting.getHwVersion());
		mMacAddress.setText(this.getResources().getText(R.string.settings_about_mac)+" "+mSetting.getMac().toUpperCase());
		
		//mFetchVersion.setText(mLogin.getFetchVersion());
	}
	
	/**
	 * 设置监听
	 * @Title: setListeners
	 * @Description: TODO
	 * @return: void
	 */
	 public void setListeners() {
		mSysInfo.setOnClickListener(this);
		mLegal.setOnClickListener(this);
		
		mSysInfo.setOnFocusChangeListener(this);
		mLegal.setOnFocusChangeListener(this);
	}
	
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub

	}
	
	/**
	 * 设置菜单栏的字体颜色，1代表系统信息，2代表法律声明
	 * @Title: setMenuTextColor
	 * @Description: TODO
	 * @param index
	 * @return: void
	 */
	public void setMenuTextColor(int index, boolean isFocus)
	{
		if (1 == index)
		{
			this.mLegal.setTextSize(getResources().getDimension(R.dimen.size_28));
			this.mSysInfo.setSelected(false);
			this.mLegal.setSelected(false);
			if(isFocus){
				this.mSysInfo.setSelected(true);
				this.mSysInfo.setTextSize(getResources().getDimension(R.dimen.size_32));
			}else{
				this.mSysInfo.setTextSize(getResources().getDimension(R.dimen.size_32));
			}
		}
		else if (2 == index)
		{
			this.mSysInfo.setTextSize(getResources().getDimension(R.dimen.size_28));
			this.mSysInfo.setSelected(false);
			this.mLegal.setSelected(false);
			if(isFocus){
				this.mLegal.setSelected(true);
				this.mLegal.setTextSize(getResources().getDimension(R.dimen.size_32));
			}else{
				this.mLegal.setTextSize(getResources().getDimension(R.dimen.size_32));
			}
		}
	}

	
	/**
	 * 按键处理
	 */
	@Override
	public boolean dispatchKeyEvent(KeyEvent event)
	{
		if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT && event.getAction() == KeyEvent.ACTION_DOWN)
		{
			doKeyRight();
		}
		else if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT && event.getAction() == KeyEvent.ACTION_DOWN)
		{
			doKeyLeft();
		}
		else if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_UP && event.getAction() == KeyEvent.ACTION_DOWN)
		{
			doKeyUp(event);
		}
		else if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_DOWN && event.getAction() == KeyEvent.ACTION_DOWN)
		{
			doKeyDown(event);
		}
		else if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN)
		{
			finish();
		}
		
		return true;
	}
	
	/**
	 * 方向右键处理
	 * @Title: doKeyRight
	 * @Description: TODO
	 * @return: void
	 */
	public void doKeyRight()
	{
		if (m_focus == FOCUS.SYSTEMINFO)
		{
			this.mLegal.requestFocus();
			m_focus = FOCUS.LEGAL;
			this.setMenuTextColor(2,true);
			legalShow();
		}
		/*else if (m_focus == FOCUS.LEGAL)
		{
			this.m_tv_detection.requestFocus();
			m_focus = FOCUS.DETECTION;
			this.detectionActivityShow();
			this.setMenuTextColor(3,true);
			mLogin.setPageType(4);
		}*/
	}
	
	/**
	 * 方向左键处理
	 * @Title: doKeyLeft
	 * @Description: TODO
	 * @return: void
	 */
	public void doKeyLeft()
	{
		if (m_focus == FOCUS.LEGAL)
		{
			this.mSysInfo.requestFocus();
			m_focus = FOCUS.SYSTEMINFO; 
			
			this.setMenuTextColor(1,true);
			systemInfoShow();
		}
	}
	
	public void doKeyUp(KeyEvent event)
	{
		if (m_focus == FOCUS.LEGAL_PAGE)
		{
			mTvLegal.dispatchKeyEvent(event);
		}
	}
	
	public void doKeyDown(KeyEvent event)
	{
		if (m_focus == FOCUS.LEGAL)
		{
			mTvLegal.requestFocus();
			m_focus = FOCUS.LEGAL_PAGE;
		}
		else if (m_focus == FOCUS.LEGAL_PAGE)
		{
			mTvLegal.dispatchKeyEvent(event);
		}
	}
	
	 /*@Override
	 public void onWindowFocusChanged(boolean hasFocus) {
	  // TODO Auto-generated method stub
		if(m_focus == FOCUS.SYSTEMINFO){
			setMenuTextColor(1, true);
		}else if(m_focus == FOCUS.LEGAL){
			setMenuTextColor(2, true);
		}else if(m_focus == FOCUS.DETECTION){
			setMenuTextColor(3, true);
		}
		super.onWindowFocusChanged(hasFocus);
	 }*/
	
	/* (non-Javadoc)
	 * @see android.view.View.OnFocusChangeListener#onFocusChange(android.view.View, boolean)
	 */
	@Override
	public void onFocusChange(View arg0, boolean arg1) {
		// TODO Auto-generated method stub
		
	}
	
	// 显示系统信息界面
	private void systemInfoShow()
	{
		mRelSysInfo.setVisibility(View.VISIBLE);
		mTvLegal.setVisibility(View.GONE);
	}
	
	// 显示法律声明界面
	private void legalShow()
	{
		mRelSysInfo.setVisibility(View.GONE);
		mTvLegal.setVisibility(View.VISIBLE);
	}
	
	public String getFileText(int id) {
		StringBuffer sBuffer = new StringBuffer("");
		BufferedReader reader = null;
		try {
			InputStream inputStream = getResources().openRawResource(id);
			InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf8");
			reader = new BufferedReader(inputStreamReader);  
			String line;  
			while ((line = reader.readLine()) != null) {  
				sBuffer.append(line);  
				sBuffer.append("\n");
			}
		} catch (UnsupportedEncodingException e1) {  
			e1.printStackTrace();  
		} catch (IOException e) {  
			e.printStackTrace();  
		} finally {
			try {
				if (reader != null) {
					reader.close();
				} 
			} catch (IOException e) {
				e.printStackTrace();
			}
		} 
		return sBuffer.toString();
	}
}
