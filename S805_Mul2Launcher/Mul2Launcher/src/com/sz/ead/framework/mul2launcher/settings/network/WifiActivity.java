/**
 * 
 * Copyright © 2014GreatVision. All rights reserved.
 *
 * @Title: WifiActivity.java
 * @Prject: BananaTvSetting
 * @Description: TODO
 * @author: luojw 
 * @date: 2014-1-21 上午11:57:55
 */
package com.sz.ead.framework.mul2launcher.settings.network;

import java.net.InetAddress;
import java.nio.channels.Selector;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.LinkAddress;
import android.net.LinkProperties;
import android.net.NetworkInfo;
import android.net.NetworkUtils;
import android.net.RouteInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiConfiguration.AuthAlgorithm;
import android.net.wifi.WifiConfiguration.IpAssignment;
import android.net.wifi.WifiConfiguration.KeyMgmt;
import android.net.wifi.WifiConfiguration.ProxySettings;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.notice.noticemanager.Notice;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sz.ead.framework.mul2launcher.R;
import com.sz.ead.framework.mul2launcher.settings.util.CommonUtils;
import com.szgvtv.ead.framework.bi.Bi;

public class WifiActivity extends RelativeLayout {

	Selector m_seletor;
	
	FOCUS m_focus;
	
	int m_index;		// 当前在哪一项
	int m_total;		// 一共有多少项
	
	//boolean m_wifiState;			// WIFI状态，true为开，false为关
	Context m_context;
	
	TextView m_ok_refresh;		// 按OK键，刷新WIFI
	TextView m_no_signal;		// No WIFI signal;
	TextView m_wifi_signal;		// 按OK键，刷新WIFI
	boolean m_no_wifi_signal;
	
	Button m_refresh;
	GridView m_listView;  
	private ProgressBar m_scan_progressBar;	// WIFI刷新旋转图标
    boolean mIsSenior;
    String[] m_wifiNames;
	boolean[] m_isLock;
	String[] m_mac;
	
	int[] m_level;
	boolean[] m_isPassword;		// 是否保存密码
	WifiConfiguration[] m_config;
	WifiConfiguration m_addConfig;
	int[] m_networkId;
	ListViewAdapter m_listAda;
	int[] m_encryption;
	
	boolean m_isFocusListView;
	
	boolean m_isConnect;  // 网络是否连接
	
	View mView = null;
	int mPos = -1;
	
	View m_currentView;
	ProgressBar m_currentBar;	//
	ImageView m_currentImage;
	ImageView m_senior;
	
	private WifiManager mWifiManager;
	
	private IntentFilter mIntentFilter;
	public static final String TAG = "network";
	private List<WifiConfiguration> mWifiConfiguration;
	private IpAssignment m_ipAssignment = IpAssignment.UNASSIGNED;
	private LinkProperties m_linkProperties = new LinkProperties();
	boolean m_scan; // 是否扫描
	boolean m_setting; //是否设置
	
	List<ScanResult> m_scanResult;// 扫描结果
	ScheduledExecutorService m_timer; 
	//Handler m_handler;
	private boolean m_isStartScan;
	private boolean m_isConfigChanged; // config file change
	// 加密方式
	static final int SECURITY_NONE = 0;
    static final int SECURITY_WEP = 1;
    static final int SECURITY_PSK = 2;
    static final int SECURITY_EAP = 3;
	
    private ConnectivityManager connectivityManager;
	private NetworkInfo m_networkInfo;
	private WifiInfo m_wifiInfo;
    	
	String mIpAddr;
	String mGateWay;
	String mDns;
	String mNetworkPrfix;
	
	WiFiPasswordAcitivity mWifipwd;
	RelativeLayout mWifiListView;
	WiFiSeniorLayout mWifiSeniorLayout;
	
	enum FOCUS
	{
		NOT_FOCUS,
		WIFI_RELRESH,
		WIFI_LISTVIEW,
		WIFI_SENIOR,
		WIFI_SENIORLAYOUT,
		WIFI_PWD
	}
	
