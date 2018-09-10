package com.cheersmind.cheersgenie.features.utils;

import java.util.List;

/**
 * Arraylist工具
 */
public class ArrayListUtil {

    /**
     * 判断集合是否为空
     * @param list
     * @return
     */
    public static boolean isEmpty(List list) {
        if (list != null && list.size() > 0) {
            return false;
        }

        return true;
    }

    /**
     * 判断集合是否不为空
     * @param list
     * @return
     */
    public static boolean isNotEmpty(List list) {
        return !isEmpty(list);
    }

}
