/**
 * 
 * Copyright © 2014GreatVision. All rights reserved.
 *
 * @Title: CommonUtils.java
 * @Prject: BananaTvSetting
 * @Description: TODO
 * @author: lijungang 
 * @date: 2014-1-24 下午1:53:56
 */
package com.sz.ead.framework.mul2launcher.settings.util;

import java.io.FileInputStream;
import java.io.InputStream;
import java.security.MessageDigest;

import android.content.Context;
import android.os.Message;
import android.setting.settingservice.SettingManager;
import android.util.Log;

public class CommonUtils {

	private static Context mContext = null;
	private static SettingManager mSetting = null;
	private static final char HEX_DIGITS[] = { '0', '1', '2', '3', '4', '5', '6', '7', 
		'8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
	/**
	 * 
	 * @Title: newMessage
	 * @Description: 创建一个message
	 * @param what:msg.waht;  obg:msg.obg;
	 * @return: Message
	 */
	public static Message newMessage(int what, String obj)
	{
		Message msg = new Message();
		msg.what = what;
		msg.obj = obj;
		
		return msg;		
	}
	
	/**
	 * 
	 * @Title: toHexString
	 * @Description: byte数组转16进制string
	 * @return: String
	 */
	public static String toHexString(byte[] b) {
        StringBuilder sb = new StringBuilder(b.length * 2);
        for (int i = 0; i < b.length; i++) {
            sb.append(HEX_DIGITS[(b[i] & 0xf0) >>> 4]);
            sb.append(HEX_DIGITS[b[i] & 0x0f]);
        }
        return sb.toString();
    }
	/**
	 * 
	 * @Title: md5sum
	 * @Description: md5加密
	 * @param filename:加密字符串
	 * @return: String:md5后字符串
	 */
	public static String md5sum(String filename) {
        InputStream fis;
        byte[] buffer = new byte[1024];
        int numRead = 0;
        MessageDigest md5;
        try{
            fis = new FileInputStream(filename);
            md5 = MessageDigest.getInstance("MD5");
            while((numRead=fis.read(buffer)) > 0) {
                md5.update(buffer,0,numRead);
            }
            fis.close();
            return toHexString(md5.digest());   
        } catch (Exception e) {
            Log.v(ConstUtil.Tag, "md5sum error");
            return null;
        }
    }
	/**
	 * 
	 * @Title: setContext
	 * @Description: 设置上下文
	 * @return:void
	 */
	public static void setContext(Context context)
	{
		mContext = context;
		mSetting = (SettingManager)mContext.getSystemService(Context.SETTING_SERVICE);
	}
	/**
	 * 
	 * @Title: getSetting
	 * @Description: 获取settingManager对象
	 * @return: SettingManager
	 */
	public static SettingManager getSetting()
	{
		return mSetting;
	}
}
