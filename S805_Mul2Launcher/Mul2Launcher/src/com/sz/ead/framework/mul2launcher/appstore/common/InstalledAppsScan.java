package com.sz.ead.framework.mul2launcher.appstore.common;


import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.sz.ead.framework.mul2launcher.db.InstalledTable;
import com.sz.ead.framework.mul2launcher.db.InstallingTable;
import com.sz.ead.framework.mul2launcher.util.FileUtil;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;



public class InstalledAppsScan {

    //已安装的应用列表 包括 系统应用
	//private static List<AppInfo> sAllAppList = null;20141210 注释掉系统应用
	//安装app列表
	private static List<AppInfo> sInstallAppList = null;
	
	private static ScanThread thread = null;
	
	public static void scanApp(Context context){
		//if(sAllAppList == null || sAllAppList.size()<=0){
			thread = new ScanThread(context);
			thread.start();
		//}
	}
	

	 /**
     * 扫描本地已安装的应用
     * 扫描结果存储在sAppList中
     * 
     * @param context 应用上下文
     * @return List<AppInfo>
     */
	public static void scanInstalledApps(Context context) {
		/*if(sAllAppList == null){20141210 不用多次加入info
			sAllAppList = new ArrayList<AppInfo>();
		}*/
		if(sInstallAppList == null){/*20141210 不用多次加入info*/
			sInstallAppList = new ArrayList<AppInfo>();
		}
		

		PackageManager pm = context.getPackageManager();
		Intent launcherIntent = new Intent(Intent.ACTION_MAIN);
        launcherIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> appInfo = context.getPackageManager().queryIntentActivities(launcherIntent, 0);   
        Collections.sort(appInfo,new ResolveInfo.DisplayNameComparator(context.getPackageManager()));
          
        try {
            for (ResolveInfo pInfo: appInfo){
            	if(!pInfo.activityInfo.packageName.equals(context.getPackageName())){ 	
            		AppInfo existAppInfo = isAddAppInfo(context, pInfo.activityInfo.packageName);
            		if(existAppInfo == null){/*20141210 不用多次加入info*/
            			//不是自己本身包
            			PackageInfo packageInfo = pm.getPackageInfo(pInfo.activityInfo.packageName,
            					PackageManager.GET_PERMISSIONS);
  
            			AppInfo info = createAppInfo(context, packageInfo);
                		 if ((pInfo.activityInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) <= 0){
                			 //安装应用
                			 sInstallAppList.add(info);
                			 
                			 if(!InstallingTable.isInstalled(info.mPackageName)){
                				 InstallingTable.insertNoStoreInstall(info); 
                			 }
                		 }else {
        					//系统应用
        				}
                		 //sAllAppList.add(info);
            		}else {
						if(!existAppInfo.mIsAppStorePic){
							/*更新如果之前是用应用包图，换为appstore图*/
							PackageInfo packageInfo = pm.getPackageInfo(pInfo.activityInfo.packageName,
	            					PackageManager.GET_PERMISSIONS);
							AppInfo Temp = createAppInfo(context, packageInfo);
							if(Temp.mIsAppStorePic){
								existAppInfo.mIcon = Temp.mIcon;
							}
						}
					}
            		
            	}else{
            		
            	}
            }
            
            /*for (AppInfo appInfo2 : sInstallAppList) {
            	try {
            		PackageInfo packageInfo = pm.getPackageInfo(appInfo2.mPackageName, PackageManager.GET_PERMISSIONS);
        			if(packageInfo == null){
        				removeInstalledAppInfo(appInfo2.mPackageName);
        			}
				} catch (Exception e) {
					removeInstalledAppInfo(appInfo2.mPackageName);
				}
    			
			}*/
		} catch (Exception e) {
			
		}
	}
	
    
    /**
     * 生成AppInfo
     * 
     * @param context 应用上下文
     * @param pInfo PackageInfo
     * @return AppInfo
     */
    public static AppInfo createAppInfo(Context context, PackageInfo pInfo) {
    	AppInfo newInfo = new AppInfo();
        try {  	
        	newInfo.mAppName = pInfo.applicationInfo.loadLabel(
    				context.getPackageManager()).toString();
    		newInfo.mPackageName = pInfo.packageName;
    		newInfo.mVersionName = pInfo.versionName;
    		newInfo.mVersionCode = pInfo.versionCode;
    		newInfo.mLastModifyDate = new Date(new File(
    				pInfo.applicationInfo.publicSourceDir).lastModified());
    		newInfo.mSize = Integer.valueOf((int) new File(
    				pInfo.applicationInfo.publicSourceDir).length());

    		/*Bitmap bitmap = null;加入取商城图片   但是只要内存存在就不要取了
    		bitmap = BitmapFactory.decodeFile(FileUtil.APPICON_PATH +"/" + pInfo.packageName + ".png");
    		if(bitmap != null){
    			newInfo.mIcon = new BitmapDrawable(bitmap);
    			newInfo.mIsAppStorePic = true;
    		}else {
    			newInfo.mIcon = pInfo.applicationInfo.loadIcon(context
        				.getPackageManager());
			}*/		
		} catch (OutOfMemoryError e) {
			newInfo.mIcon = pInfo.applicationInfo.loadIcon(context
    				.getPackageManager());
		} catch(Exception e){
			newInfo.mIcon = pInfo.applicationInfo.loadIcon(context
    				.getPackageManager());
		}
        //Log.i("DownAppIcon","createAppInfo "+newInfo.mAppName+newInfo.mIsAppStorePic);
		return newInfo;
    }
    
    

