package com.sz.ead.framework.mul2launcher.appstore.dialog;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.setting.settingservice.SettingManager;

public class PublicFun {
	public static String sPackageName = "";
	public static String sVersionName = "";   //appName
	public static String sVersionCode = "";
	public static String sAppSourceName = "";
	public static int sAppSource = 0;
	public static int sAppUpdataSource = 0;


	public static String sAppSourceId = "";


	public static final int 		UPDATA_FROM_MANAGE			   = 1;		   //应用管理
	public static final int 		UPDATA_FROM_DETAILS			   = 2;		   //应用详情


	public static final int      SOURCE_NULL                    = 0;        //未知
	public static final int 	    SOURCE_HOTRECOMMEND            = 1;        //热门推荐
	public static final int		SOURCE_NEWRECOMMEND            = 2;        //最新应用
	public static final int		SOURCE_THEMATIC                = 3;        //专题应用
	public static final int		SOURCE_VEDIOBILLBOARD          = 4;        //影视排行
	public static final int		SOURCE_GAMEBILLBOARD           = 5;        //游戏排行
	public static final int		SOURCE_CLASSIFICATION          = 6;        //分类
	public static final int		SOURCE_SIMILAR                 = 7;        //同类推荐应用
	public static final int		SOURCE_SEARCH                  = 8;        //关键词搜索
	public static final int		SOURCE_HOTWORD                 = 9;        //热词搜索
	public static final int		SOURCE_SEARCHRECOMMEND         = 10;       //搜索推荐应用
	
	private static SettingManager sm;

	public static int getsAppUpdataSource() {
		return sAppUpdataSource;
	}


	public static void setsAppUpdataSource(int sAppUpdataSource) {
		PublicFun.sAppUpdataSource = sAppUpdataSource;
	}

	public static String getsAppSourceId() {
		return sAppSourceId;
	}


	public static void setsAppSourceId(String sAppSourceId) {
		PublicFun.sAppSourceId = sAppSourceId;
	}


	public static String getsAppSourceName() {
		return sAppSourceName;
	}


	public static void setsAppSourceName(String sAppSourceName) {
		PublicFun.sAppSourceName = sAppSourceName;
	}

	public static int getsAppSource() {
		return sAppSource;
	}


	public static void setsAppSource(int sAppSource) {
		PublicFun.sAppSource = sAppSource;
	}


	public static String getsPackageName() {
		return sPackageName;
	}


	public static void setsPackageName(String sPackageName) {
		PublicFun.sPackageName = sPackageName;
	}


	public static String getsVersionName() {
		return sVersionName;
	}


	public static void setsVersionName(String sVersionName) {
		PublicFun.sVersionName = sVersionName;
	}


	public static String getsVersionCode() {
		return sVersionCode;
	}


	public static void setsVersionCode(String sVersionCode) {
		PublicFun.sVersionCode = sVersionCode;
	}

	public static void GetAppstoreInfo(Context context){
		PackageInfo info;
		  PackageManager pManager = context.getPackageManager();
		try {
			info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			// 当前应用的版本名称
//			sVersionName = info.versionName;
			sVersionName = pManager.getApplicationLabel(info.applicationInfo).toString();
			// 当前版本的版本号
			sVersionCode = info.versionCode+"";
			// 当前版本的包名
			sPackageName = info.packageName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static String getCurrentLauguage(Context context) {
		sm = (SettingManager) context.getSystemService(Context.SETTING_SERVICE);
		String lang_index = sm.getCurLang();
		return lang_index;
	}
	
	public static boolean isServiceExisted(Context context, String className) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList = activityManager.getRunningServices(Integer.MAX_VALUE);

        if(!(serviceList.size() > 0)) {
            return false;
        }

        for(int i = 0; i < serviceList.size(); i++) {
            RunningServiceInfo serviceInfo = serviceList.get(i);
            ComponentName serviceName = serviceInfo.service;

            if(serviceName.getClassName().equals(className)) {
                return true;
            }
        }
        return false;
    }
}
