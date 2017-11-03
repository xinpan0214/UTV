/**
 * Copyright © 2014GreatVision. All rights reserved.
 *
 * @Title: WiFiSeniorLayout.java
 * @Prject: BananaTvSetting
 * @Description: TODO
 * @author: luojw  
 * @date: 2014-8-2 上午10:59:45
 */
package com.sz.ead.framework.mul2launcher.settings.network;

import java.math.BigInteger;
import java.net.InetAddress;
import java.util.Iterator;

import android.content.Context;
import android.net.LinkAddress;
import android.net.LinkProperties;
import android.net.RouteInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiConfiguration.IpAssignment;
import android.notice.noticemanager.Notice;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sz.ead.framework.mul2launcher.R;
import com.sz.ead.framework.mul2launcher.settings.util.CommonUtils;

public class WiFiSeniorLayout extends RelativeLayout {
	
	public static final String TAG = "network";
	private Context mContext;
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
	
	private TextView m_tv_auto;
	private TextView m_tv_manual;
	private TextView m_ssidName;
	FOCUS m_focus;
	private ImageView m_signal;
	
	Handler mHandler;
	
	String m_ipAddr = "";
	String m_gateway = "";
	String m_dns = "";
	String m_netmask = "";
	
	String m_Prefix = "";
	
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
	
	public WiFiSeniorLayout(Context context) 
	{
		super(context);
		inflate(context, R.layout.settings_network_wifi_senior_layout, this);
		// TODO Auto-generated constructor stub
		
		mContext = context;
		findView();
	}
	
	public void findView()
	{
		this.m_tv_auto = (TextView)findViewById(R.id.id_setting_network_wifi_auto);
		this.m_tv_manual = (TextView)findViewById(R.id.id_setting_network_wifi_manual);
		this.m_ssidName = (TextView)findViewById(R.id.id_setting_network_wifi_senior_name);
		this.m_signal = (ImageView)findViewById(R.id.id_setting_network_wifi_senior_signal);
		
		m_auto_fetch_ip_view = new AutoFetchIpView(this.getContext());
		m_manual_setting_ip_view = new ManualSetingIpView(this.getContext());
		
		this.addView(m_auto_fetch_ip_view);
		this.addView(m_manual_setting_ip_view);
		
		initAutoIpTextView();
		initManualIpEdit();
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
	
	/**
	 * 设置自动获取页面UI
	 * @Title: setAutoNetworkUI
	 * @Description: TODO
	 * @return: void
	 */
	public void setAutoNetworkUI()
	{
		m_ipaddr_auto_ipaddr.setText(m_ipAddr);
        m_ipaddr_auto_mask.setText(m_netmask);
        m_ipaddr_auto_gateway.setText(m_gateway);
        m_ipaddr_auto_dns.setText(m_dns);
	}

	/**
	 * 设置手动设置页面UI
	 * @Title: setManualNetworkUI
	 * @Description: TODO
	 * @return: void
	 */
	public void setManualNetworkUI()
	{
		m_ipaddr_manual_ipaddr.setText(m_ipAddr);
        m_ipaddr_manual_mask.setText(m_netmask);
        m_ipaddr_manual_gateway.setText(m_gateway);
        m_ipaddr_manual_dns.setText(m_dns);
        
        m_ipaddr_manual_ipaddr.ipAddrFocus();
        m_ipaddr_manual_mask.ipAddrFocus();
        m_ipaddr_manual_gateway.ipAddrFocus();
        m_ipaddr_manual_dns.ipAddrFocus();
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
			m_ipaddr_manual_dns.ipAddrFocus();
			m_ipaddr_manual_dns.setBackgroundResource(R.drawable.settings_network_ipaddr_ufocus);
		}
	}
	
