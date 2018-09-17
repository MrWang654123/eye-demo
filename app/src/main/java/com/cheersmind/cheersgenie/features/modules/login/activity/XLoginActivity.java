package com.cheersmind.cheersgenie.features.modules.login.activity;

import android.content.Intent;
import android.os.Build;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.alibaba.sdk.android.man.MANService;
import com.alibaba.sdk.android.man.MANServiceProvider;
import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.constant.Dictionary;
import com.cheersmind.cheersgenie.features.constant.ErrorCode;
import com.cheersmind.cheersgenie.features.dto.ThirdLoginDto;
import com.cheersmind.cheersgenie.features.modules.base.activity.BaseActivity;
import com.cheersmind.cheersgenie.features.modules.register.activity.ClassNumActivity;
import com.cheersmind.cheersgenie.features.modules.register.activity.ParentRoleActivity;
import com.cheersmind.cheersgenie.features.modules.register.activity.RegisterPhoneNumActivity;
import com.cheersmind.cheersgenie.features.modules.test.activity.SchemaActivity;
import com.cheersmind.cheersgenie.features.modules.test.activity.TextViewForHtmlImageActivity;
import com.cheersmind.cheersgenie.features.utils.DeviceUtil;
import com.cheersmind.cheersgenie.features.utils.NetworkUtil;
import com.cheersmind.cheersgenie.features.view.dialog.CategoryDialog;
import com.cheersmind.cheersgenie.features.view.dialog.DimensionReportDialog;
import com.cheersmind.cheersgenie.main.Exception.QSCustomException;
import com.cheersmind.cheersgenie.main.constant.Constant;
import com.cheersmind.cheersgenie.main.constant.HttpConfig;
import com.cheersmind.cheersgenie.main.dao.ChildInfoDao;
import com.cheersmind.cheersgenie.main.entity.ChildInfoEntity;
import com.cheersmind.cheersgenie.main.entity.ChildInfoRootEntity;
import com.cheersmind.cheersgenie.main.entity.ErrorCodeEntity;
import com.cheersmind.cheersgenie.main.entity.QQTokenEntity;
import com.cheersmind.cheersgenie.main.entity.WXTokenEntity;
import com.cheersmind.cheersgenie.main.entity.WXUserInfoEntity;
import com.cheersmind.cheersgenie.main.event.WXLoginEvent;
import com.cheersmind.cheersgenie.main.service.BaseService;
import com.cheersmind.cheersgenie.main.service.DataRequestService;
import com.cheersmind.cheersgenie.main.util.InjectionWrapperUtil;
import com.cheersmind.cheersgenie.main.util.JsonUtil;
import com.cheersmind.cheersgenie.main.util.LogUtils;
import com.cheersmind.cheersgenie.main.util.ToastUtil;
import com.cheersmind.cheersgenie.main.view.LoadingView;
import com.cheersmind.cheersgenie.module.login.UCManager;
import com.cheersmind.cheersgenie.module.login.UserLicenseActivity;
import com.cheersmind.cheersgenie.module.login.UserService;
import com.tencent.connect.common.Constants;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.cheersmind.cheersgenie.main.constant.Constant.mTencent;

/**
 * 登陆页面
 */
public class XLoginActivity extends BaseActivity {

    @BindView(R.id.btn_login)
    Button btnLogin;
    @BindView(R.id.btn_register)
    Button btnRegister;
    //微信登录
    @BindView(R.id.iv_wx_login)
    ImageView ivWxLogin;
    //qq登录
    @BindView(R.id.iv_qq_login)
    ImageView ivQqLogin;


    @Override
    protected int setContentView() {
        return R.layout.activity_xlogin;
    }

    @Override
    protected String settingTitle() {
        return null;
    }

    @Override
    protected void onInitView() {

    }

    @Override
    protected void onInitData() {
        //注册事件
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //注销事件
        EventBus.getDefault().unregister(this);
    }


