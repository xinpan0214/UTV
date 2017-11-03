package com.sz.ead.framework.mul2launcher.appstore.downloader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.sz.ead.framework.mul2launcher.application.UILApplication;
import com.sz.ead.framework.mul2launcher.mainapp.FragmentMainApp;
import com.sz.ead.framework.mul2launcher.util.FileUtil;

public class DownAppIcon {
	Context 	mContext;
	DownloadInfo 	mInfo;
	
	public DownAppIcon(Context context, DownloadInfo info) {
		mContext = context;
		mInfo = info;
		downAppIcon();
	}
	
	/*@Override
	public void run() {
		super.run();
		downAppIcon();
	}*/
	
	public void downAppIcon(){
		//Log.i("DownAppIcon","downAppIcon start");
		if(UILApplication.mImageLoader != null && UILApplication.mImageLoader.isInited()
				&&!TextUtils.isEmpty(mInfo.getIcon())){
			//Log.i("DownAppIcon","downAppIcon start"+mInfo.getIcon());
			
			/*文件存在就不要在下载*/
			File file = new File(getCurIconPath(mContext));
			if (file.exists()) {
				Log.i("DownAppIcon",getCurIconPath(mContext)+" downAppIcon exist");
				return;
			}else {
				Log.i("DownAppIcon",getCurIconPath(mContext)+" downAppIcon not exist");
			}

			UILApplication.mImageLoader.loadImage(mInfo.getIcon(), new ImageLoadingListener() {
				public void onLoadingStarted(String imageUri, View view){		
				}
				public void onLoadingFailed(String imageUri, View view, FailReason failReason){
				}
				public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage){
					//Log.i("DownAppIcon","onLoadingComplete"+imageUri);
					/*保存的都是成功的图片*/
					if(!TextUtils.isEmpty(imageUri)){
						if(imageUri.equals(mInfo.getIcon())){
							saveMyBitmap(loadedImage);
						}	
					}	
				}
				public void onLoadingCancelled(String imageUri, View view){			
				}
			});
		}
	}
	
	public void saveMyBitmap(Bitmap mBitmap){	
		Log.i("DownAppIcon","saveMyBitmap start");
		try {
			if (null != mBitmap && !mBitmap.isRecycled()){
				File f = new File(getCurIconPath(mContext));
				try {
					f.createNewFile();
				} catch (IOException e) {
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
				//成功发广播
				Intent intent = new Intent();
				intent.setAction(FragmentMainApp.DOWNLOAD_ICON_ACTION);
				Bundle bundle = new Bundle();
				bundle.putString(FragmentMainApp.DOWNLOAD_ICON_PKG_KEY, mInfo.getPkgName());
				intent.putExtras(bundle);
				mContext.sendBroadcast(intent);
			}
		} catch (Exception e) {
		}
	}
	
	
	
	/*应用图标下载路径*/
	public String getAppIconDir(Context context) {
		File dataDir = new File(FileUtil.APPICON_PATH);	
		if (!dataDir.exists()) {
			if (!dataDir.mkdir()) {
				Log.i("DownAppIcon","Unable to create getAppIconDir directory");
			}
		}		
		return dataDir.getAbsolutePath();
	}
	
	/*得到当前iconpath*/
	public String getCurIconPath(Context context){
		String path= null;
		if(mInfo != null){
			path = getAppIconDir(context)+"/" + mInfo.getPkgName() + ".png";
		}
		return path;
	}

}
