/**
 * Copyright © 2015GreatVision. All rights reserved.
 *
 * @Title: ConstantUtil.java
 * @Prject: Mul2Launcher
 * @Package: com.sz.ead.framework.mul2launcher.util
 * @Description: 常量定义
 * @author: zhaoqy  
 * @date: 2015-4-22 下午7:32:25
 */
package com.sz.ead.framework.mul2launcher.util;

public class ConstantUtil 
{
	public static final boolean URL_LOCAL_SERVICE = false;   //是不是本地服务器 自己电脑作为服务器
	public static final int TERMAL_TYPE = 4;                 //终端类型:1:pad; 2:pc; 3:ios; 4:android
	public static boolean  UPDATE_SERVICE_IS_START = false;	// 是否启动下载应用列表下载服务
	//public static final String REQUEST_URL_MAIN_HOST = "http://sepg.qaxlist.com";  
	//public static final String REQUEST_URL_PORT = "80"; 
	//public static final String REQUEST_URL_HOST = "http://10.0.3.28/xampp/test/android/mul2Launcher/xml";
	
	public static final String REQUEST_URL_MAIN_HOST = "http://epg.sinoepg.com";  
	public static final String REQUEST_URL_PORT = "7006"; 
	public static String REQUEST_URL_HOST = ""; 
	
	public static final int MARK_APP_LIST = 1;   //应用列表
	public static final int MARK_APP_UPDATE = 2; //应用升级 
	public static final int MARK_APP_DETAIL = 3; //应用详情 
}
