package com.cheersmind.cheersgenie.features.utils;

import android.content.Context;
import android.support.annotation.NonNull;

import com.cheersmind.cheersgenie.features.constant.Dictionary;
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
    public static IntegralTipDialog buildIntegralTipDialog(@NonNull Context context, Object obj,IntegralTipDialog.OnOperationListener listener) {
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
                            return new IntegralTipDialog(context, point, listener);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 积分提示弹窗V2
     * @param context 上下文
     * @param obj http通信返回结果对象
     */
    public static IntegralTipDialog buildIntegralTipDialogV2(@NonNull Context context, Object obj,IntegralTipDialog.OnOperationListener listener) {
        try {
            if (obj != null && obj instanceof JSONObject) {
                JSONObject jsonObject = (JSONObject) obj;
                JSONArray tasks = jsonObject.getJSONArray("task_response");
                //非空
                if (tasks != null && tasks.length() > 0) {
                    JSONObject jsonObjPoint = tasks.getJSONObject(0);
                    //非空
                    if (jsonObjPoint != null) {
                        int awardType = jsonObjPoint.getInt("reward_type");
                        //积分类型
                        if (awardType == Dictionary.AWARD_TYPE_POINT) {
                            int point = jsonObjPoint.getInt("reward_value");
                            //积分弹窗提示
                            if (point > 0) {
                                return new IntegralTipDialog(context, point, listener);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

}
