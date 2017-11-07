package com.tt.main;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AlarmReceiver extends BroadcastReceiver {
private String tAGString="AlarmReceiver";
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		
		String witch=intent.getStringExtra("witchAlarm");
		int cmd=intent.getIntExtra("type", 0);
		Log.e(tAGString, "AlarmReceiver-- type="+cmd+"on_or_off="+witch);
		if(witch.equals("off")){
			switch(cmd){
			case 3:
				shutSystem2(context);
				break;
			case 4:
				rebootSystem(context);
				break;
			}
		}else if(witch.equals("on")){
			switch(cmd){
			case 3:
				powerOn(context);
				break;
			}
		}
	}
	private void powerOn(Context context){
		
	}
	private void rebootSystem(Context context){
		 Intent reboot = new Intent(Intent.ACTION_REBOOT);
		 reboot.putExtra("nowait", 1);
		 reboot.putExtra("interval", 1);
		 reboot.putExtra("window", 0);
		 context.sendBroadcast(reboot);
		}
//		private void shutSystem(Context context){
//			 Intent shut = new Intent(Intent.ACTION_SHUTDOWN);
//			 shut.putExtra("nowait", 1);
//			 shut.putExtra("interval", 1);
//			 shut.putExtra("window", 0);
//			 context.sendBroadcast(shut);
//			}
		private void shutSystem2(Context context){
			 Intent intent = new Intent("android.intent.action.ACTION_REQUEST_SHUTDOWN");
			// 源码�?"android.intent.action.ACTION_REQUEST_SHUTDOWN�? 就是 Intent.ACTION_REQUEST_SHUTDOWN方法
			  intent.putExtra("android.intent.extra.KEY_CONFIRM", false);
			// 源码�?"android.intent.extra.KEY_CONFIRM"就是 Intent.EXTRA_KEY_CONFIRM方法
			  intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			  context.startActivity(intent);
		}
}