package com.tt.main;

import java.util.Calendar;
import java.util.Date;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.tt.senbbroadcasttest.R;
import com.tt.utils.SharePreferenceUtil;

public class RebootService extends Service {
	
final private String tAGString="--RebootService--";
final private String tag_offTime="shut_time";
final private String tag_onTime="power_time";
final private String tag_effective="effective";
final private String tag_type="type";
final private String alram_action="android.intent.action.bootAlarm";
final private int REQUEST_ON=1;
final private int REQUEST_OFF=2;
PowerManager powerManager = null;
WakeLock wakeLock = null;

Preview preview;
Camera camera;

WindowManager.LayoutParams wmParams;
WindowManager mWindowManager;
FrameLayout mFloatLayout;

private final IBinder binder =  new LocalBinder();
private static final int INTERVAL = 1000 * 60 * 60 * 24;// 24h
private int cmd=0;
private String power_time=null;
private String shut_time=null;
private Boolean effective=false;

RebootReceiver rebootReceiver;
AlarmReceiver alarmReceiver;
private SharePreferenceUtil saveUtils;
private AlarmManager powerOffAlarm=null;
private AlarmManager powerOnAlarm=null;
Handler mHandler;

//
public class LocalBinder extends Binder {
	
	RebootService getService() {  
        return RebootService.this;  
    }  
} 

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		Log.e(tAGString,"onBind");
		return binder;
	}

	void getHistroyState(){
		//读取保存在系统设置里面的参数
		cmd=saveUtils.getIntValue(tag_type);
		power_time=saveUtils.getStringValue(tag_onTime);
		shut_time=saveUtils.getStringValue(tag_offTime);
		effective=saveUtils.getBoolValue(tag_effective);
	}
	void saveState(int type,String onTime,String offTime,boolean effec){
		
		saveUtils.setIntValue(tag_type, type);
		saveUtils.setStringValue(tag_onTime, onTime);
		saveUtils.setStringValue(tag_offTime, offTime);
		saveUtils.setBoolValue(tag_effective, effec);
	}
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		Log.e(tAGString,"onCreate");
		rebootReceiver = new RebootReceiver();
		// 
		IntentFilter filter = new IntentFilter();
		filter.addAction("android.intent.action.auto_power_shut");
		registerReceiver(rebootReceiver, filter);
		
//		alarmReceiver=new AlarmReceiver();
//		IntentFilter filter2 = new IntentFilter();
//		filter2.addAction(alram_action);
//		registerReceiver(alarmReceiver,filter2);
		saveUtils=new SharePreferenceUtil(getApplicationContext(),"RebootSystem");
		getHistroyState();
		if(cmd>2)//开机只执行定时任务
		setCmd(cmd, power_time,shut_time,effective);
		
	    powerManager = (PowerManager)this.getSystemService(Context.POWER_SERVICE);
        wakeLock = this.powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, "My Lock");
        ScreenWakeup();
        try {
        	 createFloatView();
		} catch (Exception e) {
			// TODO: handle exception
		}
       
		super.onCreate();
	}

 	private void ScreenWakeup() {
			 wakeLock.acquire(); 
	}
 	private void ScreenRelease() {
		 wakeLock.release(); 
}
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
//		if(myTimer!=null){
//			myTimer.cancel();
//			myTimer=null;
//		}
		ScreenRelease();
		cancelAlarm();
		super.onDestroy();
		
	}

	@Override
	public void onRebind(Intent intent) {
		// TODO Auto-generated method stub
		super.onRebind(intent);
	}

	
	public void setCmd(int cmd,String power_time,String shut_time,boolean effective){
		this.cmd=cmd;
		this.power_time=power_time;
		this.shut_time=shut_time;
		this.effective=effective;
		
		//Log.e(tAGString, "cmd="+cmd+"power_time="+power_time+"shut_time="+shut_time);
		//myTimer=new Timer();
		Intent return_intent=new Intent();
		return_intent.setAction("android.intent.action.auto_power_shut.result");
		
		if(effective){
			switch(cmd){
			case 1:
				rebootSystem();
				return_intent.putExtra("is_success", true);
				break;
			case 2:
				shutSystem2();
				return_intent.putExtra("is_success", true);
				break;
			case 3:{
				boolean success=false;
				if(shut_time!=null&&power_time!=null){
					success=setAlarmPowerOff(shut_time);
					success&=setAlarmPowerOn(power_time);
				}
				if(success){
					return_intent.putExtra("is_success", true);
				}else{
					return_intent.putExtra("is_success", false);
					Intent err_intent=new Intent();
					err_intent.setAction("android.dcdz.action.auto_power_shut.error");
					err_intent.putExtra("errorCode", 2);
					err_intent.putExtra("errorString", "unknow time format, shut_time="+shut_time+"power_time="+power_time);
					sendBroadcast(err_intent);
				}
				break;
			 }
			case 4:{
				boolean success=false;
				if(shut_time!=null){
					success=setAlarmPowerOff(shut_time);
				}
				if(success){
					return_intent.putExtra("is_success", true);
				}else{
					return_intent.putExtra("is_success", false);
					Intent err_intent=new Intent();
					err_intent.setAction("android.dcdz.action.auto_power_shut.error");
					err_intent.putExtra("errorCode", 2);
					err_intent.putExtra("errorString", "unknow time format, shut_time="+shut_time);
					sendBroadcast(err_intent);
				}
				break;	
			  }
			default:
				return_intent.putExtra("is_success", false);
				Intent err_intent=new Intent();
				err_intent.setAction("android.dcdz.action.auto_power_shut.error");
				err_intent.putExtra("errorCode", 1);
				err_intent.putExtra("errorString", "unknow type, type="+cmd);
				sendBroadcast(err_intent);
				break;
	
			 }
	
	
		}else {
			cancelAlarm();
			return_intent.putExtra("is_success", true);
		}
		
		sendBroadcast(return_intent);
		
		
	}
	

	private void rebootSystem(){
	 Intent reboot = new Intent(Intent.ACTION_REBOOT);
	 reboot.putExtra("nowait", 1);
	 reboot.putExtra("interval", 1);
	 reboot.putExtra("window", 0);
	 sendBroadcast(reboot);
	}