    /**
     * 点击事件处理
     *
     * @param view
     */
    @OnClick({R.id.btn_login, R.id.btn_register, R.id.iv_wx_login, R.id.iv_qq_login,
            R.id.btn_third_page, R.id.tv_license})
    public void click(View view) {
        switch (view.getId()) {
            //注册
            case R.id.btn_register: {
                //手机号注册
                RegisterPhoneNumActivity.startRegisterPhoneNumActivity(XLoginActivity.this, Dictionary.SmsType_Register, null, null);
                break;
            }
            //登录
            case R.id.btn_login: {
                gotoLoginAccountPage();
                break;
            }
            //微信登录
            case R.id.iv_wx_login: {
                doWxLogin();
                break;
            }
            //QQ登录
            case R.id.iv_qq_login: {
                doQQLogin();
                break;
            }
            //测试按钮
            case R.id.btn_third_page: {
                //开发第三方应用的页面
//                Intent intent = new Intent(XLoginActivity.this, SchemaActivity.class);
//                startActivity(intent);
                //分类对话框
//                new CategoryDialog(XLoginActivity.this,null).show();
                //量表报告对话框（测试）
//                try {
//                    new DimensionReportDialog(XLoginActivity.this).show();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }

                //TextView显示html文本带图片
                Intent intent = new Intent(XLoginActivity.this, TextViewForHtmlImageActivity.class);
                startActivity(intent);

                //家长角色选择页面
//                ParentRoleActivity.startParentRoleActivity(XLoginActivity.this, "我是班级号");

                break;
            }
            //服务条款（用户协议）
            case R.id.tv_license: {
                Intent intent = new Intent(XLoginActivity.this, UserLicenseActivity.class);
                startActivity(intent);
                break;
            }
        }

    }

    /**
     * 微信登录
     */
    private void doWxLogin() {
        //创建微信api并注册到微信
        if (Constant.wx_api == null) {
            Constant.wx_api = WXAPIFactory.createWXAPI(XLoginActivity.this, Constant.WX_APP_ID, true);
            Constant.wx_api.registerApp(Constant.WX_APP_ID);
        }

        //微信必须已经安装
        if(Constant.wx_api.isWXAppInstalled()){
            startWxLogin();
        }else{
            ToastUtil.showShort(getApplicationContext(), "您还未安装微信客户端");
        }
    }

    /**
     * 发起微信登录请求
     */
    private void startWxLogin(){
        //检查网络
        if(!NetworkUtil.isConnectivity(XLoginActivity.this)){
            ToastUtil.showShort(XLoginActivity.this,"网络连接异常");
            return;
        }

        //微信必须已经安装
        if (!Constant.wx_api.isWXAppInstalled()) {
            ToastUtil.showShort(getApplicationContext(), "您还未安装微信客户端");
            return;
        }
        //开启通信等待提示
        LoadingView.getInstance().show(XLoginActivity.this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                final SendAuth.Req req = new SendAuth.Req();
                req.scope = "snsapi_userinfo";
                req.state = "wechat_cheersmind_cheersgenie";
                Constant.wx_api.sendReq(req);
            }
        }).start();
    }

