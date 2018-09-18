package com.cheersmind.cheersgenie.features.modules.mine.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.modules.base.activity.BaseActivity;
import com.cheersmind.cheersgenie.features.modules.login.activity.XLoginActivity;
import com.cheersmind.cheersgenie.features.modules.register.activity.PasswordInitActivity;
import com.cheersmind.cheersgenie.features.utils.SoftInputUtil;
import com.cheersmind.cheersgenie.main.Exception.QSCustomException;
import com.cheersmind.cheersgenie.main.QSApplication;
import com.cheersmind.cheersgenie.main.service.BaseService;
import com.cheersmind.cheersgenie.main.service.DataRequestService;
import com.cheersmind.cheersgenie.main.util.EncryptUtil;
import com.cheersmind.cheersgenie.main.util.ToastUtil;
import com.cheersmind.cheersgenie.main.view.LoadingView;

import butterknife.BindView;
import butterknife.OnClick;

import static com.cheersmind.cheersgenie.R2.string.captcha;


/**
 * 密码修改页面
 */
public class ModifyPasswordActivity extends BaseActivity {

    @BindView(R.id.et_old_password)
    EditText etOldPassword;
    @BindView(R.id.cbox_password)
    CheckBox cboxPassword;
    @BindView(R.id.et_new_password)
    EditText etNewPassword;
    @BindView(R.id.cbox_new_password)
    CheckBox cboxNewPassword;
    @BindView(R.id.et_again_password)
    EditText etAgainPassword;
    @BindView(R.id.cbox_again_password)
    CheckBox cboxAgainPassword;

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


    @OnClick({R.id.et_old_password, R.id.et_new_password, R.id.et_again_password, R.id.btn_confirm})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            //确保密码编辑框点击后光标在最后
            case R.id.et_old_password:
            case R.id.et_new_password:
            case R.id.et_again_password:
//                ((EditText)view).setSelection(((EditText)view).getText().length());
                break;
            //确认修改
            case R.id.btn_confirm:
                //修改密码
                doModifyPasswordWrap();
                break;
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
    private void queryModifyPassword(String passwordOld, String passwordNew) {
        //通信提示
        LoadingView.getInstance().show(this);
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
                SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(QSApplication.getContext());
                SharedPreferences.Editor editor = pref.edit();
                editor.remove("user_password");
                editor.commit();

                //弹出修改密码成功确认对话框
                popupConfirmModifySuccessWindows();
            }
        });
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
        //确认密码
        String passwordAgain = etAgainPassword.getText().toString().trim();

        //旧密码长度验证
        if (passwordOld.length() < 6 || passwordOld.length() > 24) {
            ToastUtil.showShort(getApplicationContext(), "旧密码长度为6-24位");
            return false;
        }

        //新密码长度验证
        if (passwordNew.length() < 6 || passwordNew.length() > 24) {
            ToastUtil.showShort(getApplicationContext(), "新密码长度为6-24位");
            return false;
        }

        //确认密码长度验证
        if (passwordAgain.length() < 6 || passwordAgain.length() > 24) {
            ToastUtil.showShort(getApplicationContext(), "确认密码长度为6-24位");
            return false;
        }

        //新旧密码不能相同
        if (passwordOld.equals(passwordNew)) {
            ToastUtil.showShort(getApplicationContext(), "新旧密码不能相同");
            return false;
        }

        //新密码和确认密码一致
        if (!passwordNew.equals(passwordAgain)) {
            ToastUtil.showShort(getApplicationContext(), "确认密码不一致");
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
        dialog.show();
    }

}
