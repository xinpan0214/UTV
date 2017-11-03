/**
 * Copyright © 2015GreatVision. All rights reserved.
 *
 * @Title: CommomUtil.java
 * @Prject: Mul2Launcher
 * @Package: com.sz.ead.framework.mul2launcher.util
 * @Description: 公共Util
 * @author: zhaoqy  
 * @date: 2015-4-23 下午5:05:02
 */
package com.sz.ead.framework.mul2launcher.util;

import android.view.View;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.RelativeLayout;


public class CommomUtil 
{
	/**
	 * 
	 * @Title: setCoordinateOfView
	 * @Description: 设置坐标 (设置控件所在的位置YY，并且不改变宽高，XY为绝对位置)
	 * @param view
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @return: void
	 */
	public static void setCoordinateOfView(View view,int x,int y, int width, int height)
	{
		RelativeLayout.LayoutParams rllp =new RelativeLayout.LayoutParams(view.getLayoutParams());
		rllp.leftMargin = x;
		rllp.topMargin = y;
		rllp.width = width;
		rllp.height = height;
		view.setLayoutParams(rllp);
	} 
	
	/**
	 * 
	 * @Title: setCoordinateOfView
	 * @Description: 设置坐标 (设置控件所在的位置YY，并且不改变宽高，XY为绝对位置)
	 * @param view
	 * @param x
	 * @param y
	 * @return: void
	 */
	public static void setCoordinateOfView(View view,int x,int y)
	{
		MarginLayoutParams margin = new MarginLayoutParams(view.getLayoutParams());
		int width = x+margin.width;
		int height = y+margin.height;
		margin.setMargins(x, y, width, height);
		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(margin);
		view.setLayoutParams(layoutParams);
	} 
}
