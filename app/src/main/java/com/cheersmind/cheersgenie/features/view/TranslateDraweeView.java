package com.cheersmind.cheersgenie.features.view;

import android.content.Context;
import android.graphics.Matrix;
import android.util.AttributeSet;

import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.view.SimpleDraweeView;

/**
 * 兼容转场动画导致图像不显示的SimpleDraweeView（Fresco控件）
 */
public class TranslateDraweeView extends SimpleDraweeView {

    public TranslateDraweeView(Context context) {
        super(context);
    }

    public TranslateDraweeView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TranslateDraweeView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public TranslateDraweeView(Context context, GenericDraweeHierarchy hierarchy) {
        super(context, hierarchy);
    }

    /**
     * 转场动画后重绘
     * @param matrix
     */
//    public void animateTransform(Matrix matrix) {
//        invalidate();
//    }

}
