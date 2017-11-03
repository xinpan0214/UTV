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
import java.util.List;
import com.sz.ead.framework.mul2launcher.dataprovider.dataitem.AppItem;
import com.sz.ead.framework.mul2launcher.mainapp.ApkUtil;
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

public class InstalledTable 
{
	public static final String AUTHORITY = "com.sz.ead.framework.mul2launcher.db.DatabaseManager";          //类名
	public static final String INSTALLED_TABLE = "installedtable";                                           //表名
	public static final Uri CONTENT_SORT_URI = Uri.parse("content://" + AUTHORITY + "/" + INSTALLED_TABLE); //访问uri
	public static final String ID = "_id";                    //主键
	public static final String APP_CODE = "app_code";         //应用编码 
	public static final String APP_NAME = "app_name";         //应用名称
	public static final String PKG_NAME = "pkg_name";         //包名
	public static final String APP_POSITION = "app_position"; //应用位置
	public static final String APP_SOURCE = "app_source";     //应用来源(商城/其它)
	
	public static String getCreateSql() 
	{
		/**
		 * SQL语句：
		 * db.execSQL("CREATE TABLE  IF NOT EXISTS " + DOWNLOAD_TABLE + "( " + "_id INTEGER PRIMARY KEY AUTOINCREMENT," + 
		 * "app_code TEXT," + "app_name TEXT," + "pkg_name TEXT," + "APP_POSITION TEXT"+ "APP_SOURCE TEXT" + ");");
		 */	
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
		/**
		 * SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		 * qb.setTables(CONTENT_SORT_URI.getPathSegments().get(0)); Cursor
		 * abcCursor=qb.query(db,null,null,null,null,null,null);
		 */
		String string = "DROP TABLE IF EXISTS " + INSTALLED_TABLE;
		return string;
	}
	
	/**
	 * 
	 * @Title: queryInstalledAppList
	 * @Description: 查询已安装的应用列表
	 * @return
	 * @return: ArrayList<AppItem>
	 */
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
			PackageManager pm = DatabaseManager.mContext.getPackageManager();
			cursor.setNotificationUri(DatabaseManager.mContext.getContentResolver(),CONTENT_SORT_URI);
			int n = cursor.getCount();
			for (int i=0; i<n; i++) 
			{
				try 
				{
					cursor.moveToPosition(i);
					AppItem appItem = new AppItem();
					PackageInfo packageInfo = pm.getPackageInfo(cursor.getString(cursor.getColumnIndex(PKG_NAME)), PackageManager.GET_PERMISSIONS);
					appItem.setAppCode(cursor.getString(cursor.getColumnIndex(APP_CODE)));
					appItem.setAppName(packageInfo.applicationInfo.loadLabel(DatabaseManager.mContext.getPackageManager()).toString());
					//appItem.setAppName(cursor.getString(cursor.getColumnIndex(APP_NAME)));
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
			LogUtil.i(LogUtil.TAG, " queryAppInfo " + e.toString());
		}
		
		return appItem;
	}
	
