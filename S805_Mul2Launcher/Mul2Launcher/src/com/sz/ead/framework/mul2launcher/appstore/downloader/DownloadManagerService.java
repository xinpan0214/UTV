package com.sz.ead.framework.mul2launcher.appstore.downloader;


import java.io.BufferedInputStream;
import java.io.File;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import javax.net.ssl.HttpsURLConnection;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.DialogInterface.OnKeyListener;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.login.loginservice.LoginManager;
import android.net.Uri;
import android.notice.noticemanager.Notice;
import android.os.Binder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;

import com.sz.ead.framework.mul2launcher.R;
import com.sz.ead.framework.mul2launcher.appstore.common.AppInfo;
import com.sz.ead.framework.mul2launcher.appstore.common.InstalledAppsScan;
import com.sz.ead.framework.mul2launcher.appstore.dialog.AppInfoConstant;
import com.sz.ead.framework.mul2launcher.appstore.dialog.DialogPrompt;
import com.sz.ead.framework.mul2launcher.appstore.dialog.DialogPrompt.DialogPromptListener;
import com.sz.ead.framework.mul2launcher.appstore.dialog.PublicFun;
import com.sz.ead.framework.mul2launcher.appstore.dialog.ShowToast;
import com.sz.ead.framework.mul2launcher.dataprovider.dataitem.UpdateInfoItem;
import com.sz.ead.framework.mul2launcher.dataprovider.http.HttpEngineManager;
import com.sz.ead.framework.mul2launcher.dataprovider.http.UICallBack;
import com.sz.ead.framework.mul2launcher.dataprovider.packet.inpacket.InAppUpdatePacket;
import com.sz.ead.framework.mul2launcher.dataprovider.packet.outpacket.OutAppUpdatePacket;
import com.sz.ead.framework.mul2launcher.dataprovider.packet.outpacket.OutPacket;
import com.sz.ead.framework.mul2launcher.db.DownLoadDB;
import com.sz.ead.framework.mul2launcher.db.InstallingTable;
import com.sz.ead.framework.mul2launcher.util.Constant;
import com.sz.ead.framework.mul2launcher.util.FileUtil;
import com.sz.ead.framework.mul2launcher.util.MD5;
import com.szgvtv.ead.framework.bi.Bi;

public class DownloadManagerService extends Service implements UICallBack{

	//上下文相关
	private Context mContext;

	//下载map
	private HashMap<Object, DownloadThread> mCurDownList = new HashMap<Object, DownloadThread>();

	private static final int DOWNLOAD_RELINK_COUNT = 2;
	//记录重连次数map
	private HashMap<Integer, Integer> mDownloadReLinkList = new HashMap<Integer, Integer>();

	// 需要接受消息的handle list
	private ArrayList<Handler> mHandle = new ArrayList<Handler>();

	// 默认线程个数
	private static final int DefaultThreadCount=1;

	// 线程个数
	private int mThreadPoolSize = 1;

	// 广播接收器
	private BroadcastReceiver mdownMsgReceiver = new DownMsgIntentReceiver();

	// sd卡路径
	private final String sdCardPath = Environment.getExternalStorageDirectory()
			+ "";

	// apk文件夹
	private final String recordPath = "ApkPath";

	// 服务绑定器
	public DownloadBinder mBinder = new DownloadBinder();

	// 消息管理器
	//private DownloadNotification mDownloadNotification;

	// 检测更新timer 
	private Timer mUpdateTimer = null;

	// 检测方案timer 
	private Timer mCaseCheckTimer = null;

	// 下一次更新请求时间  24个小时
	private final int mNextUpdateRequstTime = 3600 * 24 * 1000;

	// 截图路径
	public static final String mScreenShotPath = "/data/screenShot/";
	
	// 图标路径 
	public static final String mAppIconPath = "/data/appicon/";

	//消息
	private final int MSG_UPDATE_COME=0x1100;
	private final int MSG_CASECHECK_COME=0x2200;

	private final int MSG_INSTALL_APK=0x3300;
	private final int MSG_UNINSTALL_APK_SUC=0x4400;
	private final int MSG_INSTALL_APK_SUC=0x5500;
	private final int MSG_ONCREATE_DIALOG=0x6600;
	private final int MSG_FRESH_ICON=0x7700;

	//TOKEN
	private final int TOKEN_UPDATE=0x1100;
	private final int TOKEN_CASECHECK=0x2200;

	// log filter
	private static final String TAG = "DownloadManagerService";

	// log flag
	public boolean debug = true;

	// 已经安装的apk需要删除,会出现同包名的情况，通过id区分 
	private String mDelApkId = null;

	// sd卡预留20M空间
	private static int retainSize=20*1024*1024;

	private DialogPrompt dialog;
	
	//是否跳出下载失败对话框
	private boolean mSkipApp = true;
	public boolean getSkipApp() {
		return mSkipApp;
	}
	public void setSkipApp(boolean mSkipApp) {
		Log.v(TAG, "setSkipApp"+mSkipApp);
		this.mSkipApp = mSkipApp;
	}

