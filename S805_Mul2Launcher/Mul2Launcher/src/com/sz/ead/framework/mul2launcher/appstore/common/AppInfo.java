package com.sz.ead.framework.mul2launcher.appstore.common;


import java.util.Date;

import android.graphics.drawable.Drawable;

public class AppInfo {
	public String   mAppName = null;     //应用名称
	public String   mPackageName = null; //应用包名
	
	public String   mVersionName = null; //版本名称
	public int      mVersionCode = 0;    //版本号
	public String   mAuthor;		     //开发者
	public Drawable mIcon = null;        //应用ICON(在内存中)
	
	public int      mSize = 0;           //应用包大小
	public Date     mLastModifyDate = null;//Apk最后修改时间
	public boolean  mIsAppStorePic = false;	//是商城图片
	
}
