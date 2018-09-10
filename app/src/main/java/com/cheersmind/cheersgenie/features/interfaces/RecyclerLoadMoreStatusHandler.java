package com.cheersmind.cheersgenie.features.interfaces;

/**
 * recycler的加载更多的状态处理
 */
public interface RecyclerLoadMoreStatusHandler {

    /**
     * 全部数据已经加载结束
     */
    public void loadMoreEnd();

    /**
     * 本次加载完成
     */
    public void loadMoreComplete();

    /**
     * 加载失败
     */
    public void loadMoreFail();

}
