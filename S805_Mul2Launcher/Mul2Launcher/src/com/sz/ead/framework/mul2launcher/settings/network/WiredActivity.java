/**
 * 
 * Copyright © 2014GreatVision. All rights reserved.
 *
 * @Title: WiredActivity.java
 * @Prject: BananaTvSetting
 * @Description: TODO
 * @author: luojw 
 * @date: 2014-1-21 上午11:58:35
 */
package com.sz.ead.framework.mul2launcher.settings.network;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.notice.noticemanager.Notice;
import android.os.Handler;
import android.os.Message;
import android.os.SystemProperties;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sz.ead.framework.mul2launcher.R;
import com.sz.ead.framework.mul2launcher.settings.util.CommonUtils;
import com.sz.ead.framework.mul2launcher.settings.util.ConstUtil;
import com.szgvtv.ead.framework.bi.Bi;

public class WiredActivity extends RelativeLayout 
{
	public static final String TAG = "network";
	private TextView m_tv_auto;
	private TextView m_tv_manual;
	
	private Context m_context;
	private AutoFetchIpView m_auto_fetch_ip_view;			// autofetch view
	private ManualSetingIpView m_manual_setting_ip_view;	// manual setting view

	private IpAddrAuto m_ipaddr_auto_ipaddr;
	private IpAddrAuto m_ipaddr_auto_mask;
	private IpAddrAuto m_ipaddr_auto_gateway;
	private IpAddrAuto m_ipaddr_auto_dns;
	
	private IpAddrManual m_ipaddr_manual_ipaddr;
	private IpAddrManual m_ipaddr_manual_mask;
	private IpAddrManual m_ipaddr_manual_gateway;
	private IpAddrManual m_ipaddr_manual_dns;
	
	Notice m_fetch_notice;
	private ConnectivityManager m_conManager;
	private NetworkInfo m_networkInfo;
	
	private String m_infoIpaddr;
	private String m_infoNetmask;
	private String m_infoRoute;
	private String m_infoDns;
	
	SharedPreferences m_settingNetworkInfo;
	SharedPreferences.Editor m_editor;
	boolean m_isSendBI;
	boolean m_isSettingNetwork;
	ScheduledExecutorService m_timer;
	
	
	/**
	 * 网络设置完成
	 */
	Handler mHandler = new Handler()
	{
        @Override
	    public void handleMessage(Message msg) 
        {
	        // TODO Auto-generated method stub
	        super.handleMessage(msg);
	        switch (msg.what) 
	        {
	        case 2:
	        {
	        	m_networkInfo = m_conManager.getNetworkInfo(ConnectivityManager.TYPE_ETHERNET);
	        	
				boolean isAva = m_networkInfo.isAvailable();
				boolean isCon = m_networkInfo.isConnected();
				Log.d(TAG, "network isAvailable:" + isAva);
				Log.d(TAG, "network isConnected:" + isCon);
				
				boolean hwCon = CommonUtils.getSetting().getEthMagHWConnected();
				Log.d(TAG, " HWcon-------:" + hwCon); 

				// 网线是否插上
				if (!hwCon)
				{
					Bi.sendBiMsg(Bi.BICT_FIRMWARE, Bi.BICC_NETWORK_SET,"-1");
					m_fetch_notice.cancel();
					
					Notice notice = Notice.makeNotice(m_context, m_context.getResources().getString(R.string.settings_network_fetch_fail),
							Notice.LENGTH_SHORT);
					notice.setTextColor(m_context.getResources().getColor(R.color.settings_notice_red));
					notice.cancelAll();
					notice.show();
					
					if(CommonUtils.getSetting().getEthConnectingState() == CommonUtils.getSetting().getETH_DHCP_STATE_RUNNING()){
						CommonUtils.getSetting().setEthConnectingState(CommonUtils.getSetting().getETH_DHCP_STATE_IDLE());
        			}
					
					m_isSettingNetwork = false;
					
					return;
				}	
				else
				{
					WifiManager mWifiManager = (WifiManager)m_context.getSystemService(Context.WIFI_SERVICE);
					int wifiState = mWifiManager.getWifiState();
					
					if ((wifiState != WifiManager.WIFI_STATE_DISABLED) && 
							(wifiState != WifiManager.WIFI_STATE_DISABLING))
					{
						mWifiManager.setWifiEnabled(false);
					}
				}
				
				// 网络是否连接
	        	if (isAva)
	        	{
	        		if (isCon)
	        		{
	        			Bi.sendBiMsg(Bi.BICT_FIRMWARE, Bi.BICC_NETWORK_SET,"0");
	        			m_isSendBI = true;
	        			setAutoNetworkUI();
	        			m_isSendBI = false;
	        			Log.d(TAG, "fetch dismiss");
	        			m_fetch_notice.cancel();
        				
	        			Notice notice = Notice.makeNotice(m_context, m_context.getResources().getString(R.string.settings_network_fetch_success), 
								Notice.LENGTH_SHORT,R.layout.settings_dialog_success, R.id.wait_message_success);
	        			
	        			notice.cancelAll();
	        			notice.showImmediately();
	        			
        				m_isSettingNetwork = false;
        				
	        			return;
	        		}
	        	}
	        	
	        	Bi.sendBiMsg(Bi.BICT_FIRMWARE, Bi.BICC_NETWORK_SET,"-1");
	        	m_fetch_notice.cancel();
	        	
				Notice notice = Notice.makeNotice(m_context, m_context.getResources().getString(R.string.settings_network_fetch_fail),
						Notice.LENGTH_SHORT);
				notice.setTextColor(m_context.getResources().getColor(R.color.settings_notice_red));
				notice.cancelAll();
				notice.show();
				m_isSettingNetwork = false;
	        }
	            break;
	        case 3:
	        {
            	m_networkInfo = m_conManager.getNetworkInfo(ConnectivityManager.TYPE_ETHERNET);
    			boolean isAva = m_networkInfo.isAvailable();
    			boolean isCon = m_networkInfo.isConnected();
    			Log.d(TAG, "network isAvailable:" + isAva);
    			Log.d(TAG, "network isConnected:" + isCon);
    			
    			boolean hwCon = CommonUtils.getSetting().getEthMagHWConnected();
				Log.d(TAG, "HWcon---------:" + hwCon);
				
				// 网线是否插上
				if (!hwCon)
				{
					Bi.sendBiMsg(Bi.BICT_FIRMWARE, Bi.BICC_NETWORK_SET,"-1");
					m_fetch_notice.cancel();
					Notice notice = Notice.makeNotice(m_context, m_context.getResources().getString(R.string.settings_network_manual_fail),
							Notice.LENGTH_SHORT);
					notice.setTextColor(m_context.getResources().getColor(R.color.settings_notice_red));
					notice.cancelAll();
					notice.show();
					
					m_isSettingNetwork = false;
					return;
				}		
				else
				{
					WifiManager mWifiManager = (WifiManager)m_context.getSystemService(Context.WIFI_SERVICE);
					int wifiState = mWifiManager.getWifiState();
					
					if ((wifiState != WifiManager.WIFI_STATE_DISABLED) && 
							(wifiState != WifiManager.WIFI_STATE_DISABLING))
					{
						mWifiManager.setWifiEnabled(false);
					}
				}
				
				// 网络是否连接
            	if (isAva)
            	{
            		if (isCon)
            		{    	                	
            			Bi.sendBiMsg(Bi.BICT_FIRMWARE, Bi.BICC_NETWORK_SET,"0");
	                	
	        			
	        			Notice notice = Notice.makeNotice(m_context, m_context.getResources().getString(R.string.settings_network_manual_success), 
								Notice.LENGTH_SHORT,R.layout.settings_dialog_success, R.id.wait_message_success);
	        			notice.cancelAll();
	        			notice.showImmediately();
	        			
	        			m_isSettingNetwork = false;
	        			return;
            		}
            	}
            	
            	Bi.sendBiMsg(Bi.BICT_FIRMWARE, Bi.BICC_NETWORK_SET,"-1");
            	
				Notice notice = Notice.makeNotice(m_context, m_context.getResources().getString(R.string.settings_network_manual_fail),
						Notice.LENGTH_SHORT);
				notice.setTextColor(m_context.getResources().getColor(R.color.settings_notice_red));
				notice.cancelAll();
				notice.show();
    			m_isSettingNetwork = false;
	        }
                break;
	        default:
	            break;
	        }
	    }
	};
	
