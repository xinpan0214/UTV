/**
 * Copyright © 2015GreatVision. All rights reserved.
 *
 * @Title: NetUtils.java
 * @Prject: Mul2Launcher
 * @Package: com.sz.ead.framework.mul2launcher.settings.util
 * @Description: TODO
 * @author: lijungang  
 * @date: 2015-5-4 下午3:36:07
 */
package com.sz.ead.framework.mul2launcher.settings.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.setting.settingservice.SettingManager;

import com.sz.ead.framework.mul2launcher.R;

/**
 * Copyright © 2015GreatVision. All rights reserved.
 *
 * @Title: NetUtils.java
 * @Prject: Mul2Launcher
 * @Package: com.sz.ead.framework.mul2launcher.settings.util
 * @Description: TODO
 * @author: lijungang 
 * @date: 2015-5-4 下午3:36:07
 */
public class NetUtils {
	/**
	 * 
	 * @Title: isConnected
	 * @Description: 网络链接检测
	 * @param context
	 * @return
	 * @return: boolean
	 */
	public static boolean isConnected(Context context)
	{
		 try { 
		        ConnectivityManager connectivity = (ConnectivityManager) context 
		                .getSystemService(Context.CONNECTIVITY_SERVICE); 
		        if (connectivity != null) { 
		            // 获取网络连接管理的对象 
		            NetworkInfo info = connectivity.getActiveNetworkInfo(); 
		            if (info != null&& info.isConnected()) { 
		                // 判断当前网络是否已经连接 
		                if (info.getState() == NetworkInfo.State.CONNECTED) { 
		                    return true; 
		                } 
		            } 
		        } 
		    } catch (Exception e) { 
		} 
        return false; 
	}
	/**
	 * 
	 * @Title: getWifiSSID
	 * @Description: 获取wifi的ssid
	 * @param context
	 * @return: String
	 */
	public static String getWifiSSID(Context context){
		try{
			WifiManager mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
			WifiInfo m_wifiInfo = mWifiManager.getConnectionInfo();
			return m_wifiInfo.getSSID().replace("\"", "");
		}catch (Exception e) {
			// TODO: handle exception
		}
		return context.getResources().getString(R.string.settings_menu_network_title_info_false);
	}
	/**
	 * 
	 * @Title: getNetworkTypeName
	 * @Description: 获取当前网络名
	 * @param context
	 * @return: String
	 */
	public static String getNetworkTypeName(Context context) {
        try{
        	//EthernetManager connectivity = (EthernetManager) context.getSystemService(Context.ETH_SERVICE);
        	SettingManager mSetting = (SettingManager)context.getSystemService(Context.SETTING_SERVICE);
            if (mSetting != null) {
                Boolean info = mSetting.getEthMagHWConnected();
                if(info)return context.getResources().getString(R.string.settings_menu_network_title_info_true);
                //return getWifiSSID(context);
                return "Wi-Fi";
            }
        }catch (Exception e) { 
        }
    	return context.getResources().getString(R.string.settings_menu_network_title_info_false);
    }
}
