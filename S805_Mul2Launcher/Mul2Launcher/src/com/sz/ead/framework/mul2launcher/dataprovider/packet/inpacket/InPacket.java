/**
 * Copyright © 2015GreatVision. All rights reserved.
 *
 * @Title: InPacket.java
 * @Prject: Mul2Launcher
 * @Package: com.sz.ead.framework.mul2launcher.dataprovider.packet.inpacket
 * @Description: 本文件定义下行至客户端的数据包接口类
 * @author: zhaoqy  
 * @date: 2015-4-22 下午5:42:50
 */
package com.sz.ead.framework.mul2launcher.dataprovider.packet.inpacket;

import java.nio.ByteBuffer;
import com.sz.ead.framework.mul2launcher.dataprovider.packet.outpacket.OutPacket;

public interface InPacket 
{
	/**
	 * Http请求时的响应回调
	 * @author zhaoqy
	 * @param responseBytes 从服务器接收到的数据
	 * @param encoding 从服务器接收到的数据的编码格式，如"UTF-8"
	 * @param cryptographic 是否加密
	 * @param responseFinish 是否响应完成
	 * @param id HttpEngine类中保存的请求的唯一ID
	 * @param out 本次请求上行至服务器的数据包
	 */
	void httpResponse(ByteBuffer responseBytes,String encoding, boolean cryptographic,boolean responseFinish, int id, OutPacket out);
	
	/**
	 * 接收数据时是否使用Cache
	 * @author zhaoqy
	 * @return true代表使用，将会在读取完服务器的数据后再回调onSuccessful，否则读取一次响应一次
	 */
	public boolean useCache();
	
	/**
	 * 界面回调时进入取消Http请求的回调函数
	 * @author zhaoqy
	 * @param out 本次请求上行至服务器的数据包
	 */
	public void onCancel(OutPacket out);
	
	/**
	 * 响应服务器且接收数据成功时的行为
	 * @author zhaoqy
	 * @param buffer 存放从服务器得到的数据
	 * @param bufLen 从服务器得到的数据的长度
	 */
	public void onSuccessful(ByteBuffer buffer, int bufLen);

	/**
	 * 请求失败或发生网络错误时的回调
	 * @author zhaoqy
	 * @param responseCode 服务器的Http响应代码，如200, 404等
	 * @param errorDesc 错误的描述
	 * @param id HttpEngine类中保存的请求的唯一ID
	 * @param out 本次请求上行至服务器的数据包
	 */
	public void onNetError(int responseCode, String errorDesc, int id, OutPacket out);
	
	/**
	 * 解析httpResponse中获取到的responseBytes，封装成UI需要的类结构供UI调用
	 * @author zhaoqy
	 * @param responseBytes 
	 * @param responseBytesLen
	 * @return
	 */
	public Object parse(ByteBuffer responseBytes, int responseBytesLen);
}