	enum FOCUS
	{
		NOT_FOCUS,					// not focus
		AUTO,						// auto text
		AUTO_FETCH,					// auto ok button
		MANUAL,						// manual text
		MANUAL_ADDR0,				// ipaddr
		MANUAL_ADDR1,				// netmask
		MANUAL_ADDR2,				// route
		MANUAL_ADDR3,				// dns
		MANUAL_OK					// manual ok button
	};	
	
	FOCUS m_focus;					// 当前焦点
	
	public WiredActivity(Context context) 
	{
		super(context);
		inflate(context, R.layout.settings_network_view_network_wired, this);
		// TODO Auto-generated constructor stub
		
		m_context = context;
		
		m_conManager = (ConnectivityManager)m_context.getSystemService(Context.CONNECTIVITY_SERVICE);
		
		m_isSettingNetwork = false;
		m_isSendBI = false;
		findView();
		getSettingNetworkInfo();  // 获取保存本地的手动设置信息
		
		initAutoIpTextView();
		initManualIpEdit();
	}
	

	/**
	 * 初始化控件
	 * @Title: findView
	 * @Description: TODO
	 * @return: void
	 */
	public void findView()
	{
		this.m_tv_auto = (TextView)findViewById(R.id.id_setting_network_auto);
		this.m_tv_manual = (TextView)findViewById(R.id.id_setting_network_manual);
		
		m_auto_fetch_ip_view = new AutoFetchIpView(this.getContext());
		m_manual_setting_ip_view = new ManualSetingIpView(this.getContext());
		
		this.addView(m_auto_fetch_ip_view);
		this.addView(m_manual_setting_ip_view);
		
		if (CommonUtils.getSetting().getEthMagMode().equals(CommonUtils.getSetting().getETH_CONN_MODE_DHCP()))
		{
			Log.d(TAG, "auto ip setting");
			this.autoViewShow();
			setSubMenuBg(0);
		}
		else if (CommonUtils.getSetting().getEthMagMode().equals(CommonUtils.getSetting().getETH_CONN_MODE_MANUAL()))
		{
			Log.d(TAG, "manual ip setting");
			this.manualViewShow();
			setSubMenuBg(1);
		}
	}
	
