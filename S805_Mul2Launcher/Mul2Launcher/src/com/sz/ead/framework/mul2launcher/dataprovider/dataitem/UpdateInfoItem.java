package com.sz.ead.framework.mul2launcher.dataprovider.dataitem;

public class UpdateInfoItem {
	
	String mAppCode;			//应用编码
	String mAppName;			//应用名称	
	String mPkgName;			//包名
	String mIcon;				//应用图标地址
	String mVersion;			//用于后台升级比较的版本
	String mShowVersion;		//用于展示的版本
	String mSize;					//应用大小(单位：M)
	String mMd5;				//下载包的md5值
	String mDownloadurl;		//下载地址
	
	int igron;					//取消升级
	
	public String getAppCode() {
		return mAppCode;
	}
	public void setAppCode(String mAppCode) {
		this.mAppCode = mAppCode;
	}
	public String getAppName() {
		return mAppName;
	}
	public void setAppName(String mAppName) {
		this.mAppName = mAppName;
	}
	public String getPkgName() {
		return mPkgName;
	}
	public void setPkgName(String mPkgName) {
		this.mPkgName = mPkgName;
	}
	public String getIcon() {
		return mIcon;
	}
	public void setIcon(String mIcon) {
		this.mIcon = mIcon;
	}
	public String getVersion() {
		return mVersion;
	}
	public void setVersion(String mVersion) {
		this.mVersion = mVersion;
	}
	public String getShowVersion() {
		return mShowVersion;
	}
	public void setShowVersion(String mShowVersion) {
		this.mShowVersion = mShowVersion;
	}
	public String getSize() {
		return mSize;
	}
	public void setSize(String mSize) {
		this.mSize = mSize;
	}
	public String getMd5() {
		return mMd5;
	}
	public void setMd5(String mMd5) {
		this.mMd5 = mMd5;
	}
	public String getDownloadurl() {
		return mDownloadurl;
	}
	public void setDownloadurl(String mDownloadurl) {
		this.mDownloadurl = mDownloadurl;
	}
	
	public int getIgron() {
		return igron;
	}
	public void setIgron(int igron) {
		this.igron = igron;
	}
	
}
