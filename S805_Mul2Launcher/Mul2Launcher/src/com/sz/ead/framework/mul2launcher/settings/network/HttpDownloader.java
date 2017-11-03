/**
 * 
 * Copyright © 2014GreatVision. All rights reserved.
 *
 * @Title: HttpDownloader.java
 * @Prject: BananaTvSetting
 * @Description: TODO
 * @author: luojw 
 * @date: 2014-1-21 上午11:50:11
 */
package com.sz.ead.framework.mul2launcher.settings.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyStore;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;

import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;

import android.content.Context;
import android.os.Handler;
import android.setting.settingservice.SettingManager;
import android.util.Log;

public class HttpDownloader {
	private URL url=null;
	SSLContext sslContext;
	SettingManager m_setting;
	Context mContext;
	
	//下载
	public String download(String urlStr){
		StringBuffer strBuffer = new StringBuffer();
		String line = null;
		InputStream input1 = null;
		InputStreamReader isr = null;
		BufferedReader bReader = null;
		
		try{
			url = new URL(urlStr);
			HttpURLConnection urlConn = (HttpURLConnection)url.openConnection();
			
			input1 = urlConn.getInputStream();
			isr = new InputStreamReader(urlConn.getInputStream());
			bReader = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));
			
			int a = 0;
			while((line=bReader.readLine())!= null){
				strBuffer.append(line);
			}
			
		}
		catch(Exception e){
			e.printStackTrace();
		}
		finally{
			try{
				input1.close();
				isr.close();
				bReader.close();
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
		
		return strBuffer.toString();
	}
    
	InputStream inputStream = null;
    
    public InputStream getStream()
    {
    	return this.inputStream;
    }
    
    // 下载文件
	public int downFile(Handler handler,String urlStr,boolean isHttps,Context m_context){
		
		mContext = m_context;
		m_setting = (SettingManager)mContext.getSystemService(Context.SETTING_SERVICE);
		
		try{
			inputStream = null;Log.d("network", "isHttps---:"+isHttps);
			if (isHttps)
			{
				inputStream = getInputStreamFromHttpsUrl(urlStr,m_context);
			}
			else
			{
				inputStream = getInputStreamFromUrl(urlStr,true);
			}
			
		}
		catch(Exception e){
			e.printStackTrace();
			handler.sendEmptyMessage(10);
			
			return -1;
		}
		finally{
			try{
				//inputStream.close();
			}
			catch(Exception e){
				e.printStackTrace();
				handler.sendEmptyMessage(10);
				return -1;
			}
		}
		handler.sendEmptyMessage(9);
		return 0;
	}
	
	
	// 下载DNS文件
	public int downDNSFile(Handler handler,String urlStr){
		
		try{
			inputStream = null;
			inputStream = getInputStreamFromUrl(urlStr,false);
		}
		catch(Exception e){
			e.printStackTrace();
			handler.sendEmptyMessage(8);
			
			return -1;
		}
		finally{
			try{
				//inputStream.close();
			}
			catch(Exception e){
				e.printStackTrace();
				handler.sendEmptyMessage(8);
				return -1;
			}
		}
		handler.sendEmptyMessage(7);
		return 0;
	}

	private static HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() 
	{   
	    @Override  
	    public boolean verify(String hostname, SSLSession session) {   
	        // TODO Auto-generated method stub   
	        return true;   
	    }   
	};
	
	// http下载
	public InputStream getInputStreamFromUrl(String urlStr, boolean isLocal)
			throws java.net.MalformedURLException,IOException{
			url = new URL(urlStr);
			
			HttpURLConnection urlConn = (HttpURLConnection)url.openConnection();

		    urlConn.setConnectTimeout(3000);	// 连接超时
			urlConn.setReadTimeout(3000);
			
			urlConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");//输出键值对
			urlConn.setRequestProperty("Connection", "Keep-Alive");// 维持长连接
			urlConn.setRequestProperty("Charset", "UTF-8");
			urlConn.setRequestProperty("User-Agent","firmwarehttp");
			urlConn.setRequestProperty("Accept","*/*");
			urlConn.setRequestProperty("Accept-Encoding","identity");
			
			if (isLocal)
			{
				urlConn.setRequestProperty("version",m_setting.getSfVersion());
				urlConn.setRequestProperty("lang",m_setting.getCurLang());
				urlConn.setRequestProperty("oem",m_setting.getOem());
				urlConn.setRequestProperty("model",m_setting.getHwVersion());
				urlConn.setRequestProperty("accesscode",m_setting.getAccessCode());
				urlConn.setRequestProperty("pver",m_setting.getPver());
				urlConn.setRequestProperty("ptype",m_setting.getPtype());
				urlConn.setRequestProperty("AccessCodeHash",m_setting.getAccessCodeHash());
				urlConn.setRequestProperty("timezone",m_setting.getTimeZone());
			}
			
			
			InputStream inputStream = urlConn.getInputStream();
			
			return inputStream;
		}

	// https下载
	public InputStream getInputStreamFromHttpsUrl(String urlStr,Context m_context)
		throws java.net.MalformedURLException,IOException{
		
		HttpsTrustHosts(m_context);
		url = new URL(urlStr);
		
		HttpsURLConnection urlConn = (HttpsURLConnection)url.openConnection();
		
		urlConn.setHostnameVerifier(DO_NOT_VERIFY);
		urlConn.setRequestMethod("POST");
		urlConn.setSSLSocketFactory(sslContext.getSocketFactory());
		urlConn.setRequestProperty("User-Agent","firmwarehttp");
		
		urlConn.setRequestProperty("pwd","123456");
		urlConn.setRequestProperty("version",m_setting.getSfVersion());
		urlConn.setRequestProperty("lang",m_setting.getCurLang());
		urlConn.setRequestProperty("oem",m_setting.getOem());
		urlConn.setRequestProperty("model",m_setting.getHwVersion());
		urlConn.setRequestProperty("accesscode",m_setting.getAccessCode());
		urlConn.setRequestProperty("pver",m_setting.getPver());
		urlConn.setRequestProperty("ptype",m_setting.getPtype());
		urlConn.setRequestProperty("AccessCodeHash",m_setting.getAccessCodeHash());
		urlConn.setRequestProperty("timezone",m_setting.getTimeZone());
		
	    urlConn.setDoInput(true);
	    urlConn.setConnectTimeout(3000);	// 连接超时
		urlConn.setReadTimeout(3000);
		urlConn.connect();
		
		InputStream inputStream = urlConn.getInputStream(); 
		
		return inputStream;
	}
	
	
	
	static TrustManager[] xtmArray;

    private static void trustAllHosts() {   
        // Android 采用X509的证书信息机制    
        try {   
            SSLContext sc = SSLContext.getInstance("TLS");   
            sc.init(null, xtmArray, new java.security.SecureRandom());   
            HttpsURLConnection .setDefaultSSLSocketFactory(sc.getSocketFactory());   
            // 不进行主机名确认   
        } catch (Exception e) {   
            e.printStackTrace();   
        }   
    }

    // http证书认证
    public void HttpsTrustHosts(Context mcontext){
		try {
			sslContext = SSLContext.getInstance("TLS");
			KeyManagerFactory keyManager = KeyManagerFactory
					.getInstance("X509");
			KeyStore keyKeyStore = KeyStore.getInstance("BKS");
			keyKeyStore.load(mcontext.getAssets()
					.open("httpsyj.bks"), "jps#$wa".toCharArray());
			keyManager.init(keyKeyStore, "jps#$wa".toCharArray());
			sslContext.init(keyManager.getKeyManagers(), new TmArray[] { new TmArray() }, null);
			X509HostnameVerifier hostnameVerifier = SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;
			HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			Log.d("network", "HttpsTrustHosts error!!");
		}
    }

   

}
