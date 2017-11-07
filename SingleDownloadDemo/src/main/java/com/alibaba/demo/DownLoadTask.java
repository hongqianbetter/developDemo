package com.alibaba.demo;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * Created by Hongqian.wang on 2017/8/27.
 */

public class DownLoadTask {
    private static final String TAG="DownLoadTask";
    private Context context;
    private FileInfo fileInfo;
    private ThreadDao dao;
    private int mFinished = 0;
    public boolean isPause;

    public DownLoadTask(Context context, FileInfo mFileInfo) {
        this.context = context;
        this.fileInfo = mFileInfo;
        dao = new ThreadDaoImpl(context);
    }

    public void download(){
//        读取数据库的线程信息
        List<ThreadInfo> threadInfos = dao.queryThreads(fileInfo.getUrl());

        ThreadInfo threadInfo=null;
        if(threadInfos.size()==0) {
//                  初始化线程信息对象
            threadInfo=new ThreadInfo(0,fileInfo.getUrl(),0,fileInfo.getLength(),0);
        }else {
            threadInfo = threadInfos.get(0);
        }
        new DownLoadThread(threadInfo).start();
    }




    class DownLoadThread extends Thread {
        private ThreadInfo threadInfo;

        public DownLoadThread(ThreadInfo threadInfo) {
            this.threadInfo = threadInfo;
        }

        @Override
        public void run() {
//            向数据库插入线程信息
            if (!dao.isExist(threadInfo.getUrl(), threadInfo.getId())) {
                dao.insertThread(threadInfo);
            }

            HttpURLConnection conn = null;
            URL url = null;
            InputStream inputStream=null;
            RandomAccessFile raf=null;
//            设置下载位置
            try {
                url = new URL(threadInfo.getUrl());
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(3000);
                int start = threadInfo.getStart() + threadInfo.getFinished();
                conn.setRequestProperty("Range", "bytes=" + start + "-" + threadInfo.getEnd());

                //            设置文件的写入位置 文件已经存在，拿到此对象的引用
                File file = new File(DownLoadService.DOWNLOAD_PATH, fileInfo.getFileName());
                raf=new RandomAccessFile(file,"rwd");
                raf.seek(start);

                mFinished += threadInfo.getFinished();//个人认为等同于 mFinished=threadInfo.getFinished();
                if (conn.getResponseCode() ==206) { //设置range参数 返回状态码为206
//                    读取数据
                     inputStream = conn.getInputStream();
                    byte[] buffer = new byte[1024 * 4];
                    int len = -1;
                    long time = System.currentTimeMillis();
                    while ((len = inputStream.read(buffer)) !=0) {
                        raf.write(buffer, 0, len);
//                        把下载进度发送给Activity
                        mFinished += len;

                        if (System.currentTimeMillis() - time > 500) {
                            time = 0;
                            Intent intent = new Intent(DownLoadService.ACTION_UPDATE);
                            int i = mFinished * 100 / fileInfo.getLength();
                            intent.putExtra("finished", i);
                            context.sendBroadcast(intent);
                        }

                        if (isPause) {
                            dao.updateThread(threadInfo.getId(), threadInfo.getUrl(), mFinished);
                            return;
                        }

                    }

//                    下载完成后删除线程信息
                    dao.deleteThread(threadInfo.getId(),threadInfo.getUrl());
                }

            } catch (Exception e) {
                e.printStackTrace();
            }finally {

                try {
                    conn.disconnect();
                    inputStream.close();
                    raf.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }


        }
    }
}
