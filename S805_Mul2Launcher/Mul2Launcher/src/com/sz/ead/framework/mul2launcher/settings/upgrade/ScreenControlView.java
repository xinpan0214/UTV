package com.sz.ead.framework.mul2launcher.settings.upgrade;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import com.sz.ead.framework.mul2launcher.R;


public class ScreenControlView extends View {
	/** 屏幕滑动回调接口 */
	//private OnScrollToScreenListener onScreenChangeListener;
	/** 当前屏幕序号 */
	private int nCurrentScreen = 0;
	/** 屏幕总数 */
	private int nTotolScreens = 0;
	/** View宽度 */
	private int nViewWidth = 0;
	/** View高度 */
	private int nViewHeight = 0;
	/** 圆点直径 */
	private int nCircleDiameter = 0;
	/** 圆点之间间距 */
	private int nCircleSpace = 0;
	/** 当前屏幕圆点指示图片 */
	private Bitmap bmFocusedBitmap = null;
	/** 其他屏幕圆点指示图片 */
	private Bitmap bmOtherBitmap = null;
	
	/** 最多能画的圆点数 */
	private int nMaxDots = 0;
	/** 上次屏幕序号 */
	private int nLastScreenIndex = 0;
	/** 上次画的圆点序号 */
	private int nLastDotIndex = 0;
	
	/** 如果没设定指示图片，默认当前屏指示画笔，画一个圆 */
	private Paint mDefaultFoucusPaint;
	/** 如果没设定指示图片，默认其他屏指示画笔，画一个圆 */
    private Paint mDefaultOthorPaint; 
    
    private Paint mTextPaint;

	public ScreenControlView(Context context) {
		this(context,null);
		// TODO Auto-generated constructor stub
	}

	public ScreenControlView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// xml属性获取
        TypedArray ta = context.obtainStyledAttributes(attrs,R.styleable.SettingsScreenControlView);
        nCircleDiameter = ta.getDimensionPixelSize(R.styleable.SettingsScreenControlView_circle_diameter, 0);
        nCircleSpace = ta.getDimensionPixelSize(R.styleable.SettingsScreenControlView_circle_space, 0);
        Drawable mFocusedDrawable = ta.getDrawable(R.styleable.SettingsScreenControlView_focus_drawable);
        if(mFocusedDrawable!=null) {
        	bmFocusedBitmap = ((BitmapDrawable)mFocusedDrawable).getBitmap();
        }
        Drawable mOtherDrawable = ta.getDrawable(R.styleable.SettingsScreenControlView_other_drawable);
        if(mOtherDrawable!=null) {
        	bmOtherBitmap = ((BitmapDrawable)mOtherDrawable).getBitmap(); 
        }
        
        mDefaultFoucusPaint = new Paint();
        mDefaultFoucusPaint.setStyle( Paint.Style.FILL );   
        mDefaultFoucusPaint.setColor( 0xFFFF0000 );   
        mDefaultFoucusPaint.setAntiAlias(true);
        
        mDefaultOthorPaint = new Paint();
        mDefaultOthorPaint.setStyle( Paint.Style.FILL );   
        mDefaultOthorPaint.setColor( 0xFF00FF00 );   
        mDefaultOthorPaint.setAntiAlias(true);
        
