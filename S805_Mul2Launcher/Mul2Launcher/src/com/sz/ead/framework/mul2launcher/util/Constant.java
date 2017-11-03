package com.sz.ead.framework.mul2launcher.util;

import android.content.Context;
import android.setting.settingservice.SettingManager;

public class Constant {
	// 开关,debug log信息开关
	public static final boolean DEBUG = Boolean.FALSE;
	
	//是不是本地服务器 自己电脑作为服务器
	public static final boolean URL_LOCAL_SERVICE = false;
	//是不是ip地址
	public static final boolean URL_IP_SERVICE = false;
	
	public static String REQUEST_URL_HOST = URL_LOCAL_SERVICE ? "http://10.0.3.17/xampp/test/multi/applist.xml"
			: (URL_IP_SERVICE ? "http://10.0.1.217:8899" : "http://");
//	public static String REQUEST_URL_HOST = "http://10.0.1.217:7777";
	// 网络请求的超时时间设置
	public static final int NETWORK_TIMEOUT = 15000;
	
	private static SettingManager sm;
	
	public static final String STATUS_INSTALLED_ACTION = "com.sz.ead.framework.appstore.activity.ActivityAppDetails.statusInstalled";
	public static final String PACKAGENAME = "pkgName";

	
	public static String generateGeneralParam(Context context) {
		StringBuffer sb = new StringBuffer();
//		sb.append("storeid=");
//		sb.append(AppInfoConstant.APP_ID);
//		sb.append("&");
		sb.append("mac=");
		sb.append(getMac(context));
		sb.append("&");
		sb.append("cpuid=");
		sb.append(getCPUID(context));
		sb.append("&");
		sb.append("accesscode=");
		sb.append(getAccesscode(context));
		sb.append("&");
		sb.append("AccessCodeHash=");
		sb.append(getAccessCodeHash(context));
		sb.append("&");
		sb.append("version=");
		sb.append(getVersion(context));
		sb.append("&");
		sb.append("lang=");
		sb.append(getCurrentLauguage(context));
		sb.append("&");
		sb.append("oem=");
		sb.append(getOem(context));
		sb.append("&");
		sb.append("model=");
		sb.append(getModel(context));
	
		return sb.toString();
	}
	
	public static String getCurrentLauguage(Context context) {
		sm = (SettingManager) context.getSystemService(Context.SETTING_SERVICE);
		String lang_index = sm.getCurLang();
		/*String lang_index = "en";*/
		return lang_index;
	}
	
	public static String getMac(Context context) {
		sm = (SettingManager) context.getSystemService(Context.SETTING_SERVICE);
		String mac = sm.getMac();
		return mac;
	}
	
	public static String getCPUID(Context context) {
		sm = (SettingManager) context.getSystemService(Context.SETTING_SERVICE);
		String cpuid = sm.getCPUID();
		return cpuid;
	}
	
	public static String getAccesscode(Context context) {
		sm = (SettingManager) context.getSystemService(Context.SETTING_SERVICE);
		String accesscode = sm.getAccessCode();
		return accesscode;
	}
	
	public static String getAccessCodeHash(Context context) {
		sm = (SettingManager) context.getSystemService(Context.SETTING_SERVICE);
		String accesscode = sm.getAccessCodeHash();
		return accesscode;
	}
	
	public static String getVersion(Context context) {
		sm = (SettingManager) context.getSystemService(Context.SETTING_SERVICE);
		String version = sm.getSfVersion();
		return version;
	}
	
	public static String getOem(Context context) {
		sm = (SettingManager) context.getSystemService(Context.SETTING_SERVICE);
		String oem = sm.getOem();
		return oem;
	}
	
	public static String getModel(Context context) {
		sm = (SettingManager) context.getSystemService(Context.SETTING_SERVICE);
		String model = sm.getHwVersion();
		return model;
	}
}
