/**
 * Copyright © 2015GreatVision. All rights reserved.
 *
 * @Title: RequestData.java
 * @Prject: Mul2Launcher
 * @Package: com.sz.ead.framework.mul2launcher.dataprovider.requestdata
 * @Description: 请求数据管理
 * @author: zhaoqy  
 * @date: 2015-4-22 下午9:18:43
 */
package com.sz.ead.framework.mul2launcher.dataprovider.requestdata;

import java.util.ArrayList;
import com.sz.ead.framework.mul2launcher.dataprovider.dataitem.AppItem;
import com.sz.ead.framework.mul2launcher.dataprovider.datapacket.ElementListData;
import com.sz.ead.framework.mul2launcher.dataprovider.http.HttpEngineManager;
import com.sz.ead.framework.mul2launcher.dataprovider.http.UICallBack;
import com.sz.ead.framework.mul2launcher.dataprovider.packet.inpacket.InElementListPacket;
import com.sz.ead.framework.mul2launcher.dataprovider.packet.outpacket.OutElementListPacket;
import com.sz.ead.framework.mul2launcher.util.ConstantUtil;
import android.content.Context;

public class RequestData 
{
	/**
	 * 
	 * @Title: requestData
	 * @Description:     向后台请求数据
	 * @param uiCallback 回调对象
	 * @param context    上下文切换
	 * @param mark       列表标识ID
	 * @param size       每页个数
	 * @param page       当前页
	 * @param argument   不定参数
	 * @return: void
	 */
	public static void requestData(UICallBack uiCallback, Context context, int mark, int size, int page, Object... argument)
	{
		ElementListData data = null;
		data = new ElementListData(mark, size, page, argument);
		
		OutElementListPacket mOutPacket = new OutElementListPacket(uiCallback, mark, data);
		InElementListPacket mInPacket = new InElementListPacket(uiCallback, mark, data);
		HttpEngineManager.createHttpEngine(mOutPacket, mInPacket, context);
	}
	
	/**
	 * 
	 * @Title: getData
	 * @Description: 获取数据
	 * @param in     将服务器返回的数据解析之后的数据
	 * @return
	 * @return: Object
	 */
	@SuppressWarnings("unchecked")
	public static Object getData(Object in)
	{
		Object out = null;
		ElementListData data = (ElementListData) in;
		
		switch (data.getMark()) 
		{
		case ConstantUtil.MARK_APP_LIST: //应用列表
		{
			out = new ArrayList<AppItem>();
			
			if(data != null)
			{
				for (Object item : data.getList()) 
				{
					AppItem appItem = (AppItem)item;
					((ArrayList<AppItem>)out).add(appItem);
				}
			}	
			break;
		}
		default:
			break;
		}
		
		return out;
	}
	
	/**
	 * 
	 * @Title: getTotal
	 * @Description: 获取数据个数
	 * @param in
	 * @return
	 * @return: int
	 */
	public static int getTotal(Object in)
	{
		int count = 0;
		ElementListData data = (ElementListData) in;
		
		switch (data.getMark()) 
		{
		case ConstantUtil.MARK_APP_LIST: //应用列表
		{
			count = data.getTotal();
			break;
		}
		default:
			break;
		}
		
		return count;
	}
}
