/**
 * Copyright © 2013GreatVision. All rights reserved.
 * 
 * @Title: MainActivity.java
 * @Prject: BananaTvSetting
 * @Description: TODO
 * @author: lijg
 * @date: 2013-11-28 上午10:49:32
 * @version: V1.0
 */
package com.sz.ead.framework.mul2launcher.settings.upgrade;

/**
 * @author lijungang
 *
 */
public class UpgradeBgStatus {
	public static final int UP_STATUS_INIT            = 0x1000;
    public static final int UP_STATUS_START_CHECK_VER                =0x1001; //开始检测版本
    public static final int UP_STATUS_CHECK_UPGRADE_ERROR            =0x1002;//网络链接失败
    public static final int UP_STATUS_CHECK_LOWER_VERSION            =0x1003;//当前版本为最新版本
    public static final int UP_STATUS_CHECK_HIGER_VERSION            =0x1004;//发现新版本
    public static final int UP_STATUS_FREE_SIZE_ERROR                =0x1005;//剩余空间不足
    public static final int UP_STATUS_START_DOWNLOAD                 =0x1006;//开始下载
    public static final int UP_STATUS_DOWNLOADING_FAILED             =0x1007;//下载失败，比如断网
    public static final int UP_STATUS_DOWNLOADING_TIMEOUT            =0x1008;//下载超时
    public static final int UP_STATUS_MD5_CHECK_SUCCESS              = 0x1009; // 升级文件MD5校验成功
    public static final int UP_STATUS_MD5_CHECK_FAILED               =0x100a; // 升级文件MD5校验失败
    
    public static final int UP_STATUS_DOWNLOADING                    =0x100b;//正在下载
    public static final int UP_STATUS_CHECKING_VERSION               =0x100c;//正在检测版本
    public static final int UP_STATUS_CHECK_END                      =0x100d;//检测版本完成
    public static final int UP_STATUS_SHOW_DIALOG                    =0x100e;//show dialog
    public static final int UP_STATUS_UPGRADE_IME                    =0x100f;//后台跳转升级界面
    public static final int UP_STATUS_DIALOG_FORCE_UPGRADE           =0x1010;//强制升级对话框
    public static final int UP_STATUS_IME_UPGRADE                    =0x2000;//调用升级接口
    
    public static final String UPGRADE_TIME_COUNT = "persist.sys.upgrade.timecount";//存当前下载升级包时间
}
