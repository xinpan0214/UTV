package com.sz.ead.framework.mul2launcher.db;

import com.sz.ead.framework.mul2launcher.appstore.downloader.DownConstants;
import com.sz.ead.framework.mul2launcher.appstore.downloader.DownloadInfo;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

public class DownLoadDB {
	// 全局数据库
	public static SQLiteDatabase sqlDB;
	
	// 数据库文件
	private static final String DATABASE_NAME = "download.db";
	
	// 数据库版本 修改数据库要修改此代码
	private static final int DATABASE_VERSION = 3;
	
	// 表名
	public static final String TABLE_DOWNLOAD = "download";

	// 类名
	public static final String AUTHORITY = "com.sz.ead.framework.appstore.db.DatabaseManager";
	
	// 访问uri
	public static final Uri CONTENT_SORT_URI = Uri.parse("content://"
			+ AUTHORITY + "/" + TABLE_DOWNLOAD);
	
	
	// filter
	public static final String TAG = "======DownLoadDB======";
	
	// 控制打印
	public static boolean debug = true;

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



	
	public static String getCreateSql() {
	
		StringBuffer sb = new StringBuffer();
		sb.append("CREATE TABLE  IF NOT EXISTS ");
		sb.append(TABLE_DOWNLOAD);
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
		String string = "DROP TABLE IF EXISTS " + DATABASE_NAME;
		return string;
	}
	
	/**
	 * 插入一条下载记录
	 * @author liyingying
	 * @param info 下载信息
	 */
	public static void insertADownLoad(DownloadInfo info) {
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

		db.insert(TABLE_DOWNLOAD, null, values);

	}

	/**
	 * 删除一条下载记录
	 * @author liyingying
	 * @param AppId 下载AppId
	 */
	public static void deleteADownLoad(String AppCode) {
		String where;
		if(DatabaseManager.mDbHelper==null){
			return;
		}
		SQLiteDatabase db = DatabaseManager.mDbHelper.getWritableDatabase();
		where = APP_CODE + "=\"" + AppCode + "\"";
		int n = db.delete(TABLE_DOWNLOAD, where, null);
		if (debug)
			Log.e(TAG, "deleteADownLoad" + String.valueOf(n) + " " + AppCode);
	}

	/**
	 * 修改下载状态
	 * @author liyingying
	 * @param AppId 下载AppId
	 * @param status 下载状态
	 */
	public static void modifyDownLoadStatus(String AppCode, int status) {
		String where;
		if(DatabaseManager.mDbHelper==null){
			return;
		}
		SQLiteDatabase db = DatabaseManager.mDbHelper.getWritableDatabase();	
		where = APP_CODE + "=\"" + AppCode + "\"";
		Log.e(TAG, "modifyDownLoadStatus:" +  "appcode:"+ AppCode + "  status:"+status);
		ContentValues values = new ContentValues();
		values.put(DOWN_STATUS, status);
		int n = db.update(TABLE_DOWNLOAD, values, where, null);
		if (debug)
			Log
					.e(TAG, "modifyDownLoadStatus" + String.valueOf(n) + " "
							+ AppCode);
	}

	/**
	 * 修改所有下载状态改为暂停
	 * @author liyingying
	 */
	public static void modifyDowningToPause() {
		String where;
		if(DatabaseManager.mDbHelper==null){
			return;
		}
		SQLiteDatabase db = DatabaseManager.mDbHelper.getWritableDatabase();
		where = DOWN_STATUS + "=\"" + DownConstants.STATUS_DOWN_DOWNING + "\"";

		ContentValues values = new ContentValues();
		values.put(DOWN_STATUS, DownConstants.STATUS_DOWN_PAUSE);
		int n = db.update(TABLE_DOWNLOAD, values, where, null);
		if (debug)
			Log.e(TAG, "modifyDowningToPause" + String.valueOf(n));
	}
	
