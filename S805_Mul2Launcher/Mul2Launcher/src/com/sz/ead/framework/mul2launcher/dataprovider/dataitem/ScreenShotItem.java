/**
 * Copyright © 2015GreatVision. All rights reserved.
 *
 * @Title: ScreenShotItem.java
 * @Prject: Mul2Launcher
 * @Package: com.sz.ead.framework.mul2launcher.dataprovider.dataitem
 * @Description: 应用截图Item
 * @author: zhaoqy  
 * @date: 2015-4-22 下午8:14:10
 */
package com.sz.ead.framework.mul2launcher.dataprovider.dataitem;

import android.os.Parcel;
import android.os.Parcelable;

public class ScreenShotItem implements Parcelable
{
	private String mImageUrl; //图片地址
	
	public ScreenShotItem()
	{
	}
	
	public ScreenShotItem(ScreenShotItem item)
	{
		mImageUrl = item.mImageUrl;
	}
	
	@Override
	public int describeContents() 
	{
		return 0;
	}
	
	@Override
	public void writeToParcel(Parcel out, int flags) 
	{
		 out.writeString(mImageUrl);
	}
	
	public static final Parcelable.Creator<ScreenShotItem> CREATOR = new Parcelable.Creator<ScreenShotItem>() 
	{
		public ScreenShotItem createFromParcel(Parcel in) 
		{
			return new ScreenShotItem(in);
		}

		public ScreenShotItem[] newArray(int size) 
		{
			return new ScreenShotItem[size];
		}
	};	
			
	private ScreenShotItem(Parcel in) 
	{
		mImageUrl = in.readString();	
	}
	
	public String getImageUrl() 
	{
		return mImageUrl;
	}
	
	public void setImageUrl(String imageUrl) 
	{
		mImageUrl = imageUrl;
	}

}
