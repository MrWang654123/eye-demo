package com.cheersmind.cheersgenie.features.modules.base.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.sdk.android.man.MANService;
import com.alibaba.sdk.android.man.MANServiceProvider;
import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.constant.Dictionary;
import com.cheersmind.cheersgenie.features.constant.ErrorCode;
import com.cheersmind.cheersgenie.features.dto.CreateSessionDto;
import com.cheersmind.cheersgenie.features.entity.SessionCreateResult;
import com.cheersmind.cheersgenie.features.interfaces.MessageHandlerCallback;
import com.cheersmind.cheersgenie.features.interfaces.OnResultListener;
import com.cheersmind.cheersgenie.features.modules.login.activity.XLoginAccountActivity;
import com.cheersmind.cheersgenie.features.modules.login.activity.XLoginActivity;
import com.cheersmind.cheersgenie.features.modules.register.activity.ClassNumActivity;
import com.cheersmind.cheersgenie.features.modules.register.activity.RegisterPhoneNumActivity;
import com.cheersmind.cheersgenie.features.utils.DeviceUtil;
import com.cheersmind.cheersgenie.features.utils.SoftInputUtil;
import com.cheersmind.cheersgenie.main.Exception.QSCustomException;
import com.cheersmind.cheersgenie.main.dao.ChildInfoDao;
import com.cheersmind.cheersgenie.main.entity.ChildInfoEntity;
import com.cheersmind.cheersgenie.main.entity.ChildInfoRootEntity;
import com.cheersmind.cheersgenie.main.entity.ErrorCodeEntity;
import com.cheersmind.cheersgenie.main.entity.MessageEntity;
import com.cheersmind.cheersgenie.main.service.BaseService;
import com.cheersmind.cheersgenie.main.service.DataRequestService;
import com.cheersmind.cheersgenie.main.util.InjectionWrapperUtil;
import com.cheersmind.cheersgenie.main.util.JsonUtil;
import com.cheersmind.cheersgenie.main.util.OnMultiClickListener;
import com.cheersmind.cheersgenie.main.util.ToastUtil;
import com.cheersmind.cheersgenie.main.view.LoadingView;
import com.cheersmind.cheersgenie.module.login.UCManager;
import com.cheersmind.cheersgenie.module.login.UserService;

import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 基础页面
 */
public abstract class BaseActivity extends AppCompatActivity implements MessageHandlerCallback, View.OnClickListener {

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

    //消息处理器
    protected Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            //统一回调
            BaseActivity.this.onHandleMessage(msg);
        }
    };

    /**
     * 设置Activity要显示的布局
     *
     * @return 布局的layoutId
     */
    protected abstract int setContentView();

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
        setStatusBarColor(this, getStatusBarColor());

        //设置状态栏文字颜色及图标为深色
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        //设置状态栏文字颜色及图标为浅色
//        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);

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
        //初始化数据
        onInitData();
    }

    /**
     * 设置title
     */
    protected abstract String settingTitle();

    /**
     * 设置title
     */
    protected void settingTitle(String title) {
        tvToolbarTitle.setText(title);
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
        //统计：页面埋点
        MANService manService = MANServiceProvider.getService();
        manService.getMANPageHitHelper().pageAppear(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //统计：页面埋点
        MANService manService = MANServiceProvider.getService();
        manService.getMANPageHitHelper().pageDisAppear(this);
    }


    /**
     * 设置状态栏颜色
     * （目前只支持5.0以上，4.4到5.0之间由于各厂商存在兼容问题，故暂不考虑）
     * （用全屏的方式来强行模拟，感觉没必要）
     * @param activity
     * @param colorStatus
     */
    protected void setStatusBarColor(Activity activity, int colorStatus) {
        //5.0及以上
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
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
     * @return
     */
    protected int getStatusBarColor() {
        return getResources().getColor(R.color.colorPrimary);
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
                ToastUtil.showShort(BaseActivity.this, message);
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
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//FLAG_ACTIVITY_SINGLE_TOP
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
            LoadingView.getInstance().show(BaseActivity.this);
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

        });
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
            LoadingView.getInstance().show(BaseActivity.this);
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
        });
    }


}


