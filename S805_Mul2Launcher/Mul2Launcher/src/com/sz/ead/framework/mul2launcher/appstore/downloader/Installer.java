package com.sz.ead.framework.mul2launcher.appstore.downloader;


import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.content.Intent;
import android.content.pm.IPackageDeleteObserver;
import android.content.pm.IPackageInstallObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.sz.ead.framework.mul2launcher.R;
import com.sz.ead.framework.mul2launcher.application.UILApplication;
import com.sz.ead.framework.mul2launcher.appstore.dialog.AppInfoConstant;
import com.sz.ead.framework.mul2launcher.appstore.dialog.PublicFun;
import com.sz.ead.framework.mul2launcher.appstore.dialog.ShowToast;
import com.sz.ead.framework.mul2launcher.db.DownLoadDB;
import com.sz.ead.framework.mul2launcher.util.FileUtil;
import com.szgvtv.ead.framework.bi.Bi;

public class Installer {

	private static final String TAG = "PackageInstaller";
	PackageInfo mPkgInfo;
	private final int INSTALL_COMPLETE = 1;
	private final int UNINSTALL_COMPLETE = 2;
	private final int INSTALL_PROGRESS = 3;
	Context mContext;
	private ArrayList<Handler> mHandleList = null;
	DownloadInfo info;
	int mCurProgress = 0;
	long mTotalProgress = 40;
	boolean mInstallFlag = true;