	/**
	 * 初始化自动获取ip界面
	 * @Title: initAutoIpTextView
	 * @Description: TODO
	 * @return: void
	 */
	private void initAutoIpTextView()
	{
		m_ipaddr_auto_ipaddr = (IpAddrAuto)m_auto_fetch_ip_view.findViewById(R.id.id_ipaddr_auto_ipaddr);
		m_ipaddr_auto_mask = (IpAddrAuto)m_auto_fetch_ip_view.findViewById(R.id.id_ipaddr_auto_mask);
		m_ipaddr_auto_gateway = (IpAddrAuto)m_auto_fetch_ip_view.findViewById(R.id.id_ipaddr_auto_gateway);
		m_ipaddr_auto_dns = (IpAddrAuto)m_auto_fetch_ip_view.findViewById(R.id.id_ipaddr_auto_dns);
		
		setAutoNetworkUI();
	}	
	
	/**
	 * 初始化手动设置ip界面
	 * @Title: initManualIpEdit
	 * @Description: TODO
	 * @return: void
	 */
	private void initManualIpEdit()
	{
		m_ipaddr_manual_ipaddr = (IpAddrManual)m_manual_setting_ip_view.findViewById(R.id.id_ipaddr_manual_ipaddr);
		m_ipaddr_manual_mask = (IpAddrManual)m_manual_setting_ip_view.findViewById(R.id.id_ipaddr_manual_mask);
		m_ipaddr_manual_gateway = (IpAddrManual)m_manual_setting_ip_view.findViewById(R.id.id_ipaddr_manual_gateway);
		m_ipaddr_manual_dns = (IpAddrManual)m_manual_setting_ip_view.findViewById(R.id.id_ipaddr_manual_dns);
		
		setManualNetworkUI();
	}	
	
	/**
	 * 进入页面，当当前网络为自动获取，则进入自动获取页面，反之，进入手动设置页面
	 * @Title: autoRequestFocus
	 * @Description: TODO
	 * @return: void
	 */
	public void autoRequestFocus()
	{
		if (CommonUtils.getSetting().getEthMagMode().equals(CommonUtils.getSetting().getETH_CONN_MODE_DHCP()))
		{
			this.m_tv_auto.requestFocus();
			m_focus = FOCUS.AUTO;
			this.autoViewShow();
			setSubMenuBg(2);
		}
		else if (CommonUtils.getSetting().getEthMagMode().equals(CommonUtils.getSetting().getETH_CONN_MODE_MANUAL()))
		{
			this.m_tv_manual.requestFocus();
			m_focus = FOCUS.MANUAL;
			this.manualViewShow();
			setSubMenuBg(3);
		}
	}	
	
	/**
	 * 显示自动获取页面
	 * @Title: autoViewShow
	 * @Description: TODO
	 * @return: void
	 */
	public void autoViewShow()
	{
		this.m_auto_fetch_ip_view.setVisibility(View.VISIBLE);
		this.m_manual_setting_ip_view.setVisibility(View.GONE);
	}
	
	/**
	 * 显示手动设置页面
	 * @Title: manualViewShow
	 * @Description: TODO
	 * @return: void
	 */
	public void manualViewShow()
	{
		this.m_manual_setting_ip_view.setVisibility(View.VISIBLE);
		this.m_auto_fetch_ip_view.setVisibility(View.GONE);
	}	
	
	public void doKeyDown()
	{
		if (m_focus == FOCUS.AUTO)
		{
			this.m_auto_fetch_ip_view.doKeyDown();
			m_focus = FOCUS.AUTO_FETCH;
			setSubMenuBg(0);
		}
		else if (m_focus == FOCUS.MANUAL)
		{
			m_ipaddr_manual_ipaddr.doKeyDown();
			m_focus = FOCUS.MANUAL_ADDR0;
			m_ipaddr_manual_ipaddr.ipAddrFocus();
			m_ipaddr_manual_ipaddr.setBackgroundResource(R.drawable.settings_network_ipaddr_focus);
			setSubMenuBg(1);
		}
		else if (m_focus == FOCUS.MANUAL_ADDR0)	
		{
			m_ipaddr_manual_mask.doKeyDown();
			m_focus = FOCUS.MANUAL_ADDR1;
			
			m_ipaddr_manual_ipaddr.ipAddrFocus();
			m_ipaddr_manual_mask.ipAddrFocus();
			m_ipaddr_manual_ipaddr.setBackgroundResource(R.drawable.settings_network_ipaddr_ufocus);
			m_ipaddr_manual_mask.setBackgroundResource(R.drawable.settings_network_ipaddr_focus);
		}
		else if (m_focus == FOCUS.MANUAL_ADDR1)	
		{
			m_ipaddr_manual_gateway.doKeyDown();
			m_focus = FOCUS.MANUAL_ADDR2;
			m_ipaddr_manual_mask.ipAddrFocus();
			m_ipaddr_manual_gateway.ipAddrFocus();
			m_ipaddr_manual_mask.setBackgroundResource(R.drawable.settings_network_ipaddr_ufocus);
			m_ipaddr_manual_gateway.setBackgroundResource(R.drawable.settings_network_ipaddr_focus);
		}
		else if (m_focus == FOCUS.MANUAL_ADDR2)	
		{
			m_ipaddr_manual_dns.doKeyDown();
			m_focus = FOCUS.MANUAL_ADDR3;
			
			m_ipaddr_manual_gateway.ipAddrFocus();
			m_ipaddr_manual_dns.ipAddrFocus();
			m_ipaddr_manual_gateway.setBackgroundResource(R.drawable.settings_network_ipaddr_ufocus);
			m_ipaddr_manual_dns.setBackgroundResource(R.drawable.settings_network_ipaddr_focus);
		}
		else if (m_focus == FOCUS.MANUAL_ADDR3)	
		{
			this.m_manual_setting_ip_view.doKeyDown();
			m_focus = FOCUS.MANUAL_OK;
			
			m_ipaddr_manual_dns.ipAddrFocus();
			m_ipaddr_manual_dns.setBackgroundResource(R.drawable.settings_network_ipaddr_ufocus);
		}
	}
	