	/**
	 * @Title: delAllDownload
	 * @Description: 删除所有下载
	 * @return: void
	 */
	public static void delAllDownload() {
		String where;
		if(DatabaseManager.mDbHelper==null){
			return;
		}
		SQLiteDatabase db = DatabaseManager.mDbHelper.getWritableDatabase();
		where = DOWN_STATUS + "!=" + DownConstants.STATUS_DOWN_INSTALLING;
		
		int n = db.delete(TABLE_DOWNLOAD, where, null);
		if (debug)
			Log.e(TAG, "delAllDownload" + String.valueOf(n));
	}
	
	/**
	 * @Title: delAllDownload
	 * @Description: 删除所有下载
	 * @return: void
	 */
	public static void delDownload() {
		if(DatabaseManager.mDbHelper==null){
			return;
		}
		SQLiteDatabase db = DatabaseManager.mDbHelper.getWritableDatabase();
		
		int n = db.delete(TABLE_DOWNLOAD, null, null);
		if (debug)
			Log.e(TAG, "delAllDownload" + String.valueOf(n));
	}
	
	/**
	 * @Title: queryDownloadCount
	 * @Description: 查询下载个数
	 * @return
	 * @return: int
	 */
	public static int queryDownloadCount(){
		Cursor mCursor = null;
		int count = 0;
		if(DatabaseManager.mDbHelper==null){
			return 0;
		}
		SQLiteDatabase db = DatabaseManager.mDbHelper.getWritableDatabase();
		mCursor = db.query(TABLE_DOWNLOAD, null, null, null, null, null,
				null, null);
		if (mCursor != null) {
			count = mCursor.getCount();
			if (debug)
				Log.i(TAG, "queryDownloadCount " + String.valueOf(count));
		} else {
			if (debug)
				Log.i(TAG, "queryDownloadCount " + "0");
		}
		if(mCursor != null){
			mCursor.close();
			mCursor = null;
		}
		return count;
	}
	
	/**
	 * @Title: isInstalling
	 * @Description: 应用正在安装
	 * @return
	 * @return: boolean
	 */
	public static boolean isInstalling(){
		Cursor mCursor = null;
		boolean nRet = false;
		if(DatabaseManager.mDbHelper==null){
			return false;
		}
		SQLiteDatabase db = DatabaseManager.mDbHelper.getWritableDatabase();
		mCursor = db.query(TABLE_DOWNLOAD, null, null, null, null, null,
				null, null);
		if (mCursor != null) {
			DownloadInfo info = getARecord(mCursor, 0);
			if(info != null && info.getDownStatus() == DownConstants.STATUS_DOWN_INSTALLING){
				nRet = true;
			}
		} else {
			if (debug)
				Log.i(TAG, "queryDownloadCount " + "0");
		}
		if(mCursor != null){
			mCursor.close();
			mCursor = null;
		}
		return nRet;
	}

	/**
	 * 修改下载进度
	 * @author liyingying
	 * @param AppId 下载AppId
	 * @param progress 进度
	 */
	public static void modifyDownLoadProgress(String AppCode, int progress) {
		String where;
		if(DatabaseManager.mDbHelper==null){
			return;
		}
		SQLiteDatabase db = DatabaseManager.mDbHelper.getWritableDatabase();
		where = APP_CODE + "=\"" + AppCode + "\"";

		ContentValues values = new ContentValues();
		values.put(DOWN_CURRENT_BYTE, progress);
		int n = db.update(TABLE_DOWNLOAD, values, where, null);
		if (debug)
			Log.i(TAG, "modifyDownLoadProgress" + String.valueOf(n) + " "
					+ AppCode);
	}

