/**
 * Copyright © 2015GreatVision. All rights reserved.
 *
 * @Title: AppDetailsActivity.java
 * @Prject: Mul2Launcher
 * @Package: com.sz.ead.framework.mul2launcher.appstore
 * @Description: TODO
 * @date: 2015-4-23 下午3:43:19
 */
package com.sz.ead.framework.mul2launcher.appstore;

import java.io.File;
import java.util.ArrayList;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.notice.noticemanager.Notice;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.sz.ead.framework.mul2launcher.BaseActivity;
import com.sz.ead.framework.mul2launcher.R;
import com.sz.ead.framework.mul2launcher.application.UILApplication;
import com.sz.ead.framework.mul2launcher.appstore.common.InstalledAppsScan;
import com.sz.ead.framework.mul2launcher.appstore.dialog.AppInfoConstant;
import com.sz.ead.framework.mul2launcher.appstore.dialog.PublicFun;
import com.sz.ead.framework.mul2launcher.appstore.downloader.DownConstants;
import com.sz.ead.framework.mul2launcher.appstore.downloader.DownloadInfo;
import com.sz.ead.framework.mul2launcher.appstore.downloader.InstallInfo;
import com.sz.ead.framework.mul2launcher.dataprovider.dataitem.AppItem;
import com.sz.ead.framework.mul2launcher.dataprovider.dataitem.ScreenShotItem;
import com.sz.ead.framework.mul2launcher.db.DownLoadDB;
import com.sz.ead.framework.mul2launcher.db.InstallingTable;
import com.sz.ead.framework.mul2launcher.settings.util.FileUtils;
import com.sz.ead.framework.mul2launcher.status.Statusbar;
import com.szgvtv.ead.framework.bi.Bi;


public class AppDetailsActivity extends BaseActivity {
	
	public static final String ToAppDetailKey = "to_app_detail_key";	// APPStore传递应用详情参数关键字
	public static final String TAG = "AppstoreDetail";
	public static final String mScreenShotPath = "/data/screenShot/";	// 截图保存路径
	
	public static final String mAppIconPath = "/data/appicon/";	// 图标保存路径
	private AppItem mAppDetails = null;
	
	private ImageView mImageViewLeft;		// 左侧截图
	private ImageView mImageViewMid;		// 中间截图
	private ImageView mImageViewRight;		// 右边截图
	private ImageView mImageViewBack;		// 背景截图
	
	private ImageView mAppIcon;			// 应用图标
	private TextView mAppName;			// 应用名称
	private TextView mAppSize;			// 应用大小
	private TextView mAppVersion;		// 应用显示版本
	private TextView mAppDeveloper;		// 应用开发商
	private TextView mAppIntro;			// 应用简介
	
	private ImageView mAppImgFocus;		// 截图光标
	private ImageView mAppMoveLeft;		// 截图左箭头
	private ImageView mAppMoveRight;	// 截图右箭头
	
	private boolean mIsOnlyOne;
	private TextView mAppDownloadingTv;	// 正在下载文字
	
	private ProgressBar mAppDownloadingBar;
	ArrayList<ScreenShotItem> mImageList; //截图列表
	private Drawable[] mDrawableList;
	
	ArrayList<String> mImagePathList; //截图地址
	
	private Animation left_to_mid, mid_to_right, right_to_left, back_to_left,right_to_back;	// 向左移动动画
	private Animation right_to_mid, mid_to_left, left_to_right, back_to_right,left_to_back;	// 向右移动动画
	
	private int mSelectIndex;	// 当前选中值
	private int mTotalNum;		// 截图总个数
	
	private Button mAppDownload;	// 下载（运行）按钮
	
	private Statusbar mStatusBar;	// 状态栏
	
	protected ImageLoader imageLoader = ImageLoader.getInstance();	// 图片加载控件
	
	private AppStatusIntentReceiver mAppStatusReceiver = new AppStatusIntentReceiver();	// 应用下载安装状态广播接收
	
	private FOCUS mFocus;	// 焦点
	enum FOCUS
	{
		FOCUS_DOWNLOAD_BTN,
		FOCUS_SCREENSHOT_IMG
	};
	
	private Handler mHandler = new Handler() 
	{
		public void handleMessage(Message msg) 
		{
			switch (msg.what) 
			{
			case DownConstants.MSG_DOWN_PROGRESS:		// 下载进度
				freshDownProgress(msg);
				break;
			case DownConstants.MSG_INSTALL_PROGRESS:	// 安装进度
				freshInstallProgress(msg);
				break;
			case DownConstants.MSG_INSTALL_SUCCESS:		// 安装成功
				break;
			case DownConstants.MSG_SCREENSHOT_SUCCESS:	// 截图加载完成
				initData();
				break;
			default:
				break;
			}
		};
	};
	
