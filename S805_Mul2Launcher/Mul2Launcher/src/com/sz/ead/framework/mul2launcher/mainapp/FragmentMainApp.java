/**
 * Copyright © 2015GreatVision. All rights reserved.
 *
 * @Title: FragmentMainApp.java
 * @Prject: Mul2Launcher
 * @Package: com.sz.ead.framework.mul2launcher.mainapp
 * @Description: 我的应用
 * @author: zhaoqy
 * @date: 2015-4-22 上午10:50:48
 */
package com.sz.ead.framework.mul2launcher.mainapp;

import java.util.ArrayList;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.content.DialogInterface.OnDismissListener;
import android.content.DialogInterface.OnShowListener;
import android.content.pm.PackageManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.sz.ead.framework.mul2launcher.FragmentBase;
import com.sz.ead.framework.mul2launcher.MainActivity;
import com.sz.ead.framework.mul2launcher.R;
import com.sz.ead.framework.mul2launcher.dataprovider.dataitem.AppItem;
import com.sz.ead.framework.mul2launcher.db.InstalledTable;
import com.sz.ead.framework.mul2launcher.mainapp.AppItemView.OnAppClickListener;
import com.sz.ead.framework.mul2launcher.mainapp.MenuDialog.OnMenuItemSelectedListener;
import com.sz.ead.framework.mul2launcher.mainapp.PromptDialog.PromptDialogClickListener;
import com.sz.ead.framework.mul2launcher.util.LogUtil;
import com.sz.ead.framework.mul2launcher.util.StbInfo;
import com.szgvtv.ead.framework.bi.Bi;

public class FragmentMainApp extends FragmentBase implements OnAppClickListener, OnFocusChangeListener
{
	public static final int APPITEM_STATUS_DEFAULT = 0;
	public static final int APPITEM_STATUS_UNINSTALL = 1;
	public static final int APPITEM_STATUS_MOVE = 2;
	private Activity mActivity;
	private PageIndicator mPageIndicator;
	private AppItemView mAppItemView[] = new AppItemView[18];
	private ImageView mFocusView;
	private RelativeLayout mUninstall;
	private TextView mPercent;
	private ProgressBar mBar;
	private MenuDialog mMenuDialog = null;
	private PromptDialog mPromptDialog = null;
	private ArrayList<AppItem> mAppItems = new ArrayList<AppItem>();
	private int mCurPage = 0;
	private int mTotalPage = 0;
	private int mAppCount = 0;
	private int mAppSize = 18;
	private int mFocusIndex = 0;
	private int mSrcIndex = -1;
	private int mSrcPage = -1;
	private int mStatus = 0;
	private boolean mUpdating = false;
	private boolean mInstallSuc = false;
	private boolean mMoving = false;
	
