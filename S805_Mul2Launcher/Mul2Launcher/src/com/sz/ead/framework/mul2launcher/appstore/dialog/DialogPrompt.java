package com.sz.ead.framework.mul2launcher.appstore.dialog;



import com.sz.ead.framework.mul2launcher.R;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class DialogPrompt extends Dialog implements OnClickListener 
{
	private DialogPrompt mContext = null;
	private DialogPromptListener mListener = null;
	//private ImageView mIconImg = null;
	private TextView mInfoTv = null;
	private Button mSureBtn = null;
	private Button mCancelBtn = null;
	private String msg = null,mSureText = null,mCancelText = null;
	//默认焦点在确定
	private boolean mFocusFlag = false;
	
	/**
	 *  自定义监听接口
	 */
	public interface DialogPromptListener
	{
		public void OnClick(View view);
	}

	public DialogPrompt(Context context)
	{
		super(context,R.style.DownloadFailDialog);
		this.mContext = this;
		requestWindowFeature(Window.FEATURE_NO_TITLE);
	}
	
	public DialogPrompt(Context context, int theme)
	{
		super(context, theme);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		this.getWindow().setContentView(R.layout.dialog_prompt);
		this.init();
	}
	
	private void init()
	{
		//this.mIconImg = (ImageView)findViewById(R.id.dialog_icon);
		this.mInfoTv = (TextView)findViewById(R.id.dialog_prompt_info);
		this.mSureBtn = (Button)findViewById(R.id.dialog_prompt_sure);
		this.mCancelBtn = (Button)findViewById(R.id.dialog_prompt_cancel);
		
		//this.mIconImg.setImageResource(R.drawable.dialog_prompt_about);
		this.mInfoTv.setText(this.msg);
		if(mSureText != null){
			mSureBtn.setText(mSureText);
		}
		if(mCancelText != null){
			mCancelBtn.setText(mCancelText);
		}
		this.mSureBtn.setOnClickListener(this);
		this.mCancelBtn.setOnClickListener(this);
		
		if(mFocusFlag){
			mCancelBtn.requestFocus();
		}
	}
	
	/**
	 * 
	 * @Title: setOnDialogPromptListener
	 * @Description: 监听接口函数
	 * @param listener
	 * @return: void
	 */
	public void setOnDialogPromptListener(DialogPromptListener listener)
	{
		this.mListener = listener;
	}

	public void onClick(View view) 
	{
		this.mListener.OnClick(view);
	}
	
	public void setMessage(String msg)
	{
		if(this.mInfoTv != null){
			this.mInfoTv.setText(msg);
		}
		this.msg = msg;
	}
	
	public void setPositiveButton(String text){
		if(this.mSureBtn != null){
			this.mSureBtn.setText(text);
		}
		mSureText = text;
	}
	
	public void setNeutralButton(String text){
		if(this.mCancelBtn != null){
			this.mCancelBtn.setText(text);
		}
		mCancelText = text;
	}
	
	public void setFocus(Boolean flag){
		mFocusFlag = flag;
	}
}