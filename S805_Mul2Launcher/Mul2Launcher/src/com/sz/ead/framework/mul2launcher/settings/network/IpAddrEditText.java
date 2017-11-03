/**
 * 
 * Copyright © 2014GreatVision. All rights reserved.
 *
 * @Title: IpAddrEditText.java
 * @Prject: BananaTvSetting
 * @Description: TODO
 * @author: luojw 
 * @date: 2014-1-21 上午11:52:30
 */

package com.sz.ead.framework.mul2launcher.settings.network;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.EditText;

public class IpAddrEditText extends EditText {
	
	private Paint m_paint;
	public IpAddrEditText(Context context) {
        super(context);
        // TODO Auto-generated constructor stub

    }
	
	public IpAddrEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public IpAddrEditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}
	
	
	@Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }
}
