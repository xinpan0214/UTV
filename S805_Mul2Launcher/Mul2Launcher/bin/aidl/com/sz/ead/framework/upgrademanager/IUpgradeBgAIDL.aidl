package com.sz.ead.framework.upgrademanager;

import com.sz.ead.framework.upgrademanager.IUpgradeBgCallback;

interface IUpgradeBgAIDL {
	String getVersion();
	void registerCallback(IUpgradeBgCallback callback);
	void unregisterCallback(IUpgradeBgCallback callback);
	
	void startCheckVersion();
	void startDownload();
}