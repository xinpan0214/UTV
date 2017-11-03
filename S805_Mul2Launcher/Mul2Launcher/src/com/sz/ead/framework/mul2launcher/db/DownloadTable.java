/**
 * Copyright © 2015GreatVision. All rights reserved.
 *
 * @Title: DownloadTable.java
 * @Prject: Mul2Launcher
 * @Package: com.sz.ead.framework.mul2launcher.db
 * @Description: 已下载的应用表
 * @author: zhaoqy  
 * @date: 2015-4-23 上午9:19:41
 */
package com.sz.ead.framework.mul2launcher.db;

import java.util.ArrayList;
import com.sz.ead.framework.mul2launcher.dataprovider.dataitem.AppItem;
import com.sz.ead.framework.mul2launcher.util.LogUtil;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

public class DownloadTable 
{
	public static final String AUTHORITY = "com.sz.ead.framework.mul2launcher.db.DatabaseManager";         //类名
	public static final String DOWNLOAD_TABLE = "downloadtable";                                           //表名
	public static final Uri CONTENT_SORT_URI = Uri.parse("content://" + AUTHORITY + "/" + DOWNLOAD_TABLE); //访问uri
	public static final String ID = "_id";                    //主键
	public static final String APP_CODE = "app_code";         //应用编码 
	public static final String APP_NAME = "app_name";         //应用名称
	public static final String PKG_NAME = "pkg_name";         //包名
	public static final String ICON = "icon";                 //应用图标地址
	public static final String DEVELOPER = "developer";       //开发商名称
	public static final String VERSION = "version";           //用于后台升级比较的版本
	public static final String SHOW_VERSION = "show_version"; //用于展示的版本
	public static final String SIZE = "size";                 //应用大小(单位：M)
	public static final String SUMMARY = "summary";           //应用简介
	public static final String MD5 = "md5";                   //下载包的md5值
	public static final String DOWNLOAD_URL = "download_url"; //下载地址
	
	public static String getCreateSql() 
	{
		/**
		 * SQL语句：
		 * db.execSQL("CREATE TABLE  IF NOT EXISTS " + DOWNLOAD_TABLE + "( " + "_id INTEGER PRIMARY KEY AUTOINCREMENT," + 
		 * "app_code TEXT," + "app_name TEXT," + "pkg_name TEXT," + "icon TEXT," + "developer TEXT," + "version TEXT," + 
		 * "show_version TEXT," + "size TEXT," + "summary TEXT," + "md5 TEXT," + "download_url TEXT" + ");");
		 */	
		StringBuffer sb = new StringBuffer();
		sb.append("CREATE TABLE  IF NOT EXISTS ");
		sb.append(DOWNLOAD_TABLE);
		sb.append("( ");
		sb.append(ID);
		sb.append(" INTEGER PRIMARY KEY AUTOINCREMENT,");
		sb.append(APP_CODE);
		sb.append(" TEXT,");
		sb.append(APP_NAME);
		sb.append(" TEXT,");
		sb.append(PKG_NAME);
		sb.append(" TEXT,");
		sb.append(ICON);
		sb.append(" TEXT,");
		sb.append(DEVELOPER);
		sb.append(" TEXT,");
		sb.append(VERSION);
		sb.append(" TEXT,");
		sb.append(SHOW_VERSION);
		sb.append(" TEXT,");	
		sb.append(SIZE);
		sb.append(" TEXT,");
		sb.append(SUMMARY);
		sb.append(" TEXT,");
		sb.append(MD5);
		sb.append(" TEXT,");
		sb.append(DOWNLOAD_URL);
		sb.append(" TEXT");
		sb.append(");");
		
		return sb.toString();
	}
	
	public static String getUpgradeSql() 
	{
		/**
		 * SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		 * qb.setTables(CONTENT_SORT_URI.getPathSegments().get(0)); Cursor
		 * abcCursor=qb.query(db,null,null,null,null,null,null);
		 */
		String string = "DROP TABLE IF EXISTS " + DOWNLOAD_TABLE;
		return string;
	}
	
