package com.cheersmind.smartbrain.main;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.multidex.MultiDex;

import com.alibaba.sdk.android.feedback.impl.FeedbackAPI;
import com.cheersmind.smartbrain.main.constant.Constant;
import com.cheersmind.smartbrain.main.util.CrashHandler;
import com.cheersmind.smartbrain.main.util.PhoneUtil;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.umeng.commonsdk.UMConfigure;

import org.litepal.LitePalApplication;


/**
 * Created by goodm on 2017/4/14.
 */
public class QSApplication extends LitePalApplication {

    private static Context context;
    private static Handler mHandler;

    /**
     * 网络状态
     */
    public static int sNetWorkState = PhoneUtil.NETSTATE_UNKOWN;

    /**
     * 标识是否为wifi
     */
    public static boolean sNetWorkIsWifi = false;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        mHandler = new Handler();
        Fresco.initialize(this);

        //把自定义的异常处理类设置 给主线程
        CrashHandler myCrashHandler =  CrashHandler.getInstance();
        myCrashHandler.init(getApplicationContext());
        Thread.currentThread().setUncaughtExceptionHandler(myCrashHandler);

        FeedbackAPI.init(this, Constant.FEEDBACK_APP_KEY,Constant.FEEDBACK_APP_SECRET);
        UMConfigure.init(this, UMConfigure.DEVICE_TYPE_PHONE, Constant.UAPP_KEY);

//        try {
//            // 注入
//            Field field = LitePalApplication.class.getDeclaredField("mContext");
//            field.setAccessible(true);
//            try {
//                field.set(null, this);
//            } catch (IllegalAccessException e) {
//                e.printStackTrace();
//            } catch (IllegalArgumentException e) {
//                e.printStackTrace();
//            }
//        } catch (NoSuchFieldException e) {
//            e.printStackTrace();
//        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this) ;

    }

    public static Context getContext(){
        return context;
    }

    public static  Handler getHandler() {
        return mHandler;
    }

    /**
     * 网络状态变化
     */
    public static void onNetStateChange() {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        if (info != null && info.isAvailable()) {
            String name = info.getTypeName();
            sNetWorkState = PhoneUtil.NETSTATE_ENABLE;
            sNetWorkIsWifi = info.getType() == ConnectivityManager.TYPE_WIFI;
        } else {
            sNetWorkIsWifi = false;
            sNetWorkState = PhoneUtil.NETSTATE_DISABLE;
        }
    }
}