	public boolean doKeyUp()
	{
		if (m_focus == FOCUS.AUTO)
		{
			m_focus = FOCUS.NOT_FOCUS;
			setSubMenuBg(0);
			return true;
		}
		else if (m_focus == FOCUS.MANUAL)
		{
			m_focus = FOCUS.NOT_FOCUS;
			setSubMenuBg(1);
			return true;
		}
		else if (m_focus == FOCUS.AUTO_FETCH)
		{
			this.m_tv_auto.requestFocus();
			m_focus = FOCUS.AUTO;
			setSubMenuBg(2);
		}
		else if (m_focus == FOCUS.MANUAL_ADDR0)	
		{
			this.m_tv_manual.requestFocus();
			m_focus = FOCUS.MANUAL;
			m_ipaddr_manual_ipaddr.ipAddrFocus();
			setSubMenuBg(3);
			m_ipaddr_manual_ipaddr.setBackgroundResource(R.drawable.settings_network_ipaddr_ufocus);
		}
		else if (m_focus == FOCUS.MANUAL_ADDR1)	
		{
			m_ipaddr_manual_ipaddr.doKeyUp();
			m_focus = FOCUS.MANUAL_ADDR0;
			
			m_ipaddr_manual_mask.ipAddrFocus();
			m_ipaddr_manual_ipaddr.ipAddrFocus();
			m_ipaddr_manual_ipaddr.setBackgroundResource(R.drawable.settings_network_ipaddr_focus);
			m_ipaddr_manual_mask.setBackgroundResource(R.drawable.settings_network_ipaddr_ufocus);
		}
		else if (m_focus == FOCUS.MANUAL_ADDR2)	
		{
			m_ipaddr_manual_mask.doKeyUp();
			m_focus = FOCUS.MANUAL_ADDR1;
			m_ipaddr_manual_mask.ipAddrFocus();
			m_ipaddr_manual_gateway.ipAddrFocus();
			m_ipaddr_manual_mask.setBackgroundResource(R.drawable.settings_network_ipaddr_focus);
			m_ipaddr_manual_gateway.setBackgroundResource(R.drawable.settings_network_ipaddr_ufocus);
		}
		else if (m_focus == FOCUS.MANUAL_ADDR3)	
		{
			m_ipaddr_manual_gateway.doKeyUp();
			m_focus = FOCUS.MANUAL_ADDR2;
			m_ipaddr_manual_gateway.ipAddrFocus();
			m_ipaddr_manual_dns.ipAddrFocus();
			m_ipaddr_manual_gateway.setBackgroundResource(R.drawable.settings_network_ipaddr_focus);
			m_ipaddr_manual_dns.setBackgroundResource(R.drawable.settings_network_ipaddr_ufocus);
		}
		else if (m_focus == FOCUS.MANUAL_OK) 
		{
			m_ipaddr_manual_dns.doKeyUp();
			m_focus = FOCUS.MANUAL_ADDR3;
			
			m_ipaddr_manual_dns.ipAddrFocus();
			m_ipaddr_manual_dns.setBackgroundResource(R.drawable.settings_network_ipaddr_focus);
		}
		
		return false;
	}	
	
	public void doKeyLeft()
	{
		if (m_focus == FOCUS.AUTO){
			this.m_tv_manual.requestFocus();
			m_focus = FOCUS.MANUAL;
			getSettingNetworkInfo();
			setManualNetworkUI();
			this.manualViewShow();
			setSubMenuBg(3);
		}
		else if (m_focus == FOCUS.MANUAL)
		{
			this.m_tv_auto.requestFocus();
			m_focus = FOCUS.AUTO;
			this.autoViewShow();
			setSubMenuBg(2);
		}
		else if (m_focus == FOCUS.MANUAL_ADDR0)
		{
			this.m_ipaddr_manual_ipaddr.doKeyLeft();
		}
		else if (m_focus == FOCUS.MANUAL_ADDR1)
		{
			this.m_ipaddr_manual_mask.doKeyLeft();
		}
		else if (m_focus == FOCUS.MANUAL_ADDR2)
		{
			this.m_ipaddr_manual_gateway.doKeyLeft();
		}
		else if (m_focus == FOCUS.MANUAL_ADDR3)
		{
			this.m_ipaddr_manual_dns.doKeyLeft();
		}
		else if (m_focus == FOCUS.MANUAL_ADDR0)
		{
			this.m_ipaddr_manual_ipaddr.doKeyRight();
		}
		else if (m_focus == FOCUS.MANUAL_ADDR1)
		{
			this.m_ipaddr_manual_mask.doKeyRight();
		}
		else if (m_focus == FOCUS.MANUAL_ADDR2)
		{
			this.m_ipaddr_manual_gateway.doKeyRight();
		}
		else if (m_focus == FOCUS.MANUAL_ADDR3)
		{
			this.m_ipaddr_manual_dns.doKeyRight();
		}
	}	
	
