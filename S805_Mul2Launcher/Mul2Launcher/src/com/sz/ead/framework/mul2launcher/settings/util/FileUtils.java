/**
 * Copyright © 2015GreatVision. All rights reserved.
 *
 * @Title: FileUtils.java
 * @Prject: Mul2Launcher
 * @Package: com.sz.ead.framework.mul2launcher.settings.util
 * @Description: TODO
 * @author: lijungang  
 * @date: 2015-5-4 下午3:36:17
 */
package com.sz.ead.framework.mul2launcher.settings.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.os.StatFs;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.util.Log;

/**
 * Copyright © 2015GreatVision. All rights reserved.
 *
 * @Title: FileUtils.java
 * @Prject: Mul2Launcher
 * @Package: com.sz.ead.framework.mul2launcher.settings.util
 * @Description: TODO
 * @author: lijungang 
 * @date: 2015-5-4 下午3:36:17
 */
public class FileUtils {

	public final static String TAG = "FileUtils";
	public final static String TELECHIP_PLATFORM_EXTERNAL_SDCARD_PATH = "/mnt/extsd";
	public final static String TELECHIP_PLATFORM_INTERNAL_SDCARD_PATH = "/mnt/sdcard";
	public final static String TELECHIP_PLATFORM_USB_PATHA = "/mnt/usbhost0";
	public final static String TELECHIP_PLATFORM_USB_PATHB = "/mnt/usbhost1";
	/**
	 * 
	 * @Title: isSdcardMounted
	 * @Description: 判断内置sd卡是否存在
	 * @return
	 * @return: boolean
	 */
	public static boolean isSdcardMounted() {  
	  if (android.os.Environment.getExternalStorageState().equals(  
	    android.os.Environment.MEDIA_MOUNTED)) {  
	   return true;  
	  } else  
	   return false;  
	 } 
	/**
	 * 
	 * @Title: getSDFreeSize
	 * @Description: 获取内置sd卡剩余空间大小，单位M
	 * @return
	 * @return: long
	 */
	public static long getSDFreeSize(){
		if(isSdcardMounted())
		{
			File path = Environment.getExternalStorageDirectory();   
			StatFs sf = new StatFs(path.getPath());
			long blockSize = sf.getBlockSize();   
			long freeBlocks = sf.getAvailableBlocks();  
			return freeBlocks * blockSize/1024;
		}
		return 0;
	}
	/**
	 * 
	 * @Title: isExternalSdcardMounted
	 * @Description: 判断外置sd卡是否存在
	 * @param context
	 * @return
	 * @return: boolean
	 */
	public static boolean isExternalSdcardMounted(Context context){
    	StorageManager mStorageManager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
    	StorageVolume[] storageVolumes = mStorageManager.getVolumeList();
        
    	int length = storageVolumes.length;
		for (int i = 0; i < length; i++) {
			Log.d(TAG,"StorageVolume: "+i+":"+storageVolumes[i].getPath()+"\n");
			if (TELECHIP_PLATFORM_EXTERNAL_SDCARD_PATH.equals(storageVolumes[i].getPath())) {
				if(mStorageManager.getVolumeState(storageVolumes[i].getPath()).equals("mounted"))
				{
					return true;
				}
			}
		}
		return false;
    }
	/**
	 * 
	 * @Title: getExternalSdcardPath
	 * @Description: 获取外置sd卡路径
	 * @param context
	 * @return
	 * @return: String
	 */
    public static String getExternalSdcardPath(Context context){
    	StorageManager mStorageManager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
    	StorageVolume[] storageVolumes = mStorageManager.getVolumeList();
        
    	int length = storageVolumes.length;
		for (int i = 0; i < length; i++) {
			Log.d(TAG,"StorageVolume: "+i+":"+storageVolumes[i].getPath()+"\n");
			if (TELECHIP_PLATFORM_EXTERNAL_SDCARD_PATH.equals(storageVolumes[i].getPath())) {
				return storageVolumes[i].getPath();
			}
		}
		return null;
    }
    /**
     * 
     * @Title: getPathFreeSize
     * @Description: 获取path下剩余空间大小，单位B
     * @param path
     * @return
     * @return: long
     */
	public static long getPathFreeSize(String path){
		if((path == null) || (path.length() == 0))
		{
			return 0;
		}
		else
		{
			StatFs sf = new StatFs(path);
			long blockSize = sf.getBlockSize();   
			long freeBlocks = sf.getAvailableBlocks();  
			return freeBlocks * blockSize;
		}
	}
	   /**
     * 
     * @Title: getPathTotalSize
     * @Description: 获取path下空间大小，单位G
     * @param path
     * @return
     * @return: String 保留两位小数
     */
	public static String getPathTotalSize(String path){
		if((path == null) || (path.length() == 0))
		{
			return "0";
		}
		else
		{
			StatFs sf = new StatFs(path);
			long blockSize = sf.getBlockSize();   
			long totalBlocks = sf.getBlockCount();
			DecimalFormat fnum = new DecimalFormat("##0.00");
			String dd=fnum.format((float)(totalBlocks * blockSize/1024/1024) /1024);
			return dd;
		}
	}
	/**
	 * 
	 * @Title: isUSBMounted
	 * @Description: 判断usb是否存在
	 * @param context
	 * @return
	 * @return: boolean
	 */
    public static boolean isUSBMounted(Context context){
    	StorageManager mStorageManager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
    	StorageVolume[] storageVolumes = mStorageManager.getVolumeList();
        int length = storageVolumes.length;
		for (int i = 0; i < length; i++) {
			if (TELECHIP_PLATFORM_USB_PATHA.equals(storageVolumes[i].getPath())) {
				if(mStorageManager.getVolumeState(storageVolumes[i].getPath()).equals("mounted"))
				{
					return true;
				}
			}
			if (TELECHIP_PLATFORM_USB_PATHB.equals(storageVolumes[i].getPath())) {
				if(mStorageManager.getVolumeState(storageVolumes[i].getPath()).equals("mounted"))
				{
					return true;
				}
			}
		}
		return false;
    }
    /**
     * 
     * @Title: getUSBPath
     * @Description: 返回usb路径
     * @param context
     * @return
     * @return: String
     */
    public static String getUSBPath(Context context){
    	StorageManager mStorageManager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
    	StorageVolume[] storageVolumes = mStorageManager.getVolumeList();
        
    	int length = storageVolumes.length;
		for (int i = 0; i < length; i++) {
			if (TELECHIP_PLATFORM_USB_PATHA.equals(storageVolumes[i].getPath())) {
				if(mStorageManager.getVolumeState(storageVolumes[i].getPath()).equals("mounted"))
				{
					return storageVolumes[i].getPath();
				}
			}
			if (TELECHIP_PLATFORM_USB_PATHB.equals(storageVolumes[i].getPath())) {
				if(mStorageManager.getVolumeState(storageVolumes[i].getPath()).equals("mounted"))
				{
					return storageVolumes[i].getPath();
				}
			}
		}
		return null;
    }
   /**
    * 
    * @Title: deleteFile
    * @Description: 删除文件
    * @param path
    * @param fileName
    * @return: void
    */
	public static void deleteFile(String path, String fileName) {
		File file = new File(path + fileName);
		if(file.exists()) {
			file.delete();
		}
	}
	/**
	 * 
	 * @Title: delete
	 * @Description: 删除文件
	 * @param file
	 * @return: void
	 */
	public static void delete(File file) {
		if (file.isFile()) {  
			file.delete();  
			return;  
		}  
		if(file.isDirectory()){  
			File[] childFiles = file.listFiles();  
			if (childFiles == null || childFiles.length == 0) {  
				file.delete();  
				return;}  
			for (int i = 0; i < childFiles.length; i++) 
			{  
		        delete(childFiles[i]);  
			}  
	        file.delete();  
	    }  
	}
	/**
	 * 
	 * @Title: createFile
	 * @Description: 创建文件
	 * @param pathAndFileName
	 * @return
	 * @throws IOException
	 * @return: File
	 */
	public static File createFile(String pathAndFileName) throws IOException {
		File file = new File(pathAndFileName);
		file.createNewFile();
		return file;
	}
    /**
     * 
     * @Title: createDir
     * @Description: 创建文件夹
     * @param dirName
     * @return
     * @return: File
     */
	public static File createDir(String dirName) {
		File dir = new File(dirName);
		if(!dir.exists()){
			dir.mkdir();
		}
		return dir;
	}
    /**
     * 
     * @Title: isFileExist
     * @Description: 判断文件是否存在
     * @param pathAndfileName
     * @return
     * @return: boolean
     */
	public static boolean isFileExist(String pathAndfileName) {
		File file = new File(pathAndfileName);
		return file.exists();
	}
    /**
     * 
     * @Title: chmodFile
     * @Description: 更改文件权限
     * @param mode：777; filePath：文件名
     * @return: boolean
     */
	public static boolean chmodFile (String mode, String filePath) 
	{
		String[] chmodArgs = new String[3];
		chmodArgs[0] = "chmod";
		chmodArgs[1] = mode;
		chmodArgs[2] = filePath;
		try {
			if(Runtime.getRuntime().exec(chmodArgs)==null)
			{
				return false;
			}
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public static void saveMyBitmap(String dir,String imgUrl,Bitmap mBitmap){
		
		if (null != mBitmap && !mBitmap.isRecycled())
		{
			String splistr[] = imgUrl.split("/");
			String name = splistr[splistr.length-1];
			File f = new File(dir+"/" + name);
			
			try {
				f.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block


			}
			FileOutputStream fOut = null;
			try {
				
				fOut = new FileOutputStream(f);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			
			mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
			
			try {
				fOut.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				fOut.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}
}