    /**
     * 
     * @param context 应用上下文
     * @return List<AppInfo>
     */
	public static List<AppInfo> getAllAppsList(Context context) {

	/*	if (sAllAppList == null) {
			scanInstalledApps(context);
		}
		return sAllAppList;*/
		if (sInstallAppList == null) {
			scanInstalledApps(context);
		}
		return sInstallAppList;
	}
	
    /**
     * 
     * @param context 应用上下文
     * @return List<AppInfo>
     */
	public static List<AppInfo> getInstallAppsList(Context context) {

		if (sInstallAppList == null) {
			scanInstalledApps(context);
		}
		return sInstallAppList;
	}
	
	
	
    /**
     * 删除本地已安装应用信息列表（sAppList）中的项
     * 
     * @param pkgName 要删除的应用信息的应用包名
     * @return boolean true(找到并删除了该应用信息)，false(没找到该应用信息)
     */
	public static boolean removeInstalledAppInfo(String pkgName) {
		/*if (sAllAppList != null){
			for(AppInfo info : sAllAppList){
				if (info != null && pkgName.compareTo(info.mPackageName) == 0){
					sAllAppList.remove(info);
					sInstallAppList.remove(info);
					return true;
				}		
			}			
		}*/
		
		if (sInstallAppList != null){
			for(AppInfo info : sInstallAppList){
				if (info != null && pkgName.compareTo(info.mPackageName) == 0){
					sInstallAppList.remove(info);
					return true;
				}		
			}			
		}
		return false;
	}
	

    /**
     * 向本地已安装应用信息列表（sAppList）添加应用信息
     * 
     * @param context 应用上下文
     * @param pkgName 要添加的应用信息的应用包名
     * @return boolean true(添加成功)，false(添加失败)
     */
	public static boolean addInstalledAppInfo(Context context, String pkgName) {
		
		/*try{
	        if (sAllAppList == null){
	        	scanInstalledApps(context);
	        	if (isInstalledApps(context,pkgName) != null) {
	        	    return true;
	        	}
	        } else {
		 	    PackageManager packageManager = context.getPackageManager();
		        PackageInfo pInfo = packageManager.getPackageInfo(pkgName,0);
		        if (pInfo != null) {
		        	AppInfo info = createAppInfo(context,pInfo);
		        	sAllAppList.add(info);
		        	sInstallAppList.add(info);
		            return true;
		        }	   	
	        }
	        
		}catch(Exception e){
			Log.e("addInstalledAppInfo",e.toString());
		}*/
		
		try{
	        if (sInstallAppList == null){
	        	scanInstalledApps(context);
	        	if (isInstalledApps(context,pkgName) != null) {
	        	    return true;
	        	}
	        } else {
		 	    PackageManager packageManager = context.getPackageManager();
		        PackageInfo pInfo = packageManager.getPackageInfo(pkgName,0);
		        if (pInfo != null) {
		        	AppInfo info = createAppInfo(context,pInfo);
		        	sInstallAppList.add(info);
		            return true;
		        }	   	
	        }
	        
		}catch(Exception e){
			Log.e("addInstalledAppInfo",e.toString());
		}
  
		return false;
	}
	
    /**
     * 判断应用是否已经被安装
     * 
     * @param context 应用上下文
     * @param pkgName 要判断应用的应用包名
     * @return boolean true(已安装)，false(未安装)
     */
	public static AppInfo isInstalledApps(Context context, String pkgName) {
		List<AppInfo> list = getAllAppsList(context);
		
		for(AppInfo info : list){
			if (info != null && pkgName.compareTo(info.mPackageName) == 0){
				return info;
			}
		}
		
		return null;
	}
	
	public static AppInfo isAddAppInfo(Context context, String pkgName){
		List<AppInfo> list = getAllAppsList(context);
		for(AppInfo info : list){
			if (info != null && pkgName.compareTo(info.mPackageName) == 0){
				return info;
			}
		}
		return null;
	}
	
	//取出Appinfo
	public static AppInfo getAppInfo(Context context, String pkgName) {
		try {
			PackageManager packageManager = context.getPackageManager();
	        PackageInfo pInfo = packageManager.getPackageInfo(pkgName,0);
	        if (pInfo != null) {
	        	AppInfo info = createAppInfo(context,pInfo);
	            return info;
	        }	   
		} catch (Exception e) {
		}	
		return null;
	}
	
	public static class ScanThread extends Thread{
		Context mContext;
		
		public ScanThread(Context context) {
			mContext = context;
		}
		@Override
		public void run() {
			super.run();
			scanInstalledApps(mContext);
		}
	}
	

}
