package com.cheersmind.cheersgenie.features.modules.base.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.MessageQueue;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.constant.Dictionary;
import com.cheersmind.cheersgenie.features.constant.ErrorCode;
import com.cheersmind.cheersgenie.features.dto.CreateSessionDto;
import com.cheersmind.cheersgenie.features.entity.SessionCreateResult;
import com.cheersmind.cheersgenie.features.interfaces.MessageHandlerCallback;
import com.cheersmind.cheersgenie.features.interfaces.OnResultListener;
import com.cheersmind.cheersgenie.features.manager.SynthesizerManager;
import com.cheersmind.cheersgenie.features.modules.login.activity.XLoginAccountActivity;
import com.cheersmind.cheersgenie.features.modules.login.activity.XLoginActivity;
import com.cheersmind.cheersgenie.features.modules.register.activity.ClassNumActivity;
import com.cheersmind.cheersgenie.features.utils.DeviceUtil;
import com.cheersmind.cheersgenie.features.utils.SoftInputUtil;
import com.cheersmind.cheersgenie.main.Exception.QSCustomException;
import com.cheersmind.cheersgenie.main.QSApplication;
import com.cheersmind.cheersgenie.main.dao.ChildInfoDao;
import com.cheersmind.cheersgenie.main.entity.ChildInfoEntity;
import com.cheersmind.cheersgenie.main.entity.ChildInfoRootEntity;
import com.cheersmind.cheersgenie.main.entity.ErrorCodeEntity;
import com.cheersmind.cheersgenie.main.service.BaseService;
import com.cheersmind.cheersgenie.main.service.DataRequestService;
import com.cheersmind.cheersgenie.main.util.InjectionWrapperUtil;
import com.cheersmind.cheersgenie.main.util.JsonUtil;
import com.cheersmind.cheersgenie.main.util.OnMultiClickListener;
import com.cheersmind.cheersgenie.main.util.ToastUtil;
import com.cheersmind.cheersgenie.main.view.LoadingView;
import com.cheersmind.cheersgenie.module.login.UCManager;
import com.umeng.analytics.MobclickAgent;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 基础页面
 */
public abstract class BaseActivity extends AppCompatActivity implements MessageHandlerCallback, View.OnClickListener {

    //请求读写外部文件
    public static final int WRITE_EXTERNAL_STORAGE = 950;

    //标题栏
    @BindView(R.id.toolbar)
    @Nullable Toolbar toolbar;
    //标题
    @BindView(R.id.tv_toolbar_title)
    protected @Nullable TextView tvToolbarTitle;
    //左侧按钮
    @BindView(R.id.iv_left)
    @Nullable ImageView ivLeft;

    //Session创建结果
    protected SessionCreateResult sessionCreateResult;

    //new Handler对象处理消息
    private Handler mHandler;


    //权限
    protected String[] permissions = new String[] {
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };


    //通信tag
    protected String httpTag = System.currentTimeMillis() + "";


    /**
     * 设置Activity要显示的布局
     *
     * @return 布局的layoutId
     */
    protected abstract int setContentView();

    @SuppressLint("CommitPrefEdits")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//竖屏
//        requestWindowFeature(Window.FEATURE_NO_TITLE);// 隐藏标题栏
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);// 设置全屏
        super.onCreate(savedInstanceState);

//        getSupportActionBar().hide();
        //设置布局的layoutId
        setContentView(setContentView());
        ButterKnife.bind(this);
        //用Toolbar替代原来的ActionBar
        setSupportActionBar(toolbar);

        //设置title
        if (tvToolbarTitle != null) {
            tvToolbarTitle.setText(settingTitle());
        }

        //设置状态栏颜色
        setStatusBarBackgroundColor(this, getStatusBarColor());

        //设置状态栏文字以及图标颜色
        setStatusBarTextColor();

        //设置自定义左侧键点击监听
        if (ivLeft != null) {
            ivLeft.setOnClickListener(new OnMultiClickListener() {
                @Override
                public void onMultiClick(View view) {
                    onToolbarLeftButtonClick();
                }
            });
        }

        //初始化视图控件
        onInitView();

//        //测试恢复流程
//        if (this instanceof UserInfoActivity) {
//            //判断是否是第一次启动，是否可以自动登录
//            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
//            int featureVersion = pref.getInt("feature_version",0);
//            boolean showNewFeature = featureVersion == Constant.VERSION_FEATURE;
//            if (showNewFeature) {
//                restartToSplashActivity();
//                SharedPreferences.Editor edit = pref.edit();
//                edit.putInt("feature_version", 1000);
//                edit.apply();
//                return;
//            }
//        }

        //非空表示是恢复流程
        if (savedInstanceState != null) {
            restartToSplashActivity();
            return;
        }

        //初始化数据
        onInitData();

