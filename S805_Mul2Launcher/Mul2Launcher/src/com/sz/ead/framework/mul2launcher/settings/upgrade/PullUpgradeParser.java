/**
 * 
 * Copyright © 2014GreatVision. All rights reserved.
 *
 * @Title: PullUpgradeParser.java
 * @Prject: BananaTvSetting
 * @Description: 系统在线升级后台xml消息pull解析
 * @author: lijungang 
 * @date: 2014-1-24 下午1:48:10
 */
package com.sz.ead.framework.mul2launcher.settings.upgrade;

import java.io.InputStream;
import org.xmlpull.v1.XmlPullParser;
import android.util.Xml;

public class PullUpgradeParser implements UpgradeParser{

	@Override
	public void parse(InputStream is) throws Exception {
		// TODO Auto-generated method stub 
		XmlPullParser parser = Xml.newPullParser(); 
		parser.setInput(is, "UTF-8"); 
		int eventType = parser.getEventType();  
	
		while (eventType != XmlPullParser.END_DOCUMENT) {  
			String tag = parser.getName();
            switch (eventType) { 
            case XmlPullParser.START_DOCUMENT: 
            	break;
            case XmlPullParser.START_TAG:
            	if (tag.equals("rstatus")) { 
            		UpgradeRevData.getInstance().setUpgrade_rstatus(parser.nextText());
                }else if(tag.equals("rmsg")){
            		UpgradeRevData.getInstance().setUpgrade_rmsg(parser.nextText());
            	}else if(tag.equals("version")){
            		UpgradeRevData.getInstance().setUpgrade_versionmsg_version(parser.nextText());
            	}else if(tag.equals("forced")){
            		UpgradeRevData.getInstance().setUpgrade_versionmsg_forced(parser.nextText());
            	}else if(tag.equals("bupdate")){
            		UpgradeRevData.getInstance().setUpgrade_versionmsg_bupdate(parser.nextText());
            	}else if(tag.equals("updatetime")){
            		UpgradeRevData.getInstance().setUpgrade_versionmsg_updatetime(parser.nextText());
            	}else if(tag.equals("desc")){
            		UpgradeRevData.getInstance().setUpgrade_versionmsg_desc(parser.nextText());
            	}else if(tag.equals("fid")){
            		UpgradeRevData.getInstance().setUpgrade_file_fid(parser.nextText());
            	}else if(tag.equals("fn")){
            		UpgradeRevData.getInstance().setUpgrade_file_fn(parser.nextText());
            	}else if(tag.equals("fs")){
                	try {
                		UpgradeRevData.getInstance().setUpgrade_file_fs(Long.toString((long) (Double.parseDouble(parser.nextText()))));
					} catch (Exception e) {
						// TODO: handle exception
					}
            	}else if(tag.equals("lmt")){
            		UpgradeRevData.getInstance().setUpgrade_file_lmt(parser.nextText());
            	}else if(tag.equals("ft")){
            		UpgradeRevData.getInstance().setUpgrade_file_ft(parser.nextText());
            	}else if(tag.equals("fv")){
            		UpgradeRevData.getInstance().setUpgrade_file_fv(parser.nextText());
            	}else if(tag.equals("durl")){
            		UpgradeRevData.getInstance().setUpgrade_file_durl(parser.nextText());
            	}else if(tag.equals("md5")){
            		UpgradeRevData.getInstance().setUpgrade_file_md5(parser.nextText());
            	}
                break;  
            case XmlPullParser.END_TAG:  
                break;  
            }  
            eventType = parser.next();  
        }  
	}
	
	@Override
	public String serialize() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
}
