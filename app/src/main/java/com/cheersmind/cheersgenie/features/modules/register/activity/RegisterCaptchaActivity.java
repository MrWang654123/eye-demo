package com.cheersmind.cheersgenie.features.modules.register.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.constant.Dictionary;
import com.cheersmind.cheersgenie.features.constant.ErrorCode;
import com.cheersmind.cheersgenie.features.dto.BindPhoneNumDto;
import com.cheersmind.cheersgenie.features.dto.CreateSessionDto;
import com.cheersmind.cheersgenie.features.dto.MessageCaptchaDto;
import com.cheersmind.cheersgenie.features.dto.ThirdLoginDto;
import com.cheersmind.cheersgenie.features.entity.SessionCreateResult;
import com.cheersmind.cheersgenie.features.interfaces.OnResultListener;
import com.cheersmind.cheersgenie.features.modules.base.activity.BaseActivity;
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

    //当前输入索引
    int position = 0;

    //0宽度空格符（在edittext没有内容的时候，按软键盘的删除键,不触发TextWatcher）
    private static final String ZERO_WIDTH_SPACE = "\uFEFF";

    //计时器
    private CountTimer countTimer;

    //手机号
    String phoneNum;

    //Session创建结果
    SessionCreateResult sessionCreateResult;

    /**
     * 启动注册环节手机验证码页面
     * @param context
     * @param phoneNum 手机号
     * @param smsType 短信业务类型：0:注册用户，1：短信登录，2：绑定手机号，3、重置密码
     * @param thirdLoginDto 第三方平台登录信息
     */
    public static void startRegisterCaptchaActivity(Context context, String phoneNum, int smsType, ThirdLoginDto thirdLoginDto) {
        Intent intent = new Intent(context, RegisterCaptchaActivity.class);
        Bundle extras = new Bundle();
        extras.putString(PHONE_NUM, phoneNum);
        extras.putInt(SMS_TYPE, smsType);
        extras.putSerializable(THIRD_LOGIN_DTO, thirdLoginDto);
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
    }

    @Override
    protected void onInitData() {
        if (getIntent() == null || getIntent().getExtras() == null) {
            ToastUtil.showShort(getApplicationContext(), "数据传递有误");
            return;
        }

//        topicInfoEntity = (TopicInfoEntity)getIntent().getExtras().getSerializable(TOPIC_INFO);
        phoneNum = getIntent().getExtras().getString(PHONE_NUM);
        if (TextUtils.isEmpty(phoneNum)) {
            ToastUtil.showShort(getApplicationContext(), "数据传递有误");
            return;
        }
        //发送号码提示
        tvSendPhonenumTip.setText(getResources().getString(R.string.captcha_has_send_to, phoneNum));
        //操作类型
        smsType = getIntent().getExtras().getInt(SMS_TYPE);
        //操作类型为绑定手机号
        if (smsType == Dictionary.SmsType_Bind_Phone_Num) {
            //修改按钮文字为“绑定”
            btnConfirm.setText(getResources().getString(R.string.bind));
        }

        thirdLoginDto = (ThirdLoginDto) getIntent().getSerializableExtra(THIRD_LOGIN_DTO);
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
        SoftInputUtil.closeSoftInput(RegisterCaptchaActivity.this);

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //自动操作下一步
                doNextDept();
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
            PasswordInitActivity.startPasswordInitActivity(RegisterCaptchaActivity.this, phoneNum, captcha, thirdLoginDto);

        } else if (smsType == Dictionary.SmsType_Bind_Phone_Num) {//操作类型：绑定手机号
            //绑定手机号
            bindPhoneNumWrap();

        } else if (smsType == Dictionary.SmsType_Retrieve_Password) {//操作类型：找回密码
            //跳转到重置密码页面
            RetrievePasswordActivity.startRetrievePasswordActivity(RegisterCaptchaActivity.this, phoneNum, captcha);
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
            doPostAccountSessionForBindPhoneNum();
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
                            doPostAccountSessionForBindPhoneNum();

                            //标记已经处理了异常
                            return true;

                        } else if (ErrorCode.AC_SMSCODE_ERROR_OVER_SUM.equals(errorCode)) {//您的今天短信验证码输入错误次数已超过上限
                            //禁用注册
                            forbidRegister(errorCodeEntity.getMessage());
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


    @OnClick({R.id.btn_confirm, R.id.btn_send_again, R.id.iv_image_captcha})
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
            doPostAccountSessionForSendMessageCaptcha();
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
                                doPostAccountSessionForSendMessageCaptcha();

                                //标记已经处理了异常
                                return true;

                            } else if (ErrorCode.AC_IDENTIFY_CODE_REQUIRED.equals(errorCode)) {//需要图形验证码
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
     * 创建会话（目前只用打*的两种类型）
     *
     * @param type 类型：会话类型，0：注册(手机)， *1：登录(帐号、密码登录)，2：手机找回密码，3：登录(短信登录)， *4:下发短信验证码
     * @param showLoading 是否显示通信等待
     * @param listener 监听
     */
    private void doPostAccountSession(int type, boolean showLoading, final OnResultListener listener) {
        if (showLoading) {
            LoadingView.getInstance().show(RegisterCaptchaActivity.this);
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
            }

            @Override
            public void onResponse(Object obj) {
                try {
                    LoadingView.getInstance().dismiss();

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
    private void doPostAccountSessionForSendMessageCaptcha() {
        doPostAccountSession(Dictionary.CREATE_SESSION_MESSAGE_CAPTCHA, true, new OnResultListener() {

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
    private void doPostAccountSessionForBindPhoneNum() {
        doPostAccountSession(Dictionary.CREATE_SESSION_MESSAGE_CAPTCHA, true, new OnResultListener() {

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

        } else if (msg.what == MSG_REQUIRED_IMAGE_CAPTCHA) {//需要图形验证码
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
     * 禁用注册
     * @param tip
     */
    private void forbidRegister(String tip) {
        //显示禁用登录的提示（目前用默认的文本）
        tvForbidLogin.setVisibility(View.VISIBLE);
        if (!TextUtils.isEmpty(tip)) {
            tvForbidLogin.setText(tip);
        }
        //关闭短信发送按钮
        btnSendAgain.setEnabled(false);
        //关闭登录按钮
        btnConfirm.setEnabled(false);
    }

}
