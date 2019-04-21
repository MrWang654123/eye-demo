package com.cheersmind.cheersgenie.main;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Handler;
import android.support.multidex.MultiDex;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.sdk.android.feedback.impl.FeedbackAPI;
import com.alibaba.sdk.android.feedback.util.ErrorCode;
import com.alibaba.sdk.android.feedback.util.FeedbackErrorCallback;
import com.alibaba.sdk.android.push.CloudPushService;
import com.alibaba.sdk.android.push.CommonCallback;
import com.alibaba.sdk.android.push.noonesdk.PushServiceFactory;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.cheersmind.cheersgenie.BuildConfig;
import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.manager.SynthesizerManager;
import com.cheersmind.cheersgenie.features.utils.fresco.ImagePipelineConfigFactory;
import com.cheersmind.cheersgenie.features_v2.utils.SSLSocketClient;
import com.cheersmind.cheersgenie.main.constant.Constant;
import com.cheersmind.cheersgenie.main.constant.HttpConfig;
import com.cheersmind.cheersgenie.main.util.CrashHandler;
import com.cheersmind.cheersgenie.main.util.LogUtils;
import com.cheersmind.cheersgenie.main.util.PhoneUtil;
import com.cheersmind.cheersgenie.module.login.EnvHostManager;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.github.mikephil.charting.utils.Utils;
import com.umeng.commonsdk.UMConfigure;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.https.HttpsUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.LitePalApplication;

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

import static com.taobao.accs.client.AccsConfig.build;


/**
 * Created by goodm on 2017/4/14.
 */
public class QSApplication extends LitePalApplication {

    private static Context context;
    //主线程handler
    private static Handler mHandler;

    //默认Glide处理参数
    private static RequestOptions defaultOptions;

    /**
     * 网络状态
     */
    public static int sNetWorkState = PhoneUtil.NETSTATE_UNKOWN;

    /**
     * 标识是否为wifi
     */
    public static boolean sNetWorkIsWifi = false;

    //当前顶层activity（可能会内存泄漏）
//    private static Activity topActivity;

    //屏幕宽高信息
    private static DisplayMetrics metrics;
    //状态栏高度
    private static int statusBarHeight;

    //百度音频管理器
    private SynthesizerManager synthesizerManager;

    //测试数量
    public static int TEST_COUNT = 1;


    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        mHandler = new Handler();

        //初始化Fresco（图片加载库）
//        Fresco.initialize(this);
//        ImagePipelineConfig.newBuilder(this).setBitmapsConfig(Bitmap.Config.RGB_565);
        Fresco.initialize(this, ImagePipelineConfigFactory.getImagePipelineConfig(this));


        //调试模式下不启用自定义的异常处理类
        if (!BuildConfig.DEBUG) {
            //把自定义的异常处理类设置 给主线程
            CrashHandler myCrashHandler = CrashHandler.getInstance();
            myCrashHandler.init(getApplicationContext());
            Thread.currentThread().setUncaughtExceptionHandler(myCrashHandler);
        }

//        //反馈
//        FeedbackAPI.init(this, Constant.FEEDBACK_APP_KEY,Constant.FEEDBACK_APP_SECRET);

//        //移动数据分析
//        // 获取MAN服务
//        MANService manService = MANServiceProvider.getService();
//        //调试模式下关闭crash自动采集功能
////        if (BuildConfig.DEBUG) {
//            // 关闭crash自动采集功能
////            manService.getMANAnalytics().turnOffCrashReporter();
////        }
//        //初始化
//        manService.getMANAnalytics().init(this, getApplicationContext(), Constant.FEEDBACK_APP_KEY, Constant.FEEDBACK_APP_SECRET);
//        // 通过此接口关闭页面自动打点功能
//        manService.getMANAnalytics().turnOffAutoPageTrack();
//        // 用户注销埋点
////        manService.getMANAnalytics().updateUserAccount("", "");

//        //初始化通知通道
//        initNotificationChannel();
//        //消息推送
//        initCloudChannel(this);

        //初始化阿里反馈
        initFeedbackService();
        //初始化通知通道
        initNotificationChannel();
        //初始化云推送通道
        initPushService(this);

        //设置服务器地址
       setHostType();

       //默认Glide处理参数
        defaultOptions = new RequestOptions()
                .centerCrop()//铺满、居中
            .skipMemoryCache(false)//不忽略内存
            .placeholder(R.drawable.default_image_round_article_list)//占位图
//            .dontAnimate()//Glide默认是渐变动画，设置dontAnimate()不要动画
            .diskCacheStrategy(DiskCacheStrategy.ALL);//磁盘缓存策略：缓存所有


