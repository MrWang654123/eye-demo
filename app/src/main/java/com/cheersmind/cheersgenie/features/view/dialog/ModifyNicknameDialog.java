package com.cheersmind.cheersgenie.features.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.constant.ErrorCode;
import com.cheersmind.cheersgenie.features.modules.base.activity.BaseActivity;
import com.cheersmind.cheersgenie.features.utils.DataCheckUtil;
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
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 修改昵称对话框
 */
public class ModifyNicknameDialog extends Dialog {

    private final static String HTTP_TAG = "modify_nickname";

    //昵称
    @BindView(R.id.et_nickname)
    EditText etNickname;
    //昵称的清空按钮
    @BindView(R.id.iv_clear_nickname)
    ImageView ivClearNickname;

    @BindView(R.id.tv_confirm)
    TextView tvConfirm;

    private Context context;
    private OnOperationListener callback;

    //旧的昵称
    private String oldNickname;

    public ModifyNicknameDialog(@NonNull Context context,String oldNickname, OnOperationListener callback) {
        super(context, R.style.dialog_bottom_full);
        this.context = context;
        this.callback = callback;
        this.oldNickname = oldNickname;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_modify_nickname);

        //ButterKnife绑定
        ButterKnife.bind(this);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        setCanceledOnTouchOutside(true);
        setCancelable(true);

        initView();

    }

    private void initView() {
        //昵称的文本输入监听
        etNickname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                //清除按钮显隐
                if (s.length() > 0) {
                    if (ivClearNickname.getVisibility() == View.INVISIBLE) {
                        ivClearNickname.setVisibility(View.VISIBLE);
                    }
                } else {
                    if (ivClearNickname.getVisibility() == View.VISIBLE) {
                        ivClearNickname.setVisibility(View.INVISIBLE);
                    }
                }

                //控制确定按钮的激活、失效
                String curNickname = s.toString();
                if (TextUtils.isEmpty(curNickname) || curNickname.equals(oldNickname)) {
                    tvConfirm.setEnabled(false);
                } else {
                    tvConfirm.setEnabled(true);
                }
            }
        });

        //初始化旧的昵称
        etNickname.setText(oldNickname);
        if (!TextUtils.isEmpty(oldNickname)) {
            etNickname.setSelection(oldNickname.length());
        }
    }


    @Override
    public void dismiss() {
        super.dismiss();

        //取消当前对话框的所有通信
        BaseService.cancelTag(HTTP_TAG);
    }


    @OnClick({R.id.tv_cancel, R.id.tv_confirm, R.id.iv_clear_nickname})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            //取消
            case R.id.tv_cancel: {
                dismiss();
                break;
            }
            //确定
            case R.id.tv_confirm: {
                //修改昵称
                doModifyNicknameWrap(context);
                break;
            }
            //昵称的清空按钮
            case R.id.iv_clear_nickname: {
                etNickname.setText("");
                etNickname.requestFocus();
                break;
            }
        }
    }


    //操作监听
    public interface OnOperationListener {
        //修改成功
        void onModifySuccess(String nickname);
    }


    /**
     * 验证数据格式
     * @return
     */
    private boolean checkData() {
        String nickName = etNickname.getText().toString();

        //昵称非空
        if (TextUtils.isEmpty(nickName)) {
            ToastUtil.showShort(getContext(), "请输入昵称");
            return false;
        }
        //昵称格式验证
        if (!DataCheckUtil.isNickname(nickName)) {
            ToastUtil.showShort(getContext(), getContext().getResources().getString(R.string.nickname_format_error));
            return false;
        }

        return true;
    }

    /**
     * 修改昵称
     */
    private void doModifyNicknameWrap(Context context) {
        //隐藏软键盘
//        SoftInputUtil.closeSoftInput(activity);
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(etNickname.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }

        //数据校验
        if (!checkData()) {
            return;
        }

        //修改昵称
        final String nickname = etNickname.getText().toString();
        //请求修改昵称
        patchModifyNickname(nickname);
    }

    /**
     * 请求修改昵称
     * @param nickname
     */
    private void patchModifyNickname(final String nickname) {
        //开启通信等待提示
//        LoadingView.getInstance().show(context);
        tvConfirm.setEnabled(false);
        tvConfirm.setText("提交中…");

        DataRequestService.getInstance().patchModifyNickname(nickname, new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                onFailureDefault(e);
                tvConfirm.setEnabled(true);
                tvConfirm.setText("保存");
            }

            @Override
            public void onResponse(Object obj) {
                //关闭通信等待提示
                LoadingView.getInstance().dismiss();

                tvConfirm.setEnabled(true);
                tvConfirm.setText("保存");

                //修改成功回调
                if (callback != null) {
                    callback.onModifySuccess(nickname);
                }

                //关闭对话框
                dismiss();
            }
        }, HTTP_TAG);
    }


    /**
     * 默认的通信失败处理
     * @param e 自定义异常
     */
    public void onFailureDefault(QSCustomException e) {
        onFailureDefault(e, null);
    }

    /**
     * 默认的通信失败处理
     * @param e
     * @param errorCodeCallBack ErrorCodeEntity对象回调
     */
    public void onFailureDefault(QSCustomException e, BaseActivity.FailureDefaultErrorCodeCallBack errorCodeCallBack) {
        //关闭通信等待
        LoadingView.getInstance().dismiss();
        //服务器繁忙，请稍后重试！
        String message = getContext().getResources().getString(R.string.error_code_common_text);
        //回调方法已经处理了该异常，默认false：执行默认提示
        boolean callBackHandleTheError = false;

        try {
            String bodyStr = e.getMessage();
            Map map = JsonUtil.fromJson(bodyStr, Map.class);
            ErrorCodeEntity errorCodeEntity = InjectionWrapperUtil.injectMap(map, ErrorCodeEntity.class);

            //token超时和被T的处理
            if (errorCodeEntity != null && ErrorCode.AC_AUTH_INVALID_TOKEN.equals(errorCodeEntity.getCode())) {
                message = getContext().getResources().getString(R.string.invalid_token_tip);

            } else {
                //ErrorCodeEntity对象回调
                if (errorCodeEntity != null && errorCodeCallBack != null) {
                    //true：处理了异常，则直接return；false：未处理异常，则继续走默认流程
                    callBackHandleTheError = errorCodeCallBack.onErrorCodeCallBack(errorCodeEntity);
                    if (callBackHandleTheError) {
                        return;
                    }
                }
                //服务端返回的错误消息
                String tempMessage = errorCodeEntity.getMessage();
                if (!TextUtils.isEmpty(tempMessage)) {
                    message = tempMessage;
                }
            }

        } catch (Exception err) {
            //异常处理（如何只处理自定义的异常信息，程序错误按默认信息提示？）
            String tempMessage = e.getMessage();
            if (!TextUtils.isEmpty(tempMessage)) {
                message = tempMessage;
            }

        } finally {
            //回调方法没有处理该异常，则走默认提示流程
            if (!callBackHandleTheError) {
                ToastUtil.showShort(getContext(), message);
            }
        }
    }


    @Override
    public void show() {
        super.show();
        /**
         * 设置宽度全屏，要设置在show的后面
         */
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.gravity = Gravity.CENTER_VERTICAL;
        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;

        getWindow().getDecorView().setPadding(0, 0, 0, 0);

        getWindow().setAttributes(layoutParams);
    }


}
