/**
 * 
 * Copyright © 2014GreatVision. All rights reserved.
 *
 * @Title: UpgradeData.java
 * @Prject: BananaTvSetting
 * @Description: 系统升级消息数据
 * @author: lijungang 
 * @date: 2014-1-24 下午1:50:22
 */
package com.sz.ead.framework.mul2launcher.settings.upgrade;

public class UpgradeData {

	private static UpgradeData instance = null;
	
    public static UpgradeData getInstance() 
    {
       if (instance == null) 
       {
    	   instance = new UpgradeData();
       }
       return instance;
    }

	private String cur_version; //当前软件版本
	private String upgrade_version;//需要升级版本
	
	public String getCur_version() {
		return cur_version;
	}

	public void setCur_version(String cur_version) {
		this.cur_version = cur_version;
	}

	public String getUpgrade_version() {
		return upgrade_version;
	}

	public void setUpgrade_version(String upgrade_version) {
		this.upgrade_version = upgrade_version;
	}
}