	/**
	 * 
	 * @Title: queryAppInfo
	 * @Description: 通过包名查询应用信息
	 * @param pkgName
	 * @return
	 * @return: AppItem
	 */
	public static AppItem queryAppInfo(String pkgName) 
	{
		if (DatabaseManager.mDbHelper == null) 
		{
			return null;
		}
		
		AppItem appItem = null;
		try 
		{
			Cursor cursor = null;
			String where = null;
			SQLiteDatabase db = DatabaseManager.mDbHelper.getWritableDatabase();	
			where = PKG_NAME + "=\"" + pkgName + "\"";
			cursor = db.query(DOWNLOAD_TABLE, null, where, null, null, null, null);

			if (cursor != null) 
			{
				int n = cursor.getCount();
				if (n > 0) 
				{
					cursor.moveToFirst();
					appItem = new AppItem();
					appItem.setAppCode(cursor.getString(cursor.getColumnIndex(APP_CODE)));
					appItem.setAppName(cursor.getString(cursor.getColumnIndex(APP_NAME)));
					appItem.setPkgName(cursor.getString(cursor.getColumnIndex(PKG_NAME)));
					appItem.setIcon(cursor.getString(cursor.getColumnIndex(ICON)));
					appItem.setDeveloper(cursor.getString(cursor.getColumnIndex(DEVELOPER)));
					appItem.setVersion(cursor.getString(cursor.getColumnIndex(VERSION)));
					appItem.setShowVersion(cursor.getString(cursor.getColumnIndex(SHOW_VERSION)));
					appItem.setSize(cursor.getString(cursor.getColumnIndex(SIZE)));
					appItem.setSummary(cursor.getString(cursor.getColumnIndex(SUMMARY)));
					appItem.setMd5(cursor.getString(cursor.getColumnIndex(MD5)));
					appItem.setDownloadUrl(cursor.getString(cursor.getColumnIndex(DOWNLOAD_URL)));
					appItem.setImages(ScreenShotTable.queryScreenShot(pkgName));
				}
			} 
			
			if (cursor != null) 
			{
				cursor.close();
				cursor = null;
			}
		}
		catch (Exception e) 
		{
			LogUtil.i(LogUtil.TAG, " queryAppInfo " + e);
		}
		
		return appItem;
	}
	
	/**
	 * 
	 * @Title: queryAppListInfo
	 * @Description: 查询所有应用信息
	 * @return
	 * @return: ArrayList<AppItem>
	 */
	public static ArrayList<AppItem> queryAppListInfo() 
	{
		if (DatabaseManager.mDbHelper == null) 
		{
			return null;
		}
		
		ArrayList<AppItem> appList = new ArrayList<AppItem>();
		try 
		{
			Cursor cursor = null;
			String where = null;
			SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
			SQLiteDatabase db = DatabaseManager.mDbHelper.getReadableDatabase();
			qb.setTables(CONTENT_SORT_URI.getPathSegments().get(0));
			cursor = db.query(DOWNLOAD_TABLE, null, where, null, null, null, null);
			if (cursor != null) 
			{
				cursor.setNotificationUri(DatabaseManager.mContext.getContentResolver(), CONTENT_SORT_URI);
				int n = cursor.getCount();
				for (int i=0; i<n; i++) 
				{
					cursor.moveToPosition(i);
					AppItem appItem = new AppItem();
					appItem.setAppCode(cursor.getString(cursor.getColumnIndex(APP_CODE)));
					appItem.setAppName(cursor.getString(cursor.getColumnIndex(APP_NAME)));
					appItem.setPkgName(cursor.getString(cursor.getColumnIndex(PKG_NAME)));
					appItem.setIcon(cursor.getString(cursor.getColumnIndex(ICON)));
					appItem.setDeveloper(cursor.getString(cursor.getColumnIndex(DEVELOPER)));
					appItem.setVersion(cursor.getString(cursor.getColumnIndex(VERSION)));
					appItem.setShowVersion(cursor.getString(cursor.getColumnIndex(SHOW_VERSION)));
					appItem.setSize(cursor.getString(cursor.getColumnIndex(SIZE)));
					appItem.setSummary(cursor.getString(cursor.getColumnIndex(SUMMARY)));
					appItem.setMd5(cursor.getString(cursor.getColumnIndex(MD5)));
					appItem.setDownloadUrl(cursor.getString(cursor.getColumnIndex(DOWNLOAD_URL)));
					appItem.setImages(ScreenShotTable.queryScreenShot(appItem.getPkgName()));
					appList.add(appItem);
				}
			} 
			
			if (cursor != null) 
			{
				cursor.close();
				cursor = null;
			}
		} 
		catch (Exception e) 
		{
			LogUtil.i(LogUtil.TAG, " queryAppListInfo " + e);
		}
		return appList;
	}
	