//        fixFocusedViewLeak(getApplication());
    }

    /**
     * 设置title
     */
    protected abstract String settingTitle();

    /**
     * 设置title
     */
    protected void settingTitle(String title) {
        if (tvToolbarTitle != null) {
            tvToolbarTitle.setText(title);
        }
    }

    /**
     * 初始化视图控件
     */
    protected abstract void onInitView();

    /**
     * 初始化数据
     */
    protected abstract void onInitData();

    @Override
    protected void onResume() {
        super.onResume();
//        //统计：页面埋点
//        MANService manService = MANServiceProvider.getService();
//        manService.getMANPageHitHelper().pageAppear(this);

        //友盟统计
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
//        //统计：页面埋点
//        MANService manService = MANServiceProvider.getService();
//        manService.getMANPageHitHelper().pageDisAppear(this);

        //友盟统计
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //取消当前页面的所有通信
        BaseService.cancelTag(httpTag);
        //清空mHandler
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }

        try {
            fixInputMethodManagerLeak(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 设置状态栏背景颜色
     * （目前只支持5.0以上，4.4到5.0之间由于各厂商存在兼容问题，故暂不考虑）
     * @param activity 页面
     * @param colorStatus 颜色
     */
    protected void setStatusBarBackgroundColor(Activity activity, int colorStatus) {
        //6.0及以上
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Window window = activity.getWindow();
            //取消设置透明状态栏,使 ContentView 内容不再覆盖状态栏
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

            //需要设置这个 flag 才能调用 setStatusBarColor 来设置状态栏颜色
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            //设置状态栏颜色
            window.setStatusBarColor(colorStatus);
            //底部导航栏
            //window.setNavigationBarColor(activity.getResources().getColor(colorResId));

            ViewGroup mContentView = (ViewGroup) activity.findViewById(Window.ID_ANDROID_CONTENT);
            View mChildView = mContentView.getChildAt(0);
            if (mChildView != null) {
                //注意不是设置 ContentView 的 FitsSystemWindows, 而是设置 ContentView 的第一个子 View . 预留出系统 View 的空间.
                mChildView.setFitsSystemWindows(true);
            }

            //5.0及以上
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            //取消设置透明状态栏,使 ContentView 内容不再覆盖状态栏
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

            //需要设置这个 flag 才能调用 setStatusBarBackgroundColor 来设置状态栏颜色
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            //设置状态栏颜色
            window.setStatusBarColor(getResources().getColor(R.color.color_000000));//Color.TRANSPARENT半透明
            //底部导航栏
            //window.setNavigationBarColor(activity.getResources().getColor(colorResId));

            ViewGroup mContentView = (ViewGroup) activity.findViewById(Window.ID_ANDROID_CONTENT);
            View mChildView = mContentView.getChildAt(0);
            if (mChildView != null) {
                //注意不是设置 ContentView 的 FitsSystemWindows, 而是设置 ContentView 的第一个子 View . 预留出系统 View 的空间.
                mChildView.setFitsSystemWindows(true);
            }

            //4.4到5.0
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

            /*Window window = activity.getWindow();
            ViewGroup decorViewGroup = (ViewGroup) window.getDecorView();
            View statusBarView = new View(window.getContext());
            int statusBarHeight = getStatusBarHeight(window.getContext());
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, statusBarHeight);
            params.gravity = Gravity.TOP;
            statusBarView.setLayoutParams(params);
            statusBarView.setBackgroundColor(colorStatus);
            decorViewGroup.addView(statusBarView);

            ViewGroup mContentView = (ViewGroup) window.findViewById(Window.ID_ANDROID_CONTENT);
            View mChildView = mContentView.getChildAt(0);
            if (mChildView != null) {
                //注意不是设置 ContentView 的 FitsSystemWindows, 而是设置 ContentView 的第一个子 View . 预留出系统 View 的空间.
                mChildView.setFitsSystemWindows(true);
            }*/
        }
    }

    /**
     * 用于子页面设置状态栏颜色（目前只支持5.0以后）
     * @return 颜色数值
     */
    protected int getStatusBarColor() {
        return getResources().getColor(R.color.colorPrimary);
    }


    /**
     * 设置状态栏文字以及图标颜色
     */
    protected void setStatusBarTextColor() {
        //设置状态栏文字颜色及图标为深色
        //View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN 是从API 16开始启用，实现效果：
        //视图延伸至状态栏区域，状态栏悬浮于视图之上
        //View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR 是从 API 23开始启用，实现效果：
        //设置状态栏图标和状态栏文字颜色为深色，为适应状态栏背景为浅色调，该Flag只有在使用了FLAG_DRWS_SYSTEM_BAR_BACKGROUNDS，
        // 并且没有使用FLAG_TRANSLUCENT_STATUS时才有效，即只有在透明状态栏时才有效。
        //6.0及以上
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

            //4.1及以上
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
//            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        //设置状态栏文字颜色及图标为浅色
//        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
    }

    /**
     * 获取状态栏高度
     * @param context
     * @return
     */
    private static int getStatusBarHeight(Context context) {
        int statusBarHeight = 0;
        Resources res = context.getResources();
        int resourceId = res.getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = res.getDimensionPixelSize(resourceId);
        }
        return statusBarHeight;
    }


    /**
     * mHandler发送的消息处理
     * @param msg
     */
    @Override
    public void onHandleMessage(Message msg) {

    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //toolbar中的左侧键（一般是返回键）
        if(item.getItemId()==android.R.id.home){
            onToolbarLeftButtonClick();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * toolbar左侧键点击处理
     */
    protected void onToolbarLeftButtonClick() {
        finish();
    }


    /**
     * 默认的通信失败处理
     * @param e 自定义异常
     */
    public void onFailureDefault(QSCustomException e) {
        onFailureDefault(e, null);
    }

    /**
     * 默认的通信失败处理
     * @param e
     * @param errorCodeCallBack ErrorCodeEntity对象回调
     */
    public void onFailureDefault(QSCustomException e, FailureDefaultErrorCodeCallBack errorCodeCallBack) {
        //关闭通信等待
        LoadingView.getInstance().dismiss();
        //服务器繁忙，请稍后重试！
        String message = getResources().getString(R.string.error_code_common_text);
        //回调方法已经处理了该异常，默认false：执行默认提示
        boolean callBackHandleTheError = false;

        try {
            String bodyStr = e.getMessage();
            Map map = JsonUtil.fromJson(bodyStr, Map.class);
            ErrorCodeEntity errorCodeEntity = InjectionWrapperUtil.injectMap(map, ErrorCodeEntity.class);

            //token超时和被T的处理
            if (errorCodeEntity != null && ErrorCode.AC_AUTH_INVALID_TOKEN.equals(errorCodeEntity.getCode())) {
                message = getResources().getString(R.string.invalid_token_tip);

            } else {
                //ErrorCodeEntity对象回调
                if (errorCodeEntity != null && errorCodeCallBack != null) {
                    //true：处理了异常，则直接return；false：未处理异常，则继续走默认流程
                    callBackHandleTheError = errorCodeCallBack.onErrorCodeCallBack(errorCodeEntity);
                    if (callBackHandleTheError) {
                        return;
                    }
                }
                //服务端返回的错误消息
                String tempMessage = errorCodeEntity.getMessage();
                if (!TextUtils.isEmpty(tempMessage)) {
                    message = tempMessage;
                }
            }

        } catch (Exception err) {
            //异常处理（如何只处理自定义的异常信息，程序错误按默认信息提示？）
            String tempMessage = e.getMessage();
            if (!TextUtils.isEmpty(tempMessage)) {
                message = tempMessage;
            }

        } finally {
            //回调方法没有处理该异常，则走默认提示流程
            if (!callBackHandleTheError) {
                ToastUtil.showShort(getApplication(), message);
            }
        }
    }

    /**
     * 默认错误解析的ErrorCodeEntity对象回调
     */
    public interface FailureDefaultErrorCodeCallBack {
        /**
         * ErrorCodeEntity对象回调
         * @param errorCodeEntity
         * @return true：处理了异常；false：未处理异常
         */
        public boolean onErrorCodeCallBack(ErrorCodeEntity errorCodeEntity);
    }


    /**
     * 跳转到主页面
     */
    protected void gotoMainPage(BaseActivity baseActivity) {
        Intent intent = new Intent(baseActivity, MasterTabActivity.class);
        //作为根activity
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    /**
     * 跳转到账号登录页面
     */
    protected void gotoAccountLoginPage(BaseActivity baseActivity) {
        Intent intent = new Intent(baseActivity, XLoginAccountActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//FLAG_ACTIVITY_SINGLE_TOP
        startActivity(intent);
    }

    /**
     * 跳转到登录主页面
     */
    protected void gotoLoginPage(BaseActivity from) {
        //如果是欢迎页面，则关闭欢迎页
        if (from instanceof SplashActivity) {
            from.finish();
        }
        Intent intent = new Intent(from, XLoginActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//FLAG_ACTIVITY_SINGLE_TOP
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    /**
     * 跳转到完善用户信息环节
     * @param from
     */
    protected void gotoPerfectUserInfo(BaseActivity from) {
        //如果是欢迎页面，则关闭欢迎页
        if (from instanceof SplashActivity) {
            from.finish();
        }
        //目前完善用户信息环节是从班级号输入页面开始
        Intent intent = new Intent(from, ClassNumActivity.class);
        startActivity(intent);
    }


    /**
     * 重新走欢迎页
     */
    private void restartToSplashActivity() {
        Intent intent = new Intent(this, SplashActivity.class);
        //清理之前的场景，不然之前的ActivityRecord栈仍然保留在ActivityManagerService中
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }


    /**
     * 获取当前用户的孩子列表
     */
    protected void doGetChildListWrap() {
        doGetChildListWrap(true);
    }

    /**
     * 获取当前用户的孩子列表
     * @param showLoading
     */
    protected void doGetChildListWrap(final boolean showLoading) {
        //隐藏软键盘
        SoftInputUtil.closeSoftInput(BaseActivity.this);
        if (showLoading) {
            //开启通信等待提示
            LoadingView.getInstance().show(BaseActivity.this, httpTag);
        }

        DataRequestService.getInstance().getChildListV2(new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                onFailureDefault(e);
                //跳转到登录主页面或者账号登录页面，目前默认跳转到登录主页面
                gotoLoginPage(BaseActivity.this);
            }

            @Override
            public void onResponse(Object obj) {
                if (showLoading) {
                    //关闭通信等待提示
                    LoadingView.getInstance().dismiss();
                }

                try {
                    Map dataMap = JsonUtil.fromJson(obj.toString(), Map.class);
                    ChildInfoRootEntity childData = InjectionWrapperUtil.injectMap(dataMap, ChildInfoRootEntity.class);
                    List<ChildInfoEntity> childList = childData.getItems();
                    if (childList.size() == 0) {
                        throw new Exception();
                    }

                    //清空本地数据中的孩子列表
                    ChildInfoDao.deleteAllChild();
                    //1、保存孩子列表到数据库；2、设置第一个孩子为默认孩子
                    for (int i = 0; i < childList.size(); i++) {
                        ChildInfoEntity entity = childList.get(i);
                        if (i == 0) {
                            entity.setDefaultChild(true);
                        } else {
                            entity.setDefaultChild(false);
                        }
                        UCManager.getInstance().setDefaultChild(entity);
                        entity.save();
                    }

                    //跳转到主页面
                    gotoMainPage(BaseActivity.this);

                } catch (Exception e) {
                    // 走完善信息的流程
                    gotoPerfectUserInfo(BaseActivity.this);
                }
            }

        }, httpTag, BaseActivity.this);
    }


    /**
     * 创建会话（目前只用打*的两种类型）
     *
     * @param type 类型：会话类型，0：注册(手机)， *1：登录(帐号、密码登录)，2：手机找回密码，3：登录(短信登录)， *4:下发短信验证码
     * @param showLoading 是否显示通信等待
     * @param listener 结果监听
     */
    protected void doPostAccountSession(int type, final boolean showLoading, final OnResultListener listener) {
        if (showLoading) {
            LoadingView.getInstance().show(BaseActivity.this, httpTag);
        }

        CreateSessionDto dto = new CreateSessionDto();
        dto.setSessionType(type);//类型
        dto.setTenant(Dictionary.Tenant_CheersMind);//租户名
        dto.setDeviceId(DeviceUtil.getDeviceId(getApplicationContext()));//设备ID
        //请求创建会话
        DataRequestService.getInstance().postAccountsSessions(dto, new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                e.printStackTrace();
                onFailureDefault(e);

                if (listener != null) {
                    listener.onFailed(e);
                }
            }

            @Override
            public void onResponse(Object obj) {
                try {
                    if (showLoading) {
                        LoadingView.getInstance().dismiss();
                    }

                    Map dataMap = JsonUtil.fromJson(obj.toString(), Map.class);
                    sessionCreateResult = InjectionWrapperUtil.injectMap(dataMap, SessionCreateResult.class);
                    //非空
                    if (sessionCreateResult == null || TextUtils.isEmpty(sessionCreateResult.getSessionId())) {
                        throw new Exception();
                    }

                    //成功加载
                    if (listener != null) {
                        listener.onSuccess();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    onFailure(new QSCustomException(getResources().getString(R.string.operate_fail)));
                }
            }
        }, httpTag, BaseActivity.this);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            //请求读写外部文件
            case WRITE_EXTERNAL_STORAGE: {
                //授权成功
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //初始化百度音频
                    getSynthesizerManager().initialTts();

                } else {//授权失败
                    //友好的提示
                }
                break;
            }

        }

    }


    /**
     * 获取百度音频管理器
     * @return 管理器
     */
    protected SynthesizerManager getSynthesizerManager() {
        return ((QSApplication)getApplication()).getSynthesizerManager();
    }


    //内部类解决可能存在的内存溢出
    /*
    *1、静态和非静态内部类的区别是非静态内部类持有外部类的引用。
2、内部类实例的持有对象的生命周期大于其外部类对象，那么就有可能导致内存泄露。
比如,要实例化一个超出activity生命周期的内部类对象，
避免使用非静态的内部类。建议使用静态内部类并且在内部类中持有外部类的弱引用。
    *
    * */
    //静态内部类
    private static class MyHandler extends Handler {
        private final WeakReference<BaseActivity> mActivity;
        //构造方法
        MyHandler(BaseActivity activity) {
            mActivity = new WeakReference<BaseActivity>(activity);//对外部类的弱引用
        }
        @Override
        public void handleMessage(Message msg) {
            BaseActivity activity = mActivity.get();
            //统一回调
            if (activity != null) {
                activity.onHandleMessage(msg);
            }
        }
    }

    /**
     * 获取handler
     * @return handler
     */
    public Handler getHandler() {
        if (mHandler == null) {
            mHandler = new MyHandler(this);
        }
        return mHandler;
    }

    /**
     * 解決通用的InputMethodManagerLeak
     * @param context 上下文
     */
    public static void fixInputMethodManagerLeak(Context context) {
        try {
            if (context == null) {
                return;
            }
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm == null) {
                return;
            }
            String[] arr = new String[]{"mCurRootView", "mServedView", "mNextServedView"};
            Field f = null;
            Object obj = null;
            for (int i = 0; i < arr.length; i++) {
                String param = arr[i];
                try {
                    f = imm.getClass().getDeclaredField(param);
                    if (f.isAccessible() == false) {
                        f.setAccessible(true);
                    }
                    obj = f.get(imm);
                    if (obj != null && obj instanceof View) {
                        View vGet = (View) obj;
                        if (vGet.getContext() == context) {
                            f.set(imm, null);
                        } else {
                            break;
                        }
                    }
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    /**
     * Fix for https://code.google.com/p/android/issues/detail?id=171190 .
     *
     * When a view that has focus gets detached, we wait for the main thread to be idle and then
     * check if the InputMethodManager is leaking a view. If yes, we tell it that the decor view got
     * focus, which is what happens if you press home and come back from recent apps. This replaces
     * the reference to the detached view with a reference to the decor view.
     *
     * Should be called from {@link Activity#onCreate(android.os.Bundle)} )}.
     */
    @SuppressLint("PrivateApi")
    public static void fixFocusedViewLeak(Application application) {

        // Don't know about other versions yet.
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1|| Build.VERSION.SDK_INT > 23) {
            return;
        }

        final InputMethodManager inputMethodManager =
                (InputMethodManager) application.getSystemService(Context.INPUT_METHOD_SERVICE);

        final Field mServedViewField;
        final Field mHField;
        final Method finishInputLockedMethod;
        final Method focusInMethod;
        try {
            mServedViewField = InputMethodManager.class.getDeclaredField("mServedView");
            mServedViewField.setAccessible(true);
            mHField = InputMethodManager.class.getDeclaredField("mServedView");
            mHField.setAccessible(true);
            finishInputLockedMethod = InputMethodManager.class.getDeclaredMethod("finishInputLocked");
            finishInputLockedMethod.setAccessible(true);
            focusInMethod = InputMethodManager.class.getDeclaredMethod("focusIn", View.class);
            focusInMethod.setAccessible(true);
        } catch (NoSuchMethodException  unexpected) {
            Log.e("IMMLeaks", "Unexpected reflection exception", unexpected);
            return;
        } catch (NoSuchFieldException unexpected) {
            Log.e("IMMLeaks", "Unexpected reflection exception", unexpected);
            return;
        }

        application.registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityDestroyed(Activity activity){

            }

            @Override
            public void onActivityStarted(Activity activity){

            }

            @Override
            public void onActivityResumed(Activity activity){

            }

            @Override
            public void onActivityPaused(Activity activity){

            }

            @Override
            public void onActivityStopped(Activity activity){

            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle bundle){

            }

            @Override public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                ReferenceCleaner cleaner = new ReferenceCleaner(inputMethodManager, mHField, mServedViewField,
                        finishInputLockedMethod);
                View rootView = activity.getWindow().getDecorView().getRootView();
                ViewTreeObserver viewTreeObserver = rootView.getViewTreeObserver();
                viewTreeObserver.addOnGlobalFocusChangeListener(cleaner);
            }
        });
    }

    static class ReferenceCleaner
            implements MessageQueue.IdleHandler, View.OnAttachStateChangeListener,
            ViewTreeObserver.OnGlobalFocusChangeListener {

        private final InputMethodManager inputMethodManager;
        private final Field mHField;
        private final Field mServedViewField;
        private final Method finishInputLockedMethod;

        ReferenceCleaner(InputMethodManager inputMethodManager, Field mHField, Field mServedViewField,
                         Method finishInputLockedMethod) {
            this.inputMethodManager = inputMethodManager;
            this.mHField = mHField;
            this.mServedViewField = mServedViewField;
            this.finishInputLockedMethod = finishInputLockedMethod;
        }

        @Override public void onGlobalFocusChanged(View oldFocus, View newFocus) {
            if (newFocus == null) {
                return;
            }
            if (oldFocus != null) {
                oldFocus.removeOnAttachStateChangeListener(this);
            }
            Looper.myQueue().removeIdleHandler(this);
            newFocus.addOnAttachStateChangeListener(this);
        }

        @Override public void onViewAttachedToWindow(View v) {
        }

        @Override public void onViewDetachedFromWindow(View v) {
            v.removeOnAttachStateChangeListener(this);
            Looper.myQueue().removeIdleHandler(this);
            Looper.myQueue().addIdleHandler(this);
        }

        @Override public boolean queueIdle() {
            clearInputMethodManagerLeak();
            return false;
        }

        private void clearInputMethodManagerLeak() {
            try {
                Object lock = mHField.get(inputMethodManager);
                // This is highly dependent on the InputMethodManager implementation.
                synchronized (lock) {
                    View servedView = (View) mServedViewField.get(inputMethodManager);
                    if (servedView != null) {

                        boolean servedViewAttached = servedView.getWindowVisibility() != View.GONE;

                        if (servedViewAttached) {
                            // The view held by the IMM was replaced without a global focus change. Let's make
                            // sure we get notified when that view detaches.

                            // Avoid double registration.
                            servedView.removeOnAttachStateChangeListener(this);
                            servedView.addOnAttachStateChangeListener(this);
                        } else {
                            // servedView is not attached. InputMethodManager is being stupid!
                            Activity activity = extractActivity(servedView.getContext());
                            if (activity == null || activity.getWindow() == null) {
                                // Unlikely case. Let's finish the input anyways.
                                finishInputLockedMethod.invoke(inputMethodManager);
                            } else {
                                View decorView = activity.getWindow().peekDecorView();
                                boolean windowAttached = decorView.getWindowVisibility() != View.GONE;
                                if (!windowAttached) {
                                    finishInputLockedMethod.invoke(inputMethodManager);
                                } else {
                                    decorView.requestFocusFromTouch();
                                }
                            }
                        }
                    }
                }
            } catch (IllegalAccessException unexpected) {
                Log.e("IMMLeaks", "Unexpected reflection exception", unexpected);
            } catch (InvocationTargetException unexpected) {
                Log.e("IMMLeaks", "Unexpected reflection exception", unexpected);
            }
        }

        private Activity extractActivity(Context context) {
            while (true) {
                if (context instanceof Application) {
                    return null;
                } else if (context instanceof Activity) {
                    return (Activity) context;
                } else if (context instanceof ContextWrapper) {
                    Context baseContext = ((ContextWrapper) context).getBaseContext();
                    // Prevent Stack Overflow.
                    if (baseContext == context) {
                        return null;
                    }
                    context = baseContext;
                } else {
                    return null;
                }
            }
        }
    }


}


