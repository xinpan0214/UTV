package com.sz.ead.framework.mul2launcher.appstore.downloader;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

public class DownloadManager implements IDownloadManager {

	// 应用上下文
	private Context mContext;
	
	// sd卡路径
	private final String mSdCardPath = Environment.getExternalStorageDirectory()+ "";
	
	// apk文件夹
	private final String mApkPath = "ApkPath";
	
	// apk路径
	private String mApkFileDir = "";
	
	// 可以调用服务的方法
	private DownloadManagerService.DownloadBinder mBinder=null;
	
	// log filter
	public static final String TAG = "DownloadManager";
	
	// log flag
	public boolean debug = false;

	/**
	 * 构造函数
	 * @author liyingying
	 * @param context context必须用全局 getApplicationContext来绑定服务,不然bind service不成功 切记
	 */
	public DownloadManager(Context context) {
		mContext = context;
		//createApkDir();

		Intent intent = new Intent(mContext, DownloadManagerService.class);
		
		//如果先调用startService,则在多个服务绑定对象调用unbindService后服务仍不会被销毁  
        mContext.startService(intent);
       
        if(debug){ 
        	Log.i(TAG,"=======DownloadManager=======start end======");
        }
        try {
        	Boolean bindFlag=mContext.bindService(intent, conn, Context.BIND_AUTO_CREATE);
        	  if(debug){
      			Log.i(TAG,"=======ServerBind======"+bindFlag);
        	  }
        } catch (Exception e) {
        	 if(debug){
       			Log.i(TAG,"=======ServerBind==="+e.toString());
        	 }
		}
        if(debug){
        	Log.i(TAG,"=======DownloadManager=======end======");
        }
      
	}
	
	/**
	 * 启动服务
	 * @param packageContext
	 */
	public static void startMyself(Context packageContext, boolean update){
		Intent service = new Intent();
		service.putExtra("NEEDUPDATE", update);
		service.setClass(packageContext, DownloadManagerService.class);
		packageContext.startService(service);
	}

	/**
	 * 加入下载队列
	 * @author liyingying
	 * @param item 调用者填充
	 */
	public void addDownList(DownloadInfo item) {
		if(mBinder!=null){
			mBinder.getService().addDownList(item);
		}else{
			if(debug){
			Log.i(TAG,"=======addDownList======"+"serverbind null");
			}
		}
	}

	/**
	 * 收藏队列加入下载队列
	 * @author liyingying
	 * @param downinfo 收藏队列信息
	 */
	public void collectListtoDownList(DownloadInfo downinfo) {
		if(mBinder!=null){
			mBinder.getService().collectListtoDownList(downinfo);
		}else{
			if(debug)
				Log.i(TAG,"=======collectListtoDownList======"+"serverbind null");
		}
	}

	/**
	 * 准备队列加入下载队列
	 * @author liyingying
	 */
	public void readyListToDownList() {
		if(mBinder!=null){
			mBinder.getService().readyListToDownList();
		}else{
			if(debug)
				Log.i(TAG,"=======readyListToDownList======"+"serverbind null");
		}
	}

	/**
	 * 加入收藏队列
	 * @author liyingying
	 * @param item 调用者填充
	 * @see um.market.android.downloadmanagement.downloader.IDownloadManager#addCollectList(um.market.android.downloadmanagement.downloader.DownloadItem)
	 */
	public void addCollectList(DownloadInfo item) 
	{
		if (mBinder!=null)
		{
			mBinder.getService().addCollectList(item);
		}
		else
		{
			if(debug)
				Log.i(TAG,"=======addCollectList======"+"serverbind null");
		}
	}

	/**
	 * 加入传递消息handle
	 * @author liyingying
	 * @param handle 需要得到下载状态的ui界面可以传handle
	 * @see um.market.android.downloadmanagement.downloader.IDownloadManager#addHandle(android.os.Handler)
	 */
	public void addHandle(Handler handle) {
		if(mBinder!=null)
		{
			mBinder.getService().addHandle(handle);
		}else
		{
			if(debug)
				Log.i(TAG,"=======addHandle======"+"serverbind null");
		}
	}
	
	/**
	 * 删除消息handle
	 * @author liyingying
	 */
	public void delUiHandle() 
	{
		if(mBinder!=null)
		{
			mBinder.getService().delUiHandle();
		}
		else
		{
			if(debug)
				Log.i(TAG,"=======delUiHandle======"+"serverbind null");
		}
	}

	public void delAHandle(Handler handle)
	{
		if(mBinder!=null)
		{
			mBinder.getService().delAHandle(handle);
		}
		else
		{
			if(debug)
				Log.i(TAG,"=======delUiHandle======"+"serverbind null");
		}
	}
	
	/**
	 *  根据id开始下载
	 * @author liyingying
	 * @param AppId 应用id
	 * @see um.market.android.downloadmanagement.downloader.IDownloadManager#startADownLoad(int)
	 */
	public void startADownLoad(String AppCode) 
	{
		if(mBinder!=null)
		{
			mBinder.getService().startADownLoad(AppCode);
		}
		else
		{
			if(debug)
				Log.i(TAG,"=======startADownLoad======"+"serverbind null");
		}
	}

	/**
	 * 停止下载
	 * @author liyingying
	 * @param AppId 应用id
	 * @see um.market.android.downloadmanagement.downloader.IDownloadManager#stopADownLoad(int)
	 */
	public void stopADownLoad(String AppCode) 
	{
		if(mBinder!=null)
		{
			mBinder.getService().stopADownLoad(AppCode);
		}
		else
		{
			if(debug)
				Log.i(TAG,"=======stopADownLoad======"+"serverbind null");
		}
	}

