package com.cheersmind.cheersgenie.features.view.animation;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.view.View;

import com.chad.library.adapter.base.animation.BaseAnimation;

/**
 * 底部滑出动画
 */
public class SlideInBottomAnimation implements BaseAnimation {

    //上一个视图的测量高度
    private int preMeasureHeight = 0;

    @Override
    public Animator[] getAnimators(View view) {
        if (view.getMeasuredHeight() != 0) {
            preMeasureHeight = view.getMeasuredHeight();
        }

        if (preMeasureHeight == 0) {
            preMeasureHeight = 200;
        }

        return new Animator[]{
                ObjectAnimator.ofFloat(view, "translationY", preMeasureHeight, 0)
        };
    }
}
