package com.cheersmind.cheersgenie.features.modules.login.activity;

import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.sdk.android.man.MANService;
import com.alibaba.sdk.android.man.MANServiceProvider;
import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.constant.Dictionary;
import com.cheersmind.cheersgenie.features.constant.ErrorCode;
import com.cheersmind.cheersgenie.features.dto.PhoneNumLoginDto;
import com.cheersmind.cheersgenie.features.modules.base.activity.BaseActivity;
import com.cheersmind.cheersgenie.features.modules.register.activity.RegisterCaptchaActivity;
import com.cheersmind.cheersgenie.features.modules.register.activity.RegisterPhoneNumActivity;
import com.cheersmind.cheersgenie.features.utils.DataCheckUtil;
import com.cheersmind.cheersgenie.features.utils.DeviceUtil;
import com.cheersmind.cheersgenie.features.utils.SoftInputUtil;
import com.cheersmind.cheersgenie.main.Exception.QSCustomException;
import com.cheersmind.cheersgenie.main.dao.ChildInfoDao;
import com.cheersmind.cheersgenie.main.entity.ChildInfoEntity;
import com.cheersmind.cheersgenie.main.entity.ChildInfoRootEntity;
import com.cheersmind.cheersgenie.main.entity.ErrorCodeEntity;
import com.cheersmind.cheersgenie.main.entity.WXUserInfoEntity;
import com.cheersmind.cheersgenie.main.service.BaseService;
import com.cheersmind.cheersgenie.main.service.DataRequestService;
import com.cheersmind.cheersgenie.main.util.InjectionWrapperUtil;
import com.cheersmind.cheersgenie.main.util.JsonUtil;
import com.cheersmind.cheersgenie.main.util.ToastUtil;
import com.cheersmind.cheersgenie.main.view.LoadingView;
import com.cheersmind.cheersgenie.module.login.UCManager;
import com.cheersmind.cheersgenie.module.login.UserService;

import org.litepal.crud.DataSupport;

import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 手机号快捷登录
 */
public class PhoneNumLoginActivity extends BaseActivity {

    @BindView(R.id.btn_confirm)
    Button btnConfirm;
    @BindView(R.id.btn_captcha)
    Button btnCaptcha;
    @BindView(R.id.et_phonenum)
    EditText etPhonenum;
    @BindView(R.id.et_captcha)
    EditText etCaptcha;

    //计时器
    private CountTimer countTimer;
    //倒计时的时间（s）
    private static final int COUNT_DOWN = 65000;

    @Override
    protected int setContentView() {
        return R.layout.activity_phone_num_login;
    }

    @Override
    protected String settingTitle() {
        return "手机快捷登录";
    }


    @Override
    protected void onInitView() {
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


    @OnClick({R.id.btn_captcha, R.id.btn_confirm})
    public void click(View view) {
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
        }
    }

    /**
     * 手机号登录
     */
    private void doPhoneNumLoginWrap() {
        //数据校验
        if (!checkDataForPhoneNumLogin()) {
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
        phoneNumLoginDto.setDeviceId(DeviceUtil.getDeviceId(PhoneNumLoginActivity.this));
        doPhoneNumLogin(phoneNumLoginDto);
    }

    /**
     * 手机号登录的验证数据格式
     * @return true：验证通过，false：验证不通过
     */
    private boolean checkDataForPhoneNumLogin() {
        //手机号验证
        if (!checkData()) {
            return false;
        }

        //短信验证码
        String captcha = etCaptcha.getText().toString().trim();
        //非空
        if (TextUtils.isEmpty(captcha)) {
            ToastUtil.showShort(getApplicationContext(), "请输入短信验证码");
            return false;
        }
        //长度（目前验证码长度为6）
        if (captcha.length() < 6) {
            ToastUtil.showShort(getApplicationContext(), "请完整输入短信验证码");
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
                    manService.getMANAnalytics().updateUserAccount(wxUserInfoEntity.getUserId() +"", wxUserInfoEntity.getUserId()+"");

                    //获取孩子信息
                    doGetChildListWrap();

                } catch (Exception e) {
                    //完善用户信息（生成孩子）
//                    gotoPerfectUserInfo(PhoneNumLoginActivity.this);
                    onFailure(new QSCustomException("登录失败"));
                }
            }
        });
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
     * 发送注册验证码
     */
    private void doQuerySendCaptchaWrap() {
        //隐藏软键盘
        SoftInputUtil.closeSoftInput(this);
        //格式验证
        if (!checkData()) {
            return;
        } else {
            //开始计时
            countTimer.start();
        }
        //请求发送验证码
        String phoneNum = etPhonenum.getText().toString();
        querySendCaptcha(phoneNum, Dictionary.SmsType_SmsLogin);
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


    /**
     * 请求发送注册验证码
     * @param phoneNum 手机号
     * @param smsType 短信业务类型：0:注册用户，1：短信登录，2：绑定手机号
     */
    private void querySendCaptcha(final String phoneNum, final int smsType) {
        //隐藏软键盘
        SoftInputUtil.closeSoftInput(this);
        //开启通信等待提示
        LoadingView.getInstance().show(this);

        DataRequestService.getInstance().postRegisterCaptcha(phoneNum, smsType, new BaseService.ServiceCallback() {
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
            }
        });
    }

}
