/**
 * Copyright © 2015GreatVision. All rights reserved.
 *
 * @Title: UICallBack.java
 * @Prject: Mul2Launcher
 * @Package: com.sz.ead.framework.mul2launcher.dataprovider.http
 * @Description: UI层通知回调接口类
 * @author: zhaoqy  
 * @date: 2015-4-22 下午5:40:11
 */
package com.sz.ead.framework.mul2launcher.dataprovider.http;

import com.sz.ead.framework.mul2launcher.dataprovider.packet.outpacket.OutPacket;

public interface UICallBack 
{
	/**
	 * 当任务取消时被调用,在主线程中
	 * @author zhaoqy
	 * @param out 外发的数据包
	 * @param token HTTP请求的标识，由调用者传入
	 */
	public void onCancel(OutPacket out, int mark);

	/**
	 * 请求成功时调用
	 * @author zhaoqy
	 * @param in 请求成功后，解析的网络数据结构,因为有不同的数据结构,所以以Object形式出现,用时应强制转换(InPacket in)
	 * @param token
	 */
	public void onSuccessful(Object in, int mark);  

	/**
	 * 请求失败时调用
	 * @author zhaoqy
	 * @param responseCode Http请求的返回码, 如200, 404等
	 * @param errorDesc 错误描述
	 * @param out 外发的数据包
	 * @param token HTTP请求的标识，由调用者传入
	 */
	public void onNetError(int responseCode, String errorDesc, OutPacket out, int mark);
}
