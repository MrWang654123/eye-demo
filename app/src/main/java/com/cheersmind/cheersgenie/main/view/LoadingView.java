package com.cheersmind.cheersgenie.main.view;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.cheersmind.cheersgenie.R;

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

    @SuppressLint("NewApi")
    public void show(Context context) {
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
}