	Timer mTimer = new Timer();
	TimerTask task = new TimerTask() {
		public void run() {
			Message m = new Message();
			m.what = INSTALL_PROGRESS;
			mHandler.sendMessage(m);
		}
	};

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case INSTALL_COMPLETE:
				stopInstall();
				Log.w(TAG, "mHandler_INSTALL_COMPLETE" + "returnCode = "
						+ msg.arg1);
				if (msg.arg1 == PackageManager.INSTALL_SUCCEEDED) {
					mCurProgress = (int) mTotalProgress;
					msgToUi(DownConstants.MSG_INSTALL_SUCCESS);

					/*
					 * InstalledAppsScan.addInstalledAppInfo(mContext,
					 * info.getAppCode());
					 * DownLoadDB.deleteADownLoad(info.getAppCode());
					 * FileUtil.deleteFile(info.getDownApkPath());
					 * UpdateTable.DeleteUpdate(info.getAppCode());
					 * sendDownMsgBroadCast(DownConstants.DOWN_STATUS_ACTION,
					 * DownConstants.DOWN_STATUS_DOWNLOAD_STATUS_KEY,
					 * DownConstants.DOWN_BROADCAST_TO_UI_INSTALLED,
					 * DownConstants.DOWN_STATUS_DOWNLOAD_DATA_KEY, null);
					 */

					sendDownMsgBroadCast(DownConstants.DOWN_MANAGE_ACTION,
							DownConstants.DOWN_STATUS_DOWNLOAD_STATUS_KEY,
							DownConstants.DOWN_BROADCAST_START_NEXT_APP,
							DownConstants.DOWN_STATUS_DOWNLOAD_DATA_KEY, null);

					Bi.sendBiMsg(Bi.BICT_FIRMWARE, Bi.BICC_INSTALL_RESULT,
							info.getAppCode(), info.getVersion(),
							PublicFun.getCurrentLauguage(mContext), "0",
							AppInfoConstant.APP_ID, PublicFun.getsVersionName());

				} else if (msg.arg1 == PackageManager.INSTALL_FAILED_INSUFFICIENT_STORAGE) {
					Log.d(TAG, "no storage space " + info.getAppCode());
					msgToUi(DownConstants.MSG_DOWN_DOWNFAIL);
					UILApplication.getDownLoadInstance(mContext).stopADownLoad(
							info.getAppCode());
					if (info != null) {
						
						DownLoadDB.deleteADownLoad(info.getAppCode());
						FileUtil.deleteFile(info.getDownApkPath());
					}

					ShowToast.getShowToast().createToast(
							mContext,
							mContext.getResources().getString(
									R.string.appstore_detail_install_nonestorge));

					if (info.getAddDownListStyle() == 1) {
						Bi.sendBiMsg(
								Bi.BICT_FIRMWARE,
								Bi.BICC_APP_UPGRADE,
								info.getAppCode(),
								info.getVersion(),
								PublicFun.getCurrentLauguage(mContext),
								String.valueOf(PublicFun.getsAppUpdataSource()),
								"1");
					} else {
						Bi.sendBiMsg(Bi.BICT_FIRMWARE, Bi.BICC_INSTALL_RESULT,
								info.getAppCode(), info.getVersion(),
								PublicFun.getCurrentLauguage(mContext), "1",
								AppInfoConstant.APP_ID,
								PublicFun.getsVersionName());
					}
				} else {
					UILApplication.getDownLoadInstance(mContext).stopADownLoad(
							info.getAppCode());
					if (info != null) {
						
						DownLoadDB.deleteADownLoad(info.getAppCode());
						FileUtil.deleteFile(info.getDownApkPath());
					}
					ShowToast.getShowToast().createToast(
							mContext,
							mContext.getResources().getString(
									R.string.appstore_detail_install_error));
					if (info.getAddDownListStyle() == 1) {
						Bi.sendBiMsg(
								Bi.BICT_FIRMWARE,
								Bi.BICC_APP_UPGRADE,
								info.getAppCode(),
								info.getVersion(),
								PublicFun.getCurrentLauguage(mContext),
								String.valueOf(PublicFun.getsAppUpdataSource()),
								"1");
					} else {
						Bi.sendBiMsg(Bi.BICT_FIRMWARE, Bi.BICC_INSTALL_RESULT,
								info.getAppCode(), info.getVersion(),
								PublicFun.getCurrentLauguage(mContext), "1",
								AppInfoConstant.APP_ID,
								PublicFun.getsVersionName());
					}
				}
				break;
			case UNINSTALL_COMPLETE:
				stopInstall();
				if (msg.arg1 == PackageManager.DELETE_SUCCEEDED) {
					mCurProgress = (int) mTotalProgress;
					msgToUi(DownConstants.MSG_UNINSTALL_PROGRESS);

					Bi.sendBiMsg(Bi.BICT_FIRMWARE, Bi.BICC_UNINSTALL_RESULT,
							info.getAppCode(), info.getVersion(),
							PublicFun.getCurrentLauguage(mContext), "0");

				} else if (msg.arg1 == PackageManager.DELETE_FAILED_DEVICE_POLICY_MANAGER) {
					Log.d(TAG, "Uninstall failed because " + info.getAppCode()
							+ " is a device admin");
					Bi.sendBiMsg(Bi.BICT_FIRMWARE, Bi.BICC_UNINSTALL_RESULT,
							info.getAppCode(), info.getVersion(),
							PublicFun.getCurrentLauguage(mContext), "1");
				} else {
					Bi.sendBiMsg(Bi.BICT_FIRMWARE, Bi.BICC_UNINSTALL_RESULT,
							info.getAppCode(), info.getVersion(),
							PublicFun.getCurrentLauguage(mContext), "1");
				}
				break;
			case INSTALL_PROGRESS:
				if (mCurProgress < mTotalProgress - 1) {
					Log.i(TAG, "curProgress = " + mCurProgress);
					mCurProgress++;
					if (mInstallFlag) {
						msgToUi(DownConstants.MSG_INSTALL_PROGRESS);
					} else {
						msgToUi(DownConstants.MSG_UNINSTALL_PROGRESS);
					}

				}
				break;
			default:
				break;
			}
		}
	};

	public void startInstall() {
		mTimer.schedule(task, 500, 500);

		/*
		 * Bi.sendBiMsg(Bi.BICT_FIRMWARE, Bi.BICC_APP_INSTALL,
		 * info.getAppCode(), info.getVersion(), "",
		 * PublicFun.getsAppSource()+"", PublicFun.getsAppSourceId(),
		 * PublicFun.getsAppSourceName(), PublicFun.getsPackageName(),
		 * PublicFun.getsVersionName());
		 */
	}

	public void stopInstall() {
		task.cancel();
	}

	public Installer(Context context, ArrayList<Handler> hand,
			DownloadInfo info, boolean installFlag) {
		mContext = context;
		mHandleList = hand;
		this.info = info;
		mInstallFlag = installFlag;
		if (!mInstallFlag) {
			mTotalProgress = 5;
		}
	}

	public void PackageInstall() {
		int installFlags = 0;
		PackageManager pm = mContext.getPackageManager();
		mPkgInfo = pm.getPackageArchiveInfo(info.getDownApkPath(),
				PackageManager.GET_PERMISSIONS
						| PackageManager.GET_UNINSTALLED_PACKAGES);

		try {
			PackageInfo pi = pm.getPackageInfo(mPkgInfo.packageName,
					PackageManager.GET_UNINSTALLED_PACKAGES);
			if (pi != null) {
				installFlags |= PackageManager.INSTALL_REPLACE_EXISTING;
			}

		} catch (Exception e) {
			Log.w(TAG, "PackageInstall" + e.toString());
		}

		if ((installFlags & PackageManager.INSTALL_REPLACE_EXISTING) != 0) {
			Log.w(TAG, "Replacing package:" + mPkgInfo.packageName);
		}

		PackageInstallObserver observer = new PackageInstallObserver();
		startInstall();
		msgToUi(DownConstants.MSG_INSTALL_PROGRESS);

		try {
			pm.installPackage(Uri.parse("file://" + info.getDownApkPath()),
					observer, installFlags, mPkgInfo.packageName);
		} catch (Exception e) {
			Log.w(TAG, "PackageInstall" + e.toString());
		}

	}

	public void PackageUnInstall() {
		try {
			startInstall();
			msgToUi(DownConstants.MSG_UNINSTALL_PROGRESS);
			PackageManager pm = mContext.getPackageManager();
			Log.i(TAG,
					"PackageUnInstall info.getPkgName() = " + info.getPkgName());
			mPkgInfo = pm.getPackageInfo(info.getPkgName(),
					PackageManager.GET_PERMISSIONS);

			PackageDeleteObserver observer = new PackageDeleteObserver();
			mContext.getPackageManager().deletePackage(mPkgInfo.packageName,
					observer, true ? PackageManager.DELETE_ALL_USERS : 0);

			/*
			 * Bi.sendBiMsg(Bi.BICT_FIRMWARE, Bi.BICC_APP_UNINSTALL,
			 * info.getAppCode(), info.getVersion(), "");
			 */
		} catch (Exception e) {
		}

	}

	class PackageInstallObserver extends IPackageInstallObserver.Stub {
		public void packageInstalled(String packageName, int returnCode) {
			// if(returnCode == PackageManager.INSTALL_SUCCEEDED){
			Message msg = new Message();
			msg.what = INSTALL_COMPLETE;
			msg.arg1 = returnCode;
			mHandler.sendMessage(msg);
			// }

			Log.w(TAG, "PackageInstall" + "install_complete" + "returnCode = "
					+ returnCode);
		}
	}

	class PackageDeleteObserver extends IPackageDeleteObserver.Stub {
		public void packageDeleted(String packageName, int returnCode) {
			Message msg = new Message();
			msg.what = UNINSTALL_COMPLETE;
			msg.obj = packageName;
			msg.arg1 = returnCode;
			mHandler.sendMessage(msg);
			Log.w(TAG, "PackageInstall" + "Uninstall_complete"
					+ "returnCode = " + returnCode);
		}
	}

	private void msgToUi(int msgStatus) {
		int handleCount = mHandleList.size();
		for (int i = 0; i < handleCount; ++i) {
			InstallInfo installInfo = new InstallInfo();
			installInfo.setPkgName(info.getPkgName());
			installInfo.setCurrentProgess(mCurProgress);
			installInfo.setTotalProgess((int) mTotalProgress);

			Message m = new Message();
			m.what = msgStatus;
			m.obj = installInfo;
			mHandleList.get(i).sendMessage(m);
		}
	}

	private void sendDownMsgBroadCast(String action, String extraKey,
			String status, String valueKey, DownloadInfo info) {
		Intent intent = new Intent();
		intent.setAction(action);
		Bundle bundle = new Bundle();
		bundle.putString(extraKey, status);
		if (info != null) {
			bundle.putSerializable(valueKey, info);
		}
		intent.putExtras(bundle);
		mContext.sendBroadcast(intent);
	}

}
