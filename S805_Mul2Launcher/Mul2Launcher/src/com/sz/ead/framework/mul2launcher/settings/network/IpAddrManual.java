/**
 * 
 * Copyright © 2014GreatVision. All rights reserved.
 *
 * @Title: IpAddrManual.java
 * @Prject: BananaTvSetting
 * @Description: TODO
 * @author: luojw 
 * @date: 2014-1-21 上午11:52:43
 */

package com.sz.ead.framework.mul2launcher.settings.network;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.sz.ead.framework.mul2launcher.R;

public class IpAddrManual extends RelativeLayout {
	IpAddrEditText m_ipaddr;
	
	Context mContext;
	
	EditText m_ipaddr_child0;		// 手动配置IP地址控件第一项
	EditText m_ipaddr_child1;		// 手动配置IP地址控件第二项
	EditText m_ipaddr_child2;		// 手动配置IP地址控件第三项
	EditText m_ipaddr_child3;		// 手动配置IP地址控件第四项
	
	enum FOCUS
	{
		NOT_FOCUS,				// 焦点不在上面
		IPADDR_CHILD0,			// 焦点在IP地址控件第一项
		IPADDR_CHILD1,			// 焦点在IP地址控件第二项
		IPADDR_CHILD2,			// 焦点在IP地址控件第三项
		IPADDR_CHILD3,			// 焦点在IP地址控件第四项
	};
	
	FOCUS m_focus = FOCUS.NOT_FOCUS;
	
	public IpAddrManual(Context context) {
		super(context);
		inflate(context, R.layout.settings_network_view_ipaddr_manual, this);
		// TODO Auto-generated constructor stub
		mContext = context;
		findView();
	}
	
	public IpAddrManual(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		inflate(context, R.layout.settings_network_view_ipaddr_manual, this);
		// TODO Auto-generated constructor stub
		mContext = context;
		findView();
	}
	
