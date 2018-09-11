package com.cheersmind.cheersgenie.features.modules.login.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.sdk.android.man.MANService;
import com.alibaba.sdk.android.man.MANServiceProvider;
import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.constant.Dictionary;
import com.cheersmind.cheersgenie.features.constant.ErrorCode;
import com.cheersmind.cheersgenie.features.dto.AccountLoginDto;
import com.cheersmind.cheersgenie.features.dto.PhoneNumLoginDto;
import com.cheersmind.cheersgenie.features.dto.ThirdLoginDto;
import com.cheersmind.cheersgenie.features.modules.base.activity.BaseActivity;
import com.cheersmind.cheersgenie.features.modules.base.activity.MasterTabActivity;
import com.cheersmind.cheersgenie.features.modules.register.activity.RegisterPhoneNumActivity;
import com.cheersmind.cheersgenie.features.utils.DeviceUtil;
import com.cheersmind.cheersgenie.features.utils.NetworkUtil;
import com.cheersmind.cheersgenie.features.utils.SoftInputUtil;
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
import com.cheersmind.cheersgenie.main.util.ToastUtil;
import com.cheersmind.cheersgenie.main.view.LoadingView;
import com.cheersmind.cheersgenie.module.login.UCManager;
import com.cheersmind.cheersgenie.module.login.UserService;
import com.tencent.connect.common.Constants;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
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
public class XLoginAccountActivity extends BaseActivity {

    @BindView(R.id.et_username)
    EditText etUsername;
    @BindView(R.id.et_password)
    EditText etPassword;
    @BindView(R.id.cbox_password)
    CheckBox cboxPassword;
    @BindView(R.id.btn_login)
    Button btnLogin;
    @BindView(R.id.tv_phonenum_login)
    TextView tvPhonenumLogin;
    @BindView(R.id.iv_wx_login)
    ImageView ivWxLogin;
    @BindView(R.id.iv_qq_login)
    ImageView ivQqLogin;

    @Override
    protected int setContentView() {
        return R.layout.activity_xlogin_account;
    }

    @Override
    protected String settingTitle() {
        return "账号登录";
    }

