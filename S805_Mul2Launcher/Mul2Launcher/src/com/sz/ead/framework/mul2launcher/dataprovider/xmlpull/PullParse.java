/**
 * Copyright © 2015GreatVision. All rights reserved.
 *
 * @Title: PullParse.java
 * @Prject: Mul2Launcher
 * @Package: com.sz.ead.framework.mul2launcher.dataprovider.xmlpull
 * @Description: pull解析器  注意utf-8有两种格式带bom和不带bom 现在解析是不带bom  注意属性引号必须是英文引号
 * @author: zhaoqy  
 * @date: 2015-4-22 下午7:37:33
 */
package com.sz.ead.framework.mul2launcher.dataprovider.xmlpull;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;
import com.sz.ead.framework.mul2launcher.dataprovider.dataitem.AppItem;
import com.sz.ead.framework.mul2launcher.dataprovider.dataitem.ScreenShotItem;
import com.sz.ead.framework.mul2launcher.dataprovider.datapacket.ElementListData;
import com.sz.ead.framework.mul2launcher.util.LogUtil;

public class PullParse 
{
	/**
	 * 
	 * @Title: parseAppList
	 * @Description: 解析应用列表
	 * @param responseBytes
	 * @param mark
	 * @return
	 * @return: ElementListData
	 */
	public static ElementListData parseAppList(ByteBuffer responseBytes, int mark)
	{
		ElementListData emData = new ElementListData(mark, 0, 0, "");
		AppItem appItem = null;
		ByteArrayInputStream bin = new ByteArrayInputStream(responseBytes.array());
        InputStreamReader in = new InputStreamReader(bin);
        
        try 
        {
        	XmlPullParserFactory pullFactory = XmlPullParserFactory.newInstance();     
            XmlPullParser parser = pullFactory.newPullParser();
            parser.setInput(in);
            
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) 
            {
                switch (eventType) 
                {
				case XmlPullParser.START_DOCUMENT:
					break;
				case XmlPullParser.START_TAG:
				{
					if("apps".equalsIgnoreCase(parser.getName()))
					{
						emData.setPage(Integer.valueOf(parser.getAttributeValue(null, "currentpage")));
						emData.setSize(Integer.valueOf(parser.getAttributeValue(null, "num")));
						emData.setTotal(Integer.valueOf(parser.getAttributeValue(null, "total")));
					}
					else if("app".equalsIgnoreCase(parser.getName()))
					{
						appItem = new AppItem();
					}
					else if(appItem != null)
					{
						if("appcode".equalsIgnoreCase(parser.getName()))
						{
							appItem.setAppCode(parser.nextText());  
		                }
						else if("appname".equalsIgnoreCase(parser.getName()))
						{
		                	appItem.setAppName(parser.nextText());
		                }
						else if("pkgname".equalsIgnoreCase(parser.getName()))
						{
		                	appItem.setPkgName(parser.nextText());
		                }
						else if("icon".equalsIgnoreCase(parser.getName()))
						{
		                	appItem.setIcon(parser.nextText());
		                }
						else if("image".equalsIgnoreCase(parser.getName()))
						{
		                	String imageUrl = parser.nextText();
		                	String array[] = imageUrl.split(";|；");
		                	ArrayList<ScreenShotItem> list = new ArrayList<ScreenShotItem>();
		                	
		                	for(int i = 0; i < array.length; i++)
		                	{  
		                		ScreenShotItem item = new ScreenShotItem();
		                		item.setImageUrl(array[i]);
		                		list.add(item);
		                    }
		                	appItem.setImages(list);
		                }
						else if("version".equalsIgnoreCase(parser.getName()))
						{
		                	appItem.setVersion(parser.nextText());
		                }
						else if("showversion".equalsIgnoreCase(parser.getName()))
						{
		                	appItem.setShowVersion(parser.nextText());
		                }
						else if("size".equalsIgnoreCase(parser.getName()))
						{
		                	appItem.setSize(parser.nextText());
		                }
						else if("developer".equalsIgnoreCase(parser.getName()))
						{
		                	appItem.setDeveloper(parser.nextText());
		                }
						else if("summary".equalsIgnoreCase(parser.getName()))
						{
		                	appItem.setSummary(parser.nextText());
		                }
						else if("md5".equalsIgnoreCase(parser.getName()))
						{
		                	appItem.setMd5(parser.nextText());
		                }
						else if("downloadurl".equalsIgnoreCase(parser.getName()))
						{
		                	appItem.setDownloadUrl(parser.nextText());
		                }
					}
					break;
				}
				case XmlPullParser.END_TAG:
				{
					if ("app".equalsIgnoreCase(parser.getName()) && appItem != null) 
					{
						emData.getList().add(appItem);
						appItem = null;
					}
					break;
				}
				default:
					break;
				}
  
                eventType = parser.next();  
            }  
		}  
        catch (Exception e) 
        {
        	LogUtil.i(LogUtil.TAG, " parseAppList " + e.toString());
		}
        return emData;
	}
	
	/**
	 * 
	 * @Title: parseAppUpdate
	 * @Description: 解析应用升级
	 * @param responseBytes
	 * @param mark
	 * @return
	 * @return: ElementListData
	 */
	public static ElementListData parseAppUpdate(ByteBuffer responseBytes, int mark)
	{
		ElementListData emData = new ElementListData(mark, 0, 0, "");
		AppItem appItem = null;
		ByteArrayInputStream bin = new ByteArrayInputStream(responseBytes.array());
        InputStreamReader in = new InputStreamReader(bin);
        
        try 
        {
        	XmlPullParserFactory pullFactory = XmlPullParserFactory.newInstance();     
            XmlPullParser parser = pullFactory.newPullParser();
            parser.setInput(in);
            
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) 
            {
                switch (eventType) 
                {
				case XmlPullParser.START_DOCUMENT:
					break;
				case XmlPullParser.START_TAG:
				{
					if("apps".equalsIgnoreCase(parser.getName()))
					{
						emData.setTotal(Integer.valueOf(parser.getAttributeValue(null, "total")));
					}
					else if("app".equalsIgnoreCase(parser.getName()))
					{
						appItem = new AppItem();
					}
					else if(appItem != null)
					{
						if("appcode".equalsIgnoreCase(parser.getName()))
						{
							appItem.setAppCode(parser.nextText());  
		                }
						else if("appname".equalsIgnoreCase(parser.getName()))
						{
		                	appItem.setAppName(parser.nextText());
		                }
						else if("pkgname".equalsIgnoreCase(parser.getName()))
						{
		                	appItem.setPkgName(parser.nextText());
		                }
						else if("icon".equalsIgnoreCase(parser.getName()))
						{
		                	appItem.setIcon(parser.nextText());
		                }
						else if("version".equalsIgnoreCase(parser.getName()))
						{
		                	appItem.setVersion(parser.nextText());
		                }
						else if("showversion".equalsIgnoreCase(parser.getName()))
						{
		                	appItem.setShowVersion(parser.nextText());
		                }
						else if("size".equalsIgnoreCase(parser.getName()))
						{
		                	appItem.setSize(parser.nextText());
		                }
						else if("developer".equalsIgnoreCase(parser.getName()))
						{
		                	appItem.setDeveloper(parser.nextText());
		                }
						else if("md5".equalsIgnoreCase(parser.getName()))
						{
		                	appItem.setMd5(parser.nextText());
		                }
						else if("downloadurl".equalsIgnoreCase(parser.getName()))
						{
		                	appItem.setDownloadUrl(parser.nextText());
		                }
					}
					break;
				}
				case XmlPullParser.END_TAG:
				{
					if ("app".equalsIgnoreCase(parser.getName()) && appItem != null) 
					{
						emData.getList().add(appItem);
						appItem = null;
					}
					break;
				}
				default:
					break;
				}
  
                eventType = parser.next();  
            }  
		}  
        catch (Exception e) 
        {
        	LogUtil.i(LogUtil.TAG, " parseAppUpdate " + e.toString());
		}
        return emData;
	}
	
	/**
	 * 
	 * @Title: parseAppDetail
	 * @Description: 解析应用详情
	 * @param responseBytes
	 * @param mark
	 * @return
	 * @return: ElementListData
	 */
	public static ElementListData parseAppDetail(ByteBuffer responseBytes, int mark)
	{
		ElementListData emData = new ElementListData(mark, 0, 0, "");
		AppItem appItem = null;
		ByteArrayInputStream bin = new ByteArrayInputStream(responseBytes.array());
        InputStreamReader in = new InputStreamReader(bin);
        
        try 
        {
        	XmlPullParserFactory pullFactory = XmlPullParserFactory.newInstance();     
            XmlPullParser parser = pullFactory.newPullParser();
            parser.setInput(in);
            
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) 
            {
                switch (eventType) 
                {
				case XmlPullParser.START_DOCUMENT:
					break;
				case XmlPullParser.START_TAG:
				{
					if("app".equalsIgnoreCase(parser.getName()))
					{
						appItem = new AppItem();
					}
					else if(appItem != null)
					{
						if("appcode".equalsIgnoreCase(parser.getName()))
						{
							appItem.setAppCode(parser.nextText());  
		                }
						else if("appname".equalsIgnoreCase(parser.getName()))
						{
		                	appItem.setAppName(parser.nextText());
		                }
						else if("pkgname".equalsIgnoreCase(parser.getName()))
						{
		                	appItem.setPkgName(parser.nextText());
		                }
						else if("icon".equalsIgnoreCase(parser.getName()))
						{
		                	appItem.setIcon(parser.nextText());
		                }
						else if("image".equalsIgnoreCase(parser.getName()))
						{
		                	String imageUrl = parser.nextText();
		                	String array[] = imageUrl.split(";|；");
		                	ArrayList<ScreenShotItem> list = new ArrayList<ScreenShotItem>();
		                	
		                	for(int i = 0; i < array.length; i++)
		                	{  
		                		ScreenShotItem item = new ScreenShotItem();
		                		item.setImageUrl(array[i]);
		                		list.add(item);
		                    }
		                	appItem.setImages(list);
		                }
						else if("version".equalsIgnoreCase(parser.getName()))
						{
		                	appItem.setVersion(parser.nextText());
		                }
						else if("showversion".equalsIgnoreCase(parser.getName()))
						{
		                	appItem.setShowVersion(parser.nextText());
		                }
						else if("size".equalsIgnoreCase(parser.getName()))
						{
		                	appItem.setSize(parser.nextText());
		                }
						else if("developer".equalsIgnoreCase(parser.getName()))
						{
		                	appItem.setDeveloper(parser.nextText());
		                }
						else if("summary".equalsIgnoreCase(parser.getName()))
						{
		                	appItem.setSummary(parser.nextText());
		                }
						else if("md5".equalsIgnoreCase(parser.getName()))
						{
		                	appItem.setMd5(parser.nextText());
		                }
						else if("downloadurl".equalsIgnoreCase(parser.getName()))
						{
		                	appItem.setDownloadUrl(parser.nextText());
		                }
					}
					break;
				}	
				case XmlPullParser.END_TAG:
				{
					if ("app".equalsIgnoreCase(parser.getName()) && appItem != null) 
					{
						emData.getList().add(appItem);
						appItem = null;
					}
					break;
				}
				default:
					break;
				}
  
                eventType = parser.next();  
            }  
		} 
        catch (Exception e) 
        {
			LogUtil.i(LogUtil.TAG, " parseAppDetail " + e.toString());
		}
        return emData;
	}
}
