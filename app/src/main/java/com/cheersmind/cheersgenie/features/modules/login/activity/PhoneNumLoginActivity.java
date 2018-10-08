package com.cheersmind.cheersgenie.features.modules.login.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
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
import com.cheersmind.cheersgenie.features.dto.CreateSessionDto;
import com.cheersmind.cheersgenie.features.dto.MessageCaptchaDto;
import com.cheersmind.cheersgenie.features.dto.PhoneNumLoginDto;
import com.cheersmind.cheersgenie.features.dto.ThirdLoginDto;
import com.cheersmind.cheersgenie.features.dto.ThirdPlatBindDto;
import com.cheersmind.cheersgenie.features.entity.SessionCreateResult;
import com.cheersmind.cheersgenie.features.interfaces.OnResultListener;
import com.cheersmind.cheersgenie.features.modules.base.activity.BaseActivity;
import com.cheersmind.cheersgenie.features.utils.DataCheckUtil;
import com.cheersmind.cheersgenie.features.utils.DeviceUtil;
import com.cheersmind.cheersgenie.features.utils.PhoneMessageTestUtil;
import com.cheersmind.cheersgenie.features.utils.SoftInputUtil;
import com.cheersmind.cheersgenie.main.Exception.QSCustomException;
import com.cheersmind.cheersgenie.main.constant.Constant;
import com.cheersmind.cheersgenie.main.entity.ErrorCodeEntity;
import com.cheersmind.cheersgenie.main.entity.WXUserInfoEntity;
import com.cheersmind.cheersgenie.main.service.BaseService;
import com.cheersmind.cheersgenie.main.service.DataRequestService;
import com.cheersmind.cheersgenie.main.util.InjectionWrapperUtil;
import com.cheersmind.cheersgenie.main.util.JsonUtil;
import com.cheersmind.cheersgenie.main.util.ToastUtil;
import com.cheersmind.cheersgenie.main.view.LoadingView;
import com.cheersmind.cheersgenie.module.login.UCManager;

import org.litepal.crud.DataSupport;

import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 手机号快捷登录
 */
public class PhoneNumLoginActivity extends BaseActivity {

    //刷新图形验证码
    private static final int MSG_REFRESH_IMAGE_CAPTCHA = 1;
    //需要图形验证码
    private static final int MSG_REQUIRED_IMAGE_CAPTCHA = 2;

    //绑定的手机号提示
    @BindView(R.id.tv_bind_phone_num_tip)
    TextView tvBindPhoneNumTip;
    //手机号布局
    @BindView(R.id.rl_phone_num)
    LinearLayout rlPhoneNum;

    @BindView(R.id.btn_confirm)
    Button btnConfirm;
    @BindView(R.id.btn_captcha)
    Button btnCaptcha;
    @BindView(R.id.et_phonenum)
    EditText etPhonenum;
    //手机号的清空按钮
    @BindView(R.id.iv_clear)
    ImageView ivClear;
    @BindView(R.id.et_captcha)
    EditText etCaptcha;
    @BindView(R.id.et_image_captcha)
    EditText etImageCaptcha;
    @BindView(R.id.iv_image_captcha)
    ImageView ivImageCaptcha;
    @BindView(R.id.rl_image_captcha)
    RelativeLayout rlImageCaptcha;
    @BindView(R.id.tv_forbid_login)
    TextView tvForbidLogin;

    //账号登录
    @BindView(R.id.tv_account_login)
    TextView tvAccountLogin;

    //计时器
    private CountTimer countTimer;
    //倒计时的时间（s）
    private static final int COUNT_DOWN = 65000;


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
     * 打开手机快捷登录页面（无第三方登录信息就是正常登录操作，有则是绑定账号操作）
     * @param context
     * @param thirdLoginDto 第三方平台登录信息
     * @param phoneNum 手机号
     */
    public static void startPhoneNumLoginActivity(Context context, ThirdLoginDto thirdLoginDto, String phoneNum) {
        Intent intent = new Intent(context, PhoneNumLoginActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        Bundle extras = new Bundle();
        extras.putSerializable(THIRD_LOGIN_DTO, thirdLoginDto);
        extras.putString(PHONE_NUM, phoneNum);
        intent.putExtras(extras);
        context.startActivity(intent);
    }


    @Override
    protected int setContentView() {
        return R.layout.activity_phone_num_login;
    }

    @Override
    protected String settingTitle() {
        return "验证码登录";
    }


    @Override
    protected void onInitView() {
        etPhonenum.addTextChangedListener(new TextWatcher() {
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

        //监听验证码输入框的软键盘回车键
        etCaptcha.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                //当actionId == XX_SEND 或者 XX_DONE时都触发
                //或者event.getKeyCode == ENTER 且 event.getAction == ACTION_DOWN时也触发
                //注意，这是一定要判断event != null。因为在某些输入法上会返回null。
                if (actionId == EditorInfo.IME_ACTION_SEND
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || (keyEvent != null && KeyEvent.KEYCODE_ENTER == keyEvent.getKeyCode() && KeyEvent.ACTION_DOWN == keyEvent.getAction())) {
                    //手机号登录
                    doPhoneNumLoginWrap();
                }

                return false;
            }
        });
    }

