/**
 * Copyright © 2015GreatVision. All rights reserved.
 *
 * @Title: PageIndicator.java
 * @Prject: Mul2Launcher
 * @Package: com.sz.ead.framework.mul2launcher.mainapp
 * @Description: 页码指示器
 * @author: zhaoqy  
 * @date: 2015-4-23 下午4:35:43
 */
package com.sz.ead.framework.mul2launcher.mainapp;

import com.sz.ead.framework.mul2launcher.R;
import com.sz.ead.framework.mul2launcher.util.CommomUtil;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class PageIndicator extends RelativeLayout
{
	public static final int WIDTH = 1280;                  //页码指示器的总宽度
	public static final int TOP = 676;                     //页码指示器的高度, 以(0,0)为原点
	public static final int SPACE = 12;                    //间距
	public static final int IMAGE_WIDTH = 14;              //宽度
	public static final int IMAGE_HEIGHT = 14;             //高度
	public static final int MAX = 5;                       //最多显示个数
	private Context        mContext;                       //上下文
	private ImageView      mImages[] = new ImageView[MAX]; //图片表示页码
	private TextView       mNumber;                        //数字表示页码
	private int            mTotalPage = 0;                 //总页数
	private int            mInitX = 0;                     //第一张图片的x坐标
	private int            mImageX = 0;                    //每张图片的x坐标
	private int            mTop = TOP;                     //高度
	
	public PageIndicator(Context context, AttributeSet attrs) 
	{
		super(context, attrs);
		mContext = context;
		init();
	}
	
	private void init()
	{
		inflate(mContext, R.layout.mainapp_pageindicator, this);
		mImages[0] = (ImageView) findViewById(R.id.indicator0);
		mImages[1] = (ImageView) findViewById(R.id.indicator1);
		mImages[2] = (ImageView) findViewById(R.id.indicator2);
		mImages[3] = (ImageView) findViewById(R.id.indicator3);
		mImages[4] = (ImageView) findViewById(R.id.indicator4);
		mNumber = (TextView) findViewById(R.id.number);
	}
	
	/**
	 * 
	 * @Title: setIndicatorTop
	 * @Description: 设置高度(绝对高度)
	 * @param top
	 * @return: void
	 */
	public void setIndicatorTop(int top)
	{
		mTop = top;
	}
	
	/**
	 * 
	 * @Title: setTotalPage
	 * @Description: 设置总页数(对外接口)
	 * @param page
	 * @return: void
	 */
	public void setTotalPage(int page)
	{
		mTotalPage = page;
		setIndicatorPosition();
		
		if (mTotalPage > MAX)
		{
			for(int i=0; i<MAX; i++)
			{
				mImages[i].setVisibility(View.GONE);
			}
			mNumber.setVisibility(View.VISIBLE);
		}
		else
		{
			mNumber.setVisibility(View.GONE);
			
			for(int i=0; i<MAX; i++)
			{
				if (page == 1)
				{
					mImages[i].setVisibility(View.GONE);
				}
				else
				{
					if (i < page)
					{
						mImages[i].setVisibility(View.VISIBLE);
					}
					else
					{
						mImages[i].setVisibility(View.GONE);
					}
				}
			}
		}
	}
	
	/**
	 * 
	 * @Title: setCurPage
	 * @Description: 设置当前页码(对外接口)
	 * @param page
	 * @return: void
	 */
	public void setCurPage(int page)
	{
		if (mTotalPage > MAX)
		{
			mNumber.setText(page + "/" + mTotalPage);
		}
		else
		{
			if (mTotalPage > 1 && mTotalPage <= MAX)
			{
				for (int i=0; i<mTotalPage; i++)
				{
					if (i == page-1)
					{
						mImages[i].setImageResource(R.drawable.mainapp_indicator_focus);
					}
					else
					{
						mImages[i].setImageResource(R.drawable.mainapp_indicator_unfocus);
					}
				}
			}
		}
	}
	
	/**
	 * 
	 * @Title: setIndicatorPosition
	 * @Description: 重新设置控件坐标
	 * @return: void
	 */
	private void setIndicatorPosition()
	{
		if (mTotalPage > 0)
		{
			if (mTotalPage <= MAX)
			{
				if (mTotalPage > 1)
				{
					mInitX = (int)(WIDTH - IMAGE_WIDTH*mTotalPage - SPACE*(mTotalPage-1))/2;
					
					for (int i=0; i<mTotalPage; i++)
					{
						mImageX = mInitX + i*(IMAGE_WIDTH + SPACE);
						CommomUtil.setCoordinateOfView(mImages[i], mImageX, mTop, IMAGE_WIDTH, IMAGE_HEIGHT);
					}
				}
			}
			else
			{
				mInitX = 0;
				CommomUtil.setCoordinateOfView(mNumber, mInitX, mTop);
			}
		}
	}
}
