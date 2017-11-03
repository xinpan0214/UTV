/**
 * Copyright © 2015GreatVision. All rights reserved.
 *
 * @Title: AuthResInfo.java
 * @Prject: Mul2Launcher
 * @Package: com.sz.ead.framework.mul2launcher.dataprovider.dataitem
 * @Description: 鉴权结果Item
 * @author: zhaoqy  
 * @date: 2015-5-6 上午9:45:44
 */
package com.sz.ead.framework.mul2launcher.dataprovider.dataitem;

public class AuthResInfo 
{
	private String mStatus;
	private String mMsg;
	private String mType;
	private String mTime;
	private String mPvr;

	public AuthResInfo(String status, String msg, String type, String time,String pvr) 
	{
		super();
		mStatus = status;
		mMsg = msg;
		mType = type;
		mTime = time;
		mPvr = pvr;
	}
	
	public String getStatus() 
	{
		return mStatus;
	}
	
	public void setStatus(String status) 
	{
		mStatus = status;
	}
	
	public String getMsg() 
	{
		return mMsg;
	}
	
	public void setMsg(String msg) 
	{
		mMsg = msg;
	}
	
	public String getType() 
	{
		return mType;
	}
	
	public void setType(String type) 
	{
		mType = type;
	}
	
	public String getTime() 
	{
		return mTime;
	}
	
	public void setTime(String time) 
	{
		mTime = time;
	}
	
	public String getPvr() 
	{
		return mPvr;
	}

	public void setPvr(String pvr) 
	{
		mPvr = pvr;
	}
}