        //初始化全局的Activity（可能会内存泄漏）
//        initGlobeActivity();

        //屏幕信息
        metrics = context.getResources().getDisplayMetrics();
        //状态栏高度
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = context.getResources().getDimensionPixelSize(resourceId);
        }

        //初始化OkHttpUtils
        initOkHttpUtils();

        //友盟统计
        UMConfigure.init(this, UMConfigure.DEVICE_TYPE_PHONE, Constant.UAPP_KEY);

        //图表初始化
        Utils.init(getApplicationContext());

        //初始化播放引擎
        //IjkPlayer
//        JZVideoPlayer.setMediaInterface(new JZMediaIjkplayer());
        //ExoPlayer
//        JZVideoPlayer.setMediaInterface(new JZExoPlayer());
        //MediaSystem（默认）
//        JZVideoPlayer.setMediaInterface(new JZMediaSystem());

//        //初始化声音
//        SoundPlayUtils.init(context);

        //创建百度音频管理
        synthesizerManager = new SynthesizerManager(context);

        //初始化三星手机ClipboardUIManager
        samSungClipboardUIManagerInit();
    }

    /**
     * 初始化OkHttpUtils
     */
    private void initOkHttpUtils() {

//        //设置可访问所有的https网站
//        HttpsUtils.SSLParams sslParams = HttpsUtils.getSslSocketFactory(null, null, null);
//        OkHttpClient okHttpClient = new OkHttpClient.Builder()
//                //其他配置
//                .sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager)
//                .build();
//        OkHttpUtils.initClient(okHttpClient);

        //设置具体的证书
        HttpsUtils.SSLParams sslParams = HttpsUtils.getSslSocketFactory(new InputStream[]{SSLSocketClient.getInputStream()}, null, null);
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                //其他配置
                .sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager)
                .hostnameVerifier(SSLSocketClient.getHostnameVerifier())
                .build();
        OkHttpUtils.initClient(okHttpClient);