	public void doKeyRight()
	{
		if (m_focus == FOCUS.AUTO)
		{
			getSettingNetworkInfo();
			setManualNetworkUI();
			this.m_tv_manual.requestFocus();
			m_focus = FOCUS.MANUAL;
			this.manualViewShow();
			setSubMenuBg(3);
		}
		else if (m_focus == FOCUS.MANUAL)
		{
			this.m_tv_auto.requestFocus();
			m_focus = FOCUS.AUTO;
			this.autoViewShow();
			setSubMenuBg(2);
		}
		else if (m_focus == FOCUS.MANUAL_ADDR0)
		{
			this.m_ipaddr_manual_ipaddr.doKeyRight();
		}
		else if (m_focus == FOCUS.MANUAL_ADDR1)
		{
			this.m_ipaddr_manual_mask.doKeyRight();
		}
		else if (m_focus == FOCUS.MANUAL_ADDR2)
		{
			this.m_ipaddr_manual_gateway.doKeyRight();
		}
		else if (m_focus == FOCUS.MANUAL_ADDR3)
		{
			this.m_ipaddr_manual_dns.doKeyRight();
		}
	}	
	
	public void doKeyNum(KeyEvent event)
	{
		if (m_focus == FOCUS.MANUAL_ADDR0){
			this.m_ipaddr_manual_ipaddr.doKeyNum(event);
		}
		else if (m_focus == FOCUS.MANUAL_ADDR1)
		{
			this.m_ipaddr_manual_mask.doKeyNum(event);
		}
		else if (m_focus == FOCUS.MANUAL_ADDR2)
		{
			this.m_ipaddr_manual_gateway.doKeyNum(event);
		}
		else if (m_focus == FOCUS.MANUAL_ADDR3)
		{
			this.m_ipaddr_manual_dns.doKeyNum(event);
		}
	}	
	
	public void doKeyDel()
	{
		if (m_focus == FOCUS.MANUAL_ADDR0)
		{
			this.m_ipaddr_manual_ipaddr.doKeyDel();
		}
		else if (m_focus == FOCUS.MANUAL_ADDR1)
		{
			this.m_ipaddr_manual_mask.doKeyDel();
		}
		else if (m_focus == FOCUS.MANUAL_ADDR2)
		{
			this.m_ipaddr_manual_gateway.doKeyDel();
		}
		else if (m_focus == FOCUS.MANUAL_ADDR3)
		{
			this.m_ipaddr_manual_dns.doKeyDel();
		}
	}	
	
	public void doKeyOk()
	{
		if (!m_isSettingNetwork)
		{
			if (m_focus == FOCUS.AUTO_FETCH)
			{
				this.setDhcpSetting();
			}
			else if (m_focus == FOCUS.MANUAL_OK)
			{
				this.setManualSetting();
			}
			else if (m_focus == FOCUS.MANUAL_ADDR0)
			{
				this.m_ipaddr_manual_ipaddr.doKeyOk();
			}
			else if (m_focus == FOCUS.MANUAL_ADDR1)
			{
				this.m_ipaddr_manual_mask.doKeyOk();
			}
			else if (m_focus == FOCUS.MANUAL_ADDR2)
			{
				this.m_ipaddr_manual_gateway.doKeyOk();
			}
			else if (m_focus == FOCUS.MANUAL_ADDR3)
			{
				this.m_ipaddr_manual_dns.doKeyOk();
			}
		}
	}
	
	/**
	 * 是否正在设置网络
	 * @Title: isSettingOn
	 * @Description: TODO
	 * @return
	 * @return: boolean
	 */
	public boolean isSettingOn()
	{
		return m_isSettingNetwork;
	}
	/**
	 * 自动获取，手动配置toogle
	 * @Title: setSubMenuBg
	 * @Description: 
	 * @return
	 */
	public void setSubMenuBg(int index)
	{
		if (0 == index)
		{
			this.m_tv_auto.setBackgroundResource(R.drawable.settings_network_submenu_select);
			this.m_tv_manual.setBackgroundResource(R.drawable.settings_network_submenu_unfocus);
			
			this.m_tv_auto.setTextColor(this.getResources().getColor(R.drawable.settings_network_tv_text_focus));
			this.m_tv_manual.setTextColor(this.getResources().getColor(R.drawable.settings_network_tv_text_unfocus));
		}
		else if (1 == index)
		{
			this.m_tv_auto.setBackgroundResource(R.drawable.settings_network_submenu_unfocus);
			this.m_tv_manual.setBackgroundResource(R.drawable.settings_network_submenu_select);
			
			this.m_tv_auto.setTextColor(this.getResources().getColor(R.drawable.settings_network_tv_text_unfocus));
			this.m_tv_manual.setTextColor(this.getResources().getColor(R.drawable.settings_network_tv_text_focus));
		}
		else if (2 == index)
		{
			this.m_tv_auto.setBackgroundResource(R.drawable.settings_network_submenu_focus);
			this.m_tv_manual.setBackgroundResource(R.drawable.settings_network_submenu_unfocus);
			
			this.m_tv_auto.setTextColor(this.getResources().getColor(R.drawable.settings_network_tv_text_focus));
			this.m_tv_manual.setTextColor(this.getResources().getColor(R.drawable.settings_network_tv_text_unfocus));
		}
		else if (3 == index)
		{
			this.m_tv_auto.setBackgroundResource(R.drawable.settings_network_submenu_unfocus);
			this.m_tv_manual.setBackgroundResource(R.drawable.settings_network_submenu_focus);
			
			this.m_tv_auto.setTextColor(this.getResources().getColor(R.drawable.settings_network_tv_text_unfocus));
			this.m_tv_manual.setTextColor(this.getResources().getColor(R.drawable.settings_network_tv_text_focus));
		}
	}
	
