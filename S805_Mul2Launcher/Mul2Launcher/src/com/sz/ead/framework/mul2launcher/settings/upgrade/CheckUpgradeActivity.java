/**
 * 
 * Copyright © 2014GreatVision. All rights reserved.
 *
 * @Title: CheckUpgradeActivity.java
 * @Prject: BananaTvSetting
 * @Description: 检测在线升级界面
 * @author: lijungang 
 * @date: 2014-1-24 下午1:46:41
 */
package com.sz.ead.framework.mul2launcher.settings.upgrade;


import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONObject;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Rect;
import android.notice.noticemanager.Notice;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.setting.settingservice.SettingManager;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.sz.ead.framework.mul2launcher.BaseActivity;
import com.sz.ead.framework.mul2launcher.R;
import com.sz.ead.framework.mul2launcher.settings.util.CommonUtils;
import com.sz.ead.framework.mul2launcher.settings.util.ConstUtil;
import com.sz.ead.framework.mul2launcher.settings.util.FileUtils;
import com.sz.ead.framework.mul2launcher.status.Statusbar;
import com.sz.ead.framework.upgrademanager.IUpgradeBgAIDL;
import com.sz.ead.framework.upgrademanager.IUpgradeBgCallback;
import com.szgvtv.ead.framework.bi.Bi;

public class CheckUpgradeActivity extends BaseActivity implements OnClickListener{
	
