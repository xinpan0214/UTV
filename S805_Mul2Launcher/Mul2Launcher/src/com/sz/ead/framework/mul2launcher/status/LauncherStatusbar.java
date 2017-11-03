package com.sz.ead.framework.mul2launcher.status;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.login.loginservice.LoginManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.os.storage.StorageEventListener;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.setting.settingservice.SettingManager;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.sz.ead.framework.mul2launcher.R;

public class LauncherStatusbar extends LinearLayout{

	private Context mContext;
	public static final String ACTION_STATUS_AUTH = "com.sz.ead.framework.info.InfoService.auth";
	public static final String ACTION_STATUS_MARQUEE = "com.sz.ead.framework.message.title_show_msg";
	private SettingManager settingManager;
	private LoginManager loginManager;
	private StatusWifiSignalBroadcastReceiver mWifiSignalReceiver;
	private StatusEthernetBroadcastReceiver mEtheReceiver;
	private StatusAuthBroadcastReceiver mAuthReceiver;
	private StatusMarqueeBroadcastReceiver mMarqueeReceiver;
	private StorageManager manager;
	private StorageEventListener listener;
	private static final String USB_PATH0 = "/storage/external_storage/sda";
	private static final String USB_PATH1 = "/storage/external_storage/sda1";
	private static final String USB_PATH2 = "/storage/external_storage/sda2";
	private static final String USB_PATH3 = "/storage/external_storage/sda3";
	private static final String USB_PATH4 = "/storage/external_storage/sda4";
	private static final String USB_PATH5 = "/storage/external_storage/sdb";
	private static final String USB_PATH6 = "/storage/external_storage/sdb1";
	private static final String USB_PATH7 = "/storage/external_storage/sdb2";
	private static final String USB_PATH8 = "/storage/external_storage/sdb3";
	private static final String USB_PATH9 = "/storage/external_storage/sdb4";
	private static final String USB_PATH10 = "/storage/external_storage/sdc";
	private static final String USB_PATH11 = "/storage/external_storage/sdc1";
	private static final String USB_PATH12 = "/storage/external_storage/sdc2";
	private static final String USB_PATH13 = "/storage/external_storage/sdc3";
	private static final String USB_PATH14 = "/storage/external_storage/sdc4";
	private static final String SDCARD_PATH = "/storage/external_storage/sdcard1";
	private static final String STORAGE_MOUNTED = "mounted";
	private static final String STORAGE_CHECKING = "checking";
	int[] SINGAL_LEVEL = { R.drawable.status_icon_wifi1_3,
			R.drawable.status_icon_wifi1_2, R.drawable.status_icon_wifi1_1, R.drawable.status_icon_wifi1_0 };

	private static final int UPDATE_TIME = 1;
	private static final int UPDATE_AUTH_SUCCESS = 2;
	private static final int UPDATE_AUTH_FAILED = 3;
	private static final int UPDATE_MSG_TEXT = 4;
	private static final int UPDATE_MESSAGE_CHANGE = 6;
	private static final int UPGRADE_DISAPPEAR_MESSAGE = 7;

	private RelativeLayout iv_header;
	private RelativeLayout TvpadBg;
	private ViewFlipper iv_header_message_content;
	private ImageView iv_header_wifi_logo;
	private ImageView iv_header_tf_logo;
	private ImageView iv_header_usb_logo;
	private ImageView iv_header_adsl_logo;
	private ImageView iv_header_login;
	private ImageView iv_header_speaker;
	private ImageView iv_header_logo;
	private TextView mTextClock;
	private TextView message_view1;

	private ScheduledExecutorService mTimeTimer = null;
	private RotateAnimation rotateAnimSD = null;
	private RotateAnimation rotateAnimUSB0 = null;
	
	private ScheduledExecutorService mMsgTimer = null;
    private ArrayList<String> mMessageList = new ArrayList<String>();
    private ArrayList<String> mMessageListForDisplay = new ArrayList<String>();
    private static int mMessageRepeateCount = 2;
    private int mRepeatedly;
    private int mTotalDisplayMsgNum;
    private int mCurMessage;
    private Animation mInAnimPushUpMsg;
    private Animation mOutAnimPushUpMsg;
    private static int ANIMATION_TIME_MSG = 200;
    private Animation mInAnimInUseMsg;
    private Animation mOutAnimInUseMsg;
    private String mMessageLang = "";

	public LauncherStatusbar(Context context) {
		this(context, null);
	}

