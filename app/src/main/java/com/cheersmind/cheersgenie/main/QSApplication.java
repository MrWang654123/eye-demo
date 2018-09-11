package com.cheersmind.cheersgenie.main;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.multidex.MultiDex;
import android.util.Log;

import com.alibaba.sdk.android.feedback.impl.FeedbackAPI;
import com.alibaba.sdk.android.man.MANService;
import com.alibaba.sdk.android.man.MANServiceProvider;
import com.alibaba.sdk.android.push.CloudPushService;
import com.alibaba.sdk.android.push.CommonCallback;
import com.alibaba.sdk.android.push.noonesdk.PushServiceFactory;
import com.cheersmind.cheersgenie.BuildConfig;
import com.cheersmind.cheersgenie.main.constant.Constant;
import com.cheersmind.cheersgenie.main.constant.HttpConfig;
import com.cheersmind.cheersgenie.main.util.CrashHandler;
import com.cheersmind.cheersgenie.main.util.LogUtils;
import com.cheersmind.cheersgenie.main.util.PhoneUtil;
import com.cheersmind.cheersgenie.module.login.EnvHostManager;
import com.facebook.drawee.backends.pipeline.Fresco;

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
//        myCrashHandler.init(getApplicationContext());
//        Thread.currentThread().setUncaughtExceptionHandler(myCrashHandler);

        //反馈
        FeedbackAPI.init(this, Constant.FEEDBACK_APP_KEY,Constant.FEEDBACK_APP_SECRET);

        //统计
        // 获取MAN服务
        MANService manService = MANServiceProvider.getService();
        // 关闭crash自动采集功能
        manService.getMANAnalytics().turnOffCrashReporter();
        //初始化
        manService.getMANAnalytics().init(this, getApplicationContext(), Constant.FEEDBACK_APP_KEY, Constant.FEEDBACK_APP_SECRET);
        // 通过此接口关闭页面自动打点功能
        manService.getMANAnalytics().turnOffAutoPageTrack();
        // 用户注销埋点
//        manService.getMANAnalytics().updateUserAccount("", "");

        //消息推送
        initCloudChannel(this);

        //设置服务器地址
       setHostType();

       //设置

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


    public static void setHostType(){
        String hostType = BuildConfig.HOST_TYPE;
        if("product".equals(hostType)){
            EnvHostManager.getInstance().setUcHost(EnvHostManager.UC_HOST_PRODUCT);
            EnvHostManager.getInstance().setApiHost(EnvHostManager.API_HOST_PRODUCT);
            EnvHostManager.getInstance().setWebHost(EnvHostManager.WEB_HOST_PRODUCT);
            EnvHostManager.getInstance().setVideoSignKey(EnvHostManager.VIDEO_SIGN_KEY_PRODUCT);
        }else if("productB".equals(hostType)){
            EnvHostManager.getInstance().setUcHost(EnvHostManager.UC_HOST_PRODUCT_B);
            EnvHostManager.getInstance().setApiHost(EnvHostManager.API_HOST_PRODUCT_B);
            EnvHostManager.getInstance().setWebHost(EnvHostManager.WEB_HOST_PRODUCT_B);
        }else if("local".equals(hostType)){
            EnvHostManager.getInstance().setUcHost(EnvHostManager.UC_HOST_LOCAL);
            EnvHostManager.getInstance().setApiHost(EnvHostManager.API_HOST_LOCAL);
            EnvHostManager.getInstance().setWebHost(EnvHostManager.WEB_HOST_LOCAL);
        }else if("develop".equals(hostType)){
            EnvHostManager.getInstance().setUcHost(EnvHostManager.UC_HOST_DEVELOP);
            EnvHostManager.getInstance().setApiHost(EnvHostManager.API_HOST_DEVELOP);
            EnvHostManager.getInstance().setWebHost(EnvHostManager.WEB_HOST_DEVELOP);
            EnvHostManager.getInstance().setVideoSignKey(EnvHostManager.VIDEO_SIGN_KEY_DEVELOP);
        }else if("local_other".equals(hostType)){
            EnvHostManager.getInstance().setUcHost(EnvHostManager.UC_HOST_LOCAL_OTHER);
            EnvHostManager.getInstance().setApiHost(EnvHostManager.API_HOST_LOCAL_OTHER);
            EnvHostManager.getInstance().setWebHost(EnvHostManager.WEB_HOST_LOCAL_OTHER);
        }else {
            EnvHostManager.getInstance().setUcHost(EnvHostManager.UC_HOST_DEVELOP);
            EnvHostManager.getInstance().setApiHost(EnvHostManager.API_HOST_DEVELOP);
            EnvHostManager.getInstance().setWebHost(EnvHostManager.WEB_HOST_DEVELOP);
            EnvHostManager.getInstance().setVideoSignKey(EnvHostManager.VIDEO_SIGN_KEY_DEVELOP);
        }
        LogUtils.w("host_type:",HttpConfig.API_HOST);
    }


    private static final String TAG = "Init";

    /**
     * 初始化云推送通道
     * @param applicationContext
     */
    private void initCloudChannel(final Context applicationContext) {
        PushServiceFactory.init(applicationContext);
        final CloudPushService pushService = PushServiceFactory.getCloudPushService();
        pushService.register(applicationContext, new CommonCallback() {
            @Override
            public void onSuccess(String response) {
                Log.i(TAG, "init cloudchannel success");
//                setConsoleText("init cloudchannel success");
            }

            @Override
            public void onFailed(String errorCode, String errorMessage) {
                Log.e(TAG, "init cloudchannel failed -- errorcode:" + errorCode + " -- errorMessage:" + errorMessage);
//                setConsoleText("init cloudchannel failed -- errorcode:" + errorCode + " -- errorMessage:" + errorMessage);
            }
        });

//        MiPushRegister.register(applicationContext, "XIAOMI_ID", "XIAOMI_KEY"); // 初始化小米辅助推送
//        HuaWeiRegister.register(applicationContext); // 接入华为辅助推送
//        GcmRegister.register(applicationContext, "send_id", "application_id"); // 接入FCM/GCM初始化推送
    }

}
