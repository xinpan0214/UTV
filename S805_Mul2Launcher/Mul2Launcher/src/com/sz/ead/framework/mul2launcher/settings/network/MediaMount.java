/**
 * Copyright © 2014GreatVision. All rights reserved.
 *
 * @Title: MediaMount.java
 * @Prject: BananaTvSetting
 * @Description: TODO
 * @date: 2014-9-3 下午12:18:07
 */
package com.sz.ead.framework.mul2launcher.settings.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.sz.ead.framework.mul2launcher.settings.util.FileUtils;
import com.szgvtv.ead.framework.bi.Bi;

/**
 * Copyright © 2014GreatVision. All rights reserved.
 *
 * @Title: MediaMount.java
 * @Prject: BananaTvSetting
 * @Description: TODO
 * @author: lijungang 
 * @date: 2014-9-3 下午12:18:07
 */
public class MediaMount extends BroadcastReceiver {

	public static final String TAG = "network";
	private static final String USB_PATH0 = "/storage/external_storage/sda";
	private static final String USB_PATH1 = "/storage/external_storage/sdb";
	private static final String USB_PATH2 = "/storage/external_storage/sda1";
	private static final String USB_PATH3 = "/storage/external_storage/sdb1";
	private static final String SDCARD_PATH = "/storage/external_storage/sdcard1";
	/* (non-Javadoc)
	 * @see android.content.BroadcastReceiver#onReceive(android.content.Context, android.content.Intent)
	 */
	@Override
	public void onReceive(Context arg0, Intent arg1) {
		// TODO Auto-generated method stub
		if(arg1.getAction().equals(Intent.ACTION_MEDIA_MOUNTED)){			// 外设插拔
			Log.d(TAG, "receive ACTION_MEDIA_MOUNTED");
			String path = arg1.getDataString();
			String size = "";
			try {
				path = path.substring("file://".length());
				size = FileUtils.getPathTotalSize(path) + "G";
			} catch (Exception e) {
				// TODO: handle exception
			}
			if(path.equals(USB_PATH0) || 
					path.equals(USB_PATH1) || 
					path.equals(USB_PATH2) || 
					path.equals(USB_PATH3) ){
				Bi.sendBiMsg(Bi.BICT_FIRMWARE, Bi.BICC_EXTERNAL_DEVICE, "2", "", "", size, "");
			}else if(path.equals(SDCARD_PATH)){
				Bi.sendBiMsg(Bi.BICT_FIRMWARE, Bi.BICC_EXTERNAL_DEVICE, "1", "", "", size, "");
			}
		}
	}
}