//	private void shutSystem(){
//		 Intent shut = new Intent(Intent.ACTION_SHUTDOWN);
//		 shut.putExtra("nowait", 1);
//		 shut.putExtra("interval", 1);
//		 shut.putExtra("window", 0);
//		 sendBroadcast(shut);
//		}
	private void shutSystem2(){
		 Intent intent = new Intent("android.intent.action.ACTION_REQUEST_SHUTDOWN");
		// 源码�?"android.intent.action.ACTION_REQUEST_SHUTDOWN�? 就是 Intent.ACTION_REQUEST_SHUTDOWN方法
		  intent.putExtra("android.intent.extra.KEY_CONFIRM", false);
		// 源码�?"android.intent.extra.KEY_CONFIRM"就是 Intent.EXTRA_KEY_CONFIRM方法
		  intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		  startActivity(intent);
	}
	private void powerOn(){

		}
	
	void cancelAlarm(){
		if(powerOffAlarm!=null){
	          Intent intent = new Intent(this, AlarmReceiver.class);
	          PendingIntent sender = PendingIntent.getBroadcast(this,
	                  0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
			 powerOffAlarm.cancel(sender);
			 powerOffAlarm=null;
		}
		if(powerOnAlarm!=null){
	          Intent intent = new Intent(this, AlarmReceiver.class);
	          PendingIntent sender = PendingIntent.getBroadcast(this,
	                  0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
	          powerOnAlarm.cancel(sender);
	          powerOnAlarm=null;
		}
		
	}
	boolean setAlarmPowerOff(String time){
        // When the alarm goes off, we want to broadcast an Intent to our
        // BroadcastReceiver. Here we make an Intent with an explicit class
        // name to have our own receiver (which has been published in
        // AndroidManifest.xml) instantiated and called, and then create an
        // IntentSender to have the intent executed as a broadcast.
        // Note that unlike above, this IntentSender is configured to
        // allow itself to be sent multiple times.


	          Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);
	          intent.setAction(alram_action);
	          intent.putExtra("witchAlarm", "off");
	          intent.putExtra("type", cmd);
	          PendingIntent sender = PendingIntent.getBroadcast(getApplicationContext(),
	        		  REQUEST_OFF, intent, PendingIntent.FLAG_CANCEL_CURRENT);

	          // Schedule the alarm!
	           powerOffAlarm = (AlarmManager) getApplicationContext()
	                  .getSystemService(Context.ALARM_SERVICE);

	          Calendar calendar = Calendar.getInstance();
	          calendar.setTimeInMillis(System.currentTimeMillis());
	          Date nowDate=new Date(System.currentTimeMillis());
	          int nowHour=nowDate.getHours();
	          int nowMinute=nowDate.getMinutes();
	          int nowSecond=nowDate.getSeconds();
	          long setTimeLong=calendar.getTimeInMillis();;
	        //  calendar.setTime(date);
//	          if(time.contains(":")){
//	        	  int hour=new Integer(time.substring(0, time.indexOf(":")));
//	        	  int minute=new Integer(time.substring(time.indexOf(":")+1));
	          		int hour=0;
	          		int minute=0;
	          		int second=0;
	        	  String subTime[] =time.split(":");
	        	  if(subTime.length==0)
	        		  return false;
	        	  
	        	  if(subTime.length>0){
	        		   hour=new Integer(subTime[0]); 
	        	  }
	        	  if(subTime.length>1){
	        		  minute=new Integer(subTime[1]); 
	        	  }
	        	  if(subTime.length>2){
	        		  second=new Integer(subTime[2]); 
	        	  }
	        	  

		          Log.e(tAGString, "hour="+hour+"minute="+minute+"second="+second);
		          calendar.set(Calendar.HOUR_OF_DAY, hour);
		          calendar.set(Calendar.MINUTE, minute);
		          calendar.set(Calendar.SECOND, second);
		           setTimeLong=calendar.getTimeInMillis();
	        	  if(hour<nowHour){ //设置的时间在过去，就加上一天
	        		  setTimeLong+= INTERVAL;
	        	  }else if(hour==nowHour){
	        		  if(minute<nowMinute){
	        			  setTimeLong+= INTERVAL;
	        		  }else if(minute==nowMinute){
	        			  if(second <nowSecond){
	        				  setTimeLong+= INTERVAL;
	        			  }
	        		  }
	        	  }
		          powerOffAlarm.setRepeating(AlarmManager.RTC_WAKEUP, setTimeLong,INTERVAL, sender);  
		          return true;
	         // }
	          
	        //  return false;
	         // calendar.set(Calendar.SECOND, 10);
	         // calendar.set(Calendar.MILLISECOND, 0);

	}
	
	boolean setAlarmPowerOn(String time){

	          Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);
	          intent.setAction(alram_action);
	          intent.putExtra("witchAlarm", "on");
	          intent.putExtra("type", cmd);
	          PendingIntent sender = PendingIntent.getBroadcast(getApplicationContext(),
	                  REQUEST_ON, intent, PendingIntent.FLAG_CANCEL_CURRENT);
	          // Schedule the alarm!
	          AlarmManager powerOnAlarm = (AlarmManager) getApplicationContext()
	                  .getSystemService(Context.ALARM_SERVICE);
	          Calendar calendar = Calendar.getInstance();
	          calendar.setTimeInMillis(System.currentTimeMillis());
	          Date nowDate=new Date(System.currentTimeMillis());
	          int nowHour=nowDate.getHours();
	          int nowMinute=nowDate.getMinutes();
	          int nowSecond=nowDate.getSeconds();
	          long setTimeLong=calendar.getTimeInMillis();;
	        //  calendar.setTime(date);
//	          if(time.contains(":")){
//	        	  int hour=new Integer(time.substring(0, time.indexOf(":")));
//	        	  int minute=new Integer(time.substring(time.indexOf(":")+1));
	          		int hour=0;
	          		int minute=0;
	          		int second=0;
	        	  String subTime[] =time.split(":");
	        	  if(subTime.length==0)
	        		  return false;
	        	  
	        	  if(subTime.length>0){
	        		   hour=new Integer(subTime[0]); 
	        	  }
	        	  if(subTime.length>1){
	        		  minute=new Integer(subTime[1]); 
	        	  }
	        	  if(subTime.length>2){
	        		  second=new Integer(subTime[2]); 
	        	  }
	        	  

		          Log.e(tAGString, "hour="+hour+"minute="+minute+"second="+second);
		          calendar.set(Calendar.HOUR_OF_DAY, hour);
		          calendar.set(Calendar.MINUTE, minute);
		          calendar.set(Calendar.SECOND, second);
		           setTimeLong=calendar.getTimeInMillis();
	        	  if(hour<nowHour){ //设置的时间在过去，就加上一天
	        		  setTimeLong+= INTERVAL;
	        	  }else if(hour==nowHour){
	        		  if(minute<nowMinute){
	        			  setTimeLong+= INTERVAL;
	        		  }else if(minute==nowMinute){
	        			  if(second <nowSecond){
	        				  setTimeLong+= INTERVAL;
	        			  }
	        		  }
	        	  }
		          powerOnAlarm.setRepeating(AlarmManager.RTC_WAKEUP, setTimeLong,
		                  INTERVAL, sender);
		          return true;
	          
	         // return false;
	         // calendar.set(Calendar.SECOND, 10);
	         // calendar.set(Calendar.MILLISECOND, 0);

	}	
