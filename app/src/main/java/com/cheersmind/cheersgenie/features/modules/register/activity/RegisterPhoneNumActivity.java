package com.cheersmind.cheersgenie.features.modules.register.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.constant.Dictionary;
import com.cheersmind.cheersgenie.features.constant.ErrorCode;
import com.cheersmind.cheersgenie.features.dto.CreateSessionDto;
import com.cheersmind.cheersgenie.features.dto.MessageCaptchaDto;
import com.cheersmind.cheersgenie.features.dto.ThirdLoginDto;
import com.cheersmind.cheersgenie.features.entity.SessionCreateResult;
import com.cheersmind.cheersgenie.features.modules.base.activity.BaseActivity;
import com.cheersmind.cheersgenie.features.utils.DataCheckUtil;
import com.cheersmind.cheersgenie.features.utils.DeviceUtil;
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

    //短信业务类型：0:注册用户，1：短信登录，2、绑定手机，3、重置密码
    private static final String  SMS_TYPE = "sms_type";
    //第三方平台登录信息Dto
    private static final String THIRD_LOGIN_DTO = "thirdLoginDto";

    //短信业务类型：0:注册用户，1：短信登录，2、绑定手机，3、重置密码
    private int smsType;
    //第三方平台登录信息dto
    private ThirdLoginDto thirdLoginDto;

    @BindView(R.id.btn_confirm)
    Button btnConfirm;
    @BindView(R.id.et_phonenum)
    EditText etPhonenum;
    @BindView(R.id.tv_goto_login)
    TextView tvGotoLogin;


    /**
     * * 开启手机号输入页面
     * @param context
     * @param smsType 短信业务类型(必填)：0:注册用户，1：短信登录，2、绑定手机，3、重置密码
     * @param thirdLoginDto 第三方平台登录信息
     */
    public static void startRegisterPhoneNumActivity(Context context, int smsType, ThirdLoginDto thirdLoginDto) {
        Intent intent = new Intent(context, RegisterPhoneNumActivity.class);
        Bundle extras = new Bundle();
        extras.putInt(SMS_TYPE, smsType);
        extras.putSerializable(THIRD_LOGIN_DTO, thirdLoginDto);
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
    }

    @Override
    protected void onInitData() {
        if (getIntent() == null || getIntent().getExtras() == null) {
            ToastUtil.showShort(getApplicationContext(), "数据传递有误");
            return;
        }
        smsType = getIntent().getExtras().getInt(SMS_TYPE);
        thirdLoginDto = (ThirdLoginDto) getIntent().getSerializableExtra(THIRD_LOGIN_DTO);

    }


    @OnClick({R.id.btn_confirm,R.id.tv_goto_login})
    public void click(View view) {
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
                            //手机号已注册
                            if (ErrorCode.AC_PHONE_HAS_REGISTER.equals(errorCode)) {
                                //显示可以直接跳转到账号登录页面的按钮
                                tvGotoLogin.setVisibility(View.VISIBLE);
                                ToastUtil.showShort(getApplicationContext(), "该手机号已注册，请登录");
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
                    //跳转验证码输入页面
                    RegisterCaptchaActivity.startRegisterCaptchaActivity(RegisterPhoneNumActivity.this, phoneNum, smsType, thirdLoginDto);
                }
            });
        } catch (QSCustomException e) {
            e.printStackTrace();
            ToastUtil.showShort(getApplicationContext(), e.getMessage());
        }
    }


    //Session创建结果
    SessionCreateResult sessionCreateResult;

    /**
     * 创建会话（目前只用打*的两种类型）
     *
     * @param type 类型：会话类型，0：注册(手机)， *1：登录(帐号、密码登录)，2：手机找回密码，3：登录(短信登录)， *4:下发短信验证码
     */
    private void doPostAccountSession(int type) {
        CreateSessionDto dto = new CreateSessionDto();
        dto.setSessionType(type);//类型
        dto.setTenant(Dictionary.Tenant_CheersMind);//租户名
        dto.setDeviceId(DeviceUtil.getDeviceId(getApplicationContext()));//设备ID
        //请求创建会话
        DataRequestService.getInstance().postAccountsSessions(dto, new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                onFailureDefault(e);
            }

            @Override
            public void onResponse(Object obj) {
                try {
                    Map dataMap = JsonUtil.fromJson(obj.toString(), Map.class);
                    sessionCreateResult = InjectionWrapperUtil.injectMap(dataMap, SessionCreateResult.class);
                    //非空
                    if (sessionCreateResult == null || TextUtils.isEmpty(sessionCreateResult.getSessionId())) {
                        throw new Exception();
                    }

                    //不正常
                    if (!sessionCreateResult.getNormal()) {
                        //显示图形验证码布局
//                        rlImageCaptcha.setVisibility(View.VISIBLE);
                        //请求图形验证码
//                        getImageCaptcha(sessionCreateResult.getSessionId());
                    } else {
                        //请求发送短信验证码
                        String phoneNum = etPhonenum.getText().toString();
                        querySendCaptcha(phoneNum, smsType, sessionCreateResult.getSessionId(), null);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    onFailure(new QSCustomException("操作失败"));
                }
            }
        });
    }


    /**
     * 下一步操作：1、格式验证；2、请求发送验证码；3、验证成功后跳转验证码输入页面
     */
    private void doNextDept() {
        //隐藏软键盘
        SoftInputUtil.closeSoftInput(RegisterPhoneNumActivity.this);
        //格式验证
        if (!checkData()) return;

        //创建会话，类型：下发短信验证码
        doPostAccountSession(Dictionary.CREATE_SESSION_MESSAGE_CAPTCHA);
        //请求发送短信验证码
//        String phoneNum = etPhonenum.getText().toString();
//        querySendCaptcha(phoneNum, smsType);


        //测试：跳转验证码输入页面
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
            ToastUtil.showShort(getApplicationContext(), "请输入手机号");
            return false;

        } else {
            //手机号格式
            if (!DataCheckUtil.isMobileNO(phoneNum)) {
                ToastUtil.showShort(getApplicationContext(), "请输入正确的手机号");
                return  false;
            }
        }

        return true;
    }

}
