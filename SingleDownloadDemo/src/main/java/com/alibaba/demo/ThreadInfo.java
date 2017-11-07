package com.alibaba.demo;

import java.io.Serializable;

/**
 * Created by Hongqian.wang on 2017/8/30.
 */

public class ThreadInfo implements Serializable{
    private int id;//线程的id
    private String url;//文件下载的地址  与FileInfo的url相同
    private int start;//下载的开始位置   适用于多线程分段下载同一个文件
    private int end;   //下载的结束位置
    private int finished;//下载完成的长度

    public ThreadInfo() {
    }
    public ThreadInfo(int id, String url, int start, int end, int finished) {
        this.id = id;
        this.url = url;
        this.start = start;
        this.end = end;
        this.finished = finished;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public int getFinished() {
        return finished;
    }

    public void setFinished(int finished) {
        this.finished = finished;
    }

    @Override
    public String toString() {
        return "ThreadInfo{" +
                "id=" + id +
                ", url='" + url + '\'' +
                ", start=" + start +
                ", end=" + end +
                ", finished=" + finished +
                '}';
    }
}
