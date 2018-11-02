package com.cheersmind.cheersgenie.features.modules.register.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
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
import com.cheersmind.cheersgenie.features.dto.BindPhoneNumDto;
import com.cheersmind.cheersgenie.features.dto.CreateSessionDto;
import com.cheersmind.cheersgenie.features.dto.MessageCaptchaDto;
import com.cheersmind.cheersgenie.features.dto.RegisterDto;
import com.cheersmind.cheersgenie.features.dto.ResetPasswordDto;
import com.cheersmind.cheersgenie.features.dto.ThirdLoginDto;
import com.cheersmind.cheersgenie.features.dto.ThirdPlatBindDto;
import com.cheersmind.cheersgenie.features.entity.SessionCreateResult;
import com.cheersmind.cheersgenie.features.interfaces.OnResultListener;
import com.cheersmind.cheersgenie.features.modules.base.activity.BaseActivity;
import com.cheersmind.cheersgenie.features.modules.login.activity.XLoginAccountActivity;
import com.cheersmind.cheersgenie.features.modules.login.activity.XLoginActivity;
import com.cheersmind.cheersgenie.features.modules.mine.activity.ModifyPasswordActivity;
import com.cheersmind.cheersgenie.features.utils.DataCheckUtil;
import com.cheersmind.cheersgenie.features.utils.DeviceUtil;
import com.cheersmind.cheersgenie.features.utils.PhoneMessageTestUtil;
import com.cheersmind.cheersgenie.features.utils.SoftInputUtil;
import com.cheersmind.cheersgenie.main.Exception.QSCustomException;
import com.cheersmind.cheersgenie.main.QSApplication;
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
import com.umeng.analytics.MobclickAgent;

import org.litepal.crud.DataSupport;

import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.OnClick;

/**
 * 验证码输入页面（注册）
 */
public class RegisterCaptchaActivity extends BaseActivity {
    //刷新图形验证码
    private static final int MSG_REFRESH_IMAGE_CAPTCHA = 1;
    //需要图形验证码
    private static final int MSG_REQUIRED_IMAGE_CAPTCHA = 2;

    //手机号
    private static final String PHONE_NUM = "phone_num";
    //短信业务类型：0:注册用户，1：短信登录，2：绑定手机号，3、重置密码
    private static final String  SMS_TYPE = "sms_type";
    //第三方平台登录信息Dto
    private static final String THIRD_LOGIN_DTO = "thirdLoginDto";
    //短信验证码
    private static final String MESSAGE_CAPTCHA = "MESSAGE_CAPTCHA";

    //短信业务类型：0:注册用户，1：短信登录，2：绑定手机号，3、重置密码
    private int smsType;
    //第三方平台登录信息dto
    private ThirdLoginDto thirdLoginDto;

    //倒计时的时间（s）
    private static final int COUNT_DOWN = 65000;

    @BindView(R.id.btn_confirm)
    Button btnConfirm;
    @BindView(R.id.btn_send_again)
    Button btnSendAgain;
    @BindView(R.id.tv_send_phonenum_tip)
    TextView tvSendPhonenumTip;
    @BindView(R.id.et_image_captcha)
    EditText etImageCaptcha;
    @BindView(R.id.iv_image_captcha)
    ImageView ivImageCaptcha;
    @BindView(R.id.rl_image_captcha)
    RelativeLayout rlImageCaptcha;
    @BindView(R.id.tv_forbid_login)
    TextView tvForbidLogin;

    @BindViews({R.id.et_captcha_num1, R.id.et_captcha_num2, R.id.et_captcha_num3,
            R.id.et_captcha_num4, R.id.et_captcha_num5, R.id.et_captcha_num6})
    List<EditText> etCaptchaNumList;

    //密码格式提示
    @BindView(R.id.tv_password_format_tip)
    TextView tvPasswordFormatTip;
    //密码模块
    @BindView(R.id.rl_password)
    LinearLayout rlPassword;
    @BindView(R.id.et_password)
    EditText etPassword;
    //密码的清空按钮
    @BindView(R.id.iv_clear_pw)
    ImageView ivClearPw;
    @BindView(R.id.cbox_password)
    CheckBox cboxPassword;

    //当前输入索引
    int position = 0;

    //0宽度空格符（在edittext没有内容的时候，按软键盘的删除键,不触发TextWatcher）
    private static final String ZERO_WIDTH_SPACE = "\uFEFF";

    //计时器
    private CountTimer countTimer;

    //手机号
    String phoneNum;
    //短信验证码
    private String messageCaptcha;

