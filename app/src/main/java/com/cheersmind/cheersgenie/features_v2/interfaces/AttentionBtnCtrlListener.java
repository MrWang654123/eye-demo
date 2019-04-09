package com.cheersmind.cheersgenie.features_v2.interfaces;

/**
 * 关注按钮显隐控制监听
 */
public interface AttentionBtnCtrlListener {
    /**
     * 控制状态
     * @param hasAttention true：已经关注
     */
    void ctrlStatus(boolean hasAttention);
}