	/**
	 * 自动设置网络
	 * @Title: setDhcpSetting
	 * @Description: TODO
	 * @return: void
	 */
	public void setDhcpSetting()
	{
		if (!CommonUtils.getSetting().getEthMagHWConnected())
		{
			Notice notice = Notice.makeNotice(m_context, 
					m_context.getResources().getString(R.string.settings_network_detection_not_newtork),
					Notice.LENGTH_SHORT);
			notice.setTextColor(m_context.getResources().getColor(R.color.settings_notice_red));
			notice.cancelAll();
			notice.show();
			
			return;
		}
		
		m_isSettingNetwork = true;
	
		m_fetch_notice = Notice.makeNotice(m_context, m_context.getResources().getString(R.string.settings_network_fetching),
				Notice.LENGTH_INFINITE, R.layout.settings_dialog_waiting, R.id.wait_message);
		m_fetch_notice.cancelAll();
		m_fetch_notice.show();
				
		Runnable dhcpRunnable = new Runnable()
		{
        	@Override  
        	public void run() 
        	{ 
        		int ethState = CommonUtils.getSetting().getEthMagState();
        		Log.d(TAG, "getEthState:" + ethState);
        		
        		if((ethState == CommonUtils.getSetting().getETH_CNT_STATE_ENABLING()) || 
        				(ethState == CommonUtils.getSetting().getETH_CNT_STATE_DISABLED()))
        		{
        			if(CommonUtils.getSetting().getEthConnectingState() == CommonUtils.getSetting().getETH_DHCP_STATE_RUNNING()){
						CommonUtils.getSetting().setEthConnectingState(CommonUtils.getSetting().getETH_DHCP_STATE_IDLE());
        			}
        			//CommonUtils.getSetting().setEthMagState(false);
        			
        			CommonUtils.getSetting().updateEthDevInfo( CommonUtils.getSetting().getETH_CONN_MODE_DHCP(), null, null, null, null);
        			
        			//CommonUtils.getSetting().setEthMagState(true);
        			if (CommonUtils.getSetting().getEthMagState() == CommonUtils.getSetting().getETH_CNT_STATE_ENABLED())
        			{
        				Log.d(TAG, "setEthMagState:true");
        				CommonUtils.getSetting().setEthMagState(true);
        			}
        			ethState = CommonUtils.getSetting().getEthMagState();
        			Log.d(TAG, "getEthState:" + ethState);
        			fetchTimerStart();
        			
        			return;
        		}
        		
        		CommonUtils.getSetting().updateEthDevInfo( CommonUtils.getSetting().getETH_CONN_MODE_DHCP(), null, null, null, null);
				//CommonUtils.getSetting().setEthMagState(false);
				
				if(CommonUtils.getSetting().getEthConnectingState() == CommonUtils.getSetting().getETH_DHCP_STATE_RUNNING()){
					CommonUtils.getSetting().setEthConnectingState(CommonUtils.getSetting().getETH_DHCP_STATE_IDLE());
    			}
				
				//CommonUtils.getSetting().setEthMagState(true);
				if (CommonUtils.getSetting().getEthMagState() == CommonUtils.getSetting().getETH_CNT_STATE_ENABLED())
    			{
    				Log.d(TAG, "setEthMagState:true");
    				CommonUtils.getSetting().setEthMagState(true);
    			}
				fetchTimerStart();
        	}
		};
		
		new Thread(dhcpRunnable).start();
	}
	