	@Override
	public void onAttach(Activity activity) 
	{
		super.onAttach(activity);
		mActivity = activity;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
	{
		View view = inflater.inflate(R.layout.mainapp_fragment, container, false);
		mAppItemView[0] = (AppItemView) view.findViewById(R.id.mainapp_item0);
		mAppItemView[1] = (AppItemView) view.findViewById(R.id.mainapp_item1);
		mAppItemView[2] = (AppItemView) view.findViewById(R.id.mainapp_item2);
		mAppItemView[3] = (AppItemView) view.findViewById(R.id.mainapp_item3);
		mAppItemView[4] = (AppItemView) view.findViewById(R.id.mainapp_item4);
		mAppItemView[5] = (AppItemView) view.findViewById(R.id.mainapp_item5);
		mAppItemView[6] = (AppItemView) view.findViewById(R.id.mainapp_item6);
		mAppItemView[7] = (AppItemView) view.findViewById(R.id.mainapp_item7);
		mAppItemView[8] = (AppItemView) view.findViewById(R.id.mainapp_item8);
		mAppItemView[9] = (AppItemView) view.findViewById(R.id.mainapp_item9);
		mAppItemView[10] = (AppItemView) view.findViewById(R.id.mainapp_item10);
		mAppItemView[11] = (AppItemView) view.findViewById(R.id.mainapp_item11);
		mAppItemView[12] = (AppItemView) view.findViewById(R.id.mainapp_item12);
		mAppItemView[13] = (AppItemView) view.findViewById(R.id.mainapp_item13);
		mAppItemView[14] = (AppItemView) view.findViewById(R.id.mainapp_item14);
		mAppItemView[15] = (AppItemView) view.findViewById(R.id.mainapp_item15);
		mAppItemView[16] = (AppItemView) view.findViewById(R.id.mainapp_item16);
		mAppItemView[17] = (AppItemView) view.findViewById(R.id.mainapp_item17);
		mFocusView = (ImageView) view.findViewById(R.id.mainapp_focus);
		mUninstall = (RelativeLayout) view.findViewById(R.id.mainapp_uninstall);
		mBar = (ProgressBar) view.findViewById(R.id.mainapp_progress);
		mPercent = (TextView) view.findViewById(R.id.mainapp_percent);
		mPageIndicator = (PageIndicator) view.findViewById(R.id.mainapp_pageindicator);
		
		init();
		initData();
		setAppMenu();
		registerIntentReceivers();
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) 
	{
		super.onActivityCreated(savedInstanceState);
	}
	
	private void init()
	{
		mPageIndicator.setTop(676);
		for (int i=0; i<mAppSize; i++)
		{
			mAppItemView[i].setVisibility(View.INVISIBLE);
			mAppItemView[i].setOnAppClick(this);
			mAppItemView[i].setOnFocusChangeListener(this);
			mAppItemView[i].setAppItemSource(AppItemView.APPITEM_SOURCE_MAINAPP);
		}
	}
	
	/**
	 * 
	 * @Title: initData
	 * @Description: 
	 * @return: void
	 */
	private void initData()
	{
		mAppItems = getAppList();
		if (mAppItems != null)
		{
			AppItem appItem = new AppItem();
			appItem.setAppName(mActivity.getResources().getString(R.string.mainapp_add_app));
			appItem.setIconBitmap(mActivity.getResources().getDrawable(R.drawable.mainapp_add_app_default));
			mAppItems.add(appItem);
			mAppCount = mAppItems.size();
			
			if (mAppCount > 0)
			{
				mCurPage = 1;
				mTotalPage = (mAppCount-1)/mAppSize + 1;
				freshCurPageAppList();
			}
		}
	}
	
	/**
	 * 
	 * @Title: getAppList
	 * @Description: 从系统获取应用列表将其插入数据库，再从数据库返回应用列表
	 * @return
	 * @return: ArrayList<AppItem>
	 */
	private ArrayList<AppItem> getAppList()
	{
		ArrayList<AppItem> temps = ApkUtil.getInstalledApp(mActivity);
		for (int i=0; i<temps.size(); i++)
		{
			InstalledTable.insertApp(mActivity, temps.get(i).getPkgName());
		}
		ArrayList<AppItem> appItems = InstalledTable.queryInstalledAppList();
		return appItems;
	}
	
	/**
	 * 
	 * @Title: freshCurPageAppList
	 * @Description: 刷新页面
	 * @return: void
	 */
	private void freshCurPageAppList()
	{
		for (int i=0; i<mAppSize; i++)
		{
			if(i+(mCurPage-1)*mAppSize < mAppCount)
			{
				AppItem appItem = mAppItems.get(i+(mCurPage-1)*mAppSize);
				mAppItemView[i].setAppItemSource(AppItemView.APPITEM_SOURCE_MAINAPP);
				mAppItemView[i].setAppItem(appItem);
				mAppItemView[i].setVisibility(View.VISIBLE);
			}
			else
			{
				mAppItemView[i].setVisibility(View.INVISIBLE);
			}
		}
		freshPageIndicator();
	}
	
	/**
	 * 
	 * @Title: freshPageIndicator
	 * @Description: 刷新页码
	 * @return: void
	 */
	private void freshPageIndicator()
	{
		if (mCurPage > 0 && mCurPage <= mTotalPage)
		{
			mPageIndicator.setTotalPage(mTotalPage);
			mPageIndicator.setCurPage(mCurPage);
		}
	}
	
	/**
	 * 
	 * @Title: setAppMenu
	 * @Description: 设置菜单项
	 * @return: void
	 */
	private void setAppMenu()
	{
		mMenuDialog = new MenuDialog(mActivity);
		mMenuDialog.setOnMenuItemSelectedListener(new OnMenuItemSelectedListener() 
		{
			public void OnMenuItemSelected(int menuItem) 
			{
				switch (menuItem) 
				{
				case MenuDialog.MOVE:
				{
					mStatus = APPITEM_STATUS_MOVE;
					setAppItemStatus(mAppItemView[mFocusIndex], mFocusView, mStatus);
					
					mSrcIndex = mFocusIndex;
					mSrcPage = mCurPage;
					break;
				}
				case MenuDialog.TOP:	
				{
					mMoving = true;
					//先删除该Item, 然后插入到首部
					int index = (mCurPage-1)*mAppSize + mFocusIndex;
					AppItem appItem = mAppItems.get(index);
					mAppItems.remove(index);
					mAppItems.add(0, appItem);
					InstalledTable.moveAppPostion(index, 0);
					
					//刷新页面, 光标放在第一个应用上
					mCurPage = 1;
					mFocusIndex = 0;
					freshCurPageAppList();
					setAppItemStatus(mAppItemView[mFocusIndex], mFocusView, mStatus);
					mMoving = false;
					break;
				}
				case MenuDialog.BOTTOM:
				{
					mMoving = true;
					int index = (mCurPage-1)*mAppSize + mFocusIndex;
					AppItem appItem = mAppItems.get(index);
					mAppItems.remove(index);
					mAppItems.add(mAppCount-2, appItem);
					InstalledTable.moveAppPostion(index, mAppCount-2);
					
					//刷新页面, 光标放在最后一个应用上
					mCurPage = (mAppCount-2)/mAppSize + 1;
					mFocusIndex = (mAppCount-2)%mAppSize;
					freshCurPageAppList();
					setAppItemStatus(mAppItemView[mFocusIndex], mFocusView, mStatus);
					mMoving = false;
					break;
				}
				case MenuDialog.UNINSTALL:
				{
					int index = (mCurPage-1)*mAppSize + mFocusIndex;
					AppItem appItem = mAppItems.get(index);
					doUnInstallApp(appItem);
					break;
				}
				default:
					break;
				}
			}
		});
		mMenuDialog.setOnDismissListener(new OnDismissListener() 
		{
			public void onDismiss(DialogInterface arg0) 
			{
			}
		});
		mMenuDialog.setOnShowListener(new OnShowListener() 
		{
			public void onShow(DialogInterface arg0) 
			{
			}
		});
	}
	
	/**
	 * 
	 * @Title: doUnInstallApp
	 * @Description: 卸载应用
	 * @param appItem
	 * @return: void
	 */
	public void doUnInstallApp(AppItem appItem) 
	{
		try 
		{
			mPromptDialog = new PromptDialog(mActivity);
			final String appName = appItem.getAppName();
			final String packageName = appItem.getPkgName();
			final String appCode = appItem.getAppCode();
			final String appVersion = appItem.getVersion();
			mPromptDialog.setOnPromptDialogClickListener(new PromptDialogClickListener() 
			{
				@Override
				public void onClick(View v) 
				{
					switch (v.getId()) 
					{
					case R.id.promptdialog_sure:
					{
						Bi.sendBiMsg(Bi.BICT_FIRMWARE, Bi.BICC_APP_UNINSTALL, appCode, appVersion, StbInfo.getCurLang(mActivity));
						try 
						{
							//开始卸载, 设置正在卸载标识符为true
							mStatus = APPITEM_STATUS_UNINSTALL;
							ApkUtil.uninstallApk(mActivity, packageName);
							Bi.sendBiMsg(Bi.BICT_FIRMWARE, Bi.BICC_UNINSTALL_RESULT, appCode, appVersion, StbInfo.getCurLang(mActivity), "0");
						} 
						catch (Exception e) 
						{
							Bi.sendBiMsg(Bi.BICT_FIRMWARE, Bi.BICC_UNINSTALL_RESULT, appCode, appVersion, StbInfo.getCurLang(mActivity), "-1");
						}
						mPromptDialog.dismiss();
						break;
					}
					case R.id.promptdialog_cancel:
					{
						mPromptDialog.dismiss();
						break;
					}	
					default:
						break;
					}
				}
			});
			mPromptDialog.show();
			mPromptDialog.setAppName(appName);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}

	@Override
	public int dispatchKeyEvent(KeyEvent event) 
	{
		int ret = -1;
		
		if (event.getAction() == KeyEvent.ACTION_DOWN) 
		{
			switch (event.getKeyCode()) 
			{
			case KeyEvent.KEYCODE_DPAD_UP:
			{
				ret = doKeyUp();
				break;
			}
			case KeyEvent.KEYCODE_DPAD_DOWN:
			{
				ret = doKeyDown();
				break;
			}
			case KeyEvent.KEYCODE_PAGE_UP:
			{
				ret = doKeyPageUp();
				break;
			}
			case KeyEvent.KEYCODE_PAGE_DOWN:
			{
				ret = doKeyPageDown();
				break;
			}
			case KeyEvent.KEYCODE_DPAD_LEFT:
			{
				ret = doKeyLeft();
				break;
			}
			case KeyEvent.KEYCODE_DPAD_RIGHT:
			{
				ret = doKeyRight();
				break;
			}
			case KeyEvent.KEYCODE_BACK:
			{
				ret = doKeyBack();
				break;
			}
			case KeyEvent.KEYCODE_MENU:
			{
				ret = doKeyMenu();
				break;
			}
			default:
				break;
			}
		}
		
		return ret;
	}
	
	/**
	 * 
	 * @Title: doKeyUp
	 * @Description: 响应上键
	 * @return
	 * @return: int
	 */
	private int doKeyUp()
	{
		switch (mStatus) 
		{
		case APPITEM_STATUS_DEFAULT:
		{
			if (mFocusIndex >= 0 && mFocusIndex <= 5)
			{
				mFocusView.setVisibility(View.INVISIBLE);  //光标返回到导航栏上
				return 2;
			}
			else
			{
				mFocusIndex -= 6;
				setAppItemStatus(mAppItemView[mFocusIndex], mFocusView, mStatus);
			}
			break;
		}
		case APPITEM_STATUS_MOVE:
		{
			if (mFocusIndex > 5)
			{
				beforeMoveAppItem();
				mFocusIndex -= 6;
				setAppItemStatus(mAppItemView[mFocusIndex], mFocusView, mStatus);
				afterMoveAppItem();
			}
			break;
		}
		default:
			break;
		}
		return 0;
	}
	
	/**
	 * 
	 * @Title: doKeyDown
	 * @Description: 响应下键
	 * @return
	 * @return: int
	 */
	private int doKeyDown()
	{
		switch (mStatus) 
		{
		case APPITEM_STATUS_DEFAULT:
		{
			if (mCurPage == mTotalPage)
			{
				int curLine = mFocusIndex/6 + 1;  //这里的6指每行显示6个AppItemView
				int maxIndex = (mAppCount-1)%mAppSize;
				int maxLine = maxIndex/6 + 1;
				if (curLine < maxLine)
				{
					mFocusIndex += 6;
					if (mFocusIndex > maxIndex)
					{
						mFocusIndex = maxIndex;
					}
					setAppItemStatus(mAppItemView[mFocusIndex], mFocusView, mStatus);
				}
			}
			else
			{
				if (mFocusIndex >= 0 && mFocusIndex <= 11)
				{
					mFocusIndex += 6;
					setAppItemStatus(mAppItemView[mFocusIndex], mFocusView, mStatus);
				}
			}
			break;
		}
		case APPITEM_STATUS_MOVE:
		{
			if (mCurPage == mTotalPage)
			{
				int curLine = mFocusIndex/6 + 1;
				int maxIndex = (mAppCount-1)%mAppSize;
				int maxLine = maxIndex/6 + 1;
				if (curLine < maxLine-1)
				{
					beforeMoveAppItem();
					mFocusIndex += 6;
					setAppItemStatus(mAppItemView[mFocusIndex], mFocusView, mStatus);
					afterMoveAppItem();
				}
				else if (curLine == maxLine-1)
				{
					int maxLineMaxIndex = maxIndex%6;
					if (maxLineMaxIndex > 0)
					{
						beforeMoveAppItem();
						mFocusIndex += 6;
						if (mFocusIndex > maxIndex-1) //不能移到"添加应用"这个位置
						{
							mFocusIndex = maxIndex-1;
						}
						setAppItemStatus(mAppItemView[mFocusIndex], mFocusView, mStatus);
						afterMoveAppItem();
					}
				}
			}
			else
			{
				if (mFocusIndex >= 0 && mFocusIndex <= 11)
				{
					beforeMoveAppItem();
					mFocusIndex += 6;
					setAppItemStatus(mAppItemView[mFocusIndex], mFocusView, mStatus);
					afterMoveAppItem();
				}
			}
			break;
		}
		default:
			break;
		}
		return 0;
	}
	
	/**
	 * 
	 * @Title: doKeyPageUp
	 * @Description: 响应上一页
	 * @return
	 * @return: int
	 */
	private int doKeyPageUp()
	{
		switch (mStatus) 
		{
		case APPITEM_STATUS_DEFAULT:
		{
			if (mCurPage > 1)
			{
				mCurPage--;
				freshCurPageAppList();
				mFocusIndex = 0;
				setAppItemStatus(mAppItemView[mFocusIndex], mFocusView, mStatus);
			}
			break;
		}
		case APPITEM_STATUS_MOVE:
		{
			if (mCurPage > 1)
			{
				beforeMoveAppItem();
				mCurPage--;
				freshCurPageAppList();
				mFocusIndex = 0;
				setAppItemStatus(mAppItemView[mFocusIndex], mFocusView, mStatus);
				afterMoveAppItem();
			}
			break;
		}
		default:
			break;
		}
		return 0;
	}
	
	/**
	 * 
	 * @Title: doKeyPageDown
	 * @Description: 响应下一页
	 * @return
	 * @return: int
	 */
	private int doKeyPageDown()
	{
		switch (mStatus) 
		{
		case APPITEM_STATUS_DEFAULT:
		{
			if (mCurPage < mTotalPage)
			{
				mCurPage++;
				freshCurPageAppList();
				mFocusIndex = 0;
				setAppItemStatus(mAppItemView[mFocusIndex], mFocusView, mStatus);
			}
			break;
		}
		case APPITEM_STATUS_MOVE:
		{
			if (mCurPage < mTotalPage)
			{
				if (mCurPage == mTotalPage-1)  //不能移动到"添加应用"这个位置
				{
					int maxIndex = (mAppCount-1)%mAppSize;
					if (maxIndex == 0)
					{
						return 0;
					}
				}
			
				beforeMoveAppItem();
				mCurPage++;
				freshCurPageAppList();
				mFocusIndex = 0;
				setAppItemStatus(mAppItemView[mFocusIndex], mFocusView, mStatus);
				afterMoveAppItem();
			}
			break;
		}
		default:
			break;
		}
		return 0;
	}
	
	/**
	 * 
	 * @Title: doKeyLeft
	 * @Description: 响应左键
	 * @return
	 * @return: int
	 */
	private int doKeyLeft()
	{
		switch (mStatus) 
		{
		case APPITEM_STATUS_DEFAULT:
		{
			if (mFocusIndex == 0 || mFocusIndex == 6 || mFocusIndex == 12)  //左边界
			{
				if (mTotalPage == 1)  //只有1页
				{
					mFocusIndex -= 1;
					if (mFocusIndex < 0)
					{
						mFocusIndex = mAppCount-1;
					}
					setAppItemStatus(mAppItemView[mFocusIndex], mFocusView, mStatus);
				}
				else  //至少有2页
				{
					if (mCurPage > 1)
					{
						mCurPage--;
						mFocusIndex += 5;
						freshCurPageAppList();
						setAppItemStatus(mAppItemView[mFocusIndex], mFocusView, mStatus);
					}
				}
			}
			else
			{
				mFocusIndex -= 1;
				setAppItemStatus(mAppItemView[mFocusIndex], mFocusView, mStatus);
			}
			break;
		}
		case APPITEM_STATUS_MOVE:
		{
			if (mFocusIndex == 0 || mFocusIndex == 6 || mFocusIndex == 12)  //左边界
			{
				if (mTotalPage == 1)  //只有1页
				{
					if (mFocusIndex > 0)  //不能移动到"添加应用"这个位置, 只有1页时, 不能循环
					{
						beforeMoveAppItem();
						mFocusIndex -= 1;
						setAppItemStatus(mAppItemView[mFocusIndex], mFocusView, mStatus);
						afterMoveAppItem();
					}
				}
				else  //至少有2页
				{
					if (mCurPage > 1)
					{
						beforeMoveAppItem();
						mCurPage--;
						mFocusIndex += 5;
						freshCurPageAppList();
						setAppItemStatus(mAppItemView[mFocusIndex], mFocusView, mStatus);
						afterMoveAppItem();
					}
				}
			}
			else
			{
				beforeMoveAppItem();
				mFocusIndex -= 1;
				setAppItemStatus(mAppItemView[mFocusIndex], mFocusView, mStatus);
				afterMoveAppItem();
			}
			break;
		}
		default:
			break;
		}
		return 0;
	}
	
	/**
	 * 
	 * @Title: doKeyRight
	 * @Description: 响应右键
	 * @return
	 * @return: int
	 */
	private int doKeyRight()
	{
		switch (mStatus) 
		{
		case APPITEM_STATUS_DEFAULT:
		{
			if (mFocusIndex == 5 || mFocusIndex == 11 || mFocusIndex == 17)  //右边界
			{
				if (mTotalPage == 1)  //只有1页
				{
					mFocusIndex += 1;
					if (mFocusIndex > mAppCount-1)
					{
						mFocusIndex = 0;
					}
					setAppItemStatus(mAppItemView[mFocusIndex], mFocusView, mStatus);
				}
				else  //至少有2页
				{
					if (mCurPage < mTotalPage-1)
					{
						mCurPage++;
						mFocusIndex -= 5;
						freshCurPageAppList();
						setAppItemStatus(mAppItemView[mFocusIndex], mFocusView, mStatus);
					}
					else if (mCurPage == mTotalPage-1)
					{
						mCurPage++;
						mFocusIndex -= 5;
						int maxIndex = (mAppCount-1)%mAppSize;
						if (mFocusIndex > maxIndex)
						{
							mFocusIndex = maxIndex;
						}
						freshCurPageAppList();
						setAppItemStatus(mAppItemView[mFocusIndex], mFocusView, mStatus);
					}
				}
			}
			else
			{
				if (mCurPage < mTotalPage)
				{
					mFocusIndex += 1;
					setAppItemStatus(mAppItemView[mFocusIndex], mFocusView, mStatus);
				}
				else
				{
					int maxIndex = (mAppCount-1)%mAppSize;
					if (mFocusIndex < maxIndex)
					{
						mFocusIndex += 1;
						setAppItemStatus(mAppItemView[mFocusIndex], mFocusView, mStatus);
					}
					else
					{
						if (mTotalPage == 1)
						{
							mFocusIndex = 0;
							setAppItemStatus(mAppItemView[mFocusIndex], mFocusView, mStatus);
						}
					}
				}
			}
			break;
		}
		case APPITEM_STATUS_MOVE:
		{
			if (mFocusIndex == 5 || mFocusIndex == 11 || mFocusIndex == 17)  //右边界
			{
				if (mTotalPage == 1)  //只有1页
				{
					if (mFocusIndex < mAppCount-2) //不能移动到"添加应用"这个位置, 只有1页时, 不能循环
					{
						beforeMoveAppItem();
						mFocusIndex += 1;
						setAppItemStatus(mAppItemView[mFocusIndex], mFocusView, mStatus);
						afterMoveAppItem();
					}
				}
				else  //至少有2页
				{
					if (mCurPage < mTotalPage-1)
					{
						beforeMoveAppItem();
						mCurPage++;
						mFocusIndex -= 5;
						freshCurPageAppList();
						setAppItemStatus(mAppItemView[mFocusIndex], mFocusView, mStatus);
						afterMoveAppItem();
					}
					else if (mCurPage == mTotalPage-1)
					{
						int maxIndex = (mAppCount-1)%mAppSize;
						if (maxIndex == 0)  //不能移动到"添加应用"这个位置
						{
							return 0;
						}
						
						beforeMoveAppItem();
						mCurPage++;
						mFocusIndex -= 5;
						if (mFocusIndex > maxIndex-1)
						{
							mFocusIndex = maxIndex-1;
						}
						freshCurPageAppList();
						setAppItemStatus(mAppItemView[mFocusIndex], mFocusView, mStatus);
						afterMoveAppItem();
					}
				}
			}
			else
			{
				if (mCurPage < mTotalPage)
				{
					beforeMoveAppItem();
					mFocusIndex += 1;
					setAppItemStatus(mAppItemView[mFocusIndex], mFocusView, mStatus);
					afterMoveAppItem();
				}
				else
				{
					int maxIndex = (mAppCount-1)%mAppSize;
					if (mFocusIndex < maxIndex-1)  //不能移动到"添加应用"这个位置
					{
						beforeMoveAppItem();
						mFocusIndex += 1;
						setAppItemStatus(mAppItemView[mFocusIndex], mFocusView, mStatus);
						afterMoveAppItem();
					}
				}
			}
			break;
		}
		default:
			break;
		}
		return 0;
	}
	
	/**
	 * 
	 * @Title: doKeyBack
	 * @Description: 响应返回键
	 * @return
	 * @return: int
	 */
	private int doKeyBack()
	{
		int ret = 0; 
		switch (mStatus) 
		{
		case APPITEM_STATUS_DEFAULT:
		{
			ret = 0;
			break;
		}
		case APPITEM_STATUS_UNINSTALL:
		{
			ret = 0;
			break;
		}
		case APPITEM_STATUS_MOVE:
		{
			//取消移动
			mStatus = APPITEM_STATUS_DEFAULT;
			mFocusIndex = mSrcIndex;
			if (mCurPage != mSrcPage)
			{
				mCurPage = mSrcPage;
				freshCurPageAppList();
			}
			mAppItemView[mFocusIndex].setVisibility(View.VISIBLE);
			setAppItemStatus(mAppItemView[mFocusIndex], mFocusView, mStatus);
			//还原初始值
			mSrcIndex = -1;
			mSrcPage = -1;
			ret = 0;
			break;
		}
		default:
			break;
		}
		return ret;
	}
	
	/**
	 * 
	 * @Title: doKeyMenu
	 * @Description: 响应菜单键
	 * @return: int
	 */
	private int doKeyMenu()
	{
		switch (mStatus) 
		{
		case APPITEM_STATUS_DEFAULT:
		{
			if(!mMenuDialog.isShowing())
			{
				int curIndex = (mCurPage-1)*mAppSize + mFocusIndex;
				if ((curIndex < 0) || (curIndex == mAppCount-1))   //没有已安装的应用(只有一个"添加应用"的入口)
				{
					return 0;
				}
				//有2个内置应用+一个"添加应用"的入口, mAppCount大于2
				AppItem appItem = mAppItems.get(curIndex);
				String pkgName = appItem.getPkgName();
				int actionType = 0;
				if (curIndex == 0)  
				{
					//第一个应用: 不能置顶
					actionType = MenuDialog.ACTION_TYPE_UNTOP;
					
					if (pkgName.endsWith("com.google.android.gms") || pkgName.endsWith("com.android.vending"))
					{
						//第一个应用是内置应用: 不能置顶 + 不能卸载
						actionType = MenuDialog.ACTION_TYPE_UNTOP_DISUNINSTALL;
					}
				}
				else if (curIndex > 0 && curIndex < mAppCount-2)
				{
					//第一个和最后一个之间的应用: 默认能移动 + 置顶 + 置底 + 卸载
					actionType = MenuDialog.ACTION_TYPE_DEFAULT;
					
					if (pkgName.endsWith("com.google.android.gms") || pkgName.endsWith("com.android.vending"))
					{
						//第一个和最后一个之间的应用是内置应用: 不能卸载
						actionType = MenuDialog.ACTION_TYPE_DEFAULT_DISUNINSTALL;
					}
				}
				else if (curIndex == mAppCount-2)  
				{
					//最后一个应用: 不能置底
					actionType = MenuDialog.ACTION_TYPE_UNBOTTOM;
					
					if (pkgName.endsWith("com.google.android.gms") || pkgName.endsWith("com.android.vending"))
					{
						//最后一个应用是内置应用: 不能置底 + 不能卸载
						actionType = MenuDialog.ACTION_TYPE_UNBOTTOM_DISUNINSTALL;
					}
				}
				mMenuDialog.show();
				mMenuDialog.setDialog(appItem.getIconBitmap().mutate(), appItem.getAppName(), actionType);
			}
			break;
		}
		default:
			break;
		}
		return 0;
	}
	
	/**
	 * 
	 * @Title: afterMoveAppItem
	 * @Description: 移动AppItem前
	 * @return: void
	 */
	private void afterMoveAppItem()
	{
		if (mStatus == APPITEM_STATUS_MOVE)
		{
			if (mSrcPage == mCurPage)
			{
				if (mSrcIndex == mFocusIndex)
				{
					mAppItemView[mFocusIndex].setVisibility(View.VISIBLE); //显示该项
				}
				else
				{
					mAppItemView[mSrcIndex].setVisibility(View.INVISIBLE); //隐藏该项
				}
			}
		}
	}
	
	/**
	 * 
	 * @Title: beforeMoveAppItem
	 * @Description: 移动AppItem前
	 * @return: void
	 */
	private void beforeMoveAppItem()
	{
		if (mStatus == APPITEM_STATUS_MOVE)
		{
			if (mSrcPage == mCurPage && mSrcIndex == mFocusIndex)
			{
				mAppItemView[mFocusIndex].setVisibility(View.INVISIBLE);
			}
		}
	}
	
	@Override
	public void onHiddenChanged(boolean hidden) 
	{
		if(!hidden)
		{
		}
		super.onHiddenChanged(hidden);
	}
	
	@Override
	public void onDestroyView() 
	{
		super.onDestroyView();
	}
	
	@Override
	public void onDestroy() 
	{
		super.onDestroy();
		unregisterIntentReceivers();
	}

	@Override
	public void onDetach() 
	{
		super.onDetach();
	}

	@Override
	public void initFragmentFocus()
	{
		mStatus = APPITEM_STATUS_DEFAULT;
		mFocusIndex = 0;
		setAppItemStatus(mAppItemView[mFocusIndex], mFocusView, mStatus);
	}
	
	@Override
	public void resetFragment() 
	{
		mStatus = APPITEM_STATUS_DEFAULT;
		mFocusIndex = 0;
		mCurPage = 1;
		freshCurPageAppList();
	}

	@Override
	public void OnAppClick(View v) 
	{
		switch (v.getId())
		{
		case R.id.mainapp_item0:
		case R.id.mainapp_item1:
		case R.id.mainapp_item2:
		case R.id.mainapp_item3:
		case R.id.mainapp_item4:
		case R.id.mainapp_item5:
		case R.id.mainapp_item6:
		case R.id.mainapp_item7:
		case R.id.mainapp_item8:
		case R.id.mainapp_item9:
		case R.id.mainapp_item10:
		case R.id.mainapp_item11:
		case R.id.mainapp_item12:
		case R.id.mainapp_item13:
		case R.id.mainapp_item14:
		case R.id.mainapp_item15:
		case R.id.mainapp_item16:
		case R.id.mainapp_item17:
		{
			if (mStatus == APPITEM_STATUS_DEFAULT)
			{
				if(((MainActivity) mActivity).checkAuthRes())  //鉴权成功, 则可以点击
				{
					if (mCurPage < mTotalPage)
					{
						startApk(mFocusIndex);
					}
					else if (mCurPage == mTotalPage)
					{
						int maxIndex = (mAppCount-1)%mAppSize;
						
						if (mFocusIndex < maxIndex)
						{
							startApk(mFocusIndex);
						}
						else
						{
							mFocusView.setVisibility(View.INVISIBLE);
							//点击"添加应用"后, 切换到应用商城
							Intent intent = new Intent();
							intent.setAction(MainActivity.ACTION_SWITCH_TO_APPSTORE);
							mActivity.sendBroadcast(intent);
						}
					}
				}
			}
			else if (mStatus == APPITEM_STATUS_MOVE)
			{
				//移动完成
				mStatus = APPITEM_STATUS_DEFAULT;
				int src = (mSrcPage-1)*mAppSize + mSrcIndex;
				int dst = (mCurPage-1)*mAppSize + mFocusIndex;
				if (src == dst)
				{
					//没移动
					mFocusIndex = mSrcIndex;
					setAppItemStatus(mAppItemView[mFocusIndex], mFocusView, mStatus);
				}
				else
				{
					//移动
					AppItem appItem = mAppItems.get(src);
					mAppItems.remove(src);
					mAppItems.add(dst, appItem);
					InstalledTable.moveAppPostion(src, dst);
					//刷新页面
					freshCurPageAppList();
					setAppItemStatus(mAppItemView[mFocusIndex], mFocusView, mStatus);
				}
				//还原初始值
				mSrcIndex = -1;
				mSrcPage = -1;
			}
			break;
		}
		default:
			break;
		}
	}
	
	@Override
	public void onFocusChange(View v, boolean hasFocus) 
	{
		v.setSelected(hasFocus);
	}
	
	/**
	 * 
	 * @Title: startApk
	 * @Description: 启动应用
	 * @param curIndex
	 * @return: void
	 */
	private void startApk(int curIndex)
	{
		int index = (mCurPage-1)*mAppSize + curIndex;
		AppItem appItem = mAppItems.get(index);
		String pkgName = appItem.getPkgName();
		String appCode = appItem.getAppCode();
		String appVersion = appItem.getVersion();
		try 
		{
			Intent intent = null;
			PackageManager packageManager = mActivity.getPackageManager();
			intent = packageManager.getLaunchIntentForPackage(pkgName);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			mActivity.startActivity(intent);
			//启动应用BI
			Bi.sendBiMsg(Bi.BICT_FIRMWARE, Bi.BICC_APP_START, appCode, appVersion, StbInfo.getCurLang(mActivity), "", "0");
		} 
		catch (Exception e) 
		{
			//启动应用BI
			Bi.sendBiMsg(Bi.BICT_FIRMWARE, Bi.BICC_APP_START, appCode, appVersion, StbInfo.getCurLang(mActivity), "", "1");
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @Title: getIndexByPkgName
	 * @Description: 通过包名获取索引
	 * @param pkgName
	 * @return
	 * @return: int
	 */
	private int getIndexByPkgName(String pkgName)
	{
		for (int i=0; i<mAppCount; i++)
		{
			if (pkgName.equals(mAppItems.get(i).getPkgName()))
			{
				return i;
			}
		}
		return -1;
	}
	
	/**
	 * 
	 * @Title: registerIntentReceivers
	 * @Description: 注册广播
	 * @return: void
	 */
	private void registerIntentReceivers() 
	{
		IntentFilter filter = new IntentFilter();
		filter.addAction(ACTION_DELETE_START);    //卸载应用开始
		filter.addAction(ACTION_DELETE_PROGRESS); //卸载应用进度
		filter.addAction(ACTION_DELETE_COMPLETE); //卸载应用结束
		mActivity.registerReceiver(mReceiver, filter);
		
		IntentFilter filter1 = new IntentFilter();
		filter1.addAction(Intent.ACTION_PACKAGE_ADDED);   //应用在商城安装成功之后, "我的应用"要刷新
		filter1.addAction(Intent.ACTION_PACKAGE_REMOVED); //应用在"我的应用"卸载完成之后, "我的应用"也要刷新
		filter1.addDataScheme("package");
		mActivity.registerReceiver(mReceiver, filter1);
		
		IntentFilter filter2 = new IntentFilter();
		filter2.addAction(DOWNLOAD_ICON_ACTION); //下载应用icon完成后, 刷新"我的应用"里面对应的应用的图标
		filter2.addAction(Intent.ACTION_EXTERNAL_APPLICATIONS_AVAILABLE);
		filter2.addAction(Intent.ACTION_EXTERNAL_APPLICATIONS_UNAVAILABLE);
		mActivity.registerReceiver(mReceiver, filter2);
	}
	
	/**
	 * 
	 * @Title: unregisterIntentReceivers
	 * @Description: 注销广播
	 * @return: void
	 */
	private void unregisterIntentReceivers() 
	{
		if (mReceiver != null) 
		{
			mActivity.unregisterReceiver(mReceiver);
			mReceiver = null;
		}
	}
	
	/*卸载开始*/
	public static final String ACTION_DELETE_START = "com.sz.ead.framework.packageinstaller.InstallerInter.DELETE_START";
	/*卸载完成，判断返回值*/
	public static final String ACTION_DELETE_COMPLETE = "com.sz.ead.framework.packageinstaller.InstallerInter.DELETE_COMPLETE";
	/*卸载进度*/
	public static final String ACTION_DELETE_PROGRESS = "com.sz.ead.framework.packageinstaller.InstallerInter.DELETE_PROGRESS";
	
	//下载应用icon的action和Key
	public static final String DOWNLOAD_ICON_ACTION = "com.sz.ead.framework.appstore.downloader.DownConstants.DownIcon_Complete";
	public static final String DOWNLOAD_ICON_PKG_KEY = "down_icon_pkg_key";
	
	private MainAppReceiver mReceiver = new MainAppReceiver();
	private class MainAppReceiver extends BroadcastReceiver 
	{
		public void onReceive(Context context, Intent intent) 
		{
			LogUtil.d(LogUtil.TAG, " FragmentMainApp onReceive Action: " + intent.getAction());
			if (intent.getAction().equals(Intent.ACTION_PACKAGE_REMOVED)) 
			{
				Uri data = intent.getData();
				String pkgName = data.getEncodedSchemeSpecificPart();
				Message msg = new Message();	
				msg.what= MSG_UNINSTALL_APK_SUC;
				msg.obj = (Object)pkgName;	
				handler.sendMessage(msg);
			}
			else if(intent.getAction().equals(Intent.ACTION_PACKAGE_ADDED))
			{
				Uri data = intent.getData();
				String pkgName = data.getEncodedSchemeSpecificPart();
				Message msg = new Message();
				msg.what= MSG_INSTALL_APK_SUC;
				msg.obj = (Object)pkgName;
				handler.sendMessage(msg);
			}
			else if(intent.getAction().equals(ACTION_DELETE_START))
			{
				//显示卸载
				mUninstall.setVisibility(View.VISIBLE);
				setAppItemStatus(mAppItemView[mFocusIndex], mUninstall, mStatus);
			}
			else if(intent.getAction().equals(ACTION_DELETE_PROGRESS))
			{
				if (mAppItemView[mFocusIndex] != null)
				{
					int max = intent.getIntExtra("total_progress", 100);
					int progress = intent.getIntExtra("current_progress", 0);
					
					mBar.setMax(max);
					mBar.setProgress(progress);
					mPercent.setText(progress + "%");
				}
			}
			else if(intent.getAction().equals(ACTION_DELETE_COMPLETE))
			{	
				if (mAppItemView[mFocusIndex] != null)
				{
					int ret_code = intent.getIntExtra("ret_code", -1);
					if(ret_code != 1)
					{
						//卸载失败
						try 
						{
						} 
						catch (Error e) 
						{
						}
					}
					mBar.setMax(100);
					mBar.setProgress(100);
					mPercent.setText(100 + "%");
				}
			}
			else if(intent.getAction().equals(DOWNLOAD_ICON_ACTION))
			{
				String pkgName = intent.getStringExtra(DOWNLOAD_ICON_PKG_KEY);
				if(!TextUtils.isEmpty(pkgName))
				{
					int index = getIndexByPkgName(pkgName);
					if (index >= 0)
					{
						int curIndex = index%mAppSize;
						int page = index/mAppSize;
						if (page == mCurPage) //如果是当前页, 则马上更新
						{
							AppItem appItem = InstalledTable.queryAppInfo(pkgName);
							if (mAppItemView[curIndex] != null && appItem != null)
							{
								mAppItemView[curIndex].setAppItemSource(AppItemView.APPITEM_SOURCE_MAINAPP);
								mAppItemView[curIndex].setAppItem(appItem);
								mAppItemView[curIndex].updateAppItem();
							}
						}
					}
				}	
			}
			else if(intent.getAction().equals(Intent.ACTION_EXTERNAL_APPLICATIONS_AVAILABLE))
			{
				String skipPackages[] = intent.getStringArrayExtra(Intent.EXTRA_CHANGED_PACKAGE_LIST);
				for (String s : skipPackages) 
				{
					Message msg = new Message();
					msg.what= MSG_EXTERNAL_APP_AVAILABLE;
					msg.obj = (Object)s;
					handler.sendMessage(msg);
				}
			}
			else if(intent.getAction().equals(Intent.ACTION_EXTERNAL_APPLICATIONS_UNAVAILABLE))
			{
				String skipPackages[] = intent.getStringArrayExtra(Intent.EXTRA_CHANGED_PACKAGE_LIST);
				for (String s : skipPackages) 
				{
					Message msg = new Message();	
					msg.what= MSG_EXTERNAL_APP_UNAVAILABLE;
					msg.obj = (Object)s;
					handler.sendMessage(msg);
				}
			}
		}
	}
	
	private final int MSG_INSTALL_APK_SUC = 0x100;
	private final int MSG_UNINSTALL_APK_SUC = 0x200;
	private final int MSG_IS_THIRD_PARTY_UNINSTALL_APK = 0x300;
	private final int MSG_EXTERNAL_APP_AVAILABLE = 0x400;
	private final int MSG_EXTERNAL_APP_UNAVAILABLE = 0x500;
	private final int MSG_DELAY_REFRESH_INSTALL_APK_SUC = 0x600;
	
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler()
	{
		public void handleMessage(Message msg) 
		{
			switch (msg.what) 
			{
			case MSG_UNINSTALL_APK_SUC:  //卸载成功 (1.主动在桌面卸载应用; 2. 应用升级时, 先卸载后安装)
			{
				String pkgName = (String)msg.obj;
				if (mStatus == APPITEM_STATUS_UNINSTALL)
				{
					//主动在桌面卸载应用; 刷新当前页
					int index = (mCurPage-1)*mAppSize + mFocusIndex;
					mAppItems.remove(index);
					mAppCount = mAppItems.size();
					int totalPage = (mAppCount-1)/mAppSize + 1;
					if (totalPage < mTotalPage)
					{
						mTotalPage = totalPage;
					}
					freshCurPageAppList();
					mStatus = APPITEM_STATUS_DEFAULT;
					setAppItemStatus(mAppItemView[mFocusIndex], mFocusView, mStatus);
					//从已安装表中删除该应用信息
					InstalledTable.deleteApp(pkgName);
				}
				else
				{
					//应用升级时, 需要先卸载应用
					mUpdating = true;
					mInstallSuc = false;
	
					//也有可能是第三方应用上卸载应用
					Message message = new Message();
					message.what= MSG_IS_THIRD_PARTY_UNINSTALL_APK;
					message.obj = (Object)pkgName;
					handler.sendMessageDelayed(message, 1000);
				}
				break;
			}
			case MSG_INSTALL_APK_SUC:
			{
				mInstallSuc = true;
				LogUtil.d(LogUtil.TAG, " mUpdating: " + mUpdating);
				String pkgName = (String)msg.obj;
				if (mUpdating)
				{
					mUpdating = false;
					//应用升级时, 先卸载应用, 再安装应用; 该应用保持原先的位置
					//先取到该应用的信息
					AppItem appItem = new AppItem();
					appItem = ApkUtil.getAppInfoByPackageName(mActivity, pkgName);
					if (appItem != null)
					{
						//1. 应用自升级; 2. 第三方应用上升级(类似商城)
						int index = getIndexByPkgName(pkgName);
						//int current = (mCurPage-1)*mAppSize + mFocusIndex;
						if(index >=0){
							mAppItems.remove(index);
							mAppItems.add(index, appItem);
							freshCurPageAppList();
						}
						//只需要更新该appItem的内容即可
						//InstalledTable.updateAppItem(appItem);
					}
				}
				else
				{
					LogUtil.d(LogUtil.TAG, " MSG_INSTALL_APK_SUC mMoving: " + mMoving);
					if (mMoving)
					{
						Message message = new Message();
						message.what= MSG_DELAY_REFRESH_INSTALL_APK_SUC;
						message.obj = (Object)pkgName;
						handler.sendMessageDelayed(message, 200);
					}
					else
					{
						//主动安装应用(来源应用商城或其它途径)
						AppItem appItem = new AppItem();
						appItem = ApkUtil.getAppInfoByPackageName(mActivity, pkgName);  //在系统中查找
						if (appItem != null)
						{
							//将该应用信息添加到已安装表中
							InstalledTable.insertApp(mActivity, pkgName);
							AppItem temp = InstalledTable.queryAppInfo(pkgName);  //得到下载的应用图标
							if (temp != null)
							{
								appItem = temp;
							}
							//添加到应用列表的末尾中
							mAppItems.add(mAppCount-1, appItem);
							mAppCount = mAppItems.size();
							int totalPage = (mAppCount-1)/mAppSize + 1;
							//如果光标在最后一页, 则刷新当前页
							if (mCurPage == mTotalPage)
							{
								freshCurPageAppList();
								//如果此时处于移动状态中, 则要保留移动状态
								afterMoveAppItem();
							}
							
							if (totalPage > mTotalPage)
							{
								mTotalPage = totalPage;
							}
							
							//应用安装结果BI
							String appCode = appItem.getAppCode();
							String appVersion = appItem.getVersion();
							String storeCode = "";
							String storeName = "";
							Bi.sendBiMsg(Bi.BICT_FIRMWARE, Bi.BICC_INSTALL_RESULT , appCode, appVersion, StbInfo.getCurLang(mActivity), "0", storeCode, storeName);
						}
					}
				}
				break;
			}
			case MSG_IS_THIRD_PARTY_UNINSTALL_APK:
			{
				//第三方应用上卸载应用
				LogUtil.d(LogUtil.TAG, " mInstallSuc: " + mInstallSuc);
				if (!mInstallSuc)
				{
					String pkgName = (String)msg.obj;
					int index = getIndexByPkgName(pkgName);
					if (index >= 0)
					{
						mAppItems.remove(index);
						mAppCount = mAppItems.size();
						int totalPage = (mAppCount-1)/mAppSize + 1;
						if (totalPage < mTotalPage)
						{
							mTotalPage = totalPage;
						}
						freshCurPageAppList();
						mStatus = APPITEM_STATUS_DEFAULT;
						setAppItemStatus(mAppItemView[mFocusIndex], mFocusView, mStatus);
						
						//从已安装表中删除该应用信息
						InstalledTable.deleteApp(pkgName);
					}
				}
				else
				{
					mUpdating = false;
				}
				break;
			}
			case MSG_EXTERNAL_APP_AVAILABLE:
			{
				String pkgName = (String)msg.obj;
				AppItem appItem = new AppItem();
				appItem = ApkUtil.getAppInfoByPackageName(mActivity, pkgName);  //在系统中查找
				if (appItem != null)
				{
					InstalledTable.insertApp(mActivity, pkgName);
					AppItem temp = InstalledTable.queryAppInfo(pkgName);  //得到下载的应用图标
					if (temp != null)
					{
						appItem = temp;
					}
					//添加到应用列表的末尾中
					mAppItems.add(mAppCount-1, appItem);
					mAppCount = mAppItems.size();
					int totalPage = (mAppCount-1)/mAppSize + 1;
					//如果光标在最后一页, 则刷新当前页
					if (mCurPage == mTotalPage)
					{
						freshCurPageAppList();
						//如果此时处于移动状态中, 则要保留移动状态
						afterMoveAppItem();
					}
					
					if (totalPage > mTotalPage)
					{
						mTotalPage = totalPage;
					}
				}
				break;
			}
			case MSG_EXTERNAL_APP_UNAVAILABLE:
			{
				String pkgName = (String)msg.obj;
				int index = getIndexByPkgName(pkgName);
				if (index >= 0)
				{
					mAppItems.remove(index);
					mAppCount = mAppItems.size();
					int totalPage = (mAppCount-1)/mAppSize + 1;
					if (totalPage < mTotalPage)
					{
						mTotalPage = totalPage;
					}
					freshCurPageAppList();
					//如果此时处于移动状态中, 则要保留移动状态
					afterMoveAppItem();
					//从已安装表中删除该应用信息
					InstalledTable.deleteApp(pkgName);
				}
				break;
			}
			case MSG_DELAY_REFRESH_INSTALL_APK_SUC:
			{
				String pkgName = (String)msg.obj;
				LogUtil.d(LogUtil.TAG, " MSG_DELAY_REFRESH_INSTALL_APK_SUC mMoving: " + mMoving);
				if (mMoving)
				{
					Message message = new Message();
					message.what= MSG_DELAY_REFRESH_INSTALL_APK_SUC;
					message.obj = (Object)pkgName;
					handler.sendMessageDelayed(message, 200);
				}
				else
				{
					//主动安装应用(来源应用商城或其它途径)
					AppItem appItem = new AppItem();
					appItem = ApkUtil.getAppInfoByPackageName(mActivity, pkgName);  //在系统中查找
					if (appItem != null)
					{
						//将该应用信息添加到已安装表中
						InstalledTable.insertApp(mActivity, pkgName);
						AppItem temp = InstalledTable.queryAppInfo(pkgName);  //得到下载的应用图标
						if (temp != null)
						{
							appItem = temp;
						}
						//添加到应用列表的末尾中
						mAppItems.add(mAppCount-1, appItem);
						mAppCount = mAppItems.size();
						int totalPage = (mAppCount-1)/mAppSize + 1;
						//如果光标在最后一页, 则刷新当前页
						if (mCurPage == mTotalPage)
						{
							freshCurPageAppList();
							//如果此时处于移动状态中, 则要保留移动状态
							afterMoveAppItem();
						}
						
						if (totalPage > mTotalPage)
						{
							mTotalPage = totalPage;
						}
						
						//应用安装结果BI
						String appCode = appItem.getAppCode();
						String appVersion = appItem.getVersion();
						String storeCode = "";
						String storeName = "";
						Bi.sendBiMsg(Bi.BICT_FIRMWARE, Bi.BICC_INSTALL_RESULT , appCode, appVersion, StbInfo.getCurLang(mActivity), "0", storeCode, storeName);
					}
				}
				break;
			}
			default:
				break;
			}
		}
	};
	
	/**
	 * 
	 * @Title: setAppItemStatus
	 * @Description: 设置AppItem的状态(默认 卸载 移动)
	 * @param view
	 * @param focus
	 * @param status
	 * @return: void
	 */
	private void setAppItemStatus(View view, View focus, int status)
	{
		int width = view.getWidth();
		int height = view.getHeight();
		int top = view.getTop();
		int left = view.getLeft();
		
		RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) focus.getLayoutParams();
		params.leftMargin = left;
		params.topMargin = top;
		params.width = width;
		params.height = height;
		
		switch (status) 
		{
		case APPITEM_STATUS_DEFAULT:
		{
			focus.setBackground(mActivity.getResources().getDrawable(R.drawable.mainapp_appitem_focus));
			mUninstall.setVisibility(View.INVISIBLE);
			break;
		}
		case APPITEM_STATUS_UNINSTALL:
		{
			mFocusView.setVisibility(View.INVISIBLE);
			break;
		}	
		case APPITEM_STATUS_MOVE:
		{
			focus.setBackground(mActivity.getResources().getDrawable(R.drawable.mainapp_appitem_move));
			mUninstall.setVisibility(View.INVISIBLE);
			break;
		}	
		default:
			break;
		}
				                   
		focus.setLayoutParams(params);
		focus.setVisibility(View.VISIBLE);
		focus.bringToFront();
		view.requestFocus();
	}
	
	public void doForHomeKey()
	{
		if(mMenuDialog != null)
		{
			if(mMenuDialog.isShowing())
			{
				mMenuDialog.dismiss();
			}
		}
		if(mPromptDialog != null)
		{
			if(mPromptDialog.isShowing())
			{
				mPromptDialog.dismiss();
			}
		}
		
		mFocusView.setVisibility(View.INVISIBLE);
		mStatus = APPITEM_STATUS_DEFAULT;
		mFocusIndex = 0;
		mCurPage = 1;
		freshCurPageAppList();
	}
}
