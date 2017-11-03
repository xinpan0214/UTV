/**
 * 
 * Copyright © 2014GreatVision. All rights reserved.
 *
 * @Title: UpgradeActivity.java
 * @Prject: BananaTvSetting
 * @Description: 系统升级界面
 * @author: lijungang 
 * @date: 2014-1-24 下午1:49:06
 */
package com.sz.ead.framework.mul2launcher.settings.upgrade;

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
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.sz.ead.framework.mul2launcher.BaseActivity;
import com.sz.ead.framework.mul2launcher.MainActivity;
import com.sz.ead.framework.mul2launcher.R;
import com.sz.ead.framework.mul2launcher.settings.util.ConstUtil;
import com.sz.ead.framework.mul2launcher.status.Statusbar;
import com.sz.ead.framework.upgrademanager.IUpgradeBgAIDL;
import com.sz.ead.framework.upgrademanager.IUpgradeBgCallback;
import com.szgvtv.ead.framework.bi.Bi;

public class UpgradeActivity extends BaseActivity implements OnClickListener{

	private Button setting_upgrade_button;
	private TextView setting_upgrade_version;
	private String cur_version;
	private Statusbar mStatus_Bar;
	private SettingManager settingManager;
	
	private IUpgradeBgAIDL mService = null;
	private final int BIND_SUCCESS = 0 ;//绑定服务成功
	private int curStatus = -1;
	private boolean isOnStart = false;	
	private Notice mCheckNotice = null;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings_upgrade_activity);
		
		setting_upgrade_button = (Button) findViewById(R.id.setting_upgrade_button);
		setting_upgrade_version = (TextView)findViewById(R.id.setting_upgrade_version);
		mStatus_Bar = (Statusbar) findViewById(R.id.llyt_main_header);
		mStatus_Bar.setTaskBarTitle(this.getResources().getString(R.string.settings_menu_upgrade_online));
		
		setting_upgrade_button.setOnClickListener(this);
		settingManager = (SettingManager)getSystemService(Context.SETTING_SERVICE);
		initVersion();
		Intent mIntent = new Intent(ConstUtil.UPGRADE_SERVICE_NAME);
		bindService(mIntent, conn, BIND_AUTO_CREATE);
	}
	
	/**
	 * 
	 * @Title: Handler
	 * @Description: 防止阻塞发生，服务端回调到客户端ui线程上
	 * @return: void
	 */
	Handler mHandler = new Handler(){
		public void handleMessage(Message msg) {
			switch (msg.what) {	
			case BIND_SUCCESS:
				curStatus = BIND_SUCCESS;
				break;
			case UpgradeBgStatus.UP_STATUS_CHECK_UPGRADE_ERROR://网络链接失败
				if(isOnStart){
					if(mCheckNotice != null){
						mCheckNotice.cancel();
						mCheckNotice = null;
					}
					showUpgradeInfoDialog(0);
					isOnStart = false;
				}
				break;
			case UpgradeBgStatus.UP_STATUS_CHECK_LOWER_VERSION://当前版本为最新版本
				if(isOnStart){
					if(mCheckNotice != null){
						mCheckNotice.cancel();
						mCheckNotice = null;
					}
					showUpgradeInfoDialog(1);
					isOnStart = false;
				}
				break;
			case UpgradeBgStatus.UP_STATUS_CHECK_HIGER_VERSION://发现新版本
			case UpgradeBgStatus.UP_STATUS_START_DOWNLOAD://正在下载
			case UpgradeBgStatus.UP_STATUS_MD5_CHECK_SUCCESS://下载完成
				if(isOnStart){
					if(mCheckNotice != null){
						mCheckNotice.cancel();
						mCheckNotice = null;
					}
					startToCheckUpgradeActivity();
					isOnStart = false;
				}
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}
	};
	
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
			mHandler.sendEmptyMessage(UpgradeBgStatus.UP_STATUS_MD5_CHECK_SUCCESS);
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
	 * @Title: initVersion
	 * @Description: 初始换显示版本
	 * @return: void
	 */
	private void initVersion(){
		cur_version = settingManager.getSfVersion();
		UpgradeData.getInstance().setCur_version(cur_version);
		setting_upgrade_version.setText(getResources().getString(R.string.settings_upgrade_version) + " V" + cur_version);
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v == setting_upgrade_button)
		{
			Bi.sendBiMsg(Bi.BICT_FIRMWARE, Bi.BICC_CHECK_UPGRADE);
			if(curStatus == BIND_SUCCESS){
				if(mCheckNotice == null){
					mCheckNotice = Notice.makeNotice(this, getResources().getString(R.string.settings_upgrade_check_version), Notice.LENGTH_INFINITE
							, R.layout.settings_dialog_waiting, R.id.wait_message);
					mCheckNotice.cancelAll();
					mCheckNotice.show();
				}
				try {
					isOnStart = true;
					mService.startCheckVersion();
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else{
				showUpgradeInfoDialog(2);
			}
		}
	}
	
	/**
	 * 
	 * @Title: startToCheckUpgradeActivity
	 * @Description: 跳转详细信息界面
	 * @return: void
	 */
	public void startToCheckUpgradeActivity(){
		resetService();
		Intent intent_upgrade = new Intent();
		intent_upgrade.setClass(this, CheckUpgradeActivity.class);
		startActivityForResult(intent_upgrade, 0);
	}
	
	/**
	 * 
	 * @Title: onBackPressed
	 * @Description: 返回键处理
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
	 * @Title: showNoUpgradeInfo
	 * @Description: 下方消息框
	 * @param context
	 * @param flag
	 * @return: void
	 */
	public void showUpgradeInfoDialog(int flag){
		if(flag == 0)
		{
			Notice mNotice = Notice.makeNotice(this, getResources().getString(R.string.settings_upgrade_check_net_error), Notice.LENGTH_SHORT);
			mNotice.cancelAll();
			mNotice.setTextColor(getResources().getColor(R.color.settings_notice_red));
			mNotice.show();
		}else if(flag == 1){
			Notice mNotice = Notice.makeNotice(this, getResources().getString(R.string.settings_upgrade_no_upgrade_info), Notice.LENGTH_SHORT);
			mNotice.cancelAll();
			mNotice.show();
		}else if(flag == 2){
			Notice mNotice = Notice.makeNotice(this, getResources().getString(R.string.settings_upgrade_check_bind_error), Notice.LENGTH_SHORT);
			mNotice.cancelAll();
			mNotice.show();
		}
	}
	
	/**
	 * 
	 * @Title: resetService
	 * @Description: 重置客户端，状态置空，断开服务
	 * @return: void
	 */
	private void resetService(){
		isOnStart = false;
		curStatus = -1;
		try {
			if(mService != null)
			mService.unregisterCallback(mCallback);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		unbindService(conn);
	}
	
	/**
	 * 
	 * @Title: onBackPressed
	 * @Description: 返回键处理
	 * @return: void
	 */
	public void onBackPressed(){
		resetService();
		setResult(1);//下面一排在返回时需要刷新倒影
		finish();
	}
}
