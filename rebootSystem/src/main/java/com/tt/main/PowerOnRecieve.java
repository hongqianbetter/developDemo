package com.tt.main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class PowerOnRecieve extends BroadcastReceiver {
final static String TAG_STRING="powerOnReceiver";

//boolean serciceBinded = false; 
 RebootService rebootService;  



	private boolean isUnBind(){
		return rebootService==null;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.e(TAG_STRING, "----onReceive---------------");
		Intent serviceIntent=new Intent(context,RebootService.class);
		context.startService(serviceIntent);
//		Intent activyIntent=new Intent(context,MainActivity.class);
//		activyIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//		context.startActivity(activyIntent);	
	}

 
}