	/**
	 * 
	 * @Title: queryAppInfo
	 * @Description: 通过应用位置查询应用信息
	 * @param postion
	 * @return
	 * @return: AppItem
	 */
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
			LogUtil.i(LogUtil.TAG, " queryAppInfo " + e.toString());
		}
		
		return appItem;
	}
	
	/**
	 * 
	 * @Title: isLauncherApp
	 * @Description: 是否是桌面应用
	 * @param context
	 * @param pkgName
	 * @return
	 * @return: boolean
	 */
	public static boolean isLauncherApp(Context context, String pkgName)
	{
		List<AppItem> appList = ApkUtil.getInstalledApp(context);
		for (int i=0; i<appList.size(); i++)
		{
			if (pkgName.equals(appList.get(i).getPkgName()))
			{
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 
	 * @Title: insertApp
	 * @Description: 插入应用
	 * @param pkgName
	 * @return
	 * @return: boolean
	 */
	public static boolean insertApp(Context context, String pkgName) 
	{
		if (DatabaseManager.mDbHelper == null) 
		{
			return false;
		}
		
		//如果不是桌面应用, 则不插入
		if (!isLauncherApp(context, pkgName))
		{
			return false;
		}
		
		//如果已经插入, 则不再插入
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
			info.setVersion(packageInfo.versionCode+"");
			
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
			LogUtil.i(LogUtil.TAG, " insertApp " + e.toString());
		}
		return true;
	}
	
	/**
	 * 
	 * @Title: deleteApp
	 * @Description: 删除应用
	 * @param pkgName
	 * @return
	 * @return: boolean
	 */
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
				LogUtil.i(LogUtil.TAG, " deleteApp index: " + String.valueOf(n));
					
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
			LogUtil.d(LogUtil.TAG, " deleteApp " + e.toString());
		}
		return true;
	}
	
	/**
	 * 
	 * @Title: changeAppPostion
	 * @Description: 改变应用位置
	 * @param src
	 * @param dst
	 * @return: void
	 */
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
	
	/**
	 * 
	 * @Title: MoveAppPostion
	 * @Description: 移动应用位置
	 * @param src
	 * @param dst
	 * @return: void
	 */
	public static void moveAppPostion(int src, int dst) 
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
		
		for (int i=0; i<n; i++) 
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
	
	/**
	 * 
	 * @Title: updateInstalledAppLang
	 * @Description: 更新所有已安装应用的语言
	 * @return: void
	 */
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
		while (mCursor.moveToNext()) 
		{
			pagNames.add(mCursor.getString(mCursor.getColumnIndex(PKG_NAME)));
		}
		mCursor.close();
		db.close();
		updateAppLang(pagNames);
	}
	
	/**
	 * 
	 * @Title: updateAppLang
	 * @Description: 更新应用语言
	 * @param pagNames
	 * @return: void
	 */
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
				LogUtil.i(LogUtil.TAG, "updateAppLang: " + e.toString());
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
	
	/**
	 * 
	 * @Title: updateAppSource
	 * @Description: 更新应用来源
	 * @param pagName
	 * @param source
	 * @return: void
	 */
	public static void updateAppSource(String pkgName, int source)
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
			db.update(INSTALLED_TABLE, values, where, new String[]{pkgName});
			LogUtil.i(LogUtil.TAG, " installedtable updateSource ");
		}
		catch (Exception e) 
		{
			LogUtil.i(LogUtil.TAG, " updateSource: " + e.toString());
		}
		finally
		{
			if(db != null && db.isOpen())
			{
				db.close();
			}
		}
	}
	
	/**
	 * 
	 * @Title: updateAppSource
	 * @Description: 更新应用信息
	 * @param pagName
	 * @param source
	 * @return: void
	 */
	public static void updateAppItem(AppItem appItem)
	{
		if (DatabaseManager.mDbHelper == null) 
		{
			return;
		}
		
		if (appItem == null)
		{
			return;
		}
		
		SQLiteDatabase db = null;
		try 
		{
			String pkgName = appItem.getPkgName();
			String where;
			db = DatabaseManager.mDbHelper.getWritableDatabase();	
			where = PKG_NAME + "=\"" + pkgName + "\"";
			ContentValues values = new ContentValues();
			values.put(APP_CODE, appItem.getAppCode());
			values.put(APP_NAME, appItem.getAppName());
			values.put(APP_POSITION, appItem.getPosition());
			db.update(INSTALLED_TABLE, values, where, null);
			LogUtil.i(LogUtil.TAG, " installedtable updateAppItem ");
		}
		catch (Exception e) 
		{
			LogUtil.i(LogUtil.TAG, " updateSource: " + e.toString());
		}
		finally
		{
			if(db != null && db.isOpen())
			{
				db.close();
			}
		}
	}
	
	/**
	 * 
	 * @Title: getIconBitmap
	 * @Description: 获取应用图标位图
	 * @param pkgName
	 * @return
	 * @return: Drawable
	 */
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
}
