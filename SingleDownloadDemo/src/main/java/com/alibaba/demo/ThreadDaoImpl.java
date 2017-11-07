package com.alibaba.demo;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Hongqian.wang on 2017/8/27.
 */

public class ThreadDaoImpl implements ThreadDao {

    private DBHelper dbHelper;

    public ThreadDaoImpl(Context context) {
        dbHelper = new DBHelper(context);
    }

    @Override
    public void insertThread(ThreadInfo threadInfo) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        database.execSQL("insert into thread_info(thread_id,url,start,end,finished)" +
                "values(?,?,?,?,?)", new Object[]{threadInfo.getId(), threadInfo.getUrl()
                , threadInfo.getStart()
                , threadInfo.getEnd(), threadInfo.getFinished()});
        database.close();
    }

    @Override
    public void deleteThread(int id, String url) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        database.execSQL("delete from thread_info where url = ？and thread_id = ？",
                new Object[]{url,id});
        database.close();
    }

    @Override
    public void updateThread(int id, String url, int finished) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        database.execSQL("update  thread_info set finished = ? where url = ? and thread_id = ?",
                new Object[]{finished,url,id});
        database.close();
    }

    @Override
    public List<ThreadInfo> queryThreads(String url) {
        ArrayList<ThreadInfo> list = new ArrayList<>();
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        Cursor cursor = database.rawQuery("select * from thread_info where url = ?",
                new String[]{url});
        while (cursor.moveToNext()) {
            ThreadInfo thread=new ThreadInfo();
            thread.setId(cursor.getInt(cursor.getColumnIndex("thread_id")));
            thread.setUrl(cursor.getString(cursor.getColumnIndex("url")));
            thread.setStart(cursor.getInt(cursor.getColumnIndex("start")));
            thread.setEnd(cursor.getInt(cursor.getColumnIndex("end")));
            thread.setFinished(cursor.getInt(cursor.getColumnIndex("finished")));
            list.add(thread);
        }
           cursor.close();
        database.close();
        return list;
    }

    @Override
    public boolean isExist(String url, int thread_id) {
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        Cursor cursor = database.rawQuery("select *  from thread_info where url = ? and thread_id = ?",
                new String[]{url,thread_id+""});
        boolean isExist = cursor.moveToNext();
        cursor.close();
        database.close();
        return isExist;
    }
}
