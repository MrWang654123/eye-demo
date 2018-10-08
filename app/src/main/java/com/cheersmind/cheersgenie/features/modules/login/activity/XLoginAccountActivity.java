package com.cheersmind.cheersgenie.features.modules.login.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.sdk.android.man.MANService;
import com.alibaba.sdk.android.man.MANServiceProvider;
import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.constant.Dictionary;
import com.cheersmind.cheersgenie.features.constant.ErrorCode;
import com.cheersmind.cheersgenie.features.dto.AccountLoginDto;
import com.cheersmind.cheersgenie.features.dto.CreateSessionDto;
import com.cheersmind.cheersgenie.features.dto.ThirdLoginDto;
import com.cheersmind.cheersgenie.features.dto.ThirdPlatBindDto;
import com.cheersmind.cheersgenie.features.entity.SessionCreateResult;
import com.cheersmind.cheersgenie.features.interfaces.OnResultListener;
import com.cheersmind.cheersgenie.features.modules.base.activity.BaseActivity;
import com.cheersmind.cheersgenie.features.modules.mine.activity.AccountBindActivity;
import com.cheersmind.cheersgenie.features.modules.register.activity.RegisterPhoneNumActivity;
import com.cheersmind.cheersgenie.features.utils.DeviceUtil;
import com.cheersmind.cheersgenie.features.utils.NetworkUtil;
import com.cheersmind.cheersgenie.features.utils.SoftInputUtil;
import com.cheersmind.cheersgenie.main.Exception.QSCustomException;
import com.cheersmind.cheersgenie.main.constant.Constant;
import com.cheersmind.cheersgenie.main.constant.HttpConfig;
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
import com.tencent.connect.common.Constants;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.cheersmind.cheersgenie.main.constant.Constant.mTencent;

/**
 * 登陆页面（传递数据中无第三方登录信息就是正常登录操作，有则是绑定账号操作）
 */
public class XLoginAccountActivity extends BaseActivity {

    //刷新图形验证码
    private static final int MSG_REFRESH_IMAGE_CAPTCHA = 1;
    //需要图形验证码
    private static final int MSG_REQUIRED_IMAGE_CAPTCHA = 2;

    //绑定的手机号提示
    @BindView(R.id.tv_bind_phone_num_tip)
    TextView tvBindPhoneNumTip;
    //用户名布局
    @BindView(R.id.rl_user_name)
    LinearLayout rlUserName;
    //用户名
    @BindView(R.id.et_username)
    EditText etUsername;
    //用户名的清空按钮
    @BindView(R.id.iv_clear)
    ImageView ivClear;
    //密码
    @BindView(R.id.et_password)
    EditText etPassword;
    //密码显隐
    @BindView(R.id.cbox_password)
    CheckBox cboxPassword;
    //密码的清空按钮
    @BindView(R.id.iv_clear_pw)
    ImageView ivClearPw;
    @BindView(R.id.btn_login)
    Button btnLogin;
    //手机号短信快捷登录
    @BindView(R.id.tv_phonenum_login)
    TextView tvPhonenumLogin;
    //第三方账号布局
    @BindView(R.id.ll_third_login)
    LinearLayout llThirdLogin;
    //微信登录
    @BindView(R.id.iv_wx_login)
    ImageView ivWxLogin;
    //QQ登录
    @BindView(R.id.iv_qq_login)
    ImageView ivQqLogin;

    @BindView(R.id.et_image_captcha)
    EditText etImageCaptcha;
    @BindView(R.id.iv_image_captcha)
    ImageView ivImageCaptcha;
    @BindView(R.id.rl_image_captcha)
    RelativeLayout rlImageCaptcha;

    //手机号
    private static final String PHONE_NUM = "phone_num";
    //第三方平台登录信息Dto
    private static final String THIRD_LOGIN_DTO = "thirdLoginDto";
    //第三方平台登录信息
    private ThirdLoginDto thirdLoginDto;
    //手机号
    private String phoneNum;
    //是否已经绑定成功
    private boolean hasBindSuccess;


