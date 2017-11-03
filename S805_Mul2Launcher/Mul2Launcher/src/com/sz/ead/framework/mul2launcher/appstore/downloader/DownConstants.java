package com.sz.ead.framework.mul2launcher.appstore.downloader;

public class DownConstants {
	
	/**
	 * 下载队列各种状态
	 * 下载状态   0是未知状态    1 收藏      2暂停     3准备下载    4正在下载   5下载完  6下载失败   7下载中断  100已安装
	 */
	public static final int STATUS_DOWN_UNKNOWN = 0x0000;	
	public static final int STATUS_DOWN_COLLECT = 0x0001;
	public static final int STATUS_DOWN_PAUSE = 0x0002;
	public static final int STATUS_DOWN_READY = 0x0004;
	public static final int STATUS_DOWN_DOWNING = 0x0008;
	public static final int STATUS_DOWN_DOWNCOMPLETE = 0x0010;
	//public static final int STATUS_DOWN_DOWNFAIL = 0x0020;
	public static final int STATUS_DOWN_BREAKPOINT = 0x0040;	
	public static final int STATUS_DOWN_STOP = 0x0080;
	public static final int STATUS_DOWN_INSTALLING = 0x0100;
	public static final int STATUS_APP_INSTALLED=0x0200;
	public static final int STATUS_APP_CAN_UPDATE=0x0400;
	public static final int STATUS_APP_CAN_REPLACE=0x0600; //其它渠道安装应用 本商城可覆盖
	
	//已经下载状态
	public static final int STATUS_DOWN_STARTED = STATUS_DOWN_PAUSE | STATUS_DOWN_READY 
			| STATUS_DOWN_DOWNING | STATUS_DOWN_DOWNCOMPLETE | STATUS_DOWN_INSTALLING;
									

	/**
	 * 下载给UI的通知消息
	 */
	// 下载完成消息
	public static final int MSG_DOWN_DOWNOK = 0x1000;
	
	// 下载进度消息
	public static final int MSG_DOWN_PROGRESS = 0x2000;
	
	// 下载失败消息
	public static final int MSG_DOWN_DOWNFAIL = 0x3000;
	
	// 下载取消消息
	public static final int MSG_DOWN_DOWNCANCEL = 0x4000;
	
	// 下载中
	public static final int MSG_DOWN_DOWNLOADING = 0x5000;
	
	// 更新
	public static final int MSG_DOWN_UPDATE_INFO = 0x6000;
	
	// 安装进度
	public static final int MSG_INSTALL_PROGRESS = 0x7000;
	
	// 卸载进度
	public static final int MSG_UNINSTALL_PROGRESS = 0x8000;
	
	// 卸载进度
	public static final int MSG_INSTALL_SUCCESS = 0x9000;
	
	// 截图加载完成
	public static final int MSG_SCREENSHOT_SUCCESS = 0x1011;
	
	/**
	 * 广播action
	 */
	// 自己下载服务内部action
	public static final String DOWN_MANAGE_ACTION="com.sz.ead.framework.appstore.action.downloadmanage";
	
	// 提示ui更新界面action
	public static final String DOWN_STATUS_ACTION="com.sz.ead.framework.appstore.update";
	
	public static final String URL_ACTION="com.sz.ead.framework.FetchServer.ServerUrlChange";
	
	//发广播通知launcher是商城安装应用
	public static final String DOWN_INSTALL_ACTION="com.sz.ead.framework.appstore.downloader.DownConstants.Install_Source";
	public static final String DOWN_INSTALL_PKG_KEY="install_package_name";
	public static final String DOWN_INSTALL_APPID_KEY="install_appid";
	
	//下载应用icon的action和Key
	//public static final String DOWN_DOWN_ICON_ACTION="com.sz.ead.framework.appstore.downloader.DownConstants.DownIcon_Complete";
	//public static final String DOWN_ICON_PKG_KEY="down_icon_pkg_key";
	
	//取storeid
	public static final String DOWN_APPSTORE_ID="com.sz.ead.framework.info.InfoService.storeid";
	
