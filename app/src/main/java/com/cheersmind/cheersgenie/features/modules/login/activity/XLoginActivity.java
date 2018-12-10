package com.cheersmind.cheersgenie.features.modules.login.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.text.TextUtils;
import android.util.DisplayMetrics;
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
import com.cheersmind.cheersgenie.features.modules.register.activity.RegisterPhoneNumActivity;
import com.cheersmind.cheersgenie.features.modules.test.activity.EditTextPasteActivity;
import com.cheersmind.cheersgenie.features.utils.DeviceUtil;
import com.cheersmind.cheersgenie.features.utils.NetworkUtil;
import com.cheersmind.cheersgenie.main.Exception.QSCustomException;
import com.cheersmind.cheersgenie.main.QSApplication;
import com.cheersmind.cheersgenie.main.constant.Constant;
import com.cheersmind.cheersgenie.main.entity.ErrorCodeEntity;
import com.cheersmind.cheersgenie.main.entity.QQTokenEntity;
import com.cheersmind.cheersgenie.main.entity.WXTokenEntity;
import com.cheersmind.cheersgenie.main.entity.WXUserInfoEntity;
import com.cheersmind.cheersgenie.main.event.WXLoginEvent;
import com.cheersmind.cheersgenie.main.service.BaseService;
import com.cheersmind.cheersgenie.main.service.DataRequestService;
import com.cheersmind.cheersgenie.main.util.InjectionWrapperUtil;
import com.cheersmind.cheersgenie.main.util.JsonUtil;
import com.cheersmind.cheersgenie.main.util.ToastUtil;
import com.cheersmind.cheersgenie.main.view.LoadingView;
import com.cheersmind.cheersgenie.module.login.UCManager;
import com.cheersmind.cheersgenie.module.login.UserLicenseActivity;
import com.tencent.connect.common.Constants;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

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
        //测试
        DisplayMetrics metrics = QSApplication.getMetrics();
        Configuration configuration = getResources().getConfiguration();
        System.out.println("XLoginActivity：我是测试啊【DisplayMetrics】【Configuration】");
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
    public void onViewClick(View view) {
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
//                Intent intent = new Intent(XLoginActivity.this, TextViewForHtmlImageActivity.class);
//                startActivity(intent);

                //家长角色选择页面
//                ParentRoleActivity.startParentRoleActivity(XLoginActivity.this, "我是班级号");

                //完善信息页面
//                Intent intent = new Intent(XLoginActivity.this, UserInfoInitActivity.class);
//                startActivity(intent);

                //重新安装Apk
//                String mFile = "/storage/emulated/0/Android/data/com.cheersmind.cheersgenie.debug/cache/com.cheersmind.cheersgenie.debug.apk";
//                PackageUtils.installPackage(QSApplication.getContext(), new File(mFile));

                //Gif测试页面
//                Intent intent = new Intent(XLoginActivity.this, GifActivity.class);
//                startActivity(intent);

                //崩溃日志是否会上传的测试
//                throw new NullPointerException("666666666666666666666666666我是空啊");

                //积分提示对话框
//                new IntegralTipDialog(XLoginActivity.this, 16,null).show();

                //SpannableString测试（文字后面最后一个字跟图标按钮）
//                Intent intent = new Intent(XLoginActivity.this, SpannableStringActivity.class);
//                startActivity(intent);

                //编辑框粘贴
                Intent intent = new Intent(XLoginActivity.this, EditTextPasteActivity.class);
                startActivity(intent);

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
            ToastUtil.showShort(getApplication(), "您还未安装微信客户端");
        }
    }

    /**
     * 发起微信登录请求
     */
    private void startWxLogin(){
        //检查网络
        if(!NetworkUtil.isConnectivity(XLoginActivity.this)){
            ToastUtil.showShort(getApplication(),getResources().getString(R.string.network_no));
            return;
        }

        //微信必须已经安装
        if (!Constant.wx_api.isWXAppInstalled()) {
            ToastUtil.showShort(getApplication(), "您还未安装微信客户端");
            return;
        }
        //开启通信等待提示
        LoadingView.getInstance().show(XLoginActivity.this, httpTag);
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
            getHandler().post(new Runnable() {
                @Override
                public void run() {
                    //关闭通信等待提示
                    LoadingView.getInstance().dismiss();
                    ToastUtil.showShort(getApplication(),"微信授权失败！");
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

        DataRequestService.getInstance().getWeChartToken(Constant.WX_APP_ID, Constant.WX_APP_SECTET, code, new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                getHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        LoadingView.getInstance().dismiss();
                        ToastUtil.showShort(getApplication(), "微信授权失败..");
                    }
                });
            }

            @Override
            public void onResponse(Object obj) {
                try {
                    final String responseStr = obj.toString();

                    Map dataMap = JsonUtil.fromJson(responseStr, Map.class);
                    final WXTokenEntity entity = InjectionWrapperUtil.injectMap(dataMap, WXTokenEntity.class);
                    if (entity != null && !TextUtils.isEmpty(entity.getAccessToken())) {
                        //获取微信token成功之后的处理
                        doGetWxTokenComplete(entity);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    onFailure(new QSCustomException("微信授权失败..."));
                }
            }
        }, httpTag, XLoginActivity.this);
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
            thirdLoginDto.setDeviceId(DeviceUtil.getDeviceId(getApplication()));//设备ID
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
        LoadingView.getInstance().show(XLoginActivity.this, httpTag);
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

                    //登录统计
                    MANService manService = MANServiceProvider.getService();
                    // 用户登录埋点("usernick", "userid")
                    manService.getMANAnalytics().updateUserAccount(wxUserInfoEntity.getUserId() +"", wxUserInfoEntity.getUserId()+"");

                    //友盟统计：当用户使用第三方账号（如新浪微博）登录时，可以这样统计：
                    MobclickAgent.onProfileSignIn(thirdLoginDto.getPlatSource(), String.valueOf(wxUserInfoEntity.getUserId()));

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
        }, httpTag, XLoginActivity.this);
    }


    /**
     * QQ登录
     */
    private void doQQLogin() {
        if (mTencent == null) {
            mTencent = Tencent.createInstance(Constant.QQ_APP_ID,getApplication());
        }

        //检查网络
        if(!NetworkUtil.isConnectivity(XLoginActivity.this)){
            ToastUtil.showShort(getApplication(),getResources().getString(R.string.network_no));
            return;
        }


        //开启通信等待提示
        LoadingView.getInstance().show(XLoginActivity.this, httpTag);
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
                ToastUtil.showShort(getApplication(), "登录失败");
                return;
            }
            JSONObject jsonResponse = (JSONObject) response;
            if (null != jsonResponse && jsonResponse.length() == 0) {
                ToastUtil.showShort(getApplication(), "未返回数据，登录失败");
                return;
            }

//            ToastUtil.showShort(getApplication(), "登录成功");
            doComplete(jsonResponse);
        }

        protected void doComplete(JSONObject values) {

        }

        @Override
        public void onError(UiError e) {
            //关闭通信等待提示
            LoadingView.getInstance().dismiss();
            ToastUtil.showShort(getApplication(), "QQ登录失败");
        }

        @Override
        public void onCancel() {
            //关闭通信等待提示
            LoadingView.getInstance().dismiss();
//            ToastUtil.showShort(getApplication(), "取消登录");
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
