/**
 * Copyright © 2015GreatVision. All rights reserved.
 *
 * @Title: InstalledTable.java
 * @Prject: Mul2Launcher
 * @Package: com.sz.ead.framework.mul2launcher.db
 * @Description: 已安装的应用表
 * @author: zhaoqy  
 * @date: 2015-4-23 上午9:21:14
 */
package com.sz.ead.framework.mul2launcher.db;

import java.util.ArrayList;

import com.sz.ead.framework.mul2launcher.appstore.common.AppInfo;
import com.sz.ead.framework.mul2launcher.appstore.downloader.DownloadInfo;
import com.sz.ead.framework.mul2launcher.dataprovider.dataitem.AppItem;
import com.sz.ead.framework.mul2launcher.util.FileUtil;
import com.sz.ead.framework.mul2launcher.util.LogUtil;

import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;

public class InstallingTable
{
	// 表名
		public static final String TABLE_INSTALLED = "InstallingTable";
		// 类名
		public static final String AUTHORITY = "com.sz.ead.framework.appstore.db.DatabaseManager";
		// 访问uri
		public static final Uri CONTENT_INSTALLED_URI = Uri.parse("content://"
				+ AUTHORITY + "/" + TABLE_INSTALLED);

	// 数据库的属性列 
		public static final String ID = "_id";
			
		// 应用编码  包名
		public static final String APP_CODE = "app_code";
			
		// 应用名称
		public static final String APP_NAME = "app_name";
			
			//包名
		public static final String PKG_NAME = "pkg_name";
			
		// 应用图标地址
		public static final String ICON = "icon";
			
		// 用于后台升级比较的版本
		public static final String VERSION = "version";
			
		// 用于展示的版本
		public static final String SHOW_VERSION = "show_version";
			
		// 应用大小(单位：M)
		public static final String SIZE = "size";
			
		// 应用价格
		public static final String PRICE = "price";
			
		// 分类编码
		public static final String CLASSIFY_CODE = "classify_code";
			
		// 下载包的md5值
		public static final String MD5 = "md5";
			
		// 下载地址
		public static final String DOWNLOAD_URL = "download_url";
			

			
		// 当前下载的总字节数
		public static final String DOWN_TOTAL_BYTE = "down_total_byte";
				
		// 当前下载的字节数
		public static final String DOWN_CURRENT_BYTE = "down_current_byte";
			
		// 下载状态 收藏 暂停 准备下载 正在下载 下载完 0是未知状态 
		public static final String DOWN_STATUS = "down_status";
			
		// apk保存路径
		public static final String DOWN_APK_PATH = "down_apk_path";
			
		// 状态点下载进入下载队列还是点更新进入下载队列0为默认1为从更新进入 
		private static final String DOWN_ADDDOWNLIST_STYLE = "down_adddownlist_style";




		// filter
		public static final String TAG = "InstallTable";	
		// 控制打印
		public static boolean debug = true;
	
		public static String getCreateSql() {
			StringBuffer sb = new StringBuffer();
			sb.append("CREATE TABLE  IF NOT EXISTS ");
			sb.append(TABLE_INSTALLED);
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
			sb.append(VERSION);
			sb.append(" TEXT,");
			sb.append(SHOW_VERSION);
			sb.append(" TEXT,");	
			sb.append(SIZE);
			sb.append(" TEXT,");
			sb.append(PRICE);
			sb.append(" TEXT,");
			sb.append(CLASSIFY_CODE);
			sb.append(" TEXT,");
			sb.append(MD5);
			sb.append(" TEXT,");
			sb.append(DOWNLOAD_URL);
			sb.append(" TEXT,");
			sb.append(DOWN_TOTAL_BYTE);
			sb.append(" Integer,");
			sb.append(DOWN_CURRENT_BYTE);
			sb.append(" Integer,");
			sb.append(DOWN_STATUS);
			sb.append(" Integer,");
			sb.append(DOWN_APK_PATH);
			sb.append(" TEXT,");
			sb.append(DOWN_ADDDOWNLIST_STYLE);
			sb.append(" Integer");
			
			sb.append(");");
			
			return sb.toString();
		}

		public static String getUpgradeSql() {
			/*
			 * SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
			 * qb.setTables(CONTENT_SORT_URI.getPathSegments().get(0)); Cursor
			 * abcCursor=qb.query(db,null,null,null,null,null,null);
			 */
			String string = "DROP TABLE IF EXISTS " + TABLE_INSTALLED;
			return string;
		}