//    @Subscribe(threadMode = ThreadMode.MAIN)
    @Subscribe
    public void onWXTokenEntity(WXLoginEvent event) {
        if("error".equals(event.getCode())){
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    //关闭通信等待提示
                    LoadingView.getInstance().dismiss();
                    ToastUtil.showShort(XLoginActivity.this,"微信授权失败！");
                }
            });
            return;
        }
        //成功
        if(event!=null && !TextUtils.isEmpty(event.getCode())){
            //获取微信token根据返回的code
            getWxToken(event.getCode());
        }
    }

    /**
     * 获取微信token根据code
     * @param code
     */
    private void getWxToken(String code){
        String url = HttpConfig.URL_WX_GET_TOKEN
                .replace("{appid}", Constant.WX_APP_ID)
                .replace("{secret}", Constant.WX_APP_SECTET)
                .replace("{code}", code);

//        LoadingView.getInstance().show(XLoginActivity.this);
        OkHttpClient client = new OkHttpClient();
        //构造Request对象
        //采用建造者模式，链式调用指明进行Get请求,传入Get的请求地址
        Request request = new Request.Builder().get().url(url).build();
        Call call = client.newCall(request);
        //异步调用并设置回调函数
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        LoadingView.getInstance().dismiss();
                        ToastUtil.showShort(getApplicationContext(), "微信授权失败..");
                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
//                LoadingView.getInstance().dismiss();
                final String responseStr = response.body().string();

                Map dataMap = JsonUtil.fromJson(responseStr, Map.class);
                final WXTokenEntity entity = InjectionWrapperUtil.injectMap(dataMap, WXTokenEntity.class);
                if (entity != null && !TextUtils.isEmpty(entity.getAccessToken())) {
                    //获取微信token成功之后的处理
                    doGetWxTokenComplete(entity);
                }
            }
        });
    }

    /**
     * 获取微信token成功之后的处理
     * @param entity 微信token对象
     */
    private void doGetWxTokenComplete(WXTokenEntity entity){
        //第三方登入返回
        if(entity!=null){
            //请求数据
            final ThirdLoginDto thirdLoginDto = new ThirdLoginDto();
            thirdLoginDto.setOpenId(entity.getOpenid());//openId
            thirdLoginDto.setPlatSource(Dictionary.Plat_Source_Weixin);//平台名
            thirdLoginDto.setThirdAccessToken(entity.getAccessToken());//访问token
            thirdLoginDto.setTenant(Dictionary.Tenant_CheersMind);//租户名
            thirdLoginDto.setDeviceType("android");//设备类型
            thirdLoginDto.setDeviceDesc(Build.MODEL);//设备描述（华为 XXXX）
            thirdLoginDto.setDeviceId(DeviceUtil.getDeviceId(getApplicationContext()));//设备ID
            //请求第三方登录
            doThirdLogin(thirdLoginDto);
        }
    }

    /**
     * 第三方登录
     * @param thirdLoginDto 第三方平台登录信息dto
     */
    private void doThirdLogin(final ThirdLoginDto thirdLoginDto) {
        //开启通信等待提示
        LoadingView.getInstance().show(XLoginActivity.this);
        //第三方登录
        DataRequestService.getInstance().postUcThirdLogin(thirdLoginDto, new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                onFailureDefault(e, new FailureDefaultErrorCodeCallBack() {
                    @Override
                    public boolean onErrorCodeCallBack(ErrorCodeEntity errorCodeEntity) {
                        String errorCode = errorCodeEntity.getCode();
                        //第三方账号不存在
                        if (ErrorCode.AC_THIRD_ACCOUNT_NOT_EXIST.equals(errorCode)) {
                            //走手机注册页面流程
                            RegisterPhoneNumActivity.startRegisterPhoneNumActivity(XLoginActivity.this, Dictionary.SmsType_Register, thirdLoginDto, null);
                            //标记已经处理了异常
                            return true;
                        }

                        //标记未处理异常，继续走默认处理流程
                        return false;
                    }
                });

            }

            @Override
            public void onResponse(Object obj) {
                //关闭通信等待提示
//                LoadingView.getInstance().dismiss();
                try {
                    Map dataMap = JsonUtil.fromJson(obj.toString(), Map.class);
                    //解析用户数据
                    final WXUserInfoEntity wxUserInfoEntity = InjectionWrapperUtil.injectMap(dataMap, WXUserInfoEntity.class);
                    //临时缓存用户数据
                    UCManager.getInstance().settingUserInfo(wxUserInfoEntity);
                    //保存用户数据到数据库
                    DataSupport.deleteAll(WXUserInfoEntity.class);
                    wxUserInfoEntity.save();

                    MANService manService = MANServiceProvider.getService();
                    // 用户登录埋点("usernick", "userid")
                    manService.getMANAnalytics().updateUserAccount(wxUserInfoEntity.getUserId() +"", wxUserInfoEntity.getUserId()+"");

                    //未绑定手机号则跳转绑定
                    if (!wxUserInfoEntity.isBindMobile()) {
                        //关闭通信等待提示
                        LoadingView.getInstance().dismiss();
                        //跳转手机号绑定流程
                        RegisterPhoneNumActivity.startRegisterPhoneNumActivity(XLoginActivity.this, Dictionary.SmsType_Bind_Phone_Num, null, null);

                    } else {
                        //获取孩子信息
                        doGetChildListWrap();
                    }

                } catch (Exception e) {
                    onFailure(new QSCustomException("登录失败"));
                }
            }
        });
    }


    /**
     * QQ登录
     */
    private void doQQLogin() {
        if (mTencent == null) {
            mTencent = Tencent.createInstance(Constant.QQ_APP_ID,getApplicationContext());
        }

        //检查网络
        if(!NetworkUtil.isConnectivity(XLoginActivity.this)){
            ToastUtil.showShort(XLoginActivity.this,"网络连接异常");
            return;
        }


        //开启通信等待提示
        LoadingView.getInstance().show(XLoginActivity.this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                //请求登录（get_simple_userinfo、all）
                mTencent.login(XLoginActivity.this, "get_simple_userinfo", loginListener);
            }
        }).start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
            //QQ在某些低端机上调用登录后，由于内存紧张导致APP被系统回收，登录成功后无法成功回传数据。//解决如下
            if (requestCode == Constants.REQUEST_LOGIN) {
                Tencent.onActivityResultData(requestCode,resultCode,data,loginListener);
            }