    @Override
    protected void onInitView() {
        //密码显隐
        cboxPassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    //如果选中，显示密码
                    etPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    //否则隐藏密码
                    etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
                //光标置于末尾
                etPassword.setSelection(etPassword.getText().length());
            }
        });


        //监听密码输入框的软键盘回车键
        etPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                //当actionId == XX_SEND 或者 XX_DONE时都触发
                //或者event.getKeyCode == ENTER 且 event.getAction == ACTION_DOWN时也触发
                //注意，这是一定要判断event != null。因为在某些输入法上会返回null。
                if (actionId == EditorInfo.IME_ACTION_SEND
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || (keyEvent != null && KeyEvent.KEYCODE_ENTER == keyEvent.getKeyCode() && KeyEvent.ACTION_DOWN == keyEvent.getAction())) {
                    //登录
                    doAccountLoginWrap();
                }

                return false;
            }
        });

        //初始化缓存的用户名和密码到页面中
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        String userNameLocal = pref.getString("user_name", "");
        String passwordLocal = pref.getString("user_password", "");
        etUsername.setText(userNameLocal);
        if (!TextUtils.isEmpty(passwordLocal)) {
            etUsername.setSelection(userNameLocal.length());//将光标移至文字末尾
        }
        etPassword.setText(passwordLocal);
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
    @OnClick({R.id.btn_login, R.id.tv_phonenum_login, R.id.tv_retrieve_password, R.id.iv_wx_login, R.id.iv_qq_login})
    public void click(View view) {
        switch (view.getId()) {
            //手机号快捷登录
            case R.id.tv_phonenum_login: {
                Intent intent = new Intent(XLoginAccountActivity.this, PhoneNumLoginActivity.class);
                startActivity(intent);
                break;
            }
            //找回密码
            case R.id.tv_retrieve_password: {
                RegisterPhoneNumActivity.startRegisterPhoneNumActivity(XLoginAccountActivity.this, Dictionary.SmsType_Retrieve_Password, null);
                break;
            }
            //登录
            case R.id.btn_login: {
                doAccountLoginWrap();
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
        }

    }

    /**
     * 账号登录
     */
    private void doAccountLoginWrap() {
        //数据校验
//        if (!checkDataForPhoneNumLogin()) {
//            return;
//        }

        //请求账号登录
        String loginName = etUsername.getText().toString();
        String pwd = etPassword.getText().toString();
        AccountLoginDto accountLoginDto = new AccountLoginDto();
        accountLoginDto.setAccount(loginName);//用户名
        accountLoginDto.setPassword(pwd);//密码
        accountLoginDto.setTenant(Dictionary.Tenant_CheersMind);//租户名
        accountLoginDto.setDeviceType("android");//设备类型
//        accountLoginDto.setDeviceDesc(Build.MODEL);//设备描述（华为 XXXX）
        accountLoginDto.setDeviceDesc("android phone");//设备描述（华为 XXXX）
        accountLoginDto.setDeviceId(DeviceUtil.getDeviceId(getApplicationContext()));//设备ID
        doAccountLogin(accountLoginDto);
    }


    /**
     * 账号登录
     * @param accountLoginDto
     */
    private void doAccountLogin(final AccountLoginDto accountLoginDto) {
        //关闭软键盘
        SoftInputUtil.closeSoftInput(XLoginAccountActivity.this);
        //开启通信等待提示
        LoadingView.getInstance().show(this);
        //账号登录
        DataRequestService.getInstance().postAccountLogin(accountLoginDto, new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                onFailureDefault(e, new FailureDefaultErrorCodeCallBack() {
                    @Override
                    public boolean onErrorCodeCallBack(ErrorCodeEntity errorCodeEntity) {
                        String errorCode = errorCodeEntity.getCode();
                        //账号不存在
                        if (ErrorCode.AC_ACCOUNT_NOT_EXIST.equals(errorCode)) {
                            ToastUtil.showShort(getApplicationContext(), "用户名或密码错误");
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
                LoadingView.getInstance().dismiss();
                try {
                    Map dataMap = JsonUtil.fromJson(obj.toString(), Map.class);
                    //解析用户数据
                    final WXUserInfoEntity wxUserInfoEntity = InjectionWrapperUtil.injectMap(dataMap, WXUserInfoEntity.class);
                    //临时缓存用户数据
                    UCManager.getInstance().settingUserInfo(wxUserInfoEntity);
                    //保存用户名和密码到缓存中
                    saveUserAccount(etUsername.getText().toString(), etPassword.getText().toString());
                    //保存用户数据到数据库
                    DataSupport.deleteAll(WXUserInfoEntity.class);
                    wxUserInfoEntity.save();

                    //第三方的统计操作
                    //当用户使用自有账号登录时，可以这样统计：
                    //MobclickAgent.onProfileSignIn(String.valueOf(userId));
                    //当用户使用第三方账号（如新浪微博）登录时，可以这样统计：
                    //MobclickAgent.onProfileSignIn("WB", "userID");
                    //……

                    //未绑定手机号则跳转绑定
                    if (!wxUserInfoEntity.isBindMobile()) {
                        //跳转手机号绑定流程
                        RegisterPhoneNumActivity.startRegisterPhoneNumActivity(XLoginAccountActivity.this, Dictionary.SmsType_Bind_Phone_Num, null);

                    } else {
                        //获取孩子信息
                        doGetChildListWrap();
                    }
                    MANService manService = MANServiceProvider.getService();
                    // 用户登录埋点("usernick", "userid")
                    manService.getMANAnalytics().updateUserAccount(wxUserInfoEntity.getUserId() +"", wxUserInfoEntity.getUserId()+"");

                    //获取孩子信息
//                    doGetChildListWrap();

                } catch (Exception e) {
                    //完善用户信息（生成孩子）
//                    gotoPerfectUserInfo(PhoneNumLoginActivity.this);
                    onFailure(new QSCustomException("登录失败"));
                }
            }
        });
    }



    /**
     * 本地缓存用户名和密码
     *
     * @param userName
     * @param password
     */
    private void saveUserAccount(String userName, String password) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("user_name", userName);
        editor.putString("user_password", password);
        editor.commit();
    }



    /**
     * 微信登录
     */
    private void doWxLogin() {
        //创建微信api并注册到微信
        if (Constant.wx_api == null) {
            Constant.wx_api = WXAPIFactory.createWXAPI(XLoginAccountActivity.this, Constant.WX_APP_ID, true);
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
        if(!NetworkUtil.isConnectivity(XLoginAccountActivity.this)){
            ToastUtil.showShort(XLoginAccountActivity.this,"网络连接异常");
            return;
        }

        //微信必须已经安装
        if (!Constant.wx_api.isWXAppInstalled()) {
            ToastUtil.showShort(getApplicationContext(), "您还未安装微信客户端");
            return;
        }
        //开启通信等待提示
        LoadingView.getInstance().show(XLoginAccountActivity.this);
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
    @Subscribe(priority = 1)
    public void onWXTokenEntity(WXLoginEvent event) {
        //取消事件继续往下传送
        EventBus.getDefault().cancelEventDelivery(event) ;

        if("error".equals(event.getCode())){
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    //关闭通信等待提示
                    LoadingView.getInstance().dismiss();
                    ToastUtil.showShort(XLoginAccountActivity.this,"微信授权失败！");
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
                LoadingView.getInstance().dismiss();
//                finish();
                ToastUtil.showShort(getApplicationContext(), "微信授权失败..");
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
        LoadingView.getInstance().show(XLoginAccountActivity.this);
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
                            RegisterPhoneNumActivity.startRegisterPhoneNumActivity(XLoginAccountActivity.this, Dictionary.SmsType_Register, thirdLoginDto);
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
                LoadingView.getInstance().dismiss();
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
                        //跳转手机号绑定流程
                        RegisterPhoneNumActivity.startRegisterPhoneNumActivity(XLoginAccountActivity.this, Dictionary.SmsType_Bind_Phone_Num, null);

                    } else {
                        //获取孩子信息
                        doGetChildListWrap();
                    }

                } catch (Exception e) {
                    ErrorCodeEntity errorCodeEntity = new ErrorCodeEntity();
                    errorCodeEntity.setMessage("登录失败");
                    String errorStr = JsonUtil.toJson(errorCodeEntity);
                    onFailure(new QSCustomException(errorStr));
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
        if(!NetworkUtil.isConnectivity(XLoginAccountActivity.this)){
            ToastUtil.showShort(XLoginAccountActivity.this,"网络连接异常");
            return;
        }


        //开启通信等待提示
        LoadingView.getInstance().show(XLoginAccountActivity.this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                //请求登录（get_simple_userinfo、all）
                mTencent.login(XLoginAccountActivity.this, "get_simple_userinfo", loginListener);
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

}
