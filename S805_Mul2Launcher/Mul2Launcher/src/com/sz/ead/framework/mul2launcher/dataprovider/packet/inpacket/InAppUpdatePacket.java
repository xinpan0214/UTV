package com.sz.ead.framework.mul2launcher.dataprovider.packet.inpacket;

import java.nio.ByteBuffer;

import com.sz.ead.framework.mul2launcher.dataprovider.http.UICallBack;
import com.sz.ead.framework.mul2launcher.dataprovider.packet.outpacket.OutPacket;

public class InAppUpdatePacket implements InPacket{
	
	// 界面回调
	private UICallBack mCallBack;
	
	// 标识一个Http请求，由调用者传入
	private int mToken;
	 
	
	/**
	 * 从服务器接收到的元素列表数据包的构造函数
	 * @author 李英英
	 * @param uiCallback 提供给上层的界面回调
	 * @param token Http请求的标识，由调用者传入
	 */
	public InAppUpdatePacket(UICallBack uiCallback, int token) {
		this.mCallBack = uiCallback;
		this.mToken = token;
	}

	/**
	 * Http请求时的响应回调
	 * @author 李英英
	 * @param responseBytes 从服务器接收到的数据
	 * @param encoding 从服务器接收到的数据的编码格式，如"UTF-8"
	 * @param cryptographic 是否加密
	 * @param responseFinish 是否响应完成
	 * @param id HttpEngine类中保存的请求的唯一ID
	 * @param out 本次请求上行至服务器的数据包
	 * @see um.market.android.network.packet.InPacket#httpResponse(java.nio.ByteBuffer, java.lang.String, boolean, boolean, int, um.market.android.network.packet.OutPacket)
	 */
	public void httpResponse(ByteBuffer responseBytes, String encoding,
			boolean cryptographic, boolean responseFinish, int id, OutPacket out) {
		return;
	}

	/**
	 * 接收数据时是否使用Cache
	 * @author 李英英
	 * @return true代表使用，将会在读取完服务器的数据后再回调onSuccessful，否则读取一次响应一次
	 */
	public boolean useCache() {
		return true;
	}

	/**
	 * 界面回调时进入取消Http请求的回调函数
	 * @author 李英英
	 * @param out 本次请求上行至服务器的数据包
	 */
	public void onCancel(OutPacket out) {
		mCallBack.onCancel(out, mToken);
	}

	/**
	 * 请求失败或发生网络错误时的回调
	 * @author 李英英
	 * @param responseCode 服务器的Http响应代码，如200, 404等
	 * @param errorDesc 错误的描述
	 * @param id HttpEngine类中保存的请求的唯一ID
	 * @param out 本次请求上行至服务器的数据包
	 */
	public void onNetError(int responseCode, String errorDesc, int id,
			OutPacket out) {
		mCallBack.onNetError(responseCode, errorDesc, out, mToken);
	}

	public void onSuccessful(ByteBuffer buffer, int bufLen) {
		// TODO Auto-generated method stub
		//Object object = pullParse.parseAllApp(buffer);
		//mCallBack.onSuccessful(object, mToken);
	}	
	
	public Object parse(ByteBuffer responseBytes, int responseBytesLen)
	{
		return null;
	}
}