/**
 * 
 * Copyright © 2014GreatVision. All rights reserved.
 *
 * @Title: IpAddrAuto.java
 * @Prject: BananaTvSetting
 * @Description: TODO
 * @author: luojw 
 * @date: 2014-1-21 上午11:50:29
 */
package com.sz.ead.framework.mul2launcher.settings.network;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sz.ead.framework.mul2launcher.R;

public class IpAddrAuto extends RelativeLayout {

	public TextView m_ipaddr1;		// IP地址控件第一项
	public TextView m_ipaddr2;		// IP地址控件第二项
	public TextView m_ipaddr3;		// IP地址控件第三项
	public TextView m_ipaddr4;		// IP地址控件第四项
	
	public IpAddrAuto(Context context) {
		super(context);
		inflate(context, R.layout.settings_network_view_ipaddr_auto, this);
		// TODO Auto-generated constructor stub
		
		m_ipaddr1 = (TextView)findViewById(R.id.id_setting_network_auto_ipaddress1);
		m_ipaddr2 = (TextView)findViewById(R.id.id_setting_network_auto_ipaddress2);
		m_ipaddr3 = (TextView)findViewById(R.id.id_setting_network_auto_ipaddress3);
		m_ipaddr4 = (TextView)findViewById(R.id.id_setting_network_auto_ipaddress4);
	}

	public IpAddrAuto(Context context, AttributeSet attrs) 
	{
		super(context, attrs);
		
		inflate(context, R.layout.settings_network_view_ipaddr_auto, this);
		// TODO Auto-generated constructor stub
		
		m_ipaddr1 = (TextView)findViewById(R.id.id_setting_network_auto_ipaddress1);
		m_ipaddr2 = (TextView)findViewById(R.id.id_setting_network_auto_ipaddress2);
		m_ipaddr3 = (TextView)findViewById(R.id.id_setting_network_auto_ipaddress3);
		m_ipaddr4 = (TextView)findViewById(R.id.id_setting_network_auto_ipaddress4);
	}
	
	/**
	 * 传入地址，分别设置每个TextView的值，如果传入的值不符合，则默认为0.0.0.0 
	 * @Title: setText
	 * @Description: TODO
	 * @param addr
	 * @return: void
	 */
	public void setText(String addr)
	{
		if (addr == null)
		{
			setDefault();
		}
		else
		{
			String[] strArray = addr.split("\\.");
			
			if (strArray.length < 4)
			{
				setDefault();
			}
			else
			{
				m_ipaddr1.setText(strArray[0]);
				m_ipaddr2.setText(strArray[1]);
				m_ipaddr3.setText(strArray[2]);
				m_ipaddr4.setText(strArray[3]);
			}
		}
	}
	
	/**
	 * 设置默认值为0.0.0.0 
	 * @Title: setDefault
	 * @Description: TODO
	 * @return: void
	 */
	public void setDefault()
	{
		m_ipaddr1.setText(R.string.settings_network_0);
		m_ipaddr2.setText(R.string.settings_network_0);
		m_ipaddr3.setText(R.string.settings_network_0);
		m_ipaddr4.setText(R.string.settings_network_0);
	}
	
	/**
	 * 获取控件一行控件内容
	 * @Title: getText
	 * @Description: TODO
	 * @return
	 * @return: String
	 */
	public String getText()
	{
		String ipaddr = "";
		
		ipaddr = m_ipaddr1.getText().toString();
		ipaddr += ".";
		ipaddr = m_ipaddr2.getText().toString();
		ipaddr += ".";
		ipaddr = m_ipaddr3.getText().toString();
		ipaddr += ".";
		ipaddr = m_ipaddr4.getText().toString();
		
		return ipaddr;
	}

}
