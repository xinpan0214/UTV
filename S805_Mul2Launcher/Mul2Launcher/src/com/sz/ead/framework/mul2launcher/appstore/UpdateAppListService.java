/**
 * Copyright © 2015GreatVision. All rights reserved.
 *
 * @Title: UpdateAppListService.java
 * @Prject: Mul2Launcher
 * @Package: com.sz.ead.framework.mul2launcher.appstore
 * @Description: 定时更新商城应用列表service
 * @author: zhaoqy  
 * @date: 2015-4-28 下午4:48:31
 */
package com.sz.ead.framework.mul2launcher.appstore;

import java.util.ArrayList;
import com.sz.ead.framework.mul2launcher.MainActivity;
import com.sz.ead.framework.mul2launcher.appstore.update.UpdateManager;
import com.sz.ead.framework.mul2launcher.dataprovider.dataitem.AppItem;
import com.sz.ead.framework.mul2launcher.dataprovider.http.UICallBack;
import com.sz.ead.framework.mul2launcher.dataprovider.packet.outpacket.OutPacket;
import com.sz.ead.framework.mul2launcher.dataprovider.requestdata.RequestData;
import com.sz.ead.framework.mul2launcher.db.DownloadTable;
import com.sz.ead.framework.mul2launcher.db.InstalledTable;
import com.sz.ead.framework.mul2launcher.util.ConstantUtil;
import com.sz.ead.framework.mul2launcher.util.LogUtil;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;

public class UpdateAppListService extends Service implements UICallBack
{
	public static final int REQUEST_APPLIST = 1; //请求应用列表
	private Context mContext;  //上下文
	private ArrayList<AppItem> mAppItems = new ArrayList<AppItem>();
	private ArrayList<AppItem> mUpdateList = new ArrayList<AppItem>();
	private int mAppSize = 18;
	private int mCurPage = 1;
	private int mTotalPage = 1;
	private int mAppCount = 0;
	private int mTime = 0;
	
	private UpdateManager mManager;
	
	@Override
	public void onCreate() 
	{
		super.onCreate();
		mAppItems.clear();
		mUpdateList.clear();
		
		mContext = this;
		mHandler.sendEmptyMessage(REQUEST_APPLIST);
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) 
	{
		return super.onStartCommand(intent, flags, startId);
	}
	
	@Override
	public void onDestroy() 
	{
		super.onDestroy();
		
		if(mHandler != null)
		{
			mHandler.removeCallbacksAndMessages(null);
		}
	}
	
	private void requestAppList(int page)
	{
		if (mCurPage == 1)
		{
			mAppItems.clear();
			mTime++;
			LogUtil.d(LogUtil.TAG, " mTime: " + mTime);
		}
		RequestData.requestData(this, mContext, ConstantUtil.MARK_APP_LIST, mAppSize, page, "");
	}
	
	@Override
	public IBinder onBind(Intent intent) 
	{
		return null;
	}

	@Override
	public void onCancel(OutPacket out, int mark) 
	{
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onSuccessful(Object in, int mark) 
	{
		try 
		{
			switch (mark) 
			{
			case ConstantUtil.MARK_APP_LIST:
			{
				ArrayList<AppItem> temp = new ArrayList<AppItem>();
				temp = (ArrayList<AppItem>) RequestData.getData(in);
				for (int i=0; i<temp.size(); i++)
				{
					AppItem newAppItem = temp.get(i);
					mAppItems.add(newAppItem);
					
					String pkgName = newAppItem.getPkgName();
					AppItem oldAppItem = InstalledTable.queryAppInfo(pkgName);
					if (oldAppItem != null)
					{
						//已安装, 检测是否可以升级
						PackageManager pm = mContext.getPackageManager();
						PackageInfo packageInfo = pm.getPackageInfo(pkgName, PackageManager.GET_PERMISSIONS);
						String localVersion = packageInfo.versionCode + "";
						String serverVersion = newAppItem.getVersion();
						LogUtil.d(LogUtil.TAG, " ++++++++++++++++++++++++++++++++++++ ");
						LogUtil.d(LogUtil.TAG, " appname: " + newAppItem.getAppName());
						LogUtil.d(LogUtil.TAG, " localVersion:  " + localVersion);
						LogUtil.d(LogUtil.TAG, " serverVersion: " + serverVersion);
						
						if (!TextUtils.isEmpty(localVersion) && !TextUtils.isEmpty(serverVersion))
						{
							if (serverVersion.compareTo(localVersion) > 0)
							{
								mUpdateList.add(newAppItem);
							}
						}
					}
				}
				
				if (mCurPage == 1)
				{
					mAppCount = RequestData.getTotal(in);
					mTotalPage = (mAppCount-1)/mAppSize + 1;
				}
				
				if (mCurPage < mTotalPage)
				{
					mCurPage++;
					requestAppList(mCurPage);
				}
				else
				{
					if (mAppItems.size() > 0)
					{
						ArrayList<AppItem> temp0 = new ArrayList<AppItem>();
						temp0 = DownloadTable.queryAppListInfo();
						if (temp0.size() > 0)
						{
							DownloadTable.deleteAllAppInfo();
						}
						DownloadTable.insertAppList(mAppItems);
						
						if (mTime > 1)
						{
							LogUtil.d(LogUtil.TAG, " sendBroadcast: " + MainActivity.ACTION_UPDATE_APPLIST);
							//应用商城更新应用列表
							Intent intent = new Intent();
							intent.setAction(MainActivity.ACTION_UPDATE_APPLIST);
							mContext.sendBroadcast(intent);
						}
					}
					//每30分钟定时取一次, (30*60)*1000
					mCurPage = 1;
					mHandler.sendEmptyMessageDelayed(REQUEST_APPLIST, 1800000 /*1800000*/);  
					
					//升级管理
					checkManager();
				}
				break;
			}
			default:
				break;
			}
		} 
		catch (Exception e) 
		{
			LogUtil.d(LogUtil.TAG, " Request appList error: " + e.toString());
			e.printStackTrace();
			mCurPage = 1;
			mHandler.sendEmptyMessageDelayed(REQUEST_APPLIST, 1800000);
		}
	}

	@Override
	public void onNetError(int responseCode, String errorDesc, OutPacket out, int mark) 
	{
		LogUtil.d(LogUtil.TAG, " onNetError ");
		mCurPage = 1;
		mHandler.sendEmptyMessageDelayed(REQUEST_APPLIST, 1800000);
	}
	
	private void checkManager()
	{
		if (mManager == null)
		{
			mManager = new UpdateManager(mContext, mUpdateList);
			mManager.start();
		}
		else
		{
			if (!mManager.isAlive())
			{
				mManager = null;
				mManager = new UpdateManager(mContext, mUpdateList);
				mManager.start();
			}
		}
	}

	@SuppressLint("HandlerLeak")
	Handler mHandler = new Handler() 
	{
		public void handleMessage(Message msg) 
		{
			switch (msg.what) 
			{
			case REQUEST_APPLIST:
			{
				requestAppList(mCurPage);
				break;
			}
			default:
				break;
			}
		}
	};
}
