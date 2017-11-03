/**
 * Copyright © 2015GreatVision. All rights reserved.
 *
 * @Title: AppItem.java
 * @Prject: Mul2Launcher
 * @Package: com.sz.ead.framework.mul2launcher.dataprovider.dataitem
 * @Description: 应用信息Item
 * @author: zhaoqy  
 * @date: 2015-4-22 下午5:11:39
 */
package com.sz.ead.framework.mul2launcher.dataprovider.dataitem;

import java.util.ArrayList;

import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;

public class AppItem implements Parcelable
{
	private String mAppCode;     //应用编码
	private String mAppName;	 //应用名称	
	private String mPkgName;     //包名
	private String mIcon;		 //应用图标地址
	private ArrayList<ScreenShotItem> mImages; //应用截图地址（包含三张图片的地址）
	private String mDeveloper;	 //开发商名称
	private String mVersion;	 //用于后台升级比较的版本
	private String mShowVersion; //用于展示的版本
	private String mSize;		 //应用大小(单位：M)
	private String mSummary;	 //应用简介
	private String mMd5;		 //下载包的md5值
	private String mDownloadUrl; //下载地址
	private int mPosition;       //应用位置
	private Drawable mIconBitmap = null; //应用图标位图
	
	public AppItem()
	{
	}
	
	public AppItem(AppItem item)
	{
		mAppCode = item.mAppCode;
		mAppName = item.mAppName;
		mPkgName = item.mPkgName;
		mIcon = item.mIcon;
		mImages = item.mImages;
		mDeveloper = item.mDeveloper;
		mVersion = item.mVersion;
		mShowVersion = item.mShowVersion;
		mSize = item.mSize;
		mSummary = item.mSummary;
		mMd5 = item.mMd5;
		mDownloadUrl = item.mDownloadUrl;
		mPosition = item.mPosition;
		mIconBitmap = item.mIconBitmap;
	}
	
	public void setAppItem(AppItem item)
	{
		mAppCode = item.getAppCode();
		mAppName = item.getAppName();
		mPkgName = item.getPkgName();
		mIcon = item.getIcon();
		mImages = item.getImages();
		mDeveloper = item.getDeveloper();
		mVersion = item.getVersion();
		mShowVersion = item.getShowVersion();
		mSize = item.getSize();
		mSummary = item.getSummary();
		mMd5 = item.getMd5();
		mDownloadUrl = item.getDownloadUrl();
		mPosition = item.getPosition();
		mIconBitmap = item.getIconBitmap();
	}
	
	@Override
	public int describeContents() 
	{
		return 0;
	}

	@Override
	public void writeToParcel(Parcel out, int flags) 
	{
		out.writeString(mAppCode);
		out.writeString(mAppName);
		out.writeString(mPkgName);
		out.writeString(mIcon);
		out.writeList(mImages);
		out.writeString(mDeveloper);
		out.writeString(mVersion);
		out.writeString(mShowVersion);
		out.writeString(mSize);
		out.writeString(mSummary);
		out.writeString(mMd5);
		out.writeString(mDownloadUrl);
		out.writeInt(mPosition);
	}
	
	public static final Parcelable.Creator<AppItem> CREATOR = new Parcelable.Creator<AppItem>() 
	{
		public AppItem createFromParcel(Parcel in) 
		{
			return new AppItem(in);
		}

		public AppItem[] newArray(int size) 
		{
			return new AppItem[size];
		}
	};

	@SuppressWarnings("unchecked")
	private AppItem(Parcel in) 
	{
		mAppCode = in.readString();
		mAppName = in.readString();
		mPkgName = in.readString();
		mIcon = in.readString();
		mImages = in.readArrayList(ScreenShotItem.class.getClassLoader());
		mDeveloper = in.readString();
		mVersion = in.readString();
		mShowVersion = in.readString();
		mSize = in.readString();
		mSummary = in.readString();
		mMd5 = in.readString();
		mDownloadUrl = in.readString();
		mPosition = in.readInt();
	}
	
	public void setAppCode(String appCode)
	{
		mAppCode = appCode;
	}
	
	public String getAppCode()
	{
		return mAppCode;
	}
	
	public void setAppName(String appName)
	{
		mAppName = appName;
	}
	
	public String getAppName()
	{
		return mAppName;
	}
	
	public void setPkgName(String pkgName)
	{
		mPkgName = pkgName;
	}
	
	public String getPkgName()
	{
		return mPkgName;
	}
	
	public void setIcon(String icon)
	{
		mIcon = icon;
	}
	
	public String getIcon()
	{
		return mIcon;
	}
	
	public void setImages(ArrayList<ScreenShotItem> images)
	{
		mImages = images;
	}
	
	public ArrayList<ScreenShotItem> getImages() 
	{
		return mImages;
	}
	
	public void setDeveloper(String developer)
	{
		mDeveloper = developer;
	}
	
	public String getDeveloper()
	{
		return mDeveloper;
	}
	
	public void setVersion(String version)
	{
		mVersion = version;
	}
	
	public String getVersion()
	{
		return mVersion;
	}
	
	public void setShowVersion(String showVersion)
	{
		mShowVersion = showVersion;
	}
	
	public String getShowVersion()
	{
		return mShowVersion;
	}
	
	public void setSize(String size)
	{
		mSize = size;
	}
	
	public String getSize()
	{
		return mSize;
	}
	
	public void setSummary(String summary)
	{
		mSummary = summary;
	}
	
	public String getSummary()
	{
		return mSummary;
	}
	
	public void setMd5(String md5)
	{
		mMd5 = md5;
	}
	
	public String getMd5()
	{
		return mMd5;
	}
	
	public void setDownloadUrl(String downloadUrl)
	{
		mDownloadUrl = downloadUrl;
	}
	
	public String getDownloadUrl()
	{
		return mDownloadUrl;
	}
	
	public void setPosition(int position)
	{
		mPosition = position;
	}
	
	public int getPosition()
	{
		return mPosition;
	}
	
	public Drawable getIconBitmap() 
	{
		return mIconBitmap;
	}

	public void setIconBitmap(Drawable iconBitmap) 
	{
		mIconBitmap = iconBitmap;
	}
}
