/**
 * 
 * Copyright © 2014GreatVision. All rights reserved.
 *
 * @Title: NetworkBroadcastReceiver.java
 * @Prject: BananaTvSetting
 * @Description: TODO
 * @author: luojw 
 * @date: 2014-1-21 上午11:56:35
 */
package com.sz.ead.framework.mul2launcher.settings.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ethernet.EthernetManager;
import android.net.ethernet.EthernetStateTracker;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.setting.settingservice.SettingManager;
import android.util.Log;

import com.sz.ead.framework.mul2launcher.settings.util.ConstUtil;

public class NetworkBroadcastReceiver extends BroadcastReceiver {

	public static final String TAG = "network";
	private final String MANUAL_ACTION = "com.sz.ead.framework.guider.network.WiredActivity.saveManualSetting";
	private final String MANUAL_INFO_DEL_ACTION = "com.sz.ead.framework.adapter.PlatformAdapter.clearWiredConfig";
	
	@Override
	public void onReceive(Context arg0, Intent arg1) {
		// TODO Auto-generated method stub
		
		if (arg1.getAction().equals(Intent.ACTION_BOOT_COMPLETED))				// 开机广播
		{
			WifiManager mWifiManager = (WifiManager)arg0.getSystemService(Context.WIFI_SERVICE);
    		Log.d(TAG, "onReceive boot msg");
			int wifiState = mWifiManager.getWifiState();
			SettingManager mSetting = (SettingManager)arg0.getSystemService(Context.SETTING_SERVICE);
			
			if (mSetting.getEthMagState() == mSetting.getETH_CNT_STATE_DISABLED())
			{
				Log.d(TAG, "EthMagState false");
				mSetting.setEthMagState(true);
			}
			
			boolean hwConnected = mSetting.getEthMagHWConnected();
			Log.d(TAG, "hwConnected:"+hwConnected);
			
			if (hwConnected)
			{
				if ((wifiState != WifiManager.WIFI_STATE_DISABLED) && 
						(wifiState != WifiManager.WIFI_STATE_DISABLING))
				{
					mWifiManager.setWifiEnabled(false);
				}
			}
			else
			{
				if ((wifiState != WifiManager.WIFI_STATE_ENABLED) && 
						(wifiState != WifiManager.WIFI_STATE_ENABLING))
				{
					mWifiManager.setWifiEnabled(true);
				}
			}
		}
		else if (arg1.getAction().equals(MANUAL_ACTION))				// 开机向导设置手动配置，网络信息接收
		{
			
			SharedPreferences m_settingNetworkInfo = arg0.getSharedPreferences(ConstUtil.SETTING_NETWORK_FILE_NAME, Context.MODE_PRIVATE);
			SharedPreferences.Editor m_editor = m_settingNetworkInfo.edit();
			
			Bundle bundle = arg1.getExtras();
		
			String ipaddr = bundle.getString("MANUAL_IP");
			String netmask = bundle.getString("MANUAL_MASK");
			String route = bundle.getString("MANUAL_ROUTE");
			String dns = bundle.getString("MANUAL_DNS");
			
			Log.d(TAG, "receive ipaddr"+ipaddr);
			Log.d(TAG, "receive netmask"+netmask);
			Log.d(TAG, "receive route"+route);
			Log.d(TAG, "receive dns"+dns);
			
			m_editor.putString(ConstUtil.SETTING_IPADDR, ipaddr);
			m_editor.putString(ConstUtil.SETTING_NETMASK, netmask);
			m_editor.putString(ConstUtil.SETTING_ROUTE, route);
			m_editor.putString(ConstUtil.SETTING_DNS, dns);
			
			m_editor.commit();
			
		}
		else if (arg1.getAction().equals(MANUAL_INFO_DEL_ACTION))				// 删除配置信息
		{
			Log.d(TAG, "delete manual wired info");
			SharedPreferences m_settingNetworkInfo = arg0.getSharedPreferences(ConstUtil.SETTING_NETWORK_FILE_NAME, Context.MODE_PRIVATE);
			SharedPreferences.Editor m_editor = m_settingNetworkInfo.edit();
			m_editor.clear();
			m_editor.commit();
		}
		else if (arg1.getAction().equals(EthernetManager.ETH_STATE_CHANGED_ACTION))
		{
			WifiManager mWifiManager = (WifiManager)arg0.getSystemService(Context.WIFI_SERVICE);
			int state = arg1.getIntExtra(EthernetManager.EXTRA_ETH_STATE,
                    EthernetStateTracker.EVENT_HW_DISCONNECTED);
			SettingManager mSetting = (SettingManager)arg0.getSystemService(Context.SETTING_SERVICE);
			Log.d(TAG, "recv state:"+state);
			
            if (state == EthernetStateTracker.EVENT_HW_PHYCONNECTED) {
            	
            	int wifiState = mWifiManager.getWifiState();
    			if ((wifiState != WifiManager.WIFI_STATE_DISABLED) && 
    					(wifiState != WifiManager.WIFI_STATE_DISABLING))
    			{
    				Log.d(TAG, "set wifi false");
    				mWifiManager.setWifiEnabled(false);
    			}
            } else if (state == EthernetStateTracker.EVENT_HW_DISCONNECTED) {
                // Unfortunately, the interface will still be listed when this
                // intent is sent, so delay updating.
            	
            	boolean hwConnect = mSetting.getEthMagHWConnected();
            	
            	if (!hwConnect)
            	{
            		Log.d(TAG, "hwConnect false");
            		int wifiState = mWifiManager.getWifiState();
        			if ((wifiState != WifiManager.WIFI_STATE_ENABLED) && 
        					(wifiState != WifiManager.WIFI_STATE_ENABLING))
        			{
        				Log.d(TAG, "set wifi true");
                    	mWifiManager.setWifiEnabled(true);
        			}
            	}
            	else 
            	{
            		Log.d(TAG, "hwConnect true");
            	}
            }
		}
	}
}
