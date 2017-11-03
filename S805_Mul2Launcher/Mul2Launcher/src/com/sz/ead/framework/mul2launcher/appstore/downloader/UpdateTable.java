package com.sz.ead.framework.mul2launcher.appstore.downloader;


import com.sz.ead.framework.mul2launcher.dataprovider.dataitem.UpdateInfoItem;
import com.sz.ead.framework.mul2launcher.db.DatabaseManager;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

public class UpdateTable {
	// 表名
	public static final String TABLE_UPDATE = "updatetable";
	// 类名
	public static final String AUTHORITY = "com.sz.ead.framework.appstore.db.DatabaseManager";
	// 访问uri
	public static final Uri CONTENT_UPDATE_URI = Uri.parse("content://"
			+ AUTHORITY + "/" + TABLE_UPDATE);
	

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
	
		
	// 下载包的md5值
	public static final String MD5 = "md5";
		
	// 下载地址
	public static final String DOWNLOAD_URL = "download_url";
	
	//更新标志 0为不忽略 1为忽略更新
	public static final String IGNORE_UPDATE_FLAG="ignore_update_flag";
	
	public static final int IGNORE_UPDATE_STATUS_NO=0;
	public static final int IGNORE_UPDATE_STATUS_YES=1;
	
	
	// filter
	public static final String TAG = "UpdateTable";	
	// 控制打印
	public static boolean debug = true;

	public static String getCreateSql() {
		StringBuffer sb = new StringBuffer();
		sb.append("CREATE TABLE  IF NOT EXISTS ");
		sb.append(TABLE_UPDATE);
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
		sb.append(" Integer,");
		sb.append(MD5);
		sb.append(" TEXT,");
		sb.append(DOWNLOAD_URL);
		sb.append(" TEXT,");
		sb.append(IGNORE_UPDATE_FLAG);
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
		String string = "DROP TABLE IF EXISTS " + TABLE_UPDATE;
		return string;
	}
	
