package com.sz.ead.framework.upgrademanager;

oneway interface IUpgradeBgCallback {
	void onCheckVersionCallback(int ret);
	void onDownloadProgress(int progress);
	void onDownloadComplete(String filePath, String upgrade_version);
	void onDownloadFailed(String upgrade_version);
	void onDownloadingTimeout(String upgrade_version);
	void onCheckMd5Failed(String upgrade_version);
}