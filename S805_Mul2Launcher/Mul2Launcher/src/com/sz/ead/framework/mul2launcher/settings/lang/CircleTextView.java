/**
 * Copyright © 2015GreatVision. All rights reserved.
 *
 * @Title: ChangeTextView.java
 * @Prject: Mul2Launcher
 * @Package: com.sz.ead.framework.mul2launcher.settings.lang
 * @Description: TODO
 * @author: lijungang  
 * @date: 2015-4-29 下午2:11:28
 */
package com.sz.ead.framework.mul2launcher.settings.lang;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.sz.ead.framework.mul2launcher.R;

/**
 * Copyright © 2015GreatVision. All rights reserved.
 *
 * @Title: ChangeTextView.java
 * @Prject: Mul2Launcher
 * @Package: com.sz.ead.framework.mul2launcher.settings.lang
 * @Description: TODO
 * @author: lijungang 
 * @date: 2015-4-29 下午2:11:28
 */
public class CircleTextView extends View{
	
	private List<String> mList;
	//初始化五个paint字体，及一个绘画动作字体
	private Paint mPaint0, mPaint1, mPaint2, mPaint3, mPaint4, mPaint; 
	//0：初始化， 1：向左旋转， 2：向右旋转
	private int flag;
	//五个字体的横纵坐标数组
	private int[][] mPosition = new int[5][2];
	//绘画次数计数
	private int drawCount;
	// 每次按键绘画次数
	private final int PaintTimes = 12;
	//模五后的按键次数
	private int keyCount; 
	//实际画的y坐标位置
	private float[] new_y = new float[5]; 
	/**
	 * @param arg0
	 */
	public CircleTextView(Context arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
		onCreate();
	}
	/**
	 * @param context
	 * @param attrs
	 */
	public CircleTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		onCreate();
	}
	/**
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 */
	public CircleTextView(Context arg0, AttributeSet arg1, int arg2) {
		super(arg0, arg1, arg2);
		// TODO Auto-generated constructor stub
		onCreate();
	}
	/**
	 * 
	 * @Title: onCreate
	 * @Description: 初始化6个paint画笔
	 * @return: void
	 */
	public void onCreate(){
		mPaint0 = new Paint();
		mPaint1 = new Paint();
		mPaint2 = new Paint();
		mPaint3 = new Paint();
		mPaint4 = new Paint();
		mPaint = new Paint();
		
		mPaint0.setColor(getResources().getColor(R.color.settings_lang_1_color));
		mPaint0.setTextSize(getResources().getDimension(R.dimen.size_26));
		mPaint0.setTextAlign(Paint.Align.CENTER);
		mPaint0.setFlags(Paint.ANTI_ALIAS_FLAG);
		
		mPaint1.setColor(getResources().getColor(R.color.settings_lang_2_color));
		mPaint1.setTextSize(getResources().getDimension(R.dimen.size_30));
		mPaint1.setTextAlign(Paint.Align.CENTER);
		mPaint1.setFlags(Paint.ANTI_ALIAS_FLAG);
		
		mPaint2.setColor(getResources().getColor(R.color.settings_lang_3_color));
		mPaint2.setTextSize(getResources().getDimension(R.dimen.size_40));
		mPaint2.setTextAlign(Paint.Align.CENTER);
		mPaint2.setFlags(Paint.ANTI_ALIAS_FLAG);
		
		mPaint3.setColor(getResources().getColor(R.color.settings_lang_2_color));
		mPaint3.setTextSize(getResources().getDimension(R.dimen.size_30));
		mPaint3.setTextAlign(Paint.Align.CENTER);
		mPaint3.setFlags(Paint.ANTI_ALIAS_FLAG);
		
		mPaint4.setColor(getResources().getColor(R.color.settings_lang_1_color));
		mPaint4.setTextSize(getResources().getDimension(R.dimen.size_26));
		mPaint4.setTextAlign(Paint.Align.CENTER);
		mPaint4.setFlags(Paint.ANTI_ALIAS_FLAG);
		
		mPosition[0][0] = 200;
		mPosition[0][1] = 329;
		mPosition[1][0] = 380;
		mPosition[1][1] = 326;
		mPosition[2][0] = 640;
		mPosition[2][1] = 323;
		mPosition[3][0] = 900;
		mPosition[3][1] = 326;
		mPosition[4][0] = 1080;
		mPosition[4][1] = 329;
		
		drawCount = 0;
		keyCount= 0;
		for(int i=0; i < 5; i++){
			new_y[i] = 0;
		}
	}
	
	public void init(List<String> lang){
		mList = new ArrayList<String>(lang);
		flag = 0;
		invalidate();
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		if(flag == 0){
			initTextView(canvas);
		}else if(flag == 1){
			rotateLeft(canvas);
		}else if(flag == 2){
			rotateRight(canvas);
		}
	}
	
	/**
	 * 
	 * @Title: initTextView
	 * @Description: 初始化view
	 * @return: void
	 */
	private void initTextView(Canvas canvas){
		FontMetrics fontMetrics0 = mPaint0.getFontMetrics(); 
		new_y[0] = mPosition[0][1] + (mPaint0.getTextSize() - 
        		(fontMetrics0.bottom - fontMetrics0.top))/2 - fontMetrics0.top;
		FontMetrics fontMetrics1 = mPaint1.getFontMetrics(); 
		new_y[1] = mPosition[1][1] + (mPaint1.getTextSize() - 
        		(fontMetrics1.bottom - fontMetrics1.top))/2 - fontMetrics1.top;
		FontMetrics fontMetrics2 = mPaint2.getFontMetrics(); 
		new_y[2] = mPosition[2][1] + (mPaint2.getTextSize() - 
        		(fontMetrics2.bottom - fontMetrics2.top))/2 - fontMetrics2.top;
		FontMetrics fontMetrics3 = mPaint3.getFontMetrics(); 
		new_y[3] = mPosition[3][1] + (mPaint3.getTextSize() - 
        		(fontMetrics3.bottom - fontMetrics3.top))/2 - fontMetrics3.top;
		FontMetrics fontMetrics4 = mPaint4.getFontMetrics(); 
		new_y[4] = mPosition[4][1] + (mPaint4.getTextSize() - 
        		(fontMetrics4.bottom - fontMetrics4.top))/2 - fontMetrics4.top;
		
		canvas.drawText(mList.get(0).toString(), mPosition[0][0], new_y[0], mPaint0);
		canvas.drawText(mList.get(1).toString(), mPosition[1][0], new_y[1], mPaint1);
		canvas.drawText(mList.get(2).toString(), mPosition[2][0], new_y[2], mPaint2);
		canvas.drawText(mList.get(3).toString(), mPosition[3][0], new_y[3], mPaint3);
		canvas.drawText(mList.get(4).toString(), mPosition[4][0], new_y[4], mPaint4);
	}
	
	/**
	 * 
	 * @Title: rotateLeft
	 * @Description: 递归调用invalidate进行向左旋转
	 * @return: void
	 */
	private void rotateLeft(Canvas canvas){
		drawCount++;
		float x, y;
		float size = 0;
		if(drawCount == PaintTimes+1){
			if(keyCount%15 == 0){
				canvas.drawText(mList.get(0).toString(), mPosition[0][0], new_y[0], mPaint0);
				canvas.drawText(mList.get(1).toString(), mPosition[1][0], new_y[1], mPaint1);
				canvas.drawText(mList.get(2).toString(), mPosition[2][0], new_y[2], mPaint2);
				canvas.drawText(mList.get(3).toString(), mPosition[3][0], new_y[3], mPaint3);
				canvas.drawText(mList.get(4).toString(), mPosition[4][0], new_y[4], mPaint4);
			}else if(keyCount%15 == 1){
				canvas.drawText(mList.get(1).toString(), mPosition[0][0], new_y[0], mPaint0);
				canvas.drawText(mList.get(2).toString(), mPosition[1][0], new_y[1], mPaint1);
				canvas.drawText(mList.get(3).toString(), mPosition[2][0], new_y[2], mPaint2);
				canvas.drawText(mList.get(4).toString(), mPosition[3][0], new_y[3], mPaint3);
				canvas.drawText(mList.get(5).toString(), mPosition[4][0], new_y[4], mPaint4);
			}else if(keyCount%15 == 2){
				canvas.drawText(mList.get(2).toString(), mPosition[0][0], new_y[0], mPaint0);
				canvas.drawText(mList.get(3).toString(), mPosition[1][0], new_y[1], mPaint1);
				canvas.drawText(mList.get(4).toString(), mPosition[2][0], new_y[2], mPaint2);
				canvas.drawText(mList.get(5).toString(), mPosition[3][0], new_y[3], mPaint3);
				canvas.drawText(mList.get(6).toString(), mPosition[4][0], new_y[4], mPaint4);
			}else if(keyCount%15 == 3){
				canvas.drawText(mList.get(3).toString(), mPosition[0][0], new_y[0], mPaint0);
				canvas.drawText(mList.get(4).toString(), mPosition[1][0], new_y[1], mPaint1);
				canvas.drawText(mList.get(5).toString(), mPosition[2][0], new_y[2], mPaint2);
				canvas.drawText(mList.get(6).toString(), mPosition[3][0], new_y[3], mPaint3);
				canvas.drawText(mList.get(7).toString(), mPosition[4][0], new_y[4], mPaint4);
			}else if(keyCount%15 == 4){
				canvas.drawText(mList.get(4).toString(), mPosition[0][0], new_y[0], mPaint0);
				canvas.drawText(mList.get(5).toString(), mPosition[1][0], new_y[1], mPaint1);
				canvas.drawText(mList.get(6).toString(), mPosition[2][0], new_y[2], mPaint2);
				canvas.drawText(mList.get(7).toString(), mPosition[3][0], new_y[3], mPaint3);
				canvas.drawText(mList.get(8).toString(), mPosition[4][0], new_y[4], mPaint4);
			}
			else if(keyCount%15 == 5){
				canvas.drawText(mList.get(5).toString(), mPosition[0][0], new_y[0], mPaint0);
				canvas.drawText(mList.get(6).toString(), mPosition[1][0], new_y[1], mPaint1);
				canvas.drawText(mList.get(7).toString(), mPosition[2][0], new_y[2], mPaint2);
				canvas.drawText(mList.get(8).toString(), mPosition[3][0], new_y[3], mPaint3);
				canvas.drawText(mList.get(9).toString(), mPosition[4][0], new_y[4], mPaint4);
			}
			else if(keyCount%15 == 6){
				canvas.drawText(mList.get(6).toString(), mPosition[0][0], new_y[0], mPaint0);
				canvas.drawText(mList.get(7).toString(), mPosition[1][0], new_y[1], mPaint1);
				canvas.drawText(mList.get(8).toString(), mPosition[2][0], new_y[2], mPaint2);
				canvas.drawText(mList.get(9).toString(), mPosition[3][0], new_y[3], mPaint3);
				canvas.drawText(mList.get(10).toString(), mPosition[4][0], new_y[4], mPaint4);
			}
			else if(keyCount%15 == 7){
				canvas.drawText(mList.get(7).toString(), mPosition[0][0], new_y[0], mPaint0);
				canvas.drawText(mList.get(8).toString(), mPosition[1][0], new_y[1], mPaint1);
				canvas.drawText(mList.get(9).toString(), mPosition[2][0], new_y[2], mPaint2);
				canvas.drawText(mList.get(10).toString(), mPosition[3][0], new_y[3], mPaint3);
				canvas.drawText(mList.get(11).toString(), mPosition[4][0], new_y[4], mPaint4);
			}
			else if(keyCount%15 == 8){
				canvas.drawText(mList.get(8).toString(), mPosition[0][0], new_y[0], mPaint0);
				canvas.drawText(mList.get(9).toString(), mPosition[1][0], new_y[1], mPaint1);
				canvas.drawText(mList.get(10).toString(), mPosition[2][0], new_y[2], mPaint2);
				canvas.drawText(mList.get(11).toString(), mPosition[3][0], new_y[3], mPaint3);
				canvas.drawText(mList.get(12).toString(), mPosition[4][0], new_y[4], mPaint4);
			}
			else if(keyCount%15 == 9){
				canvas.drawText(mList.get(9).toString(), mPosition[0][0], new_y[0], mPaint0);
				canvas.drawText(mList.get(10).toString(), mPosition[1][0], new_y[1], mPaint1);
				canvas.drawText(mList.get(11).toString(), mPosition[2][0], new_y[2], mPaint2);
				canvas.drawText(mList.get(12).toString(), mPosition[3][0], new_y[3], mPaint3);
				canvas.drawText(mList.get(13).toString(), mPosition[4][0], new_y[4], mPaint4);
			}
			else if(keyCount%15 == 10){
				canvas.drawText(mList.get(10).toString(), mPosition[0][0], new_y[0], mPaint0);
				canvas.drawText(mList.get(11).toString(), mPosition[1][0], new_y[1], mPaint1);
				canvas.drawText(mList.get(12).toString(), mPosition[2][0], new_y[2], mPaint2);
				canvas.drawText(mList.get(13).toString(), mPosition[3][0], new_y[3], mPaint3);
				canvas.drawText(mList.get(14).toString(), mPosition[4][0], new_y[4], mPaint4);
			}
			else if(keyCount%15 == 11){
				canvas.drawText(mList.get(11).toString(), mPosition[0][0], new_y[0], mPaint0);
				canvas.drawText(mList.get(12).toString(), mPosition[1][0], new_y[1], mPaint1);
				canvas.drawText(mList.get(13).toString(), mPosition[2][0], new_y[2], mPaint2);
				canvas.drawText(mList.get(14).toString(), mPosition[3][0], new_y[3], mPaint3);
				canvas.drawText(mList.get(0).toString(), mPosition[4][0], new_y[4], mPaint4);
			}
			else if(keyCount%15 == 12){
				canvas.drawText(mList.get(12).toString(), mPosition[0][0], new_y[0], mPaint0);
				canvas.drawText(mList.get(13).toString(), mPosition[1][0], new_y[1], mPaint1);
				canvas.drawText(mList.get(14).toString(), mPosition[2][0], new_y[2], mPaint2);
				canvas.drawText(mList.get(0).toString(), mPosition[3][0], new_y[3], mPaint3);
				canvas.drawText(mList.get(1).toString(), mPosition[4][0], new_y[4], mPaint4);
			}
			else if(keyCount%15 == 13){
				canvas.drawText(mList.get(13).toString(), mPosition[0][0], new_y[0], mPaint0);
				canvas.drawText(mList.get(14).toString(), mPosition[1][0], new_y[1], mPaint1);
				canvas.drawText(mList.get(0).toString(), mPosition[2][0], new_y[2], mPaint2);
				canvas.drawText(mList.get(1).toString(), mPosition[3][0], new_y[3], mPaint3);
				canvas.drawText(mList.get(2).toString(), mPosition[4][0], new_y[4], mPaint4);
			}
			else if(keyCount%15 == 14){
				canvas.drawText(mList.get(14).toString(), mPosition[0][0], new_y[0], mPaint0);
				canvas.drawText(mList.get(0).toString(), mPosition[1][0], new_y[1], mPaint1);
				canvas.drawText(mList.get(1).toString(), mPosition[2][0], new_y[2], mPaint2);
				canvas.drawText(mList.get(2).toString(), mPosition[3][0], new_y[3], mPaint3);
				canvas.drawText(mList.get(3).toString(), mPosition[4][0], new_y[4], mPaint4);
			}
			drawCount = 0;
			return;
		}
		
		for(int i=0; i<4 ; i++){
			x = mPosition[i+1][0] - (mPosition[i+1][0]- mPosition[i][0])*drawCount/PaintTimes;
			y = mPosition[i+1][1] - (mPosition[i+1][1] - mPosition[i][1])*drawCount/PaintTimes;
			if(i == 0){
				size =mPaint1.getTextSize() - (mPaint1.getTextSize() - mPaint0.getTextSize())*drawCount/PaintTimes;
			}else if(i == 1){
				size =mPaint2.getTextSize() - (mPaint2.getTextSize() - mPaint1.getTextSize())*drawCount/PaintTimes;
			}else if(i == 2){
				size =mPaint3.getTextSize() - (mPaint3.getTextSize() - mPaint2.getTextSize())*drawCount/PaintTimes;
			}else if(i == 3){
				size =mPaint4.getTextSize() - (mPaint4.getTextSize() - mPaint3.getTextSize())*drawCount/PaintTimes;
			}
			mPaint.setTextSize(size);
			mPaint.setColor(getResources().getColor(R.color.settings_lang_1_color));
			mPaint.setTextAlign(Paint.Align.CENTER);
			mPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
			
			FontMetrics fontMetrics = mPaint.getFontMetrics(); 
	        float new_y = y + (mPaint.getTextSize() - 
	        		(fontMetrics.bottom - fontMetrics.top))/2 - fontMetrics.top;
			canvas.drawText(mList.get((i+keyCount)%15).toString(), x, new_y, mPaint);
		}
		if(drawCount <= PaintTimes){
			invalidate();
		}
	}
	
	/**
	 * 
	 * @Title: rotateRight
	 * @Description: 递归调用invalidate进行向右旋转
	 * @return: void
	 */
	private void rotateRight(Canvas canvas){
		drawCount++;
		float x, y;
		float size = 0;
		if(drawCount == PaintTimes + 1){
			if(keyCount%15 == 0){
				canvas.drawText(mList.get(0).toString(), mPosition[0][0], new_y[0], mPaint0);
				canvas.drawText(mList.get(1).toString(), mPosition[1][0], new_y[1], mPaint1);
				canvas.drawText(mList.get(2).toString(), mPosition[2][0], new_y[2], mPaint2);
				canvas.drawText(mList.get(3).toString(), mPosition[3][0], new_y[3], mPaint3);
				canvas.drawText(mList.get(4).toString(), mPosition[4][0], new_y[4], mPaint4);
			}else if(keyCount%15 == 1){
				canvas.drawText(mList.get(1).toString(), mPosition[0][0], new_y[0], mPaint0);
				canvas.drawText(mList.get(2).toString(), mPosition[1][0], new_y[1], mPaint1);
				canvas.drawText(mList.get(3).toString(), mPosition[2][0], new_y[2], mPaint2);
				canvas.drawText(mList.get(4).toString(), mPosition[3][0], new_y[3], mPaint3);
				canvas.drawText(mList.get(5).toString(), mPosition[4][0], new_y[4], mPaint4);
			}else if(keyCount%15 == 2){
				canvas.drawText(mList.get(2).toString(), mPosition[0][0], new_y[0], mPaint0);
				canvas.drawText(mList.get(3).toString(), mPosition[1][0], new_y[1], mPaint1);
				canvas.drawText(mList.get(4).toString(), mPosition[2][0], new_y[2], mPaint2);
				canvas.drawText(mList.get(5).toString(), mPosition[3][0], new_y[3], mPaint3);
				canvas.drawText(mList.get(6).toString(), mPosition[4][0], new_y[4], mPaint4);
			}else if(keyCount%15 == 3){
				canvas.drawText(mList.get(3).toString(), mPosition[0][0], new_y[0], mPaint0);
				canvas.drawText(mList.get(4).toString(), mPosition[1][0], new_y[1], mPaint1);
				canvas.drawText(mList.get(5).toString(), mPosition[2][0], new_y[2], mPaint2);
				canvas.drawText(mList.get(6).toString(), mPosition[3][0], new_y[3], mPaint3);
				canvas.drawText(mList.get(7).toString(), mPosition[4][0], new_y[4], mPaint4);
			}else if(keyCount%15 == 4){
				canvas.drawText(mList.get(4).toString(), mPosition[0][0], new_y[0], mPaint0);
				canvas.drawText(mList.get(5).toString(), mPosition[1][0], new_y[1], mPaint1);
				canvas.drawText(mList.get(6).toString(), mPosition[2][0], new_y[2], mPaint2);
				canvas.drawText(mList.get(7).toString(), mPosition[3][0], new_y[3], mPaint3);
				canvas.drawText(mList.get(8).toString(), mPosition[4][0], new_y[4], mPaint4);
			}
			else if(keyCount%15 == 5){
				canvas.drawText(mList.get(5).toString(), mPosition[0][0], new_y[0], mPaint0);
				canvas.drawText(mList.get(6).toString(), mPosition[1][0], new_y[1], mPaint1);
				canvas.drawText(mList.get(7).toString(), mPosition[2][0], new_y[2], mPaint2);
				canvas.drawText(mList.get(8).toString(), mPosition[3][0], new_y[3], mPaint3);
				canvas.drawText(mList.get(9).toString(), mPosition[4][0], new_y[4], mPaint4);
			}
			else if(keyCount%15 == 6){
				canvas.drawText(mList.get(6).toString(), mPosition[0][0], new_y[0], mPaint0);
				canvas.drawText(mList.get(7).toString(), mPosition[1][0], new_y[1], mPaint1);
				canvas.drawText(mList.get(8).toString(), mPosition[2][0], new_y[2], mPaint2);
				canvas.drawText(mList.get(9).toString(), mPosition[3][0], new_y[3], mPaint3);
				canvas.drawText(mList.get(10).toString(), mPosition[4][0], new_y[4], mPaint4);
			}
			else if(keyCount%15 == 7){
				canvas.drawText(mList.get(7).toString(), mPosition[0][0], new_y[0], mPaint0);
				canvas.drawText(mList.get(8).toString(), mPosition[1][0], new_y[1], mPaint1);
				canvas.drawText(mList.get(9).toString(), mPosition[2][0], new_y[2], mPaint2);
				canvas.drawText(mList.get(10).toString(), mPosition[3][0], new_y[3], mPaint3);
				canvas.drawText(mList.get(11).toString(), mPosition[4][0], new_y[4], mPaint4);
			}
			else if(keyCount%15 == 8){
				canvas.drawText(mList.get(8).toString(), mPosition[0][0], new_y[0], mPaint0);
				canvas.drawText(mList.get(9).toString(), mPosition[1][0], new_y[1], mPaint1);
				canvas.drawText(mList.get(10).toString(), mPosition[2][0], new_y[2], mPaint2);
				canvas.drawText(mList.get(11).toString(), mPosition[3][0], new_y[3], mPaint3);
				canvas.drawText(mList.get(12).toString(), mPosition[4][0], new_y[4], mPaint4);
			}
			else if(keyCount%15 == 9){
				canvas.drawText(mList.get(9).toString(), mPosition[0][0], new_y[0], mPaint0);
				canvas.drawText(mList.get(10).toString(), mPosition[1][0], new_y[1], mPaint1);
				canvas.drawText(mList.get(11).toString(), mPosition[2][0], new_y[2], mPaint2);
				canvas.drawText(mList.get(12).toString(), mPosition[3][0], new_y[3], mPaint3);
				canvas.drawText(mList.get(13).toString(), mPosition[4][0], new_y[4], mPaint4);
			}
			else if(keyCount%15 == 10){
				canvas.drawText(mList.get(10).toString(), mPosition[0][0], new_y[0], mPaint0);
				canvas.drawText(mList.get(11).toString(), mPosition[1][0], new_y[1], mPaint1);
				canvas.drawText(mList.get(12).toString(), mPosition[2][0], new_y[2], mPaint2);
				canvas.drawText(mList.get(13).toString(), mPosition[3][0], new_y[3], mPaint3);
				canvas.drawText(mList.get(14).toString(), mPosition[4][0], new_y[4], mPaint4);
			}
			else if(keyCount%15 == 11){
				canvas.drawText(mList.get(11).toString(), mPosition[0][0], new_y[0], mPaint0);
				canvas.drawText(mList.get(12).toString(), mPosition[1][0], new_y[1], mPaint1);
				canvas.drawText(mList.get(13).toString(), mPosition[2][0], new_y[2], mPaint2);
				canvas.drawText(mList.get(14).toString(), mPosition[3][0], new_y[3], mPaint3);
				canvas.drawText(mList.get(0).toString(), mPosition[4][0], new_y[4], mPaint4);
			}
			else if(keyCount%15 == 12){
				canvas.drawText(mList.get(12).toString(), mPosition[0][0], new_y[0], mPaint0);
				canvas.drawText(mList.get(13).toString(), mPosition[1][0], new_y[1], mPaint1);
				canvas.drawText(mList.get(14).toString(), mPosition[2][0], new_y[2], mPaint2);
				canvas.drawText(mList.get(0).toString(), mPosition[3][0], new_y[3], mPaint3);
				canvas.drawText(mList.get(1).toString(), mPosition[4][0], new_y[4], mPaint4);
			}
			else if(keyCount%15 == 13){
				canvas.drawText(mList.get(13).toString(), mPosition[0][0], new_y[0], mPaint0);
				canvas.drawText(mList.get(14).toString(), mPosition[1][0], new_y[1], mPaint1);
				canvas.drawText(mList.get(0).toString(), mPosition[2][0], new_y[2], mPaint2);
				canvas.drawText(mList.get(1).toString(), mPosition[3][0], new_y[3], mPaint3);
				canvas.drawText(mList.get(2).toString(), mPosition[4][0], new_y[4], mPaint4);
			}
			else if(keyCount%15 == 14){
				canvas.drawText(mList.get(14).toString(), mPosition[0][0], new_y[0], mPaint0);
				canvas.drawText(mList.get(0).toString(), mPosition[1][0], new_y[1], mPaint1);
				canvas.drawText(mList.get(1).toString(), mPosition[2][0], new_y[2], mPaint2);
				canvas.drawText(mList.get(2).toString(), mPosition[3][0], new_y[3], mPaint3);
				canvas.drawText(mList.get(3).toString(), mPosition[4][0], new_y[4], mPaint4);
			}
			drawCount = 0;
			return;
		}
		
		for(int i=0; i<4 ; i++){
			x = mPosition[i][0] + (mPosition[i+1][0]- mPosition[i][0])*drawCount/PaintTimes;
			y = mPosition[i][1] + (mPosition[i+1][1] - mPosition[i][1])*drawCount/PaintTimes;
			if(i == 0){
				size =mPaint0.getTextSize() + (mPaint1.getTextSize() - mPaint0.getTextSize())*drawCount/PaintTimes;
			}else if(i == 1){
				size =mPaint1.getTextSize() + (mPaint2.getTextSize() - mPaint1.getTextSize())*drawCount/PaintTimes;
			}else if(i == 2){
				size =mPaint2.getTextSize() + (mPaint3.getTextSize() - mPaint2.getTextSize())*drawCount/PaintTimes;
			}else if(i == 3){
				size =mPaint3.getTextSize() + (mPaint4.getTextSize() - mPaint3.getTextSize())*drawCount/PaintTimes;
			}
			mPaint.setTextSize(size);
			mPaint.setColor(getResources().getColor(R.color.settings_lang_1_color));
			mPaint.setTextAlign(Paint.Align.CENTER);
			mPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
			
			FontMetrics fontMetrics = mPaint.getFontMetrics(); 
	        float new_y = y + (mPaint.getTextSize() - 
	        		(fontMetrics.bottom - fontMetrics.top))/2 - fontMetrics.top;
			canvas.drawText(mList.get((i+keyCount + 1)%15).toString(), x, new_y, mPaint);
		}
		if(drawCount <= PaintTimes){
			invalidate();
		}
	}
	/**
	 * 
	 * @Title: doKeyRight
	 * @Description: 向右按键
	 * @return: void
	 */
	public void doKeyRight(int count){
		flag = 1;
		drawCount = 0;
		keyCount = count;
		invalidate();
	}
	/**
	 * 
	 * @Title: doKeyLeft
	 * @Description: 向左按键
	 * @return: void
	 */
	public void doKeyLeft(int count){
		flag = 2;
		drawCount = 0;
		keyCount --;
		keyCount = count;
		invalidate();
	}
}
