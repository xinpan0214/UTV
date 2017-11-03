/**
 * Copyright © 2015GreatVision. All rights reserved.
 *
 * @Title: InElementListPacket.java
 * @Prject: Mul2Launcher
 * @Package: com.sz.ead.framework.mul2launcher.dataprovider.packet.inpacket
 * @Description: 下行客户端数据
 * @author: zhaoqy  
 * @date: 2015-4-22 下午5:42:17
 */
package com.sz.ead.framework.mul2launcher.dataprovider.packet.inpacket;

import java.nio.ByteBuffer;
import com.sz.ead.framework.mul2launcher.dataprovider.datapacket.ElementListData;
import com.sz.ead.framework.mul2launcher.dataprovider.http.UICallBack;
import com.sz.ead.framework.mul2launcher.dataprovider.packet.outpacket.OutPacket;
import com.sz.ead.framework.mul2launcher.dataprovider.xmlpull.PullParse;
import com.sz.ead.framework.mul2launcher.util.ConstantUtil;

public class InElementListPacket implements InPacket
{
	private int             mMark;           //标识一个Http请求，由调用者传入
	private UICallBack      mCallBack;        //界面回调
	private ElementListData mElementLitData; 
	
	/**
	 * 从服务器接收到的元素列表数据包的构造函数
	 * @author zhaoqy
	 * @param uiCallback 提供给上层的界面回调
	 * @param token Http请求的标识，由调用者传入
	 */
	public InElementListPacket(UICallBack uiCallback, int mark, ElementListData data) 
	{
		mCallBack = uiCallback;
		mMark = mark;
		mElementLitData = data;
	}

	/**
	 * Http请求时的响应回调
	 * @author zhaoqy
	 * @param responseBytes 从服务器接收到的数据
	 * @param encoding 从服务器接收到的数据的编码格式，如"UTF-8"
	 * @param cryptographic 是否加密
	 * @param responseFinish 是否响应完成
	 * @param id HttpEngine类中保存的请求的唯一ID
	 * @param out 本次请求上行至服务器的数据包
	 * @see um.market.android.network.packet.InPacket#httpResponse(java.nio.ByteBuffer, java.lang.String, boolean, boolean, int, um.market.android.network.packet.OutPacket)
	 */
	@Override
	public void httpResponse(ByteBuffer responseBytes, String encoding, boolean cryptographic, boolean responseFinish, int id, OutPacket out) 
	{
		return;
	}

	/**
	 * 接收数据时是否使用Cache
	 * @author zhaoqy
	 * @return true代表使用，将会在读取完服务器的数据后再回调onSuccessful，否则读取一次响应一次
	 */
	@Override
	public boolean useCache() 
	{
		return true;
	}

	/**
	 * 界面回调时进入取消Http请求的回调函数
	 * @author zhaoqy
	 * @param out 本次请求上行至服务器的数据包
	 */
	@Override
	public void onCancel(OutPacket out) 
	{
		mCallBack.onCancel(out, mMark);
	}

	/**
	 * 响应服务器且接收数据成功时的行为
	 * @author zhaoqy
	 * @param buffer 存放从服务器得到的数据
	 * @param bufLen 从服务器得到的数据的长度
	 */
	@Override
	public void onSuccessful(ByteBuffer buffer, int bufLen) 
	{
		Object object = parse(buffer, bufLen);
		
		if (object != null)
		{
			mCallBack.onSuccessful(object, mMark);
		}
		else
		{
			mCallBack.onNetError(-1, "error", null, mMark);
		}
	}

	/**
	 * 请求失败或发生网络错误时的回调
	 * @author zhaoqy
	 * @param responseCode 服务器的Http响应代码，如200, 404等
	 * @param errorDesc 错误的描述
	 * @param id HttpEngine类中保存的请求的唯一ID
	 * @param out 本次请求上行至服务器的数据包
	 */
	@Override
	public void onNetError(int responseCode, String errorDesc, int id, OutPacket out) 
	{
		mCallBack.onNetError(responseCode, errorDesc, out, mMark);
	}

	/**
	 * 数据解析
	 * @author zhaoqy
	 * @param responseBytes 服务器返回数据
	 * @param responseBytesLen 错误的描述
	 */
	@Override
	public Object parse(ByteBuffer responseBytes, int responseBytesLen) 
	{
		ElementListData uiObjects = null;
		
		switch (mElementLitData.getMark()) 
		{
		case ConstantUtil.MARK_APP_LIST: //应用鉴权
		{
			uiObjects = PullParse.parseAppList(responseBytes, mElementLitData.getMark());
			break;
		}
		case ConstantUtil.MARK_APP_UPDATE: //应用升级
		{
			uiObjects = PullParse.parseAppUpdate(responseBytes, mElementLitData.getMark());
			break;
		}
		case ConstantUtil.MARK_APP_DETAIL: //应用详情
		{
			uiObjects = PullParse.parseAppDetail(responseBytes, mElementLitData.getMark());
			break;
		}
		default:
			break;
		}
		return uiObjects;
	}
}