	private Button setting_upgrade_ime_button;
	private TextView setting_upgrade_ime_version;
	private TextView setting_upgrade_ime_info;
	private Statusbar mStatus_Bar;
	private ScreenControlView indicator;
	private ImageView setting_upgrade_right_arrow;
	private ImageView setting_upgrade_left_arrow;
	private ArrayList<String> mUpgradeMsgList;
	private ArrayList<String> mUpgradeMsgListForDisplay;
	private int mTotalDisplayMsgNum = 0;
	private int mTotalPageNum = 0;
	private int mCurPageNum = 0;
	private PullUpgradeParser mParser;
	private SettingManager settingManager;
	private IUpgradeBgAIDL mService;
	private final int BIND_SUCCESS = 0 ;//绑定服务成功
	private int curStatus = -1;
	private boolean isOnStart = false;
	private String UpgradePath = "";
	private Timer mTimer = null;
	private boolean canBackPress = true;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings_upgrade_ime_activity);
		settingManager = (SettingManager)getSystemService(Context.SETTING_SERVICE);
		
		setting_upgrade_ime_button = (Button)findViewById(R.id.setting_upgrade_ime_button);
		setting_upgrade_ime_version = (TextView)findViewById(R.id.setting_upgrade_ime_version);
		setting_upgrade_ime_info = (TextView)findViewById(R.id.setting_upgrade_ime_info);
		indicator = (ScreenControlView) findViewById(R.id.idScreenControl);
		setting_upgrade_right_arrow = (ImageView)findViewById(R.id.setting_upgrade_right_arrow);
		setting_upgrade_left_arrow = (ImageView)findViewById(R.id.setting_upgrade_left_arrow);
		mStatus_Bar = (Statusbar) findViewById(R.id.llyt_main_header);
		mStatus_Bar.setTaskBarTitle(this.getResources().getString(R.string.settings_menu_upgrade_online));
		Intent mIntent = new Intent(ConstUtil.UPGRADE_SERVICE_NAME);
		bindService(mIntent, conn, BIND_AUTO_CREATE);
		
		initFileData("/cache/checkUpgrade.xml");
		setting_upgrade_ime_version.setText(getResources().getString(R.string.settings_upgrade_ime_version) + "V" + UpgradeRevData.getInstance().getUpgrade_versionmsg_version());
		setting_upgrade_ime_button.setOnClickListener(this);
		initTextView();
		initPointArrowView();
		showPageText();
	}
	
	/**
	 * 
	 * @Title: ServiceConnection
	 * @Description: 客户端绑定升级服务
	 * @return: void
	 */
	private ServiceConnection conn = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			// TODO Auto-generated method stub
			mService = IUpgradeBgAIDL.Stub.asInterface(service);
			try {
				mService.registerCallback(mCallback);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			mHandler.sendEmptyMessage(BIND_SUCCESS);
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			// TODO Auto-generated method stub
			mService = null;
		}
	};
	
	/**
	 * 
	 * @Title: IUpgradeBgCallback
	 * @Description: 服务端回调接口
	 * @return: void
	 */
	private IUpgradeBgCallback mCallback = new IUpgradeBgCallback.Stub() {

		@Override
		public void onCheckVersionCallback(int ret) throws RemoteException {
			// TODO Auto-generated method stub
			mHandler.sendEmptyMessage(ret);
		}

		@Override
		public void onDownloadProgress(int progress) throws RemoteException {
			// TODO Auto-generated method stub
			mHandler.sendEmptyMessage(UpgradeBgStatus.UP_STATUS_START_DOWNLOAD);
		}

		@Override
		public void onDownloadComplete(String filePath, String upgrade_version) throws RemoteException {
			// TODO Auto-generated method stub
			UpgradeData.getInstance().setUpgrade_version(upgrade_version);
			mHandler.sendMessage(CommonUtils.newMessage(UpgradeBgStatus.UP_STATUS_MD5_CHECK_SUCCESS, filePath));
		}

		@Override
		public void onDownloadFailed(String upgrade_version) throws RemoteException {
			// TODO Auto-generated method stub
			UpgradeData.getInstance().setUpgrade_version(upgrade_version);
		}

		@Override
		public void onDownloadingTimeout(String upgrade_version) throws RemoteException {
			// TODO Auto-generated method stub
			UpgradeData.getInstance().setUpgrade_version(upgrade_version);
		}

		@Override
		public void onCheckMd5Failed(String upgrade_version) throws RemoteException {
			// TODO Auto-generated method stub
			UpgradeData.getInstance().setUpgrade_version(upgrade_version);
		}
	};
	
	/**
	 * 
	 * @Title: initFileData
	 * @Description: 解析指定路径下的新版本信息
	 * @return: void
	 */
	private void initFileData(String filePath){
		File file = new File(filePath);
    	mParser = new PullUpgradeParser();
		if(file.exists()){
	        try {
				InputStream in = new FileInputStream(file);
	        	mParser.parse(in);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 
	 * @Title: initTextView
	 * @Description: 检测升级消息格式是否合法
	 * @return: void
	 */
	private void initTextView(){
		//UpgradeRevData.getInstance().setUpgrade_versionmsg_desc_lang("1、图片加加载速\n2、UI风格提升。\n3、泰捷TV store新增推荐、管理功能。\n4、图片加加载速度提升。\n5、UI风格提升。\n6、泰捷TV store新增推荐、管理功能。");
		mUpgradeMsgList = new ArrayList<String>();
		mUpgradeMsgList.clear();
		jsonPaser();
		if(null == UpgradeRevData.getInstance().getUpgrade_versionmsg_desc_lang() || UpgradeRevData.getInstance().getUpgrade_versionmsg_desc_lang().isEmpty()){
			prepareDspList();
		}else{
			String[] msg = UpgradeRevData.getInstance().getUpgrade_versionmsg_desc_lang().split("\n");
		    for (int i = 0 ; i <msg.length ; i++ ) {
		    	mUpgradeMsgList.add(i, msg[i]); 
		    }
		    prepareDspList();
		}
	}
	
	/**
	 * 
	 * @Title: jsonPaser
	 * @Description: json解析lang对应的msg
	 * @return: void
	 */
	private void jsonPaser(){
		JSONObject jsonParams;
		String lang = "";
		lang = settingManager.getCurLang();
		try {
			jsonParams = new JSONObject(UpgradeRevData.getInstance().getUpgrade_versionmsg_desc());
			if (lang.equals("zh_CN")) {
				UpgradeRevData.getInstance().setUpgrade_versionmsg_desc_lang(jsonParams.getString("zh_CN"));
			}else if(lang.equals("zh_TW")) {
				UpgradeRevData.getInstance().setUpgrade_versionmsg_desc_lang(jsonParams.getString("zh_TW"));
			}else if(lang.equals("en")) {
				UpgradeRevData.getInstance().setUpgrade_versionmsg_desc_lang(jsonParams.getString("en"));
			}else if(lang.equals("ja")){
				UpgradeRevData.getInstance().setUpgrade_versionmsg_desc_lang(jsonParams.getString("ja"));
			}else if(lang.equals("ko")){
				UpgradeRevData.getInstance().setUpgrade_versionmsg_desc_lang(jsonParams.getString("ko"));
			}else{
				UpgradeRevData.getInstance().setUpgrade_versionmsg_desc_lang(jsonParams.getString("en"));
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	/**
	 * 
	 * @Title: prepareDspList
	 * @Description: 为换行正确显示信息做预处理，本函数主要处理换行功能
	 * @return: void
	 */
	private void prepareDspList(){
		mUpgradeMsgListForDisplay = new ArrayList<String>();
		mUpgradeMsgListForDisplay.clear();
		
		int contentLength = 0;
		contentLength = 640;
		int j = 0;
		for(int i=0; i< mUpgradeMsgList.size(); i++){
			Rect bounds = new Rect();
			TextPaint paint;
			paint = setting_upgrade_ime_info.getPaint();
			paint.getTextBounds(mUpgradeMsgList.get(i), 0, mUpgradeMsgList.get(i).length(), bounds);
			int width = bounds.width();
			if(width > contentLength){
				StringBuffer sb = new StringBuffer(mUpgradeMsgList.get(i).toString()); 
				int k = 0;
				while(k < mUpgradeMsgList.get(i).length()){
					String ellipsizeStr = (String) TextUtils.ellipsize(sb.substring(k), (TextPaint) paint, contentLength, TextUtils.TruncateAt.END);
					if(ellipsizeStr.endsWith("…"))
					{
						mUpgradeMsgListForDisplay.add(j, ellipsizeStr.substring(0, ellipsizeStr.length() - 1));
						j = j+1;
						k = k + ellipsizeStr.length() - 1;
					}else if(ellipsizeStr.endsWith("...")){
						mUpgradeMsgListForDisplay.add(j, ellipsizeStr.substring(0, ellipsizeStr.length() - 3));
						j = j+1;
						k = k + ellipsizeStr.length() - 3;
					}else{
						mUpgradeMsgListForDisplay.add(j, ellipsizeStr.substring(0, ellipsizeStr.length()));
						j = j+1;
						k = k + ellipsizeStr.length();
					}
				}
			}else{
				mUpgradeMsgListForDisplay.add(j, mUpgradeMsgList.get(i));
				j = j +1;
			}
		}
		mTotalDisplayMsgNum = mUpgradeMsgListForDisplay.size();
		if(mTotalDisplayMsgNum%5 == 0){
			mTotalPageNum = mTotalDisplayMsgNum/5;
		}else{
			mTotalPageNum = mTotalDisplayMsgNum/5 + 1;
		}
		if(mTotalPageNum > 5) mTotalPageNum = 5;
	}
	
	/**
	 * 
	 * @Title: initPointArrowView
	 * @Description: 初始化箭头显示
	 * @return: void
	 */
	private void initPointArrowView(){
		if(mTotalPageNum == 0 || mTotalPageNum == 1){
			indicator.setVisibility(View.GONE);
			setting_upgrade_right_arrow.setVisibility(View.INVISIBLE);
			setting_upgrade_left_arrow.setVisibility(View.INVISIBLE);
		}else{
			indicator.setTotolScreens(mTotalPageNum);
			indicator.setVisibility(View.VISIBLE);
			indicator.setCurrentScreen(0);
			setting_upgrade_right_arrow.setVisibility(View.VISIBLE);
			setting_upgrade_left_arrow.setVisibility(View.INVISIBLE);
		}
	}
	
	/**
	 * 
	 * @Title: showPageText
	 * @Description: 显示下一页版本信息
	 * @return: void
	 */
	private void showPageText(){
		String text = "";
		if(mTotalPageNum == 0 || mTotalPageNum == 1){
			setting_upgrade_ime_info.setText(UpgradeRevData.getInstance().getUpgrade_versionmsg_desc_lang());
		}else{
			if(mCurPageNum + 1 < mTotalPageNum ){
				text = mUpgradeMsgListForDisplay.get(mCurPageNum *5) + "\n" + 
						mUpgradeMsgListForDisplay.get(mCurPageNum *5 + 1) + "\n"+ 
						mUpgradeMsgListForDisplay.get(mCurPageNum *5 + 2) + "\n"+ 
						mUpgradeMsgListForDisplay.get(mCurPageNum *5 + 3) + "\n"+ 
						mUpgradeMsgListForDisplay.get(mCurPageNum *5 + 4);
			}else{
				for(int m = mCurPageNum*5; m<mUpgradeMsgListForDisplay.size(); m++ ){
					text = text + mUpgradeMsgListForDisplay.get(m) + "\n";
				}
			}
			setting_upgrade_ime_info.setText(text);
		}
	}
	
	/**
	 * 
	 * @Title: showPointArrowView
	 * @Description: 显示新的箭头
	 * @return: void
	 */
	private void showPointArrowView(){
		if(mTotalPageNum == 0 || mTotalPageNum == 1){
			indicator.setVisibility(View.GONE);
			setting_upgrade_right_arrow.setVisibility(View.INVISIBLE);
			setting_upgrade_left_arrow.setVisibility(View.INVISIBLE);
		}else{
			indicator.setCurrentScreen(mCurPageNum);
			if(mCurPageNum == 0){
				setting_upgrade_right_arrow.setVisibility(View.VISIBLE);
				setting_upgrade_left_arrow.setVisibility(View.INVISIBLE);
			}else if((mCurPageNum + 1) == mTotalPageNum){
				setting_upgrade_right_arrow.setVisibility(View.INVISIBLE);
				setting_upgrade_left_arrow.setVisibility(View.VISIBLE);
			}else{
				setting_upgrade_right_arrow.setVisibility(View.VISIBLE);
				setting_upgrade_left_arrow.setVisibility(View.VISIBLE);
			}
		}
	}
	
	/**
	 * 
	 * @Title: Handler
	 * @Description: 防止阻塞发生，服务端回调到客户端ui线程上
	 * @return: void
	 */
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage (Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case BIND_SUCCESS:
				curStatus = BIND_SUCCESS;
				break;
			case UpgradeBgStatus.UP_STATUS_START_DOWNLOAD://开始下载
				if(isOnStart){
					startToUpgradingActivity();
					isOnStart = false;
				}
				break;
			case UpgradeBgStatus.UP_STATUS_FREE_SIZE_ERROR://空间不足
				if(isOnStart){
					Bi.sendBiMsg(Bi.BICT_FIRMWARE, Bi.BICC_SYSTEM_UPDATE, UpgradeData.getInstance().getUpgrade_version(), "1", "1", "空间不足", settingManager.getProperty(UpgradeBgStatus.UPGRADE_TIME_COUNT));
					showCheckUpgradeDialog(2);
					isOnStart = false;
				}
				break;
			case UpgradeBgStatus.UP_STATUS_MD5_CHECK_SUCCESS://下载完成
				if(isOnStart){
					Log.v(ConstUtil.Tag, "start upgrade!!!  path:" + (String)msg.obj);
					Bi.sendBiMsg(Bi.BICT_FIRMWARE, Bi.BICC_SYSTEM_UPDATE, UpgradeData.getInstance().getUpgrade_version(), "1", "0", "", settingManager.getProperty(UpgradeBgStatus.UPGRADE_TIME_COUNT));
					FileUtils.createDir("/cache/isupgrade");
					FileUtils.chmodFile("777", "/cache/isupgrade");
					canBackPress = false;
					UpgradePath = (String)msg.obj;
					showRebootDialog();
					isOnStart = false;
				}
				break;
			case UpgradeBgStatus.UP_STATUS_IME_UPGRADE:
				settingManager.startUpgrade(UpgradePath);
				break;
			default:
				break;
			}
		}
	};
	
	/**
	 * 
	 * @Title: startToUpgradingActivity
	 * @Description: 启动升级进度条界面跳转
	 * @return: void
	 */
	public void startToUpgradingActivity(){
		resetService();
		Intent intent_upgrade = new Intent();
		intent_upgrade.setClass(CheckUpgradeActivity.this, UpgradingActivity.class);
		startActivityForResult(intent_upgrade, 0);
	}
	
	/**
	 * 
	 * @Title: resetService
	 * @Description: 重置客户端与服务端链接
	 * @return: void
	 */
	private void resetService(){
		isOnStart = false;
		curStatus = -1;
		try {
			mService.unregisterCallback(mCallback);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		unbindService(conn);
	}
	
	/**
	 * 
	 * @Title: onActivityResult
	 * @Description: 下一个接界面返回处理函数
	 * @return: void
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		Intent mIntent = new Intent(ConstUtil.UPGRADE_SERVICE_NAME);
		bindService(mIntent, conn, BIND_AUTO_CREATE);
	}
	
	/**
	 * 
	 * @Title: showCheckUpgradeDialog
	 * @Description: 弹框消息处理
	 * @return: void
	 */
	public void showCheckUpgradeDialog(int flag){
		if(flag == 0){
			Notice mNotice = Notice.makeNotice(this, getResources().getString(R.string.settings_upgrade_check_bind_error), Notice.LENGTH_SHORT);
			mNotice.cancelAll();
			mNotice.setTextColor(getResources().getColor(R.color.settings_notice_red));
			mNotice.show();
		}else if(flag == 2){
			Notice mNotice = Notice.makeNotice(this, getResources().getString(R.string.settings_upgrade_cache_low_on_space), Notice.LENGTH_SHORT);
			mNotice.cancelAll();
			mNotice.setTextColor(getResources().getColor(R.color.settings_notice_red));
			mNotice.show();
		}else if(flag == 1){
			Notice mNotice = Notice.makeNotice(this, getResources().getString(R.string.settings_upgrade_reboot), Notice.LENGTH_SHORT);
			mNotice.cancelAll();
			mNotice.show();
		}
	}
	
	/**
	 * 
	 * @Title: showRebootDialog
	 * @Description: 因升级接口直接调用会关掉系统服务，导致弹框不出现，故在弹框2s后再调用升级接口
	 * @return: void
	 */
	public void showRebootDialog(){
		Notice mNotice = Notice.makeNotice(this, getResources().getString(R.string.settings_upgrade_reboot), Notice.LENGTH_SHORT);
		mNotice.cancelAll();
		mNotice.show();
		startUpgradeTimerTask();
	}
	
	private void startUpgradeTimerTask(){
      	if(mTimer != null){
      		mTimer.cancel(); 
      		mTimer = null;
    	}
      	
		if(mTimer == null){
			mTimer = new Timer(); 
			mTimer.schedule(new UpgradeTask(), 2000, 100000);
		}
	}
	
	/**
	 * 
	 * @Title: UpgradeTask
	 * @Description: 定时2s后处理升级
	 * @return: void
	 */
	class UpgradeTask extends TimerTask{
		@Override
		public void run() {
		// TODO Auto-generated method stub
			try {
				mHandler.sendEmptyMessage(UpgradeBgStatus.UP_STATUS_IME_UPGRADE);
		      	if(mTimer != null){
		      		mTimer.cancel(); 
		      		mTimer = null;
		    	}
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
	}
	
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v == setting_upgrade_ime_button)
		{
			if(curStatus == BIND_SUCCESS){
				isOnStart = true;
				try {
					mService.startDownload();
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else{
				showCheckUpgradeDialog(0);
			}
		}
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		switch(keyCode)
		{
			case KeyEvent.KEYCODE_DPAD_RIGHT:
			{
				doKeyRight();
				break;
			}
			case KeyEvent.KEYCODE_DPAD_LEFT:
			{
				doKeyLeft();
				break;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * 
	 * @Title: doKeyLeft
	 * @Description: 左键处理函数
	 * @return: void
	 */
	private void doKeyLeft(){
		mCurPageNum = mCurPageNum -1;
		if(mCurPageNum == -1)
		{
			mCurPageNum = mCurPageNum +1;
			return;
		}
		showPageText();
		showPointArrowView();
	}
	
	/**
	 * 
	 * @Title: doKeyRight
	 * @Description: 右键处理函数
	 * @return: void
	 */
	private void doKeyRight(){
		mCurPageNum = mCurPageNum + 1;
		if(mCurPageNum == mTotalPageNum)
		{
			mCurPageNum = mCurPageNum -1;
			return;
		}
		showPageText();
		showPointArrowView();
	}
	
	/**
	 * 
	 * @Title: onBackPressed
	 * @Description: 返回键处理函数
	 * @return: void
	 */
	public void onBackPressed(){
		if(canBackPress){
		  	if(mTimer != null){
		  		mTimer.cancel(); 
		  		mTimer = null;
			}
		  	resetService();
			Intent intent_lang = new Intent();
			intent_lang.setClass(CheckUpgradeActivity.this, UpgradeActivity.class);
			setResult(0, intent_lang);
			finish();
		}
	}
}
