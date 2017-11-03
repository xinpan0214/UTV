/**
 * Copyright © 2015GreatVision. All rights reserved.
 *
 * @Title: DatabaseManager.java
 * @Prject: Mul2Launcher
 * @Package: com.sz.ead.framework.mul2launcher.db
 * @Description: 实现数据库管理
 * @author: zhaoqy  
 * @date: 2015-4-22 下午9:33:34
 */
package com.sz.ead.framework.mul2launcher.db;

import com.sz.ead.framework.mul2launcher.appstore.downloader.UpdateTable;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

public class DatabaseManager extends ContentProvider
{
	public static final String DATABASE_NAME = "data.db"; //数据库文件
	public static final int DATABASE_VERSION = 3;         //数据库版本 修改数据库要修改此代码
	public static SQLiteDatabase mSqlDB;     //全局数据库
	public static DatabaseHelper mDbHelper;  //数据库帮助器
	public static Context        mContext;   //应用上下文
	
	@Override
	public boolean onCreate() 
	{
		if (mDbHelper == null) 
		{
			mDbHelper = new DatabaseHelper(getContext());
			mContext = getContext();
		}
		return (mDbHelper == null) ? false : true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) 
	{
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		SQLiteDatabase db = mDbHelper.getReadableDatabase();
		qb.setTables(uri.getPathSegments().get(0));

		Cursor c = qb.query(db, projection, selection, null, null, null, sortOrder);
		c.setNotificationUri(getContext().getContentResolver(), uri);
		return c;
	}

	@Override
	public String getType(Uri uri) 
	{
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) 
	{
		return null;
	}

	@Override
	public int delete(Uri uri, String s, String[] as) 
	{
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		return db.delete(uri.getPathSegments().get(0), s, as);
	}

	@Override
	public int update(Uri uri, ContentValues values, String s, String[] as) 
	{
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		return db.update(uri.getPathSegments().get(0), values, s, as);
	}
	
	public static class DatabaseHelper extends SQLiteOpenHelper 
	{
		DatabaseHelper(Context context) 
		{
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) 
		{
			db.execSQL(DownLoadDB.getCreateSql());
			db.execSQL(UpdateTable.getCreateSql());
			db.execSQL(DownloadTable.getCreateSql());
			db.execSQL(InstalledTable.getCreateSql());
			db.execSQL(ScreenShotTable.getCreateSql());
			db.execSQL(InstallingTable.getCreateSql());
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) 
		{
			db.execSQL(DownLoadDB.getUpgradeSql());
			db.execSQL(UpdateTable.getUpgradeSql());
			db.execSQL(DownloadTable.getUpgradeSql());
			db.execSQL(InstalledTable.getUpgradeSql());
			db.execSQL(ScreenShotTable.getUpgradeSql());
			db.execSQL(InstallingTable.getCreateSql());
			onCreate(db);
		}
	}
}