	private Handler handler = new Handler(){
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_ONCREATE_DIALOG:{
				DownloadInfo  downInfo= (DownloadInfo)msg.obj;
				if(downInfo != null){
					DownLoadDB.deleteADownLoad(downInfo.getAppCode());
					FileUtil.deleteFile(downInfo.getDownApkPath());
					if(!mSkipApp){
						if(dialog == null || (dialog != null && !dialog.isShowing())){
							onCreatDialog(downInfo);
						}	
					}
				}
				break;
			}
			case MSG_INSTALL_APK: 
				DownloadInfo  info= (DownloadInfo)msg.obj;
				Installer installer = new Installer(mContext, mHandle, info, true);
				installer.PackageInstall();
				break;
			case MSG_UPDATE_COME:{
				requestUpdateList();	
				break;
			}case MSG_FRESH_ICON:{
				//执行完升级就刷新
				freshInstallAppIcon();
				break;
			}
			case MSG_UNINSTALL_APK_SUC:{
				
				InstalledAppsScan.removeInstalledAppInfo((String)msg.obj);
				InstallingTable.deleteAInstalled((String)msg.obj);
				UpdateTable.DeleteUpdate((String)msg.obj);
				
				if (!((String)msg.obj).equals(""))
				{
					Log.d(TAG, "delete path:"+mScreenShotPath+(String)msg.obj);
					
					File file = new File(mScreenShotPath+(String)msg.obj);
					FileUtil.delete(file);
					
					File fileIcon = new File(mAppIconPath+(String)msg.obj+".png");
					FileUtil.delete(fileIcon);
				}
				
				sendDownMsgBroadCast(DownConstants.DOWN_STATUS_ACTION, DownConstants.DOWN_STATUS_DOWNLOAD_STATUS_KEY, DownConstants.DOWN_BROADCAST_TO_UI_REMOVED,
						DownConstants.DOWN_STATUS_DOWNLOAD_DATA_KEY, null);
				break;
			}
			case MSG_INSTALL_APK_SUC:{
				String pkgName = (String)msg.obj;
				try {
					Log.v(TAG, "aaaaaaa"+pkgName);
					PackageManager pm = mContext.getPackageManager();
					PackageInfo packageInfo = pm.getPackageInfo(pkgName, PackageManager.GET_PERMISSIONS);
					
					if(!hasLauncherActivities(pkgName)){
						return;
					}
					
					DownloadInfo info1 = DownLoadDB.queryADownRecord(pkgName);
					if(info1 != null){
						DownLoadDB.deleteADownLoad(info1.getAppCode());
						FileUtil.deleteFile(info1.getDownApkPath());

						if(InstallingTable.isInstalled(info1.getPkgName())){
							InstallingTable.deleteAInstalled(info1.getPkgName());
						}
						InstallingTable.insertAInstalled(info1);  
						//发给launcher
					
						Intent intent = new Intent();
						intent.setAction(DownConstants.DOWN_INSTALL_ACTION);

						Bundle bundle = new Bundle();
						bundle.putString(DownConstants.DOWN_INSTALL_PKG_KEY,pkgName);
						bundle.putString(DownConstants.DOWN_INSTALL_APPID_KEY,info1.getAppCode());
						intent.putExtras(bundle);
						mContext.sendBroadcast(intent);				
					}else{
						try {
							//系统应用不能加入安装表
							PackageManager packageManager = mContext.getPackageManager();
					        PackageInfo pInfo = packageManager.getPackageInfo(pkgName,0);
					        if(pInfo != null){
					        	if ((pInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) <= 0){
					        		AppInfo appInfo = InstalledAppsScan.getAppInfo(mContext,pkgName);
									if(appInfo != null&&!InstallingTable.isInstalled(appInfo.mPackageName)){
										InstallingTable.insertNoStoreInstall(appInfo);
									}
					        	}
					        }
						} catch (Exception e) {
						}
					}
					InstalledAppsScan.addInstalledAppInfo(mContext, pkgName);
					UpdateTable.DeleteUpdate(pkgName);
					sendDownMsgBroadCast(DownConstants.DOWN_STATUS_ACTION, DownConstants.DOWN_STATUS_DOWNLOAD_STATUS_KEY, DownConstants.DOWN_BROADCAST_TO_UI_INSTALLED,
							DownConstants.DOWN_STATUS_DOWNLOAD_DATA_KEY, null);	
					

				} catch (PackageManager.NameNotFoundException e) {
					//包未安装
					Message msgs = new Message();
					msgs.what= MSG_INSTALL_APK_SUC;
					msgs.obj = (Object)pkgName;
					handler.sendMessageDelayed(msgs, 1000);
				}

				break;
			}
			default:
				break;
			}

		}
	};

	public  boolean hasLauncherActivities(String pkg) { 
		boolean nRet = false;
		try {
			Intent intent = new Intent(Intent.ACTION_MAIN);
		     intent.addCategory(Intent.CATEGORY_LAUNCHER);
		     intent.setPackage(pkg);
		     List<ResolveInfo> appInfo = mContext.getPackageManager().queryIntentActivities(intent, 0);
		     if(appInfo.size() > 0){
		    	  nRet = true;
		     }
		} catch (Exception e) {
		} 
		return nRet;
	  }

	@Override
	public void onCreate() {
		super.onCreate();
		mContext = this;

		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_PACKAGE_ADDED);
		filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
		filter.addAction(Intent.ACTION_PACKAGE_FULLY_REMOVED);
		filter.addDataScheme("package");
		mContext.registerReceiver(mdownMsgReceiver, filter);

		IntentFilter filter2 = new IntentFilter();
		filter2.addAction(DownConstants.DOWN_MANAGE_ACTION);
		filter2.addAction(DownConstants.DOWN_STATUS_ACTION);
		mContext.registerReceiver(mdownMsgReceiver, filter2);

		IntentFilter filter3 = new IntentFilter();
		filter3.addAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
		mContext.registerReceiver(mdownMsgReceiver, filter3);

		IntentFilter filter4 = new IntentFilter();
		filter4.addAction(DownConstants.DOWN_APPSTORE_ID);
		mContext.registerReceiver(mdownMsgReceiver, filter4);

		
		DownLoadDB.delDownload(); /*服务重启就要删除下载20141212*/
		//mDownloadNotification = new DownloadNotification(mContext);
	}


	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		mContext = this;
		setSkipApp(false);
		if (debug)
			Log.i(TAG, "onStart()--->" + "start onStart~~~");
		
		//先不做升级 
		//startCheckUpdate();
		if(mHandle != null){
			handler.sendEmptyMessage(MSG_FRESH_ICON);
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		if (debug)
			Log.i(TAG, "onBind()--->" + "start onBind~~~");
		return mBinder;
	}

	@Override
	public boolean onUnbind(Intent intent) {
		if (debug)
			Log.i(TAG, "onUnbind()--->" + "start onUnbind~~~");
		return super.onUnbind(intent);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (debug)
			Log.i(TAG, "onDestroy()--->" + "start onDestroy~~~");
		//stopAllDownload();
		cancelUpdate();
		stopBackDownload();
	}


	/**
	 * 请求更新打开
	 * @author liyingying
	 */
	public void startCheckUpdate() {
		if (mUpdateTimer != null) {
			mUpdateTimer.cancel();
			mUpdateTimer=null;
		}
		mUpdateTimer = new Timer();
		TimerTask timeTask = new TimerTask() {
			@Override
			public void run() {
				if (debug)
					Log.i(TAG, "requestUpdate()--->" + "start requestUpdate~~~");
				Message m = new Message();
				m.what = MSG_UPDATE_COME;
				handler.sendMessage(m);
				// 这里不能处理其它事情 
			}
		};

		// 开始请求
		mUpdateTimer.schedule(timeTask, 15000, mNextUpdateRequstTime);
	}

	/**
	 * 取消更新请求
	 * @author liyingying
	 */
	public void cancelUpdate() {
		if (mUpdateTimer != null) {
			mUpdateTimer.cancel();
			mUpdateTimer = null;
		}
	}
	
	/*刷新商城安装应用图标*/
	private void freshInstallAppIcon(){
		delUninstallAppIcon();
		ArrayList<DownloadInfo> appInfo = InstallingTable.queryInstalledList();
		for (DownloadInfo downloadInfo : appInfo) {
			if(!TextUtils.isEmpty(downloadInfo.getIcon())){
				new DownAppIcon(mContext, downloadInfo);
			}
		}
	}
	
	/*删除卸载icon*/
	private void delUninstallAppIcon(){
		try {
			File filepath = new File(FileUtil.APPICON_PATH);
			File[] files = filepath.listFiles();
			if (files.length > 0) {
				for (File file : files){
					if(file.isFile()){
						String pkg = file.getName().substring(0,file.getName().lastIndexOf("."));
						//Log.i(TAG, "delUninstallAppIcon " + pkg);
						DownloadInfo info = InstallingTable.getInstallPkgInfo(pkg);
						if(info == null){
							file.delete();
						}
					}
				}
			}
		} catch (Exception e) {
		}
	}
	
	/*icond地址变化刷新iconurl20141213*/
	private void freshIconUrl(DownloadInfo oldUrl, UpdateInfoItem newUrl){
		String oldAddr,newAddr;
		boolean freflag = false;
		oldAddr = oldUrl.getIcon();
		newAddr = newUrl.getIcon();
		if(TextUtils.isEmpty(oldAddr)&&TextUtils.isEmpty(newAddr)){
			freflag = false;
		}else if(!TextUtils.isEmpty(oldAddr)&&!TextUtils.isEmpty(newAddr)){
			if(oldAddr.equals(newAddr)){
				freflag = false;
			}else {
				freflag = true;
			}
		}else if(TextUtils.isEmpty(oldAddr)){
			freflag = true;		
		}else if(TextUtils.isEmpty(newAddr)){
			freflag = true;
		}

		if(freflag){
			try {	
				File file = new File(FileUtil.getAppIconDir(mContext)+"/" + oldUrl.getPkgName() + ".png");
				if (file.exists()) {
					file.delete();
				}
			} catch (Exception e) {
			}	
			InstallingTable.modifyIconUrl(oldUrl.getPkgName(), newAddr);	
		}	
	}

	/**
	 * 请求更新APP
	 * @author liyingying
	 */
	private void requestUpdateList() {
		Log.d(TAG, "requestUpdateList");
		// 上行参数
		ArrayList<UpdateInfoItem> datas = new ArrayList<UpdateInfoItem>();
		//		List<AppInfo> installApk= InstalledAppsScan.getAllAppsList(mContext);
		List<DownloadInfo> installApk = InstallingTable.queryInstalledList();
		for (DownloadInfo appinfo : installApk) {
			Log.d(TAG, "111");
			UpdateInfoItem data = new UpdateInfoItem();
			data.setPkgName(appinfo.getPkgName());
			datas.add(data);
		}
		Log.d(TAG, "222");
		OutAppUpdatePacket mOutPacket = new OutAppUpdatePacket(this, TOKEN_UPDATE, datas);
		InAppUpdatePacket mInPacket = new InAppUpdatePacket(this,TOKEN_UPDATE);
		HttpEngineManager.createHttpEngine(mOutPacket, mInPacket, this);
	}

	public void onCancel(OutPacket out, int token) {
		if(mHandle != null){
			handler.sendEmptyMessage(MSG_FRESH_ICON);
		}
	}

	public void onSuccessful(Object in, int token) {
		try {
			if(token == TOKEN_UPDATE){
				new VersionThread(in).start();
			}

		} catch (Exception e) {
			Log.i(TAG, "update Exception " + e.toString());
			if(mHandle != null){
				handler.sendEmptyMessage(MSG_FRESH_ICON);
			}
		}
			
	}

	public void onNetError(int responseCode, String errorDesc, OutPacket out,
			int token) {
		if(mHandle != null){
			handler.sendEmptyMessage(MSG_FRESH_ICON);
		}
	}

	/**
	 * 
	 * Copyright © 2013好视网络. All rights reserved.
	 *
	 * @Title: DownloadManagerService.java
	 * @Prject: AppStore
	 * @Description: 版本比较线程
	 * @author: 李英英 
	 * @date: 2013年11月26日 上午9:52:18
	 * @version: V1.0
	 */
	public class VersionThread extends Thread{
		Object mObject;

		public VersionThread(Object object) {
			mObject = object;
		}
		@Override
		public void run() {
			super.run();
			retUpdateDate(mObject);
		}
	}

	/**
	 * @Title: retUpdateDate
	 * @Description: 比较升级版本
	 * @param in
	 * @return: void
	 */
	private void retUpdateDate(Object in){
		try {
			ArrayList<UpdateInfoItem> result = ((ArrayList<UpdateInfoItem>) in);
			
			//删除已经卸载应用升级
			Cursor mCursor = UpdateTable.QueryAllUpdate();
			if(mCursor != null){
				for (int i = 0; i < mCursor.getCount(); i++) {
					UpdateInfoItem infoItem = UpdateTable.getARecord(mCursor, i);
					if(infoItem != null){
						//if(InstallingTable.isInstalled(infoItem.getPkgName())){//2014-6-23 更新升级表
						UpdateTable.DeleteUpdate(infoItem.getPkgName());
						//}
					}
				}
			}

			if(mCursor != null){
				mCursor.close();
				mCursor = null;
			}

			for (UpdateInfoItem data : result) {
				String verCode= getVerCode(data.getPkgName());
				if(!TextUtils.isEmpty(verCode)){
					Log.i(TAG, "====appname="+data.getAppName());
					if(isHigherVersion(verCode, data.getVersion())){
						if(!UpdateTable.isExistUpdate(data.getPkgName())){
							UpdateTable.InsertUpdate(data);
						}
					}
				}
				
				/*icon地址变化 更新icon地址 20141213*/
				DownloadInfo info = InstallingTable.getInstallPkgInfo(data.getPkgName());
				if(info != null){
					freshIconUrl(info,data);
				}
			}
		
		} catch (Exception e) {
		}	
		if(mHandle != null){
			handler.sendEmptyMessage(MSG_FRESH_ICON);
		}
		sendDownMsgBroadCast(DownConstants.DOWN_STATUS_ACTION, DownConstants.DOWN_STATUS_DOWNLOAD_STATUS_KEY, DownConstants.DOWN_BROADCAST_TO_UI_UPDATE,
				DownConstants.DOWN_STATUS_DOWNLOAD_DATA_KEY, null);	
	}
	
	/**
	 * @Title: isHigherVersion
	 * @Description: 是不是高版本
	 * @param installVersion
	 * @param serverVersion
	 * @return
	 * @return: boolean
	 */
	private boolean isHigherVersion(String installVersion, String serverVersion){
		/*if(serverVersion.compareTo(installVersion)>0){
			return true;
		}else{
			return false;
		}*/
		Double localVer = 0.0,serviceVer = 0.0;
		boolean nRet = false;
		try {
			localVer = new Double(installVersion).doubleValue();
			serviceVer = new Double(serverVersion).doubleValue();
			if(serviceVer > localVer){
				nRet = true;
			}
		} catch (Exception e) {
		}
		Log.i(TAG, " serverVer"+serviceVer+" localVer"+localVer);	
		return nRet;
	}


	/**
	 * 加入下载队列
	 * @author liyingying
	 * @param item 调用者填充
	 */
	public void addDownList(DownloadInfo item) {
		if (FileUtil.isSdCardExist()) {
			if (isValidFile(item) >= 0) {
				int status = getDownLoadStatus(item.getAppCode());
				Log.i(TAG, " addDownList status"+status);	
				
				if (status == DownConstants.STATUS_DOWN_COLLECT) {
					DownloadInfo downloadInfo = new DownloadInfo(item,
							FileUtil.getApkDir(mContext));
					DownLoadDB.deleteADownLoad(item.getAppCode());
					DownLoadDB.insertADownLoad(downloadInfo);
					if (mCurDownList.size() < mThreadPoolSize) {
						DownloadThread downloadThread = new DownloadThread(
								downloadInfo);
						downloadInfo.setDownStatus(DownConstants.STATUS_DOWN_DOWNING);
						DownLoadDB.modifyDownLoadStatus(downloadInfo.getAppCode(),
								downloadInfo.getDownStatus());
						appendThread(downloadInfo.getAppCode(), downloadThread);
						downloadThread.start();
					}

					sendDownMsgBroadCast(DownConstants.DOWN_MANAGE_ACTION, DownConstants.DOWN_STATUS_DOWNLOAD_STATUS_KEY, DownConstants.DOWN_BROADCAST_TO_UI_UPDATE,
							DownConstants.DOWN_STATUS_DOWNLOAD_DATA_KEY, downloadInfo);
					sendDownMsgBroadCast(DownConstants.DOWN_STATUS_ACTION, DownConstants.DOWN_STATUS_DOWNLOAD_STATUS_KEY, DownConstants.DOWN_BROADCAST_TO_UI_UPDATE,
							DownConstants.DOWN_STATUS_DOWNLOAD_DATA_KEY, downloadInfo);
				} else if (status == DownConstants.STATUS_DOWN_DOWNCOMPLETE) {
				} else if (status == DownConstants.STATUS_DOWN_DOWNING
						|| status == DownConstants.STATUS_DOWN_PAUSE
						|| status == DownConstants.STATUS_DOWN_READY
						|| status == DownConstants.STATUS_DOWN_BREAKPOINT) {
				} else {
					Log.d(TAG, "DownLoadDB.queryDownloadCount:"+DownLoadDB.queryDownloadCount());
					if(DownLoadDB.queryDownloadCount() <= 0){
						DownloadInfo downloadInfo = new DownloadInfo(item,
								FileUtil.getApkDir(mContext));
						downloadInfo.setDownStatus(DownConstants.STATUS_DOWN_READY);
						DownLoadDB.insertADownLoad(downloadInfo);
						if (mCurDownList.size() < mThreadPoolSize) {
							DownloadThread downloadThread = new DownloadThread(
									downloadInfo);
							downloadInfo.setDownStatus(DownConstants.STATUS_DOWN_DOWNING);
							DownLoadDB.modifyDownLoadStatus(downloadInfo.getAppCode(),
									downloadInfo.getDownStatus());
							appendThread(downloadInfo.getAppCode(), downloadThread);
							downloadThread.start();
							if(debug)
								Log.i(TAG, "addDownList" + "==start download==");
						}
						if(debug)
							Log.i(TAG, "addDownList" + "==add list==");
						sendDownMsgBroadCast(DownConstants.DOWN_MANAGE_ACTION, DownConstants.DOWN_STATUS_DOWNLOAD_STATUS_KEY, DownConstants.DOWN_BROADCAST_TO_UI_UPDATE,
								DownConstants.DOWN_STATUS_DOWNLOAD_DATA_KEY, downloadInfo);
						sendDownMsgBroadCast(DownConstants.DOWN_STATUS_ACTION, DownConstants.DOWN_STATUS_DOWNLOAD_STATUS_KEY, DownConstants.DOWN_BROADCAST_TO_UI_UPDATE,
								DownConstants.DOWN_STATUS_DOWNLOAD_DATA_KEY, downloadInfo);
						
						/*12月1日加入下载icon图片*/
						DownAppIcon downAppIcon = new DownAppIcon(mContext, downloadInfo);
						//downAppIcon.start();
					}else{
						//ShowToast.getShowToast().createToast(mContext, mContext.getResources().getString(R.string.appstore_detail_app_downing));
						Notice notice = Notice.makeNotice(mContext, mContext.getResources().getString(R.string.appstore_detail_app_downing),
								Notice.LENGTH_SHORT);
						notice.cancelAll();
						notice.show();
					}

				}
			} else {
			}

		} else {
		}

	}

	/**
	 * 收藏队列加入下载队列
	 * @author liyingying
	 * @param downinfo 收藏队列信息
	 */
	public void collectListtoDownList(DownloadInfo downinfo) {
		DownloadInfo downloadInfo = downinfo;

		if (mCurDownList.size() < mThreadPoolSize) {
			downloadInfo.setDownStatus(DownConstants.STATUS_DOWN_DOWNING);
			DownLoadDB.modifyDownLoadStatus(downloadInfo.getAppCode(),
					downloadInfo.getDownStatus());
			DownloadThread downloadThread = new DownloadThread(downloadInfo);
			appendThread(downloadInfo.getAppCode(),
					downloadThread);
			downloadThread.start();
		} else {
			DownLoadDB.modifyDownLoadStatus(downloadInfo.getAppCode(),
					DownConstants.STATUS_DOWN_BREAKPOINT);
		}

		/*Toast.makeText(
				mContext,
				FileUtil.getString(mContext,
						R.string.download_add_download_suc),
				Toast.LENGTH_SHORT).show();*/
	}

	/**
	 * 判断是不是无效应用
	 * @author liyingying
	 * @param item 根据传进来参数判断是不是无效应用
	 * @return 0有效 -1无效
	 */
	public int isValidFile(DownloadInfo item) {
		if (TextUtils.isEmpty(item.getDownloadurl())) {
			return -1;
		} else {
			return 0;
		}
	}

	/**
	 * 准备队列加入下载队列
	 * @author liyingying
	 */
	public void readyListToDownList() {
		Cursor mCursor;

		mCursor = DownLoadDB.queryReadyList();
		if (mCursor != null && mCursor.getCount() > 0) {
			DownloadInfo downinfo = DownLoadDB.getARecord(mCursor, 0);
			if (downinfo != null) {
				if (mCurDownList.size() < mThreadPoolSize) {
					/*			downinfo.setDownStatus(DownConstants.STATUS_DOWN_DOWNING);
					DownLoadDB.modifyDownLoadStatus(downinfo.getAppCode(),
							downinfo.getDownStatus());*/
					DownloadThread downloadThread = new DownloadThread(downinfo);
					appendThread(downinfo.getAppCode(),
							downloadThread);
					downloadThread.start();
				}
			}

		}

		if(mCursor !=null){
			mCursor.close();
			mCursor=null;
		}
	}

	/**
	 * 加入收藏队列
	 * @author liyingying
	 * @param item 调用者填充
	 */
	public void addCollectList(DownloadInfo item) {
		DownloadInfo downloadInfo = new DownloadInfo(item, FileUtil.getApkDir(mContext));
		downloadInfo.setDownStatus(DownConstants.STATUS_DOWN_COLLECT);

		if (isValidFile(item) >= 0) {
			int status = getAppStatus(item.getAppCode(), item.getPkgName());
			if (status == DownConstants.STATUS_DOWN_COLLECT) {
				/*Toast.makeText(
						mContext,
						FileUtil.getString(mContext,
								R.string.download_added_collectlist),
						Toast.LENGTH_SHORT).show();*/
			} else if (status == DownConstants.STATUS_DOWN_DOWNCOMPLETE) {
				/*Toast.makeText(
						mContext,
						FileUtil.getString(mContext,
								R.string.download_download_compelete),
						Toast.LENGTH_SHORT).show();*/
			} else if (status == DownConstants.STATUS_DOWN_DOWNING
					|| status == DownConstants.STATUS_DOWN_PAUSE
					|| status == DownConstants.STATUS_DOWN_READY
					|| status == DownConstants.STATUS_DOWN_BREAKPOINT) {
				/*Toast.makeText(
						mContext,
						FileUtil.getString(mContext,
								R.string.download_added_downloadlist),
						Toast.LENGTH_SHORT).show();*/
			}else if(status == DownConstants.STATUS_APP_INSTALLED){
				/*Toast.makeText(
						mContext,
						FileUtil.getString(mContext,
								R.string.download_installed),
						Toast.LENGTH_SHORT).show();*/
			}
			else {
				DownLoadDB.insertADownLoad(downloadInfo);
				/*Toast.makeText(
						mContext,
						FileUtil.getString(mContext,
								R.string.download_add_collect_suc),
						Toast.LENGTH_SHORT).show();*/

				sendDownMsgBroadCast(DownConstants.DOWN_MANAGE_ACTION, DownConstants.DOWN_STATUS_DOWNLOAD_STATUS_KEY, DownConstants.DOWN_BROADCAST_TO_UI_STATUS_COLLECT_SUCCESS,
						DownConstants.DOWN_STATUS_DOWNLOAD_DATA_KEY, downloadInfo);
				sendDownMsgBroadCast(DownConstants.DOWN_STATUS_ACTION, DownConstants.DOWN_STATUS_DOWNLOAD_STATUS_KEY, DownConstants.DOWN_BROADCAST_TO_UI_STATUS_COLLECT_SUCCESS,
						DownConstants.DOWN_STATUS_DOWNLOAD_DATA_KEY, downloadInfo);
			}
		} else {
			/*Toast
					.makeText(
							mContext,
							FileUtil.getString(mContext,
									R.string.download_invalid_app),
							Toast.LENGTH_SHORT).show();*/
		}

	}

	/**
	 * 加入传递消息handle
	 * @author liyingying
	 * @param handle 需要得到下载状态的ui界面可以传handle
	 */
	public void addHandle(Handler handle) {
		Log.d(TAG, "addHandle");
		int mHandCount = mHandle.size();
		Log.d(TAG, "addHandle size"+mHandCount);
		int curPos = 0;
		for (; curPos < mHandCount; ++curPos) {
			if (handle.equals(mHandle.get(curPos))) {
				break;
			}
		}
		if (curPos >= mHandCount) {
			mHandle.add(handle);
		}
	}

	/**
	 * 删除消息handle
	 * @author liyingying
	 */
	public void delUiHandle() {
		mHandle.clear();
	}

	/**
	 * 删除消息handle
	 * @author liyingying
	 */
	public void delAHandle(Handler handle) {
		int mHandCount = mHandle.size();
		int curPos = 0;
		if(handle==null){
			return;
		}
		for (; curPos < mHandCount; ++curPos) {
			if (handle.equals(mHandle.get(curPos))) {
				mHandle.remove(curPos);
				break;
			}
		}
	}

	/**
	 *  根据id开始下载
	 * @author liyingying
	 * @param AppId 应用id
	 */
	public void startADownLoad(String AppCode) {
		DownloadInfo info;
		if (mCurDownList.size() < mThreadPoolSize) {
			info = DownLoadDB.queryADownRecord(AppCode);
			if (info != null && !existThread(AppCode)) {
				DownloadThread downloadThread = new DownloadThread(info);
				info.setDownStatus(DownConstants.STATUS_DOWN_DOWNING);
				DownLoadDB.modifyDownLoadStatus(info.getAppCode(), info.getDownStatus());
				appendThread(AppCode, downloadThread);
				downloadThread.start();

				sendDownMsgBroadCast(DownConstants.DOWN_MANAGE_ACTION, DownConstants.DOWN_STATUS_DOWNLOAD_STATUS_KEY, DownConstants.DOWN_BROADCAST_TO_UI_UPDATE,
						DownConstants.DOWN_STATUS_DOWNLOAD_DATA_KEY, info);
				sendDownMsgBroadCast(DownConstants.DOWN_STATUS_ACTION, DownConstants.DOWN_STATUS_DOWNLOAD_STATUS_KEY, DownConstants.DOWN_BROADCAST_TO_UI_UPDATE,
						DownConstants.DOWN_STATUS_DOWNLOAD_DATA_KEY, info);

			} else {

			}

		} else {
			if(mThreadPoolSize >= DefaultThreadCount){
				//Toast.makeText(mContext,FileUtil.getString(mContext,R.string.download_downing_max),Toast.LENGTH_SHORT).show();
			}else{
				//Toast.makeText(mContext,FileUtil.getString(mContext,R.string.download_downing_full),Toast.LENGTH_SHORT).show();
			}
		}

	}

	/**
	 * 停止下载
	 * @author liyingying
	 * @param AppId 应用id
	 */
	public void stopADownLoad(String AppCode) {
		DownloadThread downloadThread = mCurDownList.get(AppCode);
		if (downloadThread != null) {
			downloadThread.stopDownLoad(DownConstants.STATUS_DOWN_STOP);
			DownLoadDB.deleteADownLoad(AppCode);
		} else {
			// 直接删除数据库 
			DownLoadDB.deleteADownLoad(AppCode);
		}
		
		sendDownMsgBroadCast(DownConstants.DOWN_MANAGE_ACTION, DownConstants.DOWN_STATUS_DOWNLOAD_STATUS_KEY, DownConstants.DOWN_BROADCAST_TO_UI_UPDATE,
				DownConstants.DOWN_STATUS_DOWNLOAD_DATA_KEY, null);
		sendDownMsgBroadCast(DownConstants.DOWN_STATUS_ACTION, DownConstants.DOWN_STATUS_DOWNLOAD_STATUS_KEY, DownConstants.DOWN_BROADCAST_TO_UI_UPDATE,
				DownConstants.DOWN_STATUS_DOWNLOAD_DATA_KEY, null);

		//RemoveRelink(AppCode);
	}

	/**
	 * 暂停下载
	 * @author liyingying
	 * @param AppId 应用id
	 */
	public void pauseADownLoad(String AppId) {
		DownloadThread downloadThread = mCurDownList.get(AppId);
		if (downloadThread != null) {
			downloadThread.pauseDownLoad(DownConstants.STATUS_DOWN_PAUSE);

			sendDownMsgBroadCast(DownConstants.DOWN_MANAGE_ACTION, DownConstants.DOWN_STATUS_DOWNLOAD_STATUS_KEY, DownConstants.DOWN_BROADCAST_TO_UI_UPDATE,
					DownConstants.DOWN_STATUS_DOWNLOAD_DATA_KEY, null);
			sendDownMsgBroadCast(DownConstants.DOWN_STATUS_ACTION, DownConstants.DOWN_STATUS_DOWNLOAD_STATUS_KEY, DownConstants.DOWN_BROADCAST_TO_UI_UPDATE,
					DownConstants.DOWN_STATUS_DOWNLOAD_DATA_KEY, null);
		}

		//RemoveRelink(AppId);
	}


	/**
	 * 得到线程下载状态
	 * @author liyingying
	 * @param packagename
	 * @param appId 应用id
	 * @return 状态
	 */
	public int getDownLoadStatus(String AppCode) {
		int status;
		status = DownLoadDB.queryDownloadStatus(AppCode);

		// 加入容错处理 出现数据库记录下载状态但是没有线程 
		if (status == DownConstants.STATUS_DOWN_DOWNING) {
			if (!existThread(AppCode)) {
				DownLoadDB.modifyDownLoadStatus(AppCode,
						DownConstants.STATUS_DOWN_BREAKPOINT);
			} else {

			}
		}
		return status;
	}

	/**
	 * 得到应用状态 不包括安装
	 * @author liyingying
	 * @param packagename
	 * @param appId
	 * @return 状态
	 */
	public int getAppStatusNoInstall(String AppCode) {

		int status = DownConstants.STATUS_DOWN_UNKNOWN;
		status = DownLoadDB.queryDownloadStatus(AppCode);
		if (status == DownConstants.STATUS_DOWN_PAUSE
				|| status == DownConstants.STATUS_DOWN_READY
				|| status == DownConstants.STATUS_DOWN_DOWNING
				|| status == DownConstants.STATUS_DOWN_BREAKPOINT) {
			//status = DownConstants.STATUS_DOWN_DOWNING;
		}

		return status;
	}

	/**
	 * 得到应用状态
	 * @author liyingying
	 * @param packagename
	 * @param appId
	 * @return 状态
	 */
	public int getAppStatus(String appCode, String pkgName) {
		
		
		
		int status = DownConstants.STATUS_DOWN_UNKNOWN;
		Log.d("test", "getAppStatus11");
		status = DownLoadDB.queryDownloadStatus(appCode);
		Log.d("test", "getAppStatus12:"+status);
		if (status == DownConstants.STATUS_DOWN_PAUSE
				|| status == DownConstants.STATUS_DOWN_READY
				|| status == DownConstants.STATUS_DOWN_DOWNING
				|| status == DownConstants.STATUS_DOWN_BREAKPOINT) {
			//status = DownConstants.STATUS_DOWN_DOWNING;

		}

		if (!TextUtils.isEmpty(pkgName)) {
			if (InstallingTable.isInstalled(pkgName)) {
				if(UpdateTable.isExistUpdate(pkgName)) {
					if(status!=DownConstants.STATUS_DOWN_DOWNING){
						if(status!=DownConstants.STATUS_DOWN_DOWNCOMPLETE){
							if(status!=DownConstants.STATUS_DOWN_INSTALLING){/*20141216修改*/
								status = DownConstants.STATUS_APP_CAN_UPDATE;
							}
							
						}		
					}
				} else {				
					status = DownConstants.STATUS_APP_INSTALLED;
				}
			} else {
				try {
					//Log.v(TAG, "uuuuuuuuuuuuu"+pkgName);
					PackageManager pm = mContext.getPackageManager();
					PackageInfo packageInfo = pm.getPackageInfo(pkgName, PackageManager.GET_PERMISSIONS);
					if(packageInfo != null){
						status = DownConstants.STATUS_APP_INSTALLED;
					}
				} catch (PackageManager.NameNotFoundException e) {
					//包未安装
					//Log.i(TAG, "getAppStatus " + e.toString());
				}	
			}
		}
		//Log.i(TAG, "getAppStatus " + status);
		return status;
	}

	/**
	 * @Title: stopBackDownload
	 * @Description: 停止后台下载
	 * @return: void
	 */
	public void stopBackDownload(){
		
		Iterator it = mCurDownList.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			DownloadThread value = (DownloadThread) entry.getValue();
			if (value.getDownLoadStatus() == DownConstants.STATUS_DOWN_DOWNING) {
				stopADownLoad(value.mDownloadinfo.getAppCode());
				FileUtil.deleteFile(value.mDownloadinfo.getDownApkPath());
			}
		}
	}

	/**
	 * 停止所有下载
	 * @author liyingying
	 */
	public void stopAllDownload() {

		Iterator it = mCurDownList.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			DownloadThread value = (DownloadThread) entry.getValue();
			if (value.getDownLoadStatus() == DownConstants.STATUS_DOWN_DOWNING) {
				value.pauseDownLoad(DownConstants.STATUS_DOWN_PAUSE);
			}
		}

		// 更新数据库 开启状态的都要暂停 
		DownLoadDB.modifyDowningToPause();

		if (mdownMsgReceiver != null) {
			mContext.unregisterReceiver(mdownMsgReceiver);
			mdownMsgReceiver = null;
		}
		int mHandCount = mHandle.size();
		for (int i = 0; i < mHandCount; ++i) {
			mHandle.remove(i);
		}
	}

	// 出现异常需要改变下载状态改为暂停状态 
	public void modifyDownloadStatus() {
		Cursor mCursor;
		mCursor = DownLoadDB.queryDowningList();

		if (mCursor != null) {
			int count = mCursor.getCount();
			for (int i = 0; i < count; ++i) {
				DownloadInfo info = DownLoadDB.getARecord(mCursor, i);
				if (info != null) {
					if (existThread(info.getAppCode())) {
						// 在线程中就是在下载 
					} else {

						// 不在线程中 就要修改状态 
						/*DownLoadDB.modifyDownLoadStatus(info
								.getAppCode(),
								DownConstants.STATUS_DOWN_PAUSE);*/
						DownLoadDB.deleteADownLoad(info.getAppCode());
					}
				}
			}
		}
		if (mCursor != null) {
			mCursor.close();
			mCursor = null;
		}

		// 数据库有记录，但是文件已被删除
		mCursor=DownLoadDB.queryDownLoadStart();
		if (mCursor != null) {
			int count = mCursor.getCount();
			for (int i = 0; i < count; ++i) {
				DownloadInfo info = DownLoadDB.getARecord(mCursor, i);
				if (info != null) {
					if(info.getDownStatus()==DownConstants.STATUS_DOWN_DOWNCOMPLETE
							&&!FileUtil.isFileExist(info.getDownApkPath())){
						DownLoadDB.deleteADownLoad(info.getAppCode());
						continue;
					}

					if(info.getDownStatus() == DownConstants.STATUS_DOWN_PAUSE
							||info.getDownStatus() == DownConstants.STATUS_DOWN_BREAKPOINT
							||info.getDownStatus() == DownConstants.STATUS_DOWN_DOWNING){
						if(info.getDownCurrentBytes()>0&&!FileUtil.isFileExist(info.getDownApkPath())){
							pauseADownLoad(info.getAppCode());//文件删除需要停止线程
							DownLoadDB.modifyDownLoadProgress(info.getAppCode(), 0);
							DownLoadDB.modifyDownLoadStatus(info.getAppCode(), DownConstants.STATUS_DOWN_PAUSE);
						}
					}
				}
			}
		}
		if (mCursor != null) {
			mCursor.close();
			mCursor = null;
		}
	}

	/**
	 * 得到apk路径
	 * @author liyingying
	 * @param AppId 应用id
	 * @return 保存在sd卡路径
	 */
	public String getApkPath(String AppCode) {
		String apkPath = null;

		apkPath = DownLoadDB.queryApkPath(AppCode);
		if (apkPath == null) {
			mDelApkId = null;
		} else {
			mDelApkId = AppCode;
		}
		return apkPath;
	}

	/**
	 * 加入线程
	 * @author liyingying
	 * @param AppId 线程id
	 * @param downloadThread 线程参数
	 */
	protected synchronized void appendThread(String AppCode,
			DownloadThread downloadThread) {
		mCurDownList.put(AppCode, downloadThread);
	}

	/**
	 * 删除线程
	 * @author liyingying
	 * @param AppId 线程id
	 */
	protected synchronized void removeThread(String AppCode) {
		mCurDownList.remove(AppCode);
	}

	/**
	 * 存在下载线程
	 * @author liyingying
	 * @param AppId 线程id
	 * @return true 存在  反之不存在
	 */
	protected boolean existThread(String AppCode) {

		DownloadThread packageThread = mCurDownList.get(AppCode);
		if (packageThread != null) {
			return true;
		} else {
			return false;
		}

	}

	protected int getThreadCount() {
		return mCurDownList.size();
	}

	/**
	 * @Title: onCreatDialog
	 * @Description: TODO
	 * @param id
	 * @param position
	 * @return: void
	 */
	private void onCreatDialog(final DownloadInfo info){
		dialog = new DialogPrompt(mContext);
		final DownloadInfo iteminfo =  info;
		dialog.setMessage("“"+iteminfo.getAppName()+"”"+mContext.getResources().getString(R.string.appstore_detail_app_downing_fail));
		dialog.setPositiveButton(mContext.getResources().getString(R.string.appstore_detail_download_again));
		dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
		dialog.setOnDialogPromptListener(new DialogPromptListener() {
			public void OnClick(View view) {
				switch (view.getId()) {
				case R.id.dialog_prompt_sure: {

					dialog.dismiss();
					DownloadInfo newinfo = new DownloadInfo(info);
					newinfo.setDownCurrentBytes(0);
					newinfo.setDownTotalBytes(-1);
					addDownList(newinfo);
					sendDownMsgBroadCast(DownConstants.DOWN_STATUS_ACTION, DownConstants.DOWN_STATUS_DOWNLOAD_STATUS_KEY, DownConstants.DOWN_BROADCAST_TO_UI_UPDATE,
							DownConstants.DOWN_STATUS_DOWNLOAD_DATA_KEY, null);
					break;
				}
				case R.id.dialog_prompt_cancel: {
					dialog.dismiss();
					sendDownMsgBroadCast(DownConstants.DOWN_STATUS_ACTION, DownConstants.DOWN_STATUS_DOWNLOAD_STATUS_KEY, DownConstants.DOWN_BROADCAST_TO_UI_UPDATE,
							DownConstants.DOWN_STATUS_DOWNLOAD_DATA_KEY, null);
					break;
				}
				default:
					break;
				}

			}
		});
		dialog.setOnKeyListener(new OnKeyListener() {
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				if(keyCode == KeyEvent.KEYCODE_BACK){
					sendDownMsgBroadCast(DownConstants.DOWN_STATUS_ACTION, DownConstants.DOWN_STATUS_DOWNLOAD_STATUS_KEY, DownConstants.DOWN_BROADCAST_TO_UI_UPDATE,
							DownConstants.DOWN_STATUS_DOWNLOAD_DATA_KEY, null);
				}
				return false;
			}
		});
		dialog.show();
		
		Bi.sendBiMsg(Bi.BICT_FIRMWARE, 
				Bi.BICC_DOWNLOAD_RESULT, 
				info.getAppCode(),
				info.getVersion(),
				PublicFun.getCurrentLauguage(mContext),
				"1",
				AppInfoConstant.APP_ID, 
				PublicFun.getsVersionName(),
				String.valueOf(PublicFun.getsAppSource()),
				PublicFun.getsAppSourceId(),
				PublicFun.getsAppSourceName());
	}

	/**
	 * 广播接收类
	 * @author liyingying
	 */
	private class DownMsgIntentReceiver extends BroadcastReceiver {
		public void onReceive(Context context, Intent intent) {
			//if(debug)
			Log.i("DownMsgIntentReceiver","action==="
					+ intent.getAction());

			if (intent.getAction().equals(DownConstants.DOWN_MANAGE_ACTION)) {	
				String str = intent.getStringExtra(DownConstants.DOWN_STATUS_DOWNLOAD_STATUS_KEY);
				Log.i("DownMsgIntentReceiver","str==="
						+ str);
				if (str != null) {
					if (str.equals(DownConstants.DOWN_BROADCAST_TO_UI_STATUS_DOWN_COMPLETE)) {

						DownloadInfo downInfo = (DownloadInfo)intent.getSerializableExtra(DownConstants.DOWN_STATUS_DOWNLOAD_DATA_KEY);//这个action没有传downInfo也可以取到
						int i = Bi.sendBiMsg(Bi.BICT_FIRMWARE, 
								Bi.BICC_DOWNLOAD_RESULT, 
								downInfo.getAppCode(),
								downInfo.getVersion(),
								PublicFun.getCurrentLauguage(mContext),
								"0",
								AppInfoConstant.APP_ID, 
								PublicFun.getsVersionName(),
								String.valueOf(PublicFun.getsAppSource()),
								PublicFun.getsAppSourceId(),
								PublicFun.getsAppSourceName());

						Log.v(TAG, "dddowm::::"+i);

						Bi.sendBiMsg(Bi.BICT_FIRMWARE, 
								Bi.BICC_APP_INSTALL, 
								downInfo.getAppCode(),
								downInfo.getVersion(),
								PublicFun.getCurrentLauguage(mContext),
								String.valueOf(PublicFun.getsAppSource()),
								PublicFun.getsAppSourceId(),
								PublicFun.getsAppSourceName(),
								AppInfoConstant.APP_ID, 
								PublicFun.getsVersionName());

						//readyListToDownList();
						if(downInfo!=null){

							String archiveFilePath= downInfo.getDownApkPath();//安装包路径
							Log.v("path:", archiveFilePath);
							PackageManager pm = getPackageManager();  
							PackageInfo info = pm.getPackageArchiveInfo(archiveFilePath, PackageManager.GET_ACTIVITIES);  
							if(info != null){  
								ApplicationInfo appInfo = info.applicationInfo;  
								String packageName = appInfo.packageName;  //得到安装包名称

								Log.v("pkgNameInfo:", "1 pkgName:"+downInfo.getPkgName());
								Log.v("pkgNameInfo:", "2 pkgName:"+packageName);
								if (!downInfo.getPkgName().equals(packageName)){
									Log.v("install error:", "wrong pkgName:"+downInfo.getPkgName()+" pkgNmae:"+packageName);
									DownLoadDB.deleteADownLoad(downInfo.getAppCode());
									FileUtil.deleteFile(downInfo.getDownApkPath());
									sendDownMsgBroadCast(DownConstants.DOWN_STATUS_ACTION, DownConstants.DOWN_STATUS_DOWNLOAD_STATUS_KEY, DownConstants.DOWN_BROADCAST_TO_UI_UPDATE,
											DownConstants.DOWN_STATUS_DOWNLOAD_DATA_KEY, null);
									//ShowToast.getShowToast().createToast(mContext, mContext.getResources().getString(R.string.appstore_detail_install_error));
									Notice notice = Notice.makeNotice(mContext, mContext.getResources().getString(R.string.appstore_detail_install_error),
											Notice.LENGTH_SHORT);
									notice.cancelAll();
									notice.show();
									
									Bi.sendBiMsg(Bi.BICT_FIRMWARE, Bi.BICC_INSTALL_RESULT,
											downInfo.getAppCode(), downInfo.getVersion(),
											PublicFun.getCurrentLauguage(mContext), "1",
											AppInfoConstant.APP_ID,
											PublicFun.getsVersionName());
									
									return;
								}
								
								String localMD5 = MD5.md5sum(downInfo.getDownApkPath());
								Log.v("MD5:", "1 service:"+downInfo.getMd5());
								Log.v("MD5:", "2 local:"+localMD5);
								if(!TextUtils.isEmpty(downInfo.getMd5())&&!TextUtils.isEmpty(localMD5)){
									if(!localMD5.equalsIgnoreCase(downInfo.getMd5())){
										DownLoadDB.deleteADownLoad(downInfo.getAppCode());
										FileUtil.deleteFile(downInfo.getDownApkPath());
										sendDownMsgBroadCast(DownConstants.DOWN_STATUS_ACTION, DownConstants.DOWN_STATUS_DOWNLOAD_STATUS_KEY, DownConstants.DOWN_BROADCAST_TO_UI_UPDATE,
												DownConstants.DOWN_STATUS_DOWNLOAD_DATA_KEY, null);
										//ShowToast.getShowToast().createToast(mContext, mContext.getResources().getString(R.string.appstore_detail_install_error));
										Notice notice = Notice.makeNotice(mContext, mContext.getResources().getString(R.string.appstore_detail_install_error),
												Notice.LENGTH_SHORT);
										notice.cancelAll();
										notice.show();
										
										Bi.sendBiMsg(Bi.BICT_FIRMWARE, Bi.BICC_INSTALL_RESULT,
												downInfo.getAppCode(), downInfo.getVersion(),
												PublicFun.getCurrentLauguage(mContext), "1",
												AppInfoConstant.APP_ID,
												PublicFun.getsVersionName());
										
										return;
									}
								}
								
							}else{
								Log.v("downinfo:", "null");
								DownLoadDB.deleteADownLoad(downInfo.getAppCode());
								sendDownMsgBroadCast(DownConstants.DOWN_STATUS_ACTION, DownConstants.DOWN_STATUS_DOWNLOAD_STATUS_KEY, DownConstants.DOWN_BROADCAST_TO_UI_UPDATE,
										DownConstants.DOWN_STATUS_DOWNLOAD_DATA_KEY, null);
								//ShowToast.getShowToast().createToast(mContext, mContext.getResources().getString(R.string.appstore_detail_install_error));
								Notice notice = Notice.makeNotice(mContext, mContext.getResources().getString(R.string.appstore_detail_install_error),
										Notice.LENGTH_SHORT);
								notice.cancelAll();
								notice.show();
								
								Bi.sendBiMsg(Bi.BICT_FIRMWARE, Bi.BICC_INSTALL_RESULT,
										downInfo.getAppCode(), downInfo.getVersion(),
										PublicFun.getCurrentLauguage(mContext), "1",
										AppInfoConstant.APP_ID,
										PublicFun.getsVersionName());
								
								return;
							}



							if(!isSystemApp()){
								String apkPath = downInfo.getDownApkPath();
								if (FileUtil.isFileExist(apkPath)) {	
									Intent mIntent = new Intent();
									mIntent.setAction(android.content.Intent.ACTION_VIEW);
									mIntent.setDataAndType(Uri
											.parse("file://" + apkPath),
											"application/vnd.android.package-archive");
									mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
									startActivity(mIntent);
								}
							}else{
								Message msg = new Message();
								msg.what = MSG_INSTALL_APK;
								msg.obj = downInfo;
								handler.sendMessage(msg);
								downInfo.setDownStatus(DownConstants.STATUS_DOWN_INSTALLING);   
								DownLoadDB.modifyDownLoadStatus(downInfo.getAppCode(),
										DownConstants.STATUS_DOWN_INSTALLING);
							}


						}

					}else if(str.equals(DownConstants.DOWN_BROADCAST_START_NEXT_APP)){
						//readyListToDownList();
					}else if (str.equals(DownConstants.DOWN_BROADCAST_TO_UI_STATUS_DOWNLOAD_FAIL)) {
						DownloadInfo downInfo = (DownloadInfo)intent.getSerializableExtra(DownConstants.DOWN_STATUS_DOWNLOAD_DATA_KEY);
	
						Message msg = new Message();
						msg.what= MSG_ONCREATE_DIALOG;
						msg.obj = (Object)downInfo;
						handler.sendMessageDelayed(msg, 1000);

					}else if (str.equals(DownConstants.DOWN_BROADCAST_TO_UI_UPDATE)) {

					}else if(str.equals(DownConstants.DOWN_BROADCAST_TO_SERVICE_REQUEST_UPDATE)){

						//UI請求更新
						int handleCount = mHandle.size();
						for (int i = 0; i < handleCount; ++i) {
							Message m = new Message();
							m.what = DownConstants.MSG_DOWN_UPDATE_INFO;
							mHandle.get(i).sendMessage(m);
						}

					}else if(str.equals(DownConstants.DOWN_BROADCAST_TO_SERVICE_NEED_CHECK_UPDATE)){

					}else if(str.equals(DownConstants.DOWN_BROADCAST_TO_UI_STATUS_SD_NOT_ENOUGH_SPACE)){
						//ShowToast.getShowToast().createToast(mContext, mContext.getResources().getString(R.string.appstore_detail_download_nonestorge));
						/*Toast.makeText(mContext,FileUtil.getString(mContext,R.string.download_not_enough_sd_space),
								Toast.LENGTH_SHORT).show();*/
						Notice notice = Notice.makeNotice(mContext, mContext.getResources().getString(R.string.appstore_detail_download_nonestorge),
								Notice.LENGTH_SHORT);
						notice.cancelAll();
						notice.show();
					}else if(str.equals(DownConstants.DOWN_BROADCAST_TO_UI_STATUS_SD_NOT_EXIST)){
						/*Toast.makeText(mContext,FileUtil.getString(mContext,R.string.download_pls_insert_sd),
								Toast.LENGTH_SHORT).show();*/

					}else if(str.equals(DownConstants.DOWN_BROADCAST_OPENWIFI)){
						//将暂停状态变为准备状态
						//没有下载的就将准备状态进入下载状态

						/*		if(debug)
							Log.i(TAG,"=====open wifi message====");
						pauseToReady();
						if(debug)
							Log.i(TAG,"=====queryDowningListCount===="+DownLoadDB.queryDowningListCount());
						if(DownLoadDB.queryDowningListCount()<=0){
							readyListToDownList();
						}*/
					}
				}


				//mDownloadNotification.downingNotification();

			} else if (intent.getAction().equals(Intent.ACTION_PACKAGE_ADDED)) {
				/*Uri data = intent.getData();
					String pkgName = data.getEncodedSchemeSpecificPart();
					DownloadInfo info = DownLoadDB.queryADownRecord(pkgName);
					if(info != null){
						DownLoadDB.deleteADownLoad(info.getAppCode());
                    	FileUtil.deleteFile(info.getDownApkPath());

                    	InstallingTable.insertAInstalled(info);   	
					}
					InstalledAppsScan.addInstalledAppInfo(mContext, pkgName);
					UpdateTable.DeleteUpdate(pkgName);
					sendDownMsgBroadCast(DownConstants.DOWN_STATUS_ACTION, DownConstants.DOWN_STATUS_DOWNLOAD_STATUS_KEY, DownConstants.DOWN_BROADCAST_TO_UI_INSTALLED,
							DownConstants.DOWN_STATUS_DOWNLOAD_DATA_KEY, null);	*/
				Log.v(TAG, "bbbbbbb");
				Uri  data = intent.getData();
				String pkgName = data.getEncodedSchemeSpecificPart();
				Message msg = new Message();
				msg.what= MSG_INSTALL_APK_SUC;
				msg.obj = (Object)pkgName;
				handler.sendMessageDelayed(msg, 2000);

			} else if (intent.getAction().equals(Intent.ACTION_PACKAGE_REMOVED)) {
				/*Uri data = intent.getData();
					String pkgName = data.getEncodedSchemeSpecificPart();
					//InstalledAppsScan.removeInstalledAppInfo(pkgName);
					InstallingTable.deleteAInstalled(pkgName);
					UpdateTable.DeleteUpdate(pkgName);

					sendDownMsgBroadCast(DownConstants.DOWN_STATUS_ACTION, DownConstants.DOWN_STATUS_DOWNLOAD_STATUS_KEY, DownConstants.DOWN_BROADCAST_TO_UI_REMOVED,
							DownConstants.DOWN_STATUS_DOWNLOAD_DATA_KEY, null);*/
				Uri data = intent.getData();
				String pkgName = data.getEncodedSchemeSpecificPart();
				Message msg = new Message();
				msg.what= MSG_UNINSTALL_APK_SUC;
				msg.obj = (Object)pkgName;
				handler.sendMessageDelayed(msg, 2000);

			}else if (intent.getAction().equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
				String reason = intent.getStringExtra("reason");
				if (reason != null) {
					if (reason.equals("homekey")) {
						if(dialog != null && dialog.isShowing()){
							dialog.dismiss();
						}
						//stopBackDownload();//20141203 按home不关掉下载 
					}
				}
			}

		}
	}



	/**
	 * 发广播函数
	 * @author liyingying
	 * @param action action
	 * @param extraKey 状态key
	 * @param status 状态
	 * @param valueKey 信息key
	 * @param info 信息
	 */
	private void sendDownMsgBroadCast(String action, String extraKey, String status, String valueKey, DownloadInfo info) {
		Intent intent = new Intent();
		intent.setAction(action);
		if(extraKey != null){
			Bundle bundle = new Bundle();
			bundle.putString(extraKey, status);
			if (info != null) {
				bundle.putSerializable(valueKey, info);
			}
			intent.putExtras(bundle);
		}
		mContext.sendBroadcast(intent);
	}


	/**
	 * 得到线程个数
	 * @author liyingying
	 * @return 线程个数
	 */
	public int getThreadPoolSize() {
		return mThreadPoolSize;
	}

	/**
	 * 设置线程个数
	 * @author liyingying
	 * @param threadPoolSize 设置线程
	 */
	public void setThreadPoolSize(int threadPoolSize) {
		this.mThreadPoolSize = threadPoolSize;
	}

	/**
	 * 下载绑定类
	 * @author liyingying
	 */
	public class DownloadBinder extends Binder {
		public DownloadManagerService getService() {
			return DownloadManagerService.this;
		}
	}

	/**
	 * 更新下载重连列表
	 * @author liyingying
	 * @param id
	 */
	protected synchronized void FailRelink(int id) {
		Integer old_count = null;

		old_count=mDownloadReLinkList.get(new Integer(id));
		if(old_count == null){
			if(debug)
				Log.i(TAG,"======FailRelink==add reload id======");
			mDownloadReLinkList.put(new Integer(id), new Integer(1));
		}else{
			int cur_count = 0;

			cur_count=old_count.intValue();
			cur_count++;
			if(cur_count>DOWNLOAD_RELINK_COUNT){
				if(debug)
					Log.i(TAG,"======FailRelink===remove=====");
				mDownloadReLinkList.remove(new Integer(id));
			}else{
				if(debug)
					Log.i(TAG,"======FailRelink========cur_count= "+cur_count);
				mDownloadReLinkList.put(new Integer(id), new Integer(cur_count));
			}
		}

	}

	/**
	 * 是否需要重连
	 * @author liyingying
	 * @param id
	 */
	protected synchronized boolean needRelink(int id) {	
		Integer old_count = null;
		boolean nRet = false;

		old_count=mDownloadReLinkList.get(new Integer(id));
		if(old_count != null){
			if(old_count.intValue() <= DOWNLOAD_RELINK_COUNT){
				nRet = true;
			}
		}else{

		}

		return nRet;
	}

	/**
	 * 删除下载重连
	 * @author liyingying
	 * @param id
	 */
	protected synchronized void RemoveRelink(int id) {
		if(debug)
			Log.i(TAG,"======RemoveRelink========");
		//mDownloadReLinkList.remove(new Integer(id));
	}

	/**
	 * 所有pause到准备
	 * @author liyingying
	 */
	private void pauseToReady(){
		Cursor mCursor;

		mCursor = DownLoadDB.queryPauseList();
		if (mCursor != null) {
			int count = mCursor.getCount();
			for (int i = 0; i < count; ++i) {
				DownloadInfo info = DownLoadDB.getARecord(mCursor, i);
				if (info != null) {
					info.setDownStatus(DownConstants.STATUS_DOWN_BREAKPOINT);
					DownLoadDB.modifyDownLoadStatus(info.getAppCode(),
							info.getDownStatus());
				}
			}
		}

		if(mCursor !=null){
			mCursor.close();
			mCursor=null;
		}
	}

	private boolean isSystemApp(){
		boolean nRet = false;
		try {
			PackageManager pm = mContext.getPackageManager();
			PackageInfo packageInfo = pm.getPackageInfo(mContext.getPackageName(),PackageManager.GET_PERMISSIONS);
			if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) <= 0){
				//安装应用
				nRet = false;
			}else {
				//系统应用
				nRet = true;
			}
		} catch (Exception e) {
		}
		return nRet;
	}


	/*得到versioncode apk包里面版本超过整形打完包后appcode会变*/
	public String getVerCode(String pkg){
		String  verCode= null;
		try {
			PackageManager manager = mContext.getPackageManager();
			PackageInfo info = manager.getPackageInfo(pkg, 0);
			verCode = info.versionCode+"";
		} catch (Exception e) {
			return null;
		}
		return verCode;
	}
	
	/*public String getVerCode(String pkg){
		String  verCode= null;
		try {
			PackageManager manager = mContext.getPackageManager();
			PackageInfo info = manager.getPackageInfo(pkg, 0);
			verCode = mXmlParser.Load(info);
		} catch (Exception e) {
			return null;
		}
		return verCode;
	}*/





	/**
	 * 下载线程
	 * @author liyingying
	 */
	public class DownloadThread extends Thread {
		// 设置下载buf
		private static final int BUFFER_SIZE = 100 * 1024;
		// 下载信息
		private DownloadInfo mDownloadinfo;
		// 请求超时
		public static final int REQUEST_TIMEOUT=30000;
		// 读取超时
		public static final int READ_TIMEOUT=60000; 
		// URL
		HttpURLConnection connection = null;
		// 下载流
		BufferedInputStream bis = null;			
		// 下载文件
		RandomAccessFile fos = null;			
		// 下载buf
		byte[] buf = new byte[BUFFER_SIZE];			
		// 当前文件进度
		int fileProgress = 0;			
		//需要下载大小
		int needDownSize = 0;				
		// 达到一定次数才通知ui
		int msgtoUiFlag = 0;
		//请求响应
		int responseCode = 0;
		// filter
		public static final String TAG = "======DownloadThread======";	
		// 控制打印 
		public boolean debug = false;

		/**
		 * 构造函数
		 * @author liyingying
		 * @param downinfo
		 */
		public DownloadThread(DownloadInfo downinfo) {
			this.mDownloadinfo = downinfo;
		}

		/**
		 * 线程处理函数
		 * @author liyingying
		 * @see java.lang.Thread#run()
		 */
		public void run(){
			// 用户行为参数
			String param = "";
			try {
				startRequest();
				// 丢弃
				if(isPauseStatus()){
					return;
				}
				if (HttpURLConnection.HTTP_OK == responseCode
						||HttpsURLConnection.HTTP_PARTIAL == responseCode) {
					AckEvent();
				} else {
					throw new Exception("======getResponseCode other code======");
				}
				// 丢弃
				if(isPauseStatus()){
					closeStream();
					return;
				}
				msgtoUiFlag=0;
				while (true) {	
					// 丢弃
					if(isPauseStatus()){
						return;
					}
					if (mDownloadinfo.getDownStatus() == DownConstants.STATUS_DOWN_STOP
							|| mDownloadinfo.getDownStatus() == DownConstants.STATUS_DOWN_BREAKPOINT
							|| mDownloadinfo.getDownStatus() == DownConstants.STATUS_DOWN_PAUSE) {		
						cancelDownload();
						closeStream();
						break;
					}else {
						if(isPauseStatus()){
							closeStream();
							return;
						}
						getDataFromStream();
					}
				}
				//三星机器在这里关闭会异常
				closeStream();
			} catch (Exception e) {
				e.printStackTrace();
				// 丢弃
				if(isPauseStatus()){
					return;
				}
				downloadExceptionEvent(e);
			}finally{
				closeStream();
			}

		}

		/**
		 * 关闭流
		 * @throws Exception
		 */
		void closeStream(){
			try {
				if(bis != null){
					bis.close();
					bis=null;
				}
				if(fos != null){
					fos.close();
					fos=null;
				}
			} catch (Exception e) {
				// TODO: handle exception
			}

		}

		/**
		 * @Title: startRequest
		 * @Description: 开始请求
		 * @throws Exception
		 * @return: void
		 */
		public void startRequest() throws Exception{
			if (debug)
				Log.i(TAG, "===============download start=============");

			//String urlStr = mDownloadinfo.getDownApkUrl()+"?" + strUrlParamString + umidParam + param;
			String urlStr = mDownloadinfo.getDownloadurl();
			URL url = new URL(urlStr);		

			if (debug)
				Log.i(TAG, "downloadurl==="+url.toString());
			connection = (HttpURLConnection) url.openConnection();
			//4.0中设置httpCon.setDoOutput(true),将导致请求以post方式提交,即使设置了httpCon.setRequestMethod("GET");
			//connection.setDoOutput(true);

			connection.setRequestMethod("GET");
			connection.setUseCaches(false);
			connection.setReadTimeout(READ_TIMEOUT);
			connection.setConnectTimeout(REQUEST_TIMEOUT);
			if(mDownloadinfo.getDownCurrentBytes() > 0){
				//下载长度大于0 才会设置断点位置
				connection.setRequestProperty("Range", "bytes="
						+ mDownloadinfo.getDownCurrentBytes() + "-");
			}
			connection.setRequestProperty("Connection", "Keep-Alive");
			connection.setRequestProperty("User-Agent","firmwarehttp");
			responseCode = connection.getResponseCode();
			if (debug)
				Log.i(TAG, "Threadid===" + Thread.currentThread().getId()
						+ mDownloadinfo.getAppCode() + "======="
						+ "Response code :" + responseCode);

		}

		/**
		 * @Title: AckEvent
		 * @Description: 正常响应处理
		 * @return: void
		 */
		public void AckEvent() throws Exception{
			File file = new File(mDownloadinfo.getDownApkPath());
			fos = new RandomAccessFile(file, "rw");
			fos.seek(mDownloadinfo.getDownCurrentBytes());
			needDownSize=connection.getContentLength();
			int fileSize=needDownSize;
			if (debug)
				Log.i(TAG, "Threadid==="
						+ Thread.currentThread().getId()
						+ mDownloadinfo.getAppCode()
						+ "=======" + "fileSize=" + fileSize);
			mDownloadinfo.setDownTotalBytes(fileSize);
			DownLoadDB.modifyDownLoadTotalSize(mDownloadinfo
					.getAppCode(), mDownloadinfo
					.getDownTotalBytes());

			// 没有取到文件大小 
			if (fileSize <= 0) {
				throw new Exception("======fileSize<=0======");
			}else{
				if(fileSize>(FileUtil.getAvailaleSize()-retainSize)){
					throw new Exception("======sdcard space not enough======");
				}		
			}

			bis = new BufferedInputStream(connection.getInputStream());
			if (debug)
				Log.i(TAG, "Threadid==="
						+ Thread.currentThread().getId()
						+ mDownloadinfo.getAppCode()
						+ "=======" + "responseCode=" + responseCode
						+ ",downCurrentBytes="
						+ mDownloadinfo.getDownCurrentBytes()
						+ ",TotalBytes="
						+ mDownloadinfo.getDownTotalBytes());

			fileProgress = mDownloadinfo.getDownCurrentBytes();

			// 下载开始  这里代码是必须的
			DownLoadDB.modifyDownLoadStatus(mDownloadinfo
					.getAppCode(),
					DownConstants.STATUS_DOWN_DOWNING);

			sendDownMsgBroadCast(DownConstants.DOWN_MANAGE_ACTION, DownConstants.DOWN_STATUS_DOWNLOAD_STATUS_KEY, DownConstants.DOWN_BROADCAST_TO_UI_UPDATE,
					DownConstants.DOWN_STATUS_DOWNLOAD_DATA_KEY, mDownloadinfo);
			sendDownMsgBroadCast(DownConstants.DOWN_STATUS_ACTION, DownConstants.DOWN_STATUS_DOWNLOAD_STATUS_KEY, DownConstants.DOWN_BROADCAST_TO_UI_UPDATE,
					DownConstants.DOWN_STATUS_DOWNLOAD_DATA_KEY, mDownloadinfo);
		}

		/**
		 * @Title: cancelDownload
		 * @Description: 主动取消下载
		 * @return: void
		 */
		public void cancelDownload()throws Exception{
			if (debug)
				Log.i(TAG, "Threadid==="
						+ Thread.currentThread().getId()
						+ mDownloadinfo.getAppCode()
						+ "=======" + "curstatus="
						+ DownConstants.STATUS_DOWN_STOP);
			DownLoadDB.modifyDownLoadStatus(mDownloadinfo
					.getAppCode(),
					mDownloadinfo.getDownStatus());
			removeThread(mDownloadinfo.getAppCode());
			if(mDownloadinfo.getDownStatus() == DownConstants.STATUS_DOWN_STOP){
				DownLoadDB.deleteADownLoad(mDownloadinfo
						.getAppCode());
			}
			sendDownMsgBroadCast(DownConstants.DOWN_MANAGE_ACTION, DownConstants.DOWN_STATUS_DOWNLOAD_STATUS_KEY, DownConstants.DOWN_BROADCAST_TO_UI_UPDATE,
					DownConstants.DOWN_STATUS_DOWNLOAD_DATA_KEY, mDownloadinfo);
			sendDownMsgBroadCast(DownConstants.DOWN_STATUS_ACTION, DownConstants.DOWN_STATUS_DOWNLOAD_STATUS_KEY, DownConstants.DOWN_BROADCAST_TO_UI_UPDATE,
					DownConstants.DOWN_STATUS_DOWNLOAD_DATA_KEY, mDownloadinfo);
		}

		/**
		 * @Title: downloadExceptionEvent
		 * @Description: 所有下载异常处理
		 * @return: void
		 */
		public void downloadExceptionEvent(Exception e){

			// 下载出错 
			if (debug) {
				if (NotNull(mDownloadinfo)) {
					Log.i(TAG ,"Threadid==="
							+ Thread.currentThread().getId()
							+ mDownloadinfo.getAppCode()
							+ "===httprequest====" + "Exception"+e.toString());
				}

			}	

			//三星机器关闭下载流会异常 在这里做异常处理
			if(mDownloadinfo.getDownStatus() == DownConstants.STATUS_DOWN_STOP){
				DownLoadDB.deleteADownLoad(mDownloadinfo
						.getAppCode());
				removeThread(mDownloadinfo.getAppCode());

				sendDownMsgBroadCast(DownConstants.DOWN_MANAGE_ACTION, DownConstants.DOWN_STATUS_DOWNLOAD_STATUS_KEY, DownConstants.DOWN_BROADCAST_TO_UI_UPDATE,
						DownConstants.DOWN_STATUS_DOWNLOAD_DATA_KEY, mDownloadinfo);
				sendDownMsgBroadCast(DownConstants.DOWN_STATUS_ACTION, DownConstants.DOWN_STATUS_DOWNLOAD_STATUS_KEY, DownConstants.DOWN_BROADCAST_TO_UI_UPDATE,
						DownConstants.DOWN_STATUS_DOWNLOAD_DATA_KEY, mDownloadinfo);

				Bi.sendBiMsg(Bi.BICT_FIRMWARE, 
						Bi.BICC_DOWNLOAD_RESULT, 
						mDownloadinfo.getAppCode(),
						mDownloadinfo.getVersion(),
						PublicFun.getCurrentLauguage(mContext),
						"1",
						AppInfoConstant.APP_ID, 
						PublicFun.getsVersionName(),
						String.valueOf(PublicFun.getsAppSource()),
						PublicFun.getsAppSourceId(),
						PublicFun.getsAppSourceName());
				return;
			}

			if(mDownloadinfo.getDownCurrentBytes() >= mDownloadinfo
					.getDownTotalBytes()&& mDownloadinfo.getDownTotalBytes()!=-1){
				//已经下载完 但是去请求断点续传会出现的异常
				DownLoadDB.modifyDownLoadStatus(mDownloadinfo.getAppCode(),
						DownConstants.STATUS_DOWN_DOWNCOMPLETE);
				removeThread(mDownloadinfo.getAppCode());

				sendDownMsgBroadCast(DownConstants.DOWN_MANAGE_ACTION, DownConstants.DOWN_STATUS_DOWNLOAD_STATUS_KEY, DownConstants.DOWN_BROADCAST_TO_UI_STATUS_DOWN_COMPLETE,
						DownConstants.DOWN_STATUS_DOWNLOAD_DATA_KEY, mDownloadinfo);
				sendDownMsgBroadCast(DownConstants.DOWN_STATUS_ACTION, DownConstants.DOWN_STATUS_DOWNLOAD_STATUS_KEY, DownConstants.DOWN_BROADCAST_TO_UI_STATUS_DOWN_COMPLETE,
						DownConstants.DOWN_STATUS_DOWNLOAD_DATA_KEY, mDownloadinfo);
				
			}else{
				DownLoadDB.modifyDownLoadStatus(mDownloadinfo
						.getAppCode(),
						DownConstants.STATUS_DOWN_BREAKPOINT);
				removeThread(mDownloadinfo.getAppCode());

				if(!FileUtil.isSdCardExist()){
					// sd卡不存在
					DownLoadDB.deleteADownLoad(mDownloadinfo
							.getAppCode());
					removeThread(mDownloadinfo.getAppCode());
					FileUtil.deleteFile(mDownloadinfo.getDownApkPath());
					
					sendDownMsgBroadCast(DownConstants.DOWN_MANAGE_ACTION, DownConstants.DOWN_STATUS_DOWNLOAD_STATUS_KEY, DownConstants.DOWN_BROADCAST_TO_UI_STATUS_SD_NOT_EXIST,
							DownConstants.DOWN_STATUS_DOWNLOAD_DATA_KEY, mDownloadinfo);
					sendDownMsgBroadCast(DownConstants.DOWN_STATUS_ACTION, DownConstants.DOWN_STATUS_DOWNLOAD_STATUS_KEY, DownConstants.DOWN_BROADCAST_TO_UI_STATUS_SD_NOT_EXIST,
							DownConstants.DOWN_STATUS_DOWNLOAD_DATA_KEY, mDownloadinfo);
				}else{	

					if(needDownSize>(FileUtil.getAvailaleSize()-retainSize) || FileUtil.getAvailaleSize() < retainSize){
						Log.v("needDownSize:", needDownSize+"");
						Log.v("getAvailaleSize:", "sd卡没有足够空间"+FileUtil.getAvailaleSize());
						// sd卡没有足够空间
						DownLoadDB.deleteADownLoad(mDownloadinfo
								.getAppCode());
						removeThread(mDownloadinfo.getAppCode());
						FileUtil.deleteFile(mDownloadinfo.getDownApkPath());

						sendDownMsgBroadCast(DownConstants.DOWN_MANAGE_ACTION, DownConstants.DOWN_STATUS_DOWNLOAD_STATUS_KEY, DownConstants.DOWN_BROADCAST_TO_UI_STATUS_SD_NOT_ENOUGH_SPACE,
								DownConstants.DOWN_STATUS_DOWNLOAD_DATA_KEY, mDownloadinfo);
						sendDownMsgBroadCast(DownConstants.DOWN_STATUS_ACTION, DownConstants.DOWN_STATUS_DOWNLOAD_STATUS_KEY, DownConstants.DOWN_BROADCAST_TO_UI_STATUS_SD_NOT_ENOUGH_SPACE,
								DownConstants.DOWN_STATUS_DOWNLOAD_DATA_KEY, mDownloadinfo);
						//ShowToast.getShowToast().createToast(mContext, mContext.getResources().getString(R.string.download_error_nonestorge));

					}else{		
						removeThread(mDownloadinfo.getAppCode());
						sendDownMsgBroadCast(DownConstants.DOWN_MANAGE_ACTION,DownConstants.DOWN_STATUS_DOWNLOAD_STATUS_KEY,DownConstants.DOWN_BROADCAST_TO_UI_STATUS_DOWNLOAD_FAIL,
									DownConstants.DOWN_STATUS_DOWNLOAD_DATA_KEY,
									mDownloadinfo);
						sendDownMsgBroadCast(DownConstants.DOWN_STATUS_ACTION,DownConstants.DOWN_STATUS_DOWNLOAD_STATUS_KEY,DownConstants.DOWN_BROADCAST_TO_UI_STATUS_DOWNLOAD_FAIL,
									DownConstants.DOWN_STATUS_DOWNLOAD_DATA_KEY,
									mDownloadinfo);
					}
				}	
			}
		}

		/**
		 * @Title: getDataFromStream
		 * @Description: 流循环读数据
		 * @throws Exception
		 * @return: void
		 */
		public void getDataFromStream() throws Exception{

			// 怎么取到都是小于100K 不管BUFFER_SIZE设置多大
			int len = bis.read(buf, 0, BUFFER_SIZE);
			if (debug)
				Log.i(TAG, "Threadid==="
						+ Thread.currentThread().getId()
						+ "=========len============"+len);
			if (len == -1) {
				// 下载完 
				if (mDownloadinfo.getDownCurrentBytes() >= mDownloadinfo.getDownTotalBytes()
						&& mDownloadinfo.getDownTotalBytes() > 0) {
					throw new Exception("========= data complete 111========");
				} else {
					throw new Exception("=========get data null========");
				}

			}

			// 下载正常 
			fos.write(buf, 0, len);
			fileProgress += len;
			mDownloadinfo.setDownCurrentBytes(fileProgress);
			DownLoadDB.modifyDownLoadProgress(mDownloadinfo
					.getAppCode(), mDownloadinfo
					.getDownCurrentBytes());


			// 判断是否下载完
			if (mDownloadinfo.getDownCurrentBytes() >= mDownloadinfo.getDownTotalBytes()
					&& mDownloadinfo.getDownTotalBytes() > 0) {
				throw new Exception("========= data complete 222========");
			}					

			if (debug)
				Log.i(TAG, "Threadid==="
						+ Thread.currentThread().getId()
						+ mDownloadinfo.getAppCode()
						+ "=======" + "Downloading===="
						+ "downCurrentBytes="
						+ mDownloadinfo.getDownCurrentBytes()
						+ ",TotalBytes="
						+ mDownloadinfo.getDownTotalBytes()
						+ ",fileProgress=" + fileProgress
						+"msgtoUi="+msgtoUiFlag);

			//根据下载数据多少进行通知
			msgtoUiFlag += len;
			if(msgtoUiFlag >= 1024*1024){
				Log.d(TAG, "msgToUi");
				msgToUi(DownConstants.MSG_DOWN_PROGRESS);
				msgtoUiFlag = 0;
			}else{	
				Log.d(TAG, "not msgToUi");
			}
			sleep(200);
			
			
			
			//点击下载多个还是会卡顿
			/*if(msgtoUiFlag>=10){
				msgToUi(DownConstants.MSG_DOWN_PROGRESS);
				msgtoUiFlag=0;
				sleep((getThreadCount()-1)*120);
			}else{
				msgtoUiFlag++;
				sleep((getThreadCount()-1)*120);
			}*/
				
			//msgToUi(DownConstants.MSG_DOWN_PROGRESS);
			
			//sleep(500);  //2014-06-23 不延时

		}

		/**
		 * 停止下载
		 * @author liyingying
		 * @param status 下载状态
		 */
		public void stopDownLoad(int status) {
			mDownloadinfo.setDownStatus(status);
			DownLoadDB.modifyDownLoadStatus(
					mDownloadinfo.getAppCode(), status);
			// 已经丢弃线程，马上删除线程
			removeThread(mDownloadinfo.getAppCode());
		}

		/**
		 * 暂停下载
		 * @author liyingying
		 * @param status 下载状态
		 */
		public void pauseDownLoad(int status) {
			if(debug)
				Log.i(TAG , "===============pauseDownLoad==================");
			mDownloadinfo.setDownStatus(status);
			// 这个是必须的 不然状态不对 
			DownLoadDB.modifyDownLoadStatus(
					mDownloadinfo.getAppCode(), status);
			// 已经丢弃线程，马上删除线程
			removeThread(mDownloadinfo.getAppCode());
		}

		/**
		 *  得到线程下载状态
		 * @author liyingying
		 * @return	下载状态
		 */
		public int getDownLoadStatus() {
			return mDownloadinfo.getDownStatus();
		}

		/**
		 * 是不是从开始下载
		 * @author liyingying
		 * @return true 从开始下载 反之不是
		 */
		private boolean isBegin() {
			if (mDownloadinfo.getDownCurrentBytes() <= 0) {
				return true;
			} else {
				return false;
			}
		}

		/**
		 * 传消息给UI
		 * @author liyingying
		 * @param msgStatus 接收消息ui的msg
		 */
		private void msgToUi(int msgStatus) {
			int handleCount = mHandle.size();
			Log.d(TAG, "handleCount:"+handleCount);
			for (int i = 0; i < handleCount; ++i) {
				Message m = new Message();
				m.what = msgStatus;
				m.obj = mDownloadinfo;
				mHandle.get(i).sendMessage(m);
			}
		}

		/**
		 * 成员结构体不为空
		 * @author liyingying
		 * @param object
		 * @return true 不为空，反之为空
		 */
		private boolean NotNull(Object object) {/*  */
			if (object != null) {
				return true;
			} else {
				return false;
			}
		}

		/**
		 * 是暂停下载状态就要跳出 丢弃线程
		 * @author liyingying
		 * @return true是暂停状态 反之不是
		 */
		private boolean isPauseStatus(){
			boolean bFlag=false;
			if(getDownLoadStatus()==DownConstants.STATUS_DOWN_PAUSE){
				bFlag = true;
				if(debug)
					Log.i(TAG , "===============need give up thread==================");
			}

			return bFlag;
		}


	}


}
