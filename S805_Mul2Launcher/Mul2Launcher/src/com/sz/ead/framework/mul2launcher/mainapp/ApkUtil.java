/**
 * Copyright © 2015GreatVision. All rights reserved.
 *
 * @Title: ApkUtil.java
 * @Prject: Mul2Launcher
 * @Package: com.sz.ead.framework.mul2launcher.mainapp
 * @Description: 应用包Util
 * @author: zhaoqy  
 * @date: 2015-4-28 上午10:06:11
 */
package com.sz.ead.framework.mul2launcher.mainapp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import com.sz.ead.framework.mul2launcher.dataprovider.dataitem.AppItem;
import com.sz.ead.framework.mul2launcher.util.LogUtil;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.net.Uri;

public class ApkUtil 
{
	/**
	 * 
	 * @Title: uninstallApk
	 * @Description: 卸载应用
	 * @param context
	 * @param packageName
	 * @return: void
	 */
	public static void uninstallApk(Context context, String packageName) 
	{
		LogUtil.d(LogUtil.TAG, " uninstall apk: " + packageName);
		try 
		{
			Intent intent = new Intent();
			intent.setAction(Intent.ACTION_VIEW);
			intent.setDataAndType(Uri.parse("package:" + packageName), "on_application/vnd.android.package-archive");
			context.startService(intent);
		} 
		catch (Exception e) 
		{
			LogUtil.e(LogUtil.TAG, " uninstall apk error: " + e.toString());
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @Title: getInstalledApp
	 * @Description: 获取已安装的app列表
	 * @param context
	 * @param type
	 * @return
	 * @return: List<AppItem>
	 */
	public static ArrayList<AppItem> getInstalledApp(Context context) 
	{
		if (context == null) 
		{
			return null;
		}
		
		ArrayList<AppItem> apps = new ArrayList<AppItem>();
		apps.clear();
		PackageManager pm = context.getPackageManager();
		//List<PackageInfo> packages = pm.getInstalledPackages(0);
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_LAUNCHER);
		List<ResolveInfo> appInfo = context.getPackageManager().queryIntentActivities(intent, 0);
		Collections.sort(appInfo, new ResolveInfo.DisplayNameComparator(context.getPackageManager()));
		
		for (int i=0; i<appInfo.size(); i++) 
		{
			ResolveInfo pkginfo = appInfo.get(i);
			AppItem appItem = new AppItem();
			//appItem.setAppName(pm.getApplicationLabel(pkginfo.applicationInfo).toString());
			//appItem.setPkgName(pkginfo.packageName);
			//appItem.setIconBitmap(pm.getApplicationIcon(pkginfo.applicationInfo));
			appItem.setAppName(pkginfo.loadLabel(pm).toString());
			appItem.setPkgName(pkginfo.activityInfo.packageName);
			appItem.setIconBitmap(pkginfo.loadIcon(pm));
			apps.add(appItem);
		}
		return apps;
	}
	
	public static List<ResolveInfo> getInstalledPackageList(Context context) 
	{
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_LAUNCHER);
		List<ResolveInfo> appInfo = context.getPackageManager().queryIntentActivities(intent, 0);
		Collections.sort(appInfo, new ResolveInfo.DisplayNameComparator(context.getPackageManager()));
		return appInfo;
	}

	
	/**
	 * 
	 * @Title: getAppInfoByPackageName
	 * @Description: 根据包名获取app信息
	 * @param context
	 * @param packageName
	 * @return
	 * @return: AppItem
	 */
	public static AppItem getAppInfoByPackageName(Context context, String packageName) 
	{	
		try 
		{
			PackageManager pm = context.getPackageManager();
			PackageInfo pkginfo = pm.getPackageInfo(packageName, 0);
			ApplicationInfo appinfo = pm.getApplicationInfo(packageName, 0);
			
			AppItem appItem = new AppItem();
			appItem.setAppName(appinfo.loadLabel(pm).toString());
			appItem.setPkgName(pkginfo.packageName);
			appItem.setIconBitmap(appinfo.loadIcon(pm));
			return appItem;		
		} 
		catch (NameNotFoundException e) 
		{
			e.printStackTrace();
		}
		return null;
	}
}