    @Override
    protected void onInitData() {
        //初始化计时器
        countTimer = new CountTimer(COUNT_DOWN, 1000);

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

        //创建会话后处理是否得输入图形验证码
//        doPostAccountSessionForImageCaptcha(false);
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
        btnConfirm.setText(getResources().getString(R.string.bind_phone_num));

        //隐藏手机号布局
        rlPhoneNum.setVisibility(View.GONE);
        //设置手机号（此时是隐藏的）
        etPhonenum.setText(phoneNum);
        //隐藏账号登录
        tvAccountLogin.setVisibility(View.GONE);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        //释放计时器
        if (countTimer != null) {
            countTimer.cancel();
            countTimer = null;
        }
    }


    @OnClick({R.id.btn_captcha, R.id.btn_confirm, R.id.iv_image_captcha, R.id.tv_account_login, R.id.iv_clear})
    public void onViewClick(View view) {
        switch (view.getId()) {
            //发送短信验证码
            case R.id.btn_captcha: {
                //发送短信验证码
                doQuerySendCaptchaWrap();
                break;
            }
            //登录
            case R.id.btn_confirm: {
                //手机号登录
                doPhoneNumLoginWrap();
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
            //账号登录
            case R.id.tv_account_login: {
                Intent intent = new Intent(this, XLoginAccountActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);//FLAG_ACTIVITY_SINGLE_TOP
                startActivity(intent);
                break;
            }
            //手机号的清空按钮
            case R.id.iv_clear: {
                etPhonenum.setText("");
                etPhonenum.requestFocus();
                break;
            }
        }
    }

    /**
     * 手机号登录
     */
    private void doPhoneNumLoginWrap() {
        //隐藏软键盘
        SoftInputUtil.closeSoftInput(this);

        //数据校验
        if (!checkDataForPhoneNumLogin()) {
            return;
        }

        //账号登录必须要有sessionId
        if (sessionCreateResult == null || TextUtils.isEmpty(sessionCreateResult.getSessionId())) {
            //创建会话后直接登录
            LoadingView.getInstance().show(this);
            doPostAccountSessionForPhoneNumLogin(false);
            return;
        }

        //请求手机号登录
        PhoneNumLoginDto phoneNumLoginDto = new PhoneNumLoginDto();
        phoneNumLoginDto.setMobile(etPhonenum.getText().toString().trim());//手机号
        phoneNumLoginDto.setMobile_code(etCaptcha.getText().toString().trim());//短信验证码
        phoneNumLoginDto.setTenant(Dictionary.Tenant_CheersMind);//租户名
        phoneNumLoginDto.setDeviceType("android");//设备类型
        phoneNumLoginDto.setDeviceDesc(Build.MODEL);//设备描述（华为 XXXX）
        phoneNumLoginDto.setDeviceId(DeviceUtil.getDeviceId(getApplicationContext()));//设备ID
        phoneNumLoginDto.setSessionId(sessionCreateResult.getSessionId());//会话ID
//        phoneNumLoginDto.setDeviceId(DeviceUtil.getDeviceId(PhoneNumLoginActivity.this));
        doPhoneNumLogin(phoneNumLoginDto);
    }

    /**
     * 手机号登录的验证数据格式
     * @return true：验证通过，false：验证不通过
     */
    private boolean checkDataForPhoneNumLogin() {
        //手机号验证
        String phoneNum = etPhonenum.getText().toString().trim();
        //非空
        if (TextUtils.isEmpty(phoneNum)) {
            ToastUtil.showShort(getApplicationContext(), "请输入手机号");
            etPhonenum.requestFocus();
            return false;

        } else {
            //手机号格式
            if (!DataCheckUtil.isMobileNO(phoneNum)) {
                ToastUtil.showShort(getApplicationContext(), "请输入正确的手机号");
                etPhonenum.requestFocus();
                return  false;
            }
        }

        //短信验证码
        String captcha = etCaptcha.getText().toString().trim();
        //非空
        if (TextUtils.isEmpty(captcha)) {
            ToastUtil.showShort(getApplicationContext(), "请输入短信验证码");
            etCaptcha.requestFocus();
            return false;
        }
        //长度（目前验证码长度为6）
        if (captcha.length() < 6) {
            ToastUtil.showShort(getApplicationContext(), "请完整输入短信验证码");
            etCaptcha.requestFocus();
            return false;
        }

        return true;
    }

    /**
     * 请求手机号登录
     * @param phoneNumLoginDto
     */
    private void doPhoneNumLogin(final PhoneNumLoginDto phoneNumLoginDto) {
        //关闭软键盘
        SoftInputUtil.closeSoftInput(PhoneNumLoginActivity.this);
        //开启通信等待提示
        LoadingView.getInstance().show(PhoneNumLoginActivity.this);
        //手机号登录
        DataRequestService.getInstance().postPhoneNumLogin(phoneNumLoginDto, new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                onFailureDefault(e, new FailureDefaultErrorCodeCallBack() {
                    @Override
                    public boolean onErrorCodeCallBack(ErrorCodeEntity errorCodeEntity) {
                        String errorCode = errorCodeEntity.getCode();
                        //账号不存在
                        if (ErrorCode.AC_ACCOUNT_NOT_EXIST.equals(errorCode)) {
                            ToastUtil.showShort(getApplicationContext(), "该手机号未注册");
                            //走手机注册页面流程
//                            RegisterPhoneNumActivity.startRegisterPhoneNumActivity(PhoneNumLoginActivity.this, Dictionary.SmsType_Register, thirdLoginDto);
                            //标记已经处理了异常
                            return true;

                        } else if (ErrorCode.AC_SMS_INVALID.equals(errorCode)) {//短信验证码无效
                            //不处理
                            return false;

                        } else if (ErrorCode.AC_SESSION_EXPIRED.equals(errorCode) || ErrorCode.AC_SESSION_INVALID.equals(errorCode)) {//Session 未创建或已过期、无效
                            //重新获取会话
                            sessionCreateResult = null;
                            //创建会话后直接登录
                            LoadingView.getInstance().show(PhoneNumLoginActivity.this);
                            doPostAccountSessionForPhoneNumLogin(false);

                            //标记已经处理了异常
                            return true;

                        } else if (ErrorCode.AC_SMSCODE_ERROR_OVER_SUM.equals(errorCode)) {//您的今天短信验证码输入错误次数已超过上限
                            //禁用登录
                            forbidLogin(errorCodeEntity.getMessage());
                            //继续走默认提示
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
                    //保存用户数据到数据库
                    DataSupport.deleteAll(WXUserInfoEntity.class);
                    wxUserInfoEntity.save();

                    //登录统计
                    MANService manService = MANServiceProvider.getService();
                    // 用户登录埋点("usernick", "userid")
                    manService.getMANAnalytics().updateUserAccount(wxUserInfoEntity.getUserId() +"", wxUserInfoEntity.getUserId()+"");

                    //获取孩子信息
//                    doGetChildListWrap();

                    //正常的登录
                    if (thirdLoginDto == null) {
                        //获取孩子信息
                        doGetChildListWrap();

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

                } catch (Exception e) {
                    onFailure(new QSCustomException("登录失败"));
                }
            }
        });
    }


    /**
     * 请求第三方平台账号绑定
     * @param bindDto
     */
    private void doThirdPlatBind(final ThirdPlatBindDto bindDto) {
        LoadingView.getInstance().show(PhoneNumLoginActivity.this);
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
                btnConfirm.setText(getResources().getString(R.string.login));

                //获取孩子信息
                doGetChildListWrap();
            }
        });
    }