	//storeid KEY
	public static final String DOWN_APPSTORE_ID_KEY="STORE_ID";
	
	// 列表刷新广播
	//public static final String LIST_FREASH_ACTION="um.market.android.listfreash";
	
	/**
	 * 广播传消息key
	 */
	// 传状态 KEY
	public static final String DOWN_STATUS_DOWNLOAD_STATUS_KEY="down_status_download_status_key";
	
	// 传递数据key
	public static final String DOWN_STATUS_DOWNLOAD_DATA_KEY="down_status_download_data_key";
	
	/**
	 * 广播各种状态消息
	 */
	// 更新状态消息
	public static final String DOWN_BROADCAST_TO_UI_UPDATE="down_update_extra";
	
	// 发送安装消息
	public static final String DOWN_BROADCAST_TO_UI_INSTALLED="apk_installed";
	
	// 发送移除消息
	public static final String DOWN_BROADCAST_TO_UI_REMOVED="apk_removed";
	
	// 发送下载完成消息
	public static final String DOWN_BROADCAST_TO_UI_STATUS_DOWN_COMPLETE="apk_down_complete";
	
	// 收藏成功消息
	public static final String DOWN_BROADCAST_TO_UI_STATUS_COLLECT_SUCCESS="apk_collect_success";
	
	// 下载失败消息
	public static final String DOWN_BROADCAST_TO_UI_STATUS_DOWNLOAD_FAIL="apk_down_fail";
	
	// 服务bind成功消息
	public static final String DOWN_BROADCAST_TO_UI_BIND_SERVICE_SUCCESS="bind_service_sucess";	
	
	// 向服務請求更新
	public static final String DOWN_BROADCAST_TO_SERVICE_REQUEST_UPDATE="to_service_update";
	
	// 要求服务器检查更新
	public static final String DOWN_BROADCAST_TO_SERVICE_NEED_CHECK_UPDATE="need_service_check_update";
	
	// sd卡没有足够空间
	public static final String DOWN_BROADCAST_TO_UI_STATUS_SD_NOT_ENOUGH_SPACE="sd_not_enough_space";
	
	// sd卡不存在
	public static final String DOWN_BROADCAST_TO_UI_STATUS_SD_NOT_EXIST="sd_not_exist";
	
	// 打开wifi所有下载进入下载队列
	public static final String DOWN_BROADCAST_OPENWIFI="open_wifi";
	
	// 开始下载下一个app
	public static final String DOWN_BROADCAST_START_NEXT_APP="start_next_app";
	
	/**
	 * 下载已完成列表已安装更新状态
	 */
	// 0已安装	1更新
	public static final int STATUS_TODOWNLIST_DOWNLOAD = 0;
	public static final int STATUS_TODOWNLIST_UPDATE = 1;
	
	
	/**
	 * notifcation跳转
	 */
	// 主tab KEY
	public static final String SKIP_MAIN_PAGE = "SKIP_MAIN_PAGE";
	
	// 二级tab KEY
	public static final String SKIP_SUB_PAGE= "SKIP_SUB_PAGE";
	
	// 一级TAB分类
	public static final String SKIP_TO_RECOMMEND ="SKIP_TO_RECOMMEND";
	public static final String SKIP_TO_APP_RANK="SKIP_TO_APP_RANK";
	public static final String SKIP_TO_APP_SORT="SKIP_TO_APP_SORT";
	public static final String SKIP_TO_APP_SEARCH="SKIP_TO_APP_SEARCH";
	public static final String SKIP_TO_DOWNLAOD ="SKIP_TO_DOWNLAOD";
	
	// 下载二级分类
	public static final String SKIP_TO_DOWNLAOD_DOWNLOADED= "SKIP_TO_DOWNLAOD_DOWNLOADED";
	public static final String SKIP_TO_DOWNLAOD_DOWNLOADING= "SKIP_TO_DOWNLAOD_DOWNLOADING";
	public static final String SKIP_TO_DOWNLAOD_COLLECT= "SKIP_TO_DOWNLAOD_COLLECT";
	

}
