package com.cheersmind.cheersgenie.features.modules.register.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cheersmind.cheersgenie.BuildConfig;
import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.constant.Dictionary;
import com.cheersmind.cheersgenie.features.constant.ErrorCode;
import com.cheersmind.cheersgenie.features.dto.CreateSessionDto;
import com.cheersmind.cheersgenie.features.dto.MessageCaptchaDto;
import com.cheersmind.cheersgenie.features.dto.ThirdLoginDto;
import com.cheersmind.cheersgenie.features.entity.SessionCreateResult;
import com.cheersmind.cheersgenie.features.interfaces.OnResultListener;
import com.cheersmind.cheersgenie.features.modules.base.activity.BaseActivity;
import com.cheersmind.cheersgenie.features.modules.login.activity.PhoneNumLoginActivity;
import com.cheersmind.cheersgenie.features.modules.login.activity.XLoginAccountActivity;
import com.cheersmind.cheersgenie.features.utils.DataCheckUtil;
import com.cheersmind.cheersgenie.features.utils.DeviceUtil;
import com.cheersmind.cheersgenie.features.utils.PhoneMessageTestUtil;
import com.cheersmind.cheersgenie.features.utils.SoftInputUtil;
import com.cheersmind.cheersgenie.main.Exception.QSCustomException;
import com.cheersmind.cheersgenie.main.entity.ErrorCodeEntity;
import com.cheersmind.cheersgenie.main.service.BaseService;
import com.cheersmind.cheersgenie.main.service.DataRequestService;
import com.cheersmind.cheersgenie.main.util.InjectionWrapperUtil;
import com.cheersmind.cheersgenie.main.util.JsonUtil;
import com.cheersmind.cheersgenie.main.util.ToastUtil;
import com.cheersmind.cheersgenie.main.view.LoadingView;

import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 手机号输入页面（注册环节）
 */
public class RegisterPhoneNumActivity extends BaseActivity {

    //刷新图形验证码
    private static final int MSG_REFRESH_IMAGE_CAPTCHA = 1;
    //需要图形验证码
    private static final int MSG_REQUIRED_IMAGE_CAPTCHA = 2;

    //短信业务类型：0:注册用户，1：短信登录，2、绑定手机，3、重置密码
    private static final String  SMS_TYPE = "sms_type";
    //第三方平台登录信息Dto
    private static final String THIRD_LOGIN_DTO = "thirdLoginDto";
    //手机号
    private static final String PHONE_NUM = "phone_num";

    //短信业务类型：0:注册用户，1：短信登录，2、绑定手机，3、重置密码
    private int smsType;
    //第三方平台登录信息dto
    private ThirdLoginDto thirdLoginDto;
    //手机号
    String phoneNum;

    //子标题
    @BindView(R.id.tv_sub_title)
    TextView tvSubTitle;
    //子标题：绑定第三方提示
    @BindView(R.id.tv_sub_title_bind)
    TextView tvSubTitleBind;

    @BindView(R.id.btn_confirm)
    Button btnConfirm;
    @BindView(R.id.et_phonenum)
    EditText etPhonenum;
    //手机号的清空按钮
    @BindView(R.id.iv_clear)
    ImageView ivClear;
    @BindView(R.id.tv_goto_login)
    TextView tvGotoLogin;
    @BindView(R.id.et_image_captcha)
    EditText etImageCaptcha;
    @BindView(R.id.iv_image_captcha)
    ImageView ivImageCaptcha;
    @BindView(R.id.rl_image_captcha)
    RelativeLayout rlImageCaptcha;


    //前往登录页面的提示文本
    private String tvGotoLoginTip = "<font color='#333333'>检测到您的手机号已注册，</font><font color='#FF8A08'>请登录</font>";

