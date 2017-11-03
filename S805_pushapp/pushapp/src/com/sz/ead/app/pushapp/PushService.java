package com.sz.ead.app.pushapp;

import java.util.ArrayList;
import java.util.List;

import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;

import android.app.Service;  
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;  
import android.content.IntentFilter;
import android.os.IBinder;  
import android.setting.settingservice.SettingManager;
import android.util.Log;   
  
public class PushService extends Service{     
  
    public void onCreate(){     
        super.onCreate();   
        
        PushManager.startWork(getApplicationContext(),
                PushConstants.LOGIN_TYPE_API_KEY,
                Utils.getMetaValue(this, "api_key"));
        
        /*SettingManager setting; 
        String tag = "";
        try
		{
			setting = ((SettingManager)this.getSystemService(Context.SETTING_SERVICE));
			tag = setting.getDevName();
		}
		catch (Error e)
		{
			e.printStackTrace();
		}
        
        Log.d("PushService", tag);
        
        setTags(tag);*/
    }     
         
    public IBinder onBind(Intent intent){    
        return null;     
    }    
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        //unregisterReceiver(mBroadcastReceiver);
    }
 // 设置标签
    private void setTags(String tag) {
        List<String> tags = new ArrayList<String>();
        tags.add(tag);
        PushManager.setTags(this, tags);
    }
} 