	/**
	 * 插入一条更新记录
	 * @author liyingying
	 * @param info
	 */
	public static void InsertUpdate(UpdateInfoItem info){
		if (info == null || DatabaseManager.mDbHelper==null) {
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
		values.put(MD5, info.getMd5());
		values.put(DOWNLOAD_URL, info.getDownloadurl());
		values.put(IGNORE_UPDATE_FLAG, IGNORE_UPDATE_STATUS_NO);
		
		db.insert(TABLE_UPDATE, null, values);
	}
	
	public static void DeleteUpdate(String pkgName){
		if(DatabaseManager.mDbHelper==null){
			return;
		}
		String where;
		SQLiteDatabase db = DatabaseManager.mDbHelper.getWritableDatabase();
		
		where = PKG_NAME + "=" + "\""+pkgName + "\"";
		db.delete(TABLE_UPDATE, where, null);
	}
	
	/**
	 * 改变包的更新状态
	 * @author liyingying
	 * @param pachage
	 * @param status
	 */
	public static void UpdateIngoreStatus(String pkgName,int status){
		if(DatabaseManager.mDbHelper==null){
			return;
		}
		String where;
		
		SQLiteDatabase db = DatabaseManager.mDbHelper.getWritableDatabase();
	
		where = PKG_NAME+ "=" + "\""+pkgName + "\"";
		ContentValues values = new ContentValues();
		values.put(IGNORE_UPDATE_FLAG, status);
		int n = db.update(TABLE_UPDATE, values, where, null);
		
	}
	
	/**
	 * 查询更新
	 * @author liyingying
	 * @param packagename
	 */
	public static UpdateInfoItem QueryUpdate(String pkgName){
		Cursor cursor = null;
		String where;
		if(DatabaseManager.mDbHelper==null){
			return null;
		}
		SQLiteDatabase db = DatabaseManager.mDbHelper.getWritableDatabase();
		UpdateInfoItem info = null;

		where = PKG_NAME + "=" + "\""+pkgName + "\"";
		cursor = db.query(TABLE_UPDATE, null, where, null, null, null, null);

		if (cursor != null) {
			int n = cursor.getCount();
			if (n > 0) {
				cursor.moveToFirst();
				info = new UpdateInfoItem();
				info.setAppCode(cursor.getString(cursor.getColumnIndex(APP_CODE)));
				info.setAppName(cursor.getString(cursor.getColumnIndex(APP_NAME)));
				info.setPkgName(cursor.getString(cursor.getColumnIndex(PKG_NAME)));
				info.setIcon(cursor.getString(cursor.getColumnIndex(ICON)));
				info.setVersion(cursor.getString(cursor.getColumnIndex(VERSION)));
				info.setShowVersion(cursor.getString(cursor.getColumnIndex(SHOW_VERSION)));
				info.setSize(cursor.getString(cursor.getColumnIndex(SIZE)));
				info.setMd5(cursor.getString(cursor.getColumnIndex(MD5)));
				info.setDownloadurl(cursor.getString(cursor.getColumnIndex(DOWNLOAD_URL)));
				info.setIgron(cursor.getInt(cursor.getColumnIndex(IGNORE_UPDATE_FLAG)));


			}
		} else {

		}
		if (cursor != null) {
			cursor.close();
			cursor = null;
		}
		return info;
	}

	/**
	 * 是否存在更新在数据库
	 * @author liyingying
	 * @param packagename
	 * @return warning "\""+packagename + "\"";字符串一定要转义
	 */
	public static boolean isExistUpdate(String pkgName){
		if(DatabaseManager.mDbHelper==null){
			return false;
		}
		boolean bRet=false;
		Cursor mCursor = null;
		String where;
		SQLiteDatabase db = DatabaseManager.mDbHelper.getWritableDatabase();	
		where = PKG_NAME + "=" + "\""+pkgName + "\"";
		mCursor = db.query(TABLE_UPDATE, null, where, null, null, null, null);
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
	 * 查询更新
	 * @author liyingying
	 * @param packagename
	 */
	public static Cursor QueryAllUpdate(){
		Cursor cursor = null;
		String where;
		if (debug)
			Log.i(TAG, "QueryAllUpdate " + "mDbHelper = "+DatabaseManager.mDbHelper);
		if(DatabaseManager.mDbHelper==null){
			return null;
		}
		SQLiteDatabase db = DatabaseManager.mDbHelper.getWritableDatabase();	
		UpdateInfoItem info = null;

		where = IGNORE_UPDATE_FLAG + "=" + IGNORE_UPDATE_STATUS_NO;
		cursor = db.query(TABLE_UPDATE, null, where, null, null, null, null);
		if (debug)
			Log.i(TAG, "QueryAllUpdate " + "cursor = "+cursor);
		return cursor;
	}
	
	/**
	 * 查询更新个数
	 * @author liyingying
	 * @param packagename
	 */
	public static int QueryUpdateCount(){
		Cursor cursor = null;
		String where;
		if(DatabaseManager.mDbHelper==null){
			return 0;
		}
		SQLiteDatabase db = DatabaseManager.mDbHelper.getWritableDatabase();
		where = IGNORE_UPDATE_FLAG + "=" + IGNORE_UPDATE_STATUS_NO;
		cursor = db.query(TABLE_UPDATE, null, where, null, null, null, null);
		
		if(cursor!=null){
			return cursor.getCount();
		}else{
			return 0;
		}

	}
	
	
	/**
	 * cursor中取出一条更新记录
	 * @author liyingying
	 * @param cursor cursor
	 * @param pos 位置
	 * @return 记录
	 */
	public static UpdateInfoItem getARecord(Cursor cursor, int pos) {
		UpdateInfoItem info = null;
		if (cursor != null && pos < cursor.getCount()) {
			if (pos != -1) {
				cursor.moveToPosition(pos);
			}
			info = new UpdateInfoItem();
			info.setAppCode(cursor.getString(cursor.getColumnIndex(APP_CODE)));
			info.setAppName(cursor.getString(cursor.getColumnIndex(APP_NAME)));
			info.setPkgName(cursor.getString(cursor.getColumnIndex(PKG_NAME)));
			info.setIcon(cursor.getString(cursor.getColumnIndex(ICON)));
			info.setVersion(cursor.getString(cursor.getColumnIndex(VERSION)));
			info.setShowVersion(cursor.getString(cursor.getColumnIndex(SHOW_VERSION)));
			info.setSize(cursor.getString(cursor.getColumnIndex(SIZE)));
			info.setMd5(cursor.getString(cursor.getColumnIndex(MD5)));
			info.setDownloadurl(cursor.getString(cursor.getColumnIndex(DOWNLOAD_URL)));
			info.setIgron(cursor.getInt(cursor.getColumnIndex(IGNORE_UPDATE_FLAG)));

		} else {

		}
		
		return info;
	}
	
}