		public static ArrayList<DownloadInfo> queryInstalledList() {
			Cursor mCursor = null;
			String where = null;
			ArrayList<DownloadInfo> list = new ArrayList<DownloadInfo>();
			if(DatabaseManager.mDbHelper==null){
				return list;
			}
			SQLiteDatabase db = DatabaseManager.mDbHelper.getReadableDatabase();
			mCursor = db.query(TABLE_INSTALLED, null, where, null, null, null, null);
			if (mCursor != null) {
				int n = mCursor.getCount();
				for (int i = 0; i < n; i++) {
					mCursor.moveToPosition(i);
					DownloadInfo downinfo = null;
					downinfo = new DownloadInfo();
					downinfo.setAppCode(mCursor.getString(mCursor
							.getColumnIndex(APP_CODE)));
					downinfo.setAppName(mCursor.getString(mCursor
							.getColumnIndex(APP_NAME)));
					downinfo.setPkgName(mCursor.getString(mCursor
							.getColumnIndex(PKG_NAME)));
					downinfo.setIcon(mCursor.getString(mCursor
							.getColumnIndex(ICON)));
					downinfo.setVersion(mCursor.getString(mCursor
							.getColumnIndex(VERSION)));
					downinfo.setShowVersion(mCursor.getString(mCursor
							.getColumnIndex(SHOW_VERSION)));
					downinfo.setSize(mCursor.getString(mCursor
							.getColumnIndex(SIZE)));
					downinfo.setPrice(mCursor.getString(mCursor
							.getColumnIndex(PRICE)));
					downinfo.setClassifycode(mCursor.getString(mCursor
							.getColumnIndex(CLASSIFY_CODE)));
					downinfo.setMd5(mCursor.getString(mCursor
							.getColumnIndex(MD5)));
					
					downinfo.setDownloadurl(mCursor.getString(mCursor
							.getColumnIndex(DOWNLOAD_URL)));
					downinfo.setDownTotalBytes(mCursor.getInt(mCursor
							.getColumnIndex(DOWN_TOTAL_BYTE)));
					downinfo.setDownCurrentBytes(mCursor.getInt(mCursor
							.getColumnIndex(DOWN_CURRENT_BYTE)));
					downinfo.setDownStatus(mCursor.getInt(mCursor
							.getColumnIndex(DOWN_STATUS)));
					downinfo.setDownApkPath(mCursor.getString(mCursor
							.getColumnIndex(DOWN_APK_PATH)));
					downinfo.setAddDownListStyle(mCursor.getInt(mCursor
							.getColumnIndex(DOWN_ADDDOWNLIST_STYLE)));
					list.add(downinfo);
				}
			} else {
				if (debug)
					Log.i(TAG, "queryDowningList " + "0");
			}
			if(mCursor != null){
				mCursor.close();
				mCursor = null;
			}
			return list;
		}

		public static void deleteAInstalled(String pkgName) {
			String where;
			if(DatabaseManager.mDbHelper==null){
				return;
			}
			SQLiteDatabase db = DatabaseManager.mDbHelper.getWritableDatabase();
			where = PKG_NAME + "=\"" + pkgName + "\"";
			int n = db.delete(TABLE_INSTALLED, where, null);
			if (debug)
				Log.e(TAG, "deleteADownLoad" + String.valueOf(n) + " " + pkgName);
		}

		public static void insertAInstalled(DownloadInfo info) {
			if (info == null) {
				return;
			}
			if(DatabaseManager.mDbHelper==null){
				return;
			}
			SQLiteDatabase db = DatabaseManager.mDbHelper.getWritableDatabase();
			ContentValues values = new ContentValues();
			values.put(APP_CODE, info.getAppCode());
			values.put(APP_NAME, info.getAppName());
			values.put(PKG_NAME, info.getPkgName());
			values.put(ICON, info.getIcon());
			values.put(VERSION, info.getVersion());
			values.put(SHOW_VERSION, info.getShowVersion());
			values.put(SIZE, info.getSize());
			values.put(PRICE, info.getPrice());
			values.put(CLASSIFY_CODE, info.getClassifycode());
			values.put(MD5, info.getMd5());
			values.put(DOWNLOAD_URL, info.getDownloadurl());
			values.put(DOWN_TOTAL_BYTE, info.getDownTotalBytes());
			values.put(DOWN_CURRENT_BYTE, info.getDownCurrentBytes());
			values.put(DOWN_STATUS, info.getDownStatus());
			values.put(DOWN_APK_PATH, info.getDownApkPath());
			values.put(DOWN_ADDDOWNLIST_STYLE, info.getAddDownListStyle());

			db.insert(TABLE_INSTALLED, null, values);

		}
		
		public static void insertNoStoreInstall(AppInfo info) {
			if(info != null){
				DownloadInfo noAppStoreInstall = new DownloadInfo();
				noAppStoreInstall.setAppCode("");
				noAppStoreInstall.setAppName(info.mAppName);
				noAppStoreInstall.setVersion(info.mVersionName);
				noAppStoreInstall.setPkgName(info.mPackageName);
				InstallingTable.insertAInstalled(noAppStoreInstall);
			}
		}
		
		/*修改icon url地址*/
		public static void modifyIconUrl(String pkgName, String iconUrl) {
			String where;
			if(DatabaseManager.mDbHelper==null){
				return;
			}
			SQLiteDatabase db = DatabaseManager.mDbHelper.getWritableDatabase();	
			where = PKG_NAME + "=\"" + pkgName + "\"";

			ContentValues values = new ContentValues();
			values.put(ICON, iconUrl);
			int n = db.update(TABLE_INSTALLED, values, where, null);
		}
		
