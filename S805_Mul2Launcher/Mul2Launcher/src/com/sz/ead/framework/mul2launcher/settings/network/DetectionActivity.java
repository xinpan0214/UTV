/**
 * 
 * Copyright © 2014GreatVision. All rights reserved.
 *
 * @Title: DetectionActivity.java
 * @Prject: BananaTvSetting
 * @Description: TODO
 * @author: luojw 
 * @date: 2014-1-21 上午11:49:38
 */
package com.sz.ead.framework.mul2launcher.settings.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import android.content.Context;
import android.login.loginservice.LoginManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.notice.noticemanager.Notice;
import android.os.Handler;
import android.os.Message;
import android.os.SystemProperties;
import android.setting.settingservice.SettingManager;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sz.ead.framework.mul2launcher.R;
import com.sz.ead.framework.mul2launcher.settings.util.CommonUtils;
import com.szgvtv.ead.framework.bi.Bi;

public class DetectionActivity extends RelativeLayout {
	
    private ImageView mImageCircle;
    private ImageView mImageIpaddrLine;
    private ImageView mImageGateWayLine;
    private ImageView mImageDnsLine;
    private ImageView mImageServerLine;
    
    private ImageView mImageIpaddr;
    private ImageView mImageGateWay;
    private ImageView mImageDns;
    private ImageView mImageServer;
    private TextView mResult;						// 检测结果显示
	private Button mButton;
	private Animation rotateSelf;
	private static final int DETECTION_TIMECOUNT = 1;

	String TAG = "network";
	String m_ipAddr;
	String m_gateway;
	boolean m_isGatewayCorrect;			// 网关是否正确
	String m_dns;
	int m_dnsNormal;
	Context m_context;
	int m_result;
	ScheduledExecutorService mTimer;
    boolean m_isCheck;
    int mIndex;
    int m_resultTmp;
    int m_typeServer;
    HttpDownloader m_httpDownloader;		// 下载
    int mMsgType;
    String m_url;
    
    boolean mServerResult;
    
    private LoginManager m_login;			// Login服务
	private SettingManager m_setting;		//	Setting服务
    
