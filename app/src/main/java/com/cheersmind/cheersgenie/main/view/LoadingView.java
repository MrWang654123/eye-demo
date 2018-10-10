package com.cheersmind.cheersgenie.main.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.main.QSApplication;
import com.cheersmind.cheersgenie.main.service.BaseService;

/**
 * Created by goodm on 2017/4/15.
 */
public class LoadingView {
    private static LoadingView mLoadingView = null;
    private AlertDialog dlg;

    private LoadingView() {
    }

    public static LoadingView getInstance() {
        if (mLoadingView == null) {
            mLoadingView = new LoadingView();
        }
        return mLoadingView;
    }

    /**
     * 显示
     * @param context
     */
    @SuppressLint("NewApi")
    public void show(final Context context) {
        if (dlg != null && dlg.isShowing()) {
            return;
        }
        try {
            dlg = new AlertDialog.Builder(context, R.style.loading_dialog).create();
            dlg.setCanceledOnTouchOutside(false);
            dlg.setInverseBackgroundForced(false);
//			dlg.getWindow().setFlags(
//					WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
//					WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
            dlg.show();
            final Window window = dlg.getWindow();
            window.setContentView(R.layout.view_loading);

            //背景透明度
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.dimAmount = 0.1f;
            window.setAttributes(lp);

            final Animation ani = AnimationUtils.loadAnimation(context, R.anim.loading);
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    //动画效果
                    ImageView imageView = (ImageView) window.findViewById(R.id.loadingImg);
                    imageView.startAnimation(ani);

                }
            }, 50) ;

            //监听回退
            dlg.setOnKeyListener(new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    if(keyCode==KeyEvent.KEYCODE_BACK){
                        String tag = QSApplication.getCurrentActivity().getLocalClassName();
                        BaseService.cancelTag(tag);
                    }
                    return false;
                }
            });

        } catch (Exception e) {
        }
    }

    /**
     * 取消
     */
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
     * 是否正在显示
     * @return
     */
    public boolean isShowing() {
        if (dlg != null) {
            return dlg.isShowing();
        }

        return false;
    }

}
