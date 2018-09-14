package com.cheersmind.cheersgenie.features.utils;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import com.cheersmind.cheersgenie.main.util.JsonUtil;
import com.cheersmind.cheersgenie.main.util.ToastUtil;

import java.util.Map;

/**
 * 手机短信测试工具
 */
public class PhoneMessageTestUtil {

    /**
     * 提示显示短信验证码
     * @param context
     * @param obj
     */
    public static void toastShowMessage(Context context, Object obj) {
        //测试显示短信验证码
        try {
            Map dataMap = JsonUtil.fromJson(obj.toString(), Map.class);
            String message = "短信验证码【" + dataMap.get("code").toString()  +"】";
            ToastUtil.showLong(context, message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
