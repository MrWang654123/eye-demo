package com.cheersmind.cheersgenie.main.view;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;

import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.modules.base.activity.SplashActivity;
import com.cheersmind.cheersgenie.features.modules.login.activity.XLoginActivity;
import com.cheersmind.cheersgenie.main.entity.ChildInfoEntity;
import com.cheersmind.cheersgenie.main.entity.WXUserInfoEntity;
import com.cheersmind.cheersgenie.module.login.UCManager;
import com.umeng.analytics.MobclickAgent;

import org.litepal.crud.DataSupport;

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
                //目前获取服务端时间戳失败、本地不存在用户信息、token已失效、获取孩子列表失败都会跳转到登录主页面
                //前往登录主页面
//                gotoLoginPage(context);
            } else {
                //显示对话框
                dlg = new android.support.v7.app.AlertDialog.Builder(context)
                        .setTitle(context.getResources().getString(R.string.dialog_common_title))
                        .setMessage(context.getResources().getString(R.string.invalid_token_tip))
                        .setNegativeButton("前往登录", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
//                                //前往登录主页面
//                                gotoLoginPage(context);
                                //退出操作
                                doExit(context);
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
                if (dlg.getWindow() != null) {
                    dlg.getWindow().setWindowAnimations(R.style.WUI_Animation_Dialog);
                }
                dlg.show();
            }
        } catch (Exception e) {
        }
    }


    /**
     * 退出操作
     */
    private void doExit(Context context) {
        //清空用户名、密码
//        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(QSApplication.getContext());
//        SharedPreferences.Editor editor = pref.edit();
////        editor.remove("user_name");
//        editor.remove("user_password");
//        editor.apply();
        //删除数据库中的用户对象
        DataSupport.deleteAll(WXUserInfoEntity.class);
        //删除数据库中的孩子对象
        DataSupport.deleteAll(ChildInfoEntity.class);
        //清空登录信息的临时缓存
        UCManager.getInstance().clearToken();
//                SharedPreferencesUtils.setParam(this, MainActivity.SLIDING_ITEM_SHARE_KEY, 0);

//        // 用户注销埋点
//        MANService manService = MANServiceProvider.getService();
//        manService.getMANAnalytics().updateUserAccount("", "");

        //友盟统计：登出
        MobclickAgent.onProfileSignOff();

        //前往登录主页面
        gotoLoginPage(context);
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
