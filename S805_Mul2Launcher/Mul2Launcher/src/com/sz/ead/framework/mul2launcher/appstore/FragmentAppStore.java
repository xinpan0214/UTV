/**
 * Copyright © 2015GreatVision. All rights reserved.
 *
 * @Title: FragmentAppStore.java
 * @Prject: Mul2Launcher
 * @Package: com.sz.ead.framework.mul2launcher.appstore
 * @Description: 应用商城
 * @author: zhaoqy
 * @date: 2015-4-22 上午10:50:48
 */
package com.sz.ead.framework.mul2launcher.appstore;

import java.util.ArrayList;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.sz.ead.framework.mul2launcher.FragmentBase;
import com.sz.ead.framework.mul2launcher.MainActivity;
import com.sz.ead.framework.mul2launcher.R;
import com.sz.ead.framework.mul2launcher.dataprovider.dataitem.AppItem;
import com.sz.ead.framework.mul2launcher.db.DownloadTable;
import com.sz.ead.framework.mul2launcher.mainapp.AppItemView;
import com.sz.ead.framework.mul2launcher.mainapp.AppItemView.OnAppClickListener;
import com.sz.ead.framework.mul2launcher.mainapp.PageIndicator;
import com.sz.ead.framework.mul2launcher.util.LogUtil;

public class FragmentAppStore extends FragmentBase implements OnAppClickListener, OnFocusChangeListener
{
	private Activity mActivity;
	private PageIndicator mPageIndicator;
	private AppItemView mAppItemView[] = new AppItemView[18];
	private ImageView mNoapp;
	private ImageView mFocusView;
	private ArrayList<AppItem> mAppItems = new ArrayList<AppItem>();
	private int mCurPage = 0;
	private int mTotalPage = 0;
	private int mAppCount = 0;
	private int mAppSize = 18;
	private int mFocusIndex = 0;
	
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
		View view = inflater.inflate(R.layout.appstore_fragment, container, false);
		mAppItemView[0] = (AppItemView) view.findViewById(R.id.appstore_item0);
		mAppItemView[1] = (AppItemView) view.findViewById(R.id.appstore_item1);
		mAppItemView[2] = (AppItemView) view.findViewById(R.id.appstore_item2);
		mAppItemView[3] = (AppItemView) view.findViewById(R.id.appstore_item3);
		mAppItemView[4] = (AppItemView) view.findViewById(R.id.appstore_item4);
		mAppItemView[5] = (AppItemView) view.findViewById(R.id.appstore_item5);
		mAppItemView[6] = (AppItemView) view.findViewById(R.id.appstore_item6);
		mAppItemView[7] = (AppItemView) view.findViewById(R.id.appstore_item7);
		mAppItemView[8] = (AppItemView) view.findViewById(R.id.appstore_item8);
		mAppItemView[9] = (AppItemView) view.findViewById(R.id.appstore_item9);
		mAppItemView[10] = (AppItemView) view.findViewById(R.id.appstore_item10);
		mAppItemView[11] = (AppItemView) view.findViewById(R.id.appstore_item11);
		mAppItemView[12] = (AppItemView) view.findViewById(R.id.appstore_item12);
		mAppItemView[13] = (AppItemView) view.findViewById(R.id.appstore_item13);
		mAppItemView[14] = (AppItemView) view.findViewById(R.id.appstore_item14);
		mAppItemView[15] = (AppItemView) view.findViewById(R.id.appstore_item15);
		mAppItemView[16] = (AppItemView) view.findViewById(R.id.appstore_item16);
		mAppItemView[17] = (AppItemView) view.findViewById(R.id.appstore_item17);
		mNoapp = (ImageView) view.findViewById(R.id.appstore_noapp);
		mFocusView = (ImageView) view.findViewById(R.id.appstore_focus);
		mPageIndicator = (PageIndicator) view.findViewById(R.id.appstore_pageindicator);
		
