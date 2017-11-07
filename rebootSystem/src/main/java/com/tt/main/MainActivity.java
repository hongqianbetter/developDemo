package com.tt.main;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.tt.main.RebootService.LocalBinder;
import com.tt.senbbroadcasttest.R;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.os.Build;

public class MainActivity extends Activity  {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
	}



	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment implements OnClickListener{

		final String TAG_STRING="main_fragment";
		 RebootService rebootService=null;  
			Button btn1,btn2,btn3,btn4;
			EditText edt_off,edt_reboot,edt_on;
		 private  ServiceConnection mConnection = new ServiceConnection() {  
		 	@Override
		     public void onServiceConnected(ComponentName className,IBinder localBinder) { 
		 		Log.d(TAG_STRING, "==============onServiceConnected");
		 		rebootService = ((LocalBinder) localBinder).getService();  
		 		//serciceBinded=true;
		     }  
		 	@Override
		     public void onServiceDisconnected(ComponentName arg0) { 
		 		Log.d(TAG_STRING, "onServiceDisconnected");
		 		rebootService = null;  
		 	//	serciceBinded=false;
		     }
		  
		 };

		private boolean isUnBind(){
			return rebootService==null;
		}


		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);

			if(isUnBind()){
			Intent serviceIntent= new Intent(getActivity(), RebootService.class);
			Log.d(TAG_STRING, "after new serviceIntent");
			getActivity().bindService(serviceIntent, mConnection, Context.BIND_AUTO_CREATE); 
		
		}

			initView(rootView);
			return rootView;
		}
		
		void initView(View rootView){
			btn1=(Button)rootView.findViewById(R.id.btn1);
			btn2=(Button)rootView.findViewById(R.id.btn2);
			btn3=(Button)rootView.findViewById(R.id.btn3);
			btn4=(Button)rootView.findViewById(R.id.btn4);
			edt_off=(EditText)rootView.findViewById(R.id.edt_offTime);
			edt_on=(EditText)rootView.findViewById(R.id.edt_onTime);
			edt_reboot=(EditText)rootView.findViewById(R.id.edt_reboot);

			btn1.setOnClickListener(this);
			btn2.setOnClickListener(this);
			btn3.setOnClickListener(this);
			btn4.setOnClickListener(this);
			
			SimpleDateFormat    formatter    =   new    SimpleDateFormat    ("HH:mm:ss");
			long curtimeInMillis=System.currentTimeMillis();
			long futureTime=curtimeInMillis+1000*60;
			long onTime=curtimeInMillis+1000*60*3;
			Date    setDate    =   new  Date(futureTime);    
			String    time1   =formatter.format(setDate);  
			setDate    =   new  Date(onTime);
			String    time2   =formatter.format(setDate); 
			edt_off.setText(time1);
			edt_on.setText(time2);
			

			long offTime=curtimeInMillis+1000*90;
			Date    setDate2    =   new  Date(offTime);   
			String    time3   =formatter.format(setDate); 
			edt_reboot.setText(time3);
		}
		
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch(v.getId()){
			case R.id.btn1:
				sentBroadcast(1,"0","0",true);
				break;
			case R.id.btn2:
				sentBroadcast(2,"0","0",true);
				break;
			case R.id.btn3:{

				
				
				String    time1=edt_off.getText().toString();
				String    time2=edt_on.getText().toString();

					sentBroadcast(3,time1,time2,true);
				}
				break;
			case R.id.btn4:
			{
 
				String    time1=edt_reboot.getText().toString();
				sentBroadcast(4,time1,"",true);
			}
			
				break;
			}
		}
		
		private void sentBroadcast(int type,String shutTime,String rebootTime,boolean effective){
		 Intent intent = new Intent();
		 intent.setAction("android.intent.action.auto_power_shut");
		 intent.putExtra("power_type", type);
		 intent.putExtra("power_time",rebootTime);
		 intent.putExtra("shut_time", shutTime);
		 intent.putExtra("effective", effective);
		 Log.e("sendcast","--------sentBroadcast---------");
		 intent.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
		 getActivity().sendBroadcast(intent);
		}
	}



}