	/**
	 * 
	 * @Title: insertAppList
	 * @Description: 插入应用信息
	 * @param appList
	 * @return
	 * @return: boolean
	 */
	public static boolean insertAppList(ArrayList<AppItem> appList)
	{
		if (DatabaseManager.mDbHelper == null) 
		{
			return false;
		}
		
		try 
		{
			SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
			SQLiteDatabase db = DatabaseManager.mDbHelper.getReadableDatabase();
			qb.setTables(CONTENT_SORT_URI.getPathSegments().get(0));
			
			db.beginTransaction();
			for (int i=0; i<appList.size(); i++) 
			{
				AppItem appItem = appList.get(i);
				deleteAppInfo(appItem.getPkgName());
				//插入app信息
				ContentValues values = new ContentValues();
				values.put(APP_CODE, appItem.getAppCode());
				values.put(APP_NAME, appItem.getAppName());
				values.put(PKG_NAME, appItem.getPkgName());
				values.put(ICON, appItem.getIcon());
				values.put(DEVELOPER, appItem.getDeveloper());
				values.put(VERSION, appItem.getVersion());
				values.put(SHOW_VERSION, appItem.getShowVersion());
				values.put(SIZE, appItem.getSize());
				values.put(SUMMARY, appItem.getSummary());
				values.put(MD5, appItem.getMd5());
				values.put(DOWNLOAD_URL, appItem.getDownloadUrl());
				db.insert(DOWNLOAD_TABLE, null, values);
				ScreenShotTable.insertScreenShot(appItem.getImages(), appItem.getPkgName());
			}
			db.setTransactionSuccessful();
			db.endTransaction();
		} 
		catch (Exception e) 
		{
			LogUtil.i(LogUtil.TAG, " insertAppList " + e);
		}
		return true;
	}

	/**
	 * 
	 * @Title: deleteAppInfo
	 * @Description: 删除应用
	 * @param pkgName
	 * @return
	 * @return: void
	 */
	public static void deleteAppInfo(String pkgName) 
	{
		if (DatabaseManager.mDbHelper == null) 
		{
			return;
		}
		
		String where = null;
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		where = PKG_NAME + "=\"" + pkgName + "\"";
		SQLiteDatabase db = DatabaseManager.mDbHelper.getReadableDatabase();
		qb.setTables(CONTENT_SORT_URI.getPathSegments().get(0));
		db.delete(DOWNLOAD_TABLE, where, null);
		ScreenShotTable.deleteScreenShot(pkgName);
	}
	
	/**
	 * 
	 * @Title: deleteAllAppInfo
	 * @Description: 删除所有appItem
	 * @return: void
	 */
	public static void deleteAllAppInfo() 
	{
		if (DatabaseManager.mDbHelper == null) 
		{
			return;
		}
		
		String where = null;
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		where = null;
		SQLiteDatabase db = DatabaseManager.mDbHelper.getReadableDatabase();
		qb.setTables(CONTENT_SORT_URI.getPathSegments().get(0));
		db.delete(DOWNLOAD_TABLE, where, null);
		ScreenShotTable.deleteAllScreenShot();
	}
}