		init();
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
		mAppItems.clear();
		for (int i=0; i<mAppSize; i++)
		{
			mAppItemView[i].setVisibility(View.INVISIBLE);
			mAppItemView[i].setOnAppClick(this);
			mAppItemView[i].setOnFocusChangeListener(this);
			mAppItemView[i].setAppItemSource(AppItemView.APPITEM_SOURCE_APPSTORE);
		}
	}
	
	/**
	 * 
	 * @Title: getAppList
	 * @Description: 获取appList
	 * @return: void
	 */
	private void getAppList()
	{
		mAppItems = DownloadTable.queryAppListInfo();
		if (mAppItems != null)
		{
			mAppCount = mAppItems.size();
			LogUtil.d(LogUtil.TAG, " mAppCount: " + mAppCount);
			if (mAppCount > 0)
			{
				mCurPage = 1;
				mTotalPage = (mAppCount-1)/mAppSize + 1;
			}
		}
	}
	
	/**
	 * 
	 * @Title: reGetAppList
	 * @Description: 重新获取appList
	 * @return: void
	 */
	public void reGetAppList()
	{
		ArrayList<AppItem> temp = new ArrayList<AppItem>();
		temp = DownloadTable.queryAppListInfo();
		if (temp != null)
		{
			mAppItems.clear();
			for (int i=0; i<temp.size(); i++)
			{
				mAppItems.add(temp.get(i));
			}
			
			mAppCount = mAppItems.size();
			if (mAppCount > 0)
			{
				mCurPage = 1;
				mTotalPage = (mAppCount-1)/mAppSize + 1;
			}
		}
	}
	
	/**
	 * 
	 * @Title: freshAppList
	 * @Description: 刷新页面
	 * @return: void
	 */
	private void freshAppList()
	{
		for (int i=0; i<mAppSize; i++)
		{
			if(i+(mCurPage-1)*mAppSize < mAppCount)
			{
				AppItem appItem = mAppItems.get(i+(mCurPage-1)*mAppSize);
				mAppItemView[i].setAppItemSource(AppItemView.APPITEM_SOURCE_APPSTORE);
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
		if (mFocusIndex >= 0 && mFocusIndex <= 5)
		{
			mFocusView.setVisibility(View.INVISIBLE);  //光标返回到导航栏上
			return 2;
		}
		else
		{
			mFocusIndex -= 6;
			setAppItemFocus(mAppItemView[mFocusIndex]);
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
				setAppItemFocus(mAppItemView[mFocusIndex]);
			}
		}
		else
		{
			if (mFocusIndex >= 0 && mFocusIndex <= 11)
			{
				mFocusIndex += 6;
				setAppItemFocus(mAppItemView[mFocusIndex]);
			}
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
		if (mCurPage > 1)
		{
			mCurPage--;
			freshAppList();
			mFocusIndex = 0;
			setAppItemFocus(mAppItemView[mFocusIndex]);
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
		if (mCurPage < mTotalPage)
		{
			mCurPage++;
			freshAppList();
			mFocusIndex = 0;
			setAppItemFocus(mAppItemView[mFocusIndex]);
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
		if (mFocusIndex == 0 || mFocusIndex == 6 || mFocusIndex == 12) //左边界
		{
			if (mTotalPage == 1) //只有1页
			{
				mFocusIndex -= 1;
				if (mFocusIndex < 0)
				{
					mFocusIndex = mAppCount-1;
				}
				setAppItemFocus(mAppItemView[mFocusIndex]);
			}
			else  //至少有2页
			{
				if (mCurPage > 1)
				{
					mCurPage--;
					mFocusIndex += 5;
					freshAppList();
					setAppItemFocus(mAppItemView[mFocusIndex]);
				}
			}
		}
		else
		{
			mFocusIndex -= 1;
			setAppItemFocus(mAppItemView[mFocusIndex]);
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
		if (mFocusIndex == 5 || mFocusIndex == 11 || mFocusIndex == 17)  //右边界
		{
			if (mTotalPage == 1)  //只有1页
			{
				mFocusIndex += 1;
				if (mFocusIndex > mAppCount-1)
				{
					mFocusIndex = 0;
				}
				setAppItemFocus(mAppItemView[mFocusIndex]);
			}
			else  //至少有2页
			{
				if (mCurPage < mTotalPage-1)
				{
					mCurPage++;
					mFocusIndex -= 5;
					freshAppList();
					setAppItemFocus(mAppItemView[mFocusIndex]);
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
					freshAppList();
					setAppItemFocus(mAppItemView[mFocusIndex]);
				}
			}
		}
		else
		{
			if (mCurPage < mTotalPage)
			{
				mFocusIndex += 1;
				setAppItemFocus(mAppItemView[mFocusIndex]);
			}
			else
			{
				int maxIndex = (mAppCount-1)%mAppSize;
				if (mFocusIndex < maxIndex)
				{
					mFocusIndex += 1;
					setAppItemFocus(mAppItemView[mFocusIndex]);
				}
				else
				{
					if (mTotalPage == 1)
					{
						mFocusIndex = 0;
						setAppItemFocus(mAppItemView[mFocusIndex]);
					}
				}
			}
		}
		return 0;
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
		if (mAppCount > 0 && mNoapp.getVisibility() == View.INVISIBLE)
		{
			mFocusIndex = 0;
			setAppItemFocus(mAppItemView[mFocusIndex]);
		}
	}
	
	public int getAppCount()
	{
		return mAppCount;
	}

	@Override
	public void resetFragment() 
	{
		if (mAppCount > 0)
		{
			mNoapp.setVisibility(View.INVISIBLE);
			mFocusIndex = 0;
			mCurPage = 1;
			freshAppList();
		}
		else
		{
			getAppList();
			if (mAppCount > 0)
			{
				mNoapp.setVisibility(View.INVISIBLE);
				mCurPage = 1;
				freshAppList();
			}
			else
			{
				mNoapp.setVisibility(View.VISIBLE);
			}
		}
	}

	@Override
	public void OnAppClick(View v) 
	{
		switch (v.getId())
		{
		case R.id.appstore_item0:
		case R.id.appstore_item1:
		case R.id.appstore_item2:
		case R.id.appstore_item3:
		case R.id.appstore_item4:
		case R.id.appstore_item5:
		case R.id.appstore_item6:
		case R.id.appstore_item7:
		case R.id.appstore_item8:
		case R.id.appstore_item9:
		case R.id.appstore_item10:
		case R.id.appstore_item11:
		case R.id.appstore_item12:
		case R.id.appstore_item13:
		case R.id.appstore_item14:
		case R.id.appstore_item15:
		case R.id.appstore_item16:
		case R.id.appstore_item17:
		{
			if(((MainActivity) mActivity).checkAuthRes())  //鉴权成功, 则可以点击
			{
				AppItem appItem = mAppItems.get((mCurPage-1)*mAppSize + mFocusIndex);
				Intent intent = new Intent(mActivity, AppDetailsActivity.class);
				Bundle bundle = new Bundle();
				bundle.putParcelable(AppDetailsActivity.ToAppDetailKey, appItem);
				intent.putExtras(bundle);
				mActivity.startActivity(intent);
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
	 * @Title: setAppItemFocus
	 * @Description: 设置AppItem选中
	 * @param view
	 * @param focus
	 * @return: void
	 */
	private void setAppItemFocus(View view)
	{
		int width = view.getWidth();
		int height = view.getHeight();
		int top = view.getTop();
		int left = view.getLeft();
		
		RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mFocusView.getLayoutParams();
		params.leftMargin = left;
		params.topMargin = top;
		params.width = width;
		params.height = height;
				                   
		mFocusView.setLayoutParams(params);
		mFocusView.setVisibility(View.VISIBLE);
		mFocusView.bringToFront();
		view.requestFocus();
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
	
	public void doForHomeKey()
	{
		mFocusView.setVisibility(View.INVISIBLE);
	}
	
	/**
	 * 
	 * @Title: registerIntentReceivers
	 * @Description: 注册广播
	 * @return: void
	 */
	private void registerIntentReceivers() 
	{
		IntentFilter filter1 = new IntentFilter();
		filter1.addAction(Intent.ACTION_PACKAGE_ADDED);   //应用在商城安装成功之后, "我的应用"要刷新
		filter1.addAction(Intent.ACTION_PACKAGE_REMOVED); //应用在"我的应用"卸载完成之后, "我的应用"也要刷新
		filter1.addDataScheme("package");
		mActivity.registerReceiver(mReceiver, filter1);
		
		IntentFilter filter2 = new IntentFilter();
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
	
	private AppStoreReceiver mReceiver = new AppStoreReceiver();
	private class AppStoreReceiver extends BroadcastReceiver 
	{
		public void onReceive(Context context, Intent intent) 
		{
			LogUtil.d(LogUtil.TAG, " onReceive Action: " + intent.getAction());
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
	private final int MSG_EXTERNAL_APP_AVAILABLE = 0x300;
	private final int MSG_EXTERNAL_APP_UNAVAILABLE = 0x400;
	
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler()
	{
		public void handleMessage(Message msg) 
		{
			switch (msg.what) 
			{
			case MSG_UNINSTALL_APK_SUC:  
			{
				String pkgName = (String)msg.obj;
				int index = getIndexByPkgName(pkgName);
				if (index >= 0)
				{
					int tempPage = index/mAppSize + 1;
					if (tempPage == mCurPage)
					{
						int curIndex = index%mAppSize;
						AppItem appItem = mAppItems.get(index);
						mAppItemView[curIndex].setAppItemSource(AppItemView.APPITEM_SOURCE_APPSTORE);
						mAppItemView[curIndex].setAppItem(appItem);
					}
				}
				break;
			}
			case MSG_INSTALL_APK_SUC:
			{
				String pkgName = (String)msg.obj;
				int index = getIndexByPkgName(pkgName);
				if (index >= 0)
				{
					int tempPage = index/mAppSize + 1;
					if (tempPage == mCurPage)
					{
						int curIndex = index%mAppSize;
						AppItem appItem = mAppItems.get(index);
						mAppItemView[curIndex].setAppItemSource(AppItemView.APPITEM_SOURCE_APPSTORE);
						mAppItemView[curIndex].setAppItem(appItem);
					}
				}
				break;
			}
			case MSG_EXTERNAL_APP_AVAILABLE:
			{
				String pkgName = (String)msg.obj;
				int index = getIndexByPkgName(pkgName);
				if (index >= 0)
				{
					int tempPage = index/mAppSize + 1;
					if (tempPage == mCurPage)
					{
						int curIndex = index%mAppSize;
						AppItem appItem = mAppItems.get(index);
						mAppItemView[curIndex].setAppItemSource(AppItemView.APPITEM_SOURCE_APPSTORE);
						mAppItemView[curIndex].setAppItem(appItem);
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
					int tempPage = index/mAppSize + 1;
					if (tempPage == mCurPage)
					{
						int curIndex = index%mAppSize;
						AppItem appItem = mAppItems.get(index);
						mAppItemView[curIndex].setAppItemSource(AppItemView.APPITEM_SOURCE_APPSTORE);
						mAppItemView[curIndex].setAppItem(appItem);
					}
				}
				break;
			}
			default:
				break;
			}
		}
	};
}
