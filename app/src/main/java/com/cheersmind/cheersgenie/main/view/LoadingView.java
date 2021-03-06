package com.cheersmind.cheersgenie.main.view;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.view.material.ProgressView.MaterialProgressView;
import com.cheersmind.cheersgenie.main.QSApplication;
import com.cheersmind.cheersgenie.main.service.BaseService;

/**
 * 通用加载等待视图
 */
public class LoadingView {
    private static LoadingView mLoadingView = null;
    private AlertDialog dlg;

    //通信等待视图
//    MaterialProgressView materialProgressView;

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
     * @param context 上下文
     * @param httpTag 通信标记
     */
    @SuppressLint("NewApi")
    public void show(final Context context, final String httpTag) {
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

            //图片动画
//            final Animation ani = AnimationUtils.loadAnimation(context, R.anim.loading);
//            new Handler().postDelayed(new Runnable() {
//
//                @Override
//                public void run() {
//                    //动画效果
//                    ImageView imageView = (ImageView) window.findViewById(R.id.loadingImg);
//                    imageView.startAnimation(ani);
//
//                }
//            }, 50) ;

//            materialProgressView = window.findViewById(R.id.progress_view);
//            materialProgressView.setVisibility(View.VISIBLE);

            //监听回退
            dlg.setOnKeyListener(new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    if(keyCode==KeyEvent.KEYCODE_BACK){
                        BaseService.cancelTag(httpTag);
                    }
                    return false;
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 取消
     */
    public void dismiss() {
        try {
            if (dlg != null) {
                dlg.setOnKeyListener(null);
                dlg.setOnDismissListener(null);
                dlg.setOnCancelListener(null);
                dlg.setOnShowListener(null);
                dlg.dismiss();
                dlg.cancel();
                dlg = null;
            }

//            if (materialProgressView != null) {
//                materialProgressView.setVisibility(View.GONE);
//                materialProgressView = null;
//            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 是否正在显示
     * @return true：显示
     */
    public boolean isShowing() {
        return dlg != null && dlg.isShowing();
    }

}