	/**
	 * 设置完成处理
	 */
	Handler m_handler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            
            switch (msg.what) {
            case 1:
            {
            	Log.d(TAG, "msg m_setting:"+m_setting);
            	
            	if (m_setting)
            	{
            		m_setting = false;
            		m_networkInfo = connectivityManager.getActiveNetworkInfo();
            		
            		if (null != m_currentBar)
            		{
            			m_currentBar.setVisibility(View.GONE);
            		}
            		
            		 if (m_networkInfo != null && m_networkInfo.isAvailable()) 
            		 {
            			 Bi.sendBiMsg(Bi.BICT_FIRMWARE, Bi.BICC_NETWORK_SET,"0");
            			 
            			 m_isConnect = true;
                     }
            		 else
            		 {
            			 Bi.sendBiMsg(Bi.BICT_FIRMWARE, Bi.BICC_NETWORK_SET,"-1");
            			 m_isConnect = false;
                    	 
                    	 Notice notice = Notice.makeNotice(m_context, m_context.getResources().getString(R.string.settings_network_wifi_connect_fail), 
 								Notice.LENGTH_SHORT);
                    	 notice.setTextColor(m_context.getResources().getColor(R.color.settings_notice_red));
 	        			 notice.cancelAll();
 	        			 notice.showImmediately();
                     }
            		 scanWifi();
            	}
            }
                break;
            /*case 2:
            {            	
            	mIpAddr = msg.getData().getString("ipAddr");
            	mGateWay = msg.getData().getString("gateWay");
            	mDns = msg.getData().getString("dns");
            	mNetworkPrfix = msg.getData().getString("networkPrfix");
				
				Log.d(TAG, "get ipAddr4:"+mIpAddr);
				Log.d(TAG, "get gateWay4:"+mGateWay);
				Log.d(TAG, "get dns4:"+mDns);
				Log.d(TAG, "get networkPrfix4:"+mNetworkPrfix);
            }
            break;*/
            case 3:
            {
            	Log.d(TAG, "m_focus"+m_focus);
            	if (m_focus == FOCUS.WIFI_LISTVIEW)
            	{
            		if (null != m_listView.getChildAt(0))
            		{
            			Log.d(TAG, "11 m_listView not null");
            			mView = m_listView.getChildAt(0);
            			mPos = 0;
            			
                    	setItem(true,mView,mPos);
                    	m_senior.setVisibility(View.VISIBLE);
            		}
            	}
            	else if (m_focus == FOCUS.WIFI_PWD)
            	{
            		if (null != m_listView.getChildAt(0))
            		{
            			Log.d(TAG, "11 m_listView not null WIFI_PWD");
            			mView = m_listView.getChildAt(0);
            			mPos = 0;
            			
                    	setItem(true,mView,mPos);
                    	m_senior.setVisibility(View.VISIBLE);
            		}
            	}
            	else if (m_focus == FOCUS.WIFI_SENIOR || m_focus == FOCUS.WIFI_SENIORLAYOUT)
            	{
            		m_senior.requestFocus();
            		
            		if (null != m_listView.getChildAt(0))
            		{
            			mView = m_listView.getChildAt(0);
            			mPos = 0;
            		}
            	}
            	else
            	{
            		if (null != m_listView.getChildAt(0))
            		{
            			mView = m_listView.getChildAt(0);
            			mPos = 0;
            		}
            	}
            }
            break;
            case 4:
            {
            	onKeyBack();
            	
            	Message getMsg = new Message();
    			Bundle bundle = new Bundle(); 
    			
    			getMsg.what = 1;  
    			
    		    bundle.putString("password",msg.getData().getString("password"));  //往Bundle中存放数据
    			
    		    getMsg.setData(bundle);//mes利用Bundle传递数据   
            	
            	((Handler)msg.obj).sendMessage(getMsg);
            }
            break;
            case 5:
            {
            	onKeyBack();
            	
            	Message getMsg = new Message();
    			Bundle bundle = new Bundle(); 
            	String password = msg.getData().getString("password");
            	
            	String ipAddr = msg.getData().getString("ipAddr");
				String gateWay = msg.getData().getString("gateWay");
				String dns = msg.getData().getString("dns");
				String networkPrfix = msg.getData().getString("networkPrfix");
				
				bundle.putString("password",password);  //往Bundle中存放数据   
		        bundle.putString("ipAddr",ipAddr);
		        bundle.putString("gateWay",gateWay);
		        bundle.putString("dns",dns);
		        bundle.putString("networkPrfix",networkPrfix);
            	
    			getMsg.what = 2;  
    		    getMsg.setData(bundle);//mes利用Bundle传递数据   
            	
            	((Handler)msg.obj).sendMessage(getMsg);
            }
            break;
            default:
                break;
            }
        }
    };
	
    /**
     * 接收广播
     */
	private final BroadcastReceiver mWifiReceiver = new BroadcastReceiver() {

	    @Override
	    public void onReceive(Context context, Intent intent) 
	    {
	    	
	    	if (intent.getAction().equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION))	// 扫描结果
	    	{
	    		if (m_scan)
	    		{
		    		HandlerWifiResult();
		    		m_scan = false;
	    		}
	    	}
	    	else if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION))	// 网络状态
	    	{            	
            	if (m_isStartScan)
                {
            		if (!m_setting)
                	{
                		if (!CommonUtils.getSetting().getEthMagHWConnected())
                    	{
                			scanWifi();
                    	}
                	}
                }
            	
            	if (m_setting)
            	{
            		if (!CommonUtils.getSetting().getEthMagHWConnected())
            		{
            			m_networkInfo = connectivityManager.getActiveNetworkInfo();  
                        
                        if(m_networkInfo != null && m_networkInfo.isAvailable()) 
                        {
                        	Bi.sendBiMsg(Bi.BICT_FIRMWARE, Bi.BICC_NETWORK_SET,"0");
                        	m_isConnect = true;
                        	
                        	if(m_timer != null)
                    		{
                    			m_timer.shutdownNow();
                    			m_timer = null;
                    		}
                        	
                        	if (m_currentBar != null)
                        	{
                        		m_currentBar.setVisibility(View.GONE);
                        	}
                        	
                        	m_setting = false;
                        	
                        	scanWifi();
                        }
            		}
            	}
	    	}
	    	else if (intent.getAction().equals(WifiManager.CONFIGURED_NETWORKS_CHANGED_ACTION))	// wifi配置文件修改
	    	{
	    		if (m_isConfigChanged)
	    		{
	    			m_isConfigChanged = false;
	    			scanWifi();
	    		}
	    	}
		}
	};
	
	public WifiActivity(Context context) {
		super(context);
		inflate(context, R.layout.settings_network_view_network_wifi, this);
		// TODO Auto-generated constructor stub
		m_context = context;
		m_focus = FOCUS.NOT_FOCUS;
		findView();
		m_isFocusListView = false;
		m_index = 0;
		m_total = -1;
		//wifiList(20);
		m_scan = false;
		m_setting = false;
		m_isStartScan = false;
		m_isConfigChanged = false;
		m_no_wifi_signal = true;
		m_listAda = new ListViewAdapter();
		
		mWifiManager = (WifiManager) m_context.getSystemService(Context.WIFI_SERVICE);
		//mEthManager = (EthernetManager) m_context.getSystemService(Context.ETH_SERVICE);
		connectivityManager = (ConnectivityManager)m_context.getSystemService(Context.CONNECTIVITY_SERVICE);
				
		mIntentFilter = new IntentFilter();
		mIntentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
		mIntentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
		mIntentFilter.addAction(WifiManager.NETWORK_IDS_CHANGED_ACTION);
		mIntentFilter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
		mIntentFilter.addAction(WifiManager.CONFIGURED_NETWORKS_CHANGED_ACTION);
		mIntentFilter.addAction(WifiManager.LINK_CONFIGURATION_CHANGED_ACTION);
		mIntentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
		mIntentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
	}
	
	
	/**
	 * 初始化控件
	 * @Title: findView
	 * @Description: TODO
	 * @return: void
	 */
	public void findView()
	{
		m_refresh = (Button)findViewById(R.id.id_setting_network_wifi_refresh);
		
		m_scan_progressBar = (ProgressBar)findViewById(R.id.id_setting_network_scan_progressbar);
		m_scan_progressBar.setVisibility(View.GONE);
		
		m_ok_refresh = (TextView)findViewById(R.id.id_setting_network_wifi_ok_refresh);
		m_ok_refresh.setVisibility(View.VISIBLE);
		
		m_no_signal = (TextView)findViewById(R.id.id_setting_network_wifi_no_signal);
		m_no_signal.setVisibility(View.GONE);
		
		m_wifi_signal = (TextView)findViewById(R.id.id_setting_network_wifi_wifi_text);
		
		m_listView = (GridView)this.findViewById(R.id.id_setting_netwok_listview);
		m_senior = (ImageView)this.findViewById(R.id.id_setting_network_senior);
		m_senior.setVisibility(View.GONE);
		
		mWifiListView = (RelativeLayout)this.findViewById(R.id.id_wifi_listview_view);
		
		mWifipwd = new WiFiPasswordAcitivity(this.getContext());
		this.addView(mWifipwd);
		mWifipwd.setVisibility(View.GONE);
		
		mWifiSeniorLayout = new WiFiSeniorLayout(this.getContext());
		this.addView(mWifiSeniorLayout);
		mWifiSeniorLayout.setVisibility(View.GONE);
	}
	
	/**
	 * 焦点是否在listview上面
	 * @Title: getIsFocusListView
	 * @Description: TODO
	 * @return
	 * @return: boolean
	 */
	public boolean getIsFocusListView()
	{
		return m_isFocusListView;
	}
	
	/**
	 * 扫描wifi信号
	 * @Title: scanWifi
	 * @Description: TODO
	 * @return: void
	 */
	public void scanWifi()
	{
		int wifiState = mWifiManager.getWifiState();
		Log.d(TAG, "wifiState:"+wifiState);
		
		if (WifiManager.WIFI_STATE_ENABLED != wifiState)
		{	
			mWifiManager.setWifiEnabled(true);
			wifiState = mWifiManager.getWifiState();
			Log.d(TAG, "wifiState:"+wifiState);
		}
		
		boolean startScan = mWifiManager.startScanActive();
		Log.d(TAG, "startScan:"+startScan);
		m_scan_progressBar.setVisibility(View.VISIBLE);
		m_scan = true;
	}
	
	/**
	 * 处理扫描结果
	 * @Title: HandlerWifiResult
	 * @Description: TODO
	 * @return: void
	 */
	public void HandlerWifiResult()
	{
		Log.d(TAG,"scan result");
		
		m_scanResult = mWifiManager.getScanResults();
		
		int scanResultNum = m_scanResult.size();
		Log.d(TAG,"scan result size1:"+scanResultNum);
				
		for (int i = 0; i < m_scanResult.size(); i++)
		{
			 if (m_scanResult.get(i).SSID == null || m_scanResult.get(i).SSID.length() == 0 ||
					 m_scanResult.get(i).capabilities.contains("[IBSS]")) 
			 {
				 Log.d(TAG,"error+++++++ SSID:" + m_scanResult.get(i).SSID);
				 m_scanResult.remove(i);
             }
		}
		
		scanResultNum = m_scanResult.size();
		Log.d(TAG,"scan result size2:"+scanResultNum);
		
		if (0 == scanResultNum)
		{
			m_no_wifi_signal = true;
			m_no_signal.setVisibility(View.VISIBLE);
			m_refresh.requestFocus();
			m_focus = FOCUS.WIFI_RELRESH;
			m_isFocusListView = false;
			m_senior.setVisibility(View.GONE);
		}
		else
		{
			m_no_wifi_signal = false;
			m_no_signal.setVisibility(View.GONE);
		}
		
		m_wifiNames = new String[scanResultNum];
		m_isLock = new boolean[scanResultNum];
		m_level = new int[scanResultNum];
		m_encryption = new int[scanResultNum]; 
		m_isPassword = new boolean[scanResultNum];
		m_config = new WifiConfiguration[scanResultNum];
		m_networkId = new int[scanResultNum];
		m_mac = new String[scanResultNum];
		
		for (int i = 0; i <scanResultNum; i++)
		{
			
			m_isPassword[i] = false;
			m_networkId[i] = -1;
		}
		
		System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");
		try
		{
			Collections.sort(m_scanResult,new Comparator<ScanResult>(){  
	            @Override  
	            public int compare(ScanResult b1, ScanResult b2) {  
	            	int level1 = WifiManager.calculateSignalLevel(b1.level,4);
	            	int level2 = WifiManager.calculateSignalLevel(b2.level,4);
	            	 return (level1 < level2 ? 1 : -1);
	            }
	        });  
		}
		catch(IllegalArgumentException e)
		{
			Log.d(TAG, "catch sort error");
		}
		
		m_isConnect = false;
		
		//获取是否连接,是的话则显示在第一个
		m_networkInfo = connectivityManager.getActiveNetworkInfo();
		
        if(m_networkInfo != null && m_networkInfo.isAvailable()) 
        {
            m_wifiInfo = mWifiManager.getConnectionInfo();
            
			if (m_wifiInfo != null)
			{
				for (int i = 0; i < m_scanResult.size(); i++)
				{
					String wifiInfoSSID = m_wifiInfo.getSSID().replace("\"", "");
					
					if (m_scanResult.get(i).SSID.equals(wifiInfoSSID))
					{
						//m_mac = m_wifiInfo.getMacAddress();
						m_isConnect = true;
						Log.d(TAG, "connect SSID:"+wifiInfoSSID);
						
						ScanResult scan = m_scanResult.get(i);
						m_scanResult.remove(i);
						m_scanResult.add(0, scan); 
						
						break;
					}
				}
			}
			else
			{
				Log.d(TAG, "wifiInfo null");
			}
        }
        else
        {
        	Log.d(TAG, "m_networkInfo null");
        }
    
		
        // 保存密码的ITEM
		mWifiConfiguration = mWifiManager.getConfiguredNetworks();
		
		if (null != mWifiConfiguration)
		{
			Log.d(TAG, "mWifiConfiguration size:"+mWifiConfiguration.size());
			
			int j = 0;
			
			if (m_isConnect)
			{
				j = 1;
			}
			//mWifiConfiguration.get(0).ipAssignment;
			for (int i = 0; i < mWifiConfiguration.size(); i++)
			{
				Log.d(TAG, "Configurateion"+i+":"+mWifiConfiguration.get(i).SSID);
				
				if (mWifiConfiguration.get(i).SSID == null)
				{
					continue;
				}
				String configurationSSID = mWifiConfiguration.get(i).SSID.replace("\"", ""); 
				
				int ret = isExist(configurationSSID);
				Log.d(TAG, "ret:"+ret);
				if (-1 != ret)
				{
					if (m_isConnect && 0 == ret)
					{
						this.m_isPassword[ret] = true;
						m_config[ret] = mWifiConfiguration.get(i);
						this.m_networkId[ret] = mWifiConfiguration.get(i).networkId;
						
						continue;
					}
					
					Log.d(TAG, "savePassword SSID:" + m_scanResult.get(ret).SSID);
					
					ScanResult scan = m_scanResult.get(ret);
					m_scanResult.remove(ret);
					m_scanResult.add(j, scan); 
					this.m_isPassword[j] = true;
					m_config[j] = mWifiConfiguration.get(i);
					this.m_networkId[j] = mWifiConfiguration.get(i).networkId;
					j++;
				}
			}
		}
		else
		{
			Log.d(TAG, "mWifiConfiguration is null");
		}
		
		// 按顺序显示
		for (int i = 0; i < m_scanResult.size(); i++)
		{
			String SSID = m_scanResult.get(i).SSID;
			String BSSID = m_scanResult.get(i).BSSID;
			String capabilities = m_scanResult.get(i).capabilities;
			
			Log.d(TAG,"after SSID"+i+":" + SSID);
			Log.d(TAG,"capabilities:" + capabilities); 
			Log.d(TAG,"BSSID:" + BSSID);
			
			m_wifiNames[i] = SSID;
			m_mac[i] = BSSID;
			
			if (SECURITY_NONE == getSecurity(m_scanResult.get(i)))
			{
				m_isLock[i] = false;
				m_encryption[i] = SECURITY_NONE;
			}
			else if (SECURITY_WEP == getSecurity(m_scanResult.get(i)))
			{
				m_isLock[i] = true;
				m_encryption[i] = SECURITY_WEP;
			}
			else if (SECURITY_PSK == getSecurity(m_scanResult.get(i)))
			{
				m_isLock[i] = true;
				m_encryption[i] = SECURITY_PSK;
			}
			else if (SECURITY_EAP == getSecurity(m_scanResult.get(i)))
			{
				m_isLock[i] = true;
				m_encryption[i] = SECURITY_EAP;
			}
			
			int level = WifiManager.calculateSignalLevel(m_scanResult.get(i).level, 4);
			Log.d(TAG,"level:" + level);
			m_level[i] = level;
	
			Log.d(TAG,"------------------------------------");
		}
		
		wifiList();
		
	}
	
	/**
	 * 是否存在此ssid
	 * @Title: isExist
	 * @Description: TODO
	 * @param ssid
	 * @return
	 * @return: int
	 */
	private int isExist(String ssid)
	{
		for (int i = 0; i < m_scanResult.size(); i++)
		{
			if (m_scanResult.get(i).SSID.equals(ssid))
			{
				return i;
			}
		}
		
		return -1;
	}
	
	/**
	 * 获取wifi信号安全性类型
	 * @Title: getSecurity
	 * @Description: TODO
	 * @param result
	 * @return
	 * @return: int
	 */
	private static int getSecurity(ScanResult result) 
	{
        if (result.capabilities.contains("WEP")) 
        {
            return SECURITY_WEP;
        } else if (result.capabilities.contains("PSK")) 
        {
            return SECURITY_PSK;
        } else if (result.capabilities.contains("EAP")) 
        {
            return SECURITY_EAP;
        }
        
        return SECURITY_NONE;
    }
	
	/**
	 * 显示wifi信号
	 * @Title: wifiList
	 * @Description: TODO
	 * @return: void
	 */
	public void wifiList()
	{
		m_scan_progressBar.setVisibility(View.GONE);
		m_ok_refresh.setVisibility(View.GONE);
		
		m_listView.setVisibility(View.VISIBLE);
		
		m_listAda.setListView(m_wifiNames, m_isLock,m_level,m_isConnect, -1,this.getContext());
        m_listView.setAdapter(m_listAda);   
        m_listView.setSelection(0);
        
        this.m_total = m_listView.getCount();
        Log.d(TAG, "m_total :"+m_total);
        
       
        
        m_listView.setOnKeyListener(new OnKeyListener() {
        	
        	@Override
        	public boolean onKey(View v, int keyCode, KeyEvent event) 
        	{        		
        		if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER && event.getAction() == KeyEvent.ACTION_DOWN)
				{
				}
				else if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN)
				{
					if(m_timer != null)
					{
						m_timer.shutdownNow();
						m_timer = null;
					}
				}
				else if (keyCode == KeyEvent.KEYCODE_DPAD_UP && event.getAction() == KeyEvent.ACTION_DOWN)
				{	
					if(m_listView.getSelectedItemPosition() > 0){
						setItem(false,mView,mPos);
					}
					if (0 == m_listView.getSelectedItemPosition())
					{
						m_refresh.requestFocus();
						m_senior.setVisibility(View.GONE);
						m_focus = FOCUS.WIFI_RELRESH;
						m_isFocusListView = false;
						setItem(false,mView,mPos);
					}
				}
				else if(keyCode == KeyEvent.KEYCODE_DPAD_DOWN && event.getAction() == KeyEvent.ACTION_DOWN){
					if(m_listView.getSelectedItemPosition() + 1 < m_total){
						setItem(false,mView,mPos);
					}
				}
				return false;
        	}
        });
        
        
        m_listView.setOnItemClickListener(new OnItemClickListener()
        {
	    	@Override
	    	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) 
	    	{
	    		if (CommonUtils.getSetting().getEthMagHWConnected())
				{
					Notice notice = Notice.makeNotice(m_context, m_context.getResources().getString(R.string.settings_network_connect_wired), 
							Notice.LENGTH_SHORT);
        			notice.cancelAll();
        			notice.showImmediately();
					return;
				}
	    		
	    		Log.d(TAG, "test_index:"+String.valueOf(arg2));
	    		
	    		m_currentView = arg1;
	    		mView = arg1;
	    		m_setting = true;
	    		
	    		if (SECURITY_NONE == m_encryption[arg2])
	    		{
	    			if (m_isConnect && 0 == arg2)
	    			{
	    				WiFiDetailShow(arg2, true);
	    			}
	    			else
	    			{
	    				WiFiSetting(arg2,true,false);
	    			}
	    		}
	    		else
	    		{
	    			if (m_isPassword[arg2])
		    		{
		    			if (m_isConnect && 0 == arg2)
		    			{
		    				WiFiDetailShow(arg2, true);
		    			}
		    			else
		    			{
		    				WiFiDetailShow(arg2, false);
		    			}
		    		}
		    		else
		    		{
		    			WiFiSetting(arg2,false,true);
		    		}
	    		}
	    		
	    	}
	    	});
        
        m_listView.setOnItemSelectedListener(new OnItemSelectedListener(){
        	 @Override
             public void onItemSelected(AdapterView<?> arg0, View arg1,int arg2, long arg3) {
        		 
        		 m_index = arg2;

                 if (null == mView)
                 {
                	 mView = arg1;
                	 mPos = arg2;
                	 setItem(true,arg1,arg2);
                 }
                 else
                 {
                	 setItem(false,mView,mPos);
                	 setItem(true,arg1,arg2);
                	 
                	 mView = arg1;
                	 mPos = arg2;
                 }
        	 }
        	 
        	 @Override
 			public void onNothingSelected(AdapterView<?> parent) {
 				// TODO Auto-generated method stub
 				
 			}

        });
        
        Message msg = new Message();
        msg.what = 3;
        m_handler.sendMessage(msg);
        //m_handler.sendMessageDelayed(msg, 2000);
	}
	
	public void setItem(boolean focus,View view,int pos)
	{
		if (null != view)
		{
			TextView tvName = (TextView)view.findViewById(R.id.id_setting_network_wifi_name); 
	   		ImageView imgBg = (ImageView)view.findViewById(R.id.id_setting_network_wifi_listview_bg);
	   		ImageView imgLock = (ImageView)view.findViewById(R.id.id_setting_wifi_lockImage);
	   		ImageView imgSignal = (ImageView)view.findViewById(R.id.id_setting_network_wifi_signal);
	   		//imgBg.getTop()
	   		//+150
	   		int top = view.getTop() + 266;
	   		//+590
	   		RelativeLayout.LayoutParams params_senior = new RelativeLayout.LayoutParams(72,72);
			params_senior.leftMargin = 1062;
			params_senior.topMargin = top;
			this.m_senior.setLayoutParams(params_senior);
	   		
			if (focus)
			{				
		   		imgBg.setImageResource(R.drawable.settings_network_wifi_refresh_focus);
		   		imgLock.setImageResource(R.drawable.settings_network_wifi_lock_f);
		   		tvName.setTextColor(getResources().getColor(R.color.settings_timezone_f_color));
		   		if (0 == m_level[pos])
		        {
		   			imgSignal.setImageResource(R.drawable.settings_network_wifi_wifi0_f);
		        }
		        else if (1 == m_level[pos])
		        {
		        	imgSignal.setImageResource(R.drawable.settings_network_wifi_wifi1_f);
		        }
		        else if (2 == m_level[pos])
		        {
		        	imgSignal.setImageResource(R.drawable.settings_network_wifi_wifi2_f);
		        }
		        else if (3 == m_level[pos])
		        {
		        	imgSignal.setImageResource(R.drawable.settings_network_wifi_wifi3_f);
		        }
			}
			else
			{				
		   		imgBg.setImageResource(R.drawable.settings_network_wifi_refresh_unfocus);
		   		imgLock.setImageResource(R.drawable.settings_network_wifi_lock_f);
		   		tvName.setTextColor(getResources().getColor(R.color.settings_timezone_uf_color));
		   		if (0 == m_level[pos])
		        {
		   			imgSignal.setImageResource(R.drawable.settings_network_wifi_wifi0_uf);
		        }
		        else if (1 == m_level[pos])
		        {
		        	imgSignal.setImageResource(R.drawable.settings_network_wifi_wifi1_uf);
		        }
		        else if (2 == m_level[pos])
		        {
		        	imgSignal.setImageResource(R.drawable.settings_network_wifi_wifi2_uf);
		        }
		        else if (3 == m_level[pos])
		        {
		        	imgSignal.setImageResource(R.drawable.settings_network_wifi_wifi3_uf);
		        }
			}
		}
	}

	
	/**
	 * 设置wifi
	 * @Title: WiFiSetting
	 * @Description: TODO
	 * @param index
	 * @param isNone
	 * @return: void
	 */
	public void WiFiSetting(int index, boolean isNone, boolean isSenior)
	{
		m_index = index;
		mIsSenior = isSenior;
		Handler handler = new Handler(){

            @Override
            public void handleMessage(Message msg) {
                // TODO Auto-generated method stub
                super.handleMessage(msg);
                
                switch (msg.what) 
                {
                case 1:									// dhcp
                	//msg.setData(data)
                {
                	
                	m_setting = false;
                	
                	// 正在连接
                	if (m_setting)
    	    		{
                		if (null != m_currentBar)
                		{
                			m_currentBar.setVisibility(View.GONE);
                		}
    	    		}
                	
            		m_currentBar = (ProgressBar)mView.findViewById(R.id.id_setting_network_linking_progressbar);
                	m_currentImage = (ImageView)mView.findViewById(R.id.id_setting_network_wifi_connect);
                	
                	m_currentBar.setVisibility(View.VISIBLE);
                	m_currentImage.setVisibility(View.GONE);
                	
                	m_ipAssignment = IpAssignment.DHCP;
                	m_linkProperties.clear();
                	
                	String password = msg.getData().getString("password");//
                	Log.d(TAG, "get Password:"+password);
                	
                	m_listAda.setListView(m_wifiNames, m_isLock,m_level,false,m_index, m_context);
                	m_listAda.notifyDataSetChanged();
                	
                	Bi.sendBiMsg(Bi.BICT_FIRMWARE, Bi.BICC_NETWORK,"2",m_wifiNames[m_index],"DHCP","","","");
                	
                	WifiConfiguration config = getConfig(m_index, m_encryption[m_index], password);
                	configWifi(config);
                }
                    break;
                case 2:									// static
                {
                	m_setting = false;
                	if (m_setting)
    	    		{
                		if (null != m_currentBar)
                		{
                			m_currentBar.setVisibility(View.GONE);
                		}
    	    		}
                	mWifiListView.setVisibility(View.VISIBLE);
                	m_focus = FOCUS.WIFI_LISTVIEW;
                	m_isFocusListView = true;
                	m_listView.requestFocus();
                	setItem(true,mView,mPos);
                	
                	m_currentBar = (ProgressBar)mView.findViewById(R.id.id_setting_network_linking_progressbar);
                	m_currentImage = (ImageView)mView.findViewById(R.id.id_setting_network_wifi_connect);
                	m_currentBar.setVisibility(View.VISIBLE);
                	m_currentImage.setVisibility(View.GONE);
                	
                	m_ipAssignment = IpAssignment.STATIC;
                	m_linkProperties.clear();
                	
                	String password = msg.getData().getString("password");
                	
                	String ipAddr = msg.getData().getString("ipAddr");
    				String gateWay = msg.getData().getString("gateWay");
    				String dns = msg.getData().getString("dns");
    				String networkPrfix = msg.getData().getString("networkPrfix");
    				
    				Log.d(TAG, "get Password:"+password);
    				Log.d(TAG, "get ipAddr:"+ipAddr);
    				Log.d(TAG, "get gateWay:"+gateWay);
    				Log.d(TAG, "get dns:"+dns);
    				Log.d(TAG, "get networkPrfix:"+networkPrfix);
    				
    				validateIpConfigFields(ipAddr,gateWay,dns,networkPrfix);
		
    				m_listAda.setListView(m_wifiNames, m_isLock,m_level,false,m_index, m_context);
                    //m_listView.setAdapter(m_listAda);   
                	m_listAda.notifyDataSetChanged();
                	
                	Bi.sendBiMsg(Bi.BICT_FIRMWARE, Bi.BICC_NETWORK,"2",m_wifiNames[m_index],"Manual",ipAddr,gateWay,dns);
                	
                	WifiConfiguration config = getConfig(m_index, m_encryption[m_index], password);
                	configWifi(config);
                }
                break;
                case 3:
                {
                	m_setting = false;
                	if (m_setting)
    	    		{
                		if (null != m_currentBar)
                		{
                			m_currentBar.setVisibility(View.GONE);
                		}
    	    		}
            
                	m_currentBar = (ProgressBar)m_currentView.findViewById(R.id.id_setting_network_linking_progressbar);
                	m_currentImage = (ImageView)m_currentView.findViewById(R.id.id_setting_network_wifi_connect);
                	m_currentBar.setVisibility(View.VISIBLE);
                	m_currentImage.setVisibility(View.GONE);
                	
                	m_ipAssignment = IpAssignment.DHCP;
                	m_linkProperties.clear();
                	
                	//ListViewAdapter listAda = new ListViewAdapter();
                	m_listAda.setListView(m_wifiNames, m_isLock,m_level,false,m_index, m_context);
                    //m_listView.setAdapter(m_listAda);
                    m_listAda.notifyDataSetChanged();
                    
                    Bi.sendBiMsg(Bi.BICT_FIRMWARE, Bi.BICC_NETWORK,"2",m_wifiNames[m_index],"DHCP","","","");
                	
                	WifiConfiguration config = getConfig(m_index, m_encryption[m_index], "");
                	configWifi(config);
                }
                break;
                case 4:
                	m_setting = false;
                	break;
                default:
                    break;
                }
            }
        };
        
        
    	if (isNone)
        {
        	Message msg = new Message();
        	msg.what = 3;
        	handler.sendMessage(msg);
        }
        else
        {
        	if (m_encryption[index] == SECURITY_NONE)
        	{
        		Log.d(TAG, "SECURITY_NONE");
        		onKeyBack();
        		if (isSenior)
                {
        			Message getMsg = new Message();
        			Bundle bundle = new Bundle(); 
        			
        			getMsg.what = 1;  
        		    getMsg.setData(bundle);//mes利用Bundle传递数据   
                	
        		    handler.sendMessage(getMsg);
                }
        		else
        		{
                	Message getMsg = new Message();
        			Bundle bundle = new Bundle(); 
    				
    		        bundle.putString("ipAddr",mIpAddr);
    		        bundle.putString("gateWay",mGateWay);
    		        bundle.putString("dns",mDns);
    		        bundle.putString("networkPrfix",mNetworkPrfix);
                	
        			getMsg.what = 2;  
        		    getMsg.setData(bundle);//mes利用Bundle传递数据   
                	
        		    handler.sendMessage(getMsg);
        		}
        	}
        	else
        	{
        		if (isSenior)
                {
            		Log.d(TAG, "ShowDhcp");
            		mWifipwd.ShowDhcp(m_wifiNames[index],m_level[index],handler,m_handler);
                }
            	else
            	{
            		Log.d(TAG, "ShowManual");
            		mWifipwd.ShowManual(m_wifiNames[index],m_level[index],handler,m_handler,
            				mIpAddr,mGateWay,mDns,mNetworkPrfix);	
            	}
            	mWifiListView.setVisibility(View.GONE);
            	m_focus = FOCUS.WIFI_PWD;
            	m_isFocusListView = false;
        	}
        }
	}
	
	/**
	 * 显示wifi信号详细信息
	 * @Title: WiFiDetailShow
	 * @Description: TODO
	 * @param index
	 * @param isConnect
	 * @return: void
	 */
	public void WiFiDetailShow(int index, boolean isConnect)
	{
		m_index = index;
		Handler handler = new Handler(){

            @Override
            public void handleMessage(Message msg) {
                // TODO Auto-generated method stub
                super.handleMessage(msg);
                
                switch (msg.what) {
                case 1:
                	m_setting = false;
                	if (m_setting)
    	    		{
    	    			m_currentBar.setVisibility(View.GONE);
    	    		}
                	
                	DelNetwork(m_index);
                	m_isConfigChanged = true;
                    break;
                case 2:
                	m_setting = false;
                	if (m_setting)
    	    		{
    	    			m_currentBar.setVisibility(View.GONE);
    	    		}
                	m_currentBar = (ProgressBar)mView.findViewById(R.id.id_setting_network_linking_progressbar);
                	m_currentImage = (ImageView)mView.findViewById(R.id.id_setting_network_wifi_connect);
                	
                	/*m_currentBar = (ProgressBar)m_currentView.findViewById(R.id.id_setting_network_linking_progressbar);
                	m_currentImage = (ImageView)m_currentView.findViewById(R.id.id_setting_network_wifi_connect);*/
                	m_currentBar.setVisibility(View.VISIBLE);
                	m_currentImage.setVisibility(View.GONE);
                	
                	m_ipAssignment = IpAssignment.DHCP;
                	m_linkProperties.clear();
                	
                	
                	m_listAda.setListView(m_wifiNames, m_isLock,m_level,false,m_index, m_context);
                    //m_listView.setAdapter(m_listAda);
                    m_listAda.notifyDataSetChanged();
                    
                	
                	configWifi(m_config[m_index]);
                	
                	break;
                case 3:
                	m_setting = false;
                	break;
                default:
                    break;
                }
            }
        };
		
		WifiDetailDlg wifiDetailDlg = new WifiDetailDlg(m_context,handler,isConnect);
		wifiDetailDlg.setWifiName(this.m_wifiNames[index]);
		wifiDetailDlg.setSignal(this.m_level[index]);
		wifiDetailDlg.setSecurity(this.m_encryption[index]);
		wifiDetailDlg.setMacAddr(this.m_mac[index]);
		
		Log.d(TAG, "isConnect:"+isConnect);

		wifiDetailDlg.show();
	}
	
	/**
	 * 删除此wifi信号
	 * @Title: DelNetwork
	 * @Description: TODO
	 * @param index
	 * @return: void
	 */
	public void DelNetwork(int index)
	{
		Log.d(TAG, "index:"+index+"   networkId"+m_networkId[index]);
		mWifiManager.removeNetwork(this.m_networkId[index]);
		mWifiManager.saveConfiguration();
	}
	
	/**
	 * wifi静态信息
	 * @Title: validateIpConfigFields
	 * @Description: TODO
	 * @param ip
	 * @param gateway
	 * @param route
	 * @param network_prefix
	 * @return
	 * @return: int
	 */
	private int validateIpConfigFields(String ip, String gateway, String route, String network_prefix) 
	{
		m_linkProperties.clear();
		
		String ipAddr = ip;
		InetAddress inetAddr = null;
		try {
		    inetAddr = NetworkUtils.numericToInetAddress(ipAddr);
		} catch (IllegalArgumentException e) {
		    return -1;
		}
		
		int networkPrefixLength = -1;
		try 
		{
		    networkPrefixLength = Integer.parseInt(network_prefix);
		} 
		catch (NumberFormatException e) 
		{
			return -1;
		}
		
		if (networkPrefixLength < 0 || networkPrefixLength > 32) 
		{
		    return -1;
		}
		
		m_linkProperties.addLinkAddress(new LinkAddress(inetAddr, networkPrefixLength));
		
		String gateWay = gateway;
		InetAddress gatewayAddr = null;
		try {
		    gatewayAddr = NetworkUtils.numericToInetAddress(gateWay);
		} catch (IllegalArgumentException e) {
		    return -1;
		}
		m_linkProperties.addRoute(new RouteInfo(gatewayAddr));
		
		
		String dns = route;
		InetAddress dnsAddr = null;
		try {
		    dnsAddr = NetworkUtils.numericToInetAddress(dns);
		} catch (IllegalArgumentException e) {
		    return -1;
		}
		m_linkProperties.addDns(dnsAddr);
		
		return 0;
	}
	
	/**
	 * 配置wifi
	 * @Title: configWifi
	 * @Description: TODO
	 * @param config
	 * @return: void
	 */
	public void configWifi(WifiConfiguration config)
	{
		m_setting = true;
		m_addConfig = config;
		
		Runnable wifiConfig = new Runnable()
		{
        	@Override  
        	public void run() 
        	{ 
        		boolean ret = addNewWifi(m_addConfig);
        		
        		Log.d(TAG, "addNet return:"+ret);
        		
        		if (ret)
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
        					   m_handler.sendEmptyMessage(1);
        				    } catch (Exception e) {
        				// TODO: handle exception
        				      e.printStackTrace();
        				    }
        				    }
        			};
        	    	
        			m_timer.scheduleAtFixedRate(new Task(), 30*1000, 15*1000,TimeUnit.MILLISECONDS);
        			/*
        			m_timer.cancel();
            		m_timer = new Timer();
                    m_timerTask = new TimerTask() {
                    	
                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                        	m_timer.cancel();
                        	
                            m_handler.sendEmptyMessage(1);
                        } 
                    };
                    m_timer.schedule(m_timerTask, 30*1000, 15*1000);
                    */
        		}
        		else
        		{
        			m_handler.sendEmptyMessage(1);
        		}
        	}
		};
		
		new Thread(wifiConfig).start();
	}
	
	
	public String convertToQuotedString(String string) {
        return "\"" + string + "\"";
    }
	
	public WifiConfiguration getConfig(int index, int type, String editPassword) {
		
		WifiConfiguration config = new WifiConfiguration();
		
		config.SSID = convertToQuotedString(this.m_wifiNames[index]);
		
		switch (type) {
		    case SECURITY_NONE:
		        config.allowedKeyManagement.set(KeyMgmt.NONE);
		        break;
		
		    case SECURITY_WEP:
		        config.allowedKeyManagement.set(KeyMgmt.NONE);
		        config.allowedAuthAlgorithms.set(AuthAlgorithm.OPEN);
		        config.allowedAuthAlgorithms.set(AuthAlgorithm.SHARED);
		        if (editPassword.length() != 0) {
		            int length = editPassword.length();
		            String password = editPassword;
		            // WEP-40, WEP-104, and 256-bit WEP (WEP-232?)
		            if ((length == 10 || length == 26 || length == 58) &&
		                    password.matches("[0-9A-Fa-f]*")) 
		            {
		                config.wepKeys[0] = password;
		            } 
		            else 
		            {
		                config.wepKeys[0] = '"' + password + '"';
		            }
		        }
		        break;
		
		    case SECURITY_PSK:
		        config.allowedKeyManagement.set(KeyMgmt.WPA_PSK);
		        if (editPassword.length() != 0) {
		            String password = editPassword;
		            if (password.matches("[0-9A-Fa-f]{64}")) 
		            {
		                config.preSharedKey = password;
		            } 
		            else 
		            {
		                config.preSharedKey = '"' + password + '"';
		            }
		        }
		        break;
		    default:
		            return null;
		}
		
		
		config.proxySettings = ProxySettings.NONE;
		config.ipAssignment = m_ipAssignment;
		config.linkProperties = new LinkProperties(m_linkProperties);
		//config.linkProperties = m_linkProperties;
		return config;
	}
	
	/**
	 * 连接wifi信号
	 * @Title: addNewWifi
	 * @Description: TODO
	 * @param newConfig
	 * @return
	 * @return: boolean
	 */
	public boolean addNewWifi(WifiConfiguration newConfig)
	{
		if (newConfig == null)
		{
			return false;
		}
		mWifiManager.connect(newConfig, null);
        mWifiManager.saveConfiguration();
        
        Runnable setWifiRunnable = new Runnable()
		{
        	@Override  
        	public void run() 
        	{ 
        		 mWifiManager.setWifiEnabled(false);
        		 try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        		mWifiManager.setWifiEnabled(true);
        	}
		};
       
		new Thread(setWifiRunnable).start();
        
        return true;
	}
	
	public void startScanWifi()
	{
		this.scanWifi();
		m_setting = false;
		m_isStartScan = true;
	}
	
	public boolean doKeyOk()
	{
		if (m_focus == FOCUS.WIFI_RELRESH)
		{
			ConnectivityManager m_conManager = (ConnectivityManager)m_context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo m_ethInfo = m_conManager.getNetworkInfo(ConnectivityManager.TYPE_ETHERNET);
			boolean isAva = m_ethInfo.isAvailable();
			boolean isCon = m_ethInfo.isConnected();
			Log.d(TAG, "eth0 network isAvailable:" + isAva);
			Log.d(TAG, "eth0 network isConnected:" + isCon);
			
			if (CommonUtils.getSetting().getEthMagHWConnected())
			{
				Notice notice = Notice.makeNotice(m_context, 
						m_context.getResources().getString(R.string.settings_network_connect_wired), 
						Notice.LENGTH_SHORT);
    			notice.cancelAll();
    			notice.showImmediately();
			}
			else
			{
				this.scanWifi();
				m_setting = false;
				m_isStartScan = true;
			}
		}
		else if (m_focus == FOCUS.WIFI_PWD)
		{
			if (mWifipwd.doKeyOk())
			{
				mWifipwd.setVisibility(View.GONE);
			}
		}
		else if (m_focus == FOCUS.WIFI_SENIOR)
		{
			if (CommonUtils.getSetting().getEthMagHWConnected())
			{
				Notice notice = Notice.makeNotice(mContext, 
						mContext.getResources().getString(R.string.settings_network_connect_wired), 
						Notice.LENGTH_SHORT);
    			notice.cancelAll();
    			notice.showImmediately();
    			return false;
			}
			
			mWifiSeniorLayout.Show(m_wifiNames[m_index],m_level[m_index],m_handler,m_isPassword[m_index],m_config[m_index]);
			mWifiSeniorLayout.setVisibility(View.VISIBLE);
        	mWifiListView.setVisibility(View.GONE);
        	m_focus = FOCUS.WIFI_SENIORLAYOUT;
        	
        	return true;
		}
		else if (m_focus == FOCUS.WIFI_SENIORLAYOUT)
		{
			int ret = mWifiSeniorLayout.doKeyOk();
			
			if (1 == ret)
			{
				mWifiSeniorLayout.setVisibility(View.GONE);
				WiFiSetting(m_index,false,true);
				return false;
			}
			else if (2 == ret)
			{
				mIpAddr = mWifiSeniorLayout.getIpaddr();
            	mGateWay = mWifiSeniorLayout.getGateway();
            	mDns = mWifiSeniorLayout.getDns();
            	mNetworkPrfix = mWifiSeniorLayout.getNetworkPrfix();
				
				mWifiSeniorLayout.setVisibility(View.GONE);
				WiFiSetting(m_index,false,false);
				return false;
			}
			
			return true;
		}
		return false;
	}
	
	public void doKeyRight()
	{
		if (m_focus == FOCUS.WIFI_LISTVIEW)
		{
			Log.d(TAG, "right");
			m_senior.requestFocus();
			setItem(false,mView,mPos);
			m_focus = FOCUS.WIFI_SENIOR;
			m_isFocusListView = false;
		}
		else if (m_focus == FOCUS.WIFI_SENIORLAYOUT)
		{
			mWifiSeniorLayout.doKeyRight();
		}
	}
	
	public void doKeyLeft()
	{
		if (m_focus == FOCUS.WIFI_SENIOR)
		{
			Log.d(TAG, "left");
			m_listView.requestFocus();
			setItem(true,mView,mPos);
			m_focus = FOCUS.WIFI_LISTVIEW;
			m_isFocusListView = true;
		}
		else if (m_focus == FOCUS.WIFI_SENIORLAYOUT)
		{
			mWifiSeniorLayout.doKeyLeft();
		}
	}
	
	public void doKeyDown()
	{
		if (m_focus == FOCUS.NOT_FOCUS)
		{
			m_refresh.requestFocus();
			m_focus = FOCUS.WIFI_RELRESH;
		}
		else if (m_focus == FOCUS.WIFI_RELRESH)
		{
			if (!m_no_wifi_signal)
			{
				if (m_isStartScan)
				{
					m_listView.requestFocus();
					m_listView.setSelected(true);
					m_listView.setSelection(0);
					setItem(true,mView,mPos);
					//m_listView.setSelected(true);
					
					m_focus = FOCUS.WIFI_LISTVIEW;
					m_isFocusListView = true;
					
					if (null == m_listView.getChildAt(0))
					{
						Log.d(TAG, "m_listView null");
					}
					else
					{
						Log.d(TAG, "m_listView not null");
						mView = m_listView.getChildAt(0);
						mPos = 0;
						
	                	setItem(true,mView,mPos);
	                	m_senior.setVisibility(View.VISIBLE);
					}
				}
				
			}
		}
		else if (m_focus == FOCUS.WIFI_PWD)
		{
			mWifipwd.doKeyDown();
		}
		else if (m_focus == FOCUS.WIFI_SENIORLAYOUT)
		{
			mWifiSeniorLayout.doKeyDown();
		}
	}
	
	/**
	 * 上按键处理，返回false表示光标回到顶级菜单(Wi-Fi)，返回true则代表依然在WIFI_ACTIVITY中处理按键
	 * @Title: doKeyUp
	 * @Description: TODO
	 * @return
	 * @return: boolean
	 */
	public boolean doKeyUp()
	{		
		if (m_focus == FOCUS.WIFI_RELRESH)
		{
			m_focus = FOCUS.NOT_FOCUS;
			return true;
		}
		else if (m_focus == FOCUS.WIFI_PWD)
		{
			return mWifipwd.doKeyUp();
		}
		else if (m_focus == FOCUS.WIFI_SENIORLAYOUT)
		{
			mWifiSeniorLayout.doKeyUp();
		}
		
		return false;
	}
	
	
	public void doKeyNum(KeyEvent event)
	{
		if (m_focus == FOCUS.WIFI_PWD)
		{
			mWifipwd.doKeyNum(event);
		}
		else if (m_focus == FOCUS.WIFI_SENIORLAYOUT)
		{
			mWifiSeniorLayout.doKeyNum(event);
		}
	}
	
	public void doKeyDel()
	{
		if (m_focus == FOCUS.WIFI_PWD)
		{
			mWifipwd.doKeyDel();
		}
		else if (m_focus == FOCUS.WIFI_SENIORLAYOUT)
		{
			mWifiSeniorLayout.doKeyDel();
		}
	}
	
	public boolean onKeyBack()
	{
		if (m_focus == FOCUS.WIFI_PWD)
		{
			mWifipwd.setVisibility(View.GONE);
        	mWifiListView.setVisibility(View.VISIBLE);
        	m_focus = FOCUS.WIFI_LISTVIEW;
        	m_isFocusListView = true;
        	m_listView.requestFocus();
        	
        	Log.d(TAG, "back view:"+mView);
        	
        	if (null == mView)
        	{
        		Message msg = new Message();
                msg.what = 3;
                m_handler.sendMessage(msg);
        	}
        	else
        	{
        		setItem(true,mView,mPos);
        	}
        	
        	return true;
		}
		else if (m_focus == FOCUS.WIFI_SENIORLAYOUT)
		{
			mWifiSeniorLayout.setVisibility(View.GONE);
			mWifiListView.setVisibility(View.VISIBLE);
        	m_focus = FOCUS.WIFI_SENIOR;
        	m_senior.requestFocus();
        	
        	Log.d(TAG, "back view:"+mView);
        	
        	if (null == mView)
        	{
        		Message msg = new Message();
                msg.what = 3;
                m_handler.sendMessage(msg);
        	}
        	/*else
        	{
        		setItem(true,mView,mPos);
        	}*/
        	
        	return true;
		}
		return false;
	}
	
	/**
	 * 网线插上，界面隐藏
	 * @Title: checkHWConnect
	 * @Description: TODO
	 * @return: void
	 */
	public void checkHWConnect()
	{
		if (CommonUtils.getSetting().getEthMagHWConnected())
		{
			Log.d(TAG, "checkHWConnect");
			m_scan_progressBar.setVisibility(View.GONE);
			m_listView.setVisibility(View.GONE);
			m_senior.setVisibility(View.GONE);
			
			m_ok_refresh.setVisibility(View.VISIBLE);;		// 按OK键，刷新WIFI
			m_ok_refresh.setText(this.getResources().getString(R.string.settings_network_ethernet_connect));
			m_no_signal.setVisibility(View.GONE);
			m_isStartScan = false;
			m_no_wifi_signal = true;
		}
		else
		{
			m_ok_refresh.setText(this.getResources().getString(R.string.settings_network_ok_refresh));
		}
	}
	
	/**
	 * 是否正在设置
	 * @Title: isSetting
	 * @Description: TODO
	 * @return
	 * @return: boolean
	 */
	public boolean isSetting()
	{
		return m_setting;
	}
	
	/**
	 * 页面返回处理
	 * @Title: back
	 * @Description: TODO
	 * @return: void
	 */
	public void back()
	{
		if(m_timer != null)
		{
			m_timer.shutdownNow();
			m_timer = null;
		}
	}
	
	public void resume() {
        m_context.registerReceiver(mWifiReceiver, mIntentFilter);
    }

    public void pause() {
        m_context.unregisterReceiver(mWifiReceiver);
    }
}