    /**
     * 启动注册环节手机验证码页面
     * @param context
     * @param phoneNum 手机号
     * @param smsType 短信业务类型：0:注册用户，1：短信登录，2：绑定手机号，3、重置密码
     * @param thirdLoginDto 第三方平台登录信息
     * @param messageCaptcha 短信验证码（用于测试环境，弹窗显示）
     */
    public static void startRegisterCaptchaActivity(Context context, String phoneNum, int smsType, ThirdLoginDto thirdLoginDto, String messageCaptcha) {
        Intent intent = new Intent(context, RegisterCaptchaActivity.class);
        Bundle extras = new Bundle();
        extras.putString(PHONE_NUM, phoneNum);
        extras.putInt(SMS_TYPE, smsType);
        extras.putSerializable(THIRD_LOGIN_DTO, thirdLoginDto);
        extras.putString(MESSAGE_CAPTCHA, messageCaptcha);
        intent.putExtras(extras);
        context.startActivity(intent);
    }


    @Override
    protected int setContentView() {
        return R.layout.activity_register_captcha;
    }

    @Override
    protected String settingTitle() {
        return "验证码输入";
    }

    @Override
    protected void onInitView() {
        //初始编辑框的焦点
        switchFocus(position);
        //初始化文本监听
        initEditTextListener();

        //初始化计时器
        countTimer = new CountTimer(COUNT_DOWN, 1000);
        countTimer.start();

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
                    //下一步
                    doNextDept();
                }

                return false;
            }
        });

    }

    @Override
    protected void onInitData() {
        if (getIntent() == null || getIntent().getExtras() == null) {
            ToastUtil.showShort(getApplicationContext(), "数据传递有误");
            return;
        }

//        topicInfoEntity = (TopicInfoEntity)getIntent().getExtras().getSerializable(TOPIC_INFO);
        phoneNum = getIntent().getStringExtra(PHONE_NUM);
        if (TextUtils.isEmpty(phoneNum)) {
            ToastUtil.showShort(getApplicationContext(), "数据传递有误");
            return;
        }

        //发送号码提示
        tvSendPhonenumTip.setText(phoneNum);

        //初始隐藏验证码模块
        rlImageCaptcha.setVisibility(View.GONE);
        //初始隐藏密码模块
        rlPassword.setVisibility(View.GONE);
        tvPasswordFormatTip.setVisibility(View.GONE);

        //操作类型
        smsType = getIntent().getIntExtra(SMS_TYPE, Dictionary.SmsType_Register);
        switch (smsType) {
            //操作类型：绑定手机号
            case Dictionary.SmsType_Bind_Phone_Num: {
                //设置标题
                settingTitle("绑定手机号");
                //修改按钮文字为“绑定”
                btnConfirm.setText(getResources().getString(R.string.bind));
                break;
            }
            //操作类型：注册用户
            case Dictionary.SmsType_Register: {
                //设置标题
                settingTitle("账号注册");
                //修改按钮文字为“注册”
                btnConfirm.setText(getResources().getString(R.string.register));
                //密码编辑框默认提示文本
                etPassword.setHint("请输入密码");
                //显示密码模块
                rlPassword.setVisibility(View.VISIBLE);
                tvPasswordFormatTip.setVisibility(View.VISIBLE);
                break;
            }
            //操作类型：找回密码
            case Dictionary.SmsType_Retrieve_Password: {
                //设置标题
                settingTitle("重置密码");
                //修改按钮文字为“登录”
                btnConfirm.setText("确定");
                //密码编辑框默认提示文本
                etPassword.setHint("请输入新密码");
                //显示密码模块
                rlPassword.setVisibility(View.VISIBLE);
                tvPasswordFormatTip.setVisibility(View.VISIBLE);
                break;
            }
        }

        thirdLoginDto = (ThirdLoginDto) getIntent().getSerializableExtra(THIRD_LOGIN_DTO);

        //短信验证码
        messageCaptcha = getIntent().getStringExtra(MESSAGE_CAPTCHA);
        if (!TextUtils.isEmpty(messageCaptcha)) {
            PhoneMessageTestUtil.toastShowMessage(RegisterCaptchaActivity.this, messageCaptcha);
        }
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


    /**
     * 初始化数字编辑框监听
     */
    private void initEditTextListener() {
        for (int i = 0; i < etCaptchaNumList.size(); i++) {
            EditText et = etCaptchaNumList.get(i);
            //文本变化监听
            et.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    System.out.println(charSequence.toString());
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    int len = editable.toString().length();
                    //两个字符时（一个0宽度空格，一个数字），跳转到下一个编辑框
                    if (len == 2) {
                        char c = editable.charAt(1);
                        if (TextUtils.isDigitsOnly(c + "")) {
                            //验证码输入结束的处理
                            if (position == etCaptchaNumList.size() - 1) {
                                captchaInputComplete();
                            } else {
                                //跳转到下一个数字编辑框
                                toNextEdit();
                            }
                        }

                    } else if (len == 0) {
                        //防止第一个编辑框的0宽度空格符被删除
                        if (position == 0) {
                            etCaptchaNumList.get(position).setText(ZERO_WIDTH_SPACE);
                            etCaptchaNumList.get(position).setSelection(1);
                        } else {
                            //回退到前一个编辑框
                            toBackEdit();
                        }
                    }
                }
            });


            //点击监听，确保光标总是在末尾
            et.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EditText tempEt = (EditText) v;