		public static DownloadInfo getInstallPkgInfo(String pkgName){
			if(DatabaseManager.mDbHelper==null){
				return null;
			}
			boolean bRet=false;
			Cursor mCursor = null;
			DownloadInfo downinfo = null;
			String where;
			SQLiteDatabase db = DatabaseManager.mDbHelper.getWritableDatabase();	
			where = PKG_NAME + "=" + "\""+pkgName + "\"";
			mCursor = db.query(TABLE_INSTALLED, null, where, null, null, null, null);
			if (mCursor != null) {
				int n = mCursor.getCount();
				if(n>0){
					mCursor.moveToPosition(0);
					
					downinfo = new DownloadInfo();
					downinfo.setAppCode(mCursor.getString(mCursor
							.getColumnIndex(APP_CODE)));
					downinfo.setAppName(mCursor.getString(mCursor
							.getColumnIndex(APP_NAME)));
					downinfo.setPkgName(mCursor.getString(mCursor
							.getColumnIndex(PKG_NAME)));
					downinfo.setIcon(mCursor.getString(mCursor
							.getColumnIndex(ICON)));
					downinfo.setVersion(mCursor.getString(mCursor
							.getColumnIndex(VERSION)));
					downinfo.setShowVersion(mCursor.getString(mCursor
							.getColumnIndex(SHOW_VERSION)));
					downinfo.setSize(mCursor.getString(mCursor
							.getColumnIndex(SIZE)));
					downinfo.setPrice(mCursor.getString(mCursor
							.getColumnIndex(PRICE)));
					downinfo.setClassifycode(mCursor.getString(mCursor
							.getColumnIndex(CLASSIFY_CODE)));
					downinfo.setMd5(mCursor.getString(mCursor
							.getColumnIndex(MD5)));
					
					downinfo.setDownloadurl(mCursor.getString(mCursor
							.getColumnIndex(DOWNLOAD_URL)));
					downinfo.setDownTotalBytes(mCursor.getInt(mCursor
							.getColumnIndex(DOWN_TOTAL_BYTE)));
					downinfo.setDownCurrentBytes(mCursor.getInt(mCursor
							.getColumnIndex(DOWN_CURRENT_BYTE)));
					downinfo.setDownStatus(mCursor.getInt(mCursor
							.getColumnIndex(DOWN_STATUS)));
					downinfo.setDownApkPath(mCursor.getString(mCursor
							.getColumnIndex(DOWN_APK_PATH)));
					downinfo.setAddDownListStyle(mCursor.getInt(mCursor
							.getColumnIndex(DOWN_ADDDOWNLIST_STYLE)));
				}
				
			} else {
			}
			
			if (mCursor != null) {
				mCursor.close();
				mCursor = null;
			}
			return downinfo;
		}
		
		public static boolean isInstalled(String pkgName){
			if(DatabaseManager.mDbHelper==null){
				return false;
			}
			boolean bRet=false;
			Cursor mCursor = null;
			String where;
			SQLiteDatabase db = DatabaseManager.mDbHelper.getWritableDatabase();	
			where = PKG_NAME + "=" + "\""+pkgName + "\"";
			mCursor = db.query(TABLE_INSTALLED, null, where, null, null, null, null);
			if (mCursor != null) {
				int n = mCursor.getCount();
				if(n>0){
					bRet=true;
				}
				
			} else {

				bRet=false;
			}
			
			if (mCursor != null) {
				mCursor.close();
				mCursor = null;
			}
			return bRet;
		}
		
		/**
		 * 防止桌面删除 更新某个apk数据库状态
		 */
		public static void freshInstalledTable(Context context, String pkgname)
		{
			if (!pkgname.equals(""))
			{
				try {
					PackageManager pm = context.getPackageManager();
	    			PackageInfo packageInfo = pm.getPackageInfo(pkgname, PackageManager.GET_PERMISSIONS);
	    			if(packageInfo == null){
	    				InstallingTable.deleteAInstalled(pkgname);
	    			}
				} catch (PackageManager.NameNotFoundException e) {
					//包未安装
					InstallingTable.deleteAInstalled(pkgname);
					Log.i(TAG, "queryDowningList " + e.toString());
				}
			}
		}
		
		/**
		 * 防止桌面删除更新全部 数据表没更新
		 */
		public static void freshInstalledTable(Context context) {
			Cursor mCursor = null;
			String where = null;
			ArrayList<DownloadInfo> list = new ArrayList<DownloadInfo>();
			if(DatabaseManager.mDbHelper==null){
				return ;
			}
			SQLiteDatabase db = DatabaseManager.mDbHelper.getReadableDatabase();
			mCursor = db.query(TABLE_INSTALLED, null, where, null, null, null, null);
			if (mCursor != null) {
				int n = mCursor.getCount();
				for (int i = 0; i < n; i++) {
					mCursor.moveToPosition(i);
					DownloadInfo downinfo = null;
					downinfo = new DownloadInfo();
					downinfo.setAppCode(mCursor.getString(mCursor
							.getColumnIndex(APP_CODE)));
					downinfo.setAppName(mCursor.getString(mCursor
							.getColumnIndex(APP_NAME)));
					downinfo.setPkgName(mCursor.getString(mCursor
							.getColumnIndex(PKG_NAME)));
					downinfo.setIcon(mCursor.getString(mCursor
							.getColumnIndex(ICON)));
					downinfo.setVersion(mCursor.getString(mCursor
							.getColumnIndex(VERSION)));
					downinfo.setShowVersion(mCursor.getString(mCursor
							.getColumnIndex(SHOW_VERSION)));
					downinfo.setSize(mCursor.getString(mCursor
							.getColumnIndex(SIZE)));
					downinfo.setPrice(mCursor.getString(mCursor
							.getColumnIndex(PRICE)));
					downinfo.setClassifycode(mCursor.getString(mCursor
							.getColumnIndex(CLASSIFY_CODE)));
					downinfo.setMd5(mCursor.getString(mCursor
							.getColumnIndex(MD5)));
					
					downinfo.setDownloadurl(mCursor.getString(mCursor
							.getColumnIndex(DOWNLOAD_URL)));
					downinfo.setDownTotalBytes(mCursor.getInt(mCursor
							.getColumnIndex(DOWN_TOTAL_BYTE)));
					downinfo.setDownCurrentBytes(mCursor.getInt(mCursor
							.getColumnIndex(DOWN_CURRENT_BYTE)));
					downinfo.setDownStatus(mCursor.getInt(mCursor
							.getColumnIndex(DOWN_STATUS)));
					downinfo.setDownApkPath(mCursor.getString(mCursor
							.getColumnIndex(DOWN_APK_PATH)));
					downinfo.setAddDownListStyle(mCursor.getInt(mCursor
							.getColumnIndex(DOWN_ADDDOWNLIST_STYLE)));
					if(downinfo.getPkgName() != null){
						try {
							//Log.v(TAG, "kkkkkkk"+downinfo.getPkgName());
							PackageManager pm = context.getPackageManager();
		        			PackageInfo packageInfo = pm.getPackageInfo(downinfo.getPkgName(), PackageManager.GET_PERMISSIONS);
		        			if(packageInfo == null){
		        				InstallingTable.deleteAInstalled(downinfo.getPkgName());
		        			}
						} catch (PackageManager.NameNotFoundException e) {
							//包未安装
							InstallingTable.deleteAInstalled(downinfo.getPkgName());
							Log.i(TAG, "queryDowningList " + e.toString());
						}
						
					}
				}
			} else {
				if (debug)
					Log.i(TAG, "queryDowningList " + "0");
			}
			if(mCursor != null){
				mCursor.close();
				mCursor = null;
			}
			return ;
		}
		
