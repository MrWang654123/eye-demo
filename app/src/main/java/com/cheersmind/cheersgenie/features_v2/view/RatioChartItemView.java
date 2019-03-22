package com.cheersmind.cheersgenie.features_v2.view;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.annotation.ColorInt;
import android.support.constraint.ConstraintLayout;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.cheersmind.cheersgenie.R;
import com.facebook.common.util.UriUtil;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;

/**
 * 左右比例图表项视图
 */
public class RatioChartItemView extends LinearLayout {

    private final Context context;

    //内容视图
    private View contentView;

    public RatioChartItemView(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public RatioChartItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    private void init() {
        contentView = View.inflate(context, R.layout.layout_ratio_chart_item, null);

        TextView textView = new TextView(context);
        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) textView.getLayoutParams();

        setBackgroundColor(-1);
        addView(contentView);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        onSkinChanged();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    public void onSkinChanged() {
    }

    /**
     * 设置背景
     * @param drawableId Id
     */
    @Override
    public void setBackgroundResource(int drawableId) {
        super.setBackgroundResource(drawableId);
        if (contentView != null) {
            contentView.setBackgroundResource(drawableId);
        }
    }

    /**
     * 设置背景色
     * @param color 颜色值 AARRGGBB
     */
    @Override
    public void setBackgroundColor(@ColorInt int color) {
        super.setBackgroundColor(color);
        if (contentView != null) {
            contentView.setBackgroundColor(color);
        }
    }

}