	/**
	 * 暂停下载
	 * @author liyingying
	 * @param AppId 应用id
	 * @see um.market.android.downloadmanagement.downloader.IDownloadManager#pauseADownLoad(int)
	 */
	public void pauseADownLoad(String AppCode) {
		if(mBinder!=null){
			mBinder.getService().pauseADownLoad(AppCode);
		}else{
			if(debug)
				Log.i(TAG,"=======pauseADownLoad======"+"serverbind null");
		}
	}

	/**
	 * 得到线程下载状态
	 * @author liyingying
	 * @param packagename
	 * @param appId 应用id
	 * @return 状态
	 */
	public int getDownLoadStatus(String AppCode) {
		if(mBinder!=null){
			return mBinder.getService().getAppStatusNoInstall(AppCode);
		}else{
			if(debug)
				Log.i(TAG,"=======getDownLoadStatus======"+"serverbind null");
			return DownConstants.STATUS_DOWN_UNKNOWN;
		}
	}
	
	/**
	 * 得到应用状态 不包括安装
	 * @author liyingying
	 * @param packagename
	 * @param appId
	 * @return 状态
	 * @see um.market.android.downloadmanagement.downloader.IDownloadManager#getAppStatusNoInstall(java.lang.String, int)
	 */
	public int getAppStatusNoInstall(String AppCode) {
		if(mBinder!=null){
			return mBinder.getService().getAppStatusNoInstall(AppCode);
		}else{
			if(debug)
				Log.i(TAG,"=======getAppStatusNoInstall======"+"serverbind null");
			return DownConstants.STATUS_DOWN_UNKNOWN;
		}
	}

	/**
	 * 得到应用状态
	 * @author liyingying
	 * @param packagename
	 * @param appId
	 * @return 状态
	 * @see um.market.android.downloadmanagement.downloader.IDownloadManager#getAppStatus(java.lang.String, int)
	 */
	public int getAppStatus(String AppCode, String pkgName) {		
		if(mBinder!=null){
			return mBinder.getService().getAppStatus(AppCode, pkgName);
		}else{
			if(debug)
				Log.i(TAG,"=======getAppStatus======"+"serverbind null");
			return DownConstants.STATUS_DOWN_UNKNOWN;
		}
	}
	
	/**
	 * 停止所有下载
	 * @author liyingying
	 * @see um.market.android.downloadmanagement.downloader.IDownloadManager#stopAllDownload()
	 */
	public void stopAllDownload() 
	{	
		delUiHandle();
		if(mBinder!=null)
		{
			try {
				if(debug)
					Log.i(TAG,"=======stopAllDownload======");
				mBinder.getService().cancelUpdate();/*马上退出就要取消升级和下载icon*/
				mBinder.getService().setSkipApp(true);
				//mBinder.getService().stopBackDownload();//20141203  退出不关掉下载
				mContext.unbindService(conn);
			} 
			catch (Exception e) 
			{
				
			}
			
		}
		
	}

	/**
	 * 得到apk路径
	 * @author liyingying
	 * @param AppId 应用id
	 * @return 保存在sd卡路径
	 * @see um.market.android.downloadmanagement.downloader.IDownloadManager#getApkPath(int)
	 */
	public String getApkPath(String AppCode) {
		if(mBinder!=null){
			return mBinder.getService().getApkPath(AppCode);
		}else{
			if(debug)
				Log.i(TAG,"=======getApkPath======"+"serverbind null");
			return null;
		}
	}
	
	public int getThreadCount() {
		if(mBinder!=null){
			return mBinder.getService().getThreadCount();
		}else{
			if(debug)
				Log.i(TAG,"=======getApkPath======"+"serverbind null");
			return 0;
		}
	}

	/**
	 * 创建文件夹目录
	 * @author liyingying
	 * @return true
	 */
	/*private boolean createApkDir() {
		StringBuffer sb = new StringBuffer();
		sb.append(mSdCardPath);
		sb.append("/");
		sb.append(mContext.getResources().getString(R.string.appstore_sd_folder));
		sb.append("/");
		sb.append(mApkPath);
		sb.append("/");
		mApkFileDir = sb.toString();
		FileUtil.isFolderExists(mApkFileDir);
		return true;
	}*/

	/**
	 * 绑定服务
	 */
	private ServiceConnection conn = new ServiceConnection() {	  
        public void onServiceConnected(ComponentName name, IBinder service) {  
        	mBinder = (DownloadManagerService.DownloadBinder) service;
            if(debug)
				Log.i(TAG,"=======ServerBind==success====");
            /*发个广播更新*/
            Intent intent = new Intent();
    		intent.setAction(DownConstants.DOWN_STATUS_ACTION);
    		Bundle bundle = new Bundle();
    		bundle.putString(DownConstants.DOWN_STATUS_DOWNLOAD_STATUS_KEY, DownConstants.DOWN_BROADCAST_TO_UI_BIND_SERVICE_SUCCESS);
    		intent.putExtras(bundle);
    		mContext.sendBroadcast(intent);
    		
    		/*DownloadItem iteminfo = new DownloadItem();
            iteminfo.setDownApkUrl("http://192.168.20.136:8080/voicejoke/VoiceJoke/Gaya3D_Launcher_201206a.apk");     
            iteminfo.setElementFlag(2736287);
            iteminfo.setDownPackageName("com.ts.launcher.v3");
            iteminfo.setDownSize(4807577);
            iteminfo.setDownName("gaiya");
            mBinder.getService().addDownList(iteminfo);	*/
        }  
 
        public void onServiceDisconnected(ComponentName name) {  
        	if(debug)
				Log.i(TAG,"=======onServiceDisconnected===fail===");
        }  
    };  

}