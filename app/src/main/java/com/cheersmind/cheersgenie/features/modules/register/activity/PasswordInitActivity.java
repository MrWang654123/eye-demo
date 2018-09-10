package com.cheersmind.cheersgenie.features.modules.register.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
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
import com.cheersmind.cheersgenie.features.dto.ThirdLoginDto;
import com.cheersmind.cheersgenie.features.modules.base.activity.BaseActivity;
import com.cheersmind.cheersgenie.features.utils.SoftInputUtil;
import com.cheersmind.cheersgenie.main.Exception.QSCustomException;
import com.cheersmind.cheersgenie.main.constant.HttpConfig;
import com.cheersmind.cheersgenie.main.entity.ErrorCodeEntity;
import com.cheersmind.cheersgenie.main.entity.WXUserInfoEntity;
import com.cheersmind.cheersgenie.main.service.BaseService;
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
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 密码初始化页面（注册）
 */
public class PasswordInitActivity extends BaseActivity {

    //手机号
    private static final String PHONE_NUM = "phone_num";
    //验证码
    private static final String CAPTCHA = "captcha";
    //第三方平台登录信息Dto
    private static final String THIRD_LOGIN_DTO = "thirdLoginDto";

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
    //第三方平台登录信息dto
    private ThirdLoginDto thirdLoginDto;

    /**
     * 启动密码初始化页面
     * @param context
     * @param phoneNum 手机号
     * @param captcha 短信验证码
     * @param thirdLoginDto 第三方平台登录信息
     */
    public static void startPasswordInitActivity(Context context, String phoneNum, String captcha, ThirdLoginDto thirdLoginDto) {
        Intent intent = new Intent(context, PasswordInitActivity.class);
        Bundle extras = new Bundle();
        extras.putSerializable(PHONE_NUM, phoneNum);
        extras.putSerializable(CAPTCHA, captcha);
        extras.putSerializable(THIRD_LOGIN_DTO, thirdLoginDto);
        intent.putExtras(extras);
        context.startActivity(intent);
    }

    @Override
    protected int setContentView() {
        return R.layout.activity_password_init;
    }

    @Override
    protected String settingTitle() {
        return "密码初始化";
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
                    //注册
                    doRegister();
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

        thirdLoginDto = (ThirdLoginDto) getIntent().getSerializableExtra(THIRD_LOGIN_DTO);
    }


    @OnClick(R.id.btn_confirm)
    public void submit(View view) {
        switch (view.getId()) {
            case R.id.btn_confirm:
                //注册
                doRegister();
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
            ToastUtil.showShort(getApplicationContext(), "密码长度为6-24位");
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
     * 注册
     */
    private void doRegister() {
        //隐藏软键盘
        SoftInputUtil.closeSoftInput(PasswordInitActivity.this);
        //格式验证
        if (!checkData()) return;
        //请求注册
        queryRegister(phoneNum, captcha, etPassword.getText().toString(), thirdLoginDto);
        //跳转班级号输入页面
//        gotoClassNumPage();
    }

    /**
     * 跳转班级号输入页面
     */
    private void gotoClassNumPage() {
        Intent intent = new Intent(PasswordInitActivity.this, ClassNumActivity.class);
        startActivity(intent);
    }

    /**
     * 请求手机短信账号注册
     * @param phoneNum 手机号
     * @param captcha 短信验证码
     * @param password 密码
     * @param thirdLoginDto 第三方平台登录信息dto
     */
    private void queryRegister(final String phoneNum, String captcha, final String password, ThirdLoginDto thirdLoginDto) {
        String url = HttpConfig.URL_PHONE_MESSAGE_REGISTER;
//        {
//            "mobile":"string",            //手机号(必填)
//                "mobile_code":"string",       //手机验证码(必填)，
//                "password":"string",      //登录密码(必填)
//                "area_code":"string",     //手机国际区号(选填)，中国：+86（默认）
//                "tenant":"string",        //租户名称（选填）
//                "open_id":"",             //第三方平台ID(第三方+手机号时必填)
//                "plat_source":"",         //微信：weixin,(第三方+手机号时必填)
//                "third_access_token":""   //第三方平台token(第三方+手机号时必填)
//        }
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("mobile", phoneNum);
        map.put("mobile_code", captcha);
        //密码加密
        String pwdMd5 = EncryptUtil.encryptMD5_QS(password);
        map.put("password", pwdMd5);
        map.put("tenant", Dictionary.Tenant_CheersMind);
        //第三方登录的初次注册
        if (thirdLoginDto != null) {
            map.put("open_id",thirdLoginDto.getOpenId());
            map.put("plat_source",thirdLoginDto.getPlatSource());
            map.put("third_access_token",thirdLoginDto.getThirdAccessToken());
            //我们的应用在第三方平台注册的ID
            if (!TextUtils.isEmpty(thirdLoginDto.getAppId())) {
                map.put("app_id", thirdLoginDto.getAppId());
            }
        }
        //开启通信等待提示
        LoadingView.getInstance().show(PasswordInitActivity.this);
        //手机短信账号注册
        BaseService.post(url,map, false, new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                onFailureDefault(e);
            }

            @Override
            public void onResponse(Object obj) {
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
                    saveUserAccount(phoneNum, password);

                    // 统计：注册用户埋点("usernick")
                    MANService manService = MANServiceProvider.getService();
                    manService.getMANAnalytics().userRegister(wxUserInfoEntity.getUserId() +"");

                    //跳转班级号输入页面
                    gotoClassNumPage();

                } catch (Exception e) {
                    ErrorCodeEntity errorCodeEntity = new ErrorCodeEntity();
                    errorCodeEntity.setMessage("服务器数据异常");
                    String errorStr = JsonUtil.toJson(errorCodeEntity);
                    onFailure(new QSCustomException(errorStr));
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

}