	/**
	 * 修改apk总大小
	 * @author liyingying
	 * @param AppId 下载AppId
	 * @param size 大小
	 */
	public static void modifyDownLoadTotalSize(String AppCode, int size) {
		String where;
		if(DatabaseManager.mDbHelper==null){
			return;
		}
		SQLiteDatabase db = DatabaseManager.mDbHelper.getWritableDatabase();
		where = APP_CODE + "=\"" + AppCode + "\"";

		ContentValues values = new ContentValues();
		values.put(DOWN_TOTAL_BYTE, size);
		int n = db.update(TABLE_DOWNLOAD, values, where, null);
		if (debug)
			Log.i(TAG, "modifyDownLoadProgress" + String.valueOf(n) + " "
					+ AppCode);
	}

	/**
	 * 查询正在下载列表  包括暂停等状态
	 * @author liyingying
	 * @return 列表cursor
	 */
	public static Cursor queryDowningList() {
		Cursor mCursor = null;
		String where;
		if(DatabaseManager.mDbHelper==null){
			return null;
		}
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		SQLiteDatabase db = DatabaseManager.mDbHelper.getReadableDatabase();
		qb.setTables(CONTENT_SORT_URI.getPathSegments().get(0));
		where = DOWN_STATUS + "=" + DownConstants.STATUS_DOWN_DOWNING + " "+ "OR" + " "
				+ DOWN_STATUS + "=" + DownConstants.STATUS_DOWN_PAUSE + " " + "OR" + " "
				+ DOWN_STATUS + "=" + DownConstants.STATUS_DOWN_BREAKPOINT + " " + "OR" + " " 
				+ DOWN_STATUS + "=" + DownConstants.STATUS_DOWN_DOWNCOMPLETE + " " + "OR" + " " 
				+ DOWN_STATUS + "=" + DownConstants.STATUS_DOWN_READY;
		mCursor = db.query(TABLE_DOWNLOAD, null, where, null, null, null, null);
		if (mCursor != null) {
			mCursor.setNotificationUri(DatabaseManager.mContext.getContentResolver(),
					CONTENT_SORT_URI);
			int n = mCursor.getCount();
			if (debug)
				Log.i(TAG, "queryDowningList " + String.valueOf(n));
		} else {
			if (debug)
				Log.i(TAG, "queryDowningList " + "0");
		}

		return mCursor;
	}

	/**
	 * 查询正在下载列表 
	 * @author liyingying
	 * @return 列表cursor
	 */
	public static Cursor queryDowningListEx() {
		Cursor mCursor = null;
		String where;
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		if (DatabaseManager.mDbHelper != null) {
			SQLiteDatabase db = DatabaseManager.mDbHelper.getReadableDatabase();
			qb.setTables(CONTENT_SORT_URI.getPathSegments().get(0));
			where = DOWN_STATUS + "=" + DownConstants.STATUS_DOWN_DOWNING;
			mCursor = db.query(TABLE_DOWNLOAD, null, where, null, null, null,
					null);
			if (mCursor != null) {
				mCursor.setNotificationUri(DatabaseManager.mContext.getContentResolver(),
						CONTENT_SORT_URI);
				int n = mCursor.getCount();
				if (debug)
					Log.i(TAG, "queryDowningList " + String.valueOf(n));
			} else {
				if (debug)
					Log.i(TAG, "queryDowningList " + "0");
			}
		}

		return mCursor;
	}

	/**
	 * 正在下载个数
	 * @author liyingying
	 * @return 个数
	 */
	public static int queryDowningListCount() {
		Cursor mCursor = null;
		String where;
		if(DatabaseManager.mDbHelper==null){
			return 0;
		}
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		SQLiteDatabase db = DatabaseManager.mDbHelper.getReadableDatabase();
		
		int count = 0;

		qb.setTables(CONTENT_SORT_URI.getPathSegments().get(0));
		where = DOWN_STATUS + "=" + DownConstants.STATUS_DOWN_DOWNING;
		mCursor = db.query(TABLE_DOWNLOAD, null, where, null, null, null, null);
		if (mCursor != null) {
			mCursor.setNotificationUri(DatabaseManager.mContext.getContentResolver(),
					CONTENT_SORT_URI);
			count = mCursor.getCount();
			mCursor.close();
			mCursor = null;
			if (debug)
				Log.i(TAG, "queryDowningListCount " + String.valueOf(count));
		} else {
			if (debug)
				Log.i(TAG, "queryDowningListCount " + "0");
		}

		return count;
	}