	/**
	 * 设置网络十秒钟后去检测网络
	 * @Title: fetchTimerStart
	 * @Description: TODO
	 * @return: void
	 */
	public void fetchTimerStart()
	{
		if(m_timer != null)
		{
			m_timer.shutdownNow();
			m_timer = null;
		}
		
		if(m_timer == null)
		{
			m_timer = Executors.newScheduledThreadPool(1);
		}
    	
		class Task implements Runnable{
			/* (non-Javadoc)
			 * @see java.util.TimerTask#run()
			 */
			@Override
			public void run() {
			   try {
				   	m_timer.shutdownNow();
				   	m_timer = null;
	                mHandler.sendEmptyMessage(2);
			    } catch (Exception e) {
			// TODO: handle exception
			      e.printStackTrace();
			    }
			    }
		};
    	
		m_timer.scheduleAtFixedRate(new Task(), 10*1000, 10*1000,TimeUnit.MILLISECONDS);
	}
	
	
	/**
	 * 手动设置网络
	 * @Title: setManualSetting
	 * @Description: TODO
	 * @return: void
	 */
	public void setManualSetting()
	{
		if (!CommonUtils.getSetting().getEthMagHWConnected())
		{
			Notice notice = Notice.makeNotice(m_context, 
					m_context.getResources().getString(R.string.settings_network_detection_not_newtork),
					Notice.LENGTH_SHORT);
			notice.setTextColor(m_context.getResources().getColor(R.color.settings_notice_red));
			notice.cancelAll();
			notice.show();
			
			return;
		}
		
		m_isSettingNetwork = true;
     		
		m_fetch_notice = Notice.makeNotice(m_context, m_context.getResources().getString(R.string.settings_prompt_dialog_loading_info),
				Notice.LENGTH_INFINITE, R.layout.settings_dialog_waiting, R.id.wait_message);
		m_fetch_notice.cancelAll();
		m_fetch_notice.show();
		
		Runnable manualRunnable = new Runnable()
		{
        	@Override  
        	public void run() 
        	{ 
        		String ipaddr = m_ipaddr_manual_ipaddr.getText();
                String netmask = m_ipaddr_manual_mask.getText();
                String route = m_ipaddr_manual_gateway.getText();
                String dns = m_ipaddr_manual_dns.getText();
                
                Log.d("network", "ipaddr:"+ ipaddr);
        		Log.d("network", "netmask:"+ netmask);
        		Log.d("network", "route:"+ route);
        		Log.d("network", "dns:"+ dns);
        		
        		setSettingNetworkInfo(ipaddr,netmask,route,dns);
        		
        		Bi.sendBiMsg(Bi.BICT_FIRMWARE, Bi.BICC_NETWORK,"1","Manual",ipaddr,netmask,dns);
        		
        		int ethState = CommonUtils.getSetting().getEthMagState();
        		Log.d(TAG, "getEthState:" + ethState);
        		
        		if (CommonUtils.getSetting().getETH_CNT_STATE_ENABLED() != ethState)
        		{
        			if(CommonUtils.getSetting().getEthConnectingState() == CommonUtils.getSetting().getETH_DHCP_STATE_RUNNING()){
						CommonUtils.getSetting().setEthConnectingState(CommonUtils.getSetting().getETH_DHCP_STATE_IDLE());
        			}
        			//CommonUtils.getSetting().setEthMagState(false);
        			
        			
        			CommonUtils.getSetting().updateEthDevInfo( CommonUtils.getSetting().getETH_CONN_MODE_MANUAL(), ipaddr, route, dns, netmask);
        			//CommonUtils.getSetting().setEthMagState(true);
        			if (CommonUtils.getSetting().getEthMagState() == CommonUtils.getSetting().getETH_CNT_STATE_ENABLED())
        			{
        				Log.d(TAG, "setEthMagState:true");
        				CommonUtils.getSetting().setEthMagState(true);
        			}
        			ethState = CommonUtils.getSetting().getEthMagState();
        			Log.d(TAG, "getEthState:" + ethState); 
        			
        			manualStartTimer();
        			
        			return;
        		}
        		CommonUtils.getSetting().updateEthDevInfo( CommonUtils.getSetting().getETH_CONN_MODE_MANUAL(), ipaddr, route, dns, netmask);
        		
        		//CommonUtils.getSetting().setEthMagState(false);
        		
        		if(CommonUtils.getSetting().getEthConnectingState() == CommonUtils.getSetting().getETH_DHCP_STATE_RUNNING()){
					CommonUtils.getSetting().setEthConnectingState(CommonUtils.getSetting().getETH_DHCP_STATE_IDLE());
    			}
        		
        		//CommonUtils.getSetting().setEthMagState(true);
        		if (CommonUtils.getSetting().getEthMagState() == CommonUtils.getSetting().getETH_CNT_STATE_ENABLED())
    			{
    				Log.d(TAG, "setEthMagState:true");
    				CommonUtils.getSetting().setEthMagState(true);
    			}
        		manualStartTimer();
        	}
		};
		
		new Thread(manualRunnable).start();
	}	
	
	/**
	 * 手动设置网络十秒钟后检测网络
	 * @Title: manualStartTimer
	 * @Description: TODO
	 * @return: void
	 */
	public void manualStartTimer()
	{
		if(m_timer != null)
		{
			m_timer.shutdownNow();
			m_timer = null;
		}
		
		if(m_timer == null)
		{
			m_timer = Executors.newScheduledThreadPool(1);
		}
    	
		class Task implements Runnable{
			/* (non-Javadoc)
			 * @see java.util.TimerTask#run()
			 */
			@Override
			public void run() {
			   try {
				   	m_timer.shutdownNow();
					m_timer = null;
				   	m_fetch_notice.cancel();
	                 mHandler.sendEmptyMessage(3);
			    } catch (Exception e) {
			// TODO: handle exception
			      e.printStackTrace();
			    }
			    }
		};
    	
		m_timer.scheduleAtFixedRate(new Task(), 10*1000, 10*1000,TimeUnit.MILLISECONDS);
	}
	
	/**
	 * 设置自动获取页面UI
	 * @Title: setAutoNetworkUI
	 * @Description: TODO
	 * @return: void
	 */
	public void setAutoNetworkUI()
	{
		String ipProp = "dhcp."+"eth0"+".ipaddress";
        String netmaskProp = "dhcp."+"eth0"+".mask";
        String routeProp = "dhcp."+"eth0"+".gateway";
        String dnsProp = "dhcp."+"eth0"+".dns1";
        
		String ipaddr = SystemProperties.get(ipProp);
		String netmask = SystemProperties.get(netmaskProp);
		String route = SystemProperties.get(routeProp);
		String dns = SystemProperties.get(dnsProp);
		
		Log.d(TAG, "ipaddr:"+ ipaddr);
		Log.d(TAG, "netmask:"+ netmask);
		Log.d(TAG, "route:"+ route);
		Log.d(TAG, "dns:"+ dns);
		
		if (m_isSendBI)
		{
			Bi.sendBiMsg(Bi.BICT_FIRMWARE, Bi.BICC_NETWORK,"1","DHCP",ipaddr,netmask,dns);
		}
		        
        m_ipaddr_auto_ipaddr.setText(ipaddr);
		m_ipaddr_auto_mask.setText(netmask);
		m_ipaddr_auto_gateway.setText(route);
		m_ipaddr_auto_dns.setText(dns);	
	}	