//	private class AlarmReceiver extends BroadcastReceiver {
//
//		@Override
//		public void onReceive(Context context, Intent intent) {
//			// TODO Auto-generated method stub
//			Log.e(tAGString, "AlarmReceiver--");
//			String witch=intent.getStringExtra("witchAlarm");
//			if(witch.equals("off")){
//				switch(cmd){
//				case 3:
//					shutSystem2();
//					break;
//				case 4:
//					rebootSystem();
//					break;
//				}
//			}else if(witch.equals("on")){
//				switch(cmd){
//				case 3:
//					powerOn();
//					break;
//				}
//			}
//		}
//	}

	
	
	private class RebootReceiver extends BroadcastReceiver {
		final static String TAG_STRING="RebootReceiver";

			@Override
			public void onReceive(Context context, Intent intent) {
				Log.e(TAG_STRING, "----onReceive---------------");
				String power_time=null;
				String shut_time=null;
				boolean effective=false;
				int type=0;
				if(intent.getExtras().containsKey("power_type"))
					type= (Integer) intent.getExtras().get("power_type");
				if(intent.getExtras().containsKey("power_time"))
					power_time=(String)intent.getExtras().get("power_time");
				if(intent.getExtras().containsKey("shut_time"))
					shut_time=(String)intent.getExtras().get("shut_time");
				if(intent.getExtras().containsKey("effective"))
					effective=(Boolean)intent.getExtras().get("effective");
				Log.e(TAG_STRING, "power_type="+type+"  power_time="+power_time+"effective="+effective);
				saveState(type,power_time,shut_time,effective);
				setCmd(type, power_time,shut_time,effective);

			}



			

		}
	

	
	private void createFloatView()
	{
		wmParams = new WindowManager.LayoutParams();
		mWindowManager = (WindowManager)getApplication().getSystemService(getApplication().WINDOW_SERVICE);
		wmParams.type = LayoutParams.TYPE_PHONE; 
        wmParams.format = PixelFormat.RGBA_8888; 
        wmParams.flags = 
//          LayoutParams.FLAG_NOT_TOUCH_MODAL |
          LayoutParams.FLAG_NOT_FOCUSABLE;
//          LayoutParams.FLAG_NOT_TOUCHABLE
         // ;

        wmParams.gravity = Gravity.BOTTOM ;//| Gravity.BOTTOM; ֵ
        wmParams.x = 0;
        wmParams.y = 0;
        wmParams.width = 5;
        wmParams.height =5;
        LayoutInflater inflater = LayoutInflater.from(getApplication());
        //��ȡ����������ͼ���ڲ���
        mFloatLayout =  (FrameLayout) inflater.inflate(R.layout.view_float, null);

        mWindowManager.addView(mFloatLayout, wmParams);

	
        
        
        
        preview = new Preview(this, (SurfaceView)mFloatLayout.findViewById(R.id.surfaceView));
		preview.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		((FrameLayout) mFloatLayout.findViewById(R.id.layout)).addView(preview);
     

      //  img1=(RoundAngleImageView)mFloatLayout.findViewById(R.id.img_1);
	     mFloatLayout.measure(View.MeasureSpec.makeMeasureSpec(0,
						View.MeasureSpec.UNSPECIFIED), View.MeasureSpec
						.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

        startCamera();
        mHandler=new Handler();
        mHandler.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				closeFloatView();
				
			}
		}, 3000);
        
	
	}

	void closeFloatView(){
		stopCamera();
		mWindowManager.removeView(mFloatLayout);
	}
	
	
 void startCamera() {
		int numCams = Camera.getNumberOfCameras();
		if(numCams > 0){
			try{
				camera = Camera.open(0);
				camera.startPreview();
				preview.setCamera(camera);
			} catch (RuntimeException ex){
				//Toast.makeText(ctx, getString(R.string.camera_not_found), Toast.LENGTH_LONG).show();
			}
		}
	}

 void stopCamera() {
		if(camera != null) {
			camera.stopPreview();
			preview.setCamera(null);
			camera.release();
			camera = null;
		}
	}

	private void resetCam() {
		camera.startPreview();
		preview.setCamera(camera);
	}
	

}