	/**
	 * 查询已经完成下载列表
	 * @author liyingying
	 * @return 列表cursor
	 */
	public static Cursor queryDownCompleteList() {
		Cursor mCursor = null;
		String where, orderBy;
		if(DatabaseManager.mDbHelper==null){
			return null;
		}
		SQLiteDatabase db = DatabaseManager.mDbHelper.getWritableDatabase();
		where = DOWN_STATUS + "=" + DownConstants.STATUS_DOWN_DOWNCOMPLETE;
		orderBy = ID + " DESC";
		mCursor = db.query(TABLE_DOWNLOAD, null, where, null, null, null,
				orderBy, null);
		if (mCursor != null) {
			int n = mCursor.getCount();
			if (debug)
				Log.i(TAG, "queryDownCompleteList " + String.valueOf(n));
		} else {
			if (debug)
				Log.i(TAG, "queryDownCompleteList " + "0");
		}

		return mCursor;
	}
	
	/**
	 * 查询收藏列表
	 * @author liyingying
	 * @return 列表cursor
	 */
	public static Cursor queryDownCollectList() {
		Cursor mCursor = null;
		String where;
		if(DatabaseManager.mDbHelper==null){
			return null;
		}
		SQLiteDatabase db = DatabaseManager.mDbHelper.getWritableDatabase();
		
		where = DOWN_STATUS + "=" + DownConstants.STATUS_DOWN_COLLECT;
		mCursor = db.query(TABLE_DOWNLOAD, null, where, null, null, null, null);
		if (mCursor != null) {
			int n = mCursor.getCount();
			if (debug)
				Log.i(TAG, "queryDownCollectList " + String.valueOf(n));
		} else {
			if (debug)
				Log.i(TAG, "queryDownCollectList " + "0");
		}

		return mCursor;
	}

	/**
	 * 查询某个应用在不在搜藏列表
	 * @author liyingying
	 * @param AppId 下载AppId
	 * @return true 存在 反之不存在
	 */
	public static boolean queryAppInDownCollectList(String AppCode) {

		Cursor mCursor = null;
		String where;
		if(DatabaseManager.mDbHelper==null){
			return false;
		}
		SQLiteDatabase db = DatabaseManager.mDbHelper.getWritableDatabase();
		
		where = DOWN_STATUS + "=" + DownConstants.STATUS_DOWN_COLLECT + "\""
				+ " " + "AND" + " " + APP_CODE + "=\"" + AppCode + "\"";
		mCursor = db.query(TABLE_DOWNLOAD, null, where, null, null, null, null);

		if (mCursor != null) {
			int n = mCursor.getCount();
			if (n > 0) {
				if (debug)
					Log
							.i(TAG, "queryAppInDownCollectList "
									+ String.valueOf(n));
				return true;
			}
		} else {

		}
		if (mCursor != null) {
			mCursor.close();
			mCursor = null;
		}
		return false;
	}

