package com.sz.ead.framework.mul2launcher.appstore.downloader;


import java.io.Serializable;

import com.sz.ead.framework.mul2launcher.dataprovider.dataitem.AppItem;


public class DownloadInfo implements Serializable{

	private static final long serialVersionUID = 1L;
	
	String mAppCode;			//应用编码  包名
	String mAppName;			//应用名称		
	String mPkgName;			//包名
	String mIcon;				//应用图标地址
	String mVersion;			//用于后台升级比较的版本
	String mShowVersion;		//用于展示的版本
	String mSize;				//应用大小(单位：M)
	String mPrice;				//应用价格
	String mClassifycode;		//分类编码
	String mMd5;				//下载包的md5值
	String mDownloadurl;		//下载地址
	int mDownTotalBytes;        //当前下载的总字节数
	int mDownCurrentBytes;      //当前下载的字节数	
	int mDownStatus;    		//下载状态     收藏      暂停     准备下载   正在下载   下载完  下载失败  未知状态
	String mDownApkPath;		//apk保存路径
	int mAddDownListStyle;      //状态点下载进入下载队列还是点更新进入下载队列
	int mDownSource;
	


	public DownloadInfo(){
		super();
		this.mAppCode = null;
		this.mAppName=null;
		this.mPkgName=null;
		this.mIcon=null;
		this.mVersion=null;
		this.mShowVersion=null;
		this.mSize=null;
		//this.mPrice=null;
		//this.mClassifycode=null;
		this.mMd5=null;
		this.mDownloadurl=null;
		this.mDownTotalBytes = -1;
		this.mDownCurrentBytes=0;
		this.mDownStatus=0;
		this.mDownApkPath=null;
		this.mAddDownListStyle=0;
		this.mDownSource = 0;
	}
	
	public DownloadInfo(AppItem info, int source){
		this.mAppCode = info.getAppCode();
		this.mAppName=info.getAppName();
		this.mPkgName=info.getPkgName();
		this.mIcon=info.getIcon();
		this.mVersion=info.getVersion();
		this.mShowVersion=info.getShowVersion();
		this.mSize=info.getSize();
		//this.mPrice=info.getPrice();
		//this.mClassifycode=info.getClassifycode();
		this.mMd5=info.getMd5();
		this.mDownloadurl=info.getDownloadUrl();
		this.mDownTotalBytes = -1;
		this.mDownSource = source;
	}
	
	public DownloadInfo(DownloadInfo item,String apkpath){
		this(item);
			
		if(item.getVersion()==null){
			this.mDownApkPath=apkpath + "/" + item.getAppName()+item.getAppCode()+".apk";
		}else{
			this.mDownApkPath=apkpath + "/" + item.getAppName()+item.getVersion()+".apk";
		}
		this.mDownTotalBytes = -1;
	}
	
	public DownloadInfo(DownloadInfo info){
		this.mAppCode = info.getAppCode();
		this.mAppName=info.getAppName();
		this.mPkgName=info.getPkgName();
		this.mIcon=info.getIcon();
		this.mVersion=info.getVersion();
		this.mShowVersion=info.getShowVersion();
		this.mSize=info.getSize();
		this.mPrice=info.getPrice();
		this.mClassifycode=info.getClassifycode();
		this.mMd5=info.getMd5();
		this.mDownloadurl=info.getDownloadurl();
		this.mDownTotalBytes=info.getDownTotalBytes();
		this.mDownCurrentBytes=info.getDownCurrentBytes();
		this.mDownStatus=info.getDownStatus();
		this.mDownApkPath=info.getDownApkPath();
		this.mAddDownListStyle=info.getAddDownListStyle();
		this.mDownSource = info.getDownSource();
	}
	
	
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


	public String getPrice() {
		return mPrice;
	}


	public void setPrice(String mPrice) {
		this.mPrice = mPrice;
	}


	public String getClassifycode() {
		return mClassifycode;
	}


	public void setClassifycode(String mClassifycode) {
		this.mClassifycode = mClassifycode;
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

	public int getDownTotalBytes() {
		return mDownTotalBytes;
	}


	public void setDownTotalBytes(int mDownTotalBytes) {
		this.mDownTotalBytes = mDownTotalBytes;
	}

	public int getDownCurrentBytes() {
		return mDownCurrentBytes;
	}


	public void setDownCurrentBytes(int mDownCurrentBytes) {
		this.mDownCurrentBytes = mDownCurrentBytes;
	}


	public int getDownStatus() {
		return mDownStatus;
	}


	public void setDownStatus(int mDownStatus) {
		this.mDownStatus = mDownStatus;
	}


	public String getDownApkPath() {
		return mDownApkPath;
	}


	public void setDownApkPath(String mDownApkPath) {
		this.mDownApkPath = mDownApkPath;
	}


	public int getAddDownListStyle() {
		return mAddDownListStyle;
	}


	public void setAddDownListStyle(int mAddDownListStyle) {
		this.mAddDownListStyle = mAddDownListStyle;
	}


	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	public int getDownSource() {
		return mDownSource;
	}

	public void setDownSource(int mDownSource) {
		this.mDownSource = mDownSource;
	}
}