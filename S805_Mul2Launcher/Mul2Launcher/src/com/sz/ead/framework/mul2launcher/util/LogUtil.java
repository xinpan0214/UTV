/**
 * Copyright © 2015GreatVision. All rights reserved.
 *
 * @Title: LogUtil.java
 * @Prject: Mul2Launcher
 * @Package: com.sz.ead.framework.mul2launcher.util
 * @Description: 打印日志
 * @author: zhaoqy  
 * @date: 2015-4-22 下午7:13:02
 */
package com.sz.ead.framework.mul2launcher.util;

import android.util.Log;

public class LogUtil 
{
	public static final String TAG = "mul2launcher";   //打印标签
	private static boolean sVerbose = true; //冗长
	private static boolean sDebug = true;   //调试
	private static boolean sInfo = true;    //信息
	private static boolean sWarn = true;    //警告
	private static boolean sError = true;   //错误
	
	public static void v(String tag, String msg)
	{
		if (sVerbose)
		{
			Log.v(tag, msg);
		}
	}
	
	public static void d(String tag, String msg)
	{
		if (sDebug)
		{
			Log.d(tag, msg);
		}
	}
	
	public static void i(String tag, String msg)
	{
		if (sInfo)
		{
			Log.i(tag, msg);
		}
	}
	
	public static void w(String tag, String msg)
	{
		if (sWarn)
		{
			Log.w(tag, msg);
		}
	}
	
	public static void e(String tag, String msg)
	{
		if (sError)
		{
			Log.e(tag, msg);
		}
	}
}