	// 中间的截图
	private DisplayImageOptions mScreenShotMidDefault = new DisplayImageOptions.Builder()
	.showStubImage(R.drawable.appstore_detail_big_default)
	.showImageForEmptyUri(R.drawable.appstore_detail_big_default)
	.showImageOnFail(R.drawable.appstore_detail_big_default)
	.cacheInMemory()
	.cacheOnDisc()
	.displayer(new SimpleBitmapDisplayer()) 
	.build();
	
	// 应用图标
	private DisplayImageOptions mAppIconDefault = new DisplayImageOptions.Builder()
	.showStubImage(R.drawable.appstore_detail_appicon_default)
	.showImageForEmptyUri(R.drawable.appstore_detail_appicon_default)
	.showImageOnFail(R.drawable.appstore_detail_appicon_default)
	.cacheInMemory()
	.cacheOnDisc()
	.displayer(new SimpleBitmapDisplayer()) 
	.build();
	
	// 应用下载安装状态广播接收
	private class AppStatusIntentReceiver extends BroadcastReceiver 
	{
		public void onReceive(Context context, Intent intent) 
		{
			Logd("receive:"+intent.getAction());
			
			if (intent.getAction().equals(DownConstants.DOWN_STATUS_ACTION)) 
			{
				String str = null;
				try {
					str = intent.getStringExtra(DownConstants.DOWN_STATUS_DOWNLOAD_STATUS_KEY);
				} 
				catch (Exception e) 
				{
					e.printStackTrace();
				}
				
				if (str != null) 
				{
					freshBtnUI();		// 刷新界面
				}
			}
		}
	}
	
	private void registerIntentReceivers() 
	{
		IntentFilter filter = new IntentFilter();
		filter.addAction(DownConstants.DOWN_STATUS_ACTION);
		registerReceiver(mAppStatusReceiver, filter);
	}

	private void unregisterIntentReceivers() 
	{
		if (mAppStatusReceiver != null) 
		{
			unregisterReceiver(mAppStatusReceiver);
			mAppStatusReceiver = null;
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.appstore_app_details);
		
		getAppDetailInfo();		// 获取应用信息
		findViews();
		initScreenShot();		// 下载截图
		
		InstalledAppsScan.scanApp(getApplicationContext());
		InstallingTable.freshInstalledTable(this);
		
		mSelectIndex = 0;
		initData();
		initAnim();
		freshBtnUI();
		
		registerIntentReceivers();
	}
	
	public void findViews()
	{
		mAppIcon = (ImageView) findViewById(R.id.appstore_detail_app_icon);
		mAppName = (TextView) findViewById(R.id.appstore_detail_app_name);
		mAppSize = (TextView) findViewById(R.id.appstore_detail_app_size);
		mAppVersion = (TextView) findViewById(R.id.appstore_detail_app_version);
		mAppDeveloper = (TextView) findViewById(R.id.appstore_detail_app_developer);
		mAppIntro = (TextView) findViewById(R.id.appstore_detail_app_intro);
		
		mAppImgFocus = (ImageView) findViewById(R.id.appstore_detail_screenshot_focus);
		
		mAppMoveLeft = (ImageView) findViewById(R.id.appstore_detail_move_left);
		mAppMoveRight = (ImageView) findViewById(R.id.appstore_detail_move_right);
		
		mAppDownloadingBar = (ProgressBar) findViewById(R.id.appstore_detail_progressbar);
		mAppDownloadingTv = (TextView) findViewById(R.id.appstore_detail_downloading);
		
		mImageViewLeft = (ImageView) findViewById(R.id.appstore_detail_select_left);
        mImageViewMid = (ImageView) findViewById(R.id.appstore_detail_select_mid);
        mImageViewRight = (ImageView) findViewById(R.id.appstore_detail_select_right);
        mImageViewBack = (ImageView) findViewById(R.id.appstore_detail_select_back);
        
        mStatusBar = (Statusbar) findViewById(R.id.llyt_main_header);
		mStatusBar.setTaskBarTitle(getResources().getString(R.string.appstore_detail));
        
        mAppDownload = (Button) findViewById(R.id.appstore_btn_download);
        mAppDownload.requestFocus();
        mFocus = FOCUS.FOCUS_DOWNLOAD_BTN;
        
        if (mHandler != null) 
        {
			UILApplication.getDownLoadInstance(this).addHandle(mHandler);
		}
		
        if (UILApplication.mImageLoader != null)
		{
			UILApplication.mImageLoader.displayImage(mAppDetails.getIcon(), mAppIcon, 
					mAppIconDefault);
		}
        
		mAppName.setText(mAppDetails.getAppName());
		mAppSize.setText(this.getResources().getString(R.string.appstore_detail_size)
				+ mAppDetails.getSize());
		mAppVersion.setText(this.getResources().getString(R.string.appstore_detail_version)
				+ mAppDetails.getShowVersion());
		mAppDeveloper.setText(this.getResources().getString(R.string.appstore_detail_developer)
				+ mAppDetails.getDeveloper());
		mAppIntro.setText(this.getResources().getString(R.string.appstore_detail_intro)
				+ mAppDetails.getSummary());
	}
	
