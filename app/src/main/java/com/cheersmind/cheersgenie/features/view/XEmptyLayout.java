package com.cheersmind.cheersgenie.features.view;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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

/**
 * 空数据布局
 */
public class XEmptyLayout extends LinearLayout {
    //加载成功 隐藏emptylayout了
    public static final int HIDE_LAYOUT = 4;
    //网络连接有误，或者请求失败
    public static final int NETWORK_ERROR = 1;
    //正在加载数据
    public static final int NETWORK_LOADING = 2;
    //没有数据
    public static final int NO_DATA = 3;
    //没有数据可以点击刷新
    public static final int NO_DATA_ENABLE_CLICK = 5;

    private ProgressBar animProgress;
    //没有数据的时候是否可以点击
    private boolean clickEnableForNoData = false;

    private final Context context;
    //当前状态
    private int mErrorState;
    //无数据情况提示文本
    private String noDataTip = "";

    //提示图标
    public ImageView ivErrorIcon;
    //提示文字
    private TextView tvErrorTip;
    //重新加载
    private Button btnReload;
    //跳转到相关页面
    private Button btnGotoRelation;

    public XEmptyLayout(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public XEmptyLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    private void init() {
        View view = View.inflate(context, R.layout.layout_xempty, null);

        ivErrorIcon = view.findViewById(R.id.iv_error_icon);
        tvErrorTip = view.findViewById(R.id.tv_error_tip);
        animProgress = view.findViewById(R.id.animProgress);
        btnReload = view.findViewById(R.id.btn_reload);
        btnGotoRelation = view.findViewById(R.id.btn_goto_relation);

        setBackgroundColor(-1);
        addView(view);
        changeErrorLayoutBgMode(context);
    }

    public void changeErrorLayoutBgMode(Context context1) {
        // mLayout.setBackgroundColor(SkinsUtil.getColor(context1,
        // "bgcolor01"));
        // tv.setTextColor(SkinsUtil.getColor(context1, "textcolor05"));
    }

    public void dismiss() {
        mErrorState = HIDE_LAYOUT;
        setVisibility(View.GONE);
    }

    public int getErrorState() {
        return mErrorState;
    }

    public boolean isLoadError() {
        return mErrorState == NETWORK_ERROR;
    }

    public boolean isLoading() {
        return mErrorState == NETWORK_LOADING;
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
     * 设置错误提示文本
     * @param msg
     */
    private void setErrorTip(String msg) {
        if (tvErrorTip.getVisibility() == GONE) {
            tvErrorTip.setVisibility(VISIBLE);
        }
        tvErrorTip.setText(msg);
    }

    /**
     * 设置错误提示文本
     * @param resId
     */
    private void setErrorTip(int resId) {
        if (tvErrorTip.getVisibility() == GONE) {
            tvErrorTip.setVisibility(VISIBLE);
        }
        tvErrorTip.setText(resId);
    }

    /**
     * 设置图标和提示文字
     * @param imgResource 图片的id
     *
     */
    private void setErrorImage(int imgResource) {
        try {
            if (ivErrorIcon.getVisibility() == GONE) {
                ivErrorIcon.setVisibility(VISIBLE);
            }
            ivErrorIcon.setBackgroundResource(imgResource);
        } catch (Exception e) {
        }
    }


    /**
     * 设置无数据情况是否可点击（显示去看看吧按钮）
     * @param clickEnableForNoData
     */
    public void setClickEnableForNoData(boolean clickEnableForNoData) {
        this.clickEnableForNoData = clickEnableForNoData;
    }

    /**
     * 设置类型
     * @param i
     */
    public void setErrorType(int i) {
        setVisibility(View.VISIBLE);
        switch (i) {
            //网络错误
            case NETWORK_ERROR: {
                mErrorState = NETWORK_ERROR;
                if (isConnectivity(context)) {
                    //修改提示文本
                    setErrorTip(R.string.error_view_load_error_click_to_refresh);
                } else {
                    //修改提示文本
                    setErrorTip(R.string.error_view_network_error_click_to_refresh);
                }
                //修改图标
                setErrorImage(R.drawable.page_icon_network);
                //隐藏通信等待
                animProgress.setVisibility(View.GONE);
                //显示重载按钮
                btnReload.setVisibility(VISIBLE);
                //隐藏跳转相关按钮
                btnGotoRelation.setVisibility(GONE);
                break;
            }
            //正在加载
            case NETWORK_LOADING: {
                mErrorState = NETWORK_LOADING;
                //隐藏图标
                ivErrorIcon.setVisibility(View.GONE);
                //修改提示文本
                setErrorTip(R.string.error_view_loading);
                //显示通信等待
                animProgress.setVisibility(View.VISIBLE);
                //隐藏重载按钮
                btnReload.setVisibility(GONE);
                //隐藏跳转相关按钮
                btnGotoRelation.setVisibility(GONE);
                break;
            }
            //无数据
            case NO_DATA: {
                mErrorState = NO_DATA;
                //修改图标
                setErrorImage(R.drawable.page_icon_empty);
                //修改提示文本
                if (!TextUtils.isEmpty(noDataTip)) {
                    setErrorTip(noDataTip);
                } else {
                    setErrorTip(R.string.error_view_no_data);
                }
                //隐藏通信等待
                animProgress.setVisibility(View.GONE);
                //隐藏重载按钮
                btnReload.setVisibility(GONE);
                //如果无数据情况可点击
                if (clickEnableForNoData) {
                    //显示跳转相关按钮
                    btnGotoRelation.setVisibility(VISIBLE);
                } else {
                    //隐藏跳转相关按钮
                    btnGotoRelation.setVisibility(GONE);
                }
                break;
            }
            //隐藏
            case HIDE_LAYOUT: {
                setVisibility(View.GONE);
                break;
            }
            //无数据，可点击重载
            case NO_DATA_ENABLE_CLICK: {
                mErrorState = NO_DATA_ENABLE_CLICK;
                //修改图标
                setErrorImage(R.drawable.page_icon_empty);
                //修改提示文本
                setErrorTip(R.string.error_view_load_error_click_to_refresh);
                //隐藏通信等待
                animProgress.setVisibility(View.GONE);
                //显示重载按钮
                btnReload.setVisibility(VISIBLE);
                //隐藏跳转相关按钮
                btnGotoRelation.setVisibility(GONE);
                break;
            }
            default:
                break;
        }
    }


    /**
     * 设置重新加载监听
     * @param listener
     */
    public void setOnReloadListener(View.OnClickListener listener) {
        btnReload.setOnClickListener(listener);
    }


    /**
     * 设置跳转相关页面监听
     * @param listener
     */
    public void setOnGotoRelationListener(View.OnClickListener listener) {
        btnGotoRelation.setOnClickListener(listener);
    }


    /**
     * 设置无数据提示文本
     * @param noDataTip
     */
    public void setNoDataTip(String noDataTip) {
        this.noDataTip = noDataTip;
    }

    @Override
    public void setVisibility(int visibility) {
        if (visibility == View.GONE)
            mErrorState = HIDE_LAYOUT;
        super.setVisibility(visibility);
    }


    /**
     *
     * 描述：是否有网络连接.androidbase中AbWifiUtil中的方法
     * @param context
     * @return
     */
    public static boolean isConnectivity(Context context) {

        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        TelephonyManager telephonyManager = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        return ((connectivityManager.getActiveNetworkInfo() != null && connectivityManager
                .getActiveNetworkInfo().getState() == NetworkInfo.State.CONNECTED) || telephonyManager
                .getNetworkType() == TelephonyManager.NETWORK_TYPE_UMTS);
    }

}