	/**
	 * 得到准备队列
	 * @author liyingying
	 * @return 列表cursor
	 */ 
	public static Cursor queryReadyList() {
		Cursor mCursor = null;
		String where;
		if(DatabaseManager.mDbHelper==null){
			return null;
		}
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		SQLiteDatabase db = DatabaseManager.mDbHelper.getReadableDatabase();
		
		qb.setTables(CONTENT_SORT_URI.getPathSegments().get(0));
		where = DOWN_STATUS + "=" + DownConstants.STATUS_DOWN_BREAKPOINT + " "
				+ "OR" + " " + DOWN_STATUS + "="
				+ DownConstants.STATUS_DOWN_READY;
		mCursor = db.query(TABLE_DOWNLOAD, null, where, null, null, null, null);
		if (mCursor != null) {
			mCursor.setNotificationUri(DatabaseManager.mContext.getContentResolver(),
					CONTENT_SORT_URI);
			int n = mCursor.getCount();
			if (debug)
				Log.i(TAG, "queryDowningList " + String.valueOf(n));
		} else {
			if (debug)
				Log.i(TAG, "queryDowningList " + "0");
		}

		return mCursor;
	}
	
	
	/**
	 * 查询暂停状态 
	 * @author liyingying
	 * @return 列表cursor
	 */
	public static Cursor queryPauseList() {
		Cursor mCursor = null;
		String where;
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		if (DatabaseManager.mDbHelper != null) {
			SQLiteDatabase db = DatabaseManager.mDbHelper.getReadableDatabase();
			qb.setTables(CONTENT_SORT_URI.getPathSegments().get(0));
			where = DOWN_STATUS + "=" + DownConstants.STATUS_DOWN_PAUSE;
			mCursor = db.query(TABLE_DOWNLOAD, null, where, null, null, null,
					null);
			if (mCursor != null) {
				mCursor.setNotificationUri(DatabaseManager.mContext.getContentResolver(),
						CONTENT_SORT_URI);
				int n = mCursor.getCount();
				if (debug)
					Log.i(TAG, "queryPauseList " + String.valueOf(n));
			} else {
				if (debug)
					Log.i(TAG, "queryPauseList " + "0");
			}
		}

		return mCursor;
	}
	
	/**
	 * 查询已经开始过的下载
	 * @author liyingying
	 * @return 列表cursor
	 */
	public static Cursor queryDownLoadStart() {
		Cursor mCursor = null;
		String where;
		if(DatabaseManager.mDbHelper==null){
			return null;
		}
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();	
		SQLiteDatabase db = DatabaseManager.mDbHelper.getReadableDatabase();
		qb.setTables(CONTENT_SORT_URI.getPathSegments().get(0));
		where = DOWN_STATUS + "=" + DownConstants.STATUS_DOWN_DOWNING + " "
				+ "OR" + " " + DOWN_STATUS + "="
				+ DownConstants.STATUS_DOWN_PAUSE + " " + "OR" + " "
				+ DOWN_STATUS + "=" + DownConstants.STATUS_DOWN_BREAKPOINT
				+ " " + "OR" + " " + DOWN_STATUS + "="
				+ DownConstants.STATUS_DOWN_DOWNCOMPLETE
				+ " " + "OR" + " " + DOWN_STATUS + "="
				+ DownConstants.STATUS_DOWN_STOP
				+ " " + "OR" + " " + DOWN_STATUS + "="
				+ DownConstants.STATUS_DOWN_READY;
		
		mCursor = db.query(TABLE_DOWNLOAD, null, where, null, null, null, null);
		if (mCursor != null) {
			mCursor.setNotificationUri(DatabaseManager.mContext.getContentResolver(),
					CONTENT_SORT_URI);
			int n = mCursor.getCount();
			if (debug)
				Log.i(TAG, "queryDownLoadStart " + String.valueOf(n));
		} else {
			if (debug)
				Log.i(TAG, "queryDownLoadStart " + "0");
		}

		return mCursor;
	}

	/**
	 * 查询下载状态
	 * @author liyingying
	 * @param AppId 下载AppId
	 * @return 状态
	 */
	public static int queryDownloadStatus(String AppCode) {
		Cursor mCursor = null;
		String where;
		if(DatabaseManager.mDbHelper==null){
			return 0;
		}
		SQLiteDatabase db = DatabaseManager.mDbHelper.getWritableDatabase();	
		int status = 0;
		where = APP_CODE + "=\"" + AppCode + "\"";
		Log.d(TAG, "where:"+where);
		mCursor = db.query(TABLE_DOWNLOAD, null, where, null, null, null, null);

		if (mCursor != null) {
			int n = mCursor.getCount();
			if (n > 0) {
				if (debug)
					Log.i(TAG, "queryDownCollectList " + String.valueOf(n));
				mCursor.moveToFirst();
				int index = mCursor.getColumnIndex(DOWN_STATUS);Log.d(TAG, "index:"+index);
				status = mCursor.getInt(index);Log.d(TAG, "status:"+status);
			}
		} else {

		}
		if (mCursor != null) {
			mCursor.close();
			mCursor = null;
		}
		return status;
	}

