package com.sz.ead.framework.mul2launcher.appstore.downloader;


import java.io.Serializable;

/**
 * 安装过程需要此信息
 * @author liyingying
 */
public class InstallInfo implements Serializable{

	private static final long serialVersionUID = 1L;

	//应用id
	private String mPkgName;
	
	//总进度
	private int mTotalProgess;
	
	//当前进度
	private int mCurrentProgess;

	public String getPkgName() {
		return mPkgName;
	}

	public void setPkgName(String mPkgName) {
		this.mPkgName = mPkgName;
	}

	public int getTotalProgess() {
		return mTotalProgess;
	}

	public void setTotalProgess(int mTotalProgess) {
		this.mTotalProgess = mTotalProgess;
	}

	public int getCurrentProgess() {
		return mCurrentProgess;
	}

	public void setCurrentProgess(int mCurrentProgess) {
		this.mCurrentProgess = mCurrentProgess;
	}
	
	
	
}