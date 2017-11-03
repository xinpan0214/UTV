/**
 * Copyright © 2015GreatVision. All rights reserved.
 *
 * @Title: FragmentSettingsSection.java
 * @Prject: Mul2Launcher
 * @Package: com.sz.ead.framework.mul2launcher.settings
 * @Description: TODO
 * @author: lijungang  
 * @date: 2015-4-25 上午9:51:24
 */
package com.sz.ead.framework.mul2launcher.settings;

import com.sz.ead.framework.mul2launcher.R;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Copyright © 2015GreatVision. All rights reserved.
 *
 * @Title: FragmentSettingsSection.java
 * @Prject: Mul2Launcher
 * @Package: com.sz.ead.framework.mul2launcher.settings
 * @Description: TODO
 * @author: lijungang 
 * @date: 2015-4-25 上午9:51:24
 */
public class FragmentSettingsSection extends RelativeLayout{
	
	public RelativeLayout section_bg;
	public ImageView section_img;
	public TextView section_text_1;
	public TextView section_text_2;
	
	/**
	 * @param context
	 */
	public FragmentSettingsSection(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		inflate(context, R.layout.settings_fragment_section, this);
		
		section_img = (ImageView) findViewById(R.id.id_settings_section_img);
		section_text_1 = (TextView) findViewById(R.id.id_settings_section_text_1);
		section_text_2 = (TextView) findViewById(R.id.id_settings_section_text_2);
	}
	/**
	 * @param context
	 * @param attrs
	 */
	public FragmentSettingsSection(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		inflate(context, R.layout.settings_fragment_section, this);
		section_bg = (RelativeLayout)findViewById(R.id.id_settings_section);
		section_img = (ImageView) findViewById(R.id.id_settings_section_img);
		section_text_1 = (TextView) findViewById(R.id.id_settings_section_text_1);
		section_text_2 = (TextView) findViewById(R.id.id_settings_section_text_2);
	}
	
	public void setBgImg(int drawableID){
		section_bg.setBackground(getResources().getDrawable(drawableID));
	}
	
	public void setSectionImg(int drawableID){
		section_img.setBackground(getResources().getDrawable(drawableID));
	}
	
	public void setSectionText1(String text, int colorID){
		section_text_1.setText(text);
		section_text_1.setTextColor(getResources().getColor(colorID));
	}
	
	public void setSectionText2(String text, int colorID){
		section_text_2.setText(text);
		section_text_2.setTextColor(getResources().getColor(colorID));
	}
}
