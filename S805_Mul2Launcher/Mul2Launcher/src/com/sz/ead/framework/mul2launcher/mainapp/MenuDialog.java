/**
 * Copyright © 2015GreatVision. All rights reserved.
 *
 * @Title: MenuDialog.java
 * @Prject: Mul2Launcher
 * @Package: com.sz.ead.framework.mul2launcher.mainapp
 * @Description: 响应菜单键对话框
 * @author: zhaoqy  
 * @date: 2015-4-24 下午2:56:33
 */
package com.sz.ead.framework.mul2launcher.mainapp;

import com.sz.ead.framework.mul2launcher.R;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MenuDialog extends Dialog
{
	public final static int  MOVE      = 0; //移动
	public final static int  TOP       = 1; //置顶
	public final static int  BOTTOM    = 2; //置底
	public final static int  UNINSTALL = 3; //卸载
	
	public final static int  ACTION_TYPE_UNTOP                 = 0; //不能置顶
	public final static int  ACTION_TYPE_UNTOP_DISUNINSTALL    = 1; //不能置顶 + 不能卸载
	public final static int  ACTION_TYPE_DEFAULT               = 2; //默认
	public final static int  ACTION_TYPE_DEFAULT_DISUNINSTALL  = 3; //默认 + 不能卸载
	public final static int  ACTION_TYPE_UNBOTTOM              = 4; //不能置底
	public final static int  ACTION_TYPE_UNBOTTOM_DISUNINSTALL = 5; //不能置底  + 不能卸载
	
	private Context          mContext;                     
	private RelativeLayout[] mRllt = new RelativeLayout[4]; 
	private ImageView[]      mIcon = new ImageView[4];      
	private TextView[]       mAction = new TextView[4];     
	private ImageView        mAppIcon;                      
	private TextView         mAppName;                     
	private int              mActionType;                
	private int              mFocusIndex = 0;              
	
	private OnMenuItemSelectedListener mOnMenuItemSelectedListener;
	
	public interface OnMenuItemSelectedListener 
	{
		void OnMenuItemSelected(int mFocusIndex);
	}

	public void setOnMenuItemSelectedListener(OnMenuItemSelectedListener menuItemSelectedListener) 
	{
		mOnMenuItemSelectedListener = menuItemSelectedListener;
	}
	
	public MenuDialog(Context context) 
	{
		super(context, R.style.mainapp_menu_dialog);
		mContext = context;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setContentView(R.layout.mainapp_menudialog);
		init();
	}
	
	private void init()
	{
		mAppIcon = (ImageView) findViewById(R.id.menudialog_appicon);
		mAppName = (TextView) findViewById(R.id.menudialog_appname);
		mRllt[0] = (RelativeLayout) findViewById(R.id.menudialog_move);
		mRllt[1] = (RelativeLayout) findViewById(R.id.menudialog_top);
		mRllt[2] = (RelativeLayout) findViewById(R.id.menudialog_bottom);
		mRllt[3] = (RelativeLayout) findViewById(R.id.menudialog_uninstall);
		mIcon[0] = (ImageView) findViewById(R.id.menudialog_move_icon);
		mIcon[1] = (ImageView) findViewById(R.id.menudialog_top_icon);
		mIcon[2] = (ImageView) findViewById(R.id.menudialog_bottom_icon);
		mIcon[3] = (ImageView) findViewById(R.id.menudialog_uninstall_icon);
		mAction[0] = (TextView) findViewById(R.id.menudialog_move_action);
		mAction[1] = (TextView) findViewById(R.id.menudialog_top_action);
		mAction[2] = (TextView) findViewById(R.id.menudialog_bottom_action);
		mAction[3] = (TextView) findViewById(R.id.menudialog_uninstall_action);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) 
	{
		switch (keyCode) 
		{
		case KeyEvent.KEYCODE_DPAD_LEFT:
		{
			doKeyLeft();
			return true;
		}
		case KeyEvent.KEYCODE_DPAD_RIGHT:
		{
			doKeyRight();
			return true;
		}
		case KeyEvent.KEYCODE_DPAD_UP:
		{
			doKeyUp();
			return true;
		}
		case KeyEvent.KEYCODE_DPAD_DOWN:
		{
			doKeyDown();
			return true;
		}
		case KeyEvent.KEYCODE_DPAD_CENTER:
		{
			if (mOnMenuItemSelectedListener != null) 
			{
				mOnMenuItemSelectedListener.OnMenuItemSelected(mFocusIndex);
			}
			dismiss();
			return true;
		}
		case KeyEvent.KEYCODE_MENU:
		{
			dismiss();
			return true;
		}
		default:
			break;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	private void doKeyLeft()
	{
		switch (mFocusIndex) 
		{
		case TOP:
		{
			int previous = mFocusIndex;
			mFocusIndex -= 1;
			refreshMenuDialog(previous, mFocusIndex);
			break;
		}
		case BOTTOM:
		{
			int previous = mFocusIndex;
			if (mActionType == ACTION_TYPE_UNTOP || mActionType == ACTION_TYPE_UNTOP_DISUNINSTALL)
			{
				mFocusIndex -= 2;
				refreshMenuDialog(previous, mFocusIndex);
			}
			else if (mActionType == ACTION_TYPE_DEFAULT || mActionType == ACTION_TYPE_DEFAULT_DISUNINSTALL)
			{
				mFocusIndex -= 1;
				refreshMenuDialog(previous, mFocusIndex);
			}
			break;
		}
		case UNINSTALL:
		{
			int previous = mFocusIndex;
			if (mActionType == ACTION_TYPE_UNTOP || mActionType == ACTION_TYPE_DEFAULT) 
			{
				mFocusIndex -= 1;
				refreshMenuDialog(previous, mFocusIndex);
			}
			if (mActionType == ACTION_TYPE_UNBOTTOM) 
			{
				mFocusIndex -= 2;
				refreshMenuDialog(previous, mFocusIndex);
			}
			break;
		}
		default:
			break;
		}
	}
	
	private void doKeyRight()
	{
		switch (mFocusIndex) 
		{
		case MOVE:
		{
			int previous = mFocusIndex;
			if (mActionType == ACTION_TYPE_UNTOP || mActionType == ACTION_TYPE_UNTOP_DISUNINSTALL)
			{
				mFocusIndex += 2;
				refreshMenuDialog(previous, mFocusIndex);
			}
			else if (mActionType == ACTION_TYPE_DEFAULT || mActionType == ACTION_TYPE_DEFAULT_DISUNINSTALL ||
					mActionType == ACTION_TYPE_UNBOTTOM || mActionType == ACTION_TYPE_UNBOTTOM_DISUNINSTALL)
			{
				mFocusIndex += 1;
				refreshMenuDialog(previous, mFocusIndex);
			}
			break;
		}
		case TOP:
		{
			int previous = mFocusIndex;
			if (mActionType == ACTION_TYPE_DEFAULT || mActionType == ACTION_TYPE_DEFAULT_DISUNINSTALL)
			{
				mFocusIndex += 1;
				refreshMenuDialog(previous, mFocusIndex);
			}
			else if (mActionType == ACTION_TYPE_UNBOTTOM)
			{
				mFocusIndex += 2;
				refreshMenuDialog(previous, mFocusIndex);
			}
			break;
		}
		case BOTTOM:
		{
			int previous = mFocusIndex;
			if (mActionType == ACTION_TYPE_UNTOP || mActionType == ACTION_TYPE_DEFAULT)
			{
				mFocusIndex += 1;
				refreshMenuDialog(previous, mFocusIndex);
			}
			break;
		}
		default:
			break;
		}
	}
	
	private void doKeyUp()
	{
		switch (mFocusIndex) 
		{
		case BOTTOM:
		{
			int previous = mFocusIndex;
			mFocusIndex -= 2;
			refreshMenuDialog(previous, mFocusIndex);
			break;
		}
		case UNINSTALL:
		{
			int previous = mFocusIndex;
			if (mActionType == ACTION_TYPE_DEFAULT || mActionType == ACTION_TYPE_UNBOTTOM)
			{
				mFocusIndex -= 2;
				refreshMenuDialog(previous, mFocusIndex);
			}
			else if (mActionType == ACTION_TYPE_UNTOP)
			{
				mFocusIndex -= 3;
				refreshMenuDialog(previous, mFocusIndex);
			}
			break;
		}
		default:
			break;
		}
	}
	
	private void doKeyDown()
	{
		switch (mFocusIndex) 
		{
		case MOVE:
		{
			int previous = mFocusIndex;
			if (mActionType == ACTION_TYPE_UNTOP || 
				mActionType == ACTION_TYPE_UNTOP_DISUNINSTALL || 
				mActionType == ACTION_TYPE_DEFAULT ||
				mActionType == ACTION_TYPE_DEFAULT_DISUNINSTALL)
			{
				mFocusIndex += 2;
				refreshMenuDialog(previous, mFocusIndex);
			}
			else if (mActionType == ACTION_TYPE_UNBOTTOM)
			{
				mFocusIndex += 3;
				refreshMenuDialog(previous, mFocusIndex);
			}
			break;
		}
		case TOP:
		{
			int previous = mFocusIndex;
			if (mActionType == ACTION_TYPE_DEFAULT || mActionType == ACTION_TYPE_UNBOTTOM)
			{
				mFocusIndex += 2;
				refreshMenuDialog(previous, mFocusIndex);
			}
			else if (mActionType == ACTION_TYPE_DEFAULT_DISUNINSTALL || mActionType == ACTION_TYPE_UNBOTTOM_DISUNINSTALL)
			{
				mFocusIndex += 1;
				refreshMenuDialog(previous, mFocusIndex);
			}
			break;
		}
		default:
			break;
		}
	}

	/**
	 * 
	 * @Title: refreshMenuDialog
	 * @Description: 重新刷新菜单
	 * @param previous
	 * @param current
	 * @return: void
	 */
	private void refreshMenuDialog(int previous, int current) 
	{
		switch (previous) 
		{
		case MOVE:
		{
			mIcon[previous].setImageDrawable(mContext.getResources().getDrawable(R.drawable.mainapp_menudialog_move_default));
			break;
		}
		case TOP:
		{
			mIcon[previous].setImageDrawable(mContext.getResources().getDrawable(R.drawable.mainapp_menudialog_top_default));
			break;
		}
		case BOTTOM:
		{
			mIcon[previous].setImageDrawable(mContext.getResources().getDrawable(R.drawable.mainapp_menudialog_bottom_default));
			break;
		}
		case UNINSTALL:
		{
			mIcon[previous].setImageDrawable(mContext.getResources().getDrawable(R.drawable.mainapp_menudialog_uninstall_default));
			break;
		}
		default:
			break;
		}
		mAction[previous].setTextColor(mContext.getResources().getColor(R.color.mainapp_menudialog_default_color));
		
		switch (current) 
		{
		case MOVE:
		{
			mIcon[current].setImageDrawable(mContext.getResources().getDrawable(R.drawable.mainapp_menudialog_move_focus));
			break;
		}
		case TOP:
		{
			mIcon[current].setImageDrawable(mContext.getResources().getDrawable(R.drawable.mainapp_menudialog_top_focus));
			break;
		}
		case BOTTOM:
		{
			mIcon[current].setImageDrawable(mContext.getResources().getDrawable(R.drawable.mainapp_menudialog_bottom_focus));
			break;
		}
		case UNINSTALL:
		{
			mIcon[current].setImageDrawable(mContext.getResources().getDrawable(R.drawable.mainapp_menudialog_uninstall_focus));
			break;
		}
		default:
			break;
		}
		mAction[current].setTextColor(mContext.getResources().getColor(R.color.mainapp_menudialog_foucs_color));
		mRllt[current].requestFocus();
	}

	@Override
	public void dismiss() 
	{
		mActionType = 2;
		reInit();
		super.dismiss();
	}
	
	/**
	 * 
	 * @Title: reInit
	 * @Description: 将菜单重新初始化成默认状态
	 * @return: void
	 */
	private void reInit()
	{
		mIcon[MOVE].setImageDrawable(mContext.getResources().getDrawable(R.drawable.mainapp_menudialog_move_default));
		mAction[MOVE].setTextColor(mContext.getResources().getColor(R.color.mainapp_menudialog_default_color));
		
		mIcon[TOP].setImageDrawable(mContext.getResources().getDrawable(R.drawable.mainapp_menudialog_top_default));
		mAction[TOP].setTextColor(mContext.getResources().getColor(R.color.mainapp_menudialog_default_color));
		
		mIcon[BOTTOM].setImageDrawable(mContext.getResources().getDrawable(R.drawable.mainapp_menudialog_bottom_default));
		mAction[BOTTOM].setTextColor(mContext.getResources().getColor(R.color.mainapp_menudialog_default_color));
		
		mIcon[UNINSTALL].setImageDrawable(mContext.getResources().getDrawable(R.drawable.mainapp_menudialog_uninstall_default));
		mAction[UNINSTALL].setTextColor(mContext.getResources().getColor(R.color.mainapp_menudialog_default_color));
	}

	public void setDialog(Bitmap bMap, String appName, int actionType) 
	{
		mAppIcon.setImageBitmap(bMap);
		setDialog(appName, actionType);
	}

	public void setDialog(Drawable draw, String appName, int actionType) 
	{
		mAppIcon.setImageDrawable(draw);
		setDialog(appName, actionType);
	}
	
	/**
	 * 
	 * @Title: setDialog
	 * @Description: 设置菜单项
	 * @param appName
	 * @param topOrBottom
	 * @return: void
	 */
	private void setDialog(String appName, int actionType) 
	{
		mAppName.setText(appName);
		mActionType = actionType;
		
		//默认选中"移动"
		mFocusIndex = MOVE;
		mIcon[MOVE].setImageDrawable(mContext.getResources().getDrawable(R.drawable.mainapp_menudialog_move_focus));
		mAction[MOVE].setTextColor(mContext.getResources().getColor(R.color.mainapp_menudialog_foucs_color));
		mRllt[MOVE].requestFocus();
		
		switch (actionType) 
		{
		case ACTION_TYPE_UNTOP:
		{
			//不能置顶
			mIcon[TOP].setImageDrawable(mContext.getResources().getDrawable(R.drawable.mainapp_menudialog_top_disable));
			mAction[TOP].setTextColor(mContext.getResources().getColor(R.color.mainapp_menudialog_disable_color));
			break;
		}
		case ACTION_TYPE_UNTOP_DISUNINSTALL:
		{
			//不能置顶 + 不能卸载
			mIcon[TOP].setImageDrawable(mContext.getResources().getDrawable(R.drawable.mainapp_menudialog_top_disable));
			mAction[TOP].setTextColor(mContext.getResources().getColor(R.color.mainapp_menudialog_disable_color));
			mIcon[UNINSTALL].setImageDrawable(mContext.getResources().getDrawable(R.drawable.mainapp_menudialog_uninstall_disable));
			mAction[UNINSTALL].setTextColor(mContext.getResources().getColor(R.color.mainapp_menudialog_disable_color));
			break;
		}
		case ACTION_TYPE_DEFAULT:
		{
			break;
		}
		case ACTION_TYPE_DEFAULT_DISUNINSTALL:
		{
			//默认 + 不能卸载
			mIcon[UNINSTALL].setImageDrawable(mContext.getResources().getDrawable(R.drawable.mainapp_menudialog_uninstall_disable));
			mAction[UNINSTALL].setTextColor(mContext.getResources().getColor(R.color.mainapp_menudialog_disable_color));
			break;
		}
		case ACTION_TYPE_UNBOTTOM:
		{
			//不能置底
			mIcon[BOTTOM].setImageDrawable(mContext.getResources().getDrawable(R.drawable.mainapp_menudialog_bottom_disable));
			mAction[BOTTOM].setTextColor(mContext.getResources().getColor(R.color.mainapp_menudialog_disable_color));
			break;
		}
		case ACTION_TYPE_UNBOTTOM_DISUNINSTALL:
		{
			//不能置底  + 不能卸载
			mIcon[BOTTOM].setImageDrawable(mContext.getResources().getDrawable(R.drawable.mainapp_menudialog_bottom_disable));
			mAction[BOTTOM].setTextColor(mContext.getResources().getColor(R.color.mainapp_menudialog_disable_color));
			mIcon[UNINSTALL].setImageDrawable(mContext.getResources().getDrawable(R.drawable.mainapp_menudialog_uninstall_disable));
			mAction[UNINSTALL].setTextColor(mContext.getResources().getColor(R.color.mainapp_menudialog_disable_color));
			break;
		}
		default:
			break;
		}
	}
}
