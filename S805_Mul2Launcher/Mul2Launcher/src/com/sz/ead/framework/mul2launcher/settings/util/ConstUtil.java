/**
 * 
 * Copyright © 2014GreatVision. All rights reserved.
 *
 * @Title: ConstUtil.java
 * @Prject: BananaTvSetting
 * @Description: 系统设置常量
 * @author: lijungang 
 * @date: 2014-1-24 下午1:54:21
 */
package com.sz.ead.framework.mul2launcher.settings.util;

public class ConstUtil {

	public static String SETTING_FILE_NAME = "setting_info";
	public static String SETTING_LANG = "setting_lang_index";//当前语言索引
	public static String SETTING_LANG_FIRST_IN = "setting_lang_first_in";//区别语言设置是ok进入的还是更新语言自动进入的
	public static String SETTING_WALLPAPER = "setting_wallpaper_index";
	public static String SETTING_ZOOM = "setting_zoom";
	public static String SETTING_UPGRADE = "setting_upgrade";
	public static String SETTING_VIDEO = "setting_video_index";
	public static String SETTING_ABOUT = "setting_about";
	
	public static final String UPGRADEFILE_NAME = "update_";
	public static final String UPGRADE_PATH = "update";
	public static final String UPGRADE_CACHE_PATH = "/cache";
	
	public static String SETTING_NETWORK_FILE_NAME = "setting_network_info";
	public static String SETTING_IPADDR = "setting_ipaddr";
	public static String SETTING_NETMASK = "setting_netmask";
	public static String SETTING_ROUTE = "setting_route";
	public static String SETTING_DNS = "setting_dns";
	
	public static String Tag = "Setting";
	public static final String UPGRADE_SERVICE_NAME = "com.sz.ead.framework.upgrademanager.UpgradeBgService";
	public static final String TVPAD_VIDEO_CHANGED = "com.sz.ead.framework.volume.VolumeService.videochange";
}