//                    tempEt.requestFocus();
                    tempEt.setSelection(tempEt.getText().length());
                }
            });
        }
    }


    /**
     * 验证码输入完成
     */
    private void captchaInputComplete() {
//        ToastUtil.showShort(RegisterCaptchaActivity.this, "验证码输入结束，请求服务器进行验证");
        //隐藏软键盘
//        SoftInputUtil.closeSoftInput(RegisterCaptchaActivity.this);

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //自动操作下一步
//                doNextDept();
                switch (smsType) {
                    //操作类型：绑定手机号
                    case Dictionary.SmsType_Bind_Phone_Num: {
                        //自动操作下一步
                        doNextDept();
                        break;
                    }
                    //操作类型：注册用户
                    case Dictionary.SmsType_Register:
                    //操作类型：找回密码
                    case Dictionary.SmsType_Retrieve_Password:{
                        //如果图形验证码为显示状态则聚焦，否则聚焦密码输入
                        if (rlImageCaptcha.getVisibility() == View.VISIBLE) {
                            etImageCaptcha.requestFocus();
                        } else {
                            //聚焦密码输入
                            etPassword.requestFocus();
                            etPassword.setSelection(etPassword.getText().length());
                        }

                        break;
                    }
                }
            }
        }, 0);
    }

    /**
     * 下一步
     */
    private void doNextDept() {
        //数据验证
        if (!checkData()) return;

        //获取完整的验证码字符串
        String captcha = getFullCaptchaStr();

        //操作类型：注册
        if (smsType == Dictionary.SmsType_Register) {
            //跳转到密码初始化页面
//            PasswordInitActivity.startPasswordInitActivity(RegisterCaptchaActivity.this, phoneNum, captcha, thirdLoginDto);
            //注册
            doRegister();

        } else if (smsType == Dictionary.SmsType_Bind_Phone_Num) {//操作类型：绑定手机号
            //绑定手机号
            bindPhoneNumWrap();

        } else if (smsType == Dictionary.SmsType_Retrieve_Password) {//操作类型：找回密码
            //跳转到重置密码页面
//            RetrievePasswordActivity.startRetrievePasswordActivity(RegisterCaptchaActivity.this, phoneNum, captcha);
            //找回密码
            doResetPassword();
        }
    }


    /**
     * 绑定手机号验证数据格式
     * @return true：验证通过，false：验证不通过
     */
    private boolean checkDataForBindPhoneNum() {

        if (!checkData()) {
            return false;
        }

        String phoneNum = this.phoneNum;
        //非空
        if (TextUtils.isEmpty(phoneNum)) {
            ToastUtil.showShort(getApplicationContext(), "请输入手机号");
            finish();
            return false;

        } else {
            //手机号格式
            if (!DataCheckUtil.isMobileNO(phoneNum)) {
                ToastUtil.showShort(getApplicationContext(), "请输入正确的手机号");
                finish();
                return  false;
            }
        }

        return true;
    }

    /**
     * 绑定手机号
     */
    private void bindPhoneNumWrap() {
        //隐藏软键盘
        SoftInputUtil.closeSoftInput(RegisterCaptchaActivity.this);

        if (!checkDataForBindPhoneNum()) {
            return;
        }

        //绑定手机号必须要有sessionId
        if (sessionCreateResult == null || TextUtils.isEmpty(sessionCreateResult.getSessionId())) {
            //创建会话后直接绑定手机号
            LoadingView.getInstance().show(RegisterCaptchaActivity.this);
            doPostAccountSessionForBindPhoneNum(false);
            return;
        }

        //获取完整的验证码字符串
        String captcha = getFullCaptchaStr();

        BindPhoneNumDto dto = new BindPhoneNumDto();
        dto.setMobile(phoneNum);
        dto.setMobileCode(captcha);
        dto.setTenant(Dictionary.Tenant_CheersMind);
        dto.setAreaCode(Dictionary.Area_Code_86);
        dto.setSessionId(sessionCreateResult.getSessionId());
        //请求绑定手机号
        bindPhoneNum(dto);
    }

    /**
     * 绑定手机号
     * @param dto
     */
    private void bindPhoneNum(BindPhoneNumDto dto) {
        //隐藏软键盘
        SoftInputUtil.closeSoftInput(RegisterCaptchaActivity.this);
        //开启通信等待提示
        LoadingView.getInstance().show(this);

        DataRequestService.getInstance().putBindPhoneNum(dto, new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                onFailureDefault(e, new FailureDefaultErrorCodeCallBack() {
                    @Override
                    public boolean onErrorCodeCallBack(ErrorCodeEntity errorCodeEntity) {
                        String errorCode = errorCodeEntity.getCode();
                        if (ErrorCode.AC_SMS_INVALID.equals(errorCode)) {//短信验证码无效
                            //不处理
                            return false;

                        } else if (ErrorCode.AC_SESSION_EXPIRED.equals(errorCode) || ErrorCode.AC_SESSION_INVALID.equals(errorCode)) {//Session 未创建或已过期、无效
                            //重新获取会话
                            sessionCreateResult = null;
                            //创建会话后直接绑定手机号
                            LoadingView.getInstance().show(RegisterCaptchaActivity.this);
                            doPostAccountSessionForBindPhoneNum(false);

                            //标记已经处理了异常
                            return true;

                        } else if (ErrorCode.AC_SMSCODE_ERROR_OVER_SUM.equals(errorCode)) {//您的今天短信验证码输入错误次数已超过上限
                            //禁用注册
                            forbidOperate(errorCodeEntity.getMessage());
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
                //获取孩子列表信息
                doGetChildListWrap();
            }
        });
    }


    /**
     * 重置密码验证数据格式
     * @return true：验证通过，false：验证不通过
     */
    private boolean checkDataForResetPassword() {
        if (!checkData()) {
            return false;
        }

        //密码
        String password = etPassword.getText().toString();

        //密码长度验证
        if (password.length() < 6 || password.length() > 24) {
            ToastUtil.showShort(getApplicationContext(), "新密码长度为6-24位");
            return false;
        }

        return true;
    }

    /**
     * 重置密码
     */
    private void doResetPassword() {
        //隐藏软键盘
        SoftInputUtil.closeSoftInput(RegisterCaptchaActivity.this);
        //格式验证
        if (!checkDataForResetPassword()) return;

        //重置密码必须要有sessionId
        if (sessionCreateResult == null || TextUtils.isEmpty(sessionCreateResult.getSessionId())) {
            //创建会话后直接重置密码
            LoadingView.getInstance().show(RegisterCaptchaActivity.this);
            doPostAccountSessionForRetrievePassword(false);
            return;
        }

        //获取完整的验证码字符串
        String captcha = getFullCaptchaStr();

        //请求重置密码
        ResetPasswordDto dto = new ResetPasswordDto();
        dto.setMobile(phoneNum);
        dto.setMobile_code(captcha);
        dto.setTenant(Dictionary.Tenant_CheersMind);
        dto.setNew_password(etPassword.getText().toString());
        dto.setArea_code(Dictionary.Area_Code_86);
        dto.setSessionId(sessionCreateResult.getSessionId());
        patchResetPassword(dto);
    }


    /**
     * 请求重置密码
     * @param dto
     */
    private void patchResetPassword(ResetPasswordDto dto) {
        //通信等待提示
        LoadingView.getInstance().show(RegisterCaptchaActivity.this);
        //重置密码
        DataRequestService.getInstance().patchResetPassword(dto, new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                onFailureDefault(e, new FailureDefaultErrorCodeCallBack() {
                    @Override
                    public boolean onErrorCodeCallBack(ErrorCodeEntity errorCodeEntity) {
                        String errorCode = errorCodeEntity.getCode();
                        if (ErrorCode.AC_SMS_INVALID.equals(errorCode)) {//短信验证码无效
                            //不处理
                            return false;

                        } else if (ErrorCode.AC_SESSION_EXPIRED.equals(errorCode) || ErrorCode.AC_SESSION_INVALID.equals(errorCode)) {//Session 未创建或已过期、无效
                            //重新获取会话
                            sessionCreateResult = null;
                            //创建会话后直接重置密码
                            LoadingView.getInstance().show(RegisterCaptchaActivity.this);
                            doPostAccountSessionForRetrievePassword(false);

                            //标记已经处理了异常
                            return true;

                        } else if (ErrorCode.AC_SMSCODE_ERROR_OVER_SUM.equals(errorCode)) {//您的今天短信验证码输入错误次数已超过上限
                            //禁用重置密码
                            forbidOperate(errorCodeEntity.getMessage());
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
//                LoadingView.getInstance().dismiss();

                //重置密码成功后的处理：重置用户名，清空密码
                SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(QSApplication.getContext());
                SharedPreferences.Editor editor = pref.edit();
                editor.putString("user_name", phoneNum);
                editor.remove("user_password");
                editor.apply();

                //弹出重置密码成功确认对话框
//                popupResetPasswordSuccessWindows();

                //直接登录
                //这时候如果需要图形验证码，则跳转到登录主页面
                doPostAccountSessionForAccountLogin(false);
            }
        });
    }


    /**
     * 账号登录
     */
    private void doAccountLoginWrap() {
        //请求账号登录
        String loginName = phoneNum;
        String pwd = etPassword.getText().toString();
        AccountLoginDto accountLoginDto = new AccountLoginDto();
        accountLoginDto.setAccount(loginName);//用户名
        accountLoginDto.setPassword(pwd);//密码
        accountLoginDto.setSessionId(sessionCreateResult.getSessionId());//会话ID
        //图形验证码
//        if (!sessionCreateResult.getNormal()) {
//            accountLoginDto.setVerificationCode(etImageCaptcha.getText().toString());
//        }
        accountLoginDto.setTenant(Dictionary.Tenant_CheersMind);//租户名
        accountLoginDto.setDeviceType("android");//设备类型
//        accountLoginDto.setDeviceDesc(Build.MODEL);//设备描述（华为 XXXX）
        accountLoginDto.setDeviceDesc("android phone");//设备描述（华为 XXXX）
        accountLoginDto.setDeviceId(DeviceUtil.getDeviceId(getApplicationContext()));//设备ID
        doAccountLogin(accountLoginDto);
    }


    /**
     * 账号登录
     *
     * @param accountLoginDto
     */
    private void doAccountLogin(final AccountLoginDto accountLoginDto) {
        //关闭软键盘
        SoftInputUtil.closeSoftInput(RegisterCaptchaActivity.this);
        //开启通信等待提示
        LoadingView.getInstance().show(this);
        //账号登录
        DataRequestService.getInstance().postAccountLogin(accountLoginDto, new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
//                onFailureDefault(e);
                //关闭通信等待提示
                LoadingView.getInstance().dismiss();

//                ToastUtil.showShort(getApplicationContext(), "重置密码成功，请重新登录");
                //跳转到登录主页面（作为根activity）
//                gotoLoginPage(RegisterCaptchaActivity.this);

                //弹出重置密码成功确认对话框
                popupResetPasswordSuccessWindows();
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

                    //未绑定手机号则跳转绑定
                    if (!wxUserInfoEntity.isBindMobile()) {
                        //关闭通信等待提示
                        LoadingView.getInstance().dismiss();
                        //跳转手机号绑定流程
                        RegisterPhoneNumActivity.startRegisterPhoneNumActivity(RegisterCaptchaActivity.this, Dictionary.SmsType_Bind_Phone_Num, null, null);

                    } else {
                        //获取孩子信息
                        doGetChildListWrap();
                    }

                    //登录统计
                    MANService manService = MANServiceProvider.getService();
                    // 用户登录埋点("usernick", "userid")
                    manService.getMANAnalytics().updateUserAccount(wxUserInfoEntity.getUserId() + "", wxUserInfoEntity.getUserId() + "");

                    //友盟统计：当用户使用自有账号登录时，可以这样统计：
                    MobclickAgent.onProfileSignIn(String.valueOf(wxUserInfoEntity.getUserId()));

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
     * 注册验证数据格式
     * @return true：验证通过，false：验证不通过
     */
    private boolean checkDataForRegister() {
        if (!checkData()) {
            return false;
        }

        //密码
        String password = etPassword.getText().toString();

        //密码长度验证
        if (password.length() < 6 || password.length() > 24) {
            ToastUtil.showShort(getApplicationContext(), "密码长度为6-24位");
            return false;
        }

        return true;
    }


    /**
     * 注册
     */
    private void doRegister() {
        //隐藏软键盘
        SoftInputUtil.closeSoftInput(RegisterCaptchaActivity.this);
        //格式验证
        if (!checkDataForRegister()) return;

        //账号注册必须要有sessionId
        if (sessionCreateResult == null || TextUtils.isEmpty(sessionCreateResult.getSessionId())) {
            //创建会话后直接注册
            LoadingView.getInstance().show(RegisterCaptchaActivity.this);
            doPostAccountSessionForRegister(false);
            return;
        }

        //获取完整的验证码字符串
        String captcha = getFullCaptchaStr();

        //请求注册
        RegisterDto dto = new RegisterDto();
        dto.setMobile(phoneNum);
        dto.setMobileCode(captcha);
        dto.setPassword(etPassword.getText().toString());
        dto.setAreaCode(Dictionary.Area_Code_86);
        dto.setTenant(Dictionary.Tenant_CheersMind);
        dto.setThirdLoginDto(thirdLoginDto);
        dto.setSessionId(sessionCreateResult.getSessionId());
        queryRegister(dto);
        //跳转班级号输入页面
//        gotoClassNumPage();
    }


    /**
     * 请求手机短信账号注册
     * @param dto
     */
    private void queryRegister(final RegisterDto dto) {
        //关闭软键盘
        SoftInputUtil.closeSoftInput(RegisterCaptchaActivity.this);
        //开启通信等待提示
        LoadingView.getInstance().show(RegisterCaptchaActivity.this);
        //请求注册
        DataRequestService.getInstance().postRegister(dto, new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                onFailureDefault(e, new FailureDefaultErrorCodeCallBack() {
                    @Override
                    public boolean onErrorCodeCallBack(ErrorCodeEntity errorCodeEntity) {
                        String errorCode = errorCodeEntity.getCode();
                        if (ErrorCode.AC_SMS_INVALID.equals(errorCode)) {//短信验证码无效
                            //不处理
                            return false;

                        } else if (ErrorCode.AC_SESSION_EXPIRED.equals(errorCode) || ErrorCode.AC_SESSION_INVALID.equals(errorCode)) {//Session 未创建或已过期、无效
                            //重新获取会话
                            sessionCreateResult = null;
                            //创建会话后直接注册
                            LoadingView.getInstance().show(RegisterCaptchaActivity.this);
                            doPostAccountSessionForRegister(false);

                            //标记已经处理了异常
                            return true;

                        } else if (ErrorCode.AC_SMSCODE_ERROR_OVER_SUM.equals(errorCode)) {//您的今天短信验证码输入错误次数已超过上限
                            //禁用操作
                            forbidOperate(errorCodeEntity.getMessage());
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
                    //本地缓存用户名和密码（这边就以手机号作为用户名）
                    saveUserAccount(phoneNum, dto.getPassword());

                    // 统计：注册用户埋点("usernick")
                    MANService manService = MANServiceProvider.getService();
                    manService.getMANAnalytics().userRegister(wxUserInfoEntity.getUserId() +"");

                    //友盟统计：当用户使用自有账号登录时，可以这样统计：
                    MobclickAgent.onProfileSignIn(String.valueOf(wxUserInfoEntity.getUserId()));

                    //跳转班级号输入页面
                    gotoPerfectUserInfo(RegisterCaptchaActivity.this);
                    finish();

                } catch (Exception e) {
                    onFailure(new QSCustomException("注册异常，请稍后再试"));
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
        editor.apply();
    }


    /**
     * 获取完整的验证码字符串（正常情况输入完验证码时，每个数字编辑框中包含1个0宽度空格符和一个数字）
     * @return
     */
    private String getFullCaptchaStr() {
        String captcha = "";
        for (EditText editText : etCaptchaNumList) {
            captcha += (editText.getText().charAt(1) + "");
        }

        return captcha;
    }

    /**
     * 短信验证数据格式
     * @return
     */
    private boolean checkData() {
        for (int i=0; i<etCaptchaNumList.size(); i++) {
            EditText editText = etCaptchaNumList.get(i);
            String num = editText.getText().toString().trim();
            //每个数字编辑框内容为1个ZERO_WIDTH_SPACE和一个数字时说明验证码已经填写完成，否则验证不通过
            if (num.length() == 2 && TextUtils.isDigitsOnly(num.charAt(1)+"")) {

            } else {
                //验证不通过的处理
                doCheckDataError(i);
                return false;
            }
        }

        return true;
    }

    /**
     * 数据验证不通过的处理
     * @param position
     */
    private void doCheckDataError(int position) {
        //验证不通过
        this.position = position;
        //初始编辑框的焦点
        switchFocus(position);
        SoftInputUtil.openSoftInput(RegisterCaptchaActivity.this, etCaptchaNumList.get(position));
        ToastUtil.showShort(getApplicationContext(), "请输入短信验证码");
    }

    /**
     * 跳转到下一个输入框
     */
    private void toNextEdit() {
        if (position < etCaptchaNumList.size() - 1) {
            position++;
        } else {
            return;
        }
        switchFocus(position);
    }

    /**
     * 回退一个输入框
     */
    private void toBackEdit() {
        if (position > 0) {
            position--;
        } else {
            return;
        }
        switchFocus(position);
    }

    /**
     * 切换焦点数字输入框
     *
     * @param position
     */
    private void switchFocus(int position) {

        //要先聚焦，否则下面的setFocusable(false)会把软键盘给收起来
        EditText etTemp = etCaptchaNumList.get(position);
        etTemp.setEnabled(true);
        etTemp.setFocusable(true);
        etTemp.setFocusableInTouchMode(true);
        etTemp.requestFocus();
        //设置编辑框的内容为ZERO_WIDTH_SPACE（用于监听删除键的响应）
        etTemp.setText(ZERO_WIDTH_SPACE);
        etTemp.setSelection(1);

        for (int i = 0; i < etCaptchaNumList.size(); i++) {
            EditText et = etCaptchaNumList.get(i);
            if (i == position) {
                /*et.setEnabled(true);
                et.setFocusable(true);
                et.setFocusableInTouchMode(true);
                et.requestFocus();
                //设置编辑框的内容为ZERO_WIDTH_SPACE（用于监听删除键的响应）
                et.setText(ZERO_WIDTH_SPACE);
                et.setSelection(1);*/

            } else if (i > position) {
                et.setEnabled(false);
                //当前焦点之后的清空（赋值空白符，赋值空串会被监听然后回退）
                et.setFocusable(false);
                et.setFocusableInTouchMode(false);
                et.setText(ZERO_WIDTH_SPACE);

            } else {
                et.setEnabled(false);
                et.setFocusable(false);
                et.setFocusableInTouchMode(false);
            }
        }

        /*EditText et = etCaptchaNumList.get(position);
        et.setEnabled(true);
        et.setFocusable(true);
        et.setFocusableInTouchMode(true);
        et.requestFocus();
        //设置编辑框的内容为ZERO_WIDTH_SPACE（用于监听删除键的响应）
        et.setText(ZERO_WIDTH_SPACE);
        et.setSelection(1);*/

        /*//放到for外面处理是为了保证光标置于ZERO_WIDTH_SPACE之后
        EditText et = etCaptchaNumList.get(position);
        et.setFocusable(true);
        et.setFocusableInTouchMode(true);
        et.requestFocus();
        //设置编辑框的内容为ZERO_WIDTH_SPACE（用于监听删除键的响应）
        etCaptchaNumList.get(position).setText(ZERO_WIDTH_SPACE);
        etCaptchaNumList.get(position).setSelection(1);*/
    }


    @OnClick({R.id.btn_confirm, R.id.btn_send_again, R.id.iv_image_captcha, R.id.iv_clear_pw})
    public void onViewClick(View view) {
        switch (view.getId()) {
            //重新发送
            case R.id.btn_send_again: {
                //发送短信验证码
                doQuerySendCaptchaWrap();
                break;
            }
            //下一步
            case R.id.btn_confirm: {
                doNextDept();
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
            //密码的清空按钮
            case R.id.iv_clear_pw: {
                etPassword.setText("");
                etPassword.requestFocus();
                break;
            }
        }
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
            btnSendAgain.setText("重新发送（" + second + "s)");
            btnSendAgain.setEnabled(false);
        }

        @Override
        public void onFinish() {
            btnSendAgain.setText("重新发送");
            btnSendAgain.setEnabled(true);
        }
    }


    /**
     * 发送短信验证数据格式
     * @return true：验证通过，false：验证不通过
     */
    private boolean checkDataForSendMessage() {
        String phoneNum = this.phoneNum;
        //非空
        if (TextUtils.isEmpty(phoneNum)) {
            ToastUtil.showShort(getApplicationContext(), "请输入手机号");
            finish();
            return false;

        } else {
            //手机号格式
            if (!DataCheckUtil.isMobileNO(phoneNum)) {
                ToastUtil.showShort(getApplicationContext(), "请输入正确的手机号");
                finish();
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
     * 发送短信验证码
     */
    private void doQuerySendCaptchaWrap() {
        //隐藏软键盘
        SoftInputUtil.closeSoftInput(this);

        //格式验证
        if (!checkDataForSendMessage()) {
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
        String phoneNum = this.phoneNum;
        String sessionId = sessionCreateResult.getSessionId();
        //图形验证码
        String imageCaptcha = null;
        if (!sessionCreateResult.getNormal()) {
            imageCaptcha = etImageCaptcha.getText().toString();
        }

        //请求发送短信验证码
        querySendMessageCaptcha(phoneNum, smsType, sessionId, imageCaptcha);
    }


    /**
     * 请求发送短信验证码
     * @param phoneNum
     */
    private void querySendMessageCaptcha(final String phoneNum,final int smsType, String sessionId, String imageCaptcha) {
        //隐藏软键盘
        SoftInputUtil.closeSoftInput(RegisterCaptchaActivity.this);
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
                            if (ErrorCode.AC_SESSION_EXPIRED.equals(errorCode) || ErrorCode.AC_SESSION_INVALID.equals(errorCode)) {//Session 未创建或已过期、无效
                                //重新获取会话
                                sessionCreateResult = null;
                                //创建会话然后发送短信验证码
                                LoadingView.getInstance().show(RegisterCaptchaActivity.this);
                                doPostAccountSessionForSendMessageCaptcha(false);

                                //标记已经处理了异常
                                return true;

                            } else if (ErrorCode.AC_IDENTIFY_CODE_REQUIRED.equals(errorCode)) {//需要图形验证码
                                //如果当前状态为正常，则置为不正常
                                if (sessionCreateResult.getNormal()) {
                                    sessionCreateResult.setNormal(false);
                                }
                                //开启通信等待
                                LoadingView.getInstance().show(RegisterCaptchaActivity.this);
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
                    PhoneMessageTestUtil.toastShowMessage(RegisterCaptchaActivity.this, obj);

                    //关闭通信等待提示
                    LoadingView.getInstance().dismiss();
                    //清空短信验证码
                    clearMessageCaptcha();
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
     * 创建会话后直接绑定手机号
     */
    private void doPostAccountSessionForBindPhoneNum(boolean showLoading) {
        doPostAccountSession(Dictionary.CREATE_SESSION_MESSAGE_CAPTCHA, showLoading, new OnResultListener() {

            @Override
            public void onSuccess(Object... objects) {
                //绑定手机号
                bindPhoneNumWrap();
            }

            @Override
            public void onFailed(Object... objects) {

            }
        });
    }


    /**
     * 创建会话后直接重置密码
     */
    private void doPostAccountSessionForRetrievePassword(boolean showLoading) {
        doPostAccountSession(Dictionary.CREATE_SESSION_MESSAGE_CAPTCHA, showLoading, new OnResultListener() {

            @Override
            public void onSuccess(Object... objects) {
                //重置密码
                doResetPassword();
            }

            @Override
            public void onFailed(Object... objects) {

            }
        });
    }


    /**
     * 创建会话后直接注册
     */
    private void doPostAccountSessionForRegister(boolean showLoading) {
        doPostAccountSession(Dictionary.CREATE_SESSION_MESSAGE_CAPTCHA, showLoading, new OnResultListener() {

            @Override
            public void onSuccess(Object... objects) {
                //账号注册
                doRegister();
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
                //跳转到登录主页面（作为根activity）
                gotoLoginPage(RegisterCaptchaActivity.this);
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
        //清空短信验证码
        clearMessageCaptcha();
        //清空并聚焦图形验证码
        etImageCaptcha.setText("");
        etImageCaptcha.requestFocus();

        //释放计时器
        if (countTimer != null) {
            countTimer.cancel();
            countTimer = null;
        }
        //还原按钮
        btnSendAgain.setText("重新发送");
        btnSendAgain.setEnabled(true);
    }


    /**
     * 清空短信验证码
     */
    private void clearMessageCaptcha() {
        this.position = 0;
        switchFocus(position);
        SoftInputUtil.openSoftInput(RegisterCaptchaActivity.this, etCaptchaNumList.get(position));
    }

    /**
     * 禁用操作
     * @param tip
     */
    private void forbidOperate(String tip) {
        //显示禁用操作的提示（目前用默认的文本）
        tvForbidLogin.setVisibility(View.VISIBLE);
        if (!TextUtils.isEmpty(tip)) {
            tvForbidLogin.setText(tip);
        }
        //关闭短信发送按钮
        btnSendAgain.setEnabled(false);
        //关闭确认按钮
        btnConfirm.setEnabled(false);
    }


    /**
     * 弹出重置密码成功确认对话框
     */
    private void popupResetPasswordSuccessWindows() {
        AlertDialog dialog = new android.support.v7.app.AlertDialog.Builder(RegisterCaptchaActivity.this)
                .setTitle(getResources().getString(R.string.dialog_common_title))
                .setMessage("重置密码成功，请重新登录")
//                .setNegativeButton("关闭", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                    }
//                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                        //跳转到登录主页面（作为根activity）
                        gotoLoginPage(RegisterCaptchaActivity.this);
                    }
                })
                .create();

        //不能回退关闭
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setWindowAnimations(R.style.WUI_Animation_Dialog);
        dialog.show();
    }

}
