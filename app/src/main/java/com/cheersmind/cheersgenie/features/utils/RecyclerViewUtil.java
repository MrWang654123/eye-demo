package com.cheersmind.cheersgenie.features.utils;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;

import java.lang.reflect.Field;

/**
 * RecyclerView工具类
 */
public class RecyclerViewUtil {

    /**
     * 设置RecyclerView的最大滑动速度（系统默认8000dp）
     * @param recyclerView
     * @param velocity
     */
    public static void setMaxFlingVelocity(@NonNull RecyclerView recyclerView, int velocity) {
        try {
            Field field = recyclerView.getClass().getDeclaredField("mMaxFlingVelocity");
            field.setAccessible(true);
            field.set(recyclerView, velocity);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
