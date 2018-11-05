package com.cheersmind.cheersgenie.features.utils;

import android.content.Context;
import android.support.annotation.NonNull;

import com.cheersmind.cheersgenie.features.view.dialog.IntegralTipDialog;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * 积分工具
 */
public class IntegralUtil {

    /**
     * 积分提示弹窗
     * @param context 上下文
     * @param obj http通信返回结果对象
     */
    public static void showIntegralTipDialog(@NonNull Context context, Object obj,IntegralTipDialog.OnOperationListener listener) {
        try {
            if (obj != null && obj instanceof JSONObject) {
                JSONObject jsonObject = (JSONObject) obj;
                JSONArray tasks = jsonObject.getJSONArray("tasks");
                //非空
                if (tasks != null && tasks.length() > 0) {
                    JSONObject jsonObjPoint = tasks.getJSONObject(0);
                    //非空
                    if (jsonObjPoint != null) {
                        int point = jsonObjPoint.getInt("point");
                        //积分弹窗提示
                        if (point > 0) {
                            new IntegralTipDialog(context, point, listener).show();
                        }
                    }
                }
            }
        } catch (Exception e) {
        }
    }

}
