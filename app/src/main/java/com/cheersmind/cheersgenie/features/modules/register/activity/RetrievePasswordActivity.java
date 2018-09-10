package com.cheersmind.cheersgenie.features.modules.register.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.sdk.android.man.MANService;
import com.alibaba.sdk.android.man.MANServiceProvider;
import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.constant.Dictionary;
import com.cheersmind.cheersgenie.features.dto.ResetPasswordDto;
import com.cheersmind.cheersgenie.features.dto.ThirdLoginDto;
import com.cheersmind.cheersgenie.features.modules.base.activity.BaseActivity;
import com.cheersmind.cheersgenie.features.modules.login.activity.XLoginActivity;
import com.cheersmind.cheersgenie.features.modules.mine.activity.ModifyPasswordActivity;
import com.cheersmind.cheersgenie.features.utils.SoftInputUtil;
import com.cheersmind.cheersgenie.main.Exception.QSCustomException;
import com.cheersmind.cheersgenie.main.QSApplication;
import com.cheersmind.cheersgenie.main.constant.HttpConfig;
import com.cheersmind.cheersgenie.main.entity.ErrorCodeEntity;
import com.cheersmind.cheersgenie.main.entity.WXUserInfoEntity;
import com.cheersmind.cheersgenie.main.service.BaseService;
import com.cheersmind.cheersgenie.main.service.DataRequestService;
import com.cheersmind.cheersgenie.main.util.EncryptUtil;
import com.cheersmind.cheersgenie.main.util.InjectionWrapperUtil;
import com.cheersmind.cheersgenie.main.util.JsonUtil;
import com.cheersmind.cheersgenie.main.util.ToastUtil;
import com.cheersmind.cheersgenie.main.view.LoadingView;
import com.cheersmind.cheersgenie.module.login.UCManager;

import org.litepal.crud.DataSupport;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 找回密码
 */
public class RetrievePasswordActivity extends BaseActivity {

    //手机号
    private static final String PHONE_NUM = "phone_num";
    //验证码
    private static final String CAPTCHA = "captcha";

    @BindView(R.id.et_password)
    EditText etPassword;
    @BindView(R.id.et_again_password)
    EditText etAgainPassword;
    @BindView(R.id.btn_confirm)
    Button btnConfirm;
    @BindView(R.id.cbox_password)
    CheckBox cboxPassword;
    @BindView(R.id.cbox_again_password)
    CheckBox cboxAgainPassword;

    //手机号
    String phoneNum;
    //短信验证码
    String captcha;

    /**
     * 启动密码初始化页面
     * @param context
     * @param phoneNum 手机号
     * @param captcha 短信验证码
     */
    public static void startRetrievePasswordActivity(Context context, String phoneNum, String captcha) {
        Intent intent = new Intent(context, RetrievePasswordActivity.class);
        Bundle extras = new Bundle();
        extras.putSerializable(PHONE_NUM, phoneNum);
        extras.putSerializable(CAPTCHA, captcha);
        intent.putExtras(extras);
        context.startActivity(intent);
    }

    @Override
    protected int setContentView() {
        return R.layout.activity_retrieve_password;
    }

    @Override
    protected String settingTitle() {
        return "重置密码";
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

        //确认密码显隐
        cboxAgainPassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    //如果选中，显示密码
                    etAgainPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    //否则隐藏密码
                    etAgainPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
                //光标置于末尾
                etAgainPassword.setSelection(etAgainPassword.getText().length());
            }
        });

        //监听确认密码输入框的软键盘回车键
        etAgainPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                //当actionId == XX_SEND 或者 XX_DONE时都触发
                //或者event.getKeyCode == ENTER 且 event.getAction == ACTION_DOWN时也触发
                //注意，这是一定要判断event != null。因为在某些输入法上会返回null。
                if (actionId == EditorInfo.IME_ACTION_SEND
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || (keyEvent != null && KeyEvent.KEYCODE_ENTER == keyEvent.getKeyCode() && KeyEvent.ACTION_DOWN == keyEvent.getAction())) {
                    //重置密码
                    doResetPassword();
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

        phoneNum = getIntent().getExtras().getString(PHONE_NUM);
        captcha = getIntent().getExtras().getString(CAPTCHA);
        if (TextUtils.isEmpty(phoneNum) || TextUtils.isEmpty(captcha)) {
            ToastUtil.showShort(getApplicationContext(), "数据传递有误");
            return;
        }

    }


    @OnClick(R.id.btn_confirm)
    public void submit(View view) {
        switch (view.getId()) {
            case R.id.btn_confirm:
                //重置密码
                doResetPassword();
                break;
        }
    }

    /**
     * 验证数据格式
     * （密码长度6-24位，可以是数字、字母等任意字符）
     * @return
     */
    private boolean checkData() {
        //密码
        String password = etPassword.getText().toString();
        //确认密码
        String passwordAgain = etAgainPassword.getText().toString();

        //密码长度验证
        if (password.length() < 6 || password.length() > 24) {
            ToastUtil.showShort(getApplicationContext(), "新密码长度为6-24位");
            return false;
        }

        //确认密码长度验证
        //密码长度验证
        if (passwordAgain.length() < 6 || passwordAgain.length() > 24) {
            ToastUtil.showShort(getApplicationContext(), "确认密码长度为6-24位");
            return false;
        }

        //两次密码是否一致
        if (!password.equals(passwordAgain)) {
            ToastUtil.showShort(getApplicationContext(), "确认密码不一致");
            return false;
        }

        return true;
    }

    /**
     * 重置密码
     */
    private void doResetPassword() {
        //隐藏软键盘
        SoftInputUtil.closeSoftInput(RetrievePasswordActivity.this);
        //格式验证
        if (!checkData()) return;

        //请求重置密码
        ResetPasswordDto dto = new ResetPasswordDto();
        dto.setMobile(phoneNum);
        dto.setMobile_code(captcha);
        dto.setTenant(Dictionary.Tenant_CheersMind);
        dto.setNew_password(etPassword.getText().toString());
        dto.setArea_code(Dictionary.Area_Code_86);
        patchResetPassword(dto);
    }

    /**
     * 跳转班级号输入页面
     */
    private void gotoClassNumPage() {
        Intent intent = new Intent(RetrievePasswordActivity.this, ClassNumActivity.class);
        startActivity(intent);
    }

    /**
     * 请求重置密码
     * @param dto
     */
    private void patchResetPassword(ResetPasswordDto dto) {
        //通信等待提示
        LoadingView.getInstance().show(RetrievePasswordActivity.this);
        //重置密码
        DataRequestService.getInstance().patchResetPassword(dto, new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                onFailureDefault(e);
            }

            @Override
            public void onResponse(Object obj) {
                LoadingView.getInstance().dismiss();

                //重置密码成功后的处理
                //清空密码缓存
                SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(QSApplication.getContext());
                SharedPreferences.Editor editor = pref.edit();
                editor.remove("user_password");
                editor.commit();

                //跳转到登录主页面（作为根activity）
                Intent intent = new Intent(RetrievePasswordActivity.this, XLoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });
    }


}