	/**
	 * 设置手动设置页面UI
	 * @Title: setManualNetworkUI
	 * @Description: TODO
	 * @return: void
	 */
	public void setManualNetworkUI()
	{
		
		/*Log.d(TAG, "savefile ipaddr:"+ m_infoIpaddr);
		Log.d(TAG, "savefile netmask:"+ m_infoNetmask);
		Log.d(TAG, "savefile route:"+ m_infoRoute);
		Log.d(TAG, "savefile dns:"+ m_infoDns);*/
		
		//this.m_ipaddr_manual[]
		m_ipaddr_manual_ipaddr.setText(m_infoIpaddr);
		m_ipaddr_manual_mask.setText(m_infoNetmask);
		m_ipaddr_manual_gateway.setText(m_infoRoute);
		m_ipaddr_manual_dns.setText(m_infoDns);
	}
	
	/**
	 * 读取本地保存的手动设置信息
	 * @Title: getSettingNetworkInfo
	 * @Description: TODO
	 * @return: void
	 */
	private void getSettingNetworkInfo()
	{	
		m_settingNetworkInfo = m_context.getSharedPreferences(ConstUtil.SETTING_NETWORK_FILE_NAME, Context.MODE_PRIVATE);
		m_editor = m_settingNetworkInfo.edit();
		
		if (m_settingNetworkInfo.getString(ConstUtil.SETTING_IPADDR, "").isEmpty() &&
				m_settingNetworkInfo.getString(ConstUtil.SETTING_IPADDR, "").isEmpty() &&
				m_settingNetworkInfo.getString(ConstUtil.SETTING_IPADDR, "").isEmpty() &&
				m_settingNetworkInfo.getString(ConstUtil.SETTING_IPADDR, "").isEmpty())
				{
					Log.d(TAG, "manual info is null,use dhcp info");
					
					String ipProp = "dhcp."+"eth0"+".ipaddress";
			        String netmaskProp = "dhcp."+"eth0"+".mask";
			        String routeProp = "dhcp."+"eth0"+".gateway";
			        String dnsProp = "dhcp."+"eth0"+".dns1";
			        
			        m_infoIpaddr = SystemProperties.get(ipProp);
			        m_infoNetmask = SystemProperties.get(netmaskProp);
			        m_infoRoute = SystemProperties.get(routeProp);
			        m_infoDns = SystemProperties.get(dnsProp);
					
					Log.d(TAG, "ipaddr:"+ m_infoIpaddr);
					Log.d(TAG, "netmask:"+ m_infoNetmask);
					Log.d(TAG, "route:"+ m_infoRoute);
					Log.d(TAG, "dns:"+ m_infoDns);
					
					return;
				}
		
		// ipaddr
		if (m_settingNetworkInfo.getString(ConstUtil.SETTING_IPADDR, "").isEmpty())
		{
			m_infoIpaddr = "0.0.0.0";
			m_editor.putString(ConstUtil.SETTING_IPADDR, m_infoIpaddr);
		}
		else
		{
			m_infoIpaddr = m_settingNetworkInfo.getString(ConstUtil.SETTING_IPADDR, "");
		}
		
		//netmask
		if (m_settingNetworkInfo.getString(ConstUtil.SETTING_NETMASK, "").isEmpty())
		{
			m_infoNetmask = "0.0.0.0";
			m_editor.putString(ConstUtil.SETTING_NETMASK, m_infoNetmask);
		}
		else
		{
			m_infoNetmask = m_settingNetworkInfo.getString(ConstUtil.SETTING_NETMASK, "");
		}
		
		// route
		if (m_settingNetworkInfo.getString(ConstUtil.SETTING_ROUTE, "").isEmpty())
		{
			m_infoRoute = "0.0.0.0";
			m_editor.putString(ConstUtil.SETTING_ROUTE, m_infoRoute);
		}
		else
		{
			m_infoRoute = m_settingNetworkInfo.getString(ConstUtil.SETTING_ROUTE, "");
		}
		
		// dns
		if (m_settingNetworkInfo.getString(ConstUtil.SETTING_DNS, "").isEmpty())
		{
			m_infoDns = "0.0.0.0";
			m_editor.putString(ConstUtil.SETTING_DNS, m_infoDns);
		}
		else
		{
			m_infoDns = m_settingNetworkInfo.getString(ConstUtil.SETTING_DNS, "");
		}
		
		m_editor.commit();
	}
	
	/**
	 * 保存手动设置信息到本地
	 * @Title: setSettingNetworkInfo
	 * @Description: TODO
	 * @param ipaddr
	 * @param netmask
	 * @param route
	 * @param dns
	 * @return: void
	 */
	private void setSettingNetworkInfo(String ipaddr, String netmask, String route, String dns)
	{
		m_settingNetworkInfo = m_context.getSharedPreferences(ConstUtil.SETTING_NETWORK_FILE_NAME, Context.MODE_PRIVATE);
		m_editor = m_settingNetworkInfo.edit();
		
		m_infoIpaddr = ipaddr;
		m_infoNetmask = netmask;
		m_infoRoute = route;
		m_infoDns = dns;
		
		m_editor.putString(ConstUtil.SETTING_IPADDR, m_infoIpaddr);
		m_editor.putString(ConstUtil.SETTING_NETMASK, m_infoNetmask);
		m_editor.putString(ConstUtil.SETTING_ROUTE, m_infoRoute);
		m_editor.putString(ConstUtil.SETTING_DNS, m_infoDns);
		
		m_editor.commit();
	}
}
