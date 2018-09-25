package com.cheersmind.cheersgenie.main.view;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;

import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.modules.base.activity.BaseActivity;
import com.cheersmind.cheersgenie.features.modules.base.activity.SplashActivity;
import com.cheersmind.cheersgenie.features.modules.login.activity.XLoginActivity;

/**
 * token超时视图
 */
public class TokenTimeOutView {
    private static TokenTimeOutView tokenTimeOutView = null;
    private AlertDialog dlg;

    private TokenTimeOutView() {
    }

    public static TokenTimeOutView getInstance() {
        if (tokenTimeOutView == null) {
            tokenTimeOutView = new TokenTimeOutView();
        }
        return tokenTimeOutView;
    }

    public void show(final Context context) {
        if (dlg != null && dlg.isShowing()) {
            return;
        }
        try {
            //如果是欢迎页，则直接跳转到登录主页面
            if (context instanceof SplashActivity) {
                //前往登录主页面
                gotoLoginPage(context);
            } else {
                //显示对话框
                dlg = new android.support.v7.app.AlertDialog.Builder(context)
                        .setTitle(context.getResources().getString(R.string.dialog_common_title))
                        .setMessage(context.getResources().getString(R.string.invalid_token_tip))
                        .setNegativeButton("前往登录", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                //前往登录主页面
                                gotoLoginPage(context);
                            }
                        })
                        .create();
                dlg.setCanceledOnTouchOutside(false);
                dlg.setCancelable(false);
                dlg.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        dlg = null;
                    }
                });
                dlg.show();
            }
        } catch (Exception e) {
        }
    }

    public void dismiss() {
        try {
            if (dlg != null) {
                dlg.dismiss();
                dlg.cancel();
                dlg = null;
            }
        } catch (Exception e) {

        }
    }

    /**
     * 跳转到登录主页面
     */
    private void gotoLoginPage(Context context) {
        Intent intent = new Intent(context, XLoginActivity.class);
        //作为根activity
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

}
