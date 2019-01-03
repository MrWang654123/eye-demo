package com.cheersmind.cheersgenie.main.util;

/**
 * 点击工具
 * 1、点击太快判断
 */
public class RepetitionClickUtil {

    // 两次点击按钮之间的点击间隔时间
    private static final int MIN_CLICK_DELAY_TIME = 600;
    public static final int MIN_CLICK_DELAY_TIME_LONG = 800;
    private static long lastClickTime;

    /**
     * 是否点击太快
     * @return true：是
     */
    public static boolean isFastClick() {
        boolean flag = true;
        long curClickTime = System.currentTimeMillis();
        if ((curClickTime - lastClickTime) >= MIN_CLICK_DELAY_TIME) {
            flag = false;
        }
        lastClickTime = curClickTime;
        return flag;
    }

    /**
     * 是否点击太快
     * @return true：是
     */
    public static boolean isFastClickLong() {
        boolean flag = true;
        long curClickTime = System.currentTimeMillis();
        if ((curClickTime - lastClickTime) >= MIN_CLICK_DELAY_TIME_LONG) {
            flag = false;
        }
        lastClickTime = curClickTime;
        return flag;
    }

}
