/**
 * Copyright © 2015GreatVision. All rights reserved.
 *
 * @Title: PromptDialog.java
 * @Prject: Mul2Launcher
 * @Package: com.sz.ead.framework.mul2launcher.mainapp
 * @Description: 提示对话框
 * @author: zhaoqy  
 * @date: 2015-4-27 下午8:31:08
 */
package com.sz.ead.framework.mul2launcher.mainapp;

import com.sz.ead.framework.mul2launcher.R;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class PromptDialog extends Dialog implements OnClickListener
{
	private Context  mContext; //上下文
	private TextView mMessage; //信息
	private Button   mSure;    //确定
	private Button   mCancel;  //取消
	private PromptDialogClickListener mPromptDialogClickListener;
	
	public interface PromptDialogClickListener
	{
		void onClick(View v);
	}
	
	public void setOnPromptDialogClickListener(PromptDialogClickListener onPromptDialogClickListener)
	{
		mPromptDialogClickListener = onPromptDialogClickListener;
	}
	
	public PromptDialog(Context context) 
	{
		super(context, R.style.mainapp_menu_dialog);
		mContext = context;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setContentView(R.layout.mainapp_promptdialog);
		init();
	}
	
	private void init()
	{
		mMessage = (TextView) findViewById(R.id.promptdialog_message);
		mSure = (Button) findViewById(R.id.promptdialog_sure);
		mCancel = (Button) findViewById(R.id.promptdialog_cancel);
		mSure.setOnClickListener(this);
		mCancel.setOnClickListener(this);
		mCancel.requestFocus();
	}
	
	public void setAppName(String appName)
	{
		String text = String.format(mContext.getResources().getString(R.string.mainapp_promptdialog_message), appName);
		mMessage.setText(text);
	}
	
	@Override
	public void onClick(View v) 
	{
		mPromptDialogClickListener.onClick(v);
	}
}
