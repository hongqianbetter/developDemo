package net.hongqianfly.myview;

import android.support.annotation.StringRes;

/**
 * Created by HongQian.Wang on 2017/11/7.
 */

public interface TriggerView {
    /**
     * 没有数据显示空布局,隐藏当前显示布局
     */
    void triggerEmpty();

    /**
     * 网络错误
     */
    void triggerNetError();

    /**
     * 加载错误,并显示错误信息
     */
    void triggerError(@StringRes int strRes);


    /**
     * 显示正在加载状态
     */

    void triggerLoading();

    /**
     * 数据加载成功,调用该方法时应该隐藏当前占位布局
     */

    void triggerOk();

    /**
     * 该方法如果传入的isOk为true则为成功状态
     * 此时隐藏布局,反之显示空布局
     */

    void triggerOkOrEmpty(boolean isOk);
}
