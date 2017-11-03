/**
 * Copyright © 2015GreatVision. All rights reserved.
 *
 * @Title: StbInfo.java
 * @Prject: Mul2Launcher
 * @Package: com.sz.ead.framework.mul2launcher.util
 * @Description: 机顶盒信息
 * @author: zhaoqy  
 * @date: 2015-4-22 下午8:10:19
 */
package com.sz.ead.framework.mul2launcher.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.setting.settingservice.SettingManager;

public class StbInfo 
{
	/**
	 * 
	 * @Title: getCPUID
	 * @Description: 获取cpuid
	 * @return
	 * @return: String
	 */
	@SuppressLint("DefaultLocale")
	public static String getCpuid(Context context) 
    {  
		String cpuid = "";
		if (context != null)
		{
			SettingManager setting; 
			try
			{
				setting = ((SettingManager)context.getSystemService(Context.SETTING_SERVICE));
				cpuid = setting.getCPUID();
			}
			catch (Error e)
			{
				e.printStackTrace();
			}
		}
		return cpuid;
    }
	
	/**
	 * 
	 * @Title: getLocalMacAddress
	 * @Description: 获取mac
	 * @return
	 * @return: String
	 */
	@SuppressLint("DefaultLocale")
	public static String getMacAddress(Context context) 
	{  
		String mac = "";  
		if (context != null)
		{
			SettingManager setting; 
			try
			{
				setting = ((SettingManager)context.getSystemService(Context.SETTING_SERVICE));
				mac = setting.getMac();
			}
			catch (Error e)
			{
				e.printStackTrace();
			}
		}
        return mac;  
    } 
	
	/**
	 * 
	 * @Title: getTimeZone
	 * @Description: 获取时区
	 * @param context
	 * @return
	 * @return: String
	 */
	public static String getTimeZone(Context context) 
	{
		String timezone = "";
		if (context != null)
		{
			SettingManager setting; 
			try
			{
				setting = ((SettingManager)context.getSystemService(Context.SETTING_SERVICE));
				timezone = setting.getTimeZone();
			}
			catch (Error e)
			{
				e.printStackTrace();
			}
		}
		return timezone;
	}
	
	/**
	 * 
	 * @Title: getHwVersion
	 * @Description: 获取硬件型号
	 * @param context
	 * @return
	 * @return: String
	 */
	public static String getHwVersion(Context context)
	{
		String hwversion = "";
		if (context != null)
		{
			SettingManager setting; 
			try
			{
				setting = ((SettingManager)context.getSystemService(Context.SETTING_SERVICE));
				hwversion = setting.getHwVersion();
			}
			catch (Error e)
			{
				e.printStackTrace();
			}
		}
		return hwversion;
	}
	
	/**
	 * 
	 * @Title: getVersion
	 * @Description: 获取固件版本
	 * @param context
	 * @return
	 * @return: String
	 */
	public static String getVersion(Context context)
	{
		String version = "";
		if (context != null)
		{
			SettingManager setting; 
			try
			{
				setting = ((SettingManager)context.getSystemService(Context.SETTING_SERVICE));
				version = setting.getSfVersion();
			}
			catch (Error e)
			{
				e.printStackTrace();
			}
		}
		return version;
	}
	
	/**
	 * 
	 * @Title: getPmVersion
	 * @Description: 获取产品型号
	 * @param context
	 * @return
	 * @return: String
	 */
	public static String getPmVersion(Context context)
	{
		String pmversion = "";
		if (context != null)
		{
			SettingManager setting; 
			try
			{
				setting = ((SettingManager)context.getSystemService(Context.SETTING_SERVICE));
				pmversion = setting.getPver();
			}
			catch (Error e)
			{
				e.printStackTrace();
			}
		}
		return pmversion;
	}
	
	/**
	 * 
	 * @Title: getCulLang
	 * @Description: 获取当前语言
	 * @param context
	 * @return
	 * @return: String
	 */
	public static String getCurLang(Context context)
	{
		String lang = "";
		if (context != null)
		{
			SettingManager setting; 
			try
			{
				setting = ((SettingManager)context.getSystemService(Context.SETTING_SERVICE));
				lang = setting.getCurLang();
			}
			catch (Error e)
			{
				e.printStackTrace();
			}
		}
		return lang;
	}
}