	/*public static String getCreateSql() 
	{
		*//**
		 * SQL语句：
		 * db.execSQL("CREATE TABLE  IF NOT EXISTS " + DOWNLOAD_TABLE + "( " + "_id INTEGER PRIMARY KEY AUTOINCREMENT," + 
		 * "app_code TEXT," + "app_name TEXT," + "pkg_name TEXT," + "APP_POSITION TEXT"+ "APP_SOURCE TEXT" + ");");
		 *//*	
		StringBuffer sb = new StringBuffer();
		sb.append("CREATE TABLE  IF NOT EXISTS ");
		sb.append(INSTALLED_TABLE);
		sb.append("( ");
		sb.append(ID);
		sb.append(" INTEGER PRIMARY KEY AUTOINCREMENT,");
		sb.append(APP_CODE);
		sb.append(" TEXT,");
		sb.append(APP_NAME);
		sb.append(" TEXT,");
		sb.append(PKG_NAME);
		sb.append(" TEXT,");
		sb.append(APP_POSITION);
		sb.append(" Integer,");
		sb.append(APP_SOURCE);
		sb.append(" TEXT");
		sb.append(");");
		
		return sb.toString();
	}
	
	public static String getUpgradeSql() 
	{
		*//**
		 * SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		 * qb.setTables(CONTENT_SORT_URI.getPathSegments().get(0)); Cursor
		 * abcCursor=qb.query(db,null,null,null,null,null,null);
		 *//*
		String string = "DROP TABLE IF EXISTS " + INSTALLED_TABLE;
		return string;
	}
	
	*//**
	 * 
	 * @Title: queryInstalledAppList
	 * @Description: 查询已安装的应用列表
	 * @return
	 * @return: ArrayList<AppItem>
	 *//*
	public static ArrayList<AppItem> queryInstalledAppList()
	{
		if (DatabaseManager.mDbHelper == null) 
		{
			return null;
		}
		
		Cursor cursor = null;
		String where = null;
		String orderBy = null;
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		ArrayList<AppItem> appList = new ArrayList<AppItem>();
		
		SQLiteDatabase db = DatabaseManager.mDbHelper.getReadableDatabase();
		qb.setTables(CONTENT_SORT_URI.getPathSegments().get(0));
		orderBy = APP_POSITION + " ASC";
		cursor = db.query(INSTALLED_TABLE, null, where, null, null, null, orderBy);
		if (cursor != null) 
		{
			cursor.setNotificationUri(DatabaseManager.mContext.getContentResolver(),CONTENT_SORT_URI);
			int n = cursor.getCount();
			for (int i=0; i<n; i++) 
			{
				try 
				{
					cursor.moveToPosition(i);
					AppItem appItem = new AppItem();
					appItem.setAppCode(cursor.getString(cursor.getColumnIndex(APP_CODE)));
					appItem.setAppName(cursor.getString(cursor.getColumnIndex(APP_NAME)));
					appItem.setPkgName(cursor.getString(cursor.getColumnIndex(PKG_NAME)));
					appItem.setPosition(cursor.getInt(cursor.getColumnIndex(APP_POSITION)));
					appItem.setIconBitmap(getIconBitmap(appItem.getPkgName()));
					
					if(appItem.getIconBitmap() != null)
					{
						appList.add(appItem);
					}	
				} 
				catch (Exception e) 
				{
					LogUtil.i(LogUtil.TAG, " queryAllAppList " + e.toString());
				}
			}
		} 
		
		if (cursor != null) 
		{
			cursor.close();
			cursor = null;
		}
		
		return appList;
	}
	
	*//**
	 * 
	 * @Title: queryAppInfo
	 * @Description: 通过包名查询应用信息
	 * @param pkgName
	 * @return
	 * @return: AppItem
	 *//*
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
			String where;
			SQLiteDatabase db = DatabaseManager.mDbHelper.getWritableDatabase();	
			where = PKG_NAME + "=\"" + pkgName + "\"";
			cursor = db.query(INSTALLED_TABLE, null, where, null, null, null, null);

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
					appItem.setPosition(cursor.getInt(cursor.getColumnIndex(APP_POSITION)));
					appItem.setIconBitmap(getIconBitmap(appItem.getPkgName()));
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
	
	*//**
	 * 
	 * @Title: queryAppInfo
	 * @Description: 通过应用位置查询应用信息
	 * @param postion
	 * @return
	 * @return: AppItem
	 *//*
	public static AppItem queryAppInfo(int postion) 
	{
		if (DatabaseManager.mDbHelper == null) 
		{
			return null;
		}
		
		AppItem appItem = null;
		try 
		{
			Cursor cursor = null;
			String where;
			SQLiteDatabase db = DatabaseManager.mDbHelper.getWritableDatabase();	
			where = APP_POSITION + "=" + postion;
			cursor = db.query(INSTALLED_TABLE, null, where, null, null, null, null);

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
					appItem.setPosition(cursor.getInt(cursor.getColumnIndex(APP_POSITION)));
					appItem.setIconBitmap(getIconBitmap(appItem.getPkgName()));
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
	
	*//**
	 * 
	 * @Title: insertApp
	 * @Description: 插入应用
	 * @param pkgName
	 * @return
	 * @return: boolean
	 *//*
	public static boolean insertApp(String pkgName) 
	{
		if (DatabaseManager.mDbHelper == null) 
		{
			return false;
		}
		
		if(queryAppInfo(pkgName) != null)
		{			
			return false;
		}
		
		try 
		{
			PackageManager pm = DatabaseManager.mContext.getPackageManager();
			PackageInfo packageInfo = pm.getPackageInfo(pkgName, PackageManager.GET_PERMISSIONS);

			AppItem info = new AppItem();
			info.setAppName(packageInfo.applicationInfo.loadLabel(DatabaseManager.mContext.getPackageManager()).toString());
			info.setPkgName(packageInfo.packageName);
			
			Cursor cursor = null;
			SQLiteDatabase db = DatabaseManager.mDbHelper.getWritableDatabase();
			cursor = db.query(INSTALLED_TABLE, null, null, null, null, null, null);
			if (cursor != null) 
			{
				int n = cursor.getCount();
				info.setPosition(n);
			} 
			
			if (cursor != null) 
			{
				cursor.close();
				cursor = null;
			}
			
			ContentValues values = new ContentValues();
			values.put(APP_CODE, info.getAppCode());
			values.put(APP_NAME, info.getAppName());
			values.put(PKG_NAME, info.getPkgName());
			values.put(APP_POSITION, info.getPosition());
			db.insert(INSTALLED_TABLE, null, values);
		} 
		catch (Exception e) 
		{
			LogUtil.i(LogUtil.TAG, " insertApp " + e);
		}
		return true;
	}
	
	*//**
	 * 
	 * @Title: deleteApp
	 * @Description: 删除应用
	 * @param pkgName
	 * @return
	 * @return: boolean
	 *//*
	public static boolean deleteApp(String pkgName) 
	{
		if (DatabaseManager.mDbHelper == null) 
		{
			return false;
		}
		
		try 
		{
			LogUtil.d(LogUtil.TAG, " deleteApp package name " + pkgName);
			AppItem appinfo = queryAppInfo(pkgName);
			
			Cursor cursor = null;
			String where = null;
			SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
			SQLiteDatabase db = DatabaseManager.mDbHelper.getReadableDatabase();
			qb.setTables(CONTENT_SORT_URI.getPathSegments().get(0));
			where = APP_POSITION + ">" + appinfo.getPosition();
			cursor = db.query(INSTALLED_TABLE, null, where, null, null, null, null);
			if (cursor != null) 
			{
				cursor.setNotificationUri(DatabaseManager.mContext.getContentResolver(), CONTENT_SORT_URI);
				int n = cursor.getCount();
				LogUtil.i(LogUtil.TAG, " queryAllApp " + String.valueOf(n));
					
				for (int i = 0; i < n; i++) 
				{
					cursor.moveToPosition(i);
					where = PKG_NAME + "=\"" + cursor.getString(cursor.getColumnIndex(PKG_NAME)) + "\"";
					ContentValues values = new ContentValues();
					values.put(APP_POSITION, cursor.getInt(cursor.getColumnIndex(APP_POSITION))-1);
					db.update(INSTALLED_TABLE, values, where, null);
				}
			} 
			
			where = PKG_NAME + "=\"" + pkgName + "\"";
			db.delete(INSTALLED_TABLE, where, null);
			if (cursor != null) 
			{
				cursor.close();
				cursor = null;
			}
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
			LogUtil.d(LogUtil.TAG, " deleteApp " + e);
		}
		return true;
	}
	
	*//**
	 * 
	 * @Title: changeAppPostion
	 * @Description: 改变应用位置
	 * @param src
	 * @param dst
	 * @return: void
	 *//*
	public static void changeAppPostion(int src, int dst)
	{
		if (DatabaseManager.mDbHelper == null) 
		{
			return;
		}
		AppItem srcInfo, dstInfo;
		srcInfo = queryAppInfo(src);
		dstInfo = queryAppInfo(dst);
		
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		SQLiteDatabase db = DatabaseManager.mDbHelper.getReadableDatabase();
		qb.setTables(CONTENT_SORT_URI.getPathSegments().get(0));
		String where = PKG_NAME + "=\"" + srcInfo.getPkgName() + "\"";
		ContentValues values = new ContentValues();
		values.put(APP_POSITION, dstInfo.getPosition());
		db.update(INSTALLED_TABLE, values, where, null);
		
		where = PKG_NAME + "=\"" + dstInfo.getPkgName() + "\"";
		values.clear();
		values.put(APP_POSITION, srcInfo.getPosition());
		db.update(INSTALLED_TABLE, values, where, null);
	}
	
	*//**
	 * 
	 * @Title: MoveAppPostion
	 * @Description: 移动应用位置
	 * @param src
	 * @param dst
	 * @return: void
	 *//*
	public static void MoveAppPostion(int src, int dst) 
	{
		if (DatabaseManager.mDbHelper == null) 
		{
			return;
		}
		
		AppItem srcInfo, dstInfo;
		srcInfo = queryAppInfo(src);
		dstInfo = queryAppInfo(dst);
		
		Cursor mCursor = null;
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		SQLiteDatabase db = DatabaseManager.mDbHelper.getReadableDatabase();
		qb.setTables(CONTENT_SORT_URI.getPathSegments().get(0));
		String where;
		int moveIndex = 0;
		if (src > dst) 
		{
			where = APP_POSITION + ">=" + dst + " " + "AND" + " " + APP_POSITION + "<" + src;
			moveIndex = +1;
		} 
		else 
		{
			where = APP_POSITION + ">" + src + " " + "AND" + " " + APP_POSITION + "<=" + dst;
			moveIndex = -1;
		}

		mCursor = db.query(INSTALLED_TABLE, null, where, null, null, null, null);
		int n = mCursor.getCount();
		LogUtil.i(LogUtil.TAG, " MoveAppPostion " + String.valueOf(n));
		
		for (int i = 0; i < n; i++) 
		{
			mCursor.moveToPosition(i);
			where = PKG_NAME + "=\"" + mCursor.getString(mCursor.getColumnIndex(PKG_NAME))+ "\"";
			ContentValues values = new ContentValues();
			values.put(APP_POSITION, mCursor.getInt(mCursor.getColumnIndex(APP_POSITION)) + moveIndex);
			db.update(INSTALLED_TABLE, values, where, null);
		}

		where = PKG_NAME + "=\"" + srcInfo.getPkgName() + "\"";
		ContentValues values = new ContentValues();
		values.put(APP_POSITION, dstInfo.getPosition());
		db.update(INSTALLED_TABLE, values, where, null);
		
		if (mCursor != null) 
		{
			mCursor.close();
			mCursor = null;
		}
		
		if(db != null)
		{
			db.close();
		}
	}
	
	*//**
	 * 
	 * @Title: updateInstalledAppLang
	 * @Description: 更新所有已安装应用的语言
	 * @return: void
	 *//*
	public static void updateInstalledAppLang()
	{
		if (DatabaseManager.mDbHelper == null) 
		{
			return;
		}
		
		ArrayList<String> pagNames = new ArrayList<String>();
		Cursor mCursor = null;
		String where = null;
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();	
		SQLiteDatabase db = DatabaseManager.mDbHelper.getReadableDatabase();
		qb.setTables(CONTENT_SORT_URI.getPathSegments().get(0));
		mCursor = db.query(INSTALLED_TABLE, null, where, null, null, null, null);
		while (mCursor.moveToNext()) {
			pagNames.add(mCursor.getString(mCursor.getColumnIndex(PKG_NAME)));
		}
		mCursor.close();
		db.close();
		updateAppLang(pagNames);
	}
	
	*//**
	 * 
	 * @Title: updateAppLang
	 * @Description: 更新应用语言
	 * @param pagNames
	 * @return: void
	 *//*
	public static void updateAppLang(ArrayList<String> pagNames)
	{
		if (DatabaseManager.mDbHelper == null) 
		{
			return;
		}
		
		for (int i = 0; i < pagNames.size(); i++) 
		{
			SQLiteDatabase db = null;
			try 
			{
				PackageManager pm = DatabaseManager.mContext.getPackageManager();
				PackageInfo packageInfo = pm.getPackageInfo(pagNames.get(i), PackageManager.GET_PERMISSIONS);
				String where;
				db = DatabaseManager.mDbHelper.getWritableDatabase();	
				where = PKG_NAME + "=?";
				ContentValues values = new ContentValues();
				values.put(APP_NAME, packageInfo.applicationInfo.loadLabel(DatabaseManager.mContext.getPackageManager()).toString());
				db.update(INSTALLED_TABLE, values, where, new String[]{pagNames.get(i)});
			}
			catch (Exception e) 
			{
				LogUtil.i(LogUtil.TAG, "updateLang: " + e);
			}
			finally
			{
				if(db != null && db.isOpen())
				{
					db.close();
				}
			}
		}
	}
	
	*//**
	 * 
	 * @Title: updateAppSource
	 * @Description: 更新应用来源
	 * @param pagName
	 * @param source
	 * @return: void
	 *//*
	public static void updateAppSource(String pagName, int source)
	{
		if (DatabaseManager.mDbHelper == null) 
		{
			return;
		}
		
		SQLiteDatabase db = null;
		try 
		{
			String where;
			db = DatabaseManager.mDbHelper.getWritableDatabase();	
			where = PKG_NAME + "=?";
			ContentValues values = new ContentValues();
			values.put(APP_SOURCE, source);
			db.update(INSTALLED_TABLE, values, where, new String[]{pagName});
			LogUtil.i(LogUtil.TAG, " installedtable updateSource ");
		}
		catch (Exception e) 
		{
			LogUtil.i(LogUtil.TAG, " updateSource: " + e);
		}
		finally
		{
			if(db != null && db.isOpen())
			{
				db.close();
			}
		}
	}
	
	*//**
	 * 
	 * @Title: getIconBitmap
	 * @Description: 获取应用图标位图
	 * @param pkgName
	 * @return
	 * @return: Drawable
	 *//*
	@SuppressWarnings("deprecation")
	private static Drawable getIconBitmap(String pkgName)
	{
		Drawable drawable = null;
		PackageManager pm = DatabaseManager.mContext.getPackageManager();
		PackageInfo packageInfo = null;
		
		try 
		{
			packageInfo = pm.getPackageInfo(pkgName, PackageManager.GET_PERMISSIONS);
		} 
		catch (Exception e) 
		{
			return null;
		}
		
		try 
		{
			//取商城图片, 如果内存 存在就不要取了
			Bitmap bitmap = null;
			bitmap = BitmapFactory.decodeFile(FileUtil.APPICON_PATH + "/" + pkgName + ".png");
			if(bitmap != null)
			{
				drawable = new BitmapDrawable(bitmap);
			}
			else 
			{
				drawable = packageInfo.applicationInfo.loadIcon(pm);
			}
		} 
		catch (OutOfMemoryError e)
		{
			drawable = packageInfo.applicationInfo.loadIcon(pm);
		} 
		catch(Exception e)
		{
			drawable = packageInfo.applicationInfo.loadIcon(pm);
		}			
		return drawable;
	}
	
	public static void deleteAInstalled(String pkgName) {
		String where;
		if(DatabaseManager.mDbHelper==null){
			return;
		}
		SQLiteDatabase db = DatabaseManager.mDbHelper.getWritableDatabase();
		where = PKG_NAME + "=\"" + pkgName + "\"";
		int n = db.delete(TABLE_INSTALLED, where, null);
	}
	
	public static boolean isInstalled(String pkgName){
		if(DatabaseManager.mDbHelper==null){
			return false;
		}
		boolean bRet=false;
		Cursor mCursor = null;
		String where;
		SQLiteDatabase db = DatabaseManager.mDbHelper.getWritableDatabase();	
		where = PKG_NAME + "=" + "\""+pkgName + "\"";
		mCursor = db.query(TABLE_INSTALLED, null, where, null, null, null, null);
		if (mCursor != null) {
			int n = mCursor.getCount();
			if(n>0){
				bRet=true;
			}
			
		} else {

			bRet=false;
		}
		
		if (mCursor != null) {
			mCursor.close();
			mCursor = null;
		}
		return bRet;
	}
	
	public static void insertAInstalled(DownloadInfo info) {
		if (info == null) {
			return;
		}
		if(DatabaseManager.mDbHelper==null){
			return;
		}
		SQLiteDatabase db = DatabaseManager.mDbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(APP_CODE, info.getAppCode());
		values.put(APP_NAME, info.getAppName());
		values.put(PKG_NAME, info.getPkgName());
		values.put(ICON, info.getIcon());
		values.put(VERSION, info.getVersion());
		values.put(SHOW_VERSION, info.getShowVersion());
		values.put(SIZE, info.getSize());
		values.put(PRICE, info.getPrice());
		values.put(CLASSIFY_CODE, info.getClassifycode());
		values.put(MD5, info.getMd5());
		values.put(DOWNLOAD_URL, info.getDownloadurl());
		values.put(DOWN_TOTAL_BYTE, info.getDownTotalBytes());
		values.put(DOWN_CURRENT_BYTE, info.getDownCurrentBytes());
		values.put(DOWN_STATUS, info.getDownStatus());
		values.put(DOWN_APK_PATH, info.getDownApkPath());
		values.put(DOWN_ADDDOWNLIST_STYLE, info.getAddDownListStyle());

		db.insert(TABLE_INSTALLED, null, values);
	}
	
	public static void insertNoStoreInstall(AppInfo info) {
		if(info != null){
			DownloadInfo noAppStoreInstall = new DownloadInfo();
			noAppStoreInstall.setAppCode("");
			noAppStoreInstall.setAppName(info.mAppName);
			noAppStoreInstall.setVersion(info.mVersionName);
			noAppStoreInstall.setPkgName(info.mPackageName);
			InstalledTable.insertAInstalled(noAppStoreInstall);
		}
	}
	
	public static ArrayList<DownloadInfo> queryInstalledList() {
		Cursor mCursor = null;
		String where = null;
		ArrayList<DownloadInfo> list = new ArrayList<DownloadInfo>();
		if(DatabaseManager.mDbHelper==null){
			return list;
		}
		SQLiteDatabase db = DatabaseManager.mDbHelper.getReadableDatabase();
		mCursor = db.query(TABLE_INSTALLED, null, where, null, null, null, null);
		if (mCursor != null) {
			int n = mCursor.getCount();
			for (int i = 0; i < n; i++) {
				mCursor.moveToPosition(i);
				DownloadInfo downinfo = null;
				downinfo = new DownloadInfo();
				downinfo.setAppCode(mCursor.getString(mCursor
						.getColumnIndex(APP_CODE)));
				downinfo.setAppName(mCursor.getString(mCursor
						.getColumnIndex(APP_NAME)));
				downinfo.setPkgName(mCursor.getString(mCursor
						.getColumnIndex(PKG_NAME)));
				downinfo.setIcon(mCursor.getString(mCursor
						.getColumnIndex(ICON)));
				downinfo.setVersion(mCursor.getString(mCursor
						.getColumnIndex(VERSION)));
				downinfo.setShowVersion(mCursor.getString(mCursor
						.getColumnIndex(SHOW_VERSION)));
				downinfo.setSize(mCursor.getString(mCursor
						.getColumnIndex(SIZE)));
				downinfo.setPrice(mCursor.getString(mCursor
						.getColumnIndex(PRICE)));
				downinfo.setClassifycode(mCursor.getString(mCursor
						.getColumnIndex(CLASSIFY_CODE)));
				downinfo.setMd5(mCursor.getString(mCursor
						.getColumnIndex(MD5)));
				
				downinfo.setDownloadurl(mCursor.getString(mCursor
						.getColumnIndex(DOWNLOAD_URL)));
				downinfo.setDownTotalBytes(mCursor.getInt(mCursor
						.getColumnIndex(DOWN_TOTAL_BYTE)));
				downinfo.setDownCurrentBytes(mCursor.getInt(mCursor
						.getColumnIndex(DOWN_CURRENT_BYTE)));
				downinfo.setDownStatus(mCursor.getInt(mCursor
						.getColumnIndex(DOWN_STATUS)));
				downinfo.setDownApkPath(mCursor.getString(mCursor
						.getColumnIndex(DOWN_APK_PATH)));
				downinfo.setAddDownListStyle(mCursor.getInt(mCursor
						.getColumnIndex(DOWN_ADDDOWNLIST_STYLE)));
				list.add(downinfo);
			}
		} else {
			
		}
		if(mCursor != null){
			mCursor.close();
			mCursor = null;
		}
		return list;
	}
	
	public static DownloadInfo getInstallPkgInfo(String pkgName){
		if(DatabaseManager.mDbHelper==null){
			return null;
		}
		boolean bRet=false;
		Cursor mCursor = null;
		DownloadInfo downinfo = null;
		String where;
		SQLiteDatabase db = DatabaseManager.mDbHelper.getWritableDatabase();	
		where = PKG_NAME + "=" + "\""+pkgName + "\"";
		mCursor = db.query(TABLE_INSTALLED, null, where, null, null, null, null);
		if (mCursor != null) {
			int n = mCursor.getCount();
			if(n>0){
				mCursor.moveToPosition(0);
				
				downinfo = new DownloadInfo();
				downinfo.setAppCode(mCursor.getString(mCursor
						.getColumnIndex(APP_CODE)));
				downinfo.setAppName(mCursor.getString(mCursor
						.getColumnIndex(APP_NAME)));
				downinfo.setPkgName(mCursor.getString(mCursor
						.getColumnIndex(PKG_NAME)));
				downinfo.setIcon(mCursor.getString(mCursor
						.getColumnIndex(ICON)));
				downinfo.setVersion(mCursor.getString(mCursor
						.getColumnIndex(VERSION)));
				downinfo.setShowVersion(mCursor.getString(mCursor
						.getColumnIndex(SHOW_VERSION)));
				downinfo.setSize(mCursor.getString(mCursor
						.getColumnIndex(SIZE)));
				downinfo.setPrice(mCursor.getString(mCursor
						.getColumnIndex(PRICE)));
				downinfo.setClassifycode(mCursor.getString(mCursor
						.getColumnIndex(CLASSIFY_CODE)));
				downinfo.setMd5(mCursor.getString(mCursor
						.getColumnIndex(MD5)));
				
				downinfo.setDownloadurl(mCursor.getString(mCursor
						.getColumnIndex(DOWNLOAD_URL)));
				downinfo.setDownTotalBytes(mCursor.getInt(mCursor
						.getColumnIndex(DOWN_TOTAL_BYTE)));
				downinfo.setDownCurrentBytes(mCursor.getInt(mCursor
						.getColumnIndex(DOWN_CURRENT_BYTE)));
				downinfo.setDownStatus(mCursor.getInt(mCursor
						.getColumnIndex(DOWN_STATUS)));
				downinfo.setDownApkPath(mCursor.getString(mCursor
						.getColumnIndex(DOWN_APK_PATH)));
				downinfo.setAddDownListStyle(mCursor.getInt(mCursor
						.getColumnIndex(DOWN_ADDDOWNLIST_STYLE)));
			}
			
		} else {
		}
		
		if (mCursor != null) {
			mCursor.close();
			mCursor = null;
		}
		return downinfo;
	}
	
	修改icon url地址
	public static void modifyIconUrl(String pkgName, String iconUrl) {
		String where;
		if(DatabaseManager.mDbHelper==null){
			return;
		}
		SQLiteDatabase db = DatabaseManager.mDbHelper.getWritableDatabase();	
		where = PKG_NAME + "=\"" + pkgName + "\"";

		ContentValues values = new ContentValues();
		values.put(ICON, iconUrl);
		int n = db.update(TABLE_INSTALLED, values, where, null);
	}*/
}
