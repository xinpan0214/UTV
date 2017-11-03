package com.sz.ead.framework.mul2launcher.appstore.dialog;

import com.sz.ead.framework.mul2launcher.R;

import android.content.Context;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


public class ShowToast {
	private static ShowToast mShowToast = new ShowToast();
	private static Toast toast = null;
	
	public static ShowToast getShowToast()
	{
		if (mShowToast == null){
			mShowToast = new ShowToast();
		}
		
		return mShowToast;
	}
	
	public void createToast(Context context,String text)
	{
		View view = View.inflate(context, R.layout.toast_search, null);
		TextView textView = (TextView) view.findViewById(R.id.toasts_text);
		textView.setText(text);
		
		if(toast == null){
			toast = new Toast(context);
		}
		toast.setDuration(Toast.LENGTH_LONG);
		toast.setView(view);
		toast.show();
	}
}
