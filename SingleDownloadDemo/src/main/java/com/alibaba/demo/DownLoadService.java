package com.alibaba.demo;

import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownLoadService extends Service {
    public static final String TAG = "DownLoadService";
    public static final String ACTION_START = "ACTION_START";
    public static final String ACTION_STOP = "ACTION_STOP";
    public static final String ACTION_UPDATE = "ACTION_UPDATE";
    public static final String DOWNLOAD_PATH = Environment.getExternalStorageDirectory() + "/download_whq/";
    public static final int MSG_INIT = 0;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_INIT:
                    FileInfo what = (FileInfo) msg.obj;
                    task = new DownLoadTask(DownLoadService.this, what);
                    task.download();
                    break;
            }
        }
    };
    private DownLoadTask task;


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand()执行了");
        if (intent != null) {
            if (ACTION_START.equals(intent.getAction())) {
                FileInfo fileInfo = (FileInfo) intent.getSerializableExtra("fileInfo");
                Log.e(TAG, fileInfo.toString());
                new InitThread(fileInfo).start();
            } else if (ACTION_STOP.equals(intent.getAction())) {
                FileInfo fileInfo = (FileInfo) intent.getSerializableExtra("fileInfo");
                Log.e(TAG, fileInfo.toString());
                if (task != null) {//如果下载已经开始了
                    task.isPause = true;
                }
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

//    在本地创建文件 初始化文件的长度

    class InitThread extends Thread {
        private FileInfo fileInfo;

        public InitThread(FileInfo fileInfo) {
            this.fileInfo = fileInfo;
        }

        public void run() {
            HttpURLConnection conn = null;
            RandomAccessFile raf = null;
            try {
                URL url = new URL(fileInfo.getUrl());
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(5000);
                int length = -1;
                Log.e(TAG, "初始线程连接状态" + conn.getResponseCode());
                if (conn.getResponseCode() == 200) {
                    length = conn.getContentLength();
                    if (length <= 0) {
                        Log.e(TAG, "网络文件长度为0");
                        return;
                    }

//                    判断下载到sdcard的文件所在路径是否存在
                    File dir = new File(DOWNLOAD_PATH);
                    if (!dir.exists()) {
                        dir.mkdir();
                    }
                    fileInfo.setLength(length);
                    File file = new File(DOWNLOAD_PATH, fileInfo.getFileName());
                    raf = new RandomAccessFile(file, "rwd");
                    raf.setLength(length);
                    Log.e(TAG, file.length() + "");
                    Message message = mHandler.obtainMessage(MSG_INIT);
                    message.obj = fileInfo;
                    message.sendToTarget();
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                conn.disconnect();
                try {
                    raf.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }

}
