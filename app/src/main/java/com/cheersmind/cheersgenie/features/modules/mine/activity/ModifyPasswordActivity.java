package com.cheersmind.cheersgenie.features.modules.mine.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.sdk.android.man.MANService;
import com.alibaba.sdk.android.man.MANServiceProvider;
import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.constant.Dictionary;
import com.cheersmind.cheersgenie.features.dto.ThirdPlatBindDto;
import com.cheersmind.cheersgenie.features.modules.base.activity.BaseActivity;
import com.cheersmind.cheersgenie.features.modules.login.activity.XLoginAccountActivity;
import com.cheersmind.cheersgenie.features.modules.login.activity.XLoginActivity;
import com.cheersmind.cheersgenie.features.modules.register.activity.PasswordInitActivity;
import com.cheersmind.cheersgenie.features.modules.register.activity.RegisterPhoneNumActivity;
import com.cheersmind.cheersgenie.features.utils.SoftInputUtil;
import com.cheersmind.cheersgenie.main.Exception.QSCustomException;
import com.cheersmind.cheersgenie.main.QSApplication;
import com.cheersmind.cheersgenie.main.constant.Constant;
import com.cheersmind.cheersgenie.main.entity.WXUserInfoEntity;
import com.cheersmind.cheersgenie.main.service.BaseService;
import com.cheersmind.cheersgenie.main.service.DataRequestService;
import com.cheersmind.cheersgenie.main.util.EncryptUtil;
import com.cheersmind.cheersgenie.main.util.InjectionWrapperUtil;
import com.cheersmind.cheersgenie.main.util.JsonUtil;
import com.cheersmind.cheersgenie.main.util.ToastUtil;
import com.cheersmind.cheersgenie.main.view.LoadingView;
import com.cheersmind.cheersgenie.module.login.UCManager;
import com.umeng.analytics.MobclickAgent;

import org.litepal.crud.DataSupport;

import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

import static com.cheersmind.cheersgenie.R2.string.captcha;


/**
 * 密码修改页面
 */
public class ModifyPasswordActivity extends BaseActivity {

    @BindView(R.id.et_old_password)
    EditText etOldPassword;
    //密码的清空按钮
    @BindView(R.id.iv_clear_pw)
    ImageView ivClearPw;
    @BindView(R.id.cbox_password)
    CheckBox cboxPassword;
    @BindView(R.id.et_new_password)
    EditText etNewPassword;
    //新密码的清空按钮
    @BindView(R.id.iv_clear_new_pw)
    ImageView ivClearNewPw;
    @BindView(R.id.cbox_new_password)
    CheckBox cboxNewPassword;


    @Override
    protected int setContentView() {
        return R.layout.activity_modify_password;
    }

    @Override
    protected String settingTitle() {
        return "修改密码";
    }

    @Override
    protected void onInitView() {
        etOldPassword.addTextChangedListener(new TextWatcher() {
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
        //旧密码显隐
        cboxPassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    //如果选中，显示密码
                    etOldPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    //否则隐藏密码
                    etOldPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
                //光标置于末尾
                etOldPassword.setSelection(etOldPassword.getText().length());
            }
        });

        etNewPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    if (ivClearNewPw.getVisibility() == View.INVISIBLE) {
                        ivClearNewPw.setVisibility(View.VISIBLE);
                    }
                } else {
                    if (ivClearNewPw.getVisibility() == View.VISIBLE) {
                        ivClearNewPw.setVisibility(View.INVISIBLE);
                    }
                }
            }
        });
        //新密码显隐
        cboxNewPassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    //如果选中，显示密码
                    etNewPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    //否则隐藏密码
                    etNewPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
                //光标置于末尾
                etNewPassword.setSelection(etNewPassword.getText().length());
            }
        });

        //监听确认密码输入框的软键盘回车键
        etNewPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                //当actionId == XX_SEND 或者 XX_DONE时都触发
                //或者event.getKeyCode == ENTER 且 event.getAction == ACTION_DOWN时也触发
                //注意，这是一定要判断event != null。因为在某些输入法上会返回null。
                if (actionId == EditorInfo.IME_ACTION_SEND
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || (keyEvent != null && KeyEvent.KEYCODE_ENTER == keyEvent.getKeyCode() && KeyEvent.ACTION_DOWN == keyEvent.getAction())) {
                    //修改密码
                    doModifyPasswordWrap();
                }

                return false;
            }
        });
    }

    @Override
    protected void onInitData() {

    }


    @OnClick({R.id.et_old_password, R.id.et_new_password, R.id.btn_confirm,
            R.id.iv_clear_pw, R.id.iv_clear_new_pw})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            //确保密码编辑框点击后光标在最后
            case R.id.et_old_password:
            case R.id.et_new_password:
