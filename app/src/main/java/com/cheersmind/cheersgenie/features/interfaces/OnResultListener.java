package com.cheersmind.cheersgenie.features.interfaces;

/**
 * 返回结果监听
 */
public interface OnResultListener {

    //成功
    public void onSuccess(Object... objects);

    //失败
    public void onFailed(Object... objects);
}