    /**
     * 打开账号登录页面（无第三方登录信息就是正常登录操作，有则是绑定账号操作）
     * @param context
     * @param thirdLoginDto 第三方平台登录信息
     * @param phoneNum 手机号
     */
    public static void startXLoginAccountActivity(Context context, ThirdLoginDto thirdLoginDto, String phoneNum) {
        Intent intent = new Intent(context, XLoginAccountActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        Bundle extras = new Bundle();
        extras.putSerializable(THIRD_LOGIN_DTO, thirdLoginDto);
        extras.putString(PHONE_NUM, phoneNum);
        intent.putExtras(extras);
        context.startActivity(intent);
    }


    @Override
    protected int setContentView() {
        return R.layout.activity_xlogin_account;
    }

    @Override
    protected String settingTitle() {
        return "密码登录";
    }

    @Override
    protected void onInitView() {
        //用户名的文本输入监听
        etUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    if (ivClear.getVisibility() == View.INVISIBLE) {
                        ivClear.setVisibility(View.VISIBLE);
                    }
                } else {
                    if (ivClear.getVisibility() == View.VISIBLE) {
                        ivClear.setVisibility(View.INVISIBLE);
                    }
                }
            }
        });

        etPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    if (ivClearPw.getVisibility() == View.INVISIBLE) {
                        ivClearPw.setVisibility(View.VISIBLE);
                    }
                } else {
                    if (ivClearPw.getVisibility() == View.VISIBLE) {
                        ivClearPw.setVisibility(View.INVISIBLE);
                    }
                }
            }
        });

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
        etPassword.setText(passwordLocal);
        //设置焦点：没有密码时聚焦密码框，否则聚焦账号框
        if (!TextUtils.isEmpty(userNameLocal) && TextUtils.isEmpty(passwordLocal)) {
            etPassword.requestFocus();
            etPassword.setSelection(passwordLocal.length());//将光标移至文字末尾
        } else {
            etUsername.requestFocus();
            etUsername.setSelection(userNameLocal.length());//将光标移至文字末尾
        }

    }

    @Override
    protected void onInitData() {
        //注册事件
        EventBus.getDefault().register(this);

        //第三方登录信息
        if (getIntent() != null || getIntent().getExtras() != null) {
            thirdLoginDto = (ThirdLoginDto) getIntent().getSerializableExtra(THIRD_LOGIN_DTO);
            phoneNum = getIntent().getStringExtra(PHONE_NUM);

            if (thirdLoginDto != null) {
                if (TextUtils.isEmpty(phoneNum)) {
                    ToastUtil.showShort(getApplicationContext(), "绑定操作，手机号不能为空");
                    finish();
                    return;
                }
                //改变正常登录为绑定操作的视图
                changeViewForBind();
            }
        }

        //创建会话：类型账号登陆
//        doPostAccountSessionForImageCaptcha(Dictionary.CREATE_SESSION_ACCOUNT_LOGIN);
    }

    /**
     * 改变正常登录为绑定操作的视图
     */
    private void changeViewForBind() {
        //设置title
        if (tvToolbarTitle != null) {
            tvToolbarTitle.setText("绑定手机号");
        }
        //显示绑定的手机号提示
        tvBindPhoneNumTip.setVisibility(View.VISIBLE);
        tvBindPhoneNumTip.setText(getResources().getString(R.string.bind_phone_num_tip, phoneNum));
        //按钮显示为绑定
        btnLogin.setText(getResources().getString(R.string.bind_phone_num));
        //清空且聚焦密码
        etPassword.requestFocus();
        etPassword.setText("");
        etPassword.setSelection(etPassword.getText().length());

        //隐藏用户名布局
        rlUserName.setVisibility(View.GONE);
        //用户名设置为手机号（用户名此时是隐藏的）
        etUsername.setText(phoneNum);
        //隐藏手机短信快捷登录
        tvPhonenumLogin.setVisibility(View.GONE);
        //隐藏第三方登录
        llThirdLogin.setVisibility(View.GONE);
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
    @OnClick({R.id.btn_login, R.id.tv_phonenum_login, R.id.tv_retrieve_password, R.id.iv_wx_login,
            R.id.iv_qq_login, R.id.iv_image_captcha, R.id.iv_clear, R.id.iv_clear_pw})
    public void onClickView(View view) {
        switch (view.getId()) {
            //手机号快捷登录
            case R.id.tv_phonenum_login: {
                Intent intent = new Intent(XLoginAccountActivity.this, PhoneNumLoginActivity.class);
                startActivity(intent);
                break;
            }
            //找回密码
            case R.id.tv_retrieve_password: {
                RegisterPhoneNumActivity.startRegisterPhoneNumActivity(XLoginAccountActivity.this, Dictionary.SmsType_Retrieve_Password, null, phoneNum);
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
            //图形验证码
            case R.id.iv_image_captcha: {
                //请求图形验证码
                if (sessionCreateResult != null) {
                    getImageCaptcha(sessionCreateResult.getSessionId(), null);
                }
                break;
            }
            //用户名的清空按钮
            case R.id.iv_clear: {
                etUsername.setText("");
                etUsername.requestFocus();
                break;
            }
            //密码的清空按钮
            case R.id.iv_clear_pw: {
                etPassword.setText("");
                etPassword.requestFocus();
                break;
            }
        }

    }

    /**
     * 账号登录
     */
    private void doAccountLoginWrap() {
        //隐藏软键盘
        SoftInputUtil.closeSoftInput(this);

        //数据校验
        if (!checkData()) {
            return;
        }

        //账号登录必须要有sessionId
        if (sessionCreateResult == null || TextUtils.isEmpty(sessionCreateResult.getSessionId())) {
            //创建会话然后直接登录
            LoadingView.getInstance().show(this);
            doPostAccountSessionForAccountLogin(false);
            return;
        }

        //请求账号登录
        String loginName = etUsername.getText().toString();
        String pwd = etPassword.getText().toString();
        AccountLoginDto accountLoginDto = new AccountLoginDto();
        accountLoginDto.setAccount(loginName);//用户名
        accountLoginDto.setPassword(pwd);//密码
        accountLoginDto.setSessionId(sessionCreateResult.getSessionId());//会话ID
        //图形验证码
        if (!sessionCreateResult.getNormal()) {
            accountLoginDto.setVerificationCode(etImageCaptcha.getText().toString());
        }
        accountLoginDto.setTenant(Dictionary.Tenant_CheersMind);//租户名
        accountLoginDto.setDeviceType("android");//设备类型
//        accountLoginDto.setDeviceDesc(Build.MODEL);//设备描述（华为 XXXX）
        accountLoginDto.setDeviceDesc("android phone");//设备描述（华为 XXXX）
        accountLoginDto.setDeviceId(DeviceUtil.getDeviceId(getApplicationContext()));//设备ID
        doAccountLogin(accountLoginDto);
    }

    /**
     * 校验数据格式
     * @return
     */
    private boolean checkData() {
        //用户名
        String userName = etUsername.getText().toString();
        if (TextUtils.isEmpty(userName)) {
            ToastUtil.showShort(getApplicationContext(), "请输入用户名");
            return false;
        }

        //密码
        String password = etPassword.getText().toString();
        if (TextUtils.isEmpty(password)) {
            ToastUtil.showShort(getApplicationContext(), "请输入密码");
            return false;
        }

        //图形验证码
        if (sessionCreateResult != null && !sessionCreateResult.getNormal()) {
            String imageCaptcha = etImageCaptcha.getText().toString();
            if (TextUtils.isEmpty(imageCaptcha)) {
                //需要图形验证码
                Message.obtain(mHandler, MSG_REQUIRED_IMAGE_CAPTCHA).sendToTarget();
                return false;
            }
        }

        return true;
    }


    /**
     * 账号登录
     *
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
                        //账号不存在、密码不正确
                        if (ErrorCode.AC_ACCOUNT_NOT_EXIST.equals(errorCode)
                                || ErrorCode.AC_WRONG_PASSWORD.equals(errorCode)) {
                            ToastUtil.showShort(getApplicationContext(), "用户名或密码错误");
                            //重新获取会话，处理是否需要图形验证码
//                            sessionCreateResult = null;
//                            doPostAccountSessionForImageCaptcha(false);

                            //如果当前状态为不正常，则重置图形验证码
                            if (sessionCreateResult != null && !sessionCreateResult.getNormal()) {
                                //请求图形验证码
                                getImageCaptcha(sessionCreateResult.getSessionId(), null);
                                //清空图形验证码，并聚焦图形验证码
                                etImageCaptcha.setText("");
//                                etImageCaptcha.requestFocus();
                            }

                            //标记已经处理了异常
                            return true;

                        } else if (ErrorCode.AC_SESSION_EXPIRED.equals(errorCode) || ErrorCode.AC_SESSION_INVALID.equals(errorCode)) {//Session 未创建或已过期、无效
                            //重新获取会话
                            sessionCreateResult = null;
                            //创建会话后直接登录
                            LoadingView.getInstance().show(XLoginAccountActivity.this);
                            doPostAccountSessionForAccountLogin(false);

                            //标记已经处理了异常
                            return true;

                        } else if (ErrorCode.AC_IDENTIFY_CODE_REQUIRED.equals(errorCode)) {//需要图形验证码
                            //如果当前状态为正常，则置为不正常
                            if (sessionCreateResult.getNormal()) {
                                sessionCreateResult.setNormal(false);
                            }
                            //开启通信等待
                            LoadingView.getInstance().show(XLoginAccountActivity.this);
                            //请求图形验证码
                            getImageCaptcha(sessionCreateResult.getSessionId(), new OnResultListener() {

                                @Override
                                public void onSuccess(Object... objects) {
                                    //关闭通信等待
                                    LoadingView.getInstance().dismiss();
                                    //需要图形验证码，才能登录的提示处理
                                    requiredImageCaptchaForSendError();
                                }

                                @Override
                                public void onFailed(Object... objects) {

                                }
                            });

                            //标记已经处理了异常
                            return true;

                        } else if (ErrorCode.AC_IDENTIFY_CODE_INVALID.equals(errorCode)) {//无效的验证码
                            //清空并聚焦
                            etImageCaptcha.setText("");
                            etImageCaptcha.requestFocus();
                            //请求图形验证码
                            if (sessionCreateResult != null) {
                                getImageCaptcha(sessionCreateResult.getSessionId(), null);
                            }

                            //标记未处理异常，采用服务端返回的提示
                            return false;
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
                    //保存用户名和密码到缓存中
                    saveUserAccount(accountLoginDto.getAccount(), accountLoginDto.getPassword());
                    //保存用户数据到数据库
                    DataSupport.deleteAll(WXUserInfoEntity.class);
                    wxUserInfoEntity.save();

                    //正常的登录
                    if (thirdLoginDto == null) {
                        //未绑定手机号则跳转绑定
                        if (!wxUserInfoEntity.isBindMobile()) {
                            //关闭通信等待提示
                            LoadingView.getInstance().dismiss();
                            //跳转手机号绑定流程
                            RegisterPhoneNumActivity.startRegisterPhoneNumActivity(XLoginAccountActivity.this, Dictionary.SmsType_Bind_Phone_Num, null, null);

                        } else {
                            //获取孩子信息
                            doGetChildListWrap();
                        }

                    } else {//绑定操作
                        //已经绑定成功则进入正常登录成功后的流程
                        if (hasBindSuccess) {
                            //获取孩子信息
                            doGetChildListWrap();

                        } else {//未绑定则进行绑定操作
                            ThirdPlatBindDto dto = new ThirdPlatBindDto();
                            dto.setOpenId(thirdLoginDto.getOpenId());//openId
                            dto.setPlatSource(thirdLoginDto.getPlatSource());//平台名称
                            //QQ平台需要传APP_ID
                            if (Dictionary.Plat_Source_QQ.equals(thirdLoginDto.getPlatSource())) {
                                dto.setAppId(Constant.QQ_APP_ID);
                            }
                            dto.setThirdAccessToken(thirdLoginDto.getThirdAccessToken());//访问token
                            dto.setTenant(thirdLoginDto.getTenant());//租户名
                            //请求绑定
                            doThirdPlatBind(dto);
                        }
                    }

                    //登录统计
                    MANService manService = MANServiceProvider.getService();
                    // 用户登录埋点("usernick", "userid")
                    manService.getMANAnalytics().updateUserAccount(wxUserInfoEntity.getUserId() + "", wxUserInfoEntity.getUserId() + "");

                    //获取孩子信息
//                    doGetChildListWrap();

                } catch (Exception e) {
                    //完善用户信息（生成孩子）
//                    gotoPerfectUserInfo(PhoneNumLoginActivity.this);
                    onFailure(new QSCustomException(getResources().getString(R.string.operate_fail)));
                }
            }
        });
    }


    /**
     * 请求第三方平台账号绑定
     * @param bindDto
     */
    private void doThirdPlatBind(final ThirdPlatBindDto bindDto) {
        LoadingView.getInstance().show(XLoginAccountActivity.this);
        //绑定第三方平台账号
        DataRequestService.getInstance().postThirdPlatBind(bindDto, new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                onFailureDefault(e);
            }

            @Override
            public void onResponse(Object obj) {
                //关闭通信等待
//                LoadingView.getInstance().dismiss();
                ToastUtil.showShort(getApplicationContext(), "绑定成功");
                //标记已经绑定成功
                hasBindSuccess = true;
                //按钮显示为登录
                btnLogin.setText(getResources().getString(R.string.login));

                //获取孩子信息
                doGetChildListWrap();
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
        if (Constant.wx_api.isWXAppInstalled()) {
            startWxLogin();
        } else {
            ToastUtil.showShort(getApplicationContext(), "您还未安装微信客户端");
        }
    }

    /**
     * 发起微信登录请求
     */
    private void startWxLogin() {
        //检查网络
        if (!NetworkUtil.isConnectivity(XLoginAccountActivity.this)) {
            ToastUtil.showShort(XLoginAccountActivity.this, "网络连接异常");
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
        EventBus.getDefault().cancelEventDelivery(event);

        if ("error".equals(event.getCode())) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    //关闭通信等待提示
                    LoadingView.getInstance().dismiss();
                    ToastUtil.showShort(XLoginAccountActivity.this, "微信授权失败！");
                }
            });
            return;
        }
        //成功
        if (event != null && !TextUtils.isEmpty(event.getCode())) {
            //获取微信token根据返回的code
            getWxToken(event.getCode());
        }
    }

    /**
     * 获取微信token根据code
     *
     * @param code
     */
    private void getWxToken(String code) {
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
     *
     * @param entity 微信token对象
     */
    private void doGetWxTokenComplete(WXTokenEntity entity) {
        //第三方登入返回
        if (entity != null) {
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
     *
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
                            RegisterPhoneNumActivity.startRegisterPhoneNumActivity(XLoginAccountActivity.this, Dictionary.SmsType_Register, thirdLoginDto, null);
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
                    manService.getMANAnalytics().updateUserAccount(wxUserInfoEntity.getUserId() + "", wxUserInfoEntity.getUserId() + "");

                    //未绑定手机号则跳转绑定
                    if (!wxUserInfoEntity.isBindMobile()) {
                        //跳转手机号绑定流程
                        RegisterPhoneNumActivity.startRegisterPhoneNumActivity(XLoginAccountActivity.this, Dictionary.SmsType_Bind_Phone_Num, null, null);

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
            mTencent = Tencent.createInstance(Constant.QQ_APP_ID, getApplicationContext());
        }

        //检查网络
        if (!NetworkUtil.isConnectivity(XLoginAccountActivity.this)) {
            ToastUtil.showShort(XLoginAccountActivity.this, "网络连接异常");
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
            Tencent.onActivityResultData(requestCode, resultCode, data, loginListener);
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
     *
     * @param entity 微信token对象
     */
    private void doGetQqTokenComplete(QQTokenEntity entity) {
        //第三方登入返回
        if (entity != null) {
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
     *
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
        } catch (Exception e) {
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
     * 创建会话后处理是否得输入图形验证码
     * @param showLoading
     */
    private void doPostAccountSessionForImageCaptcha(final boolean showLoading) {

        doPostAccountSession(Dictionary.CREATE_SESSION_MESSAGE_CAPTCHA, showLoading, new OnResultListener() {

            @Override
            public void onSuccess(Object... objects) {
                if (showLoading)

                //不正常
                if (!sessionCreateResult.getNormal()) {
                    //显示图形验证码布局
//                    rlImageCaptcha.setVisibility(View.VISIBLE);
                    //请求图形验证码
                    getImageCaptcha(sessionCreateResult.getSessionId(), null);
                }
            }

            @Override
            public void onFailed(Object... objects) {

            }
        });
    }


    /**
     * 创建会话然后直接登录
     * @param showLoading 是否显示通信等待
     */
    private void doPostAccountSessionForAccountLogin(boolean showLoading) {
        doPostAccountSession(Dictionary.CREATE_SESSION_ACCOUNT_LOGIN, showLoading, new OnResultListener() {
            @Override
            public void onSuccess(Object... objects) {
                //账号登录
                doAccountLoginWrap();
            }

            @Override
            public void onFailed(Object... objects) {

            }
        });
    }


    /**
     * 获取图形验证码
     * @param sessionId
     */
    private void getImageCaptcha(String sessionId, final OnResultListener listener) {
        DataRequestService.getInstance().getImageCaptcha(sessionId, new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                onFailureDefault(e, new FailureDefaultErrorCodeCallBack() {
                    @Override
                    public boolean onErrorCodeCallBack(ErrorCodeEntity errorCodeEntity) {
                        String errorCode = errorCodeEntity.getCode();
                        //Session 未创建或已过期、无效
                        if (ErrorCode.AC_SESSION_EXPIRED.equals(errorCode) || ErrorCode.AC_SESSION_INVALID.equals(errorCode)) {
                            //重新获取会话
                            sessionCreateResult = null;
                            doPostAccountSessionForImageCaptcha(false);

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
                //通过handler更新UI
                Message.obtain(mHandler, MSG_REFRESH_IMAGE_CAPTCHA, obj).sendToTarget();

                //成功回调
                if (listener != null) {
                    listener.onSuccess();
                }
            }
        });
    }


    /**
     * mHandler发送的消息处理
     * @param msg
     */
    @Override
    public void onHandleMessage(Message msg) {
        //图形验证码
        if (msg.what == MSG_REFRESH_IMAGE_CAPTCHA) {
            //显示图形验证码布局
            rlImageCaptcha.setVisibility(View.VISIBLE);
            //刷新图形验证码
            byte[] captcha = (byte[]) msg.obj;
            Bitmap bitmap = BitmapFactory.decodeByteArray(captcha, 0, captcha.length);
            ivImageCaptcha.setImageBitmap(bitmap);

            //清空图形验证码
            etImageCaptcha.setText("");

        }  else if (msg.what == MSG_REQUIRED_IMAGE_CAPTCHA) {//需要图形验证码
            //关闭通信等待
            LoadingView.getInstance().dismiss();

            //如果当前图形验证码的布局还没显示，则显示并且请求图形验证码
            if (rlImageCaptcha.getVisibility() == View.GONE) {
                //显示图形验证码布局
                rlImageCaptcha.setVisibility(View.VISIBLE);
                //需要图形验证码，才能登录的提示处理
                requiredImageCaptchaForSendError();
                //请求图形验证码
                getImageCaptcha(sessionCreateResult.getSessionId(), null);
            } else {
                //聚焦图形验证码
                etImageCaptcha.requestFocus();
                ToastUtil.showShort(getApplicationContext(), "请输入图形验证码");
            }
        }
    }


    /**
     * 需要图形验证码，才能登录的提示处理
     */
    private void requiredImageCaptchaForSendError() {
        ToastUtil.showShort(getApplicationContext(),"请输入图形验证码后，重新登录");

        //清空图形验证码，并聚焦图形验证码
        etImageCaptcha.setText("");
        etImageCaptcha.requestFocus();
    }

}
