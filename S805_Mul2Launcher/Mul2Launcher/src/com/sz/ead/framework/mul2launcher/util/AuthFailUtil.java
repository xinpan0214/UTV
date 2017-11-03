/**
 * Copyright © 2015GreatVision. All rights reserved.
 *
 * @Title: AuthFailUtil.java
 * @Prject: Mul2Launcher
 * @Package: com.sz.ead.framework.mul2launcher.util
 * @Description: 鉴权失败Util
 * @author: zhaoqy  
 * @date: 2015-5-6 上午10:04:54
 */
package com.sz.ead.framework.mul2launcher.util;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import com.sz.ead.framework.mul2launcher.dataprovider.dataitem.AppItem;
import com.sz.ead.framework.mul2launcher.db.InstalledTable;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;

public class AuthFailUtil 
{
	public static void killThirdApps(Context context) 
	{
		LogUtil.d(LogUtil.TAG, " +++++++++ killThirdApps +++++++++++");
		ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> runningAppInfoList = activityManager.getRunningAppProcesses();
		ArrayList<AppItem> installedAppList = InstalledTable.queryInstalledAppList();
		for (RunningAppProcessInfo pInfo : runningAppInfoList) 
		{
			LogUtil.d(LogUtil.TAG, " +++++++++ killThirdApps: " + pInfo.processName);
			for (AppItem appItem : installedAppList) 
			{
				if (pInfo.processName.equals(appItem.getPkgName())) 
				{
					Method method;
					try 
					{
						method = Class.forName("android.app.ActivityManager").getMethod("forceStopPackage", String.class);
						method.invoke(activityManager, appItem.getPkgName());
						LogUtil.d(LogUtil.TAG, " +++++++++ killThirdApps: " + appItem.getPkgName());
					} 
					catch (Exception e) 
					{
						e.printStackTrace();
					}
				}
			}
		}
	}
}