	/**
	 * 查询下载状态
	 * @author liyingying
	 * @param packageName 包名
	 * @param appId	下载id
	 * @return 状态
	 */
	/*public static int queryDownloadStatus(String packageName, int appId) {
		Cursor mCursor = null;
		String where;
		SQLiteDatabase db = DatabaseManager.mDbHelper.getWritableDatabase();
		if(DatabaseManager.mDbHelper==null){
			return 0;
		}
		int status = 0;

		where = DOWN_PACKAGE_NAME + "=\"" + packageName + "\"" + " " + "AND"
				+ " " + DOWN_ELEMENT_FLAG + "=" + appId;
		mCursor = db.query(TABLE_DOWNLOAD, null, where, null, null, null, null);

		if (mCursor != null) {
			int n = mCursor.getCount();
			if (n > 0) {
				if (debug)
					Log.i(TAG, "queryDownCollectList " + String.valueOf(n));
				mCursor.moveToFirst();
				int index = mCursor.getColumnIndex(DOWN_STATUS);
				status = mCursor.getInt(index);
			}
		} else {

		}
		if (mCursor != null) {
			mCursor.close();
			mCursor = null;
		}
		return status;
	}*/

	/**
	 * 查询下载记录
	 * @author liyingying
	 * @param AppId id
	 * @return 一条记录
	 */
	public static DownloadInfo queryADownRecord(String pkgName) {
		Cursor cursor = null;
		String where;
		if(DatabaseManager.mDbHelper==null){
			return null;
		}
		SQLiteDatabase db = DatabaseManager.mDbHelper.getWritableDatabase();	
		DownloadInfo downinfo = null;

		where = PKG_NAME + "=\"" + pkgName + "\"";
		cursor = db.query(TABLE_DOWNLOAD, null, where, null, null, null, null);

		if (cursor != null) {
			int n = cursor.getCount();
			if (n > 0) {
				if (debug)
					Log.i(TAG, "queryADownRecord " + String.valueOf(n));
				cursor.moveToFirst();

				downinfo = new DownloadInfo();
				downinfo.setAppCode(cursor.getString(cursor
						.getColumnIndex(APP_CODE)));
				downinfo.setAppName(cursor.getString(cursor
						.getColumnIndex(APP_NAME)));
				downinfo.setPkgName(cursor.getString(cursor
						.getColumnIndex(PKG_NAME)));
				downinfo.setIcon(cursor.getString(cursor
						.getColumnIndex(ICON)));
				downinfo.setVersion(cursor.getString(cursor
						.getColumnIndex(VERSION)));
				downinfo.setShowVersion(cursor.getString(cursor
						.getColumnIndex(SHOW_VERSION)));
				downinfo.setSize(cursor.getString(cursor
						.getColumnIndex(SIZE)));
				downinfo.setPrice(cursor.getString(cursor
						.getColumnIndex(PRICE)));
				downinfo.setClassifycode(cursor.getString(cursor
						.getColumnIndex(CLASSIFY_CODE)));
				downinfo.setMd5(cursor.getString(cursor
						.getColumnIndex(MD5)));
				
				downinfo.setDownloadurl(cursor.getString(cursor
						.getColumnIndex(DOWNLOAD_URL)));
				downinfo.setDownTotalBytes(cursor.getInt(cursor
						.getColumnIndex(DOWN_TOTAL_BYTE)));
				downinfo.setDownCurrentBytes(cursor.getInt(cursor
						.getColumnIndex(DOWN_CURRENT_BYTE)));
				downinfo.setDownStatus(cursor.getInt(cursor
						.getColumnIndex(DOWN_STATUS)));
				downinfo.setDownApkPath(cursor.getString(cursor
						.getColumnIndex(DOWN_APK_PATH)));
				downinfo.setAddDownListStyle(cursor.getInt(cursor
						.getColumnIndex(DOWN_ADDDOWNLIST_STYLE)));

			}
		} else {

		}
		if (cursor != null) {
			cursor.close();
			cursor = null;
		}
		return downinfo;
	}

