package com.cheersmind.cheersgenie.features.utils;

import android.text.TextUtils;

/**
 * 图片工具
 */
public class ImageUtil {

    /**
     * 从图片url解析出图片名称
     * @param imageUrl
     * @return
     */
    public static String parseImageNameFromUrl(String imageUrl) {
        String imageName = "";

        try {
            if (!TextUtils.isEmpty(imageUrl)) {
                imageName = imageUrl.substring((imageUrl.lastIndexOf("/") + 1));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return imageName;
    }

}
