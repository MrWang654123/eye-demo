package com.cheersmind.smartbrain.main.util;

import android.util.Log;

/**
 * Created by Administrator on 2018/7/3.
 */

public class LogUtils {

    public static boolean isDebug = true;

    private final static String APP_TAG = "SmartBrain_Log";

    /**
     * 获取相关数据:类名,方法名,行号等.用来定位行<br>
     * at cn.utils.MainActivity.onCreate(MainActivity.java:17) 就是用來定位行的代碼<br>
     *
     * @return [ Thread:main, at
     *         cn.utils.MainActivity.onCreate(MainActivity.java:17)]
     */
    private static String getFunctionName() {
        StackTraceElement[] sts = Thread.currentThread().getStackTrace();
        if (sts != null) {
            for (StackTraceElement st : sts) {
                if (st.isNativeMethod()) {
                    continue;
                }
                if (st.getClassName().equals(Thread.class.getName())) {
                    continue;
                }
                if (st.getClassName().equals(LogUtils.class.getName())) {
                    continue;
                }
                return "[ Thread:" + Thread.currentThread().getName() + ", at " + st.getClassName() + "." + st.getMethodName()
                        + "(" + st.getFileName() + ":" + st.getLineNumber() + ")" + " ]";
            }
        }
        return null;
    }

    public static void w(String msg) {
        if (isDebug) {
            Log.w(APP_TAG, getMsgFormat(msg));
        }
    }

    public static void w(String tag, String msg) {
        if (isDebug) {
            Log.w(tag, getMsgFormat(msg));
        }
    }

    /** 输出格式定义 */
    private static String getMsgFormat(String msg) {
        return msg + " ;" + getFunctionName();
    }

}
