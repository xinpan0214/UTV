package com.sz.ead.framework.mul2launcher.appstore.downloader;


import android.os.Handler;

public interface IDownloadManager {
	
	// 加入下载队列
	public void addDownList(DownloadInfo item);
	
	// 加入收藏队列
	public void addCollectList(DownloadInfo item);

	// 加入传递消息handle
	public void addHandle(Handler handle);
	
	// 根据id开始下载
	public void startADownLoad(String AppCode);
	
	// 停止一个下载 
	public void stopADownLoad(String AppCode);
	
	// 暂停一个下载 
	public void pauseADownLoad(String AppCode);
	
	// 得到下载状态 
	public int getDownLoadStatus(String AppCode);
	
	// 得到应用状态 不包括安装
	public int getAppStatusNoInstall(String AppCode);
	
	// 得到应用状态 
	public int getAppStatus(String AppCode, String PkgName);
	
	// 停止所有下载
	public void stopAllDownload();
	
	// 找已下载apk路径
	public String getApkPath(String AppCode);
	
}
