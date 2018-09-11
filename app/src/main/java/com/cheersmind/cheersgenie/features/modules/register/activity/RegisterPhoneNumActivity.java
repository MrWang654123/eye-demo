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
import com.cheersmind.cheersgenie.features.dto.ThirdLoginDto;
import com.cheersmind.cheersgenie.features.modules.base.activity.BaseActivity;
import com.cheersmind.cheersgenie.features.utils.DataCheckUtil;
import com.cheersmind.cheersgenie.features.utils.SoftInputUtil;
import com.cheersmind.cheersgenie.main.Exception.QSCustomException;
import com.cheersmind.cheersgenie.main.entity.ErrorCodeEntity;
import com.cheersmind.cheersgenie.main.service.BaseService;
import com.cheersmind.cheersgenie.main.service.DataRequestService;
import com.cheersmind.cheersgenie.main.util.ToastUtil;
import com.cheersmind.cheersgenie.main.view.LoadingView;

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
     * 请求发送注册验证码
     * @param phoneNum 手机号
     * @param smsType 短信业务类型：0:注册用户，1：短信登录，2、绑定手机，3、重置密码
     */
    private void querySendCaptcha(final String phoneNum, final int smsType) {
        //隐藏软键盘
        SoftInputUtil.closeSoftInput(RegisterPhoneNumActivity.this);
        //开启通信等待提示
        LoadingView.getInstance().show(this);

        DataRequestService.getInstance().postRegisterCaptcha(phoneNum, smsType, new BaseService.ServiceCallback() {
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
    }

    /**
     * 下一步操作：1、格式验证；2、请求发送验证码；3、验证成功后跳转验证码输入页面
     */
    private void doNextDept() {
        //隐藏软键盘
        SoftInputUtil.closeSoftInput(RegisterPhoneNumActivity.this);
        //格式验证
        if (!checkData()) return;
        //请求发送验证码
        String phoneNum = etPhonenum.getText().toString();
        querySendCaptcha(phoneNum, smsType);
        //跳转验证码输入页面
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
