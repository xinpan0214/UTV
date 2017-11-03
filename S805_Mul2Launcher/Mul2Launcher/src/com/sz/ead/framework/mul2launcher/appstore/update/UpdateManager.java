/**
 * Copyright © 2016. All rights reserved.
 *
 * @Title: UpdateManager.java
 * @Prject: Mul2Launcher
 * @Package: com.sz.ead.framework.mul2launcher.appstore.update
 * @Description: 自升级管理
 * @author: zhaoqy  
 * @date: 2016-1-13 上午11:01:36
 */

package com.sz.ead.framework.mul2launcher.appstore.update;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;
import com.sz.ead.framework.mul2launcher.dataprovider.dataitem.AppItem;
import com.sz.ead.framework.mul2launcher.db.InstalledTable;
import com.sz.ead.framework.mul2launcher.util.FileUtil;
import com.sz.ead.framework.mul2launcher.util.LogUtil;
import com.sz.ead.framework.mul2launcher.util.MD5;

public class UpdateManager extends Thread
{
	private ArrayList<AppItem> mUpdateList;
	private Context mContext;
	private String  mSavePath; //下载保存路径
	
	public UpdateManager(Context context, ArrayList<AppItem> AppList)
	{
		mContext = context;
		mUpdateList = AppList;
		clearApkDir();
	}
	
	@Override
	public void run() 
	{
		for (int i=0; i<mUpdateList.size(); i++)
		{
			AppItem appItem = mUpdateList.get(i);
			downloadApk(appItem);
			if (checkMD5(appItem))
			{
				if (checkPkgName(appItem))
				{
					//判断该App是否存在(主要是为了防止升级过程中卸载了该App)
					if (isAppExist(appItem.getPkgName()))
					{
						installApk(appItem);
					}
				}
			}
		}
	}
	
	private boolean isAppExist(String pkgname)
	{
		if (InstalledTable.queryAppInfo(pkgname) != null)
		{
			LogUtil.d(LogUtil.TAG, " +++++++++++++ isAppExist: true +++++++++++++ ");
			return true;
		}
		LogUtil.d(LogUtil.TAG, " +++++++++++++ isAppExist: false +++++++++++++ ");
		return false;
	}
	
	
	/**
	 * 
	 * @Title: downloadApk
	 * @Description: 下载apk文件
	 * @param apkurl
	 * @return: void
	 */
	private void downloadApk(AppItem appItem)
	{
		try
		{
			LogUtil.d(LogUtil.TAG, " +++++++++++++ downloadApk +++++++++++++ ");
			//判断SD卡是否存在，并且是否具有读写权限
			if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
			{
				mSavePath = getApkDir();
				String apkName = appItem.getPkgName() + ".apk";
				String apkurl = appItem.getDownloadUrl();
				URL url = new URL(apkurl);
				//创建连接
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.connect();
				//创建输入流
				InputStream is = conn.getInputStream();
				File file = new File(mSavePath);
				//判断文件目录是否存在
				if (!file.exists())
				{
					file.mkdir();
				}
				File apkFile = new File(mSavePath, apkName);
				FileOutputStream fos = new FileOutputStream(apkFile);
				byte buf[] = new byte[1024];
				
				do
				{
					//写入到文件中
					int numread = is.read(buf);
					if (numread <= 0)
					{
						//下载完成
						break;
					}
					//写入文件
					fos.write(buf, 0, numread);
				} while (true);
				fos.close();
				is.close();
			}
		} 
		catch (MalformedURLException e)
		{
			e.printStackTrace();
		} 
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @Title: checkMD5
	 * @Description: 比较MD5值
	 * @param appItem
	 * @return
	 * @return: boolean
	 */
	private boolean checkMD5(AppItem appItem)
	{
		String apkName = appItem.getPkgName() + ".apk";
		String localMd5 = MD5.md5sum(mSavePath + "/" + apkName);
		String serverMd5 = appItem.getMd5();
		
		LogUtil.d(LogUtil.TAG, " mSavePath: " + mSavePath);
		LogUtil.d(LogUtil.TAG, " apkName: " + apkName);
		LogUtil.d(LogUtil.TAG, " localMd5: " + localMd5);
		LogUtil.d(LogUtil.TAG, " serverMd5: " + serverMd5);
		
		if (!TextUtils.isEmpty(localMd5) && !TextUtils.isEmpty(serverMd5))
		{
			if (localMd5.equalsIgnoreCase(serverMd5))
			{
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 
	 * @Title: checkPkgName
	 * @Description: 比较包名
	 * @param appItem
	 * @return
	 * @return: boolean
	 */
	private boolean checkPkgName(AppItem appItem)
	{
		String apkName = appItem.getPkgName() + ".apk";
		PackageManager pm = mContext.getPackageManager();
		PackageInfo info = pm.getPackageArchiveInfo(mSavePath + "/" + apkName, PackageManager.GET_ACTIVITIES);
		if (info != null)
		{
			String calPkgName = info.applicationInfo.packageName;
			String serverPkgName = appItem.getPkgName();
			LogUtil.d(LogUtil.TAG, " calPkgName: " + calPkgName);
			LogUtil.d(LogUtil.TAG, " serverPkgName: " + serverPkgName);
			
			if (!TextUtils.isEmpty(calPkgName) && !TextUtils.isEmpty(serverPkgName))
			{
				if (calPkgName.equalsIgnoreCase(serverPkgName))
				{
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * 
	 * @Title: installApk
	 * @Description: 安装APK文件
	 * @return: void
	 */
	private void installApk(AppItem appItem)
	{
		LogUtil.d(LogUtil.TAG, " +++++++++++++ installApk +++++++++++++ ");
		String apkName = appItem.getPkgName() + ".apk";
		File apkfile = new File(mSavePath, apkName);
		if (!apkfile.exists())
		{
			return;
		}
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(apkfile), "on_application/vnd.android.package-archive");
		mContext.startService(intent);
	}
	
	/**
	 * 
	 * @Title: getApkDir
	 * @Description: 获取升级包路径
	 * @return: String
	 */
	private String getApkDir()
	{
		//获得存储卡的路径
		String sdpath = Environment.getExternalStorageDirectory() + "/";
		return sdpath + "update";
	}
	
	/**
	 * 
	 * @Title: clearApkDir
	 * @Description: 清除文件夹下所有文件
	 * @return: void
	 */
	private void clearApkDir()
	{
		String savePath = getApkDir();
		File file = new File(savePath);
		FileUtil.delete(file);
	}
}