	/**
	 * 查询下载记录
	 * @author liyingying
	 * @param packageName 包名
	 * @return 一条记录
	 */
	/*public static DownloadInfo queryADownRecord(String packageName) {
		Cursor cursor = null;
		String where;
		SQLiteDatabase db = DatabaseManager.mDbHelper.getWritableDatabase();
		if(DatabaseManager.mDbHelper==null){
			return null;
		}
		DownloadInfo downinfo = null;

		where = DOWN_PACKAGE_NAME + "=\"" + packageName + "\"";
		cursor = db.query(TABLE_DOWNLOAD, null, where, null, null, null, null);

		if (cursor != null) {
			int n = cursor.getCount();
			if (n > 0) {
				if (debug)
					Log.i(TAG, "queryADownRecord " + String.valueOf(n));
				cursor.moveToFirst();

				downinfo = new DownloadInfo();
				downinfo.setDownName(cursor.getString(cursor
						.getColumnIndex(DOWN_NAME)));
				downinfo.setDownApkUrl(cursor.getString(cursor
						.getColumnIndex(DOWN_APK_URL)));
				downinfo.setDownApkPath(cursor.getString(cursor
						.getColumnIndex(DOWN_APK_PATH)));
				downinfo.setDownIconUrl(cursor.getString(cursor
						.getColumnIndex(DOWN_ICON_URL)));
				downinfo.setDownIconPath(cursor.getString(cursor
						.getColumnIndex(DOWN_ICON_PATH)));
				downinfo.setDownStatus(cursor.getInt(cursor
						.getColumnIndex(DOWN_STATUS)));
				downinfo.setDownsize(cursor.getInt(cursor
						.getColumnIndex(DOWN_SIZE)));
				downinfo.setDownTotalBytes(cursor.getInt(cursor
						.getColumnIndex(DOWN_TOTAL_BYTES)));
				downinfo.setDownCurrentBytes(cursor.getInt(cursor
						.getColumnIndex(DOWN_CURRENT_BYTES)));
				downinfo.setDownPackageName(cursor.getString(cursor
						.getColumnIndex(DOWN_PACKAGE_NAME)));
				downinfo.setDownRate(cursor.getInt(cursor
						.getColumnIndex(DOWN_RATE)));
				downinfo.setDownHint(cursor.getString(cursor
						.getColumnIndex(DOWN_HINT)));
				downinfo.setDownCount(cursor.getInt(cursor
						.getColumnIndex(DOWN_COUNT)));
				downinfo.setDownVersion(cursor.getString(cursor
						.getColumnIndex(DOWN_CURRENT_VER)));

				downinfo.setDown_elementflag(cursor.getInt(cursor
						.getColumnIndex(DOWN_ELEMENT_FLAG)));
				downinfo.setDown_developer(cursor.getString(cursor
						.getColumnIndex(DOWN_DEVELOPER)));
				downinfo.setDown_comment_count(cursor.getInt(cursor
						.getColumnIndex(DOWN_COMMENT_COUNT)));
				downinfo.setAdddownListStyle(cursor.getInt(cursor
						.getColumnIndex(DOWN_ADDDOWNLIST_STYLE)));

			}
		} else {

		}
		if (cursor != null) {
			cursor.close();
			cursor = null;
		}
		return downinfo;
	}*/