	public boolean doKeyUp()
	{
		if (m_focus == FOCUS.AUTO_FETCH)
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
			m_ipaddr_manual_ipaddr.doKeyLeft();
		}
		else if (m_focus == FOCUS.MANUAL_ADDR1)
		{
			m_ipaddr_manual_mask.doKeyLeft();
		}
		else if (m_focus == FOCUS.MANUAL_ADDR2)
		{
			m_ipaddr_manual_gateway.doKeyLeft();
		}
		else if (m_focus == FOCUS.MANUAL_ADDR3)
		{
			m_ipaddr_manual_dns.doKeyLeft();
		}
		else if (m_focus == FOCUS.MANUAL_ADDR0)
		{
			m_ipaddr_manual_ipaddr.doKeyRight();
		}
		else if (m_focus == FOCUS.MANUAL_ADDR1)
		{
			m_ipaddr_manual_mask.doKeyRight();
		}
		else if (m_focus == FOCUS.MANUAL_ADDR2)
		{
			m_ipaddr_manual_gateway.doKeyRight();
		}
		else if (m_focus == FOCUS.MANUAL_ADDR3)
		{
			m_ipaddr_manual_dns.doKeyRight();
		}
	}	
	
	public void doKeyRight()
	{
		if (m_focus == FOCUS.AUTO)
		{
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
			m_ipaddr_manual_ipaddr.doKeyRight();
		}
		else if (m_focus == FOCUS.MANUAL_ADDR1)
		{
			m_ipaddr_manual_mask.doKeyRight();
		}
		else if (m_focus == FOCUS.MANUAL_ADDR2)
		{
			m_ipaddr_manual_gateway.doKeyRight();
		}
		else if (m_focus == FOCUS.MANUAL_ADDR3)
		{
			m_ipaddr_manual_dns.doKeyRight();
		}
	}	
	
	public void doKeyNum(KeyEvent event)
	{
		if (m_focus == FOCUS.MANUAL_ADDR0){
			m_ipaddr_manual_ipaddr.doKeyNum(event);
		}
		else if (m_focus == FOCUS.MANUAL_ADDR1)
		{
			m_ipaddr_manual_mask.doKeyNum(event);
		}
		else if (m_focus == FOCUS.MANUAL_ADDR2)
		{
			m_ipaddr_manual_gateway.doKeyNum(event);
		}
		else if (m_focus == FOCUS.MANUAL_ADDR3)
		{
			m_ipaddr_manual_dns.doKeyNum(event);
		}
	}	
	
	public void doKeyDel()
	{
		if (m_focus == FOCUS.MANUAL_ADDR0)
		{
			m_ipaddr_manual_ipaddr.doKeyDel();
		}
		else if (m_focus == FOCUS.MANUAL_ADDR1)
		{
			m_ipaddr_manual_mask.doKeyDel();
		}
		else if (m_focus == FOCUS.MANUAL_ADDR2)
		{
			m_ipaddr_manual_gateway.doKeyDel();
		}
		else if (m_focus == FOCUS.MANUAL_ADDR3)
		{
			m_ipaddr_manual_dns.doKeyDel();
		}
	}	
	
	public int doKeyOk()
	{
		if (m_focus == FOCUS.AUTO_FETCH)
		{
			Log.d(TAG, "AUTO_FETCH");

			if (CommonUtils.getSetting().getEthMagHWConnected())
			{
				Notice notice = Notice.makeNotice(mContext, 
						mContext.getResources().getString(R.string.settings_network_connect_wired), 
						Notice.LENGTH_SHORT);
    			notice.cancelAll();
    			notice.showImmediately();
    			return 0;
			}
			return 1;
		}
		else if (m_focus == FOCUS.MANUAL_OK)
		{
			Log.d(TAG, "MANUAL_OK");

			if (CommonUtils.getSetting().getEthMagHWConnected())
			{
				Notice notice = Notice.makeNotice(mContext, 
						mContext.getResources().getString(R.string.settings_network_connect_wired), 
						Notice.LENGTH_SHORT);
    			notice.cancelAll();
    			notice.showImmediately();
    			return 0;
			}
			return 2;
		}
		else if (m_focus == FOCUS.MANUAL_ADDR0)
		{
			m_ipaddr_manual_ipaddr.doKeyOk();
		}
		else if (m_focus == FOCUS.MANUAL_ADDR1)
		{
			m_ipaddr_manual_mask.doKeyOk();
		}
		else if (m_focus == FOCUS.MANUAL_ADDR2)
		{
			m_ipaddr_manual_gateway.doKeyOk();
		}
		else if (m_focus == FOCUS.MANUAL_ADDR3)
		{
			m_ipaddr_manual_dns.doKeyOk();
		}
		
		return 0;
	}
	
	public String getIpaddr()
	{
		return m_ipaddr_manual_ipaddr.getText();
	}
	
	public String getGateway()
	{
		return m_ipaddr_manual_gateway.getText();
	}
	
	public String getDns()
	{
		return m_ipaddr_manual_dns.getText();
	}
	
	public String getNetworkPrfix()
	{
		String netmask = m_ipaddr_manual_mask.getText();
		String[] netmaskList = netmask.split("\\.");
		Log.d(TAG, "routeList:"+ netmaskList.length);
		String s0 = Integer.toBinaryString(Integer.parseInt(netmaskList[0]));
		String s1 = Integer.toBinaryString(Integer.parseInt(netmaskList[1]));
		String s2 = Integer.toBinaryString(Integer.parseInt(netmaskList[2]));
		String s3 = Integer.toBinaryString(Integer.parseInt(netmaskList[3]));
		
		Log.d(TAG, "s0:"+ s0);
		Log.d(TAG, "s1:"+ s1);
		Log.d(TAG, "s2:"+ s2);
		Log.d(TAG, "s3:"+ s3);
		
		int count = getCount(s0+s1+s2+s3);
		return String.valueOf(count);
	}
	
	public int getCount(String str)
	{
		int x=0;  
        for(int i=0;i<=str.length()-1;i++) {  
            String getstr=str.substring(i,i+1);  
            if(getstr.equals("1")){  
                x++;  
            }  
        }
        return x;
	}
	
	public void Show(String ssidName, int level, Handler handler, boolean isPwd, WifiConfiguration config)
	{
		m_ipAddr = "";
		m_gateway = "";
		m_dns = "";
		m_netmask = "";
		
		if (0 == level)
        {
			m_signal.setImageResource(R.drawable.settings_network_wifi_wifi0_uf);
        }
        else if (1 == level)
        {
        	m_signal.setImageResource(R.drawable.settings_network_wifi_wifi1_uf);
        }
        else if (2 == level)
        {
        	m_signal.setImageResource(R.drawable.settings_network_wifi_wifi2_uf);
        }
		
		mHandler = handler;
		m_ssidName.setText(ssidName);
		
		Log.d(TAG, "Show isPwd:"+isPwd);
		
		if (isPwd)
		{
			if (config.ipAssignment == IpAssignment.STATIC) 
			{
				Log.d(TAG, "IpAssignment: static");
				
				this.m_tv_manual.requestFocus();
				m_focus = FOCUS.MANUAL;
				this.manualViewShow();
				setSubMenuBg(3);
            } 
			else 
			{
				Log.d(TAG, "IpAssignment: dhcp");
				
				this.m_tv_auto.requestFocus();
				m_focus = FOCUS.AUTO;
				this.autoViewShow();
				setSubMenuBg(2);
            }
			
			LinkProperties linkProperties = config.linkProperties;
            Iterator<LinkAddress> iterator = linkProperties.getLinkAddresses().iterator();
            if (iterator.hasNext()) {
                LinkAddress linkAddress = iterator.next();
                m_ipAddr = linkAddress.getAddress().getHostAddress();
                
                m_Prefix = Integer.toString(linkAddress.getNetworkPrefixLength());
                Log.d(TAG, "getNetworkPrefixLength:" + m_Prefix);
                
                int prefix = Integer.parseInt(m_Prefix);
                
                int count = prefix % 8;		// 剩余的位数
                int num = prefix / 8;		// 包含几个255
                
                String netmask = "";
                String zero ="00000000";
                String one ="11111111";
                
                if (4 == num)
                {
                	netmask = "255.255.255.255";
                }
                else if (3 == num)
                {
                	//count
                	String before = String.valueOf(count);
                	Log.d(TAG, "before:" + before);
                	String after = one.substring(0, count) + zero.substring(0, 8 - Integer.parseInt(before));
                	Log.d(TAG, "after:" + after);
                	BigInteger src = new BigInteger(after,2);
                	
                	netmask = "255.255.255." + src.toString();
                }
                else if (2 == num)
                {
                	//count
                	String before = String.valueOf(count);
                	Log.d(TAG, "before:" + before);
                	String after =  one.substring(0, count) + zero.substring(0, 8 - Integer.parseInt(before));
                	Log.d(TAG, "after:" + after);
                	BigInteger src = new BigInteger(after,2);
                	netmask = "255.255." + src.toString() + ".0";
                }
                else if (1 == num)
                {
                	//count
                	String before = String.valueOf(count);
                	Log.d(TAG, "before:" + before);
                	String after =  one.substring(0, count) + zero.substring(0, 8 - Integer.parseInt(before));
                	Log.d(TAG, "after:" + after);
                	BigInteger src = new BigInteger(after,2);
                	netmask = "255." + src.toString() + ".0.0";
                }
                else if (0 == num)
                {
                	//count
                	String before = String.valueOf(count);
                	Log.d(TAG, "before:" + before);
                	
                	if (before.equals("0"))
                	{
                		netmask = "0.0.0.0";
                	}
                	else
                	{
                		String after =  one.substring(0, count) + zero.substring(0, Integer.parseInt(before));
                    	Log.d(TAG, "after:" + after);
                    	BigInteger src = new BigInteger(after,2);
                    	netmask = src.toString() + ".0.0.0";
                	}
                }
                
                Log.d(TAG, "m_netmask:" + m_netmask);
                
                m_netmask = netmask;
            }

            for (RouteInfo route : linkProperties.getRoutes()) {
                if (route.isDefaultRoute()) {
                	m_gateway = route.getGateway().getHostAddress();
                    break;
                }
            }

            Iterator<InetAddress> dnsIterator = linkProperties.getDnses().iterator();
            if (dnsIterator.hasNext()) {
            	m_dns = dnsIterator.next().getHostAddress();
            }
		}
		else
		{
			this.m_tv_auto.requestFocus();
			m_focus = FOCUS.AUTO;
			this.autoViewShow();
			setSubMenuBg(2);
		}
		
		Log.d(TAG, "m_ipAddr1:"+m_ipAddr);
		Log.d(TAG, "m_gateway1:"+m_gateway);
		Log.d(TAG, "m_netmask1:"+m_netmask);
		Log.d(TAG, "m_dns1:"+m_dns);
		
		setAutoNetworkUI();
		setManualNetworkUI();
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
			this.m_tv_auto.setBackgroundResource(R.drawable.settings_network_submenu_unfocus);
			this.m_tv_manual.setBackgroundResource(R.drawable.settings_network_submenu_unfocus);
			
			this.m_tv_auto.setTextColor(this.getResources().getColor(R.drawable.settings_network_tv_text_focus));
			this.m_tv_manual.setTextColor(this.getResources().getColor(R.drawable.settings_network_tv_text_unfocus));
		}
		else if (1 == index)
		{
			this.m_tv_auto.setBackgroundResource(R.drawable.settings_network_submenu_unfocus);
			this.m_tv_manual.setBackgroundResource(R.drawable.settings_network_submenu_unfocus);
			
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
	
}
