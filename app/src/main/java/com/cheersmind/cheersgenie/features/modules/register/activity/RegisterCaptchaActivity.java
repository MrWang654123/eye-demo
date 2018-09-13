package com.cheersmind.cheersgenie.features.modules.register.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.constant.Dictionary;
import com.cheersmind.cheersgenie.features.dto.MessageCaptchaDto;
import com.cheersmind.cheersgenie.features.dto.ThirdLoginDto;
import com.cheersmind.cheersgenie.features.modules.base.activity.BaseActivity;
import com.cheersmind.cheersgenie.features.utils.SoftInputUtil;
import com.cheersmind.cheersgenie.main.Exception.QSCustomException;
import com.cheersmind.cheersgenie.main.service.BaseService;
import com.cheersmind.cheersgenie.main.service.DataRequestService;
import com.cheersmind.cheersgenie.main.util.ToastUtil;
import com.cheersmind.cheersgenie.main.view.LoadingView;

import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.OnClick;

/**
 * 验证码输入页面（注册）
 */
public class RegisterCaptchaActivity extends BaseActivity {
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
            bindPhoneNum(phoneNum, captcha);

        } else if (smsType == Dictionary.SmsType_Retrieve_Password) {//操作类型：找回密码
            //跳转到重置密码页面
            RetrievePasswordActivity.startRetrievePasswordActivity(RegisterCaptchaActivity.this, phoneNum, captcha);
        }
    }

    /**
     * 绑定手机号
     * @param phoneNum
     * @param captcha
     */
    private void bindPhoneNum(String phoneNum, String captcha) {
        //隐藏软键盘
        SoftInputUtil.closeSoftInput(RegisterCaptchaActivity.this);
        //开启通信等待提示
        LoadingView.getInstance().show(this);

        DataRequestService.getInstance().putBindPhoneNum(phoneNum, captcha,
                Dictionary.Tenant_CheersMind, Dictionary.Area_Code_86, new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                onFailureDefault(e);
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
     * 验证数据格式
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
        ToastUtil.showShort(getApplicationContext(), "请输入验证码");
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
        for (int i = 0; i < etCaptchaNumList.size(); i++) {
            EditText et = etCaptchaNumList.get(i);
            if (i == position) {
                et.setFocusable(true);
                et.setFocusableInTouchMode(true);
                et.requestFocus();
                //设置编辑框的内容为ZERO_WIDTH_SPACE（用于监听删除键的响应）
                etCaptchaNumList.get(position).setText(ZERO_WIDTH_SPACE);
                etCaptchaNumList.get(position).setSelection(1);
            } else {
                et.setFocusable(false);
                et.setFocusableInTouchMode(false);
            }
        }
    }


    @OnClick({R.id.btn_confirm, R.id.btn_send_again})
    public void click(View view) {
        switch (view.getId()) {
            //重新发送
            case R.id.btn_send_again: {
                //开启计时器
                countTimer.start();
                //请求发送验证码
                querySendMessageCaptcha(phoneNum, smsType, null, null);
                break;
            }
            //下一步
            case R.id.btn_confirm: {
                doNextDept();
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
                    onFailureDefault(e);
                }

                @Override
                public void onResponse(Object obj) {
                    //关闭通信等待提示
                    LoadingView.getInstance().dismiss();
                }
            });
        } catch (QSCustomException e) {
            e.printStackTrace();
            ToastUtil.showShort(getApplicationContext(), e.getMessage());
        }
    }

}
