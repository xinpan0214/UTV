/**
 * Copyright © 2013GreatVision. All rights reserved.
 * 
 * @Title: MainActivity.java
 * @Prject: BananaTvSetting
 * @Description: TODO
 * @author: lijg
 * @date: 2013-11-28 上午10:49:32
 * @version: V1.0
 */
package com.sz.ead.framework.mul2launcher.status;

import android.graphics.Camera;
import android.graphics.Matrix;
import android.view.animation.Animation;
import android.view.animation.Transformation;

/**
 * @author lijungang
 *
 */
public class RotateAnimation extends Animation {
	/** 沿Y轴正方向看，数值减1时动画逆时针旋转。 */
	public static final boolean ROTATE_DECREASE = true;
	/** 沿Y轴正方向看，数值减1时动画顺时针旋转。 */
	public static final boolean ROTATE_INCREASE = false;
	/** Z轴上最大深度。 */
	public static final float DEPTH_Z = 310.0f;
	/** 动画显示时长。 */
	public static final long DURATION = 1200l;
	/** 动画重复次数。-1代表无线循环 */
	public static final int REPEATCOUNT = -1;
	private Camera camera;
	/** 用于监听动画进度。当值过半时需更新txtNumber的内容。 */
	private InterpolatedTimeListener listener;

	public RotateAnimation() {
		setDuration(DURATION);
		setRepeatCount(REPEATCOUNT);
	}

	public void initialize(int width, int height, int parentWidth, int parentHeight) {
		super.initialize(width, height, parentWidth, parentHeight);
		camera = new Camera();
	}

	public void setInterpolatedTimeListener(InterpolatedTimeListener listener) {
		this.listener = listener;
	}

	protected void applyTransformation(float interpolatedTime, Transformation transformation) {
		if (listener != null) {
			listener.interpolatedTime(interpolatedTime);
		}
		float from = 0.0f, to = 0.0f;
		from = 0.0f;
		to = 360.0f;
		float degree = from + (to - from) * interpolatedTime;
		boolean overHalf = (interpolatedTime > 0.5f);
		float depth = 0.0f;
		final Matrix matrix = transformation.getMatrix();
		camera.save();
		camera.translate(0.0f, 0.0f, depth);
		camera.rotateY(degree);
		camera.getMatrix(matrix);
		camera.restore();
		matrix.preTranslate(-20.0f, 0.0f);
		matrix.postTranslate(20.0f, 0.0f);
	}

	/** 动画进度监听器。 */
	public static interface InterpolatedTimeListener {
		public void interpolatedTime(float interpolatedTime);
	}
}
