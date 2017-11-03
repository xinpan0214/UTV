/**
 * Copyright © 2015GreatVision. All rights reserved.
 *
 * @Title: OutPacket.java
 * @Prject: Mul2Launcher
 * @Package: com.sz.ead.framework.mul2launcher.dataprovider.packet.outpacket
 * @Description: 本文件定义上行至服务器的数据包接口类
 * @author: zhaoqy  
 * @date: 2015-4-22 下午5:43:49
 */
package com.sz.ead.framework.mul2launcher.dataprovider.packet.outpacket;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.SocketTimeoutException;
import java.net.URL;
import android.content.Context;

public interface OutPacket 
{
	public enum Method 
	{
		GET,
		POST,
	}

	/**
	 * 设置通传参数的应用详情包的请求URL地址
	 * @author zhaoqy
	 * @param context android context
	 * @return 添加了通传参数的应用详情包的请求URL地址
	 */
	public URL serviceURL(Context context) throws ProtocolException, IOException;
	
	/**
	 * 设置外发包请求服务器时所用的方法
	 * @author zhaoqy
	 * @return 外发包向服务器的请求方法
	 */
	public Method requestMethod();
	
	/**
	 * 设置外发包请求服务器的连接超时，数据读取超时
	 * @author zhaoqy
	 * @return 请求超时的时间
	 */
	public int getTimeout();
	
	/**
	 * 设置外发包请求服务器时所用的方法，通用请求参数HttpEngine中已有设置
	 * @author zhaoqy
	 * @return 外发包向服务器的请求方法
	 */
	public void setRequestProperty(final HttpURLConnection httpConn) throws SocketTimeoutException, IOException;
	
	/**
	 * 填充TLV数据至上行的Http流中
	 * @author zhaoqy
	 * @param output 程序的上行Http数据流
	 * @param context android上下文环境
	 * @return true代表填充数据流成功，false代表填充失败
	 * @throws IOException
	 */
	public boolean fillData(OutputStream output, Context context) throws IOException;
	
	/**
	 * 生成通传参数的一串字符
	 * 例如 imei=abcd&rhv=Moral&md=techaiz
	 * @return
	 */
	public String generateGeneralParam(Context context);
}