	// 显示截图，如果不存在，显示默认图片
	public void setImage(ImageView imageview, Drawable drawable)
	{
		if (null == drawable)
		{
			imageview.setImageDrawable(this.getResources().getDrawable(R.drawable.appstore_detail_big_default));
		}
		else
		{
			imageview.setImageDrawable(drawable);
		}
	}
	
	public void initData()
	{		
		// 加载左边截图
		if (mSelectIndex == 0)
		{
			setImage(mImageViewLeft,mDrawableList[mTotalNum-1]);
		}
		else
		{
			setImage(mImageViewLeft,mDrawableList[mSelectIndex-1]);
		}
		
		// 加载中间截图
		setImage(mImageViewMid,mDrawableList[mSelectIndex]);
		
		// 加载右边截图
		if((mSelectIndex+1) > (mTotalNum-1))
		{
			setImage(mImageViewRight,mDrawableList[mSelectIndex + 1-mTotalNum]);
		}
		else
		{
			setImage(mImageViewRight,mDrawableList[mSelectIndex+1]);
		}
		
		// 加载背景截图
		if((mSelectIndex+mTotalNum -2)>(mTotalNum-1))
		{
			setImage(mImageViewBack,mDrawableList[mSelectIndex -2]);
		}
		else
		{
			setImage(mImageViewBack,mDrawableList[mSelectIndex+mTotalNum -2]);
		}
		
		// 如果只有一张图片，隐藏左右箭头等图片
		if (mIsOnlyOne)
		{
			mImageViewBack.setVisibility(View.GONE);
			mImageViewRight.setVisibility(View.GONE);
			mImageViewLeft.setVisibility(View.GONE);
			mAppMoveLeft.setVisibility(View.GONE);
			mAppMoveRight.setVisibility(View.GONE);
		}
		
	}
	
	private void initAnim()
	{
		// 从左往右移动
		left_to_mid = AnimationUtils.loadAnimation(this,
				R.anim.appstore_detail_left_to_mid);
		left_to_mid.setFillAfter(true);
		left_to_mid.setFillEnabled(true);
		
		mid_to_right = AnimationUtils.loadAnimation(this,
				R.anim.appstore_detail_mid_to_right);
		mid_to_right.setFillAfter(true);
		mid_to_right.setFillEnabled(true);
		
		right_to_left = AnimationUtils.loadAnimation(this,
				R.anim.appstore_detail_right_to_left);
		right_to_left.setFillAfter(true);
		right_to_left.setFillEnabled(true);
		
		back_to_left = AnimationUtils.loadAnimation(this,
				R.anim.appstore_detail_back_to_left);
		back_to_left.setFillAfter(true);
		back_to_left.setFillEnabled(true);
		
		right_to_back = AnimationUtils.loadAnimation(this,
				R.anim.appstore_detail_right_to_back);
		right_to_back.setFillAfter(true);
		right_to_back.setFillEnabled(true);
		
		// --------------------------------
		// 从右往左移动
		right_to_mid = AnimationUtils.loadAnimation(this,
				R.anim.appstore_detail_right_to_mid);
		right_to_mid.setFillAfter(true);
		right_to_mid.setFillEnabled(true);
		
		mid_to_left = AnimationUtils.loadAnimation(this,
				R.anim.appstore_detail_mid_to_left);
		mid_to_left.setFillAfter(true);
		mid_to_left.setFillEnabled(true);
		
		left_to_right = AnimationUtils.loadAnimation(this,
				R.anim.appstore_detail_left_to_right);
		left_to_right.setFillAfter(true);
		left_to_right.setFillEnabled(true);
		
		back_to_right = AnimationUtils.loadAnimation(this,
				R.anim.appstore_detail_back_to_right);
		back_to_right.setFillAfter(true);
		back_to_right.setFillEnabled(true);
		
		left_to_back = AnimationUtils.loadAnimation(this,
				R.anim.appstore_detail_left_to_back);
		left_to_back.setFillAfter(true);
		left_to_back.setFillEnabled(true);
    }
	
