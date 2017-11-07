package com.alibaba.demo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    private Button btn_stop;
    private Button btn_start;
    private ProgressBar progressBar;
    public static final String url = "http://10.3.102.55:8080/DownLoadDemo/2.mp3";
    private FileInfo fileInfo;
    private BroadcastReceiver receiver;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_stop = (Button) findViewById(R.id.btn_stop);
        btn_start = (Button) findViewById(R.id.btn_start);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setMax(100);


        btn_start.setOnClickListener(this);
        btn_stop.setOnClickListener(this);
        //初始化文件对象，包括url和fileName,除了id 其余设为为0
        fileInfo = new FileInfo(0, url, "黄金时代", 0, 0);

        receiver = new BroadcastReceiver() {

                @Override
                public void onReceive(Context context, Intent intent) {
                    if(intent.getAction().equals(DownLoadService.ACTION_UPDATE)) {
                        int finished =intent.getIntExtra("finished", 0);
                        progressBar.setProgress(finished);
                    }
                }
            };

        IntentFilter filter = new IntentFilter();
        filter.addAction(DownLoadService.ACTION_UPDATE);
        registerReceiver(receiver,filter);

    }

    @Override
    public void onClick(View view) {
        intent = new Intent(MainActivity.this, DownLoadService.class);
        if (view.getId() == R.id.btn_start) {
            intent.setAction(DownLoadService.ACTION_START);
        } else if (view.getId() == R.id.btn_stop) {
            intent.setAction(DownLoadService.ACTION_STOP);
        }
        intent.putExtra("fileInfo",fileInfo);
        startService(intent);
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(receiver);
//        stopService(intent);
        super.onDestroy();
        Log.e("MainActivity","onDestory()执行了");
    }
}