//                ((EditText)view).setSelection(((EditText)view).getText().length());
                break;
            //确认修改
            case R.id.btn_confirm:
                //修改密码
                doModifyPasswordWrap();
                break;
            //旧密码的清空按钮
            case R.id.iv_clear_pw: {
                etOldPassword.setText("");
                etOldPassword.requestFocus();
                break;
            }
            //新密码的清空按钮
            case R.id.iv_clear_new_pw: {
                etNewPassword.setText("");
                etNewPassword.requestFocus();
                break;
            }
        }
    }

    /**
     * 修改密码
     */
    private void doModifyPasswordWrap() {
        //隐藏软键盘
        SoftInputUtil.closeSoftInput(ModifyPasswordActivity.this);
        //格式验证
        if (!checkData()) return;
        //请求修改密码
        queryModifyPassword(etOldPassword.getText().toString().trim(), etNewPassword.getText().toString().trim());
    }

    /**
     * 请求修改密码
     * @param passwordOld 旧密码
     * @param passwordNew 新密码
     */
    private void queryModifyPassword(String passwordOld, final String passwordNew) {
        //通信提示
        LoadingView.getInstance().show(this, httpTag);
        //请求修改密码
        DataRequestService.getInstance().patchModifyPassword(passwordOld, passwordNew, new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                onFailureDefault(e);
            }

            @Override
            public void onResponse(Object obj) {
                LoadingView.getInstance().dismiss();

                //修改密码成功后的处理

                //清空密码缓存
                /*SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(QSApplication.getContext());
                SharedPreferences.Editor editor = pref.edit();
                editor.remove("user_password");
                editor.apply();

                //弹出修改密码成功确认对话框
                popupConfirmModifySuccessWindows();*/

                try {
                    Map dataMap = JsonUtil.fromJson(obj.toString(), Map.class);
                    //解析用户数据
                    final WXUserInfoEntity wxUserInfoEntity = InjectionWrapperUtil.injectMap(dataMap, WXUserInfoEntity.class);
                    //临时缓存用户数据
                    UCManager.getInstance().settingUserInfo(wxUserInfoEntity);
                    //更新密码到缓存中
                    SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(QSApplication.getContext());
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString("user_password", passwordNew);
                    editor.apply();
                    //保存用户数据到数据库
                    DataSupport.deleteAll(WXUserInfoEntity.class);
                    wxUserInfoEntity.save();

                    ToastUtil.showLong(getApplication(), "修改成功");
                    finish();

                } catch (Exception e) {
                    //完善用户信息（生成孩子）
//                    gotoPerfectUserInfo(PhoneNumLoginActivity.this);
                    onFailure(new QSCustomException(getResources().getString(R.string.operate_fail)));
                }
            }
        }, httpTag, ModifyPasswordActivity.this);
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
     * 验证数据格式
     * （密码长度6-24位，可以是数字、字母等任意字符）
     * @return
     */
    private boolean checkData() {
        //旧密码
        String passwordOld = etOldPassword.getText().toString().trim();
        //新密码
        String passwordNew = etNewPassword.getText().toString().trim();

        //旧密码长度验证
        if (passwordOld.length() < 6 || passwordOld.length() > 24) {
            ToastUtil.showShort(getApplication(), "旧密码长度为6-24位");
            return false;
        }

        //新密码长度验证
        if (passwordNew.length() < 6 || passwordNew.length() > 24) {
            ToastUtil.showShort(getApplication(), "新密码长度为6-24位");
            return false;
        }


        //新旧密码不能相同
        if (passwordOld.equals(passwordNew)) {
            ToastUtil.showShort(getApplication(), "新旧密码不能相同");
            return false;
        }


        return true;
    }


    /**
     * 弹出修改密码成功确认对话框
     */
    private void popupConfirmModifySuccessWindows() {
        AlertDialog dialog = new android.support.v7.app.AlertDialog.Builder(ModifyPasswordActivity.this)
                .setTitle(getResources().getString(R.string.dialog_common_title))
                .setMessage("修改密码成功，请重新登录")
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
                        Intent intent = new Intent(ModifyPasswordActivity.this, XLoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
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
