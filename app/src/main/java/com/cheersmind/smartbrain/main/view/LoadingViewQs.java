package com.cheersmind.smartbrain.main.view;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.cheersmind.smartbrain.R;

/**
 * Created by Administrator on 2017/12/19.
 */

public class LoadingViewQs {

    private static LoadingViewQs mLoadingView = null;
    private AlertDialog dlg;
    private AnimationDrawable animationDrawable;

    private LoadingViewQs() {
    }

    public static LoadingViewQs getInstance() {
        if (mLoadingView == null) {
            mLoadingView = new LoadingViewQs();
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
            window.setContentView(R.layout.view_loading_qs);

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
                    animationDrawable = (AnimationDrawable) imageView.getDrawable();
                    animationDrawable.start();
//                    imageView.startAnimation(ani);

                }
            }, 50) ;
        } catch (Exception e) {
        }
    }

    public void dismiss() {
        try {
            if(animationDrawable!=null){
                animationDrawable.stop();
            }
            if (dlg != null) {
                dlg.dismiss();
                dlg.cancel();
                dlg = null;
            }
        } catch (Exception e) {

        }
    }
}