	public LauncherStatusbar(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	@SuppressLint("NewApi")
	public LauncherStatusbar(Context context, AttributeSet attrs, int defaultstyle) {
		super(context, attrs, defaultstyle);
		mContext = context;
		init();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
	}

	@Override
	protected void onDetachedFromWindow() {
		if (mTimeTimer != null) {
			mTimeTimer.shutdownNow();
			mTimeTimer = null;
		}
		destroyMessage();
		mContext.unregisterReceiver(mWifiSignalReceiver);
		mContext.unregisterReceiver(mEtheReceiver);
		mContext.unregisterReceiver(mAuthReceiver);
		mContext.unregisterReceiver(mMarqueeReceiver);
		manager.unregisterListener(listener);
		super.onDetachedFromWindow();
	}

	private void init() {
		settingManager = (SettingManager) mContext
				.getSystemService(Context.SETTING_SERVICE);
		loginManager = (LoginManager) mContext
				.getSystemService(Context.LOGIN_SERVICE);
		IntentFilter filter;
		mWifiSignalReceiver = new StatusWifiSignalBroadcastReceiver();
		filter = new IntentFilter();
		filter.addAction(WifiManager.RSSI_CHANGED_ACTION);
		mContext.registerReceiver(mWifiSignalReceiver, filter);
		filter = new IntentFilter();
		filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
		filter.addAction("android.net.wifi.WIFI_STATE_CHANGED");
		filter.addAction("android.net.wifi.STATE_CHANGE");
		mContext.registerReceiver(mWifiSignalReceiver, filter);

		mEtheReceiver = new StatusEthernetBroadcastReceiver();
		filter = new IntentFilter();
		filter.addAction("android.net.ethernet.ETH_STATE_CHANGED");
		mContext.registerReceiver(mEtheReceiver, filter);

		mAuthReceiver = new StatusAuthBroadcastReceiver();
		filter = new IntentFilter();
		filter.addAction(ACTION_STATUS_AUTH);
		mContext.registerReceiver(mAuthReceiver, filter);
		
		mMarqueeReceiver = new StatusMarqueeBroadcastReceiver();
		filter = new IntentFilter();
		filter.addAction(ACTION_STATUS_MARQUEE);
		mContext.registerReceiver(mMarqueeReceiver, filter);

		findViews();
		RelativeLayout.LayoutParams vp = new RelativeLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		addView(iv_header, vp);

		changeWallpaperPic();
		UpdateNetInfo();
		initStoragy();
		manager = StorageManager.from(mContext);
		listener = new StorageEventListener() {
			@Override
			public void onStorageStateChanged(String path, String oldState,
					String newState) {
				if (path.equals(USB_PATH0) || path.equals(USB_PATH1) || path.equals(USB_PATH2) || 
						path.equals(USB_PATH3) || path.equals(USB_PATH4) || path.equals(USB_PATH5) || 
						path.equals(USB_PATH6) || path.equals(USB_PATH7) || path.equals(USB_PATH8) || 
						path.equals(USB_PATH9) || path.equals(USB_PATH10) || path.equals(USB_PATH11) ||
						path.equals(USB_PATH12) || path.equals(USB_PATH13) || path.equals(USB_PATH14)) {
					if (newState.equals(STORAGE_MOUNTED)) {
						clearUSB0Anim();
						iv_header_usb_logo.setVisibility(View.VISIBLE);
						iv_header_usb_logo.setImageResource(R.drawable.status_icon_usb1_1);
					} else if(newState.equals(STORAGE_CHECKING)){
						iv_header_usb_logo.setVisibility(View.VISIBLE);
						iv_header_usb_logo.setImageResource(R.drawable.status_icon_usb1_2);
						startUSB0Anim();
					} else {
						clearUSB0Anim();
						iv_header_usb_logo.setVisibility(View.GONE);
					}
				} else if (path.equals(SDCARD_PATH)) {
					if (newState.equals(STORAGE_MOUNTED)) {
						clearSDAnim();
						iv_header_tf_logo.setVisibility(View.VISIBLE);
						iv_header_tf_logo.setImageResource(R.drawable.status_icon_tfcard1_1);
					} else if(newState.equals(STORAGE_CHECKING)){
						iv_header_tf_logo.setVisibility(View.VISIBLE);
						iv_header_tf_logo.setImageResource(R.drawable.status_icon_tfcard1_2);
						startSDAnim();
					} else {
						clearSDAnim();
						iv_header_tf_logo.setVisibility(View.GONE);
					}
				}
				super.onStorageStateChanged(path, oldState, newState);
			}
		};
		manager.registerListener(listener);

		if (loginManager.getAuthStatus().equals("101")
				|| loginManager.getAuthStatus().isEmpty()) {
			iv_header_login.setImageResource(R.drawable.status_icon_login1_1);
		}else{
			iv_header_login.setImageResource(R.drawable.status_icon_login1_2);
		}

		iv_header.setVisibility(View.VISIBLE);

		if (mTimeTimer == null) {
			mTimeTimer = Executors.newScheduledThreadPool(1);
			mTimeTimer.scheduleAtFixedRate(new TimeTask(), 1000, 5000, TimeUnit.MILLISECONDS);
		}
	}

	@SuppressLint("NewApi")
	private void changeWallpaperPic() {
		TvpadBg.setBackground(getResources().getDrawable(R.drawable.main_bg));
	}

	/**
	 * 
	 * @Title: startUSB0Anim
	 * @Description: u盘插入时动画
	 * @return: void
	 */
	private void startUSB0Anim() {
		rotateAnimUSB0 = new RotateAnimation();
		if (rotateAnimUSB0 != null) {
			rotateAnimUSB0.setFillAfter(true);
			iv_header_usb_logo.startAnimation(rotateAnimUSB0);
		}
		rotateAnimUSB0.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
			}
		});
	}
	
	private void clearUSB0Anim(){
		if (rotateAnimUSB0 != null) {
			iv_header_usb_logo.clearAnimation();
		}
	}
	
	/**
	 * 
	 * @Title: startSDAnim
	 * @Description: sd卡插入时动画
	 * @return: void
	 */
	private void startSDAnim() {
		rotateAnimSD = new RotateAnimation();
		if (rotateAnimSD != null) {
			rotateAnimSD.setFillAfter(true);
			iv_header_tf_logo.startAnimation(rotateAnimSD);
		}
		rotateAnimSD.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
			}
		});
	}

	private void clearSDAnim(){
		if (rotateAnimSD != null) {
			iv_header_tf_logo.clearAnimation();
		}
	}
	
	@SuppressLint("NewApi")
	private void findViews() {
		iv_header = (RelativeLayout) View.inflate(mContext, R.layout.status_launcher,
				null);
		TvpadBg = (RelativeLayout) iv_header.findViewById(R.id.TvpadBg);
		iv_header_message_content = (ViewFlipper) iv_header.findViewById(R.id.iv_header_message_content);
		iv_header_message_content.setMeasureAllChildren(true);
		iv_header_logo = (ImageView) iv_header
				.findViewById(R.id.iv_header_logo);
		iv_header_adsl_logo = (ImageView) iv_header
				.findViewById(R.id.iv_header_adsl_logo);
		iv_header_usb_logo = (ImageView) iv_header
				.findViewById(R.id.iv_header_usb_logo);
		iv_header_wifi_logo = (ImageView) iv_header
				.findViewById(R.id.iv_header_wifi_logo);
		iv_header_tf_logo = (ImageView) iv_header
				.findViewById(R.id.iv_header_tf_logo);
		iv_header_login = (ImageView) iv_header
				.findViewById(R.id.iv_header_login);
		iv_header_speaker = (ImageView)iv_header.
				findViewById(R.id.iv_header_speaker);
		message_view1 = (TextView)iv_header.findViewById(R.id.message_view1);
		mTextClock = (TextView) iv_header.findViewById(R.id.digiclock_header);
		iv_header_speaker.setVisibility(View.INVISIBLE);
		if (settingManager.getLogoByte() != null)
			iv_header_logo.setImageBitmap(BitmapFactory.decodeByteArray(
					settingManager.getLogoByte(), 0,
					settingManager.getLogoByte().length));
		updateTime();
	}
	/**
	 * 
	 * @Title: initMessageList
	 * @Description: 重组message信息
	 * @return: void
	 */
	private boolean initMessageList(){
		mCurMessage = 0;
        for (int i = 0; i < iv_header_message_content.getChildCount(); i++) {
        	iv_header_message_content.getChildAt(i).setVisibility(View.INVISIBLE);
        }
        stopMessageAnimation();
        prepareDspList();
        if(mTotalDisplayMsgNum == 0) return false;
        ((TextView)iv_header_message_content.getCurrentView()).setText(mMessageListForDisplay.get(mCurMessage).toString());
        iv_header_message_content.getCurrentView().setVisibility(View.VISIBLE);
        iv_header_speaker.setVisibility(View.VISIBLE);
		mRepeatedly = 0;
		if(mMsgTimer == null){
			mMsgTimer = Executors.newScheduledThreadPool(1); 
			mMsgTimer.scheduleAtFixedRate(new MessageTask(), 5000, 5000, TimeUnit.MILLISECONDS); 
		}
		return true;
	}
	/**
	 * 
	 * @Title: MessageTask
	 * @Description: 定时刷新message
	 * @return: void
	 */
    class MessageTask implements Runnable{
		/* (non-Javadoc)
		 * @see java.util.TimerTask#run()
		 */
		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {
				mHandler.sendEmptyMessage(UPDATE_MESSAGE_CHANGE);
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
    }
	/**
	 * 
	 * @Title: stopMessageAnimation
	 * @Description: 取消message动画
	 * @return: void 
	 */
    private void stopMessageAnimation() {
    	iv_header_message_content.stopFlipping();
    }
	/**
	 * 
	 * @Title: prepareDspList
	 * @Description: 将收到的信息处理为需要显示的信息，消息分段。
	 * @return: void
	 */
	private void prepareDspList(){
		mMessageListForDisplay.clear();
		
		int contentLength = 0;
		contentLength = 720;
		int j = 0;
		for(int i=0; i< mMessageList.size(); i++){
			Rect bounds = new Rect();
			TextPaint paint;
			paint = message_view1.getPaint();
			paint.getTextBounds(mMessageList.get(i), 0, mMessageList.get(i).length(), bounds);
			int width = bounds.width();
			if(width > contentLength){
				StringBuffer sb = new StringBuffer(mMessageList.get(i).toString()); 
				int k = 0;
				while(k < mMessageList.get(i).length()){
					String ellipsizeStr = (String) TextUtils.ellipsize(sb.substring(k), (TextPaint) paint, contentLength, TextUtils.TruncateAt.END);
					if(ellipsizeStr.endsWith("…"))
					{
						if(null == mMessageLang || mMessageLang.isEmpty()){
							mMessageListForDisplay.add(j, ellipsizeStr.substring(0, ellipsizeStr.length() - 1));
							j = j+1;
							k = k + ellipsizeStr.length() - 1;
						}else{
							if(mMessageLang.equals("en")){ //对英语把单词拆开两行显示的问题做处理
								int m = ellipsizeStr.length() - 1;
								if(sb.charAt(k + ellipsizeStr.length() -1) == 32){
									m = ellipsizeStr.length() - 1;
									mMessageListForDisplay.add(j, ellipsizeStr.substring(0, m));
								}else{
									boolean flag = false;
									for(m=ellipsizeStr.length() - 2; m>0; m--){
										if(ellipsizeStr.charAt(m) == 32){
											flag = true;
											break;
										}
									}
									if(flag){
										mMessageListForDisplay.add(j, ellipsizeStr.substring(0, m));
									}else{
										m = ellipsizeStr.length() - 1;
										mMessageListForDisplay.add(j, ellipsizeStr.substring(0, m));
									}
								}
								j = j+1;
								k = k + m;
							}else{
								mMessageListForDisplay.add(j, ellipsizeStr.substring(0, ellipsizeStr.length() - 1));
								j = j+1;
								k = k + ellipsizeStr.length() - 1;
							}	
						}
					}else if(ellipsizeStr.endsWith("...")){
						if(null == mMessageLang || mMessageLang.isEmpty()){
							mMessageListForDisplay.add(j, ellipsizeStr.substring(0, ellipsizeStr.length() - 3));
							j = j+1;
							k = k + ellipsizeStr.length() - 3;
						}else{
							if(mMessageLang.equals("en")){//对英语把单词拆开两行显示的问题做处理
								int m = ellipsizeStr.length() - 3;
								if(sb.charAt(k + ellipsizeStr.length() -1) == 32){
									m = ellipsizeStr.length() - 3;
									mMessageListForDisplay.add(j, ellipsizeStr.substring(0, m));
								}else{
									boolean flag = false;
									for(m=ellipsizeStr.length() - 4; m>0; m--){
										if(ellipsizeStr.charAt(m) == 32){
											flag = true;
											break;
										}
									}
									if(flag){
										mMessageListForDisplay.add(j, ellipsizeStr.substring(0, m));
									}else{
										m = ellipsizeStr.length() - 3;
										mMessageListForDisplay.add(j, ellipsizeStr.substring(0, m));
									}
								}
								j = j+1;
								k = k + m;
							}else{
								mMessageListForDisplay.add(j, ellipsizeStr.substring(0, ellipsizeStr.length() - 3));
								j = j+1;
								k = k + ellipsizeStr.length() - 3;
							}
						}
					}else{
						mMessageListForDisplay.add(j, ellipsizeStr.substring(0, ellipsizeStr.length()));
						j = j+1;
						k = k + ellipsizeStr.length();
					}
				}
			}else{
				mMessageListForDisplay.add(j, mMessageList.get(i));
				j = j +1;
			}
		}
		mTotalDisplayMsgNum = mMessageListForDisplay.size();
	}
	/**
	 * 
	 * @Title: showNextMessage
	 * @Description: 显示下一条消息
	 * @return: void
	 */
	private void showNextMessage(){
		Log.v("Status", "showNextMessage!!");
		boolean mMsgStop = false;
        if (iv_header_message_content.isFlipping()) {
            return;
        }

        int child = iv_header_message_content.getDisplayedChild();
        int childNext = (child + 1) % 2;
        mCurMessage++;
        if(mCurMessage == mTotalDisplayMsgNum){
        	mRepeatedly = mRepeatedly + 1;
        	mCurMessage = 0;
        	if(mRepeatedly == mMessageRepeateCount){
        		mHandler.sendEmptyMessage(UPGRADE_DISAPPEAR_MESSAGE);
        		mMsgStop = true;
        	}
        }
        if(!mMsgStop){
	        ((TextView)iv_header_message_content.getChildAt(childNext)).setText(mMessageListForDisplay.get(mCurMessage).toString());
	        loadMessageAnimation();
	        startMessageAnimation();
        }
	}
	/**
	 * 
	 * @Title: updateTime
	 * @Description: 更新状态栏显示时间
	 * @return: void
	 */
	public void updateTime() {
		final Calendar c = Calendar.getInstance();
		c.setTimeZone(TimeZone.getTimeZone("GMT" + settingManager.getTimeZone()));
		int hour = c.get(Calendar.HOUR_OF_DAY);
		int minute = c.get(Calendar.MINUTE);
		String str_hour = "", str_min = "";
		if (Integer.toString(hour).length() == 1) {
			str_hour = "0" + Integer.toString(hour);
		} else {
			str_hour = Integer.toString(hour);
		}
		if (Integer.toString(minute).length() == 1) {
			str_min = "0" + Integer.toString(minute);
		} else {
			str_min = Integer.toString(minute);
		}
		mTextClock.setText(str_hour + ":" + str_min);
	}
	/**
	 * 
	 * @Title: loadMessageAnimation
	 * @Description: 加载消息状态栏动画
	 * @return: void 
	 */
    public void loadMessageAnimation() {
        if (null == mInAnimPushUpMsg) {
            mInAnimPushUpMsg = createAnimation(0, 0, 1.0f, 0, 0, 1.0f,
                    ANIMATION_TIME_MSG);
            mOutAnimPushUpMsg = createAnimation(0, 0, 0, -1.0f, 1.0f, 0,
                    ANIMATION_TIME_MSG);
        }
        mInAnimInUseMsg = mInAnimPushUpMsg;
        mOutAnimInUseMsg = mOutAnimPushUpMsg;

        mInAnimInUseMsg.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationRepeat(Animation arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationEnd(Animation arg0) {
				// TODO Auto-generated method stub
				
			}
		});
        iv_header_message_content.setInAnimation(mInAnimInUseMsg);
        iv_header_message_content.setOutAnimation(mOutAnimInUseMsg);
    }
	/**
	 * 
	 * @Title: startMessageAnimation
	 * @Description: 启动message动画
	 * @return: void 
	 */
    private void startMessageAnimation() {
        iv_header_message_content.showNext();
    }
	/**
	 * 
	 * @Title: createAnimation
	 * @Description: 创建动画
	 * @return: void 
	 */
    private Animation createAnimation(float xFrom, float xTo, float yFrom,
            float yTo, float alphaFrom, float alphaTo, long duration) {
        AnimationSet animSet = new AnimationSet(getContext(), null);
        Animation trans = new TranslateAnimation(Animation.RELATIVE_TO_SELF,
                xFrom, Animation.RELATIVE_TO_SELF, xTo,
                Animation.RELATIVE_TO_SELF, yFrom, Animation.RELATIVE_TO_SELF,
                yTo);
        Animation alpha = new AlphaAnimation(alphaFrom, alphaTo);
        animSet.addAnimation(trans);
        animSet.addAnimation(alpha);
        animSet.setDuration(duration);
        return animSet;
    }
    
	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case UPDATE_TIME:
				updateTime();
				break;
			case UPDATE_AUTH_SUCCESS:
				iv_header_login.setImageResource(R.drawable.status_icon_login1_1);
				break;
			case UPDATE_AUTH_FAILED:
				iv_header_login.setImageResource(R.drawable.status_icon_login1_2);
				break;
			case UPDATE_MSG_TEXT:
				initMessageList();
				break;
			case UPDATE_MESSAGE_CHANGE:
				showNextMessage();
				break;
			case UPGRADE_DISAPPEAR_MESSAGE:
				destroyMessage();
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}
	};
	
	private void destroyMessage(){
		iv_header_message_content.stopFlipping();
		iv_header_message_content.getCurrentView().setVisibility(View.INVISIBLE);
		iv_header_speaker.setVisibility(View.INVISIBLE);
    	if(mMsgTimer != null){
    		mMsgTimer.shutdownNow(); 
    		mMsgTimer = null;
    	}
	}
	
	class TimeTask implements Runnable {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {
				mHandler.sendEmptyMessage(UPDATE_TIME);
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
	}

	/**
	 * 
	 * @Title: StatusWifiSignalBroadcastReceiver
	 * @Description: wifi广播
	 * @return: void
	 */
	private class StatusWifiSignalBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equalsIgnoreCase(
					"android.net.conn.CONNECTIVITY_CHANGE")) {
				UpdateNetInfo();
			} else if (intent.getAction().equalsIgnoreCase(
					"android.net.wifi.WIFI_STATE_CHANGED")) {
				UpdateNetInfo();
			} else if (intent.getAction().equalsIgnoreCase(
					"android.net.wifi.STATE_CHANGE")) {
				UpdateNetInfo();
			} else if (intent.getAction().equalsIgnoreCase(
					WifiManager.RSSI_CHANGED_ACTION)) {
				UpdateWifiSignal();
			}
		}
	}

	/**
	 * 
	 * @Title: StatusEthernetBroadcastReceiver
	 * @Description: 有线网络广播
	 * @return: void
	 */
	private class StatusEthernetBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equalsIgnoreCase(
					"android.net.ethernet.ETH_STATE_CHANGED")) {
				UpdateNetInfo();
			}
		}
	}
	public class StatusMarqueeBroadcastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if (intent.getAction().equalsIgnoreCase(ACTION_STATUS_MARQUEE)) {
				Log.v("Status", "recive:" + "Marquee broadcast!");
				mMessageList.clear();
				mMessageList = intent.getStringArrayListExtra("title_msg");
				mMessageLang = intent.getStringExtra("lang");
				Log.v("Status", "Msg size:" + mMessageList.size());
				for(int i=0; i<mMessageList.size(); i++){
					Log.v("Status", "Marquee " + i + ":" + mMessageList.get(i));
				}
				if(mMessageList.size() == 0){
					Message msg = mHandler.obtainMessage(UPGRADE_DISAPPEAR_MESSAGE);
					mHandler.sendMessage(msg);
				}else{
					Message msg = mHandler.obtainMessage(UPDATE_MSG_TEXT);
					mHandler.sendMessage(msg);
				}
			}
		}
	}
	/**
	 * 
	 * @Title: StatusAuthBroadcastReceiver
	 * @Description: 鉴权广播
	 * @return: void
	 */
	public class StatusAuthBroadcastReceiver extends BroadcastReceiver {

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * android.content.BroadcastReceiver#onReceive(android.content.Context,
		 * android.content.Intent)
		 */
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if (intent.getStringExtra("AUTH_STATUS").equals("101")) {
				Log.v("Status", "recive:" + "auth broadcast!");
				Message msg = mHandler.obtainMessage(UPDATE_AUTH_SUCCESS);
				mHandler.sendMessage(msg);
			} else if (intent.getStringExtra("AUTH_STATUS").equals("102")
					|| intent.getStringExtra("AUTH_STATUS").equals("103")
					|| intent.getStringExtra("AUTH_STATUS").equals("104")
					|| intent.getStringExtra("AUTH_STATUS").equals("105")) {
				Message msg = mHandler.obtainMessage(UPDATE_AUTH_FAILED);
				mHandler.sendMessage(msg);
			}
		}
	}

	/**
	 * 
	 * @Title: initStoragy
	 * @Description: 初始化sd卡和u盘图标
	 * @return: void
	 */
	private void initStoragy() {
		File file_sd = new File(SDCARD_PATH);
		long filespace_sd = file_sd.getTotalSpace();
		File[] files_sd = file_sd.listFiles();
		if (files_sd != null && filespace_sd > 0) {
			iv_header_tf_logo.setVisibility(View.VISIBLE);
			iv_header_tf_logo.setImageResource(R.drawable.status_icon_tfcard1_1);
		} else {
			iv_header_tf_logo.setVisibility(View.GONE);
		}
		
		File file_usb0 = new File(USB_PATH0);
		File file_usb1 = new File(USB_PATH1);
		File file_usb2 = new File(USB_PATH2);
		File file_usb3 = new File(USB_PATH3);
		File file_usb4 = new File(USB_PATH4);
		File file_usb5 = new File(USB_PATH5);
		File file_usb6 = new File(USB_PATH6);
		File file_usb7 = new File(USB_PATH7);
		File file_usb8 = new File(USB_PATH8);
		File file_usb9 = new File(USB_PATH9);
		File file_usb10 = new File(USB_PATH10);
		File file_usb11 = new File(USB_PATH11);
		File file_usb12 = new File(USB_PATH12);
		File file_usb13 = new File(USB_PATH13);
		File file_usb14 = new File(USB_PATH14);
		long filespace_usb0 = file_usb0.getTotalSpace();
		long filespace_usb1 = file_usb1.getTotalSpace();
		long filespace_usb2 = file_usb2.getTotalSpace();
		long filespace_usb3 = file_usb3.getTotalSpace();
		long filespace_usb4 = file_usb4.getTotalSpace();
		long filespace_usb5 = file_usb5.getTotalSpace();
		long filespace_usb6 = file_usb6.getTotalSpace();
		long filespace_usb7 = file_usb7.getTotalSpace();
		long filespace_usb8 = file_usb8.getTotalSpace();
		long filespace_usb9 = file_usb9.getTotalSpace();
		long filespace_usb10 = file_usb10.getTotalSpace();
		long filespace_usb11 = file_usb11.getTotalSpace();
		long filespace_usb12 = file_usb12.getTotalSpace();
		long filespace_usb13 = file_usb13.getTotalSpace();
		long filespace_usb14 = file_usb14.getTotalSpace();
		File[] files_usb0 = file_usb0.listFiles();
		File[] files_usb1 = file_usb1.listFiles();
		File[] files_usb2 = file_usb2.listFiles();
		File[] files_usb3 = file_usb3.listFiles();
		File[] files_usb4 = file_usb4.listFiles();
		File[] files_usb5 = file_usb5.listFiles();
		File[] files_usb6 = file_usb6.listFiles();
		File[] files_usb7 = file_usb7.listFiles();
		File[] files_usb8 = file_usb8.listFiles();
		File[] files_usb9 = file_usb9.listFiles();
		File[] files_usb10 = file_usb10.listFiles();
		File[] files_usb11 = file_usb11.listFiles();
		File[] files_usb12 = file_usb12.listFiles();
		File[] files_usb13 = file_usb13.listFiles();
		File[] files_usb14 = file_usb14.listFiles();

		if ((files_usb0 != null && filespace_usb0 > 0) || (files_usb1 != null && filespace_usb1 > 0) || (files_usb2 != null && filespace_usb2 > 0) || 
				(files_usb3 != null && filespace_usb3 > 0) || (files_usb4 != null && filespace_usb4 > 0) || (files_usb5 != null && filespace_usb5 > 0) || 
				(files_usb6 != null && filespace_usb6 > 0) || (files_usb7 != null && filespace_usb7 > 0) || (files_usb8 != null && filespace_usb8 > 0) || 
				(files_usb9 != null && filespace_usb9 > 0) || (files_usb10 != null && filespace_usb10 > 0) || (files_usb11 != null && filespace_usb11 > 0) || 
				(files_usb12 != null && filespace_usb12 > 0) || (files_usb13 != null && filespace_usb13 > 0) || (files_usb14 != null && filespace_usb14 > 0)) {
			iv_header_usb_logo.setVisibility(View.VISIBLE);
			iv_header_usb_logo.setImageResource(R.drawable.status_icon_usb1_1);
		} else {
			iv_header_usb_logo.setVisibility(View.GONE);
		}
	}
	
	/**
	 * 
	 * @Title: UpdateWifiSignal
	 * @Description: 更新wifi强弱信号
	 * @return: void
	 */
	private void UpdateWifiSignal() {
		if (isNetConnected(mContext)) {
			iv_header_wifi_logo.setVisibility(View.VISIBLE);
			iv_header_wifi_logo
					.setImageResource(SINGAL_LEVEL[getWifiLevel(mContext)]);
		} else {
			iv_header_wifi_logo.setVisibility(View.GONE);
		}
	}

	/**
	 * 
	 * @Title: UpdateNetInfo
	 * @Description: 更新网络图标
	 * @return: void
	 */
	private void UpdateNetInfo() {
		if(isNetAvailable(mContext)){
			if (isNetConnected(mContext)) {
				if (getNetType(mContext) == 1) {
					iv_header_adsl_logo.setImageResource(R.drawable.status_icon_cable1_1);
					iv_header_adsl_logo.setVisibility(View.VISIBLE);
					iv_header_wifi_logo.setVisibility(View.GONE);
				} else {
					iv_header_adsl_logo.setImageResource(R.drawable.status_icon_cable1_2);
					iv_header_adsl_logo.setVisibility(View.GONE);
					iv_header_wifi_logo.setVisibility(View.VISIBLE);
					UpdateWifiSignal();
				}
			}else{
				iv_header_adsl_logo.setImageResource(R.drawable.status_icon_cable1_3);
				iv_header_adsl_logo.setVisibility(View.VISIBLE);
				iv_header_wifi_logo.setVisibility(View.GONE);
			}
		}else{
			iv_header_adsl_logo.setImageResource(R.drawable.status_icon_cable1_2);
			iv_header_adsl_logo.setVisibility(View.VISIBLE);
			iv_header_wifi_logo.setVisibility(View.GONE);
		}
	}

	/**
	 * 
	 * @Title: isNetConnected
	 * @Description: 网络是否链接
	 * @return: boolean
	 */
	public static boolean isNetConnected(Context context) {
		ConnectivityManager conManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = conManager.getActiveNetworkInfo();
		if (networkInfo != null) {
			return networkInfo.isConnected();
		}
		return false;
	}

	/**
	 * 
	 * @Title: isNetAvailable
	 * @Description: 网络是否链接
	 * @return: boolean
	 */
	public static boolean isNetAvailable(Context context) {
		ConnectivityManager conManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = conManager.getActiveNetworkInfo();
		if (networkInfo != null) {
			return networkInfo.isAvailable();
		}
		return false;
	}
	
	/**
	 * 
	 * @Title: getNetType
	 * @Description: 获取网络类型
	 * @return: int，0没网络 1网线 2是wifi
	 */
	public static int getNetType(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
		//activeNetInfo.getType();
		if (activeNetInfo != null) {
			if (TextUtils.isEmpty((activeNetInfo.getExtraInfo()))) {
				return 1;
			} else {
				return 2;
			}
		}
		return 0;
	}

	/**
	 * 
	 * @Title: getWifiLevel
	 * @Description: 获取wifi信号强度
	 * @return: int
	 */
	public static int getWifiLevel(Context context) {
		int strength = 0;
		WifiManager wifiManager = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = wifiManager.getConnectionInfo();
		if (info.getBSSID() != null) {
			strength = WifiManager.calculateSignalLevel(info.getRssi(), 4);
		}
		return strength;
	}

	public static boolean isUSBMounted(Context context) {
		StorageManager mStorageManager = (StorageManager) context
				.getSystemService(Context.STORAGE_SERVICE);
		StorageVolume[] storageVolumes = mStorageManager.getVolumeList();
		int length = storageVolumes.length;
		for (int i = 0; i < length; i++) {
			if (USB_PATH1.equals(storageVolumes[i].getPath())) {
				if (mStorageManager.getVolumeState(storageVolumes[i].getPath())
						.equals("mounted")) {
					return true;
				}
			}
			if (USB_PATH2.equals(storageVolumes[i].getPath())) {
				if (mStorageManager.getVolumeState(storageVolumes[i].getPath())
						.equals("mounted")) {
					return true;
				}
			}
		}
		return false;
	}
}