    /**
     * * 开启手机号输入页面
     * @param context
     * @param smsType 短信业务类型(必填)：0:注册用户，1：短信登录，2、绑定手机，3、重置密码
     * @param thirdLoginDto 第三方平台登录信息
     * @param phoneNum 手机号
     */
    public static void startRegisterPhoneNumActivity(Context context, int smsType, ThirdLoginDto thirdLoginDto,  String phoneNum) {
        Intent intent = new Intent(context, RegisterPhoneNumActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        Bundle extras = new Bundle();
        extras.putInt(SMS_TYPE, smsType);
        extras.putSerializable(THIRD_LOGIN_DTO, thirdLoginDto);
        extras.putString(PHONE_NUM, phoneNum);
        intent.putExtras(extras);
        context.startActivity(intent);
    }


    @Override
    protected int setContentView() {
        return R.layout.activity_register_phonenum;
    }

    @Override
    protected String settingTitle() {
        return "手机号输入";
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
        //监听确认密码输入框的软键盘回车键
        etPhonenum.setOnEditorActionListener(new TextView.OnEditorActionListener() {
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

        //设置前往登录页面的提示文本
        tvGotoLogin.setText(Html.fromHtml(tvGotoLoginTip));
    }

    @Override
    protected void onInitData() {
        if (getIntent() == null || getIntent().getExtras() == null) {
            ToastUtil.showShort(getApplication(), "数据传递有误");
            return;
        }

        thirdLoginDto = (ThirdLoginDto) getIntent().getSerializableExtra(THIRD_LOGIN_DTO);
        phoneNum = getIntent().getExtras().getString(PHONE_NUM);

        //初始化手机号
        if (!TextUtils.isEmpty(phoneNum)) {
            etPhonenum.setText(phoneNum);
            etPhonenum.setSelection(phoneNum.length());
        }


        //操作类型
        smsType = getIntent().getIntExtra(SMS_TYPE, Dictionary.SmsType_Register);
        tvSubTitle.setVisibility(View.GONE);
        tvSubTitleBind.setVisibility(View.GONE);
        switch (smsType) {
            //操作类型：绑定手机号
            case Dictionary.SmsType_Bind_Phone_Num: {
                //设置标题
                settingTitle("绑定手机号");
                tvSubTitleBind.setVisibility(View.VISIBLE);
                if (thirdLoginDto != null) {
                    //微信
                    if (Dictionary.Plat_Source_Weixin.equals(thirdLoginDto.getPlatSource())) {
                        tvSubTitleBind.setText(getString(R.string.bind_tip, "微信"));

                    } else if (Dictionary.Plat_Source_QQ.equals(thirdLoginDto.getPlatSource())) {
                        tvSubTitleBind.setText(getString(R.string.bind_tip, "QQ"));

                    } else {
                        tvSubTitleBind.setText(getString(R.string.bind_tip, "账号"));
                    }
                } else {
                    tvSubTitleBind.setText(getString(R.string.bind_tip, "账号"));
                }
                break;
            }
            //操作类型：注册用户
            case Dictionary.SmsType_Register: {
                if (thirdLoginDto == null) {
                    //设置标题
                    settingTitle("账号注册");
                    tvSubTitle.setVisibility(View.VISIBLE);
                } else {
                    //设置标题
                    settingTitle("绑定手机号");
                    tvSubTitleBind.setVisibility(View.VISIBLE);
                    //微信
                    if (Dictionary.Plat_Source_Weixin.equals(thirdLoginDto.getPlatSource())) {
                        tvSubTitleBind.setText(getString(R.string.bind_tip, "微信"));

                    } else if (Dictionary.Plat_Source_QQ.equals(thirdLoginDto.getPlatSource())) {
                        tvSubTitleBind.setText(getString(R.string.bind_tip, "QQ"));
                    } else {
                        tvSubTitleBind.setText(getString(R.string.bind_tip, "账号"));
                    }
                }
                break;
            }
            //操作类型：找回密码
            case Dictionary.SmsType_Retrieve_Password: {
                //设置标题
                settingTitle("重置密码");
                tvSubTitle.setVisibility(View.VISIBLE);
                break;
            }
        }
    }


    @OnClick({R.id.btn_confirm,R.id.tv_goto_login, R.id.iv_image_captcha,R.id.iv_clear})
    public void onViewClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            //下一步
            case R.id.btn_confirm: {
                doNextDept();
                break;
            }
            //跳转到账号登录页面
            case R.id.tv_goto_login: {
                gotoAccountLoginPage(RegisterPhoneNumActivity.this);
                finish();
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
            //手机号的清空按钮
            case R.id.iv_clear: {
                etPhonenum.setText("");
                etPhonenum.requestFocus();
                break;
            }
        }
    }

    /**
     * 请求发送短信验证码
     * @param phoneNum 手机号
     * @param smsType 短信业务类型：0:注册用户，1：短信登录，2、绑定手机，3、重置密码
     */
    private void querySendCaptcha(final String phoneNum, final int smsType, String sessionId, String imageCaptcha) {
        //隐藏软键盘
        SoftInputUtil.closeSoftInput(RegisterPhoneNumActivity.this);
        //开启通信等待提示
        LoadingView.getInstance().show(this, httpTag);

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
                            //手机号已注册
                            if (ErrorCode.AC_PHONE_HAS_REGISTER.equals(errorCode)) {
                                //绑定手机号
                                if (smsType == Dictionary.SmsType_Bind_Phone_Num) {
                                    //显示可以直接跳转到账号登录页面的按钮
                                    tvGotoLogin.setVisibility(View.VISIBLE);
                                    ToastUtil.showShort(getApplication(), "该手机号已注册，请登录");
                                    //重新请求图形验证码
                                    requestImageCaptchaAgainWhenVisible();

                                } else if (smsType == Dictionary.SmsType_Register) {//注册
                                    //注册
                                    if (thirdLoginDto == null) {
                                        //显示可以直接跳转到账号登录页面的按钮
                                        tvGotoLogin.setVisibility(View.VISIBLE);
                                        ToastUtil.showShort(getApplication(), "该手机号已注册，请登录");
                                        //重新请求图形验证码
                                        requestImageCaptchaAgainWhenVisible();

                                    } else {//第三方平台登录（用户可以有两个选择：1、跳转账号登录页面；2、用该手机号绑定该第三方平台）
                                        //显示可以直接跳转到账号登录页面的按钮
                                        tvGotoLogin.setVisibility(View.VISIBLE);

                                        //直接进入绑定环节
                                        PhoneNumLoginActivity.startPhoneNumLoginActivity(RegisterPhoneNumActivity.this, thirdLoginDto, phoneNum);
//                                        XLoginAccountActivity.startXLoginAccountActivity(RegisterPhoneNumActivity.this, thirdLoginDto, phoneNum);
//                                        ToastUtil.showShort(getApplication(), "该手机号已注册，请登录");
                                    }
                                }

                                //标记已经处理了异常
                                return true;

                            }else if (ErrorCode.AC_USER_NOT_EXIST.equals(errorCode)) {//用户不存在
                                //找回密码
                                if (smsType == Dictionary.SmsType_Retrieve_Password) {
                                    //重新请求图形验证码
                                    requestImageCaptchaAgainWhenVisible();
                                }

                            } else if (ErrorCode.AC_SESSION_EXPIRED.equals(errorCode) || ErrorCode.AC_SESSION_INVALID.equals(errorCode)) {//Session 未创建或已过期、无效
                                //重新获取会话
                                sessionCreateResult = null;
                                //创建会话然后发送短信验证码
                                LoadingView.getInstance().show(RegisterPhoneNumActivity.this, httpTag);
                                doPostAccountSessionForSendMessageCaptcha(false);

                                //标记已经处理了异常
                                return true;

                            } else if (ErrorCode.AC_IDENTIFY_CODE_REQUIRED.equals(errorCode)) {//需要图形验证码
                                //如果当前状态为正常，则置为不正常
                                if (sessionCreateResult.getNormal()) {
                                    sessionCreateResult.setNormal(false);
                                }
                                //开启通信等待
                                LoadingView.getInstance().show(RegisterPhoneNumActivity.this, httpTag);
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
                    LoadingView.getInstance().dismiss();
                    //隐藏可以直接跳转到账号登录页面的按钮
                    if (tvGotoLogin.getVisibility() == View.VISIBLE) {
                        tvGotoLogin.setVisibility(View.GONE);
                    }

                    String messageCaptcha = "";
                    String hostType = BuildConfig.HOST_TYPE;
                    if(!"product".equals(hostType)) {
                        Map dataMap = JsonUtil.fromJson(obj.toString(), Map.class);
                        messageCaptcha = "短信验证码【" + dataMap.get("code").toString() + "】";
                    }
                    //跳转验证码输入页面
                    RegisterCaptchaActivity.startRegisterCaptchaActivity(RegisterPhoneNumActivity.this, phoneNum, smsType, thirdLoginDto, messageCaptcha);
                }
            }, httpTag, RegisterPhoneNumActivity.this);

        } catch (QSCustomException e) {
            e.printStackTrace();
            ToastUtil.showShort(getApplication(), e.getMessage());
        }
    }


    /**
     * 下一步操作：1、格式验证；2、请求发送验证码；3、验证成功后跳转验证码输入页面
     */
    private void doNextDept() {
        //隐藏软键盘
        SoftInputUtil.closeSoftInput(RegisterPhoneNumActivity.this);
        //格式验证
        if (!checkData()) return;

        //发送短信验证码必须要有sessionId
        if (sessionCreateResult == null || TextUtils.isEmpty(sessionCreateResult.getSessionId())) {
            //创建会话然后发送短信验证码
            LoadingView.getInstance().show(this, httpTag);
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
        querySendCaptcha(phoneNum, smsType, sessionId, imageCaptcha);
        //测试：跳转验证码输入页面
//        String phoneNum = etPhonenum.getText().toString();
//        RegisterCaptchaActivity.startRegisterCaptchaActivity(RegisterPhoneNumActivity.this, phoneNum, smsType, thirdLoginDto);
    }

    /**
     * 验证数据格式
     * @return true：验证通过，false：验证不通过
     */
    private boolean checkData() {
        String phoneNum = etPhonenum.getText().toString();
        //非空
        if (TextUtils.isEmpty(phoneNum)) {
            ToastUtil.showShort(getApplication(), "请输入手机号");
            etPhonenum.requestFocus();
            return false;

        } else {
            //手机号格式
            if (!DataCheckUtil.isMobileNO(phoneNum)) {
                ToastUtil.showShort(getApplication(), "请输入正确的手机号");
                etPhonenum.requestFocus();
                return  false;
            }
        }

        //图形验证码
        if (sessionCreateResult != null && !sessionCreateResult.getNormal()) {
            String imageCaptcha = etImageCaptcha.getText().toString();
            if (TextUtils.isEmpty(imageCaptcha)) {
                //需要图形验证码
                Message.obtain(getHandler(), MSG_REQUIRED_IMAGE_CAPTCHA).sendToTarget();
                return false;
            }
        }

        return true;
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
                //下一步（发送短信验证码）
                doNextDept();
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
                Message.obtain(getHandler(), MSG_REFRESH_IMAGE_CAPTCHA, obj).sendToTarget();

                //成功回调
                if (listener != null) {
                    listener.onSuccess();
                }
            }
        }, httpTag);
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
            //如果当前图形验证码的布局还没显示，则显示并且请求图形验证码
            if (rlImageCaptcha.getVisibility() == View.GONE) {
                /*//显示图形验证码布局
                rlImageCaptcha.setVisibility(View.VISIBLE);
                //需要图形验证码，才能发送短信的提示处理
                requiredImageCaptchaForSendError();
                //请求图形验证码
                getImageCaptcha(sessionCreateResult.getSessionId(), null);*/

                //开启通信等待
                LoadingView.getInstance().show(RegisterPhoneNumActivity.this, httpTag);
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

            } else {
                //关闭通信等待
                LoadingView.getInstance().dismiss();

                //聚焦图形验证码
                etImageCaptcha.requestFocus();
                ToastUtil.showShort(getApplication(), "请输入图形验证码");
            }
        }
    }

    /**
     * 需要图形验证码，才能发送短信的提示处理
     */
    private void requiredImageCaptchaForSendError() {
        ToastUtil.showShort(getApplication(),"请输入图形验证码后，重新操作");

        //清空图形验证码，并聚焦
        etImageCaptcha.setText("");
        etImageCaptcha.requestFocus();

    }

    /**
     *  如果当前图形验证码布局是显示着的，则请求图形验证码，并清空图形验证码编辑框
     */
    private void requestImageCaptchaAgainWhenVisible() {
        if (rlImageCaptcha.getVisibility() == View.VISIBLE) {
            etImageCaptcha.setText("");
            getImageCaptcha(sessionCreateResult.getSessionId(), null);
        }
    }


}
