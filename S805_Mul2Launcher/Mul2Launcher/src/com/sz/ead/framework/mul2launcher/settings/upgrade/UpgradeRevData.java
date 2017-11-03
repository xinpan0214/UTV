/**
 * Copyright © 2014GreatVision. All rights reserved.
 *
 * @Title: UpgradeRevData.java
 * @Prject: BananaTvSetting
 * @Description: TODO
 * @author: lijungang  
 * @date: 2014-7-29 下午2:19:43
 */
package com.sz.ead.framework.mul2launcher.settings.upgrade;

/**
 * Copyright © 2014GreatVision. All rights reserved.
 *
 * @Title: UpgradeRevData.java
 * @Prject: BananaTvSetting
 * @Description: TODO
 * @author: lijungang 
 * @date: 2014-7-29 下午2:19:43
 */
public class UpgradeRevData {
	
	private static UpgradeRevData instance = null;
	
    public static UpgradeRevData getInstance() 
    {
       if (instance == null) 
       {
    	   instance = new UpgradeRevData();
       }
       return instance;
    }
	
	private String upgrade_rstatus; //有无升级版本
	private String upgrade_rmsg; //升级消息
	
	private String upgrade_versionmsg_version; //升级版本
	private String upgrade_versionmsg_forced; //1强制升级，0非强制升级
	private String upgrade_versionmsg_bupdate; //1全部升级，2差分升级
	private String upgrade_versionmsg_updatetime; //更新时间
	private String upgrade_versionmsg_desc; //新版变化
	private String upgrade_versionmsg_desc_lang;//对应语言的解析
	private String upgrade_file_fid; //升级文件id
	private String upgrade_file_fn; //升级文件名
	private String upgrade_file_fs; //升级文件大小
	private String upgrade_file_lmt; //升级文件id
	private String upgrade_file_ft; //升级文件id
	private String upgrade_file_fv; //升级文件id
	private String upgrade_file_durl; //升级文件地址
	private String upgrade_file_md5; //升级文件md5校验
	
	public String getUpgrade_rstatus() {
		return upgrade_rstatus;
	}
	public void setUpgrade_rstatus(String upgrade_rstatus) {
		this.upgrade_rstatus = upgrade_rstatus;
	}
	public String getUpgrade_rmsg() {
		return upgrade_rmsg;
	}
	public void setUpgrade_rmsg(String upgrade_rmsg) {
		this.upgrade_rmsg = upgrade_rmsg;
	}
	public String getUpgrade_versionmsg_version() {
		return upgrade_versionmsg_version;
	}
	public void setUpgrade_versionmsg_version(String upgrade_versionmsg_version) {
		this.upgrade_versionmsg_version = upgrade_versionmsg_version;
	}
	public String getUpgrade_versionmsg_forced() {
		return upgrade_versionmsg_forced;
	}
	public void setUpgrade_versionmsg_forced(String upgrade_versionmsg_forced) {
		this.upgrade_versionmsg_forced = upgrade_versionmsg_forced;
	}
	public String getUpgrade_versionmsg_bupdate() {
		return upgrade_versionmsg_bupdate;
	}
	public void setUpgrade_versionmsg_bupdate(String upgrade_versionmsg_bupdate) {
		this.upgrade_versionmsg_bupdate = upgrade_versionmsg_bupdate;
	}
	public String getUpgrade_versionmsg_updatetime() {
		return upgrade_versionmsg_updatetime;
	}
	public void setUpgrade_versionmsg_updatetime(
			String upgrade_versionmsg_updatetime) {
		this.upgrade_versionmsg_updatetime = upgrade_versionmsg_updatetime;
	}
	public String getUpgrade_versionmsg_desc() {
		return upgrade_versionmsg_desc;
	}
	public void setUpgrade_versionmsg_desc(String upgrade_versionmsg_desc) {
		this.upgrade_versionmsg_desc = upgrade_versionmsg_desc;
	}
	public String getUpgrade_versionmsg_desc_lang() {
		return upgrade_versionmsg_desc_lang;
	}
	public void setUpgrade_versionmsg_desc_lang(String upgrade_versionmsg_desc_lang) {
		this.upgrade_versionmsg_desc_lang = upgrade_versionmsg_desc_lang;
	}
	public String getUpgrade_file_fid() {
		return upgrade_file_fid;
	}
	public void setUpgrade_file_fid(String upgrade_file_fid) {
		this.upgrade_file_fid = upgrade_file_fid;
	}
	public String getUpgrade_file_fn() {
		return upgrade_file_fn;
	}
	public void setUpgrade_file_fn(String upgrade_file_fn) {
		this.upgrade_file_fn = upgrade_file_fn;
	}
	public String getUpgrade_file_fs() {
		return upgrade_file_fs;
	}
	public void setUpgrade_file_fs(String upgrade_file_fs) {
		this.upgrade_file_fs = upgrade_file_fs;
	}
	public String getUpgrade_file_lmt() {
		return upgrade_file_lmt;
	}
	public void setUpgrade_file_lmt(String upgrade_file_lmt) {
		this.upgrade_file_lmt = upgrade_file_lmt;
	}
	public String getUpgrade_file_ft() {
		return upgrade_file_ft;
	}
	public void setUpgrade_file_ft(String upgrade_file_ft) {
		this.upgrade_file_ft = upgrade_file_ft;
	}
	public String getUpgrade_file_fv() {
		return upgrade_file_fv;
	}
	public void setUpgrade_file_fv(String upgrade_file_fv) {
		this.upgrade_file_fv = upgrade_file_fv;
	}
	public String getUpgrade_file_durl() {
		return upgrade_file_durl;
	}
	public void setUpgrade_file_durl(String upgrade_file_durl) {
		this.upgrade_file_durl = upgrade_file_durl;
	}
	public String getUpgrade_file_md5() {
		return upgrade_file_md5;
	}
	public void setUpgrade_file_md5(String upgrade_file_md5) {
		this.upgrade_file_md5 = upgrade_file_md5;
	}
}
