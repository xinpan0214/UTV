/**
 * 
 * Copyright © 2014GreatVision. All rights reserved.
 *
 * @Title: ZoomActivity.java
 * @Prject: BananaTvSetting
 * @Description: 系统缩放比例接口
 * @author: lijungang 
 * @date: 2014-1-24 下午2:00:35
 */
package com.sz.ead.framework.mul2launcher.settings.zoom;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.setting.settingservice.SettingManager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.sz.ead.framework.mul2launcher.BaseActivity;
import com.sz.ead.framework.mul2launcher.MainActivity;
import com.sz.ead.framework.mul2launcher.R;
import com.sz.ead.framework.mul2launcher.status.Statusbar;
import com.szgvtv.ead.framework.bi.Bi;

public class ZoomActivity extends BaseActivity{
	
	private TextView setting_zoom_value;
	private int zoom_value;
	private ImageView setting_zoom_circle_content;
	private SettingManager settingManager;
	private Statusbar mStatus_Bar;
	private boolean isBiSend;
	private ImageView setting_zoom_right;
	private ImageView setting_zoom_left;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings_zoom_activity);

		settingManager = (SettingManager)getSystemService(Context.SETTING_SERVICE);
		setting_zoom_value = (TextView) findViewById(R.id.setting_zoom_value);
		mStatus_Bar = (Statusbar) findViewById(R.id.llyt_main_header);
		mStatus_Bar.setTaskBarTitle(this.getResources().getString(R.string.settings_menu_zoom_title));
		setting_zoom_circle_content = (ImageView) findViewById(R.id.setting_zoom_circle_content);
		setting_zoom_right = (ImageView) findViewById(R.id.setting_zoom_right_arrow);
		setting_zoom_left = (ImageView) findViewById(R.id.setting_zoom_select_left_arrow);
		zoom_value = settingManager.getCurZoom();
		setting_zoom_value.setText(Integer.toString(zoom_value) + "%");
		setting_zoom_circle_content.setBackground(zoomDrawable(getResources().getDrawable(
				R.drawable.settings_zoom_circle_content), Math.round(360*zoom_value/100), Math.round(266*zoom_value/100)));
		if(zoom_value > 0){
			setting_zoom_circle_content.setVisibility(View.VISIBLE);
		}else{
			setting_zoom_circle_content.setVisibility(View.INVISIBLE);
		}
		isBiSend = false;
	}
	
	/**
	 * 
	 * @Title: drawableToBitmap
	 * @Description: drawable转bitmap
	 * @return: void
	 */
	public static Bitmap drawableToBitmap(Drawable drawable) {  
        int w = drawable.getIntrinsicWidth();  
        int h = drawable.getIntrinsicHeight();  
  
        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888  
                : Bitmap.Config.RGB_565;  
        Bitmap bitmap = Bitmap.createBitmap(w, h, config); 
        Canvas canvas = new Canvas(bitmap);  
        drawable.setBounds(0, 0, w, h);  
        drawable.draw(canvas);  
        return bitmap;  
    }
	
	/**
	 * 
	 * @Title: zoomDrawable
	 * @Description: 缩放指定大小后返回drawable
	 * @return: Drawable 
	 */
	public static Drawable zoomDrawable(Drawable drawable, int w, int h) { 
		if(w == 0) w=1;
		if(h == 0) h=1;
	    int width = drawable.getIntrinsicWidth();  
	    int height = drawable.getIntrinsicHeight();  
	    Bitmap oldbmp = drawableToBitmap(drawable);  
	    Matrix matrix = new Matrix();  
	    float sx = ((float) w / width);  
	    float sy = ((float) h / height);  
	    matrix.postScale(sx, sy);  
	    Bitmap newbmp = Bitmap.createBitmap(oldbmp, 0, 0, width, height,  
	            matrix, true);  
	    return new BitmapDrawable(newbmp);  
	} 
	
	/**
	 * 
	 * @Title: keyRight
	 * @Description: 右键处理函数
	 * @return: void 
	 */
	private void keyRight(){
		switch(zoom_value){
		case 95:
		case 90:
		case 85:
		case 80:
		case 75:
		case 70:
		case 65:
		case 60:
		case 55:
		case 50:
		case 45:
		case 40:
		case 35:
		case 30:
		case 25:
		case 20:
		case 15:
		case 10:
		case 5:
		case 0:
			zoom_value = zoom_value +5;
			break;
		default:
			return;
		}
		if((zoom_value <= 100) || (zoom_value >= 0))
		{
			setting_zoom_circle_content.setBackground(zoomDrawable(getResources().getDrawable(
					R.drawable.settings_zoom_circle_content), Math.round(360*zoom_value/100), Math.round(266*zoom_value/100)));
			if(zoom_value > 0){
				setting_zoom_circle_content.setVisibility(View.VISIBLE);
			}
			setting_zoom_value.setText(Integer.toString(zoom_value) + "%");
			settingManager.setSystemZoom(zoom_value);
		}
	}
	
	/**
	 * 
	 * @Title: keyRight
	 * @Description: 左键处理函数
	 * @return: void 
	 */
	private void keyLeft(){
		switch(zoom_value){
		case 100:
		case 95:
		case 90:
		case 85:
		case 80:
		case 75:
		case 70:
		case 65:
		case 60:
		case 55:
		case 50:
		case 45:
		case 40:
		case 35:
		case 30:
		case 25:
		case 20:
		case 15:
		case 10:
		case 5:
			zoom_value = zoom_value -5;
			break;
		default:
			return;
		}
		if((zoom_value <= 100) || (zoom_value >= 0))
		{
			setting_zoom_circle_content.setBackground(zoomDrawable(getResources().getDrawable(
					R.drawable.settings_zoom_circle_content), Math.round(360*zoom_value/100), Math.round(266*zoom_value/100)));
			if(zoom_value == 0){
				setting_zoom_circle_content.setVisibility(View.INVISIBLE);
			}
			setting_zoom_value.setText(Integer.toString(zoom_value) + "%");
			settingManager.setSystemZoom(zoom_value);
		}
	}
	/**
	 * 
	 * @Title: dispatchKeyEvent
	 * @Description: 按键操作
	 * @return: boolean 
	 */
	@Override
	public boolean dispatchKeyEvent(KeyEvent event)
	{	
		if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT && event.getAction() == KeyEvent.ACTION_DOWN)
		{
			setting_zoom_right.setImageResource(R.drawable.settings_zoom_arrow_right_focus);
			isBiSend = true;
			keyRight();
		}
		else if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT && event.getAction() == KeyEvent.ACTION_UP)
		{
			setting_zoom_right.setImageResource(R.drawable.settings_zoom_arrow_right);
		}
		else if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT && event.getAction() == KeyEvent.ACTION_DOWN)
		{
			setting_zoom_left.setImageResource(R.drawable.settings_zoom_arrow_left_focus);
			isBiSend = true;
			keyLeft();
		}
		else if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT && event.getAction() == KeyEvent.ACTION_UP)
		{
			setting_zoom_left.setImageResource(R.drawable.settings_zoom_arrow_left);
		}
		else if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP)
		{
			onKeyBack();
		}
		
		return true;
	}
	/**
	 * 
	 * @Title: onPause
	 * @Description:pause状态保存当前缩放比例值 
	 * @return: void 
	 */
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		Thread thr = new Thread(){
			@Override
			public void run() {
				// TODO Auto-generated method stub
				super.run();
				settingManager.saveSystemZoom(zoom_value);
			}
		};
		thr.start();
	}
	
	/**
	 * 
	 * @Title: onBackPressed
	 * @Description: 返回键处理函数
	 * @return: void 
	 */
	public void onKeyBack(){
		if(isBiSend)
		{
			Bi.sendBiMsg(Bi.BICT_FIRMWARE, Bi.BICC_SET_ZOOM, Integer.toString(zoom_value));
		}
		settingManager.saveSystemZoom(zoom_value);
		setResult(0);
		finish();
	}
}
