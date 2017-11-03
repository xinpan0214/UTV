/**
 * Copyright © 2015GreatVision. All rights reserved.
 *
 * @Title: ScreenShotTable.java
 * @Prject: Mul2Launcher
 * @Package: com.sz.ead.framework.mul2launcher.db
 * @Description: 应用截图表
 * @author: zhaoqy  
 * @date: 2015-4-23 上午9:56:01
 */
package com.sz.ead.framework.mul2launcher.db;

import java.util.ArrayList;
import com.sz.ead.framework.mul2launcher.dataprovider.dataitem.ScreenShotItem;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

public class ScreenShotTable 
{
	public static final String AUTHORITY = "com.sz.ead.framework.mul2launcher.db.DatabaseManager";           //类名
	public static final String SCREENSHOT_TABLE = "screenshottable";                                         //表名
	public static final Uri CONTENT_SORT_URI = Uri.parse("content://" + AUTHORITY + "/" + SCREENSHOT_TABLE); //访问uri
	public static final String ID = "_id";            //主键
	public static final String PKG_NAME = "pkg_name"; //包名
	public static final String IMAGE = "image";       //应用截图地址
	
	public static String getCreateSql() 
	{
		/**
		 * SQL语句：
		 * db.execSQL("CREATE TABLE  IF NOT EXISTS " + SCREENSHOT_TABLE + "( " + "_id INTEGER PRIMARY KEY AUTOINCREMENT," + 
		 * "pkg_name TEXT," + "image TEXT" + ");");
		 */	
		StringBuffer sb = new StringBuffer();
		sb.append("CREATE TABLE  IF NOT EXISTS ");
		sb.append(SCREENSHOT_TABLE);
		sb.append("( ");
		sb.append(ID);
		sb.append(" INTEGER PRIMARY KEY AUTOINCREMENT,");
		sb.append(PKG_NAME);
		sb.append(" TEXT,");
		sb.append(IMAGE);
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
		String string = "DROP TABLE IF EXISTS " + SCREENSHOT_TABLE;
		return string;
	}
	
	/**
	 * 
	 * @Title: queryScreenShot
	 * @Description: 查询应用截图
	 * @param pkgname
	 * @return
	 * @return: ArrayList<ScreenShotItem>
	 */
	public static ArrayList<ScreenShotItem> queryScreenShot(String pkgName)
	{
		if (DatabaseManager.mDbHelper == null) 
		{
			return null;
		}
		
		ArrayList<ScreenShotItem> screenshotList = new ArrayList<ScreenShotItem>();
		Cursor cursor = null;
		String where = null;
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		where = PKG_NAME + "=\"" + pkgName + "\"";
		SQLiteDatabase db = DatabaseManager.mDbHelper.getReadableDatabase();
		qb.setTables(CONTENT_SORT_URI.getPathSegments().get(0));
		cursor = db.query(SCREENSHOT_TABLE, null, where, null, null, null, null);
		if (cursor != null) 
		{
			cursor.setNotificationUri(DatabaseManager.mContext.getContentResolver(),CONTENT_SORT_URI);
			int n = cursor.getCount();
			for (int i=0; i<n; i++) 
			{
				cursor.moveToPosition(i);
				ScreenShotItem item = new ScreenShotItem();
				item.setImageUrl(cursor.getString(cursor.getColumnIndex(IMAGE)));
				screenshotList.add(item);
			}
		} 
		
		if (cursor != null) 
		{
			cursor.close();
			cursor = null;
		}
		return screenshotList;
	}
	
	/**
	 * 
	 * @Title: insertScreenShot
	 * @Description: 插入应用截图
	 * @param screenshotList
	 * @param pkgname
	 * @return: void
	 */
	public static void insertScreenShot(ArrayList<ScreenShotItem> screenshotList, String pkgName)
	{
		if (DatabaseManager.mDbHelper == null) 
		{
			return ;
		}
		
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		SQLiteDatabase db = DatabaseManager.mDbHelper.getReadableDatabase();
		qb.setTables(CONTENT_SORT_URI.getPathSegments().get(0));
		
		db.beginTransaction();
		for (int i=0; i<screenshotList.size(); i++) 
		{
			ScreenShotItem item = screenshotList.get(i);
			ContentValues values = new ContentValues();
			values.put(PKG_NAME, pkgName);
			values.put(IMAGE, item.getImageUrl());
			db.insert(SCREENSHOT_TABLE, null, values);
		}
		db.setTransactionSuccessful();
		db.endTransaction();
	}
	
	/**
	 * 
	 * @Title: deleteScreenShot
	 * @Description: 删除应用截图
	 * @param pkgname
	 * @return: void
	 */
	public static void deleteScreenShot(String pkgName)
	{
		if (DatabaseManager.mDbHelper == null) 
		{
			return ;
		}
		
		String where = null;
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		where = PKG_NAME + "=\"" + pkgName + "\"";
		SQLiteDatabase db = DatabaseManager.mDbHelper.getReadableDatabase();
		qb.setTables(CONTENT_SORT_URI.getPathSegments().get(0));
		db.delete(SCREENSHOT_TABLE, where, null);
	}
	
	/**
	 * 
	 * @Title: deleteAllScreenShot
	 * @Description: 删除所有截图
	 * @return: void
	 */
	public static void deleteAllScreenShot()
	{
		if (DatabaseManager.mDbHelper == null) 
		{
			return ;
		}
		
		String where = null;
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		where = null;
		SQLiteDatabase db = DatabaseManager.mDbHelper.getReadableDatabase();
		qb.setTables(CONTENT_SORT_URI.getPathSegments().get(0));
		db.delete(SCREENSHOT_TABLE, where, null);
	}
}
