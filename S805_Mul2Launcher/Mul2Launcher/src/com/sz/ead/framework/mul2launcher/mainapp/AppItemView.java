/**
 * Copyright © 2015GreatVision. All rights reserved.
 *
 * @Title: AppItemView.java
 * @Prject: Mul2Launcher
 * @Package: com.sz.ead.framework.mul2launcher.mainapp
 * @Description: appItem
 * @author: zhaoqy  
 * @date: 2015-4-23 下午4:34:33
 */
package com.sz.ead.framework.mul2launcher.mainapp;

import com.sz.ead.framework.mul2launcher.R;
import com.sz.ead.framework.mul2launcher.application.UILApplication;
import com.sz.ead.framework.mul2launcher.dataprovider.dataitem.AppItem;
import com.sz.ead.framework.mul2launcher.db.InstalledTable;
import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class AppItemView extends RelativeLayout 
{
	public static final int APPITEM_SOURCE_MAINAPP = 0;  //我的应用
	public static final int APPITEM_SOURCE_APPSTORE = 1; //应用商城
	private OnAppClickListener mOnAppClickListener;
	private Context mContext;
	private ImageView mIcon;
	private ImageView mInstalled;
	private TextView mName;
	private AppItem mAppItem;
	private int mAppSource = 0;
	private int mIsInstall = 0;  //0-未安装; 1-已安装
	
	public interface OnAppClickListener
	{
		void OnAppClick(View v);
	}
	
	public void setOnAppClick(OnAppClickListener listen)
	{
		mOnAppClickListener = listen;
	}

	public AppItemView(Context context, AttributeSet attrs) 
	{
		super(context, attrs);
		mContext = context;
		init();
	}
	
	private void init()
	{
		inflate(mContext, R.layout.mainapp_appitemview, this);
		mIcon = (ImageView) findViewById(R.id.appitem_icon);
		mInstalled = (ImageView) findViewById(R.id.appitem_installed);
		mName = (TextView) findViewById(R.id.appitem_name);
	}
	
	/**
	 * 
	 * @Title: setAppItemSource
	 * @Description: 设置AppItem来源
	 * @param source
	 * @return: void
	 */
	public void setAppItemSource(int source)
	{
		mAppSource = source;
	}
	
	/**
	 * 
	 * @Title: setAppItem
	 * @Description: 设置AppItem
	 * @param appItem
	 * @return: void
	 */
	public void setAppItem(AppItem appItem)
	{
		mAppItem = appItem;
		switch (mAppSource) 
		{
		case APPITEM_SOURCE_MAINAPP:
		{
			mIcon.setImageDrawable(mAppItem.getIconBitmap());
			mName.setText(mAppItem.getAppName());
			break;
		}
		case APPITEM_SOURCE_APPSTORE:
		{
			if (UILApplication.mImageLoader != null)
			{
				UILApplication.mImageLoader.displayImage(mAppItem.getIcon(), mIcon, UILApplication.mAppIconOption);
			}
			setAppIsInstalled();
			mName.setText(mAppItem.getAppName());
			break;
		}	
		default:
			break;
		}
	}
	
	/**
	 * 
	 * @Title: updateAppItem
	 * @Description: 更新AppItem
	 * @return: void
	 */
	public void updateAppItem()
	{
		if(mAppItem != null)
		{
			mIcon.setImageDrawable(mAppItem.getIconBitmap());
			mName.setText(mAppItem.getAppName());
		}
	}
	
	/**
	 * 
	 * @Title: setAppIsInstalled
	 * @Description: 设置AppItem是否已安装
	 * @return: void
	 */
	public void setAppIsInstalled()
	{
		if (mAppItem != null)
		{
			String pkgName = mAppItem.getPkgName();
			AppItem item = InstalledTable.queryAppInfo(pkgName);
			if (item != null)
			{
				mIsInstall = 1;
				mInstalled.setVisibility(View.VISIBLE);
			}
			else
			{
				mIsInstall = 0;
				mInstalled.setVisibility(View.INVISIBLE);
			}
		}
	}
	
	/**
	 * 
	 * @Title: getAppIsInstalled
	 * @Description: 获取AppItem是否安装的状态(0-未安装; 1-已安装)
	 * @return
	 * @return: int
	 */
	public int getAppIsInstall()
	{
		return mIsInstall;
	}
	
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) 
	{
		switch (event.getKeyCode()) 
		{
		case KeyEvent.KEYCODE_DPAD_CENTER: 
		{
			if (event.getAction() == KeyEvent.ACTION_DOWN) 
			{
				mOnAppClickListener.OnAppClick(this);
			}
			break;
		}
		default:
			break;
		}
		return super.dispatchKeyEvent(event);
	}
}
