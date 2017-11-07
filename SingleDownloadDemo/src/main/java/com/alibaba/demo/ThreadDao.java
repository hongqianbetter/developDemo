package com.alibaba.demo;

import java.util.List;

/**
 * Created by Hongqian.wang on 2017/8/27.
 * 数据访问接口
 */

public interface ThreadDao {
    /**
     * 插入线程信息
     *
     * @param threadInfo
     */
    public void insertThread(ThreadInfo threadInfo);

    /**
     * 删除线程信息
     *
     * @param id
     * @param url
     * 多线程下载时 同一个文件有多个线程分段下载,根据id和url
     */
    public void deleteThread(int id, String url);

    /**
     * 更新线程下载进度
     * 多线程下载时 同一个文件有多个线程分段下载,根据id和url
     */

    public void updateThread(int id, String url, int finished);

    /**
     * 查询线程信息
     */
   public List<ThreadInfo> queryThreads(String url);

    /**
     * 判断线程是否存在
     * @param url
     * @param thread_id
     * @return
     */
    public boolean isExist(String url, int thread_id);

}