//        Tencent.onActivityResultData(requestCode, resultCode, data, loginListener);
//
//        if(requestCode == Constants.REQUEST_API) {
//            if(resultCode == Constants.REQUEST_LOGIN) {
//                Tencent.handleResultData(data, loginListener);
//            }
//        }
    }

    /**
     * 获取QQ token成功之后的处理
     * @param entity 微信token对象
     */
    private void doGetQqTokenComplete(QQTokenEntity entity){
        //第三方登入返回
        if(entity!=null){
            //请求数据
            final ThirdLoginDto thirdLoginDto = new ThirdLoginDto();
            thirdLoginDto.setOpenId(entity.getOpenid());
            thirdLoginDto.setPlatSource(Dictionary.Plat_Source_QQ);
            thirdLoginDto.setThirdAccessToken(entity.getAccessToken());
            thirdLoginDto.setTenant(Dictionary.Tenant_CheersMind);
            thirdLoginDto.setAppId(Constant.QQ_APP_ID);
            //请求第三方登录
            doThirdLogin(thirdLoginDto);
        }
    }

    /**
     * QQ登录监听
     */
    IUiListener loginListener = new BaseUiListener() {
        @Override
        protected void doComplete(JSONObject values) {
            initOpenidAndToken(values);
//            updateUserInfo();
//            updateLoginButton();
            //解析QQ token对象
            Map dataMap = JsonUtil.fromJson(values.toString(), Map.class);
            QQTokenEntity entity = InjectionWrapperUtil.injectMap(dataMap, QQTokenEntity.class);
            //获取QQ token成功之后的处理
            doGetQqTokenComplete(entity);
        }
    };

    /**
     * 初始化QQ的token等信息
     * @param jsonObject
     */
    public static void initOpenidAndToken(JSONObject jsonObject) {
        try {
            String token = jsonObject.getString(Constants.PARAM_ACCESS_TOKEN);
            String expires = jsonObject.getString(Constants.PARAM_EXPIRES_IN);
            String openId = jsonObject.getString(Constants.PARAM_OPEN_ID);
            if (!TextUtils.isEmpty(token) && !TextUtils.isEmpty(expires)
                    && !TextUtils.isEmpty(openId)) {
                mTencent.setAccessToken(token, expires);
                mTencent.setOpenId(openId);
            }
        } catch(Exception e) {
        }
    }

    /**
     * QQ登录监听器
     */
    private class BaseUiListener implements IUiListener {

        @Override
        public void onComplete(Object response) {
            //关闭通信等待提示
//            LoadingView.getInstance().dismiss();

            if (null == response) {
                ToastUtil.showShort(getApplicationContext(), "登录失败");
                return;
            }
            JSONObject jsonResponse = (JSONObject) response;
            if (null != jsonResponse && jsonResponse.length() == 0) {
                ToastUtil.showShort(getApplicationContext(), "未返回数据，登录失败");
                return;
            }

//            ToastUtil.showShort(getApplicationContext(), "登录成功");
            doComplete(jsonResponse);
        }

        protected void doComplete(JSONObject values) {

        }

        @Override
        public void onError(UiError e) {
            //关闭通信等待提示
            LoadingView.getInstance().dismiss();
            ToastUtil.showShort(getApplicationContext(), "QQ登录失败");
        }

        @Override
        public void onCancel() {
            //关闭通信等待提示
            LoadingView.getInstance().dismiss();
//            ToastUtil.showShort(getApplicationContext(), "取消登录");
        }
    }


    /**
     * 跳转到账号登录页面
     */
    private void gotoLoginAccountPage() {
        Intent intent = new Intent(XLoginActivity.this, XLoginAccountActivity.class);
        startActivity(intent);
    }


}