	public void initScreenShot()
	{
		String path = mScreenShotPath + mAppDetails.getPkgName();
		
		int size = mAppDetails.getImages().size();
		
		mImagePathList = new ArrayList<String>();
		mDrawableList =  new Drawable[size];
		
		// 创建截图文件保存路径
		createDir(mScreenShotPath);
		
		// 以包名命名
		createDir(path);
		
		//String iconPath = mAppIconPath + mAppDetails.getPkgName() + ".png";
		
		
		for (int i=0; i < size; i++)
		{
			mDrawableList[i] = null;
			String url = mAppDetails.getImages().get(i).getImageUrl();
						
			String splistr[] = url.split("/");
			String name = splistr[splistr.length-1];
			
			//保存截图路径，以包名为文件夹名称，url为文件名称
			String pkgNamePath = path  + "/"+ name;
			
			
			File f = new File(pkgNamePath);
			
			if (f.exists() && !url.equals(""))
			{
				 try{
					 mImagePathList.add(i, pkgNamePath);	// 截图列表
					 
					 // 解析本地图片
					 CreateDrawableThread draw = new CreateDrawableThread(i, path, name);
					 draw.start();
				 }catch(java.lang.OutOfMemoryError e){
				    //TODO 替代方案
					   Logd("java.lang.OutOfMemoryError e ");
				   }
			}
			else
			{
				Logd("not exist");
				 try{
				     //load big memory data
					 mImagePathList.add(i, "");			// 不存在该图片文件，以空代替，然后去下载截图
					 imageLoader.loadImage(url,mScreenShotMidDefault, new ImageListener());
				   }catch(java.lang.OutOfMemoryError e){
				    //TODO 替代方案
					   Logd("java.lang.OutOfMemoryError e ");
				   }
			}
			
		}
	}
	
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) 
	{
		if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_UP && event.getAction() == KeyEvent.ACTION_DOWN)
		{
			doKeyUp();
		}
		else if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_DOWN && event.getAction() == KeyEvent.ACTION_DOWN)
		{
			doKeyDown();
		}
		else if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT && event.getAction() == KeyEvent.ACTION_DOWN)
		{
			doKeyRight();
			return true;
		}
		else if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT && event.getAction() == KeyEvent.ACTION_DOWN)
		{
			doKeyLeft();
			return true;
		}
		else if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_CENTER && event.getAction() == KeyEvent.ACTION_DOWN)
		{
			doKeyOK();
			return true;
		}
		
		return super.dispatchKeyEvent(event);
	}
	
	public void doKeyOK()
	{
		if (mFocus == FOCUS.FOCUS_DOWNLOAD_BTN && 
				mAppDownload.getText().equals(this.getResources().
						getString(R.string.appstore_detail_down_btn))) // 焦点在下载按钮上
		{
			startDownload(0);
		}
		else if (mFocus == FOCUS.FOCUS_DOWNLOAD_BTN && 
				mAppDownload.getText().equals(this.getResources().
						getString(R.string.appstore_detail_start_btn)))	// 焦点在下载运行上
		{
			PackageManager packageManager = getPackageManager();
			Intent intent = packageManager
					.getLaunchIntentForPackage(mAppDetails.getPkgName());
			if (intent != null) 
			{
				startActivity(intent);							// 启动应用

				Bi.sendBiMsg(Bi.BICT_FIRMWARE, Bi.BICC_APP_START,
						mAppDetails.getAppCode(), mAppDetails.getVersion(),
						PublicFun.getCurrentLauguage(this), "", "0");
			} 
			else 
			{
				Bi.sendBiMsg(Bi.BICT_FIRMWARE, Bi.BICC_APP_START,
						mAppDetails.getAppCode(), mAppDetails.getVersion(),
						PublicFun.getCurrentLauguage(this), "", "1");
				Notice notice = Notice.makeNotice(this, getResources().getString(R.string.appstore_detail_app_cannot_start),
						Notice.LENGTH_SHORT);
				notice.cancelAll();
				notice.show();
			}
		}
	}
		
	public void doKeyLeft()
	{
		if (mFocus == FOCUS.FOCUS_SCREENSHOT_IMG && !mIsOnlyOne)
		{
			setImageViewPic(0);
			mImageViewLeft.startAnimation(left_to_mid);
			mImageViewMid.startAnimation(mid_to_right);
			mImageViewRight.startAnimation(right_to_back);
			mImageViewBack.startAnimation(back_to_left);
						
			mid_to_right.setAnimationListener(new AnimationListener() {
				
				@Override
				public void onAnimationStart(Animation animation) {
					// TODO Auto-generated method stub
					
					mImageViewLeft.bringToFront();
					mImageViewMid.bringToFront();
					
					mAppMoveLeft.bringToFront();
					mAppMoveRight.bringToFront();
				}
				
				@Override
				public void onAnimationRepeat(Animation animation) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onAnimationEnd(Animation animation) {
					// TODO Auto-generated method stub
										
					mImageViewLeft.bringToFront();
					mAppImgFocus.bringToFront();
					
					mAppMoveLeft.bringToFront();
					mAppMoveRight.bringToFront();
				}
			});
			
			mSelectIndex = mSelectIndex -1;
			if(mSelectIndex < 0)
			{
				mSelectIndex = mTotalNum -1;
			}
		}
	}
	
	public void doKeyRight()
	{
		if (mFocus == FOCUS.FOCUS_SCREENSHOT_IMG && !mIsOnlyOne)
		{
			setImageViewPic(1);
			
			mImageViewRight.startAnimation(right_to_mid);
			mImageViewMid.startAnimation(mid_to_left);
			mImageViewLeft.startAnimation(left_to_back);
			mImageViewBack.startAnimation(back_to_right);
			
			mid_to_left.setAnimationListener(new AnimationListener() {
				
				@Override
				public void onAnimationStart(Animation animation) {
					// TODO Auto-generated method stub
					
					mImageViewRight.bringToFront();
					mImageViewMid.bringToFront();
					
					mAppMoveLeft.bringToFront();
					mAppMoveRight.bringToFront();
				}
				
				@Override
				public void onAnimationRepeat(Animation animation) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onAnimationEnd(Animation animation) {
					// TODO Auto-generated method stub
					
					mImageViewRight.bringToFront();
					mAppImgFocus.bringToFront();
					
					mAppMoveLeft.bringToFront();
					mAppMoveRight.bringToFront();
				}
			});
			
			mSelectIndex = mSelectIndex + 1;
			if(mSelectIndex > mTotalNum -1)
			{
				mSelectIndex = 0;
			}
		}
	}

	
	public void doKeyUp()
	{
		if (mFocus == FOCUS.FOCUS_SCREENSHOT_IMG && mAppDownload.isShown())	// 应用正在下载过程中，不允许向上
		{
			mAppDownload.requestFocus();
			mFocus = FOCUS.FOCUS_DOWNLOAD_BTN;
			mAppImgFocus.setVisibility(View.GONE);
		}
	}
	
	public void doKeyDown()
	{
		if (mFocus == FOCUS.FOCUS_DOWNLOAD_BTN)
		{
			mAppImgFocus.requestFocus();
			mFocus = FOCUS.FOCUS_SCREENSHOT_IMG;
			mAppImgFocus.setVisibility(View.VISIBLE);
		}
	}
	
	// 设置显示图片
	public void setImageViewPic(int flag)
	{
		if(flag == 0)
		{
			if((mSelectIndex-2)<0)
			{
				setImage(mImageViewBack,mDrawableList[mTotalNum+mSelectIndex-2]);
			}
			else
			{
				setImage(mImageViewBack,mDrawableList[mSelectIndex-2]);
			}
		}
		else
		{
			if((mSelectIndex+2) > (mTotalNum-1))
			{
				setImage(mImageViewBack,mDrawableList[mSelectIndex + 2-mTotalNum]);
			}
			else
			{
				setImage(mImageViewBack,mDrawableList[mSelectIndex + 2]);
			}
		}
		
		if ((mSelectIndex-1)<0)
		{
			setImage(mImageViewLeft,mDrawableList[mTotalNum+mSelectIndex-1]);
		}
		else
		{
			setImage(mImageViewLeft,mDrawableList[mSelectIndex-1]);
		}

		
		setImage(mImageViewMid,mDrawableList[mSelectIndex]);
		
		if((mSelectIndex+1) > (mTotalNum-1))
		{
			setImage(mImageViewRight,mDrawableList[mSelectIndex + 1-mTotalNum]);
		}
		else
		{
			setImage(mImageViewRight,mDrawableList[mSelectIndex + 1]);
		}
	}
	
	// 开始下载
	private void startDownload(int downSort) 
	{
		if (mAppDetails != null) 
		{
			DownloadInfo iteminfo = new DownloadInfo(mAppDetails,
					PublicFun.getsAppSource());
			iteminfo.setAddDownListStyle(downSort);
			UILApplication.getDownLoadInstance(this.getApplicationContext())
					.addDownList(iteminfo);
		}
	}
	
	
	
	@SuppressWarnings("static-access")
	private void getAppDetailInfo() 
	{
		Bundle bundle = getIntent().getExtras();
		this.mAppDetails = bundle.getParcelable(this.ToAppDetailKey);
		
		try {
			int biinfo = Bi.sendBiMsg(Bi.BICT_FIRMWARE, Bi.BICC_APP_CLICK,
					mAppDetails.getAppCode(), mAppDetails.getVersion(),
					PublicFun.getCurrentLauguage(this),
					PublicFun.getsAppSource() + "",
					PublicFun.getsAppSourceId(), PublicFun.getsAppSourceName(),
					AppInfoConstant.APP_ID, PublicFun.getsVersionName());
			
			// 获取截图URL
			mImageList = mAppDetails.getImages();
			
			int size = mImageList.size();
			Logd("img url size" + mImageList.size());
			
			// 是否 没有截图或者只有一个截图
			if (1 == size || 0 == size)
			{
				mIsOnlyOne = true;
			}
			else
			{
				mIsOnlyOne = false;
			}
			
			// 不足3个截图，凑足3个，默认为空字符串
			if (size < 3)
			{
				for (int i = size; i < 3; i++)
				{
					ScreenShotItem object = new ScreenShotItem();
					object.setImageUrl("");
					mImageList.add(object);
				}
			}
			
			mTotalNum = mImageList.size();
			
			for (int i = 0; i < mImageList.size(); i++)
			{
				Logd("mImageList"+String.valueOf(i)+":"+mImageList.get(i).getImageUrl());
			}
			
			Logd("mImageList size" + mImageList.size());
			
			Logd("code=" + mAppDetails.getAppCode());
			Logd("name=" + mAppDetails.getAppName());
			Logd("developer=" + mAppDetails.getDeveloper());
			Logd("url=" + mAppDetails.getDownloadUrl());
			Logd("md5=" + mAppDetails.getMd5());
			Logd("pkgname=" + mAppDetails.getPkgName());
			Logd("showversion=" + mAppDetails.getShowVersion());
			Logd("size=" + mAppDetails.getSize());
			Logd("summary=" + mAppDetails.getSummary());
			Logd("version=" + mAppDetails.getVersion());
			Logd("icon=" + mAppDetails.getIcon());
			Logd("img size=" + size);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

	}
	
	// 刷新界面
	private void freshBtnUI() 
	{
		int status = DownConstants.STATUS_DOWN_UNKNOWN;
		if (mAppDetails != null) 
		{
			status = UILApplication.getDownLoadInstance(this).getAppStatus(
					mAppDetails.getAppCode(), mAppDetails.getPkgName());
		} 
		else 
		{
			return;
		}
		
		Logd("freshBtnUI:"+status);
		
		if (status == DownConstants.STATUS_APP_INSTALLED) 
		{
			mAppDownload.setVisibility(View.VISIBLE);
			mAppDownload.setText(this.getResources().getString(R.string.appstore_detail_start_btn));
			mAppDownloadingBar.setVisibility(View.INVISIBLE);
			mAppDownloadingTv.setVisibility(View.INVISIBLE);
			
			mAppDownload.requestFocus();
			mFocus = FOCUS.FOCUS_DOWNLOAD_BTN;
			mAppImgFocus.setVisibility(View.INVISIBLE);
		} 
		else if (status == DownConstants.STATUS_DOWN_DOWNING) 
		{
			mAppImgFocus.requestFocus();
			mFocus = FOCUS.FOCUS_SCREENSHOT_IMG;
			mAppImgFocus.setVisibility(View.VISIBLE);
			
			mAppDownload.setVisibility(View.INVISIBLE);
			mAppDownloadingBar.setVisibility(View.VISIBLE);
			mAppDownloadingTv.setVisibility(View.VISIBLE);
			DownloadInfo downloadInfo = (DownloadInfo) DownLoadDB
					.queryADownRecord(mAppDetails.getPkgName());
			if (downloadInfo != null) 
			{
				Logd(downloadInfo.getDownTotalBytes() + "  "
								+ downloadInfo.getDownCurrentBytes());
				mAppDownloadingBar.setMax(downloadInfo.getDownTotalBytes());
				mAppDownloadingBar.setProgress(downloadInfo.getDownCurrentBytes());
				mAppDownloadingTv.setText(getResources().getString(
						R.string.appstore_detail_downing_tips)
						+ getPrecent(downloadInfo.getDownCurrentBytes(),
								downloadInfo.getDownTotalBytes()));
			}
		} 
		else if (status == DownConstants.STATUS_DOWN_DOWNCOMPLETE
				|| status == DownConstants.STATUS_DOWN_INSTALLING) 
		{
			mAppDownload.setVisibility(View.INVISIBLE);
			mAppDownloadingBar.setVisibility(View.VISIBLE);
			mAppDownloadingTv.setVisibility(View.VISIBLE);
		} 
		else if (status == DownConstants.STATUS_DOWN_BREAKPOINT) 
		{
			// 下载失败
			if (mAppDetails != null
					&& !TextUtils.isEmpty(mAppDetails.getDownloadUrl())) 
			{
				mAppDownload.setText(this.getResources().getString(R.string.appstore_detail_down_btn));
				mAppDownload.setVisibility(View.VISIBLE);
			}
			mAppDownloadingBar.setVisibility(View.INVISIBLE);
			mAppDownloadingTv.setVisibility(View.INVISIBLE);
		} 
		else 
		{
			if (mAppDetails != null
					&& !TextUtils.isEmpty(mAppDetails.getDownloadUrl())) 
			{
			
				mAppDownload.setText(this.getResources().getString(R.string.appstore_detail_down_btn));
				mAppDownload.setVisibility(View.VISIBLE);
			}
			mAppDownloadingBar.setVisibility(View.INVISIBLE);
			mAppDownloadingTv.setVisibility(View.INVISIBLE);
			mAppDownload.requestFocus();
			mFocus = FOCUS.FOCUS_DOWNLOAD_BTN;
			mAppImgFocus.setVisibility(View.INVISIBLE);
		}
	}
	
	// 刷新下载界面
	private void freshDownProgress(Message msg) 
	{
		DownloadInfo downloadInfo = (DownloadInfo) msg.obj;
		if (downloadInfo != null) {
			mAppDownloadingBar.setMax(downloadInfo.getDownTotalBytes());
			mAppDownloadingBar.setProgress(downloadInfo.getDownCurrentBytes());
			mAppDownloadingTv.setText(getResources().getString(
					R.string.appstore_detail_downing_tips)
					+ getPrecent(downloadInfo.getDownCurrentBytes(),
							downloadInfo.getDownTotalBytes()));
		}
	}
	
	// 刷新安装界面
	private void freshInstallProgress(Message msg) 
	{
		InstallInfo installInfo = (InstallInfo) msg.obj;
		if (installInfo != null) 
		{
			mAppDownloadingBar.setMax(installInfo.getTotalProgess());
			mAppDownloadingBar.setProgress(installInfo.getCurrentProgess());
			mAppDownloadingTv.setText(getResources().getString(
					R.string.appstore_detail_install_tips)
					+ getPrecent(installInfo.getCurrentProgess(),
							installInfo.getTotalProgess()));
		}
	}
	
	public void Logd(String msg)
	{
		Log.d(TAG, msg);
	}
	
	/**
	 * 得到下载百分比
	 * 
	 * @author liyingying
	 * @param cur
	 *            当前下载大小
	 * @param total
	 *            总大小
	 * @return 百分比
	 */
	public static String getPrecent(int cur, int total) 
	{
		double perFloat = cur / (total * 1.0);
		StringBuffer sb = new StringBuffer();
		int perInt;
		if (perFloat >= 0.0 && perFloat <= 1.0) 
		{
			perInt = (int) (perFloat * 100);
		} 
		else 
		{
			// total为负数
			perInt = 0;
		}
		sb.append(perInt);
		sb.append("%");
		return sb.toString();
	}
	
	/**
	 * 文件是否存在
	 * @param pathAndfileName
	 * @return
	 */
    private boolean fileIsExists(String pathAndfileName)
    {
        try{
            File f = new File(pathAndfileName);
            if (!f.exists())
            {
                 return false;
            }
        }
        catch (Exception e) {
                // TODO: handle exception
                return false;
        }
        return true;
    }
    
    /**
     * 创建文件夹
     * @param dirName
     */
    private void createDir(String dirName) 
    {
    	if(!fileIsExists(dirName))
    	{
    		File dir = new File(dirName);
    		boolean result = dir.mkdir();
    		Logd("create " + dirName + result);
    	}
	}
    
    class AppIconListener extends SimpleImageLoadingListener{  
		  
	    @Override  
	    public void onLoadingStarted(String imageUri, View view) {  
	       // imageView.setImageResource(R.drawable.loading); 
	        super.onLoadingStarted(imageUri, view);  
	    }  
	  
	    @Override  
	    public void onLoadingFailed(String imageUri, View view,  
	            FailReason failReason) {  
	       // imageView.setImageResource(R.drawable.no_pic);  
	    	
	        super.onLoadingFailed(imageUri, view, failReason);  
	    }  
	  
	    @Override  
	    public void onLoadingComplete(String imageUri, View view,  
	            Bitmap loadedImage) {  
	    	Logd("appIconUri:"+imageUri);
	    	if (loadedImage != null)
	    	{
	    		setAppIconImg(imageUri,loadedImage);
	    	}
	    	
	      //  imageView.setImageDrawable(loadedImage);  
	        super.onLoadingComplete(imageUri, view, loadedImage);  
	    }  
	  
	    @Override  
	    public void onLoadingCancelled(String imageUri, View view) {  
	     //   imageView.setImageResource(R.drawable.cancel);  
	        super.onLoadingCancelled(imageUri, view);  
	    }
	}
    
    // 保存图标
    private void setAppIconImg(String imageUri,Bitmap loadeImage)
	{
    	
    	String iconPath = mAppIconPath + mAppDetails.getPkgName() + ".png";
    	
    	if (!fileIsExists(iconPath))
    	{
    		Log.d(TAG, "appicon not exist:"+iconPath);
    		FileUtils.saveMyBitmap(mAppIconPath,mAppDetails.getPkgName() + ".png",loadeImage);
    	}
    	else
    	{
    		Log.d(TAG, "appicon exist:"+iconPath);
    	}
	}
    
    class ImageListener extends SimpleImageLoadingListener{  
		  
	    @Override  
	    public void onLoadingStarted(String imageUri, View view) {  
	       // imageView.setImageResource(R.drawable.loading); 
	        super.onLoadingStarted(imageUri, view);  
	    }  
	  
	    @Override  
	    public void onLoadingFailed(String imageUri, View view,  
	            FailReason failReason) {  
	       // imageView.setImageResource(R.drawable.no_pic);  
	    	
	        super.onLoadingFailed(imageUri, view, failReason);  
	    }  
	  
	    @Override  
	    public void onLoadingComplete(String imageUri, View view,  
	            Bitmap loadedImage) {  
	    	
	    	if (loadedImage != null)
	    	{
	    		setSuccessImg(imageUri,loadedImage);
	    	}
	    	
	      //  imageView.setImageDrawable(loadedImage);  
	        super.onLoadingComplete(imageUri, view, loadedImage);  
	    }  
	  
	    @Override  
	    public void onLoadingCancelled(String imageUri, View view) {  
	     //   imageView.setImageResource(R.drawable.cancel);  
	        super.onLoadingCancelled(imageUri, view);  
	    }
	}
    
    // 保存截图
    private void setSuccessImg(String imageUri,Bitmap loadeImage)
	{
		int count = mImageList.size();
		for(int i=0;i<count;i++)
		{
			if (mImageList.get(i).getImageUrl().equals(imageUri))
			{
				String splistr[] = imageUri.split("/");
				String name = splistr[splistr.length-1];
				
				mImagePathList.add(i, mScreenShotPath + mAppDetails.getPkgName() + name);

				// 保存和解析本地图片
				CreateDrawableThread draw = new CreateDrawableThread(i,mScreenShotPath + mAppDetails.getPkgName(),name);
				draw.saveBitmap(loadeImage);
				draw.start();

				return;
			}
		}
	}
    
    public void removeDrawable()
    {
		for(int i=0; i<mTotalNum; i++)
		{
			if(null != mDrawableList[i])
			{
				
				BitmapDrawable bd = (BitmapDrawable) mDrawableList[i];
				Bitmap bm = bd.getBitmap();
				
				if (bm != null && !bm.isRecycled()) 
				{ 
					bm.recycle();
				    System.gc();
					bm=null;
				}     
				mDrawableList[i] = null;
				
			}
		}		
	}
    
    @Override
	protected void onDestroy() 
	{
		// TODO Auto-generated method stub
		unregisterIntentReceivers();
		PublicFun.setsAppSource(PublicFun.SOURCE_NULL);
		if (mHandler != null) 
		{
			UILApplication.getDownLoadInstance(this).delAHandle(mHandler);
		}		
		removeDrawable();
		
		super.onDestroy();
	}
    
    // 保存和解析图片
 	public class CreateDrawableThread extends Thread
 	{
 		private String mScreenShotPath;
 	    private String mPkgNamePath;
 	    private int mIndex;
 	    private String mName;
 	    private Bitmap mLoadeImage;
 	    private boolean isSave = false;	// 是否下载保存图片
 	    
 	    public CreateDrawableThread(int index,String packageNamePath, String name)
 	    {
 	    	mIndex = index;
 	    	mPkgNamePath = packageNamePath;
 	    	mName = name;
 	    	mScreenShotPath = packageNamePath + "/" + name;
 	    }
 	    
 	    public void saveBitmap(Bitmap loadeImage)
 	    {
 	    	isSave = true;
 	    	mLoadeImage = loadeImage;
 	    }
 	    
 	    public void run()
 	    {
 	    	if (isSave)
 	    	{
 	    		// 保存截图
 	    		FileUtils.saveMyBitmap(mPkgNamePath,mName,mLoadeImage);
 	    	}
 	    	
 	    	// 通过截图路径生成Drawable格式
 	    	mDrawableList[mIndex] = BitmapDrawable.createFromPath(mScreenShotPath);
 	    	mHandler.sendEmptyMessage(DownConstants.MSG_SCREENSHOT_SUCCESS);
 	    }
 	}
}

