/**
 * Copyright © 2015GreatVision. All rights reserved.
 *
 * @Title: OutElementListPacket.java
 * @Prject: Mul2Launcher
 * @Package: com.sz.ead.framework.mul2launcher.dataprovider.packet.outpacket
 * @Description: 本文件定义上行至服务器的元素列表数据包
 * @author: zhaoqy  
 * @date: 2015-4-22 下午5:43:20
 */
package com.sz.ead.framework.mul2launcher.dataprovider.packet.outpacket;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.SocketTimeoutException;
import java.net.URL;
import com.sz.ead.framework.mul2launcher.dataprovider.datapacket.ElementListData;
import com.sz.ead.framework.mul2launcher.dataprovider.http.UICallBack;
import com.sz.ead.framework.mul2launcher.util.ConstantUtil;
import com.sz.ead.framework.mul2launcher.util.LogUtil;
import com.sz.ead.framework.mul2launcher.util.StbInfo;
import android.content.Context;

public class OutElementListPacket implements OutPacket
{
	public static final int NETWORK_TIMEOUT = 15000; //网络请求的超时时间设置
	private ElementListData mElementLitData; 
	
	/**
	 * 外发元素列表数据的构造函数
	 * @author zhaoqy
	 * @param uiCallback
	 * @param token
	 * @param datas
	 */
	public OutElementListPacket(UICallBack uiCallback, int mark, ElementListData data) 
	{
		mElementLitData = data;
	}

	/**
	 * 
	 * @author zhaoqy
	 * @param context  android上下文context
	 * @return 请求URL地址
	 * @throws ProtocolException
	 * @throws IOException
	 */
	@Override
	public URL serviceURL(Context context) throws ProtocolException, IOException 
	{
		StringBuffer sb = new StringBuffer();
		sb.append(ConstantUtil.REQUEST_URL_HOST);
		
		switch (mElementLitData.getMark()) 
		{
		case ConstantUtil.MARK_APP_LIST: //应用列表
		{
			if(ConstantUtil.URL_LOCAL_SERVICE)
			{
				sb.append("/applist.xml");
			}
			else
			{
				sb.append("/");
				sb.append("applist.action?");
				sb.append("lang=");
				sb.append(StbInfo.getCurLang(context));
				sb.append("&");
				sb.append("mac=");
				sb.append(StbInfo.getMacAddress(context));
				sb.append("&");
				sb.append("cpuid=");
				sb.append(StbInfo.getCpuid(context));
				sb.append("&");
				sb.append("size=");
				sb.append(mElementLitData.getSize());
				sb.append("&");
				sb.append("page=");
				sb.append(mElementLitData.getPage());
			}
			break;
		}
		case ConstantUtil.MARK_APP_UPDATE: //应用升级
		{
			if(ConstantUtil.URL_LOCAL_SERVICE)
			{
				sb.append("appupdate.xml");
			}
			else
			{
				sb.append("/");
				sb.append("update.action?");
				sb.append("lang=");
				sb.append(StbInfo.getCurLang(context));
				sb.append("&");
				sb.append("mac=");
				sb.append(StbInfo.getMacAddress(context));
				sb.append("&");
				sb.append("cupid=");
				sb.append(StbInfo.getCpuid(context));
				sb.append("&");
				sb.append("pkgname="); 
				sb.append(mElementLitData.getArgument().get(0)); //把pkgname传进来
			}
			break;
		}
		case ConstantUtil.MARK_APP_DETAIL: //应用详情
		{
			if(ConstantUtil.URL_LOCAL_SERVICE)
			{
				sb.append("appdetail.xml");
			}
			else
			{
				sb.append("/");
				sb.append("appdetail.action?");
				sb.append("lang=");
				sb.append(StbInfo.getCurLang(context));
				sb.append("&");
				sb.append("mac=");
				sb.append(StbInfo.getMacAddress(context));
				sb.append("&");
				sb.append("cupid=");
				sb.append(StbInfo.getCpuid(context));
				sb.append("&");
				sb.append("pkgname="); 
				sb.append(mElementLitData.getArgument().get(0)); //把pkgname传进来
			}
			break;
		}
		default:
			break;
		}
		
		LogUtil.i(LogUtil.TAG, " URL: " + sb.toString());
		return new URL(sb.toString());
	}

	/**
	 * 设置外发包请求服务器时所用的方法
	 * @author zhaoqy
	 * @return 外发包向服务器的请求方法
	 */
	@Override
	public Method requestMethod() 
	{
		Method method = Method.GET;
		
		switch (mElementLitData.getMark()) 
		{
		case ConstantUtil.MARK_APP_LIST:            //应用鉴权
		{
			method = Method.GET;
			break;
		}
		case ConstantUtil.MARK_APP_UPDATE:         //应用升级
		{
			method = Method.GET;
			break;
		}
		case ConstantUtil.MARK_APP_DETAIL:         //应用升级
		{
			method = Method.GET;
			break;
		}
		default:
			break;
		}
		return method;
	}

	/**
	 * 设置外发包请求服务器的连接超时，数据读取超时
	 * @author zhaoqy
	 * @return 请求超时的时间
	 */
	@Override
	public int getTimeout() 
	{
		return NETWORK_TIMEOUT;
	}

	/**
	 * 设置请求Http头信息
	 * @author zhaoqy
	 * @param httpConn Http请求连接
	 * @throws SocketTimeoutException
	 * @throws IOException
	 */
	@Override
	public void setRequestProperty(HttpURLConnection httpConn) throws SocketTimeoutException, IOException 
	{
		return;
	}
	
	@Override
	public boolean fillData(OutputStream output, Context context) throws IOException 
	{
		return true;
	}

	@Override
	public String generateGeneralParam(Context context) 
	{
		return null;
	}
}
