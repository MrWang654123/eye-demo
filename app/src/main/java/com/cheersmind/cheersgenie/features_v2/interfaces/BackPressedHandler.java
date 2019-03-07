package com.cheersmind.cheersgenie.features_v2.interfaces;

/**
 * 回退键处理
 */
public interface BackPressedHandler {

    /**
     * 是否已经处理回退操作
     * @return true：回退已经被处理了
     */
    boolean hasHandlerBackPressed();

}