    /**
     * 检测结果处理
     */
    Handler m_handler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            
            switch (msg.what) {
            case DETECTION_TIMECOUNT:						
            {
        		if (0 == mIndex)				// IP地址检测
        		{
        			if (m_ipAddr.equals(""))
    				{
        				mImageIpaddr.setImageResource(R.drawable.settings_network_detection_warn);
    				}else{
        				mImageIpaddr.setImageResource(R.drawable.settings_network_detection_correct);
        			}
        			mImageIpaddr.setVisibility(View.VISIBLE);
        			mImageIpaddrLine.setVisibility(View.VISIBLE);
        		}else if (1 == mIndex){  //网关检测
        			if (m_gateway.equals(""))
    				{
        				mImageGateWay.setImageResource(R.drawable.settings_network_detection_warn);
    				}else{
        				if (m_isGatewayCorrect)
        				{
        					mImageGateWay.setImageResource(R.drawable.settings_network_detection_correct);
        				}else{
        					mImageGateWay.setImageResource(R.drawable.settings_network_detection_warn);
        				}
        			}
        			mImageGateWay.setVisibility(View.VISIBLE);
        			mImageGateWayLine.setVisibility(View.VISIBLE);
        		}else if (2 == mIndex){   //DNS检测
        			if (-2 != m_dnsNormal)
        			{
        				if (m_dns.equals(""))
        				{
            				mImageDns.setImageResource(R.drawable.settings_network_detection_warn);
        				}else{
            				if (-1 == m_dnsNormal)
            				{
            					mImageDns.setImageResource(R.drawable.settings_network_detection_warn);
            				}
            				else
            				{
            					mImageDns.setImageResource(R.drawable.settings_network_detection_correct);
            				}
            			}
            			mImageDns.setVisibility(View.VISIBLE);
            			mImageDnsLine.setVisibility(View.VISIBLE);
            			checkServer();
        			}
        			else
        			{
        				return;
        			}
        			
        		}else if (3 == mIndex){
        		}else if (4 == mIndex){
        			if (!m_isCheck)
        			{
            			if(mTimer != null)
            			{
            				mTimer.shutdownNow();
            				mTimer = null;
            			}
            			mImageCircle.clearAnimation();
            			if (mServerResult)					// 服务器是否正常
            			{
            				mImageServer.setImageResource(R.drawable.settings_network_detection_correct);
            				mImageServerLine.setVisibility(View.VISIBLE);
                			mImageServer.setVisibility(View.VISIBLE);
							mButton.setText(mContext.getResources().getString(R.string.settings_network_detetion));
							Notice notice = Notice.makeNotice(m_context, 
            						m_context.getResources().getString(R.string.settings_network_detection_normal), 
    								Notice.LENGTH_SHORT);
    	        			notice.cancelAll();
    	        			notice.showImmediately();
            			}else{
            				mImageServer.setImageResource(R.drawable.settings_network_detection_warn);
                			mImageServer.setVisibility(View.VISIBLE);
                			mImageServerLine.setVisibility(View.VISIBLE);
            				Log.d(TAG, "get text:"+mResult.getText().toString());
            				Log.d(TAG, "m_result:"+m_result);
            				
            				mResult.setText("");
            				mResult.setText(m_context.getResources().getString(R.string.settings_network_errorcode)
            						+String.valueOf(m_result));
            				mResult.setVisibility(View.VISIBLE);
            				
							mButton.setText(mContext.getResources().getString(R.string.settings_network_re_detetion));
							Notice notice = Notice.makeNotice(m_context, m_context.getResources().getString(R.string.settings_network_detection_abnormal),
            						Notice.LENGTH_SHORT);
            				notice.setTextColor(m_context.getResources().getColor(R.color.settings_notice_red));
            				notice.cancelAll();
            				notice.show();
            			}
        			}
        			else
        			{
        				return;
        			}
        			
        		}
        		mIndex++;
            }
            break;
            case 7:
            {
            	Log.d(TAG, "dns ret 0");
            	m_dnsNormal = 0;
            }
            break;
            case 8:
            {
            	Log.d(TAG, "dns ret -1");
            	m_dnsNormal = -1;
            }
            break;
            case 9:
            {
            	Log.d(TAG, "m_typeServer："+m_typeServer);
            	if (0 == m_typeServer)
            	{
            		m_result = 0;
            		m_typeServer++;
            	}
            	else if (1 == m_typeServer)
            	{
            		m_result = m_result|(0<<1);
            		m_typeServer++;
            		
            	}
            	else if (2 == m_typeServer)
            	{
            		m_result = m_result|(0<<2);
            		if (m_result == 7)
            	    {
            			m_result = -1;
            	    }
            		showServerResult();
            		return;
            	}
            	serverDetection(m_typeServer);
            }
            	break;
            case 10:
            {
            	Log.d(TAG, "m_typeServer："+m_typeServer);
            	if (0 == m_typeServer)
            	{
            		m_result = 1;
            		m_typeServer++;
            	}
            	else if (1 == m_typeServer)
            	{
            		m_result = m_result|(1<<1);
            		m_typeServer++;
            		
            	}
            	else if (2 == m_typeServer)
            	{
            		m_result = m_result|(1<<2);
            		if (m_result == 7)
            	    {
            			m_result = -1;
            	    }
            		showServerResult();
            		return;
            	}
            	serverDetection(m_typeServer);
            }
            	break;
            case 11:				// 检测结果显示
            {
            	mImageCircle.clearAnimation();
            	Notice notice = Notice.makeNotice(m_context, 
    					m_context.getResources().getString(R.string.settings_network_detection_not_newtork),
						Notice.LENGTH_SHORT);
				notice.setTextColor(m_context.getResources().getColor(R.color.settings_notice_red));
				notice.cancelAll();
				notice.show();
            }
            	break;
            default:
                break;
            }
        }
    };
    
	public DetectionActivity(Context context) {
		super(context);
		inflate(context, R.layout.settings_network_view_network_detection, this);
		// TODO Auto-generated constructor stub
		m_context = context;
		findView();
	}
	
	/**
	 * 控件初始化
	 * @Title: findView
	 * @Description: TODO
	 * @return: void
	 */
	public void findView()
	{
		mButton = (Button)this.findViewById(R.id.id_setting_network_detection_button);
		mImageCircle = (ImageView)this.findViewById(R.id.id_setting_network_decetion_img);
		mImageIpaddrLine = (ImageView)this.findViewById(R.id.id_setting_network_ipaddr_line);
		mImageGateWayLine = (ImageView)this.findViewById(R.id.id_setting_network_gateway_line);
		mImageDnsLine = (ImageView)this.findViewById(R.id.id_setting_network_dns_line);
		mImageServerLine = (ImageView)this.findViewById(R.id.id_setting_network_server_line);
		
		mImageIpaddr = (ImageView)this.findViewById(R.id.id_setting_network_ipaddr_result);
	    mImageGateWay = (ImageView)this.findViewById(R.id.id_setting_network_gateway_result);
	    mImageDns = (ImageView)this.findViewById(R.id.id_setting_network_dns_result);
	    mImageServer = (ImageView)this.findViewById(R.id.id_setting_network_server_result);
		
	    mResult = (TextView)this.findViewById(R.id.id_setting_network_detection_result);
	    
	    init();
	    m_isCheck = false;
	    m_login = (LoginManager)m_context.getSystemService(Context.LOGIN_SERVICE);
	    m_setting = (SettingManager)m_context.getSystemService(Context.SETTING_SERVICE);
	}
	
	/**
	 * 网络检测初始化
	 * @Title: init
	 * @Description: TODO
	 * @return: void
	 */
	public void init()
	{
		mImageIpaddrLine.setVisibility(View.GONE);
		mImageGateWayLine.setVisibility(View.GONE);
		mImageDnsLine.setVisibility(View.GONE);
		mImageServerLine.setVisibility(View.GONE);
		mImageIpaddr.setVisibility(View.GONE);
	    mImageGateWay.setVisibility(View.GONE);
	    mImageDns.setVisibility(View.GONE);
	    mImageServer.setVisibility(View.GONE);
	    mResult.setVisibility(View.GONE);
	    
	    m_ipAddr = "";
		m_gateway = "";
		m_dns = "";
		mServerResult = false;
		
		rotateSelf = AnimationUtils.loadAnimation(m_context, R.anim.settings_network_detection_rotate);  
		LinearInterpolator lin = new LinearInterpolator();  
		rotateSelf.setInterpolator(lin);
	}
	
	/**
	 * OK键处理
	 * @Title: doKeyOk
	 * @Description: TODO
	 * @return: void
	 */
	public void doKeyOk()
	{
		if (!m_isCheck)
		{
			Bi.sendBiMsg(Bi.BICT_FIRMWARE, Bi.BICC_CHECK_NETWORK);
			init();
			mImageCircle.startAnimation(rotateSelf);
			mIndex = 0;
			m_isCheck = true;
			m_isGatewayCorrect = false;
			
			Runnable checkRunnable = new Runnable()
			{
	        	@Override  
	        	public void run() 
	        	{ 
	        		boolean hwCon = CommonUtils.getSetting().getEthMagHWConnected();
					Log.d(TAG, " HWcon-------:" + hwCon); 
					
					//网线是否插上
	    			if (hwCon)
	    			{
	    				ethernetCheck();
	    			}else{
	    				ConnectivityManager connectivityManager = (ConnectivityManager)m_context.getSystemService(Context.CONNECTIVITY_SERVICE);
	    				NetworkInfo m_networkInfo = connectivityManager.getActiveNetworkInfo();
	            		
	            		if (m_networkInfo != null && m_networkInfo.isAvailable()) 
	            		{
	            			Log.d(TAG, " isAvailable-------:" + m_networkInfo.isAvailable());
	            			wifiCheck();
	                    }
	            		else
	            		{
	            			m_handler.sendEmptyMessage(11);	// network not connect
	            			m_isCheck = false;
	            			Log.d(TAG, "return");
            				return;
	            		}
	    			}
	    			
	    			if(mTimer != null)
        			{
	    				mTimer.shutdownNow();
	    				mTimer = null;
        			}
        			
        			if(mTimer == null)
        			{
        				mTimer = Executors.newScheduledThreadPool(1);
        			}
        	    	
        			class Task implements Runnable{
        				/* (non-Javadoc)
        				 * @see java.util.TimerTask#run()
        				 */
        				@Override
        				public void run() {
        				   try {
        					   m_handler.sendEmptyMessage(1);
        				    } catch (Exception e) {
        				// TODO: handle exception
        				      e.printStackTrace();
        				    }
        				    }
        			};
        	    	
        			mTimer.scheduleAtFixedRate(new Task(), 3*1000, 3*1000,TimeUnit.MILLISECONDS);
	        	}
			};
			
			new Thread(checkRunnable).start();
		}
		
	}
	
	
	/**
	 * 向下键处理
	 * @Title: doKeyDown
	 * @Description: TODO
	 * @return: void
	 */
	public void doKeyDown()
	{
		mButton.requestFocus();
	}
	
	/**
	 * 有线网络处理
	 * @Title: ethernetCheck
	 * @Description: TODO
	 * @return: void
	 */
	public void ethernetCheck()
	{
		parseIp(true);
		parseGateWay();
		parseDns();
	}
	
	/**
	 * 解析IP
	 * @Title: parseIp
	 * @Description: TODO
	 * @param isEth0
	 * @return: void
	 */
	public void parseIp(boolean isEth0)
	{
		m_ipAddr = ""; 
		
		String ipaddr = "";
		try
		{
			ipaddr = getIpaddr(isEth0);
		}
		catch(Exception e) 
		{   
            e.printStackTrace();
            ipaddr = "";
            return;
        }   
		
		Log.d(TAG, "ipaddr line:"+ipaddr);
		
		String trimIpaddr = "";
		String replaceIpaddr = "";
		
		if (ipaddr != null)
		{
			if (!ipaddr.equals(""))
			{
				trimIpaddr = ipaddr.trim();
				Log.d(TAG, "ipaddr 111:"+trimIpaddr);
				replaceIpaddr = trimIpaddr.replace("inet addr:", "");
				
				Log.d(TAG, "ipaddr 222:"+replaceIpaddr);
				String[] result = replaceIpaddr.split(" ");
				
				for (int i = 0; i < result.length; i ++)
				{
					Log.d(TAG, "ipaddr child:"+result[i]);
				}
				
				m_ipAddr = result[0];
			}
		}		
	}
	
	/**
	 * 解析网关
	 * @Title: parseGateWay
	 * @Description: TODO
	 * @return: void
	 */
	public void parseGateWay()
	{
		m_gateway = "";
		String gateway = "";
		try
		{
			gateway = getEth0GateWay();
		}
		catch(Exception e) 
		{   
            e.printStackTrace();
            gateway = "";
            return;
        }   
		
		Log.d(TAG, "gateway line:"+gateway);
		
		String trimIpaddr = "";
		
		if (gateway != null)
		{
			if (!gateway.equals(""))
			{
				trimIpaddr = gateway.trim();
				Log.d(TAG, "gateway 111:"+trimIpaddr);
				
				String[] result = trimIpaddr.split(" ");
				
				for (int i = 0; i < result.length; i ++)
				{
					if (i != 0 && !result[i].equals(""))
					{
						Log.d(TAG, "gateway 222:"+result[i]);
						m_gateway = result[i];
						break;
					}
				}
				
				if (pingIpAddr(m_gateway))
				{
					m_isGatewayCorrect = true;
				}
				else
				{
					m_isGatewayCorrect = false;
				}
			}
		}
	}
	
	/**
	 * 网关是否ping通
	 * @Title: pingIpAddr
	 * @Description: TODO
	 * @param ipAddress
	 * @return
	 * @return: boolean
	 */
	private  boolean pingIpAddr(String ipAddress) {
        String mPingIpAddrResult;
        try {
            Process p = Runtime.getRuntime().exec("ping -c 1 -w 30 " + ipAddress); 
            
            Log.d(TAG, "ping -c 1 -w 30 " + ipAddress);
            
            int status = p.waitFor();
            Log.d(TAG, "status:" + status);
            if (status == 0) {
                return true;
            } else {
                mPingIpAddrResult = "Fail: IP addr not reachable";
            }
        } catch (IOException e) {
            mPingIpAddrResult = "Fail: IOException";
        } catch (InterruptedException e) {
            mPingIpAddrResult = "Fail: InterruptedException";
        }
        return false;
    }
	
	/**
	 * 解析DNS
	 * @Title: parseDns
	 * @Description: TODO
	 * @return: void
	 */
	public void parseDns()
	{
		m_dnsNormal = -2;
		m_dns = "";
		String dnsProp = "net.dns1";
		
		String dns = SystemProperties.get(dnsProp);
		Log.d(TAG, " dns:"+dns);
		
		if (dns != null)
		{
			if (!dns.equals(""))
			{
				m_dns = dns;
				
				Runnable dnsCheckRun = new Runnable()
				{
		        	@Override  
		        	public void run() 
		        	{ 
		        		try 
		        		{
		            		getIPByName("www.baidu.com");
		        			m_handler.sendEmptyMessage(7);
		        		} 
		        		catch (UnknownHostException e)
		        		{
		        			e.printStackTrace();
		        			m_handler.sendEmptyMessage(8);
		        		}
		        	}
				};
				
				new Thread(dnsCheckRun).start();
			}
			else
			{
				m_handler.sendEmptyMessage(8);
			}
		}
		else
		{
			m_handler.sendEmptyMessage(8);
		}
	}
	
	public static String getIPByName(String hostName)
            throws UnknownHostException 
            {
        InetAddress addr = InetAddress.getByName(hostName);
        
        return addr.getHostAddress();
    }
	
	/**
	 * 解析wifiDNS
	 * @Title: parseWlan0Dns
	 * @Description: TODO
	 * @return: void
	 */
	public void parseWlan0Dns()
	{
		m_dns = "";
		
		String dnsProp = "dhcp."+"wlan0"+".dns1";
		Log.d(TAG, "wlan0 dns getprop:"+dnsProp);
		String dns = SystemProperties.get(dnsProp);
		
		if (dns != null)
		{
			if (!dns.equals(""))
			{
				m_dns = dns; 
				Log.d(TAG, "wlan0 dns:"+m_dns);
			}
		}
	}
	
	/**
	 * 检测wifi
	 * @Title: wifiCheck
	 * @Description: TODO
	 * @return: void
	 */
	public void wifiCheck()
	{
		parseIp(false);
		parseGateWay();
		parseDns();
	}
	
	/**
	 * 获取IP地址
	 * @Title: getIpaddr
	 * @Description: TODO
	 * @param isEth0
	 * @return
	 * @throws IOException
	 * @return: String
	 */
	public String getIpaddr(boolean isEth0) throws IOException {  
       
		String cmd;
		
		if (isEth0)
		{
			Log.d(TAG, "busybox ifconfig eth0");
			cmd = "busybox ifconfig eth0"; // 
		}
		else
		{
			Log.d(TAG, "busybox ifconfig wlan0");
			cmd = "busybox ifconfig wlan0";
		}
		
        //Runtime对象

        Runtime runtime = Runtime.getRuntime();

        try {
              Process process = runtime.exec(cmd);

              //获得结果的输入流
              InputStream input = process.getInputStream();
              BufferedReader br = new BufferedReader(new InputStreamReader(input));

              String strLine;

              while (null != (strLine = br.readLine()))
              {
            	  if (strLine.contains("inet addr"))
            	  {
            		  return strLine;
            	  }
              }
        }
        catch (IOException e) 
        {
        	e.printStackTrace();
        }
        return "";
    }
	/**
	 * 获取网关
	 * @Title: getEth0GateWay
	 * @Description: TODO
	 * @return
	 * @throws IOException
	 * @return: String
	 */
	public String getEth0GateWay() throws IOException {  
	       
		Log.d(TAG, "busybox route -n");
		String cmd = "busybox route -n"; // 
        //Runtime对象

        Runtime runtime = Runtime.getRuntime();

        try {
              Process process = runtime.exec(cmd);

              //获得结果的输入流

              InputStream input = process.getInputStream();
              BufferedReader br = new BufferedReader(new InputStreamReader(input));

              String strLine;

              while (null != (strLine = br.readLine()))
              {
            	  if (strLine.startsWith("0.0.0.0"))
            	  {
            		  return strLine;
            	  }
              }
        }
        catch (IOException e) 
        {
        	e.printStackTrace();
        }
        return "";
    }
	
	/**
	 * 检测网关是否正确
	 * @Title: pingGateWay
	 * @Description: TODO
	 * @return
	 * @throws IOException
	 * @throws InterruptedException
	 * @return: String
	 */
	public String pingGateWay() throws IOException, InterruptedException {  
	       
		Log.d(TAG, "busybox ping -c 2 -q " + m_gateway);
		String cmd = "busybox ping -c 2 -q " + m_gateway;
        //Runtime对象

        Runtime runtime = Runtime.getRuntime();

        try {
              Process process = runtime.exec(cmd);

              //获得结果的输入流
              InputStream input = process.getInputStream();

              BufferedReader br = new BufferedReader(new InputStreamReader(input));

              String strLine;

              while (null != (strLine = br.readLine()))
              {
            	  if (strLine.contains("packet loss"))
            	  {
            		  //return strLine;
            		  Log.d(TAG, "contains:");
            		  return strLine;
            	  }
              }
              process.waitFor();
        }
        catch (IOException e) 
        {
        	m_isGatewayCorrect = false;
        	e.printStackTrace();
        }
        return "";
    }
	
	/**
	 * 是否正在检测
	 * @Title: isCheck
	 * @Description: TODO
	 * @return
	 * @return: boolean
	 */
	public boolean isCheck()
	{
		return this.m_isCheck;
	}
	
	public void checkServer()
	{
		m_result = 0;
		m_typeServer = 0;
		serverDetection(m_typeServer);
	}
	
	
	/**
	 * 依次检测各个服务器
	 * @Title: serverDetection
	 * @Description: TODO
	 * @param index
	 * @return: void
	 */
	public void serverDetection(int index)
	{
		m_httpDownloader = new HttpDownloader(); 
		
		m_resultTmp = 1;
				
		if (0 == index)
		{			
			if (m_login.getSTUrl().equals(""))
			{
				Log.d(TAG, "st is null");
				m_handler.sendEmptyMessage(10);
				return;
			}
			
			m_url = "http://" + m_login.getSTUrl() + "/timezone.action";
			Log.d(TAG, "st url:" + m_url);
		
		}
		else if (1 == index)
		{
			if (m_login.getASUrl().equals(""))
			{
				Log.d(TAG, "as is null");
				m_handler.sendEmptyMessage(10);
				return;
			}
			
			m_url = "http://" + m_login.getASUrl() + "/applist.action?"+ 
					"mac="+m_setting.getMac() + "&lang=" + m_setting.getCurLang()+"&cpuid="+m_setting.getCPUID()+
					"&accesscode="+m_setting.getAccessCode()+"&version="+m_setting.getSfVersion()+"&oem="+m_setting.getOem()+
					"&model="+m_setting.getHwVersion()+"&page=1&size=10";
			Log.d(TAG, "as url:" + m_url);
		}
		else if (2 == index)
		{
			if (m_login.getBIUrl().equals(""))
			{
				Log.d(TAG, "bi is null");
				m_handler.sendEmptyMessage(10);
				return;
			}
			m_url = "http://"+m_login.getBIUrl();
			
			Log.d(TAG, "bi url:" + m_url);
		}
		else if (3 == index)
		{
			if (m_login.getBIUrl().equals(""))
			{
				Log.d(TAG, "bi is null");
				m_handler.sendEmptyMessage(10);
				return;
			}
			m_url = "http://"+m_login.getBIUrl();
			
			Log.d(TAG, "bi url:" + m_url);
		}
		else if (4 == index)
		{
			if (m_login.getBOSSUrl().equals(""))
			{
				Log.d(TAG, "boss is null");
				m_handler.sendEmptyMessage(10);
				return;
			}
			m_url = "https://"+m_login.getBOSSUrl()+"/balance.action";
			
			Log.d(TAG, "boss url:" + m_url);
		}
		else if (5 == index)
		{
			if (m_login.getServerUrl("AD").equals(""))
			{
				Log.d(TAG, "ad is null");
				m_handler.sendEmptyMessage(10);
				return;
			}
			m_url = "http://"+m_login.getServerUrl("AD")+"/adnew.action?mac="+m_setting.getMac() + "&lang=" + m_setting.getCurLang()+"&type=0"+"&cpuid="+m_setting.getCPUID()+
					"&accesscode="+m_setting.getAccessCode()+"&version="+m_setting.getSfVersion()+"&oem="+m_setting.getOem()+
					"&model="+m_setting.getHwVersion()+"&timezone="+m_setting.getTimeZone()+"&datetime="+getDateTime()+"&locationid=q2Z42";
			
			m_url = m_url.replace("+", "%2b");
			m_url = m_url.replace(" ", "%20");
			
			Log.d(TAG, "ad url:" + m_url);
		}
		
		Runnable downloadRun = new Runnable()
		{
        	@Override  
        	public void run() 
        	{ 
        		if (4 == m_typeServer)
        		{
        			m_resultTmp = m_httpDownloader.downFile(m_handler,m_url,true,m_context);
        		}
        		else
        		{
        			m_resultTmp = m_httpDownloader.downFile(m_handler,m_url,false,m_context);
        		}
    			Log.d(TAG, "ret:" + String.valueOf(m_resultTmp));
        	}
		};
		
		new Thread(downloadRun).start();
	}
	
	/**
	 * 显示服务器检测结果
	 * @Title: showServerResult
	 * @Description: TODO
	 * @return: void
	 */
	public void showServerResult()
	{
		if (0 == m_result)
    	{
			mServerResult = true;
    	}
    	else
    	{
    		mServerResult = false;
    	}
    	
    	m_context.isRestricted();
    	m_isCheck = false;
	}
	
	 public String getDateTime() {
			String format = "yyyy-MM-dd HH:mm:ss";
			SimpleDateFormat sdf = new SimpleDateFormat(format);
			Date now = new Date(System.currentTimeMillis());
			return sdf.format(now);
		}
}