	/**
	 * 查询下载记录 一个包名有几个应用
	 * @author liyingying
	 * @param packageName 包名
	 * @return 记录 cursor
	 */
	public static Cursor queryDownRecord(String packageName) {
		Cursor cursor = null;
		String where;
		if(DatabaseManager.mDbHelper==null){
			return null;
		}
		SQLiteDatabase db = DatabaseManager.mDbHelper.getWritableDatabase();
		DownloadInfo downinfo = null;

		where = PKG_NAME + "=\"" + packageName + "\"";
		cursor = db.query(TABLE_DOWNLOAD, null, where, null, null, null, null);

		return cursor;
	}

	/**
	 * 查询apk路径
	 * @author liyingying
	 * @param AppId appid
	 * @return apk路径
	 */
	public static String queryApkPath(String AppCode) {
		Cursor mCursor = null;
		String where;
		if(DatabaseManager.mDbHelper==null){
			return null;
		}
		SQLiteDatabase db = DatabaseManager.mDbHelper.getWritableDatabase();
		String path = null;

		where = APP_CODE + "=\"" + AppCode + "\"";
		mCursor = db.query(TABLE_DOWNLOAD, null, where, null, null, null, null);

		if (mCursor != null) {
			int n = mCursor.getCount();
			if (n > 0) {
				if (debug)
					Log.i(TAG, "queryApkPath " + String.valueOf(n));
				mCursor.moveToFirst();
				path = mCursor.getString(mCursor.getColumnIndex(DOWN_APK_PATH));
				if (debug)
					Log.i(TAG, "queryApkPath===" + path);
			}
		} else {

		}
		if (mCursor != null) {
			mCursor.close();
			mCursor = null;
		}

		return path;
	}

	/**
	 * cursor中取出一条下载记录
	 * @author liyingying
	 * @param cursor cursor
	 * @param pos 位置
	 * @return 记录
	 */
	public static DownloadInfo getARecord(Cursor cursor, int pos) {
		DownloadInfo downinfo = null;
		if (cursor != null && pos < cursor.getCount()) {
			if (pos != -1) {
				cursor.moveToPosition(pos);
			}
			downinfo = new DownloadInfo();
			downinfo.setAppCode(cursor.getString(cursor
					.getColumnIndex(APP_CODE)));
			downinfo.setAppName(cursor.getString(cursor
					.getColumnIndex(APP_NAME)));
			downinfo.setPkgName(cursor.getString(cursor
					.getColumnIndex(PKG_NAME)));
			downinfo.setIcon(cursor.getString(cursor
					.getColumnIndex(ICON)));
			downinfo.setVersion(cursor.getString(cursor
					.getColumnIndex(VERSION)));
			downinfo.setShowVersion(cursor.getString(cursor
					.getColumnIndex(SHOW_VERSION)));
			downinfo.setSize(cursor.getString(cursor
					.getColumnIndex(SIZE)));
			downinfo.setPrice(cursor.getString(cursor
					.getColumnIndex(PRICE)));
			downinfo.setClassifycode(cursor.getString(cursor
					.getColumnIndex(CLASSIFY_CODE)));
			downinfo.setMd5(cursor.getString(cursor
					.getColumnIndex(MD5)));
			
			downinfo.setDownloadurl(cursor.getString(cursor
					.getColumnIndex(DOWNLOAD_URL)));
			downinfo.setDownTotalBytes(cursor.getInt(cursor
					.getColumnIndex(DOWN_TOTAL_BYTE)));
			downinfo.setDownCurrentBytes(cursor.getInt(cursor
					.getColumnIndex(DOWN_CURRENT_BYTE)));
			downinfo.setDownStatus(cursor.getInt(cursor
					.getColumnIndex(DOWN_STATUS)));
			downinfo.setDownApkPath(cursor.getString(cursor
					.getColumnIndex(DOWN_APK_PATH)));
			downinfo.setAddDownListStyle(cursor.getInt(cursor
					.getColumnIndex(DOWN_ADDDOWNLIST_STYLE)));

		} else {

		}
		if (debug)
			Log.i(TAG, "getARecord " + "Success");
		return downinfo;
	}

	
}
