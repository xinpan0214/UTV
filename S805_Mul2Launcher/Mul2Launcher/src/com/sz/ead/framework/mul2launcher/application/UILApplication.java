/**
 * 
 * Copyright © 2015GreatVision. All rights reserved.
 *
 * @Title: UILApplication.java
 * @Prject: Mul2Launcher
 * @Package: com.sz.ead.framework.mul2launcher.application
 * @Description: Application对象
 * @author: zhaoqy
 * @date: 2015-4-22 下午5:03:22
 */

package com.sz.ead.framework.mul2launcher.application;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.sz.ead.framework.mul2launcher.R;
import com.sz.ead.framework.mul2launcher.appstore.downloader.DownloadManager;

public class UILApplication extends Application
{
	public static ImageLoader mImageLoader; //图片下载管理
	private HomeKeyEventBroadCastReceiver mHomeKeyEventBroadCastReceiver;
	public static boolean isSwitchToHome;
	
	@Override
	public void onCreate() 
	{
		super.onCreate();
		mHomeKeyEventBroadCastReceiver = new HomeKeyEventBroadCastReceiver();  
		registerReceiver(mHomeKeyEventBroadCastReceiver, new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)); 
	}
	
	@Override
	public void onTerminate() 
	{
		super.onTerminate();
		unregisterReceiver(mHomeKeyEventBroadCastReceiver);
	}
	
	/**
	 * 
	 * @Title: initService
	 * @Description: 初始化各类服务(广告服务 图片下载服务 BI服务 ppp服务)
	 * @param context
	 * @return: void
	 */
	public static void initService(Context context)
	{
		initImageLoaderService(context);  //初始化图片下载服务
		getDownLoadInstance(context);
	}
	
	/**
	 * 
	 * @Title: unInitService
	 * @Description: 反初始化各类服务
	 * @return: void
	 */
	public static void unInitService()
	{
		unInitImageLoaderService();
	}
	
	/**
	 * 
	 * @Title: initImageLoaderService
	 * @Description: 初始化图片下载服务
	 * @param context
	 * @return: void
	 */
	public static void initImageLoaderService(Context context)
	{
		if(!ImageLoader.getInstance().isInited())
		{
			initImageLoader(context.getApplicationContext());
			mImageLoader = ImageLoader.getInstance();
		}
	}
	
	/**
	 * 
	 * @Title: initImageLoader
	 * @Description: 初始化图片下载器
	 * @param context
	 * @return: void
	 */
	public static void initImageLoader(Context context) 
	{
		/*
		 * This configuration tuning is custom. You can tune every option, 
		 * you may tune some of them, or you can create default configuration 
		 * by ImageLoaderConfiguration.createDefault(this); 
		 */
		
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
				.threadPriority(Thread.NORM_PRIORITY - 2)
				.denyCacheImageMultipleSizesInMemory()
				.discCacheFileNameGenerator(new Md5FileNameGenerator())
				.tasksProcessingOrder(QueueProcessingType.LIFO)
				.discCacheSize(10*1024*1024)
				//.enableLogging() 
				.build();
		ImageLoader.getInstance().init(config);
	}
	
	/**
	 * 
	 * @Title: unInit
	 * @Description: 反初始化图片下载服务,清除缓存
	 * @return: void
	 */
	public static void unInitImageLoaderService()
	{
		if(mImageLoader != null)
		{
			mImageLoader.clearMemoryCache();
			//mImageLoader.clearDiscCache();
		}	
	}
	
	//应用图标选项
	public static DisplayImageOptions mAppIconOption = new DisplayImageOptions.Builder()
                                                   .showStubImage(R.drawable.mainapp_appitem_deault)
                                                   .showImageForEmptyUri(R.drawable.mainapp_appitem_deault)
                                                   .showImageOnFail(R.drawable.mainapp_appitem_deault)
                                                   .cacheInMemory()
                                                   .cacheOnDisc()
                                                   .displayer(new SimpleBitmapDisplayer())
                                                   .build();
	
	protected class HomeKeyEventBroadCastReceiver extends BroadcastReceiver {

		static final String SYSTEM_REASON = "reason";
		static final String SYSTEM_HOME_KEY = "homekey";//home key
		static final String SYSTEM_RECENT_APPS = "recentapps";//long home key
		
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
				String reason = intent.getStringExtra(SYSTEM_REASON);
				if (reason != null) {
					if (reason.equals(SYSTEM_HOME_KEY)) {
						isSwitchToHome = true;
					} else if (reason.equals(SYSTEM_RECENT_APPS)) {
						// long home key处理点
					}
				}
			}
		}
	}
	
	public static void UnInitGobalInfo(Context context){
		if(mDownloadManage!=null){
			mDownloadManage.stopAllDownload();
			mDownloadManage=null;
		}	
	}
	
	private static DownloadManager mDownloadManage=null;
	/*下载管理实例*/
	public static  DownloadManager getDownLoadInstance(Context context){
		//Log.i(TAG,"=======mDownloadManage===is=======enter===");
		if(mDownloadManage==null){
			if(true)
      			Log.i("UILApplication","=======mDownloadManage===is=======null===");
			mDownloadManage=new DownloadManager(context);	
		}else{
			/*if(true)
      			Log.i("UILApplication","=======mDownloadManage===is====not===null===");*/
		}
		return mDownloadManage;
	}
}
