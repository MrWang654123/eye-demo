package com.cheersmind.cheersgenie.features.utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import com.cheersmind.cheersgenie.BuildConfig;
import com.cheersmind.cheersgenie.main.util.JsonUtil;
import com.cheersmind.cheersgenie.main.util.ToastUtil;

import java.util.Map;

/**
 * 手机短信测试工具
 */
public class PhoneMessageTestUtil {

    /**
     * 提示显示短信验证码
     * @param activity
     * @param obj
     */
    public static void toastShowMessage(Activity activity, Object obj) {
        //非生产环境都用toast提示
        String hostType = BuildConfig.HOST_TYPE;
        if(!"product".equals(hostType)) {
            //测试显示短信验证码
            try {
                Map dataMap = JsonUtil.fromJson(obj.toString(), Map.class);
                String message = "短信验证码【" + dataMap.get("code").toString() + "】";
//                ToastUtil.showLong(activity, message);
                android.app.AlertDialog dialog = new android.app.AlertDialog.Builder(activity)
//                .setTitle("班级号")//设置对话框的标题
                        .setMessage(message)//设置对话框的内容
                        //设置对话框的按钮
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).create();
                dialog.setCanceledOnTouchOutside(true);
                dialog.setCancelable(true);
                dialog.show();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 提示显示短信验证码
     * @param activity
     * @param message
     */
    public static void toastShowMessage(Activity activity, String message) {
        //非生产环境都用toast提示
        String hostType = BuildConfig.HOST_TYPE;
        if(!"product".equals(hostType)) {
            //测试显示短信验证码
            try {
//                ToastUtil.showLong(activity, message);
                android.app.AlertDialog dialog = new android.app.AlertDialog.Builder(activity)
//                .setTitle("班级号")//设置对话框的标题
                        .setMessage(message)//设置对话框的内容
                        //设置对话框的按钮
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).create();
                dialog.setCanceledOnTouchOutside(true);
                dialog.setCancelable(true);
                dialog.show();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


}
