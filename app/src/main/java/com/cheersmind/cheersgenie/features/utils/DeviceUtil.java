package com.cheersmind.cheersgenie.features.utils;

import android.content.Context;
import android.os.Build;
import android.provider.Settings;

/**
 * 设备工具
 */
public class DeviceUtil {

    /**
     * 获取设备ID（AndroidId和Serial Number）
     * @param context
     * @return
     */
    public static String getDeviceId(Context context) {
        String androidID = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        String id = androidID + Build.SERIAL;

        return id;
    }

}
