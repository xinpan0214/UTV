/**
 * Copyright © 2015GreatVision. All rights reserved.
 *
 * @Title: FileUtil.java
 * @Prject: Mul2Launcher
 * @Package: com.sz.ead.framework.mul2launcher.util
 * @Description: 文件Util
 * @author: zhaoqy  
 * @date: 2015-4-23 上午11:20:51
 */
package com.sz.ead.framework.mul2launcher.util;

import java.io.File;
import java.lang.reflect.Field;
import java.text.DecimalFormat;

import com.nostra13.universalimageloader.utils.L;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

public class FileUtil 
{
	public static final String APPICON_PATH = "/data/appicon";
	
	/**
	 * 
	 * @Title: getAppIconDir
	 * @Description: 获取appicon目录
	 * @param context
	 * @return
	 * @return: String
	 */
	public static String getAppIconDir(Context context) 
	{
		File dataDir = new File(FileUtil.APPICON_PATH);	
		if (!dataDir.exists()) 
		{
			if (!dataDir.mkdir()) 
			{
				LogUtil.i(LogUtil.TAG, " Unable to create getAppIconDir directory");
			}
		}		
		return dataDir.getAbsolutePath();
	}
	
	/**
	 * 删除文件
	 * @author liyingying
	 * @param path 删除文件路径
	 */
	public static void deleteFile(String path) {
		File file = new File(path);

		if (file.exists()) {
			file.delete();
		} else {
			Log.i("=====FILE=====", "deleteFile");
		}
		
	}
	
	/**
	 * 
	 * @Title: delete
	 * @Description: 删除文件
	 * @param file
	 * @return: void
	 */
	public static void delete(File file) {
		if (file.isFile()) {  
			file.delete();  
			return;  
		}  
		if(file.isDirectory()){  
			File[] childFiles = file.listFiles();  
			if (childFiles == null || childFiles.length == 0) {  
				file.delete();  
				return;}  
			for (int i = 0; i < childFiles.length; i++) 
			{  
		        delete(childFiles[i]);  
			}  
	        file.delete();  
	    }  
	}
	
	/**
	 * 文件夹目录是否存在 不存在就创建
	 * @author liyingying
	 * @param strFolder 文件夹目录
	 * @return true代表文件夹创建成功，反之失败
	 */
	public static boolean isFolderExists(String strFolder) {
		File file = new File(strFolder);
		if (!file.exists()) {
			if (file.mkdirs()) {
				return true;
			} else {
				return false;
			}
		}
		return true;
	}

	/**
	 * 文件是否存在
	 * @author liyingying
	 * @param path 文件路径
	 * @return true代表文件创建成功，反之失败
	 */
	public static boolean isFileExist(String path) {
		File file = new File(path);
		if (file.exists()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 判断sd卡是不是存在
	 * @author liyingying
	 * @return true代表存在，反之不存在
	 */
	public static boolean isSdCardExist() {
		if (android.os.Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 格式化文件大小
	 * @author liyingying
	 * @param size 原来文件byte
	 * @return 格式化文件大小
	 */
	public static String FormatFileSize(long size) {
		double fSize;
		String str = "";	

		if (size >= 0) {
			fSize = size / (1024 * 1024.0);
			str = new DecimalFormat("0.0").format(fSize) + "M";

		} else {
			str = new DecimalFormat("0.0").format(0) + "M";
		}

		return str;
	}

	/**
	 * 格式化文件大小
	 * @author liyingying
	 * @param size 原来文件byte
	 * @return 格式化文件大小
	 */
	public static String FormatFileSizeEx(int size) {
		double fSize;
		String str = "";

		if (size >= 1024 * 1024) {
			fSize = size / (1024 * 1024.0);
			str = new DecimalFormat("0.0").format(fSize) + "MB";
		} else if (size >= 1024 && size < 1024 * 1024) {
			fSize = size / (1024.0);
			str = new DecimalFormat("0.0").format(fSize) + "KB";
		} else if (size >= 0 && size < 1024) {
			fSize = size / (1024.0);
			str = new DecimalFormat("0.0").format(fSize) + "KB";
		} else {
			str = new DecimalFormat("0.0").format(0) + "KB";
		}

		return str;
	}

	/**
	 * 得到下载百分比
	 * @author liyingying
	 * @param cur 当前下载大小
	 * @param total 总大小
	 * @return 百分比
	 */
	public static String getPrecent(int cur, int total) {
		double perFloat = cur / (total * 1.0);
		StringBuffer sb = new StringBuffer();
		int perInt;
		if (perFloat >= 0.0 && perFloat <= 1.0) {
			perInt = (int) (perFloat * 100);
		} else {
			//total为负数 
			perInt = 0;
		}
		sb.append(perInt);
		sb.append("%");
		return sb.toString();
	}

	/**
	 * 得到任务栏高
	 * @author liyingying
	 * @param context 应用上下文
	 * @return 任务栏高度
	 */
	public static int getTaskbarHeight(Context context) {
		int taskbar = 0;
		try {
			Class c = Class.forName("com.android.internal.R$dimen");
			Object obj = c.newInstance();
			Field field = c.getField("status_bar_height");
			int x = Integer.parseInt(field.get(obj).toString());
			taskbar = context.getResources().getDimensionPixelSize(x);
		} catch (Exception e) {
		}

		return taskbar;
	}

	/**
	 * 得到字符串
	 * @author liyingying
	 * @param context 应用上下文
	 * @param id 字符串id
	 * @return 字符串
	 */
	public static String getString(Context context, int id) {
		return context.getResources().getString(id);
	}
	
	/**
	 * 得到data还存在大小 因为应用是装在data分区
	 * @author liyingying
	 * @return 大小Byte
	 */
	public static long getAvailaleSize(){
		long count=0;
		
		//取得sdcard文件路径
		//File path = Environment.getExternalStorageDirectory(); 
		File path = new File("/data");
		StatFs stat = new StatFs(path.getPath()); 
		//获取block的SIZE
		long blockSize = stat.getBlockSize(); 
		//空闲的Block的数量
		long availableBlocks = stat.getAvailableBlocks();
		count=availableBlocks * blockSize;
		//返回byte大小值
		return count; 
		}
	
	public static String getApkDir(Context context) {
		File dataDir = new File(new File(Environment.getExternalStorageDirectory(), "Android"), "data");
		//跟图片下载在一个路径
		File appCacheDir = new File(new File(dataDir, context.getPackageName()), "apkpath");
		if (!appCacheDir.exists()) {
			if (!appCacheDir.mkdirs()) {
				L.w("Unable to create external cache directory");
			}
		}	
		return appCacheDir.getAbsolutePath();
	}

}
