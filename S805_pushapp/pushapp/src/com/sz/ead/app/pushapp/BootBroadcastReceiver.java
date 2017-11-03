package com.sz.ead.app.pushapp;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootBroadcastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
			Log.i("BootBroadcastReceiver", "onReceive ACTION_BOOT_COMPLETED broadcast");
			Intent bootStartIntent=new Intent(context,PushService.class);
	        context.startService(bootStartIntent);
		
        }
	}


}