//        OkHttpClient okHttpClient = new OkHttpClient.Builder()
////                .addInterceptor(new LoggerInterceptor("TAG"))
//                .connectTimeout(10000L, TimeUnit.MILLISECONDS)
//                .readTimeout(20000L, TimeUnit.MILLISECONDS)
//                //其他配置
//                .build();
//
//        OkHttpUtils.initClient(okHttpClient);
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
            EnvHostManager.getInstance().setVideoSignKey(EnvHostManager.VIDEO_SIGN_KEY_DEVELOP);
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

    /**
     * 初始化通知通道
     */
    private void initNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            // 通知渠道的id
            String id = "1";
            // 用户可以看到的通知渠道的名字.
            CharSequence name = "notification channel";
            // 用户可以看到的通知渠道的描述
            String description = "notification description";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(id, name, importance);
            // 配置通知渠道的属性
            mChannel.setDescription(description);
            // 设置通知出现时的闪灯（如果 android 设备支持的话）
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.RED);
            // 设置通知出现时的震动（如果 android 设备支持的话）
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            try {
                if (mNotificationManager != null) {
                    //最后在notificationmanager中创建该通知渠道
                    mNotificationManager.createNotificationChannel(mChannel);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 获取默认Glide处理参数
     * @return
     */
    public static RequestOptions getDefaultOptions() {
        return defaultOptions;
    }


    /**
     * 监听Activity生命周期
     */
//    private void initGlobeActivity() {
//        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
//            @Override
//            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
//                topActivity = activity;
//                LogUtils.w("onActivityCreated===", topActivity + "");
//            }
//
//            @Override
//            public void onActivityDestroyed(Activity activity) {
////                preActivity = activity;
//                LogUtils.w("onActivityDestroyed===", topActivity + "");
//
//                //登录页面和主页面的退出时清空topActivity
//                if (activity != null && topActivity != null) {
//                    if ((activity instanceof MasterTabActivity && topActivity instanceof MasterTabActivity)
//                            || (activity instanceof XLoginActivity && topActivity instanceof XLoginActivity)) {
//                        topActivity = null;
//                    }
//                }
//            }
//
//            /** Unused implementation **/
//            @Override
//            public void onActivityStarted(Activity activity) {
////                topActivity = activity;
//                LogUtils.w("onActivityStarted===", topActivity + "");
//            }
//
//            @Override
//            public void onActivityResumed(Activity activity) {
////                topActivity = activity;
//                LogUtils.w("onActivityResumed===", topActivity + "");
//            }
//
//            @Override
//            public void onActivityPaused(Activity activity) {
////                topActivity = activity;
//                LogUtils.w("onActivityPaused===", topActivity + "");
//            }
//
//            @Override
//            public void onActivityStopped(Activity activity) {
////                topActivity = activity;
//                LogUtils.w("onActivityStopped===", topActivity + "");
//            }
//
//            @Override
//            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
//            }
//        });
//    }
//
//    /**
//     * 公开方法，外部可通过 MyApplication.getInstance().getCurrentActivity() 获取到当前最上层的activity
//     */
//    public static Activity getCurrentActivity() {
//        return topActivity;
//    }


    /**
     * 获取屏幕宽高信息
     * @return
     */
    public static DisplayMetrics getMetrics() {
        return metrics;
    }

    /**
     * 获取状态栏高度
     * @return
     */
    public static int getStatusBarHeight() {
        return statusBarHeight;
    }

    /**
     * 获取百度音频管理器
     * @return 管理器
     */
    public SynthesizerManager getSynthesizerManager() {
        return synthesizerManager;
    }

    /**
     * 初始化三星手机ClipboardUIManager，避免内存泄漏
     */
    private void samSungClipboardUIManagerInit() {
        try {
            Class cls = Class.forName("android.sec.clipboard.ClipboardUIManager");
            Method m = cls.getDeclaredMethod("getInstance", Context.class);
            m.setAccessible(true);
            m.invoke(null, context);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
//            ClipboardManager;
            Class cls = Class.forName("com.samsung.android.content.clipboard.SemClipboardManager");
            Method m = cls.getDeclaredMethod("getInstance", Context.class);
            m.setAccessible(true);
            m.invoke(null, this);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 初始化阿里反馈
     */
    private void initFeedbackService() {
        /**
         * 添加自定义的error handler
         */
        FeedbackAPI.addErrorCallback(new FeedbackErrorCallback() {
            @Override
            public void onError(Context context, String errorMessage, ErrorCode code) {
                Toast.makeText(context, "ErrMsg is: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
        FeedbackAPI.addLeaveCallback(new Callable() {
            @Override
            public Object call() throws Exception {
                Log.d("DemoApplication", "custom leave callback");
                return null;
            }
        });
        /**
         * 建议放在此处做初始化
         */
        //默认初始化
        FeedbackAPI.init(this);
        //FeedbackAPI.init(this, "DEFAULT_APPKEY", "DEFAULT_APPSECRET");
        /**
         * 在Activity的onCreate中执行的代码
         * 可以设置状态栏背景颜色和图标颜色，这里使用com.githang:status-bar-compat来实现
         */
        //FeedbackAPI.setActivityCallback(new IActivityCallback() {
        //    @Override
        //    public void onCreate(Activity activity) {
        //        StatusBarCompat.setStatusBarColor(activity,getResources().getColor(R.color.aliwx_setting_bg_nor),true);
        //    }
        //});
        /**
         * 自定义参数演示
         */
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("loginTime", "登录时间");
            jsonObject.put("visitPath", "登陆，关于，反馈");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        FeedbackAPI.setAppExtInfo(jsonObject);
        /**
         * 以下是设置UI
         */
        //设置默认联系方式
//        FeedbackAPI.setDefaultUserContactInfo("13800000000");
        //沉浸式任务栏，控制台设置为true之后此方法才能生效
        FeedbackAPI.setTranslucent(true);
        //设置返回按钮图标
        //FeedbackAPI.setBackIcon(R.drawable.ali_feedback_common_back_btn_bg);
        //设置标题栏"历史反馈"的字号，需要将控制台中此字号设置为0
//        FeedbackAPI.setHistoryTextSize(20);
        //设置标题栏高度，单位为像素
//        FeedbackAPI.setTitleBarHeight(100);
    }


    /**
     * 初始化云推送通道
     * @param applicationContext
     */
    private void initPushService(final Context applicationContext) {
        PushServiceFactory.init(applicationContext);
        final CloudPushService pushService = PushServiceFactory.getCloudPushService();
        pushService.register(applicationContext, new CommonCallback() {
            @Override
            public void onSuccess(String response) {
                Log.i(TAG, "init cloudchannel success");
                //setConsoleText("init cloudchannel success");
            }
            @Override
            public void onFailed(String errorCode, String errorMessage) {
                Log.e(TAG, "init cloudchannel failed -- errorcode:" + errorCode + " -- errorMessage:" + errorMessage);
                //setConsoleText("init cloudchannel failed -- errorcode:" + errorCode + " -- errorMessage:" + errorMessage);
            }
        });
    }

}