    /**
     * 禁用登录
     * @param tip
     */
    private void forbidLogin(String tip) {
        //显示禁用登录的提示（目前用默认的文本）
        tvForbidLogin.setVisibility(View.VISIBLE);
        if (!TextUtils.isEmpty(tip)) {
            tvForbidLogin.setText(tip);
        }
        //关闭短信发送按钮
        btnCaptcha.setEnabled(false);
        //关闭登录按钮
        btnConfirm.setEnabled(false);
    }


    /**
     * 计时器
     */
    class CountTimer extends CountDownTimer {

        public CountTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long l) {
            //加上100是为了防止秒数丢失：9秒时显示成8999 /1000 = 8秒
            long second = (l + 100) / 1000;
            btnCaptcha.setText("重新发送（" + second + "s)");
            btnCaptcha.setEnabled(false);
        }

        @Override
        public void onFinish() {
            btnCaptcha.setText("重新发送");
            btnCaptcha.setEnabled(true);
        }
    }

    /**
     * 发送短信验证码
     */
    private void doQuerySendCaptchaWrap() {
        //隐藏软键盘
        SoftInputUtil.closeSoftInput(this);

        //格式验证
        if (!checkData()) {
            return;
        } else {
            if (countTimer == null) {
                //初始化计时器
                countTimer = new CountTimer(COUNT_DOWN, 1000);
            }
            //开始计时
            countTimer.start();
        }

        //发送短信验证码必须要有sessionId
        if (sessionCreateResult == null || TextUtils.isEmpty(sessionCreateResult.getSessionId())) {
            //创建会话然后发送短信验证码
            LoadingView.getInstance().show(this);
            doPostAccountSessionForSendMessageCaptcha(false);
            return;
        }

        //请求发送验证码
        String phoneNum = etPhonenum.getText().toString();
        String sessionId = sessionCreateResult.getSessionId();
        //图形验证码
        String imageCaptcha = null;
        if (!sessionCreateResult.getNormal()) {
            imageCaptcha = etImageCaptcha.getText().toString();
        }

        //请求发送短信验证码
        querySendMessageCaptcha(phoneNum, Dictionary.SmsType_SmsLogin, sessionId, imageCaptcha);
    }

    /**
     * 验证数据格式
     * @return true：验证通过，false：验证不通过
     */
    private boolean checkData() {
        String phoneNum = etPhonenum.getText().toString().trim();
        //非空
        if (TextUtils.isEmpty(phoneNum)) {
            ToastUtil.showShort(getApplicationContext(), "请输入手机号");
            etPhonenum.requestFocus();
            return false;

        } else {
            //手机号格式
            if (!DataCheckUtil.isMobileNO(phoneNum)) {
                ToastUtil.showShort(getApplicationContext(), "请输入正确的手机号");
                etPhonenum.requestFocus();
                return  false;
            }
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
     * 请求发送短信验证码
     * @param phoneNum 手机号
     * @param smsType 短信业务类型：0:注册用户，1：短信登录，2：绑定手机号
     */
    private void querySendMessageCaptcha(final String phoneNum, final int smsType, String sessionId, String imageCaptcha) {
        //隐藏软键盘
        SoftInputUtil.closeSoftInput(this);
        //开启通信等待提示
        LoadingView.getInstance().show(this);

        MessageCaptchaDto dto = new MessageCaptchaDto();
        dto.setMobile(phoneNum);
        dto.setType(smsType);
        dto.setTenant(Dictionary.Tenant_CheersMind);
        dto.setAreaCode(Dictionary.Area_Code_86);
        dto.setSessionId(sessionId);
        dto.setImageCaptcha(imageCaptcha);

        try {
            DataRequestService.getInstance().postSendMessageCaptcha(dto, new BaseService.ServiceCallback() {
                @Override
                public void onFailure(QSCustomException e) {
                    onFailureDefault(e, new FailureDefaultErrorCodeCallBack() {
                        @Override
                        public boolean onErrorCodeCallBack(ErrorCodeEntity errorCodeEntity) {
                            String errorCode = errorCodeEntity.getCode();
                            //账号不存在
                            if (ErrorCode.AC_USER_NOT_EXIST.equals(errorCode)) {
                                ToastUtil.showShort(getApplicationContext(), "该手机号未注册");
                                //标记已经处理了异常
                                return true;

                            } else if (ErrorCode.AC_SESSION_EXPIRED.equals(errorCode) || ErrorCode.AC_SESSION_INVALID.equals(errorCode)) {//Session 未创建或已过期、无效
                                //重新获取会话
                                sessionCreateResult = null;
                                //创建会话然后发送短信验证码
                                LoadingView.getInstance().show(PhoneNumLoginActivity.this);
                                doPostAccountSessionForSendMessageCaptcha(false);

                                //标记已经处理了异常
                                return true;

                            } else if (ErrorCode.AC_IDENTIFY_CODE_REQUIRED.equals(errorCode)) {//需要图形验证码
                                //如果当前状态为正常，则置为不正常
                                if (sessionCreateResult.getNormal()) {
                                    sessionCreateResult.setNormal(false);
                                }
                                //开启通信等待
                                LoadingView.getInstance().show(PhoneNumLoginActivity.this);
                                //请求图形验证码
                                getImageCaptcha(sessionCreateResult.getSessionId(), new OnResultListener() {

                                    @Override
                                    public void onSuccess(Object... objects) {
                                        //关闭通信等待
                                        LoadingView.getInstance().dismiss();
                                        //需要图形验证码，才能发送短信的提示处理
                                        requiredImageCaptchaForSendError();
                                    }

                                    @Override
                                    public void onFailed(Object... objects) {

                                    }
                                });

                                //标记已经处理了异常
                                return true;

                            } else if (ErrorCode.AC_IDENTIFY_CODE_INVALID.equals(errorCode)) {//无效的验证码
                                //清空短信验证码、清空并聚焦图形验证码、还原发送按钮
                                clearCaptchaAndSendButton();
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

                    //提示显示短信验证码
                    PhoneMessageTestUtil.toastShowMessage(PhoneNumLoginActivity.this, obj);

                    //关闭通信等待提示
                    LoadingView.getInstance().dismiss();

                    //清空并聚焦短信验证码
                    etCaptcha.setText("");
                    etCaptcha.requestFocus();
                }
            });
        } catch (QSCustomException e) {
            e.printStackTrace();
            ToastUtil.showShort(getApplicationContext(), e.getMessage());
        }
    }


    /**
     * 创建会话后处理是否得输入图形验证码
     * @param showLoading
     */
    private void doPostAccountSessionForImageCaptcha(boolean showLoading) {

        doPostAccountSession(Dictionary.CREATE_SESSION_MESSAGE_CAPTCHA, showLoading, new OnResultListener() {

            @Override
            public void onSuccess(Object... objects) {
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
     * 创建会话然后发送短信验证码
     */
    private void doPostAccountSessionForSendMessageCaptcha(boolean showLoading) {
        doPostAccountSession(Dictionary.CREATE_SESSION_MESSAGE_CAPTCHA, showLoading, new OnResultListener() {

            @Override
            public void onSuccess(Object... objects) {
                //发送短信验证码
                doQuerySendCaptchaWrap();
            }

            @Override
            public void onFailed(Object... objects) {

            }
        });
    }


    /**
     * 创建会话后直接登录
     */
    private void doPostAccountSessionForPhoneNumLogin(boolean showLoading) {
        doPostAccountSession(Dictionary.CREATE_SESSION_MESSAGE_CAPTCHA, showLoading, new OnResultListener() {

            @Override
            public void onSuccess(Object... objects) {
                //手机短信登录
                doPhoneNumLoginWrap();
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
        //刷新图形验证码
        if (msg.what == MSG_REFRESH_IMAGE_CAPTCHA) {
            //显示图形验证码布局
            rlImageCaptcha.setVisibility(View.VISIBLE);
            //刷新图形验证码
            byte[] captcha = (byte[]) msg.obj;
            Bitmap bitmap = BitmapFactory.decodeByteArray(captcha, 0, captcha.length);
            ivImageCaptcha.setImageBitmap(bitmap);

            //清空图形验证码
            etImageCaptcha.setText("");

        } else if (msg.what == MSG_REQUIRED_IMAGE_CAPTCHA) {//需要图形验证码
            //关闭通信等待
            LoadingView.getInstance().dismiss();

            //如果当前图形验证码的布局还没显示，则显示并且请求图形验证码
            if (rlImageCaptcha.getVisibility() == View.GONE) {
                //显示图形验证码布局
                rlImageCaptcha.setVisibility(View.VISIBLE);
                //需要图形验证码，才能发送短信的提示处理
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
     * 需要图形验证码，才能发送短信的提示处理
     */
    private void requiredImageCaptchaForSendError() {
        ToastUtil.showShort(getApplicationContext(),"请输入图形验证码后，重新发送");
        //清空短信验证码、清空并聚焦图形验证码、还原发送按钮
        clearCaptchaAndSendButton();
    }

    /**
     * 清空短信验证码、清空并聚焦图形验证码、还原发送按钮
     */
    private void clearCaptchaAndSendButton() {
        //清空短信验证码和图形验证码，并聚焦图形验证码
        etCaptcha.setText("");
        etImageCaptcha.setText("");
        etImageCaptcha.requestFocus();

        //释放计时器
        if (countTimer != null) {
            countTimer.cancel();
            countTimer = null;
        }
        //还原按钮
        btnCaptcha.setText("重新发送");
        btnCaptcha.setEnabled(true);
    }


}
