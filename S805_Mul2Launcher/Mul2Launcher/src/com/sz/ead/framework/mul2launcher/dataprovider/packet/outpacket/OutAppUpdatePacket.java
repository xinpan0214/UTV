package com.sz.ead.framework.mul2launcher.dataprovider.packet.outpacket;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import com.sz.ead.framework.mul2launcher.dataprovider.dataitem.UpdateInfoItem;
import com.sz.ead.framework.mul2launcher.dataprovider.http.UICallBack;
import com.sz.ead.framework.mul2launcher.util.Constant;
import com.sz.ead.framework.mul2launcher.util.LogUtil;
import android.content.Context;

public class OutAppUpdatePacket implements OutPacket{

	private UICallBack mCallBack;
	private int mToken;
	private ArrayList<UpdateInfoItem> mDatas;
	
	/**
	 * 应用升级更新的上行数据包类
	 * @author 李英英
	 * @param uiCallback 界面回调
	 * @param token 调用者传入的用于标识本次Http请求的标识符
	 * @param datas 调用者传入的程序更新升级数据信息
	 */
	public OutAppUpdatePacket(UICallBack uiCallback, int token, ArrayList<UpdateInfoItem> datas) {
		this.mCallBack = uiCallback;
		this.mToken = token;
		this.mDatas = datas;
	}

	/**
	 * 返回应用升级更新的服务器请求地址
	 * @author 李英英
	 * @param context android上下文环境
	 * @return 添加了通传参数的应用升级更新包的请求URL地址
	 * @throws ProtocolException
	 * @throws IOException
	 * @see um.market.android.network.packet.OutPacket#serviceURL(android.content.Context)
	 */
	public URL serviceURL(Context context) throws ProtocolException,
			IOException {
		
		StringBuffer sb = new StringBuffer();
		sb.append(Constant.REQUEST_URL_HOST);
		if(Constant.URL_LOCAL_SERVICE){
			sb.append("/update.xml");
		}else{
			sb.append("/update.action?");
			
		}
		LogUtil.i(LogUtil.TAG, " URL: " + sb.toString());
		return new URL(sb.toString());
	}

	/**
	 * 设置外发包请求服务器时所用的方法
	 * @author 李英英
	 * @return 外发包向服务器的请求方法
	 */
	public Method requestMethod() {
		return Method.POST;
	}

	/**
	 * 设置外发包请求服务器的连接超时，数据读取超时
	 * @author 李英英
	 * @return 请求超时的时间
	 */
	public int getTimeout() {
		return Constant.NETWORK_TIMEOUT;
	}

	/**
	 * 设置请求Http头信息
	 * @author 李英英
	 * @param httpConn Http请求连接
	 * @throws SocketTimeoutException
	 * @throws IOException
	 */
	public void setRequestProperty(HttpURLConnection httpConn)
			throws SocketTimeoutException, IOException {
		return;
	}
		
	public boolean fillData(OutputStream output, Context context) throws IOException {
		
		try {
			StringBuffer sb = new StringBuffer();
			sb.append(Constant.generateGeneralParam(context));
			sb.append("&");
			sb.append("pkgname=");
			for (int i = 0; i < mDatas.size(); i++) {
				UpdateInfoItem infoItem = mDatas.get(i);
				sb.append(infoItem.getPkgName());
				sb.append(";");
			}
			output.write(sb.toString().getBytes());			
			return true;
		} catch (Exception e) {
			// TODO: handle exception
			return false;
		}
	}

	public String generateGeneralParam(Context context) {
		return null;
	}
}