        mTextPaint = new Paint();  
        mTextPaint.setColor(0xFFFFFFFF);   
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextSize(nCircleDiameter);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        
        nTotolScreens = 5;
		nCurrentScreen = 2;
		ta.recycle();
	}
	
	/**
	 * @author chenshiqiang
	 * Description: 绑定到ScrollLayout
	 * @param scrollLayout
	 */
	/*public void bindToScrollLayout(ScrollLayout scrollLayout) {
		// TODO Auto-generated method stub
		nTotolScreens = scrollLayout.getChildCount();
		nCurrentScreen = scrollLayout.getCurScreen();
		this.postInvalidate();
		scrollLayout.setOnScrollToScreen(onScreenChangeListener);
	}*/
	
	public int getCurrentScreen() {
		return nCurrentScreen;
	}

	public void setCurrentScreen(int nCurrentScreen) {
		this.nCurrentScreen = nCurrentScreen;
		invalidate();
	}

	public int getTotolScreens() {
		return nTotolScreens;
	}

	public void setTotolScreens(int nTotolScreens) {
		this.nTotolScreens = nTotolScreens;
		if(this.nTotolScreens <= 1){
			this.setVisibility(View.INVISIBLE);
		}else{
			this.setVisibility(View.VISIBLE);
		}
		invalidate();
	}

	@Override
	public void draw(Canvas canvas) {
		super.draw(canvas);
		
		Rect r = new Rect( );   
        this.getDrawingRect( r );
        
        if(nViewWidth==0) nViewWidth = r.width();
        if(nViewHeight==0) nViewHeight = r.height();
        if(nCircleDiameter==0) nCircleDiameter = nViewHeight/3;
        if(nCircleSpace==0) nCircleSpace = nViewHeight;
        if(nMaxDots==0) nMaxDots = (nViewWidth-nCircleSpace)/(nCircleDiameter+nCircleSpace);
        
        if(nTotolScreens > 5){
        	int cx = nViewWidth/2;
        	int cy = nViewHeight;
        	canvas.drawText((nCurrentScreen+1) + "/" + nTotolScreens, cx, cy, mTextPaint);
        	return;
        }
        
        //画指示圆点
        if(nTotolScreens <= nMaxDots) {
        	//正常情况
        	for( int i = 0; i < nTotolScreens; i++ )   
            { 
            	int cx = (nViewWidth - nTotolScreens*nCircleDiameter - nTotolScreens*nCircleSpace + nCircleSpace
            			+ 2*i*nCircleSpace + 2*i*nCircleDiameter + nCircleDiameter)/2;
            	int cy = nViewHeight/2;
            	if(i==nCurrentScreen)
            	{
            		if(bmFocusedBitmap==null)
                	{
            			canvas.drawCircle(cx, cy, nCircleDiameter/2, mDefaultFoucusPaint);
                	}
                	else
                	{
                		Rect src = new Rect(0, 0, bmFocusedBitmap.getWidth(), bmFocusedBitmap.getHeight());
                		int desL = cx - nCircleDiameter/2;
                		int desR = cx + nCircleDiameter/2;
                		int desT = cy - nCircleDiameter/2;
                		int desB = cy + nCircleDiameter/2;
                		Rect dst = new Rect(desL, desT, desR, desB);
                		canvas.drawBitmap(bmFocusedBitmap, src, dst, null);
                	}
            	}
            	else
            	{
            		if(bmOtherBitmap==null)
                	{
            			canvas.drawCircle(cx, cy, nCircleDiameter/2, mDefaultOthorPaint);
                	}
                	else
                	{
                		Rect src = new Rect(0, 0, bmOtherBitmap.getWidth(), bmOtherBitmap.getHeight());
                		int desL = cx - nCircleDiameter/2;
                		int desR = cx + nCircleDiameter/2;
                		int desT = cy - nCircleDiameter/2;
                		int desB = cy + nCircleDiameter/2;
                		Rect dst = new Rect(desL, desT, desR, desB);
                		canvas.drawBitmap(bmOtherBitmap, src, dst, null);
                	}
            	}
            }
        }else {
        	//屏幕数大于能画的圆点数，循环画圆
        	int nShouldDrawIndex = 0;   //这次应该画的圆点序号
        	if(nCurrentScreen == nLastScreenIndex ) {
        		if(nCurrentScreen == 0 && nLastScreenIndex == 0) {
        			nShouldDrawIndex = 0;
        		}else {
        			nShouldDrawIndex = nLastDotIndex;
        		}
        	}else if( ((nCurrentScreen > nLastScreenIndex)&&(nCurrentScreen - nLastScreenIndex == 1))
        		|| (nLastScreenIndex-nCurrentScreen)==(nTotolScreens-1)) {
        		if(nLastDotIndex==nMaxDots-1) {
        			nShouldDrawIndex = 0;
        		}else {
        			nShouldDrawIndex = nLastDotIndex+1;
        		}
        	}else {
        		if(nLastDotIndex==0) {
        			nShouldDrawIndex = nMaxDots-1;
        		}else {
        			nShouldDrawIndex = nLastDotIndex-1;
        		}
        	}
        	
        	for( int i = 0; i < nMaxDots; i++ ) {
        		int cx = (nViewWidth - nMaxDots*nCircleDiameter - nMaxDots*nCircleSpace + nCircleSpace
            			+ 2*i*nCircleSpace + 2*i*nCircleDiameter + nCircleDiameter)/2;
            	int cy = nViewHeight/2;
            	if(i==nShouldDrawIndex)
            	{
            		if(bmFocusedBitmap==null)
                	{
            			canvas.drawCircle(cx, cy, nCircleDiameter/2, mDefaultFoucusPaint);
                	}
                	else
                	{
                		Rect src = new Rect(0, 0, bmFocusedBitmap.getWidth(), bmFocusedBitmap.getHeight());
                		int desL = cx - nCircleDiameter/2;
                		int desR = cx + nCircleDiameter/2;
                		int desT = cy - nCircleDiameter/2;
                		int desB = cy + nCircleDiameter/2;
                		Rect dst = new Rect(desL, desT, desR, desB);
                		canvas.drawBitmap(bmFocusedBitmap, src, dst, null);
                	}
            	}
            	else
            	{
            		if(bmOtherBitmap==null)
                	{
            			canvas.drawCircle(cx, cy, nCircleDiameter/2, mDefaultOthorPaint);
                	}
                	else
                	{
                		Rect src = new Rect(0, 0, bmOtherBitmap.getWidth(), bmOtherBitmap.getHeight());
                		int desL = cx - nCircleDiameter/2;
                		int desR = cx + nCircleDiameter/2;
                		int desT = cy - nCircleDiameter/2;
                		int desB = cy + nCircleDiameter/2;
                		Rect dst = new Rect(desL, desT, desR, desB);
                		canvas.drawBitmap(bmOtherBitmap, src, dst, null);
                	}
            	}
            }
        	
        	nLastScreenIndex = nCurrentScreen;
            nLastDotIndex = nShouldDrawIndex;
        }
	}
}
