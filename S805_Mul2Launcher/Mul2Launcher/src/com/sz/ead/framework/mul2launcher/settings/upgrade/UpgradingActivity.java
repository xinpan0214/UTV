/**
 * 
 * Copyright © 2014GreatVision. All rights reserved.
 *
 * @Title: UpgradingActivity.java
 * @Prject: BananaTvSetting
 * @Description: 正在升级界面
 * @author: lijungang 
 * @date: 2014-1-24 下午1:53:15
 */
package com.sz.ead.framework.mul2launcher.settings.upgrade;

import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.notice.noticemanager.Notice;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.setting.settingservice.SettingManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
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

public class UpgradingActivity extends BaseActivity implements OnClickListener{

	private ProgressBar setting_upgrading_progress;
	private TextView setting_upgrading_progress_num;
	private TextView setting_upgrading_info;
	private Button setting_upgrading_button;
	private boolean upgrade_again;
	private SettingManager settingManager;
	private boolean canBackPress = false;
	private Statusbar mStatus_Bar;
	private IUpgradeBgAIDL mService;
	private final int BIND_SUCCESS = 0 ;//绑定服务成功
	private int curStatus = -1;
	private String UpgradePath = "";
	private Timer mTimer = null;
	
	public static boolean break_download_thread = false;
	private int progress_num = 0;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings_upgrading_activity);
		setupView();
		upgrade_again = false;
		Intent mIntent = new Intent(ConstUtil.UPGRADE_SERVICE_NAME);
		bindService(mIntent, conn, BIND_AUTO_CREATE);
	}
	/**
	 * 
	 * @Title: setupView
	 * @Description: findView
	 * @return: void
	 */
	private void setupView(){
		setting_upgrading_progress = (ProgressBar)findViewById(R.id.setting_upgrading_progress);
		setting_upgrading_progress_num = (TextView)findViewById(R.id.setting_upgrading_progress_num);
		setting_upgrading_info = (TextView)findViewById(R.id.setting_upgrading_info);
		setting_upgrading_button = (Button)findViewById(R.id.setting_upgrading_button);

		mStatus_Bar = (Statusbar) findViewById(R.id.llyt_main_header);
		mStatus_Bar.setTaskBarTitle(this.getResources().getString(R.string.settings_menu_upgrade_online));
		
		setting_upgrading_button.setVisibility(View.INVISIBLE);
		setting_upgrading_button.setOnClickListener(this);
		setting_upgrading_info.setText(getResources().getString(R.string.settings_upgrading_info));
		settingManager = (SettingManager)getSystemService(Context.SETTING_SERVICE);
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
		}

		@Override
		public void onDownloadProgress(int progress) throws RemoteException {
			// TODO Auto-generated method stub
			progress_num = progress;
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
			mHandler.sendEmptyMessage(UpgradeBgStatus.UP_STATUS_DOWNLOADING_FAILED);
		}

		@Override
		public void onDownloadingTimeout(String upgrade_version) throws RemoteException {
			// TODO Auto-generated method stub
			UpgradeData.getInstance().setUpgrade_version(upgrade_version);
			mHandler.sendEmptyMessage(UpgradeBgStatus.UP_STATUS_DOWNLOADING_TIMEOUT);
		}

		@Override
		public void onCheckMd5Failed(String upgrade_version) throws RemoteException {
			// TODO Auto-generated method stub
			UpgradeData.getInstance().setUpgrade_version(upgrade_version);
			mHandler.sendEmptyMessage(UpgradeBgStatus.UP_STATUS_MD5_CHECK_FAILED);
		}
	};
	
	/**
	 * 
	 * @Title: Handler
	 * @Description: 防止阻塞发生，服务端回调到客户端ui线程上
	 * @return: void
	 */
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {	
			case BIND_SUCCESS:
				curStatus = BIND_SUCCESS;
				break;
			case UpgradeBgStatus.UP_STATUS_START_DOWNLOAD:
				setting_upgrading_info.setText(getResources().getString(R.string.settings_upgrading_info));
				setting_upgrading_progress.setProgress(progress_num);
				setting_upgrading_progress_num.setText(Integer.toString(progress_num) + "%");
				setting_upgrading_button.setVisibility(View.INVISIBLE);
				upgrade_again = false;
				canBackPress = false;
				break;
			case UpgradeBgStatus.UP_STATUS_MD5_CHECK_SUCCESS:
				Log.v(ConstUtil.Tag, "start upgrade!!!");
				Bi.sendBiMsg(Bi.BICT_FIRMWARE, Bi.BICC_SYSTEM_UPDATE, UpgradeData.getInstance().getUpgrade_version(), "1", "0", "", settingManager.getProperty(UpgradeBgStatus.UPGRADE_TIME_COUNT));
				FileUtils.createDir("/cache/isupgrade");
				FileUtils.chmodFile("777", "/cache/isupgrade");
				UpgradePath = (String)msg.obj;
				showRebootDialog();
				break;
			case UpgradeBgStatus.UP_STATUS_DOWNLOADING_FAILED:
				Bi.sendBiMsg(Bi.BICT_FIRMWARE, Bi.BICC_SYSTEM_UPDATE, UpgradeData.getInstance().getUpgrade_version(), "1", "1", "下载失败", settingManager.getProperty(UpgradeBgStatus.UPGRADE_TIME_COUNT));
				upgrade_again = true;
				canBackPress = true;
				setting_upgrading_info.setText("");
				setting_upgrading_button.setVisibility(View.VISIBLE);
				showUpgradingDialog(1);
				break;
			case UpgradeBgStatus.UP_STATUS_DOWNLOADING_TIMEOUT:
				Bi.sendBiMsg(Bi.BICT_FIRMWARE, Bi.BICC_SYSTEM_UPDATE, UpgradeData.getInstance().getUpgrade_version(), "1", "1", "网络链接超时", settingManager.getProperty(UpgradeBgStatus.UPGRADE_TIME_COUNT));
				upgrade_again = true;
				canBackPress = true;
				setting_upgrading_info.setText("");
				setting_upgrading_button.setVisibility(View.VISIBLE);
				showUpgradingDialog(1);
				break;
			case UpgradeBgStatus.UP_STATUS_MD5_CHECK_FAILED:
				Bi.sendBiMsg(Bi.BICT_FIRMWARE, Bi.BICC_SYSTEM_UPDATE, UpgradeData.getInstance().getUpgrade_version(), "1", "1", "md5校验失败", settingManager.getProperty(UpgradeBgStatus.UPGRADE_TIME_COUNT));
				upgrade_again = true;
				canBackPress = true;
				setting_upgrading_info.setText("");
				setting_upgrading_button.setVisibility(View.VISIBLE);
				showUpgradingDialog(1);
				break;
			case UpgradeBgStatus.UP_STATUS_IME_UPGRADE:
				settingManager.startUpgrade(UpgradePath);
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}
	};
	
	/**
	 * 
	 * @Title: onBackPressed
	 * @Description: 升级失败返回键处理
	 * @return: void
	 */
	public void onBackPressed(){
		if(canBackPress){
	      	if(mTimer != null){
	      		mTimer.cancel(); 
	      		mTimer = null;
	    	}
			curStatus = -1;
			try {
				mService.unregisterCallback(mCallback);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			unbindService(conn);
			finish();
		}
	}
	
	protected void onResume() {
		super.onResume();
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v == setting_upgrading_button)
		{
			if(upgrade_again)
			{
				if(curStatus == BIND_SUCCESS){
					try {
						mService.startDownload();
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}else{
					showUpgradingDialog(0);
				}
			}
		}
	}
	
	/**
	 * 
	 * @Title: showRebootDialog
	 * @Description: 升级对话框，因升级接口关掉了各系统服务，导致弹框不出现，所以停留2s再调用接口
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
	 * @Description: 定时2s调用升级接口
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
	
	/**
	 * 
	 * @Title: showUpgradingDialog
	 * @Description: 弹框处理
	 * @return: void
	 */
	private void showUpgradingDialog(int flag){
		if(flag == 0){
			Notice mNotice = Notice.makeNotice(this, getResources().getString(R.string.settings_upgrade_check_bind_error), Notice.LENGTH_SHORT);
			mNotice.cancelAll();
			mNotice.setTextColor(getResources().getColor(R.color.settings_notice_red));
			mNotice.show();
		}else if(flag == 1){
			Notice mNotice = Notice.makeNotice(this, getResources().getString(R.string.settings_upgrade_again_upgrade), Notice.LENGTH_SHORT);
			mNotice.cancelAll();
			mNotice.setTextColor(getResources().getColor(R.color.settings_notice_red));
			mNotice.show();
		}else if(flag == 2){
			Notice mNotice = Notice.makeNotice(this, getResources().getString(R.string.settings_upgrade_reboot), Notice.LENGTH_SHORT);
			mNotice.cancelAll();
			mNotice.show();
		}
	}
}
