package com.cheersmind.cheersgenie.features.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.cheersmind.cheersgenie.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 积分提示对话框
 */
public class IntegralTipDialog extends Dialog {

    @BindView(R.id.tv_integral)
    TextView tvIntegral;

    //积分
    private int integralVal;

    //操作监听
    private OnOperationListener listener;


    public IntegralTipDialog(@NonNull Context context, int integralVal, OnOperationListener listener) {
        super(context, R.style.loading_dialog);
        this.integralVal = integralVal;
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_integral_tip);
        //ButterKnife绑定
        ButterKnife.bind(this);

        //修改对话框属性
        Window window = getWindow();
        if (window != null) {
            //设置宽度全屏，要设置在show的后面
            WindowManager.LayoutParams layoutParams = window.getAttributes();
            if (layoutParams != null) {
                //影响到了状态栏颜色
                //位于底部
//                layoutParams.gravity = Gravity.CENTER_VERTICAL;
                //宽度
//                layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                //高度
//                layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;


                //DecorView的内间距（目前测试的机型还没发现有影响）
//                window.getDecorView().setPadding(0, 0, 0, 0);
                //背景灰度
                layoutParams.dimAmount = 0.0f;
                //设置属性
                window.setAttributes(layoutParams);
            }
        }

        //初始化积分文本
        tvIntegral.setText(getContext().getResources().getString(R.string.integral_tip_add, String.valueOf(integralVal)));
    }


    @Override
    public void dismiss() {
        super.dismiss();

    }

    @Override
    public void show() {
        super.show();

        doAnimation();
    }

    /**
     * 动画
     */
    private void doAnimation() {
        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.integral_tip);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                dismiss();

                if (listener != null) {
                    listener.onAnimationEnd();
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
//        new Handler().postDelayed(new Runnable() {
//
//                @Override
//                public void run() {
//                    //动画效果
//                    tvIntegral.startAnimation(animation);
//                }
//            }, 50) ;

        //动画效果
        tvIntegral.startAnimation(animation);
    }


    //操作监听
    public interface OnOperationListener {
        //动画结束
        void onAnimationEnd();
    }

}