	Handler mHandler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            
            switch (msg.what) {
            case 1:
            {
            	int num = msg.arg1;
            	
            	if (num > -1 && num < 10)	// 数字键
            	{
            		inPutNum(num);
            	}
            	else if (num == 11)			// 删除键
            	{
            		doKeyDel();
            	}
            }
            break;
            default:
                break;
            }
        }
    };
	
	/**
	 * 此控件是否选中 
	 * @Title: ipAddrFocus
	 * @Description: TODO
	 * @param focus
	 * @return: void
	 */
	public void ipAddrFocus()
	{
		this.setNullDefault();
	}
	
	/**
	 * 初始化控件
	 * @Title: findView
	 * @Description: TODO
	 * @return: void
	 */
	public void findView()
	{
		m_ipaddr = (IpAddrEditText)findViewById(R.id.id_setting_network_manual_ipaddress);
		
		m_ipaddr_child0 = (EditText)findViewById(R.id.id_setting_network_manual_ipaddress1);
		m_ipaddr_child1 = (EditText)findViewById(R.id.id_setting_network_manual_ipaddress2);
		m_ipaddr_child2 = (EditText)findViewById(R.id.id_setting_network_manual_ipaddress3);
		m_ipaddr_child3 = (EditText)findViewById(R.id.id_setting_network_manual_ipaddress4);
		
		m_ipaddr_child0.setInputType(InputType.TYPE_NULL);
		m_ipaddr_child1.setInputType(InputType.TYPE_NULL);
		m_ipaddr_child2.setInputType(InputType.TYPE_NULL);
		m_ipaddr_child3.setInputType(InputType.TYPE_NULL);
	}
	
	public void doKeyUp()
	{
		m_ipaddr_child0.requestFocus();
		m_focus = FOCUS.IPADDR_CHILD0;
	}
	
	@SuppressLint("NewApi")
	public void doKeyDown()
	{
		m_ipaddr_child0.requestFocus();
		m_focus = FOCUS.IPADDR_CHILD0;
	}
	
	public void doKeyLeft()
	{	
		if (m_focus == FOCUS.IPADDR_CHILD1)
		{
			this.editIsNull(this.m_ipaddr_child1);
			
			m_ipaddr_child0.requestFocus();
			m_focus = FOCUS.IPADDR_CHILD0;
		}
		else if (m_focus == FOCUS.IPADDR_CHILD2)
		{
			this.editIsNull(this.m_ipaddr_child2);
			
			m_ipaddr_child1.requestFocus();
			m_focus = FOCUS.IPADDR_CHILD1;
		}
		else if (m_focus == FOCUS.IPADDR_CHILD3)
		{
			this.editIsNull(this.m_ipaddr_child3);
			
			m_ipaddr_child2.requestFocus();
			m_focus = FOCUS.IPADDR_CHILD2;
		}
	}
	
	public void doKeyRight()
	{
		if (m_focus == FOCUS.IPADDR_CHILD0)
		{
			this.editIsNull(this.m_ipaddr_child0);
			
			m_ipaddr_child1.requestFocus();
			m_focus = FOCUS.IPADDR_CHILD1;
		}
		else if (m_focus == FOCUS.IPADDR_CHILD1)
		{
			this.editIsNull(this.m_ipaddr_child1);
			
			m_ipaddr_child2.requestFocus();
			m_focus = FOCUS.IPADDR_CHILD2;
		}
		else if (m_focus == FOCUS.IPADDR_CHILD2)
		{
			this.editIsNull(this.m_ipaddr_child2);
			
			m_ipaddr_child3.requestFocus();
			m_focus = FOCUS.IPADDR_CHILD3;
		}
	}
	
	public void doKeyOk()
	{
		//doKeyRight();
		IPInputDialog ipInputDialog = new IPInputDialog(mContext, mHandler);
		ipInputDialog.show();
	}
	
	/**
	 * 数字键 
	 * @Title: doKeyNum
	 * @Description: TODO
	 * @param event
	 * @return: void
	 */
	public void doKeyNum(KeyEvent event)
	{
		int key = -1;
		
		switch (event.getKeyCode())
		{
		case KeyEvent.KEYCODE_0:
			key = 0;
			break;
		case KeyEvent.KEYCODE_1:
			key = 1;
			break;
		case KeyEvent.KEYCODE_2:
			key = 2;
			break;
		case KeyEvent.KEYCODE_3:
			key = 3;
			break;
		case KeyEvent.KEYCODE_4:
			key = 4;
			break;
		case KeyEvent.KEYCODE_5:
			key = 5;
			break;
		case KeyEvent.KEYCODE_6:
			key = 6;
			break;
		case KeyEvent.KEYCODE_7:
			key = 7;
			break;
		case KeyEvent.KEYCODE_8:
			key = 8;
			break;
		case KeyEvent.KEYCODE_9:
			key = 9;
			break;
		default:
			break;
		}
		
		inPutNum(key);
	}
	
	/**
	 * 数字键输入处理 
	 * @Title: inPutNum
	 * @Description: TODO
	 * @param key
	 * @return: void
	 */
	public void inPutNum(int key)
	{
		if (m_focus == FOCUS.IPADDR_CHILD0)
		{
			if (matchText(this.m_ipaddr_child0,key))
			{
				this.m_ipaddr_child1.requestFocus();
				this.m_focus = FOCUS.IPADDR_CHILD1;
			}
		}
		else if (m_focus == FOCUS.IPADDR_CHILD1)
		{
			if (matchText(this.m_ipaddr_child1,key))
			{
				this.m_ipaddr_child2.requestFocus();
				this.m_focus = FOCUS.IPADDR_CHILD2;
			}
		}
		else if (m_focus == FOCUS.IPADDR_CHILD2)
		{
			if (matchText(this.m_ipaddr_child2,key))
			{
				this.m_ipaddr_child3.requestFocus();
				this.m_focus = FOCUS.IPADDR_CHILD3;
			}
		}
		else if (m_focus == FOCUS.IPADDR_CHILD3)
		{
			matchText(this.m_ipaddr_child3,key);
		}
	}
	
	/**
	 * Ip地址中数字是否已有三个，有的话返回true，反之则false 
	 * @Title: matchText
	 * @Description: TODO
	 * @param edittext
	 * @param key
	 * @return
	 * @return: boolean
	 */
	public boolean matchText(EditText edittext,int key) 
	{
		String editAddr = edittext.getText().toString();
		
		if (3 == editAddr.length())
		{
			edittext.setText(String.valueOf(key));
			edittext.setSelection(edittext.getText().toString().length());
			return false;
		}
		
		if ((1 == editAddr.length()) && (editAddr.matches("0")))
		{
			edittext.setText(String.valueOf(key));
			edittext.setSelection(edittext.getText().toString().length());
			return false;
		}
		
		String newAddr = editAddr + String.valueOf(key);
		
		if (Integer.parseInt(newAddr) > 255)
		{
			String defaultAddr = "255";
			edittext.setText(defaultAddr);
			edittext.setSelection(edittext.getText().toString().length());
			return true;
		}
		else
		{
			edittext.setText(newAddr);
			edittext.setSelection(edittext.getText().toString().length());
			if (3 == newAddr.length())
			{
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * 删除键处理 
	 * @Title: doKeyDel
	 * @Description: TODO
	 * @return: void
	 */
	public void doKeyDel()
	{
		if (m_focus == FOCUS.IPADDR_CHILD0)
		{
			this.delIpAddr(this.m_ipaddr_child0);
		}
		else if (m_focus == FOCUS.IPADDR_CHILD1)
		{
			this.delIpAddr(this.m_ipaddr_child1);
		}
		else if (m_focus == FOCUS.IPADDR_CHILD2)
		{
			this.delIpAddr(this.m_ipaddr_child2);
		}
		else if (m_focus == FOCUS.IPADDR_CHILD3)
		{
			this.delIpAddr(this.m_ipaddr_child3);
		}
	}
	
	/**
	 * 删除IP地址控件中的最后一位
	 * @Title: delIpAddr
	 * @Description: TODO
	 * @param edittext
	 * @return: void
	 */
	public void delIpAddr(EditText edittext)
	{
		String culAddr = edittext.getText().toString();
		
		String newAddr = ""; 
		
		if (3 == culAddr.length())
		{
			newAddr = String.valueOf((Integer.parseInt(culAddr) - ((Integer.parseInt(culAddr)%100)%10))/10);
		}
		else if (2 == culAddr.length())
		{
			newAddr = String.valueOf((Integer.parseInt(culAddr) - (Integer.parseInt(culAddr)%10))/10);
		}
		else if (1 == culAddr.length())
		{
			newAddr = "";
		}
		
		edittext.setText(newAddr);
		edittext.setSelection(edittext.getText().toString().length());
	}
	
	/**
	 * 如果EditText为空时，默认为0 
	 * @Title: editIsNull
	 * @Description: TODO
	 * @param edittext
	 * @return: void
	 */
	public void editIsNull(EditText edittext)
	{
		if (edittext.getText().toString().matches(""))
		{
			edittext.setText("0");
			edittext.setSelection(edittext.getText().toString().length());
			
		}
	}
	
	/**
	 * 如果控件内容为空时，默认为0
	 * @Title: setNullDefault
	 * @Description: TODO
	 * @return: void
	 */
	public void setNullDefault()
	{
		if (m_ipaddr_child0.getText().toString().matches(""))
		{
			m_ipaddr_child0.setText("0");
			m_ipaddr_child0.setSelection(m_ipaddr_child0.getText().toString().length());
		}
		
		if (m_ipaddr_child1.getText().toString().matches(""))
		{
			m_ipaddr_child1.setText("0");
			m_ipaddr_child1.setSelection(m_ipaddr_child1.getText().toString().length());
		}
		
		if (m_ipaddr_child2.getText().toString().matches(""))
		{
			m_ipaddr_child2.setText("0");
			m_ipaddr_child2.setSelection(m_ipaddr_child2.getText().toString().length());
		}
		
		if (m_ipaddr_child3.getText().toString().matches(""))
		{
			m_ipaddr_child3.setText("0");
			m_ipaddr_child3.setSelection(m_ipaddr_child3.getText().toString().length());
		}
	}
	
	/**
	 * 设置控件内容
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
				m_ipaddr_child0.setText(strArray[0]);
				m_ipaddr_child0.setSelection(m_ipaddr_child0.getText().toString().length());
				m_ipaddr_child1.setText(strArray[1]);
				m_ipaddr_child1.setSelection(m_ipaddr_child1.getText().toString().length());
				m_ipaddr_child2.setText(strArray[2]);
				m_ipaddr_child2.setSelection(m_ipaddr_child2.getText().toString().length());
				m_ipaddr_child3.setText(strArray[3]);
				m_ipaddr_child3.setSelection(m_ipaddr_child3.getText().toString().length());
			}
		}
	}
	
	/**
	 * 初始化控件内容
	 * @Title: setDefault
	 * @Description: TODO
	 * @return: void
	 */
	public void setDefault()
	{
		m_ipaddr_child0.setText(R.string.settings_network_0);
		m_ipaddr_child0.setSelection(m_ipaddr_child0.getText().toString().length());
		m_ipaddr_child1.setText(R.string.settings_network_0);
		m_ipaddr_child1.setSelection(m_ipaddr_child1.getText().toString().length());
		m_ipaddr_child2.setText(R.string.settings_network_0);
		m_ipaddr_child2.setSelection(m_ipaddr_child2.getText().toString().length());
		m_ipaddr_child3.setText(R.string.settings_network_0);
		m_ipaddr_child3.setSelection(m_ipaddr_child3.getText().toString().length());
	}
	
	/**
	 * 获取控件内容
	 * @Title: getText
	 * @Description: TODO
	 * @return
	 * @return: String
	 */
	public String getText()
	{
		String ipaddr1 = m_ipaddr_child0.getText().toString();
		String ipaddr2 = ipaddr1 + ".";
		String ipaddr3 = ipaddr2 + m_ipaddr_child1.getText().toString();
		String ipaddr4 = ipaddr3 + ".";
		String ipaddr5 = ipaddr4 + m_ipaddr_child2.getText().toString();
		String ipaddr6 = ipaddr5 + ".";
		String ipaddr7 = ipaddr6 + m_ipaddr_child3.getText().toString();
		
		return ipaddr7;
	}
}
