package com.cheersmind.cheersgenie.features.interfaces;

/**
 * 搜索布局控制监听
 */
public interface SearchLayoutControlListener {
    //搜索布局控制
    void searchLayoutControl(boolean show);

    //隐藏软键盘和覆盖层
    void hideSoftInputAndOverlay();
}
